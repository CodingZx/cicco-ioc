package lol.cicco.ioc.core.module.property;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberPropertyHandler {

    private static final Collection<PropertyHandler<?>> handlers = new LinkedList<>();

    static {
        handlers.add(new GeneralPropertyHandler<Integer>(Integer.TYPE) {
            @Override
            public Integer covertNonNullProperty(String propertyName, String propertyValue) {
                return Integer.parseInt(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Byte>(Byte.TYPE) {
            @Override
            public Byte covertNonNullProperty(String propertyName, String propertyValue) {
                return Byte.parseByte(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Short>(Short.TYPE) {
            @Override
            public Short covertNonNullProperty(String propertyName, String propertyValue) {
                return Short.parseShort(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Long>(Long.TYPE) {
            @Override
            public Long covertNonNullProperty(String propertyName, String propertyValue) {
                return Long.parseLong(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Double>(Double.TYPE) {
            @Override
            public Double covertNonNullProperty(String propertyName, String propertyValue) {
                return Double.parseDouble(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<Float>(Float.TYPE) {
            @Override
            public Float covertNonNullProperty(String propertyName, String propertyValue) {
                return Float.parseFloat(propertyValue);
            }
        });

        handlers.add(new GeneralPropertyHandler<AtomicInteger>(AtomicInteger.class) {
            @Override
            public AtomicInteger covertNonNullProperty(String propertyName, String propertyValue) {
                return new AtomicInteger(Integer.parseInt(propertyValue));
            }
        });

        handlers.add(new GeneralPropertyHandler<AtomicLong>(AtomicLong.class) {
            @Override
            public AtomicLong covertNonNullProperty(String propertyName, String propertyValue) {
                return new AtomicLong(Long.parseLong(propertyValue));
            }
        });

        handlers.add(new GeneralPropertyHandler<BigDecimal>(BigDecimal.class) {
            @Override
            public BigDecimal covertNonNullProperty(String propertyName, String propertyValue) {
                return BigDecimal.valueOf(Double.parseDouble(propertyValue));
            }
        });

        handlers.add(new GeneralPropertyHandler<BigInteger>(BigInteger.class) {
            @Override
            public BigInteger covertNonNullProperty(String propertyName, String propertyValue) {
                return BigInteger.valueOf(Long.parseLong(propertyValue));
            }
        });
    }

    private NumberPropertyHandler() {
    }

    static Collection<PropertyHandler<?>> create() {
        return handlers;
    }

}
