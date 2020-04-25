package lol.cicco.ioc.core.module.property;

public interface PropertyChangeListener {

    /**
     * 监听属性名称
     */
    String propertyName();

    /**
     * 监听器标识 重复标识无法注册
     */
    String listenerSign();

    /**
     * 属性改变时执行方法
     */
    void onChange();
}
