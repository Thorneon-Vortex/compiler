import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Compare {
    public static void main(String[] args) {
        String file1 = "parser.txt";
        String file2 = "parser1.txt";

        try {
            compareFiles(file1, file2);
        } catch (IOException e) {
            System.err.println("文件读取错误: " + e.getMessage());
        }
    }

    public static void compareFiles(String file1, String file2) throws IOException {
        Path path1 = Paths.get(file1);
        Path path2 = Paths.get(file2);

        // 读取文件内容到列表
        List<String> lines1 = Files.readAllLines(path1);
        List<String> lines2 = Files.readAllLines(path2);

        boolean filesEqual = true;
        int maxLines = Math.max(lines1.size(), lines2.size());

        System.out.println("比较文件 " + file1 + " 和 " + file2 + ":");

        // 逐行比较
        for (int i = 0; i < maxLines; i++) {
            String line1 = i < lines1.size() ? lines1.get(i) : "";
            String line2 = i < lines2.size() ? lines2.get(i) : "";

            if (!line1.equals(line2)) {
                filesEqual = false;
                System.out.println("第 " + (i + 1) + " 行不同:");
                if (i < lines1.size()) {
                    System.out.println("  " + file1 + ": " + line1);
                } else {
                    System.out.println("  " + file1 + ": (文件结束)");
                }
                if (i < lines2.size()) {
                    System.out.println("  " + file2 + ": " + line2);
                } else {
                    System.out.println("  " + file2 + ": (文件结束)");
                }
                System.out.println();
            }
        }

        // 检查文件行数是否相同
        if (lines1.size() != lines2.size()) {
            filesEqual = false;
            System.out.println("文件行数不同: " + file1 + " 有 " + lines1.size() + " 行, "
                    + file2 + " 有 " + lines2.size() + " 行");
        }

        if (filesEqual) {
            System.out.println("两个文件内容完全相同");
        }
    }
}

