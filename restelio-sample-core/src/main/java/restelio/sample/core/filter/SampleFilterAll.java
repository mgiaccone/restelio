/*
 * Copyright 2014 Matteo Giaccone and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package restelio.sample.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.annotation.RestelioFilter;
import restelio.router.exception.RestException;
import restelio.router.RouteFilter;
import restelio.router.RouteFilterChain;
import restelio.support.RequestContext;

@RestelioFilter(value = "/*")
public class SampleFilterAll implements RouteFilter {

    static final Logger log = LoggerFactory.getLogger(SampleFilterAll.class);

    @Override
    public void apply(RequestContext context, RouteFilterChain chain) throws RestException {

    }

}
