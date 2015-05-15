package org.informaticisenzafrontiere.strillone.xml;

import java.util.Calendar;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name="articolo")
@Order(elements={"titolo","testo"})
public class Articolo {
	
	@Element(name="titolo", data=true)
	private String titolo;
	
	@Element(name="testo", data=true)
	private String testo;

	public Articolo() { }
	
	public Articolo(String titolo, String testo ) {
		
		setTitolo(titolo);
		setTesto(testo);
		
	} // marta

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
	
}





	

