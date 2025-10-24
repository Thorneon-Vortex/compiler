package codegen.ir.values;
import codegen.ir.types.FunctionType;

import java.util.ArrayList;
import java.util.List;

public class Function extends Value {
    private final List<Argument> arguments = new ArrayList<>();
    private final List<BasicBlock> blocks = new ArrayList<>();
    
    public Function(FunctionType type, String name) {
        super(type, name);
    }

    public void addBasicBlock(BasicBlock bb) { 
        blocks.add(bb); 
        bb.setParent(this);
    }
    
    public void addArgument(Argument arg) {
        arguments.add(arg);
    }
    
    public List<Argument> getArguments() { return arguments; }
    public List<BasicBlock> getBlocks() { return blocks; }
    public BasicBlock getEntryBlock() { return blocks.isEmpty() ? null : blocks.get(0); }
}