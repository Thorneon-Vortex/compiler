package frontend;

import error.LexerError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 词法分析器类 (Lexer)
 * 采用单例模式
 */
public class Lexer {
    // --- 单例模式实现 ---
    private static final Lexer instance = new Lexer();
    private Lexer() {}
    public static Lexer getInstance() {
        return instance;
    }

    //数据成员
    private String source;          // 源程序字符串
    private char[] sourceChars;     // 源程序的字符数组，提高访问效率
    private int curPos;             // 当前字符串位置指针

    private String token;           // 解析出的单词值
    private TokenType tokenType;    // 解析出的单词类型
    private int lineNum;            // 当前行号
    private int number;             // 如果是INTCON，这是它的数值

    private final Map<String, TokenType> reserveWords = new HashMap<>(); // 保留字表
    private final List<LexerError> errors = new ArrayList<>();

    /**
     * 初始化词法分析器
     * @param sourceCode 源代码字符串
     */
    public void init(String sourceCode) {
        this.source = sourceCode;
        this.sourceChars = sourceCode.toCharArray();
        this.curPos = 0;//当前位置
        this.lineNum = 1; // 行号从1开始
        this.errors.clear();//清空错误列表
        initReserveWords();
    }

    /**
     * 初始化保留字（关键字）表
     */
    private void initReserveWords() {
        reserveWords.put("const", TokenType.CONSTTK);
        reserveWords.put("int", TokenType.INTTK);
        reserveWords.put("static", TokenType.STATICTK);
        reserveWords.put("break", TokenType.BREAKTK);
        reserveWords.put("continue", TokenType.CONTINUETK);
        reserveWords.put("if", TokenType.IFTK);
        reserveWords.put("main", TokenType.MAINTK);
        reserveWords.put("else", TokenType.ELSETK);
        reserveWords.put("for", TokenType.FORTK);
        reserveWords.put("return", TokenType.RETURNTK);
        reserveWords.put("void", TokenType.VOIDTK);
        reserveWords.put("printf", TokenType.PRINTFTK);
    }

    /**
     * 核心方法：处理下一个单词
     */
    public void next() {
        //错误时需要记录
        // 清空上一个token的信息
        token = "";
        number = 0;

        // 1. 跳过空白字符（空格、tab、回车、换行）
        skipWhitespace();

        // 2. 检查是否到达文件末尾
        if (curPos >= sourceChars.length) {
            tokenType = TokenType.EOF;
            return;
        }

        char currentChar = peek();

        // 3. 状态机逻辑：根据当前字符判断Token类型
        if (Character.isLetter(currentChar) || currentChar == '_') {
            // 可能是标识符或关键字
            parseIdentifierOrKeyword();
        } else if (Character.isDigit(currentChar)) {
            // 肯定是数字
            parseIntConst();
        } else if (currentChar == '"') {
            // 肯定是字符串常量
            parseStringConst();
        } else {
            // 可能是运算符、分界符或注释
            parseOperatorOrDelimiter();
        }
    }

    private void parseIdentifierOrKeyword() {
        StringBuilder sb = new StringBuilder();
        
        // 1. 循环读取字母、数字、下划线
        while (curPos < sourceChars.length && 
               (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            sb.append(get());
        }
        
        // 2. 将StringBuilder转为String
        token = sb.toString();
        
        // 3. 查reserveWords表，如果存在，tokenType就是对应的关键字类型
        if (reserveWords.containsKey(token)) {
            tokenType = reserveWords.get(token);
        } else {
            // 4. 如果不存在，tokenType就是IDENFR
            tokenType = TokenType.IDENFR;
        }
    }

    private void parseIntConst() {
        StringBuilder sb = new StringBuilder();
        
        // 1. 循环读取数字
        while (curPos < sourceChars.length && Character.isDigit(peek())) {
            sb.append(get());
        }
        
        // 2. 将StringBuilder转为String
        token = sb.toString();
        
        // 3. 使用Integer.parseInt()转换为数值，存入number字段
        try {
            number = Integer.parseInt(token);
        } catch (NumberFormatException e) {
            // 如果数字过大，设置为最大值并记录错误
            number = Integer.MAX_VALUE;
            errors.add(new LexerError(lineNum, "number_overflow"));
        }
        
        // 4. tokenType设为INTCON
        tokenType = TokenType.INTCON;
    }

    private void parseStringConst() {
        StringBuilder sb = new StringBuilder();
        
        // 1. 跳过开头的 "
        get(); // 消耗开头的引号
        sb.append('"');
        
        // 2. 循环读取字符直到下一个 "
        while (curPos < sourceChars.length) {
            char c = peek();
            if (c == '"') {
                // 找到结束引号
                get(); // 消耗结束引号
                sb.append('"');
                break;
            } else if (c == '\\') {
                // 3. 处理转义字符
                get(); // 消耗反斜杠
                if (curPos < sourceChars.length) {
                    char nextChar = get();
                    if (nextChar == 'n') {
                        sb.append("\\n");
                    } else if (nextChar == 't') {
                        sb.append("\\t");
                    } else if (nextChar == '\\') {
                        sb.append("\\\\");
                    } else if (nextChar == '"') {
                        sb.append("\\\"");
                    } else {
                        // 其他转义字符
                        sb.append('\\').append(nextChar);
                    }
                }
            } else if (c == '\n') {
                // 字符串中不应该有未转义的换行符
                lineNum++;
                sb.append(get());
            } else {
                sb.append(get());
            }
        }
        
        token = sb.toString();
        // 4. tokenType设为STRCON
        tokenType = TokenType.STRCON;
    }

    private void parseOperatorOrDelimiter() {
        char c = get(); // 读取并消耗当前字符
        switch (c) {
            case '/':
                // 难点：可能是除号、单行注释、多行注释
                if (peek() == '/') {
                    skipLineComment();
                    next(); // 注释不是一个token，跳过它，继续分析下一个
                } else if (peek() == '*') {
                    skipBlockComment();
                    next(); // 注释不是一个token，跳过它，继续分析下一个
                } else {
                    token = "/";
                    tokenType = TokenType.DIV;
                }
                break;
            case '&':
                // 难点：可能是 && 或 a类错误
                if (peek() == '&') {
                    get(); // 消耗第二个 '&'
                    token = "&&";
                    tokenType = TokenType.AND;
                } else {
                    // a类错误！
                    token = "&";
                    errors.add(new LexerError(this.lineNum,"a"));
                    // TODO: 在这里记录错误信息 (行号: lineNum, 错误类型: a)
                    // 即使是错误的，也要设置一个类型，让语法分析器知道这里有个东西
                    // 暂时可以设为一个ERROR类型，或者根据具体要求处理
                    tokenType = TokenType.ERRORAND; // 假设有一个ERROR类型
                }
                break;
            case '|':
                if (peek() == '|') {
                    get();
                    token = "||";
                    tokenType = TokenType.OR;
                } else {
                    // a类错误！记录下来
                    token = "|";
                    errors.add(new LexerError(this.lineNum, "a"));
                    tokenType = TokenType.ERROROR;
                }
                break;
            case '=':
                if (peek() == '=') {
                    get(); // 消耗第二个 '='
                    token = "==";
                    tokenType = TokenType.EQL;
                } else {
                    token = "=";
                    tokenType = TokenType.ASSIGN;
                }
                break;
            case '!':
                if (peek() == '=') {
                    get(); // 消耗 '='
                    token = "!=";
                    tokenType = TokenType.NEQ;
                } else {
                    token = "!";
                    tokenType = TokenType.NOT;
                }
                break;
            case '<':
                if (peek() == '=') {
                    get(); // 消耗 '='
                    token = "<=";
                    tokenType = TokenType.LEQ;
                } else {
                    token = "<";
                    tokenType = TokenType.LSS;
                }
                break;
            case '>':
                if (peek() == '=') {
                    get(); // 消耗 '='
                    token = ">=";
                    tokenType = TokenType.GEQ;
                } else {
                    token = ">";
                    tokenType = TokenType.GRE;
                }
                break;
            case '+':
                token = "+";
                tokenType = TokenType.PLUS;
                break;
            case '-':
                token = "-";
                tokenType = TokenType.MINU;
                break;
            case '*':
                token = "*";
                tokenType = TokenType.MULT;
                break;
            case '%':
                token = "%";
                tokenType = TokenType.MOD;
                break;
            case ';':
                token = ";";
                tokenType = TokenType.SEMICN;
                break;
            case ',':
                token = ",";
                tokenType = TokenType.COMMA;
                break;
            case '(':
                token = "(";
                tokenType = TokenType.LPARENT;
                break;
            case ')':
                token = ")";
                tokenType = TokenType.RPARENT;
                break;
            case '[':
                token = "[";
                tokenType = TokenType.LBRACK;
                break;
            case ']':
                token = "]";
                tokenType = TokenType.RBRACK;
                break;
            case '{':
                token = "{";
                tokenType = TokenType.LBRACE;
                break;
            case '}':
                token = "}";
                tokenType = TokenType.RBRACE;
                break;
            default:
                // 未知符号，作为错误处理
                token = String.valueOf(c);
                tokenType = TokenType.ERROR;
                break;
        }
    }

    public List<LexerError> getErrors() {
        return this.errors;
    }

    //辅助方法
    private void skipWhitespace() {
        while (curPos < sourceChars.length && Character.isWhitespace(sourceChars[curPos])) {
            if (sourceChars[curPos] == '\n') {
                lineNum++;
            }
            curPos++;
        }
    }

    private void skipLineComment() {
        // 跳过 "//" 
        get(); // 消耗第二个 '/'
        
        // 从当前位置一直向后扫描，直到遇到'\n'或文件末尾
        while (curPos < sourceChars.length && peek() != '\n') {
            get();
        }
        // 注意：不消耗换行符，让skipWhitespace处理它以正确更新行号
    }

    private void skipBlockComment() {
        // 跳过 "/*"
        get(); // 消耗 '*'
        
        // 从当前位置一直向后扫描，直到遇到 "*/"
        while (curPos < sourceChars.length - 1) {
            char current = get();
            if (current == '\n') {
                lineNum++; // 更新行号
            }
            if (current == '*' && peek() == '/') {
                get(); // 消耗 '/'
                break;
            }
        }
    }

    /**
     * 预读一个字符，但不移动指针
     */
    private char peek() {
        if (curPos < sourceChars.length) {
            return sourceChars[curPos];
        }
        return '\0'; // 表示文件结束
    }

    /**
     * 读取一个字符，并移动指针
     */
    private char get() {
        if (curPos < sourceChars.length) {
            return sourceChars[curPos++];
        }
        return '\0'; // 表示文件结束
    }


    //接口
    public String getToken() {
        return token;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public int getLineNum() {
        return lineNum;
    }

    public int getNumber() {
        return number;
    }
}