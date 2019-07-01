package com.update.zeus.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

/**
 * 由于应用程序使用的ClassLoader为PathClassLoader
 * 最终继承自 BaseDexClassLoader
 * 查看源码得知,这个BaseDexClassLoader加载代码根据一个叫做
 * dexElements的数组进行, 因此我们把包含代码的dex文件插入这个数组
 * 系统的classLoader就能帮助我们找到这个类
 * <p>
 * 这个类用来进行对于BaseDexClassLoader的Hook
 * 类名太长, 不要吐槽.
 *
 * @author weishu
 * @date 16/3/28
 */
public final class BaseDexClassLoaderHookHelper {
    public static void patchClassLoader(ClassLoader cl, File apkFile, File optDexFile) throws IOException {
        // 获取 BaseDexClassLoader # pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), cl, "pathList");

        // 获取 PathList # Element[] dexElements
        Object[] dexElements = (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");

        // Element类型
        Class<?> elementClass = dexElements.getClass().getComponentType();

        // 创建一个数组 用来替换原始的数组
        Object[] newElements = (Object[]) Array.newInstance(elementClass, dexElements.length + 1);
        // 构造插件 Element(File file, boolean isDirectory, File zip, DexFile dexFile) 这个构造函数
        Class[] c = {File.class, boolean.class, File.class, DexFile.class};
        Object[] p = {apkFile, false, apkFile, DexFile.loadDex(apkFile.getCanonicalPath(), apkFile.getAbsolutePath(), 0)};
        Object o = RefInvoke.createObject(elementClass, c, p);

        Object[] toAddElementArray = {o};

        //TODO 为什么这里是将插件置在末尾而不是第一位
        // 插件因为要保证宿主绝对可用,肯定是不能插件覆盖宿主的,所以放后面
        // 热修复要保证补丁可用,所以补丁放前面

        // 把原始的elements复制进去
        System.arraycopy(dexElements, 0, newElements, 0, dexElements.length);
        // 插件的那个element复制进去
        System.arraycopy(toAddElementArray, 0, newElements, dexElements.length, toAddElementArray.length);

        // 替换
        RefInvoke.setFieldObject(pathListObj, "dexElements", newElements);
    }
}
