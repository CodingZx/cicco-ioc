package lol.cicco.ioc.core.binder;

public class StringBindHandler extends GeneralBindHandler<String> {

    public static final StringBindHandler handler = new StringBindHandler();

    private StringBindHandler() {
        super(String.class);
    }

    static BindHandler<?> create() {
        return handler;
    }

    @Override
    public String covertProperty(String propertyName, String propertyValue) {
        return propertyValue;
    }

}
