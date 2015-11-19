package org.zalando.zmon.actuator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.http.client.ClientHttpRequestInterceptor;

import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

@Component
public class ZmonRestFilterBeanPostProcessor implements BeanPostProcessor {
    private static final Log logger = LogFactory.getLog(ZmonRestFilterBeanPostProcessor.class);

    private final ZmonRestResponseFilter zmonRestResponseFilter;
    private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

    @Autowired
    public ZmonRestFilterBeanPostProcessor(final ZmonRestResponseFilter zmonRestResponseFilter) {
        this.zmonRestResponseFilter = zmonRestResponseFilter;
        interceptors.add(zmonRestResponseFilter);
    }

    @Override
    public Object postProcessBeforeInitialization(final Object possiblyRestTemplateBean, final String beanName)
        throws BeansException {

        if (possiblyRestTemplateBean instanceof RestTemplate) {

            RestTemplate restTemplateBean = (RestTemplate) possiblyRestTemplateBean;

            restTemplateBean.setInterceptors(interceptors);
            logger.info("Added " + ZmonRestFilterBeanPostProcessor.class.getCanonicalName() + " instance to "
                    + beanName);
        }

        return possiblyRestTemplateBean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object o, final String s) throws BeansException {
        return o;
    }

}
