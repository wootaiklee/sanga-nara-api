package com.gsntalk.api.apis.interfaces;

import org.springframework.stereotype.Repository;

import com.gsntalk.api.common.extend.CommonDAO;
import com.gsntalk.api.common.vo.KakaoMessageVO;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.util.GsntalkEncryptor;

@Repository( "com.gsntalk.api.apis.interfaces.InterfacesDAO" )
public class InterfacesDAO extends CommonDAO {

	public InterfacesDAO() {
		super( InterfacesDAO.class );
	}
	
	/**
	 * 휴대폰 본인인증 데이터 존재여부 및 추가발송 가능여부 확인
	 * 
	 * @param mobNo
	 * @return
	 * @throws Exception
	 */
	public String getMobileVerificationCnt( String mobNo )throws Exception {
		return sqlSession.selectOne( "InterfacesMapper.getMobileVerificationCnt", mobNo );
	}
	
	/**
	 * 휴대폰 본인인증 인증코드 등록
	 * 
	 * @param mobNo
	 * @param mobVrfNo
	 */
	public void registerMobileVerification( String mobNo, String mobVrfNo )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMobNo( mobNo );
		memberVO.setMobVrfNo( mobVrfNo );
		
		sqlSession.insert( "InterfacesMapper.registerMobileVerification", memberVO );
	}
	
	/**
	 * 휴대폰 본인인증 인증코드 갱신
	 * 
	 * @param mobNo
	 * @param mobVrfNo
	 */
	public void updateMobileVerification( String mobNo, String mobVrfNo )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMobNo( mobNo );
		memberVO.setMobVrfNo( mobVrfNo );
		
		sqlSession.update( "InterfacesMapper.updateMobileVerification", memberVO );
	}
	
	/**
	 * 휴대폰 본인인증 번호 검증
	 * 
	 * @param mobNo
	 * @param mobVrfNo
	 * @return
	 * @throws Exception
	 */
	public String mobNoVerification( String mobNo, String mobVrfNo )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMobNo( mobNo );
		memberVO.setMobVrfNo( mobVrfNo );
		
		return sqlSession.selectOne( "InterfacesMapper.mobNoVerification", memberVO );
	}
	
	/**
	 * 휴대폰 본인인증 실패횟수 추가
	 * 
	 * @param mobNo
	 * @throws Exception
	 */
	public void updateMobNoVrfFailCnt( String mobNo )throws Exception {
		sqlSession.update( "InterfacesMapper.updateMobNoVrfFailCnt", mobNo );
	}
	
	/**
	 * 휴대폰 본인인증 검증확인토큰 등록
	 * 
	 * @param mobNo
	 * @param vrfCnfrmToken
	 * @throws Exception
	 */
	public void updateMobVrfNoCnfrmToken( String mobNo, String vrfCnfrmToken )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setMobNo( mobNo );
		memberVO.setVrfCnfrmToken( vrfCnfrmToken );
		
		sqlSession.update( "InterfacesMapper.updateMobVrfNoCnfrmToken", memberVO );
	}
	
	/**
	 * 알림톡 메시지 발송이력 등록
	 * 
	 * @param rcpntMobNo
	 * @param msgTmpltCd
	 * @param msgMappngVal
	 * @param reqId
	 * @param reqRslt
	 * @throws Exception
	 */
	public void registerKakaoMessageSendHist( String rcpntMobNo, String msgTmpltCd, String msgMappngVal, String reqId, String reqRslt )throws Exception {
		KakaoMessageVO kakaoMessageVO = new KakaoMessageVO();
		kakaoMessageVO.setRcpntMobNo( rcpntMobNo );
		kakaoMessageVO.setMsgTmpltCd( msgTmpltCd );
		kakaoMessageVO.setMsgMappngVal( msgMappngVal );
		kakaoMessageVO.setReqId( reqId );
		kakaoMessageVO.setReqRslt( reqRslt );
		
		sqlSession.insert( "InterfacesMapper.registerKakaoMessageSendHist", kakaoMessageVO );
	}
	
	/**
	 * 이메일/휴대폰번호 검증
	 * 
	 * @param email
	 * @param mobNo
	 * @return
	 * @throws Exception
	 */
	public String emailMobnoValidation( String email, String mobNo )throws Exception {
		MemberVO memberVO = new MemberVO();
		memberVO.setEmail( email );
		memberVO.setMobNo( GsntalkEncryptor.encrypt( mobNo ) );
		
		return sqlSession.selectOne( "InterfacesMapper.emailMobnoValidation", memberVO );
	}
}