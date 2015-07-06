package org.informaticisenzafrontiere.strillone.xml;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.util.Log;

import org.informaticisenzafrontiere.strillone.MainActivity;
import org.informaticisenzafrontiere.strillone.util.Configuration;

@Root(name="bookmark")
public class Bookmark implements Cloneable {
	
	private final static String TAG = Bookmark.class.getSimpleName();
	
	public Bookmark(){ 
	}
	
	@ElementList(name="giornali")
    private List<GiornaleBookmark> giornali=new ArrayList<GiornaleBookmark>();
	
	
    public ArrayList<GiornaleBookmark> getGiornali() {
			
    	return (ArrayList<GiornaleBookmark>) giornali;
		}
	
	public void setGiornali(ArrayList<GiornaleBookmark>  giornali) {
			
		this.giornali=giornali;
		}
    
    // Procedura che permette di conoscere la posizione di un giornale preferito all'interno dei Bookmarks
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
	
    // Procedura che permette di conoscere la posizione di una sezione preferito all'interno di un giornale preferito
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
	
    // Procedura che permette di conoscere la posizione di un articolo preferito all'interno di un giornale preferito
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
	
    // Procedura che permette di verificare la presenza di un giornale preferito all'interno dei Bookmarks
	public boolean existsGiornaleBookmark(String idGiornale) {
		
		boolean exists=false;
		int k=0;
		while ((k<giornali.size()) && !(exists)) {
			  if (giornali.get(k).getId().equals(idGiornale)){
				  
				  exists=true;
				  if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale presente nei preferiti");
			  }
				 
			  k++;
		 }
	     return exists;
	 }
	
    
    // Procedura che permette di verificare la presenza di una sezione preferita all'interno di un giornale preferito
	public boolean existsSezioneBookmark(String idGiornale,String idSezione) {   
		
		boolean exists=false;
		int g=this.posGiornaleBookmark(idGiornale);
		int k=0;
		while ((k<giornali.get(g).getSezioni().size()) && (!exists)) {
				
			if (giornali.get(g).getSezioni().get(k).getId().equals(idSezione)) {
				 exists=true;
			     if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione presente nei preferiti");
			 }
			 k++;
		    }
		
	 return exists; 
	}
	    

    // Procedura che permette di verificare la presenza di un articolo preferito all'interno di un giornale preferito
	public boolean existsArticoloBookmark(String idGiornale,String idSezione,String titolo) {
	
		boolean exists=false;
		if (this.existsSezioneBookmark(idGiornale, idSezione))  {
			
			int g=this.posGiornaleBookmark(idGiornale);
			int s=this.posSezioneBookmark(idGiornale, idSezione);
			int k=0;
			while ((k<this.giornali.get(g).getSezioni().get(s).getArticoli().size()) && (!exists)) {
				
				if (this.giornali.get(g).getSezioni().get(s).getArticoli().get(k).getTitolo().equals(titolo))  {
				    exists=true;
				    if (Configuration.DEBUGGABLE) Log.d(TAG, "Articolo presente nei preferiti");
				}
			k++;
		    }	 
		}
	return exists;     
}
	
    // Procedura che permette di aggiungere un giornale preferito all'interno dei Bookmarks
	public boolean addBookmarkGiornale(GiornaleBookmark giornale) {
		
		boolean add=false;
		if (!this.existsGiornaleBookmark(giornale.getId())){
			
			giornali.add(giornale);
			add=true;
			if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale inserito nei preferiti");
	    }
		return add;
	}
	
    // Procedura che permette di aggiungere una sezione preferita all'interno di un giornale preferito
	public boolean addBookmarkSezione(GiornaleBookmark giornale,Sezione sezione) {
		
         boolean add=false;		
		 if (!this.existsGiornaleBookmark(giornale.getId())) { //non è presente il giornale al quale appartiene la sezione che si vuole aggiungere
				
			 giornale.getSezioni().add(sezione);
			 giornali.add(giornale);
			 add=true;
			 if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale e sezione inseriti nei preferiti");
		 }
		 else   {
			 if (!this.existsSezioneBookmark(giornale.getId(),sezione.getId())) {  // aggiungere solo la sezione
				 int k=this.posGiornaleBookmark(giornale.getId());
				 giornali.get(k).getSezioni().add(sezione);
				 if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione inserita nei preferiti");
				 add=true;
			}

		 }
		return add;
	}
	
	// Procedura che permette di aggiungere un articolo preferito all'interno di un giornale preferito
	public boolean addBookmarkArticolo(GiornaleBookmark giornale, Articolo articolo) throws ParseException {
		
		boolean add=false;
        
		if (!this.existsGiornaleBookmark(giornale.getId())) {//manca il giornale, la sezione e l'articolo
			 
			   Sezione articoliSalvati=new Sezione(calculateHash("bookmark "+giornale.getResource()),"Articoli salvati "+giornale.getTestata(),new ArrayList<Articolo>());
			
			   ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
					   											 articolo.getTesto(),
					   											 giornale.getEdizione());
			   articoliSalvati.getArticoli().add(newArticolo);
			   giornale.getSezioni().add(articoliSalvati);
		       giornali.add(giornale);
		       add=true;
		 }
		 else {
			 if (!this.existsSezioneBookmark(giornale.getId(),calculateHash("bookmark "+giornale.getResource()))){ // manca la sezione e l'articolo
				 int g=this.posGiornaleBookmark(giornale.getId());
				 
				 //se il giornale era salvato interamente, inserire tutte le sezioni di origine, a cui si aggiungerà quella dei preferiti
				 if ((giornali.get(g).getSezioni().size())==0){
					
				 List<Sezione> sezioni_originali = giornale.getSezioni();
				 giornali.get(g).setSezioni(sezioni_originali);
				 }
				
		         ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
							 									  articolo.getTesto(),
							 									  giornale.getEdizione());
		         Sezione articoliSalvati=new Sezione(calculateHash("bookmark "+giornale.getResource()),"Articoli salvati "+giornale.getTestata(),new ArrayList<Articolo>());
		         articoliSalvati.getArticoli().add(newArticolo);
					
				 giornali.get(g).getSezioni().add(articoliSalvati);
				 add=true;
				 
			 }
			 	//manca l'articolo
			 else if (!this.existsArticoloBookmark(giornale.getId(), calculateHash("bookmark "+giornale.getResource()), articolo.getTitolo())){
			 		
				 int g=this.posGiornaleBookmark(giornale.getId());
			     int s=this.posSezioneBookmark(giornale.getId(),calculateHash("bookmark "+giornale.getResource()));
			     ArticoloBookmark newArticolo=new ArticoloBookmark(articolo.getTitolo(),
							 									   articolo.getTesto(),
							 									   giornale.getEdizione());
			     giornali.get(g).getSezioni().get(s).getArticoli().add(newArticolo);
			     add=true;
			 	}
	         }
		return add;
   }
			 
	// Procedura che permette di rimuovere un giornale preferito all'interno dei Bookmarks
    public void deleteGiornale(String idGiornale) {
    	
    	if(this.existsGiornaleBookmark(idGiornale)){
    		this.giornali.remove(this.posGiornaleBookmark(idGiornale));
    		if (Configuration.DEBUGGABLE) Log.d(TAG, "Giornale rimosso dai preferiti");
    	}
    	
	}
	
    
    // Procedura che permette di rimuovere una sezione preferita all'interno di un giornale preferito
    public void deleteSezione(String idGiornale,String idSezione) {
    	
    	if(this.existsSezioneBookmark(idGiornale, idSezione)){
    		if (this.getGiornali().get(this.posGiornaleBookmark(idGiornale)).getSezioni().size()==1) {// La sezione è unica, quindi va cancellato l'intero giornale
    			deleteGiornale(idGiornale);
    		}
    		
    		else if  (this.getGiornali().get(this.posGiornaleBookmark(idGiornale)).getSezioni().size()>1)// La sezione non è unica, quindi va cancellata solo la sezione
    			giornali.get(this.posGiornaleBookmark(idGiornale)).getSezioni().remove(this.posSezioneBookmark(idGiornale, idSezione));
    	     
    		if (Configuration.DEBUGGABLE) Log.d(TAG, "Sezione rimossa dai preferiti");       
    	}
	}
		
    // Procedura che permette di rimuovere un articolo preferito all'interno di un giornale preferito
    public void deleteArticolo(String idGiornale, String titolo){
    	
    	String idSezione=calculateHash("bookmark "+this.giornali.get(this.posGiornaleBookmark(idGiornale)).getResource());

    	if (this.existsArticoloBookmark(idGiornale, idSezione, titolo)){
    		
    		if (this.getGiornali().get(this.posGiornaleBookmark(idGiornale)).
    				getSezioni().get(this.posSezioneBookmark(idGiornale, idSezione)).getArticoli().size()==1 )
    			this.deleteSezione(idGiornale,idSezione);
    		else if (this.getGiornali().get(this.posGiornaleBookmark(idGiornale)).
    				getSezioni().get(this.posSezioneBookmark(idGiornale, idSezione)).getArticoli().size()>1){
    			
    			giornali.get(this.posGiornaleBookmark(idGiornale)).getSezioni().get(this.posSezioneBookmark(idGiornale, idSezione)).getArticoli().
    			remove(this.posArticoloBookmark(idGiornale, idSezione, titolo));
    			if (Configuration.DEBUGGABLE) Log.d(TAG, "Articolo rimosso dai preferiti");  
    	   }
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
		int p=this.posGiornaleBookmark(giornale.getId());
		Giornale bookmark= new Giornale();

		if (((this.getGiornali().get(p).getSezioni().size() == giornale.getSezioni().size())
		  		 || ((this.getGiornali().get(p).getSezioni().size()==0)))
		  		 && !(this.existsSezioneBookmark(this.getGiornali().get(p).getId(), calculateHash("bookmark "+this.getGiornali().get(p).getResource()))))
		  		 bookmark=giornale; //tutte le sezioni del giornale sono preferite
		  	
		  	else 
		  		{ //prendo solo le sezioni preferite o tutte più la sezione degli articoli preferiti
		  		
		  		List<Sezione> sezioniPref=new ArrayList<Sezione>();
		  	    for (int k1=0;k1<this.getGiornali().get(p).getSezioni().size();k1++){ 
		  	
		  			boolean trovato=false;
		  			int j=0;
		  			
		  			while (j< giornale.getSezioni().size() && !trovato){
		  				if ( this.getGiornali().get(p).getSezioni().get(k1).getId().equals(giornale.getSezioni().get(j).getId())){
	  					  sezioniPref.add(giornale.getSezioni().get(j));
	  					 }
		  				j++;
                    }
		  			  			
		  		}
		  		if (this.existsSezioneBookmark(this.getGiornali().get(p).getId(), calculateHash("bookmark "+this.getGiornali().get(p).getResource()))){
		  		int i=this.posSezioneBookmark(this.getGiornali().get(p).getId(), calculateHash("bookmark "+this.getGiornali().get(p).getResource()));
		  		sezioniPref.add(this.getGiornali().get(p).getSezioni().get(i));
		  		}
		  		bookmark= new Giornale(giornale.getId(),giornale.getTestata(),giornale.getEdizione(),giornale.getLingua(),sezioniPref);
		    }
		 
		 return bookmark;  	
    }
      

  public boolean  deleteOldArticoli() {
	  
	  Calendar today=Calendar.getInstance();
      long millisecondsToday = today.getTimeInMillis();
      boolean updateFile=false;
      int g=0;
      List<GiornaleBookmark> giornaliBookmark= (List<GiornaleBookmark>) (this.getGiornali()).clone(); // uso questa copia, altrimenti perdo i giornali man mano che cancello
      while (g<giornaliBookmark.size()){
    	  boolean existsGiornale=true;
    	  String idGiornale=giornaliBookmark.get(g).getId();
		  if (this.existsSezioneBookmark(idGiornale,calculateHash("bookmark "+giornaliBookmark.get(g).getResource()))){
			  int posSezPref=this.posSezioneBookmark(giornaliBookmark.get(g).getId(),calculateHash("bookmark "+giornaliBookmark.get(g).getResource()));
			  int a=0;                   
			  while( existsGiornale && a<giornaliBookmark.get(g).getSezioni().get(posSezPref).getArticoli().size())
			  {
				  ArticoloBookmark articolo=(ArticoloBookmark) giornaliBookmark.get(g).getSezioni().get(posSezPref).getArticoli().get(a); // faccio il cast per recuperare la data
				  Date edizione=articolo.getData();
	              Calendar cal = Calendar.getInstance();
	              cal.setTime(edizione);
	              long millisecondsData =cal.getTimeInMillis();
	              long diff=millisecondsToday-millisecondsData;
	              long diffDays = diff / (24 * 60 * 60 * 1000);	//differenza in giorni
	              if (diffDays>=15) {  	
	            	  this.deleteArticolo(idGiornale,giornaliBookmark.get(g).getSezioni().get(posSezPref).getArticoli().get(a).getTitolo());
	            	  if (Configuration.DEBUGGABLE) Log.d(TAG, "Articolo rimosso perchè troppo vecchio.");
	            	  existsGiornale=this.existsGiornaleBookmark(idGiornale);// verifico che il giornale esista ancora dopo la rimozione
					  updateFile=true;
					}	
				a++;
				}
			  }	  
	  g++;
	}
	return updateFile;
}
  
 public String calculateHash(String string){
    	 
    	 
    	MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		
        md.update(string.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        string=sb.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return string;
     } 
 
 
 public Object clone() {
	 try {
	 Bookmark b = (Bookmark)super.clone(); // Object.clone copia campo a campo
	 return b;
	 } catch (CloneNotSupportedException e) {
	 // non puo' accadere, ma va comunque gestito
	 throw new InternalError(e.toString());
	 }	
 }	
	
	
}
            
	
    
	


