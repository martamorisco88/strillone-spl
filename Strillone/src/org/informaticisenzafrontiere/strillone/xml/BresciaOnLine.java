package org.informaticisenzafrontiere.strillone.xml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="brescia_on_line")
public class BresciaOnLine {
	
	@Element(name="giornale")
	private Giornale giornale;
	
	public BresciaOnLine() { }

	public Giornale getGiornale() {
		return giornale;
	}

	public void setGiornale(Giornale giornale) {
		this.giornale = giornale;
	}

}
