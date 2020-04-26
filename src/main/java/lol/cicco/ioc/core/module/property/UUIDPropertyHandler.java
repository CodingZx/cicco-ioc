package lol.cicco.ioc.core.module.property;

import java.util.UUID;

public class UUIDPropertyHandler extends GeneralPropertyHandler<UUID> {

    private static final UUIDPropertyHandler handler = new UUIDPropertyHandler();

    private UUIDPropertyHandler() {
        super(UUID.class);
    }

    static PropertyHandler<?> create() {
        return handler;
    }

    @Override
    public UUID covertNonNullProperty(String propertyName, String propertyValue) {
        return UUID.fromString(propertyValue);
    }
}
