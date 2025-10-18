package ast.topLevelNodes;

import ast.Declaration;
import ast.statementNodes.Block;

//lineNum为main的行数
public class mainFuncDef extends Declaration {
    public final Block body;
    public mainFuncDef(int lineNum, Block body) {
        super(lineNum);
        this.body = body;
    }
}
