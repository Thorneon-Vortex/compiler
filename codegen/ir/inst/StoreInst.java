package codegen.ir.inst;

import codegen.ir.types.VoidType;
import codegen.ir.values.Value;

// StoreInst.java
public class StoreInst extends Instruction {
    public StoreInst(Value value, Value pointer) {
        super(new VoidType(), ""); // Store没有返回值，名字也无意义
        addOperand(value);   // 第0个操作数是 value
        addOperand(pointer); // 第1个操作数是 pointer
    }
    public Value getValueOperand() { return getOperand(0); }
    public Value getPointerOperand() { return getOperand(1); }
    
    @Override
    public String toString() {
        return "store " + getValueOperand().toString() + ", " + 
               getPointerOperand().toString();
    }
}