package lol.cicco.ioc.util;

import java.util.HashSet;
import java.util.Set;

public class ClassUtils {

    private ClassUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * 获得Class所实现所有类和接口
     */
    public static Set<Class<?>> getClassTypes(Class<?> cls) {
        if (Object.class.equals(cls)) {
            return new HashSet<>();
        }
        Set<Class<?>> allCastClasses = new HashSet<>();
        allCastClasses.add(cls); // 添加自身

        for (Class<?> clsInterface : cls.getInterfaces()) {
            allCastClasses.addAll(getClassTypes(clsInterface));
        }

        Class<?> superCls = cls.getSuperclass();
        if (superCls != null) {
            allCastClasses.addAll(getClassTypes(superCls));
        }
        return allCastClasses;
    }

}
