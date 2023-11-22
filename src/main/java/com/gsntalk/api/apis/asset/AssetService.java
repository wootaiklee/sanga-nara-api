package com.gsntalk.api.apis.asset;

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

import com.gsntalk.api.apis.gsntalk.GsntalkDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.AssetVO;
import com.gsntalk.api.common.vo.CommonCodeVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkMathUtil;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Service( "AssetService" )
public class AssetService extends CommonService {

	@Autowired
	private AssetDAO assetDAO;
	
	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	public AssetService() {
		super( AssetService.class );
	}
	
	/**
	 * FRT - 일반회원 자산 등록 1단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tmpAddrYn
	 * @param unregistYn
	 * @param addr
	 * @param roadAddr
	 * @param dtlAddr
	 * @param lat
	 * @param lng
	 * @param splyArea
	 * @param prvArea
	 * @param lndArea
	 * @param totFlrArea
	 * @param inspGbCd
	 * @param bizmanGbCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject registerAssetStep1Item( long memSeqno, long assetSeqno, String estateTypGbCd, String estateTypCd, String tmpAddrYn, String unregistYn,
			String addr, String roadAddr, String dtlAddr, double lat, double lng, double splyArea, double prvArea, double lndArea, double totFlrArea, String inspGbCd, String bizmanGbCd )throws Exception {
		
		if( assetSeqno != 0 ) {
			AssetVO assetVO = assetDAO.getAssetVO( memSeqno, assetSeqno );
			if( assetVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
			}
		}
		
		String regTmpKey = null;
		
		// encoding XSS
		addr		= GsntalkXSSUtil.encodeXss( addr );
		roadAddr	= GsntalkXSSUtil.encodeXss( roadAddr );
		dtlAddr		= GsntalkXSSUtil.encodeXss( dtlAddr );
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "estateTypGbCd", estateTypGbCd );
		tmpItem.put( "estateTypCd", estateTypCd );
		tmpItem.put( "tmpAddrYn", tmpAddrYn );
		tmpItem.put( "unregistYn", unregistYn );
		tmpItem.put( "addr", addr );
		tmpItem.put( "roadAddr", roadAddr );
		tmpItem.put( "dtlAddr", dtlAddr );
		tmpItem.put( "lat", lat );
		tmpItem.put( "lng", lng );
		tmpItem.put( "splyArea", splyArea );
		tmpItem.put( "prvArea", prvArea );
		tmpItem.put( "lndArea", lndArea );
		tmpItem.put( "totFlrArea", totFlrArea );
		tmpItem.put( "inspGbCd", inspGbCd );
		tmpItem.put( "bizmanGbCd", bizmanGbCd );
		
		// 자산시퀀스가 없으면 신규 생성 및 임시등록 처리
		if( assetSeqno == 0L ) {
			
			// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
			regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			// 등록단계별 임시정보 데이터 등록
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 1, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			
		// 자산시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			// 자산 임시 1단계 정보 조회
			regTmpKey = assetDAO.getRegTmpKeyOfAssetStep1( memSeqno, assetSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from assetSeqno [ " + assetSeqno + " ]" );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 1, tmpItem.toJSONString() );
			
			// 자산 1단계정보 업데이트
			assetDAO.updateAssetStep1Data( memSeqno, assetSeqno, estateTypGbCd, estateTypCd, tmpAddrYn, unregistYn, addr, roadAddr, dtlAddr, lat, lng, splyArea, prvArea, inspGbCd, bizmanGbCd );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * FRT - 일반회원 자산 1단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetStep1DtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetStep1Data( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		
		item.put( "estateTypGbCd", assetVO.getEstateTypGbCd() );
		item.put( "estateTypCd", assetVO.getEstateTypCd() );
		item.put( "tmpAddrYn", assetVO.getTmpAddrYn() );
		item.put( "unregistYn", assetVO.getUnregistYn() );
		item.put( "addr", assetVO.getAddr() );
		item.put( "roadAddr", assetVO.getRoadAddr() );
		item.put( "dtlAddr", assetVO.getDtlAddr() );
		item.put( "lat", assetVO.getLat() );
		item.put( "lng", assetVO.getLng() );
		item.put( "splyArea", GsntalkUtil.parsePyungToMeters( assetVO.getSplyArea() ) );
		item.put( "prvArea", GsntalkUtil.parsePyungToMeters( assetVO.getPrvArea() ) );
		item.put( "lndArea", GsntalkUtil.parsePyungToMeters( assetVO.getLndArea() ) );
		item.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( assetVO.getTotFlrArea() ) );
		item.put( "inspGbCd", assetVO.getInspGbCd() );
		item.put( "bizmanGbCd", assetVO.getBizmanGbCd() );
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 기존 자산 주소 중복 확인
	 * 
	 * @param memSeqno
	 * @param addr
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject checkForExistsAddress( long memSeqno, String addr )throws Exception {
		int c = assetDAO.getDuplicatedAssetAddressCnt( memSeqno, addr );
		
		JSONObject item = new JSONObject();
		item.put( "existsYn", c > 0 ? GsntalkConstants.YES : GsntalkConstants.NO );
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 자산 등록 2단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param regTmpKey
	 * @param tranTypGbCd
	 * @param dealAmt
	 * @param contDate
	 * @param registDate
	 * @param acqstnTaxRatio
	 * @param etcCost
	 * @param taxofcCost
	 * @param estFeeAmt
	 * @param loanAmt
	 * @param loanIntrRatio
	 * @param loanMonTerm
	 * @param loanDate
	 * @param loanMthdGbCd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject registerAssetStep2Item( long memSeqno, long assetSeqno, String regTmpKey, String tranTypGbCd, long dealAmt, String contDate, String registDate,
			double acqstnTaxRatio, long etcCost, int taxofcCost, int estFeeAmt, long loanAmt, double loanIntrRatio, int loanMonTerm, String loanDate, String loanMthdGbCd )throws Exception {
		
		if( assetSeqno != 0 ) {
			AssetVO assetVO = assetDAO.getAssetVO( memSeqno, assetSeqno );
			if( assetVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
			}
		}
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "tranTypGbCd", tranTypGbCd );
		tmpItem.put( "dealAmt", dealAmt );
		tmpItem.put( "contDate", contDate );
		tmpItem.put( "registDate", registDate );
		tmpItem.put( "acqstnTaxRatio", acqstnTaxRatio );
		tmpItem.put( "etcCost", etcCost );
		tmpItem.put( "taxofcCost", taxofcCost );
		tmpItem.put( "estFeeAmt", estFeeAmt );
		tmpItem.put( "loanAmt", loanAmt );
		tmpItem.put( "loanIntrRatio", loanIntrRatio );
		tmpItem.put( "loanMonTerm", loanMonTerm );
		tmpItem.put( "loanDate", loanDate );
		tmpItem.put( "loanMthdGbCd", loanMthdGbCd );
		
		// 자산시퀀스가 없으면 임시등록 처리 (regTmpKey 있음)
		if( assetSeqno == 0L ) {
			// 1단계 정보 검증
			String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 1 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "1단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
			tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 2 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 2, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			}else {
				gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 2, tmpItem.toJSONString() );
			}
			
		// 자산시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			// 자산 임시 1단계 정보 조회
			regTmpKey = assetDAO.getRegTmpKeyOfAssetStep1( memSeqno, assetSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from assetSeqno [ " + assetSeqno + " ]" );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 2, tmpItem.toJSONString() );
			
			// 자산 2단계정보 업데이트
			assetDAO.updateAssetStep2Data( memSeqno, assetSeqno, tranTypGbCd, dealAmt, contDate, registDate,
					acqstnTaxRatio, etcCost, taxofcCost, estFeeAmt, loanAmt, loanIntrRatio, loanMonTerm, loanDate, loanMthdGbCd );
			
			
			/** 수정시에 수익정보 필드 재계산 */
			// 수익률 필드 재계산을 위한 1단계 정보 조회
			AssetVO asset1StepVO = assetDAO.getAssetStep1Data( memSeqno, assetSeqno );
			if( asset1StepVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
			}
			
			// 수익률 필드 재계산을 위한 3단계 정보 조회
			AssetVO asset3StepVO = assetDAO.getAssetStep3Data( memSeqno, assetSeqno );
			if( asset3StepVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
			}
			
			long dpstAmt = asset3StepVO.getDpstAmt();					// 보증금액(원)
			int montRentAmt = asset3StepVO.getMontRentAmt();				// 월임대료(원)
			
			// [총 비용] :: 세무비용 + 부동산수수료 + 기타비용
			long totCost = etcCost + taxofcCost + estFeeAmt;
			
			// [취득세 금액] :: 거래금액 * 취득세율[%] / 100
			int acqstnTaxAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.multiply( dealAmt, acqstnTaxRatio ), 100L );
			
			// [실 투자금액] :: 거래금액( 매매가 or 분양가 ) + 취득세 + 총비용 - 대출금액 - 보증금액
			long realInvestAmt =
					dealAmt
					+ acqstnTaxAmt
					+ totCost
					- loanAmt
					- dpstAmt;
			
			// [대출 이자금액] :: 대출금액 * 대출금리[%] / 12 / 100
			int loanIntrAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.divide( GsntalkMathUtil.multiply( loanAmt, loanIntrRatio ), 12 ), 100 );
			
			// [월 순수익금액] :: 월 임대료 - 대출 이자금액
			int monProfitAmt = montRentAmt - loanIntrAmt;
			
			// [수익률] :: 월 순수익금액 / 실 투자금액 * 100
			double rtnRatio = GsntalkMathUtil.multiply( GsntalkMathUtil.divide( monProfitAmt, realInvestAmt, 12 ), 100, 2 );
			
			// [평단가금액] :: 거래금액 / 공급면적
			int pyUnitAmt = (int)GsntalkMathUtil.divide( dealAmt, asset1StepVO.getSplyArea() );
			
			// [시세 차익금액] :: ?
			long mkProfitAmt = 0L;
			
			// [양도세 금액] :: ?
			int trnsfTaxAmt = 0;
			
			// 자산 수익정보 필드 업데이트
			assetDAO.updateAssetProfitInfo( assetSeqno, realInvestAmt, loanIntrAmt, acqstnTaxAmt, totCost, rtnRatio, monProfitAmt, pyUnitAmt, mkProfitAmt, trnsfTaxAmt );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * FRT - 일반회원 자산 2단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetStep2DtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetStep2Data( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "tranTypGbCd", assetVO.getTranTypGbCd() );
		item.put( "dealAmt", assetVO.getDealAmt() );
		item.put( "contDate", assetVO.getContDate() );
		item.put( "registDate", assetVO.getRegistDate() );
		item.put( "acqstnTaxRatio", assetVO.getAcqstnTaxRatio() );
		item.put( "etcCost", assetVO.getEtcCost() );
		item.put( "taxofcCost", assetVO.getTaxofcCost() );
		item.put( "estFeeAmt", assetVO.getEstFeeAmt() );
		item.put( "loanAmt", assetVO.getLoanAmt() );
		item.put( "loanIntrRatio", assetVO.getLoanIntrRatio() );
		item.put( "loanMonTerm", assetVO.getLoanMonTerm() );
		item.put( "loanDate", assetVO.getLoanDate() );
		item.put( "loanMthdGbCd", assetVO.getLoanMthdGbCd() );
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 자산 등록 3단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param regTmpKey
	 * @param emptyTypGbCd
	 * @param leseeNm
	 * @param leseeTelNo
	 * @param rentAmtPayMthdGbCd
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param monthlyPayDayGbCd
	 * @param rentContStDate
	 * @param rentContEdDate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject registerAssetStep3Item( long memSeqno, long assetSeqno, String regTmpKey, String emptyTypGbCd, String leseeNm, String leseeTelNo,
			String rentAmtPayMthdGbCd, long dpstAmt, int montRentAmt, String monthlyPayDayGbCd, String rentContStDate, String rentContEdDate )throws Exception {
		
		// 공실유형이 계약중이면서 월세 입금일자 구분코드가 있으면
		if( "C".equals( emptyTypGbCd ) && !GsntalkUtil.isEmpty( monthlyPayDayGbCd ) ) {
			// 월세 입금일자 구분코드 목록조회
			List<CommonCodeVO> commonCodeList = gsntalkDAO.getComnCdList( "MONTHLY_PAY_DAY_GB_CD" );
			boolean commonCdMatched = false;
			for( CommonCodeVO vo : commonCodeList ) {
				if( vo.getItemCd().equals( monthlyPayDayGbCd ) ) {
					commonCdMatched = true;
					break;
				}
			}
			if( !commonCdMatched ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "monthlyPayDayGbCd 값이 잘못됨 ->  see CommonCode [MONTHLY_PAY_DAY_GB_CD]" );
			}
		}
		
		if( assetSeqno != 0 ) {
			AssetVO assetVO = assetDAO.getAssetVO( memSeqno, assetSeqno );
			if( assetVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
			}
		}
		
		// encrypt
		leseeTelNo = GsntalkEncryptor.encrypt( leseeTelNo );
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "emptyTypGbCd", emptyTypGbCd );
		tmpItem.put( "leseeNm", leseeNm );
		tmpItem.put( "leseeTelNo", leseeTelNo );
		tmpItem.put( "rentAmtPayMthdGbCd", rentAmtPayMthdGbCd );
		tmpItem.put( "dpstAmt", dpstAmt );
		tmpItem.put( "montRentAmt", montRentAmt );
		tmpItem.put( "monthlyPayDayGbCd", monthlyPayDayGbCd );
		tmpItem.put( "rentContStDate", rentContStDate );
		tmpItem.put( "rentContEdDate", rentContEdDate );
		
		// 자산시퀀스가 없으면 임시등록 처리 (regTmpKey 있음)
		if( assetSeqno == 0L ) {
			// 1단계 정보 검증
			String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 1 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "1단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 기존 3단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
			tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 3 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 3, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			}else {
				gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 3, tmpItem.toJSONString() );
			}
			
		// 자산시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			// 자산 임시 1단계 정보 조회
			regTmpKey = assetDAO.getRegTmpKeyOfAssetStep1( memSeqno, assetSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from assetSeqno [ " + assetSeqno + " ]" );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 3, tmpItem.toJSONString() );
			
			// 자산 3단계정보 업데이트
			assetDAO.updateAssetStep3Data( memSeqno, assetSeqno, emptyTypGbCd, leseeNm, leseeTelNo,
					rentAmtPayMthdGbCd, dpstAmt, montRentAmt, monthlyPayDayGbCd, rentContStDate, rentContEdDate );
			
			/** 수정시에 수익정보 필드 재계산 */
			// 수익률 필드 재계산을 위한 1단계 정보 조회
			AssetVO asset1StepVO = assetDAO.getAssetStep1Data( memSeqno, assetSeqno );
			if( asset1StepVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
			}
			
			// 수익률 필드 재계산을 위한 2단계 정보 조회
			AssetVO asset2StepVO = assetDAO.getAssetStep2Data( memSeqno, assetSeqno );
			if( asset2StepVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
			}
			
			// [총 비용] :: 세무비용 + 부동산수수료 + 기타비용
			long totCost = asset2StepVO.getEtcCost() + asset2StepVO.getTaxofcCost() + asset2StepVO.getEstFeeAmt();
			
			// [취득세 금액] :: 거래금액 * 취득세율[%] / 100
			int acqstnTaxAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.multiply( asset2StepVO.getDealAmt(), asset2StepVO.getAcqstnTaxRatio() ), 100L );
			
			// [실 투자금액] :: 거래금액( 매매가 or 분양가 ) + 취득세 + 총비용 - 대출금액 - 보증금액
			long realInvestAmt =
					asset2StepVO.getDealAmt()
					+ acqstnTaxAmt
					+ totCost
					- asset2StepVO.getLoanAmt()
					- dpstAmt;
			
			// [대출 이자금액] :: 대출금액 * 대출금리[%] / 12 / 100
			int loanIntrAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.divide( GsntalkMathUtil.multiply( asset2StepVO.getLoanAmt(), asset2StepVO.getLoanIntrRatio() ), 12 ), 100 );
			
			// [월 순수익금액] :: 월 임대료 - 대출 이자금액
			int monProfitAmt = montRentAmt - loanIntrAmt;
			
			// [수익률] :: 월 순수익금액 / 실 투자금액 * 100
			double rtnRatio = GsntalkMathUtil.multiply( GsntalkMathUtil.divide( monProfitAmt, realInvestAmt, 12 ), 100, 2 );
			
			// [평단가금액] :: 거래금액 / 공급면적
			int pyUnitAmt = (int)GsntalkMathUtil.divide( asset2StepVO.getDealAmt(), asset1StepVO.getSplyArea() );
			
			// [시세 차익금액] :: ?
			long mkProfitAmt = 0L;
			
			// [양도세 금액] :: ?
			int trnsfTaxAmt = 0;
			
			// 자산 수익정보 필드 업데이트
			assetDAO.updateAssetProfitInfo( assetSeqno, realInvestAmt, loanIntrAmt, acqstnTaxAmt, totCost, rtnRatio, monProfitAmt, pyUnitAmt, mkProfitAmt, trnsfTaxAmt );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * FRT - 일반회원 자산 3단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetStep3DtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetStep3Data( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "emptyTypGbCd", assetVO.getEmptyTypGbCd() );
		item.put( "leseeNm", assetVO.getLeseeNm() );
		item.put( "leseeTelNo", GsntalkEncryptor.decrypt( assetVO.getLeseeTelNo() ) );			// decrypt.
		item.put( "rentAmtPayMthdGbCd", assetVO.getRentAmtPayMthdGbCd() );
		item.put( "dpstAmt", assetVO.getDpstAmt() );
		item.put( "montRentAmt", assetVO.getMontRentAmt() );
		item.put( "monthlyPayDayGbCd", assetVO.getMonthlyPayDayGbCd() );
		item.put( "rentContStDate", assetVO.getRentContStDate() );
		item.put( "rentContEdDate", assetVO.getRentContEdDate() );
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 자산 등록 최종단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @param regTmpKey
	 * @param scFile
	 * @param brFile
	 * @param rcFile
	 * @param etFile
	 * @throws Exception
	 */
	@Transactional( rollbackFor = Exception.class )
	public void registerAssetFinalStepItem( long memSeqno, long assetSeqno, String regTmpKey, MultipartFile scFile, MultipartFile brFile, MultipartFile rcFile, MultipartFile etFile,
			String scFileDelYn, String brFileDelYn, String rcFileDelYn, String etFileDelYn )throws Exception {
		if( assetSeqno != 0 ) {
			AssetVO assetVO = assetDAO.getAssetVO( memSeqno, assetSeqno );
			if( assetVO == null ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
			}
		}
		
		String orgFileNm = "";
		String uploadFileFormat = "";
		
		// 매매계약서 첨부파일이 있으면
		if( scFile != null && scFile.getSize() != 0L ) {
			orgFileNm = scFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "scFile : " + orgFileNm );
			}
		}
		// 사업자등록증 첨부파일이 있으면
		if( brFile != null && brFile.getSize() != 0L ) {
			orgFileNm = brFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "brFile : " + orgFileNm );
			}
		}
		// 임대차계약서 첨부파일이 있으면
		if( rcFile != null && rcFile.getSize() != 0L ) {
			orgFileNm = rcFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "rcFile : " + orgFileNm );
			}
		}
		// 기타서류 첨부파일이 있으면
		if( etFile != null && etFile.getSize() != 0L ) {
			orgFileNm = etFile.getOriginalFilename();
			uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
			
			if( !GsntalkUtil.isAcceptedUploadImageFileFormat( uploadFileFormat ) ) {
				// 허용되지 않은 파일 포맷
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_ACCEPT_FILE_FORMAT, "etFile : " + orgFileNm );
			}
		}
		
		// 자산시퀀스가 없으면 등록 (regTmpKey 있음)
		if( assetSeqno == 0L ) {
			// 1단계 임시저장 JSON 조회
			String firstTmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 1 );
			if( GsntalkUtil.isEmpty( firstTmpJsonData ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "1단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 2단계 임시저장 JSON 조회
			String secondTmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 2 );
			if( GsntalkUtil.isEmpty( secondTmpJsonData ) ) {
				// 이전 단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "2단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 3단계 임시저장 JSON 조회
			String thirdTmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_MY_ASST, regTmpKey, 3 );
			if( GsntalkUtil.isEmpty( secondTmpJsonData ) ) {
				// 이전 단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "3단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 1단계 임시저장 정보
			JSONObject firstItem = (JSONObject)this.jsonParser.parse( firstTmpJsonData );
			String estateTypGbCd			= GsntalkUtil.getString( firstItem.get( "estateTypGbCd" ) );
			String estateTypCd				= GsntalkUtil.getString( firstItem.get( "estateTypCd" ) );
			String tmpAddrYn				= GsntalkUtil.getString( firstItem.get( "tmpAddrYn" ) );
			String unregistYn				= GsntalkUtil.getString( firstItem.get( "unregistYn" ) );
			String addr						= GsntalkUtil.getString( firstItem.get( "addr" ) );						// xss encoded.
			String roadAddr					= GsntalkUtil.getString( firstItem.get( "roadAddr" ) );					// xss encoded.
			String dtlAddr					= GsntalkUtil.getString( firstItem.get( "dtlAddr" ) );					// xss encoded.
			double lat						= GsntalkUtil.getDouble( firstItem.get( "lat" ) );
			double lng						= GsntalkUtil.getDouble( firstItem.get( "lng" ) );
			double splyArea					= GsntalkUtil.getDouble( firstItem.get( "splyArea" ) );
			double prvArea					= GsntalkUtil.getDouble( firstItem.get( "prvArea" ) );
			double lndArea					= GsntalkUtil.getDouble( firstItem.get( "splyArea" ) );
			double totFlrArea				= GsntalkUtil.getDouble( firstItem.get( "totFlrArea" ) );
			String inspGbCd					= GsntalkUtil.getString( firstItem.get( "inspGbCd" ) );
			String bizmanGbCd				= GsntalkUtil.getString( firstItem.get( "bizmanGbCd" ) );
			
			// 2단계 임시저장 정보
			JSONObject secondItem = (JSONObject)this.jsonParser.parse( secondTmpJsonData );
			String tranTypGbCd				= GsntalkUtil.getString( secondItem.get( "tranTypGbCd" ) );
			long dealAmt					= GsntalkUtil.getLong( secondItem.get( "dealAmt" ) );
			String contDate					= GsntalkUtil.getString( secondItem.get( "contDate" ) );
			String registDate				= GsntalkUtil.getString( secondItem.get( "registDate" ) );
			double acqstnTaxRatio			= GsntalkUtil.getDouble( secondItem.get( "acqstnTaxRatio" ) );
			long etcCost					= GsntalkUtil.getLong( secondItem.get( "etcCost" ) );
			int taxofcCost					= GsntalkUtil.getInteger( secondItem.get( "taxofcCost" ) );
			int estFeeAmt					= GsntalkUtil.getInteger( secondItem.get( "estFeeAmt" ) );
			long loanAmt					= GsntalkUtil.getLong( secondItem.get( "loanAmt" ) );
			double loanIntrRatio			= GsntalkUtil.getDouble( secondItem.get( "loanIntrRatio" ) );
			int loanMonTerm					= GsntalkUtil.getInteger( secondItem.get( "loanMonTerm" ) );
			String loanDate					= GsntalkUtil.getString( secondItem.get( "loanDate" ) );
			String loanMthdGbCd				= GsntalkUtil.getString( secondItem.get( "loanMthdGbCd" ) );
			
			// 3단계 임시저장 정보
			JSONObject thirdItem = (JSONObject)this.jsonParser.parse( thirdTmpJsonData );
			String emptyTypGbCd				= GsntalkUtil.getString( thirdItem.get( "emptyTypGbCd" ) );
			String leseeNm					= GsntalkUtil.getString( thirdItem.get( "leseeNm" ) );
			String leseeTelNo				= GsntalkUtil.getString( thirdItem.get( "leseeTelNo" ) );				// encrypted.
			String rentAmtPayMthdGbCd		= GsntalkUtil.getString( thirdItem.get( "rentAmtPayMthdGbCd" ) );
			long dpstAmt					= GsntalkUtil.getLong( thirdItem.get( "dpstAmt" ) );
			int montRentAmt					= GsntalkUtil.getInteger( thirdItem.get( "montRentAmt" ) );
			String monthlyPayDayGbCd		= GsntalkUtil.getString( thirdItem.get( "monthlyPayDayGbCd" ) );
			String rentContStDate			= GsntalkUtil.getString( thirdItem.get( "rentContStDate" ) );
			String rentContEdDate			= GsntalkUtil.getString( thirdItem.get( "rentContEdDate" ) );
			
			// 자산 등록
			assetSeqno = assetDAO.registerAsset( memSeqno, estateTypGbCd, estateTypCd, tmpAddrYn, unregistYn, addr, roadAddr, dtlAddr, lat, lng, splyArea, prvArea, lndArea, totFlrArea, inspGbCd, bizmanGbCd,
					tranTypGbCd, dealAmt, contDate, registDate, acqstnTaxRatio, etcCost, taxofcCost, estFeeAmt, loanAmt, loanIntrRatio, loanMonTerm, loanDate, loanMthdGbCd,
					emptyTypGbCd, leseeNm, leseeTelNo, rentAmtPayMthdGbCd, dpstAmt, montRentAmt, monthlyPayDayGbCd, rentContStDate, rentContEdDate );
			
			/** 신규등록시에 수익정보 계산 */
			
			// [총 비용] :: 세무비용 + 부동산수수료 + 기타비용
			long totCost = etcCost + taxofcCost + estFeeAmt;
			
			// [취득세 금액] :: 거래금액 * 취득세율[%] / 100
			int acqstnTaxAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.multiply( dealAmt, acqstnTaxRatio ), 100L );
			
			// [실 투자금액] :: 거래금액( 매매가 or 분양가 ) + 취득세 + 총비용 - 대출금액 - 보증금액
			long realInvestAmt =
					dealAmt
					+ acqstnTaxAmt
					+ totCost
					- loanAmt
					- dpstAmt;
			
			// [대출 이자금액] :: 대출금액 * 대출금리[%] / 12 / 100
			int loanIntrAmt = (int)GsntalkMathUtil.divide( GsntalkMathUtil.divide( GsntalkMathUtil.multiply( loanAmt, loanIntrRatio ), 12 ), 100 );
			
			// [월 순수익금액] :: 월 임대료 - 대출 이자금액
			int monProfitAmt = montRentAmt - loanIntrAmt;
			
			// [수익률] :: 월 순수익금액 / 실 투자금액 * 100
			double rtnRatio = GsntalkMathUtil.multiply( GsntalkMathUtil.divide( monProfitAmt, realInvestAmt, 12 ), 100, 2 );
			
			// [평단가금액] :: 거래금액 / 공급면적
			int pyUnitAmt = (int)GsntalkMathUtil.divide( dealAmt, splyArea );
			
			// [시세 차익금액] :: ?
			long mkProfitAmt = 0L;
			
			// [양도세 금액] :: ?
			int trnsfTaxAmt = 0;
			
			// 자산 수익정보 필드 업데이트
			assetDAO.updateAssetProfitInfo( assetSeqno, realInvestAmt, loanIntrAmt, acqstnTaxAmt, totCost, rtnRatio, monProfitAmt, pyUnitAmt, mkProfitAmt, trnsfTaxAmt );
			
		// 자산시퀀스가 있으면(수정) 삭제 요청 파일 삭제처리
		}else {
			if( GsntalkConstants.YES.equals( scFileDelYn ) ) {
				assetDAO.deleteAssetAtchDocItem( assetSeqno, "SC" );
			}
			if( GsntalkConstants.YES.equals( brFileDelYn ) ) {
				assetDAO.deleteAssetAtchDocItem( assetSeqno, "BR" );
			}
			if( GsntalkConstants.YES.equals( rcFileDelYn ) ) {
				assetDAO.deleteAssetAtchDocItem( assetSeqno, "RC" );
			}
			if( GsntalkConstants.YES.equals( etFileDelYn ) ) {
				assetDAO.deleteAssetAtchDocItem( assetSeqno, "ET" );
			}
		}
		
		
		JSONObject scFileItem = null;
		JSONObject brFileItem = null;
		JSONObject rcFileItem = null;
		JSONObject etFileItem = null;
		
		// 매매계약서 첨부파일이 있으면
		if( scFile != null && scFile.getSize() != 0L ) {
			scFileItem = this.gsntalkS3Util.uploadAssetAtchFile( assetSeqno, scFile );
		}
		// 사업자등록증 첨부파일이 있으면
		if( brFile != null && brFile.getSize() != 0L ) {
			brFileItem = this.gsntalkS3Util.uploadAssetAtchFile( assetSeqno, brFile );
		}
		// 임대차계약서 첨부파일이 있으면
		if( rcFile != null && rcFile.getSize() != 0L ) {
			rcFileItem = this.gsntalkS3Util.uploadAssetAtchFile( assetSeqno, rcFile );
		}
		// 기타서류 첨부파일이 있으면
		if( etFile != null && etFile.getSize() != 0L ) {
			etFileItem = this.gsntalkS3Util.uploadAssetAtchFile( assetSeqno, etFile );
		}
		
		if( scFileItem != null ) {
			assetDAO.registerAssetAtchDocItem( assetSeqno, "SC", GsntalkUtil.getString( scFileItem.get( "orgFileNm" ) ), GsntalkUtil.getString( scFileItem.get( "saveFileNm" ) ), GsntalkUtil.getString( scFileItem.get( "fileUrl" ) ) );
		}
		if( brFileItem != null ) {
			assetDAO.registerAssetAtchDocItem( assetSeqno, "BR", GsntalkUtil.getString( brFileItem.get( "orgFileNm" ) ), GsntalkUtil.getString( brFileItem.get( "saveFileNm" ) ), GsntalkUtil.getString( brFileItem.get( "fileUrl" ) ) );
		}
		if( rcFileItem != null ) {
			assetDAO.registerAssetAtchDocItem( assetSeqno, "RC", GsntalkUtil.getString( rcFileItem.get( "orgFileNm" ) ), GsntalkUtil.getString( rcFileItem.get( "saveFileNm" ) ), GsntalkUtil.getString( rcFileItem.get( "fileUrl" ) ) );
		}
		if( etFileItem != null ) {
			assetDAO.registerAssetAtchDocItem( assetSeqno, "ET", GsntalkUtil.getString( etFileItem.get( "orgFileNm" ) ), GsntalkUtil.getString( etFileItem.get( "saveFileNm" ) ), GsntalkUtil.getString( etFileItem.get( "fileUrl" ) ) );
		}
	}
	
	/**
	 * FRT - 일반회원 자산 최종단계 수정용 정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetFinalStepDtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetFinalStepData( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "assetSeqno : " + assetSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "scFileUrl", assetVO.getScFileUrl() );
		item.put( "brFileUrl", assetVO.getBrFileUrl() );
		item.put( "rcFileUrl", assetVO.getRcFileUrl() );
		item.put( "etFileUrl", assetVO.getEtFileUrl() );
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 자산 요약정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetSummaryItem( long memSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetSummaryItem( memSeqno );
		
		JSONObject item = new JSONObject();
		if( assetVO == null ) {
			item.put( "totAssetAmt",			"0" );
			item.put( "totRealInvestAmt",		"0" );
			item.put( "totMonProfitAmt",		"0" );
			item.put( "rtnRatio",				0.0d );
			item.put( "totDpstAmt",				"0" );
			item.put( "totMontRentAmt",			"0" );
			item.put( "totLoanAmt",				"0" );
			item.put( "totLoanIntrAmt",			"0" );
			item.put( "totAcqstnTaxAmt",		"0" );
			item.put( "totCost",				"0" );
		}else {
			item.put( "totAssetAmt",			GsntalkUtil.set1000Comma( assetVO.getDealAmt() ) );
			item.put( "totRealInvestAmt",		GsntalkUtil.set1000Comma( assetVO.getRealInvestAmt() ) );
			item.put( "totMonProfitAmt",		GsntalkUtil.set1000Comma( assetVO.getMonProfitAmt() ) );
			item.put( "rtnRatio",				assetVO.getRtnRatio() );
			item.put( "totDpstAmt",				GsntalkUtil.set1000Comma( assetVO.getDpstAmt() ) );
			item.put( "totMontRentAmt",			GsntalkUtil.set1000Comma( assetVO.getMontRentAmt() ) );
			item.put( "totLoanAmt",				GsntalkUtil.set1000Comma( assetVO.getLoanAmt() ) );
			item.put( "totLoanIntrAmt",			GsntalkUtil.set1000Comma( assetVO.getLoanIntrAmt() ) );
			item.put( "totAcqstnTaxAmt",		GsntalkUtil.set1000Comma( assetVO.getAcqstnTaxAmt() ) );
			item.put( "totCost",				GsntalkUtil.set1000Comma( assetVO.getTotCost() ) );
		}
		
		return item;
	}
	
	/**
	 * FRT - 일반회원 내 자산 목록조회 
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray myAssetItems( long memSeqno )throws Exception {
		JSONArray items = new JSONArray();
		JSONObject item = null;
		JSONArray assetItems = null;
		JSONObject assetItem = null;
		
		// 내 자산 지번주소 그룹핑 목록조회
		List<String> addrGrpList = assetDAO.getAssetAddressGroupList( memSeqno );
		if( GsntalkUtil.isEmptyList( addrGrpList ) ) {
			addrGrpList = new ArrayList<String>();
		}
		
		List<AssetVO> assetList = null;
		int no = 0;
		for( String addr : addrGrpList ) {
			item = new JSONObject();
			
			item.put( "addr", addr );
			
			assetList = assetDAO.getMyAssetListOfAddr( memSeqno, addr );
			if( GsntalkUtil.isEmptyList( assetList ) ) {
				assetList = new ArrayList<AssetVO>();
			}
			
			assetItems = new JSONArray();
			no = 0;
			for( AssetVO vo : assetList ) {
				assetItem = new JSONObject();
				no ++;
				
				assetItem.put( "no", no );
				assetItem.put( "assetSeqno", vo.getAssetSeqno() );
				assetItem.put( "dtlAddr", vo.getDtlAddr() );
				assetItem.put( "estateTypCd", vo.getEstateTypCd() );
				assetItem.put( "estateTypNm", vo.getEstateTypNm() );
				assetItem.put( "splyArea", GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				assetItem.put( "prvArea", GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				assetItem.put( "lndArea", GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				assetItem.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				assetItem.put( "dpstAmt", GsntalkUtil.set1000Comma( vo.getDpstAmt() ) );
				assetItem.put( "montRentAmt", GsntalkUtil.set1000Comma( vo.getMontRentAmt() ) );
				assetItem.put( "monProfitAmt", GsntalkUtil.set1000Comma( vo.getMonProfitAmt() ) );
				assetItem.put( "rtnRatio", vo.getRtnRatio() );
				
				assetItems.add( assetItem );
			}
			
			item.put( "assetItems", assetItems );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * FRT - 일반회원 내 자산 개별삭제
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @throws Exception
	 */
	public void deleteAsset( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetVO( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
		}
		
		assetDAO.deleteAsset( memSeqno, assetSeqno );
	}
	
	/**
	 * FRT - 일반회원 내 자산 일괄삭제
	 * 
	 * @param memSeqno
	 * @param addr
	 * @throws Exception
	 */
	public void deleteAssetByAddr( long memSeqno, String addr )throws Exception {
		assetDAO.deleteAssetByAddr( memSeqno, addr );
	}
	
	/**
	 * FRT - 일반회원 내 자산 상세정보 조회
	 * 
	 * @param memSeqno
	 * @param assetSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getMyAssetDtlItem( long memSeqno, long assetSeqno )throws Exception {
		AssetVO assetVO = assetDAO.getAssetDtlItem( memSeqno, assetSeqno );
		if( assetVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, assetSeqno + " is not found." );
		}
		
		// 자산 요약 정보
		JSONObject summaryItem = new JSONObject();
		summaryItem.put( "dealAmt",				GsntalkUtil.set1000Comma( assetVO.getDealAmt() ) );
		summaryItem.put( "realInvestAmt",		GsntalkUtil.set1000Comma( assetVO.getRealInvestAmt() ) );
		summaryItem.put( "monProfitAmt",		GsntalkUtil.set1000Comma( assetVO.getMonProfitAmt() ) );
		summaryItem.put( "rtnRatio",			assetVO.getRtnRatio() );
		summaryItem.put( "dpstAmt",				GsntalkUtil.set1000Comma( assetVO.getDpstAmt() ) );
		summaryItem.put( "montRentAmt",			GsntalkUtil.set1000Comma( assetVO.getMontRentAmt() ) );
		summaryItem.put( "loanAmt",				GsntalkUtil.set1000Comma( assetVO.getLoanAmt() ) );
		summaryItem.put( "loanIntrRatio",		assetVO.getLoanIntrRatio() );
		summaryItem.put( "loanIntrAmt",			GsntalkUtil.set1000Comma( assetVO.getLoanIntrAmt() ) );
		summaryItem.put( "acqstnTaxAmt",		GsntalkUtil.set1000Comma( assetVO.getAcqstnTaxAmt() ) );
		summaryItem.put( "totCost",				GsntalkUtil.set1000Comma( assetVO.getTotCost() ) );
		summaryItem.put( "pyUnitAmt",			GsntalkUtil.set1000Comma( assetVO.getPyUnitAmt() ) );
		summaryItem.put( "mkProfitAmt",			GsntalkUtil.set1000Comma( assetVO.getMkProfitAmt() ) );
		summaryItem.put( "aftTaxMkPropAmt",		GsntalkUtil.set1000Comma( assetVO.getMkProfitAmt() - assetVO.getTrnsfTaxAmt() ) );
		
		// 기본정보
		JSONObject baseItem = new JSONObject();
		baseItem.put( "addr",					assetVO.getAddr() );
		baseItem.put( "roadAddr",				assetVO.getRoadAddr() );
		baseItem.put( "dtlAddr",				assetVO.getDtlAddr() );
		baseItem.put( "estateTypNm",			assetVO.getEstateTypNm() );
		baseItem.put( "inspGbNm",				assetVO.getInspGbNm() );
		baseItem.put( "bizmanGbNm",				assetVO.getBizmanGbNm() );
		baseItem.put( "splyArea",				GsntalkUtil.parsePyungToMeters( assetVO.getSplyArea() ) );
		baseItem.put( "prvArea",				GsntalkUtil.parsePyungToMeters( assetVO.getPrvArea() ) );
		baseItem.put( "contDate",				assetVO.getContDate() );
		
		// 대출정보
		JSONObject loanItem = new JSONObject();
		loanItem.put( "loanAmt",				GsntalkUtil.set1000Comma( assetVO.getLoanAmt() ) );
		loanItem.put( "loanMonTerm",			assetVO.getLoanMonTerm() );
		loanItem.put( "loanDate",				assetVO.getLoanDate() );
		loanItem.put( "loanMthdGbNm",			assetVO.getLoanMthdGbNm() );
		loanItem.put( "loanIntrRatio",			assetVO.getLoanIntrRatio() );
		loanItem.put( "loanIntrAmt",			GsntalkUtil.set1000Comma( assetVO.getLoanIntrAmt() ) );
		
		// 임대차 계약정보
		JSONObject contItem = new JSONObject();
		contItem.put( "emptyTypGbNm",			assetVO.getEmptyTypGbNm() );
		contItem.put( "rentContStDate",			assetVO.getRentContStDate() );
		contItem.put( "rentContEdDate",			assetVO.getRentContEdDate() );
		contItem.put( "leseeNm",				assetVO.getLeseeNm() );
		contItem.put( "leseeTelNo",				GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( assetVO.getLeseeTelNo() ) ) );
		contItem.put( "dpstAmt",				GsntalkUtil.set1000Comma( assetVO.getDpstAmt() ) );
		contItem.put( "montRentAmt",			GsntalkUtil.set1000Comma( assetVO.getMontRentAmt() ) );
		contItem.put( "rentAmtPayMthdGbNm",		assetVO.getRentAmtPayMthdGbNm() );
		contItem.put( "monthlyPayDayGbNm",		assetVO.getMonthlyPayDayGbNm() );
		
		// 등록 서류정보
		JSONObject atchItem = new JSONObject();
		atchItem.put( "scFileUrl",				assetVO.getScFileUrl() );
		atchItem.put( "brFileUrl",				assetVO.getBrFileUrl() );
		atchItem.put( "rcFileUrl",				assetVO.getRcFileUrl() );
		atchItem.put( "etFileUrl",				assetVO.getEtFileUrl() );
		
		
		JSONObject item = new JSONObject();
		item.put( "summaryItem",		summaryItem );
		item.put( "baseItem",			baseItem );
		item.put( "loanItem",			loanItem );
		item.put( "contItem",			contItem );
		item.put( "atchItem",			atchItem );
		
		return item;
	}
	
	/**
	 * Admin - 일반회원 자산 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMemAssetItems( String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<AssetVO> assetList = assetDAO.getMemAssetItems( srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( assetList ) ) {
			assetList = new ArrayList<AssetVO>();
		}else {
			totList = assetList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( AssetVO vo : assetList ) {
			item = new JSONObject();
			
			item.put( "no", vo.getRownum() );
			item.put( "memSeqno", vo.getMemSeqno() );
			item.put( "email", vo.getEmail() );
			item.put( "memName", vo.getMemName() );
			item.put( "totAssetAmt", GsntalkUtil.set1000Comma( vo.getDealAmt() ) );
			item.put( "mobNo", GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( vo.getMobNo() ) ) );
			item.put( "assetCnt", vo.getAssetCnt() );
			item.put( "recentDt", vo.getRecentDt() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 자산 일반회원 상세정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getAssetMemDtlItem( long memSeqno )throws Exception {
		MemberVO memberVO = assetDAO.getAssetMemDtlItem( memSeqno );
		if( memberVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, memSeqno + " user not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "prflImgUrl", memberVO.getPrflImgUrl() );
		item.put( "memName", memberVO.getMemName() );
		item.put( "email", memberVO.getEmail() );
		item.put( "mobNo", GsntalkUtil.parseTelnoFormat( GsntalkEncryptor.decrypt( memberVO.getMobNo() ) ) );
		item.put( "joinDt", memberVO.getJoinDt() );
		item.put( "totAssetAmt", GsntalkUtil.set1000Comma( memberVO.getTotAssetAmt() ) );
		item.put( "assetCnt", memberVO.getAssetCnt() );
		item.put( "recentDt", memberVO.getRecentDt() );
		
		return item;
	}
	
	/**
	 * Admin - 일반회원 자산 상세정보 조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMemAssetDtlItem( long memSeqno )throws Exception {
		/* 요약정보 */
		AssetVO assetVO = assetDAO.getAssetSummaryItem( memSeqno );
		JSONObject summaryItem = new JSONObject();
		if( assetVO == null ) {
			summaryItem.put( "totAssetAmt",			"0" );
			summaryItem.put( "totRealInvestAmt",	"0" );
			summaryItem.put( "totMonProfitAmt",		"0" );
			summaryItem.put( "rtnRatio",			0.0d );
			summaryItem.put( "totDpstAmt",			"0" );
			summaryItem.put( "totMontRentAmt",		"0" );
			summaryItem.put( "totLoanAmt",			"0" );
			summaryItem.put( "totLoanIntrAmt",		"0" );
			summaryItem.put( "totAcqstnTaxAmt",		"0" );
			summaryItem.put( "totCost",				"0" );
		}else {
			summaryItem.put( "totAssetAmt",			GsntalkUtil.set1000Comma( assetVO.getDealAmt() ) );
			summaryItem.put( "totRealInvestAmt",	GsntalkUtil.set1000Comma( assetVO.getRealInvestAmt() ) );
			summaryItem.put( "totMonProfitAmt",		GsntalkUtil.set1000Comma( assetVO.getMonProfitAmt() ) );
			summaryItem.put( "rtnRatio",			assetVO.getRtnRatio() );
			summaryItem.put( "totDpstAmt",			GsntalkUtil.set1000Comma( assetVO.getDpstAmt() ) );
			summaryItem.put( "totMontRentAmt",		GsntalkUtil.set1000Comma( assetVO.getMontRentAmt() ) );
			summaryItem.put( "totLoanAmt",			GsntalkUtil.set1000Comma( assetVO.getLoanAmt() ) );
			summaryItem.put( "totLoanIntrAmt",		GsntalkUtil.set1000Comma( assetVO.getLoanIntrAmt() ) );
			summaryItem.put( "totAcqstnTaxAmt",		GsntalkUtil.set1000Comma( assetVO.getAcqstnTaxAmt() ) );
			summaryItem.put( "totCost",				GsntalkUtil.set1000Comma( assetVO.getTotCost() ) );
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		JSONArray assetItems = null;
		JSONObject assetItem = null;
		
		// 내 자산 지번주소 그룹핑 목록조회
		List<String> addrGrpList = assetDAO.getAssetAddressGroupList( memSeqno );
		if( GsntalkUtil.isEmptyList( addrGrpList ) ) {
			addrGrpList = new ArrayList<String>();
		}
		
		List<AssetVO> assetList = null;
		int no = 0;
		for( String addr : addrGrpList ) {
			item = new JSONObject();
			
			item.put( "addr", addr );
			
			assetList = assetDAO.getMyAssetListOfAddr( memSeqno, addr );
			if( GsntalkUtil.isEmptyList( assetList ) ) {
				assetList = new ArrayList<AssetVO>();
			}
			
			assetItems = new JSONArray();
			no = 0;
			for( AssetVO vo : assetList ) {
				assetItem = new JSONObject();
				no ++;
				
				assetItem.put( "no", no );
				assetItem.put( "assetSeqno", vo.getAssetSeqno() );
				assetItem.put( "dtlAddr", vo.getDtlAddr() );
				assetItem.put( "estateTypCd", vo.getEstateTypCd() );
				assetItem.put( "estateTypNm", vo.getEstateTypNm() );
				assetItem.put( "splyArea", GsntalkUtil.parsePyungToMeters( vo.getSplyArea() ) );
				assetItem.put( "prvArea", GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				assetItem.put( "lndArea", GsntalkUtil.parsePyungToMeters( vo.getLndArea() ) );
				assetItem.put( "totFlrArea", GsntalkUtil.parsePyungToMeters( vo.getTotFlrArea() ) );
				assetItem.put( "dpstAmt", GsntalkUtil.set1000Comma( vo.getDpstAmt() ) );
				assetItem.put( "montRentAmt", GsntalkUtil.set1000Comma( vo.getMontRentAmt() ) );
				assetItem.put( "monProfitAmt", GsntalkUtil.set1000Comma( vo.getMonProfitAmt() ) );
				assetItem.put( "rtnRatio", vo.getRtnRatio() );
				
				assetItems.add( assetItem );
			}
			
			item.put( "assetItems", assetItems );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "summaryItem", summaryItem );
		resMap.put( "assetGroupItems", items );
		
		return resMap;
	}
}