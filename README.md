# cicco-ioc
简单IOC实现

包含自动扫描, 实例管理, 注解方法拦截器, 属性刷新/注入功能

#### 初始化
```java
IOC.initialize()
    .scanBasePackages("lol.cicco.ioc.bean") // 设置扫描包 扫描包中所有注册至IOC的Class信息存放至BeanRegistry
    .loadProperties("app.prop","prop/app1.prop") // 加载配置文件中属性键值至PropertyRegistry中
    .registerModule(new MybatisModule()) // 注册自定义模块 
    .done(); // 初始化完成 根据初始化配置进行IOC初始化
```
初始化所有接口请参阅<a href="https://github.com/CodingZx/cicco-ioc/blob/master/src/main/java/lol/cicco/ioc/core/Initialize.java">此处</a>
关于自定义模块的编写请参阅<a href="https://github.com/CodingZx/cicco-ioc/tree/master/src/test/java/lol/cicco/ioc/mybatis">IOC中集成Mybatis示例</a>
#### 使用IOC

- 定义Bean
```java
// Class定义
@Registration(name = "testBean2") // 此注解表明注册至Bean管理器
public class TestBean2 {
    //设置注入Bean实例, byName不为空时根据BeanName注入对应实例 
    @Inject(byName = "testBean1111")
    private TestBean testBean;

    @Inject(required = false, byName = "noReg2") // required 为是否必须注入
    // 对应bean未在容器中找到的情况下 如果require设置为false 则不会注入, 为true则会产生异常
    private NoRegisterClass cls;
}

// Method定义
@Registration
public class MethodDefineClass {
    @Registration // 将接口对象注册至IOC
    public TestBeanInterface createBean() {
        return new TestBeanInterface() {
            @Override
            @SystemClock
            public void run() {
                System.out.println("run...");
            }

            @Override
            @SystemClock
            public void aabbc() {
                System.out.println("aabbc");
            }
        };
    }
}

// 定义接口
public interface TestBeanInterface {
    @SystemLog
    void run();
    @SystemLog
    void aabbc();
}
```
- 使用Bean
> // 根据Type获取实例 <br>
> var obj = IOC.getBeanByType(TestBean2.class); <br>
> // 根据Name获取实例 <br>
> var obj = IOC.getBeanByName("testBean2"); <br>

#### 使用属性注入
- 添加Binder注解
```java
@Registration(name = "testBean2")
public class TestBean2 {
    // value对应此字段绑定PropertyRegistry的属性名称, 此时属性名称a.text
    // defaultValue值为当PropertyRegistry文件中无此属性时, 默认注入的属性. 此时为a.text不存在则注入为"默认文本"的值
    // refresh表示是否运行时更改绑定属性值,对象中showAText是否同时改变, 
    // 此例中 如果使用IOC.setProperty("a.text","改变值")修改绑定属性值, 则已使用对象的此字段自动更改为"改变值"
    @Binder(value = "a.text", defaultValue = "默认文本", refresh = true)
    private String showAText;
    // 可以使用IOC.setProperty("test.enum", "THREE")
    // 也可以将配置属性放至Property文件中, 在IOC.initialize()中加载配置文件进行初始化
    // noValueToNull若为true , 则属性不存在时且没有默认值时注入Null 
    // noValueToNull若为false , 则属性不存在时且没有默认值时抛出异常
    @Binder(value = "test.enum", noValueToNull = true)
    private TestEnum testEnum; 
}
```
- 增加类型处理器
```java
// 定义枚举
public enum TestEnum {
    ONE,
    TWO,
    THREE,
    ;
}
// 使用枚举处理器
@Registration
public class TestEnumHandler extends EnumPropertyHandler<TestEnum> {
    public TestEnumHandler() {
        super(TestEnum.class);
    }
}

// 注册LocalDateTime类型转换器
// 针对同一种类型的转换器可以注册多个, 会按顺序尝试转换, 如果全都转换失败才会抛出异常
@Registration // 类型转换器一定要注册至IOC!!!!!
public class LocalDateTimeBinderHandler extends GeneralPropertyHandler<LocalDateTime> {

    public LocalDateTimeBinderHandler() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime covertNonNullProperty(String propertyName, String propertyValue) {
        return LocalDateTime.parse(propertyValue, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
    }
}

```


#### 使用拦截器
- 编写自定义注解拦截器
```java
// 创建注解拦截器拦截器
@Registration // 必须注册至Bean中才能使用.
public class TimeInterceptor implements AnnotationInterceptor<SystemClock> {

    private final ThreadLocal<Long> threadLocal;

    public TimeInterceptor() {
        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public Class<SystemClock> getAnnotation() {
        return SystemClock.class;
    }
    
    // 方法执行前调用
    @Override
    public void before(BeforeJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("执行开始时间: " + start);
        threadLocal.set(start);
    }
    
    // 方法执行后调用
    @Override
    public void after(AfterJoinPoint point) throws Throwable {
        long start = threadLocal.get();
        long end = System.currentTimeMillis();
        System.out.println("执行结束时间: " + end);
        System.out.println("总执行时间  : " + (end - start) + "ms");
        threadLocal.remove();
    }

    // 方法抛出异常时调用
    @Override
    public void throwException(ThrowJoinPoint point) throws Throwable {
        // ...
    }
}

// 创建自定义注解
@Retention(RUNTIME)
@Target(METHOD)
public @interface SystemClock {
}

// 使用
@SystemClock
public void interceptorMethod() {
    // ....
}
```
使用实例参见<a href="https://github.com/CodingZx/cicco-ioc/blob/master/src/test/java/lol/cicco/ioc/IOCTest.java">此处</a>