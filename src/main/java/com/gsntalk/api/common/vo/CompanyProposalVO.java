package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class CompanyProposalVO extends PagingVO {

	private long memSeqno;
	private String email;
	private String memName;
	private long estBrkMemOfcSeqno;
	private String ofcNm;
	
	private long compSeqno;
	private String compNm;
	private String estBrkDispPosNm;
	
	private String recentDt;
	private int prpslCnt;
	
	private long movPrpslPrptSeqno;
	private String estateTypGbCd;
	private String estateTypCd;
	private String tmpAddrYn;
	private String addr;
	private String roadAddr;
	private String grpAddr;
	private String bldNm;
	private double lat;
	private double lng;
	private String tranTypGbCd;
	private String tranTypGbNm;
	private long salesCost;
	private long dpstAmt;
	private int montRentAmt;
	private long prmmAmt;
	private double acqstnTaxRatio;
	private long supprtAmt;
	private long etcCost;
	private double loanRatio1;
	private double loanRatio2;
	private double loanIntrRatio;
	private String investYn;
	private long investDpstAmt;
	private int investMontRentAmt;
	private double prvArea;
	private int flr;
	private int allFlr;
	private int monMntnceCost;
	private String psblMovDayTypCd;
	private String psblMovDayTypNm;
	private String psblMovDate;
	private String heatKindGbCd;
	private int parkingCarCnt;
	private String bldSpclAdvtgDscr;
	private String reqDscr;
	
	private String compPrpslBldFacTypCd;
	private double sortSerl;
	private String saveFileNm;
	private String fileUrl;
	private String photoGb;
	private String facTypDscr;
	
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getCompPrpslBldFacTypCd() {
		return compPrpslBldFacTypCd;
	}
	public void setCompPrpslBldFacTypCd(String compPrpslBldFacTypCd) {
		this.compPrpslBldFacTypCd = compPrpslBldFacTypCd;
	}
	public long getEstBrkMemOfcSeqno() {
		return estBrkMemOfcSeqno;
	}
	public void setEstBrkMemOfcSeqno(long estBrkMemOfcSeqno) {
		this.estBrkMemOfcSeqno = estBrkMemOfcSeqno;
	}
	public String getCompNm() {
		return compNm;
	}
	public void setCompNm(String compNm) {
		this.compNm = compNm;
	}
	public String getEstBrkDispPosNm() {
		return estBrkDispPosNm;
	}
	public void setEstBrkDispPosNm(String estBrkDispPosNm) {
		this.estBrkDispPosNm = estBrkDispPosNm;
	}
	public long getCompSeqno() {
		return compSeqno;
	}
	public void setCompSeqno(long compSeqno) {
		this.compSeqno = compSeqno;
	}
	public String getRecentDt() {
		return recentDt;
	}
	public void setRecentDt(String recentDt) {
		this.recentDt = recentDt;
	}
	public int getPrpslCnt() {
		return prpslCnt;
	}
	public void setPrpslCnt(int prpslCnt) {
		this.prpslCnt = prpslCnt;
	}
	public long getMovPrpslPrptSeqno() {
		return movPrpslPrptSeqno;
	}
	public void setMovPrpslPrptSeqno(long movPrpslPrptSeqno) {
		this.movPrpslPrptSeqno = movPrpslPrptSeqno;
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
	public String getTmpAddrYn() {
		return tmpAddrYn;
	}
	public void setTmpAddrYn(String tmpAddrYn) {
		this.tmpAddrYn = tmpAddrYn;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getRoadAddr() {
		return roadAddr;
	}
	public void setRoadAddr(String roadAddr) {
		this.roadAddr = roadAddr;
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
	public String getTranTypGbCd() {
		return tranTypGbCd;
	}
	public void setTranTypGbCd(String tranTypGbCd) {
		this.tranTypGbCd = tranTypGbCd;
	}
	public long getSalesCost() {
		return salesCost;
	}
	public void setSalesCost(long salesCost) {
		this.salesCost = salesCost;
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
	public long getPrmmAmt() {
		return prmmAmt;
	}
	public void setPrmmAmt(long prmmAmt) {
		this.prmmAmt = prmmAmt;
	}
	public double getAcqstnTaxRatio() {
		return acqstnTaxRatio;
	}
	public void setAcqstnTaxRatio(double acqstnTaxRatio) {
		this.acqstnTaxRatio = acqstnTaxRatio;
	}
	public long getSupprtAmt() {
		return supprtAmt;
	}
	public void setSupprtAmt(long supprtAmt) {
		this.supprtAmt = supprtAmt;
	}
	public long getEtcCost() {
		return etcCost;
	}
	public void setEtcCost(long etcCost) {
		this.etcCost = etcCost;
	}
	public double getLoanRatio1() {
		return loanRatio1;
	}
	public void setLoanRatio1(double loanRatio1) {
		this.loanRatio1 = loanRatio1;
	}
	public double getLoanRatio2() {
		return loanRatio2;
	}
	public void setLoanRatio2(double loanRatio2) {
		this.loanRatio2 = loanRatio2;
	}
	public double getLoanIntrRatio() {
		return loanIntrRatio;
	}
	public void setLoanIntrRatio(double loanIntrRatio) {
		this.loanIntrRatio = loanIntrRatio;
	}
	public String getInvestYn() {
		return investYn;
	}
	public void setInvestYn(String investYn) {
		this.investYn = investYn;
	}
	public long getInvestDpstAmt() {
		return investDpstAmt;
	}
	public void setInvestDpstAmt(long investDpstAmt) {
		this.investDpstAmt = investDpstAmt;
	}
	public int getInvestMontRentAmt() {
		return investMontRentAmt;
	}
	public void setInvestMontRentAmt(int investMontRentAmt) {
		this.investMontRentAmt = investMontRentAmt;
	}
	public double getPrvArea() {
		return prvArea;
	}
	public void setPrvArea(double prvArea) {
		this.prvArea = prvArea;
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
	public int getMonMntnceCost() {
		return monMntnceCost;
	}
	public void setMonMntnceCost(int monMntnceCost) {
		this.monMntnceCost = monMntnceCost;
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
	public String getHeatKindGbCd() {
		return heatKindGbCd;
	}
	public void setHeatKindGbCd(String heatKindGbCd) {
		this.heatKindGbCd = heatKindGbCd;
	}
	public int getParkingCarCnt() {
		return parkingCarCnt;
	}
	public void setParkingCarCnt(int parkingCarCnt) {
		this.parkingCarCnt = parkingCarCnt;
	}
	public String getBldSpclAdvtgDscr() {
		return bldSpclAdvtgDscr;
	}
	public void setBldSpclAdvtgDscr(String bldSpclAdvtgDscr) {
		this.bldSpclAdvtgDscr = bldSpclAdvtgDscr;
	}
	public String getReqDscr() {
		return reqDscr;
	}
	public void setReqDscr(String reqDscr) {
		this.reqDscr = reqDscr;
	}
	public double getSortSerl() {
		return sortSerl;
	}
	public void setSortSerl(double sortSerl) {
		this.sortSerl = sortSerl;
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
	public String getPhotoGb() {
		return photoGb;
	}
	public void setPhotoGb(String photoGb) {
		this.photoGb = photoGb;
	}
	public String getGrpAddr() {
		return grpAddr;
	}
	public void setGrpAddr(String grpAddr) {
		this.grpAddr = grpAddr;
	}
	public String getTranTypGbNm() {
		return tranTypGbNm;
	}
	public void setTranTypGbNm(String tranTypGbNm) {
		this.tranTypGbNm = tranTypGbNm;
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
	public String getOfcNm() {
		return ofcNm;
	}
	public void setOfcNm(String ofcNm) {
		this.ofcNm = ofcNm;
	}
	public String getFacTypDscr() {
		return facTypDscr;
	}
	public void setFacTypDscr(String facTypDscr) {
		this.facTypDscr = facTypDscr;
	}
	public String getPsblMovDayTypNm() {
		return psblMovDayTypNm;
	}
	public void setPsblMovDayTypNm(String psblMovDayTypNm) {
		this.psblMovDayTypNm = psblMovDayTypNm;
	}
}