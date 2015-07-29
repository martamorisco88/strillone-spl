package org.informaticisenzafrontiere.strillone.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import org.informaticisenzafrontiere.strillone.MainPresenter;
import org.informaticisenzafrontiere.strillone.R;
import org.informaticisenzafrontiere.strillone.util.App;
import org.informaticisenzafrontiere.strillone.util.Configuration;
import org.informaticisenzafrontiere.strillone.xml.Articolo;
import org.informaticisenzafrontiere.strillone.xml.Giornale;
import org.informaticisenzafrontiere.strillone.xml.GiornaleXMLHandler;
import org.informaticisenzafrontiere.strillone.xml.Sezione;
import org.informaticisenzafrontiere.strillone.xml.XMLHandler;

public class GiornaleRequestHandler extends RequestHandler {
	
	private final static String TAG = GiornaleRequestHandler.class.getSimpleName();

	private MainPresenter mainPresenter;
	private String url;
	private String filename;
	
	public GiornaleRequestHandler(MainPresenter mainPresenter, String url, String filename) {
		this.mainPresenter = mainPresenter;
		//this.url = url;
		//this.url = "http://www.walks.to/strillonews/newspapers/"+url;
		//this.url = Configuration.URL +  "/newspapers/"+url; //marta
    
		this.url ="http://192.168.1.46/strillonews/index.php/newspapers/"+url;
		
		this.filename = filename;
	}
	
	@Override
	protected String getURL() {
		return this.url;
	}

	@Override
	protected Map<String, String> getParameters() {
		return new HashMap<String, String>();
	}
	
	@Override
	public void onResponseReceived(String response) {
		if ("".equals(response)) {
			this.mainPresenter.notifyCommunicationError(App.getInstance().getString(R.string.connecting_error));
		} else {
			try {
				XMLHandler xmlHandler = new GiornaleXMLHandler();
				Giornale giornale = (Giornale)xmlHandler.deserialize(response, true);

				List<Sezione> sezioni = giornale.getSezioni();
				List<Sezione> newSezioni = new ArrayList<Sezione>();
				
				int c = 0;
				for (Sezione sezione : sezioni) {
					List<Articolo> articoli = sezione.getArticoli();
					if (articoli != null && articoli.size() > 0) {
						String nome = sezione.getNome();
						if (nome == null || nome.trim().length() == 0) {
							sezione.setNome(String.valueOf(c + 1));
						}
						
						newSezioni.add(sezione);
					}
					
					c++;
				}
				giornale.setSezioni(newSezioni);
				
				this.mainPresenter.notifyGiornaleReceived(this.filename, giornale);
			} catch (Exception e) {
				if (Configuration.DEBUGGABLE) Log.d(TAG, "Eccezione.", e);
				this.mainPresenter.notifyCommunicationError(App.getInstance().getString(R.string.connecting_error));
			}
		}
	}

}