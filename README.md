# cicco-ioc
简单IOC实现

### 初始化
- 初始化IOC
> var init = IOC.initialize();
- 加载配置文件
> init.loadProperties("/app.properties");
- 设置扫描包路径
> init.scanBasePackages("lol.cicco"); 或使用 init.scanBasePackageClasses(IOC.class);
- 添加绑定属性转换器
> init.registerPropertyHandler(new PropertyHandler<LocalDateTime>() {
>   ...
> });
- 设置注解AOP
> init.registerInterceptor(SystemClock.class, new TimeInterceptor());
- 初始化完成
> init.done();

### 使用IOC

- 设置Bean
> @Registration(name = "testBean2") <br>
> public class TestBean2 {          <br>
>    @Inject(byName = "testBean1111") // 设置注入Bean实例, byName不为空则根据Name注入对应实例 <br>
>    private TestBean testBean;     <br>
> <br>
>    @Binder("a.text") // 绑定加载的配置文件中的属性值 <br>
>    private String showAText; <br>
> <br>
>    @Inject(required = false, byName = "noReg2") // required 为是否必须注入, 对应bean未在容器中找到的情况下 如果require设置为false 对应bean未在容器中找到,则不会注入, 为true则会产生异常 <br>
>    private NoRegisterClass cls; <br>

- 根据Type获取实例
> var obj = IOC.getBeanByType(TestBean2.class);
- 根据Name获取实例
> var obj = IOC.getBeanByName("beanName"");
