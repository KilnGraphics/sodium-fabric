package me.jellysquid.mods.sodium.asm;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class AsmUtil {

    /*
     * Helper methods to create smaller constant instructions.
     * Will the bytecode be JIT'd to the same end result? Probably. Do I care? No.
     */

    public static void visitConstantInt(MethodVisitor methodVisitor, int value) {
        switch (value) {
            case -1 -> methodVisitor.visitInsn(ICONST_M1);
            case 0 -> methodVisitor.visitInsn(ICONST_0);
            case 1 -> methodVisitor.visitInsn(ICONST_1);
            case 2 -> methodVisitor.visitInsn(ICONST_2);
            case 3 -> methodVisitor.visitInsn(ICONST_3);
            case 4 -> methodVisitor.visitInsn(ICONST_4);
            case 5 -> methodVisitor.visitInsn(ICONST_5);
            default -> {
                if (Byte.MIN_VALUE <= value && value <= Byte.MAX_VALUE) {
                    methodVisitor.visitIntInsn(BIPUSH, value);
                } else if (Short.MIN_VALUE <= value && value <= Short.MAX_VALUE) {
                    methodVisitor.visitIntInsn(SIPUSH, value);
                } else {
                    methodVisitor.visitLdcInsn(value);
                }
            }
        }
    }

    public static void visitConstantLong(MethodVisitor methodVisitor, long value) {
        if (value == 0L) {
            methodVisitor.visitInsn(LCONST_0);
        } else if (value == 1L) {
            methodVisitor.visitInsn(LCONST_1);
        } else {
            methodVisitor.visitLdcInsn(value);
        }
    }
}
