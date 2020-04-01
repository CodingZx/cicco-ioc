package lol.cicco.ioc.core.binder;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberBindHandler extends GeneralBindHandler<Number> {

    public NumberBindHandler(Type type) {
        super(type);
    }

    @Override
    public Number covertProperty(String propertyName, String propertyValue) {
        if (Integer.TYPE.equals(bindType)) {
            return Integer.parseInt(propertyValue);
        }
        if (Byte.TYPE.equals(bindType)) {
            return Byte.parseByte(propertyValue);
        }
        if (Short.TYPE.equals(bindType)) {
            return Short.parseShort(propertyValue);
        }
        if (Long.TYPE.equals(bindType)) {
            return Long.parseLong(propertyValue);
        }
        if (Double.TYPE.equals(bindType)) {
            return Double.parseDouble(propertyValue);
        }
        if (Float.TYPE.equals(bindType)) {
            return Float.parseFloat(propertyValue);
        }
        if (AtomicInteger.class.equals(bindType)) {
            return new AtomicInteger(Integer.parseInt(propertyValue));
        }
        if (AtomicLong.class.equals(bindType)) {
            return new AtomicLong(Long.parseLong(propertyValue));
        }
        if (BigDecimal.class.equals(bindType)) {
            return BigDecimal.valueOf(Double.parseDouble(propertyValue));
        }
        if (BigInteger.class.equals(bindType)) {
            return BigInteger.valueOf(Long.parseLong(propertyValue));
        }
        return null;
    }

}
