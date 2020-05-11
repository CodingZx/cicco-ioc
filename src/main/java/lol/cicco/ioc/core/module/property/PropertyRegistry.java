package lol.cicco.ioc.core.module.property;

public interface PropertyRegistry {

    /**
     * 注册属性转换器
     */
    void registerHandler(PropertyHandler<?> handler);

    /**
     * 设置属性
     */
    void setProperty(String propertyName, String propertyValue);

    /**
     * 转换对应属性
     */
    <T> T convertValue(String propName, String defaultValue, Class<T> type);

    /**
     * 获取属性
     */
    String getProperty(String propertyName, String defaultValue);

    /**
     * 删除属性
     */
    void removeProperty(String propertyName);

    /**
     * 注册属性监听器
     */
    boolean registerPropertyListener(PropertyChangeListener listener);

    /**
     * 移除属性监听器
     */
    void removePropertyListener(String propertyName, String listenerSign);
}
