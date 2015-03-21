package org.informaticisenzafrontiere.strillone.xml;

import java.util.Calendar;
import java.util.Date;

import org.simpleframework.xml.Element;

public class ArticoloBookmark extends Articolo{
	
	
	@Element(name="data", data=true)
	private Calendar data;

	public ArticoloBookmark() { }
	
	
	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data=data;
	}


}
