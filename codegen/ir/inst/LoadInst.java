package codegen.ir.inst;

import codegen.ir.types.PointerType;
import codegen.ir.values.Value;

/**
 * LoadInst - 从内存中加载值
 * 例如: %1 = load i32, i32* %ptr
 */
public class LoadInst extends Instruction {
    
    public LoadInst(String name, Value pointer) {
        // Load指令的返回类型是指针指向的类型
        super(getLoadType(pointer), name);
        addOperand(pointer);
    }
    
    private static codegen.ir.types.Type getLoadType(Value pointer) {
        if (pointer.getType() instanceof PointerType) {
            return ((PointerType) pointer.getType()).getElementType();
        }
        throw new IllegalArgumentException("LoadInst requires a pointer operand");
    }
    
    public Value getPointerOperand() {
        return getOperand(0);
    }
    
    @Override
    public String toString() {
        return getName() + " = load " + getType().toString() + ", " + 
               getPointerOperand().toString();
    }
}
