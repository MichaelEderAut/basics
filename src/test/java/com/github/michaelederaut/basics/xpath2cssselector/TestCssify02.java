package com.github.michaelederaut.basics.xpath2cssselector;

import com.github.michaelederaut.basics.xpath2cssselector.CssifyCached;
import static org.apache.commons.lang3.StringUtils.LF;

public class TestCssify02 {
	public static void main(final String args[]) {
		CssifyCached O_cssify_cached;
		DomNavigator O_dom_navi_01;
		 Cssify.DomNavExtendedConversionResult  O_conv_res_01;
	//	Cssify.ConversionResult O_conv_res_01;
		String S_xpath;
		
		O_cssify_cached = new CssifyCached(true); // try DOM
		
		S_xpath = "id('xxx')";
		O_conv_res_01 = (Cssify.DomNavExtendedConversionResult)O_cssify_cached.FO_convert(S_xpath, true);
		O_dom_navi_01 = O_conv_res_01.O_dom_navgator;
		System.out.println("xpath: \'" + S_xpath + "\' c:ss_value: \'" + O_conv_res_01.S_value + "\' - err_msg_01: " +  O_conv_res_01.S_err_msg +
				           "DOM navi size: " + O_dom_navi_01.AO_ele_types.size()  + 
				           ((O_dom_navi_01.O_xpath_parsing_failure != null) ? 
				        	  ("- msg: " + O_dom_navi_01.O_xpath_parsing_failure.S_msg +
				        	  " - pos: " + O_dom_navi_01.O_xpath_parsing_failure.I_pos_f0 +
				        	  " - state: " + O_dom_navi_01.O_xpath_parsing_failure.E_parsing_state.name() +
				        	  " - char expected: " +  O_dom_navi_01.O_xpath_parsing_failure.C_actually_found) : ""));
		return;
	}

}
