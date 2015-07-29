package org.informaticisenzafrontiere.strillone;

import org.informaticisenzafrontiere.strillone.xml.Giornale;
import org.informaticisenzafrontiere.strillone.xml.Testate;

public interface IMainActivity {
	
	public void notifyHeadersReceived(Testate testate);
	
	public String getURLTestata();
	
	public void notifyGiornaleReceived(Giornale giornale);
	
	public void notifyCommunicationError(String message);
	
	public void notifyErrorDowloadingHeaders(String message);

	public String getResourceTestata();//Morisco
}
