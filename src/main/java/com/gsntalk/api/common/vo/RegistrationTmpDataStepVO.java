package com.gsntalk.api.common.vo;

public class RegistrationTmpDataStepVO {

	private long memSeqno;
	private String regClasCd;
	private String regTmpKey;
	private int regStep;
	private String tmpJsonData;
	
	private long prptSeqno;
	private long suggstnSalesSeqno;
	private long prptSuggstReqSeqno;
	private long movPrpslPrptSeqno;
	private long assetSeqno;
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getRegClasCd() {
		return regClasCd;
	}
	public void setRegClasCd(String regClasCd) {
		this.regClasCd = regClasCd;
	}
	public String getRegTmpKey() {
		return regTmpKey;
	}
	public void setRegTmpKey(String regTmpKey) {
		this.regTmpKey = regTmpKey;
	}
	public int getRegStep() {
		return regStep;
	}
	public void setRegStep(int regStep) {
		this.regStep = regStep;
	}
	public String getTmpJsonData() {
		return tmpJsonData;
	}
	public void setTmpJsonData(String tmpJsonData) {
		this.tmpJsonData = tmpJsonData;
	}
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
	}
	public long getSuggstnSalesSeqno() {
		return suggstnSalesSeqno;
	}
	public void setSuggstnSalesSeqno(long suggstnSalesSeqno) {
		this.suggstnSalesSeqno = suggstnSalesSeqno;
	}
	public long getPrptSuggstReqSeqno() {
		return prptSuggstReqSeqno;
	}
	public void setPrptSuggstReqSeqno(long prptSuggstReqSeqno) {
		this.prptSuggstReqSeqno = prptSuggstReqSeqno;
	}
	public long getMovPrpslPrptSeqno() {
		return movPrpslPrptSeqno;
	}
	public void setMovPrpslPrptSeqno(long movPrpslPrptSeqno) {
		this.movPrpslPrptSeqno = movPrpslPrptSeqno;
	}
	public long getAssetSeqno() {
		return assetSeqno;
	}
	public void setAssetSeqno(long assetSeqno) {
		this.assetSeqno = assetSeqno;
	}
}