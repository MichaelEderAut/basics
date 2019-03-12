package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.Stack;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import static com.github.michaelederaut.basics.xpath2cssselector.Cssify.S_re_attr_val;
import com.github.michaelederaut.basics.RegexpUtils;

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
	
	protected enum ParsingState {init, dot1, dot2, slash, initialSlash, doubleSlash, attrSingle, attrMulti, invalid};
	
	public enum EleType {
		ups(int.class, false), 
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
			if (T_ele_type == int.class) {
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
	        }
	    }
	
	protected static String FS_id_to_find(
			final String PI_S_xpath,
			final MutableInt PO_I_chars_consumed) {
		
		RegexpUtils.GroupMatchResult O_grp_match_res;
		
		String S_retval = null;
		if (StringUtils.isEmpty(PI_S_xpath)) {
			return S_retval;
		    }
		
		IllegalArgumentException E_ill_arg;
		int I_chars_consumed = 0;
		char C_xp;
		String S_err_msg, S_consumed_chars, AS_numbered_groups[];
		
		PO_I_chars_consumed.setValue(I_chars_consumed);
		C_xp = PI_S_xpath.charAt(0);
		O_grp_match_res = null;
		if (C_xp == 'i') {
		    O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_id_simple);
		    }
		else if (C_xp == '*') {
			O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_attr_id);
		    }
		if (O_grp_match_res == null) {
			return S_retval;
		    }
		if (O_grp_match_res.I_array_size_f1 < 4) {
			return S_retval;
		   }
		
		AS_numbered_groups = O_grp_match_res.AS_numbered_groups;
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
		int I_chars_consumed = 0;
		char C_xp;
		boolean B_possible_clazz;
		String S_err_msg, S_consumed_chars, AS_numbered_groups[];
		
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
	
	public static DomNavigator FO_create(final CharSequence PI_S_xpath) {
		MutableInt O_nbr_chars_consumed;
		MutableBoolean B_is_clazz;  // true: //*[class='foo']  - false: tag //bar
		int I_len_xpath_f1, I_nbr_chars_consumed_f1;
		char C_xp;
		String S_remaining_xp, S_value;
		boolean B_convertible_to_dom;
		

		DomNavigator O_retval_dom_navi = new DomNavigator();
		
		if ((PI_S_xpath == null) || ((I_len_xpath_f1 = PI_S_xpath.length()) == 0)) {
		   return O_retval_dom_navi;
		    }
		
	    int i1, I_len_remaining_f1, I_nbr_hierarchy_ups_f1 /*, I_len_xpath_f0 */;
	    ParsingState E_parsing_state;
	   
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
				 else {
					E_parsing_state = ParsingState.invalid; 
					break LOOP_CHARS;
					}}
		   else if (E_parsing_state == ParsingState.initialSlash) {
			   if (C_xp == '/') {
				  E_parsing_state = ParsingState.doubleSlash; 
			      }
			   else {
				 E_parsing_state = ParsingState.invalid; 
				 break LOOP_CHARS;  
			     }
		      }
		   else if (E_parsing_state == ParsingState.doubleSlash) {
			   if (S_remaining_xp == null) {
				  S_remaining_xp = PI_S_xpath.subSequence(i1, I_len_xpath_f1).toString();
			      }
			   if ((S_value = DomNavigator.FS_id_to_find(S_remaining_xp, O_nbr_chars_consumed)) != null) {
			  
				  I_nbr_chars_consumed_f1 = O_nbr_chars_consumed.getValue();
				  i1 += I_nbr_chars_consumed_f1;
				
				  O_retval_dom_navi.FV_add(EleType.id, S_value);
				  E_parsing_state = ParsingState.attrSingle;
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
				  E_parsing_state = ParsingState.invalid; 
				  break LOOP_CHARS;  
			      }
		      }
		   else if (E_parsing_state == ParsingState.attrMulti) {
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
				 	E_parsing_state = ParsingState.invalid; 
				    break LOOP_CHARS;
				    }}
		   else if (E_parsing_state == ParsingState.dot2) {
			   if (C_xp == '/') {
				  E_parsing_state = ParsingState.slash; 
				  }
			   else {
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
	         O_retval_dom_navi.AO_ele_types.clear();
		 }
		return O_retval_dom_navi;
	}
	
//	public static class NavigationElem {
//		int I_nbr_ups = -1;
//		
//	}

}
