package codegen.ir.inst;

import codegen.ir.values.BasicBlock;
import codegen.ir.types.VoidType;
import codegen.ir.values.Value;

/**
 * BranchInst - 分支指令，支持条件和无条件跳转
 * 无条件: br label %target
 * 条件: br i1 %cond, label %true_target, label %false_target
 */
public class BranchInst extends Instruction {
    
    // 无条件分支构造函数
    public BranchInst(BasicBlock target) {
        super(new VoidType(), ""); // Branch指令没有返回值
        addOperand(target);
    }
    
    // 条件分支构造函数
    public BranchInst(Value condition, BasicBlock trueTarget, BasicBlock falseTarget) {
        super(new VoidType(), "");
        addOperand(condition);
        addOperand(trueTarget);
        addOperand(falseTarget);
    }
    
    public boolean isConditional() {
        return getNumOperands() == 3;
    }
    
    public boolean isUnconditional() {
        return getNumOperands() == 1;
    }
    
    // 获取条件（仅对条件分支有效）
    public Value getCondition() {
        if (isConditional()) {
            return getOperand(0);
        }
        return null;
    }
    
    // 获取目标基本块（无条件分支）
    public BasicBlock getTarget() {
        if (isUnconditional()) {
            return (BasicBlock) getOperand(0);
        }
        return null;
    }
    
    // 获取真分支目标（条件分支）
    public BasicBlock getTrueTarget() {
        if (isConditional()) {
            return (BasicBlock) getOperand(1);
        }
        return null;
    }
    
    // 获取假分支目标（条件分支）
    public BasicBlock getFalseTarget() {
        if (isConditional()) {
            return (BasicBlock) getOperand(2);
        }
        return null;
    }
    
    @Override
    public String toString() {
        if (isUnconditional()) {
            return "br label " + getTarget().getAsOperand();
        } else {
            return "br " + getCondition().toString() + 
                   ", label " + getTrueTarget().getAsOperand() + 
                   ", label " + getFalseTarget().getAsOperand();
        }
    }
}
