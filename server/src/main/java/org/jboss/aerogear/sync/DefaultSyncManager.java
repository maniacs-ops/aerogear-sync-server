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

import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DefaultSyncManager implements SyncManager {

    private final HttpClient httpClient;
    private final StdCouchDbInstance stdCouchDbInstance;
    private final StdCouchDbConnector db;

    public DefaultSyncManager(final String url, final String dbName)  {
        try {
            httpClient = new StdHttpClient.Builder().url(url).build();
        } catch (final MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        stdCouchDbInstance = new StdCouchDbInstance(httpClient);
        db = new StdCouchDbConnector(dbName, stdCouchDbInstance);
        db.createDatabaseIfNotExists();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Document read(final String id, final String revision) {
        final Map<String, String> map = db.get(Map.class, id, revision);
        return toDocument(map);
    }

    @Override
    public Document create(final String json) {
        final Map<String, String> map = asMap(UUID.randomUUID().toString(), null, json);
        db.create(map);
        return toDocument(map);
    }

    private static Map<String, String> asMap(final String id, final String revision, final String content) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("_id", id);
        if (revision != null) {
            map.put("_rev", revision);
        }
        map.put("content", content);
        return map;
    }

    private static Document toDocument(final Map<String, String> map) {
        return new DefaultDocument(map.get("_id"), map.get("_rev"), map.get("content"));
    }

    @Override
    public Document update(final Document doc) throws ConflictException {
        try {
            final Map<String, String> map = asMap(doc.id(), doc.revision(), doc.content());
            db.update(map);
            return toDocument(map);
        } catch (final UpdateConflictException e) {
            throw new ConflictException(doc, e);
        }
    }

    @Override
    public Document delete(final String revision) {
        return null;
    }
}
