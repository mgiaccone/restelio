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

package restelio.router.event;

public class LifecycleEvent implements Event {

    public static final int REQUEST_STARTED     = 0;
    public static final int REQUEST_COMPLETED   = 1;
    public static final int HANDLER_READY       = 2;
    public static final int HANDLER_CLEANUP     = 3;

    public static LifecycleEvent requestStarted(final String path) {
        return new LifecycleEvent(REQUEST_STARTED, path);
    }

    public static LifecycleEvent requestCompleted(final String path) {
        return new LifecycleEvent(REQUEST_COMPLETED, path);
    }

    public static LifecycleEvent handlerReady(final String path) {
        return new LifecycleEvent(HANDLER_READY, path);
    }

    public static LifecycleEvent handlerCleaned(final String path) {
        return new LifecycleEvent(HANDLER_CLEANUP, path);
    }

    private int eventId;
    private String path;

    public LifecycleEvent(int eventId, String path) {
        this.eventId = eventId;
        this.path = path;
    }

    public int getEventId() {
        return eventId;
    }

    public String getPath() {
        return path;
    }
}
