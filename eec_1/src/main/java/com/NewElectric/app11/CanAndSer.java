package com.NewElectric.app11;

import java.io.FileDescriptor;

/**
 * jni实现类 不可移动 jni映射绑定
 * 或者重新编译so包 重新映射
 */

public class CanAndSer {

    static {
        System.loadLibrary("CanAndSer");
    }

    public native static FileDescriptor openCan();

    public native static FileDescriptor openSer(String path ,int baudrate);

}
