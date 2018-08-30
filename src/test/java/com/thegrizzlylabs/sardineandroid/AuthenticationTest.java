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

package com.thegrizzlylabs.sardineandroid;

import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;
import com.thegrizzlylabs.sardineandroid.impl.SardineException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class AuthenticationTest {
    @Test
    public void testBasicAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials("jenkins", "jenkins");
        try {
            URI url = URI.create("http://test.cyberduck.ch/dav/basic/");
            final List<DavResource> resources = sardine.list(url.toString());
            assertNotNull(resources);
            assertFalse(resources.isEmpty());
        } catch (SardineException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testPreemptiveBasicAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials("jenkins", "jenkins", true);
        try {
            URI url = URI.create("http://test.cyberduck.ch/dav/basic/");
            final List<DavResource> resources = sardine.list(url.toString());
            assertNotNull(resources);
            assertFalse(resources.isEmpty());
        } catch (SardineException e) {
            fail(e.getMessage());
        }
    }
}
