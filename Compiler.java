import frontend.Lexer;
import frontend.LexerError;
import frontend.TokenType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// 为了方便存储Token信息，我们创建一个简单的记录类（Record）
// Java 16+
record Token(TokenType type, String value, int lineNum) {}
public class Compiler {
    public static void main(String[] args) {
        String inputFile = "testfile.txt";
        String outputFile = "lexer.txt";
        String errorFile = "error.txt";

        try {
            String sourceCode = new String(Files.readAllBytes(Paths.get(inputFile)), StandardCharsets.UTF_8);

            Lexer lexer = Lexer.getInstance();
            lexer.init(sourceCode);

            List<Token> tokens = new ArrayList<>();

            // --- 核心分析循环 ---
            while (true) {
                lexer.next(); // 分析下一个Token
                TokenType type = lexer.getTokenType();

                // 将每个分析出的Token（包括可能的错误Token）都存起来
                tokens.add(new Token(type, lexer.getToken(), lexer.getLineNum()));

                if (type == TokenType.EOF) {
                    break; // 到达文件末尾
                }
            }

            // --- 分析结束，进行决策 ---
            List<LexerError> errors = lexer.getErrors();

            if (!errors.isEmpty()) {
                // 如果错误列表非空，则输出到 error.txt
                try (PrintWriter writer = new PrintWriter(errorFile, StandardCharsets.UTF_8)) {
                    // 按行号排序（如果需要，虽然词法分析是顺序的，一般不用）
                    // errors.sort(Comparator.comparingInt(LexerError::lineNum));
                    for (LexerError error : errors) {
                        writer.println(error.getLineNum() + " " + error.getErrorType());
                    }
                }
                System.out.println("Lexical errors found. Output written to " + errorFile);

            } else {
                // 如果没有错误，则输出到 lexer.txt
                try (PrintWriter writer = new PrintWriter(outputFile, StandardCharsets.UTF_8)) {
                    for (Token token : tokens) {
                        // EOF Token 不需要输出
                        if (token.type() != TokenType.EOF) {
                            // 按照 "类别码 单词字符串" 的格式输出
                            writer.println(token.type().name() + " " + token.value());
                        }
                    }
                }
                System.out.println("Lexical analysis successful. Output written to " + outputFile);
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }
}