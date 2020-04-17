package lol.cicco.ioc.core.module.property;

public class EnumPropertyHandler<T extends Enum<T>> extends GeneralPropertyHandler<Enum<T>> {

    private final Class<T> clsType;

    public EnumPropertyHandler(Class<T> cls) {
        super(cls);
        this.clsType = cls;
    }

    @Override
    public Enum<T> covertProperty(String propertyName, String propertyValue) {
        return Enum.valueOf(clsType, propertyValue);
    }
}
