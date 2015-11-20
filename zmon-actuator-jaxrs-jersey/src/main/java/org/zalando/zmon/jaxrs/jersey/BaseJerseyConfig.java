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
package org.zalando.zmon.jaxrs.jersey;

import javax.annotation.PostConstruct;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;

/**
 * @author jbellmann
 *
 */
public class BaseJerseyConfig extends ResourceConfig {

	@Autowired
	private JerseyProperties jerseyProperties;

    public BaseJerseyConfig() { }

    @PostConstruct
    public void registerBestMatchingPatternFilter() {
        register(new BestMatchingPatternFilter(getJerseyProperties().getApplicationPath()));
    }

    protected JerseyProperties getJerseyProperties() {
        return this.jerseyProperties;
    }

}
