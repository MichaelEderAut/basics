package com.github.michaelederaut.basics;

import com.github.michaelederaut.basics.RegexpUtils;

import regexodus.Pattern;
public class TestRegexpUtils {

	private static final String I_STRING = "i";
	
	
	public static void main(String[] args) {
	
	Pattern P_dummy;
	String S_msg_1, S_msg_2, S_res_flags;
	int I_res_flags;
	
	I_res_flags = RegexpUtils.FI_parse_flags(I_STRING);
	S_msg_1 = "String-argument: \'" + I_STRING + "\' Integer result: " + I_res_flags;
	System.out.println(S_msg_1);
    
	 P_dummy = new Pattern("", I_res_flags);
	 S_res_flags = P_dummy.flagsAsString();
	 S_msg_2 = "Int-argument: " + I_res_flags + " String result: \'" + S_res_flags + "\'";
	 System.out.println(S_msg_2);
	}

}
