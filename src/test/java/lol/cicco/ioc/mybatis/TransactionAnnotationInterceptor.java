package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.interceptor.AfterJoinPoint;
import lol.cicco.ioc.core.module.interceptor.AnnotationInterceptor;
import lol.cicco.ioc.core.module.interceptor.BeforeJoinPoint;
import lol.cicco.ioc.core.module.interceptor.ThrowJoinPoint;
import org.apache.ibatis.session.SqlSession;

@Registration
public class TransactionAnnotationInterceptor implements AnnotationInterceptor<Transaction> {

    @Override
    public Class<Transaction> getAnnotation() {
        return Transaction.class;
    }

    @Override
    public void before(BeforeJoinPoint point) {
        MybatisConstants.init();
    }

    @Override
    public void after(AfterJoinPoint point) {
        SqlSession sqlSession = MybatisConstants.getSession();
        sqlSession.commit();
        MybatisConstants.clear();
    }

    @Override
    public void throwException(ThrowJoinPoint point) {
        SqlSession sqlSession = MybatisConstants.getSession();
        sqlSession.rollback();
        MybatisConstants.clear();
    }
}
