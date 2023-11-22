package com.gsntalk.api.common.vo;

public class NotificationVO {

    private long notiSeqno;
    private long memSeqno;
    private String notiGbCd;
    private String notiTypGbCd;
    private String notiTypGbNm;
    private String notiTtl;
    private String notiDscr;
    private long prptSeqno;
    private long suggstnSalesSeqno;
    private String regDttm;
    private String cnfrmYn;

    private long publicNotiSeqno;
    private int notiCnt;
    private int prptCnt;
    private int salesCnt;
    private int schdlCnt;
    private int rcpntCnt;
    
	public long getNotiSeqno() {
		return notiSeqno;
	}
	public void setNotiSeqno(long notiSeqno) {
		this.notiSeqno = notiSeqno;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getNotiGbCd() {
		return notiGbCd;
	}
	public void setNotiGbCd(String notiGbCd) {
		this.notiGbCd = notiGbCd;
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
	public String getNotiTtl() {
		return notiTtl;
	}
	public void setNotiTtl(String notiTtl) {
		this.notiTtl = notiTtl;
	}
	public String getNotiDscr() {
		return notiDscr;
	}
	public void setNotiDscr(String notiDscr) {
		this.notiDscr = notiDscr;
	}
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
	}
	public long getSuggstnSalesSeqno() {
		return suggstnSalesSeqno;
	}
	public void setSuggstnSalesSeqno(long suggstnSalesSeqno) {
		this.suggstnSalesSeqno = suggstnSalesSeqno;
	}
	public String getRegDttm() {
		return regDttm;
	}
	public void setRegDttm(String regDttm) {
		this.regDttm = regDttm;
	}
	public String getCnfrmYn() {
		return cnfrmYn;
	}
	public void setCnfrmYn(String cnfrmYn) {
		this.cnfrmYn = cnfrmYn;
	}
	public long getPublicNotiSeqno() {
		return publicNotiSeqno;
	}
	public void setPublicNotiSeqno(long publicNotiSeqno) {
		this.publicNotiSeqno = publicNotiSeqno;
	}
	public int getNotiCnt() {
		return notiCnt;
	}
	public void setNotiCnt(int notiCnt) {
		this.notiCnt = notiCnt;
	}
	public int getPrptCnt() {
		return prptCnt;
	}
	public void setPrptCnt(int prptCnt) {
		this.prptCnt = prptCnt;
	}
	public int getSalesCnt() {
		return salesCnt;
	}
	public void setSalesCnt(int salesCnt) {
		this.salesCnt = salesCnt;
	}
	public int getSchdlCnt() {
		return schdlCnt;
	}
	public void setSchdlCnt(int schdlCnt) {
		this.schdlCnt = schdlCnt;
	}
	public int getRcpntCnt() {
		return rcpntCnt;
	}
	public void setRcpntCnt(int rcpntCnt) {
		this.rcpntCnt = rcpntCnt;
	}
}