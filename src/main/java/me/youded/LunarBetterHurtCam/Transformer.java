package me.youded.LunarBetterHurtCam;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class Transformer implements ClassFileTransformer {
    private Float hurtfloat;

    Transformer(String args) {
        if (args != null && !args.isBlank() && Float.parseFloat(args) > -1)
            this.hurtfloat = Float.parseFloat(args);
        else if (System.getProperty("hurtanimationmodifier") != null
                && !System.getProperty("hurtanimationmodifier").isBlank())
            this.hurtfloat = Float.parseFloat(System.getProperty("hurtanimationmodifier"));
        else
            this.hurtfloat = 6.0F;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classfileBuffer == null || classfileBuffer.length == 0) {
            return new byte[0];
        }
        if (!className.startsWith("net/minecraft")) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        if (cr.getAccess() == Opcodes.ACC_SUPER + Opcodes.ACC_PUBLIC) {
            ClassNode cn = new ClassNode();

            cr.accept(cn, 0);

            for (MethodNode method : cn.methods) {
                if (method.desc.endsWith("F)V")) {
                    int amountofeightthousand = 0;
                    int amountoffourteen = 0;
                    for (AbstractInsnNode insn : method.instructions) {
                        if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.equals(14.0F)) {
                            amountoffourteen++;
                        }
                        if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.equals(8000.0F)) {
                            amountofeightthousand++;
                        }
                    }
                    if (amountoffourteen == 1 && amountofeightthousand == 1) {
                        for (AbstractInsnNode insn : method.instructions) {
                            if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.equals(14.0F)) {
                                method.instructions.set(insn, new LdcInsnNode(this.hurtfloat));
                                ClassWriter cw = new ClassWriter(cr, 0);
                                cn.accept(cw);
                                return cw.toByteArray();
                            }
                        }
                    }
                }
            }
        }
        return classfileBuffer;
    }
}