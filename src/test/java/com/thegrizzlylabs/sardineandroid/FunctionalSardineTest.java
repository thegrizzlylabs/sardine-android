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
import com.thegrizzlylabs.sardineandroid.util.SardineUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.namespace.QName;

import okhttp3.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class FunctionalSardineTest {

    public static final String WEBDAV_URL = "http://demo.owncloud.org/remote.php/webdav";
    public static final String USERNAME = "test";
    public static final String PASSWORD = "test";

    private final Sardine sardine = new OkHttpSardine();
    private final String testFolder = "test" + UUID.randomUUID().toString();

    @Before
    public void setUp() throws IOException {
        sardine.setCredentials(USERNAME, PASSWORD);
        sardine.createDirectory(WEBDAV_URL + "/" + testFolder);
    }

    @After
    public void tearDown() throws IOException {
        sardine.delete(WEBDAV_URL + "/" + testFolder);
    }

    @Test
    public void testRead() throws Exception {
        byte[] content = "Test".getBytes();
        String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, content);

        final InputStream in = sardine.get(url);
        assertNotNull(in);
        in.close();
    }

    @Test
    public void testReadEmptyFile() throws Exception {
        String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, new byte[0]);

        final InputStream in = sardine.get(url);
        assertNotNull(in);
        assertEquals(-1, in.read());
        in.close();
    }

    @Test
    public void testGetFileNotFound() throws Exception {
        InputStream in = null;
        try {
            in = sardine.get(WEBDAV_URL + "/" + UUID.randomUUID().toString());
            fail("Expected 404");
        } catch (SardineException e) {
            assertEquals(404, e.getStatusCode());
        }
        assertNull(in);
    }

    @Test
    public void testGetTimestamps() throws Exception {
        byte[] content = "bla".getBytes(StandardCharsets.UTF_8);
        String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, content);

        final List<DavResource> resources = sardine.list(url);
        DavResource resource = resources.get(0);
        assertNotNull(resource.getModified());
    }

    @Test
    public void testGetLength() throws Exception {
        String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, "Test".getBytes());

        final List<DavResource> resources = sardine.list(url);
        assertEquals(1, resources.size());
        Long contentLength = resources.get(0).getContentLength();
        assertNotNull(contentLength);
        assertNotEquals(DavResource.DEFAULT_CONTENT_LENGTH, contentLength.longValue());
    }

    @Test
    public void testDavOwner() throws IOException {
        DavAcl acl = sardine.getAcl(WEBDAV_URL + "/" + testFolder);
        assertNull(acl.getOwner());
        assertNull(acl.getGroup());
    }

    @Test
    public void testDavPrincipals() throws IOException {
        List<String> principals = sardine.getPrincipalCollectionSet(WEBDAV_URL + "/" + testFolder);
        assertNotNull(principals);
        for (String p : principals) {
            assertNotNull(p);
        }
    }

    @Test
    public void testPutExpectContinue() throws Exception {
        // Anonymous PUT to restricted resource
        Sardine sardine = new OkHttpSardine();
        String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        try {
            sardine.put(url, "Test".getBytes());
            fail("Expected authorization failure");
        } catch (SardineException e) {
            // Expect Authorization Required
            assertEquals(401, e.getStatusCode());
        }
    }

    @Test
    public void testPath() throws Exception {
        List<DavResource> resources = sardine.list(WEBDAV_URL + "/" + testFolder);
        assertFalse(resources.isEmpty());
        DavResource folder = resources.get(0);
        assertEquals(testFolder, folder.getName());
        assertEquals("/remote.php/webdav/" + testFolder + "/", folder.getPath());
        assertEquals(new Long(-1), folder.getContentLength());
    }

    @Test
    public void testPut() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();

        sardine.put(url, "Test".getBytes());


        assertTrue(sardine.exists(url));
        assertEquals("Test", new BufferedReader(new InputStreamReader(sardine.get(url), "UTF-8")).readLine());
    }

    @Test
    public void testPutInputStream() throws Exception {
        Sardine sardine = new OkHttpSardine();
        final String url = String.format("http://test.cyberduck.ch/dav/anon/sardine/%s", UUID.randomUUID().toString());
        sardine.put(url, new InputStreamProvider() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("Test".getBytes());
            }

            @Override
            public MediaType getContentType() {
                return MediaType.parse("text/plain");
            }
        });
        try {
            assertTrue(sardine.exists(url));
            assertEquals("Test", new BufferedReader(new InputStreamReader(sardine.get(url), "UTF-8")).readLine());
        } finally {
            sardine.delete(url);
        }
    }

    @Test
    public void testDepth() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder;
        List<DavResource> resources = sardine.list(url, 0);

        assertNotNull(resources);
        assertEquals(1, resources.size());
    }

    @Test
    public void testDelete() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, "Test".getBytes());

        sardine.delete(url);

        assertFalse(sardine.exists(url));
    }

    @Test
    public void testMove() throws Exception {
        final String source = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        final String destination = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(source, "Test".getBytes());

        sardine.move(source, destination);

        assertFalse(sardine.exists(source));
        assertTrue(sardine.exists(destination));
    }

    @Test
    public void testMoveToFolderIncludeBlank() throws Exception {
        final String source = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        final String destination = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString() + " blank test";
        sardine.put(source, "Test".getBytes());

        sardine.move(source, destination);

        assertFalse(sardine.exists(source));
        assertTrue(sardine.exists(destination));
    }

    @Test
    public void testMoveOverwriting() throws Exception {
        final String source = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        final String destination = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(source, "Source".getBytes());
        sardine.put(destination, "Destination".getBytes());

        sardine.move(source, destination, true);

        assertFalse(sardine.exists(source));
        assertEquals("Source", new BufferedReader(new InputStreamReader(sardine.get(destination), "UTF-8")).readLine());
    }

    @Test
    public void testMoveFailOnExisting() throws Exception {
        final String source = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        final String destination = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(source, "Source".getBytes());
        sardine.put(destination, "Destination".getBytes());

        try {
            sardine.move(source, destination, false);
            fail("Expected SardineException");
        } catch (SardineException e) {
            assertEquals(412, e.getStatusCode());
        }

        assertTrue(sardine.exists(source));
        assertEquals("Destination", new BufferedReader(new InputStreamReader(sardine.get(destination), "UTF-8")).readLine());
    }

    @Test
    public void testCreateDirectory() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();

        sardine.createDirectory(url);
        assertTrue(sardine.exists(url));
        final List<DavResource> resources = sardine.list(url);
        assertNotNull(resources);
        assertEquals(1, resources.size());
    }

    @Test
    public void testExists() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, "Test".getBytes());

        assertTrue(sardine.exists(WEBDAV_URL));
        assertTrue(sardine.exists(url));
        assertFalse(sardine.exists(WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString()));
    }

    @Test
    public void testDirectoryContentType() throws Exception {
        final List<DavResource> resources = sardine.list(WEBDAV_URL + "/" + testFolder);

        assertNotNull(resources);
        assertFalse(resources.isEmpty());
        DavResource file = resources.get(0);
        assertEquals(DavResource.HTTPD_UNIX_DIRECTORY_CONTENT_TYPE, file.getContentType());
    }

    @Test
    public void testFileContentType() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, "Test".getBytes());

        final List<DavResource> resources = sardine.list(url);
        assertFalse(resources.isEmpty());
        assertEquals(1, resources.size());
        DavResource file = resources.get(0);
        assertEquals("application/octet-stream", file.getContentType());
    }

    @Ignore("it seems like owncloud doesn't support custom properties")
    @Test
    public void testMetadata() throws Exception {
        final String url = WEBDAV_URL + "/" + testFolder + "/" + UUID.randomUUID().toString();
        sardine.put(url, "Hello".getBytes("UTF-8"), "text/plain");

        // Setup some custom properties, with custom namespaces
        Map<QName, String> newProps = new HashMap<>();
        newProps.put(
                new QName("http://my.namespace.com", "mykey", "ns1"),
                "myvalue");
        newProps.put(
                new QName(SardineUtil.CUSTOM_NAMESPACE_URI, "mykey", SardineUtil.CUSTOM_NAMESPACE_PREFIX),
                "my&value2");
        newProps.put(
                new QName("hello", "mykey", "ns2"),
                "my<value3");
        sardine.patch(url, newProps);

        // Check properties are properly re-read
        List<DavResource> resources = sardine.list(url);
        assertEquals(resources.size(), 1);
        assertEquals(resources.get(0).getContentLength(), (Long) 5L);
        Map<QName, String> props = resources.get(0).getCustomPropsNS();

        for (Map.Entry<QName, String> entry : newProps.entrySet()) {
            assertEquals(entry.getValue(), props.get(entry.getKey()));
        }

        // Delete some of those added properties
        List<QName> removeProps = new ArrayList<>();
        removeProps.add(new QName("http://my.namespace.com", "mykey", "ns1"));
        sardine.patch(url, Collections.<QName, String>emptyMap(), removeProps);

        props = sardine.list(url).get(0).getCustomPropsNS();
        assertNull(props.get(new QName("http://my.namespace.com", "mykey")));
        assertEquals(props.get(new QName(SardineUtil.CUSTOM_NAMESPACE_URI, "mykey")), "my&value2");
        assertEquals(props.get(new QName("hello", "mykey")), "my<value3");
    }
}