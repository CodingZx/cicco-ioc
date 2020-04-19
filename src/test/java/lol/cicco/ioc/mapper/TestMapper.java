package lol.cicco.ioc.mapper;

import lol.cicco.ioc.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TestMapper {
    List<TestEntity> findAll();

    void save(TestEntity testEntity);

    void delete(@Param("id") UUID id);
}
