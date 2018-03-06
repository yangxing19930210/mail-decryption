package com.sekorm.util;

public class PageUtil {
	
	private Integer sEcho;//记录操作的次数  每次加1
	private Integer iDisplayStart;// 起始
	private Integer iDisplayLength;// size
	private Integer totle;
	private String sSearch;// 搜索的关键字
	
	public Integer getsEcho() {
		return sEcho;
	}
	public void setsEcho(Integer sEcho) {
		this.sEcho = sEcho;
	}
	public Integer getiDisplayStart() {
		return iDisplayStart;
	}
	public void setiDisplayStart(Integer iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}
	public Integer getiDisplayLength() {
		return iDisplayLength;
	}
	public void setiDisplayLength(Integer iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}
	public String getsSearch() {
		return sSearch;
	}
	public void setsSearch(String sSearch) {
		this.sSearch = sSearch;
	}
	public Integer getTotle() {
		return totle;
	}
	public void setTotle(Integer totle) {
		this.totle = totle;
	}
	
}
