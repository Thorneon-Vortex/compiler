package ast.topLevelNodes;// ast/FuncParam.java
import ast.Node;
import ast.declarationNodes.VarDecl;
import frontend.Token;
import symbol.ValueSymbol;

/**
 * 对应文法: FuncFParam → BType Ident ['[' ']']
 */
public class FuncParam extends Node {
    public final Token bType; // 'int'
    public final Token ident;
    public final boolean isArray; // 是否是数组类型 (a[])

    public FuncParam(Token bType, Token ident, boolean isArray) {
        super(bType.lineNum());
        this.bType = bType;
        this.ident = ident;
        this.isArray = isArray;
    }

    public ValueSymbol toValueSymbol(int scopeId) {
        if (isArray) {
            return new ValueSymbol(ident.value(), "IntArray", scopeId);
        } else {
            return new ValueSymbol(ident.value(), "Int", scopeId);
        }

    }
}