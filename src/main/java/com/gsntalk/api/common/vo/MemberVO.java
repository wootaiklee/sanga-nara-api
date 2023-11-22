package com.gsntalk.api.common.vo;


import com.gsntalk.api.common.extend.PagingVO;

public class MemberVO extends PagingVO {

	private long memSeqno;
	private String loginToken;
	private String memTypCd;
	private String memTypNm;
	private String acntAprvStatCd;
	private String acntAprvStatNm;
	private String actvStatGbCd;
	private String actvStatGbNm;
	private long aprvTreatMemSeqno;
	private String email;
	private String pwd;
	private String memName;
	private String mobNo;
	private String mobVrfNo;
	private String vrfCnfrmToken;
	private String age14OvrAgreYn;
	private String svcUseAgreYn;
	private String prsnlInfAgreYn;
	private String mktRcvAgreYn;

	private String snsGbCd;
	private String snsId;
	private String prflImgUrl;
	private String genderCd;
	private String birthYear;
	private String ageLvl;
	private String rmk;

	private String regDttm;
	private String modDttm;
	private String delYn;
	private String delDttm;
	private String lastLoginDttm;

	private String autoLoginToken;
	private String newAutoLoginToken;
	private String accsIp;
	private String userAgent;
	private String loginHistGbCd;

	private String viewTaget;

	// 중개 사무소 정보
	private long estBrkMemOfcSeqno;
	private String ofcNm;
	private String reprNm;
	private String openRegNo;
	private String propertyCnt;
	private String addr;
	private String addrShortNm;
	private String telNo;
	private String bizNo;
	private int compMovPrpslPrptCnt;
	private String recentCompMovPrpslDt;
	
	private long totAssetAmt;
	private int assetCnt;
	private int prptSuggstCnt;
	private String joinDt;
	private String recentDt;

	public String getLoginToken() {
		return loginToken;
	}
	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getMemTypCd() {
		return memTypCd;
	}
	public void setMemTypCd(String memTypCd) {
		this.memTypCd = memTypCd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
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
	public String getMobVrfNo() {
		return mobVrfNo;
	}
	public void setMobVrfNo(String mobVrfNo) {
		this.mobVrfNo = mobVrfNo;
	}
	public String getVrfCnfrmToken() {
		return vrfCnfrmToken;
	}
	public void setVrfCnfrmToken(String vrfCnfrmToken) {
		this.vrfCnfrmToken = vrfCnfrmToken;
	}
	public String getAge14OvrAgreYn() {
		return age14OvrAgreYn;
	}
	public void setAge14OvrAgreYn(String age14OvrAgreYn) {
		this.age14OvrAgreYn = age14OvrAgreYn;
	}
	public String getSvcUseAgreYn() {
		return svcUseAgreYn;
	}
	public void setSvcUseAgreYn(String svcUseAgreYn) {
		this.svcUseAgreYn = svcUseAgreYn;
	}
	public String getPrsnlInfAgreYn() {
		return prsnlInfAgreYn;
	}
	public void setPrsnlInfAgreYn(String prsnlInfAgreYn) {
		this.prsnlInfAgreYn = prsnlInfAgreYn;
	}
	public String getMktRcvAgreYn() {
		return mktRcvAgreYn;
	}
	public void setMktRcvAgreYn(String mktRcvAgreYn) {
		this.mktRcvAgreYn = mktRcvAgreYn;
	}
	public String getSnsId() {
		return snsId;
	}
	public void setSnsId(String snsId) {
		this.snsId = snsId;
	}
	public String getPrflImgUrl() {
		return prflImgUrl;
	}
	public void setPrflImgUrl(String prflImgUrl) {
		this.prflImgUrl = prflImgUrl;
	}
	public String getGenderCd() {
		return genderCd;
	}
	public void setGenderCd(String genderCd) {
		this.genderCd = genderCd;
	}
	public String getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}
	public String getAgeLvl() {
		return ageLvl;
	}
	public void setAgeLvl(String ageLvl) {
		this.ageLvl = ageLvl;
	}
	public String getSnsGbCd() {
		return snsGbCd;
	}
	public void setSnsGbCd(String snsGbCd) {
		this.snsGbCd = snsGbCd;
	}
	public String getAutoLoginToken() {
		return autoLoginToken;
	}
	public void setAutoLoginToken(String autoLoginToken) {
		this.autoLoginToken = autoLoginToken;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getNewAutoLoginToken() {
		return newAutoLoginToken;
	}
	public void setNewAutoLoginToken(String newAutoLoginToken) {
		this.newAutoLoginToken = newAutoLoginToken;
	}
	public String getAccsIp() {
		return accsIp;
	}
	public void setAccsIp(String accsIp) {
		this.accsIp = accsIp;
	}
	public String getLoginHistGbCd() {
		return loginHistGbCd;
	}
	public void setLoginHistGbCd(String loginHistGbCd) {
		this.loginHistGbCd = loginHistGbCd;
	}
	public long getAprvTreatMemSeqno() {
		return aprvTreatMemSeqno;
	}
	public int getCompMovPrpslPrptCnt() {
		return compMovPrpslPrptCnt;
	}
	public void setCompMovPrpslPrptCnt(int compMovPrpslPrptCnt) {
		this.compMovPrpslPrptCnt = compMovPrpslPrptCnt;
	}
	public String getRecentCompMovPrpslDt() {
		return recentCompMovPrpslDt;
	}
	public void setRecentCompMovPrpslDt(String recentCompMovPrpslDt) {
		this.recentCompMovPrpslDt = recentCompMovPrpslDt;
	}
	public long getTotAssetAmt() {
		return totAssetAmt;
	}
	public void setTotAssetAmt(long totAssetAmt) {
		this.totAssetAmt = totAssetAmt;
	}
	public int getAssetCnt() {
		return assetCnt;
	}
	public void setAssetCnt(int assetCnt) {
		this.assetCnt = assetCnt;
	}
	public String getJoinDt() {
		return joinDt;
	}
	public void setJoinDt(String joinDt) {
		this.joinDt = joinDt;
	}
	public String getRecentDt() {
		return recentDt;
	}
	public void setRecentDt(String recentDt) {
		this.recentDt = recentDt;
	}
	public String getAcntAprvStatCd() {
		return acntAprvStatCd;
	}
	public void setAcntAprvStatCd(String acntAprvStatCd) {
		this.acntAprvStatCd = acntAprvStatCd;
	}
	public String getAcntAprvStatNm() {
		return acntAprvStatNm;
	}
	public void setAcntAprvStatNm(String acntAprvStatNm) {
		this.acntAprvStatNm = acntAprvStatNm;
	}
	public String getActvStatGbCd() {
		return actvStatGbCd;
	}
	public void setActvStatGbCd(String actvStatGbCd) {
		this.actvStatGbCd = actvStatGbCd;
	}
	public String getActvStatGbNm() {
		return actvStatGbNm;
	}
	public void setActvStatGbNm(String actvStatGbNm) {
		this.actvStatGbNm = actvStatGbNm;
	}
	public String getRmk() {
		return rmk;
	}
	public void setRmk(String rmk) {
		this.rmk = rmk;
	}
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	public String getModDttm() {
		return modDttm;
	}
	public void setModDttm(String modDttm) {
		this.modDttm = modDttm;
	}
	public String getDelYn() {
		return delYn;
	}
	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
	public String getDelDttm() {
		return delDttm;
	}
	public void setDelDttm(String delDttm) {
		this.delDttm = delDttm;
	}
	public String getLastLoginDttm() {
		return lastLoginDttm;
	}
	public void setLastLoginDttm(String lastLoginDttm) {
		this.lastLoginDttm = lastLoginDttm;
	}
	public String getViewTaget() {
		return viewTaget;
	}
	public void setViewTaget(String viewTaget) {
		this.viewTaget = viewTaget;
	}
	public long getEstBrkMemOfcSeqno() {
		return estBrkMemOfcSeqno;
	}
	public void setEstBrkMemOfcSeqno(long estBrkMemOfcSeqno) {
		this.estBrkMemOfcSeqno = estBrkMemOfcSeqno;
	}
	public String getOfcNm() {
		return ofcNm;
	}
	public void setOfcNm(String ofcNm) {
		this.ofcNm = ofcNm;
	}
	public String getReprNm() {
		return reprNm;
	}
	public void setReprNm(String reprNm) {
		this.reprNm = reprNm;
	}
	public String getOpenRegNo() {
		return openRegNo;
	}
	public void setOpenRegNo(String openRegNo) {
		this.openRegNo = openRegNo;
	}
	public String getPropertyCnt() {
		return propertyCnt;
	}
	public void setPropertyCnt(String propertyCnt) {
		this.propertyCnt = propertyCnt;
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
	public String getTelNo() {
		return telNo;
	}
	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}
	public String getBizNo() {
		return bizNo;
	}
	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
	}
	public int getPrptSuggstCnt() {
		return prptSuggstCnt;
	}
	public void setPrptSuggstCnt(int prptSuggstCnt) {
		this.prptSuggstCnt = prptSuggstCnt;
	}
	public void setAprvTreatMemSeqno(long aprvTreatMemSeqno) {
		this.aprvTreatMemSeqno = aprvTreatMemSeqno;
	}
	public String getMemTypNm() {
		return memTypNm;
	}
	public void setMemTypNm(String memTypNm) {
		this.memTypNm = memTypNm;
	}
}