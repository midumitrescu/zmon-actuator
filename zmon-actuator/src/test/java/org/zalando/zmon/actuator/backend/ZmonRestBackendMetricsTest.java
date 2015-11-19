/**
 * Copyright (C) 2015 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zalando.zmon.actuator.backend;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.client.ClientHttpResponse;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import org.zalando.zmon.actuator.ExampleApplication;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ExampleApplication.class})
@WebIntegrationTest(randomPort = true, value = {"debug=false"})
public class ZmonRestBackendMetricsTest {

    private final Logger logger = LoggerFactory.getLogger(ZmonRestBackendMetricsTest.class);

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private MetricRegistry metricRegistry;

    private RestTemplate restTemplate;

    private final Random random = new Random(System.currentTimeMillis());

    @Before
    public void setUp() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metricRegistry).convertRatesTo(TimeUnit.SECONDS)
                                                  .convertDurationsTo(TimeUnit.MILLISECONDS).build();
        reporter.start(2, TimeUnit.SECONDS);

        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

                @Override
                public boolean hasError(final ClientHttpResponse response) throws IOException {

                    // we want them all to pass
                    return false;
                }

                @Override
                public void handleError(final ClientHttpResponse response) throws IOException { }
            });

    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 100; i++) {

            restTemplate.getForObject("http://localhost:" + port + "/hello", String.class);
            TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
        }

        assertThat(metricRegistry.getTimers().get("zmon.response.200.GET.hello")).isNotNull();
        assertThat(metricRegistry.getTimers().get("zmon.response.503.GET.hello")).isNotNull();

        String metricsEndpointResponse = restTemplate.getForObject("http://localhost:" + port + "/metrics",
                String.class);

        logger.info(metricsEndpointResponse);
    }

    @Configuration
    public static class BackendIntegratedConfiguration {

        @Bean
        public RestTemplate timeConsumingBackendCallingBean() { }

    }

}
