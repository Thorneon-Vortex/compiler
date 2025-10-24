package codegen.ir.values;
import codegen.ir.types.Type;
import codegen.ir.Use;

import java.util.LinkedList;
import java.util.List;

public abstract class Value {
    protected Type type;
    protected String name;

    // 关键：记录所有对这个Value的"使用"
    protected final List<Use> useList = new LinkedList<>();

    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public void addUse(Use use) { useList.add(use); }
    public void removeUse(Use use) { useList.remove(use); }

    // 替换所有对我的使用
    public void replaceAllUsesWith(Value newValue) {
        while (!useList.isEmpty()) {
            Use use = useList.get(0);
            use.setUsed(newValue); // 让使用者更新它的操作数
        }
    }

    // Getter methods
    public Type getType() { return type; }
    public String getName() { return name; }
    
    // 在IR中作为操作数时的字符串表示
    public String getAsOperand() {
        if (name != null && !name.isEmpty()) {
            return "%" + name;
        }
        return toString();
    }
    
    @Override
    public String toString() {
        return getType().toString() + " " + getAsOperand();
    }
}