package com.sekorm.dao;

import java.util.HashMap;
import java.util.List;

import com.sekorm.entity.Log;

public interface LogDao {
	
	public abstract boolean insert(Log log);
	
	public abstract List<HashMap<String,String>> findLogAll(HashMap<String,Object> hm);
	
	public abstract Integer findLogRecords(HashMap<String,Object> hm);
	
	public abstract List<HashMap<String,Object>> findDayInfo();
}
