package symbol;

import java.util.List;

// 函数符号
public class FuncSymbol extends Symbol {
    //private boolean isVoid;
    private List<ValueSymbol> params; // 存储形参的符号信息

    public FuncSymbol(String name, String typeName, int scopeId,List<ValueSymbol> params) {
        super(name, typeName, scopeId);
        //this.isVoid = isVoid;
        this.params = params;
    }

    public List<ValueSymbol> getParams() {
        return params;
    }

    // constructor...
}