package ast.topLevelNodes;// ast/FuncDef.java
import ast.Declaration;
import ast.Statement;
import ast.statementNodes.Block;
import ast.statementNodes.ReturnStmt;
import frontend.Token;
import symbol.ValueSymbol;

import java.util.ArrayList;
import java.util.List;


/**
 * 对应文法: FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
 *          MainFuncDef → 'int' 'main' '(' ')' Block
 */
public class FuncDef extends Declaration {
    public final Token funcType; // 'void' 或 'int'
    public final Token ident;    // 函数名
    public final List<FuncParam> params; // 形参列表, 可以为空
    public final Block body;     // 函数体

    public FuncDef(Token funcType, Token ident, List<FuncParam> params, Block body) {
        super(funcType.lineNum());
        this.funcType = funcType;
        this.ident = ident;
        this.params = params;
        this.body = body;
    }

    //返回函数体最后的}出现的行数
    public int getlastRBraceLineNum() {
        return body.getRightBraceLineNum();
    }

    //返回函数中所有的return语句
    public List<ReturnStmt> getReturnStatement() {
        return body.getReturnStatement();
    }

    public List<FuncParam> getParams() {
        return params;
    }

    public List<ValueSymbol> paramsToSymbols(int scopeId) {
        if (params == null) {
            return new ArrayList<>(); // 返回空列表，而不是 null
        }
        List<ValueSymbol> symbols = new ArrayList<>();
        for (FuncParam param : params) {
            symbols.add(param.toValueSymbol(scopeId));
        }
        return symbols;
    }
}