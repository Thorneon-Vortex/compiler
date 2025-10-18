package symbol;

import java.util.List;

// 代表一个符号（变量、常量、函数）
public abstract class Symbol implements Comparable<Symbol>{
    private String name;//本来是什么
    private String typeName; // "ConstInt", "IntFunc", etc. for output
    private int scopeId;
    
    // constructor and getters...
    public Symbol(String name, String typeName, int scopeId) {
        this.name = name;
        this.typeName = typeName;
        this.scopeId = scopeId;
    }
    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setScopeId(int scopeId) {
        this.scopeId = scopeId;
    }

//    public int compareTo(Symbol other) {
//        // 1. 主要排序键：比较 scopeId
//        int scopeCompare = Integer.compare(this.scopeId, other.scopeId);
//
//        if (scopeCompare != 0) {
//            // 如果 scopeId 不同，直接返回比较结果
//            return scopeCompare;
//        } else {
//            // 2. 次要排序键：如果 scopeId 相同，则比较声明的先后顺序
//            return Integer.compare(this.declarationOrder, other.declarationOrder);
//        }
//    }

    @Override
    public int compareTo(Symbol other) {
        return Integer.compare(this.scopeId, other.scopeId);
    }
    //重写toString
    @Override
    public String toString() {
        return scopeId + " " + name + " " + typeName;
    }
}

