package codegen.ir.types;

public abstract class Type {
    // 强制所有子类实现 toString()，以便后续打印IR时能得到正确的类型字符串
    @Override
    public abstract String toString();
}