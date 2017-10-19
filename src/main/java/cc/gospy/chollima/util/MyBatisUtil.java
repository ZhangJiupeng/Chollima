package cc.gospy.chollima.util;

import cc.gospy.chollima.entity.bean.Spider;
import cc.gospy.chollima.entity.bean.mapper.SpiderMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

import static cc.gospy.chollima.util.Constant.MYBATIS_CONFIG_PATH;

public class MyBatisUtil {
    private static SqlSessionFactory factory;

    static {
        try {
            InputStream inputStream = Resources.getResourceAsStream(MYBATIS_CONFIG_PATH);
            factory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return factory;
    }

    @FunctionalInterface
    public interface SessionHandler<T> {
        void handle(T mapper);
    }

    public static <T> void execute(Class<T> clazz, SessionHandler<T> handler) {
        SqlSession sqlSession = getSqlSessionFactory().openSession();
        T mapper = sqlSession.getMapper(clazz);
        handler.handle(mapper);
        sqlSession.commit();
        sqlSession.close();
    }

    public static void main(String[] args) {
        execute(SpiderMapper.class, mapper -> mapper.insert(new Spider()));
    }
}
