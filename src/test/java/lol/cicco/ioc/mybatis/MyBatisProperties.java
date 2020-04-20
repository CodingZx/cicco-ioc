package lol.cicco.ioc.mybatis;

import lol.cicco.ioc.annotation.Property;
import lol.cicco.ioc.annotation.Registration;
import lombok.Data;

@Data
@Registration
@Property(prefix = "mybatis")
public class MyBatisProperties {
    private String mappers;
    private String handles;
    private String xmlLocation;
}
