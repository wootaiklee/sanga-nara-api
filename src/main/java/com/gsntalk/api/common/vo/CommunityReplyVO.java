package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class CommunityReplyVO extends PagingVO {

    private long comuReSeqno;
    private long comuSeqno;
    private long memSeqno;
    private String memName;
    private String prflImgUrl;
    private long toMemSeqno;
    private String toMemName;
    private String comuReContents;
    private String regDttm;
    private String modDttm;
    private long modMemSeqno;
    private String delYn;
    
	public long getComuReSeqno() {
		return comuReSeqno;
	}
	public void setComuReSeqno(long comuReSeqno) {
		this.comuReSeqno = comuReSeqno;
	}
	public long getComuSeqno() {
		return comuSeqno;
	}
	public void setComuSeqno(long comuSeqno) {
		this.comuSeqno = comuSeqno;
	}
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public String getPrflImgUrl() {
		return prflImgUrl;
	}
	public void setPrflImgUrl(String prflImgUrl) {
		this.prflImgUrl = prflImgUrl;
	}
	public long getToMemSeqno() {
		return toMemSeqno;
	}
	public void setToMemSeqno(long toMemSeqno) {
		this.toMemSeqno = toMemSeqno;
	}
	public String getToMemName() {
		return toMemName;
	}
	public void setToMemName(String toMemName) {
		this.toMemName = toMemName;
	}
	public String getComuReContents() {
		return comuReContents;
	}
	public void setComuReContents(String comuReContents) {
		this.comuReContents = comuReContents;
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
	public long getModMemSeqno() {
		return modMemSeqno;
	}
	public void setModMemSeqno(long modMemSeqno) {
		this.modMemSeqno = modMemSeqno;
	}
	public String getDelYn() {
		return delYn;
	}
	public void setDelYn(String delYn) {
		this.delYn = delYn;
	}
}
