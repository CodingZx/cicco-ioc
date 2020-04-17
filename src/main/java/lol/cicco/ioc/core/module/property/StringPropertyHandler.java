package lol.cicco.ioc.core.module.property;

public class StringPropertyHandler extends GeneralPropertyHandler<String> {

    public static final StringPropertyHandler handler = new StringPropertyHandler();

    private StringPropertyHandler() {
        super(String.class);
    }

    static PropertyHandler<?> create() {
        return handler;
    }

    @Override
    public String covertProperty(String propertyName, String propertyValue) {
        return propertyValue;
    }

}
