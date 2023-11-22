package com.gsntalk.api.common.vo;

import org.json.simple.JSONArray;

import com.gsntalk.api.common.extend.PagingVO;

public class FavRecentPrptVO extends PagingVO {

	private long memSeqno;
	
	private String prptTyp;
	private long seqno;
	private String addr;
	private String addrShortNm;
	private double lat;
	private double lng;
	private String favYn;
	
	private String estateTypCd;
	private String tranTypGbCd;
	private String tranTypGbNm;
	private long dealAmt;
	private long dpstAmt;
	private int montRentAmt;
	private int flr;
	private int allFlr;
	private int minFlr;
	private int maxFlr;
	private double splyArea;
	private double prvArea;
	private double lndArea;
	private double totFlrArea;
	private String smplSmrDscr;
	private String repImgUrl;
	
	private String bldNm;
	private String poStatGbCd;
	
	private JSONArray resentItems;
	
	public long getMemSeqno() {
		return memSeqno;
	}
	public void setMemSeqno(long memSeqno) {
		this.memSeqno = memSeqno;
	}
	public String getPrptTyp() {
		return prptTyp;
	}
	public void setPrptTyp(String prptTyp) {
		this.prptTyp = prptTyp;
	}
	public long getSeqno() {
		return seqno;
	}
	public void setSeqno(long seqno) {
		this.seqno = seqno;
	}
	public String getAddrShortNm() {
		return addrShortNm;
	}
	public void setAddrShortNm(String addrShortNm) {
		this.addrShortNm = addrShortNm;
	}
	public String getFavYn() {
		return favYn;
	}
	public void setFavYn(String favYn) {
		this.favYn = favYn;
	}
	public String getTranTypGbNm() {
		return tranTypGbNm;
	}
	public void setTranTypGbNm(String tranTypGbNm) {
		this.tranTypGbNm = tranTypGbNm;
	}
	public int getFlr() {
		return flr;
	}
	public void setFlr(int flr) {
		this.flr = flr;
	}
	public int getAllFlr() {
		return allFlr;
	}
	public void setAllFlr(int allFlr) {
		this.allFlr = allFlr;
	}
	public int getMinFlr() {
		return minFlr;
	}
	public void setMinFlr(int minFlr) {
		this.minFlr = minFlr;
	}
	public int getMaxFlr() {
		return maxFlr;
	}
	public void setMaxFlr(int maxFlr) {
		this.maxFlr = maxFlr;
	}
	public String getSmplSmrDscr() {
		return smplSmrDscr;
	}
	public void setSmplSmrDscr(String smplSmrDscr) {
		this.smplSmrDscr = smplSmrDscr;
	}
	public String getBldNm() {
		return bldNm;
	}
	public void setBldNm(String bldNm) {
		this.bldNm = bldNm;
	}
	public String getPoStatGbCd() {
		return poStatGbCd;
	}
	public void setPoStatGbCd(String poStatGbCd) {
		this.poStatGbCd = poStatGbCd;
	}
	public JSONArray getResentItems() {
		return resentItems;
	}
	public void setResentItems(JSONArray resentItems) {
		this.resentItems = resentItems;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
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
	public String getTranTypGbCd() {
		return tranTypGbCd;
	}
	public void setTranTypGbCd(String tranTypGbCd) {
		this.tranTypGbCd = tranTypGbCd;
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
	public String getEstateTypCd() {
		return estateTypCd;
	}
	public void setEstateTypCd(String estateTypCd) {
		this.estateTypCd = estateTypCd;
	}
	public long getDealAmt() {
		return dealAmt;
	}
	public void setDealAmt(long dealAmt) {
		this.dealAmt = dealAmt;
	}
	public String getRepImgUrl() {
		return repImgUrl;
	}
	public void setRepImgUrl(String repImgUrl) {
		this.repImgUrl = repImgUrl;
	}
}