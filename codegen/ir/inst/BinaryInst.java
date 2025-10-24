package codegen.ir.inst;

import codegen.ir.types.IntegerType;
import codegen.ir.values.Value;

// BinaryInst.java
public class BinaryInst extends Instruction {
    //enum尚未完成
    public enum OpCode { ADD, SUB, MUL, SDIV, SREM, ICMP_SLT, ICMP_SGT}
    private final OpCode opcode;
    
    public BinaryInst(String name, OpCode opcode, Value op1, Value op2) {
        // 算术运算返回i32，比较运算返回i1
        super(BinaryInst.isCompare(opcode) ? new IntegerType(1) : new IntegerType(32), name);
        this.opcode = opcode;
        addOperand(op1);
        addOperand(op2);
    }
    // ... isCompare(opcode) 辅助方法 ...
    //尚未完成
    public static boolean isCompare(OpCode opcode) {
        //找出比较运算符
        return opcode == OpCode.ICMP_SGT || opcode == OpCode.ICMP_SLT;
    }
    public OpCode getOpcode() { return opcode; }
    
    public Value getLeftOperand() { return getOperand(0); }
    public Value getRightOperand() { return getOperand(1); }
    
    @Override
    public String toString() {
        return getName() + " = " + opcode.toString().toLowerCase() + " " + 
               getType().toString() + " " + 
               getLeftOperand().getAsOperand() + ", " + 
               getRightOperand().getAsOperand();
    }
}