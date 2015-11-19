package org.zalando.zmon.actuator;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import org.springframework.stereotype.Component;

import org.zalando.zmon.actuator.metrics.MetricsWrapper;

import com.google.common.base.Stopwatch;

@Component
public class ZmonRestResponseFilter implements ClientHttpRequestInterceptor {

    private final MetricsWrapper metricsWrapper;

    @Autowired
    public ZmonRestResponseFilter(final MetricsWrapper metricsWrapper) {
        this.metricsWrapper = metricsWrapper;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
            final ClientHttpRequestExecution execution) throws IOException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        ClientHttpResponse response = execution.execute(request, body);
        stopwatch.stop();

        metricsWrapper.recordBackendRoundTripMetrics(request, response, stopwatch);
        return response;
    }
}
