package com.github.michaelederaut.basics.xpath2cssselector;

import com.github.michaelederaut.basics.xpath2cssselector.Cssify;


public class TestCssify01 {
	
	public static void main(final String args[]) {
	
		Cssify.ConversionResult O_conv_res_01, O_conv_res_02, O_conv_res_03;
		String S_conv_value_01,  S_conv_value_02, S_conv_value_03, 
		       S_err_msg_01, S_err_msg_02, S_err_msg_03;
		
		
		O_conv_res_01 = Cssify.FO_convert("id('xxx')");
		S_conv_value_01 = O_conv_res_01.S_value;
		S_err_msg_01    = O_conv_res_01.S_err_msg;
		System.out.println("conv_val_01: " + S_conv_value_01 + " - err_msg_01: " + S_err_msg_01);
		
		O_conv_res_02 =  Cssify.FO_convert("//div[id='xxy']");
		S_conv_value_02 = O_conv_res_02.S_value;
		S_err_msg_02    = O_conv_res_02.S_err_msg;
		System.out.println("conv_val_02: " + S_conv_value_02 + " - err_msg_02: " + S_err_msg_02);
		
		O_conv_res_03 =  Cssify.FO_convert("//div[@id='xyx']");
		S_conv_value_03 = O_conv_res_03.S_value;
		S_err_msg_03    = O_conv_res_03.S_err_msg;
		System.out.println("conv_val_03: " + S_conv_value_03 + " - err_msg_03: " + S_err_msg_03);
		
	}	
}