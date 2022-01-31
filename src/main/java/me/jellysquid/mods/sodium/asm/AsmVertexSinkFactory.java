package me.jellysquid.mods.sodium.asm;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.UUID;

import me.jellysquid.mods.sodium.opengl.attribute.VertexFormat;
import me.jellysquid.mods.sodium.opengl.types.IntType;
import me.jellysquid.mods.sodium.render.sequence.SequenceBuilder;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class AsmVertexSinkFactory {

    /**
     * The caller of this method is responsible for keeping track of each instance generated,
     * as they are singletons. If this is called with the same parameters twice, it will
     * probably crash.
     */
    public static SequenceBuilder generateSequenceBuilder(VertexFormat<?> vertexFormat) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // we need to create the class in the current package because we don't have access to put it anywhere else.
        String currentPackagePath = lookup.lookupClass().getPackageName().replace('.', '/') + '/';

        // create new raw class
        byte[] rawClassBytes = createSinkClassBytes(vertexFormat, currentPackagePath);

        // define class using MethodHandles
        Class<?> generatedClass = lookup.defineClass(rawClassBytes);

        // create instance of generated class using no-args constructor0

        Constructor<?> noArgsConstructor = generatedClass.getConstructor();
        return (SequenceBuilder) noArgsConstructor.newInstance();
    }

    private static byte[] createSinkClassBytes(VertexFormat<?> vertexFormat, String packagePath) {

        String className = UUID.randomUUID().toString();

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, packagePath + className, null, "me/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferWriterUnsafe", new String[] { "me/jellysquid/mods/sodium/asm/GeneratedVertexSink" });

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;", null, null);
        fieldVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "createWriteVertexHandle", "()Ljava/lang/invoke/MethodHandle;", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
        methodVisitor.visitLdcInsn(Type.getType("Lme/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink;"));
        methodVisitor.visitLdcInsn("writeVertex");
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitIntInsn(BIPUSH, 8);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_2);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_3);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_4);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_5);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitIntInsn(BIPUSH, 6);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitIntInsn(BIPUSH, 7);
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
        methodVisitor.visitInsn(AASTORE);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(9, 0);
        methodVisitor.visitEnd();

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;)V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitFieldInsn(GETSTATIC, "me/jellysquid/mods/sodium/interop/vanilla/vertex/VanillaVertexFormats", "QUADS", "Lme/jellysquid/mods/sodium/interop/vanilla/vertex/VanillaVertexType;");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "me/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferWriterUnsafe", "<init>", "(Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType;)V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(3, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeVertex", "(FFFIFFIII)V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "me/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink", "writePointer", "J");
            methodVisitor.visitVarInsn(LSTORE, 10);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitVarInsn(FLOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutFloat", "(JF)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(4L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(FLOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutFloat", "(JF)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(8L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(FLOAD, 3);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutFloat", "(JF)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(12L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(ILOAD, 4);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutInt", "(JI)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(16L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(FLOAD, 5);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutFloat", "(JF)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(20L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(FLOAD, 6);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutFloat", "(JF)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(24L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(ILOAD, 8);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutInt", "(JI)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(28L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(ILOAD, 7);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutInt", "(JI)V", false);
            methodVisitor.visitVarInsn(LLOAD, 10);
            methodVisitor.visitLdcInsn(new Long(32L));
            methodVisitor.visitInsn(LADD);
            methodVisitor.visitVarInsn(ILOAD, 9);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", "memPutInt", "(JI)V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "me/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink", "advance", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(4, 12);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getWriteVertexHandle", "()Ljava/lang/invoke/MethodHandle;", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitFieldInsn(GETSTATIC, "me/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink", "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;");
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitMethodInsn(INVOKESTATIC, "me/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink", "createWriteVertexHandle", "()Ljava/lang/invoke/MethodHandle;", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "me/jellysquid/mods/sodium/asm/ExampleGeneratedVertexSink", "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;");
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
