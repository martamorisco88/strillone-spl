package org.informaticisenzafrontiere.strillone.xml;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="sezione")
public class Sezione {
	
	@Element(name="nome", required=false)
	private String nome;
	
	@Element(name="id", required=false)
	private String id;
	
	@ElementList(name="articolo", type=Articolo.class, inline=true, required=false)
	private List<Articolo> articoli=new ArrayList<Articolo>();
	
	public Sezione() { }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Articolo> getArticoli() {
		return articoli;
	}

	public void setArticoli(List<Articolo> articoli) {
		this.articoli = articoli;
	}
	

}
