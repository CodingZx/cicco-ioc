package lol.cicco.ioc.core.binder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NumberBindHandler {

    private static final Collection<BindHandler<?>> handlers = new LinkedList<>();

    static {
        synchronized (NumberBindHandler.class) {
            handlers.add(new GeneralBindHandler<Integer>(Integer.TYPE) {
                @Override
                public Integer covertProperty(String propertyName, String propertyValue) {
                    return Integer.parseInt(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Byte>(Byte.TYPE) {
                @Override
                public Byte covertProperty(String propertyName, String propertyValue) {
                    return Byte.parseByte(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Short>(Short.TYPE) {
                @Override
                public Short covertProperty(String propertyName, String propertyValue) {
                    return Short.parseShort(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Long>(Long.TYPE) {
                @Override
                public Long covertProperty(String propertyName, String propertyValue) {
                    return Long.parseLong(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Double>(Double.TYPE) {
                @Override
                public Double covertProperty(String propertyName, String propertyValue) {
                    return Double.parseDouble(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<Float>(Float.TYPE) {
                @Override
                public Float covertProperty(String propertyName, String propertyValue) {
                    return Float.parseFloat(propertyValue);
                }
            });

            handlers.add(new GeneralBindHandler<AtomicInteger>(AtomicInteger.class) {
                @Override
                public AtomicInteger covertProperty(String propertyName, String propertyValue) {
                    return new AtomicInteger(Integer.parseInt(propertyValue));
                }
            });

            handlers.add(new GeneralBindHandler<AtomicLong>(AtomicLong.class) {
                @Override
                public AtomicLong covertProperty(String propertyName, String propertyValue) {
                    return new AtomicLong(Long.parseLong(propertyValue));
                }
            });

            handlers.add(new GeneralBindHandler<BigDecimal>(BigDecimal.class) {
                @Override
                public BigDecimal covertProperty(String propertyName, String propertyValue) {
                    return BigDecimal.valueOf(Double.parseDouble(propertyValue));
                }
            });

            handlers.add(new GeneralBindHandler<BigInteger>(BigInteger.class) {
                @Override
                public BigInteger covertProperty(String propertyName, String propertyValue) {
                    return BigInteger.valueOf(Long.parseLong(propertyValue));
                }
            });
        }
    }

    private NumberBindHandler() {
    }

    static Collection<BindHandler<?>> create(){
        return handlers;
    }

}
