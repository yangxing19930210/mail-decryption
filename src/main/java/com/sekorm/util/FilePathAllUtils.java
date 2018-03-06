package com.sekorm.util;

import java.io.File;
import java.util.ArrayList;

public class FilePathAllUtils {

	public static String[] FilePathAll(String path){   
        File file = new File(path);   
        File[] array = file.listFiles();  
        String[] str=null;
        ArrayList<String> list=new ArrayList<String>();
        for(int i=0;i<array.length;i++){   
            if(array[i].isFile()){   
            	list.add(array[i].getPath());
            } 
        }
        if(list.size()>0){
        	str=(String[])list.toArray(new String[list.size()]);
        }
        return str;
    }   
	
	/*public static void main(String[] args) {
		String[] arr = FilePathAll("c:\\Outlook_a\\");
		System.out.println(arr==null);
	}*/
}
