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
	     
	public boolean existsGiornaleBookmark(GiornaleBase giornale) 

	{
		boolean exists=false;
		int k=0;
		 while ((k<giornali.size()-1) && !(exists))
			{
			  if (giornali.get(k).getId()==(giornale.getId()))
				  exists=true;
			k++;
			}
	     return exists;
	 }

	public int existsSezioneBookmark(GiornaleBase giornale,Sezione sezione) 
    
	{   boolean exists=false;
	    int result = -1;
		if (!this.existsGiornaleBookmark(giornale))
				result=1;
			
		else {   //se il giornale esiste
			List<Sezione> sezioni=giornale.getSezioni();
			int k=0;
			while ((k<sezioni.size()-1) && (!exists))
			 {
			 if (sezioni.get(k).getId()==sezione.getId())
			       {
				    result=0;
			        exists=true;
		           }
			 k++;
		    }
			if (!exists) result=2;
			
		}
	     return result; //0 esistono il giornale e la sezione
	                   // 1 manca il giornale (e la sezione)
	                   // 2 manca solo la sezione
	 }

	public int existsArticoloBookmark(GiornaleBase giornale,Sezione sezione,Articolo articolo) 

	{   boolean exists=false;
	    int result = -1;
	
		if ((this.existsSezioneBookmark(giornale, sezione)==0)) //controlla solo articolo
		{
	    List<Articolo> articoli=null;
	    articoli=sezione.getArticoli();
	    int k=0;
	    while ((k<articoli.size()-1) && (!exists))
			    	{
			    		if (articoli.get(k).getTitolo()==articolo.getTitolo())
					       exists=true;
			    		   result=0; //0 esistono il giornale, la sezione e l'articolo
			    		   k++;
			    		  
			    		    
			    	}	 
	    if (!exists) result=3; // manca solo l'articolo 
	    }
		
		else if ((this.existsSezioneBookmark(giornale, sezione)==1) || (this.existsSezioneBookmark(giornale, sezione)==2))//
				result=this.existsSezioneBookmark(giornale, sezione);     // 1 manca il giornale,la sezione e articolo
																		  // 2 manca la sezione e l'articolo
			
		return result;  //0 esistono il giornale e la sezione
        			   // 1 manca il giornale (e la sezione)
                       // 2 manca la sezione, l'articolo
		               // 3 manca solo l'articolo
		}

	public void addGiornale(GiornaleBase giornale) 
		{
		if (!this.existsGiornaleBookmark(giornale)) giornali.add(giornale);
	     }
	
	public void addSezione(Giornale giornale,Sezione sezione) 
		
		{   
		 if((this.existsSezioneBookmark(giornale,sezione))== 1)//manca il giornale e la sezione 
				{
			     addGiornale(giornale);
			     List<Sezione> sezioni=new ArrayList<Sezione>();
			     sezioni=giornale.getSezioni();
	             sezioni.add(sezione);

			     
				}
		 else  if((this.existsSezioneBookmark(giornale,sezione))== 2)//manca la sezione
		        {
			    List<Sezione> sezioni=new ArrayList<Sezione>();
				sezioni=giornale.getSezioni();
			    sezioni.add(sezione);
			    }
	    }
	
    public void addArticolo(GiornaleBase giornale,Sezione sezione,Articolo articolo) 
		
		{   addGiornale(giornale);
		    //addSezione(sezione);
			int result=this.existsArticoloBookmark(giornale, sezione, articolo);
			
			 if((this.existsArticoloBookmark(giornale,sezione, articolo))== 1)//manca il giornale, la sezione e l'articolo
				{
			     addGiornale(giornale);
			     List<Sezione> sezioni=new ArrayList<Sezione>();
			     sezioni=giornale.getSezioni();
	             sezioni.add(sezione);
	             List<Articolo> articoli=new ArrayList<Articolo>();
	             articoli.add(articolo);
			     
				}
		 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 2)//manca la sezione e l'articolo
		        {
			    List<Sezione> sezioni=new ArrayList<Sezione>();
				sezioni=giornale.getSezioni();
			    sezioni.add(sezione);
			    List<Articolo> articoli=new ArrayList<Articolo>();
	             articoli.add(articolo);
			    }
		 else  if((this.existsArticoloBookmark(giornale,sezione, articolo))== 3)//manca l'articolo
	        {
		    List<Articolo> articoli=new ArrayList<Articolo>();
            articoli.add(articolo);
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

