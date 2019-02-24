package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.HashMap;
import regexodus.Pattern;
import com.github.michaelederaut.basics.RegexpUtils;
import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;
/**
 * 
 * Originally written in Python by [santiycr](https://github.com/santiycr)
 * Ported to Java by Mr. Michael Eder
 *
 */
public class Cssify {
	
	public static class ConversionResult {
		
		public static final String ERR_MSG_NULL_XPATH = "xpath argument must not be a null-string.";
		public String S_value;
		public String S_err_msg;
		 
		public ConversionResult(final String PI_S_value) {
			this(PI_S_value, (String)null);
				return;
			}
		 
	    public ConversionResult(final String PI_S_value, final String PI_S_err_msg) {
	    	this.S_value   = PI_S_value;
	    	this.S_err_msg = PI_S_err_msg;
	     }
	}	
	
protected static final HashMap<String, String> HS_sub_re = new HashMap<String, String>(){{
	put("tag",       "([a-zA-Z][a-zA-Z0-9]{0,10}|\\*)");
	put("attribute", "[.a-zA-Z_:][-\\w:.]*(\\(\\))?");
	put("value",     "\\s*[\\w/:][-/\\w\\s,:;.]*");
}};

protected static final String S_re_validation_parser =
    "({node}" + 
       "(" +
          "^id\\([\\\"\\']?({idvalue}" + HS_sub_re.get("value") + ")[\\\"\\']?\\)" +  // special case! id(idValue)
       "|" +
          "({nav}//?)({tag}" + HS_sub_re.get("tag") + ")" +  // e.g. the tag //div
          "(\\[(" + //  [@id="foo"] and [text()="bar"]
             "({matched}({mattr}\\@?" + HS_sub_re.get("attribute") + ")[\\\"\\']({mvalue}" + HS_sub_re.get("value") + "))[\\\"\\']" +
          "|" + //  [contains(text(), "foo")] or [contains(@id, "bar")]
             "({contained}contains\\(({cattr}\\@?" + HS_sub_re.get("attribute") + "),\\s*[\\\"\\']({cvalue}" + HS_sub_re.get("value") + ")[\\\"\\']\\))" +
          ")\\])?" +
          "(\\[({nth}\\d+)\\])?" +
      ")" +
    ")"; // end of named pattern "node"

protected static final Pattern P_validation_parser = Pattern.compile(S_re_validation_parser);

public static ConversionResult FO_convert(final String PI_S_xpath) {
	
	GroupMatchResult O_grp_match_res;
	String S_err_msg, S_xpath_substr, S_diagnostic_string;
	int I_len_xpath_f1;
	
	
	int I_pos_f0 = 0;
	ConversionResult O_retval_result = new ConversionResult((String)null);
	
	if (PI_S_xpath == null) {
		S_err_msg = ConversionResult.ERR_MSG_NULL_XPATH;
		O_retval_result.S_err_msg = S_err_msg;
		return O_retval_result;
	    }
	
	I_len_xpath_f1 = PI_S_xpath.length();
	LOOP_XPATH_CHARS: while (I_pos_f0 < I_len_xpath_f1) {
		S_xpath_substr = PI_S_xpath.substring(I_pos_f0);
		O_grp_match_res = RegexpUtils.FO_match(S_xpath_substr, P_validation_parser);
		S_diagnostic_string = O_grp_match_res.toString();
		System.out.println("Group-Match result: " + S_diagnostic_string);
		// TODO
	}
	return O_retval_result;
};
}

// TODO