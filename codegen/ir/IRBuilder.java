package codegen.ir;

import codegen.ir.inst.*;
import codegen.ir.types.*;
import codegen.ir.values.*;

import java.util.List;

/**
 * IRBuilder - IR 构建器工具类，提供便捷的 IR 构建方法
 */
public class IRBuilder {
    private BasicBlock currentBlock;
    private int tempCounter = 0;
    
    public IRBuilder() {}
    
    public IRBuilder(BasicBlock block) {
        this.currentBlock = block;
    }
    
    public void setInsertPoint(BasicBlock block) {
        this.currentBlock = block;
    }
    
    public BasicBlock getCurrentBlock() {
        return currentBlock;
    }
    
    // 生成临时变量名
    private String genTempName() {
        return "t" + (tempCounter++);
    }
    
    // 创建基本类型
    public IntegerType getInt32Type() { return new IntegerType(32); }
    public IntegerType getInt1Type() { return new IntegerType(1); }
    public VoidType getVoidType() { return new VoidType(); }
    public PointerType getPointerType(Type elementType) { return new PointerType(elementType); }
    public ArrayType getArrayType(int size, Type elementType) { return new ArrayType(size, elementType); }
    
    // 创建常量
    public ConstantInt getInt32(int value) { return new ConstantInt(value); }
    
    // 创建指令并插入到当前基本块
    private void insertInstruction(Instruction inst) {
        if (currentBlock != null) {
            currentBlock.addInstruction(inst);
        }
    }
    
    // Alloca 指令
    public AllocaInst createAlloca(Type type, String name) {
        AllocaInst inst = new AllocaInst(name != null ? name : genTempName(), type);
        insertInstruction(inst);
        return inst;
    }
    
    public AllocaInst createAlloca(Type type) {
        return createAlloca(type, null);
    }
    
    // Store 指令
    public StoreInst createStore(Value value, Value pointer) {
        StoreInst inst = new StoreInst(value, pointer);
        insertInstruction(inst);
        return inst;
    }
    
    // Load 指令
    public LoadInst createLoad(Value pointer, String name) {
        LoadInst inst = new LoadInst(name != null ? name : genTempName(), pointer);
        insertInstruction(inst);
        return inst;
    }
    
    public LoadInst createLoad(Value pointer) {
        return createLoad(pointer, null);
    }
    
    // 二元运算指令
    public BinaryInst createAdd(Value left, Value right, String name) {
        BinaryInst inst = new BinaryInst(name != null ? name : genTempName(), 
                                       BinaryInst.OpCode.ADD, left, right);
        insertInstruction(inst);
        return inst;
    }
    
    public BinaryInst createAdd(Value left, Value right) {
        return createAdd(left, right, null);
    }
    
    public BinaryInst createSub(Value left, Value right, String name) {
        BinaryInst inst = new BinaryInst(name != null ? name : genTempName(), 
                                       BinaryInst.OpCode.SUB, left, right);
        insertInstruction(inst);
        return inst;
    }
    
    public BinaryInst createSub(Value left, Value right) {
        return createSub(left, right, null);
    }
    
    public BinaryInst createMul(Value left, Value right, String name) {
        BinaryInst inst = new BinaryInst(name != null ? name : genTempName(), 
                                       BinaryInst.OpCode.MUL, left, right);
        insertInstruction(inst);
        return inst;
    }
    
    public BinaryInst createMul(Value left, Value right) {
        return createMul(left, right, null);
    }
    
    public BinaryInst createICmpSLT(Value left, Value right, String name) {
        BinaryInst inst = new BinaryInst(name != null ? name : genTempName(), 
                                       BinaryInst.OpCode.ICMP_SLT, left, right);
        insertInstruction(inst);
        return inst;
    }
    
    public BinaryInst createICmpSLT(Value left, Value right) {
        return createICmpSLT(left, right, null);
    }
    
    // GEP 指令
    public GetElementPtrInst createGEP(Value basePtr, List<Value> indices, String name) {
        GetElementPtrInst inst = new GetElementPtrInst(name != null ? name : genTempName(), 
                                                     basePtr, indices);
        insertInstruction(inst);
        return inst;
    }
    
    public GetElementPtrInst createGEP(Value basePtr, List<Value> indices) {
        return createGEP(basePtr, indices, null);
    }
    
    // Call 指令
    public CallInst createCall(Function callee, List<Value> args, String name) {
        CallInst inst = new CallInst(name != null ? name : genTempName(), callee, args);
        insertInstruction(inst);
        return inst;
    }
    
    public CallInst createCall(Function callee, List<Value> args) {
        return createCall(callee, args, null);
    }
    
    // Return 指令
    public ReturnInst createRet(Value value) {
        ReturnInst inst = new ReturnInst(value);
        insertInstruction(inst);
        return inst;
    }
    
    public ReturnInst createRetVoid() {
        ReturnInst inst = new ReturnInst();
        insertInstruction(inst);
        return inst;
    }
    
    // Branch 指令
    public BranchInst createBr(BasicBlock target) {
        BranchInst inst = new BranchInst(target);
        insertInstruction(inst);
        // 建立 CFG 连接
        if (currentBlock != null) {
            currentBlock.addSuccessor(target);
        }
        return inst;
    }
    
    public BranchInst createCondBr(Value condition, BasicBlock trueTarget, BasicBlock falseTarget) {
        BranchInst inst = new BranchInst(condition, trueTarget, falseTarget);
        insertInstruction(inst);
        // 建立 CFG 连接
        if (currentBlock != null) {
            currentBlock.addSuccessor(trueTarget);
            currentBlock.addSuccessor(falseTarget);
        }
        return inst;
    }
}
