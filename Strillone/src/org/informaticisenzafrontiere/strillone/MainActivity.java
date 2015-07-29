package org.informaticisenzafrontiere.strillone;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import org.informaticisenzafrontiere.strillone.ui.StrilloneButton;
import org.informaticisenzafrontiere.strillone.ui.StrilloneProgressDialog;
import org.informaticisenzafrontiere.strillone.util.Configuration;
import org.informaticisenzafrontiere.strillone.xml.Articolo;
import org.informaticisenzafrontiere.strillone.xml.ArticoloBookmark;
import org.informaticisenzafrontiere.strillone.xml.Bookmark;
import org.informaticisenzafrontiere.strillone.xml.FileBookmarks;
import org.informaticisenzafrontiere.strillone.xml.Giornale;
import org.informaticisenzafrontiere.strillone.xml.GiornaleBookmark;
import org.informaticisenzafrontiere.strillone.xml.Sezione;
import org.informaticisenzafrontiere.strillone.xml.Testata;
import org.informaticisenzafrontiere.strillone.xml.Testate;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Point;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import 	android.speech.SpeechRecognizer;
import java.text.ParseException;



public class MainActivity extends Activity implements IMainActivity, OnInitListener, Handler.Callback, RecognitionListener,GestureDetector.OnGestureListener, OnTouchListener  {
	
	private final static String TAG = MainActivity.class.getSimpleName();
	

	private GestureDetector mDetector; 
	private static final int SWIPE_MIN_DISTANCE = 200;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	enum NavigationLevel {
		TESTATE, SEZIONI, ARTICOLI;
	}
	
	private MainPresenter mainPresenter;
	
	private TextToSpeech textToSpeech;
	private StrilloneButton upperLeftButton;
	private StrilloneButton upperRightButton;
	private StrilloneButton lowerLeftButton;
	private StrilloneButton lowerRightButton;
	private StrilloneProgressDialog progressDialog;
	private PowerManager.WakeLock wakeLock = null;
	
	private Testate testate;
	private Giornale giornale;

	
	private NavigationLevel navigationLevel;
	private int iTestata;
	private int iSezione;
	private int iArticolo;
	
	private int maxTestate;
	private int maxSezioni;
	private int maxArticoli;
	
	private Testate oldTestate;
	private Testate testateBookmarks;
	
	private String bookmarksPath;
	private final FileBookmarks fileBookmarks = new FileBookmarks();
    private Bookmark readBookmarks= new Bookmark();
    
    private boolean modeBookmarks=false;
	
	private ImageView microphone;
	
	private View view; 	
	
	// Stabilisce se ci si trova all'inizio della navigazione delle testate.
	private boolean lowerEndTestate;
	
	// Stabilisce se ci si trova alla fine della navigazione delle testate.
	private boolean upperEndTestate;
	
	private boolean lowerEndSezioni;
	private boolean upperEndSezioni;
	private boolean lowerEndArticoli;
	private boolean upperEndArticoli;
	
	private boolean reloadHeaders = false;
	
	
	private LinkedList<String> sentences;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());

	public MainActivity() {
		this.mainPresenter = new MainPresenter(this);
	}
	
	private SpeechRecognizer speechRecognizer; // the speech recognizer
    private boolean speechRecognizerOn=false;  // state of the speech recognizer. 
    private HashMap<String, String> params = new HashMap<String, String>(); //textToSpeech.speak function parameter
	

    @SuppressLint({ "NewApi", "ClickableViewAccessibility" })
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        
        this.upperLeftButton = getUpperLeftButton();
        this.upperRightButton = getUpperRightButton();
        this.lowerLeftButton = getLowerLeftButton();
        this.lowerRightButton = getLowerRightButton();
        
        view=(View)findViewById(R.id.touchView); 
        view.setOnTouchListener(this);
        mDetector = new GestureDetector(this,this);
        view.setBackgroundColor(Color.BLACK);
        view.setAlpha(0);
        
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext()); // create the speech recognizer.
        createMicrophone(); 
        
        
        try {
        	bookmarksPath=getFilesDir().getPath() + "/bookmark.xml";
        	fileBookmarks.createFileBookmarks(bookmarksPath);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        final FileBookmarks fileBookmarks = new FileBookmarks();
		final Bookmark bookmark = fileBookmarks.readBookmark(bookmarksPath);
		boolean updateFile=bookmark.deleteOldArticoli();
		if (updateFile)
			fileBookmarks.writeBookmark(bookmarksPath, bookmark);
        
        
        MainActivity.this.params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"Strillone"); //add the parametrer 
        
        this.upperLeftButton.setOnLongClickListener(new View.OnLongClickListener() {	
        	
			public boolean onLongClick(View v) {
				// Se √® un testo "splitted" perch√© troppo lungo, svuota
	    		// la code dei messaggi in modo che allo stop non venga
	    		// riprodotto il messaggio successivo.
	    		if (MainActivity.this.sentences != null)
	    			MainActivity.this.sentences.clear();
				
				MainActivity.this.textToSpeech.stop();
				MainActivity.this.textToSpeech.speak(getResources().getString(R.string.nav_closing_app), TextToSpeech.QUEUE_FLUSH, params);
				
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
				
				return true;
			}
		});
        
        this.lowerLeftButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// Se √® un testo "splitted" perch√© troppo lungo, svuota
	    		// la code dei messaggi in modo che allo stop non venga
	    		// riprodotto il messaggio successivo.
	    		/*if (MainActivity.this.sentences != null)
	    			MainActivity.this.sentences.clear();
				
				MainActivity.this.textToSpeech.stop();
				MainActivity.this.textToSpeech.speak(getResources().getString(R.string.help_text), TextToSpeech.QUEUE_FLUSH, params);
				*/
				
				if (!modeBookmarks){
	                try {
						openBookmarks();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                }
	                else closeBookmarks();
				
				return true;
			}
		});
        
        
        this.lowerRightButton.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				
				GiornaleBookmark newGiornaleBookmark= new  GiornaleBookmark(MainActivity.this.testate.getTestate().get(iTestata).getId(),
			    		                                                    MainActivity.this.testate.getTestate().get(iTestata).getNome(),
			    		                                                    MainActivity.this.testate.getTestate().get(iTestata).getEdizione(),
			    		                                                    MainActivity.this.testate.getTestate().get(iTestata).getLingua(),
			    		                                                    new ArrayList<Sezione>(),
			    		                                                    MainActivity.this.testate.getTestate().get(iTestata).getResource() );
			    
				switch (navigationLevel) {
				
				
				case TESTATE:
					if (iTestata >= 0) {					
						if (!modeBookmarks)  {  
							if (bookmark.addBookmarkGiornale(newGiornaleBookmark))
							MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_newspaper_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
							else MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_newspaper_s_bookmark_error), TextToSpeech.QUEUE_FLUSH, null);
						}
							
						else  {
							bookmark.deleteGiornale(MainActivity.this.testate.getTestate().get(iTestata).getId());
							MainActivity.this.textToSpeech.speak(getResources().getString(R.string.delete_newspaper_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
							updateHeaders(bookmark);
							}
							
						fileBookmarks.writeBookmark(bookmarksPath, bookmark); 
					} 
					break;
				case SEZIONI:
					if (iSezione >= 0) {					 
						if (!modeBookmarks)  {
						Sezione newSezioneBookmark= new Sezione(MainActivity.this.giornale.getSezioni().get(iSezione).getId(),
																MainActivity.this.giornale.getSezioni().get(iSezione).getNome(),
																new ArrayList<Articolo>());
						if(bookmark.addBookmarkSezione(newGiornaleBookmark, newSezioneBookmark))    	
						MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_section_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
						else MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_section_s_bookmark_error), TextToSpeech.QUEUE_FLUSH, null);
						}
						else  {
							//la rimozione Ë concessa solo per le sezioni salvate
							if(bookmark.existsSezioneBookmark(newGiornaleBookmark.getId(),MainActivity.this.giornale.getSezioni().get(iSezione).getId())){
							
							bookmark.deleteSezione(newGiornaleBookmark.getId(),MainActivity.this.giornale.getSezioni().get(iSezione).getId());
							MainActivity.this.textToSpeech.speak(getResources().getString(R.string.delete_section_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
							updateSections(bookmark, newGiornaleBookmark.getId()) ;
							}
						}
						fileBookmarks.writeBookmark(bookmarksPath, bookmark);
						}
					break;
				case ARTICOLI:
					if (iArticolo >= 0) { 
						 ArticoloBookmark newArticolo = null;
						
						if (!modeBookmarks)  {
						 newArticolo = new ArticoloBookmark(MainActivity.this.giornale.getSezioni().get(iSezione).getArticoli().get(iArticolo).getTitolo(),
															   MainActivity.this.giornale.getSezioni().get(iSezione).getArticoli().get(iArticolo).getTesto(),
															   MainActivity.this.giornale.getEdizione()); // da verificare il formato data
						 try {
							 //aggiungo le sezioni perchË nell'aggiunta dell'articolo potrebbe servire
							 
							 
							 for (int k=0;k < MainActivity.this.giornale.getSezioni().size();k++)
							 {
							 Sezione newSezioneBookmark= new Sezione(MainActivity.this.giornale.getSezioni().get(k).getId(),
																	MainActivity.this.giornale.getSezioni().get(k).getNome(),
																	new ArrayList<Articolo>());
							 newGiornaleBookmark.getSezioni().add(newSezioneBookmark);
							 }	
							
							if(bookmark.addBookmarkArticolo(newGiornaleBookmark,newArticolo))
							MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_article_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
							else MainActivity.this.textToSpeech.speak(getResources().getString(R.string.insert_article_s_bookmark_error), TextToSpeech.QUEUE_FLUSH, null);} 
						 catch (ParseException e) {	e.printStackTrace(); }
						 
					  }	
						else {//la rimozione Ë concessa solo per gli articoli salvati
							
							String idSezionePreferiti= bookmark.calculateHash("bookmark "+newGiornaleBookmark.getResource());
							//if( MainActivity.this.giornale.getSezioni().get(iSezione).getId().equals(idSezionePreferiti)){
							bookmark.deleteArticolo(MainActivity.this.giornale.getId(),MainActivity.this.giornale.getSezioni().get(iSezione).getArticoli().get(iArticolo).getTitolo()); 
							MainActivity.this.textToSpeech.speak(getResources().getString(R.string.delete_article_s_bookmark), TextToSpeech.QUEUE_FLUSH, null);
							updateArticles(bookmark,MainActivity.this.giornale.getId(),MainActivity.this.giornale.getSezioni().get(iSezione).getId());
							//}
						}
						fileBookmarks.writeBookmark(bookmarksPath, bookmark);		
						}
						
					break;
				  	
				default:
					break;
			}
	    	
				
				return true;
			}
		});
        
        
        this.upperRightButton.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// Se √® un testo "splitted" perch√© troppo lungo, svuota
	    		// la code dei messaggi in modo che allo stop non venga
	    		// riprodotto il messaggio successivo.
	    		if (MainActivity.this.sentences != null)
	    			MainActivity.this.sentences.clear();
				
				MainActivity.this.textToSpeech.stop();
				
				// Calcola la posizione.
				StringBuffer sbPosizione = new StringBuffer(getString(R.string.pos_current));
				if (iTestata < 0) {
					// Non √® stata selezionata alcuna testata.
					sbPosizione.append(getString(R.string.pos_no_header_selected));
				} else {
					Testata testata = MainActivity.this.testate.getTestate().get(iTestata);
					
					sbPosizione.append(testata.getNome());
					sbPosizione.append(", ");
					sbPosizione.append(sdf.format(testata.getEdizione()));
					sbPosizione.append(". ");
					
					if (iSezione < 0) {
						// Non √® stata selezionata alcuna sezione.
						sbPosizione.append(getString(R.string.pos_no_section_selected));
					} else {
						Sezione sezione = MainActivity.this.giornale.getSezioni().get(iSezione);
						sbPosizione.append(String.format(getString(R.string.pos_section_selected), sezione.getNome()));
						
						if (iArticolo < 0) {
							sbPosizione.append(getString(R.string.pos_no_article_selected));
						} else {
							Articolo articolo = sezione.getArticoli().get(iArticolo);
							sbPosizione.append(String.format(getString(R.string.pos_article_selected), articolo.getTitolo()));
						}
					}
				}
				
				MainActivity.this.textToSpeech.speak(sbPosizione.toString(), TextToSpeech.QUEUE_FLUSH, params);
				
				return true;
			}	
			
			
			
			
			
		});
        
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        
		
		
        this.textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setOnUtteranceProgressListener(utteranceProgressListener);
        
    }
    
  
    @SuppressLint("NewApi") UtteranceProgressListener utteranceProgressListener=new UtteranceProgressListener() {

        @Override
        public void onStart(String utteranceId) {
            //Log.d(TAG, "onStart ( utteranceId :"+utteranceId+" ) ");
        }

        @Override
        public void onError(String utteranceId) {
            //Log.d(TAG, "onError ( utteranceId :"+utteranceId+" ) ");
        }

        @Override
        public void onDone(String utteranceId) {
        	if (Configuration.DEBUGGABLE) Log.d(TAG, "End of speech!");
        	if (MainActivity.this.sentences != null) playNextSentence();
        	                           else {
        							   MainActivity.this.runOnUiThread(new Runnable() {
        							   @Override
        							   public void run() {
        							   if (speechRecognizerOn) createListener();
        							   }
        							   }); }//fine else
        }
    };
    
    
	@Override
	protected void onResume() {
		super.onResume();
		this.wakeLock.acquire();
		// TODO accessibilityEnabled=getAccessibilityEnabled();//to know if talkback is on or off.
	}
	
	@Override
	protected void onPause() {
		if (Configuration.DEBUGGABLE) Log.d(TAG, "onPause()");
		
		// Se stai leggendo un articolo svuota il buffer delle frasi e ferma il TTS.
		if (this.sentences != null) {
			this.sentences.clear();
		}
		this.textToSpeech.stop();
		if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {this.speechRecognizer.stopListening(); this.speechRecognizer.destroy();} //mod miky
		
		// Rilascia il controllo sullo standby.
		this.wakeLock.release();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (Configuration.DEBUGGABLE) Log.d(TAG, "onDestroy()");
		this.textToSpeech.shutdown();
		if (SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {this.speechRecognizer.stopListening(); this.speechRecognizer.destroy();} //mod miky
		super.onDestroy();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.beta:
	        	startProgressDialog(getString(R.string.connecting_headers));
	            this.mainPresenter.switchBetaState();
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	//tasto sali
	public void performUpperLeftAction(View v) {
    	if (this.textToSpeech.isSpeaking()) {
    		// Se √® un testo "splitted" perch√© troppo lungo, svuota
    		// la code dei messaggi in modo che allo stop non venga
    		// riprodotto il messaggio successivo.
    		if (this.sentences != null)
    			this.sentences.clear();
    		
    		this.textToSpeech.stop();
    	} else {
    		if (reloadHeaders) {
    			startProgressDialog(getString(R.string.connecting_headers));
    	        this.mainPresenter.downloadHeaders();
    		} else {
	    		switch (this.navigationLevel) {
					case TESTATE:
						resetNavigation();
			    		this.textToSpeech.speak(getResources().getString(R.string.nav_home), TextToSpeech.QUEUE_FLUSH, params);
						break;
					case SEZIONI:
						// Passa alla navigazione delle testate.
						this.navigationLevel = NavigationLevel.TESTATE;
						this.iSezione = -1;
						this.textToSpeech.speak(getResources().getString(R.string.nav_go_testate), TextToSpeech.QUEUE_FLUSH, params);
						break;
					case ARTICOLI:
						// Passa alla navigazione delle sezioni.
						this.navigationLevel = NavigationLevel.SEZIONI;
						this.iArticolo = -1;
						this.textToSpeech.speak(getResources().getString(R.string.nav_go_sezioni), TextToSpeech.QUEUE_FLUSH, params);
						break;
					default:
						break;
				}
    		}
    		
    	}
    }
    
	//tasto entra
    public void performLowerLeftAction(View v) {
    	switch (this.navigationLevel) {
			case TESTATE:
				if (this.iTestata >= 0) {
					// Seleziona la testata.
					startProgressDialog(String.format(getString(R.string.connecting_newspaper), this.testate.getTestate().get(this.iTestata).getNome()));
					
					this.mainPresenter.downloadGiornale();
					
//					this.lowerEndSezioni = true;
//					this.upperEndSezioni = false;
				}
				break;
			case SEZIONI:
				if (this.iSezione >= 0) {
		    		// Entra nella sezione.
		    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
		    		this.maxArticoli = sezione.getArticoli().size();
		    		
//		    		this.lowerEndArticoli = true;
//		    		this.upperEndArticoli = false;
		    		
		    		StringBuilder sbMessaggio = new StringBuilder();
		    		sbMessaggio.append(getResources().getString(R.string.nav_enter_section));
		    		sbMessaggio.append(sezione.getNome());
		    		this.navigationLevel = NavigationLevel.ARTICOLI;
		    		
		    		this.textToSpeech.speak(sbMessaggio.toString(), TextToSpeech.QUEUE_FLUSH, params);
	    		}
				break;
			case ARTICOLI:
				if (this.iArticolo >= 0) {
		    		// Leggi l'articolo.
		    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
		    		Articolo articolo = sezione.getArticoli().get(iArticolo);
		    		
		    		if (articolo.getTesto().length() <= Configuration.SENTENCE_MAX_LENGTH) {
		    			this.textToSpeech.speak(articolo.getTesto(), TextToSpeech.QUEUE_FLUSH, params);
		    		} else {
		    			/*if (Configuration.DEBUGGABLE)*/ Log.d(TAG, "Testo troppo lungo, splitting...");
		    			this.sentences = this.mainPresenter.splitString(articolo.getTesto(), Configuration.SENTENCE_MAX_LENGTH);
		    			int i = this.sentences.size();
		    			if (Configuration.DEBUGGABLE) {
		    				for (int j = 0; j < i; j++) {
		    					Log.d(TAG, "frase " + (j + 1) + ": " + sentences.get(j));
		    				}
		    			}
		    			playNextSentence();
		    		}
	    		}
				break;
			default:
				break;
		}
    	
    }
    
    //tasto precedente
    public void performUpperRightAction(View v) {
    	switch (this.navigationLevel) {
			case TESTATE:
				if (this.lowerEndTestate) {
					if (this.iTestata >= 0) {
						// 
						this.textToSpeech.speak(getString(R.string.nav_first_header), TextToSpeech.QUEUE_FLUSH, params);
					} else {
						// Caso dell'app appena avviata o resettata.
						this.textToSpeech.speak(getString(R.string.nav_use_lower_button_start_navigation_headers), TextToSpeech.QUEUE_FLUSH, params);
					}
				} else {
					if (!this.upperEndTestate) {
						if (this.iTestata > 0) {
							this.iTestata--;
						}
						
						if (this.iTestata == 0) {
							this.lowerEndTestate = true;
						}
					}
					
					if (this.iTestata >= 0) {
						Testata testata = this.testate.getTestate().get(iTestata);
						
						StringBuilder sb = new StringBuilder();
						sb.append(testata.getNome());
						sb.append(", ");
						sb.append(sdf.format(testata.getEdizione()));
						
						this.textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, params);
					}
					this.upperEndTestate = false;
				}
				
				break;
			case SEZIONI:
				if (this.lowerEndSezioni) {
					if (this.iSezione >= 0) {
						this.textToSpeech.speak(getString(R.string.nav_first_sezione), TextToSpeech.QUEUE_FLUSH, params);
					} else {
						this.textToSpeech.speak(getString(R.string.nav_use_lower_button_start_navigation_sezioni), TextToSpeech.QUEUE_FLUSH, params);
					}
				} else {
					if (!this.upperEndSezioni) {
						if (this.iSezione > 0) {
							this.iSezione--;
						}
						
						if (this.iSezione == 0) {
							this.lowerEndSezioni = true;
						}
					}
					
					if (this.iSezione >= 0) {
		    			Sezione sezione = this.giornale.getSezioni().get(iSezione);
		    			this.textToSpeech.speak(sezione.getNome(), TextToSpeech.QUEUE_FLUSH, params);
		    		}
		    		
		    		this.upperEndSezioni = false;
				}    		
	    		
				break;
			case ARTICOLI:
				if (this.lowerEndArticoli) {
					if (this.iArticolo >= 0) {
						this.textToSpeech.speak(getString(R.string.nav_first_articolo), TextToSpeech.QUEUE_FLUSH, params);
					} else {
						this.textToSpeech.speak(getString(R.string.nav_use_lower_button_start_navigation_articoli), TextToSpeech.QUEUE_FLUSH, params);
					}
				} else {
					if (!this.upperEndArticoli) {
						if (this.iArticolo > 0) {
			    			this.iArticolo--;
			    		}
						
						if (this.iArticolo == 0) {
		    				this.lowerEndArticoli = true;
		    			}
					}
					
					if (this.iArticolo >= 0) {
			    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
			    		Articolo articolo = sezione.getArticoli().get(iArticolo);
			    		this.textToSpeech.speak(articolo.getTitolo(), TextToSpeech.QUEUE_FLUSH, params);
		    		}
		    		
		    		this.upperEndArticoli = false;
				}

				break;
			default:
				break;
		}
    	
    }
    
    // tasto successivo 
    public void performLowerRightAction(View v) {  
    	switch (this.navigationLevel) {
			case TESTATE:
				if (this.upperEndTestate) {
					this.textToSpeech.speak(getString(R.string.nav_last_header), TextToSpeech.QUEUE_FLUSH, params);
				} else {
					if (!lowerEndTestate || this.iTestata < 0) {
						if (this.iTestata < this.maxTestate - 1) {
							this.iTestata++;
						}
						if (this.iTestata == this.maxTestate - 1) {
							this.upperEndTestate = true;
						}
					}
					
					Testata testata = this.testate.getTestate().get(iTestata);
					
					StringBuilder sb = new StringBuilder();
					sb.append(testata.getNome());
					sb.append(", ");
					sb.append(sdf.format(testata.getEdizione()));
					
					this.textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, params);
					
					this.lowerEndTestate = false;
				}
				
				break;
			case SEZIONI: {
					if (this.upperEndSezioni) {
						this.textToSpeech.speak(getString(R.string.nav_last_sezione), TextToSpeech.QUEUE_FLUSH, params);
					} else {
						if (!lowerEndSezioni || this.iSezione < 0) {
							if (this.iSezione < this.maxSezioni - 1) {
				    			this.iSezione++;
				    		}
							if (this.iSezione == this.maxSezioni - 1) {
								this.upperEndSezioni = true;
							}
						}
			    		
			    		Sezione sezione = this.giornale.getSezioni().get(iSezione);
			    		this.textToSpeech.speak(sezione.getNome(), TextToSpeech.QUEUE_FLUSH, params);
			    		
			    		this.lowerEndSezioni = false;
					}
					
				}
				break;
			case ARTICOLI: {
					if (this.upperEndArticoli) {
						this.textToSpeech.speak(getString(R.string.nav_last_articolo), TextToSpeech.QUEUE_FLUSH, params);
					} else {
						Sezione sezione = this.giornale.getSezioni().get(iSezione);
						if (!lowerEndArticoli || this.iArticolo < 0) {
				    		int maxArticoli = sezione.getArticoli().size();
				    		
				    		if (this.iArticolo < maxArticoli - 1) {
				    			this.iArticolo++;
				    		}
				    		
				    		if (this.iArticolo == maxArticoli - 1) {
				    			this.upperEndArticoli = true;
				    		}
						}
						
						Articolo articolo = sezione.getArticoli().get(iArticolo);
			    		this.textToSpeech.speak(articolo.getTitolo(), TextToSpeech.QUEUE_FLUSH, params);
			    		this.lowerEndArticoli = false;
					}	
				}
				
				break;
			default:
				break;
    	}
    	
    }
    
    

    @Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			startProgressDialog(getString(R.string.connecting_headers));
	        this.mainPresenter.downloadHeaders();
		} else {
			Toast.makeText(this, R.string.init_ko, Toast.LENGTH_SHORT).show();
			
			ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
	        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK);
		}
	}
    
	
/*	@Override
	public void onUtteranceCompleted(String utteranceId) {
		playNextSentence();
		Toast.makeText(this, "fine parlato", Toast.LENGTH_SHORT).show();
	}*/
	
	private void playNextSentence() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(sentences.size()));
		this.textToSpeech.speak(sentences.poll(), TextToSpeech.QUEUE_FLUSH, params);
	}

	@Override
	public void notifyCommunicationError(String message) {
		dismissProgressDialog();
		this.textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);
	}
	
	@Override
	public void notifyErrorDowloadingHeaders(String message) {
		dismissProgressDialog();
		this.textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);
		this.reloadHeaders = true;
		this.upperLeftButton.setClickable(true);
	}
	
	@Override
	public void notifyHeadersReceived(Testate testate) {
		dismissProgressDialog();
		
		this.reloadHeaders = false;
		this.testate = testate;
		this.maxTestate = testate.getTestate().size();
		this.lowerEndTestate = true;
		this.upperEndTestate = false;
		
		resetNavigation();
		
		// Visualizza i pulsanti.
		this.upperLeftButton.setVisibility(View.VISIBLE);
        this.upperRightButton.setVisibility(View.VISIBLE);
        this.lowerLeftButton.setVisibility(View.VISIBLE);
        this.lowerRightButton.setVisibility(View.VISIBLE);
		
		// Abilita i pulsanti al touch.
		this.upperLeftButton.setClickable(true);
        this.upperRightButton.setClickable(true);
        this.lowerLeftButton.setClickable(true);
        this.lowerRightButton.setClickable(true);
        
        Toast.makeText(this, R.string.init_ok, Toast.LENGTH_SHORT).show();
        
//        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
//        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        try {
        	AssetFileDescriptor descriptor = getAssets().openFd("jingle.mp3");
	        MediaPlayer mediaPlayer = new MediaPlayer();
	        mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        mediaPlayer.prepare();
	        mediaPlayer.start();
        } catch (IOException e) {
        	if (Configuration.DEBUGGABLE) Log.d(TAG, "Eccezione nella riproduzione del jingle.", e);
        }
	}
	
	@Override
	public String getURLTestata() {
		Testata testata = (Testata)this.testate.getTestate().get(iTestata);
		return testata.getUrl();
	}
	
	//Morisco
	public String getResourceTestata() {
		Testata testata = (Testata)this.testate.getTestate().get(iTestata);
		return testata.getResource();
		}
	

	//Morisco
	@Override
	public void notifyGiornaleReceived(Giornale giornale) {
		if (Configuration.DEBUGGABLE) Log.d(TAG, "notifyGiornaleReceived()");
		dismissProgressDialog();
		
		this.navigationLevel = NavigationLevel.SEZIONI;
		this.iSezione = -1;
		this.iArticolo = -1;
		
		
		StringBuilder sb = new StringBuilder();
		sb.append(giornale.getTestata());
		sb.append(getString(R.string.nav_read_success));
		if (Configuration.DEBUGGABLE) Log.d(TAG, "sb: " + sb);
    
		if (!modeBookmarks)    
			this.giornale = giornale;
        else  this.giornale=readBookmarks.createGiornaleBookmark(giornale);
     	this.maxSezioni = this.giornale.getSezioni().size();		
		this.textToSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null);
	}
	
	
	private void resetNavigation() {
		// Inizializza il livello di navigazione.
		this.navigationLevel = NavigationLevel.TESTATE;
				
		// Imposta gli indici a inizio navigazione.
		this.iTestata = -1;
    	this.iSezione = -1;
    	this.iArticolo = -1;
    	
    	this.lowerEndTestate = true;
    	this.upperEndTestate = false;
    	this.lowerEndSezioni = true;
    	this.upperEndSezioni = false;
    	this.lowerEndArticoli = true;
    	this.upperEndArticoli = false;
	}
	
	
private void closeBookmarks() {   
		
		changeHeaders(oldTestate);
	    modeBookmarks=false;
		this.textToSpeech.speak(getString(R.string.normal_mode), TextToSpeech.QUEUE_FLUSH, null);
		if (Configuration.DEBUGGABLE) Log.d(TAG, "Modalit‡ normale.");
		
	}
	private void openBookmarks() throws Exception {   
		
		readBookmarks=(Bookmark) fileBookmarks.readBookmark(bookmarksPath);
		if (readBookmarks.getGiornali().size() > 0 ){  
			oldTestate=testate;
	     	testateBookmarks=readBookmarks.newHeadersIndex();
	     	changeHeaders(testateBookmarks);
	     	modeBookmarks=true;
			this.textToSpeech.speak(getString(R.string.bookmarks_mode), TextToSpeech.QUEUE_FLUSH, null);
		} 
		else this.textToSpeech.speak(getString(R.string.bookmarks_error), TextToSpeech.QUEUE_FLUSH, null);
		
	if (Configuration.DEBUGGABLE) Log.d(TAG, "Modalit‡ preferiti.");
	}


	public void changeHeaders(Testate testate) {
	    this.reloadHeaders = false;
		this.testate = testate;
		this.maxTestate = testate.getTestate().size();
		this.lowerEndTestate = true;
		this.upperEndTestate = false;
		resetNavigation();
	}
	
	public void updateHeaders(Bookmark bookmark) {

		if((bookmark.getGiornali().size())>0)
			changeHeaders(bookmark.newHeadersIndex());
		else {
			  closeBookmarks();
		}
	}

	public void updateSections(Bookmark bookmark, String idGiornale) {

		if (!bookmark.existsGiornaleBookmark(idGiornale)) { //il giornale Ë stato rimosso insieme alla sezione
			 updateHeaders(bookmark);
		}
		else if (bookmark.getGiornali().get(bookmark.posGiornaleBookmark(idGiornale)).getSezioni().size()>0) {
			this.giornale=bookmark.getGiornali().get(bookmark.posGiornaleBookmark(idGiornale));
			this.iSezione--;
		}
	}

public void updateArticles(Bookmark bookmark, String idGiornale, String idSezione) {
	
	if (!bookmark.existsGiornaleBookmark(idGiornale)) { //il giornale Ë stato rimosso insieme alla sezione
		 updateHeaders(bookmark);
	}	 else if (!bookmark.existsSezioneBookmark(idGiornale, idSezione)) { //la sezione Ë stato rimossa insieme all'articolo
		 updateSections(bookmark, idGiornale);
		 }
		 else{
			 MainActivity.this.giornale=bookmark.getGiornali().get(bookmark.posGiornaleBookmark(idGiornale)); 
			 this.iArticolo--;
		 }
}
	
	
	private StrilloneButton getUpperLeftButton() {
		return (StrilloneButton)findViewById(R.id.upperLeftButton);
	}
	
	private StrilloneButton getUpperRightButton() {
		return (StrilloneButton)findViewById(R.id.upperRightButton);
	}
	
	private StrilloneButton getLowerLeftButton() {
		return (StrilloneButton)findViewById(R.id.lowerLeftButton);
	}
	
	private StrilloneButton getLowerRightButton() {
		return (StrilloneButton)findViewById(R.id.lowerRightButton);
	}
	
	private void startProgressDialog(String message) {
		progressDialog = new StrilloneProgressDialog(this);
		progressDialog.setProgressStyle(StrilloneProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	private void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	@SuppressWarnings("unused")
	private void playTone() {
		ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
	
	
	@Override
	public void onReadyForSpeech(Bundle params) {
		showMicrophone();
		if (Configuration.DEBUGGABLE) Log.i(TAG, "Ready for speech.");	
	}
	
	public void createMicrophone(){
		microphone = (ImageView) findViewById(R.id.microphoneImage);
		microphone.setVisibility(View.INVISIBLE);
	}
	
	@SuppressLint("NewApi") public void showMicrophone(){
		microphone.setVisibility(View.VISIBLE);
		view.setAlpha(0.8f);
	}
	
	@SuppressLint("NewApi") public void hideMicrophone(){
		microphone.setVisibility(View.INVISIBLE);
		view.setAlpha(0);
	}

	@Override
	public void onBeginningOfSpeech() {
		if (Configuration.DEBUGGABLE) Log.i(TAG, "Beginning of speech");
	}



	@Override
	public void onRmsChanged(float rmsdB) {	
	}



	@Override
	public void onBufferReceived(byte[] buffer) {
		
	}



	@Override
	public void onEndOfSpeech() {
		if (Configuration.DEBUGGABLE) Log.i(TAG, "End of speech");
		hideMicrophone();
	}



	@Override
	public void onError(int error) {
		/* 
		 1 Network operation timed out
         2 Other network related errors
         3 Audio recording error
         4 Server sends error status
         5 Other client side errors
         6 No speech input
         7 No recognition result matched
         8 RecognitionService busy
         9 Insufficient permissions
         */  
   		   speechRecognizer.destroy();
   		   hideMicrophone();
           if (error==7) createListener(); //on error "7" restart the listener.
 	}

	@Override
	public void onResults(Bundle results) {
		ArrayList<String> words;
		String command=null;
		words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		command=words.get(0);
		 if (command.equalsIgnoreCase(getResources().getString(R.string.upperLeftString))) performUpperLeftAction(upperLeftButton);
	   		else if (command.equalsIgnoreCase(getResources().getString(R.string.upperRightString))) performUpperRightAction(upperRightButton);
	   			else if (command.equalsIgnoreCase(getResources().getString(R.string.lowerLeftString))) performLowerLeftAction(lowerLeftButton);
	   				else if (command.equalsIgnoreCase(getResources().getString(R.string.lowerRightString))) performLowerRightAction(lowerRightButton);
	   					else if (command.equalsIgnoreCase(getResources().getString(R.string.close))) {speechRecognizer.destroy();speechRecognizerOn=false;}
	   						else if (command.equalsIgnoreCase(getResources().getString(R.string.close_exit))) {speechRecognizer.destroy();MainActivity.this.finish();speechRecognizerOn=false;}
	   						 else if (getFromVoice(command));
	   						 	else MainActivity.this.textToSpeech.speak(getResources().getString(R.string.unrecognized_command), TextToSpeech.QUEUE_FLUSH, params);	
		speechRecognizer.destroy();  
	}

	

	@Override
	public void onPartialResults(Bundle partialResults) {
		
	}



	@Override
	public void onEvent(int eventType, Bundle params) {
		
	}
	
	public void createListener(){	
		 this.textToSpeech.stop();
		 speechRecognizerOn=true;
	     speechRecognizer.setRecognitionListener(MainActivity.this);
	     final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	     intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	     speechRecognizer.startListening(intent); 
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event){ 
	
	return false;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		float x=e.getX();
		float y=e.getY();
		int button=detectButton(x,y);
		switch (button) {
		case Configuration.UPPERLEFTBUTTON: 
			upperLeftButton.performClick();  
			upperLeftButton.performHapticFeedback(3); 
			break;
		case Configuration.UPPERRIGHTBUTTON: 
			upperRightButton.performClick(); 
			upperRightButton.performHapticFeedback(3);
			break;
		case Configuration.LOWERLEFTBUTTON: 
			lowerLeftButton.performClick();  
			lowerLeftButton.performHapticFeedback(3); 
			break;
		case Configuration.LOWERRIGHTBUTTON: 
			lowerRightButton.performClick(); 
			lowerRightButton.performHapticFeedback(3); 
			break;
		}	
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		float x=e.getX();
		float y=e.getY();
		int button=detectButton(x,y);
		switch (button) {
		case Configuration.UPPERLEFTBUTTON: 
			upperLeftButton.performLongClick(); 
			upperLeftButton.performHapticFeedback(3); 
			break;
		case Configuration.UPPERRIGHTBUTTON: 
			upperRightButton.performLongClick(); 
			upperRightButton.performHapticFeedback(3); 
			break;
		case Configuration.LOWERLEFTBUTTON: 
			lowerLeftButton.performLongClick(); 
			lowerLeftButton.performHapticFeedback(3); 
			break;
		case Configuration.LOWERRIGHTBUTTON: 
			lowerRightButton.performLongClick(); 
			lowerRightButton.performHapticFeedback(3); 
			break;
		}	
	}

	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
		boolean result=false;
		float offsetX= Math.abs(e1.getX()-e2.getX());
		float offsetY=Math.abs(e1.getY()-e2.getY());
		// se Ë maggiore offset di X considero solo da destra a sinistra e da sinistra a destra. 
		// se Ë maggiore offset di Y considero solo da su a giu e da giu a su
		if (offsetX>offsetY) {  if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
								speechRecognizer.destroy();speechRecognizerOn=false; hideMicrophone(); result=true; }  
								else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
									createListener();  result=true; }} 
						else {  if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
							 result=true;}
								else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
								/*createListener();*/ result=true; }}
		return result;
	}

	
	 @Override
	public boolean onTouch(View v, MotionEvent event) {
		int index = event.getActionIndex();
		int pointerId = event.getPointerId(index)+1;
		  switch (event.getAction() & MotionEvent.ACTION_MASK) { 
		    case MotionEvent.ACTION_POINTER_UP:	
		    {  
		    if (pointerId==3){Toast.makeText(MainActivity.this, "Multitap con 3 dita." ,Toast.LENGTH_SHORT).show();Log.i(TAG,"multitap detected");}  
		    }
		}
		this.mDetector.onTouchEvent(event);
	 return true;
	}
		
/* TODO	public boolean getAccessibilityEnabled(){
		boolean accessibilityFound = false;
		 try {
		        int accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
		        if (Configuration.DEBUGGABLE) Log.d(TAG, "ACCESSIBILITY: " + accessibilityEnabled);
		        if (accessibilityEnabled==1){accessibilityFound=true;}
		    } catch (SettingNotFoundException e) {
		    	if (Configuration.DEBUGGABLE) Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
		    }
		 return accessibilityFound;
	}*/

     @SuppressLint("NewApi") 
     public int detectButton(float x,float y){
	 Display display = getWindowManager().getDefaultDisplay();
	 Point size = new Point();
	 display.getSize(size);
	 int width = size.x;
	 int height = size.y;	
	 int halfwidth = width/2;
	 int halfheight = height/2;
	 int button=0;
	 if ((x<halfwidth) && (y<halfheight)) {
		 button=1;
	 }
	 if ((x>halfwidth) && (y<halfheight)) {
		 button=2;
	 }
	 if ((x<halfwidth) && (y>halfheight)) {
		 button=3;
	 }
	 if ((x>halfwidth) && (y>halfheight)) {
		 button=4;
	 }
	 return button;
	 }
     
     public boolean getFromVoice(String name) { 
    	 boolean trovato=false;
    	 int k=-1;
    	 switch (this.navigationLevel) {
    	 case TESTATE:
    	 {
    	 while ((k<MainActivity.this.testate.getTestate().size()-1) && (!trovato)) {
    		 k++;
    		 if ((MainActivity.this.testate.getTestate().get(k).getNome().equalsIgnoreCase(name))||
 					(MainActivity.this.testate.getTestate().get(k).getNome().contains(name)) &&
 					  (name.length()>=(MainActivity.this.testate.getTestate().get(k).getNome().length()/3))
 					)trovato=true;
     	 }
 			if (trovato){ MainActivity.this.iTestata=k;
 			lowerLeftButton.performClick();
 			}
 	     } break;
    	 case SEZIONI:
    	 {
    	 	while ((k<MainActivity.this.giornale.getSezioni().size()-1) && (!trovato)) {
    		k++;
    			   if (MainActivity.this.giornale.getSezioni().get(k).getNome().equalsIgnoreCase(name))
    			       trovato=true;
    			}
    			if (trovato){ MainActivity.this.iSezione=k;
    			lowerLeftButton.performClick();
    			}
       	 } break;
    	default: break;
    	 }
    return trovato;
     }
     
    
}

