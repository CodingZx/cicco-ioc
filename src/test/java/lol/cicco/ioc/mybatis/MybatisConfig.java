package lol.cicco.ioc.mybatis;

import com.zaxxer.hikari.HikariDataSource;
import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.core.scanner.ResourceMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Set;

@Slf4j
@Registration
public class MybatisConfig {

    @Inject
    private DataSourceProperties properties;
    @Inject
    private MyBatisProperties myBatisProperties;

    public SqlSessionFactory createSqlFactory() {
        log.debug("开始创建MybatisSqlFactory.");

        Environment env = new Environment("default", new JdbcTransactionFactory(), configDataSource());
        Configuration config = new Configuration(env);
        if (isNullOrEmpty(myBatisProperties.getMappers())) {
            for (String mapper : myBatisProperties.getMappers().split(",")) {
                config.addMappers(mapper);
            }
        }

        if (isNullOrEmpty(myBatisProperties.getHandles())) {
            config.getTypeHandlerRegistry().register(myBatisProperties.getHandles());
        }
        if (isNullOrEmpty(myBatisProperties.getXmlLocation())) {
            log.debug("开始扫描xml");
            Set<ResourceMeta> allFiles = new XmlResourceScanner().scanXml(myBatisProperties.getXmlLocation(), MybatisConfig.class.getClassLoader());
            log.debug("扫描到所有xml");
            for (ResourceMeta path : allFiles) {
                try {
                    String xmlPath = path.getFileName().substring(0, path.getFileName().lastIndexOf("."));
                    String suffix = path.getFileName().substring(path.getFileName().lastIndexOf("."));
                    XMLMapperBuilder xmls = new XMLMapperBuilder(
                            MybatisConfig.class.getResourceAsStream("/" + xmlPath.replace(".", "/") + suffix),
                            config, path.getFileName(), config.getSqlFragments());
                    xmls.parse();
                } catch (Exception e) {
                    throw new RuntimeException("无法加载配置文件...", e);
                }
            }
        }
        return new SqlSessionFactoryBuilder().build(config);
    }

    private boolean isNullOrEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    @SneakyThrows
    private DataSource configDataSource() {
        // 创建连接池
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(properties.getUrl());
        ds.setUsername(properties.getUserName());
        ds.setPassword(properties.getPassword());
        ds.setDriverClassName(properties.getDriverClassName());
        ds.setMinimumIdle(properties.getMinimumIdle());
        ds.setMaximumPoolSize(properties.getMaximumPoolSize());
        ds.setConnectionTimeout(properties.getConnectionTimeoutMs());
        ds.setIdleTimeout(properties.getIdleTimeout());
        ds.setMaxLifetime(properties.getMaxLifetime());
        ds.setReadOnly(false);
        ds.setAutoCommit(false);
        ds.setPoolName("DataSourcePool");
        ds.setConnectionTestQuery("select 1");

        log.debug("创建DataSource成功.");

        Runtime.getRuntime().addShutdownHook(new Thread(ds::close));

        try(Connection connection = ds.getConnection()) {
            connection.createStatement().execute("create table test(id varchar(255) NOT NULL,name varchar(255) NOT NULL, PRIMARY KEY (id) );");
        }

        return ds;
    }
}
