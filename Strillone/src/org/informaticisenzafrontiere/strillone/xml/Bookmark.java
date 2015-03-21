package org.informaticisenzafrontiere.strillone.xml;
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
	

	public Bookmark( )
	{ 
	
	}
	
	 @ElementList(name="testata")
     private List<Testata> testate;
	    
	


     public List<Testata> getTestate() {
			return testate;
		}

		
	public void setTestate(List<Testata> testate) {
			this.testate=testate;
		}
		
	
		public boolean verifyTestata(Bookmark bookmark,Testata testata) 
		
		{
			List<Testata> testate=bookmark.getTestate();
			boolean exist=false;
			int k=0;
			 while ((k<testate.size()-1) && (exist==false))
				{
				  if (testate.get(k).getId()==testata.getId())
					  exist=true;
				}
		     return exist;
	     }

        public boolean verifySezione(Giornale giornale, Sezione sezione) 
		
		{
			List<Sezione> sezioni=giornale.getSezioni();
			boolean exist=false;
			int k=0;
			 while ((k<sezioni.size()-1) && (exist==false))
				{
				  if (sezioni.get(k).getId()==sezione.getId())
					  exist=true;
				}
		     return exist;
	     }
		
        
        public boolean verifyArticolo(Sezione sezione, Articolo articolo) 
		
		{
			List<Articolo> articoli=sezione.getArticoli();
			boolean exist=false;
			int k=0;
			 while ((k<articoli.size()-1) && (exist==false))
				{
				  if (articoli.get(k).getTitolo()==articolo.getTitolo())
					  exist=true;
				}
		     return exist;
	     }
		
        
       public Bookmark addTestata(Bookmark bookmark,Testata testata) 
		
		{
			//boolean exist=bookmark.verifyTestata(bookmark, testata);
			boolean exist=false;
			List<Testata> testate=null;
			if (exist==false) 	{
				testate=bookmark.getTestate();
				
				              testate.add(testata);
			                    }
			return bookmark;
			
	
			
		}
       
       
       public Bookmark addSezione(Bookmark bookmark,Giornale giornale,Sezione sezione) 
		
		{
			boolean exist=bookmark.verifySezione(giornale, sezione);
			
			if (exist==false) 	{
				List<Sezione> sezioni=giornale.getSezioni();
				              sezioni.add(sezione);
			                    }
			return bookmark;
	    }
	
       public Bookmark addArticolo(Bookmark bookmark,Giornale giornale,Sezione sezione,Articolo articolo) 
		
		{
			boolean exist=bookmark.verifyArticolo(sezione, articolo);
			
			if (exist==false) 	{
				List<Articolo> articoli=sezione.getArticoli();
				               articoli.add(articolo);
			                    }
			return bookmark;
	    }
       
       
       public Bookmark deleteTestata(Bookmark bookmark,Testata testata) 
		
		{
			boolean exist=bookmark.verifyTestata(bookmark, testata);
			
			if (exist==true) 	{
				List<Testata> testate=bookmark.getTestate();
				              testate.remove(testata);
			                    }
			return bookmark;
			
         }
       
       
       public Bookmark deleteSezione(Bookmark bookmark,Giornale giornale,Sezione sezione) 
		
		{
			boolean exist=bookmark.verifySezione(giornale, sezione);
			
			if (exist==true) 	{
				List<Sezione> sezioni=giornale.getSezioni();
				              sezioni.remove(sezione);
			                    }
			return bookmark;
	    }
		
       public Bookmark deleteArticolo(Bookmark bookmark,Giornale giornale,Sezione sezione,Articolo articolo) 
		
		{
			boolean exist=bookmark.verifyArticolo(sezione, articolo);
			
			if (exist==true) 	{
				List<Articolo> articoli=sezione.getArticoli();
				               articoli.remove(articolo);
			                    }
			return bookmark;
	    }
       
       public Bookmark deleteOldArticoli(Bookmark bookmark,Giornale giornale,Sezione sezione) 
		
		{   Calendar calendar = new GregorianCalendar();
		    Calendar today = calendar.getInstance();
	        long millisecondsToday = today.getTimeInMillis();
	        long millisecondsData;
	        long diffDays;
	        long diff;
			boolean exist=bookmark.verifySezione(giornale,sezione);
			
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
			return bookmark;
	    }
       
	
}
