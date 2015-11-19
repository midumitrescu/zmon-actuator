package org.zalando.zmon.actuator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by mdumitrescu on 11/18/15.
 */
public class ZmonRestFilterBeanPostProcessor implements BeanPostProcessor {

    private final ZmonRestResponseFilter zmonRestResponseFilter;

    @Autowired
    public ZmonRestFilterBeanPostProcessor(final ZmonRestResponseFilter zmonRestResponseFilter) {
        this.zmonRestResponseFilter = zmonRestResponseFilter;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object o, final String s) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(final Object o, final String s) throws BeansException {
        return null;
    }

}
