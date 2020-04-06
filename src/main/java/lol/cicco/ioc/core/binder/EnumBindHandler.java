package lol.cicco.ioc.core.binder;

public class EnumBindHandler<T extends Enum<T>> extends GeneralBindHandler<Enum<T>> {

    private Class<T> clsType;

    public EnumBindHandler(Class<T> cls) {
        super(cls);
        this.clsType = cls;
    }

    @Override
    public Enum<T> covertProperty(String propertyName, String propertyValue) {
        return Enum.valueOf(clsType, propertyValue);
    }
}
