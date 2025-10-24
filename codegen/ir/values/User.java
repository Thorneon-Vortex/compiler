package codegen.ir.values;
import codegen.ir.types.Type;
import codegen.ir.Use;

import java.util.ArrayList;
import java.util.List;

public class User extends Value {
    // 关键：记录这个User对其他Value的"使用"
    protected final List<Use> operands = new ArrayList<>();

    public User(Type type, String name) {
        super(type, name);
    }

    public Value getOperand(int index) {
        return operands.get(index).getUsed();
    }

    public void setOperand(int index, Value value) {
        operands.get(index).setUsed(value);
    }

    public int getNumOperands() {
        return operands.size();
    }

    // 关键：添加操作数时，建立Use关系
    protected void addOperand(Value value) {
        Use use = new Use(this, value);
        operands.add(use);
    }
}