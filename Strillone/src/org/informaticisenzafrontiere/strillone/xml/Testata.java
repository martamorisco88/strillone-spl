package org.informaticisenzafrontiere.strillone.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="testata")
public class Testata {
	
	@Element(name="lingua")
	private String lingua;

	@Element(name="nome")
	private String nome;
	
	@Element(name="edizione", required=false)
	private Date edizione;
	
	@Element(name="url", required=false)
	private String url;
	
	@Element(name="resource", required=false)
	private String resource;
	
	@Attribute(name="id", required=false)
	private String id;
	
	@Element(name="beta", required=false)
	private boolean beta; 
	
	public Testata() { }
	
	public Testata(String id, String nome, String lingua, String url, String resource, Date edizione, boolean beta) {
		
		setId(id);
		setNome(nome);
		setLingua(lingua);
		setUrl(url);
		setResource(resource);
		setEdizione(edizione);
		setBeta(beta);
	}
	
     public Testata(String id, String nome, String lingua, String url, String resource, String edizione, boolean beta) {
	
			setId(id);
			setNome(nome);
			setLingua(lingua);
			setUrl(url);
			setResource(resource);
			Date edizioneDate = null;
			try {
				edizioneDate = new SimpleDateFormat("yyyy-MM-dd").parse(edizione);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
			}
			setEdizione(edizioneDate);
			setBeta(beta);
	}
	
		public String getLingua() {
			return lingua;
		}

	public void setLingua(String lingua) {
		this.lingua = lingua;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getEdizione() {
		return edizione;
	}

	public void setEdizione(Date edizione) {
		this.edizione = edizione;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isBeta() {
		return beta;
	}

	public void setBeta(boolean beta) {
		this.beta = beta;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[nome: ");
		sb.append(this.nome);
		sb.append(", data: ");
		sb.append(new SimpleDateFormat("dd/MM/yyyy").format(this.edizione));
		sb.append(", url: ");
		sb.append(this.url);
		sb.append("]");
		
		return sb.toString();
	}
	
}