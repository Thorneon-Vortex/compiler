package codegen.ir.inst;

import codegen.ir.types.Type;
import codegen.ir.types.PointerType;

// AllocaInst.java
public class AllocaInst extends Instruction {
    // alloca i32, align 4
    public AllocaInst(String name, Type allocatedType) {
        // Alloca指令本身返回一个指向被分配类型的指针
        super(new PointerType(allocatedType), name);
    }
    public Type getAllocatedType() { return ((PointerType)getType()).getElementType(); }
    
    @Override
    public String toString() {
        return getName() + " = alloca " + getAllocatedType().toString();
    }
}