package com.thegrizzlylabs.sardineandroid.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(strict = false)
@Namespace(prefix = "D", reference = "DAV:")
public class Privilege {

    @ElementList(required = false)
	private List<Object> content;

	public List<Object> getContent() {
		if (content==null)
			content = new ArrayList<Object>();
		return content;
	}

	public void setContent(List<Object> content) {
		this.content = content;
	}

}
