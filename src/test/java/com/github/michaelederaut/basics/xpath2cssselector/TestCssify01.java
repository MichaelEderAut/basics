package com.github.michaelederaut.basics.xpath2cssselector;

import com.github.michaelederaut.basics.xpath2cssselector.Cssify;


public class TestCssify01 {
	
	public static void main(final String args[]) {
	
		Cssify.ConversionResult O_conv_res_01, O_conv_res_02, O_conv_res_03, O_conv_res_04, O_conv_res_05;
		String S_xpath, S_conv_value_01,  S_conv_value_02, S_conv_value_03, S_conv_value_04, S_conv_value_05, 
		       S_err_msg_01, S_err_msg_02, S_err_msg_03, S_err_msg_04, S_err_msg_05;
		Boolean B_ok;
		
		S_xpath = "id('xxx')";
		O_conv_res_01 = Cssify.FO_convert(S_xpath);
		S_conv_value_01 = O_conv_res_01.S_value;
		S_err_msg_01    = O_conv_res_01.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_01_1: \'" + S_conv_value_01 + "\' - err_msg_01: " + S_err_msg_01);
		
		S_xpath = "id(\"xxx\")";
		O_conv_res_01 = Cssify.FO_convert(S_xpath);
		S_conv_value_01 = O_conv_res_01.S_value;
		S_err_msg_01    = O_conv_res_01.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_01_2: \'" + S_conv_value_01 + "\' - err_msg_01: " + S_err_msg_01);
		
		O_conv_res_02 = null;
		B_ok = true;
		S_xpath = "//div[id='xxy']";
		try {
			O_conv_res_02 =  Cssify.FO_convert(S_xpath);
		} catch (IllegalArgumentException PI_E_ill_arg) {
			B_ok = false;
			System.out.println("Error trying xpath: " + S_xpath);
			PI_E_ill_arg.printStackTrace(System.err);
		    }
		if (B_ok) {
			S_conv_value_02 = O_conv_res_02.S_value;
			S_err_msg_02    = O_conv_res_02.S_err_msg;
			System.out.println("xpath: \'" + S_xpath + "\' conv_val_02: \'" + S_conv_value_02 + "\' - err_msg_02: " + S_err_msg_02);
		    }
		
		O_conv_res_03 = null;
		B_ok          = true;
		S_xpath = "//div[@id='xxy']";
		O_conv_res_03 =  Cssify.FO_convert(S_xpath);
		S_conv_value_03 = O_conv_res_03.S_value;
		S_err_msg_03    = O_conv_res_03.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_03_1: \'" + S_conv_value_03 + "\' - err_msg_03: " + S_err_msg_03);
		
		O_conv_res_03 = null;
		B_ok          = true;
		S_xpath = "//div[@id=\"xxy\"]";
		O_conv_res_03 =  Cssify.FO_convert(S_xpath);
		S_conv_value_03 = O_conv_res_03.S_value;
		S_err_msg_03    = O_conv_res_03.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_03_2: \'" + S_conv_value_03 + "\' - err_msg_03: " + S_err_msg_03);
		
		O_conv_res_04 = null;
		B_ok          = true;
		S_xpath = "//*[@class='xxy']";
		O_conv_res_04 =  Cssify.FO_convert(S_xpath);
		S_conv_value_04 = O_conv_res_04.S_value;
		S_err_msg_04    = O_conv_res_04.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_04_1: \'" + S_conv_value_04 + "\' - err_msg_04: " + S_err_msg_04);
		
		O_conv_res_04 = null;
		B_ok          = true;
		S_xpath = "//*[@class=\"xxy\"]";
		O_conv_res_04 =  Cssify.FO_convert(S_xpath);
		S_conv_value_04 = O_conv_res_04.S_value;
		S_err_msg_04    = O_conv_res_04.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_04_2: \'" + S_conv_value_04 + "\' - err_msg_04: " + S_err_msg_04);
		
		O_conv_res_05 = null;
		B_ok          = true;
		S_xpath = "//span[@class='xxy']";
		O_conv_res_05 =  Cssify.FO_convert(S_xpath);
		S_conv_value_05 = O_conv_res_05.S_value;
		S_err_msg_05    = O_conv_res_05.S_err_msg;
		System.out.println("xpath: \'" + S_xpath + "\' conv_val_05: \'" + S_conv_value_05 + "\' - err_msg_05: " + S_err_msg_05);
		
		System.out.println("\nTest-Run finished.");
	}	
}
