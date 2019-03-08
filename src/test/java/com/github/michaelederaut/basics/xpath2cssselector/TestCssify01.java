package com.github.michaelederaut.basics.xpath2cssselector;

import com.github.michaelederaut.basics.xpath2cssselector.Cssify;


public class TestCssify01 {
	
	public static void main(final String args[]) {
	
		Cssify.ConversionResult O_conv_res_01, O_conv_res_02, O_conv_res_03;
		String S_xpath, S_conv_value_01,  S_conv_value_02, S_conv_value_03, 
		       S_err_msg_01, S_err_msg_02, S_err_msg_03;
		Boolean B_ok;
		
		S_xpath = "id('xxx')";
		O_conv_res_01 = Cssify.FO_convert(S_xpath);
		S_conv_value_01 = O_conv_res_01.S_value;
		S_err_msg_01    = O_conv_res_01.S_err_msg;
		System.out.println("conv_val_01: " + S_conv_value_01 + " - err_msg_01: " + S_err_msg_01);
		
		O_conv_res_02 = null;
		B_ok = true;
		S_xpath = "//div[id='xxy']";
		try {
			O_conv_res_02 =  Cssify.FO_convert(S_xpath);
		} catch (IllegalArgumentException PI_E_ill_arg) {
			B_ok = false;
			System.out.println("error trying xpath: " + S_xpath);
			PI_E_ill_arg.printStackTrace();
		    }
		if (B_ok) {
			S_conv_value_02 = O_conv_res_02.S_value;
			S_err_msg_02    = O_conv_res_02.S_err_msg;
			System.out.println("conv_val_02: " + S_conv_value_02 + " - err_msg_02: " + S_err_msg_02);
		    }
		
		O_conv_res_03 = null;
		B_ok = true;
		S_xpath = "//div[id='xxy']";
		O_conv_res_03 =  Cssify.FO_convert(S_xpath);
		S_conv_value_03 = O_conv_res_03.S_value;
		S_err_msg_03    = O_conv_res_03.S_err_msg;
		System.out.println("conv_val_03: " + S_conv_value_03 + " - err_msg_03: " + S_err_msg_03);
	}	
}
