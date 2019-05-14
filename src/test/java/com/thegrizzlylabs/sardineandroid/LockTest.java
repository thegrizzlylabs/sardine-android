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

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class LockTest {
    @Test
    public void testLockUnlock() throws Exception {
        Sardine sardine = new OkHttpSardine();
        String url = String.format("http://test.cyberduck.ch/dav/anon/sardine/%s", UUID.randomUUID().toString());
        sardine.put(url, new byte[0]);
        try {
            String token = sardine.lock(url);
            try {
                sardine.delete(url);
                fail("Expected delete to fail on locked resource");
            } catch (SardineException e) {
                if (e.getStatusCode() != 423) {
                    throw e;
                }
            }
            sardine.unlock(url, token);
        } finally {
            sardine.delete(url);
        }
    }

    @Test
    public void testLockFailureNotImplemented() throws Exception {
        Sardine sardine = new OkHttpSardine();
        String url = "https://www.w3.org/Amaya/User/doc/WebDAV.html";
        try {
            sardine.lock(url);
            fail("Expected lock to fail");
        } catch (SardineException e) {
            if (e.getStatusCode() != 405) {
                throw e;
            }
        }
    }

    @Test
    public void lockRefreshUnlock() throws Exception {
        Sardine sardine = new OkHttpSardine();

        // Touch new file
        final UUID file = UUID.randomUUID();
        final String url = String.format("http://test.cyberduck.ch/dav/anon/sardine/%s", file);
        sardine.put(url, new byte[0]);
        try {
            String lockToken = sardine.lock(url);
            String result = sardine.refreshLock(url, lockToken, url);

            assertTrue(lockToken.startsWith("opaquelocktoken:"));
            assertTrue(lockToken.equals(result));

            sardine.unlock(url, lockToken);
        } finally {
            sardine.delete(url);
        }
    }
}
