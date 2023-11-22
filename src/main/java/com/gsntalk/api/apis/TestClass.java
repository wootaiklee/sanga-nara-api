package com.gsntalk.api.apis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;

import com.gsntalk.api.util.GsntalkEncryptor;
import com.gsntalk.api.util.GsntalkUtil;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pdfcrowd.Pdfcrowd;

public class TestClass {

	private SSLContext sc;
	
	public TestClass() {
		try {
			this.sc = SSLContext.getInstance( "SSL" );
			this.sc.init( null, this.createTrustManagers(), new SecureRandom() );
			HttpsURLConnection.setDefaultSSLSocketFactory( sc.getSocketFactory() );
			HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private TrustManager[] createTrustManagers() {
		TrustManager[] tmCerts = new TrustManager[] {
			new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}
				
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
				
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			}
		};
		return tmCerts;
	}
	
	public void download( String fileURL, String saveFile ) throws Exception {
		OutputStream os = new FileOutputStream( new File(saveFile) );
		
		PdfRendererBuilder builder = new PdfRendererBuilder();
		builder.withUri( fileURL );
		builder.toStream( os );
		builder.withW3cDocument(new W3CDom().fromJsoup( Jsoup.parse( new URL( fileURL ), 5000 ) ), fileURL );
		builder.run();
		
		System.out.println( "done.." );
	}
	
	public void downloadPdf()throws Exception {
		Pdfcrowd.HtmlToPdfClient client = new Pdfcrowd.HtmlToPdfClient("demo", "ce544b6ea52a5621fb9d55f8b542d14d");

        // run the conversion and write the result to a file
        client.convertUrlToFile( "https://viewfinder.co.kr/geoboard/home", "C:/Users/j_dev/Downloads/viewfinder.co.kr.pdf");
        
        System.out.println( "done" );
	}
	
	
	public static void main(String[] args) throws Exception {
//		new TestClass().download( "https://viewfinder.co.kr", "C:/Users/j_dev/Downloads/example.pdf" );       
//		new TestClass().downloadPdf();
		
		String pno = "+82 10-4161-1871";
		System.out.println( GsntalkUtil.parseTelnoFormat( pno ) );
	}
}