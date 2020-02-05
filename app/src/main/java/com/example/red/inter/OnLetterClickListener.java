package com.example.red.inter;

public interface OnLetterClickListener {
    //TODO 关闭
    void dismiss();

    //TODO 下一封
    void next(int pos);

    //Todo 回信
    void writeBack(int pos);

    //Todo 已阅  第一封信才调用这个接口  其他接口下一封就行了
    void readFruits();

}
