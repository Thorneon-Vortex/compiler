package symbol;

// 变量/常量/形参 符号
public class ValueSymbol extends Symbol {


    public ValueSymbol(String name, String typeName, int scopeId) {
        super(name, typeName, scopeId);

    }

    public String getTypeName() {
        return super.getTypeName();
    }
    // 可能还需要存储维度信息
    
    // constructor...
}