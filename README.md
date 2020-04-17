# cicco-ioc
简单IOC实现, 仅支持默认空构造方法创建Bean实例<br>
包含Bean实例管理, AOP, 属性注入

#### 初始化
- 初始化IOC
> var init = IOC.defaultInitialize();
- 加载配置文件
> init.loadProperties("/app.properties");
- 设置扫描包路径
> init.scanBasePackages("lol.cicco"); 或使用 init.scanBasePackageClasses(IOC.class);
- 添加绑定属性转换器, 相同类型可添加多个, 若前置转换失败会继续使用后续转换器转换
> init.registerPropertyHandler(new PropertyHandler<LocalDateTime>() {
>   ...
> });
- 初始化完成
> init.done();

#### 使用IOC

- 设置Bean
```java
@Registration(name = "testBean2")
public class TestBean2 {
    //设置注入Bean实例, byName不为空时根据BeanName注入对应实例 
    @Inject(byName = "testBean1111")
    private TestBean testBean;

    @Binder("a.text")  // Binder注解指定加载的配置文件中的属性值
    private String showAText;

    @Binder("test.enum")
    private TestEnum testEnum;

    @Inject(required = false, byName = "noReg2") // required 为是否必须注入
    // 对应bean未在容器中找到的情况下 如果require设置为false 则不会注入, 为true则会产生异常
    private NoRegisterClass cls;
}
```

- 根据Type获取实例
> var obj = IOC.getBeanByType(TestBean2.class);
- 根据Name获取实例
> var obj = IOC.getBeanByName("beanName"");

#### 使用AOP
- 使用自定义注解
```java
// 创建拦截器
@Registration
public class TimeInterceptor implements Interceptor {

    private long start;

    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        start = System.currentTimeMillis();
        System.out.println("执行开始时间: " + start);
    }

    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        long end = System.currentTimeMillis();
        System.out.println("执行结束时间: " + end);
        System.out.println("总执行时间  : " + (end - start) + "ms");
    }
}
// 创建自定义注解
@Retention(RUNTIME)
@Target(METHOD)
public @interface SystemClock {
}

// 使用
@SystemClock
public void aop() {
    // ....
}
```
