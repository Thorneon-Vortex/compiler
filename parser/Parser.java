package parser;

import ast.Declaration;
import ast.Expression;
import ast.Node; // 之后会创建更多Node
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
import error.SyntaxError;
import frontend.Token;
import frontend.TokenType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int currentPos = 0; // 指向当前token的索引
    private Token currentToken; // 当前token的快照，方便访问

    private final PrintWriter outputWriter; // 用于输出到 parser.txt
    private final List<SyntaxError> errors = new ArrayList<>();

    // 构造函数
    public Parser(List<Token> tokens, PrintWriter outputWriter) {
        this.tokens = tokens;
        this.outputWriter = outputWriter;
        // 初始化第一个token
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Token list cannot be empty");
        }
        this.currentToken = tokens.get(0);
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }

    // ----- Token流处理辅助方法 -----

    // 前进一个token
    private void nextToken() {
        if (currentPos < tokens.size() - 1) {
            currentPos++;
            currentToken = tokens.get(currentPos);
        }
    }

    // 查看当前token类型
    private TokenType peek() {
        return currentToken.type();
    }

    // 查看未来第k个token的类型（不移动指针）
    private TokenType peek(int k) {
        if (currentPos + k < tokens.size()) {
            return tokens.get(currentPos + k).type();
        }
        return TokenType.EOF; // 超出范围则返回EOF
    }

    // 消费当前token并输出
    private void consume() {
        // 输出当前token信息
        outputWriter.println(currentToken.type().name() + " " + currentToken.value());
        nextToken();
    }

    // 匹配并消费一个期望的token，否则记录错误
    private void consume(TokenType expectedType, String errorCode) {
        //i：缺少;
        //j：缺少)
        //k，缺少]
        if (peek() == expectedType) {
            consume();
        } else {
            // 错误处理：缺失了期望的token
            // 错误行号通常记录在前一个token的位置
            int errorLine = currentPos > 0 ? tokens.get(currentPos - 1).lineNum() : currentToken.lineNum();
            errors.add(new SyntaxError(errorLine, errorCode));
        }
    }

    // ----- 语法成分输出辅助方法 -----
    private void printSyntaxComponent(String name) {
        outputWriter.println("<" + name + ">");
    }

    // ----- 错误记录辅助方法 -----
    private void addError(String errorCode) {
        // 错误行号记录在当前token的位置
        errors.add(new SyntaxError(currentToken.lineNum(), errorCode));
    }


    // ----- 递归下降分析方法（主入口）-----
    public CompUnit parse() {
        return parseCompUnit();
    }

    // 接下来我们将在这里填充所有的 parseXXX() 方法
    // ...
    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private CompUnit parseCompUnit() {
        List<Declaration> declarations = new ArrayList<>();
        // 循环分析全局声明和函数定义
        while (peek() != TokenType.EOF) {
            if (peek() == TokenType.INTTK && peek(1) == TokenType.MAINTK) {
                // 遇到主函数，跳出循环
                break;
            }
            if ((peek() == TokenType.VOIDTK || peek() == TokenType.INTTK) &&
                    peek(1) == TokenType.IDENFR && peek(2) == TokenType.LPARENT) {
                // 声明const static int s;
                declarations.add(parseFuncDef());
            } else if (peek() == TokenType.CONSTTK || peek() == TokenType.STATICTK
                    || peek() == TokenType.INTTK) {
                //
                declarations.add(parseDecl());
            } else {
                // 无法识别的结构，跳过当前token避免死循环
                nextToken();
            }
        }

        // 分析主函数
        if (peek() == TokenType.INTTK && peek(1) == TokenType.MAINTK) {
            declarations.add(parseMainFuncDef());
        }

        printSyntaxComponent("CompUnit");
        return new CompUnit(declarations);
    }

    private Declaration parseFuncDef() {
        Token funcType = parseFuncType();
        int lineNum = currentToken.lineNum();//标识符行号
        Token ident = currentToken;
        consume();//indent
        consume();//(
        List<FuncParam> params = null;
        if (peek() == TokenType.INTTK)
            params = parseFuncFParams();
        consume(TokenType.RPARENT, "j");
        Block body = parseBlock();
        printSyntaxComponent("FuncDef");
        return new FuncDef(funcType, ident, params, body);
    }

    private Block parseBlock() {
        int lineNum = currentToken.lineNum();
        List<Statement> items = new ArrayList<>();
        List<Integer> returnPos = new ArrayList<>();
        consume();//{
        while (peek() != TokenType.RBRACE) {
            Statement tmp = parseBlockItem();
            items.add(tmp);
            if (tmp instanceof ReturnStmt) {
                //return的位置
                returnPos.add(items.size()-1);
            }
        }
        Token rightBrace = currentToken;
        consume();//}
        printSyntaxComponent("Block");
        return new Block(lineNum, items, rightBrace, returnPos);
    }

//    private Node parseBlockItem() {
////        if (peek() == TokenType.IDENFR) {
////            parseStmt();
////        } else {
////            parseDecl();
////        }
//        if (TokenType.isStmt(peek())) {
//            parseStmt();
//        } else {
//            int i = 0;
//            boolean flag = false;
//            while (true) {
//                TokenType type = peek(i);
//                if (type == TokenType.EOF) {
//                    //细节，这也算没读到int，程序虽然缺少；
//                    //但不在这里处理，之后调用parseStmt()处理
//                    break;
//                }
//                if (peek(i) == TokenType.INTTK) {
//                    flag = true;
//                    break;
//                }
//                if (peek(i) == TokenType.SEMICN) {
//                    break;
//                }
//                i++;
//            }
//            if (flag) {
//                parseDecl();
//            } else {
//                parseStmt();
//            }
//        }
//        //题目要求，不需要输出
//        return null;
//    }

    private Statement parseBlockItem() {
        // Decl的FIRST集是 {CONSTTK, INTTK, STATICTK}
        Statement item;
        if (peek() == TokenType.CONSTTK || peek() == TokenType.INTTK || peek() == TokenType.STATICTK) {
            item = parseDecl();
        } else {
            item = parseStmt();
        }
        // BlockItem本身不要求输出
        return item;
    }

    private List<FuncParam> parseFuncFParams() {
        List<FuncParam> params = new ArrayList<>();
        if (peek() != TokenType.RPARENT) {
            params.add(parseFuncFParam());
            while (peek() == TokenType.COMMA) {
                consume(); // 消费 ','
                params.add(parseFuncFParam());
            }
        }
        printSyntaxComponent("FuncFParams");
        return params;
    }

    private FuncParam parseFuncFParam() {
        Token bType = parseBType();
        Token ident = currentToken;
        boolean isArray = false;
        consume(); // Ident
        if (peek() == TokenType.LBRACK) {
            isArray = true;
            consume(); // '['
            consume(TokenType.RBRACK, "k"); // ']'
        }
        printSyntaxComponent("FuncFParam");
        return new FuncParam(bType, ident, isArray);
    }

    private Token parseFuncType() {
        Token token = currentToken;
        if (peek() == TokenType.VOIDTK) {
            consume();
        } else if (peek() == TokenType.INTTK) {
            consume();
        }
        printSyntaxComponent("FuncType");
        return token;
    }

    private Declaration parseMainFuncDef() {
        consume();//int
        int lineNum = currentToken.lineNum();
        consume();//main
        consume();//(
        consume(TokenType.RPARENT, "j");//)
        Block body = parseBlock();
        printSyntaxComponent("MainFuncDef");
        return new mainFuncDef(lineNum,body);
    }

    // Decl → ConstDecl | VarDecl
    private VarDecl parseDecl() {
        if (peek() == TokenType.CONSTTK) {
            return parseConstDecl();
        } else {
            return parseVarDecl();
        }
        // 注意: <Decl> 不要求输出
        //return null;
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private VarDecl parseConstDecl() {
        Token constToken = currentToken;
        consume(TokenType.CONSTTK, "expect reserved word const"); // 假设有个错误码，实际上不可能出现错误。
        Token bType = parseBType();
        List<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseConstDef());
        while (peek() == TokenType.COMMA) {
            consume(); // 消费 ','
            varDefs.add(parseConstDef());
        }
        // 错误检查 i: 缺少分号
        consume(TokenType.SEMICN, "i");
        // <ConstDecl> 不要求输出，但其子节点会输出
        printSyntaxComponent("ConstDecl");
        return new VarDecl(true,false,bType, varDefs);
    }

    private VarDef parseConstDef() {
        Token ident = currentToken;
        consume(); // Ident
        Expression indexExp = null;
        if (peek() == TokenType.LBRACK) {
            consume(); // '['
            indexExp = parseConstExp();
            consume(TokenType.RBRACK, "k"); // ']'
        }
        consume(); // '='
        InitVal initVal = parseConstInitVal();
        printSyntaxComponent("ConstDef");
        return new VarDef(ident,indexExp,initVal);
    }

    private InitVal parseConstInitVal() {
        boolean isExpression = false;
        Expression singleValue = null;
        List<Expression> listValue = null;
        //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
        if (peek() == TokenType.LBRACE) {
            int lineNum = currentToken.lineNum();
            //消耗{
            consume();
            //一维数组初值
            if (peek() != TokenType.RBRACE) {
                listValue = new ArrayList<>();
                listValue.add(parseConstExp());
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    listValue.add(parseConstExp());
                }
            }
            //吃掉}
            consume();
            return new InitVal(lineNum,listValue);
        } else {
            isExpression = true;
            singleValue = parseConstExp();
            return new InitVal(singleValue);
        }
        //printSyntaxComponent("ConstInitVal");

    }

    private Expression parseConstExp() {
        Expression exp = parseAddExp();
        printSyntaxComponent("ConstExp");
        return exp;
    }

    // BType → 'int'
    private Token parseBType() {
        Token token = currentToken;
        consume(TokenType.INTTK, "expect int");
        // <BType> 不要求输出
        return token;
    }

    // VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    private VarDecl parseVarDecl() {
        boolean isStatic = false;
        if (peek() == TokenType.STATICTK) {
            isStatic = true;
            consume();
        }
        Token bType = parseBType();
        List<VarDef> varDefs = new ArrayList<>();
        varDefs.add(parseVarDef());
        while (peek() == TokenType.COMMA) {
            consume();
            varDefs.add(parseVarDef());
        }
        consume(TokenType.SEMICN, "i");
        printSyntaxComponent("VarDecl");
        return new VarDecl(false,isStatic,bType, varDefs);
    }

    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    private VarDef parseVarDef() {
        Token ident = currentToken;
        Expression indexExp = null;
        InitVal initVal = null;
        consume(); // Ident
        while (peek() == TokenType.LBRACK) {
            consume(); // '['
            indexExp = parseConstExp();
            consume(TokenType.RBRACK, "k"); // ']' 错误检查 k
        }
        if (peek() == TokenType.ASSIGN) {
            consume(); // '='
            initVal = parseInitVal();
        }
        printSyntaxComponent("VarDef");
        return new VarDef(ident,indexExp,initVal);
    }

    private InitVal parseInitVal() {
        if (peek() == TokenType.LBRACE) {
            List<Expression> listValue = new ArrayList<>();
            int lineNum = currentToken.lineNum();
            consume(); // '{'
            if (peek() != TokenType.RBRACE) {
                listValue.add(parseExp());
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    listValue.add(parseExp());
                }
            }
            consume(); // '}'
            return new InitVal(lineNum,listValue);
        } else {
            int lineNum = currentToken.lineNum();
            Expression singleValue = parseExp();
            return new InitVal(singleValue);
        }
        //printSyntaxComponent("InitVal");
       // return null;
    }

    // Stmt → LVal '=' Exp ';' | [Exp] ';' | Block | ...
    private Statement parseStmt() {
        Statement stmt = null;
        switch (peek()) {
            case LBRACE: // Block
                stmt = parseBlock();
                break;
            case IFTK:
                // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
                int ifLineNum = currentToken.lineNum();
                consume(); // if
                consume(); // '('
                Expression condition = parseCond();
                consume(TokenType.RPARENT, "j"); // ')' 错误检查 j
                Statement thenBranch = parseStmt();
                Statement elseBranch = null;
                if (peek() == TokenType.ELSETK) {
                    consume(); // else
                    elseBranch = parseStmt();
                }
                stmt = new IfStmt(ifLineNum,condition, thenBranch, elseBranch);
                break;
            case FORTK:
                // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                int forLineNum = currentToken.lineNum();
                consume(); // for
                consume(); // '('
                List<AssignStmt> init = null;
                if (peek() == TokenType.IDENFR) {
                    init = parseForStmt();
                }
                consume(TokenType.SEMICN, "i"); // ';'
                Expression cond = null;
                if (peek() == TokenType.IDENFR || peek() == TokenType.LPARENT || peek() == TokenType.INTCON
                 || peek() == TokenType.PLUS || peek() == TokenType.MINU || peek() == TokenType.NOT) {
                    cond = parseCond();
                }
                consume(TokenType.SEMICN, "i"); // ';'
                List<AssignStmt> update = null;
                if (peek() == TokenType.IDENFR) {
                    update = parseForStmt();
                }
                consume(TokenType.RPARENT, "j"); // ')'
                Statement body = parseStmt();
                stmt = new ForStmt(forLineNum,init, cond, update, body);
                break;
            case BREAKTK:
                int breakLineNum = currentToken.lineNum();
                consume(); // break
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                stmt = new BreakStmt(breakLineNum);
                break;
            case CONTINUETK:
                int continueLineNum = currentToken.lineNum();
                consume(); //continue
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                stmt = new ContinueStmt(continueLineNum);
                break;
            case RETURNTK:
                int returnLineNum = currentToken.lineNum();
                consume(); // return
                Expression returnValue = null;
                // [Exp]
                if (peek() == TokenType.IDENFR || peek() == TokenType.LPARENT || peek() == TokenType.INTCON
                 || peek() == TokenType.PLUS || peek() == TokenType.MINU || peek() == TokenType.NOT) {
                    returnValue = parseExp();
                }
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                stmt = new ReturnStmt(returnLineNum, returnValue);
                break;
            case PRINTFTK:
                // 'printf''('StringConst {','Exp}')'';'
                int printfLineNum = currentToken.lineNum();
                consume(); // printf
                consume(); // '('
                Token formatString = currentToken;
                consume(); // StringConst
                List<Expression> args = new ArrayList<>();
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    args.add(parseExp());
                }
                consume(TokenType.RPARENT, "j"); // ')' 错误检查 j
                consume(TokenType.SEMICN, "i"); // ';' 错误检查 i
                stmt = new PrintfStmt(formatString, args);
                break;
            case SEMICN:
                //空语句
                int ExpLineNum = currentToken.lineNum();
                consume(TokenType.SEMICN, "i"); // 明确处理空语句
                stmt = new ExpStmt(ExpLineNum, null);
                break;
            default:
                // 能进入这里的，必然是 LVal=Exp; 或 Exp; (且Exp不为空)
                int i = 0;
                boolean isAssign = false;
                while (peek(i) != TokenType.SEMICN && peek(i) != TokenType.EOF) {
                    if (peek(i) == TokenType.ASSIGN) {
                        isAssign = true;
                        break;
                    }
                    i++;
                }

                if (isAssign) {
                    LVal lval = parseLVal();
                    consume(); // =
                    Expression value = parseExp();
                    consume(TokenType.SEMICN, "i"); // 最后必须有分号
                    stmt = new AssignStmt(lval, value);
                } else {
                    Expression exp = null;
                    int line = currentToken.lineNum();
                    exp = parseExp(); // 移除了 if，因为这里必然有表达式
                    consume(TokenType.SEMICN, "i"); // 最后必须有分号
                    stmt = new ExpStmt(line, exp);
                }
                //consume(TokenType.SEMICN, "i"); // 最后必须有分号

        }

        printSyntaxComponent("Stmt");
        return stmt;



    }

    //ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    private List<AssignStmt> parseForStmt() {
        List<AssignStmt> assignStmts = new ArrayList<>();
        LVal lval = parseLVal();
        consume(); // '='
        Expression value = parseExp();
        assignStmts.add(new AssignStmt(lval, value));
        while (peek() == TokenType.COMMA) {
            consume(); // ','
            lval = parseLVal();
            consume(); // '='
            value = parseExp();
            assignStmts.add(new AssignStmt(lval, value));
        }
        printSyntaxComponent("ForStmt");
        return assignStmts;
    }

    private Expression parseCond() {
        Expression exp = parseLOrExp();
        printSyntaxComponent("Cond");
        return exp;
    }

    // LOrExp → LAndExp | LOrExp '||' LAndExp
    // 消除左递归: LAndExp { '||' LAndExp }
    private Expression parseLOrExp() {
        Expression left = parseLAndExp();
        printSyntaxComponent("LOrExp");
        while (peek() == TokenType.OR || peek() == TokenType.ERROROR) {
            Token op = currentToken;
            consume();
            Expression right = parseLAndExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("LOrExp");
        }
        //printSyntaxComponent("LOrExp");
        return left;
    }

    //LAndExp → EqExp | LAndExp '&&' EqExp
    //消除左递归: EqExp { '&&' EqExp }
    private Expression parseLAndExp() {
        Expression left = parseEqExp();
        printSyntaxComponent("LAndExp");
        //注意这里认为&也是逻辑与，这里不处理。避免错误雪崩
        while (peek() == TokenType.AND || peek() == TokenType.ERRORAND) {
            Token op = currentToken;
            consume();
            Expression right = parseEqExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("LAndExp");
        }
        //printSyntaxComponent("LAndExp");
        return left;
    }

    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //消除左递归: RelExp { ('==' | '!=') RelExp }
    private Expression parseEqExp() {
        Expression left = parseRelExp();
        printSyntaxComponent("EqExp");
        while (peek() == TokenType.EQL || peek() == TokenType.NEQ) {
            Token op = currentToken;
            consume();
            Expression right = parseRelExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("EqExp");
        }
        //printSyntaxComponent("EqExp");
        return left;
    }

    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //消除左递归: AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    private Expression parseRelExp() {
        Expression left = parseAddExp();
        printSyntaxComponent("RelExp");
        while (peek() == TokenType.LSS || peek() == TokenType.GRE || peek() == TokenType.LEQ || peek() == TokenType.GEQ) {
            Token op = currentToken;
            consume();
            Expression right = parseAddExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("RelExp");
        }
        //printSyntaxComponent("RelExp");
        return left;
    }

    // Exp → AddExp
    private Expression parseExp() {
        Expression exp = parseAddExp();
        printSyntaxComponent("Exp");
        return exp;
    }

    // AddExp → MulExp { ('+' | '−') MulExp }
    // 注意：文法是左递归的，需要改写成非左递归形式进行分析
    private Expression parseAddExp() {
        Expression left = parseMulExp();
        printSyntaxComponent("AddExp");
        while (peek() == TokenType.PLUS || peek() == TokenType.MINU) {
            Token op = currentToken;//+-
            consume();
            Expression right = parseMulExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("AddExp");
        }
        //printSyntaxComponent("AddExp");
        return left;
    }

    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private Expression parseMulExp() {
        Expression left = parseUnaryExp();
        printSyntaxComponent("MulExp");
        while (peek() == TokenType.MULT || peek() == TokenType.DIV || peek() == TokenType.MOD) {
            Token op = currentToken;
            consume();
            Expression right = parseUnaryExp();
            left = new BinaryExp(left, op, right);
            printSyntaxComponent("MulExp");
        }
        //printSyntaxComponent("MulExp");
        return left;
    }

    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j
    private Expression parseUnaryExp() {
        if (peek() == TokenType.IDENFR && peek(1) == TokenType.LPARENT) {
            Token indent = currentToken;
            consume(); // Ident
            consume(); // '('
//            if (peek() != TokenType.RPARENT) {
//                parseFuncRParams();
//            }
            List<Expression> args = null;
            if (peek() == TokenType.LPARENT || peek() == TokenType.IDENFR || peek() == TokenType.INTCON
                    || peek() == TokenType.PLUS || peek() == TokenType.MINU || peek() == TokenType.NOT) {
                args = parseFuncRParams();
            }
            consume(TokenType.RPARENT, "j"); // ')'
            printSyntaxComponent("UnaryExp");
            return new FuncCall(indent, args);
        } else if (peek() == TokenType.PLUS || peek() == TokenType.MINU || peek() == TokenType.NOT) {
            Token op = currentToken;
            parseUnaryOp();
            Expression operand = parseUnaryExp();
            printSyntaxComponent("UnaryExp");
            return new UnaryExp(op, operand);
        } else {
            printSyntaxComponent("UnaryExp");
            return parsePrimaryExp();
        }
        //printSyntaxComponent("UnaryExp");
        //return null;
    }

    private List<Expression> parseFuncRParams() {
        List<Expression> args = new ArrayList<>();
        args.add(parseExp());
        while (peek() == TokenType.COMMA) {
            consume(); // ','
            args.add(parseExp());
        }
        printSyntaxComponent("FuncRParams");
        return args;
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number // j
    private Expression parsePrimaryExp() {
        Expression exp = null;
        if (peek() == TokenType.LPARENT) {
            consume();
            exp = parseExp();
            consume(TokenType.RPARENT, "j");
            return exp;
        } else if (peek() == TokenType.INTCON) {
            exp = parseNumber();
        } else {
            exp = parseLVal();
        }
        printSyntaxComponent("PrimaryExp");
        return exp;
    }

    private LVal parseLVal() {
        Token indent = currentToken;
        consume(); // Ident
        Expression indexExp = null;
        while (peek() == TokenType.LBRACK) {
            consume(); // '['
            indexExp = parseExp();
            consume(TokenType.RBRACK, "k"); // ']'
        }
        printSyntaxComponent("LVal");
        return new LVal(indent, indexExp);
    }

    private NumberLiteral parseNumber() {
        Token numberToken = currentToken;
        consume(); // IntConst
        printSyntaxComponent("Number");
        return new NumberLiteral(numberToken);
    }

    //UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
    private Node parseUnaryOp() {
        consume(); // '+' | '-' | '!'
        printSyntaxComponent("UnaryOp");
        return null;
    }

}