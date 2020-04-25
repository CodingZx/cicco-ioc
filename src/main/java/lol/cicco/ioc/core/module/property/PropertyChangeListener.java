package lol.cicco.ioc.core.module.property;

import lombok.Data;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

@Data
public class PropertyChangeListener {
    private WeakReference<?> object;
    private Field field;
    private String property;
    private String defaultValue;
}
