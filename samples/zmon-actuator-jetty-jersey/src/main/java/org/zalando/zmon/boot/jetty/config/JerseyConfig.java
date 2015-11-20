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
package org.zalando.zmon.boot.jetty.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.zalando.zmon.boot.jetty.resources.SimpleResource;
import org.zalando.zmon.jaxrs.jersey.BaseJerseyConfig;
import org.zalando.zmon.jaxrs.jersey.BestMatchingPatternFilter;

/**
 * Auto-Registration of {@link BestMatchingPatternFilter}.<br/>
 *
 * @author  jbellmann
 */
@Configuration
@Profile("!example2")
public class JerseyConfig extends BaseJerseyConfig {

    @PostConstruct
    public void init() {
        register(SimpleResource.class);
    }
}
