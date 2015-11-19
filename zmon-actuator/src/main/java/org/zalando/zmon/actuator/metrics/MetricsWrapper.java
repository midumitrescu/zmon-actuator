package org.zalando.zmon.actuator.metrics;

import java.io.IOException;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import org.springframework.stereotype.Component;

import org.springframework.web.servlet.HandlerMapping;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import com.google.common.base.Stopwatch;

@Component
public class MetricsWrapper {

    private static final String UNKNOWN_PATH_SUFFIX = "/unmapped";
    private final MetricRegistry metricRegistry;

    private static final Log logger = LogFactory.getLog(MetricsWrapper.class);

    @Autowired
    public MetricsWrapper(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public void recordClientRequestMetrics(final HttpServletRequest request, final String path, final int status,
            final long time) {
        String suffix = getFinalStatus(request);
        submitToTimer(getKey("zmon.response." + status + "." + request.getMethod().toUpperCase() + suffix), time);
    }

    public void recordBackendRoundTripMetrics(final HttpRequest request, final ClientHttpResponse response,
            final Stopwatch stopwatch) {

        try {
            submitToTimer(getKey(
                    "zmon.backend[" + request.getURI().toString() + "]." + request.getMethod().name().toUpperCase()
                        + "." + response.getStatusCode().toString()), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (IOException e) {
            logger.warn("Could not detect status for " + response);
        }
    }

    private String getFinalStatus(final HttpServletRequest request) {
        Object bestMatchingPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (bestMatchingPattern != null) {
            return fixSpecialCharacters(bestMatchingPattern.toString());
        }

        // always return unknown, using the full path leads to an explosion of metrics due to path variables
        return UNKNOWN_PATH_SUFFIX;
    }

    private String fixSpecialCharacters(final String value) {
        String result = value.replaceAll("[{}]", "-");
        result = result.replace("**", "-star-star-");
        result = result.replace("*", "-star-");
        result = result.replace("/-", "/");
        result = result.replace("-/", "/");
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }

        if (result.startsWith("-")) {
            result = result.substring(1);
        }

        return result;
    }

    private void submitToTimer(final String metricName, final long value) {
        try {
            Timer timer = metricRegistry.timer(metricName);
            timer.update(value, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("Unable to submit timer metric '" + metricName + "'", e);
        }
    }

    private String getKey(final String string) {

        // graphite compatible metric names
        String value = string.replace("/", ".");
        value = value.replace("..", ".");
        if (value.endsWith(".")) {
            value = value + "root";
        }

        if (value.startsWith("_")) {
            value = value.substring(1);
        }

        return value;
    }
}
