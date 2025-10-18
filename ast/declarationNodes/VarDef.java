package ast.declarationNodes;// ast/VarDef.java
import frontend.Token;
import ast.Expression;

import ast.Node;
import java.util.List;

/**
 * 对应文法: ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
 *          VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
 */
//题目规定只能有一维数组
public class VarDef extends Node {
    public final Token ident;
    //public final List<Expression> arraySizes; // 数组维度大小，可以为空
    public Expression indexExp;//如果是数组，则为数组的索引表达式，为 null 则为普通变量
    public final InitVal initialValue;        // 初始化值，可以为 null

    public VarDef(Token ident,Expression indexExp,InitVal initialValue) {
        super(ident.lineNum());
        this.ident = ident;
        //this.arraySizes = arraySizes;
        this.indexExp = indexExp;
        this.initialValue = initialValue;
    }
}