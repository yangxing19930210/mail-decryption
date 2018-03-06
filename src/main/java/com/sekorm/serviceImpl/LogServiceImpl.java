package com.sekorm.serviceImpl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sekorm.dao.LogDao;
import com.sekorm.entity.Log;
import com.sekorm.service.LogService;

@Service
public class LogServiceImpl implements LogService{
	
	@Autowired
	private LogDao logDao;
	
	@Override
	public List<HashMap<String,String>> findLogAll(HashMap<String,Object> hm) throws Exception {
		return logDao.findLogAll(hm);
	}

	@Override
	public Integer findLogRecords(HashMap<String,Object> hm) throws Exception {
		return logDao.findLogRecords(hm);
	}

	@Override
	public List<HashMap<String, Object>> findDayInfo() throws Exception {
		return logDao.findDayInfo();
	}

}
