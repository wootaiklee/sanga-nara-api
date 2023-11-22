package com.gsntalk.api.common.vo;

import org.json.simple.JSONArray;

import com.gsntalk.api.common.extend.PagingVO;

public class PropertyVO extends PagingVO {
	
	private long memSeqno;
	private long estBrkMemOfcSeqno;
	private String ofcNm;
	private long prptSeqno;
	private String prptNo;
	private String prptRegNo;
	private long exceptPrptSeqno;
	
	private long cost;
	private String costNm;
	private String estateTypGbCd;
	private String estateTypGbNm;
	private String estateTypCd;
	private String estateTypNm;
	private String addr;
	private String dtlAddr;
	private String addrShortNm;
	private String unregistYn;
	private double lat;
	private double lng;
	private int allFlr;
	private int flr;
	private int minFlr;
	private int maxFlr;
	private double splyArea;
	private double prvArea;
	private double lndArea;
	private double totFlrArea;
	private String useCnfrmYear;
	private String useCnfrmDate;
	private String bldUsageGbCd;
	private String bldUsageGbNm;
	private String suggstnBldUsageGbCd;
	private String suggstnBldUsageGbNm;
	private String psblMovDayTypCd;
	private String psblMovDate;
	private int monMntnceCost;
	private String loanGbCd;
	private String loanGbNm;
	private long loanAmt;
	private String parkingPsblYn;
	private int parkingCost;
	private String tranTypGbCd;
	private String tranTypGbNm;
	private long dealAmt;
	private String keyMonExstsYn;
	private long keyMonAmt;
	private long prmmAmt;
	private String dealAmtDiscsnPsblYn;
	private String existngLeaseExstsYn;
	private long dpstAmt;
	private long crntDpstAmt;
	private int crntMontRentAmt;
	private int roomCnt;
	private int bathRoomCnt;
	private String bultInYn;
	private String intrrYn;
	private String bldDirctnGbCd;
	private String bldDirctnGbNm;
	private String flrHghtTypGbCd;
	private String flrHghtTypGbNm;
	private String heatKindGbCd;
	private String heatKindGbNm;
	private String elctrPwrTypGbCd;
	private String elctrPwrTypGbNm;
	private String elvFcltExstsYn;
	private String frhgtElvExstsYn;
	private String intnlStrctrTypCd;
	private String intnlStrctrTypNm;
	private long regMemSeqno;
	private int montRentAmt;
	private String dispAreaNm;
	private double dispArea;
	private String metterPortLinkUrl;
	private String mntnceItemDscr;
	private String movDateDiscsnPsblYn;
	private String crntSectrGbNm;
	private String suggstnSectrGbNm;
	private double wghtPerPy;
	private String cmpltExpctDate;
	private String mapDispYn;
	private String tmpAddrYn;
	private String lndCrntUsageGbCd;
	private String lndCrntUsageGbNm;
	private String crntSectrGbCd;
	private String suggstnSectrGbCd;
	private String frghtElvExstsYn;
	private String dockExstsYn;
	private String hoistExstsYn;
	private String movInReprtPsblYn;
	private String cityPlanYn;
	private String bldCnfrmIssueYn;
	private String lndDealCnfrmApplYn;
	private String entrnceRoadExstsYn;
	private String optionExstsYn;
	
	private String mntnceCostTypCd;
	private String smplSmrDscr;
	private String dtlExplntnDscr;
	private String matterPortLinkUrl;
	private String prvtMemoDscr;
	private String reprImgUrl;
	private String favYn;
	private String sortItem;
	private String sortTyp;
	private String dealStatGbCd;
	private String dealStatGbNm;
	private String regExprYn;
	private String regStatGbCd;
	private String treatStatGbCd;
	private String memName;
	private String regDt;
	private String adminRegDt;
	
	private String adminCnfrmYn;
	private String deniedExprDateYn;
	private String exprYn;
	private String fromAdminYn;
	
	private JSONArray regiEstTypCdItems;
	private JSONArray commEstTypCdItems;
	private JSONArray preEstTypCdItems;
	private JSONArray dealStatGbCdItems;
	private JSONArray tranTypGbCdItems;
	private String prptStatGbCd;
	private String prptStatGbNm;
	private int tourReqCnt;
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public String getDealStatGbCd() {
		return dealStatGbCd;
	}
	public int getTourReqCnt() {
		return tourReqCnt;
	}
	public void setTourReqCnt(int tourReqCnt) {
		this.tourReqCnt = tourReqCnt;
	}
	public String getPrptStatGbCd() {
		return prptStatGbCd;
	}
	public void setPrptStatGbCd(String prptStatGbCd) {
		this.prptStatGbCd = prptStatGbCd;
	}
	public String getPrptStatGbNm() {
		return prptStatGbNm;
	}
	public void setPrptStatGbNm(String prptStatGbNm) {
		this.prptStatGbNm = prptStatGbNm;
	}
	public JSONArray getDealStatGbCdItems() {
		return dealStatGbCdItems;
	}
	public void setDealStatGbCdItems(JSONArray dealStatGbCdItems) {
		this.dealStatGbCdItems = dealStatGbCdItems;
	}
	public JSONArray getTranTypGbCdItems() {
		return tranTypGbCdItems;
	}
	public void setTranTypGbCdItems(JSONArray tranTypGbCdItems) {
		this.tranTypGbCdItems = tranTypGbCdItems;
	}
	public JSONArray getRegiEstTypCdItems() {
		return regiEstTypCdItems;
	}
	public void setRegiEstTypCdItems(JSONArray regiEstTypCdItems) {
		this.regiEstTypCdItems = regiEstTypCdItems;
	}
	public JSONArray getCommEstTypCdItems() {
		return commEstTypCdItems;
	}
	public void setCommEstTypCdItems(JSONArray commEstTypCdItems) {
		this.commEstTypCdItems = commEstTypCdItems;
	}
	public JSONArray getPreEstTypCdItems() {
		return preEstTypCdItems;
	}
	public void setPreEstTypCdItems(JSONArray preEstTypCdItems) {
		this.preEstTypCdItems = preEstTypCdItems;
	}
	public String getAdminCnfrmYn() {
		return adminCnfrmYn;
	}
	public void setAdminCnfrmYn(String adminCnfrmYn) {
		this.adminCnfrmYn = adminCnfrmYn;
	}
	public String getExprYn() {
		return exprYn;
	}
	public void setExprYn(String exprYn) {
		this.exprYn = exprYn;
	}
	public void setDealStatGbCd(String dealStatGbCd) {
		this.dealStatGbCd = dealStatGbCd;
	}
	public String getDeniedExprDateYn() {
		return deniedExprDateYn;
	}
	public void setDeniedExprDateYn(String deniedExprDateYn) {
		this.deniedExprDateYn = deniedExprDateYn;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getAdminRegDt() {
		return adminRegDt;
	}
	public void setAdminRegDt(String adminRegDt) {
		this.adminRegDt = adminRegDt;
	}
	public long getEstBrkMemOfcSeqno() {
		return estBrkMemOfcSeqno;
	}
	public void setEstBrkMemOfcSeqno(long estBrkMemOfcSeqno) {
		this.estBrkMemOfcSeqno = estBrkMemOfcSeqno;
	}
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public String getDealStatGbNm() {
		return dealStatGbNm;
	}
	public void setDealStatGbNm(String dealStatGbNm) {
		this.dealStatGbNm = dealStatGbNm;
	}
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
	}
	public String getPrptNo() {
		return prptNo;
	}
	public void setPrptNo(String prptNo) {
		this.prptNo = prptNo;
	}
	public String getPrptRegNo() {
		return prptRegNo;
	}
	public void setPrptRegNo(String prptRegNo) {
		this.prptRegNo = prptRegNo;
	}
	public long getExceptPrptSeqno() {
		return exceptPrptSeqno;
	}
	public void setExceptPrptSeqno(long exceptPrptSeqno) {
		this.exceptPrptSeqno = exceptPrptSeqno;
	}
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	public String getCostNm() {
		return costNm;
	}
	public void setCostNm(String costNm) {
		this.costNm = costNm;
	}
	public String getOfcNm() {
		return ofcNm;
	}
	public void setOfcNm(String ofcNm) {
		this.ofcNm = ofcNm;
	}
	public String getEstateTypGbCd() {
		return estateTypGbCd;
	}
	public void setEstateTypGbCd(String estateTypGbCd) {
		this.estateTypGbCd = estateTypGbCd;
	}
	public String getEstateTypGbNm() {
		return estateTypGbNm;
	}
	public void setEstateTypGbNm(String estateTypGbNm) {
		this.estateTypGbNm = estateTypGbNm;
	}
	public String getEstateTypCd() {
		return estateTypCd;
	}
	public void setEstateTypCd(String estateTypCd) {
		this.estateTypCd = estateTypCd;
	}
	public String getEstateTypNm() {
		return estateTypNm;
	}
	public void setEstateTypNm(String estateTypNm) {
		this.estateTypNm = estateTypNm;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getDtlAddr() {
		return dtlAddr;
	}
	public void setDtlAddr(String dtlAddr) {
		this.dtlAddr = dtlAddr;
	}
	public String getAddrShortNm() {
		return addrShortNm;
	}
	public void setAddrShortNm(String addrShortNm) {
		this.addrShortNm = addrShortNm;
	}
	public String getUnregistYn() {
		return unregistYn;
	}
	public String getFromAdminYn() {
		return fromAdminYn;
	}
	public void setFromAdminYn(String fromAdminYn) {
		this.fromAdminYn = fromAdminYn;
	}
	public void setUnregistYn(String unregistYn) {
		this.unregistYn = unregistYn;
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
	public int getAllFlr() {
		return allFlr;
	}
	public void setAllFlr(int allFlr) {
		this.allFlr = allFlr;
	}
	public int getFlr() {
		return flr;
	}
	public void setFlr(int flr) {
		this.flr = flr;
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
	public String getUseCnfrmYear() {
		return useCnfrmYear;
	}
	public void setUseCnfrmYear(String useCnfrmYear) {
		this.useCnfrmYear = useCnfrmYear;
	}
	public String getUseCnfrmDate() {
		return useCnfrmDate;
	}
	public String getRegStatGbCd() {
		return regStatGbCd;
	}
	public void setRegStatGbCd(String regStatGbCd) {
		this.regStatGbCd = regStatGbCd;
	}
	public void setUseCnfrmDate(String useCnfrmDate) {
		this.useCnfrmDate = useCnfrmDate;
	}
	public String getBldUsageGbCd() {
		return bldUsageGbCd;
	}
	public void setBldUsageGbCd(String bldUsageGbCd) {
		this.bldUsageGbCd = bldUsageGbCd;
	}
	public String getBldUsageGbNm() {
		return bldUsageGbNm;
	}
	public void setBldUsageGbNm(String bldUsageGbNm) {
		this.bldUsageGbNm = bldUsageGbNm;
	}
	public String getSuggstnBldUsageGbCd() {
		return suggstnBldUsageGbCd;
	}
	public void setSuggstnBldUsageGbCd(String suggstnBldUsageGbCd) {
		this.suggstnBldUsageGbCd = suggstnBldUsageGbCd;
	}
	public String getSuggstnBldUsageGbNm() {
		return suggstnBldUsageGbNm;
	}
	public void setSuggstnBldUsageGbNm(String suggstnBldUsageGbNm) {
		this.suggstnBldUsageGbNm = suggstnBldUsageGbNm;
	}
	public String getPsblMovDayTypCd() {
		return psblMovDayTypCd;
	}
	public void setPsblMovDayTypCd(String psblMovDayTypCd) {
		this.psblMovDayTypCd = psblMovDayTypCd;
	}
	public String getPsblMovDate() {
		return psblMovDate;
	}
	public void setPsblMovDate(String psblMovDate) {
		this.psblMovDate = psblMovDate;
	}
	public int getMonMntnceCost() {
		return monMntnceCost;
	}
	public void setMonMntnceCost(int monMntnceCost) {
		this.monMntnceCost = monMntnceCost;
	}
	public String getLoanGbCd() {
		return loanGbCd;
	}
	public void setLoanGbCd(String loanGbCd) {
		this.loanGbCd = loanGbCd;
	}
	public String getLoanGbNm() {
		return loanGbNm;
	}
	public void setLoanGbNm(String loanGbNm) {
		this.loanGbNm = loanGbNm;
	}
	public long getLoanAmt() {
		return loanAmt;
	}
	public void setLoanAmt(long loanAmt) {
		this.loanAmt = loanAmt;
	}
	public String getParkingPsblYn() {
		return parkingPsblYn;
	}
	public void setParkingPsblYn(String parkingPsblYn) {
		this.parkingPsblYn = parkingPsblYn;
	}
	public int getParkingCost() {
		return parkingCost;
	}
	public void setParkingCost(int parkingCost) {
		this.parkingCost = parkingCost;
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
	public long getDealAmt() {
		return dealAmt;
	}
	public void setDealAmt(long dealAmt) {
		this.dealAmt = dealAmt;
	}
	public String getKeyMonExstsYn() {
		return keyMonExstsYn;
	}
	public void setKeyMonExstsYn(String keyMonExstsYn) {
		this.keyMonExstsYn = keyMonExstsYn;
	}
	public long getKeyMonAmt() {
		return keyMonAmt;
	}
	public void setKeyMonAmt(long keyMonAmt) {
		this.keyMonAmt = keyMonAmt;
	}
	public long getPrmmAmt() {
		return prmmAmt;
	}
	public void setPrmmAmt(long prmmAmt) {
		this.prmmAmt = prmmAmt;
	}
	public String getDealAmtDiscsnPsblYn() {
		return dealAmtDiscsnPsblYn;
	}
	public void setDealAmtDiscsnPsblYn(String dealAmtDiscsnPsblYn) {
		this.dealAmtDiscsnPsblYn = dealAmtDiscsnPsblYn;
	}
	public String getExistngLeaseExstsYn() {
		return existngLeaseExstsYn;
	}
	public void setExistngLeaseExstsYn(String existngLeaseExstsYn) {
		this.existngLeaseExstsYn = existngLeaseExstsYn;
	}
	public long getDpstAmt() {
		return dpstAmt;
	}
	public void setDpstAmt(long dpstAmt) {
		this.dpstAmt = dpstAmt;
	}
	public long getCrntDpstAmt() {
		return crntDpstAmt;
	}
	public void setCrntDpstAmt(long crntDpstAmt) {
		this.crntDpstAmt = crntDpstAmt;
	}
	public int getCrntMontRentAmt() {
		return crntMontRentAmt;
	}
	public void setCrntMontRentAmt(int crntMontRentAmt) {
		this.crntMontRentAmt = crntMontRentAmt;
	}
	public int getRoomCnt() {
		return roomCnt;
	}
	public void setRoomCnt(int roomCnt) {
		this.roomCnt = roomCnt;
	}
	public int getBathRoomCnt() {
		return bathRoomCnt;
	}
	public void setBathRoomCnt(int bathRoomCnt) {
		this.bathRoomCnt = bathRoomCnt;
	}
	public String getBultInYn() {
		return bultInYn;
	}
	public void setBultInYn(String bultInYn) {
		this.bultInYn = bultInYn;
	}
	public String getIntrrYn() {
		return intrrYn;
	}
	public void setIntrrYn(String intrrYn) {
		this.intrrYn = intrrYn;
	}
	public String getBldDirctnGbCd() {
		return bldDirctnGbCd;
	}
	public void setBldDirctnGbCd(String bldDirctnGbCd) {
		this.bldDirctnGbCd = bldDirctnGbCd;
	}
	public String getBldDirctnGbNm() {
		return bldDirctnGbNm;
	}
	public void setBldDirctnGbNm(String bldDirctnGbNm) {
		this.bldDirctnGbNm = bldDirctnGbNm;
	}
	public String getFlrHghtTypGbCd() {
		return flrHghtTypGbCd;
	}
	public void setFlrHghtTypGbCd(String flrHghtTypGbCd) {
		this.flrHghtTypGbCd = flrHghtTypGbCd;
	}
	public String getFlrHghtTypGbNm() {
		return flrHghtTypGbNm;
	}
	public void setFlrHghtTypGbNm(String flrHghtTypGbNm) {
		this.flrHghtTypGbNm = flrHghtTypGbNm;
	}
	public String getHeatKindGbCd() {
		return heatKindGbCd;
	}
	public void setHeatKindGbCd(String heatKindGbCd) {
		this.heatKindGbCd = heatKindGbCd;
	}
	public String getHeatKindGbNm() {
		return heatKindGbNm;
	}
	public void setHeatKindGbNm(String heatKindGbNm) {
		this.heatKindGbNm = heatKindGbNm;
	}
	public String getElctrPwrTypGbCd() {
		return elctrPwrTypGbCd;
	}
	public void setElctrPwrTypGbCd(String elctrPwrTypGbCd) {
		this.elctrPwrTypGbCd = elctrPwrTypGbCd;
	}
	public String getElctrPwrTypGbNm() {
		return elctrPwrTypGbNm;
	}
	public void setElctrPwrTypGbNm(String elctrPwrTypGbNm) {
		this.elctrPwrTypGbNm = elctrPwrTypGbNm;
	}
	public String getElvFcltExstsYn() {
		return elvFcltExstsYn;
	}
	public void setElvFcltExstsYn(String elvFcltExstsYn) {
		this.elvFcltExstsYn = elvFcltExstsYn;
	}
	public String getFrhgtElvExstsYn() {
		return frhgtElvExstsYn;
	}
	public void setFrhgtElvExstsYn(String frhgtElvExstsYn) {
		this.frhgtElvExstsYn = frhgtElvExstsYn;
	}
	public String getIntnlStrctrTypCd() {
		return intnlStrctrTypCd;
	}
	public void setIntnlStrctrTypCd(String intnlStrctrTypCd) {
		this.intnlStrctrTypCd = intnlStrctrTypCd;
	}
	public String getIntnlStrctrTypNm() {
		return intnlStrctrTypNm;
	}
	public void setIntnlStrctrTypNm(String intnlStrctrTypNm) {
		this.intnlStrctrTypNm = intnlStrctrTypNm;
	}
	public long getRegMemSeqno() {
		return regMemSeqno;
	}
	public void setRegMemSeqno(long regMemSeqno) {
		this.regMemSeqno = regMemSeqno;
	}
	public int getMontRentAmt() {
		return montRentAmt;
	}
	public void setMontRentAmt(int montRentAmt) {
		this.montRentAmt = montRentAmt;
	}
	public String getDispAreaNm() {
		return dispAreaNm;
	}
	public void setDispAreaNm(String dispAreaNm) {
		this.dispAreaNm = dispAreaNm;
	}
	public double getDispArea() {
		return dispArea;
	}
	public String getRegExprYn() {
		return regExprYn;
	}
	public void setRegExprYn(String regExprYn) {
		this.regExprYn = regExprYn;
	}
	public void setDispArea(double dispArea) {
		this.dispArea = dispArea;
	}
	public String getMetterPortLinkUrl() {
		return metterPortLinkUrl;
	}
	public void setMetterPortLinkUrl(String metterPortLinkUrl) {
		this.metterPortLinkUrl = metterPortLinkUrl;
	}
	public String getMntnceItemDscr() {
		return mntnceItemDscr;
	}
	public void setMntnceItemDscr(String mntnceItemDscr) {
		this.mntnceItemDscr = mntnceItemDscr;
	}
	public String getMovDateDiscsnPsblYn() {
		return movDateDiscsnPsblYn;
	}
	public void setMovDateDiscsnPsblYn(String movDateDiscsnPsblYn) {
		this.movDateDiscsnPsblYn = movDateDiscsnPsblYn;
	}
	public String getCrntSectrGbNm() {
		return crntSectrGbNm;
	}
	public void setCrntSectrGbNm(String crntSectrGbNm) {
		this.crntSectrGbNm = crntSectrGbNm;
	}
	public String getSuggstnSectrGbNm() {
		return suggstnSectrGbNm;
	}
	public void setSuggstnSectrGbNm(String suggstnSectrGbNm) {
		this.suggstnSectrGbNm = suggstnSectrGbNm;
	}
	public double getWghtPerPy() {
		return wghtPerPy;
	}
	public void setWghtPerPy(double wghtPerPy) {
		this.wghtPerPy = wghtPerPy;
	}
	public String getCmpltExpctDate() {
		return cmpltExpctDate;
	}
	public void setCmpltExpctDate(String cmpltExpctDate) {
		this.cmpltExpctDate = cmpltExpctDate;
	}
	public String getMapDispYn() {
		return mapDispYn;
	}
	public void setMapDispYn(String mapDispYn) {
		this.mapDispYn = mapDispYn;
	}
	public String getTmpAddrYn() {
		return tmpAddrYn;
	}
	public void setTmpAddrYn(String tmpAddrYn) {
		this.tmpAddrYn = tmpAddrYn;
	}
	public String getLndCrntUsageGbCd() {
		return lndCrntUsageGbCd;
	}
	public void setLndCrntUsageGbCd(String lndCrntUsageGbCd) {
		this.lndCrntUsageGbCd = lndCrntUsageGbCd;
	}
	public String getLndCrntUsageGbNm() {
		return lndCrntUsageGbNm;
	}
	public void setLndCrntUsageGbNm(String lndCrntUsageGbNm) {
		this.lndCrntUsageGbNm = lndCrntUsageGbNm;
	}
	public String getCrntSectrGbCd() {
		return crntSectrGbCd;
	}
	public void setCrntSectrGbCd(String crntSectrGbCd) {
		this.crntSectrGbCd = crntSectrGbCd;
	}
	public String getSuggstnSectrGbCd() {
		return suggstnSectrGbCd;
	}
	public void setSuggstnSectrGbCd(String suggstnSectrGbCd) {
		this.suggstnSectrGbCd = suggstnSectrGbCd;
	}
	public String getFrghtElvExstsYn() {
		return frghtElvExstsYn;
	}
	public void setFrghtElvExstsYn(String frghtElvExstsYn) {
		this.frghtElvExstsYn = frghtElvExstsYn;
	}
	public String getDockExstsYn() {
		return dockExstsYn;
	}
	public void setDockExstsYn(String dockExstsYn) {
		this.dockExstsYn = dockExstsYn;
	}
	public String getHoistExstsYn() {
		return hoistExstsYn;
	}
	public void setHoistExstsYn(String hoistExstsYn) {
		this.hoistExstsYn = hoistExstsYn;
	}
	public String getMovInReprtPsblYn() {
		return movInReprtPsblYn;
	}
	public void setMovInReprtPsblYn(String movInReprtPsblYn) {
		this.movInReprtPsblYn = movInReprtPsblYn;
	}
	public String getCityPlanYn() {
		return cityPlanYn;
	}
	public void setCityPlanYn(String cityPlanYn) {
		this.cityPlanYn = cityPlanYn;
	}
	public String getBldCnfrmIssueYn() {
		return bldCnfrmIssueYn;
	}
	public void setBldCnfrmIssueYn(String bldCnfrmIssueYn) {
		this.bldCnfrmIssueYn = bldCnfrmIssueYn;
	}
	public String getLndDealCnfrmApplYn() {
		return lndDealCnfrmApplYn;
	}
	public void setLndDealCnfrmApplYn(String lndDealCnfrmApplYn) {
		this.lndDealCnfrmApplYn = lndDealCnfrmApplYn;
	}
	public String getEntrnceRoadExstsYn() {
		return entrnceRoadExstsYn;
	}
	public void setEntrnceRoadExstsYn(String entrnceRoadExstsYn) {
		this.entrnceRoadExstsYn = entrnceRoadExstsYn;
	}
	public String getOptionExstsYn() {
		return optionExstsYn;
	}
	public void setOptionExstsYn(String optionExstsYn) {
		this.optionExstsYn = optionExstsYn;
	}
	public String getMntnceCostTypCd() {
		return mntnceCostTypCd;
	}
	public void setMntnceCostTypCd(String mntnceCostTypCd) {
		this.mntnceCostTypCd = mntnceCostTypCd;
	}
	public String getSmplSmrDscr() {
		return smplSmrDscr;
	}
	public void setSmplSmrDscr(String smplSmrDscr) {
		this.smplSmrDscr = smplSmrDscr;
	}
	public String getDtlExplntnDscr() {
		return dtlExplntnDscr;
	}
	public void setDtlExplntnDscr(String dtlExplntnDscr) {
		this.dtlExplntnDscr = dtlExplntnDscr;
	}
	public String getMatterPortLinkUrl() {
		return matterPortLinkUrl;
	}
	public void setMatterPortLinkUrl(String matterPortLinkUrl) {
		this.matterPortLinkUrl = matterPortLinkUrl;
	}
	public String getPrvtMemoDscr() {
		return prvtMemoDscr;
	}
	public void setPrvtMemoDscr(String prvtMemoDscr) {
		this.prvtMemoDscr = prvtMemoDscr;
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
	public String getTreatStatGbCd() {
		return treatStatGbCd;
	}
	public void setTreatStatGbCd(String treatStatGbCd) {
		this.treatStatGbCd = treatStatGbCd;
	}
}