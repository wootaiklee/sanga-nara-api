package com.gsntalk.api.util;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GsntalkExcelUtil {

	private SXSSFWorkbook sxssfWb;
	private SXSSFSheet sxssfSheet;
	private SXSSFRow sxssfRow;
	private SXSSFCell sxssfCell;
	private XSSFWorkbook xssfWb;
	private XSSFSheet xssfSheet;
	private XSSFRow xssfRow;
	private XSSFCell xssfCell;
	private String[] titles;
	private String[] fields;
	private int[] sizes;
	private int[] aligns;
	
	/* Excel width */
	public static final int WIDTH_NO		= 10;
	public static final int WIDTH_SMALL		= 20;
	public static final int WIDTH_NORMAL	= 30;
	public static final int WIDTH_WIDE		= 50;
	public static final int WIDTH_MAX		= 70;
	
	public static final int LEFT			= 0;
	public static final int CENTER			= 1;
	public static final int RIGHT			= 2;
	
	public GsntalkExcelUtil( String[] titles, String[] fields, int[] sizes, int[] aligns ) {
		this.titles = titles;
		this.fields = fields;
		this.sizes = sizes;
		this.aligns = aligns;
	}
	
	public SXSSFWorkbook getSXSSFExcelWorkbook( JSONArray dataItems ) {
		this.sxssfWb = new SXSSFWorkbook();
		this.sxssfSheet = sxssfWb.createSheet();
		
		if( GsntalkUtil.isEmptyArray( this.titles ) ) {
			return this.sxssfWb;
		}
		
		// Column-Width Setting
		for( int i = 0; i < sizes.length; i ++ ) {
			sxssfSheet.setColumnWidth( i, sizes[i] * 256 );
		}
		
		// create title row
		XSSFCellStyle headerStyle = this.getHeaderCellStyle();
		sxssfRow = sxssfSheet.createRow( 0 );
		for( int i = 0; i < this.titles.length; i ++ ) {
			sxssfCell = sxssfRow.createCell( i );
			sxssfCell.setCellStyle( headerStyle );
			sxssfCell.setCellValue( this.titles[i] );
		}
		
		XSSFCellStyle bodyStyle = null;
		// create data row
		if( GsntalkUtil.isEmptyArray( dataItems ) ) {
			return this.sxssfWb;
		}
		JSONObject dataItem = null;
		for( int i = 0; i < dataItems.size(); i ++ ) {
			dataItem = (JSONObject)dataItems.get( i );
			
			sxssfRow = sxssfSheet.createRow( i + 1 );
			for( int j = 0; j < this.titles.length; j ++ ) {
				bodyStyle = this.getBodyCellStyle( this.aligns[j] );
				
				sxssfCell = sxssfRow.createCell( j );
				sxssfCell.setCellStyle( bodyStyle );
				sxssfCell.setCellValue( GsntalkUtil.getString( dataItem.get( this.fields[j] ) ) );
			}
		}
		
		return this.sxssfWb;
	}
	
	public XSSFWorkbook getXSSFExcelWorkbook( JSONArray dataItems ) {
		this.xssfWb = new XSSFWorkbook();
		this.xssfSheet = xssfWb.createSheet();
		
		if( GsntalkUtil.isEmptyArray( this.titles ) ) {
			return this.xssfWb;
		}
		
		// Column-Width Setting
		for( int i = 0; i < sizes.length; i ++ ) {
			xssfSheet.setColumnWidth( i, sizes[i] * 256 );
		}
		
		// create title row
		XSSFCellStyle headerStyle = this.getHeaderCellStyle();
		xssfRow = xssfSheet.createRow( 0 );
		for( int i = 0; i < this.titles.length; i ++ ) {
			xssfCell = xssfRow.createCell( i );
			xssfCell.setCellStyle( headerStyle );
			xssfCell.setCellValue( this.titles[i] );
		}
		
		XSSFCellStyle bodyStyle = null;
		// create data row
		if( GsntalkUtil.isEmptyArray( dataItems ) ) {
			return this.xssfWb;
		}
		JSONObject dataItem = null;
		for( int i = 0; i < dataItems.size(); i ++ ) {
			dataItem = (JSONObject)dataItems.get( i );
			
			xssfRow = xssfSheet.createRow( i + 1 );
			for( int j = 0; j < this.titles.length; j ++ ) {
				bodyStyle = this.getBodyCellStyle( this.aligns[j] );
				
				xssfCell = xssfRow.createCell( j );
				xssfCell.setCellStyle( bodyStyle );
				xssfCell.setCellValue( GsntalkUtil.getString( dataItem.get( this.fields[j] ) ) );
			}
		}
		
		return this.xssfWb;
	}
	
	private XSSFCellStyle getHeaderCellStyle() {
		XSSFCellStyle cellStyle = this.xssfWb.createCellStyle();
		byte[] rgb = new byte[] { (byte) 215, (byte) 215, (byte) 215 };
		
		cellStyle.setFillForegroundColor( new XSSFColor( rgb ) );
		cellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
		cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
		cellStyle.setAlignment( HorizontalAlignment.CENTER );
		
		return cellStyle;
	}
	
	private XSSFCellStyle getBodyCellStyle( int align ) {
		XSSFCellStyle cellStyle = this.xssfWb.createCellStyle();
		cellStyle.setVerticalAlignment( VerticalAlignment.CENTER );
		
		// set align
		if( align == GsntalkExcelUtil.LEFT ) {
			cellStyle.setAlignment( HorizontalAlignment.LEFT );
		}else if ( align == GsntalkExcelUtil.CENTER ) {
			cellStyle.setAlignment( HorizontalAlignment.CENTER );
		}else if ( align == GsntalkExcelUtil.RIGHT ) {
			cellStyle.setAlignment( HorizontalAlignment.RIGHT );
		}else {
			cellStyle.setAlignment( HorizontalAlignment.LEFT );
		}
		
		return cellStyle;
	}
}