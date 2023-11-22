package com.gsntalk.api.apis.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gsntalk.api.apis.gsntalk.GsntalkDAO;
import com.gsntalk.api.apis.member.MemberDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.EstateBrokerOfficeVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.common.vo.PropertyOptionVO;
import com.gsntalk.api.common.vo.PropertyPhotoVO;
import com.gsntalk.api.common.vo.PropertySuggestRequestVO;
import com.gsntalk.api.common.vo.PropertyVO;
import com.gsntalk.api.common.vo.RegistrationTmpDataStepVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkExcelUtil;
import com.gsntalk.api.util.GsntalkMathUtil;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Service( "PropertyService" )
public class PropertyService extends CommonService {

	@Autowired
	private PropertyDAO propertyDAO;
	
	@Autowired
	private MemberDAO memberDAO;
	
	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	public PropertyService() {
		super( PropertyService.class );
	}
	
	/**
	 * 관심매물 등록
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void registerFavProperty( long memSeqno, long prptSeqno )throws Exception {
		int c = propertyDAO.isAvailProperty( prptSeqno );
		if( c == 0 ) {
			// 유효하지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AVAIL_PROPERTY );
		}
		
		c = propertyDAO.isExistsFavProperty( memSeqno, prptSeqno );
		if( c == 0 ) {
			propertyDAO.registerFavProperty( memSeqno, prptSeqno );
		}
	}
	
	/**
	 * 관심매물 해제
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void releaseFavProperty( long memSeqno, long prptSeqno )throws Exception {
		int c = propertyDAO.isExistsFavProperty( memSeqno, prptSeqno );
		if( c > 0 ) {
			propertyDAO.releaseFavProperty( memSeqno, prptSeqno );
		}
	}
	
	/**
	 * 중개사 매물목록 조회 ( 중개사무소 상세페이지 )
	 * 
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param tranTypGbCd
	 * @param sortItem
	 * @param sortTyp
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkOfcPropertyItems( long memSeqno, long estBrkMemOfcSeqno, String tranTypGbCd, String sortItem, String sortTyp )throws Exception {
		EstateBrokerOfficeVO estateBrokerOfficeVO = memberDAO.getEstBrkOfcItem( estBrkMemOfcSeqno );
		if( estateBrokerOfficeVO == null ) {
			// 선택한 중개사무소 정보 확인불가
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_SELECTED_EST_BRK_OFC );
		}
		
		List<PropertyVO> propertyList = propertyDAO.getEstBrkOfcPropertyList( memSeqno, estBrkMemOfcSeqno, tranTypGbCd, sortItem, sortTyp );
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}
		
		JSONObject item = new JSONObject();
		item.put( "size", propertyList.size() );
		
		JSONArray propertyItems = new JSONArray();
		JSONObject propertyItem = null;
		for( PropertyVO vo : propertyList ) {
			propertyItem = new JSONObject();
			
			propertyItem.put( "prptSeqno",			vo.getPrptSeqno() );
			propertyItem.put( "estateTypGbCd",		vo.getEstateTypGbCd() );
			propertyItem.put( "estateTypGbNm",		vo.getEstateTypGbNm() );
			propertyItem.put( "estateTypCd",		vo.getEstateTypCd() );
			propertyItem.put( "estateTypNm",		vo.getEstateTypNm() );
			propertyItem.put( "tranTypGbCd",		vo.getTranTypGbCd() );
			propertyItem.put( "tranTypGbNm",		vo.getTranTypGbNm() );
			propertyItem.put( "cost",				GsntalkUtil.parseAmtToKr( vo.getCost() ) );
			propertyItem.put( "montRentAmt",		GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() ) );
			propertyItem.put( "splyArea",			GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
			propertyItem.put( "prvArea",			GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			propertyItem.put( "lndArea",			GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			propertyItem.put( "totFlrArea",			GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			propertyItem.put( "flr",				vo.getFlr() );
			propertyItem.put( "smplSmrDscr",		vo.getSmplSmrDscr() );
			propertyItem.put( "lat",				vo.getLat() );
			propertyItem.put( "lng",				vo.getLng() );
			propertyItem.put( "addrShortNm",		vo.getAddrShortNm() );
			propertyItem.put( "reprImgUrl",			GsntalkUtil.nullToEmptyString( vo.getReprImgUrl() ) );
			propertyItem.put( "favYn",				vo.getFavYn() );
			
			propertyItems.add( propertyItem );
		}
		item.put( "propertyItems", propertyItems );
		
		return item;
	}
	
	/**
	 * 매물 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getPropertyDtlItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = propertyDAO.getPropertyDtlItem( memSeqno, prptSeqno );
		if( propertyVO == null ) {
			// 유효하지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AVAIL_PROPERTY );
		}
		
		// 매물 사진 목록조회
		List<PropertyPhotoVO> propertyPhotoList = propertyDAO.getPropertyPhotoList( prptSeqno );
		if( GsntalkUtil.isEmptyList( propertyPhotoList ) ) {
			propertyPhotoList = new ArrayList<PropertyPhotoVO>();
		}
		
		/** 매물사진 URL 목록 */
		JSONArray photoItems = new JSONArray();
		for( PropertyPhotoVO vo : propertyPhotoList ) {
			photoItems.add( vo.getFileUrl() );
		}
		
		/** 기본정보 */
		JSONObject baseItem = new JSONObject();
		baseItem.put( "prptSeqno", propertyVO.getPrptSeqno() );
		baseItem.put( "prptNo", propertyVO.getPrptNo() );
		baseItem.put( "favYn", propertyVO.getFavYn() );
		baseItem.put( "addrShortNm", propertyVO.getAddrShortNm() );
		baseItem.put( "lat", propertyVO.getLat() );
		baseItem.put( "lng", propertyVO.getLng() );
		baseItem.put( "estateTypGbCd", propertyVO.getEstateTypGbCd() );
		baseItem.put( "estateTypGbNm", propertyVO.getEstateTypGbNm() );
		baseItem.put( "estateTypCd", propertyVO.getEstateTypCd() );
		baseItem.put( "estateTypNm", propertyVO.getEstateTypNm() );
		baseItem.put( "tranTypGbCd", propertyVO.getTranTypGbCd() );
		baseItem.put( "tranTypGbNm", propertyVO.getTranTypGbNm() );
		baseItem.put( "costNm", propertyVO.getCostNm() );
		baseItem.put( "cost", GsntalkUtil.parseAmtToKr( propertyVO.getCost() ) );
		baseItem.put( "dealAmtDiscsnPsblYn", propertyVO.getDealAmtDiscsnPsblYn() );
		baseItem.put( "montRentAmt", GsntalkUtil.parseAmtToKr( propertyVO.getMontRentAmt() ) );
		baseItem.put( "dispAreaNm", propertyVO.getDispAreaNm() );
		baseItem.put( "dispArea", GsntalkUtil.parsePyungToMeters( propertyVO.getDispArea() ) );
		baseItem.put( "unitCost", GsntalkUtil.parseAmtToKr( GsntalkMathUtil.divide( propertyVO.getCost(), propertyVO.getDispArea() ) ) );
		baseItem.put( "flr", propertyVO.getFlr() );
		baseItem.put( "allFlr", propertyVO.getAllFlr() );
		baseItem.put( "minFlr", propertyVO.getMinFlr() );
		baseItem.put( "maxFlr", propertyVO.getMaxFlr() );
		baseItem.put( "useCnfrmYear", propertyVO.getUseCnfrmYear() );
		baseItem.put( "monMntnceCost", GsntalkUtil.parseAmtToKr( propertyVO.getMonMntnceCost() ) );
		baseItem.put( "matterPortLinkUrl", propertyVO.getMatterPortLinkUrl() );
		
		/** 가격정보 */
		JSONObject costItem = new JSONObject();
		costItem.put( "costNm", propertyVO.getCostNm() );
		costItem.put( "cost", GsntalkUtil.parseAmtToKr( propertyVO.getCost() ) );
		costItem.put( "keyMonExstsYn", propertyVO.getKeyMonExstsYn() );
		costItem.put( "keyMonAmt", GsntalkUtil.parseAmtToKr( propertyVO.getKeyMonAmt() ) );
		costItem.put( "prmmAmt", GsntalkUtil.parseAmtToKr( propertyVO.getPrmmAmt() ) );
		costItem.put( "dealAmtDiscsnPsblYn", propertyVO.getDealAmtDiscsnPsblYn() );
		costItem.put( "montRentAmt", GsntalkUtil.parseAmtToKr( propertyVO.getMontRentAmt() ) );
		costItem.put( "unitCost", GsntalkUtil.parseAmtToKr( GsntalkMathUtil.divide( propertyVO.getCost(), propertyVO.getDispArea() ) ) );
		costItem.put( "monMntnceCost", GsntalkUtil.parseAmtToKr( propertyVO.getMonMntnceCost() ) );
		costItem.put( "mntnceItemDscr", propertyVO.getMntnceItemDscr() );
		costItem.put( "loanGbNm", propertyVO.getLoanGbNm() );
		costItem.put( "existngLeaseExstsYn", propertyVO.getExistngLeaseExstsYn() );
		costItem.put( "crntDpstAmt", GsntalkUtil.parseAmtToKr( propertyVO.getCrntDpstAmt() ) );
		costItem.put( "crntMontRentAmt", GsntalkUtil.parseAmtToKr( propertyVO.getCrntMontRentAmt() ) );
		
		/** 상세벙보 */
		JSONObject dtlItem = new JSONObject();
		dtlItem.put( "estateTypGbNm", propertyVO.getEstateTypGbNm() );
		dtlItem.put( "locationNm", propertyVO.getAddr().substring( 0, propertyVO.getAddr().indexOf( " " ) + 1 ) + propertyVO.getAddrShortNm() );
		dtlItem.put( "unregistYn", propertyVO.getUnregistYn() );
		dtlItem.put( "splyArea", GsntalkUtil.parsePyungToMeters( propertyVO.getSplyArea() ) );
		dtlItem.put( "prvArea", GsntalkUtil.parsePyungToMeters( propertyVO.getPrvArea() ) );
		dtlItem.put( "lndArea", GsntalkUtil.parsePyungToMeters( propertyVO.getLndArea() ) );
		dtlItem.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( propertyVO.getTotFlrArea() ) );
		dtlItem.put( "areaRatio", GsntalkMathUtil.divide( propertyVO.getPrvArea(), propertyVO.getSplyArea(), 3 ) * 100.0d );
		dtlItem.put( "flr", propertyVO.getFlr() );
		dtlItem.put( "allFlr", propertyVO.getAllFlr() );
		dtlItem.put( "minFlr", propertyVO.getMinFlr() );
		dtlItem.put( "maxFlr", propertyVO.getMaxFlr() );
		dtlItem.put( "movDateDiscsnPsblYn", propertyVO.getMovDateDiscsnPsblYn() );
		dtlItem.put( "psblMovDate", propertyVO.getPsblMovDate() );
		dtlItem.put( "crntSectrGbNm", propertyVO.getCrntSectrGbNm() );
		dtlItem.put( "suggstnSectrGbNm", propertyVO.getSuggstnSectrGbNm() );
		dtlItem.put( "bldDirctnGbNm", propertyVO.getBldDirctnGbNm() );
		dtlItem.put( "heatKindGbNm", propertyVO.getHeatKindGbNm() );
		dtlItem.put( "roomCnt", propertyVO.getRoomCnt() );
		dtlItem.put( "bathRoomCnt", propertyVO.getBathRoomCnt() );
		dtlItem.put( "elvFcltExstsYn", propertyVO.getElvFcltExstsYn() );
		dtlItem.put( "frhgtElvExstsYn", propertyVO.getFrhgtElvExstsYn() );
		dtlItem.put( "parkingPsblYn", propertyVO.getParkingPsblYn() );
		dtlItem.put( "parkingCost", GsntalkUtil.parseAmtToKr( propertyVO.getParkingCost() ) );
		dtlItem.put( "bldUsageGbNm", propertyVO.getBldUsageGbNm() );
		dtlItem.put( "suggstnBldUsageGbNm", propertyVO.getSuggstnBldUsageGbNm() );
		dtlItem.put( "lndCrntUsageGbNm", propertyVO.getLndCrntUsageGbNm() );
		dtlItem.put( "useCnfrmDate", propertyVO.getUseCnfrmDate() );
		dtlItem.put( "intnlStrctrTypNm", propertyVO.getIntnlStrctrTypNm() );
		dtlItem.put( "bultInYn", propertyVO.getBultInYn() );
		dtlItem.put( "intrrYn", propertyVO.getIntrrYn() );
		dtlItem.put( "wghtPerPy", propertyVO.getWghtPerPy() );
		dtlItem.put( "flrHghtTypGbNm", propertyVO.getFlrHghtTypGbNm() );
		dtlItem.put( "elctrPwrTypGbNm", propertyVO.getElctrPwrTypGbNm() );
		dtlItem.put( "cmpltExpctDate", propertyVO.getCmpltExpctDate() );
		
		// 옵션 항목유형 목록 조회
		int totOptionCnt = 0;
		
		List<PropertyOptionVO> optionItemTypList = propertyDAO.getOptionItemTypeList( prptSeqno );
		if( GsntalkUtil.isEmptyList( optionItemTypList ) ) {
			optionItemTypList = new ArrayList<PropertyOptionVO>();
		}
		
		JSONArray optionItems = new JSONArray();
		JSONObject optionItem = null;
		
		List<PropertyOptionVO> optionDtlList = null;
		JSONArray optionDtlItems = null;
		JSONObject optionDtlItem = null;
		
		for( PropertyOptionVO vo : optionItemTypList ) {
			optionItem = new JSONObject();
			
			optionItem.put( "optionItemTypGbCd", vo.getOptionItemTypGbCd() );
			optionItem.put( "optionItemTypGbNm", vo.getOptionItemTypGbNm() );
			
			// 옵션 상세항목 목록조회
			optionDtlList = propertyDAO.getOptionDtlList( prptSeqno, vo.getOptionItemTypGbCd() );
			if( GsntalkUtil.isEmptyList( optionDtlList ) ) {
				optionDtlList = new ArrayList<PropertyOptionVO>();
			}
			
			optionDtlItems = new JSONArray();
			for( PropertyOptionVO dtlVO : optionDtlList ) {
				optionDtlItem = new JSONObject();
				
				optionDtlItem.put( "optionItemTypCd", dtlVO.getOptionItemTypCd() );
				optionDtlItem.put( "optionItemTypNm", dtlVO.getOptionItemTypNm() );
				
				optionDtlItems.add( optionDtlItem );
			}
			
			optionItem.put( "optionDtlItems", optionDtlItems );
			
			optionItems.add( optionItem );
			
			totOptionCnt += optionDtlList.size();
		}
		
		// 중개 보수정보 계산 ( 분양권의 경우 거래금액 + 프리미엄, 나머지는 거래금액 기준 )
		long dealAmt = "PRESALE".equals( propertyVO.getEstateTypGbCd() ) ? propertyVO.getCost() + propertyVO.getPrmmAmt() : propertyVO.getCost();
		JSONObject cmmstnItem = GsntalkUtil.getEstateBrokerCommitionItem( propertyVO.getEstateTypCd(), propertyVO.getTranTypGbCd(), dealAmt );
		
		// 중개 부동산 정보 조회
		EstateBrokerOfficeVO estateBrokerOfficeVO = memberDAO.getEstBrkOfcItem( propertyVO.getEstBrkMemOfcSeqno() );
		JSONObject estBrkOfcItem = new JSONObject();
		estBrkOfcItem.put( "estBrkMemOfcSeqno",	estateBrokerOfficeVO.getEstBrkMemOfcSeqno() );
		estBrkOfcItem.put( "ofcNm",				estateBrokerOfficeVO.getOfcNm() );
		estBrkOfcItem.put( "reprNm",			estateBrokerOfficeVO.getReprNm() );
		estBrkOfcItem.put( "addr",				estateBrokerOfficeVO.getAddr() );
		estBrkOfcItem.put( "openRegNo",			estateBrokerOfficeVO.getOpenRegNo() );
		estBrkOfcItem.put( "reprTelNo",			GsntalkUtil.nullToEmptyString( estateBrokerOfficeVO.getTelNo() ) );
		estBrkOfcItem.put( "mobNo",				GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( estateBrokerOfficeVO.getMobNo() ) ) );
		
		// 중개사 최근 매물목록 조회 ( 30일 이내기준, 3건까지 ) 
		List<PropertyVO> propertyList = propertyDAO.getEstBrkOfcPropertyRecent3List( prptSeqno, propertyVO.getEstBrkMemOfcSeqno() );
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}
		JSONArray propertyItems = new JSONArray();
		JSONObject propertyItem = null;
		for( PropertyVO vo : propertyList ) {
			propertyItem = new JSONObject();
			
			propertyItem.put( "prptSeqno",			vo.getPrptSeqno() );
			propertyItem.put( "prptNo",				vo.getPrptNo() );
			propertyItem.put( "estateTypGbCd",		vo.getEstateTypGbCd() );
			propertyItem.put( "estateTypGbNm",		vo.getEstateTypGbNm() );
			propertyItem.put( "estateTypCd",		vo.getEstateTypCd() );
			propertyItem.put( "estateTypNm",		vo.getEstateTypNm() );
			propertyItem.put( "tranTypGbCd",		vo.getTranTypGbCd() );
			propertyItem.put( "tranTypGbNm",		vo.getTranTypGbNm() );
			propertyItem.put( "cost",				GsntalkUtil.parseAmtToKr( vo.getCost() ) );
			propertyItem.put( "montRentAmt",		GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() ) );
			propertyItem.put( "splyArea",			GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
			propertyItem.put( "prvArea",			GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			propertyItem.put( "lndArea",			GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			propertyItem.put( "totFlrArea",			GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			propertyItem.put( "flr",				vo.getFlr() );
			propertyItem.put( "smplSmrDscr",		vo.getSmplSmrDscr() );
			propertyItem.put( "lat",				vo.getLat() );
			propertyItem.put( "lng",				vo.getLng() );
			propertyItem.put( "addrShortNm",		vo.getAddrShortNm() );
			propertyItem.put( "reprImgUrl",			GsntalkUtil.nullToEmptyString( vo.getReprImgUrl() ) );
			
			propertyItems.add( propertyItem );
		}
		
		
		JSONObject item = new JSONObject();
		item.put( "photoItems", photoItems );							// 매물사진 URL 목록
		item.put( "baseItem", baseItem );								// 기본정보
		item.put( "costItem", costItem );								// 가격정보
		item.put( "dtlItem", dtlItem );									// 상세정보
		item.put( "totOptionCnt", totOptionCnt );						// 총 옵션 개수
		item.put( "optionItems", optionItems );							// 옵션정보
		item.put( "dtlExplntnDscr", propertyVO.getDtlExplntnDscr() );	// 상세설명내용
		item.put( "cmmstnItem", cmmstnItem );							// 중개보수정보
		item.put( "estBrkOfcItem", estBrkOfcItem );						// 중개 부동산정보
		item.put( "propertyItems", propertyItems );						// 중개사 매물 목록
		
		return item;
	}
	
	/**
	 * 중개회원 매물등록 1단계 임시저장
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param dealAmtDiscsnPsblYn
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param existngLeaseExstsYn
	 * @param crntDpstAmt
	 * @param crntMontRentAmt
	 * @param keyMonExstsYn
	 * @param keyMonAmt
	 * @param prmmAmt
	 * @param cmpltExpctDate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject registerEstBrkPrptStep1RegItem( long memSeqno, long prptSeqno, String estateTypGbCd, String estateTypCd, String tranTypGbCd, long dealAmt, String dealAmtDiscsnPsblYn, long dpstAmt, int montRentAmt,
			String existngLeaseExstsYn, long crntDpstAmt, int crntMontRentAmt, String keyMonExstsYn, long keyMonAmt, long prmmAmt, String cmpltExpctDate
	)throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개회원이 아님" );
		}
		
		/** 매물유형 구분코드 검증 */
		if( GsntalkUtil.isEmpty( estateTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL", "PRESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypGbCd 값이 잘못됨 -> see CommonCode [ESTATE_TYP_GB_CD]" );
		}
		
		/** 매물 구분코드 검증 */
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypCd 값 없음" );
		}
		if( "REGIDENTAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "APT", "OFT", "SMH", "TWN", "SHC", "HUS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in REGIDENTAL ) -> see CommonCode [ESTATE_TYP_GB_CD - REGIDENTAL]" );
			}
		}else if( "COMMERCIAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "KOG", "STR", "BLD", "LND", "FTR" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in COMMERCIAL ) -> see CommonCode [ESTATE_TYP_GB_CD - COMMERCIAL]" );
			}
		}else {
			if( !GsntalkUtil.isIn( estateTypCd, "CPS", "SPS", "APS", "OPS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in PRESALE ) -> see CommonCode [ESTATE_TYP_GB_CD - PRESALE]" );
			}
		}
		
		/** 거래유형 구분코드 검증 */
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "LEASE", "LEASE_ST", "CHARTER", "MONTLY", "RESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd 값이 잘못됨 -> see CommonCode [TRAN_TYP_GB_CD]" );
		}
		
		/** 거래유형에 따른 거래금액 검증 */
		if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
			if( dealAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dealAmt = 0L;
		}
		
		/** 거래금액 협의가능 여부 검증 */
		if( GsntalkUtil.isEmpty( dealAmtDiscsnPsblYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmtDiscsnPsblYn 값 없음" );
		}
		if( !GsntalkUtil.isIn( dealAmtDiscsnPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealAmtDiscsnPsblYn 값이 잘못됨 -> only Y or N value possible" );
		}
		
		/** 거래유형에 따른 보증금액 밍 월 임대료 검증 */
		if( GsntalkUtil.isIn( tranTypGbCd, "LEASE", "LEASE_ST", "MONTLY" ) ) {
			if( dpstAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( montRentAmt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "montRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dpstAmt = 0L;
			montRentAmt = 0;
		}
		
		/** 매물유형 구분에 따른 기존임대차 존재여부 검증 */
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			if( GsntalkUtil.isEmpty( existngLeaseExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "existngLeaseExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( existngLeaseExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "existngLeaseExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			existngLeaseExstsYn = null;
		}
		
		/** 매물유형 구분에 따른 현 보증금, 현 월임대료 검증 */
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			// 기존 임대차 존재하면 현보증금/현월임대료 값 검증
			if( GsntalkConstants.YES.equals( existngLeaseExstsYn ) ) {
				if( crntDpstAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but crntDpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
				if( crntMontRentAmt == 0 ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but crntMontRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			crntDpstAmt = 0L;
			crntMontRentAmt = 0;
		}
		
		/** 매물 구분에 따른 권리금 존재여부, 권리금액 검증 */
		if( "STR".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( keyMonExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "keyMonExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( keyMonExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "keyMonExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
			
			if( GsntalkConstants.YES.equals( keyMonExstsYn ) ) {
				if( keyMonAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "권리금 존재, but keyMonAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			keyMonExstsYn = null;
			keyMonAmt = 0L;
		}
		
		/** 매물유형 구분에 따른 준공 예정일자 검증 */
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isEmpty( cmpltExpctDate ) && !GsntalkUtil.is8DateFormat( cmpltExpctDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "cmpltExpctDate 값 잘못됨" );
			}
		}else {
			prmmAmt = 0L;
			cmpltExpctDate = null;
		}
		
		String regTmpKey = "";
		// 수정단계에서 호출하는 경우 기존 임시키 사용
		if( prptSeqno != 0L ) {
			regTmpKey = gsntalkDAO.getPropertyTempdataOfRegistrationKey( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno );
		}
		// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			regTmpKey = GsntalkUtil.createRegistrationTempKey();
		}
		
		JSONObject item = new JSONObject();
		item.put( "estateTypGbCd", estateTypGbCd );
		item.put( "estateTypCd", estateTypCd );
		item.put( "tranTypGbCd", tranTypGbCd );
		item.put( "dealAmt", dealAmt );
		item.put( "dealAmtDiscsnPsblYn", dealAmtDiscsnPsblYn );
		item.put( "dpstAmt", dpstAmt );
		item.put( "montRentAmt", montRentAmt );
		item.put( "existngLeaseExstsYn", existngLeaseExstsYn );
		item.put( "crntDpstAmt", crntDpstAmt );
		item.put( "crntMontRentAmt", crntMontRentAmt );
		item.put( "keyMonExstsYn", keyMonExstsYn );
		item.put( "keyMonAmt", keyMonAmt );
		item.put( "prmmAmt", prmmAmt );
		item.put( "cmpltExpctDate", cmpltExpctDate );
		
		// 기존 1단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1, item.toJSONString() );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * 중개회원 매물등록 2단계 임시저장
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param addr
	 * @param dtlAddr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param mapDispYn
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public void registerEstBrkPrptStep2RegItem( long memSeqno, String regTmpKey, String addr, String dtlAddr, String addrShortNm, double lat, double lng, String mapDispYn, String tmpAddrYn, String unregistYn )throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개회원이 아님" );
		}
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		// 중개회원 매물등록 1단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 이전 임시데이터에서 매물유형 구분코드 / 매물 구분코드 / 거래유형 구분코드 추출
		JSONObject tempItem = (JSONObject)jsonParser.parse( tmpJsonData );
		String estateTypGbCd = GsntalkUtil.getString( tempItem.get( "estateTypGbCd" ) );
		
		/** 필수 값 검증 */
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( GsntalkUtil.isEmpty( mapDispYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "mapDispYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( mapDispYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "mapDispYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkUtil.isEmpty( tmpAddrYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tmpAddrYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( tmpAddrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tmpAddrYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkConstants.YES.equals( tmpAddrYn ) && GsntalkUtil.isEmpty( addrShortNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "임시주소로 설정함, but addrShortNm 값 없음" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat 값 없음" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng 값 없음" );
		}
		
		/** 매물유형 구분에 따른 미등기여부 값 검증 */
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( GsntalkUtil.isEmpty( unregistYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "unregistYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( unregistYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "unregistYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			unregistYn = null;
		}
		
		// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2 );
		
		JSONObject item = new JSONObject();
		item.put( "addr", GsntalkXSSUtil.encodeXss( addr ) );
		item.put( "dtlAddr", GsntalkXSSUtil.encodeXss( dtlAddr ) );
		item.put( "addrShortNm", addrShortNm );
		item.put( "lat", lat );
		item.put( "lng", lng );
		item.put( "mapDispYn", mapDispYn );
		item.put( "tmpAddrYn", tmpAddrYn );
		item.put( "unregistYn", unregistYn );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2, item.toJSONString() );
		}
	}
	
	/**
	 * 중개회원 매물등록 3단계 임시저장
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param flr
	 * @param allFlr
	 * @param minFlr
	 * @param maxFlr
	 * @param splyArea
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @param useCnfrmDate
	 * @param bldUsageGbCd
	 * @param suggstnBldUsageGbCd
	 * @param lndCrntUsageGbCd
	 * @param psblMovDayTypCd
	 * @param psblMovDate
	 * @param monMntnceCost
	 * @param mntnceCostTypItems
	 * @param loanGbCd
	 * @param loanAmt
	 * @param parkingPsblYn
	 * @param parkingCost
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public void registerEstBrkPrptStep3RegItem( long memSeqno, String regTmpKey, int flr, int allFlr, int minFlr, int maxFlr, double splyArea, double prvArea, double lndArea, double totFlrArea, String useCnfrmDate, String bldUsageGbCd,
			String suggstnBldUsageGbCd, String lndCrntUsageGbCd, String psblMovDayTypCd, String psblMovDate, int monMntnceCost, JSONArray mntnceCostTypItems, String loanGbCd, long loanAmt, String parkingPsblYn, int parkingCost
	)throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개사무소 정보가 존재하지 않음" );
		}
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		// 중개회원 매물등록 이전단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP );
		}
		
		// 중개회원 매물등록 1단계 임시저장 JSON 조회
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		int validationCnt = 0;
		
		// 이전 임시데이터에서 매물유형 구분코드 / 매물 구분코드 / 거래유형 구분코드 추출
		JSONObject tempItem = (JSONObject)jsonParser.parse( tmpJsonData );
		String estateTypGbCd = GsntalkUtil.getString( tempItem.get( "estateTypGbCd" ) );
		String estateTypCd = GsntalkUtil.getString( tempItem.get( "estateTypCd" ) );
		
		/** 토지/임야, 단독공장이 아니면 해당층/전체틍 값 검증 */
		if( !GsntalkUtil.isIn( estateTypCd, "LND", "FTR" ) ) {
			if( flr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "flr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( allFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "allFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			flr = 0;
			allFlr = 0;
		}
		
		/** 단독공장이면 최저층/최고층 값 검증 */
		if( "FTR".equals( estateTypCd ) ) {
			if( minFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "minFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( maxFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "maxFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			minFlr = 0;
			maxFlr = 0;
		}
		
		/** 건물, 토지/임야, 단독공장이면 */
		if( GsntalkUtil.isIn( estateTypCd, "BLD", "LND", "FTR" ) ) {
			// 대지면적 검증
			if( lndArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lndArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			// 토지/임야가 아니면 연면적 추가 검증
			if( !"LND".equals( estateTypCd ) ) {
				if( totFlrArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "totFlrArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				// 토지/임야면 연멱적 없음
				totFlrArea = 0.0d;
			}
			
			// 단독공장이면 전용면적 추가 검증
			if( "FTR".equals( estateTypCd ) ) {
				if( prvArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				prvArea = 0.0d;
			}
			
			splyArea = 0.0d;
			
		/** 건물, 토지/임야, 단독공장이 아니면 */
		}else {
			// 공급면적, 전용면적 검증
			if( splyArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "splyArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( prvArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			lndArea = 0.0d;
			totFlrArea = 0.0d;
		}
		
		/** 상업용 주거용 이면서, 토지임야가 아니면 사용승인일자 필수입력 검증 */
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) && !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( useCnfrmDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "useCnfrmDate 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.is8DateFormat( useCnfrmDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "useCnfrmDate 값 잘못됨" );
			}
		}else {
			useCnfrmDate = null;
		}
		
		/** 토지/임야가 아니면 건축물 주용도 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( bldUsageGbCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bldUsageGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_USAGE_GB_CD", bldUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "bldUsageGbCd 값이 잘못됨 -> see [4. Common Codes - BLD_USAGE_GB_CD]" );
			}
		}else {
			bldUsageGbCd = null;
		}
		
		/** 단독공장인 경우 건축물 추천용도 검증 ( 필수가 아니므로 값이 있으면 유효성만 검증 ) */
		if( "FTR".equals( estateTypCd ) && !GsntalkUtil.isEmpty( suggstnBldUsageGbCd ) ) {
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_USAGE_GB_CD", suggstnBldUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "suggstnBldUsageGbCd 값이 잘못됨 -> see [4. Common Codes - BLD_USAGE_GB_CD]" );
			}
		}
		
		/** 토지/임야면 토지/임야 현재용도 검증 */
		if( "LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( lndCrntUsageGbCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lndCrntUsageGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "LND_CRNT_USAGE_GB_CD", lndCrntUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "lndCrntUsageGbCd 값이 잘못됨 -> see [4. Common Codes - LND_CRNT_USAGE_GB_CD]" );
			}
		}else {
			lndCrntUsageGbCd = null;
		}
		
		/** 토지임야가 아니면 관리비 항목 유효성 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmptyArray( mntnceCostTypItems ) ) {
				String mntnceCostTypCd = "";
				for( int i = 0; i < mntnceCostTypItems.size(); i ++ ) {
					mntnceCostTypCd = GsntalkUtil.getString( mntnceCostTypItems.get( i ) );
					
					validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "MNTNCE_COST_TYP_CD", mntnceCostTypCd, null );
					if( validationCnt < 1 ) {
						// 잘못된 파라메터 값
						throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "mntnceCostTypItems - 잘못된 관리비유형코드 값이 존재함 (" + mntnceCostTypCd + ")  -> see [4. Common Codes - MNTNCE_COST_TYP_CD]" );
					}
				}
			}
			
		}else {
			mntnceCostTypItems = new JSONArray();
		}
		
		/** 토지/임야가 아니면 입주가능일 유형, 입주가능일자 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( psblMovDayTypCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "psblMovDayTypCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			if( !GsntalkUtil.isIn( psblMovDayTypCd, "IMMDTLY", "DISCSN", "INPUT" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "psblMovDayTypCd 값이 잘못됨 -> see [4. Common Codes - PSBL_MOV_DAY_TYP_CD]" );
			}
			
			// 입주가능일 유형이 직접입력이면 입주가능일자 추가 검증
			if( "INPUT".equals( psblMovDayTypCd ) ) {
				if( GsntalkUtil.isEmpty( psblMovDate ) ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "입주가능일 유형이 직접입력, but psblMovDate 값 없음." );
				}
				if( !GsntalkUtil.is8DateFormat( psblMovDate, true ) ) {
					// 잘못된 날짜형식
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "psblMovDate 값 잘못됨" );
				}
			}else {
				psblMovDate = null;
			}
		
		}else {
			estateTypCd = null;
			psblMovDate = null;
		}
		
		/** 융자금 구분코드 검증 */
		if( GsntalkUtil.isEmpty( loanGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "loanGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( loanGbCd, "NOT", "U30", "O30" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "loanGbCd 값이 잘못됨 -> see [4. Common Codes - LOAN_GB_CD]" );
		}
		// 융자금이 있는경우 융자금액 검증
		if( !"NOT".equals( loanGbCd ) && loanAmt == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "융자금이 존재함, but loanAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		
		/** 토지/임야가 아닌경우 주차가능여부 검증 */
		if( !"LND".equals( estateTypCd ) && !GsntalkUtil.isEmpty( parkingPsblYn ) ) {
			if( !GsntalkUtil.isIn( parkingPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "parkingPsblYn 값이 잘못됨 -> only Y or N value possible when not empty." );
			}
		}else {
			parkingPsblYn = null;
		}
		
		
		// 기존 3단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3 );
		
		JSONObject item = new JSONObject();
		item.put( "flr", flr );
		item.put( "allFlr", allFlr );
		item.put( "minFlr", minFlr );
		item.put( "maxFlr", maxFlr );
		item.put( "splyArea", GsntalkUtil.parseMetersToPyung( splyArea ) );
		item.put( "prvArea", GsntalkUtil.parseMetersToPyung( prvArea ) );
		item.put( "lndArea", GsntalkUtil.parseMetersToPyung( lndArea ) );
		item.put( "totFlrArea", GsntalkUtil.parseMetersToPyung( totFlrArea ) );
		item.put( "useCnfrmDate", useCnfrmDate );
		item.put( "bldUsageGbCd", bldUsageGbCd );
		item.put( "suggstnBldUsageGbCd", suggstnBldUsageGbCd );
		item.put( "lndCrntUsageGbCd", lndCrntUsageGbCd );
		item.put( "psblMovDayTypCd", psblMovDayTypCd );
		item.put( "psblMovDate", psblMovDate );
		item.put( "monMntnceCost", monMntnceCost );
		item.put( "mntnceCostTypItems", mntnceCostTypItems );
		item.put( "loanGbCd", loanGbCd );
		item.put( "loanAmt", loanAmt );
		item.put( "parkingPsblYn", parkingPsblYn );
		item.put( "parkingCost", parkingCost );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3, item.toJSONString() );
		}
	}
	
	/**
	 * 중개회원 매물등록 4단계 임시저장
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param roomCnt
	 * @param bathRoomCnt
	 * @param crntSectrGbCd
	 * @param suggstnSectrGbCd
	 * @param bldDirctnGbCd
	 * @param heatKindGbCd
	 * @param wghtPerPy
	 * @param elvFcltExstsYn
	 * @param frghtElvExstsYn
	 * @param intrrYn
	 * @param dockExstsYn
	 * @param hoistExstsYn
	 * @param flrHghtTypGbCd
	 * @param elctrPwrTypGbCd
	 * @param intnlStrctrTypCd
	 * @param bultInYn
	 * @param movInReprtPsblYn
	 * @param cityPlanYn
	 * @param bldCnfrmIssueYn
	 * @param lndDealCnfrmApplYn
	 * @param entrnceRoadExstsYn
	 * @param optionExstsYn
	 * @param optionTypItems
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public void registerEstBrkPrptStep4RegItem( long memSeqno, String regTmpKey, int roomCnt, int bathRoomCnt, String crntSectrGbCd, String suggstnSectrGbCd, String bldDirctnGbCd, String heatKindGbCd, double wghtPerPy, String elvFcltExstsYn, String frghtElvExstsYn, String intrrYn, String dockExstsYn,
			String hoistExstsYn, String flrHghtTypGbCd, String elctrPwrTypGbCd, String intnlStrctrTypCd, String bultInYn, String movInReprtPsblYn, String cityPlanYn, String bldCnfrmIssueYn, String lndDealCnfrmApplYn, String entrnceRoadExstsYn, String optionExstsYn, JSONArray optionTypItems
	)throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개회원이 아님" );
		}
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		// 중개회원 매물등록 이전단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP );
		}
		
		// 중개회원 매물등록 1단계 임시저장 JSON 조회
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		int validationCnt = 0;
		
		// 이전 임시데이터에서 매물유형 구분코드 / 매물 구분코드 / 거래유형 구분코드 추출
		JSONObject tempItem = (JSONObject)jsonParser.parse( tmpJsonData );
		String estateTypGbCd = GsntalkUtil.getString( tempItem.get( "estateTypGbCd" ) );
		String estateTypCd = GsntalkUtil.getString( tempItem.get( "estateTypCd" ) );
		
		/** 주거용 이거나 분양권 중 아파트/오피스텔 분양권인 경우 방수, 욕실수 검증 */
		if( "REGIDENTAL".equals( estateTypGbCd ) || GsntalkUtil.isIn( estateTypGbCd, "APS", "OPS" ) ) {
			if( roomCnt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "roomCnt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( bathRoomCnt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "bathRoomCnt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			roomCnt = 0;
			bathRoomCnt = 0;
		}
		
		/** 상가면서 현업종, 추천업종 구분코드가 있는경우 코드 유효성 검증 */
		if( "STR".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( crntSectrGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SECTR_GB_CD", crntSectrGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "crntSectrGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( suggstnSectrGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SECTR_GB_CD", suggstnSectrGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "suggstnSectrGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
		}else {
			crntSectrGbCd = null;
			suggstnSectrGbCd = null;
		}
		
		/** 토지/임야 가 아니면서 건물방향/난방종류구분코드가 있으면 코드 유효성 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( bldDirctnGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_DIRCTN_GB_CD", bldDirctnGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "bldDirctnGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( heatKindGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "HEAT_KIND_GB_CD", heatKindGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "heatKindGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
		}else {
			bldDirctnGbCd = null;
			heatKindGbCd = null;
		}
		
		/** 지산/사무실/창고, 지산분양권이 아니면 톤당하중 값 삭제 */
		if( !GsntalkUtil.isIn( estateTypCd, "KOG", "CPS" ) ) {
			wghtPerPy = 0.0d;
		}
		
		/** 토지/임야 가 아니면 승강시설 존재여부 값 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( elvFcltExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "elvFcltExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( elvFcltExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "elvFcltExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			elvFcltExstsYn = null;
		}
		
		/** 지산/사무실/창고, 단독공장, 지산분양권이면 화물용승강시설여부 값 검증 */
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "FTR", "CPS" ) ) {
			if( GsntalkUtil.isEmpty( frghtElvExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "frghtElvExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( frghtElvExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "frghtElvExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}
		
		/** 지산/사무실/창고, 지산분양권이면 인테리어여부 값 검증 */
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "CPS" ) ) {
			if( GsntalkUtil.isEmpty( intrrYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "intrrYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( intrrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "intrrYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}
		
		/** 단독공장이면서 도크 존재여부, 호이스트 존재여부 값이 있으면 유효성 검증 */
		if( "FTR".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( dockExstsYn ) && !GsntalkUtil.isIn( dockExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dockExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
			if( !GsntalkUtil.isEmpty( hoistExstsYn ) && !GsntalkUtil.isIn( hoistExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "hoistExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			dockExstsYn = null;
			hoistExstsYn = null;
		}
		
		/** 지산/사무실/창고, 단독공장, 지산분양권이면서 층고유형구분코드/사용전력유형코드 값이 존재하면 코드 유효성 검증 */
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "FTR", "CPS" ) ) {
			if( !GsntalkUtil.isEmpty( flrHghtTypGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "FLR_HGHT_TYP_GB_CD", flrHghtTypGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "flrHghtTypGbCd 값이 잘못됨  -> see [4. Common Codes - FLR_HGHT_TYP_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( elctrPwrTypGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "ELCTR_PWR_TYP_GB_CD", elctrPwrTypGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "elctrPwrTypGbCd 값이 잘못됨  -> see [4. Common Codes - ELCTR_PWR_TYP_GB_CD]" );
				}
			}
		}else {
			flrHghtTypGbCd = null;
			elctrPwrTypGbCd = null;
		}
		
		/** 오피스텔, 오피스텔 분양권이면서 내부구조유형코드/빌트인여부/전입신고 가능여부 값이 있으면 코드 및 값 검증 */
		if( GsntalkUtil.isIn( estateTypCd, "OFT", "OPS" ) ) {
			if( !GsntalkUtil.isEmpty( intnlStrctrTypCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "INTNL_STRCTR_TYP_CD", intnlStrctrTypCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "intnlStrctrTypCd 값이 잘못됨  -> see [4. Common Codes - INTNL_STRCTR_TYP_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( bultInYn ) ) {
				if( !GsntalkUtil.isIn( bultInYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "bultInYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( movInReprtPsblYn ) ) {
				if( !GsntalkUtil.isIn( movInReprtPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "movInReprtPsblYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
		}else {
			intnlStrctrTypCd = null;
			bultInYn = null;
			movInReprtPsblYn = null;
		}
		
		/** 토지/임야 면서 도시계획여부, 건축허가발급여부, 토지거래허가구역해당여부, 진입도로 존재여부 값이 있으면 유효성 검증 */
		if( "LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( cityPlanYn ) ) {
				if( !GsntalkUtil.isIn( cityPlanYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "cityPlanYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( bldCnfrmIssueYn ) ) {
				if( !GsntalkUtil.isIn( bldCnfrmIssueYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "bldCnfrmIssueYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( lndDealCnfrmApplYn ) ) {
				if( !GsntalkUtil.isIn( lndDealCnfrmApplYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "lndDealCnfrmApplYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( entrnceRoadExstsYn ) ) {
				if( !GsntalkUtil.isIn( entrnceRoadExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "entrnceRoadExstsYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
		}else {
			cityPlanYn = null;
			bldCnfrmIssueYn = null;
			lndDealCnfrmApplYn = null;
			entrnceRoadExstsYn = null;
		}
		
		/** 토지/임야 가 아니면 옵션 존재여부 값 유효성 검증 */
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( optionExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "optionExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			optionExstsYn = null;
			optionTypItems = new JSONArray();
		}
		
		/** 옵션 존재여부가 존재이면서 선택옵션 항목이 있으면 옵션항목 코드 유효성 검증 */
		if( GsntalkConstants.YES.equals( optionExstsYn ) ) {
			if( GsntalkUtil.isEmptyArray( optionTypItems ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "옵션이 존재함, but optionTypItems 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				
			}else {
				JSONObject optTypGbItem = null;
				JSONArray optTypItems = null;
				String optionItemTypGbCd = null;
				String optionItemTypCd = null;
				
				for( int i = 0; i < optionTypItems.size(); i ++ ) {
					optTypGbItem = (JSONObject)optionTypItems.get( i );
					
					optionItemTypGbCd = GsntalkUtil.getString( optTypGbItem.get( "optionItemTypGbCd" ) );
					
					validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "OPTION_ITEM_TYP_GB_CD", optionItemTypGbCd, null );
					if( validationCnt < 1 ) {
						// 잘못된 파라메터 값
						throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "optionItemTypGbCd 값이 잘못됨 (" + optionItemTypGbCd + ")  -> see [4. Common Codes - OPTION_ITEM_TYP_GB_CD]" );
					}
					
					optTypItems = GsntalkUtil.getJSONArray( optTypGbItem, "optionTypCdItems" );
					if( GsntalkUtil.isEmptyArray( optTypItems ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "옵션항목유형 코드(" + optionItemTypGbCd + ")에 해당하는 optionTypCdItems 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
					}
					
					for( int j = 0; j < optTypItems.size(); j ++ ) {
						optionItemTypCd = GsntalkUtil.getString( optTypItems.get( j ) );
						
						validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "OPTION_ITEM_TYP_CD", optionItemTypCd, optionItemTypGbCd );
						if( validationCnt < 1 ) {
							// 잘못된 파라메터 값
							throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, optionItemTypGbCd + " -> optionItemTypCd 값이 잘못됨 (" + optionItemTypCd + ")  -> see [4. Common Codes - OPTION_ITEM_TYP_CD > parent code is " + optionItemTypGbCd + "]" );
						}
					}
				}
			}
		}else {
			optionTypItems = new JSONArray();
		}
		
		
		// 기존 4단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 4 );
		
		JSONObject item = new JSONObject();
		item.put( "roomCnt", roomCnt );
		item.put( "bathRoomCnt", bathRoomCnt );
		item.put( "crntSectrGbCd", crntSectrGbCd );
		item.put( "suggstnSectrGbCd", suggstnSectrGbCd );
		item.put( "bldDirctnGbCd", bldDirctnGbCd );
		item.put( "heatKindGbCd", heatKindGbCd );
		item.put( "wghtPerPy", wghtPerPy );
		item.put( "elvFcltExstsYn", elvFcltExstsYn );
		item.put( "frghtElvExstsYn", frghtElvExstsYn );
		item.put( "intrrYn", intrrYn );
		item.put( "dockExstsYn", dockExstsYn );
		item.put( "hoistExstsYn", hoistExstsYn );
		item.put( "flrHghtTypGbCd", flrHghtTypGbCd );
		item.put( "elctrPwrTypGbCd", elctrPwrTypGbCd );
		item.put( "intnlStrctrTypCd", intnlStrctrTypCd );
		item.put( "bultInYn", bultInYn );
		item.put( "movInReprtPsblYn", movInReprtPsblYn );
		item.put( "cityPlanYn", cityPlanYn );
		item.put( "bldCnfrmIssueYn", bldCnfrmIssueYn );
		item.put( "lndDealCnfrmApplYn", lndDealCnfrmApplYn );
		item.put( "entrnceRoadExstsYn", entrnceRoadExstsYn );
		item.put( "optionExstsYn", optionExstsYn );
		item.put( "optionTypItems", optionTypItems );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 4, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 4, item.toJSONString() );
		}
	}
	
	/**
	 * 중개회원 매물등록 5단계 최종 등록
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param repFile
	 * @param addFiles
	 * @param smplSmrDscr
	 * @param dtlExplntnDscr
	 * @param prvtMemoDscr
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void registerEstBrkPrptFinalStepRegItem( long memSeqno, String regTmpKey, MultipartFile repFile, List<MultipartFile> addFiles, String smplSmrDscr, String dtlExplntnDscr, String prvtMemoDscr )throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개회원이 아님" );
		}
		
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		if( GsntalkUtil.isEmpty( smplSmrDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "smplSmrDscr" );
		}
		
		if( GsntalkUtil.isEmpty( dtlExplntnDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dtlExplntnDscr" );
		}
		
		if( repFile == null || repFile.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT, "repFile" );
		}
		
		if( GsntalkUtil.isEmptyList( addFiles ) ) {
			addFiles = new ArrayList<MultipartFile>();
		}
		if( addFiles.size() > 7 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE );
		}
		
		String orgFileNm = repFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
		}
		
		for( MultipartFile file : addFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		// 1단계 임시저장 JSON 조회
		String firstJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( firstJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 2단계 임시저장 JSON 조회
		String secondJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( secondJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "2nd data missing" );
		}
		
		// 3단계 임시저장 JSON 조회
		String thirdJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3 );
		if( GsntalkUtil.isEmpty( thirdJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "3rd data missing" );
		}
		
		// 4단계 임시저장 JSON 조회
		String fourthJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 4 );
		if( GsntalkUtil.isEmpty( fourthJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "4th data missing" );
		}
		
		JSONObject firstStepItem = (JSONObject)jsonParser.parse( firstJsonData );
		JSONObject secondStepItem = (JSONObject)jsonParser.parse( secondJsonData );
		JSONObject thirdStepItem = (JSONObject)jsonParser.parse( thirdJsonData );
		JSONObject fourthStepItem = (JSONObject)jsonParser.parse( fourthJsonData );
		
		// step 1 data
		String estateTypGbCd			= GsntalkUtil.getString( firstStepItem.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( firstStepItem.get( "estateTypCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( firstStepItem.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( firstStepItem.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn		= GsntalkUtil.getString( firstStepItem.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt					= GsntalkUtil.getLong( firstStepItem.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( firstStepItem.get( "montRentAmt" ) );
		String existngLeaseExstsYn		= GsntalkUtil.getString( firstStepItem.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt				= GsntalkUtil.getLong( firstStepItem.get( "crntDpstAmt" ) );
		int crntMontRentAmt				= GsntalkUtil.getInteger( firstStepItem.get( "crntMontRentAmt" ) );
		String keyMonExstsYn			= GsntalkUtil.getString( firstStepItem.get( "keyMonExstsYn" ) );
		long keyMonAmt					= GsntalkUtil.getLong( firstStepItem.get( "keyMonAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( firstStepItem.get( "prmmAmt" ) );
		String cmpltExpctDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( firstStepItem.get( "cmpltExpctDate" ) ) );
		
		// step 2 data
		String addr						= GsntalkUtil.getString( secondStepItem.get( "addr" ) );
		String dtlAddr					= GsntalkUtil.getString( secondStepItem.get( "dtlAddr" ) );
		String addrShortNm				= GsntalkUtil.getString( secondStepItem.get( "addrShortNm" ) );
		double lat						= GsntalkUtil.getDouble( secondStepItem.get( "lat" ) );
		double lng						= GsntalkUtil.getDouble( secondStepItem.get( "lng" ) );
		String mapDispYn				= GsntalkUtil.getString( secondStepItem.get( "mapDispYn" ) );
		String tmpAddrYn				= GsntalkUtil.getString( secondStepItem.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( secondStepItem.get( "unregistYn" ) );
		
		// step 3 data
		int flr							= GsntalkUtil.getInteger( thirdStepItem.get( "flr" ) );
		int allFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "allFlr" ) );
		int minFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "minFlr" ) );
		int maxFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "maxFlr" ) );
		double splyArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "splyArea" ) ) );
		double prvArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "prvArea" ) ) );
		double lndArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "lndArea" ) ) );
		double totFlrArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "totFlrArea" ) ) );
		String useCnfrmDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( thirdStepItem.get( "useCnfrmDate" ) ) );
		String bldUsageGbCd				= GsntalkUtil.getString( thirdStepItem.get( "bldUsageGbCd" ) );
		String suggstnBldUsageGbCd		= GsntalkUtil.getString( thirdStepItem.get( "suggstnBldUsageGbCd" ) );
		String lndCrntUsageGbCd			= GsntalkUtil.getString( thirdStepItem.get( "lndCrntUsageGbCd" ) );
		String psblMovDayTypCd			= GsntalkUtil.getString( thirdStepItem.get( "psblMovDayTypCd" ) );
		String psblMovDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( thirdStepItem.get( "psblMovDate" ) ) );
		int monMntnceCost				= GsntalkUtil.getInteger( thirdStepItem.get( "monMntnceCost" ) );
		JSONArray mntnceCostTypItems	= GsntalkUtil.getJSONArray( thirdStepItem, "mntnceCostTypItems" );
		String loanGbCd					= GsntalkUtil.getString( thirdStepItem.get( "loanGbCd" ) );
		long loanAmt					= GsntalkUtil.getLong( thirdStepItem.get( "loanAmt" ) );
		String parkingPsblYn			= GsntalkUtil.getString( thirdStepItem.get( "parkingPsblYn" ) );
		int parkingCost					= GsntalkUtil.getInteger( thirdStepItem.get( "parkingCost" ) );
		
		// step 4 data
		int roomCnt						= GsntalkUtil.getInteger( fourthStepItem.get( "roomCnt" ) );
		int bathRoomCnt					= GsntalkUtil.getInteger( fourthStepItem.get( "bathRoomCnt" ) );
		String crntSectrGbCd			= GsntalkUtil.getString( fourthStepItem.get( "crntSectrGbCd" ) );
		String suggstnSectrGbCd			= GsntalkUtil.getString( fourthStepItem.get( "suggstnSectrGbCd" ) );
		String bldDirctnGbCd			= GsntalkUtil.getString( fourthStepItem.get( "bldDirctnGbCd" ) );
		String heatKindGbCd				= GsntalkUtil.getString( fourthStepItem.get( "heatKindGbCd" ) );
		double wghtPerPy				= GsntalkUtil.getDouble( fourthStepItem.get( "wghtPerPy" ) );
		String elvFcltExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "elvFcltExstsYn" ) );
		String frghtElvExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "frghtElvExstsYn" ) );
		String intrrYn					= GsntalkUtil.getString( fourthStepItem.get( "intrrYn" ) );
		String dockExstsYn				= GsntalkUtil.getString( fourthStepItem.get( "dockExstsYn" ) );
		String hoistExstsYn				= GsntalkUtil.getString( fourthStepItem.get( "hoistExstsYn" ) );
		String flrHghtTypGbCd			= GsntalkUtil.getString( fourthStepItem.get( "flrHghtTypGbCd" ) );
		String elctrPwrTypGbCd			= GsntalkUtil.getString( fourthStepItem.get( "elctrPwrTypGbCd" ) );
		String intnlStrctrTypCd			= GsntalkUtil.getString( fourthStepItem.get( "intnlStrctrTypCd" ) );
		String bultInYn					= GsntalkUtil.getString( fourthStepItem.get( "bultInYn" ) );
		String movInReprtPsblYn			= GsntalkUtil.getString( fourthStepItem.get( "movInReprtPsblYn" ) );
		String cityPlanYn				= GsntalkUtil.getString( fourthStepItem.get( "cityPlanYn" ) );
		String bldCnfrmIssueYn			= GsntalkUtil.getString( fourthStepItem.get( "bldCnfrmIssueYn" ) );
		String lndDealCnfrmApplYn		= GsntalkUtil.getString( fourthStepItem.get( "lndDealCnfrmApplYn" ) );
		String entrnceRoadExstsYn		= GsntalkUtil.getString( fourthStepItem.get( "entrnceRoadExstsYn" ) );
		String optionExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "optionExstsYn" ) );
		JSONArray optionTypItems		= GsntalkUtil.getJSONArray( fourthStepItem, "optionTypItems" );

		// 단축주소가 없는경우 geocode 에서 조회해서 적용
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
			if( geocodeItem != null ) {
				addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
			}
		}
		
		// register property
		int nextNum = propertyDAO.getNextPPrptRegNum();
		
		// 매물 등록 ( 1 ~ 4 단계 정보 )
		long prptSeqno = propertyDAO.registerProperty( memSeqno, estBrkMemOfcSeqno, GsntalkUtil.createPPrptRegno( nextNum ), estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt,
				keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn, flr, allFlr, minFlr, maxFlr,
				splyArea, prvArea, lndArea, totFlrArea, useCnfrmDate, bldUsageGbCd, suggstnBldUsageGbCd, lndCrntUsageGbCd, psblMovDayTypCd, psblMovDate, monMntnceCost, loanGbCd, loanAmt, parkingPsblYn, parkingCost,
				roomCnt, bathRoomCnt, crntSectrGbCd, suggstnSectrGbCd, bldDirctnGbCd, heatKindGbCd, wghtPerPy, elvFcltExstsYn, frghtElvExstsYn, intrrYn, dockExstsYn, hoistExstsYn, flrHghtTypGbCd,
				elctrPwrTypGbCd, intnlStrctrTypCd, bultInYn, movInReprtPsblYn, cityPlanYn, bldCnfrmIssueYn, lndDealCnfrmApplYn, entrnceRoadExstsYn, optionExstsYn );
		
		// 관리비 유형 등록
		for( int i = 0; i < mntnceCostTypItems.size(); i ++ ) {
			propertyDAO.registerPropertyMaintenanceTyps( prptSeqno, GsntalkUtil.getString( mntnceCostTypItems.get( i ) ) );
		}
		
		// 옵션유형 등록
		JSONObject optionTypItem = null;
		String optionItemTypGbCd = null;
		JSONArray optionTypCdItems = null;
		for( int i = 0; i < optionTypItems.size(); i ++ ) {
			optionTypItem = (JSONObject)optionTypItems.get( i );
			
			optionItemTypGbCd = GsntalkUtil.getString( optionTypItem.get( "optionItemTypGbCd" ) );
			optionTypCdItems = GsntalkUtil.getJSONArray( optionTypItem, "optionTypCdItems" );
			for( int j = 0; j < optionTypCdItems.size(); j ++ ) {
				propertyDAO.registerPropertyOptionTyps( prptSeqno, optionItemTypGbCd, GsntalkUtil.getString( optionTypCdItems.get( j ) ) );
			}
		}
		
		// 매물 사진정보 등록 ( 5단계 정보 )
		propertyDAO.registerPropertyPhotoInfo( prptSeqno, smplSmrDscr, dtlExplntnDscr, null, prvtMemoDscr );
		
		// 대표 사진 S3 업로드 및 등록
		JSONObject uploadItem = gsntalkS3Util.uploadPropertyImageFile( estBrkMemOfcSeqno, prptSeqno, repFile );
		
		String uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
		String saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
		String fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
		
		String repFileUrl = fileUrl;
		JSONArray addFileItems = new JSONArray();
		
		propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.YES );
		
		// 추가사진 S3 업로드 및 등록
		for( MultipartFile file :  addFiles ) {
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( estBrkMemOfcSeqno, prptSeqno, file );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			
			addFileItems.add( fileUrl );
			
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.NO );
		}
		
		// 기존 5단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5 );
		
		JSONObject item = new JSONObject();
		item.put( "repFileUrl", repFileUrl );
		item.put( "addFileUrls", addFileItems );
		item.put( "smplSmrDscr", GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		item.put( "dtlExplntnDscr", GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		item.put( "prvtMemoDscr", GsntalkXSSUtil.encodeXss( prvtMemoDscr ) );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5, item.toJSONString() );
		}
		
		// 등록단계별임시정보 매물시퀀스 등록
		gsntalkDAO.updateTempDataPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, prptSeqno );
	}
	
	/**
	 * 중개회원 - 내 매물 목록 조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param srchClasGbCd
	 * @param tranTypGbCd
	 * @param estateTypCd
	 * @param srchVal
	 * @param pageItem
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getEstBrkPropertyItems( long memSeqno, String srchClasGbCd, String tranTypGbCd, String estateTypCd, String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		if( !GsntalkUtil.isEmpty( srchClasGbCd ) && !GsntalkUtil.isIn( srchClasGbCd, "ING", "FIN", "HID" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "srchClasGbCd 값이 잘못됨  -> is not in [ ING, FIN, HID ]" );
		}
		
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<PropertyVO> propertyList = propertyDAO.getEstBrkPropertyItems( memSeqno, srchClasGbCd, tranTypGbCd, estateTypCd, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}else {
			totList = propertyList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = null;
		JSONObject areaItem = null;
		JSONArray pyAreaItems = null;
		JSONArray meterAreaItems = null;
		
		for( PropertyVO vo : propertyList ) {
			estateTypCd = vo.getEstateTypCd();
			tranTypGbCd = vo.getTranTypGbCd();
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			/** 면적정보 적용 */
			areaItem = new JSONObject();
			pyAreaItems = new JSONArray();
			meterAreaItems = new JSONArray();
			// 건물이면 대지면적/연면적
			if( "BLD".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				
			// 토지/임야면 대지면적
			}else if( "LND".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			
			// 단독공장이면 전용면적/대지면적/연면적
			}else if( "FTR".equals( estateTypCd ) ) {
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			
			// 이외에는 공급면적/전용면적
			}else {
				pyAreaItems.add( "공급 " + vo.getSplyArea() );
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				meterAreaItems.add( "공급 " + GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			}
			areaItem.put( "pyAreaItems", pyAreaItems );
			areaItem.put( "meterAreaItems", meterAreaItems );
			
			
			item = new JSONObject();
			
			item.put( "prptSeqno", vo.getPrptSeqno() );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "dealStatGbCd", vo.getDealStatGbCd() );
			item.put( "regExprYn", vo.getRegExprYn() );
			item.put( "estateTypCd", estateTypCd );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			item.put( "costDscr", costDscr );
			item.put( "prvtMemoDscr", vo.getPrvtMemoDscr() );
			item.put( "reprImgUrl", vo.getReprImgUrl() );
			item.put( "areaItem", areaItem );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * 중개회원 - 내 매물 삭제
	 * 
	 * @param memSeqno
	 * @param prptSeqnoItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deleteEstBrkPropertyItems( long memSeqno, JSONArray prptSeqnoItems )throws Exception {
		int c = 0;
		long prptSeqno = 0L;
		for( int i = 0; i < prptSeqnoItems.size(); i ++ ) {
			prptSeqno = GsntalkUtil.getLong( prptSeqnoItems.get( i ) );
			
			c = propertyDAO.isMembersProperty( memSeqno, prptSeqno );
			if( c < 1 ) {
				// 이미 삭제되었거나 삭제할 권한이 없는 매물
				throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_DELETED_OR_NO_PERM_PROPERTY, "이미 삭제되었거나 삭제할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
			}
			
			propertyDAO.deleteEstBrkProperty( prptSeqno );
		}
	}
	
	/**
	 * 중개회원 - 내 매물 상태변경
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @param dealStatGbCd
	 * @throws Exception
	 */
	public void updateEstBrkPropertyDealStatItem( long memSeqno, long prptSeqno, String dealStatGbCd )throws Exception {
		PropertyVO propertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( propertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( propertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( propertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		// 매물 상태변경 
		propertyDAO.updatePropertyDealStatGbCd( prptSeqno, dealStatGbCd );
	}
	
	/**
	 * 중개회원 - 등록만료 매물 재등록
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	public void regenEstBrkPropertyItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "재등록 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.NO.equals( prpertyVO.getRegExprYn() ) ) {
			// 아직 만료되지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_EXPIRED_PROPERTY, "아직 만료되지 않은 매물 prptSeqno -> " + prptSeqno );
		}
		
		propertyDAO.regenEstBrkProperty( prptSeqno );
	}
	
	/**
	 * 중개회원 - 매물등록 1단계 등록 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkPrptStep1RegItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		JSONObject item = null;
		
		// 1단계 임시저장 JSON 조회 - from prptSeqno
		RegistrationTmpDataStepVO registrationTmpDataStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 1 );
		
		// 1단계 임시저장 정보가 없으면 매물에서 조회하여 임시저장 데이터 생성 후 전달
		if( registrationTmpDataStepVO == null ) {
			PropertyVO propertyVO = propertyDAO.getEstBrkPrptStep1DataItem( prptSeqno );
			if( propertyVO == null ) {
				// 매물정보를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "매물정보를 찾을 수 없음 prptSeqno -> " + prptSeqno );
			}
			
			item = new JSONObject();
			item.put( "estateTypGbCd", propertyVO.getEstateTypGbCd() );
			item.put( "estateTypCd", propertyVO.getEstateTypCd() );
			item.put( "tranTypGbCd", propertyVO.getTranTypGbCd() );
			item.put( "dealAmt", propertyVO.getDealAmt() );
			item.put( "dealAmtDiscsnPsblYn", propertyVO.getDealAmtDiscsnPsblYn() );
			item.put( "dpstAmt", propertyVO.getDpstAmt() );
			item.put( "montRentAmt", propertyVO.getMontRentAmt() );
			item.put( "existngLeaseExstsYn", propertyVO.getExistngLeaseExstsYn() );
			item.put( "crntDpstAmt", propertyVO.getCrntDpstAmt() );
			item.put( "crntMontRentAmt", propertyVO.getCrntMontRentAmt() );
			item.put( "keyMonExstsYn", propertyVO.getKeyMonExstsYn() );
			item.put( "keyMonAmt", propertyVO.getKeyMonAmt() );
			item.put( "prmmAmt", propertyVO.getPrmmAmt() );
			item.put( "cmpltExpctDate", propertyVO.getCmpltExpctDate() );

			// 임시키 생성
			String regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 1, item.toJSONString(), prptSeqno, 0L, 0L, 0L, 0L );
			
			item.put( "regTmpKey", regTmpKey );
			item.put( "prptSeqno", prptSeqno );
		}else {
			item = (JSONObject)jsonParser.parse( registrationTmpDataStepVO.getTmpJsonData() );
			item.put( "regTmpKey", registrationTmpDataStepVO.getRegTmpKey() );
			item.put( "prptSeqno", prptSeqno );
		}
		
		return item;
	}
	
	/**
	 * 중개회원 - 매물등록 2단계 등록 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkPrptStep2RegItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		JSONObject item = null;
		
		// 2단계 임시저장 JSON 조회 - from prptSeqno
		RegistrationTmpDataStepVO registrationTmpDataStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 2 );
		
		// 2단계 임시저장 정보가 없으면 매물에서 조회하여 임시저장 데이터 생성 후 전달
		if( registrationTmpDataStepVO == null ) {
			PropertyVO propertyVO = propertyDAO.getEstBrkPrptStep2DataItem( prptSeqno );
			if( propertyVO == null ) {
				// 매물정보를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "매물정보를 찾을 수 없음 prptSeqno -> " + prptSeqno );
			}
			
			item = new JSONObject();
			item.put( "addr", propertyVO.getAddr() );
			item.put( "dtlAddr", propertyVO.getDtlAddr() );
			item.put( "addrShortNm", propertyVO.getAddrShortNm() );
			item.put( "lat", propertyVO.getLat() );
			item.put( "lng", propertyVO.getLng() );
			item.put( "mapDispYn", propertyVO.getMapDispYn() );
			item.put( "tmpAddrYn", propertyVO.getTmpAddrYn() );
			item.put( "unregistYn", propertyVO.getUnregistYn() );
			
			// 임시키 생성
			String regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 2, item.toJSONString(), prptSeqno, 0L, 0L, 0L, 0L );
			
			item.put( "regTmpKey", regTmpKey );
			item.put( "prptSeqno", prptSeqno );
		}else {
			item = (JSONObject)jsonParser.parse( registrationTmpDataStepVO.getTmpJsonData() );
			item.put( "regTmpKey", registrationTmpDataStepVO.getRegTmpKey() );
			item.put( "prptSeqno", prptSeqno );
		}
		
		return item;
	}
	
	/**
	 * 중개회원 - 매물등록 3단계 등록 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkPrptStep3RegItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		JSONObject item = null;
		
		// 3단계 임시저장 JSON 조회 - from prptSeqno
		RegistrationTmpDataStepVO registrationTmpDataStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 3 );
		
		// 3단계 임시저장 정보가 없으면 매물에서 조회하여 임시저장 데이터 생성 후 전달
		if( registrationTmpDataStepVO == null ) {
			PropertyVO propertyVO = propertyDAO.getEstBrkPrptStep3DataItem( prptSeqno );
			if( propertyVO == null ) {
				// 매물정보를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "매물정보를 찾을 수 없음 prptSeqno -> " + prptSeqno );
			}
			
			item = new JSONObject();
			item.put( "flr", propertyVO.getFlr() );
			item.put( "allFlr", propertyVO.getAllFlr() );
			item.put( "minFlr", propertyVO.getMinFlr() );
			item.put( "maxFlr", propertyVO.getMaxFlr() );
			item.put( "splyArea", GsntalkUtil.parsePyungToMeters( propertyVO.getSplyArea() ) );
			item.put( "prvArea", GsntalkUtil.parsePyungToMeters( propertyVO.getPrvArea() ) );
			item.put( "lndArea", GsntalkUtil.parsePyungToMeters( propertyVO.getLndArea() ) );
			item.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( propertyVO.getTotFlrArea() ) );
			item.put( "useCnfrmDate", propertyVO.getUseCnfrmDate() );
			item.put( "bldUsageGbCd", propertyVO.getBldUsageGbCd() );
			item.put( "suggstnBldUsageGbCd", propertyVO.getSuggstnBldUsageGbCd() );
			item.put( "lndCrntUsageGbCd", propertyVO.getLndCrntUsageGbCd() );
			item.put( "psblMovDayTypCd", propertyVO.getPsblMovDayTypCd() );
			item.put( "psblMovDate", propertyVO.getPsblMovDate() );
			item.put( "monMntnceCost", propertyVO.getMonMntnceCost() );
			item.put( "loanGbCd", propertyVO.getLoanGbCd() );
			item.put( "loanAmt", propertyVO.getLoanAmt() );
			item.put( "parkingPsblYn", propertyVO.getParkingPsblYn() );
			item.put( "parkingCost", propertyVO.getParkingCost() );
			
			// 선택 관리비 유형코드 목록조회
			List<String> mntnceCostTypList = propertyDAO.getPrptMntnceCostTypList( prptSeqno );
			if( GsntalkUtil.isEmptyList( mntnceCostTypList ) ) {
				mntnceCostTypList = new ArrayList<String>();
			}
			JSONArray mntnceCostTypItems = new JSONArray();
			for( String mntnceCostTypCd : mntnceCostTypList ) {
				mntnceCostTypItems.add( mntnceCostTypCd );
			}
			item.put( "mntnceCostTypItems", mntnceCostTypItems );
			
			// 임시키 생성
			String regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 3, item.toJSONString(), prptSeqno, 0L, 0L, 0L, 0L );
			
			item.put( "regTmpKey", regTmpKey );
			item.put( "prptSeqno", prptSeqno );
		}else {
			item = (JSONObject)jsonParser.parse( registrationTmpDataStepVO.getTmpJsonData() );
			
			// 평 단위값 -> 제곱미터 단위값 환산
			double splyArea = GsntalkUtil.getDouble( item.get( "splyArea" ) );
			double prvArea = GsntalkUtil.getDouble( item.get( "prvArea" ) );
			double lndArea = GsntalkUtil.getDouble( item.get( "lndArea" ) );
			double totFlrArea = GsntalkUtil.getDouble( item.get( "totFlrArea" ) );
			item.put( "splyArea", GsntalkUtil.parsePyungToMeters( splyArea ) );
			item.put( "prvArea", GsntalkUtil.parsePyungToMeters( prvArea ) );
			item.put( "lndArea", GsntalkUtil.parsePyungToMeters( lndArea ) );
			item.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( totFlrArea ) );
			
			item.put( "regTmpKey", registrationTmpDataStepVO.getRegTmpKey() );
			item.put( "prptSeqno", prptSeqno );
		}
		
		return item;
	}
	
	/**
	 * 중개회원 - 매물등록 4단계 등록 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkPrptStep4RegItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		JSONObject item = null;
		
		// 4단계 임시저장 JSON 조회 - from prptSeqno
		RegistrationTmpDataStepVO registrationTmpDataStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 4 );
		
		// 4단계 임시저장 정보가 없으면 매물에서 조회하여 임시저장 데이터 생성 후 전달
		if( registrationTmpDataStepVO == null ) {
			PropertyVO propertyVO = propertyDAO.getEstBrkPrptStep4DataItem( prptSeqno );
			if( propertyVO == null ) {
				// 매물정보를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "매물정보를 찾을 수 없음 prptSeqno -> " + prptSeqno );
			}
			
			item = new JSONObject();
			item.put( "roomCnt", propertyVO.getRoomCnt() );
			item.put( "bathRoomCnt", propertyVO.getBathRoomCnt() );
			item.put( "crntSectrGbCd", propertyVO.getCrntSectrGbCd() );
			item.put( "suggstnSectrGbCd", propertyVO.getSuggstnSectrGbCd() );
			item.put( "bldDirctnGbCd", propertyVO.getBldDirctnGbCd() );
			item.put( "heatKindGbCd", propertyVO.getHeatKindGbCd() );
			item.put( "wghtPerPy", propertyVO.getWghtPerPy() );
			item.put( "elvFcltExstsYn", propertyVO.getElvFcltExstsYn() );
			item.put( "frghtElvExstsYn", propertyVO.getFrghtElvExstsYn() );
			item.put( "intrrYn", propertyVO.getIntrrYn() );
			item.put( "dockExstsYn", propertyVO.getDockExstsYn() );
			item.put( "hoistExstsYn", propertyVO.getHoistExstsYn() );
			item.put( "flrHghtTypGbCd", propertyVO.getFlrHghtTypGbCd() );
			item.put( "elctrPwrTypGbCd", propertyVO.getElctrPwrTypGbCd() );
			item.put( "intnlStrctrTypCd", propertyVO.getIntnlStrctrTypCd() );
			item.put( "bultInYn", propertyVO.getBultInYn() );
			item.put( "movInReprtPsblYn", propertyVO.getMovInReprtPsblYn() );
			item.put( "cityPlanYn", propertyVO.getCityPlanYn() );
			item.put( "bldCnfrmIssueYn", propertyVO.getBldCnfrmIssueYn() );
			item.put( "lndDealCnfrmApplYn", propertyVO.getLndDealCnfrmApplYn() );
			item.put( "entrnceRoadExstsYn", propertyVO.getEntrnceRoadExstsYn() );
			item.put( "optionExstsYn", propertyVO.getOptionExstsYn() );
			
			// 선택 옵션유형 목록조회
			List<PropertyOptionVO> prptOptionList = propertyDAO.getPrptOptionTypList( prptSeqno );
			if( GsntalkUtil.isEmptyList( prptOptionList ) ) {
				prptOptionList = new ArrayList<PropertyOptionVO>();
			}
			
			JSONArray optionTypItems = new JSONArray();
			JSONObject optionTypItem = null;
			JSONArray optionTypCdItems = null;
			String[] optionTypCds = null;
			for( PropertyOptionVO vo : prptOptionList ) {
				optionTypItem = new JSONObject();
				optionTypItem.put( "optionItemTypGbCd", vo.getOptionItemTypGbCd() );
				
				optionTypCdItems = new JSONArray();
				optionTypCds = vo.getOptionItemTypCd().split( "," );
				for( String optionTypCd : optionTypCds ) {
					optionTypCdItems.add( optionTypCd );
				}
				optionTypItem.put( "optionTypCdItems", optionTypCdItems );
				
				optionTypItems.add( optionTypItem );
			}
			
			item.put( "optionTypItems", optionTypItems );
			
			// 임시키 생성
			String regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 4, item.toJSONString(), prptSeqno, 0L, 0L, 0L, 0L );
			
			item.put( "regTmpKey", regTmpKey );
			item.put( "prptSeqno", prptSeqno );
		}else {
			item = (JSONObject)jsonParser.parse( registrationTmpDataStepVO.getTmpJsonData() );
			
			item.put( "regTmpKey", registrationTmpDataStepVO.getRegTmpKey() );
			item.put( "prptSeqno", prptSeqno );
		}
		
		return item;
	}
	
	/**
	 * 중개회원 - 매물등록 5단계 등록 정보 조회
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getEstBrkPrptStep5RegItem( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		JSONObject item = null;
		
		// 5단계 임시저장 JSON 조회 - from prptSeqno
		RegistrationTmpDataStepVO registrationTmpDataStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 5 );
		
		// 5단계 임시저장 정보가 없으면 매물에서 조회하여 임시저장 데이터 생성 후 전달
		if( registrationTmpDataStepVO == null ) {
			PropertyPhotoVO propertyPhotoVO = propertyDAO.getEstBrkPrptStep5DataItem( prptSeqno );
			if( propertyPhotoVO == null ) {
				// 매물정보를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "매물정보를 찾을 수 없음 prptSeqno -> " + prptSeqno );
			}
			
			item = new JSONObject();
			item.put( "smplSmrDscr", propertyPhotoVO.getSmplSmrDscr() );
			item.put( "dtlExplntnDscr", propertyPhotoVO.getDtlExplntnDscr() );
			item.put( "prvtMemoDscr", propertyPhotoVO.getPrvtMemoDscr() );
			
			List<PropertyPhotoVO> propertyPhotoList = propertyDAO.getPrptPhotoList( prptSeqno );
			if( GsntalkUtil.isEmptyList( propertyPhotoList ) ) {
				propertyPhotoList = new ArrayList<PropertyPhotoVO>();
			}
			
			JSONArray addFileUrls = new JSONArray();
			for( PropertyPhotoVO vo : propertyPhotoList ) {
				if( GsntalkConstants.YES.equals( vo.getRepPhotoYn() ) ) {
					item.put( "repFileUrl", vo.getFileUrl() );
				}else {
					addFileUrls.add( vo.getFileUrl() );
				}
			}
			item.put( "addFileUrls", addFileUrls );
			
			// 임시키 생성
			String regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5, item.toJSONString(), prptSeqno, 0L, 0L, 0L, 0L );
			
			item.put( "regTmpKey", regTmpKey );
			item.put( "prptSeqno", prptSeqno );
		}else {
			item = (JSONObject)jsonParser.parse( registrationTmpDataStepVO.getTmpJsonData() );
			
			item.put( "regTmpKey", registrationTmpDataStepVO.getRegTmpKey() );
			item.put( "prptSeqno", prptSeqno );
		}
		
		return item;
	}
	
	/**
	 * 중개회원 매물수정 5단계 최종 수정 ( 매물수정 전용 )
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @param repFile
	 * @param delFileUrls
	 * @param addFiles
	 * @param smplSmrDscr
	 * @param dtlExplntnDscr
	 * @param prvtMemoDscr
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void updateEstBrkPrptFinalStepRegItem( long memSeqno, long prptSeqno, MultipartFile repFile, JSONArray delFileUrls, List<MultipartFile> addFiles, String smplSmrDscr, String dtlExplntnDscr, String prvtMemoDscr )throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 처리할 권한이 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.HAS_NO_PERMISSION_TO_PROCESS, "중개회원이 아님" );
		}
		
		if( prptSeqno == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "prptSeqno" );
		}
		
		if( GsntalkUtil.isEmpty( smplSmrDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "smplSmrDscr" );
		}
		
		if( GsntalkUtil.isEmpty( dtlExplntnDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dtlExplntnDscr" );
		}
		
		PropertyVO prpertyVO = propertyDAO.getPropertyRegExprYn( memSeqno, prptSeqno );
		if( prpertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 수정할 권한이 없는 매물 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( prpertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( prpertyVO.getRegExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		// 매물사진 목록조회 ( 대표사진 포함 )
		List<PropertyPhotoVO> prptPhotoList = propertyDAO.getPrptPhotoList( prptSeqno );
		if( GsntalkUtil.isEmptyList( prptPhotoList ) ) {
			prptPhotoList = new ArrayList<PropertyPhotoVO>();
		}
		if( GsntalkUtil.isEmptyList( addFiles ) ) {
			addFiles = new ArrayList<MultipartFile>();
		}
		if( prptPhotoList.size() - delFileUrls.size() + addFiles.size() > 8 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE );
		}
		
		String orgFileNm = "";
		String uploadFileFormat = "";
		
		// 첨부한 대표사진이 있으면
		if( repFile != null && repFile.getSize() != 0L ) {
			orgFileNm = repFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		for( MultipartFile file : addFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		// 1단계 임시저장 JSON 조회
		RegistrationTmpDataStepVO firstStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 1 );
		if( firstStepVO == null ) {
			// 1단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 2단계 임시저장 JSON 조회
		RegistrationTmpDataStepVO secondStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 2 );
		if( secondStepVO == null ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "2nd data missing" );
		}
		
		// 3단계 임시저장 JSON 조회
		RegistrationTmpDataStepVO thirdStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 3 );
		if( thirdStepVO == null ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "3rd data missing" );
		}
		
		// 4단계 임시저장 JSON 조회
		RegistrationTmpDataStepVO fourthStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 4 );
		if( fourthStepVO == null ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "4th data missing" );
		}
		
		JSONObject firstStepItem = (JSONObject)jsonParser.parse( firstStepVO.getTmpJsonData() );
		JSONObject secondStepItem = (JSONObject)jsonParser.parse( secondStepVO.getTmpJsonData() );
		JSONObject thirdStepItem = (JSONObject)jsonParser.parse( thirdStepVO.getTmpJsonData() );
		JSONObject fourthStepItem = (JSONObject)jsonParser.parse( fourthStepVO.getTmpJsonData() );
		
		// step 1 data
		String estateTypGbCd			= GsntalkUtil.getString( firstStepItem.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( firstStepItem.get( "estateTypCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( firstStepItem.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( firstStepItem.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn		= GsntalkUtil.getString( firstStepItem.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt					= GsntalkUtil.getLong( firstStepItem.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( firstStepItem.get( "montRentAmt" ) );
		String existngLeaseExstsYn		= GsntalkUtil.getString( firstStepItem.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt				= GsntalkUtil.getLong( firstStepItem.get( "crntDpstAmt" ) );
		int crntMontRentAmt				= GsntalkUtil.getInteger( firstStepItem.get( "crntMontRentAmt" ) );
		String keyMonExstsYn			= GsntalkUtil.getString( firstStepItem.get( "keyMonExstsYn" ) );
		long keyMonAmt					= GsntalkUtil.getLong( firstStepItem.get( "keyMonAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( firstStepItem.get( "prmmAmt" ) );
		String cmpltExpctDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( firstStepItem.get( "cmpltExpctDate" ) ) );
		
		// step 2 data
		String addr						= GsntalkUtil.getString( secondStepItem.get( "addr" ) );
		String dtlAddr					= GsntalkUtil.getString( secondStepItem.get( "dtlAddr" ) );
		String addrShortNm				= GsntalkUtil.getString( secondStepItem.get( "addrShortNm" ) );
		double lat						= GsntalkUtil.getDouble( secondStepItem.get( "lat" ) );
		double lng						= GsntalkUtil.getDouble( secondStepItem.get( "lng" ) );
		String mapDispYn				= GsntalkUtil.getString( secondStepItem.get( "mapDispYn" ) );
		String tmpAddrYn				= GsntalkUtil.getString( secondStepItem.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( secondStepItem.get( "unregistYn" ) );
		
		// step 3 data
		int flr							= GsntalkUtil.getInteger( thirdStepItem.get( "flr" ) );
		int allFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "allFlr" ) );
		int minFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "minFlr" ) );
		int maxFlr						= GsntalkUtil.getInteger( thirdStepItem.get( "maxFlr" ) );
		double splyArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "splyArea" ) ) );
		double prvArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "prvArea" ) ) );
		double lndArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "lndArea" ) ) );
		double totFlrArea				= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( thirdStepItem.get( "totFlrArea" ) ) );
		String useCnfrmDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( thirdStepItem.get( "useCnfrmDate" ) ) );
		String bldUsageGbCd				= GsntalkUtil.getString( thirdStepItem.get( "bldUsageGbCd" ) );
		String suggstnBldUsageGbCd		= GsntalkUtil.getString( thirdStepItem.get( "suggstnBldUsageGbCd" ) );
		String lndCrntUsageGbCd			= GsntalkUtil.getString( thirdStepItem.get( "lndCrntUsageGbCd" ) );
		String psblMovDayTypCd			= GsntalkUtil.getString( thirdStepItem.get( "psblMovDayTypCd" ) );
		String psblMovDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( thirdStepItem.get( "psblMovDate" ) ) );
		int monMntnceCost				= GsntalkUtil.getInteger( thirdStepItem.get( "monMntnceCost" ) );
		JSONArray mntnceCostTypItems	= GsntalkUtil.getJSONArray( thirdStepItem, "mntnceCostTypItems" );
		String loanGbCd					= GsntalkUtil.getString( thirdStepItem.get( "loanGbCd" ) );
		long loanAmt					= GsntalkUtil.getLong( thirdStepItem.get( "loanAmt" ) );
		String parkingPsblYn			= GsntalkUtil.getString( thirdStepItem.get( "parkingPsblYn" ) );
		int parkingCost					= GsntalkUtil.getInteger( thirdStepItem.get( "parkingCost" ) );
		
		// step 4 data
		int roomCnt						= GsntalkUtil.getInteger( fourthStepItem.get( "roomCnt" ) );
		int bathRoomCnt					= GsntalkUtil.getInteger( fourthStepItem.get( "bathRoomCnt" ) );
		String crntSectrGbCd			= GsntalkUtil.getString( fourthStepItem.get( "crntSectrGbCd" ) );
		String suggstnSectrGbCd			= GsntalkUtil.getString( fourthStepItem.get( "suggstnSectrGbCd" ) );
		String bldDirctnGbCd			= GsntalkUtil.getString( fourthStepItem.get( "bldDirctnGbCd" ) );
		String heatKindGbCd				= GsntalkUtil.getString( fourthStepItem.get( "heatKindGbCd" ) );
		double wghtPerPy				= GsntalkUtil.getDouble( fourthStepItem.get( "wghtPerPy" ) );
		String elvFcltExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "elvFcltExstsYn" ) );
		String frghtElvExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "frghtElvExstsYn" ) );
		String intrrYn					= GsntalkUtil.getString( fourthStepItem.get( "intrrYn" ) );
		String dockExstsYn				= GsntalkUtil.getString( fourthStepItem.get( "dockExstsYn" ) );
		String hoistExstsYn				= GsntalkUtil.getString( fourthStepItem.get( "hoistExstsYn" ) );
		String flrHghtTypGbCd			= GsntalkUtil.getString( fourthStepItem.get( "flrHghtTypGbCd" ) );
		String elctrPwrTypGbCd			= GsntalkUtil.getString( fourthStepItem.get( "elctrPwrTypGbCd" ) );
		String intnlStrctrTypCd			= GsntalkUtil.getString( fourthStepItem.get( "intnlStrctrTypCd" ) );
		String bultInYn					= GsntalkUtil.getString( fourthStepItem.get( "bultInYn" ) );
		String movInReprtPsblYn			= GsntalkUtil.getString( fourthStepItem.get( "movInReprtPsblYn" ) );
		String cityPlanYn				= GsntalkUtil.getString( fourthStepItem.get( "cityPlanYn" ) );
		String bldCnfrmIssueYn			= GsntalkUtil.getString( fourthStepItem.get( "bldCnfrmIssueYn" ) );
		String lndDealCnfrmApplYn		= GsntalkUtil.getString( fourthStepItem.get( "lndDealCnfrmApplYn" ) );
		String entrnceRoadExstsYn		= GsntalkUtil.getString( fourthStepItem.get( "entrnceRoadExstsYn" ) );
		String optionExstsYn			= GsntalkUtil.getString( fourthStepItem.get( "optionExstsYn" ) );
		JSONArray optionTypItems		= GsntalkUtil.getJSONArray( fourthStepItem, "optionTypItems" );

		// 단축주소가 없는경우 geocode 에서 조회해서 적용
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
			if( geocodeItem != null ) {
				addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
			}
		}
		
		// 매물 수정 ( 1 ~ 4 단계 정보 )
		propertyDAO.updateProperty( prptSeqno, estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt,
				keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn, flr, allFlr, minFlr, maxFlr,
				splyArea, prvArea, lndArea, totFlrArea, useCnfrmDate, bldUsageGbCd, suggstnBldUsageGbCd, lndCrntUsageGbCd, psblMovDayTypCd, psblMovDate, monMntnceCost, loanGbCd, loanAmt, parkingPsblYn, parkingCost,
				roomCnt, bathRoomCnt, crntSectrGbCd, suggstnSectrGbCd, bldDirctnGbCd, heatKindGbCd, wghtPerPy, elvFcltExstsYn, frghtElvExstsYn, intrrYn, dockExstsYn, hoistExstsYn, flrHghtTypGbCd,
				elctrPwrTypGbCd, intnlStrctrTypCd, bultInYn, movInReprtPsblYn, cityPlanYn, bldCnfrmIssueYn, lndDealCnfrmApplYn, entrnceRoadExstsYn, optionExstsYn, GsntalkConstants.NO, GsntalkConstants.NO );
		
		// 관리비 유형 삭제 후 재등록
		propertyDAO.removeAllPropertyMaintenanceTyps( prptSeqno );
		for( int i = 0; i < mntnceCostTypItems.size(); i ++ ) {
			propertyDAO.registerPropertyMaintenanceTyps( prptSeqno, GsntalkUtil.getString( mntnceCostTypItems.get( i ) ) );
		}
		
		// 옵션유형 삭제 후 재등록
		propertyDAO.removeAllPropertyOptionTyps( prptSeqno );
		JSONObject optionTypItem = null;
		String optionItemTypGbCd = null;
		JSONArray optionTypCdItems = null;
		for( int i = 0; i < optionTypItems.size(); i ++ ) {
			optionTypItem = (JSONObject)optionTypItems.get( i );
			
			optionItemTypGbCd = GsntalkUtil.getString( optionTypItem.get( "optionItemTypGbCd" ) );
			optionTypCdItems = GsntalkUtil.getJSONArray( optionTypItem, "optionTypCdItems" );
			for( int j = 0; j < optionTypCdItems.size(); j ++ ) {
				propertyDAO.registerPropertyOptionTyps( prptSeqno, optionItemTypGbCd, GsntalkUtil.getString( optionTypCdItems.get( j ) ) );
			}
		}
		
		// 매물 사진정보 업데이트 ( 5단계 정보 )
		propertyDAO.updatePropertyPhotoInfo( prptSeqno, smplSmrDscr, dtlExplntnDscr, prvtMemoDscr );
		
		JSONArray addFileItems = new JSONArray();
		JSONObject uploadItem = null;
		String uploadFileNm = "";
		String saveFileNm = "";
		String fileUrl = "";
		String repFileUrl = "";
		
		// 첨부한 대표사진이 있으면 갱신
		if( repFile != null && repFile.getSize() != 0L ) {
			// 대표 사진 S3 업로드 및 등록
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( estBrkMemOfcSeqno, prptSeqno, repFile );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			repFileUrl = fileUrl;
			
			// 매물 대표사진 삭제처리
			propertyDAO.removePropertyRepPhoto( prptSeqno );
			
			// 매물 대표사진 재등록
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.YES );
		}else {
			// 첨부한 대표사진이 없으면 기존 대표사진 정보에서 URL 획득
			for( PropertyPhotoVO vo : prptPhotoList ) {
				if( GsntalkConstants.YES.equals( vo.getRepPhotoYn() ) ) {
					repFileUrl = vo.getFileUrl();
				}
			}
		}
		
		// 삭제할 사진 삭제처리
		String delFileUrl = "";
		for( int i = 0; i < delFileUrls.size(); i ++ ) {
			delFileUrl = GsntalkUtil.getString( delFileUrls.get( i ) );
			
			// 매물사진 삭제처리
			propertyDAO.deletePropertyPhoto( prptSeqno, delFileUrl );
		}
		
		// 매물사진 삭제 후 재 조회
		prptPhotoList = propertyDAO.getPrptPhotoList( prptSeqno );
		if( GsntalkUtil.isEmptyList( prptPhotoList ) ) {
			prptPhotoList = new ArrayList<PropertyPhotoVO>();
		}
		
		// 기존 매물사진 획득
		for( PropertyPhotoVO vo : prptPhotoList ) {
			if( !GsntalkConstants.YES.equals( vo.getRepPhotoYn() ) ) {
				addFileItems.add( vo.getFileUrl() );
			}
		}
		
		// 추가사진 S3 업로드 및 등록
		for( MultipartFile file :  addFiles ) {
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( estBrkMemOfcSeqno, prptSeqno, file );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			
			addFileItems.add( fileUrl );
			
			// 매물 사진 등록
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.NO );
		}
		
		// 기존 5단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		RegistrationTmpDataStepVO fifthStepVO = gsntalkDAO.getTempdataOfRegistrationStepJsonFromPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, prptSeqno, 5 );
		
		JSONObject item = new JSONObject();
		item.put( "repFileUrl", repFileUrl );
		item.put( "addFileUrls", addFileItems );
		item.put( "smplSmrDscr", GsntalkXSSUtil.encodeXss( smplSmrDscr ) );
		item.put( "dtlExplntnDscr", GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		item.put( "prvtMemoDscr", GsntalkXSSUtil.encodeXss( prvtMemoDscr ) );
		
		String regTmpKey = "";
		if( fifthStepVO == null ) {
			// 임시키 생성
			regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			regTmpKey = fifthStepVO.getRegTmpKey();
			
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, 5, item.toJSONString() );
		}
		
		// 등록단계별임시정보 매물시퀀스 등록
		gsntalkDAO.updateTempDataPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT, regTmpKey, prptSeqno );
	}
	
	/**
	 * 일반회원 매물등록 1단계 임시저장
	 * 
	 * @param memSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param dealAmtDiscsnPsblYn
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param existngLeaseExstsYn
	 * @param crntDpstAmt
	 * @param crntMontRentAmt
	 * @param keyMonExstsYn
	 * @param keyMonAmt
	 * @param prmmAmt
	 * @param cmpltExpctDate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject registerMemberPrptStep1RegItem( long memSeqno, String estateTypGbCd, String estateTypCd, String tranTypGbCd, long dealAmt, String dealAmtDiscsnPsblYn, long dpstAmt, int montRentAmt,
			String existngLeaseExstsYn, long crntDpstAmt, int crntMontRentAmt, String keyMonExstsYn, long keyMonAmt, long prmmAmt, String cmpltExpctDate
	)throws Exception {
		/** 매물유형 구분코드 검증 */
		if( GsntalkUtil.isEmpty( estateTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL", "PRESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypGbCd 값이 잘못됨 -> see CommonCode [ESTATE_TYP_GB_CD]" );
		}
		
		/** 매물 구분코드 검증 */
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "estateTypCd 값 없음" );
		}
		if( "REGIDENTAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "APT", "OFT", "SMH", "TWN", "SHC", "HUS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in REGIDENTAL ) -> see CommonCode [ESTATE_TYP_GB_CD - REGIDENTAL]" );
			}
		}else if( "COMMERCIAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "KOG", "STR", "BLD", "LND", "FTR" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in COMMERCIAL ) -> see CommonCode [ESTATE_TYP_GB_CD - COMMERCIAL]" );
			}
		}else {
			if( !GsntalkUtil.isIn( estateTypCd, "CPS", "SPS", "APS", "OPS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "estateTypCd 값이 잘못됨 ( in PRESALE ) -> see CommonCode [ESTATE_TYP_GB_CD - PRESALE]" );
			}
		}
		
		/** 거래유형 구분코드 검증 */
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "LEASE", "LEASE_ST", "CHARTER", "MONTLY", "RESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCd 값이 잘못됨 -> see CommonCode [TRAN_TYP_GB_CD]" );
		}
		
		/** 거래유형에 따른 거래금액 검증 */
		if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
			if( dealAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dealAmt = 0L;
		}
		
		/** 거래금액 협의가능 여부 검증 */
		if( GsntalkUtil.isEmpty( dealAmtDiscsnPsblYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dealAmtDiscsnPsblYn 값 없음" );
		}
		if( !GsntalkUtil.isIn( dealAmtDiscsnPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealAmtDiscsnPsblYn 값이 잘못됨 -> only Y or N value possible" );
		}
		
		/** 거래유형에 따른 보증금액 밍 월 임대료 검증 */
		if( GsntalkUtil.isIn( tranTypGbCd, "LEASE", "LEASE_ST", "MONTLY" ) ) {
			if( dpstAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( montRentAmt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "montRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dpstAmt = 0L;
			montRentAmt = 0;
		}
		
		/** 매물유형 구분에 따른 기존임대차 존재여부 검증 */
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			if( GsntalkUtil.isEmpty( existngLeaseExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "existngLeaseExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( existngLeaseExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "existngLeaseExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			existngLeaseExstsYn = null;
		}
		
		/** 매물유형 구분에 따른 현 보증금, 현 월임대료 검증 */
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			// 기존 임대차 존재하면 현보증금/현월임대료 값 검증
			if( GsntalkConstants.YES.equals( existngLeaseExstsYn ) ) {
				if( crntDpstAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but crntDpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
				if( crntMontRentAmt == 0 ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but crntMontRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			crntDpstAmt = 0L;
			crntMontRentAmt = 0;
		}
		
		/** 매물 구분에 따른 권리금 존재여부, 권리금액 검증 */
		if( "STR".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( keyMonExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "keyMonExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( keyMonExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "keyMonExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
			
			if( GsntalkConstants.YES.equals( keyMonExstsYn ) ) {
				if( keyMonAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "권리금 존재, but keyMonAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			keyMonExstsYn = null;
			keyMonAmt = 0L;
		}
		
		/** 매물유형 구분에 따른 준공 예정일자 검증 */
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isEmpty( cmpltExpctDate ) && !GsntalkUtil.is8DateFormat( cmpltExpctDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "cmpltExpctDate 값 잘못됨" );
			}
		}else {
			prmmAmt = 0L;
			cmpltExpctDate = null;
		}
		
		// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
		String regTmpKey = GsntalkUtil.createRegistrationTempKey();
		
		JSONObject item = new JSONObject();
		item.put( "estateTypGbCd", estateTypGbCd );
		item.put( "estateTypCd", estateTypCd );
		item.put( "tranTypGbCd", tranTypGbCd );
		item.put( "dealAmt", dealAmt );
		item.put( "dealAmtDiscsnPsblYn", dealAmtDiscsnPsblYn );
		item.put( "dpstAmt", dpstAmt );
		item.put( "montRentAmt", montRentAmt );
		item.put( "existngLeaseExstsYn", existngLeaseExstsYn );
		item.put( "crntDpstAmt", crntDpstAmt );
		item.put( "crntMontRentAmt", crntMontRentAmt );
		item.put( "keyMonExstsYn", keyMonExstsYn );
		item.put( "keyMonAmt", keyMonAmt );
		item.put( "prmmAmt", prmmAmt );
		item.put( "cmpltExpctDate", cmpltExpctDate );
		
		// 기존 1단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 1, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 1, item.toJSONString() );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * 일반회원 매물등록 2단계 임시저장
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param addr
	 * @param dtlAddr
	 * @param addrShortNm
	 * @param lat
	 * @param lng
	 * @param mapDispYn
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public void registerMemberPrptStep2RegItem( long memSeqno, String regTmpKey, String addr, String dtlAddr, String addrShortNm, double lat, double lng, String mapDispYn, String tmpAddrYn, String unregistYn )throws Exception {
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		// 일반회원 매물등록 1단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 이전 임시데이터에서 매물유형 구분코드 / 매물 구분코드 / 거래유형 구분코드 추출
		JSONObject tempItem = (JSONObject)jsonParser.parse( tmpJsonData );
		String estateTypGbCd = GsntalkUtil.getString( tempItem.get( "estateTypGbCd" ) );
		
		/** 필수 값 검증 */
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "addr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( GsntalkUtil.isEmpty( mapDispYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "mapDispYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( mapDispYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "mapDispYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkUtil.isEmpty( tmpAddrYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tmpAddrYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( tmpAddrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tmpAddrYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkConstants.YES.equals( tmpAddrYn ) && GsntalkUtil.isEmpty( addrShortNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "임시주소로 설정함, but addrShortNm 값 없음" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lat 값 없음" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "lng 값 없음" );
		}
		
		/** 매물유형 구분에 따른 미등기여부 값 검증 */
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( GsntalkUtil.isEmpty( unregistYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "unregistYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( unregistYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "unregistYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			unregistYn = null;
		}
		
		// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 2 );
		
		JSONObject item = new JSONObject();
		item.put( "addr", addr );
		item.put( "dtlAddr", dtlAddr );
		item.put( "addrShortNm", addrShortNm );
		item.put( "lat", lat );
		item.put( "lng", lng );
		item.put( "mapDispYn", mapDispYn );
		item.put( "tmpAddrYn", tmpAddrYn );
		item.put( "unregistYn", unregistYn );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 2, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 2, item.toJSONString() );
		}
	}
	
	/**
	 * 일반회원 매물등록 3단계 최종등록
	 * 
	 * @param memSeqno
	 * @param regTmpKey
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @param repFile
	 * @param addFiles
	 * @param dtlExplntnDscr
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void registerMemberPrptFinalStepRegItem( long memSeqno, String regTmpKey, double prvArea, double lndArea, double totFlrArea, MultipartFile repFile, List<MultipartFile> addFiles, String dtlExplntnDscr )throws Exception {
		if( GsntalkUtil.isEmpty( regTmpKey ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "regTmpKey" );
		}
		
		if( GsntalkUtil.isEmpty( dtlExplntnDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "dtlExplntnDscr" );
		}
		
		if( repFile == null || repFile.getSize() == 0L ) {
			// 필수 첨부파일 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_ATTACHMENT, "repFile" );
		}
		
		if( GsntalkUtil.isEmptyList( addFiles ) ) {
			addFiles = new ArrayList<MultipartFile>();
		}
		if( addFiles.size() > 7 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE );
		}
		
		String orgFileNm = repFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
			// 허용되지 않은 파일 포맷
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
		}
		
		for( MultipartFile file : addFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		// 1단계 임시저장 JSON 조회
		String firstJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( firstJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 2단계 임시저장 JSON 조회
		String secondJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( secondJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "2nd data missing" );
		}
		
		JSONObject firstStepItem = (JSONObject)jsonParser.parse( firstJsonData );
		JSONObject secondStepItem = (JSONObject)jsonParser.parse( secondJsonData );
		
		// step 1 data
		String estateTypGbCd			= GsntalkUtil.getString( firstStepItem.get( "estateTypGbCd" ) );
		String estateTypCd				= GsntalkUtil.getString( firstStepItem.get( "estateTypCd" ) );
		String tranTypGbCd				= GsntalkUtil.getString( firstStepItem.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( firstStepItem.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn		= GsntalkUtil.getString( firstStepItem.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt					= GsntalkUtil.getLong( firstStepItem.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( firstStepItem.get( "montRentAmt" ) );
		String existngLeaseExstsYn		= GsntalkUtil.getString( firstStepItem.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt				= GsntalkUtil.getLong( firstStepItem.get( "crntDpstAmt" ) );
		int crntMontRentAmt				= GsntalkUtil.getInteger( firstStepItem.get( "crntMontRentAmt" ) );
		String keyMonExstsYn			= GsntalkUtil.getString( firstStepItem.get( "keyMonExstsYn" ) );
		long keyMonAmt					= GsntalkUtil.getLong( firstStepItem.get( "keyMonAmt" ) );
		long prmmAmt					= GsntalkUtil.getLong( firstStepItem.get( "prmmAmt" ) );
		String cmpltExpctDate			= GsntalkUtil.parseNumberString( GsntalkUtil.getString( firstStepItem.get( "cmpltExpctDate" ) ) );
		
		// step 2 data
		String addr						= GsntalkUtil.getString( secondStepItem.get( "addr" ) );
		String dtlAddr					= GsntalkUtil.getString( secondStepItem.get( "dtlAddr" ) );
		String addrShortNm				= GsntalkUtil.getString( secondStepItem.get( "addrShortNm" ) );
		double lat						= GsntalkUtil.getDouble( secondStepItem.get( "lat" ) );
		double lng						= GsntalkUtil.getDouble( secondStepItem.get( "lng" ) );
		String mapDispYn				= GsntalkUtil.getString( secondStepItem.get( "mapDispYn" ) );
		String tmpAddrYn				= GsntalkUtil.getString( secondStepItem.get( "tmpAddrYn" ) );
		String unregistYn				= GsntalkUtil.getString( secondStepItem.get( "unregistYn" ) );
		
		// 매물 구분에 따른 필수 입력값 검증
		if( "BLD".equals( estateTypCd ) ) {
			if( totFlrArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "매물유형 - 건뭎, but totFlrArea is empty." );
			}
			lndArea = 0.0d;
			prvArea = 0.0d;
		}else if( "LND".equals( estateTypCd ) ) {
			if( lndArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "매물유형 - 토지/임야, but lndArea is empty." );
			}
			totFlrArea = 0.0d;
			prvArea = 0.0d;
		}else {
			if( prvArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "매물유형이 건물/토지임야가 아님, but prvArea is empty." );
			}
			totFlrArea = 0.0d;
			lndArea = 0.0d;
		}
		
		// 제곱미터 -> 평 변환
		totFlrArea = GsntalkUtil.parseMetersToPyung( totFlrArea );
		lndArea = GsntalkUtil.parseMetersToPyung( lndArea );
		prvArea = GsntalkUtil.parseMetersToPyung( prvArea );
		
		// 단축주소가 없는경우 geocode 에서 조회해서 적용
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
			if( geocodeItem != null ) {
				addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
			}
		}
		
		// register property
		int nextNum = propertyDAO.getNextPPrptRegNum();
		
		// 매물 등록 ( 1 ~ 3 단계 정보 )
		long prptSeqno = propertyDAO.registerMembersProperty( memSeqno, GsntalkUtil.createPPrptRegno( nextNum ), estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt,
				keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn, prvArea, lndArea, totFlrArea );
		
		// 매물 사진정보 등록 ( 3단계 정보 )
		propertyDAO.registerPropertyPhotoInfo( prptSeqno, null, dtlExplntnDscr, null, null );
		
		// 대표 사진 S3 업로드 및 등록
		JSONObject uploadItem = gsntalkS3Util.uploadPropertyImageFile( 0L, prptSeqno, repFile );
		
		String uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
		String saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
		String fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
		
		String repFileUrl = fileUrl;
		JSONArray addFileItems = new JSONArray();
		
		propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.YES );
		
		// 추가사진 S3 업로드 및 등록
		for( MultipartFile file :  addFiles ) {
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( 0L, prptSeqno, file );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			
			addFileItems.add( fileUrl );
			
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.NO );
		}
		
		// 기존 3단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 3 );
		
		JSONObject item = new JSONObject();
		item.put( "prvArea", prvArea );
		item.put( "lndArea", lndArea );
		item.put( "totFlrArea", totFlrArea );
		item.put( "repFileUrl", repFileUrl );
		item.put( "addFileUrls", addFileItems );
		item.put( "dtlExplntnDscr", GsntalkXSSUtil.encodeXss( dtlExplntnDscr ) );
		
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 3, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, 3, item.toJSONString() );
		}
		
		// 등록단계별임시정보 매물시퀀스 등록
		gsntalkDAO.updateTempDataPrptSeqno( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_PRPT, regTmpKey, prptSeqno );
		
		// 매물등록요청 알림 등록 ( 매물알림 )
		String notiTtl = "매물등록 요청 완료";
		String notiDscr = "<strong>" + GsntalkUtil.getSubAddr( addr, 1 ) + " / " + gsntalkDAO.getCommonCodeNm( "ESTATE_TYP_CD", estateTypGbCd, estateTypCd ) + "</strong> 등록 요청이 완료 되었어요, 지금 확인해보세요.";
		gsntalkDAO.registerNotification( memSeqno, "PRPT", null, notiTtl, notiDscr, prptSeqno, 0L );
	}
	
	/**
	 * 등록임시키로 매물유형 및 거래유형 정보 조회 ( 중개회원/일반회원 공통 )
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param regTmpKey
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getProperyTypInfo( String memTypCd, long memSeqno, String regTmpKey )throws Exception {
		String regClasCd = GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memTypCd ) ? GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPT : GsntalkConstants.REG_CLAS_CD_MY_PRPT;
		
		// 1단계 임시저장 JSON 조회
		String firstJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( firstJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		JSONObject firstStepItem		= (JSONObject)jsonParser.parse( firstJsonData );
		
		JSONObject item = new JSONObject();
		item.put( "estateTypGbCd", GsntalkUtil.getString( firstStepItem.get( "estateTypGbCd" ) ) );
		item.put( "estateTypCd", GsntalkUtil.getString( firstStepItem.get( "estateTypCd" ) ) );
		item.put( "tranTypGbCd", GsntalkUtil.getString( firstStepItem.get( "tranTypGbCd" ) ) );
		
		return item;
	}
	
	/**
	 * 일반회원 - 내매물 목록조회 ( 페이징 )
	 * 
	 * @param memSeqno
	 * @param regStatGbCd
	 * @param tranTypGbCd
	 * @param estateTypCd
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMyPropertyItems( long memSeqno, String regStatGbCd, String tranTypGbCd, String estateTypCd, String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<PropertyVO> propertyList = propertyDAO.getMyPropertyList( memSeqno, regStatGbCd, tranTypGbCd, estateTypCd, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}else {
			totList = propertyList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = null;
		JSONObject areaItem = null;
		JSONArray pyAreaItems = null;
		JSONArray meterAreaItems = null;
		
		for( PropertyVO vo : propertyList ) {
			item = new JSONObject();
			
			item.put( "prptSeqno", vo.getPrptSeqno() );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "reprImgUrl", vo.getReprImgUrl() );
			item.put( "estateTypGbCd", vo.getEstateTypGbCd() );
			item.put( "estateTypCd", vo.getEstateTypCd() );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "dtlAddr", vo.getDtlAddr() );
			item.put( "tranTypGbCd", vo.getTranTypGbCd() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( vo.getTranTypGbCd(), "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			/** 면적정보 적용 */
			areaItem = new JSONObject();
			pyAreaItems = new JSONArray();
			meterAreaItems = new JSONArray();
			// 건물이면 대지면적/연면적
			if( "BLD".equals( estateTypCd ) ) {
				if( vo.getLndArea() != 0.0d ) {
					pyAreaItems.add( "대지 " + vo.getLndArea() );
					meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				}
				if( vo.getTotFlrArea() != 0.0d ) {
					pyAreaItems.add( "연 " + vo.getTotFlrArea() );
					meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				}
				
			// 토지/임야면 대지면적
			}else if( "LND".equals( estateTypCd ) ) {
				if( vo.getLndArea() != 0.0d ) {
					pyAreaItems.add( "대지 " + vo.getLndArea() );
					meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				}
			
			// 단독공장이면 전용면적/대지면적/연면적
			}else if( "FTR".equals( estateTypCd ) ) {
				if( vo.getPrvArea() != 0.0d ) {
					pyAreaItems.add( "전용 " + vo.getPrvArea() );
					meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				}
				if( vo.getLndArea() != 0.0d ) {
					pyAreaItems.add( "대지 " + vo.getLndArea() );
					meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				}
				if( vo.getTotFlrArea() != 0.0d ) {
					pyAreaItems.add( "연 " + vo.getTotFlrArea() );
					meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				}
			
			// 이외에는 공급면적/전용면적
			}else {
				if( vo.getSplyArea() != 0.0d ) {
					pyAreaItems.add( "공급 " + vo.getSplyArea() );
					meterAreaItems.add( "공급 " + GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				}
				if( vo.getPrvArea() != 0.0d ) {
					pyAreaItems.add( "전용 " + vo.getPrvArea() );
					meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				}
			}
			areaItem.put( "pyAreaItems", pyAreaItems );
			areaItem.put( "meterAreaItems", meterAreaItems );
			
			item.put( "costDscr", costDscr );
			item.put( "areaItem", areaItem );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * 일반회원 - 내 매물 삭제
	 * 
	 * @param prptSeqnoItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deleteMyPropertyItems( long memSeqno, JSONArray prptSeqnoItems )throws Exception {
		long prptSeqno = 0L;
		PropertyVO propertyVO = null;
		
		for( int i = 0; i < prptSeqnoItems.size(); i ++ ) {
			prptSeqno = GsntalkUtil.getLong( prptSeqnoItems.get( i ) );
			propertyVO = propertyDAO.getMyPropertyInfo( memSeqno, prptSeqno );
			
			if( propertyVO == null ) {
				// 유효하지 않은 매물
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AVAIL_PROPERTY, "not found property - " + prptSeqno );
			}
			
			propertyDAO.deleteMyPropertyItem( memSeqno, prptSeqno );
		}
	}
	
	/**
	 * Admin - 일반회원 매물 목록조회 ( 페이징 )
	 * 
	 * @param regDtSrchTyp
	 * @param dealStatGbCd
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMembersPropertyItems( String regDtSrchTyp, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, String treatStatGbCd, String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		String cd = null;
		for( int i = 0; i < dealStatGbCdItems.size(); i ++ ) {
			cd = GsntalkUtil.getString( dealStatGbCdItems.get( i ) );
			if( GsntalkUtil.isEmpty( cd ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "dealStatGbCdItems[" + i + "] value is empty." );
			}
		}
		for( int i = 0; i < tranTypGbCdItems.size(); i ++ ) {
			cd = GsntalkUtil.getString( tranTypGbCdItems.get( i ) );
			if( GsntalkUtil.isEmpty( cd ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranTypGbCdItems[" + i + "] value is empty." );
			}
		}
		
		List<PropertyVO> propertyList = propertyDAO.getMembersPropertyList( regDtSrchTyp, dealStatGbCdItems, tranTypGbCdItems, treatStatGbCd, srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}else {
			totList = propertyList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = "";
		
		for( PropertyVO vo : propertyList ) {
			item = new JSONObject();
			
			item.put( "prptSeqno", vo.getPrptSeqno() );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "dealStatGbCd", vo.getDealStatGbCd() );
			item.put( "dealStatGbNm", vo.getDealStatGbNm() );
			item.put( "memName", vo.getMemName() );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "dtlAddr", vo.getDtlAddr() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( vo.getTranTypGbCd(), "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			item.put( "costDscr", costDscr );
			
			item.put( "regDt", vo.getRegDt() );
			item.put( "adminRegDt", vo.getAdminRegDt() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 일반회원 매물 목록조회 - Excel Download
	 * 
	 * @param regDtSrchTyp
	 * @param dealStatGbCd
	 * @param tranTypGbCd
	 * @param treatStatGbCd
	 * @param srchVal
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public XSSFWorkbook downloadMembersPropertyItems( String regDtSrchTyp, String dealStatGbCd, String tranTypGbCd, String treatStatGbCd, String srchVal )throws Exception {
		List<PropertyVO> propertyList = propertyDAO.getMembersPropertyExcelDownloadList( regDtSrchTyp, dealStatGbCd, tranTypGbCd, treatStatGbCd, srchVal );
		
		String[] titles = new String[]	{ "No",							"매물번호",							"상태",							"요청자명",						"매물종류",						"주소",							"거래정보",						"회원 등록일",						"관리자 등록일" };
		String[] fields = new String[]	{ "no",							"prptNo",							"dealStatGbNm",					"memName",						"estateTypNm",					"addr",							"costDscr",						"regDt",						"adminRegDt" };
		int[] sizes		= new int[] 	{ GsntalkExcelUtil.WIDTH_NO, 	GsntalkExcelUtil.WIDTH_SMALL, 		GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_MAX,		GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL };
		int[] aligns	= new int[] 	{ GsntalkExcelUtil.CENTER, 		GsntalkExcelUtil.CENTER, 			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.LEFT,			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER };
	
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = "";
		int no = 1;
		
		for( PropertyVO vo : propertyList ) {
			item = new JSONObject();
			item.put( "no", String.valueOf( no ) );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "dealStatGbNm", vo.getDealStatGbNm() );
			item.put( "memName", vo.getMemName() );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "addr", vo.getAddr() + " " + vo.getDtlAddr() );
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( vo.getTranTypGbCd(), "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			item.put( "costDscr", vo.getTranTypGbNm() + " " + costDscr );
			
			item.put( "regDt", vo.getRegDt() );
			item.put( "adminRegDt", vo.getAdminRegDt() );
			
			items.add( item );
			
			no ++;
		}
		
		return new GsntalkExcelUtil( titles, fields, sizes, aligns ).getXSSFExcelWorkbook( items );
	}
	
	/**
	 * Admin - 일반회원 매물 삭제
	 * 
	 * @param prptSeqnoItems
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deleteMembersPropertyItems( JSONArray prptSeqnoItems )throws Exception {
		for( int i = 0; i < prptSeqnoItems.size(); i ++ ) {
			propertyDAO.deleteMembersPropertyItem( GsntalkUtil.getLong( prptSeqnoItems.get( i ) ) );
		}
	}
	
	/**
	 * Admin - 일반회원/중개회원 매물 상세정보 조회( 수정용 )
	 * 
	 * @param prptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getMembersPropertyDtlItem( long prptSeqno ) throws Exception {
		PropertyVO propertyVO = propertyDAO.getMembersPropertyDtlItem( prptSeqno );
		if( propertyVO == null ) {
			// 유효하지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AVAIL_PROPERTY, "not found property - " + prptSeqno );
		}
		
		JSONObject item = new JSONObject();
		item.put( "prptSeqno", propertyVO.getPrptSeqno() );
		item.put( "prptNo", propertyVO.getPrptNo() );
		
		/** 기본정보 세팅 */
		JSONObject subItem = new JSONObject();
		subItem.put( "estateTypGbCd",				propertyVO.getEstateTypGbCd() );
		subItem.put( "estateTypCd",					propertyVO.getEstateTypCd() );
		subItem.put( "addr",						propertyVO.getAddr() );
		subItem.put( "dtlAddr",						propertyVO.getDtlAddr() );
		subItem.put( "addrShortNm",					propertyVO.getAddrShortNm() );
		subItem.put( "lat",							propertyVO.getLat() );
		subItem.put( "lng",							propertyVO.getLng() );
		subItem.put( "mapDispYn",					propertyVO.getMapDispYn() );
		subItem.put( "tmpAddrYn",					propertyVO.getTmpAddrYn() );
		subItem.put( "unregistYn",					propertyVO.getUnregistYn() );
		subItem.put( "flr",							propertyVO.getFlr() );
		subItem.put( "allFlr",						propertyVO.getAllFlr() );
		subItem.put( "minFlr",						propertyVO.getMinFlr() );
		subItem.put( "maxFlr",						propertyVO.getMaxFlr() );
		subItem.put( "splyArea",					GsntalkUtil.parsePyungToMeters( propertyVO.getSplyArea() ) );
		subItem.put( "prvArea",						GsntalkUtil.parsePyungToMeters( propertyVO.getPrvArea() ) );
		subItem.put( "lndArea",						GsntalkUtil.parsePyungToMeters( propertyVO.getLndArea() ) );
		subItem.put( "totFlrArea",					GsntalkUtil.parsePyungToMeters( propertyVO.getTotFlrArea() ) );
		subItem.put( "useCnfrmDate",				propertyVO.getUseCnfrmDate() );
		subItem.put( "bldUsageGbCd",				propertyVO.getBldUsageGbCd() );
		subItem.put( "suggstnBldUsageGbCd",			propertyVO.getSuggstnBldUsageGbCd() );
		subItem.put( "lndCrntUsageGbCd",			propertyVO.getLndCrntUsageGbCd() );
		subItem.put( "psblMovDayTypCd",				propertyVO.getPsblMovDayTypCd() );
		subItem.put( "psblMovDate",					propertyVO.getPsblMovDate() );
		subItem.put( "monMntnceCost",				propertyVO.getMonMntnceCost() );
		subItem.put( "loanGbCd",					propertyVO.getLoanGbCd() );
		subItem.put( "loanAmt",						propertyVO.getLoanAmt() );
		subItem.put( "parkingPsblYn",				propertyVO.getParkingPsblYn() );
		subItem.put( "parkingCost",					propertyVO.getParkingCost() );
		
		// 선택 관리비 유형코드 목록조회
		List<String> mntnceCostTypList = propertyDAO.getPrptMntnceCostTypList( prptSeqno );
		if( GsntalkUtil.isEmptyList( mntnceCostTypList ) ) {
			mntnceCostTypList = new ArrayList<String>();
		}
		JSONArray mntnceCostTypItems = new JSONArray();
		for( String mntnceCostTypCd : mntnceCostTypList ) {
			mntnceCostTypItems.add( mntnceCostTypCd );
		}
		subItem.put( "mntnceCostTypItems", mntnceCostTypItems );
		item.put( "baseItem", subItem );
		
		/** 거래정보 세팅 */
		subItem = new JSONObject();
		subItem.put( "tranTypGbCd",					propertyVO.getTranTypGbCd() );
		subItem.put( "dealAmt",						propertyVO.getDealAmt() );
		subItem.put( "dealAmtDiscsnPsblYn",			propertyVO.getDealAmtDiscsnPsblYn() );
		subItem.put( "dpstAmt",						propertyVO.getDpstAmt() );
		subItem.put( "montRentAmt",					propertyVO.getMontRentAmt() );
		subItem.put( "existngLeaseExstsYn",			propertyVO.getExistngLeaseExstsYn() );
		subItem.put( "crntDpstAmt",					propertyVO.getCrntDpstAmt() );
		subItem.put( "crntMontRentAmt",				propertyVO.getCrntMontRentAmt() );
		subItem.put( "keyMonExstsYn",				propertyVO.getKeyMonExstsYn() );
		subItem.put( "keyMonAmt",					propertyVO.getKeyMonAmt() );
		subItem.put( "prmmAmt",						propertyVO.getPrmmAmt() );
		subItem.put( "cmpltExpctDate",				propertyVO.getCmpltExpctDate() );
		item.put( "tranItem", subItem );
		
		/** 추가정보 세팅 */
		subItem = new JSONObject();
		subItem.put( "roomCnt",						propertyVO.getRoomCnt() );
		subItem.put( "bathRoomCnt",					propertyVO.getBathRoomCnt() );
		subItem.put( "crntSectrGbCd",				propertyVO.getCrntSectrGbCd() );
		subItem.put( "suggstnSectrGbCd",			propertyVO.getSuggstnSectrGbCd() );
		subItem.put( "bldDirctnGbCd",				propertyVO.getBldDirctnGbCd() );
		subItem.put( "heatKindGbCd",				propertyVO.getHeatKindGbCd() );
		subItem.put( "wghtPerPy",					propertyVO.getWghtPerPy() );
		subItem.put( "elvFcltExstsYn",				propertyVO.getElvFcltExstsYn() );
		subItem.put( "frghtElvExstsYn",				propertyVO.getFrghtElvExstsYn() );
		subItem.put( "intrrYn",						propertyVO.getIntrrYn() );
		subItem.put( "dockExstsYn",					propertyVO.getDockExstsYn() );
		subItem.put( "hoistExstsYn",				propertyVO.getHoistExstsYn() );
		subItem.put( "flrHghtTypGbCd",				propertyVO.getFlrHghtTypGbCd() );
		subItem.put( "elctrPwrTypGbCd",				propertyVO.getElctrPwrTypGbCd() );
		subItem.put( "intnlStrctrTypCd",			propertyVO.getIntnlStrctrTypCd() );
		subItem.put( "bultInYn",					propertyVO.getBultInYn() );
		subItem.put( "movInReprtPsblYn",			propertyVO.getMovInReprtPsblYn() );
		subItem.put( "cityPlanYn",					propertyVO.getCityPlanYn() );
		subItem.put( "bldCnfrmIssueYn",				propertyVO.getBldCnfrmIssueYn() );
		subItem.put( "lndDealCnfrmApplYn",			propertyVO.getLndDealCnfrmApplYn() );
		subItem.put( "entrnceRoadExstsYn",			propertyVO.getEntrnceRoadExstsYn() );
		subItem.put( "optionExstsYn",				propertyVO.getOptionExstsYn() );
		
		// 선택 옵션유형 목록조회
		List<PropertyOptionVO> prptOptionList = propertyDAO.getPrptOptionTypList( prptSeqno );
		if( GsntalkUtil.isEmptyList( prptOptionList ) ) {
			prptOptionList = new ArrayList<PropertyOptionVO>();
		}
		
		JSONArray optionTypItems = new JSONArray();
		JSONObject optionTypItem = null;
		JSONArray optionTypCdItems = null;
		String[] optionTypCds = null;
		for( PropertyOptionVO vo : prptOptionList ) {
			optionTypItem = new JSONObject();
			optionTypItem.put( "optionItemTypGbCd", vo.getOptionItemTypGbCd() );
			
			optionTypCdItems = new JSONArray();
			optionTypCds = vo.getOptionItemTypCd().split( "," );
			for( String optionTypCd : optionTypCds ) {
				optionTypCdItems.add( optionTypCd );
			}
			optionTypItem.put( "optionTypCdItems", optionTypCdItems );
			
			optionTypItems.add( optionTypItem );
		}
		subItem.put( "optionTypItems", optionTypItems );
		item.put( "additionItem", subItem );
		
		/** 사진 및 상세설명 정보 세팅 */
		subItem = new JSONObject();
		subItem.put( "smplSmrDscr",					propertyVO.getSmplSmrDscr() );
		subItem.put( "dtlExplntnDscr",				propertyVO.getDtlExplntnDscr() );
		subItem.put( "matterPortLinkUrl",			propertyVO.getMatterPortLinkUrl() );
		
		// 추가사진 목록조회
		List<PropertyPhotoVO> propertyPhotoList = propertyDAO.getPrptPhotoList( prptSeqno );
		if( GsntalkUtil.isEmptyList( propertyPhotoList ) ) {
			propertyPhotoList = new ArrayList<PropertyPhotoVO>();
		}
		
		JSONArray addFileUrls = new JSONArray();
		for( PropertyPhotoVO vo : propertyPhotoList ) {
			if( GsntalkConstants.YES.equals( vo.getRepPhotoYn() ) ) {
				subItem.put( "repFileUrl", vo.getFileUrl() );
			}else {
				addFileUrls.add( vo.getFileUrl() );
			}
		}
		subItem.put( "addFileUrls", addFileUrls );
		item.put( "photoItem", subItem );
		
		return item;
	}
	
	/**
	 * Admin - 일반회원/중개회원 매물 상세정보 수정 ( 일반회원의 기존 공개되지 않은매물의 경우 공개시작 )
	 * 
	 * @param prptSeqno
	 * @param baseItem
	 * @param tranItem
	 * @param additionItem
	 * @param photoItem
	 * @param delFileUrls
	 * @param repFile
	 * @param addFiles
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void openMembersPropertyItem( long prptSeqno, JSONObject baseItem, JSONObject tranItem, JSONObject additionItem, JSONObject photoItem, JSONArray delFileUrls, MultipartFile repFile, List<MultipartFile> addFiles )throws Exception {
		PropertyVO propertyVO = propertyDAO.isAvailMembersProperty (prptSeqno );
		if( propertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 일반회원이 등록한 매물이 아님 prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( propertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "수정 불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( propertyVO.getExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "수정 불가 (이미 만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		int validationCnt = 0;
		
		/** 기본정보 추출 ( baseItem ) 및 검증 */
		String estateTypGbCd				= GsntalkUtil.getString( baseItem.get( "estateTypGbCd" ) );
		String estateTypCd					= GsntalkUtil.getString( baseItem.get( "estateTypCd" ) );
		String addr							= GsntalkUtil.getString( baseItem.get( "addr" ) );
		String dtlAddr						= GsntalkUtil.getString( baseItem.get( "dtlAddr" ) );
		String addrShortNm					= GsntalkUtil.getString( baseItem.get( "addrShortNm" ) );
		double lat							= GsntalkUtil.getDouble( baseItem.get( "lat" ) );
		double lng							= GsntalkUtil.getDouble( baseItem.get( "lng" ) );
		String mapDispYn					= GsntalkUtil.getString( baseItem.get( "mapDispYn" ) );
		String tmpAddrYn					= GsntalkUtil.getString( baseItem.get( "tmpAddrYn" ) );
		String unregistYn					= GsntalkUtil.getString( baseItem.get( "unregistYn" ) );
		int flr								= GsntalkUtil.getInteger( baseItem.get( "flr" ) );
		int allFlr							= GsntalkUtil.getInteger( baseItem.get( "allFlr" ) );
		int minFlr							= GsntalkUtil.getInteger( baseItem.get( "minFlr" ) );
		int maxFlr							= GsntalkUtil.getInteger( baseItem.get( "maxFlr" ) );
		double splyArea						= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "splyArea" ) ) );
		double prvArea						= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "prvArea" ) ) );
		double lndArea						= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "lndArea" ) ) );
		double totFlrArea					= GsntalkUtil.parseMetersToPyung( GsntalkUtil.getDouble( baseItem.get( "totFlrArea" ) ) );
		String useCnfrmDate					= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "useCnfrmDate" ) ) );
		String bldUsageGbCd					= GsntalkUtil.getString( baseItem.get( "bldUsageGbCd" ) );
		String suggstnBldUsageGbCd			= GsntalkUtil.getString( baseItem.get( "suggstnBldUsageGbCd" ) );
		String lndCrntUsageGbCd				= GsntalkUtil.getString( baseItem.get( "lndCrntUsageGbCd" ) );
		String psblMovDayTypCd				= GsntalkUtil.getString( baseItem.get( "psblMovDayTypCd" ) );
		String psblMovDate					= GsntalkUtil.parseNumberString( GsntalkUtil.getString( baseItem.get( "psblMovDate" ) ) );
		int monMntnceCost					= GsntalkUtil.getInteger( baseItem.get( "monMntnceCost" ) );
		JSONArray mntnceCostTypItems		= GsntalkUtil.getJSONArray( baseItem, "mntnceCostTypItems" );
		String loanGbCd						= GsntalkUtil.getString( baseItem.get( "loanGbCd" ) );
		long loanAmt						= GsntalkUtil.getLong( baseItem.get( "loanAmt" ) );
		String parkingPsblYn				= GsntalkUtil.getString( baseItem.get( "parkingPsblYn" ) );
		int parkingCost						= GsntalkUtil.getInteger( baseItem.get( "parkingCost" ) );
		
		// 매물유형 구분코드 검증
		if( GsntalkUtil.isEmpty( estateTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> estateTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL", "PRESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> estateTypGbCd 값이 잘못됨 -> see CommonCode [ESTATE_TYP_GB_CD]" );
		}
		
		// 매물 구분코드 검증
		if( GsntalkUtil.isEmpty( estateTypCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> estateTypCd 값 없음" );
		}
		if( "REGIDENTAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "APT", "OFT", "SMH", "TWN", "SHC", "HUS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> estateTypCd 값이 잘못됨 ( in REGIDENTAL ) -> see CommonCode [ESTATE_TYP_GB_CD - REGIDENTAL]" );
			}
		}else if( "COMMERCIAL".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isIn( estateTypCd, "KOG", "STR", "BLD", "LND", "FTR" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> estateTypCd 값이 잘못됨 ( in COMMERCIAL ) -> see CommonCode [ESTATE_TYP_GB_CD - COMMERCIAL]" );
			}
		}else {
			if( !GsntalkUtil.isIn( estateTypCd, "CPS", "SPS", "APS", "OPS" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> estateTypCd 값이 잘못됨 ( in PRESALE ) -> see CommonCode [ESTATE_TYP_GB_CD - PRESALE]" );
			}
		}
		
		if( GsntalkUtil.isEmpty( addr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> addr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( GsntalkUtil.isEmpty( mapDispYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> mapDispYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( mapDispYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> mapDispYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkUtil.isEmpty( tmpAddrYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> tmpAddrYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( tmpAddrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> tmpAddrYn 값이 잘못됨 -> only Y or N value possible" );
		}
		if( GsntalkConstants.YES.equals( tmpAddrYn ) && GsntalkUtil.isEmpty( addrShortNm ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "임시주소로 설정함, but baseItem -> addrShortNm 값 없음" );
		}
		if( lat == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lat 값 없음" );
		}
		if( lng == 0.0d ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lng 값 없음" );
		}
		
		// 매물유형 구분에 따른 미등기여부 값 검증
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( GsntalkUtil.isEmpty( unregistYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> unregistYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( unregistYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> unregistYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			unregistYn = null;
		}
		
		// 토지/임야, 단독공장이 아니면 해당층/전체틍 값 검증
		if( !GsntalkUtil.isIn( estateTypCd, "LND", "FTR" ) ) {
			if( flr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> flr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( allFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> allFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			flr = 0;
			allFlr = 0;
		}
		
		// 단독공장이면 최저층/최고층 값 검증
		if( "FTR".equals( estateTypCd ) ) {
			if( minFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> minFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( maxFlr == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> maxFlr 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			minFlr = 0;
			maxFlr = 0;
		}
		
		// 건물, 토지/임야, 단독공장이면
		if( GsntalkUtil.isIn( estateTypCd, "LND", "FTR" ) ) {
			// 대지면적 검증
			if( lndArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lndArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			// 토지/임야가 아니면 연면적 추가 검증
			if( !"LND".equals( estateTypCd ) ) {
				if( totFlrArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> totFlrArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				// 토지/임야면 연멱적 없음
				totFlrArea = 0.0d;
			}
			
			// 단독공장이면 전용면적 추가 검증
			if( "FTR".equals( estateTypCd ) ) {
				if( prvArea == 0.0d ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}else {
				prvArea = 0.0d;
			}
			
			splyArea = 0.0d;
			
		// 건물, 토지/임야, 단독공장이 아니면
		}else {
			// 공급면적, 전용면적 검증
			if( splyArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "sbaseItem -> plyArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( prvArea == 0.0d ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> prvArea 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			lndArea = 0.0d;
			totFlrArea = 0.0d;
		}
		
		// 상업용 주거용 이면서, 토지임야가 아니면 사용승인일자 필수입력 검증
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) && !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( useCnfrmDate ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> useCnfrmDate 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.is8DateFormat( useCnfrmDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "baseItem -> useCnfrmDate 값 잘못됨" );
			}
		}else {
			useCnfrmDate = null;
		}
		
		// 토지/임야가 아니면 건축물 주용도 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( bldUsageGbCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> bldUsageGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_USAGE_GB_CD", bldUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> bldUsageGbCd 값이 잘못됨 -> see [4. Common Codes - BLD_USAGE_GB_CD]" );
			}
		}else {
			bldUsageGbCd = null;
		}
		
		// 단독공장인 경우 건축물 추천용도 검증 ( 필수가 아니므로 값이 있으면 유효성만 검증 )
		if( "FTR".equals( estateTypCd ) && !GsntalkUtil.isEmpty( suggstnBldUsageGbCd ) ) {
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_USAGE_GB_CD", suggstnBldUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> suggstnBldUsageGbCd 값이 잘못됨 -> see [4. Common Codes - BLD_USAGE_GB_CD]" );
			}
		}
		
		// 토지/임야면 토지/임야 현재용도 검증
		if( "LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( lndCrntUsageGbCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> lndCrntUsageGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "LND_CRNT_USAGE_GB_CD", lndCrntUsageGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> lndCrntUsageGbCd 값이 잘못됨 -> see [4. Common Codes - LND_CRNT_USAGE_GB_CD]" );
			}
		}else {
			lndCrntUsageGbCd = null;
		}
		
		// 토지임야가 아니면 관리비 항목 유효성 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmptyArray( mntnceCostTypItems ) ) {
				String mntnceCostTypCd = "";
				for( int i = 0; i < mntnceCostTypItems.size(); i ++ ) {
					mntnceCostTypCd = GsntalkUtil.getString( mntnceCostTypItems.get( i ) );
					
					validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "MNTNCE_COST_TYP_CD", mntnceCostTypCd, null );
					if( validationCnt < 1 ) {
						// 잘못된 파라메터 값
						throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> mntnceCostTypItems - 잘못된 관리비유형코드 값이 존재함 (" + mntnceCostTypCd + ")  -> see [4. Common Codes - MNTNCE_COST_TYP_CD]" );
					}
				}
			}
			
		}else {
			mntnceCostTypItems = new JSONArray();
		}
		
		// 토지/임야가 아니면 입주가능일 유형, 입주가능일자 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( psblMovDayTypCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> psblMovDayTypCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			
			if( !GsntalkUtil.isIn( psblMovDayTypCd, "IMMDTLY", "DISCSN", "INPUT" ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> psblMovDayTypCd 값이 잘못됨 -> see [4. Common Codes - PSBL_MOV_DAY_TYP_CD]" );
			}
			
			// 입주가능일 유형이 직접입력이면 입주가능일자 추가 검증
			if( "INPUT".equals( psblMovDayTypCd ) ) {
				if( GsntalkUtil.isEmpty( psblMovDate ) ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "입주가능일 유형이 직접입력, but baseItem -> psblMovDate 값 없음." );
				}
				if( !GsntalkUtil.is8DateFormat( psblMovDate, true ) ) {
					// 잘못된 날짜형식
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "baseItem -> psblMovDate 값 잘못됨" );
				}
			}else {
				psblMovDate = null;
			}
		
		}else {
			estateTypCd = null;
			psblMovDate = null;
		}
		
		// 융자금 구분코드 검증
		if( GsntalkUtil.isEmpty( loanGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "baseItem -> loanGbCd 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		if( !GsntalkUtil.isIn( loanGbCd, "NOT", "U30", "O30" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> loanGbCd 값이 잘못됨 -> see [4. Common Codes - LOAN_GB_CD]" );
		}
		// 융자금이 있는경우 융자금액 검증
		if( !"NOT".equals( loanGbCd ) && loanAmt == 0L ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "융자금이 존재함, but baseItem -> loanAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
		}
		
		// 토지/임야가 아닌경우 주차가능여부 검증
		if( !"LND".equals( estateTypCd ) && !GsntalkUtil.isEmpty( parkingPsblYn ) ) {
			if( !GsntalkUtil.isIn( parkingPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "baseItem -> parkingPsblYn 값이 잘못됨 -> only Y or N value possible when not empty." );
			}
		}else {
			parkingPsblYn = null;
		}
		
		/** 거래정보 추출 ( tranItem ) 및 검증 */
		String tranTypGbCd					= GsntalkUtil.getString( tranItem.get( "tranTypGbCd" ) );
		long dealAmt						= GsntalkUtil.getLong( tranItem.get( "dealAmt" ) );
		String dealAmtDiscsnPsblYn			= GsntalkUtil.getString( tranItem.get( "dealAmtDiscsnPsblYn" ) );
		long dpstAmt						= GsntalkUtil.getLong( tranItem.get( "dpstAmt" ) );
		int montRentAmt						= GsntalkUtil.getInteger( tranItem.get( "montRentAmt" ) );
		String existngLeaseExstsYn			= GsntalkUtil.getString( tranItem.get( "existngLeaseExstsYn" ) );
		long crntDpstAmt					= GsntalkUtil.getLong( tranItem.get( "crntDpstAmt" ) );
		int crntMontRentAmt					= GsntalkUtil.getInteger( tranItem.get( "crntMontRentAmt" ) );
		String keyMonExstsYn				= GsntalkUtil.getString( tranItem.get( "keyMonExstsYn" ) );
		long keyMonAmt						= GsntalkUtil.getLong( tranItem.get( "keyMonAmt" ) );
		long prmmAmt						= GsntalkUtil.getLong( tranItem.get( "prmmAmt" ) );
		String cmpltExpctDate				= GsntalkUtil.parseNumberString( GsntalkUtil.getString( tranItem.get( "cmpltExpctDate" ) ) );
		
		// 거래유형 구분코드 검증
		if( GsntalkUtil.isEmpty( tranTypGbCd ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> tranTypGbCd 값 없음" );
		}
		if( !GsntalkUtil.isIn( tranTypGbCd, "TRADE", "LEASE", "LEASE_ST", "CHARTER", "MONTLY", "RESALE" ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranItem -> tranTypGbCd 값이 잘못됨 -> see CommonCode [TRAN_TYP_GB_CD]" );
		}
		
		// 거래유형에 따른 거래금액 검증
		if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
			if( dealAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> dealAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dealAmt = 0L;
		}
		
		// 거래금액 협의가능 여부 검증
		if( GsntalkUtil.isEmpty( dealAmtDiscsnPsblYn ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> dealAmtDiscsnPsblYn 값 없음" );
		}
		if( !GsntalkUtil.isIn( dealAmtDiscsnPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
			// 잘못된 파라메터 값
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranItem -> dealAmtDiscsnPsblYn 값이 잘못됨 -> only Y or N value possible" );
		}
		
		// 거래유형에 따른 보증금액 밍 월 임대료 검증
		if( GsntalkUtil.isIn( tranTypGbCd, "LEASE", "LEASE_ST", "MONTLY" ) ) {
			if( dpstAmt == 0L ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> dpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( montRentAmt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> montRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			dpstAmt = 0L;
			montRentAmt = 0;
		}
		
		// 매물유형 구분에 따른 기존임대차 존재여부 검증
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			if( GsntalkUtil.isEmpty( existngLeaseExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> existngLeaseExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( existngLeaseExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranItem -> existngLeaseExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			existngLeaseExstsYn = null;
		}
		
		// 매물유형 구분에 따른 현 보증금, 현 월임대료 검증
		if( GsntalkUtil.isIn( estateTypGbCd, "REGIDENTAL", "COMMERCIAL" ) ) {
			// 기존 임대차 존재하면 현보증금/현월임대료 값 검증
			if( GsntalkConstants.YES.equals( existngLeaseExstsYn ) ) {
				if( crntDpstAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but tranItem -> crntDpstAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
				if( crntMontRentAmt == 0 ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "기존 임대차 존재, but tranItem -> crntMontRentAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			crntDpstAmt = 0L;
			crntMontRentAmt = 0;
		}
		
		// 매물 구분에 따른 권리금 존재여부, 권리금액 검증
		if( "STR".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( keyMonExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "tranItem -> keyMonExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( keyMonExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "tranItem -> keyMonExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
			
			if( GsntalkConstants.YES.equals( keyMonExstsYn ) ) {
				if( keyMonAmt == 0L ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "권리금 존재, but tranItem -> keyMonAmt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				}
			}
		}else {
			keyMonExstsYn = null;
			keyMonAmt = 0L;
		}
		
		// 매물유형 구분에 따른 준공 예정일자 검증
		if( "PRESALE".equals( estateTypGbCd ) ) {
			if( !GsntalkUtil.isEmpty( cmpltExpctDate ) && !GsntalkUtil.is8DateFormat( cmpltExpctDate, true ) ) {
				// 잘못된 날짜형식
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_DATE_FORMAT, "tranItem -> cmpltExpctDate 값 잘못됨" );
			}
		}else {
			prmmAmt = 0L;
			cmpltExpctDate = null;
		}
		
		/** 추가정보 추출 ( additionItem ) 및 검증 */
		int roomCnt							= GsntalkUtil.getInteger( additionItem.get( "roomCnt" ) );
		int bathRoomCnt						= GsntalkUtil.getInteger( additionItem.get( "bathRoomCnt" ) );
		String crntSectrGbCd				= GsntalkUtil.getString( additionItem.get( "crntSectrGbCd" ) );
		String suggstnSectrGbCd				= GsntalkUtil.getString( additionItem.get( "suggstnSectrGbCd" ) );
		String bldDirctnGbCd				= GsntalkUtil.getString( additionItem.get( "bldDirctnGbCd" ) );
		String heatKindGbCd					= GsntalkUtil.getString( additionItem.get( "heatKindGbCd" ) );
		double wghtPerPy					= GsntalkUtil.getDouble( additionItem.get( "wghtPerPy" ) );
		String elvFcltExstsYn				= GsntalkUtil.getString( additionItem.get( "elvFcltExstsYn" ) );
		String frghtElvExstsYn				= GsntalkUtil.getString( additionItem.get( "frghtElvExstsYn" ) );
		String intrrYn						= GsntalkUtil.getString( additionItem.get( "intrrYn" ) );
		String dockExstsYn					= GsntalkUtil.getString( additionItem.get( "dockExstsYn" ) );
		String hoistExstsYn					= GsntalkUtil.getString( additionItem.get( "hoistExstsYn" ) );
		String flrHghtTypGbCd				= GsntalkUtil.getString( additionItem.get( "flrHghtTypGbCd" ) );
		String elctrPwrTypGbCd				= GsntalkUtil.getString( additionItem.get( "elctrPwrTypGbCd" ) );
		String intnlStrctrTypCd				= GsntalkUtil.getString( additionItem.get( "intnlStrctrTypCd" ) );
		String bultInYn						= GsntalkUtil.getString( additionItem.get( "bultInYn" ) );
		String movInReprtPsblYn				= GsntalkUtil.getString( additionItem.get( "movInReprtPsblYn" ) );
		String cityPlanYn					= GsntalkUtil.getString( additionItem.get( "cityPlanYn" ) );
		String bldCnfrmIssueYn				= GsntalkUtil.getString( additionItem.get( "bldCnfrmIssueYn" ) );
		String lndDealCnfrmApplYn			= GsntalkUtil.getString( additionItem.get( "lndDealCnfrmApplYn" ) );
		String entrnceRoadExstsYn			= GsntalkUtil.getString( additionItem.get( "entrnceRoadExstsYn" ) );
		String optionExstsYn				= GsntalkUtil.getString( additionItem.get( "optionExstsYn" ) );
		JSONArray optionTypItems			= GsntalkUtil.getJSONArray( additionItem, "optionTypItems" );
		
		// 주거용 이거나 분양권 중 아파트/오피스텔 분양권인 경우 방수, 욕실수 검증
		if( "REGIDENTAL".equals( estateTypGbCd ) || GsntalkUtil.isIn( estateTypGbCd, "APS", "OPS" ) ) {
			if( roomCnt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> roomCnt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( bathRoomCnt == 0 ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> bathRoomCnt 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			roomCnt = 0;
			bathRoomCnt = 0;
		}
		
		// 상가면서 현업종, 추천업종 구분코드가 있는경우 코드 유효성 검증
		if( "STR".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( crntSectrGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SECTR_GB_CD", crntSectrGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> crntSectrGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( suggstnSectrGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SECTR_GB_CD", suggstnSectrGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> suggstnSectrGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
		}else {
			crntSectrGbCd = null;
			suggstnSectrGbCd = null;
		}
		
		// 토지/임야 가 아니면서 건물방향/난방종류구분코드가 있으면 코드 유효성 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( bldDirctnGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "BLD_DIRCTN_GB_CD", bldDirctnGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> bldDirctnGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( heatKindGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "HEAT_KIND_GB_CD", heatKindGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> heatKindGbCd 값이 잘못됨  -> see [4. Common Codes - SECTR_GB_CD]" );
				}
			}
		}else {
			bldDirctnGbCd = null;
			heatKindGbCd = null;
		}
		
		// 지산/사무실/창고, 지산분양권이 아니면 톤당하중 값 삭제
		if( !GsntalkUtil.isIn( estateTypCd, "KOG", "CPS" ) ) {
			wghtPerPy = 0.0d;
		}
		
		// 토지/임야 가 아니면 승강시설 존재여부 값 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( elvFcltExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> elvFcltExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( elvFcltExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> elvFcltExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			elvFcltExstsYn = null;
		}
		
		// 지산/사무실/창고, 단독공장, 지산분양권이면 화물용승강시설여부 값 검증
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "FTR", "CPS" ) ) {
			if( GsntalkUtil.isEmpty( frghtElvExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> frghtElvExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( frghtElvExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> frghtElvExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}
		
		// 지산/사무실/창고, 지산분양권이면 인테리어여부 값 검증
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "CPS" ) ) {
			if( GsntalkUtil.isEmpty( intrrYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> intrrYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
			if( !GsntalkUtil.isIn( intrrYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> intrrYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}
		
		// 단독공장이면서 도크 존재여부, 호이스트 존재여부 값이 있으면 유효성 검증
		if( "FTR".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( dockExstsYn ) && !GsntalkUtil.isIn( dockExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> dockExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
			if( !GsntalkUtil.isEmpty( hoistExstsYn ) && !GsntalkUtil.isIn( hoistExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> hoistExstsYn 값이 잘못됨 -> only Y or N value possible" );
			}
		}else {
			dockExstsYn = null;
			hoistExstsYn = null;
		}
		
		// 지산/사무실/창고, 단독공장, 지산분양권이면서 층고유형구분코드/사용전력유형코드 값이 존재하면 코드 유효성 검증
		if( GsntalkUtil.isIn( estateTypCd, "KOG", "FTR", "CPS" ) ) {
			if( !GsntalkUtil.isEmpty( flrHghtTypGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "FLR_HGHT_TYP_GB_CD", flrHghtTypGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> flrHghtTypGbCd 값이 잘못됨  -> see [4. Common Codes - FLR_HGHT_TYP_GB_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( elctrPwrTypGbCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "ELCTR_PWR_TYP_GB_CD", elctrPwrTypGbCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> elctrPwrTypGbCd 값이 잘못됨  -> see [4. Common Codes - ELCTR_PWR_TYP_GB_CD]" );
				}
			}
		}else {
			flrHghtTypGbCd = null;
			elctrPwrTypGbCd = null;
		}
		
		// 오피스텔, 오피스텔 분양권이면서 내부구조유형코드/빌트인여부/전입신고 가능여부 값이 있으면 코드 및 값 검증
		if( GsntalkUtil.isIn( estateTypCd, "OFT", "OPS" ) ) {
			if( !GsntalkUtil.isEmpty( intnlStrctrTypCd ) ) {
				validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "INTNL_STRCTR_TYP_CD", intnlStrctrTypCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> intnlStrctrTypCd 값이 잘못됨  -> see [4. Common Codes - INTNL_STRCTR_TYP_CD]" );
				}
			}
			if( !GsntalkUtil.isEmpty( bultInYn ) ) {
				if( !GsntalkUtil.isIn( bultInYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> bultInYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( movInReprtPsblYn ) ) {
				if( !GsntalkUtil.isIn( movInReprtPsblYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> movInReprtPsblYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
		}else {
			intnlStrctrTypCd = null;
			bultInYn = null;
			movInReprtPsblYn = null;
		}
		
		// 토지/임야 면서 도시계획여부, 건축허가발급여부, 토지거래허가구역해당여부, 진입도로 존재여부 값이 있으면 유효성 검증
		if( "LND".equals( estateTypCd ) ) {
			if( !GsntalkUtil.isEmpty( cityPlanYn ) ) {
				if( !GsntalkUtil.isIn( cityPlanYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> cityPlanYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( bldCnfrmIssueYn ) ) {
				if( !GsntalkUtil.isIn( bldCnfrmIssueYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> bldCnfrmIssueYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( lndDealCnfrmApplYn ) ) {
				if( !GsntalkUtil.isIn( lndDealCnfrmApplYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> lndDealCnfrmApplYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
			if( !GsntalkUtil.isEmpty( entrnceRoadExstsYn ) ) {
				if( !GsntalkUtil.isIn( entrnceRoadExstsYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> entrnceRoadExstsYn 값이 잘못됨 -> only Y or N value possible" );
				}
			}
		}else {
			cityPlanYn = null;
			bldCnfrmIssueYn = null;
			lndDealCnfrmApplYn = null;
			entrnceRoadExstsYn = null;
		}
		
		// 토지/임야 가 아니면 옵션 존재여부 값 유효성 검증
		if( !"LND".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( optionExstsYn ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "additionItem -> optionExstsYn 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
			}
		}else {
			optionExstsYn = null;
			optionTypItems = new JSONArray();
		}
		
		// 옵션 존재여부가 존재이면서 선택옵션 항목이 있으면 옵션항목 코드 유효성 검증
		if( GsntalkConstants.YES.equals( optionExstsYn ) ) {
			if( GsntalkUtil.isEmptyArray( optionTypItems ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "옵션이 존재함, but additionItem -> optionTypItems 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
				
			}else {
				JSONObject optTypGbItem = null;
				JSONArray optTypItems = null;
				String optionItemTypGbCd = null;
				String optionItemTypCd = null;
				
				for( int i = 0; i < optionTypItems.size(); i ++ ) {
					optTypGbItem = (JSONObject)optionTypItems.get( i );
					
					optionItemTypGbCd = GsntalkUtil.getString( optTypGbItem.get( "optionItemTypGbCd" ) );
					
					validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "OPTION_ITEM_TYP_GB_CD", optionItemTypGbCd, null );
					if( validationCnt < 1 ) {
						// 잘못된 파라메터 값
						throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> optionItemTypGbCd 값이 잘못됨 (" + optionItemTypGbCd + ")  -> see [4. Common Codes - OPTION_ITEM_TYP_GB_CD]" );
					}
					
					optTypItems = GsntalkUtil.getJSONArray( optTypGbItem, "optionTypCdItems" );
					if( GsntalkUtil.isEmptyArray( optTypItems ) ) {
						// 필수 요청파라메터 누락
						throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "옵션항목유형 코드(" + optionItemTypGbCd + ")에 해당하는 additionItem -> optionTypCdItems 값 없음 -> see [5. 매물유형별 관리항목표 in API Document]" );
					}
					
					for( int j = 0; j < optTypItems.size(); j ++ ) {
						optionItemTypCd = GsntalkUtil.getString( optTypItems.get( j ) );
						
						validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "OPTION_ITEM_TYP_CD", optionItemTypCd, optionItemTypGbCd );
						if( validationCnt < 1 ) {
							// 잘못된 파라메터 값
							throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "additionItem -> " + optionItemTypGbCd + " -> optionItemTypCd 값이 잘못됨 (" + optionItemTypCd + ")  -> see [4. Common Codes - OPTION_ITEM_TYP_CD > parent code is " + optionItemTypGbCd + "]" );
						}
					}
				}
			}
		}else {
			optionTypItems = new JSONArray();
		}
		
		/** 상세설명 정보 추출 ( photoItem ) 및 검증 */
		String smplSmrDscr					= GsntalkUtil.getString( photoItem.get( "smplSmrDscr" ) );
		String dtlExplntnDscr				= GsntalkUtil.getString( photoItem.get( "dtlExplntnDscr" ) );
		String matterPortLinkUrl			= GsntalkUtil.getString( photoItem.get( "matterPortLinkUrl" ) );
		
		if( GsntalkUtil.isEmpty( smplSmrDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "photoItem -> smplSmrDscr" );
		}
		if( GsntalkUtil.isEmpty( dtlExplntnDscr ) ) {
			// 필수 요청파라메터 누락
			throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "photoItem -> dtlExplntnDscr" );
		}
		
		// 단축주소가 없는경우 geocode 에서 조회해서 적용
		if( GsntalkUtil.isEmpty( addrShortNm ) ) {
			JSONObject geocodeItem = gsntalkIFUtil.getGeocode( addr, false );
			if( geocodeItem != null ) {
				addrShortNm = GsntalkUtil.getString( geocodeItem.get( "addrShortNm" ) );
			}
		}
		
		// 매물사진 목록조회 ( 대표사진 포함 )
		List<PropertyPhotoVO> prptPhotoList = propertyDAO.getPrptPhotoList( prptSeqno );
		if( GsntalkUtil.isEmptyList( prptPhotoList ) ) {
			prptPhotoList = new ArrayList<PropertyPhotoVO>();
		}
		if( GsntalkUtil.isEmptyList( addFiles ) ) {
			addFiles = new ArrayList<MultipartFile>();
		}
		if( prptPhotoList.size() - delFileUrls.size() + addFiles.size() > 8 ) {
			// 첨부가능한 최대 파일수량 초과
			throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE );
		}
		
		String orgFileNm = "";
		String uploadFileFormat = "";
		
		// 첨부한 대표사진이 있으면
		if( repFile != null && repFile.getSize() != 0L ) {
			orgFileNm = repFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		for( MultipartFile file : addFiles ) {
			orgFileNm = file.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, orgFileNm );
			}
		}
		
		// 관리자 확인여부 조회 ( 처리전에 미리 조회 )
		String adminCnfrmYn = propertyDAO.getAdminCnfrmYn( prptSeqno );
		
		// 매물 수정 ( 1 ~ 4 단계 정보 )
		String deniedExprDateYn = GsntalkConstants.YES.equals( propertyVO.getAdminCnfrmYn() ) ? GsntalkConstants.NO : GsntalkConstants.YES;
		propertyDAO.updateProperty( prptSeqno, estateTypGbCd, estateTypCd, tranTypGbCd, dealAmt, dealAmtDiscsnPsblYn, dpstAmt, montRentAmt, existngLeaseExstsYn, crntDpstAmt, crntMontRentAmt,
				keyMonExstsYn, keyMonAmt, prmmAmt, cmpltExpctDate, addr, dtlAddr, addrShortNm, lat, lng, mapDispYn, tmpAddrYn, unregistYn, flr, allFlr, minFlr, maxFlr,
				splyArea, prvArea, lndArea, totFlrArea, useCnfrmDate, bldUsageGbCd, suggstnBldUsageGbCd, lndCrntUsageGbCd, psblMovDayTypCd, psblMovDate, monMntnceCost, loanGbCd, loanAmt, parkingPsblYn, parkingCost,
				roomCnt, bathRoomCnt, crntSectrGbCd, suggstnSectrGbCd, bldDirctnGbCd, heatKindGbCd, wghtPerPy, elvFcltExstsYn, frghtElvExstsYn, intrrYn, dockExstsYn, hoistExstsYn, flrHghtTypGbCd,
				elctrPwrTypGbCd, intnlStrctrTypCd, bultInYn, movInReprtPsblYn, cityPlanYn, bldCnfrmIssueYn, lndDealCnfrmApplYn, entrnceRoadExstsYn, optionExstsYn, deniedExprDateYn, GsntalkConstants.YES );
		
		// 관리비 유형 삭제 후 재등록
		propertyDAO.removeAllPropertyMaintenanceTyps( prptSeqno );
		for( int i = 0; i < mntnceCostTypItems.size(); i ++ ) {
			propertyDAO.registerPropertyMaintenanceTyps( prptSeqno, GsntalkUtil.getString( mntnceCostTypItems.get( i ) ) );
		}
		
		// 옵션유형 삭제 후 재등록
		propertyDAO.removeAllPropertyOptionTyps( prptSeqno );
		JSONObject optionTypItem = null;
		String optionItemTypGbCd = null;
		JSONArray optionTypCdItems = null;
		for( int i = 0; i < optionTypItems.size(); i ++ ) {
			optionTypItem = (JSONObject)optionTypItems.get( i );
			
			optionItemTypGbCd = GsntalkUtil.getString( optionTypItem.get( "optionItemTypGbCd" ) );
			optionTypCdItems = GsntalkUtil.getJSONArray( optionTypItem, "optionTypCdItems" );
			for( int j = 0; j < optionTypCdItems.size(); j ++ ) {
				propertyDAO.registerPropertyOptionTyps( prptSeqno, optionItemTypGbCd, GsntalkUtil.getString( optionTypCdItems.get( j ) ) );
			}
		}
		
		// 관리자 - 매물 사진정보 업데이트
		propertyDAO.updatePropertyPhotoInfoFromAdmin( prptSeqno, smplSmrDscr, dtlExplntnDscr, matterPortLinkUrl );
		
		JSONObject uploadItem = null;
		String uploadFileNm = "";
		String saveFileNm = "";
		String fileUrl = "";
		
		// 첨부한 대표사진이 있으면 갱신
		if( repFile != null && repFile.getSize() != 0L ) {
			// 대표 사진 S3 업로드 및 등록
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( 0L, prptSeqno, repFile );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			
			// 매물 대표사진 삭제처리
			propertyDAO.removePropertyRepPhoto( prptSeqno );
			
			// 매물 대표사진 재등록
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.YES );
		}
		
		// 삭제할 사진 삭제처리
		String delFileUrl = "";
		for( int i = 0; i < delFileUrls.size(); i ++ ) {
			delFileUrl = GsntalkUtil.getString( delFileUrls.get( i ) );
			
			// 매물사진 삭제처리
			propertyDAO.deletePropertyPhoto( prptSeqno, delFileUrl );
		}
		
		// 추가사진 S3 업로드 및 등록
		for( MultipartFile file :  addFiles ) {
			uploadItem = gsntalkS3Util.uploadPropertyImageFile( 0L, prptSeqno, file );
			
			uploadFileNm = GsntalkUtil.getString( uploadItem.get( "orgFileNm" ) );
			saveFileNm = GsntalkUtil.getString( uploadItem.get( "saveFileNm" ) );
			fileUrl = GsntalkUtil.getString( uploadItem.get( "fileUrl" ) );
			
			// 매물 사진 등록
			propertyDAO.registerPropertyPhoto( prptSeqno, uploadFileNm, saveFileNm, fileUrl, GsntalkConstants.NO );
		}
		
		// 수정 전에 관리자가 승인하지 않은 건이면 ( 최초 승인/공개 )
		if( GsntalkConstants.NO.equals( adminCnfrmYn ) ) {
			// 매물등록 알림 등록 ( 매물알림 )
			String notiTtl = "매물 등록 완료";
			String notiDscr = "<strong>" + GsntalkUtil.getSubAddr( addr, 1 ) + " / " + gsntalkDAO.getCommonCodeNm( "ESTATE_TYP_CD", estateTypGbCd, estateTypCd ) + "</strong> 등록이 완료 되었어요, 지금 확인해보세요.";
			gsntalkDAO.registerNotification( propertyVO.getMemSeqno(), "PRPT", null, notiTtl, notiDscr, prptSeqno, 0L );
		}
	}
	
	/**
	 * Admin - 일반매물 거래상태 변경
	 * 
	 * @param prptSeqno
	 * @param dealStatGbCd
	 * @throws Exception
	 */
	public void updateMembersPropertyDealStateItem( long prptSeqno, String dealStatGbCd )throws Exception {
		PropertyVO propertyVO = propertyDAO.isAvailMembersProperty (prptSeqno );
		if( propertyVO == null ) {
			// 매물정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY, "존재하지 않거나 일반회원이 등록한 매물이 아님 prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.NO.equals( propertyVO.getAdminCnfrmYn() ) ) {
			// 아직 승인되지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_CONFIRMED_PROPERTY, "거래상태 변경불가 (아직 승인되지 않은 매물) prptSeqno -> " + prptSeqno );
		}
		if( "FIN".equals( propertyVO.getDealStatGbCd()) ) {
			// 이미 거래가 완료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_FIN_PROPERTY, "거래상태 변경불가 (이미 거래가 완료된 매물) prptSeqno -> " + prptSeqno );
		}
		if( GsntalkConstants.YES.equals( propertyVO.getExprYn() ) ) {
			// 만료된 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_EXPIRED_PROPERTY, "거래상태 변경불가 (이미 만료된 매물) prptSeqno -> " + prptSeqno );
		}
		
		// 매물 상태변경
		propertyDAO.updatePropertyDealStatGbCd( prptSeqno, dealStatGbCd );
	}
	
	/**
	 * Admin - 중개사 매물 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param regiEstTypCdItems
	 * @param commEstTypCdItems
	 * @param preEstTypCdItems
	 * @param prptStatGbCd
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getEstBrkPrptItems( String srchVal, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, JSONArray regiEstTypCdItems, JSONArray commEstTypCdItems, JSONArray preEstTypCdItems, String prptStatGbCd, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<PropertyVO> propertyList = propertyDAO.getEstBrkPrptItems( srchVal, dealStatGbCdItems, tranTypGbCdItems, regiEstTypCdItems, commEstTypCdItems, preEstTypCdItems, prptStatGbCd, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( propertyList ) ) {
			propertyList = new ArrayList<PropertyVO>();
		}else {
			totList = propertyList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String tranTypGbCd = null;
		String costDscr = null;
		
		for( PropertyVO vo : propertyList ) {
			item = new JSONObject();
			
			tranTypGbCd = vo.getTranTypGbCd();
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			item.put( "prptSeqno", vo.getPrptSeqno() );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "dealStatGbNm", vo.getDealStatGbNm() );
			item.put( "prptStatGbNm", vo.getPrptStatGbNm() );
			item.put( "estBrkMemOfcSeqno", vo.getEstBrkMemOfcSeqno() );
			item.put( "memSeqno", vo.getMemSeqno() );
			item.put( "ofcNm", vo.getOfcNm() );
			item.put( "addr", vo.getAddr() );
			item.put( "dtlAddr", vo.getDtlAddr() );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			item.put( "costDscr", costDscr );
			item.put( "regDt", vo.getRegDt() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 중개사 매물 목록조회 - Excel Download
	 * 
	 * @param srchVal
	 * @param dealStatGbCdItems
	 * @param tranTypGbCdItems
	 * @param regiEstTypCdItems
	 * @param commEstTypCdItems
	 * @param preEstTypCdItems
	 * @param prptStatGbCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public XSSFWorkbook downloadEstBrkPrptItems( String srchVal, JSONArray dealStatGbCdItems, JSONArray tranTypGbCdItems, JSONArray regiEstTypCdItems, JSONArray commEstTypCdItems, JSONArray preEstTypCdItems, String prptStatGbCd )throws Exception {
		List<PropertyVO> propertyList = propertyDAO.getEstBrkPrptDownloadList( srchVal, dealStatGbCdItems, tranTypGbCdItems, regiEstTypCdItems, commEstTypCdItems, preEstTypCdItems, prptStatGbCd );
		
		String[] titles = new String[]	{ "No",							"매물번호",							"거래상태",						"매물상태",						"등록 중개사명",					"매물종류",						"주소",							"거래정보",						"등록일" };
		String[] fields = new String[]	{ "no",							"prptNo",							"dealStatGbNm",					"prptStatGbNm",					"ofcNm",						"estateTypNm",					"addr",							"costDscr",						"regDt" };
		int[] sizes		= new int[] 	{ GsntalkExcelUtil.WIDTH_NO, 	GsntalkExcelUtil.WIDTH_SMALL, 		GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL,	GsntalkExcelUtil.WIDTH_MAX,		GsntalkExcelUtil.WIDTH_NORMAL,	GsntalkExcelUtil.WIDTH_SMALL };
		int[] aligns	= new int[] 	{ GsntalkExcelUtil.CENTER, 		GsntalkExcelUtil.CENTER, 			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.LEFT,			GsntalkExcelUtil.CENTER,		GsntalkExcelUtil.CENTER };
	
		JSONArray items = new JSONArray();
		JSONObject item = null;
		String costDscr = "";
		int no = 1;
		
		for( PropertyVO vo : propertyList ) {
			item = new JSONObject();
			item.put( "no", String.valueOf( no ) );
			item.put( "prptNo", vo.getPrptNo() );
			item.put( "dealStatGbNm", vo.getDealStatGbNm() );
			item.put( "prptStatGbNm", vo.getPrptStatGbNm() );
			item.put( "ofcNm", vo.getOfcNm() );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "addr", vo.getAddr() + " " + vo.getDtlAddr() );
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( vo.getTranTypGbCd(), "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
				// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			item.put( "costDscr", vo.getTranTypGbNm() + " " + costDscr );
			item.put( "regDt", vo.getRegDt() );
			
			items.add( item );
			
			no ++;
		}
		
		return new GsntalkExcelUtil( titles, fields, sizes, aligns ).getXSSFExcelWorkbook( items );
	}
	
	/**
	 * FRT - 매물제안서 요청 1단계 임시저장 ( 일반/중개회원 공통 )
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param estateTypCd
	 * @param wishArea
	 * @param sectrGbCd
	 * @param usePplCnt
	 * @param psblMovDayTypCd
	 * @param psblMovStDate
	 * @param psblMovEdDate
	 * @param suggstRegionItems
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject registerPrptSuggstReqStep1RegItem( String memTypCd, long memSeqno, String estateTypCd, double wishArea, String sectrGbCd, int usePplCnt, String psblMovDayTypCd,
			String psblMovStDate, String psblMovEdDate, JSONArray suggstRegionItems )throws Exception {
	
		// 상가면
		if( "STR".equals( estateTypCd ) ) {
			int validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SECTR_GB_CD", sectrGbCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "sectrGbCd 값이 잘못됨 -> see [4. Common Codes - SECTR_GB_CD]" );
			}
		}
		
		
		// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
		String regTmpKey = GsntalkUtil.createRegistrationTempKey();
		
		
		JSONObject item = new JSONObject();
		item.put( "estateTypCd", estateTypCd );
		item.put( "wishArea", wishArea );
		item.put( "sectrGbCd", sectrGbCd );
		item.put( "usePplCnt", usePplCnt );
		item.put( "psblMovDayTypCd", psblMovDayTypCd );
		item.put( "psblMovStDate", psblMovStDate );
		item.put( "psblMovEdDate", psblMovEdDate );
		item.put( "suggstRegionItems", suggstRegionItems );
		
		// 기존 1단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		String regClasCd = GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memTypCd ) ? GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPSL : GsntalkConstants.REG_CLAS_CD_MY_PRPSL;
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, regClasCd, regTmpKey, 1, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, regClasCd, regTmpKey, 1, item.toJSONString() );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * FRT - 매물제안서 요청 2단계 임시저장 ( 일반/중개회원 공통 )
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param regTmpKey
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param clientNm
	 * @param compNm
	 * @param suggstSectrCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject registerPrptSuggstReqStep2RegItem( String memTypCd, long memSeqno, String regTmpKey, String tranTypGbCd, long dealAmt, long dpstAmt, int montRentAmt,
			String clientNm, String compNm, String suggstSectrCd )throws Exception {
		
		String regClasCd = GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memTypCd ) ? GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPSL : GsntalkConstants.REG_CLAS_CD_MY_PRPSL;
		
		// 일반/중개회원 매물제안서 1단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 1단계 매물구분코드 추출
		JSONObject firstItem = (JSONObject)this.jsonParser.parse( tmpJsonData );
		String estateTypCd = GsntalkUtil.getString( firstItem.get( "estateTypCd" ) );
		
		// 상가면
		if( "STR".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( clientNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "clientNm is empty ( 1단계 매물 구분이 상가임 )" );
			}
			compNm = "";
			suggstSectrCd = "";
		}else {
			if( GsntalkUtil.isEmpty( compNm ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "compNm is empty ( 1단계 매물 구분이 상가가 아님 )" );
			}
			if( !GsntalkUtil.isEmpty( suggstSectrCd ) ) {
				int validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "SUGGST_SECTR_CD", suggstSectrCd, null );
				if( validationCnt < 1 ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "suggstSectrCd 값이 잘못됨 -> see [4. Common Codes - SUGGST_SECTR_CD]" );
				}
			}
			clientNm = "";
		}
		
		JSONObject item = new JSONObject();
		item.put( "tranTypGbCd", tranTypGbCd );
		item.put( "dealAmt", dealAmt );
		item.put( "dpstAmt", dpstAmt );
		item.put( "montRentAmt", montRentAmt );
		item.put( "clientNm", GsntalkXSSUtil.encodeXss( clientNm ) );
		item.put( "compNm", GsntalkXSSUtil.encodeXss( compNm ) );
		item.put( "suggstSectrCd", suggstSectrCd );
		
		// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, regClasCd, regTmpKey, 2, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, regClasCd, regTmpKey, 2, item.toJSONString() );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * FRT - 매물제안서 요청 3단계 최종 등록 ( 일반/중개회원 공통 )
	 * 
	 * @param memTypCd
	 * @param memSeqno
	 * @param regTmpKey
	 * @param wishFlrTypCd
	 * @param intrrYn
	 * @param reqDscr
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void registerPrptSuggstReqFinalStepRegItem( String memTypCd, long memSeqno, String regTmpKey, String wishFlrTypCd, String intrrYn, String reqDscr )throws Exception {
		String regClasCd = GsntalkConstants.MEM_TYP_CD_ESTATE_AGENT.equals( memTypCd ) ? GsntalkConstants.REG_CLAS_CD_ESTBRK_PRPSL : GsntalkConstants.REG_CLAS_CD_MY_PRPSL;
		
		// 일반/중개회원 매물제안서 1단계 임시저장 JSON 조회
		String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 1 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 1단계 임시등록 데이터를 찾을 후 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1 );
		}
		
		// 1단계 정보 추출
		JSONObject firstItem = (JSONObject)this.jsonParser.parse( tmpJsonData );
		String estateTypCd				= GsntalkUtil.getString( firstItem.get( "estateTypCd" ) );
		double wishArea					= GsntalkUtil.getDouble( firstItem.get( "wishArea" ) );
		String sectrGbCd				= GsntalkUtil.getString( firstItem.get( "sectrGbCd" ) );
		int usePplCnt					= GsntalkUtil.getInteger( firstItem.get( "usePplCnt" ) );
		String psblMovDayTypCd			= GsntalkUtil.getString( firstItem.get( "psblMovDayTypCd" ) );
		String psblMovStDate			= GsntalkUtil.getString( firstItem.get( "psblMovStDate" ) );
		String psblMovEdDate			= GsntalkUtil.getString( firstItem.get( "psblMovEdDate" ) );
		JSONArray suggstRegionItems		= GsntalkUtil.getJSONArray( firstItem, "suggstRegionItems" );
		
		// 상가면
		if( "STR".equals( estateTypCd ) ) {
			if( GsntalkUtil.isEmpty( wishFlrTypCd ) ) {
				// 필수 요청파라메터 누락
				throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "wishFlrTypCd is empty ( 1단계 매물 구분이 상가임 )" );
			}
			
			int validationCnt = gsntalkDAO.isInCommonCdUndeletedItems( "WISH_FLR_TYP_CD", wishFlrTypCd, null );
			if( validationCnt < 1 ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "wishFlrTypCd 값이 잘못됨 -> see [4. Common Codes - WISH_FLR_TYP_CD]" );
			}
		}else {
			wishFlrTypCd = "";
		}
		
		// 일반/중개회원 매물제안서 1단계 임시저장 JSON 조회
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 2 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			// 이전 단계 임시등록 데이터를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP );
		}
		
		// 2단계 정보 추출
		JSONObject secondItem = (JSONObject)this.jsonParser.parse( tmpJsonData );
		String tranTypGbCd				= GsntalkUtil.getString( secondItem.get( "tranTypGbCd" ) );
		long dealAmt					= GsntalkUtil.getLong( secondItem.get( "dealAmt" ) );
		long dpstAmt					= GsntalkUtil.getLong( secondItem.get( "dpstAmt" ) );
		int montRentAmt					= GsntalkUtil.getInteger( secondItem.get( "montRentAmt" ) );
		String clientNm					= GsntalkUtil.getString( secondItem.get( "clientNm" ) );
		String compNm					= GsntalkUtil.getString( secondItem.get( "compNm" ) );
		String suggstSectrCd			= GsntalkUtil.getString( secondItem.get( "suggstSectrCd" ) );
		
		// 3단걔 임시저장 등록
		reqDscr = GsntalkXSSUtil.encodeXss( reqDscr );
		JSONObject item = new JSONObject();
		item.put( "wishFlrTypCd", wishFlrTypCd );
		item.put( "intrrYn", intrrYn );
		item.put( "reqDscr", reqDscr );
		
		// 기존 3단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
		tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, regClasCd, regTmpKey, 3 );
		if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
			gsntalkDAO.registrationTempDataStep( memSeqno, regClasCd, regTmpKey, 3, item.toJSONString(), 0L, 0L, 0L, 0L, 0L );
		}else {
			gsntalkDAO.updateTempDataStep( memSeqno, regClasCd, regTmpKey, 3, item.toJSONString() );
		}
		
		
		// 매물제안요청 등록
		long prptSuggstReqSeqno = propertyDAO.registerPropertySuggestRequest( memSeqno, estateTypCd, tranTypGbCd, wishArea, sectrGbCd, usePplCnt, psblMovDayTypCd, psblMovStDate, psblMovEdDate,
				dealAmt, dpstAmt, montRentAmt, clientNm, compNm, suggstSectrCd, wishFlrTypCd, intrrYn, reqDscr );
		
		// 매물제안요청지역 등록
		String regionNm = null;
		for( int i = 0; i < suggstRegionItems.size(); i ++ ) {
			regionNm = GsntalkUtil.getString( suggstRegionItems.get( i ) );
			
			propertyDAO.registerPropertySuggestRegion( prptSuggstReqSeqno, regionNm );
		}
		
		// 매물제안 매칭 매물조회 및 제안등록처리
		propertyDAO.registerMatchingPropertySuggest( prptSuggstReqSeqno, suggstRegionItems );
	}
	
	/**
	 * FRT - 내 매물 제안서 목록조회 ( 일반/중개회원 공통 )
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray getMyPrptSuggstItems( long memSeqno )throws Exception {
		List<PropertySuggestRequestVO> prptSuggstList = propertyDAO.getMyPrptSuggstItems( memSeqno );
		if( GsntalkUtil.isEmptyList( prptSuggstList ) ) {
			prptSuggstList = new ArrayList<PropertySuggestRequestVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		JSONArray suggstRegionItems = null;
		long prptSuggstReqSeqno = 0L;
		String estateTypCd = null;
		String tranTypGbCd = null;
		String costDscr = null;
		List<String> suggstRegionNmList = null;
		for( PropertySuggestRequestVO vo : prptSuggstList ) {
			item = new JSONObject();
			
			prptSuggstReqSeqno = vo.getPrptSuggstReqSeqno();
			estateTypCd = vo.getEstateTypCd();
			tranTypGbCd = vo.getTranTypGbCd();
			
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			item.put( "prptSuggstReqSeqno", prptSuggstReqSeqno );
			item.put( "suggstMemTypCd", vo.getSuggstMemTypCd() );
			item.put( "suggstMemSeqno", vo.getSuggstMemSeqno() );
			item.put( "suggstrNm", vo.getSuggstrNm() );
			item.put( "estateTypCd", estateTypCd );
			item.put( "estateTypNm", vo.getEstateTypNm() );
			item.put( "tranTypGbCd", tranTypGbCd );
			item.put( "tranTypGbNm", vo.getTranTypGbNm() );
			item.put( "suggstPrptCnt", vo.getSuggstPrptCnt() );
			item.put( "suggstDt", vo.getSuggstDt() );
			item.put( "usePplCnt", vo.getUsePplCnt() );
			item.put( "wishArea", GsntalkUtil.parsePyungToMeters( vo.getWishArea() ) );
			item.put( "psblMovDayTypCd", vo.getPsblMovDayTypCd() );
			item.put( "psblMovDayTypNm", vo.getPsblMovDayTypNm() );
			item.put( "psblMovStDate", vo.getPsblMovStDate() );
			item.put( "psblMovEdDate", vo.getPsblMovEdDate() );
			item.put( "costDscr", costDscr );
			item.put( "intrrYn", vo.getIntrrYn() );
			item.put( "reqDscr", vo.getReqDscr() );
			
			// 매물제안 요청지역 목록조회
			suggstRegionNmList = propertyDAO.getSuggstRegionNmList( prptSuggstReqSeqno );
			if( GsntalkUtil.isEmptyList( suggstRegionNmList ) ) {
				suggstRegionNmList = new ArrayList<String>();
			}
			suggstRegionItems = new JSONArray();
			for( String regionNm : suggstRegionNmList ) {
				suggstRegionItems.add( regionNm );
			}
			item.put( "suggstRegionItems", suggstRegionItems );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * FRT - 제안받은 매물 목록조회 ( 일반/중개회원 공통 )
	 * 
	 * @param memSeqno
	 * @param prptSuggstReqSeqno
	 * @param suggstMemTypCd
	 * @param suggstMemSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMySuggstRcvPrptItems( long memSeqno, long prptSuggstReqSeqno, String suggstMemTypCd, long suggstMemSeqno )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = propertyDAO.getMyPropertySuggestRequestVO( memSeqno, prptSuggstReqSeqno );
		if( propertySuggestRequestVO == null ) {
			// 유효하지 않은 매물제안
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY_SUGGEST );
		}
		
		// 제안자 정보 조회
		PropertySuggestRequestVO suggesterVO = propertyDAO.getMyPropertyRequestSuggesterInfo( suggstMemTypCd, suggstMemSeqno );
		if( suggesterVO == null ) {
			// 매물 제안자 정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY_REQ_SUGGESTER );
		}
		
		String estateTypCd = propertySuggestRequestVO.getEstateTypCd();
		String tranTypGbCd = propertySuggestRequestVO.getTranTypGbCd();

		/** 거래금액 적용 */
		String costDscr = "";
		// 매매, 전세, 전매면 거래가 적용
		if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
			costDscr = GsntalkUtil.parseAmtToKr( propertySuggestRequestVO.getDealAmt() );
			
			// 이외는 보증금/월 임대료 적용
		}else {
			costDscr = GsntalkUtil.parseAmtToKr( propertySuggestRequestVO.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( propertySuggestRequestVO.getMontRentAmt() );
		}
		
		// 제안 요청 정보
		JSONObject reqItem = new JSONObject();
		reqItem.put( "prptSuggstReqSeqno", propertySuggestRequestVO.getPrptSuggstReqSeqno() );
		reqItem.put( "estateTypCd", estateTypCd );
		reqItem.put( "estateTypNm", propertySuggestRequestVO.getEstateTypNm() );
		reqItem.put( "tranTypGbCd", tranTypGbCd );
		reqItem.put( "tranTypGbNm", propertySuggestRequestVO.getTranTypGbNm() );
		reqItem.put( "usePplCnt", propertySuggestRequestVO.getUsePplCnt() );
		reqItem.put( "wishArea", GsntalkUtil.parsePyungToMeters( propertySuggestRequestVO.getWishArea() ) );
		reqItem.put( "sectrGbNm", propertySuggestRequestVO.getSectrGbNm() );
		reqItem.put( "psblMovDayTypCd", propertySuggestRequestVO.getPsblMovDayTypCd() );
		reqItem.put( "psblMovDayTypNm", propertySuggestRequestVO.getPsblMovDayTypNm() );
		reqItem.put( "psblMovStDate", propertySuggestRequestVO.getPsblMovStDate() );
		reqItem.put( "psblMovEdDate", propertySuggestRequestVO.getPsblMovEdDate() );
		reqItem.put( "costDscr", costDscr );
		reqItem.put( "intrrYn", propertySuggestRequestVO.getIntrrYn() );
		reqItem.put( "reqDscr", propertySuggestRequestVO.getReqDscr() );
		
		// 제안요청지역 목록
		List<String> suggstRegionNmList = propertyDAO.getSuggstRegionNmList( prptSuggstReqSeqno );
		if( GsntalkUtil.isEmptyList( suggstRegionNmList ) ) {
			suggstRegionNmList = new ArrayList<String>();
		}
		JSONArray suggstRegionItems = new JSONArray();
		for( String regionNm : suggstRegionNmList ) {
			suggstRegionItems.add( regionNm );
		}
		reqItem.put( "suggstRegionItems", suggstRegionItems );
		
		// 제안자 정보
		JSONObject suggstrItem = new JSONObject();
		suggstrItem.put( "suggstMemTypCd", suggesterVO.getSuggstMemTypCd() );
		suggstrItem.put( "suggstMemSeqno", suggesterVO.getSuggstMemSeqno() );
		suggstrItem.put( "suggstrNm", suggesterVO.getSuggstrNm() );
		suggstrItem.put( "suggstDt", propertySuggestRequestVO.getSuggstDt() );				/** suggstDt from propertySuggestRequestVO */
		
		// 제안받은 매물 목록조회
		List<PropertyVO> suggestPropertyList = propertyDAO.getSuggestRequestPropertyList( memSeqno, prptSuggstReqSeqno, suggstMemTypCd, suggstMemSeqno );
		if( GsntalkUtil.isEmptyList( suggestPropertyList ) ) {
			suggestPropertyList = new ArrayList<PropertyVO>();
		}
		
		// 제안 매물 목록
		JSONArray prptItems = new JSONArray();
		JSONObject prptItem = null;
		JSONObject areaItem = null;
		JSONArray pyAreaItems = null;
		JSONArray meterAreaItems = null;
		for( PropertyVO vo : suggestPropertyList ) {
			/** 거래금액 적용 */
			costDscr = "";
			// 매매, 전세, 전매면 거래가 적용
			if( GsntalkUtil.isIn( tranTypGbCd, "TRADE", "CHARTER", "RESALE" ) ) {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDealAmt() );
				
			// 이외는 보증금/월 임대료 적용
			}else {
				costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
			}
			
			/** 면적정보 적용 */
			areaItem = new JSONObject();
			pyAreaItems = new JSONArray();
			meterAreaItems = new JSONArray();
			// 건물이면 대지면적/연면적
			if( "BLD".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				
			// 토지/임야면 대지면적
			}else if( "LND".equals( estateTypCd ) ) {
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
			
			// 단독공장이면 전용면적/대지면적/연면적
			}else if( "FTR".equals( estateTypCd ) ) {
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				pyAreaItems.add( "대지 " + vo.getLndArea() );
				pyAreaItems.add( "연 " + vo.getTotFlrArea() );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				meterAreaItems.add( "대지 " + GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				meterAreaItems.add( "연 " + GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
			
			// 이외에는 공급면적/전용면적
			}else {
				pyAreaItems.add( "공급 " + vo.getSplyArea() );
				pyAreaItems.add( "전용 " + vo.getPrvArea() );
				meterAreaItems.add( "공급 " + GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				meterAreaItems.add( "전용 " + GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
			}
			areaItem.put( "pyAreaItems", pyAreaItems );
			areaItem.put( "meterAreaItems", meterAreaItems );
			
			prptItem = new JSONObject();
			
			prptItem.put( "no", vo.getRownum() );
			prptItem.put( "prptSeqno", vo.getPrptSeqno() );
			prptItem.put( "reprImgUrl", vo.getReprImgUrl() );
			prptItem.put( "areaItem", areaItem );
			prptItem.put( "tranTypGbNm", vo.getTranTypGbNm() );
			prptItem.put( "costDscr", costDscr );
			prptItem.put( "addr", vo.getAddr() );
			prptItem.put( "dtlAddr", vo.getDtlAddr() );
			prptItem.put( "addrShortNm", vo.getAddrShortNm() );
			prptItem.put( "lat", vo.getLat() );
			prptItem.put( "lng", vo.getLng() );
			prptItem.put( "flr", vo.getFlr() );
			prptItem.put( "allFlr", vo.getAllFlr() );
			prptItem.put( "minFlr", vo.getMinFlr() );
			prptItem.put( "maxFlr", vo.getMaxFlr() );
			prptItem.put( "monMntnceCost", vo.getMonMntnceCost() );
			prptItem.put( "favYn", vo.getFavYn() );
			
			prptItems.add( prptItem );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "reqItem", reqItem );
		resMap.put( "suggstrItem", suggstrItem );
		resMap.put( "prptItems", prptItems );
		
		return resMap;
	}
	
	/**
	 * Admin - 매물 제안요청 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getPrptSuggstReqItems( String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<PropertySuggestRequestVO> prptSuggstReqList = propertyDAO.getPrptSuggstReqItems( srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( prptSuggstReqList ) ) {
			prptSuggstReqList = new ArrayList<PropertySuggestRequestVO>();
		}else {
			totList = prptSuggstReqList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( PropertySuggestRequestVO vo : prptSuggstReqList ) {
			item = new JSONObject();
			
			item.put( "no", vo.getRownum() );
			item.put( "memSeqno", vo.getMemSeqno() );
			item.put( "email", vo.getEmail() );
			item.put( "memName", vo.getMemName() );
			item.put( "mobNo", GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "prptSuggstCnt", vo.getPrptSuggstCnt() );
			item.put( "recentDt", vo.getRecentDt() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * 투어 요청 ( 일반/중개회원 공통 )
	 * 
	 * @param memSeqno
	 * @param prptSeqno
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void requestTour( long memSeqno, long prptSeqno )throws Exception {
		PropertyVO propertyVO = propertyDAO.getPrptTourValidationInfo( memSeqno, prptSeqno );
		if( propertyVO == null ) {
			// 유효하지 않은 매물
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_AVAIL_PROPERTY );
		}
		
		if( memSeqno == propertyVO.getMemSeqno() ) {
			// 본인의 매물에는 투어요청을 할 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_REQUEST_TOUR_MY_PRPT );
		}
		if( propertyVO.getTourReqCnt() > 0 ) {
			// 이미 신청한 투어
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_REQUESTED_TOUR );
		}
		
		// 투어 요청 등록
		propertyDAO.registerTourRequest( memSeqno, prptSeqno );
		
		// 투어 요청 알림 등록 ( 매물알림 )
		String notiTtl = "신규 투어 요청";
		String notiDscr = "<strong>" + propertyVO.getMemName() + "</strong>님이 <strong>" + propertyVO.getAddr() + " / " + propertyVO.getEstateTypGbNm() + "</strong>에 투어를 요청했어요.";
		gsntalkDAO.registerNotification( propertyVO.getMemSeqno(), "PRPT", null, notiTtl, notiDscr, prptSeqno, 0L );
	}
	
	/**
	 * FRT - 내 매물 제안서 삭제 ( 일반/중개회원 공통 )
	 * 
	 * @param memSeqno
	 * @param prptSuggstReqSeqno
	 * @param suggstMemTypCd
	 * @param suggstMemSeqno
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void deleteMyPrptSuggstItem( long memSeqno, long prptSuggstReqSeqno, String suggstMemTypCd, long suggstMemSeqno )throws Exception {
		PropertySuggestRequestVO propertySuggestRequestVO = propertyDAO.getMyPropertySuggestRequestVO( memSeqno, prptSuggstReqSeqno );
		if( propertySuggestRequestVO == null ) {
			// 유효하지 않은 매물제안
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY_SUGGEST );
		}
		
		// 제안자 정보 조회
		PropertySuggestRequestVO suggesterVO = propertyDAO.getMyPropertyRequestSuggesterInfo( suggstMemTypCd, suggstMemSeqno );
		if( suggesterVO == null ) {
			// 매물 제안자 정보를 찾을 수 없음
			throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_PROPERTY_REQ_SUGGESTER );
		}
		
		// 매물제안 삭제대상 매물시퀀스 목록조회
		List<Long> deletePropertySuggestPrptSeqnoList = propertyDAO.getDeletePropertySuggestPrptSeqnoList( prptSuggstReqSeqno, suggstMemTypCd, suggstMemSeqno );
		if( GsntalkUtil.isEmptyList( deletePropertySuggestPrptSeqnoList ) ) {
			deletePropertySuggestPrptSeqnoList = new ArrayList<Long>();
		}
		
		for( long prptSeqno : deletePropertySuggestPrptSeqnoList ) {
			propertyDAO.deletePropertySuggest( prptSuggstReqSeqno, prptSeqno );
		}
	}
	
	/**
	 * Admin - 매물제안 회원 상세정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getSuggstnPrptMemDtlItem( long memSeqno )throws Exception {
		MemberVO memberVO = propertyDAO.getSuggstnPrptMemDtlItem( memSeqno );
		if( memberVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, memSeqno + " user not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "prflImgUrl", memberVO.getPrflImgUrl() );
		item.put( "memTypNm", memberVO.getMemTypNm() );
		item.put( "ofcNm", memberVO.getOfcNm() );
		item.put( "reprNm", memberVO.getReprNm() );
		item.put( "telNo", memberVO.getTelNo() );
		item.put( "memName", memberVO.getMemName() );
		item.put( "email", memberVO.getEmail() );
		item.put( "mobNo", GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( memberVO.getMobNo() ) ) );
		item.put( "joinDt", memberVO.getJoinDt() );
		item.put( "prptSuggstCnt", memberVO.getPrptSuggstCnt() );
		item.put( "recentDt", memberVO.getRecentDt() );
		
		return item;
	}
}