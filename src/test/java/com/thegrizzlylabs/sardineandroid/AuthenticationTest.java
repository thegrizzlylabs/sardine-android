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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@Category(IntegrationTest.class)
public class AuthenticationTest {
    @Test
    public void testBasicAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(FunctionalSardineTest.USERNAME, FunctionalSardineTest.PASSWORD);
        final List<DavResource> resources = sardine.list(FunctionalSardineTest.WEBDAV_URL);
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }

    @Ignore("ownCloud doesn't support preemptive auth?")
    @Test
    public void testPreemptiveBasicAuth() throws Exception {
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(FunctionalSardineTest.USERNAME, FunctionalSardineTest.PASSWORD, true);
        final List<DavResource> resources = sardine.list(FunctionalSardineTest.WEBDAV_URL);
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }
}
