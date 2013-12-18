/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.sync;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class DefaultSyncManagerTest {

    private static DefaultSyncManager syncManager;

    @BeforeClass
    public static void createDataStore() {
        syncManager = new DefaultSyncManager("http://127.0.0.1:5984", "sync-test");
    }

    @Test
    public void create() {
        final String json = "\"model\": \"Toyota\"}";
        final Document document = syncManager.create(json);
        assertThat(document.content(), equalTo(json));
    }
}