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

package restelio.router.listener;

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restelio.router.event.LifecycleEvent;

/**
 * Request lifecycle event listener
 * @author Matteo Giaccone
 */
public abstract class LifecycleEventListener {

    static final Logger log = LoggerFactory.getLogger(LifecycleEventListener.class);

    static final ThreadLocal<Stopwatch> stopwatch = new ThreadLocal<Stopwatch>();

    public void onRequestStarted(final String path) {
        // Default empty implementation
    }

    public void onRequestCompleted(final String path) {
        // Default empty implementation
    }

    public void onHandlerReady(final String path) {
        // Default empty implementation
    }

    public void onHandlerCleanup(final String path) {
        // Default empty implementation
    }

    @Subscribe
    @AllowConcurrentEvents
    public final void handle(LifecycleEvent event) {
        switch (event.getEventId()) {
            case LifecycleEvent.REQUEST_STARTED:
                stopwatch.set(Stopwatch.createStarted());
                onRequestStarted(event.getPath());
                break;

            case LifecycleEvent.REQUEST_COMPLETED:
                onRequestCompleted(event.getPath());
                stopwatch.get().stop();
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Request handling completed in %s", stopwatch.get().toString()));
                }
                stopwatch.remove();
                break;

            case LifecycleEvent.HANDLER_READY:
                onHandlerReady(event.getPath());
                break;

            case LifecycleEvent.HANDLER_CLEANUP:
                onHandlerCleanup(event.getPath());
                break;

            default:
                log.info("Unknown lifecycle event: " + event.getEventId());
                break;
        }
    }

}
