package org.informaticisenzafrontiere.strillone.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.simpleframework.xml.Element;


public class ArticoloBookmark extends Articolo{
	
	
	@Element(name="edizione", data=true)
	private Date edizione;

	public ArticoloBookmark() { }
	
	public ArticoloBookmark(String titolo, String testo, Date edizione) {
	
	 super(titolo,testo);
	 setEdizione(edizione);
	}
	
	public ArticoloBookmark(String titolo, String testo, String edizione) {
		
		 super(titolo,testo);
		 Date edizioneDate = null;
		 try {
			edizioneDate = new SimpleDateFormat("yyyy-MM-dd").parse(edizione);
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 setEdizione(edizioneDate);
		 }
	

	public Date getData() {
		return edizione;
	}

	public void setEdizione(Date edizione) {
		this.edizione=edizione;
	}


}
