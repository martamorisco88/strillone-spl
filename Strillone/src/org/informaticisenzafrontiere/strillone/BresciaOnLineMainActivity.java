package org.informaticisenzafrontiere.strillone;

import java.io.IOException;
import java.io.InputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import org.informaticisenzafrontiere.strillone.util.Configuration;
import org.informaticisenzafrontiere.strillone.xml.Articolo;
import org.informaticisenzafrontiere.strillone.xml.BresciaOnLine;
import org.informaticisenzafrontiere.strillone.xml.Giornale;
import org.informaticisenzafrontiere.strillone.xml.Sezione;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BresciaOnLineMainActivity extends Activity implements OnInitListener {
	
	private final static String TAG = BresciaOnLineMainActivity.class.getSimpleName();
	
	private TextToSpeech textToSpeech;
	private Button upperLeftButton;
	private Button upperRightButton;
	private Button lowerLeftButton;
	private Button lowerRightButton;
	
	private BresciaOnLine bresciaOnLine;
	private Giornale giornale;
	private int iSezione;
	private int iArticolo;
	private int maxSezioni;
	private boolean inSezione;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.textToSpeech = new TextToSpeech(this, this);
        this.upperLeftButton = getUpperLeftButton();
        this.upperRightButton = getUpperRightButton();
        this.lowerLeftButton = getLowerLeftButton();
        this.lowerRightButton = getLowerRightButton();
        
        this.upperLeftButton.setOnLongClickListener(new View.OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				BresciaOnLineMainActivity.this.textToSpeech.stop();
				BresciaOnLineMainActivity.this.textToSpeech.speak(getResources().getString(R.string.nav_closing_app), TextToSpeech.QUEUE_FLUSH, null);
				
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
				
				return true;
			}
		});
        
        initializeNews();
    }
    
    

    @Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus) {
			LinearLayout containerLinearLayout = (LinearLayout)findViewById(R.id.containerLinearLayout);
			int height = containerLinearLayout.getHeight();
			
	        LinearLayout firstRowButtonsLinearLayout = (LinearLayout)findViewById(R.id.firstRowButtonsLinearLayout);
	        LinearLayout secondRowButtonsLinearLayout = (LinearLayout)findViewById(R.id.secondRowButtonsLinearLayout);
	        
	        LinearLayout.LayoutParams firstRowButtonsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / 2);
	        LinearLayout.LayoutParams secondRowButtonsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / 2);
	        
	        firstRowButtonsLinearLayout.setLayoutParams(firstRowButtonsLayoutParams);
	        secondRowButtonsLinearLayout.setLayoutParams(secondRowButtonsLayoutParams);
		}
	}

    public void performUpperLeftAction(View v) {   	
    	if (this.textToSpeech.isSpeaking()) {
    		this.textToSpeech.stop();
    	} else {
        	this.iSezione = -1;
        	this.iArticolo = -1;
        	this.inSezione = false;
    		
    		this.textToSpeech.speak(getResources().getString(R.string.nav_home), TextToSpeech.QUEUE_FLUSH, null);
    	}
    }
    
    public void performLowerLeftAction(View v) {
    	if (inSezione) {
    		if (this.iArticolo >= 0) {
	    		// Leggi l'articolo.
	    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
	    		Articolo articolo = sezione.getArticoli().get(iArticolo);
	    		
	    		this.textToSpeech.speak(articolo.getTesto(), TextToSpeech.QUEUE_FLUSH, null);
    		}
    	} else {
    		if (this.iSezione >= 0) {
	    		// Entra nella sezione.
	    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
	    		
	    		StringBuilder sbMessaggio = new StringBuilder();
	    		sbMessaggio.append(getResources().getString(R.string.nav_enter_section));
	    		sbMessaggio.append(sezione.getNome());
	    		this.inSezione = true;
	    		
	    		this.textToSpeech.speak(sbMessaggio.toString(), TextToSpeech.QUEUE_FLUSH, null);
    		}
    	}
    }
    
    public void performUpperRightAction(View v) {
    	if (inSezione) {
    		if (this.iArticolo > 0) {
    			this.iArticolo--;
    		}
    		
    		if (this.iArticolo >= 0) {
	    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
	    		Articolo articolo = sezione.getArticoli().get(iArticolo);
	    		this.textToSpeech.speak(articolo.getTitolo(), TextToSpeech.QUEUE_FLUSH, null);
    		}
    	} else {
    		// Scorri la sezione.
    		if (this.iSezione > 0) {
    			this.iSezione--;
    		}
    		
    		if (this.iSezione >= 0) {
    			Sezione sezione = this.giornale.getSezioni().get(iSezione);
    			this.textToSpeech.speak(sezione.getNome(), TextToSpeech.QUEUE_FLUSH, null);
    		}
    	}
    }
    
    public void performLowerRightAction(View v) {
    	if (inSezione) {
    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
    		int maxArticoli = sezione.getArticoli().size();
    		
    		if (this.iArticolo < maxArticoli - 1) {
    			this.iArticolo++;
    		}
    		
    		Articolo articolo = sezione.getArticoli().get(iArticolo);
    		this.textToSpeech.speak(articolo.getTitolo(), TextToSpeech.QUEUE_FLUSH, null);
    	} else {
    		if (this.iSezione < this.maxSezioni - 1) {
    			this.iSezione++;
    		}
    		
    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
    		this.textToSpeech.speak(sezione.getNome(), TextToSpeech.QUEUE_FLUSH, null);
    	}
    }

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Abilita i pulsanti al touch.
			this.upperLeftButton.setClickable(true);
	        this.upperRightButton.setClickable(true);
	        this.lowerLeftButton.setClickable(true);
	        this.lowerRightButton.setClickable(true);
	        
	        Toast.makeText(this, R.string.init_ok, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.init_ko, Toast.LENGTH_SHORT).show();
		}
	}
	
	private Button getUpperLeftButton() {
		return (Button)findViewById(R.id.upperLeftButton);
	}
	
	private Button getUpperRightButton() {
		return (Button)findViewById(R.id.upperRightButton);
	}
	
	private Button getLowerLeftButton() {
		return (Button)findViewById(R.id.lowerLeftButton);
	}
	
	private Button getLowerRightButton() {
		return (Button)findViewById(R.id.lowerRightButton);
	}
	
	private void initializeNews() {
		AssetManager assetManager = getAssets();
		
		try {
			InputStream is = assetManager.open("20121120.xml");
			
			Serializer serializer = new Persister();
			this.bresciaOnLine = serializer.read(BresciaOnLine.class, is);
			this.giornale = this.bresciaOnLine.getGiornale();
			
			this.iSezione = -1;
			this.iArticolo = -1;
			this.maxSezioni = this.giornale.getSezioni().size();
			this.inSezione = false;
			
		} catch (IOException e) {
			if (Configuration.DEBUGGABLE) Log.d(TAG, "IOException", e);
		} catch (Exception e) {
			if (Configuration.DEBUGGABLE) Log.d(TAG, "Exception", e);
		}
		
	}

}
