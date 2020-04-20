package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.annotation.Registration;
import lombok.Data;

@Data
@Registration
@Property(prefix = "mybatis.datasource")
public class DataSourceProperties {
    private String url;
    private String userName;
    private String password;
    private String driverClassName;
    private int minimumIdle = 1;
    private int maximumPoolSize = 10;
    private int connectionTimeoutMs = 30000;
    private int idleTimeout = 300000;
    private int maxLifetime = 1800000;
}
