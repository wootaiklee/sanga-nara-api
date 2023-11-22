package com.gsntalk.api.apis.compproposal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsntalk.api.apis.gsntalk.GsntalkDAO;
import com.gsntalk.api.apis.member.MemberDAO;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.common.vo.CommonCodeVO;
import com.gsntalk.api.common.vo.CompanyProposalVO;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkMathUtil;
import com.gsntalk.api.util.GsntalkUtil;
import com.gsntalk.api.util.GsntalkXSSUtil;

@Service( "CompanyProposalService" )
public class CompanyProposalService extends CommonService {

	@Autowired
	private CompanyProposalDAO companyProposalDAO;
	
	@Autowired
	private GsntalkDAO gsntalkDAO;
	
	@Autowired
	private MemberDAO memberDAO;
	
	public CompanyProposalService() {
		super( CompanyProposalService.class );
	}
	
	/**
	 * 신규기업 등록
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compNm
	 * @param estBrkDispPosNm
	 * @throws Exception
	 */
	public void registerNewCompanyItem( long estBrkMemOfcSeqno, String compNm, String estBrkDispPosNm )throws Exception {
		int c = companyProposalDAO.isExistsCompNm( estBrkMemOfcSeqno, 0L, compNm );
		if( c > 0 ) {
			// 이미 등록된 기업명
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_USED_IN_COMP_NM, compNm + " is already used." );
		}
		
		// 신규기업 등록
		companyProposalDAO.registerNewCompanyItem( estBrkMemOfcSeqno, compNm, estBrkDispPosNm );
	}
	
	/**
	 * 기업명 수정
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param compNm
	 * @throws Exception
	 */
	public void updateCompNm( long estBrkMemOfcSeqno, long compSeqno, String compNm )throws Exception {
		int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );
		}
		
		c = companyProposalDAO.isExistsCompNm( estBrkMemOfcSeqno, compSeqno, compNm );
		if( c > 0 ) {
			// 이미 등록된 기업명
			throw new GsntalkAPIException( GsntalkAPIResponse.ALREADY_USED_IN_COMP_NM, compNm + " is already used." );
		}
		
		// 기업명 수정
		companyProposalDAO.updateCompNm( compSeqno, compNm );
	}
	
	/**
	 * 기업 삭제
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @throws Exception
	 */
	public void deleteCompanyItem( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );
		}
		
		// 기업 삭제
		companyProposalDAO.deleteCompanyItem( estBrkMemOfcSeqno, compSeqno );
		
		// 기업 이전제안 매물 삭제
		companyProposalDAO.deleteCompanyPrpslPrptItems( estBrkMemOfcSeqno, compSeqno );
	}
	
	/**
	 * 기업 목록 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray getCompItems( long estBrkMemOfcSeqno )throws Exception {
		List<CompanyProposalVO> companyList = companyProposalDAO.getCompItems( estBrkMemOfcSeqno );
		if( GsntalkUtil.isEmptyList( companyList ) ) {
			companyList = new ArrayList<CompanyProposalVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( CompanyProposalVO vo : companyList ) {
			item = new JSONObject();
			
			item.put( "compSeqno", vo.getCompSeqno() );
			item.put( "compNm", vo.getCompNm() );
			item.put( "recentDt", vo.getRecentDt() );
			item.put( "prpslCnt", vo.getPrpslCnt() );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * 신규건물(매물) 추가 1단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @param estateTypGbCd
	 * @param estateTypCd
	 * @param tmpAddrYn
	 * @param addr
	 * @param roadAddr
	 * @param bldNm
	 * @param lat
	 * @param lng
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject registerBldStep1Item( long memSeqno, long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno, String estateTypGbCd, String estateTypCd, String tmpAddrYn, String addr, String roadAddr, String bldNm, double lat, double lng )throws Exception {
		int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );
		}
		
		String regTmpKey = null;
		
		// encoding XSS
		addr		= GsntalkXSSUtil.encodeXss( addr );
		roadAddr	= GsntalkXSSUtil.encodeXss( roadAddr );
		bldNm		= GsntalkXSSUtil.encodeXss( bldNm );
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "estateTypGbCd", estateTypGbCd );
		tmpItem.put( "estateTypCd", estateTypCd );
		tmpItem.put( "tmpAddrYn", tmpAddrYn );
		tmpItem.put( "addr", addr );
		tmpItem.put( "roadAddr", roadAddr );
		tmpItem.put( "bldNm", bldNm );
		tmpItem.put( "lat", lat );
		tmpItem.put( "lng", lng );
		
		// 이전제안매물시퀀스가 없으면 신규 생성 및 임시등록 처리
		if( movPrpslPrptSeqno == 0L ) {
			
			// 1 단계에서만 임시키 생성 ( 무조건 신규생성 및 등록 )
			regTmpKey = GsntalkUtil.createRegistrationTempKey();
			
			// 등록단계별 임시정보 데이터 등록
			gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 1, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			
		// 이전제안매물시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			regTmpKey = companyProposalDAO.getRegTmpKeyOfCompMovPrpslPrptStep1( memSeqno, movPrpslPrptSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from movPrpslPrptSeqno [ " + movPrpslPrptSeqno + " ]" );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 1, tmpItem.toJSONString() );
			
			// 기업이전제안매물 1단계정보 업데이트
			companyProposalDAO.updateCompMovPrpslPrptStep1Data( estBrkMemOfcSeqno, movPrpslPrptSeqno, estateTypGbCd, estateTypCd, tmpAddrYn, addr, roadAddr, GsntalkUtil.getTopAddr( addr, 2 ), bldNm, lat, lng );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * 이전제안 건물(매물) 1단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getBldStep1DtlItem( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = companyProposalDAO.getCompMovPrpslPrptStep1Data( estBrkMemOfcSeqno, compSeqno, movPrpslPrptSeqno );
		if( companyProposalVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "compSeqno : " + compSeqno + ", movPrpslPrptSeqno : " + movPrpslPrptSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		
		item.put( "estateTypGbCd", companyProposalVO.getEstateTypGbCd() );
		item.put( "estateTypCd", companyProposalVO.getEstateTypCd() );
		item.put( "tmpAddrYn", companyProposalVO.getTmpAddrYn() );
		item.put( "addr", companyProposalVO.getAddr() );
		item.put( "roadAddr", companyProposalVO.getRoadAddr() );
		item.put( "bldNm", companyProposalVO.getBldNm() );
		item.put( "lat", companyProposalVO.getLat() );
		item.put( "lng", companyProposalVO.getLng() );
		
		return item;
	}
	
	/**
	 * 신규건물(매물) 추가 2단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @param regTmpKey
	 * @param tranTypGbCd
	 * @param salesCost
	 * @param dpstAmt
	 * @param montRentAmt
	 * @param prmmAmt
	 * @param acqstnTaxRatio
	 * @param supprtAmt
	 * @param etcCost
	 * @param loanRatio1
	 * @param loanRatio2
	 * @param loanIntrRatio
	 * @param investYn
	 * @param investDpstAmt
	 * @param investMontRentAmt
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public JSONObject registerBldStep2Item( long memSeqno, long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno, String regTmpKey, String tranTypGbCd, long salesCost, long dpstAmt, int montRentAmt, long prmmAmt,
			double acqstnTaxRatio, long supprtAmt, long etcCost, double loanRatio1, double loanRatio2, double loanIntrRatio, String investYn, long investDpstAmt, int investMontRentAmt )throws Exception {
		
		int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "compSeqno : " + compSeqno + " is not found." );
		}
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "tranTypGbCd", tranTypGbCd );
		tmpItem.put( "salesCost", salesCost );
		tmpItem.put( "dpstAmt", dpstAmt );
		tmpItem.put( "montRentAmt", montRentAmt );
		tmpItem.put( "prmmAmt", prmmAmt );
		tmpItem.put( "acqstnTaxRatio", acqstnTaxRatio );
		tmpItem.put( "supprtAmt", supprtAmt );
		tmpItem.put( "etcCost", etcCost );
		tmpItem.put( "loanRatio1", loanRatio1 );
		tmpItem.put( "loanRatio2", loanRatio2 );
		tmpItem.put( "loanIntrRatio", loanIntrRatio );
		tmpItem.put( "investYn", investYn );
		tmpItem.put( "investDpstAmt", investDpstAmt );
		tmpItem.put( "investMontRentAmt", investMontRentAmt );
		
		// 이전제안매물시퀀스가 없으면 임시등록 처리 (regTmpKey 있음)
		if( movPrpslPrptSeqno == 0L ) {
			// 1단계 정보 검증
			String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 1 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "1단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 기존 2단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
			tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 2 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 2, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			}else {
				gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 2, tmpItem.toJSONString() );
			}
			
		// 이전제안매물시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			regTmpKey = companyProposalDAO.getRegTmpKeyOfCompMovPrpslPrptStep1( memSeqno, movPrpslPrptSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from movPrpslPrptSeqno [ " + movPrpslPrptSeqno + " ]" );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 2, tmpItem.toJSONString() );
			
			// 기업이전제안매물 2단계정보 업데이트
			companyProposalDAO.updateCompMovPrpslPrptStep2Data( estBrkMemOfcSeqno, movPrpslPrptSeqno, tranTypGbCd, salesCost, dpstAmt, montRentAmt, prmmAmt, acqstnTaxRatio, supprtAmt, etcCost,
					loanRatio1, loanRatio2, loanIntrRatio, investYn, investDpstAmt, investMontRentAmt );
		}
		
		JSONObject resItem = new JSONObject();
		resItem.put( "regTmpKey", regTmpKey );
		
		return resItem;
	}
	
	/**
	 * 이전제안 건물(매물) 2단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getBldStep2DtlItem( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = companyProposalDAO.getCompMovPrpslPrptStep2Data( estBrkMemOfcSeqno, compSeqno, movPrpslPrptSeqno );
		if( companyProposalVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "compSeqno : " + compSeqno + ", movPrpslPrptSeqno : " + movPrpslPrptSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		
		item.put( "tranTypGbCd", companyProposalVO.getTranTypGbCd() );
		item.put( "salesCost", companyProposalVO.getSalesCost() );
		item.put( "dpstAmt", companyProposalVO.getDpstAmt() );
		item.put( "montRentAmt", companyProposalVO.getMontRentAmt() );
		item.put( "prmmAmt", companyProposalVO.getPrmmAmt() );
		item.put( "acqstnTaxRatio", companyProposalVO.getAcqstnTaxRatio() );
		item.put( "supprtAmt", companyProposalVO.getSupprtAmt() );
		item.put( "etcCost", companyProposalVO.getEtcCost() );
		item.put( "loanRatio1", companyProposalVO.getLoanRatio1() );
		item.put( "loanRatio2", companyProposalVO.getLoanRatio2() );
		item.put( "loanIntrRatio", companyProposalVO.getLoanIntrRatio() );
		item.put( "investYn", companyProposalVO.getInvestYn() );
		item.put( "investDpstAmt", companyProposalVO.getInvestDpstAmt() );
		item.put( "investMontRentAmt", companyProposalVO.getInvestMontRentAmt() );
		
		return item;
	}
	
	/**
	 * 신규건물(매물) 추가 최종단계 ( 등록/수정 공통 )
	 * 
	 * @param memSeqno
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @param regTmpKey
	 * @param prvArea
	 * @param flr
	 * @param allFlr
	 * @param monMntnceCost
	 * @param psblMovDayTypCd
	 * @param psblMovDate
	 * @param heatKindGbCd
	 * @param parkingCarCnt
	 * @param compPrpslBldFacTypItems
	 * @param bldSpclAdvtgDscr
	 * @param reqDscr
	 * @param outerTmpFileNm
	 * @param innerTmpFileItems
	 * @param delInnterFileURLItems
	 * @param modInnerFileItems
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void registerBldFinalStepItem( long memSeqno, long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno, String regTmpKey, double prvArea, int flr, int allFlr, int monMntnceCost, String psblMovDayTypCd, String psblMovDate,
			String heatKindGbCd, int parkingCarCnt, JSONArray compPrpslBldFacTypItems, String bldSpclAdvtgDscr, String reqDscr, String outerTmpFileNm, JSONArray innerTmpFileItems, JSONArray delInnterFileURLItems, JSONArray modInnerFileItems )throws Exception {
		int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );
		}
		
		// 기업이전제안 건물시설 유형코드 목록조회
		List<CommonCodeVO> commonCodeList = gsntalkDAO.getComnCdList( "COMP_PRPSL_BLD_FAC_TYP_CD" );
		boolean commonCdMatched = false;
		
		String compPrpslBldFacTypCd = null;
		for( int i = 0; i < compPrpslBldFacTypItems.size(); i ++ ) {
			compPrpslBldFacTypCd = GsntalkUtil.getString( compPrpslBldFacTypItems.get( i ) );
			
			if( GsntalkUtil.isEmpty( compPrpslBldFacTypCd ) ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "compPrpslBldFacTypItems[" + i + "] 값이 비어있음." );
			}
			
			commonCdMatched = false;
			for( CommonCodeVO vo : commonCodeList ) {
				if( vo.getItemCd().equals( compPrpslBldFacTypCd ) ) {
					commonCdMatched = true;
					break;
				}
			}
			if( !commonCdMatched ) {
				// 잘못된 파라메터 값
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "compPrpslBldFacTypItems[" + i + "] 값이 잘못됨 ->  see CommonCode [COMP_PRPSL_BLD_FAC_TYP_CD]" );
			}
		}

		/** 최종(3)단계 정보 */
		// encoding XSS
		bldSpclAdvtgDscr		= GsntalkXSSUtil.encodeXss( bldSpclAdvtgDscr );
		reqDscr					= GsntalkXSSUtil.encodeXss( reqDscr );
		
		JSONObject tmpItem = new JSONObject();
		tmpItem.put( "prvArea", prvArea );
		tmpItem.put( "flr", flr );
		tmpItem.put( "allFlr", allFlr );
		tmpItem.put( "monMntnceCost", monMntnceCost );
		tmpItem.put( "psblMovDayTypCd", psblMovDayTypCd );
		tmpItem.put( "psblMovDate", psblMovDate );
		tmpItem.put( "heatKindGbCd", heatKindGbCd );
		tmpItem.put( "parkingCarCnt", parkingCarCnt );
		tmpItem.put( "compPrpslBldFacTypItems", compPrpslBldFacTypItems );
		tmpItem.put( "bldSpclAdvtgDscr", bldSpclAdvtgDscr );
		tmpItem.put( "reqDscr", reqDscr );
		tmpItem.put( "outerTmpFileNm", outerTmpFileNm );
		tmpItem.put( "innerTmpFileItems", innerTmpFileItems );
		tmpItem.put( "delInnterFileURLItems", delInnterFileURLItems );
		tmpItem.put( "modInnerFileItems", modInnerFileItems );
		
		
		// 이전제안매물시퀀스가 없으면 등록 (regTmpKey 있음)
		if( movPrpslPrptSeqno == 0L ) {
			
			// 1단계 임시저장 JSON 조회
			String firstTmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 1 );
			if( GsntalkUtil.isEmpty( firstTmpJsonData ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "1단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 2단계 임시저장 JSON 조회
			String secondTmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 2 );
			if( GsntalkUtil.isEmpty( firstTmpJsonData ) ) {
				// 이전 단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATE_PREV_STEP, "2단계 임시저장 정보를 찾을 수 없음" );
			}
			
			// 건물 내부사진 첨부가능 수량 검증
			if( innerTmpFileItems.size() > 4 ) {
				// 첨부가능한 최대 파일수량 초과
				throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "innerTmpFileItems size over 4." );
			}
			
			// 건물 내부사진 데이터 검증
			JSONObject innerTmpFileItem = null;
			int no = 0;
			String tmpFileNm = "";
			for( int i = 0; i < innerTmpFileItems.size(); i ++ ) {
				innerTmpFileItem = (JSONObject)innerTmpFileItems.get( i );
				
				no = GsntalkUtil.getInteger( innerTmpFileItem.get( "no" ) );
				tmpFileNm = GsntalkUtil.getString( innerTmpFileItem.get( "tmpFileNm" ) );
				
				if( no == 0 ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "innerTmpFileItem[" + i + "] -> no is empty" );
				}
				if( GsntalkUtil.isEmpty( tmpFileNm ) ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "innerTmpFileItem[" + i + "] -> tmpFileNm is empty" );
				}
			}
			
			// 기존 3단계 임시저장 JSON 조회 ( 없으면 insert, 있으면 update )
			String tmpJsonData = gsntalkDAO.getTempdataOfRegistrationStepJson( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 3 );
			if( GsntalkUtil.isEmpty( tmpJsonData ) ) {
				gsntalkDAO.registrationTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 3, tmpItem.toJSONString(), 0L, 0L, 0L, 0L, 0L );
			}else {
				gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 3, tmpItem.toJSONString() );
			}
			
			// 1단계 임시저장 정보
			JSONObject firstItem = (JSONObject)this.jsonParser.parse( firstTmpJsonData );
			String estateTypGbCd			= GsntalkUtil.getString( firstItem.get( "estateTypGbCd" ) );
			String estateTypCd				= GsntalkUtil.getString( firstItem.get( "estateTypCd" ) );
			String tmpAddrYn				= GsntalkUtil.getString( firstItem.get( "tmpAddrYn" ) );
			String addr						= GsntalkUtil.getString( firstItem.get( "addr" ) );						// XssEncoded.
			String roadAddr					= GsntalkUtil.getString( firstItem.get( "roadAddr" ) );					// XssEncoded.
			String bldNm					= GsntalkUtil.getString( firstItem.get( "bldNm" ) );					// XssEncoded.
			double lat						= GsntalkUtil.getDouble( firstItem.get( "lat" ) );
			double lng						= GsntalkUtil.getDouble( firstItem.get( "lng" ) );
			
			// 2단계 임시저장 정보
			JSONObject secondItem = (JSONObject)this.jsonParser.parse( secondTmpJsonData );
			String tranTypGbCd				= GsntalkUtil.getString( secondItem.get( "tranTypGbCd" ) );
			long salesCost					= GsntalkUtil.getLong( secondItem.get( "salesCost" ) );
			long dpstAmt					= GsntalkUtil.getLong( secondItem.get( "dpstAmt" ) );
			int montRentAmt					= GsntalkUtil.getInteger( secondItem.get( "montRentAmt" ) );
			long prmmAmt					= GsntalkUtil.getLong( secondItem.get( "prmmAmt" ) );
			double acqstnTaxRatio			= GsntalkUtil.getDouble( secondItem.get( "acqstnTaxRatio" ) );
			long supprtAmt					= GsntalkUtil.getLong( secondItem.get( "supprtAmt" ) );
			long etcCost					= GsntalkUtil.getLong( secondItem.get( "etcCost" ) );
			double loanRatio1				= GsntalkUtil.getDouble( secondItem.get( "loanRatio1" ) );
			double loanRatio2				= GsntalkUtil.getDouble( secondItem.get( "loanRatio2" ) );
			double loanIntrRatio			= GsntalkUtil.getDouble( secondItem.get( "loanIntrRatio" ) );
			String investYn					= GsntalkUtil.getString( secondItem.get( "investYn" ) );
			long investDpstAmt				= GsntalkUtil.getLong( secondItem.get( "investDpstAmt" ) );
			int investMontRentAmt			= GsntalkUtil.getInteger( secondItem.get( "investMontRentAmt" ) );
			
			// 기업이전제안 매물(건물) 등록
			movPrpslPrptSeqno = companyProposalDAO.registerCompMovPrpslPrpt( compSeqno, estBrkMemOfcSeqno, estateTypGbCd, estateTypCd, tranTypGbCd, bldNm, tmpAddrYn, addr, roadAddr, GsntalkUtil.getTopAddr( addr, 2 ), lat, lng,
					salesCost, dpstAmt, montRentAmt, prmmAmt, acqstnTaxRatio, supprtAmt, etcCost, loanRatio1, loanRatio2, loanIntrRatio, investYn, investDpstAmt, investMontRentAmt,
					prvArea, flr, allFlr, monMntnceCost, psblMovDayTypCd, psblMovDate, heatKindGbCd, parkingCarCnt, bldSpclAdvtgDscr, reqDscr );
			
			// 건물시설 유형코드 등록
			for( int i = 0; i < compPrpslBldFacTypItems.size(); i ++ ) {
				companyProposalDAO.registerCompMovPrpslPrptFacTyps( movPrpslPrptSeqno, GsntalkUtil.getString( compPrpslBldFacTypItems.get( i ) ) );
			}
			
			String imgUrl = null;
			
			// 건물 외관사진 파일 처리
			imgUrl = gsntalkS3Util.moveTmpFileToCompanyProposalPhotoFile( compSeqno, movPrpslPrptSeqno, outerTmpFileNm );
			
			// 기업이전제안 매물 사진등록
			companyProposalDAO.registerCompMovPrpslPrptPhoto( movPrpslPrptSeqno, 0.0d, outerTmpFileNm, imgUrl, "O" );
			
			// 건물 내부사진 파일 처리
			for( int i = 0; i < innerTmpFileItems.size(); i ++ ) {
				innerTmpFileItem = (JSONObject)innerTmpFileItems.get( i );
				
				no = GsntalkUtil.getInteger( innerTmpFileItem.get( "no" ) );
				tmpFileNm = GsntalkUtil.getString( innerTmpFileItem.get( "tmpFileNm" ) );
				
				imgUrl = gsntalkS3Util.moveTmpFileToCompanyProposalPhotoFile( compSeqno, movPrpslPrptSeqno, tmpFileNm );
				
				// 기업이전제안 매물 사진등록
				companyProposalDAO.registerCompMovPrpslPrptPhoto( movPrpslPrptSeqno, no, tmpFileNm, imgUrl, "I" );
			}
			
			// 등록단계별임시정보 이전제안매물시퀀스 등록
			companyProposalDAO.updateTempDataMovPrpslPrptSeqno( regTmpKey, movPrpslPrptSeqno );
			
		// 이전제안매물시퀀스가 있으면 기존 임시등록정보 검증 -> 업데이트
		}else {
			// 삭제파일 Url 검증
			for( int i = 0; i < delInnterFileURLItems.size(); i ++ ) {
				if( GsntalkUtil.isEmpty( GsntalkUtil.getString( delInnterFileURLItems.get( i ) ) ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "delInnterFileURLItems[" + i + "] 에 빈 값이 존재함." );
				}
			}
			
			// 수정파일 값 검증
			JSONObject modInnerFileItem = null;
			int no = 0;
			String newFileYn = "";
			String fileNm = "";
			for( int i = 0; i < modInnerFileItems.size(); i ++ ) {
				modInnerFileItem = (JSONObject)modInnerFileItems.get( i );
				
				no = GsntalkUtil.getInteger( modInnerFileItem.get( "no" ) );
				newFileYn = GsntalkUtil.getString( modInnerFileItem.get( "newFileYn" ) );
				fileNm = GsntalkUtil.getString( modInnerFileItem.get( "fileNm" ) );
				
				if( no == 0 ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "modInnerFileItem[" + i + "] -> no is empty" );
				}
				if( GsntalkUtil.isEmpty( newFileYn ) ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "modInnerFileItem[" + i + "] -> newFileYn is empty" );
				}
				if( !GsntalkUtil.isIn( newFileYn, GsntalkConstants.YES, GsntalkConstants.NO ) ) {
					// 잘못된 파라메터 값
					throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "modInnerFileItem[" + i + "] -> newFileYn is not in Y / N" );
				}
				if( GsntalkUtil.isEmpty( fileNm ) ) {
					// 필수 요청파라메터 누락
					throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "modInnerFileItem[" + i + "] -> fileNm is empty" );
				}
			}
			
			regTmpKey = companyProposalDAO.getRegTmpKeyOfCompMovPrpslPrptStep1( memSeqno, movPrpslPrptSeqno );
			if( GsntalkUtil.isEmpty( regTmpKey ) ) {
				// 1단계 임시등록 데이터를 찾을 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_FOUND_TEMP_DATA_STEP_1, "not found temp-data from movPrpslPrptSeqno [ " + movPrpslPrptSeqno + " ]" );
			}
			
			// 건물 내부사진 첨부가능 수량 검증
			if( delInnterFileURLItems.size() > 4 ) {
				// 첨부가능한 최대 파일수량 초과
				throw new GsntalkAPIException( GsntalkAPIResponse.OVER_CNT_ATTACHMENT_SIZE, "delInnterFileURLItems size over 4." );
			}
			
			// 등록단계별 임시정보 데이터 업데이트
			gsntalkDAO.updateTempDataStep( memSeqno, GsntalkConstants.REG_CLAS_CD_ESTBRK_TRAN_PRPSL, regTmpKey, 3, tmpItem.toJSONString() );
			
			// 기업이전제안매물 최종(3)단계정보 업데이트
			companyProposalDAO.updateCompMovPrpslPrptFinalStepData( estBrkMemOfcSeqno, movPrpslPrptSeqno, prvArea, flr, allFlr, monMntnceCost, psblMovDayTypCd,
					psblMovDate, heatKindGbCd, parkingCarCnt, bldSpclAdvtgDscr, reqDscr );
			
			// 건물시설 유형코드 삭제 후 재등록
			companyProposalDAO.clearCompMovPrpslPrptFacTyps( movPrpslPrptSeqno );
			for( int i = 0; i < compPrpslBldFacTypItems.size(); i ++ ) {
				companyProposalDAO.registerCompMovPrpslPrptFacTyps( movPrpslPrptSeqno, GsntalkUtil.getString( compPrpslBldFacTypItems.get( i ) ) );
			}
			
			String imgUrl = null;

			// 건물 외관사진 정보가 있으면 처리
			if( !GsntalkUtil.isEmpty( outerTmpFileNm ) ) {
				// 건물 외관사진 파일 처리
				imgUrl = gsntalkS3Util.moveTmpFileToCompanyProposalPhotoFile( compSeqno, movPrpslPrptSeqno, outerTmpFileNm );
				
				// 기업이전제안 매물 외관사진 삭제
				companyProposalDAO.deleteCompMovPrpslPrptOuterPhoto( movPrpslPrptSeqno );
				
				// 기업이전제안 매물 사진등록
				companyProposalDAO.registerCompMovPrpslPrptPhoto( movPrpslPrptSeqno, 0.0d, outerTmpFileNm, imgUrl, "O" );
			}
			
			// 건물 내부사진 삭제 정보가 있으면 삭제 처리
			for( int i = 0; i < delInnterFileURLItems.size(); i ++ ) {
				companyProposalDAO.deleteCompMovPrpslPrptInnterPhoto( movPrpslPrptSeqno, GsntalkUtil.getString( delInnterFileURLItems.get( i ) ) );
			}
			
			// 건물 내부사진 수정 정보가 있으면 수정 처리
			for( int i = 0; i < modInnerFileItems.size(); i ++ ) {
				modInnerFileItem = (JSONObject)modInnerFileItems.get( i );
				
				no = GsntalkUtil.getInteger( modInnerFileItem.get( "no" ) );
				newFileYn = GsntalkUtil.getString( modInnerFileItem.get( "newFileYn" ) );
				fileNm = GsntalkUtil.getString( modInnerFileItem.get( "fileNm" ) );
				
				// 신규 추가 파일이면 파일 이동 후 기업이전제안 매물 사진등록
				if( GsntalkConstants.YES.equals( newFileYn ) ) {
					imgUrl = gsntalkS3Util.moveTmpFileToCompanyProposalPhotoFile( compSeqno, movPrpslPrptSeqno, fileNm );
					
					// 기업이전제안 매물 사진등록
					companyProposalDAO.registerCompMovPrpslPrptPhoto( movPrpslPrptSeqno, no, fileNm, imgUrl, "I" );
					
				// 수정이면 순서정보만 업데이트
				}else {
					// 기업이전제안 매물 내부사진 정렬순서 변경
					companyProposalDAO.updateCompMovPrpslPrptInnterPhotoSortSerl( movPrpslPrptSeqno, no, fileNm );
				}
			}
		}
	}
	
	/**
	 * 이전제안 건물(매물) 최종단계 수정용 정보 조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getBldFinalStepDtlItem( long estBrkMemOfcSeqno, long compSeqno, long movPrpslPrptSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = companyProposalDAO.getCompMovPrpslPrptFinalStepData( estBrkMemOfcSeqno, compSeqno, movPrpslPrptSeqno );
		if( companyProposalVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "compSeqno : " + compSeqno + ", movPrpslPrptSeqno : " + movPrpslPrptSeqno + " data is not found." );
		}
		
		JSONObject item = new JSONObject();
		
		item.put( "prvArea", GsntalkUtil.parsePyungToMeters( companyProposalVO.getPrvArea() ) );
		item.put( "flr", companyProposalVO.getFlr() );
		item.put( "allFlr", companyProposalVO.getAllFlr() );
		item.put( "monMntnceCost", companyProposalVO.getMonMntnceCost() );
		item.put( "psblMovDayTypCd", companyProposalVO.getPsblMovDayTypCd() );
		item.put( "psblMovDate", companyProposalVO.getPsblMovDate() );
		item.put( "heatKindGbCd", companyProposalVO.getHeatKindGbCd() );
		item.put( "parkingCarCnt", companyProposalVO.getParkingCarCnt() );
		item.put( "bldSpclAdvtgDscr", companyProposalVO.getBldSpclAdvtgDscr() );
		item.put( "reqDscr", companyProposalVO.getReqDscr() );
		
		// 기업이전제안 매물 건물시설 유형코드 목록조회
		List<String> compPrpslBldFacTypList = companyProposalDAO.getCompPrpslBldFacTypList( movPrpslPrptSeqno );
		if( GsntalkUtil.isEmptyList( compPrpslBldFacTypList ) ) {
			compPrpslBldFacTypList = new ArrayList<String>();
		}
		JSONArray compPrpslBldFacTypItems = new JSONArray();
		for( String compPrpslBldFacTypCd : compPrpslBldFacTypList ) {
			compPrpslBldFacTypItems.add( compPrpslBldFacTypCd );
		}
		item.put( "compPrpslBldFacTypItems", compPrpslBldFacTypItems );
		
		// 기업이전제안 매물사진 목록조회
		List<CompanyProposalVO> compMovPrpslPrptPhotoList = companyProposalDAO.getCompMovPrpslPrptPhotoList( movPrpslPrptSeqno );
		if( GsntalkUtil.isEmptyList( compMovPrpslPrptPhotoList ) ) {
			compMovPrpslPrptPhotoList = new ArrayList<CompanyProposalVO>();
		}
		JSONArray innerTmpFileItems = new JSONArray();
		JSONObject innerTmpFileItem = null;
		for( CompanyProposalVO vo : compMovPrpslPrptPhotoList ) {
			if( "O".equals( vo.getPhotoGb() ) ) {
				item.put( "outerFileUrl", vo.getFileUrl() );
			}else {
				innerTmpFileItem = new JSONObject();
				
				innerTmpFileItem.put( "fileNm", vo.getSaveFileNm() );
				innerTmpFileItem.put( "fileUrl", vo.getFileUrl() );
				
				innerTmpFileItems.add( innerTmpFileItem );
			}
		}
		item.put( "innerTmpFileItems", innerTmpFileItems );
		
		return item;
	}
	
	/**
	 * 기업 이전 제안 정보조회
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject getCompMovPrpslItem( long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		CompanyProposalVO companyProposalVO = companyProposalDAO.getCompItem( estBrkMemOfcSeqno, compSeqno );
		if( companyProposalVO == null ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "compSeqno : " + compSeqno + " is not found." );
		}
		
		JSONObject item = new JSONObject();
		item.put( "compSeqno", compSeqno );
		item.put( "compNm", companyProposalVO.getCompNm() );
		
		// 기업 이전 제안메물 지번주소 그룹핑 목록조회
		List<String> addrGrpList = companyProposalDAO.getCompMovPrpslAddressGroupList( compSeqno );
		if( GsntalkUtil.isEmptyList( addrGrpList ) ) {
			addrGrpList = new ArrayList<String>();
		}
		
		JSONArray regionItems = new JSONArray();
		JSONObject regionItem = null;
		JSONArray bldItems = null;
		JSONObject bldItem = null;
		
		List<CompanyProposalVO> companyProposalList = null;
		int no = 0;
		String costDscr = null;
		for( String grpAddr : addrGrpList ) {
			regionItem = new JSONObject();
			regionItem.put( "regionNm", grpAddr );
			
			companyProposalList = companyProposalDAO.getCompMovPrpslPrptListOfGrpAddr( compSeqno, grpAddr );
			if( GsntalkUtil.isEmptyList( companyProposalList ) ) {
				companyProposalList = new ArrayList<CompanyProposalVO>();
			}
			
			bldItems = new JSONArray();
			no = 0;
			for( CompanyProposalVO vo : companyProposalList ) {
				bldItem = new JSONObject();
				no ++;
				
				/** 거래금액 적용 */
				costDscr = "";
				// 매매, 전세면 매매가 적용
				if( GsntalkUtil.isIn( vo.getTranTypGbCd(), "TRADE", "CHARTER" ) ) {
					costDscr = GsntalkUtil.parseAmtToKr( vo.getSalesCost() );
					
				// 이외는 보증금/월 임대료 적용
				}else {
					costDscr = GsntalkUtil.parseAmtToKr( vo.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( vo.getMontRentAmt() );
				}
				
				bldItem.put( "no", no );
				bldItem.put( "movPrpslPrptSeqno", vo.getMovPrpslPrptSeqno() );
				bldItem.put( "bldNm", vo.getBldNm() );
				bldItem.put( "addr", GsntalkUtil.getSubAddr( vo.getAddr(), 2 ) );
				bldItem.put( "prvArea", GsntalkUtil.parsePyungToMeters( vo.getPrvArea() ) );
				bldItem.put( "tranTypGbNm", vo.getTranTypGbNm() );
				bldItem.put( "costDscr", costDscr );
				bldItem.put( "flr", vo.getFlr() );
				bldItem.put( "allFlr", vo.getAllFlr() );
				
				bldItems.add( bldItem );
			}
			
			regionItem.put( "bldItems", bldItems );
			
			regionItems.add( regionItem );
		}
		
		item.put( "regionItems", regionItems );
		
		return item;
	}
	
	/**
	 * 기업이전 제안 건물 삭제
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param movPrpslPrptSeqno
	 * @throws Exception
	 */
	public void deleteCompPrpslBldItem( long estBrkMemOfcSeqno, long movPrpslPrptSeqno )throws Exception {
		int c = companyProposalDAO.isUsersBld( estBrkMemOfcSeqno, movPrpslPrptSeqno );
		if( c == 0 ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, "movPrpslPrptSeqno : " + movPrpslPrptSeqno + " is not found." );
		}
		
		companyProposalDAO.deleteCompPrpslBldItem( estBrkMemOfcSeqno, movPrpslPrptSeqno );
	}
	
	/**
	 * Admin - 회원별 기업이전 제안서 목록조회 ( 페이징 )
	 * 
	 * @param srchVal
	 * @param pageCnt
	 * @param nowPage
	 * @param listPerPage
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getMemCompPrpslItems( String srchVal, int pageCnt, int nowPage, int listPerPage )throws Exception {
		JSONObject reqPageItem = GsntalkUtil.getRequestPagingItem( listPerPage, nowPage );
		int stRnum = GsntalkUtil.getInteger( reqPageItem.get( "stRnum" ) );
		int edRnum = GsntalkUtil.getInteger( reqPageItem.get( "edRnum" ) );
		
		List<CompanyProposalVO> companyProposalList = companyProposalDAO.getMemCompPrpslItems( srchVal, stRnum, edRnum );
		int totList = 0;
		if( GsntalkUtil.isEmptyList( companyProposalList ) ) {
			companyProposalList = new ArrayList<CompanyProposalVO>();
		}else {
			totList = companyProposalList.get( 0 ).getTotalCount();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		for( CompanyProposalVO vo : companyProposalList ) {
			item = new JSONObject();
			
			item.put( "no", vo.getRownum() );
			item.put( "memSeqno", vo.getMemSeqno() );
			item.put( "email", vo.getEmail() );
			item.put( "memName", vo.getMemName() );
			item.put( "ofcNm", vo.getOfcNm() );
			item.put( "prpslCnt", vo.getPrpslCnt() );
			item.put( "recentDt", vo.getRecentDt() );
			
			items.add( item );
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "items", items );
		resMap.put( "pageItem", GsntalkUtil.getResponsePagingItem( pageCnt, listPerPage, totList, nowPage ) );
		
		return resMap;
	}
	
	/**
	 * Admin - 회원 기업이전 제안 작성 목록조회
	 * 
	 * @param memSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONArray getMemCompPrpslWrtItems( long memSeqno )throws Exception {
		long estBrkMemOfcSeqno = memberDAO.getEstBrkMemOfcSeqno( memSeqno );
		if( estBrkMemOfcSeqno == 0L ) {
			// 중개회원이 아님
			throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ESTATE_BROKER_USER, "memSeqno : " + memSeqno + " is not estate broker member." );
		}
		
		List<CompanyProposalVO> companyList = companyProposalDAO.getCompItems( estBrkMemOfcSeqno );
		if( GsntalkUtil.isEmptyList( companyList ) ) {
			companyList = new ArrayList<CompanyProposalVO>();
		}
		
		JSONArray items = new JSONArray();
		JSONObject item = null;
		
		List<String> addrGrpList = null;
		JSONArray regionItems = new JSONArray();
		JSONObject regionItem = null;
		JSONArray bldItems = null;
		JSONObject bldItem = null;
		List<CompanyProposalVO> companyProposalList = null;
		int no = 0;
		String costDscr = null;
		
		// 기업 목록
		for( CompanyProposalVO compVO : companyList ) {
			item = new JSONObject();
			
			item.put( "compSeqno", compVO.getCompSeqno() );
			item.put( "compNm", compVO.getCompNm() );
			item.put( "recentDt", compVO.getRecentDt() );
			item.put( "prpslCnt", compVO.getPrpslCnt() );
			
			// 기업 이전 제안메물 지번주소 그룹핑 목록조회
			addrGrpList = companyProposalDAO.getCompMovPrpslAddressGroupList( compVO.getCompSeqno() );
			if( GsntalkUtil.isEmptyList( addrGrpList ) ) {
				addrGrpList = new ArrayList<String>();
			}
			
			for( String grpAddr : addrGrpList ) {
				regionItem = new JSONObject();
				regionItem.put( "regionNm", grpAddr );
				
				// 기업이전 제안매물 목록조회
				companyProposalList = companyProposalDAO.getCompMovPrpslPrptListOfGrpAddr( compVO.getCompSeqno(), grpAddr );
				if( GsntalkUtil.isEmptyList( companyProposalList ) ) {
					companyProposalList = new ArrayList<CompanyProposalVO>();
				}
				
				bldItems = new JSONArray();
				no = 0;
				for( CompanyProposalVO prpslPrptVO : companyProposalList ) {
					bldItem = new JSONObject();
					no ++;
					
					/** 거래금액 적용 */
					costDscr = "";
					// 매매, 전세면 매매가 적용
					if( GsntalkUtil.isIn( prpslPrptVO.getTranTypGbCd(), "TRADE", "CHARTER" ) ) {
						costDscr = GsntalkUtil.parseAmtToKr( prpslPrptVO.getSalesCost() );
						
					// 이외는 보증금/월 임대료 적용
					}else {
						costDscr = GsntalkUtil.parseAmtToKr( prpslPrptVO.getDpstAmt() ) + " / " + GsntalkUtil.parseAmtToKr( prpslPrptVO.getMontRentAmt() );
					}
					
					bldItem.put( "no", no );
					bldItem.put( "movPrpslPrptSeqno", prpslPrptVO.getMovPrpslPrptSeqno() );
					bldItem.put( "bldNm", prpslPrptVO.getBldNm() );
					bldItem.put( "addr", GsntalkUtil.getSubAddr( prpslPrptVO.getAddr(), 2 ) );
					bldItem.put( "prvArea", GsntalkUtil.parsePyungToMeters( prpslPrptVO.getPrvArea() ) );
					bldItem.put( "tranTypGbNm", prpslPrptVO.getTranTypGbNm() );
					bldItem.put( "costDscr", costDscr );
					bldItem.put( "flr", prpslPrptVO.getFlr() );
					bldItem.put( "allFlr", prpslPrptVO.getAllFlr() );
					
					bldItems.add( bldItem );
				}
				
				regionItem.put( "bldItems", bldItems );
				
				regionItems.add( regionItem );
			}
			
			item.put( "regionItems", regionItems );
			
			items.add( item );
		}
		
		return items;
	}
	
	/**
	 * 기업이전 제안서 PDF 견적서용 정보 조회 ( 관리자, 중개회원 )
	 * 
	 * @param memTypCd
	 * @param estBrkMemOfcSeqno
	 * @param compSeqno
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public Map<String, Object> getCompMovPrpslQuoteItem( String memTypCd, long estBrkMemOfcSeqno, long compSeqno )throws Exception {
		// 관리자가 아니면 compSeqno 유효성 검증
		if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memTypCd ) ){
			int c = companyProposalDAO.isUsersComp( estBrkMemOfcSeqno, compSeqno );
			if( c == 0 ) {
				// 유효하지 않은 대상
				throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );
			}
		}
		
		String compNm = companyProposalDAO.getCompNm( compSeqno );
		if( GsntalkUtil.isEmpty( compNm ) ) {
			// 유효하지 않은 대상
			throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_TARGET, compSeqno + " is not found." );	
		}
		
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put( "compNm", compNm );

		/** 목자(지역) 정보 */
		JSONArray listItems = new JSONArray();
		JSONObject listItem = null;
		
		
		// 기업 이전 제안메물 지번주소 그룹핑 목록조회 ( 최대 6건 )
		List<String> addrGrpList = companyProposalDAO.getCompMovPrpslAddressGroup6List( compSeqno );
		if( GsntalkUtil.isEmptyList( addrGrpList ) ) {
			addrGrpList = new ArrayList<String>();
		}
		String regionNm = null;
		List<CompanyProposalVO> companyProposalList = null;
		JSONArray bldItems = null;
		JSONObject bldItem = null;
		for( int i = 0; i < addrGrpList.size(); i ++ ) {
			listItem = new JSONObject();
			
			regionNm = addrGrpList.get( i );
			listItem.put( "regionNm", regionNm );
			
			// 기업이전 제안매물 목록조회 ( 최대 6건 )
			companyProposalList = companyProposalDAO.getCompMovPrpslPrpt6ListOfGrpAddr( compSeqno, regionNm );
			if( GsntalkUtil.isEmptyList( companyProposalList ) ) {
				companyProposalList = new ArrayList<CompanyProposalVO>();
			}
			bldItems = new JSONArray();
			for( CompanyProposalVO prpslPrptVO : companyProposalList ) {
				bldItem = new JSONObject();
				bldItem.put( "bldNm", prpslPrptVO.getBldNm() );
				
				bldItems.add( bldItem );
			}
			listItem.put( "bldItems", bldItems );
			listItems.add( listItem );
		}
		resMap.put( "listItems", listItems );
		
		
		/** 건물 소개 정보 */
		JSONArray bldIntrItems = new JSONArray();
		JSONObject bldIntrItem = null;
		JSONObject baseItem = null;
		JSONObject costItem = null;
		JSONArray innerImgItems = null;
		JSONObject innerImgItem = null;
		JSONObject estate1Item = null;
		JSONObject estate2Item = null;
		long loanCost = 0L;
		long monIntrRtnCost = 0L;
		long acqstnTaxCost = 0L;
		long realInvestAmt = 0L;
		long monProfitAmt = 0L;
		long yearProfitAmt = 0L;
		double yearProfiRatio = 0.0d;
		double rtnDueYr = 0.0d;
		List<String> innerImgList = null;
		for( String grpAddr : addrGrpList ) {
			// 기업이전 제안매물 목록조회 ( 최대 6건 )
			companyProposalList = companyProposalDAO.getCompMovPrpslPrpt6ListOfGrpAddr( compSeqno, grpAddr );
			if( GsntalkUtil.isEmptyList( companyProposalList ) ) {
				companyProposalList = new ArrayList<CompanyProposalVO>();
			}
			for( CompanyProposalVO prpslPrptVO : companyProposalList ) {
				bldIntrItem = new JSONObject();
				
				// 기본정보
				baseItem = new JSONObject();
				baseItem.put( "bldNm",					prpslPrptVO.getBldNm() );
				baseItem.put( "outerFileUrl",			prpslPrptVO.getFileUrl() );
				baseItem.put( "addr",					prpslPrptVO.getAddr() );
				baseItem.put( "roadAddr",				prpslPrptVO.getRoadAddr() );
				baseItem.put( "lat",					prpslPrptVO.getLat() );
				baseItem.put( "lng",					prpslPrptVO.getLng() );
				baseItem.put( "prvArea",				GsntalkUtil.parsePyungToMeters( prpslPrptVO.getPrvArea() ) );
				baseItem.put( "flr",					prpslPrptVO.getFlr() );
				baseItem.put( "allFlr",					prpslPrptVO.getAllFlr() );
				baseItem.put( "psblMovDate",			"INPUT".equals( prpslPrptVO.getPsblMovDayTypCd() ) ? prpslPrptVO.getPsblMovDate() : prpslPrptVO.getPsblMovDayTypNm() );
				baseItem.put( "parkingCarCnt",			prpslPrptVO.getParkingCarCnt() );
				baseItem.put( "facTypDscr",				prpslPrptVO.getFacTypDscr() );
				bldIntrItem.put( "baseItem", baseItem );
				
				// 가격정보
				costItem = new JSONObject();
				costItem.put( "tranTypGbCd",			prpslPrptVO.getTranTypGbCd() );
				costItem.put( "tranTypGbNm",			prpslPrptVO.getTranTypGbNm() );
				costItem.put( "salesCost",				GsntalkUtil.set1000Comma( prpslPrptVO.getSalesCost() ) );
				costItem.put( "dpstAmt",				GsntalkUtil.set1000Comma( prpslPrptVO.getDpstAmt() ) );
				costItem.put( "montRentAmt",			GsntalkUtil.set1000Comma( prpslPrptVO.getMontRentAmt() ) );
				costItem.put( "prmmAmt",				GsntalkUtil.set1000Comma( prpslPrptVO.getPrmmAmt() ) );
				costItem.put( "supprtAmt",				GsntalkUtil.set1000Comma( prpslPrptVO.getSupprtAmt() ) );
				bldIntrItem.put( "costItem", costItem );
				
				// 건물 특장점
				bldIntrItem.put( "bldSpclAdvtgDscr", prpslPrptVO.getBldSpclAdvtgDscr() );
				
				// 요청사항
				bldIntrItem.put( "reqDscr", prpslPrptVO.getReqDscr() );
				
				/* 견적서1 정보 */
					// 대출금액 = 매매가 * ( 대출비율(%) / 100 )
				loanCost = GsntalkMathUtil.multiply( prpslPrptVO.getSalesCost(), GsntalkMathUtil.divide( prpslPrptVO.getLoanRatio1(), 100.0d, 2 ) );
					// 월 이자 상환액 = 대출금액 * ( 대출금리(%) / 100 ) / 12
				monIntrRtnCost = GsntalkMathUtil.divide( GsntalkMathUtil.multiply( loanCost, GsntalkMathUtil.divide( prpslPrptVO.getLoanIntrRatio(), 100.0d, 2 ) ), 12 );
					// 취득세 = 매매가 * ( 취득세율(%) / 100 )
				acqstnTaxCost = GsntalkMathUtil.multiply( prpslPrptVO.getSalesCost(), GsntalkMathUtil.divide( prpslPrptVO.getAcqstnTaxRatio(), 100.0d, 2 ) );
					// 실 투자비용 = 매매가 + 취득세 + 기타비용 - 대출금액 - 보증금액
				realInvestAmt = prpslPrptVO.getSalesCost() + acqstnTaxCost + prpslPrptVO.getEtcCost() - loanCost - prpslPrptVO.getDpstAmt();
					// 월 순수익 = 월 임대료 - 월 이자 상환액
				monProfitAmt = prpslPrptVO.getMontRentAmt() - monIntrRtnCost;
					// 연간 수익 = 월 순수익 * 12
				yearProfitAmt = monProfitAmt * 12;
					// 연 수익률(%) = 연간 수익 / 실 투자비용 * 100
				yearProfiRatio = GsntalkMathUtil.multiply( GsntalkMathUtil.divide( yearProfitAmt, realInvestAmt ), 100 );
					// 실 투자금 회수기간(연) = 실 투자비용 / 월 순수익 / 12
				rtnDueYr = yearProfitAmt < 0 ? 0 : monProfitAmt == 0 ? 0 : GsntalkMathUtil.divide( GsntalkMathUtil.divide( realInvestAmt, monProfitAmt ), 12 );
				
				estate1Item = new JSONObject();
				estate1Item.put( "tranTypGbCd",			prpslPrptVO.getTranTypGbCd() );
				estate1Item.put( "investYn",			prpslPrptVO.getInvestYn() );
				estate1Item.put( "salesCost",			GsntalkUtil.set1000Comma( prpslPrptVO.getSalesCost() ) );
				estate1Item.put( "loanRatio",			prpslPrptVO.getLoanRatio1() );
				estate1Item.put( "loanIntrRatio",		prpslPrptVO.getLoanIntrRatio() );
				estate1Item.put( "loanCost",			GsntalkUtil.set1000Comma( loanCost ) );
				estate1Item.put( "monIntrRtnCost",		GsntalkUtil.set1000Comma( monIntrRtnCost ) );
				estate1Item.put( "acqstnTaxCost",		GsntalkUtil.set1000Comma( acqstnTaxCost ) );
				estate1Item.put( "etcCost",				GsntalkUtil.set1000Comma( prpslPrptVO.getEtcCost() ) );
				estate1Item.put( "dpstAmt",				GsntalkUtil.set1000Comma( prpslPrptVO.getDpstAmt() ) );
				estate1Item.put( "montRentAmt",			GsntalkUtil.set1000Comma( prpslPrptVO.getMontRentAmt() ) );
				estate1Item.put( "realInvestAmt",		GsntalkUtil.set1000Comma( realInvestAmt ) );
				estate1Item.put( "monProfitAmt",		GsntalkUtil.set1000Comma( monProfitAmt ) );
				estate1Item.put( "yearProfitAmt",		GsntalkUtil.set1000Comma( yearProfitAmt ) );
				estate1Item.put( "yearProfiRatio",		yearProfiRatio );
				estate1Item.put( "rtnDueYr",			rtnDueYr );
				bldIntrItem.put( "estate1Item", estate1Item );
				
				/* 견적서2 정보 */
				// 대출금액 = 매매가 * ( 대출비율(%) / 100 )
				loanCost = GsntalkMathUtil.multiply( prpslPrptVO.getSalesCost(), GsntalkMathUtil.divide( prpslPrptVO.getLoanRatio2(), 100.0d, 2 ) );
				// 월 이자 상환액 = 대출금액 * ( 대출금리(%) / 100 ) / 12
				monIntrRtnCost = GsntalkMathUtil.divide( GsntalkMathUtil.multiply( loanCost, GsntalkMathUtil.divide( prpslPrptVO.getLoanIntrRatio(), 100.0d, 2 ) ), 12 );
				// 취득세 = 매매가 * ( 취득세율(%) / 100 )
				acqstnTaxCost = GsntalkMathUtil.multiply( prpslPrptVO.getSalesCost(), GsntalkMathUtil.divide( prpslPrptVO.getAcqstnTaxRatio(), 100.0d, 2 ) );
				// 실 투자비용 = 매매가 + 취득세 + 기타비용 - 대출금액 - 보증금액
				realInvestAmt = prpslPrptVO.getSalesCost() + acqstnTaxCost + prpslPrptVO.getEtcCost() - loanCost - prpslPrptVO.getDpstAmt();
				// 월 순수익 = 월 임대료 - 월 이자 상환액
				monProfitAmt = prpslPrptVO.getMontRentAmt() - monIntrRtnCost;
				// 연간 수익 = 월 순수익 * 12
				yearProfitAmt = monProfitAmt * 12;
				// 연 수익률(%) = 연간 수익 / 실 투자비용 * 100
				yearProfiRatio = GsntalkMathUtil.multiply( GsntalkMathUtil.divide( yearProfitAmt, realInvestAmt ), 100 );
				// 실 투자금 회수기간(연) = 실 투자비용 / 월 순수익 / 12
				rtnDueYr = yearProfitAmt < 0 ? 0 : monProfitAmt == 0 ? 0 : GsntalkMathUtil.divide( GsntalkMathUtil.divide( realInvestAmt, monProfitAmt ), 12 );
				
				estate2Item = new JSONObject();
				estate2Item.put( "tranTypGbCd",			prpslPrptVO.getTranTypGbCd() );
				estate2Item.put( "investYn",			prpslPrptVO.getInvestYn() );
				estate2Item.put( "salesCost",			GsntalkUtil.set1000Comma( prpslPrptVO.getSalesCost() ) );
				estate2Item.put( "loanRatio",			prpslPrptVO.getLoanRatio1() );
				estate2Item.put( "loanIntrRatio",		prpslPrptVO.getLoanIntrRatio() );
				estate2Item.put( "loanCost",			GsntalkUtil.set1000Comma( loanCost ) );
				estate2Item.put( "monIntrRtnCost",		GsntalkUtil.set1000Comma( monIntrRtnCost ) );
				estate2Item.put( "acqstnTaxCost",		GsntalkUtil.set1000Comma( acqstnTaxCost ) );
				estate2Item.put( "etcCost",				GsntalkUtil.set1000Comma( prpslPrptVO.getEtcCost() ) );
				estate2Item.put( "dpstAmt",				GsntalkUtil.set1000Comma( prpslPrptVO.getDpstAmt() ) );
				estate2Item.put( "montRentAmt",			GsntalkUtil.set1000Comma( prpslPrptVO.getMontRentAmt() ) );
				estate2Item.put( "realInvestAmt",		GsntalkUtil.set1000Comma( realInvestAmt ) );
				estate2Item.put( "monProfitAmt",		GsntalkUtil.set1000Comma( monProfitAmt ) );
				estate2Item.put( "yearProfitAmt",		GsntalkUtil.set1000Comma( yearProfitAmt ) );
				estate2Item.put( "yearProfiRatio",		yearProfiRatio );
				estate2Item.put( "rtnDueYr",			rtnDueYr );
				bldIntrItem.put( "estate2Item", estate2Item );
				
				// 건물내부사진 목록조회
				innerImgList = companyProposalDAO.getCompMovPrpslPrptInnerPhotoList( prpslPrptVO.getMovPrpslPrptSeqno() );
				if( GsntalkUtil.isEmptyList( innerImgList ) ) {
					innerImgList = new ArrayList<String>();
				}
				innerImgItems = new JSONArray();
				for( String fileUrl : innerImgList ) {
					innerImgItem = new JSONObject();
					innerImgItem.put( "fileUrl", fileUrl );
					innerImgItems.add( innerImgItem );
				}
				bldIntrItem.put( "innerImgItems", innerImgItems );
				
				bldIntrItems.add( bldIntrItem );
			}
		}
		resMap.put( "bldIntrItems", bldIntrItems );
		
		return resMap;
	}
}