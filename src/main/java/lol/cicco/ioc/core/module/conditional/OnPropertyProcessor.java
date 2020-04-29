package lol.cicco.ioc.core.module.conditional;

import lol.cicco.ioc.annotation.ConditionalOnProperty;
import lol.cicco.ioc.core.module.property.PropertyRegistry;

public class OnPropertyProcessor extends AbstractConditionalProcessor<ConditionalOnProperty> {

    private final PropertyRegistry propertyRegistry;

    public OnPropertyProcessor(PropertyRegistry registry) {
        super(ConditionalOnProperty.class);
        this.propertyRegistry = registry;
    }

    @Override
    public boolean doChecker(ConditionalOnProperty annotation) {
        for(String propertyName : annotation.name()) {
            String value = propertyRegistry.getProperty(propertyName, null);
            String havingValue = annotation.havingValue().trim();
            if(havingValue.equals("") && value == null) {
                continue;
            }
            if(!havingValue.equals(value)) {
                return false;
            }
        }
        return true;
    }
}
