package org.informaticisenzafrontiere.strillone.xml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

@Root(name="giornale")
@Order(elements={"testata","edizione","lingua","sezione"})
public class Giornale extends XMLMessage {
	
	@Element(name="lingua",required=false)
	private String lingua;
	
	@Element(name="testata")
	private String testata;
	
	@Element(name="edizione", required=false)
	private String edizione;
	
	@ElementList(name="sezione",required=false, inline=true)
	private List<Sezione> sezioni= new ArrayList<Sezione>();
	
	@Attribute(name="id")
	private String id;
	
	public Giornale() { }
	
	public Giornale(String id,String testata, String edizione, String lingua, List<Sezione> sezioni) { 
		setId(id);
		setTestata(testata);
		setEdizione(edizione);
		setLingua(lingua);
		setSezioni(sezioni);
	}
	
	public Giornale(String id,String testata, Date edizione, String lingua, List<Sezione> sezioni) { 
		setId(id);
		setTestata(testata);
		setLingua(lingua);
		setSezioni(sezioni);
		String edizioneString= new SimpleDateFormat("yyyy-MM-dd").format(edizione);
		setEdizione(edizioneString);
	}

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