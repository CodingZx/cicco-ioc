package lol.cicco.ioc.mybatis;

import javassist.util.proxy.ProxyFactory;
import lol.cicco.ioc.core.module.beans.BeanProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;

@Slf4j
public class MapperProvider implements BeanProvider {

    private final Class<?> beanType;

    private Object target;

    MapperProvider(Class<?> type) {
        this.beanType = type;
    }

    @Override
    public Class<?> beanType() {
        return beanType;
    }

    @Override
    @SneakyThrows
    public Object getObject() {
        if (target != null) {
            return target;
        }
        ProxyFactory factory = new ProxyFactory();
        factory.setUseCache(true);
        factory.setInterfaces(new Class[]{beanType});

        target = factory.create(new Class<?>[]{}, new Object[]{}, (self, thisMethod, proceed, args) -> {
            boolean needClear = false;
            SqlSession sqlSession = MybatisConstants.getSession();
            if (sqlSession == null) {
                needClear = true;
                sqlSession = MybatisConstants.init();
            }
            var obj = sqlSession.getMapper(beanType);
            Method proxyMethod = beanType.getMethod(thisMethod.getName(), thisMethod.getParameterTypes());
            Object res = proxyMethod.invoke(obj, args);

            if (needClear) {
                sqlSession.commit();
                MybatisConstants.clear();
            }
            return res;
        });
        return target;
    }
}
