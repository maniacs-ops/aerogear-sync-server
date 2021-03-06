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
package org.jboss.aerogear.sync.jsonpatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatch;
import org.jboss.aerogear.sync.PatchMessage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.jboss.aerogear.sync.jsonpatch.Patches.*;

public class JsonMapperTest {

    @Test
    public void patchMessageToJson() {
        final String documentId = "1234";
        final String clientId = "client1";
        final PatchMessage<JsonPatchEdit> patchMessage = patchMessage(documentId, clientId, newJsonPatchEdit());

        final String json = JsonMapper.toJson(patchMessage);
        final JsonNode jsonNode = JsonMapper.asJsonNode(json);
        assertThat(jsonNode.get("msgType").asText(), equalTo("patch"));
        assertThat(jsonNode.get("id").asText(), equalTo(documentId));
        assertThat(jsonNode.get("clientId").asText(), equalTo(clientId));
        final JsonNode editsNode = jsonNode.get("edits");
        assertThat(editsNode.isArray(), is(true));
        assertThat(editsNode.size(), is(1));
        final JsonNode edit = editsNode.iterator().next();
        assertThat(edit.get("serverVersion").asText(), equalTo("0"));
        assertThat(edit.get("clientVersion").asText(), equalTo("0"));
        final JsonNode diffs = edit.get("diffs");
        assertThat(diffs.isArray(), is(true));
        final JsonNode patch = diffs.get(0);
        assertThat(patch.get("op").asText(), equalTo("replace"));
        assertThat(patch.get("path").asText(), equalTo("/name"));
        assertThat(patch.get("value").asText(), equalTo("Fletch"));
    }

    @Test
    public void patchMessageFromJson() {
        final String documentId = "1234";
        final String clientId = "client1";
        final JsonPatch patch = jsonPatch();
        final String json = JsonMapper.toJson(patchMessage(documentId, clientId, newJsonPatchEdit(patch)));
        final JsonPatchMessage patchMessage = JsonMapper.fromJson(json, JsonPatchMessage.class);
        assertThat(patchMessage.documentId(), equalTo(documentId));
        assertThat(patchMessage.clientId(), equalTo(clientId));
        assertThat(patchMessage.edits().size(), is(1));
        assertThat(patchMessage.edits(), is(notNullValue()));
        assertThat(patchMessage.edits().peek().diff().jsonPatch().toString(), equalTo(patch.toString()));
    }

    @Test
    public void jsonPatchEditToJson() {
        final String json = JsonMapper.toJson(newJsonPatchEdit());
        final JsonNode edit = JsonMapper.asJsonNode(json);
        assertThat(edit.get("serverVersion").asText(), equalTo("0"));
        assertThat(edit.get("clientVersion").asText(), equalTo("0"));
        final JsonNode diffs = edit.get("diffs");
        assertThat(diffs.isArray(), is(true));
        final JsonNode patch = diffs.get(0);
        assertThat(patch.get("op").asText(), equalTo("replace"));
        assertThat(patch.get("path").asText(), equalTo("/name"));
        assertThat(patch.get("value").asText(), equalTo("Fletch"));
    }

    @Test
    public void jsonPatchEditFromJson() {
        final JsonPatch patch = jsonPatch();
        final String json = JsonMapper.toJson(newJsonPatchEdit(patch));
        final JsonPatchEdit edit = JsonMapper.fromJson(json, JsonPatchEdit.class);
        assertThat(edit.diff(), is(notNullValue()));
        assertThat(edit.diff().jsonPatch().toString(), equalTo(patch.toString()));
    }

}
