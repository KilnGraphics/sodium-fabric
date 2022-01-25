package me.jellysquid.mods.sodium.asm;

import me.jellysquid.mods.sodium.opengl.types.IntType;
import me.jellysquid.mods.sodium.render.sequence.SequenceBuilder;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.CodeSource;

import static org.objectweb.asm.Opcodes.*;

public class AsmSequenceBuilderFactory {

    /**
     * The caller of this method is responsible for keeping track of each instance generated,
     * as they are singletons. If this is called with the same parameters twice, it will
     * probably crash.
     */
    public static SequenceBuilder generateSequenceBuilder(int[] pattern, IntType elementType) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // create new raw class
        RawClass rawClass = createSequenceClassBytes(pattern, elementType);

        // load class into KnotClassLoader using defineClass and reflection
        Class<?> knotInterface = Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClassLoaderInterface");
        Method defineClassMethod = knotInterface.getMethod("defineClassFwd", String.class, byte[].class, int.class, int.class, CodeSource.class);
        defineClassMethod.setAccessible(true);
        Class<?> generatedClass = (Class<?>) defineClassMethod.invoke(
                FabricLauncherBase.getLauncher().getTargetClassLoader(),
                rawClass.classPath.replace('/','.') + rawClass.className,
                rawClass.bytes,
                0,
                rawClass.bytes.length,
                null
        );

        // create instance of generated class using no-args constuctor
        Constructor<?> noArgsConstructor = generatedClass.getConstructor();
        return (SequenceBuilder) noArgsConstructor.newInstance();
    }

    private static RawClass createSequenceClassBytes(int[] pattern, IntType elementType) {

        String classPath = "me/jellysquid/mods/sodium/render/sequence/generated/";
        StringBuilder nameGenerator = new StringBuilder("GeneratedSequence");
        nameGenerator.append(elementType.name());
        for (int i : pattern) {
            nameGenerator.append(i);
        }
        String className = nameGenerator.toString();

        ClassWriter classWriter = new ClassWriter(0);
        MethodVisitor methodVisitor;

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, classPath + className, null, "java/lang/Object", new String[] { "me/jellysquid/mods/sodium/render/sequence/SequenceBuilder" });

        //// Visit Basic Constructor
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        //// Visit DMA Write
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "write", "(JI)V", null, null);
        methodVisitor.visitCode();

        for (int i = 0; i < pattern.length; i++) {
            visitPutDMA(methodVisitor, (long) i * elementType.getSize(), pattern[i]);
        }

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(4, 4);
        methodVisitor.visitEnd();

        //// Visit NIO Write
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "write", "(Ljava/nio/ByteBuffer;I)V", null, null);
        methodVisitor.visitCode();

        // the lvt index for the buffer is 1
        methodVisitor.visitVarInsn(ALOAD, 1);

        for (int patternValue : pattern) {
            visitPutNIO(methodVisitor, patternValue);
        }
        methodVisitor.visitInsn(POP);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(3, 3);
        methodVisitor.visitEnd();

        //// Visit getVerticesPerPrimitive
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getVerticesPerPrimitive", "()I", null, null);
        methodVisitor.visitCode();

        // get min and max values of pattern entries
        int minVal = Integer.MAX_VALUE;
        int maxVal = Integer.MIN_VALUE;
        for (int patternValue : pattern) {
            if (patternValue < minVal) {
                minVal = patternValue;
            }
            if(patternValue > maxVal) {
                maxVal = patternValue;
            }
        }
        visitConstantInt(methodVisitor, maxVal - minVal + 1);

        methodVisitor.visitInsn(IRETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        //// Visit getIndicesPerPrimitive
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getIndicesPerPrimitive", "()I", null, null);
        methodVisitor.visitCode();
        // the amount of indices is the amount of entries in the array
        visitConstantInt(methodVisitor, pattern.length);
        methodVisitor.visitInsn(IRETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        //// Visit getElementType
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getElementType", "()Lme/jellysquid/mods/sodium/opengl/types/IntType;", null, null);
        methodVisitor.visitCode();
        // does getting by name always work? is there a better way to fetch the enum instance in question?
        methodVisitor.visitFieldInsn(GETSTATIC, "me/jellysquid/mods/sodium/opengl/types/IntType", elementType.name(), "Lme/jellysquid/mods/sodium/opengl/types/IntType;");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        classWriter.visitEnd();

        return new RawClass(className, classPath, classWriter.toByteArray());
    }

    private static void visitPutDMA(MethodVisitor methodVisitor, long pointerOffset, int vertexOffset) {
        // lvt entry for pointer is 1
        methodVisitor.visitVarInsn(LLOAD, 1);
        // no need to add 0
        if (pointerOffset != 0) {
            visitConstantLong(methodVisitor, pointerOffset);
            methodVisitor.visitInsn(LADD);
        }

        // lvt entry for baseVertex is 3
        methodVisitor.visitVarInsn(ILOAD, 3);
        // no need to add 0
        if (vertexOffset != 0) {
            visitConstantInt(methodVisitor, vertexOffset);
            methodVisitor.visitInsn(IADD);
        }

        // this method returns the existing ByteBuffer, so we don't need to constantly keep loading it
        methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutInt", "(JI)V", false);
    }

    // the pointer offset doesn't need to be specified here because ByteBuffer will update the pointer location after every write
    private static void visitPutNIO(MethodVisitor methodVisitor, int vertexOffset) {
        // lvt entry for baseVertex is 2
        methodVisitor.visitVarInsn(ILOAD, 2);
        // no need to add 0
        if (vertexOffset != 0) {
            visitConstantInt(methodVisitor, vertexOffset);
            methodVisitor.visitInsn(IADD);
        }

        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/nio/ByteBuffer", "putInt", "(I)Ljava/nio/ByteBuffer;", false);
    }

    /*
     * Helper methods to create smaller constant instructions.
     * Will the bytecode be JIT'd to the same end result? Probably. Do I care? No.
     */

    private static void visitConstantInt(MethodVisitor methodVisitor, int addend) {
        switch (addend) {
            case -1 -> methodVisitor.visitInsn(ICONST_M1);
            case 0 -> methodVisitor.visitInsn(ICONST_0);
            case 1 -> methodVisitor.visitInsn(ICONST_1);
            case 2 -> methodVisitor.visitInsn(ICONST_2);
            case 3 -> methodVisitor.visitInsn(ICONST_3);
            case 4 -> methodVisitor.visitInsn(ICONST_4);
            case 5 -> methodVisitor.visitInsn(ICONST_5);
            default -> {
                if (Byte.MIN_VALUE <= addend && addend <= Byte.MAX_VALUE) {
                    methodVisitor.visitIntInsn(BIPUSH, addend);
                } else if (Short.MIN_VALUE <= addend && addend <= Short.MAX_VALUE) {
                    methodVisitor.visitIntInsn(SIPUSH, addend);
                } else {
                    methodVisitor.visitLdcInsn(addend);
                }
            }
        }
    }

    private static void visitConstantLong(MethodVisitor methodVisitor, long addend) {
        if (addend == 0L) {
            methodVisitor.visitInsn(LCONST_0);
        } else if (addend == 1L) {
            methodVisitor.visitInsn(LCONST_1);
        } else {
            methodVisitor.visitLdcInsn(addend);
        }
    }

    private record RawClass(String className, String classPath, byte[] bytes) {}
}
