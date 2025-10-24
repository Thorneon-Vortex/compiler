package codegen.ir.inst;

import codegen.ir.values.Function;
import codegen.ir.types.FunctionType;
import codegen.ir.values.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * CallInst - 函数调用指令
 * 例如: %result = call i32 @func(i32 %arg1, i32 %arg2)
 *      call void @print(i32 %value)
 */
public class CallInst extends Instruction {
    
    public CallInst(String name, Function callee, List<Value> arguments) {
        super(getCallReturnType(callee), name);
        addOperand(callee);
        for (Value arg : arguments) {
            addOperand(arg);
        }
    }
    
    public CallInst(String name, Function callee, Value... arguments) {
        this(name, callee, List.of(arguments));
    }
    
    // 对于 void 函数的调用，不需要名字
    public CallInst(Function callee, List<Value> arguments) {
        this("", callee, arguments);
    }
    
    public CallInst(Function callee, Value... arguments) {
        this("", callee, List.of(arguments));
    }
    
    private static codegen.ir.types.Type getCallReturnType(Function callee) {
        if (callee.getType() instanceof FunctionType) {
            return ((FunctionType) callee.getType()).getReturnType();
        }
        throw new IllegalArgumentException("CallInst requires a function operand");
    }
    
    public Function getCallee() {
        return (Function) getOperand(0);
    }
    
    public List<Value> getArguments() {
        List<Value> args = new ArrayList<>();
        for (int i = 1; i < getNumOperands(); i++) {
            args.add(getOperand(i));
        }
        return args;
    }
    
    public Value getArgument(int index) {
        if (index >= 0 && index < getNumOperands() - 1) {
            return getOperand(index + 1);
        }
        throw new IndexOutOfBoundsException("Argument index out of bounds: " + index);
    }
    
    public int getNumArguments() {
        return getNumOperands() - 1;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // 如果有返回值，添加赋值部分
        if (getName() != null && !getName().isEmpty()) {
            sb.append(getName()).append(" = ");
        }
        
        sb.append("call ").append(getType().toString()).append(" ");
        sb.append(getCallee().getAsOperand()).append("(");
        
        // 添加参数
        List<Value> args = getArguments();
        for (int i = 0; i < args.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(args.get(i).toString());
        }
        
        sb.append(")");
        return sb.toString();
    }
}
