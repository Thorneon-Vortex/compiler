package codegen.ir;

import codegen.ir.inst.*;
import codegen.ir.types.*;
import codegen.ir.values.Argument;
import codegen.ir.values.BasicBlock;
import codegen.ir.values.Function;

import java.util.Arrays;

/**
 * IRExample - 演示如何使用 IR 系统构建简单的 LLVM IR
 * 
 * 生成的 IR 相当于以下 C 代码：
 * int add(int a, int b) {
 *     return a + b;
 * }
 * 
 * int main() {
 *     int x = 5;
 *     int y = 3;
 *     int result = add(x, y);
 *     return result;
 * }
 */
public class IRExample {
    
    public static void demonstrateIR() {
        // 创建模块
        Module module = new Module("example");
        
        // 创建 IRBuilder
        IRBuilder builder = new IRBuilder();
        
        // 1. 创建 add 函数
        FunctionType addFuncType = new FunctionType(
            builder.getInt32Type(), 
            Arrays.asList(builder.getInt32Type(), builder.getInt32Type())
        );
        Function addFunc = new Function(addFuncType, "add");
        
        // 添加参数
        Argument argA = new Argument(builder.getInt32Type(), "a");
        Argument argB = new Argument(builder.getInt32Type(), "b");
        addFunc.addArgument(argA);
        addFunc.addArgument(argB);
        
        // 创建 add 函数的基本块
        BasicBlock addEntry = new BasicBlock("entry");
        addFunc.addBasicBlock(addEntry);
        builder.setInsertPoint(addEntry);
        
        // 生成 add 指令
        BinaryInst addResult = builder.createAdd(argA, argB, "sum");
        builder.createRet(addResult);
        
        module.addFunction(addFunc);
        
        // 2. 创建 main 函数
        FunctionType mainFuncType = new FunctionType(builder.getInt32Type());
        Function mainFunc = new Function(mainFuncType, "main");
        
        BasicBlock mainEntry = new BasicBlock("entry");
        mainFunc.addBasicBlock(mainEntry);
        builder.setInsertPoint(mainEntry);
        
        // 分配局部变量
        AllocaInst xAlloca = builder.createAlloca(builder.getInt32Type(), "x");
        AllocaInst yAlloca = builder.createAlloca(builder.getInt32Type(), "y");
        AllocaInst resultAlloca = builder.createAlloca(builder.getInt32Type(), "result");
        
        // 存储初始值
        builder.createStore(builder.getInt32(5), xAlloca);
        builder.createStore(builder.getInt32(3), yAlloca);
        
        // 加载值
        LoadInst xVal = builder.createLoad(xAlloca, "x_val");
        LoadInst yVal = builder.createLoad(yAlloca, "y_val");
        
        // 调用 add 函数
        CallInst callResult = builder.createCall(addFunc, Arrays.asList(xVal, yVal), "call_result");
        
        // 存储结果
        builder.createStore(callResult, resultAlloca);
        
        // 加载并返回结果
        LoadInst finalResult = builder.createLoad(resultAlloca, "final_result");
        builder.createRet(finalResult);
        
        module.addFunction(mainFunc);
        
        // 打印生成的 IR（简化版本）
        System.out.println("Generated IR Module: " + module.getName());
        System.out.println("Functions:");
        for (Function func : module.getFunctions()) {
            System.out.println("  Function: " + func.getName());
            for (BasicBlock bb : func.getBlocks()) {
                System.out.println("    BasicBlock: " + bb.getName());
                for (Instruction inst : bb.getInstructions()) {
                    System.out.println("      " + inst.toString());
                }
            }
        }
    }
    
    public static void main(String[] args) {
        demonstrateIR();
    }
}
