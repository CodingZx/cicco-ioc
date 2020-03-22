package lol.cicco.ioc;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.core.BeanDefinition;
import lol.cicco.ioc.core.BeanDefinitionStoreException;
import lol.cicco.ioc.core.BeanNotFountException;
import lol.cicco.ioc.core.ClassPathScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOC {

    private IOC() {
        throw new IllegalAccessError();
    }

    private static final Map<Class<?>, Object> typeBeans = new HashMap<>();

    public static void initialize(String scanPackage) {
        ClassPathScanner scanner = new ClassPathScanner();
        List<BeanDefinition> definitions = scanner.doScan(scanPackage, IOC.class.getClassLoader());

        synchronized (typeBeans) {
            for(BeanDefinition definition : definitions) {
                Class<?> type = definition.getBeanType();

                try {
                    Constructor<?> defConstructor = type.getConstructor();
                    Object obj = defConstructor.newInstance();
                    typeBeans.put(type, obj);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    throw new BeanDefinitionStoreException("could not found default constructor in ["+type.toString()+"]", e);
                }
            }

            for(Class<?> cls : typeBeans.keySet()) {
                Object obj = typeBeans.get(cls);

                Field[] fields = cls.getDeclaredFields();
                for(Field field : fields) {
                    if(field.getAnnotationsByType(Inject.class) == null) {
                        continue;
                    }
                    if(!field.canAccess(obj)) {
                        field.setAccessible(true);
                    }

                    Object injectObj = getBeanByType(field.getType());
                    try {
                        field.set(obj, injectObj);
                    } catch (IllegalAccessException ignore) {
                        // nop..
                    }
                }
            }
        }
    }

    public static <T> T getBeanByType(Class<T> beanCls){
        T obj = (T)typeBeans.get(beanCls);

        if(obj == null) {
            throw new BeanNotFountException("bean ["+beanCls.toString()+"] is not found in ioc");
        }
        return obj;
    }
}
