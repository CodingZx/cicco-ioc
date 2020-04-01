package lol.cicco.ioc.core.binder;

import java.util.UUID;

public class UUIDBindHandler extends GeneralBindHandler<UUID> {

    public UUIDBindHandler() {
        super(UUID.class);
    }

    @Override
    public UUID covertProperty(String propertyName, String propertyValue) {
        return UUID.fromString(propertyValue);
    }
}
