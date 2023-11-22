package com.gsntalk.api.common.vo;

import com.gsntalk.api.common.extend.PagingVO;

public class CommunityPostVO extends PagingVO {

    private long comuSeqno;
    private long memSeqno;

    private String comuNotiYn;
    private String comuTypeCd;

    private String memName;
    private String prflImgUrl;
    private String comuPrefCd; // 커뮤니티머리말코드 ( F:자유글, A:질문/답변, R:리뷰, K:후기 )
    private String comuPrefNm;
    private String comuTitle;
    private String comuContents;

    private String comuVideoUrl;

    private String repImgUrl;

    private int comuViewCnt;
    private int comuLikeCnt;
    private int comuReplyCnt;

    private String regDttm;
    private String modDttm;
    private long modMemSeqno;
    private String delYn;

    private String cntType; // R : 댓글, L : 좋아요, V : 조회수

    private String cntState; // P : +1 , M : -1

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

	public String getComuNotiYn() {
		return comuNotiYn;
	}

	public void setComuNotiYn(String comuNotiYn) {
		this.comuNotiYn = comuNotiYn;
	}

	public String getComuTypeCd() {
		return comuTypeCd;
	}

	public void setComuTypeCd(String comuTypeCd) {
		this.comuTypeCd = comuTypeCd;
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

	public String getComuPrefCd() {
		return comuPrefCd;
	}

	public void setComuPrefCd(String comuPrefCd) {
		this.comuPrefCd = comuPrefCd;
	}

	public String getComuPrefNm() {
		return comuPrefNm;
	}

	public void setComuPrefNm(String comuPrefNm) {
		this.comuPrefNm = comuPrefNm;
	}

	public String getComuTitle() {
		return comuTitle;
	}

	public void setComuTitle(String comuTitle) {
		this.comuTitle = comuTitle;
	}

	public String getComuContents() {
		return comuContents;
	}

	public void setComuContents(String comuContents) {
		this.comuContents = comuContents;
	}

	public String getComuVideoUrl() {
		return comuVideoUrl;
	}

	public void setComuVideoUrl(String comuVideoUrl) {
		this.comuVideoUrl = comuVideoUrl;
	}

	public String getRepImgUrl() {
		return repImgUrl;
	}

	public void setRepImgUrl(String repImgUrl) {
		this.repImgUrl = repImgUrl;
	}

	public int getComuViewCnt() {
		return comuViewCnt;
	}

	public void setComuViewCnt(int comuViewCnt) {
		this.comuViewCnt = comuViewCnt;
	}

	public int getComuLikeCnt() {
		return comuLikeCnt;
	}

	public void setComuLikeCnt(int comuLikeCnt) {
		this.comuLikeCnt = comuLikeCnt;
	}

	public int getComuReplyCnt() {
		return comuReplyCnt;
	}

	public void setComuReplyCnt(int comuReplyCnt) {
		this.comuReplyCnt = comuReplyCnt;
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

	public String getCntType() {
		return cntType;
	}

	public void setCntType(String cntType) {
		this.cntType = cntType;
	}

	public String getCntState() {
		return cntState;
	}

	public void setCntState(String cntState) {
		this.cntState = cntState;
	}
}
