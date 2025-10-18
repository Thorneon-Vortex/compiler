package ast.declarationNodes;// ast/VarDecl.java
import ast.Declaration;
import frontend.Token;
import ast.Statement;
import java.util.List;

/**
 * 对应文法: ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
 *          VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
 * 这个节点同时作为全局声明 (extends Declaration) 和 块内声明 (extends Statement)
 */
public class VarDecl extends Declaration { // 也可作为Declaration, 在语义分析时区分
    public final boolean isConst;
    public final boolean isStatic;
    public final Token bType;
    public final List<VarDef> varDefs;

    public VarDecl(boolean isConst, boolean isStatic, Token bType, List<VarDef> varDefs) {
        super(bType.lineNum());
        this.isConst = isConst;
        this.isStatic = isStatic;
        this.bType = bType;
        this.varDefs = varDefs;
    }


}