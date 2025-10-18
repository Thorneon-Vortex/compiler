package ast.topLevelNodes;
// ast/CompUnit.java
import ast.Declaration;
import ast.Node;

import java.util.List;

/**
 * 对应文法: CompUnit → {Decl} {FuncDef} MainFuncDef
 * 这是整棵语法树的根节点
 */
public class CompUnit extends Node {
    // 将全局声明、函数定义、主函数都视为顶层 "声明"
    private final List<Declaration> declarations;

    public CompUnit(List<Declaration> declarations) {
        super(declarations.isEmpty() ? 0 : declarations.get(0).getLineNum());
        this.declarations = declarations;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }
}
