package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.List;
import java.util.Stack;

import javax.xml.xpath.XPathException;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import static com.github.michaelederaut.basics.xpath2cssselector.Cssify.S_re_attr_val;
import static org.apache.commons.lang3.StringUtils.LF;

import com.github.michaelederaut.basics.RegexpUtils;
import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator.ParsingState;

import regexodus.Pattern;

public class DomNavigator {
	
	// see also: www.digitalocean.com/community/tutorials/how-to-access-elements-in-the-dom
	public static final String S_re_id_simple = "^id\\((['\"])(" + S_re_attr_val + ")(['\"])\\)";
	public static final Pattern P_id_simple = Pattern.compile(S_re_id_simple);
	// public static final String S_re_attr_id = "^\\[@id=(['\"])(" + S_re_attr_val + ")(['\"])\\]";
	public static final String S_re_attr_id = "^\\*\\[@id=(['\"])(" + S_re_attr_val + ")(['\"])\\]";
	public static final Pattern P_attr_id = Pattern.compile(S_re_attr_id);
//	public static final String S_re_attr_multi = "^\\[@(name|class)=(['\"])(" + S_re_attr_val + ")(['\"])\\]";
	public static final String S_re_tag_named = "^(" + Cssify.S_re_tag_named + ")";
	public static final String S_re_class = "^\\*\\[\\@class\\=(['\"])(" + S_re_attr_val + ")(['\"])\\]";
	
	public static final Pattern P_tag_named = Pattern.compile(S_re_tag_named);
	public static final Pattern P_attr_class = Pattern.compile(S_re_class);
	
	boolean B_multi = false; // false: returns a single DOM element
	                 // true: returns an array of DOM elements
	public Stack<Elem> AO_ele_types = new Stack<Elem>();
	public Xpath2DomParsingFailure O_xpath2dom_parsing_failure = null;
	
	public enum StartsFromTopLevel {no, depends, yes};
	
	public enum ParsingState {init, dot1, dot2, slash, initialSlash, doubleSlash, attrSingle, attrMulti, invalid};
	
	public enum EleType {
		ups(int.class, false), // element hierarchy ups such as "../../.." in xpath
		nthChild(int.class, false),  // for later use
		id(String.class, false), 
		className(String.class, true), 
		tagName(String.class, true);
		
		public boolean B_multi;  // may return multiple DOM elements
		public Class<?> T_content;
		
		 EleType(
			final Class<?> PI_T_content,
			final boolean PI_B_multi) {
			 
			this.T_content = PI_T_content; 
			this.B_multi = PI_B_multi;
		} 
	}
	
	public static class Elem {
		public EleType E_type;
		public Object  O_content;
		
		public Elem(
			final EleType PI_E_type,
			final Object PI_O_content) {
			
			IllegalArgumentException E_ill_arg;
			Class<?> T_ele_type;
			String S_msg_1;
			
			T_ele_type = PI_E_type.T_content;
			if (T_ele_type == int.class)  {
				this.O_content = (int)PI_O_content;
			    }
			else if (T_ele_type == String.class) {
				this.O_content = (String)PI_O_content;
			    }
			else {
				S_msg_1 = "Invalid content type: " + E_type.T_content.getName();
				E_ill_arg = new IllegalArgumentException(S_msg_1);
				throw E_ill_arg;
		        }
			this.E_type = PI_E_type;
	        }
	    }
	
	public static class Xpath2DomParsingFailure {
		int           I_pos_f0 = -1;
		ParsingState  E_parsing_state = ParsingState.init;
		String        S_msg = null; 
		Character     C_actually_found = null;
		
		public Xpath2DomParsingFailure () {
			super();
			return;
		}
		
		public Xpath2DomParsingFailure (
				final int PI_I_pos_f0,
				final ParsingState PI_E_parsing_state,
				final String PI_S_msg,
				final Character   PI_C_actually_found) {
			this.I_pos_f0         = PI_I_pos_f0;
			this.E_parsing_state  = PI_E_parsing_state;
			this.S_msg            = PI_S_msg;
			this.C_actually_found = PI_C_actually_found;
			return;
		    }
	    }
	
	protected static boolean FB_has_top_level_show_stopper (final List<Elem> PI_AO_elems_found_so_far) {
		boolean B_retval_top_level_show_stopper = false;
		
		if (PI_AO_elems_found_so_far == null) {
			return B_retval_top_level_show_stopper;
		    }
		
		int I_nbr_elems_found_so_far_f1;
		
        if ((I_nbr_elems_found_so_far_f1 = PI_AO_elems_found_so_far.size()) == 0) {
			return B_retval_top_level_show_stopper;
		    }		
		
        int I_nbr_elems_found_so_far_f0;	
		Elem O_elem_last_found;
		EleType O_ele_type_last_found;
		
		I_nbr_elems_found_so_far_f0	= I_nbr_elems_found_so_far_f1 - 1;
        O_elem_last_found = PI_AO_elems_found_so_far.get(I_nbr_elems_found_so_far_f0);
        O_ele_type_last_found = O_elem_last_found.E_type;
        if (O_ele_type_last_found != EleType.ups) {
           B_retval_top_level_show_stopper = true;
           }
		
		return B_retval_top_level_show_stopper;
	}
	
	/**
	 * accepts<ul>
	 * <li>long form: <code>*[@id='foo']</code></li>
	 * <li>or short: form <code>id('bar')</code></li></ul>
	 * 
	 * @param PI_S_xpath String
	 * @param PO_I_chars_consumed MubableInt
	 * @return String <i>foo</i> resp. <i>bar</i> or null if there is no match.
	 */
	protected  static  String FS_id_to_find(
			final String PI_S_xpath,
			final List<Elem> PI_AO_elems_found_so_far,
			final MutableInt PO_I_chars_consumed) {
		
		RegexpUtils.GroupMatchResult O_grp_match_res;
		XPathException E_xpath;
		Elem O_elem_last_found;
		EleType O_ele_type_last_found;
		
		String S_retval = null;
		if (StringUtils.isEmpty(PI_S_xpath)) {
			return S_retval;
		    }
		
		AssertionError E_assert;
		IllegalArgumentException E_ill_arg;
		int I_nbr_elems_found_so_far_f1, I_nbr_elems_found_so_far_f0;
		
		int I_chars_consumed = 0;
		char C_xp;
		String S_err_msg, S_consumed_chars, AS_numbered_groups[], S_quote_left, S_quote_right;
		
		PO_I_chars_consumed.setValue(I_chars_consumed);
		C_xp = PI_S_xpath.charAt(0);
		O_grp_match_res = null;
		if (C_xp == 'i') {
		    O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_id_simple);
		    }
		else if (C_xp == '*') {
			O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_attr_id);
		    }
		else {
			return S_retval;
		    }
		if (O_grp_match_res == null) {
			S_err_msg = "Regexp-result must not be null here";
			E_assert = new AssertionError(S_err_msg);
			throw E_assert;
		    }
		if (O_grp_match_res.I_array_size_f1 < 4) {
			return S_retval;
		   }
		
		AS_numbered_groups = O_grp_match_res.AS_numbered_groups;
		S_quote_left  = AS_numbered_groups[1];
		S_quote_right = AS_numbered_groups[3];
		if (!S_quote_left.equals(S_quote_right)) {
			S_err_msg = "Invalid xpath: \"" + PI_S_xpath + "\"" + LF +
				    	"Different left quote (" + S_quote_left  + ") and right quote (" + S_quote_right + ") around id value.";
			E_xpath = new XPathException(S_err_msg);
			E_xpath.printStackTrace(System.out);
			return S_retval;
		}
		
		if (PI_AO_elems_found_so_far != null) {
//		   if ((I_nbr_elems_found_so_far_f1 = PI_AO_elems_found_so_far.size()) > 0) {
//               I_nbr_elems_found_so_far_f0	= I_nbr_elems_found_so_far_f1 - 1;
//               O_elem_last_found = PI_AO_elems_found_so_far.get(I_nbr_elems_found_so_far_f0);
//               O_ele_type_last_found = O_elem_last_found.E_type; 
//               if (O_ele_type_last_found != EleType.ups) {
		    if (FB_has_top_level_show_stopper(PI_AO_elems_found_so_far)) {
               return S_retval;
               }
		    }
		S_consumed_chars = AS_numbered_groups[0]; 
		I_chars_consumed = S_consumed_chars.length();
		PO_I_chars_consumed.setValue(I_chars_consumed);
		S_retval         = AS_numbered_groups[2];
			  
		return S_retval;
	}
	
	protected static String FS_get_elements(
			final String PI_S_xpath,
			final MutableBoolean PO_B_by_clazz,
			final MutableInt PO_I_chars_consumed) {
		
		RegexpUtils.GroupMatchResult O_grp_match_res;
		
		String S_retval = null;
		if (StringUtils.isEmpty(PI_S_xpath)) {
			return S_retval;
		    }
		
		IllegalArgumentException E_ill_arg;
		XPathException           E_xpath;
		int I_chars_consumed = 0;
		char C_xp;
		boolean B_possible_clazz;
		String S_err_msg, S_consumed_chars, AS_numbered_groups[], S_quote_left, S_quote_right;
		
		PO_I_chars_consumed.setValue(I_chars_consumed);
		C_xp = PI_S_xpath.charAt(0);
		if (C_xp == '*') {
			B_possible_clazz = true; //*[@class='foo']
		    O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_attr_class);
		    }
		else if (CharUtils.isAsciiAlpha(C_xp)) { // e.g. C_xp == 'b' seraching for all tags //bar
			 B_possible_clazz = false;
			 O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_tag_named);
		     }
		else {
		    return S_retval;
		    }
		
		AS_numbered_groups = O_grp_match_res.AS_numbered_groups;
		if (B_possible_clazz) {
			if (O_grp_match_res.I_array_size_f1 < 4) {
			   return S_retval;
			   }
			S_quote_left = AS_numbered_groups[1];
			S_quote_right = AS_numbered_groups[3];
			if (!S_quote_left.equals(S_quote_right)) {
				S_err_msg = "Invalid xpath: \"" + PI_S_xpath + "\"" + LF +
				"Different left quote (" + S_quote_left + ") and right quote (" + S_quote_right + ") around value of @class.";
				E_xpath = new XPathException(S_err_msg);
			    E_xpath.printStackTrace(System.out);
				return S_retval;
			    }
			PO_B_by_clazz.setValue(true);
			S_retval = AS_numbered_groups[2]; }
		else { 
			if (O_grp_match_res.I_array_size_f1 < 2) {
			  return S_retval;
			  }
			PO_B_by_clazz.setValue(false);
			S_retval = AS_numbered_groups[1];
		    }
		
		S_consumed_chars = AS_numbered_groups[0]; 
		I_chars_consumed = S_consumed_chars.length();
		PO_I_chars_consumed.setValue(I_chars_consumed);
		  
		return S_retval;
	}
	
	public void FV_add(
			final EleType PI_E_type,
			final Object PI_O_content) {
		
		Elem O_elem_new, O_elem_topmost;
		boolean B_push_new_elem;
		int I_nbr_ups_f1;
		
		B_push_new_elem = true;
		if (PI_E_type == EleType.ups) {
		   if (this.AO_ele_types.size() > 0) {
				B_push_new_elem = false;
				O_elem_topmost = this.AO_ele_types.lastElement();
				I_nbr_ups_f1 = (int)(O_elem_topmost.O_content);
				I_nbr_ups_f1++;
				O_elem_topmost.O_content = I_nbr_ups_f1;
		        }
		     }
		if (B_push_new_elem) {
		   O_elem_new = new Elem(PI_E_type, PI_O_content);
		   this.AO_ele_types.push(O_elem_new);
		   }
		return;
	}
	
	public void FV_clear() {
		this.AO_ele_types.clear();
		return;
	}
	
	public static DomNavigator FO_create(
			final CharSequence PI_S_xpath) {
		
		DomNavigator O_retval_dom_navi;
		
		O_retval_dom_navi = FO_create(PI_S_xpath, StartsFromTopLevel.depends);
		return O_retval_dom_navi;
	}
	
	public static DomNavigator FO_create(
			final CharSequence PI_S_xpath, 
			final StartsFromTopLevel PI_E_starts_from_top_level) {
		
		
		int I_len_xpath_f1;
		
		
		DomNavigator O_retval_dom_navi = new DomNavigator();
		
		if ((PI_S_xpath == null) || ((I_len_xpath_f1 = PI_S_xpath.length()) == 0)) {
		   return O_retval_dom_navi;
		   }
		
		IllegalArgumentException E_ill_arg;
		String S_err_msg;
		if (PI_E_starts_from_top_level == null) {
			S_err_msg = "Top level status must not be null here.";
			E_ill_arg = new IllegalArgumentException(S_err_msg);
			throw E_ill_arg;
		    }
		
		MutableInt O_nbr_chars_consumed;
		ParsingState E_parsing_state;
		MutableBoolean B_is_clazz;  // true: //*[class='foo']  - false: tag //bar
		char C_xp;
	    String  S_value, S_remaining_xp;
	    
	    boolean B_convertible_to_dom;
	    int i1, I_len_remaining_f1, I_nbr_hierarchy_ups_f1, I_nbr_chars_consumed_f1 /*, I_len_xpath_f0 */;
	   
	   E_parsing_state = ParsingState.init;
//	   I_len_xpath_f0 = I_len_xpath_f1 - 1;
	   I_nbr_hierarchy_ups_f1 = 0;
	   O_nbr_chars_consumed = new MutableInt();
	   B_is_clazz           = new MutableBoolean();
	   B_convertible_to_dom = false;
	   LOOP_CHARS: for (i1 = 0; i1 < I_len_xpath_f1; i1++) {
		   C_xp = PI_S_xpath.charAt(i1);
		   S_remaining_xp = null;
		   if (E_parsing_state == ParsingState.init) {
			  if (C_xp == '.') {
			      E_parsing_state = ParsingState.dot1;
				  }
			  else if (C_xp == '/') {
			    	E_parsing_state = ParsingState.initialSlash; 
			        }
			  else if ((C_xp == 'i') && (S_value = DomNavigator.FS_id_to_find(PI_S_xpath.toString(), O_retval_dom_navi.AO_ele_types, O_nbr_chars_consumed)) != null) {
				 if (PI_E_starts_from_top_level == StartsFromTopLevel.no) {
					 O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"id('...') is only valid at top level.",
						C_xp);
					    E_parsing_state = ParsingState.invalid; 
				        break LOOP_CHARS; 
			        }
				 else {
					I_nbr_chars_consumed_f1 = O_nbr_chars_consumed.getValue();
				    i1 += I_nbr_chars_consumed_f1;
				    O_retval_dom_navi.FV_add(EleType.id, S_value);
				    E_parsing_state = ParsingState.attrSingle;
				 }}
			  else {
				 O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
					i1,
				    E_parsing_state,
					"xpath does't start with one of the following: . /",
					C_xp);
				E_parsing_state = ParsingState.invalid; 
				break LOOP_CHARS;
					}}
		   else if (E_parsing_state == ParsingState.initialSlash) {
			   if (C_xp == '/') {
				  E_parsing_state = ParsingState.doubleSlash; 
			      }
			   else {
				   O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
							i1,
							E_parsing_state,
							"Initial forward slash must be followed by another /",
							C_xp);
				  E_parsing_state = ParsingState.invalid; 
				  break LOOP_CHARS;  
			     }
		      }
		   else if (E_parsing_state == ParsingState.doubleSlash) {
			   if (S_remaining_xp == null) { // only after a double slash // there is a need for a single usage of the variable S_remaining_xp
				  S_remaining_xp = PI_S_xpath.subSequence(i1, I_len_xpath_f1).toString();
			      }
			   if ((C_xp == '*') && (S_value = DomNavigator.FS_id_to_find(S_remaining_xp, (List<Elem>)null, O_nbr_chars_consumed)) != null) {
				  if ((PI_E_starts_from_top_level == StartsFromTopLevel.no) || (FB_has_top_level_show_stopper(O_retval_dom_navi.AO_ele_types))) {
					  O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"*[@id='...'] is only valid at top level.",
						C_xp);
					    E_parsing_state = ParsingState.invalid; 
				        break LOOP_CHARS; 
				       }
				  else {
					  I_nbr_chars_consumed_f1 = O_nbr_chars_consumed.getValue();
					  i1 += I_nbr_chars_consumed_f1;
					  O_retval_dom_navi.FV_add(EleType.id, S_value);
					  E_parsing_state = ParsingState.attrSingle;
				      }
				  }
			   else if ((S_value = DomNavigator.FS_get_elements(S_remaining_xp, B_is_clazz, O_nbr_chars_consumed)) != null) {
			      I_nbr_chars_consumed_f1 = O_nbr_chars_consumed.getValue();
				  i1 += I_nbr_chars_consumed_f1;
				  if (B_is_clazz.getValue()) {
					  O_retval_dom_navi.FV_add(EleType.className, S_value); 
				      }
				  else {
					  O_retval_dom_navi.FV_add(EleType.tagName, S_value); 
				      }
			      }
			   else {
				   O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"Double slash not followed by one of the following: class, id, tag",
						C_xp);
				 E_parsing_state = ParsingState.invalid; 
				 break LOOP_CHARS;  
			     }
			   E_parsing_state = ParsingState.attrMulti;
		      }
		   else if (E_parsing_state == ParsingState.attrSingle) {
			   if (C_xp == '/') {
				  E_parsing_state = ParsingState.slash; 
				  }
			   else {
				  O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"Attribute id, must be followed by a / or <EOI>.",
						C_xp);
				  E_parsing_state = ParsingState.invalid; 
				  break LOOP_CHARS;  
			      }
		      }
		   else if (E_parsing_state == ParsingState.attrMulti) {
			    O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"Multi-selector (class, tag) must be followed by <EOI>.",
						C_xp);
		       E_parsing_state = ParsingState.invalid; 
			   break LOOP_CHARS; 	
		       }
		   else if (E_parsing_state == ParsingState.dot1) {
			    if (C_xp == '.') {
				   E_parsing_state = ParsingState.dot2;
				   O_retval_dom_navi.FV_add(EleType.ups, 1);
				   }
				else if (C_xp == '/') {
				   	E_parsing_state = ParsingState.slash; 
				    }
				else {
					O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"A dot must be followed by another dot . or a forward slash /",
						C_xp);
				 	E_parsing_state = ParsingState.invalid; 
				    break LOOP_CHARS;
				    }}
		   else if (E_parsing_state == ParsingState.dot2) {
			   if (C_xp == '/') {
				  E_parsing_state = ParsingState.slash; 
				  }
			   else {
				 O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
						i1,
						E_parsing_state,
						"A double dot .. must be followed by a forward slash /",
						C_xp);
				 	E_parsing_state = ParsingState.invalid; 
				   ;
				 E_parsing_state = ParsingState.invalid; 
				 break LOOP_CHARS;
				 }
			  }
		   else if (E_parsing_state == ParsingState.slash) {
			  if (C_xp == '.') {
			    E_parsing_state = ParsingState.dot1;
				}
			  else if (C_xp == '/') {
				  E_parsing_state = ParsingState.doubleSlash;
				  }
			  else { 
				 O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
					i1,
					E_parsing_state,
					"A forward slash / must be followed by a . or anoter forward slash /",
					C_xp);
			     E_parsing_state = ParsingState.invalid; 
				 break LOOP_CHARS;
				 }
			  }
		   } // end for LOOP_CHARS
		 if ((E_parsing_state == ParsingState.dot1) || (E_parsing_state == ParsingState.dot2) ||
		     (E_parsing_state == ParsingState.attrSingle) || (E_parsing_state == ParsingState.attrMulti)) {
			 if (E_parsing_state == ParsingState.attrMulti) {
				O_retval_dom_navi.B_multi = true;
			    }
			 else {
				O_retval_dom_navi.B_multi = false; 
			    }
			 B_convertible_to_dom = true;
			 }
		 else {
			  if (O_retval_dom_navi.O_xpath2dom_parsing_failure == null) {
				 O_retval_dom_navi.O_xpath2dom_parsing_failure = new Xpath2DomParsingFailure(
				 i1,
				 E_parsing_state,
				 "Unexpected end of input.",
				 (Character)null); 
			  }
	         O_retval_dom_navi.AO_ele_types.clear();
		 }
		return O_retval_dom_navi;
	}
	
//	public static class NavigationElem {
//		int I_nbr_ups = -1;
//		
//	}

}
