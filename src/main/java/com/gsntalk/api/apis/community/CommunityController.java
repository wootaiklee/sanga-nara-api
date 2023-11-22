package com.gsntalk.api.apis.community;


import com.gsntalk.api.apis.member.MemberService;
import com.gsntalk.api.common.exception.GsntalkAPIException;
import com.gsntalk.api.common.extend.CommonController;
import com.gsntalk.api.common.vo.MemberVO;
import com.gsntalk.api.config.GsntalkCORS;
import com.gsntalk.api.config.GsntalkConstants;
import com.gsntalk.api.config.GsntalkJWTUtil;
import com.gsntalk.api.util.GsntalkAPIResponse;
import com.gsntalk.api.util.GsntalkUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping( value = "/community" )
public class CommunityController  extends CommonController {
    public CommunityController() {
        super( CommunityController.class );
    }

    @Autowired
    private CommunityService communityService;

    @Autowired
    private MemberService memberService;

    /**
     * 2.7.1 커뮤니티 게시글 등록
     * @param request
     * @param response
     * @param imgFiles
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/registerCommunityPostItem", produces = MediaType.APPLICATION_JSON_VALUE )
    public JSONObject registerCommunityPostItem(HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="imgFiles" ) List<MultipartFile> imgFiles ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }

        long memSeqno = memberVO.getMemSeqno();
        String comuNotiYn   = "N";
        String comuTypeCd   = "C";
        String comuPrefCd   = GsntalkUtil.getString( request.getParameter( "comuPrefCd" ) );
        String comuTitle    = GsntalkUtil.getString( request.getParameter( "comuTitle" ) );
        String comuContents = GsntalkUtil.getString( request.getParameter( "comuContents" ) );

        // Validation Check
        if( GsntalkUtil.isEmpty( comuPrefCd ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuPrefCd" );
        }
        if( !GsntalkUtil.isIn( comuPrefCd, "F", "A", "K", "R") ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuPrefCd" );
        }
        if( GsntalkUtil.isEmpty( comuTitle ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuTitle" );
        }
        if( GsntalkUtil.isEmpty( comuContents ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuContents" );
        }
        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 게시글 등록
            communityService.registerCommunityPostItem( memSeqno, comuNotiYn, comuTypeCd, comuPrefCd, comuTitle, comuContents, null, imgFiles );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    //
    /**
     * 2.7.2 커뮤니티 게시글 수정
     * @param request
     * @param response
     * @param imgFiles
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/updateCommunityPostItem", produces = MediaType.APPLICATION_JSON_VALUE )
    public JSONObject updateCommunityPostItem(HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="imgFiles" ) List<MultipartFile> imgFiles ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }

        long memSeqno = memberVO.getMemSeqno();

        long comuSeqno      = GsntalkUtil.getLong( request.getParameter( "comuSeqno" ) );
        String comuNotiYn   = "N";
        String comuPrefCd   = GsntalkUtil.getString( request.getParameter( "comuPrefCd" ) );
        String comuTitle    = GsntalkUtil.getString( request.getParameter( "comuTitle" ) );
        String comuContents = GsntalkUtil.getString( request.getParameter( "comuContents" ) );
        String imgItems     = GsntalkUtil.getString( request.getParameter( "imgItems" ) );


        // Validation Check
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }
        if( !GsntalkUtil.isEmpty( comuPrefCd ) && !GsntalkUtil.isIn( comuPrefCd, "F", "A", "K", "R") ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuPrefCd" );
        }
        if( GsntalkUtil.isEmpty( comuTitle ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuTitle" );
        }
        if( GsntalkUtil.isEmpty( comuContents ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuContents" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 게시글 수정
            communityService.updateCommunityPostItem( memSeqno, comuSeqno, comuNotiYn, comuPrefCd, comuTitle, comuContents, imgItems, imgFiles );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.3 커뮤니티 게시글 삭제
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/deleteCommunityPostItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject deleteCommunityPostItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }

        param                       = super.resetJSONObject( param );
        long memSeqno               = memberVO.getMemSeqno();
        JSONArray comuSeqnoItems    = GsntalkUtil.getJSONArray( param, "comuSeqnoItems" );

        // 필수 요청파라메터 누락
        if( GsntalkUtil.isEmptyArray( comuSeqnoItems ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqnoItems 값 누락" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 삭제처리
            communityService.deleteCommunityPostItems( memSeqno, comuSeqnoItems );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );

    }

    /**
     * 2.7.4 커뮤니티 내 게시글 목록 조회
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/getMyCommunityPostItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getMyCommunityPostItems(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }

        // 파라메터 추출
        param = super.resetJSONObject( param );
        String comuTypeCd               = "C";
        long memSeqno                   = memberVO.getMemSeqno();
        String comuPrefCd               = GsntalkUtil.getString( param.get( "comuPrefCd" ) );
        String comuTitle 				= GsntalkUtil.getString( param.get( "comuTitle" ) );

        JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
        int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
        int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
        int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

        // default pageItem
        pageCnt = ( pageCnt < 5 ? 5 : pageCnt );
        nowPage = ( nowPage < 1 ? 1 : nowPage );
        listPerPage = ( listPerPage < 16 ? 16 : listPerPage );

        // Validation Check
        if( !GsntalkUtil.isEmpty( comuPrefCd ) && !GsntalkUtil.isIn( comuPrefCd, "F", "A", "K", "R") ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
        }

        Map<String, Object> resMap = null;

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 게시글 목록 조회
            resMap = communityService.getCommunityPostItems( memSeqno, comuTypeCd, comuPrefCd, comuTitle, null, null, "S", "D", pageCnt, nowPage, listPerPage );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
    }

    /**
     * 2.7.5 커뮤니티 게시글 상세 조회
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/getCommunityPostInfoItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getCommunityPostInfoItem(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );

        long comuSeqno = GsntalkUtil.getLong( param.get( "comuSeqno" ) );

        // Validation Check
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }

        Map<String, Object> resMap = null;

        try{
            // 커뮤니티 게시글 상세 조회
            resMap = communityService.getCommunityPostInfoItem( comuSeqno );

            // 로그인 토큰 갱신
            if( memberVO != null ) {
                memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
            }
        }catch ( Exception e ){
            throw e;
        }

        return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
    }

    /**
     * 2.7.6 커뮤니티 게시글 목록 조회
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/getCommunityPostItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getCommunityPostItems(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );

        // 파라메터 추출
        param = super.resetJSONObject( param );

        String comuTypeCd               = GsntalkUtil.getString( param.get( "comuTypeCd") );
        String comuPrefCd               = GsntalkUtil.getString( param.get( "comuPrefCd" ) );
        String comuTitle 				= GsntalkUtil.getString( param.get( "comuTitle" ) );

        String srchDateType 			= GsntalkUtil.getString( param.get( "srchDateType" ) );		// 기간검색 ( W 이번 주 / M 이번 달  / Y 1년 )
        String srchVal 					= GsntalkUtil.getString( param.get( "srchVal" ) );

        String orderColumn              = GsntalkUtil.getString( param.get( "orderColumn" ) );      // S : 시퀀스, L : 추천, R : 댓글
        String orderSort                = GsntalkUtil.getString( param.get( "orderSort" ) );

        JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
        int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
        int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
        int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

        // default pageItem
        pageCnt = ( pageCnt < 5 ? 5 : pageCnt );
        nowPage = ( nowPage < 1 ? 1 : nowPage );
        listPerPage = ( listPerPage < 16 ? 16 : listPerPage );

        // Validation Check
        if( !GsntalkUtil.isEmpty( comuTypeCd ) &&  !GsntalkUtil.isIn( comuTypeCd, "C", "G")){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuTypeCd" );
        }
        if( !GsntalkUtil.isEmpty( comuPrefCd ) && !GsntalkUtil.isIn( comuPrefCd, "F", "A", "K", "R", "Y", "M", "N" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER,  "comuPrefCd" );
        }
        if( !GsntalkUtil.isEmpty( srchDateType ) && !GsntalkUtil.isIn( srchDateType, GsntalkConstants.SRCH_DATE_TYPE_WEEK, GsntalkConstants.SRCH_DATE_TYPE_MONTH, GsntalkConstants.SRCH_DATE_TYPE_YEAR ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER );
        }

        if( GsntalkUtil.isEmpty( orderColumn ) ){
            orderColumn = "S";
        }
        if( !GsntalkUtil.isIn( orderColumn, "S", "L", "R" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "orderColumn" );
        }

        if( GsntalkUtil.isEmpty( orderSort ) ){
            orderSort = "D";
        }
        if( !GsntalkUtil.isIn( orderSort, "D", "A" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "orderSort" );
        }

        Map<String, Object> resMap = null;

        try{
            // 게시글 목록 조회
            resMap = communityService.getCommunityPostItems( 0L, comuTypeCd, comuPrefCd, comuTitle, srchDateType, srchVal, orderColumn, orderSort, pageCnt, nowPage, listPerPage );

            // 로그인토큰 갱신
            if( memberVO != null ) {
                memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
            }
        }catch ( Exception e ){
            throw e;
        }

        return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
    }

    /**
     * 2.7.7 커뮤니티 댓글 목록 조회
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/getCommunityReplyItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getCommunityReplyItems(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );

        // 파라메터 추출
        param                           = super.resetJSONObject( param );
        long comuSeqno                  = GsntalkUtil.getLong( param.get( "comuSeqno" ) );
        String orderSort                = GsntalkUtil.getString( param.get( "orderSort" ) );

        JSONObject pageItem				= GsntalkUtil.getJSONObject( param, "pageItem" );
        int pageCnt						= GsntalkUtil.getInteger( pageItem.get( "pageCnt" ) );
        int nowPage						= GsntalkUtil.getInteger( pageItem.get( "nowPage" ) );
        int listPerPage					= GsntalkUtil.getInteger( pageItem.get( "listPerPage" ) );

        // default pageItem
        pageCnt         = ( pageCnt < 5 ? 5 : pageCnt );
        nowPage         = ( nowPage < 1 ? 1 : nowPage );
        listPerPage     = ( listPerPage < 10 ? 10 : listPerPage );

        // Validation Check
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }

        if( GsntalkUtil.isEmpty( orderSort ) ){
            orderSort = "D";
        }

        if( !GsntalkUtil.isIn( orderSort, "D", "A" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "orderSort" );
        }

        Map<String, Object> resMap = null;

        try{
            // 댓글 목록 조회
            resMap = communityService.getCommunityReplyItems( comuSeqno, orderSort, pageCnt, nowPage, listPerPage );

            // 로그인토큰 갱신
            if( memberVO != null ) {
                memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
            }
        }catch ( Exception e ){
            throw e;
        }

        return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
    }

    /**
     * 2.7.8 커뮤니티 댓글 등록
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/registerCommunityReplyItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject registerCommunityReplyItem(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO(request);
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if (memberVO == null) {
            throw new GsntalkAPIException(GsntalkAPIResponse.NEED_TO_LOGIN);
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if (GsntalkUtil.isEmpty(memberVO.getLoginToken())) {
            throw new GsntalkAPIException(GsntalkAPIResponse.FAIL_JWT_VALIDATION);
        }

        long memSeqno                   = memberVO.getMemSeqno();
        long comuSeqno                  = GsntalkUtil.getLong( param.get( "comuSeqno" ) );
        long toMemSeqno                 = GsntalkUtil.getLong( param.get( "toMemSeqno" ) );
        String comuReContents           = GsntalkUtil.getString( param.get( "comuReContents" ) );

        // Validation Check
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }
        if( GsntalkUtil.isEmpty( comuReContents ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuReContents 값 누락" );
        }
        if( comuReContents.length() > 400 ){
            throw new GsntalkAPIException( GsntalkAPIResponse.DATA_TO0_LONG_FOR_PARAMETER, "comuReContents max 400" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 댓글 등록
            communityService.registerCommunityReplyItem( memSeqno, comuSeqno, toMemSeqno, comuReContents );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.9 커뮤니티 댓글 수정
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/updateCommunityReplyItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateCommunityReplyItem(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO(request);
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if (memberVO == null) {
            throw new GsntalkAPIException(GsntalkAPIResponse.NEED_TO_LOGIN);
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if (GsntalkUtil.isEmpty(memberVO.getLoginToken())) {
            throw new GsntalkAPIException(GsntalkAPIResponse.FAIL_JWT_VALIDATION);
        }

        long memSeqno                   = memberVO.getMemSeqno();
        long comuReSeqno                = GsntalkUtil.getLong( param.get( "comuReSeqno" ) );
        long comuSeqno                  = GsntalkUtil.getLong( param.get( "comuSeqno" ) );
        long toMemSeqno                 = GsntalkUtil.getLong( param.get( "toMemSeqno" ) );
        String comuReContents           = GsntalkUtil.getString( param.get( "comuReContents" ) );

        // Validation Check
        if( comuReSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuReSeqno 값 누락" );
        }
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }
        if( GsntalkUtil.isEmpty( comuReContents ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuReContents 값 누락" );
        }
        if( comuReContents.length() > 400 ){
            throw new GsntalkAPIException( GsntalkAPIResponse.DATA_TO0_LONG_FOR_PARAMETER, "comuReContents max 400" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 댓글 수정
            communityService.updateCommunityReplyItem( comuReSeqno, memSeqno, comuSeqno, toMemSeqno, comuReContents );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.10 커뮤니티 댓글 삭제
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/deleteCommunityReplyItem", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject deleteCommunityReplyItem(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO(request);
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if (memberVO == null) {
            throw new GsntalkAPIException(GsntalkAPIResponse.NEED_TO_LOGIN);
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if (GsntalkUtil.isEmpty(memberVO.getLoginToken())) {
            throw new GsntalkAPIException(GsntalkAPIResponse.FAIL_JWT_VALIDATION);
        }

        long memSeqno                   = memberVO.getMemSeqno();
        long comuReSeqno                = GsntalkUtil.getLong( param.get( "comuReSeqno" ) );
        long comuSeqno                  = GsntalkUtil.getLong( param.get( "comuSeqno" ) );

        // Validation Check
        if( comuReSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuReSeqno 값 누락" );
        }
        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 댓글 삭제
            communityService.deleteCommunityReplyItem( comuReSeqno, memSeqno, comuSeqno );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.11 Admin 커뮤니티 공톡 게시글 등록
     * @param request
     * @param response
     * @param imgFiles
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/registerCommunityGsntalkPostItem", produces = MediaType.APPLICATION_JSON_VALUE )
    public JSONObject registerCommunityGsntalkPostItem( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="imgFiles" ) List<MultipartFile> imgFiles  ) throws Exception{
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }
        // 관리자회원이 아님
        if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
        }

        long memSeqno = memberVO.getMemSeqno();

        String comuNotiYn   = GsntalkUtil.getString( request.getParameter( "comuNotiYn" ) );
        String comuTypeCd   = "G";
        String comuPrefCd   = GsntalkUtil.getString( request.getParameter( "comuPrefCd" ) );
        String comuTitle    = GsntalkUtil.getString( request.getParameter( "comuTitle" ) );
        String comuContents = GsntalkUtil.getString( request.getParameter( "comuContents" ) );
        String comuVideoUrl = GsntalkUtil.getString( request.getParameter( "comuVideoUrl" ) );

        // Validation Check
        if( GsntalkUtil.isEmpty( comuNotiYn ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuNotiYn" );
        }
        if( GsntalkUtil.isEmpty( comuPrefCd ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuPrefCd" );
        }
        if( !GsntalkUtil.isIn( comuNotiYn, "N", "Y" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuNotiYn" );
        }
        if( !GsntalkUtil.isIn( comuPrefCd, "Y", "M", "N" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuPrefCd" );
        }
        if( GsntalkUtil.isEmpty( comuTitle ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuTitle" );
        }
        if( GsntalkUtil.isEmpty( comuContents ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuContents" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 게시글 등록
            communityService.registerCommunityPostItem( memSeqno, comuNotiYn, comuTypeCd, comuPrefCd, comuTitle, comuContents, comuVideoUrl, imgFiles );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.12 Admin 커뮤니티 게시글 공지 설정 및 해제
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/updateCommunityPostNoticeYn", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateCommunityPostNoticeYn( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO(request);
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if (memberVO == null) {
            throw new GsntalkAPIException(GsntalkAPIResponse.NEED_TO_LOGIN);
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if (GsntalkUtil.isEmpty(memberVO.getLoginToken())) {
            throw new GsntalkAPIException(GsntalkAPIResponse.FAIL_JWT_VALIDATION);
        }
        // 관리자회원이 아님
        if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
        }


        long memSeqno       = memberVO.getMemSeqno();
        long comuSeqno      = GsntalkUtil.getLong( param.get( "comuSeqno" ) );
        String comuNotiYn   = GsntalkUtil.getString( param.get( "comuNotiYn" ) );

        // Validation Check
        if( comuSeqno == 0L ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }
        if( GsntalkUtil.isEmpty( comuNotiYn ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuNotiYn 값 누락" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 게시글 등록
            communityService.updateCommunityPostNoticeYn( memSeqno, comuSeqno, comuNotiYn );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ) {
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.13 커뮤니티 게시글 좋아요 수 증가
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/updateCommunityPostLikeCnt", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject updateCommunityPostLikeCnt( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param ) throws Exception {
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );

        long memSeqno       = memberVO.getMemSeqno();
        long comuSeqno      = GsntalkUtil.getLong( param.get( "comuSeqno" ) );

        if( comuSeqno == 0L ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuSeqno 값 누락" );
        }

        try{
            // 커뮤니티 게시글 좋아요 수 증가
            communityService.updateCommunityPostLikeCnt( memSeqno, comuSeqno );

            // 로그인토큰 갱신
            if( memberVO != null ) {
                memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
            }
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

    /**
     * 2.7.14 커뮤니티 게시글 공지사항 목록 조회
     * @param request
     * @param response
     * @param param
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/getCommunityPostNoticeItems", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getCommunityPostNoticeItems( HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject param )throws Exception{
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        Map<String, Object> resMap = null;

        try{
            // 공지 목록 조회
            resMap = communityService.getCommunityPostNoticeItems();

            // 로그인 토큰 갱신
            if( memberVO != null ) {
                memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
            }
        }catch ( Exception e ){
            throw e;
        }

        return super.getMapResponse( response, GsntalkJWTUtil.updateJWTToken( request ), resMap );
    }

    /**
     * 2.7.15 Admin 커뮤니티 공지 글 등록
     * @param request
     * @param response
     * @param imgFiles
     * @return
     * @throws Exception
     */
    @GsntalkCORS
    @ResponseBody
    @PostMapping( value = "/registerCommunityPostNotice", produces = MediaType.APPLICATION_JSON_VALUE )
    public JSONObject registerCommunityPostNotice( HttpServletRequest request, HttpServletResponse response, @RequestParam( required = false, value="imgFiles" ) List<MultipartFile> imgFiles  ) throws Exception{
        // JWT 검증
        MemberVO memberVO = GsntalkJWTUtil.getMemberVO( request );
        // 미 로그인 ( 또는 JWT 토큰 누락 )
        if( memberVO == null ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.NEED_TO_LOGIN );
        }
        // JWT 토큰 검증 실패 ( 잘못된 JWT 토큰 )
        if( GsntalkUtil.isEmpty( memberVO.getLoginToken() ) ) {
            throw new GsntalkAPIException( GsntalkAPIResponse.FAIL_JWT_VALIDATION );
        }
        // 관리자회원이 아님
        if( !GsntalkConstants.MEM_TYP_CD_ADMIN_USER.equals( memberVO.getMemTypCd() ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.IS_NOT_ADMIN_USER );
        }



        long memSeqno = memberVO.getMemSeqno();

        String comuNotiYn   = "Y";
        String comuTypeCd   = "G";
        String comuPrefCd   = GsntalkUtil.getString( request.getParameter( "comuPrefCd" ) );
        String comuTitle    = GsntalkUtil.getString( request.getParameter( "comuTitle" ) );
        String comuContents = GsntalkUtil.getString( request.getParameter( "comuContents" ) );
        String comuVideoUrl = GsntalkUtil.getString( request.getParameter( "comuVideoUrl" ) );

        // Validation Check
        if( GsntalkUtil.isEmpty( comuPrefCd ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuPrefCd" );
        }
        if( !GsntalkUtil.isIn( comuPrefCd, "F", "A", "K", "R" ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.INVALID_VALUE_OF_PARAMETER, "comuPrefCd" );
        }
        if( GsntalkUtil.isEmpty( comuTitle ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuTitle" );
        }
        if( GsntalkUtil.isEmpty( comuContents ) ){
            throw new GsntalkAPIException( GsntalkAPIResponse.MISSING_REQUIRED_PARAMETER, "comuContents" );
        }

        try{
            // 로그인 토큰 검증
            memberService.validationLoginTokenExpireDttm( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );

            // 커뮤니티 게시글 등록
            communityService.registerCommunityPostItem( memSeqno, comuNotiYn, comuTypeCd, comuPrefCd, comuTitle, comuContents, comuVideoUrl, imgFiles );

            // 로그인 토큰 갱신
            memberService.renewalLoginToken( memberVO.getMemTypCd(), memberVO.getMemSeqno(), memberVO.getLoginToken() );
        }catch ( Exception e ){
            throw e;
        }

        return super.getSuccessResponse( response, GsntalkJWTUtil.updateJWTToken( request ) );
    }

}
