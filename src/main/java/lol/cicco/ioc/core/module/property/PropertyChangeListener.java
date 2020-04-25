package lol.cicco.ioc.core.module.property;

public interface PropertyChangeListener {

    /**
     * 监听属性名称
     */
    String propertyName();

    /**
     * 目标对象
     */
    Object getObject();

    /**
     * 属性改变时执行方法
     */
    void onChange();
}
