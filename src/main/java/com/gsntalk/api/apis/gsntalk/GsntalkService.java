package com.gsntalk.api.apis.gsntalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.CommonCodeVO;
import com.gsntalk.api.common.vo.EstateBrokerOfficeVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.common.vo.StandardRegionVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "GsntalkService" )
public class GsntalkService extends CommonService {

	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	public GsntalkService() {
		super( GsntalkService.class );
	}
	
	/**
	 * 공통코드 목록조회 ( 여러코드 한번에 조회 )
	 * 
	 * @param codeItems
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getComnCdItems( JSONArray codeItems )throws Exception {
		JSONObject item = new JSONObject();
		
		JSONArray items = null;
		JSONObject subItem = null;
		
		String comnCd = null;
		List<CommonCodeVO> comnCdList = null;
		for( int i = 0; i < codeItems.size(); i ++ ) {
			comnCd = GsntalkUtil.getString( codeItems.get( i ) );
			
			comnCdList = gsntalkDAO.getComnCdList( comnCd );
			items = new JSONArray();
			
			for( CommonCodeVO vo : comnCdList ) {
				subItem = new JSONObject();
				
				subItem.put( "comnCd", vo.getComnCd() );
				subItem.put( "itemCd", vo.getItemCd() );
				subItem.put( "itemCdNm", vo.getItemCdNm() );
				subItem.put( "rmk", vo.getRmk() );
				
				items.add( subItem );
			}
			
			item.put( comnCd , items );
		}
		
		return item;
	}
	
	/**
	 * 중개사 사무소 검색
	 * 
	 * @param srchVal
	 * @param lastRnum
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> srchEstBlkOfcItems( String srchVal, int lastRnum )throws Exception {
		JSONArray items = new JSONArray();
		String hasMoreYn = GsntalkConstants.NO;
		
		if( srchVal.length() < 2 ) {
			Map<String, Object> resMap = new HashMap<String, Object>();
			resMap.put( "items", items );
			resMap.put( "hasMoreYn", hasMoreYn );
			return resMap;
		}
		
		
		List<EstateBrokerOfficeVO> estateBrokerOfficeList = gsntalkDAO.srchEstBlkOfcItems( srchVal, lastRnum );
		if( GsntalkUtil.isEmptyList( estateBrokerOfficeList ) ) {
			estateBrokerOfficeList = new ArrayList<EstateBrokerOfficeVO>();
		}else {
			EstateBrokerOfficeVO lastVO = estateBrokerOfficeList.get( estateBrokerOfficeList.size() -1 );
			if( lastVO.getTotalCount() > lastVO.getRownum() ) {
				hasMoreYn = GsntalkConstants.YES;
			}
		}
		
		JSONObject item = null;
		for( EstateBrokerOfficeVO vo : estateBrokerOfficeList ) {
			item = new JSONObject();
			
			item.put( "rnum", vo.getRownum() );
			item.put( "estBrkOfcSeqno", vo.getEstBrkOfcSeqno() );
			item.put( "ofcNm", vo.getOfcNm() );
			item.put( "reprNm", vo.getReprNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "openRegNo", vo.getOpenRegNo() );
			item.put( "openRegDate", vo.getOpenRegDate() );
			item.put( "telNo", vo.getTelNo() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "hasMoreYn", hasMoreYn );
		return resMap;
	}
	
	/**
	 * 단일 공통코드 목록조회 ( 상/하위코드 목록조회 )
	 * 
	 * @param upItemCd
	 * @param itemCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray getSingleComnCdItems( String upItemCd, String itemCd )throws Exception {
		this.LOGGER.info( "####### upItemCd : " + upItemCd );
		this.LOGGER.info( "####### itemCd : " + itemCd );
		
		List<CommonCodeVO> comnCdList = gsntalkDAO.getSingleComnCdItems( upItemCd, itemCd );
		if( GsntalkUtil.isEmptyList( comnCdList ) ) {
			comnCdList = new ArrayList<CommonCodeVO>();
		}
		this.LOGGER.info( "####### comnCdList : " + comnCdList.size() );
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		for( CommonCodeVO vo : comnCdList ) {
			item = new JSONObject();
			
			item.put( "upItemCd", vo.getUpItemCd() );
			item.put( "comnCd", vo.getComnCd() );
			item.put( "itemCd", vo.getItemCd() );
			item.put( "itemCdNm", vo.getItemCdNm() );
			item.put( "rmk", vo.getRmk() );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * 기본 지역 및 지역코드 조회
	 * 
	 * @param sidoCd
	 * @param sggCd
	 * @param umdCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray searchStandardRegionItems( String sidoCd, String sggCd, String umdCd )throws Exception {
		List<StandardRegionVO> standardRegionList = gsntalkDAO.searchStandardRegionList( sidoCd, sggCd, umdCd );
		if( GsntalkUtil.isEmptyList( standardRegionList ) ) {
			standardRegionList = new ArrayList<StandardRegionVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		for( StandardRegionVO vo : standardRegionList ) {
			item = new JSONObject();
			
			item.put( "sidoCd", vo.getSidoCd() );
			item.put( "sggCd", vo.getSggCd() );
			item.put( "umdCd", vo.getUmdCd() );
			item.put( "riCd", vo.getRiCd() );
			item.put( "localNm", vo.getLocalLowNm() );
			item.put( "childExstsYn", vo.getSubCnt() < 1 ? "N" : "Y" );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * 기본지역 주소 명칭 검색
	 * 
	 * @param srchVal
	 * @param matchTag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray searchStandardRegionAddrNmItems( String srchVal, String matchTag )throws Exception {
		List<StandardRegionVO> standardRegionList = gsntalkDAO.searchStandardRegionAddrNmList( srchVal, matchTag );
		if( GsntalkUtil.isEmptyList( standardRegionList ) ) {
			standardRegionList = new ArrayList<StandardRegionVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		for( StandardRegionVO vo : standardRegionList ) {
			item = new JSONObject();
			
			item.put( "addrNm", vo.getAddrNm() );
			item.put( "wrappedAddrNm", vo.getWrappedAddrNm() );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * 임시 이미지파일 업로드
	 * 
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray uploadTmpImgFiles( List<MultipartFile> files )throws Exception {
		String orgFileNm = "";
		String uploadFileFormat = "";
		
		for( MultipartFile file : files ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		JSONArray items = new JSONArray();
		for( MultipartFile file : files ) {
			items.add( gsntalkS3Util.uploadTmpImgFile( file ) );
		}
		
		return items;
	}
	
	/**
	 * 서비스이용약관 동의여부 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject checkForServiceTermsAgreeItem( long memSeqno )throws Exception {
		JSONObject item = new JSONObject();
		item.put( "allAgreeYn",				GsntalkConstants.NO );
		item.put( "allReqItemAgreeYn",		GsntalkConstants.NO );
		item.put( "age14OvrAgreYn",			GsntalkConstants.NO );
		item.put( "svcUseAgreYn",			GsntalkConstants.NO );
		item.put( "prsnlInfAgreYn",			GsntalkConstants.NO );
		item.put( "mktRcvAgreYn",			GsntalkConstants.NO );
		
		MemberVO memberVO = gsntalkDAO.checkForServiceTermsAgreeItem( memSeqno );
		if( memberVO != null ) {
			if(
					GsntalkConstants.YES.equals( memberVO.getAge14OvrAgreYn() )
					&&
					GsntalkConstants.YES.equals( memberVO.getSvcUseAgreYn() )
					&&
					GsntalkConstants.YES.equals( memberVO.getPrsnlInfAgreYn() )
			) {
				item.put( "allReqItemAgreeYn",		GsntalkConstants.YES );
				
				if( GsntalkConstants.YES.equals( memberVO.getMktRcvAgreYn() ) ) {
					item.put( "allAgreeYn",				GsntalkConstants.YES );
				}
			}
			
			if( GsntalkConstants.YES.equals( memberVO.getAge14OvrAgreYn() ) ) {
				item.put( "age14OvrAgreYn",			GsntalkConstants.YES );
			}
			if( GsntalkConstants.YES.equals( memberVO.getSvcUseAgreYn() ) ) {
				item.put( "svcUseAgreYn",			GsntalkConstants.YES );
			}
			if( GsntalkConstants.YES.equals( memberVO.getPrsnlInfAgreYn() ) ) {
				item.put( "prsnlInfAgreYn",			GsntalkConstants.YES );
			}
			if( GsntalkConstants.YES.equals( memberVO.getMktRcvAgreYn() ) ) {
				item.put( "mktRcvAgreYn",			GsntalkConstants.YES );
			}
		}
	
		return item;
	}
	
	/**
	 * 서울/인천/경기지역 주소 명칭 검색 ( 시도->시군구 단위명칭 까지 )
	 * 
	 * @param srchVal
	 * @param matchTag
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray searchMetroRegionAddrNmItems( String srchVal )throws Exception {
		List<StandardRegionVO> standardRegionList = gsntalkDAO.searchMetroRegionAddrNmItems( srchVal );
		if( GsntalkUtil.isEmptyList( standardRegionList ) ) {
			standardRegionList = new ArrayList<StandardRegionVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		for( StandardRegionVO vo : standardRegionList ) {
			item = new JSONObject();
			
			item.put( "addrNm", vo.getAddrNm() );
			
			items.add( item );
		}
		
		return items;
	}
}