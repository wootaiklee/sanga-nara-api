package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class AssetVO extends PagingVO {

	private long memSeqno;
	private long assetSeqno;
	private String estateTypGbCd;
	private String estateTypGbNm;
	private String estateTypCd;
	private String estateTypNm;
	private String tranTypGbCd;
	private String tranTypGbNm;
	private String addr;
	private String roadAddr;
	private String dtlAddr;
	private String tmpAddrYn;
	private double lat;
	private double lng;
	private double splyArea;
	private double prvArea;
	private double lndArea;
	private double totFlrArea;
	private String inspGbCd;
	private String inspGbNm;
	private String bizmanGbCd;
	private String bizmanGbNm;
	private String unregistYn;
	private long dealAmt;
	private String contDate;
	private String registDate;
	private double acqstnTaxRatio;
	private long etcCost;
	private int taxofcCost;
	private int estFeeAmt;
	private long loanAmt;
	private double loanIntrRatio;
	private int loanMonTerm;
	private String loanDate;
	private String loanMthdGbCd;
	private String loanMthdGbNm;
	private String emptyTypGbCd;
	private String emptyTypGbNm;
	private String leseeNm;
	private String leseeTelNo;
	private String rentAmtPayMthdGbCd;
	private String rentAmtPayMthdGbNm;
	private long dpstAmt;
	private int montRentAmt;
	private String monthlyPayDayGbCd;
	private String monthlyPayDayGbNm;
	private String rentContStDate;
	private String rentContEdDate;
	
	private long realInvestAmt;
	private int loanIntrAmt;
	private int acqstnTaxAmt;
	private long totCost;
	private double rtnRatio;
	private int monProfitAmt;
	private int pyUnitAmt;
	private long mkProfitAmt;
	private int trnsfTaxAmt;
	
	private String assetAttDocGbCd;
	private String assetAttDocGbNm;
	private String uploadFileNm;
	private String saveFileNm;
	private String fileUrl;
	
	private String scFileUrl;
	private String brFileUrl;
	private String rcFileUrl;
	private String etFileUrl;
	
	private String email;
	private String memName;
	private String mobNo;
	private String recentDt;
	private int assetCnt;
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public long getAssetSeqno() {
		return assetSeqno;
	}
	public void setAssetSeqno(long assetSeqno) {
		this.assetSeqno = assetSeqno;
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
	public String getDtlAddr() {
		return dtlAddr;
	}
	public void setDtlAddr(String dtlAddr) {
		this.dtlAddr = dtlAddr;
	}
	public String getTmpAddrYn() {
		return tmpAddrYn;
	}
	public void setTmpAddrYn(String tmpAddrYn) {
		this.tmpAddrYn = tmpAddrYn;
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
	public String getInspGbCd() {
		return inspGbCd;
	}
	public void setInspGbCd(String inspGbCd) {
		this.inspGbCd = inspGbCd;
	}
	public String getInspGbNm() {
		return inspGbNm;
	}
	public void setInspGbNm(String inspGbNm) {
		this.inspGbNm = inspGbNm;
	}
	public String getBizmanGbCd() {
		return bizmanGbCd;
	}
	public void setBizmanGbCd(String bizmanGbCd) {
		this.bizmanGbCd = bizmanGbCd;
	}
	public String getBizmanGbNm() {
		return bizmanGbNm;
	}
	public void setBizmanGbNm(String bizmanGbNm) {
		this.bizmanGbNm = bizmanGbNm;
	}
	public String getUnregistYn() {
		return unregistYn;
	}
	public void setUnregistYn(String unregistYn) {
		this.unregistYn = unregistYn;
	}
	public long getDealAmt() {
		return dealAmt;
	}
	public void setDealAmt(long dealAmt) {
		this.dealAmt = dealAmt;
	}
	public String getContDate() {
		return contDate;
	}
	public void setContDate(String contDate) {
		this.contDate = contDate;
	}
	public String getRegistDate() {
		return registDate;
	}
	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}
	public double getAcqstnTaxRatio() {
		return acqstnTaxRatio;
	}
	public void setAcqstnTaxRatio(double acqstnTaxRatio) {
		this.acqstnTaxRatio = acqstnTaxRatio;
	}
	public long getEtcCost() {
		return etcCost;
	}
	public void setEtcCost(long etcCost) {
		this.etcCost = etcCost;
	}
	public int getTaxofcCost() {
		return taxofcCost;
	}
	public void setTaxofcCost(int taxofcCost) {
		this.taxofcCost = taxofcCost;
	}
	public int getEstFeeAmt() {
		return estFeeAmt;
	}
	public void setEstFeeAmt(int estFeeAmt) {
		this.estFeeAmt = estFeeAmt;
	}
	public long getLoanAmt() {
		return loanAmt;
	}
	public void setLoanAmt(long loanAmt) {
		this.loanAmt = loanAmt;
	}
	public double getLoanIntrRatio() {
		return loanIntrRatio;
	}
	public void setLoanIntrRatio(double loanIntrRatio) {
		this.loanIntrRatio = loanIntrRatio;
	}
	public int getLoanMonTerm() {
		return loanMonTerm;
	}
	public void setLoanMonTerm(int loanMonTerm) {
		this.loanMonTerm = loanMonTerm;
	}
	public String getLoanDate() {
		return loanDate;
	}
	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}
	public String getLoanMthdGbCd() {
		return loanMthdGbCd;
	}
	public void setLoanMthdGbCd(String loanMthdGbCd) {
		this.loanMthdGbCd = loanMthdGbCd;
	}
	public String getLoanMthdGbNm() {
		return loanMthdGbNm;
	}
	public void setLoanMthdGbNm(String loanMthdGbNm) {
		this.loanMthdGbNm = loanMthdGbNm;
	}
	public String getEmptyTypGbCd() {
		return emptyTypGbCd;
	}
	public void setEmptyTypGbCd(String emptyTypGbCd) {
		this.emptyTypGbCd = emptyTypGbCd;
	}
	public String getEmptyTypGbNm() {
		return emptyTypGbNm;
	}
	public void setEmptyTypGbNm(String emptyTypGbNm) {
		this.emptyTypGbNm = emptyTypGbNm;
	}
	public String getLeseeNm() {
		return leseeNm;
	}
	public void setLeseeNm(String leseeNm) {
		this.leseeNm = leseeNm;
	}
	public String getLeseeTelNo() {
		return leseeTelNo;
	}
	public void setLeseeTelNo(String leseeTelNo) {
		this.leseeTelNo = leseeTelNo;
	}
	public String getRentAmtPayMthdGbCd() {
		return rentAmtPayMthdGbCd;
	}
	public void setRentAmtPayMthdGbCd(String rentAmtPayMthdGbCd) {
		this.rentAmtPayMthdGbCd = rentAmtPayMthdGbCd;
	}
	public String getRentAmtPayMthdGbNm() {
		return rentAmtPayMthdGbNm;
	}
	public void setRentAmtPayMthdGbNm(String rentAmtPayMthdGbNm) {
		this.rentAmtPayMthdGbNm = rentAmtPayMthdGbNm;
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
	public String getMonthlyPayDayGbCd() {
		return monthlyPayDayGbCd;
	}
	public void setMonthlyPayDayGbCd(String monthlyPayDayGbCd) {
		this.monthlyPayDayGbCd = monthlyPayDayGbCd;
	}
	public String getMonthlyPayDayGbNm() {
		return monthlyPayDayGbNm;
	}
	public void setMonthlyPayDayGbNm(String monthlyPayDayGbNm) {
		this.monthlyPayDayGbNm = monthlyPayDayGbNm;
	}
	public String getRentContStDate() {
		return rentContStDate;
	}
	public void setRentContStDate(String rentContStDate) {
		this.rentContStDate = rentContStDate;
	}
	public String getRentContEdDate() {
		return rentContEdDate;
	}
	public void setRentContEdDate(String rentContEdDate) {
		this.rentContEdDate = rentContEdDate;
	}
	public String getAssetAttDocGbCd() {
		return assetAttDocGbCd;
	}
	public void setAssetAttDocGbCd(String assetAttDocGbCd) {
		this.assetAttDocGbCd = assetAttDocGbCd;
	}
	public String getAssetAttDocGbNm() {
		return assetAttDocGbNm;
	}
	public void setAssetAttDocGbNm(String assetAttDocGbNm) {
		this.assetAttDocGbNm = assetAttDocGbNm;
	}
	public String getUploadFileNm() {
		return uploadFileNm;
	}
	public void setUploadFileNm(String uploadFileNm) {
		this.uploadFileNm = uploadFileNm;
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
	public String getScFileUrl() {
		return scFileUrl;
	}
	public void setScFileUrl(String scFileUrl) {
		this.scFileUrl = scFileUrl;
	}
	public String getBrFileUrl() {
		return brFileUrl;
	}
	public void setBrFileUrl(String brFileUrl) {
		this.brFileUrl = brFileUrl;
	}
	public String getRcFileUrl() {
		return rcFileUrl;
	}
	public void setRcFileUrl(String rcFileUrl) {
		this.rcFileUrl = rcFileUrl;
	}
	public String getEtFileUrl() {
		return etFileUrl;
	}
	public void setEtFileUrl(String etFileUrl) {
		this.etFileUrl = etFileUrl;
	}
	public long getRealInvestAmt() {
		return realInvestAmt;
	}
	public void setRealInvestAmt(long realInvestAmt) {
		this.realInvestAmt = realInvestAmt;
	}
	public int getLoanIntrAmt() {
		return loanIntrAmt;
	}
	public void setLoanIntrAmt(int loanIntrAmt) {
		this.loanIntrAmt = loanIntrAmt;
	}
	public long getTotCost() {
		return totCost;
	}
	public void setTotCost(long totCost) {
		this.totCost = totCost;
	}
	public double getRtnRatio() {
		return rtnRatio;
	}
	public void setRtnRatio(double rtnRatio) {
		this.rtnRatio = rtnRatio;
	}
	public int getMonProfitAmt() {
		return monProfitAmt;
	}
	public void setMonProfitAmt(int monProfitAmt) {
		this.monProfitAmt = monProfitAmt;
	}
	public int getPyUnitAmt() {
		return pyUnitAmt;
	}
	public void setPyUnitAmt(int pyUnitAmt) {
		this.pyUnitAmt = pyUnitAmt;
	}
	public long getMkProfitAmt() {
		return mkProfitAmt;
	}
	public void setMkProfitAmt(long mkProfitAmt) {
		this.mkProfitAmt = mkProfitAmt;
	}
	public int getTrnsfTaxAmt() {
		return trnsfTaxAmt;
	}
	public void setTrnsfTaxAmt(int trnsfTaxAmt) {
		this.trnsfTaxAmt = trnsfTaxAmt;
	}
	public int getAcqstnTaxAmt() {
		return acqstnTaxAmt;
	}
	public void setAcqstnTaxAmt(int acqstnTaxAmt) {
		this.acqstnTaxAmt = acqstnTaxAmt;
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
	public String getRecentDt() {
		return recentDt;
	}
	public void setRecentDt(String recentDt) {
		this.recentDt = recentDt;
	}
	public int getAssetCnt() {
		return assetCnt;
	}
	public void setAssetCnt(int assetCnt) {
		this.assetCnt = assetCnt;
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
}