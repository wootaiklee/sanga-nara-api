package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class SuggestionSalesVO extends PagingVO {

	private long suggstnSalesSeqno;
	private long memSeqno;
	private String suggstnSalesRegionGbCd;
	private String suggstnSalesRegionGbNm;
	private String addr;
	private String addrShortNm;
	private String bldNm;
	private double lat;
	private double lng;
	private String suggstnSalesTtl;
	private String salesDtlDscr;
	private int minFlr;
	private int maxFlr;
	private int totBldCnt;
	private int parkingCarCnt;
	private int husHoldCnt;
	private double lndArea;
	private double bldArea;
	private double totFlrArea;
	private double flrAreaRatio;
	private double bldToLndRatio;
	private String cmpltnDate;
	private String expctMovMonth;
	private String devCompNm;
	private String constCompNm;
	private String matterPortLinkUrl;
	private String repImgSaveFileNm;
	private String repImgUrl;
	private String poStatGbCd;
	private String regDate;
	
	private String saveFileNm;
	private String fileUrl;
	private String favYn;

	private long dongSeqno;
	private long usageByDongSeqno;
	private long suggstnSalesFlrSeqno;
	private String dongNm;
	private String flrUsageGbCd;
	private String flrUsageGbNm;
	private int stFlr;
	private int edFlr;
	private String flrPlanSaveFileDelYn;
	private String flrPlanSaveFileNm;
	private String flrPlanFileUrl;
	
	private String prmmTtl;
	private String prmmDscr;
	private String schdlNm;
	private String schdlStDate;
	private String schdlEdDate;
	private int dday;
	
	public long getSuggstnSalesSeqno() {
		return suggstnSalesSeqno;
	}
	public void setSuggstnSalesSeqno(long suggstnSalesSeqno) {
		this.suggstnSalesSeqno = suggstnSalesSeqno;
	}
	public int getDday() {
		return dday;
	}
	public void setDday(int dday) {
		this.dday = dday;
	}
	public String getFavYn() {
		return favYn;
	}
	public void setFavYn(String favYn) {
		this.favYn = favYn;
	}
	public String getFlrPlanSaveFileDelYn() {
		return flrPlanSaveFileDelYn;
	}
	public void setFlrPlanSaveFileDelYn(String flrPlanSaveFileDelYn) {
		this.flrPlanSaveFileDelYn = flrPlanSaveFileDelYn;
	}
	public long getSuggstnSalesFlrSeqno() {
		return suggstnSalesFlrSeqno;
	}
	public void setSuggstnSalesFlrSeqno(long suggstnSalesFlrSeqno) {
		this.suggstnSalesFlrSeqno = suggstnSalesFlrSeqno;
	}
	public String getPoStatGbCd() {
		return poStatGbCd;
	}
	public void setPoStatGbCd(String poStatGbCd) {
		this.poStatGbCd = poStatGbCd;
	}
	public String getSchdlNm() {
		return schdlNm;
	}
	public void setSchdlNm(String schdlNm) {
		this.schdlNm = schdlNm;
	}
	public String getSchdlStDate() {
		return schdlStDate;
	}
	public void setSchdlStDate(String schdlStDate) {
		this.schdlStDate = schdlStDate;
	}
	public String getSchdlEdDate() {
		return schdlEdDate;
	}
	public void setSchdlEdDate(String schdlEdDate) {
		this.schdlEdDate = schdlEdDate;
	}
	public String getPrmmTtl() {
		return prmmTtl;
	}
	public void setPrmmTtl(String prmmTtl) {
		this.prmmTtl = prmmTtl;
	}
	public String getPrmmDscr() {
		return prmmDscr;
	}
	public void setPrmmDscr(String prmmDscr) {
		this.prmmDscr = prmmDscr;
	}
	public int getStFlr() {
		return stFlr;
	}
	public void setStFlr(int stFlr) {
		this.stFlr = stFlr;
	}
	public int getEdFlr() {
		return edFlr;
	}
	public void setEdFlr(int edFlr) {
		this.edFlr = edFlr;
	}
	public String getFlrPlanSaveFileNm() {
		return flrPlanSaveFileNm;
	}
	public void setFlrPlanSaveFileNm(String flrPlanSaveFileNm) {
		this.flrPlanSaveFileNm = flrPlanSaveFileNm;
	}
	public String getFlrPlanFileUrl() {
		return flrPlanFileUrl;
	}
	public void setFlrPlanFileUrl(String flrPlanFileUrl) {
		this.flrPlanFileUrl = flrPlanFileUrl;
	}
	public long getUsageByDongSeqno() {
		return usageByDongSeqno;
	}
	public void setUsageByDongSeqno(long usageByDongSeqno) {
		this.usageByDongSeqno = usageByDongSeqno;
	}
	public String getFlrUsageGbCd() {
		return flrUsageGbCd;
	}
	public void setFlrUsageGbCd(String flrUsageGbCd) {
		this.flrUsageGbCd = flrUsageGbCd;
	}
	public long getDongSeqno() {
		return dongSeqno;
	}
	public void setDongSeqno(long dongSeqno) {
		this.dongSeqno = dongSeqno;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getSuggstnSalesRegionGbCd() {
		return suggstnSalesRegionGbCd;
	}
	public void setSuggstnSalesRegionGbCd(String suggstnSalesRegionGbCd) {
		this.suggstnSalesRegionGbCd = suggstnSalesRegionGbCd;
	}
	public String getSuggstnSalesRegionGbNm() {
		return suggstnSalesRegionGbNm;
	}
	public void setSuggstnSalesRegionGbNm(String suggstnSalesRegionGbNm) {
		this.suggstnSalesRegionGbNm = suggstnSalesRegionGbNm;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getAddrShortNm() {
		return addrShortNm;
	}
	public void setAddrShortNm(String addrShortNm) {
		this.addrShortNm = addrShortNm;
	}
	public String getBldNm() {
		return bldNm;
	}
	public void setBldNm(String bldNm) {
		this.bldNm = bldNm;
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
	public String getSuggstnSalesTtl() {
		return suggstnSalesTtl;
	}
	public void setSuggstnSalesTtl(String suggstnSalesTtl) {
		this.suggstnSalesTtl = suggstnSalesTtl;
	}
	public String getSalesDtlDscr() {
		return salesDtlDscr;
	}
	public void setSalesDtlDscr(String salesDtlDscr) {
		this.salesDtlDscr = salesDtlDscr;
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
	public int getTotBldCnt() {
		return totBldCnt;
	}
	public void setTotBldCnt(int totBldCnt) {
		this.totBldCnt = totBldCnt;
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
	public double getFlrAreaRatio() {
		return flrAreaRatio;
	}
	public void setFlrAreaRatio(double flrAreaRatio) {
		this.flrAreaRatio = flrAreaRatio;
	}
	public double getBldToLndRatio() {
		return bldToLndRatio;
	}
	public void setBldToLndRatio(double bldToLndRatio) {
		this.bldToLndRatio = bldToLndRatio;
	}
	public String getCmpltnDate() {
		return cmpltnDate;
	}
	public void setCmpltnDate(String cmpltnDate) {
		this.cmpltnDate = cmpltnDate;
	}
	public String getExpctMovMonth() {
		return expctMovMonth;
	}
	public void setExpctMovMonth(String expctMovMonth) {
		this.expctMovMonth = expctMovMonth;
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
	public String getMatterPortLinkUrl() {
		return matterPortLinkUrl;
	}
	public void setMatterPortLinkUrl(String matterPortLinkUrl) {
		this.matterPortLinkUrl = matterPortLinkUrl;
	}
	public String getRepImgSaveFileNm() {
		return repImgSaveFileNm;
	}
	public void setRepImgSaveFileNm(String repImgSaveFileNm) {
		this.repImgSaveFileNm = repImgSaveFileNm;
	}
	public String getRepImgUrl() {
		return repImgUrl;
	}
	public void setRepImgUrl(String repImgUrl) {
		this.repImgUrl = repImgUrl;
	}
	public String getSaveFileNm() {
		return saveFileNm;
	}
	public void setSaveFileNm(String saveFileNm) {
		this.saveFileNm = saveFileNm;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getDongNm() {
		return dongNm;
	}
	public void setDongNm(String dongNm) {
		this.dongNm = dongNm;
	}
	public String getRegDate() {
		return regDate;
	}
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getFlrUsageGbNm() {
		return flrUsageGbNm;
	}
	public void setFlrUsageGbNm(String flrUsageGbNm) {
		this.flrUsageGbNm = flrUsageGbNm;
	}
}