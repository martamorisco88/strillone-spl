package org.informaticisenzafrontiere.strillone.xml;


import java.util.Date;
import java.util.List;
import org.simpleframework.xml.Element;


public class GiornaleBookmark  extends Giornale{

	
	@Element(name="resource", required=false)
	private String resource;
	
	public GiornaleBookmark(){}
	
	public GiornaleBookmark(String id,String testata, String edizione, String lingua, List<Sezione> sezioni, String resource){
		super(id,testata,edizione,lingua,sezioni);	
		setResource(resource);
	}
	
	public GiornaleBookmark(String id,String testata, Date edizione, String lingua, List<Sezione> sezioni, String resource){
		super(id,testata,edizione,lingua,sezioni);
		setResource(resource);
	}
	
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
}
