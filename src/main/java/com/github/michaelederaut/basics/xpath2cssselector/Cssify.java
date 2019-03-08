package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.HashMap;
import javax.xml.xpath.XPathException;

import         org.apache.commons.lang3.StringUtils;
import static  org.apache.commons.lang3.StringUtils.LF;

import regexodus.Pattern;
import com.github.michaelederaut.basics.RegexpUtils;
import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;
import com.github.michaelederaut.basics.RegexpUtils.NamedMatch;

/**
 * 
 * Originally written in Python by [santiycr](https://github.com/santiycr)
 * Ported to Java (and extended) by Mr. Michael Eder
 *
 */
public class Cssify {
	
	public static class ConversionResult {
		
		public static final String ERR_MSG_NULL_XPATH = "xpath argument must not be a null-string.";
		
		public static final String ERR_MSG_UNSUPPORTED_XPATH        = "Unsupported Xpath: ";
		public static final String ERR_MSG_INVALID_XPATH            = "Invalid Xpath: ";
		public static final String ERR_MSG_INV_OR_UNSUPPORTED_XPATH = "Invalid or unsupported Xpath: ";
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

//static final String S_re_validation_parser =
//    "({node}" + 
//       "(" + // special case! id(idValue)
//          "^id\\(({lquote}[\\\"\\']?)({idvalue}" + HS_sub_re.get("value") + ")({rquote}[\\\"\\']?)\\)" +  
//       "|" +
//          "({nav}//?)({tag}" + HS_sub_re.get("tag") + ")" +  // e.g. the tag //div
//          "(\\[(" + //  [@id="foo"] and [text()="bar"]
//             "({matched}({mattr}\\@?" + HS_sub_re.get("attribute") + ")\\=({=lquote}[\\\"\\']?)({mvalue}" + HS_sub_re.get("value") + "))({=rquote}[\\\"\\']?)" +
//          "|" + //  [contains(text(), "foo")] or [contains(@id, "bar")]
//             "({contained}contains\\(({cattr}\\@?" + HS_sub_re.get("attribute") + "),\\s*({=lquote}[\\\"\\']?)({cvalue}" + HS_sub_re.get("value") + ")({=rquote}[\\\"\\']?)\\))" +
//          "|" + //  [starts-with(text(), "foo")] or [starts-with(@id, "bar")]
//             "({startsw}starts\\-with\\(({=cattr}\\@?" + HS_sub_re.get("attribute") + "),\\s*({=lquote}[\\\"\\']?)({=cvalue}" + HS_sub_re.get("value") + ")({=rquote}[\\\"\\']?)\\))" +
//          ")\\])?" +
//          "(\\[({nth}\\d+)\\])?" +
//      ")" +
//    ")"; // end of named pattern "node" 
 
static final String S_re_validation_parser =
    "({node}" + 
       "(" + // special case! id(idValue)
          "^id\\(({lquote}[\\\"\\']?)({idvalue}" + HS_sub_re.get("value") + ")({rquote}[\\\"\\']?)\\)" +  
       "|" +
          "({nav}//?)({tag}" + HS_sub_re.get("tag") + ")" +  // e.g. the tag //div
          "(\\[(" + //  [@id="foo"] and [text()="bar"]
             "({matched}({mattr}\\@?" + HS_sub_re.get("attribute") + ")\\=({=lquote}[\\\"\\']?)({mvalue}" + HS_sub_re.get("value") + "))({=rquote}[\\\"\\']?)" +
          "|" + //  [contains(text(), "foo")] or [contains(@id, "bar")]
             "({contained}contains\\(({cattr}\\@?" + HS_sub_re.get("attribute") + "),\\s*({=lquote}[\\\"\\']?)({cvalue}" + HS_sub_re.get("value") + ")({=rquote}[\\\"\\']?)\\))" +
          "|" + //  [starts-with(text(), "foo")] or [starts-with(@id, "bar")]
             "({startsw}starts\\-with\\(({=cattr}\\@?" + HS_sub_re.get("attribute") + "),\\s*({=lquote}[\\\"\\']?)({=cvalue}" + HS_sub_re.get("value") + ")({=rquote}[\\\"\\']?)\\))" +
          ")\\])?" +
          "(\\[({nth}\\d+|last\\(\\))\\])?" +
      ")" +
    ")"; // end of named pattern "node" 
protected static final Pattern P_validation_parser = Pattern.compile(S_re_validation_parser);


public static ConversionResult FO_convert(
		final String PI_S_xpath) {
	
	ConversionResult O_retval_conversion_result;
	
	O_retval_conversion_result = FO_convert(PI_S_xpath, true);
	return O_retval_conversion_result;
}

public static ConversionResult FO_convert(
		final String PI_S_xpath,
		final boolean PI_B_throw_errs) {
	
	IllegalArgumentException E_ill_arg;
	// XPathException           E_xpath;
	AssertionError              E_assert;
	
	HashMap<String, NamedMatch> HS_named_groups;
	GroupMatchResult O_grp_match_res;
	NamedMatch
	    O_named_match_cattr, O_named_match_contained, O_named_match_starts_with, O_named_match_cvalue, O_named_match_idvalue, 
	    O_named_match_lquote, O_named_match_matched, 
	    O_named_match_mattr, O_named_match_mvalue, O_named_match_nav, O_named_match_node, 
	    O_named_match_nth, O_named_match_rquote, O_named_match_tag;
	String S_err_msg, S_xpath_substr, S_diagnostic_string,
	       S_css_ret, S_attr_ret, S_cattr /* contains*/, S_contained_value, S_cvalue, S_idvalue, S_lquote, S_rquote,
	       S_matched, S_mattr, S_mvalue, S_nav, S_nav_ret, S_node, S_node_css, S_nth, S_nth_ret, S_starts_with_value, S_tag, S_tag_ret;
	int I_len_xpath_f1, I_pos_f0;
	
	
	ConversionResult O_retval_result = new ConversionResult((String)null);
	
	if (PI_S_xpath == null) {
		S_err_msg = ConversionResult.ERR_MSG_NULL_XPATH;
		O_retval_result.S_err_msg = S_err_msg;
		if (PI_B_throw_errs) {
			E_ill_arg = new IllegalArgumentException(S_err_msg);
			throw E_ill_arg;
		}
		return O_retval_result;
	    }
	
	I_pos_f0 = 0;
	I_len_xpath_f1 = PI_S_xpath.length();
	S_css_ret = "";
	LOOP_XPATH_CHARS: while (I_pos_f0 < I_len_xpath_f1) {
		S_attr_ret = "";
		S_xpath_substr = PI_S_xpath.substring(I_pos_f0);
		O_grp_match_res = RegexpUtils.FO_match(S_xpath_substr, P_validation_parser);
//		S_diagnostic_string = O_grp_match_res.toString();
//		System.out.println("Group-Match result: " + S_diagnostic_string);
		if (O_grp_match_res.I_map_size_f1 == 0) {
			S_err_msg = ConversionResult.ERR_MSG_INV_OR_UNSUPPORTED_XPATH + "\'" + PI_S_xpath + "\'" + LF + "No named group found";
			if (PI_B_throw_errs) {
				E_ill_arg = new IllegalArgumentException(S_err_msg);
				throw E_ill_arg;
			    }
			O_retval_result.S_err_msg = S_err_msg;
			return O_retval_result;
		    }
		HS_named_groups = O_grp_match_res.HS_named_groups;
		O_named_match_node = HS_named_groups.get("node");
		if (O_named_match_node == null) {
			S_err_msg = ConversionResult.ERR_MSG_INV_OR_UNSUPPORTED_XPATH + "\'" + PI_S_xpath + "\'" + LF + "node not found";
			if (PI_B_throw_errs) {
				E_ill_arg = new IllegalArgumentException(S_err_msg);
				throw E_ill_arg;
			    }
			O_retval_result.S_err_msg = S_err_msg;
			return O_retval_result;
		    }
		S_node =  O_named_match_node.S_grp_val;
//		System.out.println(O_named_match_node.I_idx_d0 + " - " + O_named_match_node.S_grp_val);
		if (I_pos_f0 == 0) {
			S_nav_ret = "";
		    }
		else {
			if (((O_named_match_nav = HS_named_groups.get("nav")) != null) && (O_named_match_nav.S_grp_val == "//")) {
				S_nav_ret = " ";
			    }
			else {
			    S_nav_ret = " > ";	
			    }
		    }
		if (((O_named_match_tag = HS_named_groups.get("tag")) != null) && StringUtils.equals(O_named_match_tag.S_grp_val, "*")) {
				S_tag_ret = "";
			    }
		else {
		   if ((O_named_match_tag != null) && (O_named_match_tag.S_grp_val != null)) {
			  S_tag_ret = O_named_match_tag.S_grp_val;  
			  }
		   else {
		      S_tag_ret = "";  
			  }
		   }
		
		if (((O_named_match_idvalue = HS_named_groups.get("idvalue")) != null) && ((S_idvalue = O_named_match_idvalue.S_grp_val) != null)) {
		   O_named_match_lquote = HS_named_groups.get("lquote");  // s p e c i a l   c a s e!  id(idValue)
		   if (O_named_match_lquote != null) {
			  S_lquote = O_named_match_lquote.S_grp_val;
			  }
		   else {
			  S_lquote = null;
			  }	
		   if (StringUtils.isEmpty(S_lquote)) {
			  S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				 "id value must start with a quote sign.";
			  if (PI_B_throw_errs) {
				  	E_ill_arg = new IllegalArgumentException(S_err_msg);
				    throw E_ill_arg;
				    }
				    O_retval_result.S_err_msg = S_err_msg;
				    return O_retval_result;
			     }
			O_named_match_rquote = HS_named_groups.get("rquote");
			if (O_named_match_rquote != null) {
			   S_rquote = O_named_match_rquote.S_grp_val;
			   }
			else {
			   S_rquote = null;
			   }
			if (!S_lquote.equals(S_rquote)) {
			   S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				    	    "Different left quote (" + S_lquote + ") and right quote (" + S_rquote + ") around id value.";
			   if (PI_B_throw_errs) {
				  E_ill_arg = new IllegalArgumentException(S_err_msg);
				  throw E_ill_arg;
				  }
				O_retval_result.S_err_msg = S_err_msg;
				return O_retval_result; 
				}
			S_attr_ret = String.format("#%s", S_idvalue.replace(" ", "#"));
		    }
		else if (((O_named_match_matched = HS_named_groups.get("matched")) != null) && (O_named_match_matched.S_grp_val != null)) {
			S_mvalue = null;
			if ((O_named_match_mvalue = HS_named_groups.get("mvalue")) != null) {
				S_mvalue = O_named_match_mvalue.S_grp_val;
			    }
			if (S_mvalue == null) {
				S_err_msg = "Match value must not be null here.";
				E_assert = new AssertionError(S_err_msg);
				throw E_assert;	
			    }

			O_named_match_lquote = HS_named_groups.get("lquote");
			if (O_named_match_lquote != null) {
				S_lquote = O_named_match_lquote.S_grp_val;
				}
			else {
				S_lquote = null;
				}
			if (StringUtils.isEmpty(S_lquote)) {
			   S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				    	   "Attribute value must start with a quotation character (' or \").";
			   if (PI_B_throw_errs) {
				  E_ill_arg = new IllegalArgumentException(S_err_msg);
				  throw E_ill_arg;
				  }
			   O_retval_result.S_err_msg = S_err_msg;
			   return O_retval_result;
			   }
			O_named_match_rquote = HS_named_groups.get("rquote");
			if (O_named_match_rquote != null) {
			   S_rquote = O_named_match_rquote.S_grp_val;
			   }
			else {
			   S_rquote = null;
			   }
			if (!S_lquote.equals(S_rquote)) {
				S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				"Different left quote (" + S_lquote + ") and right quote (" + S_rquote + ") around value \'" + S_mvalue +  "\'";
				if (PI_B_throw_errs) {
				    E_ill_arg = new IllegalArgumentException(S_err_msg);
				    throw E_ill_arg;
				    }
				O_retval_result.S_err_msg = S_err_msg;
				return O_retval_result; 
				}
			S_mattr = null;
			if (((O_named_match_mattr = HS_named_groups.get("mattr")) != null) && (StringUtils.equals((S_mattr = O_named_match_mattr.S_grp_val), "@id"))) {
			   S_attr_ret = String.format("#%s", S_mvalue.replace(" ", "#"));
			   }
			else if (StringUtils.equals(S_mattr, "@class")) {
			   S_attr_ret = String.format(".%s", S_mvalue.replace(" ", "."));
			   }
			else if (StringUtils.equalsAny(S_mattr, "text()", ".")) {
			   S_attr_ret = String.format(":contains(^%s$)", S_mvalue);
			   }
			else if (S_mattr != null) {  // 
			   if (S_mattr.startsWith("@") && (S_mattr.length() > 1)) {
				    S_mvalue = null;
			        if (((O_named_match_mvalue = HS_named_groups.get("mvalue")) != null) && ((S_mvalue = O_named_match_mvalue.S_grp_val) != null)) {
				       if (S_mvalue.indexOf(" ") != -1) { // surround value with quotes if it contains any space character to appear as a single word
					      S_mvalue = "\"" +  S_mvalue + "\"";
				          }
				       S_attr_ret = String.format("[%s=%s]", S_mattr.substring(1), S_mvalue);
			           }
			        }
				else {  // xpath-attribute name doesnt start with a @ or contains only @
					S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF + "Attribute name: \'" + S_mattr + "\' " + 
				                 (S_mattr.equals("@") ? ("too short") : ("doesn't start with @."));
					O_retval_result.S_err_msg = S_err_msg;
					if (PI_B_throw_errs) {
				        E_ill_arg = new IllegalArgumentException(S_err_msg);
				        throw E_ill_arg;
					    }
			        return O_retval_result;	
				    }
			     }
		    }
		else if (((O_named_match_contained = HS_named_groups.get("contained")) != null) && ((S_contained_value = O_named_match_contained.S_grp_val) != null)) {			 
			S_cvalue = null; // initialize attribute value for contains function
			if ((O_named_match_cvalue = HS_named_groups.get("cvalue")) != null) {
				S_cvalue = O_named_match_cvalue.S_grp_val; 
			    }
			if (S_cvalue == null) { // check attribute value for contains function
				S_err_msg = "Contains-atribute value must not be null here.";
				E_assert = new AssertionError(S_err_msg);
				throw E_assert;	
			    }
			 O_named_match_lquote = HS_named_groups.get("lquote");
			 if (O_named_match_lquote != null) {
				S_lquote = O_named_match_lquote.S_grp_val;
				}
			 else {
				S_lquote = null;
				}
			if (StringUtils.isEmpty(S_lquote)) {
			   S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				    	   "Contains value must start with a quotation character (' or \").";
			   if (PI_B_throw_errs) {
				  E_ill_arg = new IllegalArgumentException(S_err_msg);
				  throw E_ill_arg;
				  }
			   O_retval_result.S_err_msg = S_err_msg;
			   return O_retval_result;
			   }
			O_named_match_rquote = HS_named_groups.get("rquote");
			if (O_named_match_rquote != null) {
			   S_rquote = O_named_match_rquote.S_grp_val;
			   }
			else {
			   S_rquote = null;
			   }
			if (!S_lquote.equals(S_rquote)) {
				S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				"Different left quote (" + S_lquote + ") and right quote (" + S_rquote + ") around starts-with value \'" + S_cvalue +  "\'";
				if (PI_B_throw_errs) {
				    E_ill_arg = new IllegalArgumentException(S_err_msg);
				    throw E_ill_arg;
				    }
				O_retval_result.S_err_msg = S_err_msg;
				return O_retval_result; 
				}
			 S_cattr = null;  //  initialization of attribute name for contains function
			 if (((O_named_match_cattr = HS_named_groups.get("cattr")) != null) && 
				StringUtils.startsWith((S_cattr = O_named_match_cattr.S_grp_val), "@")) {
			    S_attr_ret = String.format("[%s*=%s]", S_cattr.replace("@", ""), S_cvalue);
				}
			 else if (StringUtils.equals(S_cattr, "text()")) {
				 S_attr_ret = String.format(":contains(%s)", S_cvalue);  // jQuery required
			     }
		      }
		else if (((O_named_match_starts_with = HS_named_groups.get("startsw")) != null) && ((S_starts_with_value = O_named_match_starts_with.S_grp_val) != null)) {
			 S_cvalue = null; // initialize attribute value for start-with function
			 if ((O_named_match_cvalue = HS_named_groups.get("cvalue")) != null) {
				S_cvalue = O_named_match_cvalue.S_grp_val; 
			    }
			if (S_cvalue == null) { // check attribute value for start-with function
				S_err_msg = "starts-with attribute-value must not be null here.";
				E_assert = new AssertionError(S_err_msg);
				throw E_assert;	
			    }
			
			 O_named_match_lquote = HS_named_groups.get("lquote");
			 if (O_named_match_lquote != null) {
				S_lquote = O_named_match_lquote.S_grp_val;
				}
			 else {
				S_lquote = null;
				}
			if (StringUtils.isEmpty(S_lquote)) {
			   S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				    	   "starts-with value must start with a quotation character (' or \").";
			   if (PI_B_throw_errs) {
				  E_ill_arg = new IllegalArgumentException(S_err_msg);
				  throw E_ill_arg;
				  }
			   O_retval_result.S_err_msg = S_err_msg;
			   return O_retval_result;
			   }
			O_named_match_rquote = HS_named_groups.get("rquote");
			if (O_named_match_rquote != null) {
			   S_rquote = O_named_match_rquote.S_grp_val;
			   }
			else {
			   S_rquote = null;
			   }
			if (!S_lquote.equals(S_rquote)) {
				S_err_msg = "Invalid xpath: \"" + S_xpath_substr + "\"" + LF +
				"Different left quote (" + S_lquote + ") and right quote (" + S_rquote + ") around starts-with value \'" + S_cvalue +  "\'";
				if (PI_B_throw_errs) {
				    E_ill_arg = new IllegalArgumentException(S_err_msg);
				    throw E_ill_arg;
				    }
				O_retval_result.S_err_msg = S_err_msg;
				return O_retval_result; 
				}
			 S_cattr = null;
			 if (((O_named_match_cattr = HS_named_groups.get("cattr")) != null) && 
				StringUtils.startsWith((S_cattr = O_named_match_cattr.S_grp_val), "@")) {
			    S_attr_ret = String.format("[%s^=%s]", S_cattr.replace("@", ""), S_cvalue);
				}
			 else if (StringUtils.equals(S_cattr, "text()")) {
				S_err_msg = ConversionResult.ERR_MSG_UNSUPPORTED_XPATH + "starts-with(text())";
				if (PI_B_throw_errs) {
				  E_ill_arg = new IllegalArgumentException(S_err_msg);
				  throw E_ill_arg;
				  }
			    O_retval_result.S_err_msg = S_err_msg;
			    return O_retval_result; 
			     }
		      }
		else {
			 S_attr_ret = "";
		     }
		
		 if (((O_named_match_nth = HS_named_groups.get("nth")) != null) && ((S_nth = O_named_match_nth.S_grp_val) != null)) {
			 if (S_nth.equals("last()")) {
				 S_nth_ret  = String.format(":last-of-type");
			    }
			 else {
			    S_nth_ret = String.format(":nth-of-type(%s)", S_nth);
			    }
		     }
		 else {
			 S_nth_ret = "";
		     }
		 S_node_css = S_nav_ret + S_tag_ret + S_attr_ret + S_nth_ret;
		 S_css_ret +=  S_node_css;
		 I_pos_f0 += S_node.length();
	     }
	O_retval_result.S_value = S_css_ret;
	return O_retval_result;
    };
}

// TODO