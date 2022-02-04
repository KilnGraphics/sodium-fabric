package me.jellysquid.mods.sodium.asm;

import com.google.common.primitives.Primitives;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import me.jellysquid.mods.sodium.opengl.attribute.VertexAttributeBinding;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexType;
import me.jellysquid.mods.sodium.util.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.*;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;

import static org.objectweb.asm.Opcodes.*;

public class AsmVertexSinkClassFactory {

    /**
     * The caller of this method is responsible for keeping track of each instance generated,
     * as they are singletons. If this is called with the same parameters twice, it will
     * probably crash.
     */
    public static Class<GeneratedVertexSink> generateVertexSinkClass(VertexAttributeBinding[] attributeBindings, boolean packParams) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // we need to create the class in the current package because we don't have access to put it anywhere else.
        String currentPackagePath = lookup.lookupClass().getPackageName().replace('.', '/') + '/';

        // create new raw class
        byte[] rawClassBytes = createSinkClassBytes(attributeBindings, packParams, currentPackagePath);

        Path path = Path.of("C:", "Users", "w", "Desktop", "GeneratedVertexSink" + Integer.toUnsignedString(Arrays.hashCode(attributeBindings)) + ".class");
        Files.write(path, rawClassBytes);

        // define class using MethodHandles
        @SuppressWarnings("unchecked")
        Class<GeneratedVertexSink> generatedClass = (Class<GeneratedVertexSink>) lookup.defineClass(rawClassBytes);
        return generatedClass;
    }

    private static byte[] createSinkClassBytes(VertexAttributeBinding[] attributeBindings, boolean packParams, String packagePath) {

        String className = "GeneratedVertexSink" + Integer.toUnsignedString(Arrays.hashCode(attributeBindings)); // TODO: create better naming scheme
        String qualifiedClassName = packagePath + className;

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        classWriter.visit(V17, ACC_PUBLIC | ACC_SUPER, qualifiedClassName, null, "me/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferWriterUnsafe", new String[] { "me/jellysquid/mods/sodium/asm/GeneratedVertexSink" });

        classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC, "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;", null, null);
        fieldVisitor.visitEnd();

        //// Visit constructor
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType;)V", "(Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType<*>;)V", null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(32, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "me/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferWriterUnsafe", "<init>", "(Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType;)V", false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLineNumber(33, label1);
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", "L" + qualifiedClassName + ";", null, label0, label2, 0);
        methodVisitor.visitLocalVariable("backingBuffer", "Lme/jellysquid/mods/sodium/render/vertex/buffer/VertexBufferView;", null, label0, label2, 1);
        methodVisitor.visitLocalVariable("vertexType", "Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType;", "Lme/jellysquid/mods/sodium/render/vertex/type/BufferVertexType<*>;", label0, label2, 2);
        methodVisitor.visitMaxs(3, 3);
        methodVisitor.visitEnd();

        MappedParameter[] mappedParameters = createMappedParameterArray(attributeBindings, packParams);

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "writeVertex", createWriteVertexSignature(mappedParameters), null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, qualifiedClassName, "writePointer", "J");
        // the pointer local idx is located after all the parameter locals.
        int pointerLocalIdx = Arrays.stream(mappedParameters).mapToInt(MappedParameter::size).reduce(1, (total, next) -> total += MathUtil.ceilDiv(next, 4));
        methodVisitor.visitVarInsn(LSTORE, pointerLocalIdx);

        // 0 is the lvt index for "this"
        int localIdx = 1;
        for (MappedParameter mappedParameter : mappedParameters) {
            methodVisitor.visitVarInsn(LLOAD, pointerLocalIdx);

            int pointerOffset = mappedParameter.offset;
            if (pointerOffset > 0) {
                AsmUtil.visitConstantLong(methodVisitor, pointerOffset);
                methodVisitor.visitInsn(LADD);
            }

            visitLoad(methodVisitor, mappedParameter, localIdx);
            visitMemoryUtilPut(methodVisitor, mappedParameter);

            // each lvt slot is 4 bytes
            // if a parameter takes 5 bytes, give it 2 lvt slots,
            // if a parameter takes 4 bytes, give it 1 lvt slot
            localIdx += MathUtil.ceilDiv(mappedParameter.size, 4);
        }

        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, qualifiedClassName, "advance", "()V", false);
        methodVisitor.visitInsn(RETURN);
        // the pointer local index is the last lvt index
        // add 1 to that because the pointer takes up two local slots
        // add 1 to that to include the lvt index 0
        methodVisitor.visitMaxs(4, pointerLocalIdx + 2);
        methodVisitor.visitEnd();

        //// Visit getWriteVertexHandle
        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getWriteVertexHandle", "()Ljava/lang/invoke/MethodHandle;", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(GETSTATIC, qualifiedClassName, "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();

        //// Visit static method handle lookup and creation
        methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        Label label3 = new Label();
        Label label4 = new Label();
        Label label5 = new Label();
        methodVisitor.visitTryCatchBlock(label3, label4, label5, "java/lang/NoSuchMethodException");
        methodVisitor.visitTryCatchBlock(label3, label4, label5, "java/lang/IllegalAccessException");
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitVarInsn(ASTORE, 0);
        methodVisitor.visitLabel(label3);
        // keep void as the return type always
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
        // infer types based on vertex attribute types
        methodVisitor.visitIntInsn(BIPUSH, mappedParameters.length);
        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < mappedParameters.length; i++) {
            MappedParameter mappedParameter = mappedParameters[i];
            methodVisitor.visitInsn(DUP);
            AsmUtil.visitConstantInt(methodVisitor, i);
            visitClassObject(methodVisitor, mappedParameter.type);
            methodVisitor.visitInsn(AASTORE);
        }
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
        methodVisitor.visitLdcInsn(Type.getType("L" + qualifiedClassName + ";"));
        methodVisitor.visitLdcInsn("writeVertex");
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        methodVisitor.visitVarInsn(ASTORE, 0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "type", "()Ljava/lang/invoke/MethodType;", false);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitLdcInsn(Type.getType("Lme/jellysquid/mods/sodium/asm/GeneratedVertexSink;"));
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "changeParameterType", "(ILjava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        methodVisitor.visitVarInsn(ASTORE, 0);
        methodVisitor.visitLabel(label4);
        Label label6 = new Label();
        methodVisitor.visitJumpInsn(GOTO, label6);
        methodVisitor.visitLabel(label5);
        // error handling for if the method handle isn't found
        methodVisitor.visitFrame(Opcodes.F_FULL, 1, new Object[]{"java/lang/invoke/MethodHandle"}, 1, new Object[]{"java/lang/ReflectiveOperationException"});
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "me/jellysquid/mods/sodium/SodiumClientMod", "logger", "()Lorg/apache/logging/log4j/Logger;", false);
        methodVisitor.visitLdcInsn("Unable to locate method writeVertex for method handle");
        methodVisitor.visitVarInsn(ALOAD, 1);
        methodVisitor.visitMethodInsn(INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V", true);
        methodVisitor.visitLabel(label6);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitFieldInsn(PUTSTATIC, qualifiedClassName, "WRITE_VERTEX_HANDLE", "Ljava/lang/invoke/MethodHandle;");
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(5, 2);
        methodVisitor.visitEnd();

        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    private static void visitClassObject(MethodVisitor methodVisitor, Class<?> klass) {
        if (klass.isPrimitive()) {
            String wrapperClassName = Type.getInternalName(Primitives.wrap(klass));
            methodVisitor.visitFieldInsn(GETSTATIC, wrapperClassName, "TYPE", "Ljava/lang/Class;");
        } else {
            methodVisitor.visitLdcInsn(Type.getType(klass));
        }
    }

    private static void visitLoad(MethodVisitor methodVisitor, MappedParameter mappedParameter, int localIdx) {
        Class<?> type = mappedParameter.type;
        if (type.equals(byte.class) || type.equals(short.class) || type.equals(int.class)) {
            methodVisitor.visitVarInsn(ILOAD, localIdx);
        } else if (type.equals(float.class)) {
            methodVisitor.visitVarInsn(FLOAD, localIdx);
        } else if (type.equals(long.class)) {
            methodVisitor.visitVarInsn(LLOAD, localIdx);
        } else if (type.equals(double.class)) {
            methodVisitor.visitVarInsn(DLOAD, localIdx);
        } else {
            throw new IllegalArgumentException("Unknown parameter type: " + type.getName());
        }
    }

    private static String createWriteVertexSignature(MappedParameter[] mappedParameters) {
        Type[] typeArray = Arrays.stream(mappedParameters).map(mp -> Type.getType(mp.type)).toArray(Type[]::new);
        // always return void
        return Type.getMethodDescriptor(Type.VOID_TYPE, typeArray);
    }

    private static void visitMemoryUtilPut(MethodVisitor methodVisitor, MappedParameter mappedParameter) {
        // always return void, long pointer is always first parameter
        String methodDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, Type.LONG_TYPE, Type.getType(mappedParameter.type));
        // get type name, capitalize first letter to create method name
        String typeName = mappedParameter.type.getName();
        String methodName = "memPut" + typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "org/lwjgl/system/MemoryUtil", methodName, methodDescriptor, false);
    }

    /**
     * Converts an array of VertexAttributeBindings into a sorted array of parameters for the method,
     * splitting up vectors and other types with multiple entries into their own parameters.
     */
    private static MappedParameter[] createMappedParameterArray(VertexAttributeBinding[] attributeBindings, boolean packParams) {
        // create sorted array to make sure no empty spaces
        VertexAttributeBinding[] sortedAttributeBindings = attributeBindings.clone();
        Arrays.sort(sortedAttributeBindings, Comparator.comparingInt(VertexAttributeBinding::getIndex));

        // calculate maximum size for array initialization
        // this number may be smaller if packParams is true
        int maximumSize = Arrays.stream(sortedAttributeBindings).mapToInt(VertexAttributeBinding::getCount).sum();
        MappedParameter[] mappedParameters = new MappedParameter[maximumSize];

        int arrayIdx = 0;
        for (VertexAttributeBinding attributeBinding : sortedAttributeBindings) {
            int glFormat = attributeBinding.getFormat();
            Class<?> typeClass = getGlFormatClass(glFormat);
            int typeSize = getTypeSize(typeClass);
            int attribCount = attributeBinding.getCount();

            if (packParams && isTypePackable(typeClass)) {
                boolean packed = false;

                int currentTotalSize = typeSize * attribCount;
                if (currentTotalSize > Integer.BYTES) {
                    typeClass = long.class;
                    packed = true;
                } else if (currentTotalSize > Short.BYTES) {
                    typeClass = int.class;
                    packed = true;
                } else if (currentTotalSize > Byte.BYTES) {
                    typeClass = short.class;
                    packed = true;
                }

                if (packed) {
                    // recalculate size and count
                    typeSize = getTypeSize(typeClass);
                    attribCount = MathUtil.ceilDiv(currentTotalSize, typeSize);
                }
            }

            for (int i = 0; i < attribCount; i++) {
                // size is needed so we can see how many slots it takes in the local variable table
                mappedParameters[arrayIdx + i] = new MappedParameter(typeClass, attributeBinding.getOffset() + (i * typeSize), typeSize);
            }
            arrayIdx += attribCount;
        }

        // trim trailing nulls
        if (maximumSize > arrayIdx + 1) {
            return Arrays.copyOf(mappedParameters, arrayIdx);
        } else {
            return mappedParameters;
        }
    }

    private static int getTypeSize(Class<?> type) {
        try {
            // get the value of the static BYTES field that exists in all primitive classes
            if (!type.isPrimitive()) throw new IllegalArgumentException("Type needs to be primitive");
            return (int) Primitives.wrap(type).getField("BYTES").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            // this should never happen
            throw new RuntimeException("Unable to find bytes field for class " + type.getName(), e);
        }
    }

    private static boolean isTypePackable(Class<?> type) {
        return type.equals(boolean.class) || type.equals(byte.class) || type.equals(short.class) || type.equals(char.class) || type.equals(int.class);
    }

    @NotNull
    private static Class<?> getGlFormatClass(int glFormat) {
        return switch (glFormat) {
            case GL11C.GL_FLOAT -> float.class;
            case GL11C.GL_DOUBLE -> double.class;
            case GL11C.GL_BYTE, GL11C.GL_UNSIGNED_BYTE -> byte.class;
            case GL11C.GL_SHORT, GL11C.GL_UNSIGNED_SHORT -> short.class;
            case GL11C.GL_INT, GL11C.GL_UNSIGNED_INT, GL33C.GL_INT_2_10_10_10_REV, GL12C.GL_UNSIGNED_INT_2_10_10_10_REV, GL30C.GL_UNSIGNED_INT_10F_11F_11F_REV -> int.class;
            case GL30C.GL_HALF_FLOAT -> throw new IllegalArgumentException("Half floats unsupported");
            case GL41C.GL_FIXED -> throw new IllegalArgumentException("Fixed points unsupported");
            default -> throw new IllegalArgumentException("Unknown attribute format: " + glFormat);
        };
    }

    private record MappedParameter(Class<?> type, int offset, int size) {}

}
