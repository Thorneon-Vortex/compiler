package codegen.ir.values;

import codegen.ir.types.Type;

// 基类
public class Constant extends Value {
    public Constant(Type type) { super(type, ""); }
}