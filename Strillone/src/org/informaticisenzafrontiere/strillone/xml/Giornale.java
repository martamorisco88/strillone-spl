package org.informaticisenzafrontiere.strillone.xml;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="giornale")
public class Giornale extends GiornaleBase {
	
	
	@Element(name="lingua")
	private String lingua;

	@Element(name="edizione")
	private String edizione;
	

	public Giornale() { }
	
	
	public String getLingua() {
		return lingua;
	}

	public void setLingua(String lingua) {
		this.lingua = lingua;
	}

	public String getEdizione() {
		return edizione;
	}

	public void setEdizione(String edizione) {
		this.edizione = edizione;
	}

	
}
