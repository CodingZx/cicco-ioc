package lol.cicco.ioc.core.binder;

import java.util.UUID;

public class UUIDBindHandler extends GeneralBindHandler<UUID> {

    private static final UUIDBindHandler handler = new UUIDBindHandler();

    private UUIDBindHandler() {
        super(UUID.class);
    }

    static BindHandler<?> create(){
        return handler;
    }

    @Override
    public UUID covertProperty(String propertyName, String propertyValue) {
        return UUID.fromString(propertyValue);
    }
}
