package codegen.ir;

import codegen.ir.values.User;
import codegen.ir.values.Value;

public class Use {
    private User user;   // 使用者
    private Value used;  // 被使用的值

    public Use(User user, Value used) {
        this.user = user;
        this.used = used;
        // 建立双向链接
        if (used != null) {
            used.addUse(this);
        }
    }

    public User getUser() { return user; }
    public Value getUsed() { return used; }

    // 当操作数被替换时，更新链接
    public void setUsed(Value newUsed) {
        // 1. 在旧的Value中断开链接
        if (this.used != null) {
            this.used.removeUse(this);
        }
        // 2. 更新自己
        this.used = newUsed;
        // 3. 在新的Value上建立链接
        if (newUsed != null) {
            newUsed.addUse(this);
        }
    }
}