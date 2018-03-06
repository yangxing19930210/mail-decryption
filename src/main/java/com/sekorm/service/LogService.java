package com.sekorm.service;

import java.util.HashMap;
import java.util.List;

import com.sekorm.entity.Log;


public interface LogService {
	
	public abstract List<HashMap<String,String>> findLogAll(HashMap<String,Object> hm) throws Exception;
	
	public abstract Integer findLogRecords(HashMap<String,Object> hm) throws Exception;
	
	public abstract List<HashMap<String,Object>> findDayInfo() throws Exception;
}
