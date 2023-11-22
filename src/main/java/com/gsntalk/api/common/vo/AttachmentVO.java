package com.gsntalk.api.common.vo;

public class AttachmentVO {

	private long memSeqno;
	private String attchFileGbCd;
	private String uploadFileNm;
	private String saveFileNm;
	private String fileUrl;
	
	private long knwldgIndCmplxSeqno;
	private String knwldgCmplxAtchImgTypCd;
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getAttchFileGbCd() {
		return attchFileGbCd;
	}
	public void setAttchFileGbCd(String attchFileGbCd) {
		this.attchFileGbCd = attchFileGbCd;
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
	public long getKnwldgIndCmplxSeqno() {
		return knwldgIndCmplxSeqno;
	}
	public void setKnwldgIndCmplxSeqno(long knwldgIndCmplxSeqno) {
		this.knwldgIndCmplxSeqno = knwldgIndCmplxSeqno;
	}
	public String getKnwldgCmplxAtchImgTypCd() {
		return knwldgCmplxAtchImgTypCd;
	}
	public void setKnwldgCmplxAtchImgTypCd(String knwldgCmplxAtchImgTypCd) {
		this.knwldgCmplxAtchImgTypCd = knwldgCmplxAtchImgTypCd;
	}
}