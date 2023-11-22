package com.gsntalk.api.apis.knwldgIndCmplx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.apis.property.PropertyDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.AttachmentVO;
import com.gsntalk.api.common.vo.KnowledgeIndustryComplexVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "KnwldgIndCmplxService" )
public class KnwldgIndCmplxService extends CommonService {

	@Autowired
	private KnwldgIndCmplxDAO knwldgIndCmplxDAO;
	
	@Autowired
	private PropertyDAO propertyDAO;
	
	public KnwldgIndCmplxService() {
		super( KnwldgIndCmplxService.class );
	}
	
	/**
	 * 지식산업센터 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param knwldgIndCmplxSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getKnwldgIndCmplxDtlItem( long memSeqno, long knwldgIndCmplxSeqno )throws Exception {
		KnowledgeIndustryComplexVO knowledgeIndustryComplexVO = knwldgIndCmplxDAO.getKnwldgIndCmplxDtlItem( knwldgIndCmplxSeqno );
		if( knowledgeIndustryComplexVO == null ) {
			// 유효하지 않은 지식산업센터
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_KNWLDG_IND_CMPLX, "지식산업센터 정보를 찾을 수 없음, knwldgIndCmplxSeqno -> " + knwldgIndCmplxSeqno );
		}
		
		// 조감도 이미지 정보
		JSONObject wmapImgItem = new JSONObject();
		wmapImgItem.put( "imgUrl", knowledgeIndustryComplexVO.getVwmapImgUrl() );
		wmapImgItem.put( "orgFileNm", knowledgeIndustryComplexVO.getOrgFileNm() );
		
		JSONObject imgItem = null;
		
		// 특장점 이미지 URL 목록조회
		List<AttachmentVO> featrImgUrlList = knwldgIndCmplxDAO.getKnwldgIndCmplxImgUrlList( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR );
		if( GsntalkUtil.isEmptyList( featrImgUrlList ) ) {
			featrImgUrlList = new ArrayList<AttachmentVO>();
		}
		JSONArray featrImgUrlItems = new JSONArray();
		for( AttachmentVO vo : featrImgUrlList ) {
			
			imgItem = new JSONObject();
			imgItem.put( "imgUrl", vo.getFileUrl() );
			imgItem.put( "orgFileNm", vo.getUploadFileNm() );
			
			featrImgUrlItems.add( imgItem );
		}
		
		// 층별도면 이미지 URL 목록조회
		List<AttachmentVO> frmapImgUrlList = knwldgIndCmplxDAO.getKnwldgIndCmplxImgUrlList( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP );
		if( GsntalkUtil.isEmptyList( frmapImgUrlList ) ) {
			frmapImgUrlList = new ArrayList<AttachmentVO>();
		}
		JSONArray frmapImgUrlItems = new JSONArray();
		for( AttachmentVO vo : frmapImgUrlList ) {
			imgItem = new JSONObject();
			imgItem.put( "imgUrl", vo.getFileUrl() );
			imgItem.put( "orgFileNm", vo.getUploadFileNm() );
			
			frmapImgUrlItems.add( imgItem );
		}
		
		JSONObject item = new JSONObject();
		item.put( "bldNm",				knowledgeIndustryComplexVO.getBldNm() );
		item.put( "addr",				knowledgeIndustryComplexVO.getAddr() );
		item.put( "addrShortNm",		knowledgeIndustryComplexVO.getAddrShortNm() );
		item.put( "lat",				knowledgeIndustryComplexVO.getLat() );
		item.put( "lng",				knowledgeIndustryComplexVO.getLng() );
		item.put( "cmpltnDate",			knowledgeIndustryComplexVO.getCmpltnDate() );
		item.put( "askSalesMinPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskSalesMinPrc() ) );
		item.put( "askSalesAvgPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskSalesAvgPrc() ) );
		item.put( "askSalesMaxPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskSalesMaxPrc() ) );
		item.put( "askLeaseMinPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskLeaseMinPrc() ) );
		item.put( "askLeaseAvgPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskLeaseAvgPrc() ) );
		item.put( "askLeaseMaxPrc",		GsntalkUtil.parseAmtToKr( knowledgeIndustryComplexVO.getAskLeaseMaxPrc() ) );
		item.put( "lndArea",			GsntalkUtil.parsePyungToMeters( knowledgeIndustryComplexVO.getLndArea() ) );
		item.put( "bldArea",			GsntalkUtil.parsePyungToMeters( knowledgeIndustryComplexVO.getBldArea() ) );
		item.put( "totFlrArea",			GsntalkUtil.parsePyungToMeters( knowledgeIndustryComplexVO.getTotFlrArea() ) );
		item.put( "minFlr",				knowledgeIndustryComplexVO.getMinFlr() );
		item.put( "maxFlr",				knowledgeIndustryComplexVO.getMaxFlr() );
		item.put( "parkingCarCnt",		knowledgeIndustryComplexVO.getParkingCarCnt() );
		item.put( "husHoldCnt",			knowledgeIndustryComplexVO.getHusHoldCnt() );
		item.put( "devCompNm",			knowledgeIndustryComplexVO.getDevCompNm() );
		item.put( "constCompNm",		knowledgeIndustryComplexVO.getConstCompNm() );
		item.put( "trfcInfo",			knowledgeIndustryComplexVO.getTrfcInfo() );
		item.put( "siteExplntn",		knowledgeIndustryComplexVO.getSiteExplntn() );
		item.put( "smplSmrDscr",		knowledgeIndustryComplexVO.getSmplSmrDscr() );
		
		item.put( "wmapImgItem",		wmapImgItem );
		item.put( "featrImgUrlItems",	featrImgUrlItems );
		item.put( "frmapImgUrlItems",	frmapImgUrlItems );
		
		return item;
	}
	
	/**
	 * 지식산업센터 등록 
	 * 
	 * @param memSeqno
	 * @param addr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param bldNm
	 * @param cmpltnDate
	 * @param askSalesMinPrc
	 * @param askSalesAvgPrc
	 * @param askSalesMaxPrc
	 * @param askLeaseMinPrc
	 * @param askLeaseAvgPrc
	 * @param askLeaseMaxPrc
	 * @param lndArea
	 * @param bldArea
	 * @param totFlrArea
	 * @param minFlr
	 * @param maxFlr
	 * @param parkingCarCnt
	 * @param husHoldCnt
	 * @param devCompNm
	 * @param constCompNm
	 * @param trfcInfo
	 * @param siteExplntn
	 * @param smplSmrDscr
	 * @param vwmapFile
	 * @param featrFiles
	 * @param frmapFiles
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void registerKnwldgIndCmplxItem( long memSeqno, String addr, String addrShortNm, double lat, double lng, String bldNm, String cmpltnDate, long askSalesMinPrc, long askSalesAvgPrc, long askSalesMaxPrc, long askLeaseMinPrc, long askLeaseAvgPrc, long askLeaseMaxPrc,
			double lndArea, double bldArea, double totFlrArea, int minFlr, int maxFlr, int parkingCarCnt, int husHoldCnt, String devCompNm, String constCompNm, String trfcInfo, String siteExplntn, String smplSmrDscr, MultipartFile vwmapFile, List<MultipartFile> featrFiles, List<MultipartFile> frmapFiles )throws Exception {
		
		// 조감도 이미지 파일 검증
		if( vwmapFile == null || vwmapFile.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT, "vwmapFile" );
		}
		String orgFileNm = vwmapFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "vwmapFile -> " + orgFileNm );
		}
		
		// 특장점 이미지 파일 검증
		if( featrFiles != null && featrFiles.size() > 5 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "featrFiles" );
		}
		for( MultipartFile file : featrFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "featrFiles -> " + orgFileNm );
			}
		}
		
		// 층별도면 이미지 파일 검증
		if( frmapFiles != null && frmapFiles.size() > 50 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "frmapFiles" );
		}
		for( MultipartFile file : frmapFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "frmapFiles -> " + orgFileNm );
			}
		}
		
		
		// 지식산업센터 등록
		int nextNum = propertyDAO.getNextKPrptRegNum();
		long knwldgIndCmplxSeqno = knwldgIndCmplxDAO.registerKnwldgIndCmplxItem( memSeqno, GsntalkUtil.createKPrptRegno( nextNum ), addr, addrShortNm, lat, lng, bldNm, cmpltnDate, askSalesMinPrc, askSalesAvgPrc, askSalesMaxPrc, askLeaseMinPrc, askLeaseAvgPrc, askLeaseMaxPrc,
				lndArea, bldArea, totFlrArea, minFlr, maxFlr, parkingCarCnt, husHoldCnt, devCompNm, constCompNm, trfcInfo, siteExplntn, smplSmrDscr );
		
		// 조감도 이미지 파일 S3 업로드 및 등록
		JSONObject uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_VWMAP, vwmapFile );
		knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_VWMAP, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		
		// 특장점 이미지 파일 S3 업로드 및 등록
		for( MultipartFile file : featrFiles ) {
			uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR, file );
			knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		}
		
		// 층별도면 이미지 파일 S3 업로드 및 등록
		for( MultipartFile file : frmapFiles ) {
			uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP, file );
			knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		}
	}
	
	/**
	 * Admin - 지식산업센터 목록조회 ( 페이징 )
	 * 
	 * @param regDtSrchTyp
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getKnwldgIndCmplxItems( String regDtSrchTyp, String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<KnowledgeIndustryComplexVO> knwldgIndCmplxList = knwldgIndCmplxDAO.getKnwldgIndCmplxItems( regDtSrchTyp, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( knwldgIndCmplxList ) ) {
			knwldgIndCmplxList = new ArrayList<KnowledgeIndustryComplexVO>();
		}else {
			totList = knwldgIndCmplxList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( KnowledgeIndustryComplexVO vo : knwldgIndCmplxList ) {
			item = new JSONObject();
			
			item.put( "no", vo.getRownum() );
			item.put( "knwldgIndCmplxSeqno", vo.getKnwldgIndCmplxSeqno() );
			item.put( "bldNm", vo.getBldNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "cmpltnDate", vo.getCmpltnDate() );
			item.put( "askSalesMinPrc", vo.getAskSalesMinPrc() / 10000 );
			item.put( "askSalesAvgPrc", vo.getAskSalesAvgPrc() / 10000 );
			item.put( "askSalesMaxPrc", vo.getAskSalesMaxPrc() / 10000 );
			item.put( "askLeaseMinPrc", vo.getAskLeaseMinPrc() / 10000 );
			item.put( "askLeaseAvgPrc", vo.getAskLeaseAvgPrc() / 10000 );
			item.put( "askLeaseMaxPrc", vo.getAskLeaseMaxPrc() / 10000 );
			item.put( "frmapYn", vo.getFrmapYn() );
			item.put( "smplSmrDscr", vo.getSmplSmrDscr() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 지식산업센터 삭제
	 * 
	 * @param knwldgIndCmplxSeqnoItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deleteKnwldgIndCmplxItems( JSONArray knwldgIndCmplxSeqnoItems )throws Exception {
		for( int i = 0; i < knwldgIndCmplxSeqnoItems.size(); i ++ ) {
			knwldgIndCmplxDAO.deleteKnwldgIndCmplxItems( GsntalkUtil.getLong( knwldgIndCmplxSeqnoItems.get( i ) ) );
		}
	}
	
	/**
	 * Admin - 지식산업센터 수정 
	 * 
	 * @param knwldgIndCmplxSeqno
	 * @param addr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param bldNm
	 * @param cmpltnDate
	 * @param askSalesMinPrc
	 * @param askSalesAvgPrc
	 * @param askSalesMaxPrc
	 * @param askLeaseMinPrc
	 * @param askLeaseAvgPrc
	 * @param askLeaseMaxPrc
	 * @param lndArea
	 * @param bldArea
	 * @param totFlrArea
	 * @param minFlr
	 * @param maxFlr
	 * @param parkingCarCnt
	 * @param husHoldCnt
	 * @param devCompNm
	 * @param constCompNm
	 * @param trfcInfo
	 * @param siteExplntn
	 * @param smplSmrDscr
	 * @param vwmapFile
	 * @param delFeatrUrls
	 * @param featrFiles
	 * @param delFrmapUrls
	 * @param frmapFiles
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void updateKnwldgIndCmplxItem( long knwldgIndCmplxSeqno, String addr, String addrShortNm, double lat, double lng, String bldNm, String cmpltnDate, long askSalesMinPrc, long askSalesAvgPrc, long askSalesMaxPrc, long askLeaseMinPrc, long askLeaseAvgPrc, long askLeaseMaxPrc,
			double lndArea, double bldArea, double totFlrArea, int minFlr, int maxFlr, int parkingCarCnt, int husHoldCnt, String devCompNm, String constCompNm, String trfcInfo, String siteExplntn, String smplSmrDscr, MultipartFile vwmapFile, JSONArray delFeatrUrls, List<MultipartFile> featrFiles, JSONArray delFrmapUrls, List<MultipartFile> frmapFiles )throws Exception {
		
		String orgFileNm = "";
		String uploadFileFormat = "";
		
		// 조감도 이미지 파일 있으면 검증
		if( vwmapFile != null && vwmapFile.getSize() > 0L ) {
			orgFileNm = vwmapFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "vwmapFile -> " + orgFileNm );
			}
		}
		
		// 특장점 이미지 파일이 있으면 검증
		if( !GsntalkUtil.isEmptyList( featrFiles ) ) {
			List<AttachmentVO> featrFileList = knwldgIndCmplxDAO.getKnwldgIndCmplxImgUrlList( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR );
			if( featrFileList.size() - delFeatrUrls.size() + featrFiles.size() > 5 ) {
				// 첨부가능한 최대 파일수량 초과
				throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "featrFiles" );
			}
			
			for( MultipartFile file : featrFiles ) {
				orgFileNm = file.getOriginalFilename();
				uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
				if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
					// 허용되지 않은 파일 포맷
					throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "featrFiles -> " + orgFileNm );
				}
			}
		}
		
		// 층별도면 이미지 파일이 있으면 검증
		if( !GsntalkUtil.isEmptyList( frmapFiles ) ) {
			List<AttachmentVO> frmapFileList = knwldgIndCmplxDAO.getKnwldgIndCmplxImgUrlList( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP );
			if( frmapFileList.size() - delFrmapUrls.size() + frmapFiles.size() > 50 ) {
				// 첨부가능한 최대 파일수량 초과
				throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "featrFiles" );
			}
			
			for( MultipartFile file : frmapFiles ) {
				orgFileNm = file.getOriginalFilename();
				uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
				if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
					// 허용되지 않은 파일 포맷
					throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "frmapFiles -> " + orgFileNm );
				}
			}
		}
		
		
		// 지식산업센터 수정
		knwldgIndCmplxDAO.updateKnwldgIndCmplxItem( knwldgIndCmplxSeqno, addr, addrShortNm, lat, lng, bldNm, cmpltnDate, askSalesMinPrc, askSalesAvgPrc, askSalesMaxPrc, askLeaseMinPrc, askLeaseAvgPrc, askLeaseMaxPrc,
				lndArea, bldArea, totFlrArea, minFlr, maxFlr, parkingCarCnt, husHoldCnt, devCompNm, constCompNm, trfcInfo, siteExplntn, smplSmrDscr );
		
		JSONObject uploadItem = null;
		
		// 조감도 이미지 파일 있으면 삭제 후 재등록
		if( vwmapFile != null && vwmapFile.getSize() > 0L ) {
			// 지식산업센터 이미지 - 조감도 삭제
			knwldgIndCmplxDAO.deleteKnwldgIndCmplxVwImage( knwldgIndCmplxSeqno );
			
			// 조감도 이미지 파일 S3 업로드 및 등록
			uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_VWMAP, vwmapFile );
			knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_VWMAP, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		}
		
		// 삭제할 특장점 이미지파일 삭제
		for( int i = 0; i < delFeatrUrls.size(); i ++ ) {
			knwldgIndCmplxDAO.deleteKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR, GsntalkUtil.getString( delFeatrUrls.get( i ) ) );
		}
		// 특장점 이미지 파일 S3 업로드 및 등록
		for( MultipartFile file : featrFiles ) {
			uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR, file );
			knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FEATR, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		}
		
		// 삭제할 층별도면 이미지파일 삭제
		for( int i = 0; i < delFrmapUrls.size(); i ++ ) {
			knwldgIndCmplxDAO.deleteKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP, GsntalkUtil.getString( delFrmapUrls.get( i ) ) );
		}
		// 층별도면 이미지 파일 S3 업로드 및 등록
		for( MultipartFile file : frmapFiles ) {
			uploadItem = gsntalkS3Util.uploadKnwldgIndCmplxmageFile( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP, file );
			knwldgIndCmplxDAO.registerKnwldgIndCmplxImage( knwldgIndCmplxSeqno, GsntalkConstants.KNWLDG_CMPLX_ATCH_IMG_TYP_FRMAP, GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) ), GsntalkUtil.getString( uploadItem.get( "fileUrl" ) ));
		}
	}
}