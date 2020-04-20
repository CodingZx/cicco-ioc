package lol.cicco.ioc.service;

import lol.cicco.ioc.annotation.Inject;
import lol.cicco.ioc.annotation.Registration;
import lol.cicco.ioc.entity.TestEntity;
import lol.cicco.ioc.mapper.TestMapper;
import lol.cicco.ioc.mybatis.Transaction;

import java.util.List;
import java.util.UUID;

@Registration
public class TestBeanService {
    @Inject
    private TestMapper testMapper;

    @Transaction
    public void save(boolean throwException) {
        TestEntity testEntity = new TestEntity();
        testEntity.setId(UUID.randomUUID());
        testEntity.setName("lalalala-" + Thread.currentThread().getName());
        testMapper.save(testEntity);

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId(UUID.randomUUID());
        testEntity2.setName("lalalala-" + Thread.currentThread().getName());
        testMapper.save(testEntity2);

        if (throwException) {
            throw new RuntimeException();
        }
    }

    public void delete(UUID uuid) {
        testMapper.delete(uuid);
    }

    public List<TestEntity> all() {
        return testMapper.findAll();
    }
}
