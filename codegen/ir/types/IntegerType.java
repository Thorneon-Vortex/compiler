package codegen.ir.types;

public class IntegerType extends Type {
    private final int bits;

    public IntegerType(int bits) { this.bits = bits; }

    // 例如: new IntegerType(32) -> "i32"
    //       new IntegerType(1)  -> "i1" (用于布尔值)
    @Override
    public String toString() { return "i" + bits; }
}