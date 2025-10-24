package codegen.ir.inst;

import codegen.ir.values.BasicBlock;
import codegen.ir.types.Type;
import codegen.ir.values.User;

public abstract class Instruction extends User {
    private BasicBlock parent; // 指令所属的基本块

    public Instruction(Type type, String name) {
        super(type, name);
    }

    public BasicBlock getParent() { return parent; }
    public void setParent(BasicBlock parent) { this.parent = parent; }
}