package org.informaticisenzafrontiere.strillone.xml;


import java.io.File;
import java.io.FileOutputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;



public class FileBookmarks {

	
public void createFileBookmarks() throws Exception{
	

	Serializer serializer = new Persister();
	File source = new File("Bookmaks.xml");
	Bookmark bookmark = serializer.read(Bookmark.class, source);
	
}

public void deleteFileBookmarks(){
	
}

public void insertBookmark(){
	
}

public void deleteBookmark(){
	
}





	
	
}
