package org.informaticisenzafrontiere.strillone.xml;

import java.util.Calendar;
import java.util.Date;

import org.simpleframework.xml.Element;

public class ArticoloBookmark extends Articolo{
	
	
	@Element(name="data", data=true)
	private Date data;

	public ArticoloBookmark() { }
	
	public ArticoloBookmark(String titolo, String testo, Date data) {
	
	 super(titolo,testo);
	 setData(data);
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data=data;
	}


}
