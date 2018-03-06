package com.sekorm.dao;

import java.util.HashMap;
import java.util.List;
import com.sekorm.entity.LeaderMail;

public interface LeaderMailDao {
	
	public abstract List<LeaderMail> findByMail(HashMap<String,String> mail);
	
}
