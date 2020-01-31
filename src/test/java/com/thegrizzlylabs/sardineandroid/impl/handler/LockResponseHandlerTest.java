/*
 * Copyright 2009-2011 Jon Stevens et al.
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

package com.thegrizzlylabs.sardineandroid.impl.handler;

import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 */
public class LockResponseHandlerTest {

    @Test
    public void testBoxResponse() throws Exception {
        LockResponseHandler handler = new LockResponseHandler();
        final String response = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<d:prop xmlns:d=\"DAV:\">\n" +
                "  <d:lockdiscovery>\n" +
                "    <d:activelock>\n" +
                "      <d:lockscope>\n" +
                "        <d:exclusive/>\n" +
                "      </d:lockscope>\n" +
                "      <d:locktype>\n" +
                "        <d:write/>\n" +
                "      </d:locktype>\n" +
                "      <d:lockroot>\n" +
                "        <d:href>fake-lock-root</d:href>\n" +
                "      </d:lockroot>\n" +
                "      <d:depth>infinity</d:depth>\n" +
                "      <d:timeout>Second-</d:timeout>\n" +
                "      <d:locktoken>\n" +
                "        <d:href>fake-lock-token</d:href>\n" +
                "      </d:locktoken>\n" +
                "      <d:owner></d:owner>\n" +
                "    </d:activelock>\n" +
                "  </d:lockdiscovery>\n" +
                "</d:prop>";
        final String token = handler.getToken(new ByteArrayInputStream(response.getBytes()));
        assertEquals("fake-lock-token", token);
    }
}
