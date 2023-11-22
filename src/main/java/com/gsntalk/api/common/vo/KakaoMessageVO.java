package com.gsntalk.api.common.vo;

public class KakaoMessageVO {

	private long msgSendHistSeqno;
	private String rcpntMobNo;
	private String msgTmpltCd;
	private String msgMappngVal;
	private String reqId;
	private String reqDttm;
	private String reqRslt;
	private String sendRslt;
	private String resendStatCd;
	
	private String srchStDttm;
	private String srchEdDttm;
	
	public long getMsgSendHistSeqno() {
		return msgSendHistSeqno;
	}
	public void setMsgSendHistSeqno(long msgSendHistSeqno) {
		this.msgSendHistSeqno = msgSendHistSeqno;
	}
	public String getRcpntMobNo() {
		return rcpntMobNo;
	}
	public void setRcpntMobNo(String rcpntMobNo) {
		this.rcpntMobNo = rcpntMobNo;
	}
	public String getMsgTmpltCd() {
		return msgTmpltCd;
	}
	public void setMsgTmpltCd(String msgTmpltCd) {
		this.msgTmpltCd = msgTmpltCd;
	}
	public String getReqId() {
		return reqId;
	}
	public void setReqId(String reqId) {
		this.reqId = reqId;
	}
	public String getReqDttm() {
		return reqDttm;
	}
	public void setReqDttm(String reqDttm) {
		this.reqDttm = reqDttm;
	}
	public String getReqRslt() {
		return reqRslt;
	}
	public void setReqRslt(String reqRslt) {
		this.reqRslt = reqRslt;
	}
	public String getSendRslt() {
		return sendRslt;
	}
	public void setSendRslt(String sendRslt) {
		this.sendRslt = sendRslt;
	}
	public String getMsgMappngVal() {
		return msgMappngVal;
	}
	public void setMsgMappngVal(String msgMappngVal) {
		this.msgMappngVal = msgMappngVal;
	}
	public String getSrchStDttm() {
		return srchStDttm;
	}
	public void setSrchStDttm(String srchStDttm) {
		this.srchStDttm = srchStDttm;
	}
	public String getSrchEdDttm() {
		return srchEdDttm;
	}
	public void setSrchEdDttm(String srchEdDttm) {
		this.srchEdDttm = srchEdDttm;
	}
	public String getResendStatCd() {
		return resendStatCd;
	}
	public void setResendStatCd(String resendStatCd) {
		this.resendStatCd = resendStatCd;
	}
}