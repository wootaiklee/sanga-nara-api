package com.gsntalk.api.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GsntalkXSSUtil {

	private static final String[] ALLOW_TAGS = {
		"#root",		"html",			"head",			"body",
		"div",			"p",			"b",			"strong",		"i",			"em",		"strike",		"span",		"br",
		"table",		"thead",		"tbody",		"tfoot",		"colgrounp",	"col",		"tr",			"tt",		"th",		"td",
		"ul",			"ol",			"li",			"hr",			"a",			"img",
		"h1",			"h2",			"h3",			"h4",			"h5",			"h6"
	};
	
	private static final String[] ALLOW_ATTRS = {
		"class",		"alt",		"href",		"rel",		"src"
	};
	
	private static final boolean isAllowTag( String tagNm ) {
		for( String allowTag : GsntalkXSSUtil.ALLOW_TAGS ) {
			if( allowTag.equalsIgnoreCase( tagNm ) ) {
				return true;
			}
		}
		return false;
	}
	
	private static final boolean isAllowAttr( String attrNm ) {
		for( String allowAttr : GsntalkXSSUtil.ALLOW_ATTRS ) {
			if( allowAttr.equalsIgnoreCase( attrNm ) ) {
				return true;
			}
		}
		return false;
	}
	
	public static String encodeXss( String orgTxt ) {
		if( GsntalkUtil.isEmpty( orgTxt ) ) {
			return "";
		}
		orgTxt = orgTxt.replaceAll( "\n", "<br>" );
		
		Document htmlDoc = Jsoup.parse( orgTxt );
		Elements elems = htmlDoc.getAllElements();
		Element elem = null;
		Attributes attrs = null;
		
		for( int i = 0; i < elems.size(); i ++ ) {
			elem = elems.get(i);
			
			if( GsntalkXSSUtil.isAllowTag( elem.tagName() ) ) {
				attrs = elem.attributes();
				for( Attribute attr : attrs.asList() ) {
					if( !GsntalkXSSUtil.isAllowAttr( attr.getKey() ) ) {
						elem.removeAttr( attr.getKey() );
					}
				}
			}else {
				elem.remove();
			}
		}
		
		String clearHTML = htmlDoc.toString()
								.replaceAll( "<html>", "" )
								.replaceAll( "</html>", "" )
								.replaceAll( "<head>", "" )
								.replaceAll( "</head>", "" )
								.replaceAll( "<body>", "" )
								.replaceAll( "</body>", "" ).trim();
		
		String[] lines = clearHTML.split( "\n" );
		StringBuffer sb = new StringBuffer();
		for( String line : lines ) {
			sb.append( line.trim() );
		}
		
		return GsntalkUtil.encodingXSS( sb.toString() );
	}
}