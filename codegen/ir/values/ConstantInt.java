package codegen.ir.values;

import codegen.ir.types.IntegerType;

public class ConstantInt extends Constant {
    private final int value;

    public ConstantInt(int value) {
        super(new IntegerType(32)); // 所有int常量都是i32类型
        this.value = value;
    }
    
    public int getValue() { return value; }

    // 常量在IR中直接用它的值表示
    @Override
    public String getAsOperand() { return String.valueOf(value); }
}