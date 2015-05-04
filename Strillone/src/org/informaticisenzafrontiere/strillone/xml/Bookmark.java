package org.informaticisenzafrontiere.strillone.xml;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
    private ArrayList<GiornaleBase> giornali=new ArrayList<GiornaleBase>();
	
	
    public List<GiornaleBase> getGiornali() {
			return giornali;
		}
	
	public void setGiornali(ArrayList<GiornaleBase>  giornali) {
			this.giornali=giornali;
		}
	public int posGiornaleBookmark(GiornaleBase giornale)
	{ 
		int k=-1;
		boolean trovato=false;
		while ((k<giornali.size()) && !(trovato))
		{ 
		  k++;
		  if (giornali.get(k).getId().equals(giornale.getId()))
			  trovato=true;
		}
	   if (!trovato) k=-1;
	  return k;
	}
	
	public int posSezioneBookmark(GiornaleBase giornale, Sezione sezione)
	{ 
		int k=-1;
		boolean trovato=false;
		int g=this.posGiornaleBookmark(giornale);
		List<Sezione> sezioni=giornali.get(g).getSezioni();
		while ((k<sezioni.size()) && (!trovato))
		 {
		   k++;
		   if (sezioni.get(k).getId().equals(sezione.getId()))
		       trovato=true;
		}
	 if (!trovato) k=-1;
	 return k;
	}

	public boolean existsGiornaleBookmark(GiornaleBase giornale) 

	{
		boolean exists=false;
		int k=0;
		 while ((k<giornali.size()) && !(exists))
			{
			  if (giornali.get(k).getId().equals(giornale.getId()))
				  exists=true;
			k++;
			}
		if (exists) Log.i(TAG,"Giornale già presente nei preferiti.");
		else Log.i(TAG,"Inserimento Giornale nei preferiti.");
	     return exists;
	 }

	public int existsSezioneBookmark(GiornaleBase giornale,Sezione sezione) 
    
	{   boolean exists=false;
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
			if (!exists)  {Log.i(TAG,"Inserimento sezione nei preferiti.");
							result=2;
						  }
			else Log.i(TAG,"Sezione già presente nei preferiti.");
			
		}
	     return result; //0 esistono il giornale e la sezione
	                   // 1 manca il giornale (e la sezione)
	                   // 2 manca solo la sezione
	 }

	public int existsArticoloBookmark(GiornaleBase giornale,Sezione sezione,Articolo articolo) 

	{   boolean exists=false;
	    int result = -1;
	    
     	if ((this.existsSezioneBookmark(giornale, sezione)==0)) //controlla solo articolo
		{   int g=this.posGiornaleBookmark(giornale);
	        int s=this.posSezioneBookmark(giornale, sezione);
	        List<Articolo> articoli=null;
		    articoli=giornali.get(g).getSezioni().get(s).getArticoli();
		    int k=0;
		    while ((k<articoli.size()) && (!exists))
				    	{
				    		if (articoli.get(k).getTitolo().equals(articolo.getTitolo()))
				    		{ exists=true;
					    	  result=0; //0 esistono il giornale, la sezione e l'articolo
				    		}
						      
				    	 k++;
				         }	 
		    if (!exists) { result=3;// manca solo l'articolo 
		                   Log.i(TAG,"Articolo non presente nei preferiti.");
		                 }
		     else  Log.i(TAG,"Articolo già presente nei preferiti.");
	    }
		
		else if ((this.existsSezioneBookmark(giornale, sezione)==1) || (this.existsSezioneBookmark(giornale, sezione)==2))//
				result=this.existsSezioneBookmark(giornale, sezione);     // 1 manca il giornale,la sezione e articolo
																		  // 2 manca la sezione e l'articolo
			
		return result;  //0 esistono il giornale e la sezione
        			   // 1 manca il giornale (e la sezione)
                       // 2 manca la sezione, l'articolo
		               // 3 manca solo l'articolo
		}

	public void addBookmarkGiornale(GiornaleBase giornale) 
		{
		if (!this.existsGiornaleBookmark(giornale))
			{
			giornali.add(giornale);
			
			}
	     }
	
	public void addBookmarkSezione(GiornaleBase giornale,Sezione sezione) 
		
	{   
		 if((this.existsSezioneBookmark(giornale,sezione))== 1)//manca il giornale e la sezione 
				{
				   GiornaleBase newGiornale=new GiornaleBase();
				   newGiornale.setId(giornale.getId());
				   newGiornale.setTestata(giornale.getTestata());
				   newGiornale.getSezioni().add(sezione);
			       giornali.add(newGiornale);
				 }
		 else  if((this.existsSezioneBookmark(giornale,sezione))== 2) // aggiungere solo la sezione
		 {         int k=this.posGiornaleBookmark(giornale);
		           giornali.get(k).getSezioni().add(sezione);
		  }
	}
	
    public void addBookmarkArticolo(GiornaleBase giornale,Sezione sezione,Articolo articolo) 
		
		{   
			int result=this.existsArticoloBookmark(giornale, sezione, articolo);
			
			 if((this.existsArticoloBookmark(giornale,sezione, articolo))== 1)//manca il giornale, la sezione e l'articolo
				{
				 
				   GiornaleBase newGiornale=new GiornaleBase();
				   newGiornale.setId(giornale.getId());
				   newGiornale.setTestata(giornale.getTestata());
				  
				   Sezione newSezione= new Sezione();
				   newSezione.setId(sezione.getId());
				   newSezione.setNome(sezione.getNome());
				   newSezione.getArticoli().add(articolo);
				
				   newGiornale.getSezioni().add(newSezione);
			       giornali.add(newGiornale);
					     
				}
		 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 2)//manca la sezione e l'articolo
		        {  int g=this.posGiornaleBookmark(giornale);
				   Sezione newSezione= new Sezione();
				   newSezione.setId(sezione.getId());
				   newSezione.setNome(sezione.getNome());
				   newSezione.getArticoli().add(articolo);
				   giornali.get(g).getSezioni().add(newSezione);
				   
				   
			    }
		 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 3)//manca l'articolo
	        {
			 int g=this.posGiornaleBookmark(giornale);
			 int s=this.posSezioneBookmark(giornale, sezione);
			 
			 giornali.get(g).getSezioni().get(s).getArticoli().add(articolo);
		    }
	    }
		 
    public void deleteGiornale(GiornaleBase giornale) 
	
	{
		boolean exist=existsGiornaleBookmark(giornale);
		
		if (exist==true) 	{
			List<GiornaleBase> giornali=this.getGiornali();
			               giornali.remove(giornali);
		                    }

    }
	
    public void deleteSezione(GiornaleBase giornale,Sezione sezione) 
		
		{

			if (existsSezioneBookmark(giornale, sezione)==0)	{
				List<Sezione> sezioni=giornale.getSezioni();
				              sezioni.remove(sezione);
			                    }

	    }
		
    public void deleteArticolo(GiornaleBase giornale,Sezione sezione,Articolo articolo) 
		
		{
			if (this.existsArticoloBookmark(giornale, sezione, articolo)==0)
			
				{
				List<Articolo> articoli=sezione.getArticoli();
				               articoli.remove(articolo);
			                    }

	    }
       
    /*public void  deleteOldArticoli(GiornaleBase giornale,Sezione sezione) 
		
		{   Calendar calendar = new GregorianCalendar();
		    Calendar today = calendar.getInstance();
	        long millisecondsToday = today.getTimeInMillis();
	        long millisecondsData;
	        long diffDays;
	        long diff;
			boolean exist=this.existsSezioneBookmark(sezione);
			
			if (exist==true) 	{
				List<Articolo> articoli=sezione.getArticoli();
			    for (int k=0; k<articoli.size()-1;k++ )
				   {
					   ArticoloBookmark articolo = (ArticoloBookmark) articoli.get(k);
					   Calendar data=articolo.getData();
					   millisecondsData =data.getTimeInMillis();
					   diff=millisecondsToday-millisecondsData;
					   diffDays = diff / (24 * 60 * 60 * 1000);	//differenza in giorni
					   if (diffDays>=15)   articoli.remove(articolo);
				   }
				
	 		                    }
	    }
       */
	
}

