package com.sekorm.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sekorm.service.LogService;
import com.sekorm.util.JsonUtil;
import com.sekorm.util.PageUtil;


@RestController
@RequestMapping("/find")
public class LogController {

	@Autowired
	private LogService logService;
	
	@RequestMapping(value = "/all",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
	public String findAll(HttpServletRequest request,PageUtil page,String date,String zt,String qt) throws Exception{
		HashMap<String,Object> param=new HashMap<String, Object>();
		param.put("date","".equals(date)?null:date);
		param.put("zt","".equals(zt)?null:zt);
		param.put("qt","".equals(qt)?null:(new String(qt.getBytes("ISO-8859-1"), "UTF-8")));
		param.put("start",String.valueOf(page.getiDisplayStart()+1));
		param.put("end",String.valueOf(page.getiDisplayStart()+page.getiDisplayLength()));
	    List<HashMap<String,String>> list=logService.findLogAll(param);
	    page.setTotle(logService.findLogRecords(param));
		return  JsonUtil.voToString(page,list);
	}

}
