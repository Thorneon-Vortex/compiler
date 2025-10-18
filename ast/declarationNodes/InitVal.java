package ast.declarationNodes;//
 // ast/InitVal.java
import ast.Expression;
import ast.Node;

import java.util.List;

/**
 * 对应文法: ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
 *          InitVal → Exp | '{' [ Exp { ',' Exp } ] '}'
 */
public class InitVal extends Node {
    public final boolean isExpression;
    private Expression singleValue;  // 如果是单个表达式
    private List<Expression> listValue; // 如果是 {...} 列表

    // 构造函数 for single expression
    public InitVal(Expression singleValue) {
        super(singleValue.getLineNum());
        this.isExpression = true;
        this.singleValue = singleValue;
    }

    // 构造函数 for braced list
    public InitVal(int lineNum, List<Expression> listValue) {
        super(lineNum);
        this.isExpression = false;
        this.listValue = listValue;
    }
    
    // 提供 getter 方法以安全访问
    public Expression getSingleValue() { return singleValue; }
    public List<Expression> getListValue() { return listValue; }
}