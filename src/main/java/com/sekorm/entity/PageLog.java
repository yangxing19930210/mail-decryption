package com.sekorm.entity;

public class PageLog {
	
	private Integer start;
	private Integer end;
	private Integer size;
	private Integer page;
	private Integer total;
	private Integer rows;
	private Integer records;
	
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public Integer getRecords() {
		return records;
	}
	public void setRecords(Integer integer) {
		this.records = integer;
	}
	
}
