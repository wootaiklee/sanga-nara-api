package com.gsntalk.api.util;

import java.math.BigDecimal;

public class GsntalkMathUtil {

	public static long multiply( long val1, double val2 ) {
		return new BigDecimal( val1 ).multiply( new BigDecimal( val2 ) ).longValue();
	}
	
	public static long multiply( long val1, int val2 ) {
		return new BigDecimal( val1 ).multiply( new BigDecimal( val2 ) ).longValue();
	}
	
	public static long multiply( int val1, double val2 ) {
		return new BigDecimal( val1 ).multiply( new BigDecimal( val2 ) ).longValue();
	}
	
	public static double multiply( double val1, int val2, int underZeroSize ) {
		return new BigDecimal( val1 ).multiply( new BigDecimal( val2 ) ).setScale( underZeroSize, BigDecimal.ROUND_FLOOR ).doubleValue();
		
		// return new BigDecimal( new BigDecimal( val1 ).multiply( new BigDecimal( val2 ) ).doubleValue() ).divide( new BigDecimal( 1 ), underZeroSize, BigDecimal.ROUND_FLOOR ).doubleValue();
	}
	
	public static long divide( long val1, long val2 ) {
		return new BigDecimal( val1 ).divide( new BigDecimal( val2 ), 0, BigDecimal.ROUND_FLOOR ).longValue();
	}
	
	public static long divide( long val1, double val2 ) {
		if( val2 == 0.0d ) {
			return 0L;
		}
		return new BigDecimal( val1 ).divide( new BigDecimal( val2 ), 0, BigDecimal.ROUND_FLOOR ).longValue();
	}
	
	public static long divide( double val1, long val2 ) {
		if( val2 == 0L ) {
			return val2;
		}
		return new BigDecimal( val1 ).divide( new BigDecimal( val2 ), 0, BigDecimal.ROUND_FLOOR ).longValue();
	}
	
	public static double divide( double val1, double val2, int underZeroSize ) {
		if( val2 == 0.0d ) {
			return val2;
		}
		return new BigDecimal( val1 ).divide( new BigDecimal( val2 ), underZeroSize, BigDecimal.ROUND_FLOOR ).doubleValue();
	}
	
	public static double divide( int val1, long val2, int underZeroSize ) {
		if( val2 == 0L ) {
			return 0.0d;
		}
		return new BigDecimal( val1 ).divide( new BigDecimal( val2 ), underZeroSize, BigDecimal.ROUND_FLOOR ).doubleValue();
	}
}