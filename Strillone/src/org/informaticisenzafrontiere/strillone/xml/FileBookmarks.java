package org.informaticisenzafrontiere.strillone.xml;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;







import org.informaticisenzafrontiere.strillone.MainActivity;

import android.util.Log;



public class FileBookmarks {

private final static String TAG = FileBookmarks.class.getSimpleName();

	
public void createFileBookmarks(String path) throws Exception{

	
	File BookmarksXml = new File(path);
	if (!(BookmarksXml.exists())) {

    	Bookmark bookmark =new Bookmark();
        try {   	
        	    Serializer serializer = new Persister();
				serializer.write(bookmark,BookmarksXml);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
}

public void deleteFileBookmarks(String path){
	
	File BookmarksXml = new File(path);
	if (BookmarksXml.exists()){
		
       BookmarksXml.delete();
	}
}

public Bookmark readBookmark(String path){
	
	File BookmarksXml = new File(path);
	Bookmark bookmark = null;
    if ( BookmarksXml.exists()){
        try {
            Serializer serializer = new Persister();
            bookmark = serializer.read(Bookmark.class, BookmarksXml);     
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	else bookmark=new Bookmark();
	return bookmark;	
}

public void writeBookmark(String path,Bookmark bookmark){
	
	File BookmarksXml = new File(path);

	if ( BookmarksXml.exists()) {
        try {
            Serializer serializer = new Persister();
            serializer.write(bookmark, BookmarksXml);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
	
}
