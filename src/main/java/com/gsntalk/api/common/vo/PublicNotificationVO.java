package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class PublicNotificationVO extends PagingVO {

    private long publicNotiSeqno;
    private String notiTypGbCd;     // E : 이벤트 안내, S : 서비스안내, U : 업데이트 안내
    private String notiTypGbNm;
    private String sendDt;        // 알림발송일자
    private String notiDscr;
    private String sendTreatDttm;   // 발송처리일시
    private int rcpntCnt;           // 수신자수
    private String regDttm;
    private String modYn;
    private String delYn;
	public long getPublicNotiSeqno() {
		return publicNotiSeqno;
	}
	public void setPublicNotiSeqno(long publicNotiSeqno) {
		this.publicNotiSeqno = publicNotiSeqno;
	}
	public String getNotiTypGbCd() {
		return notiTypGbCd;
	}
	public void setNotiTypGbCd(String notiTypGbCd) {
		this.notiTypGbCd = notiTypGbCd;
	}
	public String getNotiTypGbNm() {
		return notiTypGbNm;
	}
	public void setNotiTypGbNm(String notiTypGbNm) {
		this.notiTypGbNm = notiTypGbNm;
	}
	public String getSendDt() {
		return sendDt;
	}
	public void setSendDt(String sendDt) {
		this.sendDt = sendDt;
	}
	public String getNotiDscr() {
		return notiDscr;
	}
	public void setNotiDscr(String notiDscr) {
		this.notiDscr = notiDscr;
	}
	public String getSendTreatDttm() {
		return sendTreatDttm;
	}
	public void setSendTreatDttm(String sendTreatDttm) {
		this.sendTreatDttm = sendTreatDttm;
	}
	public int getRcpntCnt() {
		return rcpntCnt;
	}
	public void setRcpntCnt(int rcpntCnt) {
		this.rcpntCnt = rcpntCnt;
	}
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	public String getModYn() {
		return modYn;
	}
	public void setModYn(String modYn) {
		this.modYn = modYn;
	}
	public String getDelYn() {
		return delYn;
	}
	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
}