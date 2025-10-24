package visitor;

import ast.Declaration;
import ast.Expression;
import ast.Node; // etc.
import ast.Statement;
import ast.declarationNodes.InitVal;
import ast.declarationNodes.VarDecl;
import ast.declarationNodes.VarDef;
import ast.expressionNodes.*;
import ast.statementNodes.*;
import ast.topLevelNodes.CompUnit;
import ast.topLevelNodes.FuncDef;
import ast.topLevelNodes.FuncParam;
import ast.topLevelNodes.mainFuncDef;
import error.CompilerError;
import error.SemanticError;
import frontend.Token;
import frontend.TokenType;
import symbol.FuncSymbol;
import symbol.Symbol;
import symbol.SymbolTable;
import symbol.ValueSymbol;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class SemanticVisitor {
    private SymbolTable symbolTable;
    private List<CompilerError> errors;

    // 状态维护
    private FuncSymbol currentFunction; // 用于检查 return 语句
    private int loopDepth; // 用于检查 break/continue

    public SemanticVisitor() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.currentFunction = null;
        this.loopDepth = 0;
    }


    // 主入口方法
    public void analyze(CompUnit root) {
        visit(root);
    }
    
    public List<CompilerError> getErrors() {
        return errors;
    }

    public List<Symbol> getAllSymbols() {
        List<Symbol> orderedSymbols = symbolTable.getAllSymbols();
        Collections.sort(orderedSymbols);
        return orderedSymbols;
    }

    // 我们将为每一种 AST 节点编写一个 visit 方法
    // public void visit(CompUnit node) { ... }
    // public void visit(FuncDef node) { ... }
    // ...
    // in SemanticVisitor.java
    public void visit(CompUnit node) {
        symbolTable.enterScope(); // 进入全局作用域 (scopeId = 1)

        // 可以预定义一些库函数, 比如 getint()
        // symbolTable.addSymbol(new FuncSymbol("getint", "IntFunc", ...));
        symbolTable.addSymbol(new FuncSymbol("getint", "IntFunc", 1, null));
        if(node.getDeclarations() != null) {
            for (Declaration decl : node.getDeclarations()) {
                // VarDecl, FuncDef, mainFuncDef 都被视为 Declaration
                if (decl instanceof VarDecl) visit((VarDecl) decl);
                if (decl instanceof FuncDef) visit((FuncDef) decl);
                if (decl instanceof mainFuncDef) visit((mainFuncDef) decl);
            }
        }

        symbolTable.exitScope(); // 退出全局作用域
    }

    public void visit(VarDecl node) {
        if(node.varDefs == null) return;
        for (VarDef def : node.varDefs) {
            // 先访问数组大小表达式
            if (def.indexExp != null) {
                visit(def.indexExp);
            }
            // 先访问初始化表达式
            if (def.initialValue != null) {
                visit(def.initialValue);
            }
            
            // 最后才添加符号到符号表
            Symbol newSymbol = createValueSymbolFromValDecl(def, node.isConst, node.isStatic);
            if (!symbolTable.addSymbol(newSymbol)) {
                errors.add(new SemanticError(def.ident.lineNum(), "b"));
            }
        }
    }

    public Symbol createValueSymbolFromValDecl(VarDef def, boolean isConst, boolean isStatic) {
        String name = def.ident.value();

        int scopeId = symbolTable.getCurrentScopeId();
        //return new Symbol(name, typeName, scopeId);
        if (isConst) {
            if (def.indexExp ==  null) {
                return new ValueSymbol(name, "ConstInt", scopeId);
            } else {
                return new ValueSymbol(name, "ConstIntArray", scopeId);
            }
        } else {
            if (def.indexExp ==  null) {
                //return new ValueSymbol(name, "Int", scopeId);
                if (isStatic) {
                    return new ValueSymbol(name, "StaticInt", scopeId);
                } else {
                    return new ValueSymbol(name, "Int", scopeId);
                }
            } else {
                //return new ValueSymbol(name, "Array", scopeId);
                if (isStatic) {
                    return new ValueSymbol(name, "StaticIntArray", scopeId);
                }else {
                    return new ValueSymbol(name, "IntArray", scopeId);
                }
            }
        }

    }

    public void visit(FuncDef node) {
        //检查函数名是否重定义
        FuncSymbol funcSymbol = createSymbolFromFuncDef(node);
        if (!symbolTable.addSymbol(funcSymbol)) {
            errors.add(new SemanticError(node.ident.lineNum(), "b"));
        }
        this.currentFunction = funcSymbol;//设置函数上下文
        symbolTable.enterScope();
        // 添加形参到新作用域
        if (node.getParams() != null) {
            for (FuncParam param : node.getParams()) {
                Symbol paramSymbol = createValueSymbolFromFuncDef(param);
                // 错误检查b: 形参名重定义
                if (!symbolTable.addSymbol(paramSymbol)) {
                    errors.add(new SemanticError(param.ident.lineNum(), "b"));
                }
            }
        }

        visit(node.body,true); // 遍历函数体

        // 错误检查g: 有返回值的函数缺少 return
        // 简化版：检查函数体最后一个语句是不是 return
        Block body = node.body;
        List<Statement> items = body.items == null ? new ArrayList<>() : body.items;
        boolean hasReturn = !items.isEmpty() && (items.get(items.size() - 1) instanceof ReturnStmt);

//        boolean hasReturn = items != null && !items.isEmpty() &&
//                (items.get(items.size() - 1) instanceof ReturnStmt);

        if (!hasReturn && node.funcType.type() != TokenType.VOIDTK) {
            errors.add(new SemanticError(body.rightBrace.lineNum(), "g"));
        }

            symbolTable.exitScope(); // 退出函数作用域
            this.currentFunction = null; // 清除上下文
    }


    private Symbol createValueSymbolFromFuncDef(FuncParam param) {
        String name = param.ident.value();
        int scopeId = symbolTable.getCurrentScopeId();
        if (param.isArray) {
            return new ValueSymbol(name, "IntArray", scopeId);
        } else {
            return new ValueSymbol(name, "Int", scopeId);
        }
    }

    private FuncSymbol createSymbolFromFuncDef(FuncDef node) {
        boolean isVoid = node.funcType.type() == TokenType.VOIDTK;
        List<ValueSymbol> valueSymbols = node.paramsToSymbols(symbolTable.getCurrentScopeId()+1);
        return new FuncSymbol(node.ident.value(), isVoid ?  "VoidFunc" : "IntFunc"
                , symbolTable.getCurrentScopeId(),valueSymbols);
    }

    private FuncSymbol createSymbolFromFuncDef(mainFuncDef node) {
        return new FuncSymbol("main", "IntFunc", symbolTable.getCurrentScopeId(),null);
    }

    public void visit(mainFuncDef node) {
        FuncSymbol symbolFromFuncDef = createSymbolFromFuncDef(node);
        this.currentFunction = symbolFromFuncDef;
        //if (!symbolTable.addSymbol(symbolFromFuncDef)) {
            //errors.add(new SemanticError(node.getLineNum(), "b"));
        //}


        symbolTable.enterScope();
        visit(node.body,true);


        Block body = node.body;
        List<Statement> items = body.items == null ? new ArrayList<>() : body.items;
        boolean hasReturn = !items.isEmpty() && (items.get(items.size() - 1) instanceof ReturnStmt);

        if (!hasReturn) {
            errors.add(new SemanticError(body.rightBrace.lineNum(), "g"));
        }


        symbolTable.exitScope();
        this.currentFunction = null;


    }

    public void visit(InitVal node) {
        if(node.isExpression) {
            visit(node.getSingleValue());
        }else{
            if (node.getListValue() != null) {
                for (Expression exp : node.getListValue()) {
                    visit(exp);
                }
            }
        }
    }

    public void visit(Block node,boolean isFunc) {
        //如果是函数的block，不用进入作用域
        if (!isFunc) {
            symbolTable.enterScope();
        }
        if (node.items != null) {
            for (Statement item : node.items) {
                visit(item);
            }
        }
        if (!isFunc) {
            symbolTable.exitScope();
        }
    }

    public void visit(Statement node) {
        if (node ==  null) return;
        if (node instanceof AssignStmt) visit((AssignStmt) node);
        if (node instanceof IfStmt) visit((IfStmt) node);
        if (node instanceof ForStmt) visit((ForStmt) node);
        if (node instanceof Block) visit((Block) node, false);
        if (node instanceof ReturnStmt) visit((ReturnStmt) node);
        if (node instanceof BreakStmt) visit((BreakStmt) node);
        if (node instanceof ContinueStmt) visit((ContinueStmt) node);
        if (node instanceof PrintfStmt) visit((PrintfStmt) node);
        if (node instanceof ExpStmt) visit((ExpStmt) node);
        if (node instanceof VarDecl) visit((VarDecl) node);
    }

    public void visit(AssignStmt node) {

        Token ident = node.lval.ident;
        //逐级查表，看看变量有没有定义过
        Symbol symbol = symbolTable.lookupSymbol(ident.value());
        if(symbol == null) {
            //检查是否未定义
            //errors.add(new SemanticError(ident.lineNum(), "c"));
        }
        else if (symbol instanceof ValueSymbol) {
            //检查是否是const
            ValueSymbol valueSymbol = (ValueSymbol) symbol;
            if (valueSymbol.getTypeName().startsWith("Const")) {
                errors.add(new SemanticError(ident.lineNum(), "h"));
            }
        }

        visit(node.lval);

//        if (node.lval.exp != null) {
//            visit(node.lval.exp);//计算数组indexExp
//        }

        if (node.value != null) {
            visit(node.value);//计算初值？
        }
    }


    public void visit(IfStmt node) {
        visit(node.condition);
        visit(node.thenBranch);
        if (node.elseBranch != null) {
            visit(node.elseBranch);
        }
    }

    public void visit(ForStmt node) {
        if (node.init != null) {
            for (AssignStmt assignStmt : node.init) {
                visit(assignStmt);
            }
        }
        if (node.condition != null) {
            visit(node.condition);
        }
        if (node.update != null) {
            for (AssignStmt assignStmt : node.update) {
                visit(assignStmt);
            }
        }
        loopDepth++;
        visit(node.body);
        loopDepth--;
    }

    public void visit(ReturnStmt node) {
        // 在这里检查：如果当前在void函数中，且returnValue不为null，报f错误
        if (currentFunction != null &&
                currentFunction.getTypeName().equals("VoidFunc") &&
                node.returnValue != null) {
            errors.add(new SemanticError(node.getLineNum(), "f"));
        }
        if (node.returnValue != null) {
            visit(node.returnValue);
        }
    }

    public void visit(BreakStmt node) {
        if (loopDepth == 0) {
            errors.add(new SemanticError(node.getLineNum(), "m"));
        }
    }

    public void visit(ContinueStmt node) {
        if (loopDepth == 0) {
            errors.add(new SemanticError(node.getLineNum(), "m"));
        }
    }

    public void visit(PrintfStmt node) {
        String value = node.formatString.value();
        //看看其中有多少%d
        int count = 0;
        for (int i = 0; i < value.length() - 1; i++) {
            if (value.charAt(i) == '%' && value.charAt(i + 1) == 'd') {
                count++;
            }
        }
        int argsCount = (node.args != null) ? node.args.size() : 0;
        if (count != argsCount)  {
            errors.add(new SemanticError(node.getLineNum(), "l"));
        }
        if (node.args != null) {
            for (Expression arg : node.args) {
                visit(arg);
            }
        }
    }

    public void visit(ExpStmt node) {
        if (node.expression != null) {
            visit(node.expression);
        }
    }

    public void visit(Expression node) {
        if (node == null)return;
        if (node instanceof BinaryExp) {
            visit((BinaryExp) node);
        } else if (node instanceof UnaryExp) {
            visit((UnaryExp) node);
        } else if (node instanceof FuncCall) {
            visit((FuncCall) node);
        }
        else if (node instanceof LVal) {
            visit((LVal) node);
        } else if (node instanceof NumberLiteral) {
            visit((NumberLiteral) node);
        }
    }

    public void visit(BinaryExp node) {
        visit(node.left);
        visit(node.right);
    }

    public void visit(UnaryExp node) {
        visit(node.operand);
    }

    public void visit(LVal node) {
        Symbol symbol = symbolTable.lookupSymbol(node.ident.value());
        if (symbol == null ) {
            errors.add(new SemanticError(node.ident.lineNum(), "c"));
        }
        if (node.exp != null) {
            //System.out.println(node.exp);
            visit(node.exp);  // 检查数组索引表达式
        }
    }

    public void visit(NumberLiteral node) {
    }

    // ==========================================================
// == REPLACE your entire visit(FuncCall) method with this ==
// ==========================================================
    public void visit(FuncCall node) {
        if (node.args != null) {
            for (Expression arg : node.args) {
                visit(arg);
            }
        }
        // Step 1: Look up the symbol ONCE.
        Symbol symbol = symbolTable.lookupSymbol(node.ident.value());
        //boolean flag = false;
        // Step 2: Validate that it's a defined function.
        if (symbol == null || !(symbol instanceof FuncSymbol)) {
            errors.add(new SemanticError(node.getLineNum(), "c"));
            //flag = true;
            // Also, we must still visit the arguments to find errors inside them.
            // e.g., in undefined_func(another_func(1,2,3)), we need to find the error in another_func.
//            if (node.args != null) {
//                for (Expression arg : node.args) {
//                    visit(arg);
//                }
//            }
            return; // CRITICAL: Stop further processing for this call.
        }
//        if (node.args != null) {
//            for (Expression arg : node.args) {
//                visit(arg);
//            }
//        }

        // Now we know we have a valid function symbol.
        FuncSymbol funcSymbol = (FuncSymbol) symbol;

        // Step 3: Validate the number of arguments.
        int actualArgCount = (node.args != null) ? node.args.size() : 0;
        List<ValueSymbol> formalParams = funcSymbol.getParams();
        int expectedArgCount = (formalParams != null) ? formalParams.size() : 0;

        if (actualArgCount != expectedArgCount) {
            errors.add(new SemanticError(node.getLineNum(), "d"));

            return; // CRITICAL: Mismatched counts, so type checking is impossible. Stop.
        }

        // Step 4: If we reach here, counts are correct. Validate argument types.
        if (node.args != null) {
            for (int i = 0; i < actualArgCount; i++) {
                Expression actualArg = node.args.get(i);
                ValueSymbol formalParam = formalParams.get(i);

                // First, visit the argument expression itself to check for errors within it.
                //visit(actualArg);

                // Now, get types and compare.
                String formalType = formalParam.getTypeName();
                String actualType = getExpressionType(actualArg); // You need the helper method for this!
                if (actualType.equals("Unknown")) {
                    break;  // 无法确定类型，停止类型检查
                }
                boolean isFormalArray = formalType.contains("Array");
                boolean isActualArray = actualType.contains("Array");

                if (isFormalArray != isActualArray) {
                    errors.add(new SemanticError(node.getLineNum(), "e"));
                    break;
                }
            }
        }
    }

    private String getExpressionType(Expression exp) {
        if (exp == null) return "Unknown";
        if (exp instanceof NumberLiteral) {
            return "Int";
        }
        //新增
        //visit(exp);
        if (exp instanceof LVal) {
            LVal lval = (LVal) exp;
            Symbol symbol = symbolTable.lookupSymbol(lval.ident.value());
            if (symbol instanceof ValueSymbol) {
                ValueSymbol valueSymbol = (ValueSymbol) symbol;
                boolean isSymbolArray = valueSymbol.getTypeName().contains("Array");
                // If the symbol is an array BUT it's being accessed with an index (e.g., a[0]),
                // the type of the EXPRESSION is Int.
                if (isSymbolArray && lval.exp != null) {
                    return "Int";
                }
                // Otherwise, the expression type is the same as the symbol type.
                return valueSymbol.getTypeName();
            }
        }
        if (exp instanceof FuncCall) {
            FuncCall funcCall = (FuncCall) exp;
            Symbol symbol = symbolTable.lookupSymbol(funcCall.ident.value());
            if (symbol instanceof FuncSymbol) {
                String funcType = ((FuncSymbol) symbol).getTypeName();
                // The type of a function call expression is its return type.
                return funcType.equals("VoidFunc") ? "Void" : "Int";
            }
        }
        // For simplicity, assume all binary/unary operations result in an Int.
        if (exp instanceof BinaryExp || exp instanceof UnaryExp) {
            return "Int";
        }

        return "Unknown"; // Default case
    }

}