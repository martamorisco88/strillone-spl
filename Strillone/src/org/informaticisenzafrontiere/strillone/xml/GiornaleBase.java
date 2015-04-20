package org.informaticisenzafrontiere.strillone.xml;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="giornale")
public class GiornaleBase extends XMLMessage {

	@Element(name="testata")
	private String testata;
	
	
	@Element(name="id")
	private String id;

	@ElementList(name="sezione",required=false,type=Sezione.class, inline=true)
	private List<Sezione> sezioni= new ArrayList<Sezione>();
	
	
	public GiornaleBase() { }

	public String getTestata() {
		return testata;
	}

	public void setTestata(String testata) {
		this.testata = testata;
	}

		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public List<Sezione> getSezioni() {
		return sezioni;
	}

	public void setSezioni(List<Sezione> sezioni) {
		this.sezioni = sezioni;
	}

}
