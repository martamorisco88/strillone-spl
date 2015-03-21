package org.informaticisenzafrontiere.strillone.xml;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="giornale")
public class Giornale extends XMLMessage {
	
	@Element(name="lingua")
	private String lingua;
	
	@Element(name="testata")
	private String testata;
	
	@Element(name="edizione")
	private String edizione;
	
	@Element(name="id", required=false)
	private String id;
	
	@ElementList(name="sezione", type=Sezione.class, inline=true)
	private List<Sezione> sezioni;
	
	public Giornale() { }

	public String getLingua() {
		return lingua;
	}

	public void setLingua(String lingua) {
		this.lingua = lingua;
	}

	public String getTestata() {
		return testata;
	}

	public void setTestata(String testata) {
		this.testata = testata;
	}

	public String getEdizione() {
		return edizione;
	}

	public void setEdizione(String edizione) {
		this.edizione = edizione;
	}

	public List<Sezione> getSezioni() {
		return sezioni;
	}

	public void setSezioni(List<Sezione> sezioni) {
		this.sezioni = sezioni;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
