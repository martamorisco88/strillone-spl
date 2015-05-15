package org.informaticisenzafrontiere.strillone.xml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.util.Log;

import org.informaticisenzafrontiere.strillone.util.Configuration;

@Root(name="bookmark")
public class Bookmark {
	
	private final static String TAG = Bookmark.class.getSimpleName();
	
	public Bookmark(){ 
	}
	
	@ElementList(name="giornale")
    private List<GiornaleBookmark> giornali=new ArrayList<GiornaleBookmark>();
	
	
    public ArrayList<GiornaleBookmark> getGiornali() {
			
    	return (ArrayList<GiornaleBookmark>) giornali;
		}
	
	public void setGiornali(ArrayList<GiornaleBookmark>  giornali) {
			
		this.giornali=giornali;
		}
	
	public int posGiornaleBookmark(String idGiornale)	{ 
		
		int k=-1;
		boolean trovato=false;
		while ((k<giornali.size()) && !(trovato)){ 
		  k++;
		  if (giornali.get(k).getId().equals(idGiornale))
			  trovato=true;
		}
	   if (!trovato) k=-1;
	  return k;
	}
	
	public int posSezioneBookmark(String idGiornale, String idSezione) { 
		
		int k=-1;
		boolean trovato=false;
		int g=this.posGiornaleBookmark(idGiornale);
		while ((k<giornali.get(g).getSezioni().size()) && (!trovato)) {
		   k++;
		   if (giornali.get(g).getSezioni().get(k).getId().equals(idSezione))
		       trovato=true;
		}
		if (!trovato) k=-1;
	 return k;
	}
	
public int posArticoloBookmark(String idGiornale, String idSezione, String titolo) { 
		
		int k=-1;
		boolean trovato=false;
		int g=this.posGiornaleBookmark(idGiornale);
		int s=this.posSezioneBookmark(idGiornale, idSezione);
		while ((k<giornali.get(g).getSezioni().get(s).getArticoli().size()) && (!trovato)) {
		   k++;
		   if (giornali.get(g).getSezioni().get(s).getArticoli().get(k).getTitolo().equals(titolo))
			   	trovato=true;
		}
		if (!trovato) k=-1;
	 return k;
	}
	

	public boolean existsGiornaleBookmark(String idGiornale) {
		
		boolean exists=false;
		int k=0;
		while ((k<giornali.size()) && !(exists)) {
			  if (giornali.get(k).getId().equals(idGiornale)){
				  
				  exists=true;
				  if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale già presente nei preferiti");
			  }
				 
			  k++;
		 }
	     return exists;
	 }
	
	public boolean existsSezioneBookmark(String idGiornale,String idSezione) {   
		
		boolean exists=false;
		int g=this.posGiornaleBookmark(idGiornale);
		int k=0;
		while ((k<giornali.get(g).getSezioni().size()) && (!exists)) {
				
			if (giornali.get(g).getSezioni().get(k).getId().equals(idSezione)) {
				 exists=true;
			     if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione già presente nei preferiti");
			 }
			 k++;
		    }
		
	 return exists; 
	}
	    


	public boolean existsArticoloBookmark(String idGiornale,String idSezione,String titolo) {
	
		boolean exists=false;
		if (this.existsSezioneBookmark(idGiornale, idSezione))  {
			
			int g=this.posGiornaleBookmark(idGiornale);
			int s=this.posSezioneBookmark(idGiornale, idSezione);
			int k=0;
			while ((k<giornali.get(g).getSezioni().get(s).getArticoli().size()) && (!exists)) {
				
				if (giornali.get(g).getSezioni().get(s).getArticoli().get(k).getTitolo().equals(titolo)) 
				 // && (giornali.get(g).getSezioni().get(s).getArticoli().get(k).g.equals(titolo)) )
				  {
				    exists=true;
				    if (Configuration.DEBUGGABLE) Log.d(TAG, "Articolo già presente nei preferiti");
				}
			k++;
		    }	 
		}
	return exists;     
}
	
	
	public void addBookmarkGiornale(GiornaleBookmark giornale) {
		
		if (!this.existsGiornaleBookmark(giornale.getId())){
			
			giornali.add(giornale);
			if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale inserito nei preferiti");
	    }
	}
	
	public void addBookmarkSezione(GiornaleBookmark giornale,Sezione sezione) {
		
		 if (!this.existsGiornaleBookmark(giornale.getId())) { //manca il giornale e la sezione 
				
			 giornale.getSezioni().add(sezione);
			 giornali.add(giornale);
			 if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale e sezione inseriti nei preferiti");
		 }
		 else   {
			 if (!this.existsSezioneBookmark(giornale.getId(),sezione.getId())) {  // aggiungere solo la sezione
				 int k=this.posGiornaleBookmark(giornale.getId());
				 giornali.get(k).getSezioni().add(sezione);
				 if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione inserita nei preferiti");
		 }
		 }
	}
	
			 
	public void addBookmarkArticolo(GiornaleBookmark giornale,Sezione sezione,Articolo articolo) throws ParseException {   
		
		if (!this.existsGiornaleBookmark(giornale.getId())) {//manca il giornale, la sezione e l'articolo
			   Sezione newSezione=new Sezione(sezione.getId(),sezione.getNome(),new ArrayList<Articolo>());
			   ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
					   											 articolo.getTesto(),
					   											 giornale.getEdizione());
			   newSezione.getArticoli().add(newArticolo);
			   giornale.getSezioni().add(newSezione);
		       giornali.add(giornale);
		 }
		 else {
			 if (!this.existsSezioneBookmark(giornale.getId(), sezione.getId())){ // manca la sezione e l'articolo
				 int g=this.posGiornaleBookmark(giornale.getId());
		         Sezione newSezione=new Sezione(sezione.getId(),sezione.getNome(), new ArrayList<Articolo>());
		         ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
							 									  articolo.getTesto(),
							 									  giornale.getEdizione());
				 newSezione.getArticoli().add(newArticolo);
				 giornali.get(g).getSezioni().add(newSezione);
				 
			 }
			 	else {
				 int g=this.posGiornaleBookmark(giornale.getId());
			     int s=this.posSezioneBookmark(giornale.getId(), sezione.getId());
			     ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
							 									   articolo.getTesto(),
							 									   giornale.getEdizione());
			     giornali.get(g).getSezioni().get(s).getArticoli().add(newArticolo);
			 	}
	         }
   }
			 
	
    public void deleteGiornale(String idGiornale) {
    	
    	if(this.existsGiornaleBookmark(idGiornale)){
    		this.giornali.remove(this.posGiornaleBookmark(idGiornale));
    		if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale rimosso dai preferiti");
    	}
	}
	
    public void deleteSezione(String idGiornale,String idSezione) {
    	
    	if(this.existsSezioneBookmark(idGiornale, idSezione)){
    	giornali.get(this.posGiornaleBookmark(idGiornale)).getSezioni().remove(this.posSezioneBookmark(idGiornale, idSezione));
    	     if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione rimossa dai preferiti");       
    	}
	}
		
    public void deleteArticolo(String idGiornale,String idSezione,String titolo, String edizione){
			
    	if (this.existsArticoloBookmark(idGiornale, idSezione, titolo)){
	       giornali.get(this.posGiornaleBookmark(idGiornale)).getSezioni().get(this.posSezioneBookmark(idGiornale, idSezione)).getArticoli().
	       remove(this.posArticoloBookmark(idGiornale, idSezione, titolo));
	    }
    }

	public Testate newHeadersIndex() {

		Testate newTestate = new Testate();
	
        for (int i=0; i<(giornali.size());i++) {
        	Testata newTestata = new Testata(giornali.get(i).getId(),giornali.get(i).getTestata(),
					                         giornali.get(i).getLingua(),
					                         null,
					                         giornali.get(i).getResource(),
					                         giornali.get(i).getEdizione(),					                     
					                         false);
       newTestate.getTestate().add(newTestata);
        
		}
	return newTestate;
    }

	public Giornale createGiornaleBookmark(Giornale giornale) {
		
		Giornale giornaleBookmark= new Giornale();
		int p=this.posGiornaleBookmark(giornale.getId());
		if ((this.getGiornali().get(p).getSezioni().size() == giornale.getSezioni().size())
		  		 || ((this.getGiornali().get(p).getSezioni().size()==0)))
		  				 
		  				 giornaleBookmark=giornale; //tutte le sezioni del giornale sono preferite
		  	
		  	else if (this.getGiornali().get(p).getSezioni().size() < giornale.getSezioni().size()) {//prendo solo le sezioni preferite
		  		
		  		giornaleBookmark= new Giornale(giornale.getId(),giornale.getTestata(),giornale.getEdizione(),giornale.getLingua(),this.getGiornali().get(p).getSezioni());
		  		
		  		for (int k=0;k<giornaleBookmark.getSezioni().size();k++){ //riempio gli articoli delle sezioni preferite
		  			 
		  			boolean trovato=false;
		  			int j=0;
		  			while (j< giornale.getSezioni().size() && !trovato){
		  				
		  				if ( giornaleBookmark.getSezioni().get(k).getId().equals(giornale.getSezioni().get(j).getId())){
		  					
		  					trovato=true;
		  					giornaleBookmark.getSezioni().get(k).setArticoli(giornale.getSezioni().get(j).getArticoli());
		  					
		  				}
		  				j++;
                    }
		  		}
		  	}
		  	
		  	
	    
		return giornaleBookmark;  	
    }
      

	// da completare...
/*  public void  deleteOldArticoli() {
	  
	  Calendar today = today.getInstance();
      long millisecondsToday = today.getTimeInMillis();
	 
	  for (int g=0; g<this.giornali.size();g++){ 
       for (int s=0; s<this.giornali.get(g).getSezioni().size(); s++){ 
     	  for (int a=0;a<this.giornali.get(g).getSezioni().get(s).getArticoli().size();a++){ 
                
				   Calendar cal = Calendar.getInstance();
				   cal.setTime(this.giornali.get(g).getSezioni().get(s).getArticoli().get(a).getEdizione());
				   long millisecondsData =cal.getTimeInMillis();
				   long diff=millisecondsToday-millisecondsData;
				   long diffDays = diff / (24 * 60 * 60 * 1000);	//differenza in giorni
				   if (diffDays>=15)  this.giornali.get(g).getSezioni().get(s).getArticoli().remove(a);
		  }
			
	    }
      }
  }*/
	  
 
}
            
	
    
	


