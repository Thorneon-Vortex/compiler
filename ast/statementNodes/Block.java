package ast.statementNodes;// ast/Block.java

import ast.Node;
import ast.Statement;
import frontend.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应文法: Block → '{' { BlockItem } '}'
 * BlockItem → Decl | Stmt
 */
//lineNum是左括号{行号
public class Block extends Statement {
    // 列表里可以包含 VarDecl 或其他 Statement
    public final List<Statement> items;
    //特别记录一下},因为g类错误需要获取}行号
    public final Token rightBrace;
    //记录所return语句的位置
    public final List<Integer> returnPos;

    public Block(int lineNum, List<Statement> items, Token rightBrace, List<Integer> returnPos) {
        super(lineNum);
        this.items = items;
        this.rightBrace = rightBrace;
        this.returnPos = returnPos;
    }

    public int getRightBraceLineNum() {
        return rightBrace.lineNum();
    }

    public List<ReturnStmt> getReturnStatement() {
        List<ReturnStmt> returnStmts = new ArrayList<>();
        if (returnPos != null && items != null) {
            for (Integer po : returnPos) {
                returnStmts.add((ReturnStmt) items.get(po));
            }
        }
        return returnStmts;
    }
}