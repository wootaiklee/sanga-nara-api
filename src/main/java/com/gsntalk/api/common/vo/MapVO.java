package com.gsntalk.api.common.vo;

import java.util.List;

public class MapVO {
	
	private long memSeqno;
	
	private long estBrkMemOfcSeqno;
	private long prptSeqno;
	private long knwldgIndCmplxSeqno;
	private long estBrkOfcSeqno;
	private double swLat;
	private double swLng;
	private double neLat;
	private double neLng;
	
	private String estateTypGbCd;
	private String estateTypGbNm;
	private String estateTypCd;
	private String estateTypNm;
	private String tranTypGbYn;
	private List<String> tranTypGbCdList;
	private String dealAmtYn;
	private long dealMinAmt;
	private long dealMaxAmt;
	private String dpstAmtYn;
	private long dpstMinAmt;
	private long dpstMaxAmt;
	private String monRentAmtYn;
	private long monRentMinAmt;
	private long monRentMaxAmt;
	private String splyAreaYn;
	private double minSplyArea;
	private double maxSplyArea;
	private String prvAreaYn;
	private double minPrvArea;
	private double maxPrvArea;
	private String lndAreaYn;
	private double minLndArea;
	private double maxLndArea;
	private String totFlrAreaYn;
	private double minTotFlrArea;
	private double maxTotFlrArea;
	private String monMntnceCostYn;
	private int monMntnceMinCost;
	private int monMntnceMaxCost;
	private String useCnfrmYearSrchTypCd;
	private String loanGbCd;
	private String prmmAmtYn;
	private long minPrmmAmt;
	private long maxPrmmAmt;
	private String sectrGbYn;
	private List<String>  sectrGbCdList;
	private String flrHghtTypGbCd;
	private String elctrPwrTypGbCd;
	private String sortItem;
	private String sortTyp;
	
	private String tranTypGbCd;
	private String tranTypGbNm;
	private double splyArea;
	private double prvArea;
	private double lndArea;
	private double totFlrArea;
	private double bldArea;
	private int flr;
	private int allFlr;
	private int minFlr;
	private int maxFlr;
	private String smplSmrDscr;
	private double lat;
	private double lng;
	private String addr;
	private String addrShortNm;
	private String prflImgUrl;
	private String reprImgUrl;
	private String favYn;
	private String bldNm;
	private String ofcNm;
	
	private long askSalesMinPrc;
	private long askSalesAvgPrc;
	private long askSalesMaxPrc;
	private long askLeaseMinPrc;
	private long askLeaseAvgPrc;
	private long askLeaseMaxPrc;
	
	private long cost;
	private long montRentAmt;
	private String dealStatGbCd;
	
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
	}
	public long getKnwldgIndCmplxSeqno() {
		return knwldgIndCmplxSeqno;
	}
	public void setKnwldgIndCmplxSeqno(long knwldgIndCmplxSeqno) {
		this.knwldgIndCmplxSeqno = knwldgIndCmplxSeqno;
	}
	public long getEstBrkOfcSeqno() {
		return estBrkOfcSeqno;
	}
	public void setEstBrkOfcSeqno(long estBrkOfcSeqno) {
		this.estBrkOfcSeqno = estBrkOfcSeqno;
	}
	public double getSwLat() {
		return swLat;
	}
	public void setSwLat(double swLat) {
		this.swLat = swLat;
	}
	public double getSwLng() {
		return swLng;
	}
	public void setSwLng(double swLng) {
		this.swLng = swLng;
	}
	public double getNeLat() {
		return neLat;
	}
	public void setNeLat(double neLat) {
		this.neLat = neLat;
	}
	public double getNeLng() {
		return neLng;
	}
	public void setNeLng(double neLng) {
		this.neLng = neLng;
	}
	public String getEstateTypGbCd() {
		return estateTypGbCd;
	}
	public void setEstateTypGbCd(String estateTypGbCd) {
		this.estateTypGbCd = estateTypGbCd;
	}
	public String getEstateTypCd() {
		return estateTypCd;
	}
	public void setEstateTypCd(String estateTypCd) {
		this.estateTypCd = estateTypCd;
	}
	public List<String> getTranTypGbCdList() {
		return tranTypGbCdList;
	}
	public void setTranTypGbCdList(List<String> tranTypGbCdList) {
		this.tranTypGbCdList = tranTypGbCdList;
	}
	public long getDealMinAmt() {
		return dealMinAmt;
	}
	public void setDealMinAmt(long dealMinAmt) {
		this.dealMinAmt = dealMinAmt;
	}
	public long getDealMaxAmt() {
		return dealMaxAmt;
	}
	public void setDealMaxAmt(long dealMaxAmt) {
		this.dealMaxAmt = dealMaxAmt;
	}
	public long getDpstMinAmt() {
		return dpstMinAmt;
	}
	public void setDpstMinAmt(long dpstMinAmt) {
		this.dpstMinAmt = dpstMinAmt;
	}
	public long getDpstMaxAmt() {
		return dpstMaxAmt;
	}
	public void setDpstMaxAmt(long dpstMaxAmt) {
		this.dpstMaxAmt = dpstMaxAmt;
	}
	public long getMonRentMinAmt() {
		return monRentMinAmt;
	}
	public void setMonRentMinAmt(long monRentMinAmt) {
		this.monRentMinAmt = monRentMinAmt;
	}
	public long getMonRentMaxAmt() {
		return monRentMaxAmt;
	}
	public void setMonRentMaxAmt(long monRentMaxAmt) {
		this.monRentMaxAmt = monRentMaxAmt;
	}
	public double getMinSplyArea() {
		return minSplyArea;
	}
	public void setMinSplyArea(double minSplyArea) {
		this.minSplyArea = minSplyArea;
	}
	public double getMaxSplyArea() {
		return maxSplyArea;
	}
	public void setMaxSplyArea(double maxSplyArea) {
		this.maxSplyArea = maxSplyArea;
	}
	public double getMinPrvArea() {
		return minPrvArea;
	}
	public void setMinPrvArea(double minPrvArea) {
		this.minPrvArea = minPrvArea;
	}
	public double getMaxPrvArea() {
		return maxPrvArea;
	}
	public void setMaxPrvArea(double maxPrvArea) {
		this.maxPrvArea = maxPrvArea;
	}
	public double getMinLndArea() {
		return minLndArea;
	}
	public void setMinLndArea(double minLndArea) {
		this.minLndArea = minLndArea;
	}
	public double getMaxLndArea() {
		return maxLndArea;
	}
	public void setMaxLndArea(double maxLndArea) {
		this.maxLndArea = maxLndArea;
	}
	public double getMinTotFlrArea() {
		return minTotFlrArea;
	}
	public void setMinTotFlrArea(double minTotFlrArea) {
		this.minTotFlrArea = minTotFlrArea;
	}
	public double getMaxTotFlrArea() {
		return maxTotFlrArea;
	}
	public void setMaxTotFlrArea(double maxTotFlrArea) {
		this.maxTotFlrArea = maxTotFlrArea;
	}
	public int getMonMntnceMinCost() {
		return monMntnceMinCost;
	}
	public void setMonMntnceMinCost(int monMntnceMinCost) {
		this.monMntnceMinCost = monMntnceMinCost;
	}
	public int getMonMntnceMaxCost() {
		return monMntnceMaxCost;
	}
	public void setMonMntnceMaxCost(int monMntnceMaxCost) {
		this.monMntnceMaxCost = monMntnceMaxCost;
	}
	public String getUseCnfrmYearSrchTypCd() {
		return useCnfrmYearSrchTypCd;
	}
	public void setUseCnfrmYearSrchTypCd(String useCnfrmYearSrchTypCd) {
		this.useCnfrmYearSrchTypCd = useCnfrmYearSrchTypCd;
	}
	public String getLoanGbCd() {
		return loanGbCd;
	}
	public void setLoanGbCd(String loanGbCd) {
		this.loanGbCd = loanGbCd;
	}
	public long getMinPrmmAmt() {
		return minPrmmAmt;
	}
	public void setMinPrmmAmt(long minPrmmAmt) {
		this.minPrmmAmt = minPrmmAmt;
	}
	public long getMaxPrmmAmt() {
		return maxPrmmAmt;
	}
	public void setMaxPrmmAmt(long maxPrmmAmt) {
		this.maxPrmmAmt = maxPrmmAmt;
	}
	public String getTranTypGbYn() {
		return tranTypGbYn;
	}
	public void setTranTypGbYn(String tranTypGbYn) {
		this.tranTypGbYn = tranTypGbYn;
	}
	public String getSectrGbYn() {
		return sectrGbYn;
	}
	public void setSectrGbYn(String sectrGbYn) {
		this.sectrGbYn = sectrGbYn;
	}
	public List<String> getSectrGbCdList() {
		return sectrGbCdList;
	}
	public void setSectrGbCdList(List<String> sectrGbCdList) {
		this.sectrGbCdList = sectrGbCdList;
	}
	public String getFlrHghtTypGbCd() {
		return flrHghtTypGbCd;
	}
	public void setFlrHghtTypGbCd(String flrHghtTypGbCd) {
		this.flrHghtTypGbCd = flrHghtTypGbCd;
	}
	public String getElctrPwrTypGbCd() {
		return elctrPwrTypGbCd;
	}
	public void setElctrPwrTypGbCd(String elctrPwrTypGbCd) {
		this.elctrPwrTypGbCd = elctrPwrTypGbCd;
	}
	public String getSortItem() {
		return sortItem;
	}
	public void setSortItem(String sortItem) {
		this.sortItem = sortItem;
	}
	public String getSortTyp() {
		return sortTyp;
	}
	public void setSortTyp(String sortTyp) {
		this.sortTyp = sortTyp;
	}
	public String getTranTypGbCd() {
		return tranTypGbCd;
	}
	public void setTranTypGbCd(String tranTypGbCd) {
		this.tranTypGbCd = tranTypGbCd;
	}
	public String getTranTypGbNm() {
		return tranTypGbNm;
	}
	public void setTranTypGbNm(String tranTypGbNm) {
		this.tranTypGbNm = tranTypGbNm;
	}
	public double getSplyArea() {
		return splyArea;
	}
	public void setSplyArea(double splyArea) {
		this.splyArea = splyArea;
	}
	public double getPrvArea() {
		return prvArea;
	}
	public void setPrvArea(double prvArea) {
		this.prvArea = prvArea;
	}
	public double getLndArea() {
		return lndArea;
	}
	public void setLndArea(double lndArea) {
		this.lndArea = lndArea;
	}
	public double getTotFlrArea() {
		return totFlrArea;
	}
	public void setTotFlrArea(double totFlrArea) {
		this.totFlrArea = totFlrArea;
	}
	public double getBldArea() {
		return bldArea;
	}
	public void setBldArea(double bldArea) {
		this.bldArea = bldArea;
	}
	public int getFlr() {
		return flr;
	}
	public void setFlr(int flr) {
		this.flr = flr;
	}
	public int getAllFlr() {
		return allFlr;
	}
	public void setAllFlr(int allFlr) {
		this.allFlr = allFlr;
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
	public String getPrflImgUrl() {
		return prflImgUrl;
	}
	public void setPrflImgUrl(String prflImgUrl) {
		this.prflImgUrl = prflImgUrl;
	}
	public String getReprImgUrl() {
		return reprImgUrl;
	}
	public void setReprImgUrl(String reprImgUrl) {
		this.reprImgUrl = reprImgUrl;
	}
	public String getFavYn() {
		return favYn;
	}
	public void setFavYn(String favYn) {
		this.favYn = favYn;
	}
	public String getBldNm() {
		return bldNm;
	}
	public void setBldNm(String bldNm) {
		this.bldNm = bldNm;
	}
	public String getOfcNm() {
		return ofcNm;
	}
	public void setOfcNm(String ofcNm) {
		this.ofcNm = ofcNm;
	}
	public String getEstateTypGbNm() {
		return estateTypGbNm;
	}
	public void setEstateTypGbNm(String estateTypGbNm) {
		this.estateTypGbNm = estateTypGbNm;
	}
	public String getEstateTypNm() {
		return estateTypNm;
	}
	public void setEstateTypNm(String estateTypNm) {
		this.estateTypNm = estateTypNm;
	}
	public String getDealAmtYn() {
		return dealAmtYn;
	}
	public void setDealAmtYn(String dealAmtYn) {
		this.dealAmtYn = dealAmtYn;
	}
	public String getDpstAmtYn() {
		return dpstAmtYn;
	}
	public void setDpstAmtYn(String dpstAmtYn) {
		this.dpstAmtYn = dpstAmtYn;
	}
	public String getMonRentAmtYn() {
		return monRentAmtYn;
	}
	public void setMonRentAmtYn(String monRentAmtYn) {
		this.monRentAmtYn = monRentAmtYn;
	}
	public String getSplyAreaYn() {
		return splyAreaYn;
	}
	public void setSplyAreaYn(String splyAreaYn) {
		this.splyAreaYn = splyAreaYn;
	}
	public String getPrvAreaYn() {
		return prvAreaYn;
	}
	public void setPrvAreaYn(String prvAreaYn) {
		this.prvAreaYn = prvAreaYn;
	}
	public String getLndAreaYn() {
		return lndAreaYn;
	}
	public void setLndAreaYn(String lndAreaYn) {
		this.lndAreaYn = lndAreaYn;
	}
	public String getTotFlrAreaYn() {
		return totFlrAreaYn;
	}
	public void setTotFlrAreaYn(String totFlrAreaYn) {
		this.totFlrAreaYn = totFlrAreaYn;
	}
	public String getMonMntnceCostYn() {
		return monMntnceCostYn;
	}
	public void setMonMntnceCostYn(String monMntnceCostYn) {
		this.monMntnceCostYn = monMntnceCostYn;
	}
	public String getPrmmAmtYn() {
		return prmmAmtYn;
	}
	public void setPrmmAmtYn(String prmmAmtYn) {
		this.prmmAmtYn = prmmAmtYn;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	public long getEstBrkMemOfcSeqno() {
		return estBrkMemOfcSeqno;
	}
	public void setEstBrkMemOfcSeqno(long estBrkMemOfcSeqno) {
		this.estBrkMemOfcSeqno = estBrkMemOfcSeqno;
	}
	public long getMontRentAmt() {
		return montRentAmt;
	}
	public void setMontRentAmt(long montRentAmt) {
		this.montRentAmt = montRentAmt;
	}
	public String getDealStatGbCd() {
		return dealStatGbCd;
	}
	public void setDealStatGbCd(String dealStatGbCd) {
		this.dealStatGbCd = dealStatGbCd;
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
}