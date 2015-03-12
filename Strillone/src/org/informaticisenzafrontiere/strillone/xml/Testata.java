package org.informaticisenzafrontiere.strillone.xml;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="testata")
public class Testata {
	
	@Element(name="lingua")
	private String lingua;

	@Element(name="nome")
	private String nome;
	
	@Element(name="edizione")
	private Date edizione;
	
	@Element(name="url", required=false)
	private String url;
	
	@Element(name="resource", required=false)
	private String resource;
	
	@Element(name="beta", required=false)
	private boolean beta; 
	
	public Testata() { }

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