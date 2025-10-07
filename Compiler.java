import error.CompilerError;
import frontend.Lexer;
import error.LexerError;
import frontend.Token;
import frontend.TokenType;
import error.SyntaxError;
import parser.Parser;

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
        String outputFile = "parser.txt";
        String errorFile = "error.txt";

        // --- 核心数据结构 ---
        List<CompilerError> allErrors = new ArrayList<>();
        List<Token> tokens;

        try(StringWriter stringWriter = new StringWriter()) {
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
            //StringWriter stringWriter = new StringWriter();
            try (PrintWriter parserOutputWriter = new PrintWriter(stringWriter)) {

                Parser parser = new Parser(tokens, parserOutputWriter);
                parser.parse(); // 启动语法分析过程

                // 收集语法阶段发现的错误
                allErrors.addAll(parser.getErrors());
            }


            // ===================================
            //         3. 决策与输出阶段
            // ===================================

            if (!allErrors.isEmpty()) {
                // 如果总错误列表非空，则只输出到 error.txt

                // **关键步骤：对所有收集到的错误按行号排序**
                Collections.sort(allErrors);

                try (PrintWriter errorWriter = new PrintWriter(errorFile, StandardCharsets.UTF_8)) {
                    for (CompilerError error : allErrors) {
                        errorWriter.println(error);
                    }
                }
                System.out.println("Errors found during compilation. Output written to " + errorFile);

            } else {
                // 如果没有任何错误，则将缓存的正确分析过程输出到 parser.txt

                try (PrintWriter outputWriter = new PrintWriter(outputFile, StandardCharsets.UTF_8)) {
                    outputWriter.print(stringWriter.toString());
                }
                System.out.println("Compilation successful. Output written to " + outputFile);
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
        //

    }
}