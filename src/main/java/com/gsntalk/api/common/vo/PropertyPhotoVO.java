package com.gsntalk.api.common.vo;

public class PropertyPhotoVO {
	private long memSeqno;
	private long prptSeqno;
	
	private String smplSmrDscr;
	private String dtlExplntnDscr;
	private String matterPortLinkUrl;
	private String prvtMemoDscr;
	
	private String uploadFileNm;
	private String saveFileNm;
	private String fileUrl;
	private String repPhotoYn;
	
	public long getPrptSeqno() {
		return prptSeqno;
	}
	public void setPrptSeqno(long prptSeqno) {
		this.prptSeqno = prptSeqno;
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
	public String getRepPhotoYn() {
		return repPhotoYn;
	}
	public void setRepPhotoYn(String repPhotoYn) {
		this.repPhotoYn = repPhotoYn;
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
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
}