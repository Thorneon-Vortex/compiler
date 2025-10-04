// 在 parser 包或者一个通用的 error 包中
package error;

public abstract class CompilerError implements Comparable<CompilerError> {
    private int lineNum;//错误行号
    private String errorType;//错误类型

    public CompilerError(int lineNum, String errorCode) {
        this.lineNum = lineNum;
        this.errorType = errorCode;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public int compareTo(CompilerError other) {
        // 主要按行号排序
        return Integer.compare(this.lineNum, other.lineNum);
    }

    @Override
    public String toString() {
        return lineNum + " " + errorType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CompilerError that = (CompilerError) obj;
        return lineNum == that.lineNum && errorType.equals(that.errorType);
    }

    @Override
    public int hashCode() {
        return lineNum * 31 + errorType.hashCode();
    }
}