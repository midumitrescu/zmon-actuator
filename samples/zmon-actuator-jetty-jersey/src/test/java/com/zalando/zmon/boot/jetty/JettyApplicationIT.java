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
package com.zalando.zmon.boot.jetty;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.zalando.zmon.boot.jetty.JettyApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {JettyApplication.class})
@WebIntegrationTest
public class JettyApplicationIT {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void run() throws InterruptedException {
        RestTemplate rest = new RestTemplate();
        for (int i = 0; i < 100; i++) {
            ResponseEntity<String> response = rest.getForEntity("http://localhost:" + port + "/simple/" + i
                        + "/complex", String.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

    }

}
