package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.michaelederaut.basics.xpath2cssselector.Cssify.ConversionResult;

public class CssifyCached {
	
	   	public static final int I_max_nbr_elements_dflt = 1024;
		public        int I_max_nbr_elements;
		ConversionResults HS_conversion_res;
		
	
	public  class ConversionResults extends LinkedHashMap<String, Cssify.ConversionResult>  {
		public static final int I_init_capacity = 16;

		protected boolean RemoveEldestEntry(Map.Entry<String, Cssify.ConversionResult> PI_O_eledest) {
			
			boolean B_retval_remove;
		
			if (this.size() > CssifyCached.this.I_max_nbr_elements ) {
			   B_retval_remove = true; }
			else {
			   B_retval_remove = false;
			   }
			return B_retval_remove;
		}
		
		public ConversionResults() {
			 this( ConversionResults.I_init_capacity);
		     }
		
		public ConversionResults(final int PI_I_capacity) {
			super(PI_I_capacity < I_init_capacity ? PI_I_capacity : I_init_capacity, (float)0.75, true); // true access-order
			I_max_nbr_elements = PI_I_capacity;
		    }	
	}

	public CssifyCached() {
		this(I_max_nbr_elements_dflt);
	}
	public CssifyCached(final int PI_I_max_nbr_elements) {
		this.I_max_nbr_elements = PI_I_max_nbr_elements;
		this.HS_conversion_res = new ConversionResults(PI_I_max_nbr_elements);
		return;
	    }
	
	public ConversionResult FO_convert(final String PI_S_xpath) {
		ConversionResult O_retval_conv_res;
		String S_err_msg;
		
		if (PI_S_xpath == null) {
		   S_err_msg = ConversionResult.ERR_MSG_NULL_XPATH;;
		   O_retval_conv_res = new ConversionResult(null, S_err_msg);
		   return O_retval_conv_res;
	       }
		
		O_retval_conv_res = this.HS_conversion_res.get(PI_S_xpath);
		if (O_retval_conv_res != null) {
			return O_retval_conv_res;
		    }
		
		O_retval_conv_res = Cssify.FO_convert(PI_S_xpath);
	    this.HS_conversion_res.put(PI_S_xpath, O_retval_conv_res);
		
		return O_retval_conv_res;
	}
	
}
