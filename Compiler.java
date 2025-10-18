import ast.Node;
import ast.topLevelNodes.CompUnit;
import error.CompilerError;
import frontend.Lexer;
import error.LexerError;
import frontend.Token;
import frontend.TokenType;
import error.SyntaxError;
import parser.Parser;
import symbol.Symbol;
import visitor.SemanticVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 为了方便存储Token信息，我们创建一个简单的记录类（Record）
// Java 16+
public class Compiler {
    public static void main(String[] args) {
        // --- 文件路径定义 ---
        String inputFile = "testfile.txt";
        String outputFile = "symbol.txt";
        String errorFile = "error.txt";

        // --- 核心数据结构 ---
        List<CompilerError> allErrors = new ArrayList<>();
        List<Token> tokens;

        CompUnit compUnit = null;
        try {
            // --- 读取源代码 ---
            String sourceCode = new String(Files.readAllBytes(Paths.get(inputFile)), StandardCharsets.UTF_8);

            // ===================================
            //         1. 词法分析阶段
            // ===================================
            Lexer lexer = Lexer.getInstance();
            lexer.init(sourceCode);
            tokens = new ArrayList<>();

            while (true) {
                lexer.next(); // 分析下一个Token
                TokenType type = lexer.getTokenType();

                // 将所有分析出的Token（包括ERROR类型）都存起来，供语法分析使用
                tokens.add(new Token(type, lexer.getToken(), lexer.getLineNum()));

                if (type == TokenType.EOF) {
                    break; // 到达文件末尾
                }
            }

            // 收集词法阶段发现的错误
            allErrors.addAll(lexer.getErrors());


            // ===================================
            //         2. 语法分析阶段
            // ===================================

            // 使用 StringWriter 来缓存正确的输出。
            // 这样，如果最后发现了任何错误，我们就可以不输出 parser.txt，只输出 error.txt。
            StringWriter stringWriter = new StringWriter();
            try (PrintWriter parserOutputWriter = new PrintWriter(stringWriter)) {

                Parser parser = new Parser(tokens, parserOutputWriter);
                compUnit = parser.parse();// 启动语法分析过程,并拿到语法树根节点

                // 收集语法阶段发现的错误
                allErrors.addAll(parser.getErrors());
            }
            //语义分析
            SemanticVisitor semanticVisitor = new SemanticVisitor();
            semanticVisitor.analyze(compUnit);

            allErrors.addAll(semanticVisitor.getErrors());


            // ===================================
            //         3. 决策与输出阶段
            // ===================================

            if (!allErrors.isEmpty()) {
                // 如果总错误列表非空，则只输出到 error.txt

                // **关键步骤：对所有收集到的错误按行号排序**
                Collections.sort(allErrors);

                try (PrintWriter errorWriter = new PrintWriter(errorFile, StandardCharsets.UTF_8)) {
//                    for (CompilerError error : allErrors) {
//                        errorWriter.println(error);
//                    }
                    for(int i = 0;i < allErrors.size();i++) {
                        CompilerError compilerError = allErrors.get(i);
                        if (i < allErrors.size()-1 && compilerError.equals(allErrors.get(i+1))){
                            continue;
                        }
                        errorWriter.println(compilerError);
                    }
                }
                System.out.println("Errors found during compilation. Output written to " + errorFile);

            } else {
                // 如果没有任何错误，则将缓存的正确分析过程输出到 parser.txt

                try (PrintWriter outputWriter = new PrintWriter(outputFile, StandardCharsets.UTF_8)) {
                    //outputWriter.print(stringWriter.toString());
                    //按顺序输出符号表中字段
//                    semanticVisitor.getAllSymbols().forEach(symbol -> {
//                        outputWriter.println(symbol.toString());
//                    });
                    List<Symbol> allSymbols = semanticVisitor.getAllSymbols();
//                    for (Symbol symbol : allSymbols) {
//                        if (symbol.getName().equals("main") || symbol.getName().equals("getint")) {
//                            continue;
//                        }
//
//                        outputWriter.println(symbol.toString());
//                    }
                    for(int i = 0; i < allSymbols.size(); i++) {
                        Symbol symbol = allSymbols.get(i);
                        if (symbol.getName().equals("main") || symbol.getName().equals("getint")) {
                            continue;
                        }
//                        if (i < allSymbols.size()-1 && symbol.getName().equals(allSymbols.get(i+1).getName())) {//去重
//                            continue;
//                        }
                        outputWriter.println(symbol.toString());
                    }
                }
                System.out.println("Compilation successful. Output written to " + outputFile);
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}