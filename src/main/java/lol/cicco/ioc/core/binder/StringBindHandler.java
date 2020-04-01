package lol.cicco.ioc.core.binder;

public class StringBindHandler extends GeneralBindHandler<String> {

    public StringBindHandler() {
        super(String.class);
    }

    @Override
    public String covertProperty(String propertyName, String propertyValue) {
        return propertyValue;
    }

}
