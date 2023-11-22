package com.gsntalk.api.common.extend;

public class PagingVO {


	private int rownum;
	private String srchGbCd;
	private String srchVal;
	private String srchValEnc;
	private String srchDateType;
	private int totalCount;
	private int cntPerPage;
	private String orderColumn;
	private String orderSort;
	
	private int stRnum;
	private int edRnum;
	
	public int getRownum() {
		return rownum;
	}
	public void setRownum(int rownum) {
		this.rownum = rownum;
	}
	public String getSrchVal() {
		return srchVal;
	}
	public void setSrchVal(String srchVal) {
		this.srchVal = srchVal;
	}
	public String getSrchGbCd() {
		return srchGbCd;
	}
	public void setSrchGbCd(String srchGbCd) {
		this.srchGbCd = srchGbCd;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getCntPerPage() {
		return cntPerPage;
	}
	public void setCntPerPage(int cntPerPage) {
		this.cntPerPage = cntPerPage;
	}
	public int getStRnum() {
		return stRnum;
	}
	public void setStRnum(int stRnum) {
		this.stRnum = stRnum;
	}
	public int getEdRnum() {
		return edRnum;
	}
	public void setEdRnum(int edRnum) {
		this.edRnum = edRnum;
	}

	public String getSrchValEnc() {return srchValEnc;}

	public void setSrchValEnc(String srchValEnc) {this.srchValEnc = srchValEnc;}

	public String getSrchDateType() {return srchDateType;}

	public void setSrchDateType(String srchDateType) {this.srchDateType = srchDateType;}

	public String getOrderColumn() { return orderColumn;}

	public void setOrderColumn(String orderColumn) { this.orderColumn = orderColumn; }

	public String getOrderSort() { return orderSort; }

	public void setOrderSort(String orderSort) { this.orderSort = orderSort;}
}