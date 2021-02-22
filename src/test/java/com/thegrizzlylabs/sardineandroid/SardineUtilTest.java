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

import com.thegrizzlylabs.sardineandroid.model.Allprop;
import com.thegrizzlylabs.sardineandroid.model.Prop;
import com.thegrizzlylabs.sardineandroid.model.Propfind;
import com.thegrizzlylabs.sardineandroid.util.SardineUtil;

import org.junit.Test;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 */
public class SardineUtilTest {
    @Test
    public void testParseDate() {
        assertNotNull(SardineUtil.parseDate("2007-07-16T13:35:49Z"));
        assertNotNull(SardineUtil.parseDate("Mon, 16 Jul 2007 13:35:49 GMT"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(SardineUtil.parseDate("2007-07-16T13:35:49.324Z"));
        assertEquals(324, calendar.get(Calendar.MILLISECOND));
    }

    @Test
    public void testAllpropSerialization() {
        Propfind body = new Propfind();
        body.setAllprop(new Allprop());
        String xml = SardineUtil.toXml(body);
        assertThat(xml, containsString("propfind>"));
        assertThat(xml, containsString("allprop/>"));
    }

    @Test
    public void testXmlVersionDeclaration() {
        String xml = SardineUtil.toXml(new Propfind());
        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
    }

    @Test
    public void testPropSerialization() {
        Prop prop = new Prop();
        List<Element> any = prop.getAny();
        Element element1 = SardineUtil.createElement(SardineUtil.createQNameWithCustomNamespace("hello"));
        element1.setTextContent("bla");
        any.add(element1);

        Element element2 = SardineUtil.createElement(SardineUtil.createQNameWithDefaultNamespace("hello"));
        element2.setTextContent("bla");
        any.add(element2);

        prop.setDisplayname("bli");

        String xml = SardineUtil.toXml(prop);
        String expectedXML =
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<D:prop xmlns:D=\"DAV:\">\n" +
                "   <s:hello xmlns:s=\"SAR:\">bla</s:hello>\n" +
                "   <D:hello>bla</D:hello>\n" +
                "   <D:displayname>bli</D:displayname>\n" +
                "</D:prop>";

        assertEquals(expectedXML, xml);
    }

    @Test
    public void testPropDeserialization() throws Exception {
        String xml =
                "<D:prop xmlns:D=\"DAV:\">\n" +
                "   <D:resourcetype/>\n" +
                "   <D:creationdate>2016-07-07T07:15:17Z</D:creationdate>\n" +
                "   <D:getcontentlength>31</D:getcontentlength>\n" +
                "   <D:getlastmodified>Thu, 07 Jul 2016 07:15:17 GMT</D:getlastmodified>\n" +
                "   <D:getetag>\"f45e6a-1f-5370672b15b20\"</D:getetag>\n" +
                "   <D:executable>F</D:executable>\n" +
                "   <D:supportedlock>\n" +
                "       <D:lockentry>\n" +
                "           <D:lockscope><D:exclusive/></D:lockscope>\n" +
                "           <D:locktype><D:write/></D:locktype>\n" +
                "       </D:lockentry>\n" +
                "       <D:lockentry>\n" +
                "           <D:lockscope><D:shared/></D:lockscope>\n" +
                "           <D:locktype><D:write/></D:locktype>\n" +
                "       </D:lockentry>\n" +
                "   </D:supportedlock>\n" +
                "   <D:lockdiscovery/>\n" +
                "   <D:getcontenttype>text/plain</D:getcontenttype>\n" +
                "</D:prop>";

        Prop prop = SardineUtil.unmarshal(Prop.class, new ByteArrayInputStream(xml.getBytes()));
        assertEquals("2016-07-07T07:15:17Z", prop.getCreationdate());
        assertEquals("31", prop.getGetcontentlength());
        assertEquals("Thu, 07 Jul 2016 07:15:17 GMT", prop.getGetlastmodified());
        assertEquals("\"f45e6a-1f-5370672b15b20\"", prop.getGetetag());
        assertEquals("text/plain", prop.getGetcontenttype());
        assertEquals(2, prop.getSupportedlock().getLockentryList().size());
    }

    @Test
    public void testEmptySupportedLock() throws Exception {
        String xml =
                "<D:prop xmlns:D=\"DAV:\">\n" +
                        "   <D:resourcetype/>\n" +
                        "   <D:creationdate>2016-07-07T07:15:17Z</D:creationdate>\n" +
                        "   <D:getcontentlength>31</D:getcontentlength>\n" +
                        "   <D:getlastmodified>Thu, 07 Jul 2016 07:15:17 GMT</D:getlastmodified>\n" +
                        "   <D:getetag>\"f45e6a-1f-5370672b15b20\"</D:getetag>\n" +
                        "   <D:executable>F</D:executable>\n" +
                        "   <D:supportedlock/>\n" +
                        "   <D:lockdiscovery/>\n" +
                        "   <D:getcontenttype>text/plain</D:getcontenttype>\n" +
                        "</D:prop>";

        Prop prop = SardineUtil.unmarshal(Prop.class, new ByteArrayInputStream(xml.getBytes()));
        assertTrue(prop.getSupportedlock().getLockentryList().isEmpty());
    }
}
