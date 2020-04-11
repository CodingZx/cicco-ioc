//package lol.cicco.ioc.core.aop;
//
//import javassist.util.proxy.ProxyFactory;
//import lombok.SneakyThrows;
//
//import java.lang.reflect.Method;
//import java.util.List;
//import java.util.Map;
//
//final class JavassistEnhance {
//
//    @SneakyThrows
//    static Object proxyEnhance(Class<?> originCls, Map<Method, List<Interceptor>> interceptors) {
//        ProxyFactory factory = new ProxyFactory();
//        factory.setSuperclass(originCls);
//        factory.setUseCache(true);
//        factory.setFilter(interceptors::containsKey);
//        return factory.create(new Class<?>[]{}, new Object[]{}, (self, thisMethod, proceed, args) -> {
//            List<Interceptor> hasInterceptors = interceptors.get(thisMethod);
//            if (hasInterceptors == null) {
//                return thisMethod.invoke(self, args);
//            }
//
//            JoinPointImpl point = new JoinPointImpl(self, thisMethod, args);
//            for (Interceptor interceptor : hasInterceptors) {
//                interceptor.before(point);
//            }
//
//            Object result = proceed.invoke(self, args);
//
//            point.setReturnValue(result);
//            for (Interceptor interceptor : hasInterceptors) {
//                interceptor.after(point);
//            }
//            return result;
//        });
//    }
//}
