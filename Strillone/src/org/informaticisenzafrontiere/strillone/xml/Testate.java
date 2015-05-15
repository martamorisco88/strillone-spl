package org.informaticisenzafrontiere.strillone.xml;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="testate")
public class Testate extends XMLMessage {

	@ElementList(name="testata",inline=true)
	private List<Testata> testate=new ArrayList<Testata>();
	
	public Testate() { }

	public List<Testata> getTestate() {
		return testate;
	}

	public void setTestate(List<Testata> testate) {
		this.testate = testate;
	}
	
}
