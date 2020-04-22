package lol.cicco.ioc.core.module.register;

import javassist.util.proxy.ProxyFactory;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.InterceptorRegistry;
import lol.cicco.ioc.core.module.aop.JoinPointImpl;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

abstract class AbstractBeanProvider implements BeanProvider {

}
