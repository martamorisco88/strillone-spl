package org.informaticisenzafrontiere.strillone.xml;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public abstract class XMLHandler {
	
	public XMLMessage deserialize(String xml, boolean strict) throws Exception {
		Serializer serializer = new Persister();
		return serializer.read(getXMLMessageClass(), xml, strict);
	}
	
	protected abstract Class<? extends XMLMessage> getXMLMessageClass();
	
}
