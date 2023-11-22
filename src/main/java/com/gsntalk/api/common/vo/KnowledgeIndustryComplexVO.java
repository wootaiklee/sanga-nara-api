package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class KnowledgeIndustryComplexVO extends PagingVO {
	
	private long memSeqno;
	
	private long knwldgIndCmplxSeqno;
	private String prptRegNo;
	private String addr;
	private String addrShortNm;
	private double lat;
	private double lng;
	private String bldNm;
	private String cmpltnDate;
	private long askSalesMinPrc;
	private long askSalesAvgPrc;
	private long askSalesMaxPrc;
	private long askLeaseMinPrc;
	private long askLeaseAvgPrc;
	private long askLeaseMaxPrc;
	private double lndArea;
	private double bldArea;
	private double totFlrArea;
	private int minFlr;
	private int maxFlr;
	private int parkingCarCnt;
	private int husHoldCnt;
	private String devCompNm;
	private String constCompNm;
	private String trfcInfo;
	private String siteExplntn;
	private String smplSmrDscr;
	private String vwmapImgUrl;
	private String orgFileNm;
	private String frmapYn;
	
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getBldNm() {
		return bldNm;
	}
	public void setBldNm(String bldNm) {
		this.bldNm = bldNm;
	}
	public String getCmpltnDate() {
		return cmpltnDate;
	}
	public void setCmpltnDate(String cmpltnDate) {
		this.cmpltnDate = cmpltnDate;
	}
	public long getAskSalesMinPrc() {
		return askSalesMinPrc;
	}
	public void setAskSalesMinPrc(long askSalesMinPrc) {
		this.askSalesMinPrc = askSalesMinPrc;
	}
	public long getAskSalesAvgPrc() {
		return askSalesAvgPrc;
	}
	public void setAskSalesAvgPrc(long askSalesAvgPrc) {
		this.askSalesAvgPrc = askSalesAvgPrc;
	}
	public long getAskSalesMaxPrc() {
		return askSalesMaxPrc;
	}
	public void setAskSalesMaxPrc(long askSalesMaxPrc) {
		this.askSalesMaxPrc = askSalesMaxPrc;
	}
	public long getAskLeaseMinPrc() {
		return askLeaseMinPrc;
	}
	public void setAskLeaseMinPrc(long askLeaseMinPrc) {
		this.askLeaseMinPrc = askLeaseMinPrc;
	}
	public long getAskLeaseAvgPrc() {
		return askLeaseAvgPrc;
	}
	public void setAskLeaseAvgPrc(long askLeaseAvgPrc) {
		this.askLeaseAvgPrc = askLeaseAvgPrc;
	}
	public long getAskLeaseMaxPrc() {
		return askLeaseMaxPrc;
	}
	public void setAskLeaseMaxPrc(long askLeaseMaxPrc) {
		this.askLeaseMaxPrc = askLeaseMaxPrc;
	}
	public double getLndArea() {
		return lndArea;
	}
	public void setLndArea(double lndArea) {
		this.lndArea = lndArea;
	}
	public double getBldArea() {
		return bldArea;
	}
	public void setBldArea(double bldArea) {
		this.bldArea = bldArea;
	}
	public double getTotFlrArea() {
		return totFlrArea;
	}
	public void setTotFlrArea(double totFlrArea) {
		this.totFlrArea = totFlrArea;
	}
	public int getMinFlr() {
		return minFlr;
	}
	public void setMinFlr(int minFlr) {
		this.minFlr = minFlr;
	}
	public int getMaxFlr() {
		return maxFlr;
	}
	public void setMaxFlr(int maxFlr) {
		this.maxFlr = maxFlr;
	}
	public int getParkingCarCnt() {
		return parkingCarCnt;
	}
	public void setParkingCarCnt(int parkingCarCnt) {
		this.parkingCarCnt = parkingCarCnt;
	}
	public int getHusHoldCnt() {
		return husHoldCnt;
	}
	public void setHusHoldCnt(int husHoldCnt) {
		this.husHoldCnt = husHoldCnt;
	}
	public String getDevCompNm() {
		return devCompNm;
	}
	public void setDevCompNm(String devCompNm) {
		this.devCompNm = devCompNm;
	}
	public String getConstCompNm() {
		return constCompNm;
	}
	public void setConstCompNm(String constCompNm) {
		this.constCompNm = constCompNm;
	}
	public String getTrfcInfo() {
		return trfcInfo;
	}
	public void setTrfcInfo(String trfcInfo) {
		this.trfcInfo = trfcInfo;
	}
	public String getSiteExplntn() {
		return siteExplntn;
	}
	public void setSiteExplntn(String siteExplntn) {
		this.siteExplntn = siteExplntn;
	}
	public String getSmplSmrDscr() {
		return smplSmrDscr;
	}
	public void setSmplSmrDscr(String smplSmrDscr) {
		this.smplSmrDscr = smplSmrDscr;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getPrptRegNo() {
		return prptRegNo;
	}
	public void setPrptRegNo(String prptRegNo) {
		this.prptRegNo = prptRegNo;
	}
	public String getAddrShortNm() {
		return addrShortNm;
	}
	public void setAddrShortNm(String addrShortNm) {
		this.addrShortNm = addrShortNm;
	}
	public long getKnwldgIndCmplxSeqno() {
		return knwldgIndCmplxSeqno;
	}
	public void setKnwldgIndCmplxSeqno(long knwldgIndCmplxSeqno) {
		this.knwldgIndCmplxSeqno = knwldgIndCmplxSeqno;
	}
	public String getVwmapImgUrl() {
		return vwmapImgUrl;
	}
	public void setVwmapImgUrl(String vwmapImgUrl) {
		this.vwmapImgUrl = vwmapImgUrl;
	}
	public String getOrgFileNm() {
		return orgFileNm;
	}
	public void setOrgFileNm(String orgFileNm) {
		this.orgFileNm = orgFileNm;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getFrmapYn() {
		return frmapYn;
	}
	public void setFrmapYn(String frmapYn) {
		this.frmapYn = frmapYn;
	}
}