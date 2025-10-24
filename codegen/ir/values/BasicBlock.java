package codegen.ir.values;
import codegen.ir.inst.Instruction;
import codegen.ir.types.LabelType;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class BasicBlock extends Value {
    private Function parent;
    private final List<Instruction> instructions = new LinkedList<>();
    
    // CFG 支持：前驱和后继基本块
    private final List<BasicBlock> predecessors = new ArrayList<>();
    private final List<BasicBlock> successors = new ArrayList<>();

    public BasicBlock(String name) {
        super(new LabelType(), name); // 基本块的类型是Label
    }
    
    public void addInstruction(Instruction inst) {
        instructions.add(inst);
        inst.setParent(this);
    }

    public List<Instruction> getInstructions() { return instructions; }
    
    public Function getParent() { return parent; }
    public void setParent(Function parent) { this.parent = parent; }
    
    // 获取最后一条指令，它必须是终结者指令
    public Instruction getTerminator() {
        if (instructions.isEmpty()) return null;
        return instructions.get(instructions.size() - 1);
    }
    
    // CFG 相关方法
    public List<BasicBlock> getPredecessors() { return new ArrayList<>(predecessors); }
    public List<BasicBlock> getSuccessors() { return new ArrayList<>(successors); }
    
    public void addPredecessor(BasicBlock pred) {
        if (!predecessors.contains(pred)) {
            predecessors.add(pred);
        }
    }
    
    public void addSuccessor(BasicBlock succ) {
        if (!successors.contains(succ)) {
            successors.add(succ);
            succ.addPredecessor(this);
        }
    }
    
    public void removePredecessor(BasicBlock pred) {
        predecessors.remove(pred);
    }
    
    public void removeSuccessor(BasicBlock succ) {
        if (successors.remove(succ)) {
            succ.removePredecessor(this);
        }
    }
    
    // 检查是否有终结者指令
    public boolean hasTerminator() {
        return !instructions.isEmpty() && isTerminator(getTerminator());
    }
    
    // 判断指令是否是终结者指令
    private boolean isTerminator(Instruction inst) {
        if (inst == null) return false;
        String className = inst.getClass().getSimpleName();
        return className.equals("ReturnInst") || className.equals("BranchInst");
    }
    
    // 获取第一条指令
    public Instruction getFirstInstruction() {
        return instructions.isEmpty() ? null : instructions.get(0);
    }
    
    // 在指定位置插入指令
    public void insertInstruction(int index, Instruction inst) {
        instructions.add(index, inst);
        inst.setParent(this);
    }
    
    // 在终结者指令前插入指令
    public void insertBeforeTerminator(Instruction inst) {
        if (hasTerminator()) {
            insertInstruction(instructions.size() - 1, inst);
        } else {
            addInstruction(inst);
        }
    }
    
    @Override
    public String getAsOperand() {
        return "%" + getName();
    }
}