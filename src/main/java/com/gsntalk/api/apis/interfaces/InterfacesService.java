package com.gsntalk.api.apis.interfaces;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonService;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;

@Service( "InterfacesService" )
public class InterfacesService extends CommonService {

	@Autowired
	private InterfacesDAO interfacesDAO;
	
	public InterfacesService() {
		super( InterfacesService.class );
	}
	
	/**
	 * 휴대폰번호 본인확인용 인증번호 알림톡 or SMS 발송 ( 재전송 공용 )
	 * 
	 * @param mobNo
	 * @param email
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	@Transactional( rollbackFor = Exception.class )
	public void sendMobNoVerification( String mobNo, String email )throws Exception {
		// 이메일 값이 있으면 회원정보 이메일 일치여부 확인
		if( !GsntalkUtil.isEmpty( email ) ) {
			String snsGbCd = interfacesDAO.emailMobnoValidation( email, mobNo );
			if( GsntalkUtil.isEmpty( snsGbCd ) ) {
				// 이메일과 휴대폰번호가 일치하는 계정이 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_MATCHED_USER_EMAIL_AND_MOBN_NO );
			}else if( !"E".equals( snsGbCd ) ) {
				// SNS가입회원은 비밀번호를 변경할 수 없음
				throw new GsntalkAPIException( GsntalkAPIResponse.CANNOT_CHANGE_PWD_SNS_USER );
			}
		}
		
		String mobVrfNo = GsntalkUtil.getRandomString( 3, 6 );
		
		// 휴대폰 본인인증 데이터 존재여부 및 추가발송 가능여부 확인
		String canSendTyp = interfacesDAO.getMobileVerificationCnt( mobNo );
		
		if( GsntalkUtil.isEmpty( canSendTyp ) ) {
			// 휴대폰 본인인증 인증코드 등록
			interfacesDAO.registerMobileVerification( mobNo, mobVrfNo );
			
		}else {
			if( "CAN".equals( canSendTyp ) ) {
				// 휴대폰 본인인증 인증코드 갱신
				interfacesDAO.updateMobileVerification( mobNo, mobVrfNo );
			}else {
				// 휴대폰 본인인증 연속발송 제한
				throw new GsntalkAPIException( GsntalkAPIResponse.NOT_YET_SEND_MOB_VRF_NO );
			}
		}
		
		JSONObject templateParam = new JSONObject();
		templateParam.put( "인증번호", mobVrfNo );
		
		// 카카오 알림톡 발송
		JSONObject messageObj = gsntalkIFUtil.sendKakaoMessage( mobNo, GsntalkConstants.KAKAO_MESSAGE_TEMPLATE_MOB_NO_VRF, templateParam );
		JSONArray sendRsltItems = GsntalkUtil.getJSONArray( messageObj, "sendResults" );
		JSONObject sendRsltItem = (JSONObject)sendRsltItems.get( 0 );
		
		String reqId = GsntalkUtil.getString( messageObj.get( "requestId" ) );
		String reqRsltCd = GsntalkUtil.getString( sendRsltItem.get( "resultCode" ) );
		String reqRsltMsg = GsntalkUtil.getString( sendRsltItem.get( "resultMessage" ) );
		
		// 알림톡 메시지 발송이력 등록
		interfacesDAO.registerKakaoMessageSendHist( mobNo, GsntalkConstants.KAKAO_MESSAGE_TEMPLATE_MOB_NO_VRF, templateParam.toJSONString(), reqId, reqRsltCd + " : " + reqRsltMsg );
	}
	
	/**
	 * 휴대폰 본인인증 번호 검증
	 * 
	 * @param mobNo
	 * @param mobVrfNo
	 * @throws Exception
	 */
	public String checkMobNoVerification( String mobNo, String mobVrfNo )throws Exception {
		String vrfTyp = interfacesDAO.mobNoVerification( mobNo, mobVrfNo );
		if( GsntalkUtil.isEmpty( vrfTyp ) ) {
			// 휴대폰 본인인증 실패횟수 추가
			interfacesDAO.updateMobNoVrfFailCnt( mobNo );
			
			// 휴대폰 본인인증 실패
			throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_TO_MOB_NO_VERIFICATION );
		}
		if( "F".equals( vrfTyp ) ) {
			// 휴대폰 본인인증 만료
			throw new GsntalkAPIException( GsntalkAPIResponse.EXPIRED_MOB_VRF_NO );
		}
		
		// 검증 성공인 경우 검증확인토큰 등록
		String vrfCnfrmToken = GsntalkUtil.getRandomString( -1, 32 );
		interfacesDAO.updateMobVrfNoCnfrmToken( mobNo, vrfCnfrmToken );
		
		return vrfCnfrmToken;
	}
	
	/**
	 * 사업자 진위여부 확인 및 상태조회
	 * 
	 * @param bizNo
	 * @param openRegDate
	 * @param reprNm
	 * @return
	 * @throws Exception
	 */
	public JSONObject checkBizValidation( String bizNo, String openRegDate, String reprNm )throws Exception {
		return gsntalkIFUtil.validationBizinfo( bizNo, openRegDate, reprNm );
	}
}