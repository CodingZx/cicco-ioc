package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.module.aop.AfterJoinPoint;
import lol.cicco.ioc.core.module.aop.BeforeJoinPoint;
import lol.cicco.ioc.core.module.aop.AnnotationInterceptor;
import lol.cicco.ioc.core.module.aop.ThrowJoinPoint;
import org.apache.ibatis.session.SqlSession;

@Registration
public class TransactionAnnotationInterceptor implements AnnotationInterceptor<Transaction> {

    @Override
    public Class<Transaction> getAnnotation() {
        return Transaction.class;
    }

    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        MybatisConstants.init();
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        SqlSession sqlSession = MybatisConstants.getSession();
        sqlSession.commit();
        MybatisConstants.clear();
    }

    @Override
    public void throwException(ThrowJoinPoint point) throws Throwable {
        SqlSession sqlSession = MybatisConstants.getSession();
        sqlSession.rollback();
        MybatisConstants.clear();
    }
}
