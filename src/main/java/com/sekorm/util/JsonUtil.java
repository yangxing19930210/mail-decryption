package com.sekorm.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class JsonUtil {
	
	public static String voToString(PageUtil page,List<HashMap<String,String>> list){
		StringBuilder sb=new StringBuilder();
		sb.append("{");
		sb.append("\"recordsFiltered\":\""+page.getTotle()+"\",");
		sb.append("\"recordsTotal\":\""+page.getTotle()+"\",");
		sb.append( "\"data\": [");
		for(HashMap<String,String> hm:list){
			sb.append("{");
			sb.append( "\"ID\":\""+String.valueOf(hm.get("NUM"))+"\",");
			sb.append( "\"SENDNAME\":\""+getReplace(hm.get("SENDNAME"))+"\",");
			sb.append( "\"ADDRESSENAME\":\""+getReplace(hm.get("ADDRESSENAME"))+"\",");
			sb.append( "\"CCRNAME\":\""+(hm.get("CCRNAME")==null?"":getReplace(hm.get("CCRNAME")))+"\",");
			sb.append( "\"SUBJECT\":\""+(hm.get("SUBJECT")==null?"":hm.get("SUBJECT"))+"\",");
			if(String.valueOf(hm.get("ACCESSORY")).indexOf(File.separator)!=-1){
				String result=String.valueOf(hm.get("ACCESSORY"));
				result=result.replace("c:\\Outlook_a\\","**");
				sb.append( "\"ACCESSORY\":\""+result+"\",");
			}else{
				sb.append( "\"ACCESSORY\":\""+hm.get("ACCESSORY")+"\",");
			}
			sb.append( "\"STATUS\":\""+hm.get("STATUS")+"\",");
			sb.append( "\"TIME\":\""+hm.get("TIME")+"\"");
			sb.append("},");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("]}");
		return sb.toString();
	}
	
	@SuppressWarnings("unused")
	private static String getReplace(String str){
		   str=str.replaceAll("<"," ");
		   str=str.replaceAll(">"," ");
		return str;
	}
	
}
