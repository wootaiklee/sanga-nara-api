package com.gsntalk.api.util;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Component
@Scope( value = "singleton" )
public class GsntalkS3Util {
	
	private final Logger LOGGER = LoggerFactory.getLogger( this.getClass() );
	
	@Value("${aws.s3.access-key}")
	private String S3_ACCESS_KEY;
	
	@Value("${aws.s3.secret-key}")
	private String S3_SECRET_KEY;

	@Value("${aws.s3.bucket-name}")
	private String S3_BUCKET_NAME;
	
	@Value("${aws.s3.access-url}")
	private String S3_ACCESS_URL;
	
	@Value("${aws.s3.test-upload.images.prefix}")
	private String TEST_IMAGE_UPLOAD_PREFIX;
	
	@Value("${aws.s3.estate-broker-office-upload.files.prefix}")
	private String EST_BRK_OFC_FILES_PREFIX;
	
	@Value("${aws.s3.property-photo-upload.files.prefix}")
	private String PROPERTY_PHOTO_FILES_PREFIX;
	
	@Value("${aws.s3.knwldg-ind-cmplx-photo-upload.files.prefix}")
	private String KNWLDG_IND_CMPLX_PHOTO_FILES_PREFIX;

	@Value("${aws.s3.member-upload.files.prefix}")
	private String MEMBER_FILES_PREFIX;

	@Value("${aws.s3.community-upload.files.prefix}")
	private String COMMUNITY_FILES_PREFIX;
	
	@Value("${aws.s3.suggstn-sales-upload.files.prefix}")
	private String SUGGSTN_SALES_FILES_PREFIX;
	
	@Value("${aws.s3.company-proposal-upload.files.prefix")
	private String COMPANY_PROPOSAL_FILES_PREFIX;
	
	@Value("${aws.s3.temp-upload.files.prefix}")
	private String TEMP_FILES_PREFIX;
	
	@Value("${aws.s3.asset-upload.files.prefix}")
	private String ASSET_FILES_PREFIX;

	private AWSCredentials credentials;
	private AmazonS3 s3Client;
	
	public void createClient() {
		if( credentials == null ) {
			try {
				credentials = new BasicAWSCredentials( GsntalkEncryptor.decrypt( this.S3_ACCESS_KEY ), GsntalkEncryptor.decrypt( this.S3_SECRET_KEY ) );
			}catch( Exception e ) {
				LOGGER.error( "AWSCredentials create Error", e );
			}
		}
		if( s3Client == null ) {
			s3Client = AmazonS3ClientBuilder.standard().withRegion( Regions.AP_NORTHEAST_2 ).withCredentials( new AWSStaticCredentialsProvider( credentials ) ).build();
		}
	}
	
	/**
	 * 테스트용 이미지파일 S3 업로드
	 * 
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	public String uploadTestImageFile( MultipartFile imgFile ) throws Exception {
		this.createClient();
		
		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		
		String suffix = "/" + GsntalkUtil.getServerTime( "%Y%m" );
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getServerTime( "%d%H%i" ) + GsntalkUtil.getRandomString( -1, 8 ) ) + "." + uploadFileFormat;
		
		String key = this.TEST_IMAGE_UPLOAD_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching
		
		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		
		return S3_ACCESS_URL + "/" + key;
	}
	
	/**
	 * 중개사업자 첨부서류 업로드
	 * 
	 * @param memSeqno
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadEstateBrokerOfficeFile( long memSeqno, MultipartFile imgFile ) throws Exception {
		this.createClient();
		
		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		
		String suffix = "/" + memSeqno + "/docs";
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getServerTime( "%d%H%i" ) + GsntalkUtil.getRandomString( -1, 8 ) ) + "." + uploadFileFormat;
		
		String key = this.EST_BRK_OFC_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching
		
		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;
		
		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );
		
		return item;
	}
	
	/**
	 * 매물사진 업로드
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadPropertyImageFile( long estBrkMemOfcSeqno, long prptSeqno, MultipartFile imgFile ) throws Exception {
		this.createClient();
		
		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		
		String suffix = "/" + estBrkMemOfcSeqno + "/property/photo/" + prptSeqno;
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getRandomString( -1, 16 ) ) + "." + uploadFileFormat;
		
		String key = this.PROPERTY_PHOTO_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching
		
		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;
		
		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );
		
		return item;
	}
	
	/**
	 * 지식산업센터 사진 업로드
	 * 
	 * @param estBrkMemOfcSeqno
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadKnwldgIndCmplxmageFile( long knwldgIndCmplxSeqno, String knwldgCmplxAtchImgTypCd, MultipartFile imgFile ) throws Exception {
		this.createClient();
		
		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		
		String suffix = "/" + knwldgIndCmplxSeqno + "/" + knwldgCmplxAtchImgTypCd;
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getServerTime( "%m%d" ) + GsntalkUtil.getRandomString( -1, 12 ) ) + "." + uploadFileFormat;
		
		String key = this.KNWLDG_IND_CMPLX_PHOTO_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching
		
		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;
		
		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );
		
		return item;
	}

	/**
	 * 프로필 사진 업로드
	 * 
	 * @param memSeqno
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadMemberProfileImageFile( long memSeqno, MultipartFile imgFile ) throws Exception {
		this.createClient();

		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();

		String suffix = "/" + memSeqno + "/" + "profile";
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getServerTime( "%m%d" ) + GsntalkUtil.getRandomString( -1, 12 ) ) + "." + uploadFileFormat;

		String key = this.MEMBER_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching

		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;

		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );

		return item;
	}


	/**
	 * 커뮤니티 사진 업로드
	 * 
	 * @param comuSeqno
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadCommunityImageFile( long comuSeqno, MultipartFile imgFile ) throws Exception {
		this.createClient();

		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();

		String suffix = "/" + comuSeqno;
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getServerTime( "%m%d" ) + GsntalkUtil.getRandomString( -1, 12 ) ) + "." + uploadFileFormat;

		String key = this.COMMUNITY_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching

		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;

		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );

		return item;
	}

	/**
	 * 임시 이미지파일 업로드
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadTmpImgFile( MultipartFile imgFile ) throws Exception {
		this.createClient();

		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();

		String suffix = "/images";
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getRandomString( -1, 4 ) + GsntalkUtil.getServerTime( "%Y%m%d%H%i%s" ) + GsntalkUtil.getRandomString( -1, 4 ) ) + "." + uploadFileFormat;

		String key = this.TEMP_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=14400" );		// 4 hour caching

		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;

		JSONObject item = new JSONObject();
		item.put( "tmpFileNm", saveFileNm );
		item.put( "tmpFileUrl", fileUrl );

		return item;
	}
	
	/**
	 * 자산 첨부파일 업로드
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings( "unchecked" )
	public JSONObject uploadAssetAtchFile( long assetSeqno, MultipartFile imgFile ) throws Exception {
		this.createClient();
		
		String orgFileNm = imgFile.getOriginalFilename();
		String uploadFileFormat = orgFileNm.substring( orgFileNm.lastIndexOf( "." ) + 1 ).toLowerCase();
		
		String suffix = "/" + assetSeqno;
		String saveFileNm = GsntalkEncryptor.sha512( GsntalkUtil.getRandomString( -1, 4 ) + GsntalkUtil.getServerTime( "%Y%m%d%H%i%s" ) + GsntalkUtil.getRandomString( -1, 4 ) ) + "." + uploadFileFormat;
		
		String key = this.ASSET_FILES_PREFIX + suffix + "/" + saveFileNm;
		ObjectMetadata oMeta = new ObjectMetadata();
		oMeta.setContentType( imgFile.getContentType() );
		oMeta.setContentLength( imgFile.getSize() );
		oMeta.setCacheControl( "public, max-age=15552000" );		// 180 day caching
		
		s3Client.putObject( this.S3_BUCKET_NAME, key, imgFile.getInputStream(), oMeta );
		String fileUrl = this.S3_ACCESS_URL + "/" + key;
		
		JSONObject item = new JSONObject();
		item.put( "orgFileNm", orgFileNm );
		item.put( "saveFileNm", saveFileNm );
		item.put( "fileUrl", fileUrl );
		
		return item;
	}
	
	/**
	 * 임시 이미지파일 -> 추천분양 대표이미지 경로로 이동
	 * 
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	public String moveTmpFileToSuggstnRepFile( long suggstnSalesSeqno, String repTmpFileNm )throws Exception {
		this.createClient();
		
		String fromSuffix = "/images";
		String fromKey = this.TEMP_FILES_PREFIX + fromSuffix + "/" + repTmpFileNm;
		
		String destinationSuffix = "/rep" + "/" + GsntalkUtil.getServerTime( "%Y_%m" ) + "/" + suggstnSalesSeqno;
		String destinationKey = this.SUGGSTN_SALES_FILES_PREFIX + destinationSuffix + "/" + repTmpFileNm;
		
		try {
			s3Client.copyObject( this.S3_BUCKET_NAME, fromKey, this.S3_BUCKET_NAME, destinationKey );
			s3Client.deleteObject( this.S3_BUCKET_NAME, fromKey );
		}catch( Exception e ) {}
		
		return this.S3_ACCESS_URL + "/" + destinationKey;
	}
	
	/**
	 * 임시 이미지파일 -> 추천분양 교육자료 경로로 이동
	 * 
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	public String moveTmpFileToSuggstnEduFile( long suggstnSalesSeqno, String eduTmpFileNm )throws Exception {
		this.createClient();
		
		String fromSuffix = "/images";
		String fromKey = this.TEMP_FILES_PREFIX + fromSuffix + "/" + eduTmpFileNm;
		
		String destinationSuffix = "/edu" + "/" + GsntalkUtil.getServerTime( "%Y_%m" ) + "/" + suggstnSalesSeqno;
		String destinationKey = this.SUGGSTN_SALES_FILES_PREFIX + destinationSuffix + "/" + eduTmpFileNm;
		
		try {
			s3Client.copyObject( this.S3_BUCKET_NAME, fromKey, this.S3_BUCKET_NAME, destinationKey );
			s3Client.deleteObject( this.S3_BUCKET_NAME, fromKey );
		}catch( Exception e ) {}
		
		return this.S3_ACCESS_URL + "/" + destinationKey;
	}
	
	/**
	 * 임시 이미지파일 -> 추천분양 층별도면 경로로 이동
	 * 
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	public String moveTmpFileToSuggstnFloorPlanFile( long dongSeqno, long suggstnSalesSeqno, String floorTmpFileNm )throws Exception {
		this.createClient();
		
		String fromSuffix = "/images";
		String fromKey = this.TEMP_FILES_PREFIX + fromSuffix + "/" + floorTmpFileNm;
		
		String destinationSuffix = "/flr-plan" + "/" + GsntalkUtil.getServerTime( "%Y_%m" ) + "/" + suggstnSalesSeqno + "/" + dongSeqno;
		String destinationKey = this.SUGGSTN_SALES_FILES_PREFIX + destinationSuffix + "/" + floorTmpFileNm;
		
		try {
			s3Client.copyObject( this.S3_BUCKET_NAME, fromKey, this.S3_BUCKET_NAME, destinationKey );
			s3Client.deleteObject( this.S3_BUCKET_NAME, fromKey );
		}catch( Exception e ) {}
		
		return this.S3_ACCESS_URL + "/" + destinationKey;
	}
	
	/**
	 * 임시 이미지파일 -> 기업이전 제안 사진 경로로 이동
	 * 
	 * @param compSeqno
	 * @param movPrpslPrptSeqno
	 * @param repTmpFileNm
	 * @return
	 * @throws Exception
	 */
	public String moveTmpFileToCompanyProposalPhotoFile( long compSeqno, long movPrpslPrptSeqno, String tmpFileNm )throws Exception {
		this.createClient();
		
		String fromSuffix = "/images";
		String fromKey = this.TEMP_FILES_PREFIX + fromSuffix + "/" + tmpFileNm;
		
		String destinationSuffix = compSeqno + "/" + movPrpslPrptSeqno;
		String destinationKey = this.COMPANY_PROPOSAL_FILES_PREFIX + destinationSuffix + "/" + tmpFileNm;
		
		try {
			s3Client.copyObject( this.S3_BUCKET_NAME, fromKey, this.S3_BUCKET_NAME, destinationKey );
			s3Client.deleteObject( this.S3_BUCKET_NAME, fromKey );
		}catch( Exception e ) {}
		
		return this.S3_ACCESS_URL + "/" + destinationKey;
	}
}