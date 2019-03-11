package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import static com.github.michaelederaut.basics.xpath2cssselector.Cssify.S_re_attr_val;
import com.github.michaelederaut.basics.RegexpUtils;

import regexodus.Pattern;

public class DomNavigator {
	
	public static final String S_re_id_simple = "^id\\((['\"])" + S_re_attr_val + "(['\"])\\)";
	public static final Pattern P_id_simple = Pattern.compile(S_re_id_simple);
	public static final String S_re_id_attr = "^\\[@id=(['\"])" + S_re_attr_val + "(['\"])\\]";
	public static final Pattern P_id_attr = Pattern.compile(S_re_id_attr);
	
	boolean B_multi = false; // false: returns a single DOM element
	                 // true: returns an array of DOM elements
	public Stack<Elem> AO_ele_types = new Stack<Elem>();
	
	protected enum ParsingState {init, dot1, dot2, slash, initialSlash, doubleSlash, attrSingle, attrMulti, invalid};
	
	public enum EleType {
		ups(int.class, false), 
		id(String.class, false), 
		classname(String.class, true), 
		tag(String.class, true);
		
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
	
	protected static String FS_id_to_find (
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
		else if (C_xp == '[') {
			O_grp_match_res = RegexpUtils.FO_match(PI_S_xpath, P_id_attr);
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
	
	public void FV_add(
			final EleType PI_E_type,
			final Object PI_O_content) {
		Elem O_elem;
		
		O_elem = new Elem(PI_E_type, PI_O_content);
		this.AO_ele_types.push(O_elem);
		return;
	}
	
	public void FV_clear() {
		this.AO_ele_types.clear();
		return;
	}
	
	public static DomNavigator FO_create(final CharSequence PI_S_xpath) {
		MutableInt O_nbr_chars_consumed;
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
			   S_value = DomNavigator.FS_id_to_find(S_remaining_xp, O_nbr_chars_consumed); 
			   if (S_value != null) {
				  I_nbr_chars_consumed_f1 = O_nbr_chars_consumed.getValue();
				  i1 += I_nbr_chars_consumed_f1;
				  E_parsing_state = ParsingState.attrSingle;
			      }
			   else {
				 E_parsing_state = ParsingState.invalid; 
				 break LOOP_CHARS;  
			     }
		      }
		   else if (E_parsing_state == ParsingState.dot1) {
			    if (C_xp == '.') {
				   E_parsing_state = ParsingState.dot2;
				   I_nbr_hierarchy_ups_f1++;
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
			   }
		 if ((E_parsing_state == ParsingState.dot1) || (E_parsing_state == ParsingState.dot2)) {
				   B_convertible_to_dom = true;
			       }
	          
		return  O_retval_dom_navi;
	}
	
//	public static class NavigationElem {
//		int I_nbr_ups = -1;
//		
//	}

}
