package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class EstateBrokerOfficeVO extends PagingVO {

	private long estBrkOfcSeqno;
	private long estBrkMemOfcSeqno;
	private String openRegNo;
	private String openRegDate;
	private String openEstBrkClasGb;
	private String ofcNm;
	private String mltJoinYn;
	private String reprNm;
	private String telNo;
	private String addrRoad;
	private String addrPost;
	private String addr;
	private String addrShortNm;
	private int estBrkCnt;
	private int estAsstCnt;
	private double lat;
	private double lng;
	private String dataStndDate;
	private String offrInstCd;
	private String offrInstNm;
	
	private long memSeqno;
	private String actvStatGbCd;
	private String email;
	private String memName;
	private String bizNo;
	private String mobNo;
	private String prflImgUrl;
	private int regPrptCnt;
	private String regDt;
	private String rcntAccsDt;
	private String estRegImgUrl;
	private String bizRegImgUrl;
	
	public String getOpenRegNo() {
		return openRegNo;
	}
	public void setOpenRegNo(String openRegNo) {
		this.openRegNo = openRegNo;
	}
	public String getOpenRegDate() {
		return openRegDate;
	}
	public void setOpenRegDate(String openRegDate) {
		this.openRegDate = openRegDate;
	}
	public String getOpenEstBrkClasGb() {
		return openEstBrkClasGb;
	}
	public void setOpenEstBrkClasGb(String openEstBrkClasGb) {
		this.openEstBrkClasGb = openEstBrkClasGb;
	}
	public String getOfcNm() {
		return ofcNm;
	}
	public void setOfcNm(String ofcNm) {
		this.ofcNm = ofcNm;
	}
	public String getMltJoinYn() {
		return mltJoinYn;
	}
	public void setMltJoinYn(String mltJoinYn) {
		this.mltJoinYn = mltJoinYn;
	}
	public String getReprNm() {
		return reprNm;
	}
	public void setReprNm(String reprNm) {
		this.reprNm = reprNm;
	}
	public String getTelNo() {
		return telNo;
	}
	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}
	public String getAddrRoad() {
		return addrRoad;
	}
	public void setAddrRoad(String addrRoad) {
		this.addrRoad = addrRoad;
	}
	public String getAddrPost() {
		return addrPost;
	}
	public void setAddrPost(String addrPost) {
		this.addrPost = addrPost;
	}
	public int getEstBrkCnt() {
		return estBrkCnt;
	}
	public void setEstBrkCnt(int estBrkCnt) {
		this.estBrkCnt = estBrkCnt;
	}
	public int getEstAsstCnt() {
		return estAsstCnt;
	}
	public void setEstAsstCnt(int estAsstCnt) {
		this.estAsstCnt = estAsstCnt;
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
	public String getDataStndDate() {
		return dataStndDate;
	}
	public void setDataStndDate(String dataStndDate) {
		this.dataStndDate = dataStndDate;
	}
	public String getOffrInstCd() {
		return offrInstCd;
	}
	public void setOffrInstCd(String offrInstCd) {
		this.offrInstCd = offrInstCd;
	}
	public String getOffrInstNm() {
		return offrInstNm;
	}
	public void setOffrInstNm(String offrInstNm) {
		this.offrInstNm = offrInstNm;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public long getEstBrkOfcSeqno() {
		return estBrkOfcSeqno;
	}
	public void setEstBrkOfcSeqno(long estBrkOfcSeqno) {
		this.estBrkOfcSeqno = estBrkOfcSeqno;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getBizNo() {
		return bizNo;
	}
	public void setBizNo(String bizNo) {
		this.bizNo = bizNo;
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
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	public long getEstBrkMemOfcSeqno() {
		return estBrkMemOfcSeqno;
	}
	public void setEstBrkMemOfcSeqno(long estBrkMemOfcSeqno) {
		this.estBrkMemOfcSeqno = estBrkMemOfcSeqno;
	}
	public String getActvStatGbCd() {
		return actvStatGbCd;
	}
	public void setActvStatGbCd(String actvStatGbCd) {
		this.actvStatGbCd = actvStatGbCd;
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
	public int getRegPrptCnt() {
		return regPrptCnt;
	}
	public void setRegPrptCnt(int regPrptCnt) {
		this.regPrptCnt = regPrptCnt;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}
	public String getRcntAccsDt() {
		return rcntAccsDt;
	}
	public void setRcntAccsDt(String rcntAccsDt) {
		this.rcntAccsDt = rcntAccsDt;
	}
	public String getEstRegImgUrl() {
		return estRegImgUrl;
	}
	public void setEstRegImgUrl(String estRegImgUrl) {
		this.estRegImgUrl = estRegImgUrl;
	}
	public String getBizRegImgUrl() {
		return bizRegImgUrl;
	}
	public void setBizRegImgUrl(String bizRegImgUrl) {
		this.bizRegImgUrl = bizRegImgUrl;
	}
}