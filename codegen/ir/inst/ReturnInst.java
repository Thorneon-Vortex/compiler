package codegen.ir.inst;

import codegen.ir.types.VoidType;
import codegen.ir.values.Value;

/**
 * ReturnInst - 函数返回指令
 * 例如: ret i32 %value 或 ret void
 */
public class ReturnInst extends Instruction {
    
    // 有返回值的构造函数
    public ReturnInst(Value returnValue) {
        super(new VoidType(), ""); // Return指令本身没有返回值
        if (returnValue != null) {
            addOperand(returnValue);
        }
    }
    
    // 无返回值的构造函数 (void return)
    public ReturnInst() {
        this(null);
    }
    
    public boolean hasReturnValue() {
        return getNumOperands() > 0;
    }
    
    public Value getReturnValue() {
        if (hasReturnValue()) {
            return getOperand(0);
        }
        return null;
    }
    
    @Override
    public String toString() {
        if (hasReturnValue()) {
            Value retVal = getReturnValue();
            return "ret " + retVal.toString();
        } else {
            return "ret void";
        }
    }
}
