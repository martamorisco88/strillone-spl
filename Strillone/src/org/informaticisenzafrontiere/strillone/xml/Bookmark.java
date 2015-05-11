package org.informaticisenzafrontiere.strillone.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.util.Log;
import android.widget.Toast;

import org.informaticisenzafrontiere.strillone.MainActivity;
import org.informaticisenzafrontiere.strillone.R;

@Root(name="bookmark")
public class Bookmark {
	
	private final static String TAG = Bookmark.class.getSimpleName();
	
	public Bookmark()
	{ 
	}
	
	@ElementList(name="giornale")
    private List<GiornaleBookmark> giornali=new ArrayList<GiornaleBookmark>();
	
	
    public ArrayList<GiornaleBookmark> getGiornali() {
			
    	return (ArrayList<GiornaleBookmark>) giornali;
		}
	
	public void setGiornali(ArrayList<GiornaleBookmark>  giornali) {
			
		this.giornali=giornali;
		}
	
	public int posGiornaleBookmark(Giornale giornale)	{ 
		
		int k=-1;
		boolean trovato=false;
		while ((k<giornali.size()) && !(trovato)){ 
		  k++;
		  if (giornali.get(k).getId().equals(giornale.getId()))
			  trovato=true;
		}
	   if (!trovato) k=-1;
	  return k;
	}
	
	public int posSezioneBookmark(Giornale giornale, Sezione sezione) { 
		
		int k=-1;
		boolean trovato=false;
		int g=this.posGiornaleBookmark(giornale);
		List<Sezione> sezioni=giornali.get(g).getSezioni();
		while ((k<sezioni.size()) && (!trovato)) {
		   k++;
		   if (sezioni.get(k).getId().equals(sezione.getId()))
		       trovato=true;
		}
		if (!trovato) k=-1;
	 return k;
	}

	public boolean existsGiornaleBookmark(Giornale giornale) {
		
		boolean exists=false;
		int k=0;
		while ((k<giornali.size()) && !(exists)) {
			  if (giornali.get(k).getId().equals(giornale.getId()))
				  exists=true;
			  k++;
		 }
		//if (exists) Log.i(TAG,"Giornale già presente nei preferiti.");
		//else Log.i(TAG,"Inserimento Giornale nei preferiti.");
	     return exists;
	 }

	public int existsSezioneBookmark(Giornale giornale,Sezione sezione) {   
		 
		boolean exists=false;
	    int result = -1;
		if (!this.existsGiornaleBookmark(giornale))
				result=1;
			
		else {   //se il giornale esiste
			int g=this.posGiornaleBookmark(giornale);
			List<Sezione> sezioni=giornali.get(g).getSezioni();
			int k=0;
			while ((k<sezioni.size()) && (!exists))
			{
			 if (sezioni.get(k).getId().equals(sezione.getId()))
			       {
				    result=0;
			        exists=true;
		           }
			 k++;
		    }
			if (!exists)  {//Log.i(TAG,"Inserimento sezione nei preferiti.");
							result=2;
						  }
			//else Log.i(TAG,"Sezione già presente nei preferiti.");
			
		}
	     return result; //0 esistono il giornale e la sezione
	                   // 1 manca il giornale (e la sezione)
	                   // 2 manca solo la sezione
	 }

	public int existsArticoloBookmark(Giornale giornale,Sezione sezione,Articolo articolo) {  
		
		boolean exists=false;
	    int result = -1;
	    if ((this.existsSezioneBookmark(giornale, sezione)==0)) { //controlla solo articolo
		    
		    int g=this.posGiornaleBookmark(giornale);
	        int s=this.posSezioneBookmark(giornale, sezione);
	        List<Articolo> articoli=null;
		    articoli=giornali.get(g).getSezioni().get(s).getArticoli();
		    int k=0;
		    while ((k<articoli.size()) && (!exists)) {
		    	
				    		if (articoli.get(k).getTitolo().equals(articolo.getTitolo())){ 
				    		  
				    		  exists=true;
					    	  result=0; //0 esistono il giornale, la sezione e l'articolo
				    		}
						      
				    	 k++;
			}	 
		    if (!exists) { 
		    	result=3;// manca solo l'articolo 
		    }
		    // else  Log.i(TAG,"Articolo già presente nei preferiti.");
	    }
		
		else if ((this.existsSezioneBookmark(giornale, sezione)==1) || (this.existsSezioneBookmark(giornale, sezione)==2))//
				result=this.existsSezioneBookmark(giornale, sezione);     // 1 manca il giornale,la sezione e articolo
																		  // 2 manca la sezione e l'articolo
			
		return result;  //0 esistono il giornale e la sezione
        			   // 1 manca il giornale (e la sezione)
                       // 2 manca la sezione, l'articolo
		               // 3 manca solo l'articolo
		}

	public void addBookmarkGiornale(GiornaleBookmark giornale) {
		
		if (!this.existsGiornaleBookmark(giornale)){
			
			giornali.add(giornale);
	    }
	}
	
	public void addBookmarkSezione(GiornaleBookmark giornale,Sezione sezione) {
		
		 if((this.existsSezioneBookmark(giornale,sezione))== 1) { //manca il giornale e la sezione 
				
			 giornale.getSezioni().add(sezione);
			 giornali.add(giornale);
		 }
		 else  if((this.existsSezioneBookmark(giornale,sezione))== 2) {  // aggiungere solo la sezione
		     
			 int k=this.posGiornaleBookmark(giornale);
		      giornali.get(k).getSezioni().add(sezione);
		 }
	}
	
    public void addBookmarkArticolo(GiornaleBookmark giornale,Sezione sezione,Articolo articolo) {   
			int result=this.existsArticoloBookmark(giornale, sezione, articolo);
			if((this.existsArticoloBookmark(giornale,sezione, articolo))== 1) {//manca il giornale, la sezione e l'articolo
				 
				   Sezione newSezione=new Sezione(sezione.getId(),sezione.getNome(),null);
				   newSezione.getArticoli().add(articolo);
				   giornale.getSezioni().add(newSezione);
			       giornali.add(giornale);
			 }
			 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 2) {// manca la sezione e l'articolo
			           int g=this.posGiornaleBookmark(giornale);
			           Sezione newSezione=new Sezione(sezione.getId(),sezione.getNome(),null);
					   newSezione.getArticoli().add(articolo);
					   giornali.get(g).getSezioni().add(newSezione);
				    }
			 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 3){//manca l'articolo
		               int g=this.posGiornaleBookmark(giornale);
					   int s=this.posSezioneBookmark(giornale, sezione);
					   giornali.get(g).getSezioni().get(s).getArticoli().add(articolo);
			        }
		    }
		 
    public void deleteGiornale(GiornaleBookmark giornale) {
    	
		boolean exist=existsGiornaleBookmark(giornale);
		
		if (exist) 	 giornali.remove(giornale);
	}
	
    public void deleteSezione(GiornaleBookmark giornale,Sezione sezione) {
         
    	if (existsSezioneBookmark(giornale, sezione)==0) {  
    		
        	 int p=this.posGiornaleBookmark(giornale);
             int s=this.existsSezioneBookmark(giornale, sezione);
             giornali.get(p).getSezioni().remove(s);
        }	              
	}
		
    public void deleteArticolo(GiornaleBookmark giornale,Sezione sezione,Articolo articolo){
			
    	
    	if (this.existsArticoloBookmark(giornale, sezione, articolo)==0){
	        	 
    		int p=this.posGiornaleBookmark(giornale);
	        int s=this.existsSezioneBookmark(giornale, sezione);
	        giornali.get(p).getSezioni().get(s).getArticoli().remove(articolo);
	    }
    }

	public Testate newIndex() throws Exception {
		
		Testate newTestate = new Testate();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault());
		for (int i=0; i<(giornali.size());i++) {
			
		Testata newTestata = new Testata(giornali.get(i).getId(),giornali.get(i).getTestata(),
					                         giornali.get(i).getLingua(),null,
					                         giornali.get(i).getResource(),/*sdf.parse(giornali.get(i).getEdizione())*/null,false);
		newTestate.getTestate().add(newTestata);
		}
	return newTestate;
    }

	/*public Giornale createLiteGiornale(Giornale giornale) {
		
		
		if (this.existsGiornaleBookmark(giornale))
		{   
	
		  	int p=this.posGiornaleBookmark(giornale);
		  	boolean trovato=false;
		  	GiornaleBookmark giornaleBookmark=this.getGiornali().get(p);
		  	Giornale liteGiornale= new Giornale();
		  	liteGiornale.setEdizione(giornale.getEdizione());
		  	liteGiornale.setId(giornale.getId());
		  	liteGiornale.setLingua(giornale.getLingua());
		  	liteGiornale.setTestata(giornale.getTestata());
		  	for (int k=0; k<giornaleBookmark.getSezioni().size();k++ )
		  	{   
		  		int j=0;
		  		while (j<giornale.getSezioni())
		  	}
		  	
		}  	
		  	
	return giornale;
		
	}*/
      

/*  public void  deleteOldArticoli() 
		
    {   Calendar calendar = new GregorianCalendar();
		    Calendar today = calendar.getInstance();
	        long millisecondsToday = today.getTimeInMillis();
	        long millisecondsData;
	        long diffDays;
	        long diff;
            for (int g=0; g<this.giornali.size();g++)
             { 
              for (int s=0; s<this.giornali.get(g).getSezioni().size(); s++)
               { 
            	  for (int a=0;a<this.giornali.get(g).getSezioni().get(s).getArticoli().size();a++)
            	   { 
                       
					   Calendar data=this.giornali.get(g).getSezioni().get(s).getArticoli().get(a).getData();
					   millisecondsData =data.getTimeInMillis();
					   diff=millisecondsToday-millisecondsData;
					   diffDays = diff / (24 * 60 * 60 * 1000);	//differenza in giorni
					   if (diffDays>=15)  this.giornali.get(g).getSezioni().get(s).getArticoli().remove(a);
				   }
				
	 		   }
	    }*/
	}
    
	


