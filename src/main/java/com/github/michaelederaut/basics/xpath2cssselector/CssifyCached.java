package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.michaelederaut.basics.xpath2cssselector.Cssify.ConversionResult;
import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator.StartsFromTopLevel;

public class CssifyCached {
	
	   	public static final int I_max_nbr_elements_dflt = 2047;
		public        int I_max_nbr_elements;
		public        boolean B_try_dom_path;
	//	public        boolean B_starts_from_top_level;
		public        StartsFromTopLevel E_starts_from_top_level;
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
			 this(ConversionResults.I_init_capacity);
		     }
		
		public ConversionResults(final int PI_I_capacity) {
			super(PI_I_capacity < I_init_capacity ? PI_I_capacity : I_init_capacity, (float)0.75, true); // true access-order
			I_max_nbr_elements = PI_I_capacity;
		    }
	 }

	public CssifyCached() {
		this(I_max_nbr_elements_dflt, false); // don't try to convert to DOM path first by default
	    }
	
	public CssifyCached(final boolean PI_B_try_dom_path) {
		this(I_max_nbr_elements_dflt, PI_B_try_dom_path);
		return;
	    }
	
	public CssifyCached(final int  PI_I_max_nbr_elements) {
		this(PI_I_max_nbr_elements, false); // don't try to convert to DOM path first by default
		return;
	    }
	
	public CssifyCached(
			final int  PI_I_max_nbr_elements,
			final boolean PI_B_try_dom_path) {
		this(PI_I_max_nbr_elements, PI_B_try_dom_path, StartsFromTopLevel.depends);  // don't start from top-level
		return;
	    }
	
	public CssifyCached(
			final int  PI_I_max_nbr_elements,
			final boolean PI_B_try_dom_path,
			final StartsFromTopLevel PI_E_starts_from_top_level) {
		
		this.I_max_nbr_elements     = PI_I_max_nbr_elements;
		this.B_try_dom_path         = PI_B_try_dom_path;
		this.E_starts_from_top_level = PI_E_starts_from_top_level;
		this.HS_conversion_res = new ConversionResults(PI_I_max_nbr_elements);
		return;
	    }
	
	public ConversionResult FO_convert(
			final String PI_S_xpath) {
	   ConversionResult O_retval_conversion_result;
	
	   O_retval_conversion_result = this.FO_convert(PI_S_xpath, true); // throw occurring errs == true
	   return O_retval_conversion_result;
	}
	
	public ConversionResult FO_convert(
			final String PI_S_xpath,
			final boolean PI_B_throw_errs) {
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
		
		O_retval_conv_res = Cssify.FO_convert(PI_S_xpath, PI_B_throw_errs, this.B_try_dom_path);
	    this.HS_conversion_res.put(PI_S_xpath, O_retval_conv_res);
		
		return O_retval_conv_res;
	}
}
