package codegen.ir.inst;

import codegen.ir.types.ArrayType;
import codegen.ir.types.PointerType;
import codegen.ir.types.Type;
import codegen.ir.values.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * GetElementPtrInst - 计算数组或结构体元素的地址
 * 例如: %ptr = getelementptr [10 x i32], [10 x i32]* %arr, i32 0, i32 %index
 */
public class GetElementPtrInst extends Instruction {
    
    public GetElementPtrInst(String name, Value basePtr, List<Value> indices) {
        super(calculateResultType(basePtr, indices), name);
        addOperand(basePtr);
        for (Value index : indices) {
            addOperand(index);
        }
    }
    
    public GetElementPtrInst(String name, Value basePtr, Value... indices) {
        this(name, basePtr, List.of(indices));
    }
    
    private static Type calculateResultType(Value basePtr, List<Value> indices) {
        Type currentType = basePtr.getType();
        
        // 第一个索引处理指针解引用
        if (currentType instanceof PointerType && !indices.isEmpty()) {
            currentType = ((PointerType) currentType).getElementType();
            
            // 处理剩余的索引
            for (int i = 1; i < indices.size(); i++) {
                if (currentType instanceof ArrayType) {
                    currentType = ((ArrayType) currentType).getElementType();
                } else {
                    break; // 如果不是数组类型，停止处理
                }
            }
        }
        
        // GEP 总是返回指针类型
        return new PointerType(currentType);
    }
    
    public Value getPointerOperand() {
        return getOperand(0);
    }
    
    public List<Value> getIndices() {
        List<Value> indices = new ArrayList<>();
        for (int i = 1; i < getNumOperands(); i++) {
            indices.add(getOperand(i));
        }
        return indices;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" = getelementptr ");
        
        // 添加基础类型信息
        Value basePtr = getPointerOperand();
        if (basePtr.getType() instanceof PointerType) {
            Type elementType = ((PointerType) basePtr.getType()).getElementType();
            sb.append(elementType.toString()).append(", ");
        }
        
        sb.append(basePtr.toString());
        
        // 添加索引
        for (Value index : getIndices()) {
            sb.append(", ").append(index.toString());
        }
        
        return sb.toString();
    }
}
