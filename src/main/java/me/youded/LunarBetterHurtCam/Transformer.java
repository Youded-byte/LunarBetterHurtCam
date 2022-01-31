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
    private boolean found;

    
    Transformer(String args){
        if(args != null && !args.isBlank() && Float.parseFloat(args) > -1)
            this.hurtfloat = Float.parseFloat(args);
        else if(System.getProperty("hurtanimationmodifier") != null && !System.getProperty("hurtanimationmodifier").isBlank())
            this.hurtfloat = Float.parseFloat(System.getProperty("hurtanimationmodifier"));
        else
            this.hurtfloat = 6.0F;
            
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classfileBuffer == null || classfileBuffer.length == 0) {
            return new byte[0];
        }

        if(!className.startsWith("net/minecraft")) {
            return classfileBuffer;
        }

        ClassReader cr = new ClassReader(classfileBuffer);
        if(!this.found && cr.getInterfaces().length == 2 && "java/lang/Object".equals(cr.getSuperName())) {
            ClassNode cn = new ClassNode();

            cr.accept(cn, 0);

            for (MethodNode method : cn.methods) {
                if ("(F)V".equals(method.desc) && method.access == 1
                && method.instructions.get(0).getOpcode() == -1
                && method.instructions.get(1).getOpcode() == -1
                && method.instructions.get(2).getOpcode() == 25
                && method.instructions.get(3).getOpcode() == 180
                && method.instructions.get(4).getOpcode() == 182
                && method.instructions.get(5).getOpcode() == 193
                && method.instructions.get(6).getOpcode() == 153
                && method.instructions.get(7).getOpcode() == -1
                && method.instructions.get(8).getOpcode() == -1
                && method.instructions.get(9).getOpcode() == 25
                && method.instructions.get(10).getOpcode() == 180
                && method.instructions.get(11).getOpcode() == 182
                && method.instructions.get(12).getOpcode() == 192
                && method.instructions.get(13).getOpcode() == 58
                ) {
                    for(AbstractInsnNode insn : method.instructions) {
                        if(insn.getOpcode() == Opcodes.LDC && (Float)((LdcInsnNode)insn).cst == 14.0F) {
                            this.found = true;
                            method.instructions.set(insn, new LdcInsnNode(this.hurtfloat));
                            ClassWriter cw = new ClassWriter(cr, 0);
                            cn.accept(cw);
                            return cw.toByteArray();
                        }
                    }
                }
            }
        }
        return classfileBuffer;
    }
}