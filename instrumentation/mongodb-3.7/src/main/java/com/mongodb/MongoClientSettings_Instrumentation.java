/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.mongodb;

import com.mongodb.event.CommandListener;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.agent.mongodb.NewRelicCommandListener;

import java.util.concurrent.atomic.AtomicBoolean;

@Weave(type = MatchType.ExactClass, originalName = "com/mongodb/MongoClientSettings")
public class MongoClientSettings_Instrumentation {

    @Weave(type = MatchType.ExactClass, originalName = "com/mongodb/MongoClientSettings$Builder")
    public static class Builder {

        @NewField
        private final AtomicBoolean listenerAdded = new AtomicBoolean(false);

        // Gotta match the existing constructor
        private Builder() {
        }

        public Builder addCommandListener(final CommandListener commandListener) {
            return Weaver.callOriginal();
        }

        public MongoClientSettings_Instrumentation build() {
            if (listenerAdded.compareAndSet(false, true)) {
                addCommandListener(new NewRelicCommandListener());
            }
            return Weaver.callOriginal();
        }
    }
}
