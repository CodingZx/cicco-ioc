package lol.cicco.ioc.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class MybatisConstants {

    static SqlSessionFactory factory;

    private static final ThreadLocal<SqlSession> local = new ThreadLocal<>();

    public static SqlSession init() {
        if (local.get() != null) {
            return local.get();
        }
        var session = factory.openSession(false);
        local.set(session);
        return session;
    }

    public static SqlSession getSession() {
        return local.get();
    }

    public static void clear() {
        local.get().close();
        local.remove();
    }

}
