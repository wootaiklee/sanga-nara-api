package com.gsntalk.api.common.vo;

import org.json.simple.JSONArray;

import com.gsntalk.api.common.extend.PagingVO;

public class PropertySuggestRequestVO extends PagingVO {
	
	private long prptSuggstReqSeqno;
	private long memSeqno;
	private long suggstMemSeqno;
	private String memTypCd;
	private String memTypNm;
	private String suggstMemTypCd;
	private String suggstMemTypNm;
	private String suggstrNm;
	JSONArray estateTypCdItems;
	private String estateTypCd;
	private String estateTypNm;
	private String tranTypGbCd;
	private String tranTypGbNm;
	private int suggstPrptCnt;
	private String suggstDt;
	private double wishArea;
	private String sectrGbCd;
	private String sectrGbNm;
	private int usePplCnt;
	private String psblMovDayTypCd;
	private String psblMovDayTypNm;
	private String psblMovStDate;
	private String psblMovEdDate;
	private long dealAmt;
	private long dpstAmt;
	private int montRentAmt;
	private String clientNm;
	private String compNm;
	private String suggstSectrCd;
	private String wishFlrTypCd;
	private String intrrYn;
	private String reqDscr;
	private String regionNm;
	
	private long prptSeqno;
	private String email;
	private String memName;
	private String mobNo;
	private int prptSuggstCnt;
	private String regDt;
	private String recentDt;
	
	private JSONArray suggstRegionItems;
	 
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
	}
	public String getEstateTypCd() {
		return estateTypCd;
	}
	public void setEstateTypCd(String estateTypCd) {
		this.estateTypCd = estateTypCd;
	}
	public String getTranTypGbCd() {
		return tranTypGbCd;
	}
	public void setTranTypGbCd(String tranTypGbCd) {
		this.tranTypGbCd = tranTypGbCd;
	}
	public double getWishArea() {
		return wishArea;
	}
	public void setWishArea(double wishArea) {
		this.wishArea = wishArea;
	}
	public String getSectrGbCd() {
		return sectrGbCd;
	}
	public void setSectrGbCd(String sectrGbCd) {
		this.sectrGbCd = sectrGbCd;
	}
	public int getUsePplCnt() {
		return usePplCnt;
	}
	public void setUsePplCnt(int usePplCnt) {
		this.usePplCnt = usePplCnt;
	}
	public String getPsblMovDayTypCd() {
		return psblMovDayTypCd;
	}
	public void setPsblMovDayTypCd(String psblMovDayTypCd) {
		this.psblMovDayTypCd = psblMovDayTypCd;
	}
	public String getPsblMovStDate() {
		return psblMovStDate;
	}
	public void setPsblMovStDate(String psblMovStDate) {
		this.psblMovStDate = psblMovStDate;
	}
	public String getPsblMovEdDate() {
		return psblMovEdDate;
	}
	public void setPsblMovEdDate(String psblMovEdDate) {
		this.psblMovEdDate = psblMovEdDate;
	}
	public long getDealAmt() {
		return dealAmt;
	}
	public void setDealAmt(long dealAmt) {
		this.dealAmt = dealAmt;
	}
	public long getDpstAmt() {
		return dpstAmt;
	}
	public void setDpstAmt(long dpstAmt) {
		this.dpstAmt = dpstAmt;
	}
	public int getMontRentAmt() {
		return montRentAmt;
	}
	public void setMontRentAmt(int montRentAmt) {
		this.montRentAmt = montRentAmt;
	}
	public String getClientNm() {
		return clientNm;
	}
	public void setClientNm(String clientNm) {
		this.clientNm = clientNm;
	}
	public String getCompNm() {
		return compNm;
	}
	public void setCompNm(String compNm) {
		this.compNm = compNm;
	}
	public String getSuggstSectrCd() {
		return suggstSectrCd;
	}
	public void setSuggstSectrCd(String suggstSectrCd) {
		this.suggstSectrCd = suggstSectrCd;
	}
	public String getWishFlrTypCd() {
		return wishFlrTypCd;
	}
	public void setWishFlrTypCd(String wishFlrTypCd) {
		this.wishFlrTypCd = wishFlrTypCd;
	}
	public String getIntrrYn() {
		return intrrYn;
	}
	public void setIntrrYn(String intrrYn) {
		this.intrrYn = intrrYn;
	}
	public String getReqDscr() {
		return reqDscr;
	}
	public void setReqDscr(String reqDscr) {
		this.reqDscr = reqDscr;
	}
	public long getPrptSuggstReqSeqno() {
		return prptSuggstReqSeqno;
	}
	public void setPrptSuggstReqSeqno(long prptSuggstReqSeqno) {
		this.prptSuggstReqSeqno = prptSuggstReqSeqno;
	}
	public String getRegionNm() {
		return regionNm;
	}
	public void setRegionNm(String regionNm) {
		this.regionNm = regionNm;
	}
	public JSONArray getSuggstRegionItems() {
		return suggstRegionItems;
	}
	public void setSuggstRegionItems(JSONArray suggstRegionItems) {
		this.suggstRegionItems = suggstRegionItems;
	}
	public long getSuggstMemSeqno() {
		return suggstMemSeqno;
	}
	public void setSuggstMemSeqno(long suggstMemSeqno) {
		this.suggstMemSeqno = suggstMemSeqno;
	}
	public String getSuggstMemTypCd() {
		return suggstMemTypCd;
	}
	public void setSuggstMemTypCd(String suggstMemTypCd) {
		this.suggstMemTypCd = suggstMemTypCd;
	}
	public String getSuggstrNm() {
		return suggstrNm;
	}
	public void setSuggstrNm(String suggstrNm) {
		this.suggstrNm = suggstrNm;
	}
	public String getEstateTypNm() {
		return estateTypNm;
	}
	public void setEstateTypNm(String estateTypNm) {
		this.estateTypNm = estateTypNm;
	}
	public String getTranTypGbNm() {
		return tranTypGbNm;
	}
	public void setTranTypGbNm(String tranTypGbNm) {
		this.tranTypGbNm = tranTypGbNm;
	}
	public String getPsblMovDayTypNm() {
		return psblMovDayTypNm;
	}
	public void setPsblMovDayTypNm(String psblMovDayTypNm) {
		this.psblMovDayTypNm = psblMovDayTypNm;
	}
	public int getSuggstPrptCnt() {
		return suggstPrptCnt;
	}
	public void setSuggstPrptCnt(int suggstPrptCnt) {
		this.suggstPrptCnt = suggstPrptCnt;
	}
	public String getSuggstDt() {
		return suggstDt;
	}
	public void setSuggstDt(String suggstDt) {
		this.suggstDt = suggstDt;
	}
	public String getSectrGbNm() {
		return sectrGbNm;
	}
	public void setSectrGbNm(String sectrGbNm) {
		this.sectrGbNm = sectrGbNm;
	}
	public String getMemTypCd() {
		return memTypCd;
	}
	public void setMemTypCd(String memTypCd) {
		this.memTypCd = memTypCd;
	}
	public String getMemTypNm() {
		return memTypNm;
	}
	public void setMemTypNm(String memTypNm) {
		this.memTypNm = memTypNm;
	}
	public String getSuggstMemTypNm() {
		return suggstMemTypNm;
	}
	public void setSuggstMemTypNm(String suggstMemTypNm) {
		this.suggstMemTypNm = suggstMemTypNm;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	public int getPrptSuggstCnt() {
		return prptSuggstCnt;
	}
	public void setPrptSuggstCnt(int prptSuggstCnt) {
		this.prptSuggstCnt = prptSuggstCnt;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public JSONArray getEstateTypCdItems() {
		return estateTypCdItems;
	}
	public void setEstateTypCdItems(JSONArray estateTypCdItems) {
		this.estateTypCdItems = estateTypCdItems;
	}
	public String getRecentDt() {
		return recentDt;
	}
	public void setRecentDt(String recentDt) {
		this.recentDt = recentDt;
	}
}