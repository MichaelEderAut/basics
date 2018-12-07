package com.github.michaelederaut.basics;

import static org.apache.commons.lang3.StringUtils.LF;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.BiConsumer;

import regexodus.Pattern;
import regexodus.Matcher;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.github.michaelederaut.basics.StaticMethodUtils;

/**
 * 
 * @author Lee
 * @see <a href="http://www.regexplanet.com/advanced/java/index.html">RegexPlanet</a>,
 * <a href="https://en.wikipedia.org/wiki/Comparison_of_regular_expression_engines">Comparison of Regexp-Engines</a>
 */
public class RegexpUtils {

	public static final  String PARSE_FLAGS = "parseFlags";
	
	  public static int FI_parse_flags(final String PI_S_flags) {
		  
		  RuntimeException E_rt;
	      String S_msg_1;
		  int I_retval;
		  Object O_flags;
		  
		  I_retval = 0;
		  
		  try {
			  O_flags = StaticMethodUtils.invokeStaticMethod(
						Pattern.class,
						true, // force access
						PARSE_FLAGS,
						new Object[] {PI_S_flags},
				        new Class[]  {PI_S_flags.getClass()});		
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException PI_E_inv_target) {
				S_msg_1 = "Unable to invoke static private method \'" + 
						Pattern.class.getName() + "." + PARSE_FLAGS + "(\"" +
						PI_S_flags + "\")";
				E_rt = new RuntimeException(S_msg_1, PI_E_inv_target);
				throw E_rt;
			}
		    I_retval = (int)O_flags;
			return I_retval;
	  }
	
	  public static class RegexKey {
		   public String S_regexp;
		   public int I_flags;
	 
	  public RegexKey (final String PI_S_regexp) {
		  this.S_regexp = PI_S_regexp;
		  }
	  
	  public RegexKey (
			  final String PI_S_regexp,
			  final int    PI_I_flags) {
		  
		this.S_regexp = PI_S_regexp;  
		this.I_flags  = PI_I_flags;  
	     }
	  
	  public RegexKey (
			  final String PI_S_regexp,
			  final String PI_S_flags) {
		  
		this.S_regexp = PI_S_regexp;  
		this.I_flags  = FI_parse_flags(PI_S_flags);  
	     }
	  
	  @Override
	  public boolean equals(final Object PI_O_regex_key) {
		  
		  RegexKey O_regex_key;   
		 
		  boolean B_retval_equals;
		  int I_flags_this, I_flags_other;
		  
		  B_retval_equals = false;
		  
		  if (!(PI_O_regex_key instanceof RegexKey)) {
			  return B_retval_equals; 
		      }
		  O_regex_key = (RegexKey)PI_O_regex_key;
		  if (!(this.S_regexp.equals(O_regex_key.S_regexp))) {
			  return B_retval_equals;
		      }
	
		 I_flags_this  = this.I_flags;
	     I_flags_other = O_regex_key.I_flags;
		 if (I_flags_this == I_flags_other) {
			 return true;
		          }
		     else {
			    return false;
			    }
	     }
	     
	  @Override
	  public int hashCode() {
		  String S_flags;
		  int I_retval, I_hc_1, I_hc_2;
		  
		  I_hc_1 = this.S_regexp.hashCode();
		  I_hc_2  = (Integer)this.I_flags;
		  I_retval = I_hc_1 ^ I_hc_2;
		  return I_retval;
	      }
	  }
	  
	  public static HashMap<RegexKey, Pattern> HS_known_patterns = new HashMap<RegexKey, Pattern>();
	  
	  public static class NamedPattern extends Pattern {
		  
		  public static final String S_FN_named_group_map = "namedGroupMap";
		  public HashMap<String, Integer> HS_named_group_map;
		  
	  public NamedPattern (final String PI_S_regexp) {
		  super (PI_S_regexp);
		  this.HS_named_group_map = FHS_get_named_group_map(this);
		  return;
		  }	  
	     
	  public NamedPattern (
			  final String PI_S_regexp,
			  final int    PI_I_flags) {
		  super (PI_S_regexp, PI_I_flags);
		  this.HS_named_group_map = FHS_get_named_group_map(this);
		  return;
		  }	  
	     
	  public NamedPattern (
			  final String  PI_S_regexp,
			  final String    PI_S_flags) {
		  super (PI_S_regexp, PI_S_flags);
		  this.HS_named_group_map = FHS_get_named_group_map(this);
		  return;
		  }	  
	  
	  public  NamedPattern(final Pattern PI_O_pattern) {
		  super (PI_O_pattern.toString(), PI_O_pattern.getFlags());
		  this.HS_named_group_map = FHS_get_named_group_map(this);
	  }
	  
	  public static NamedPattern FO_create_named_pattern(final Pattern PI_O_pattern) {
		  
		  NamedPattern O_retval_named_pattern;
		  String S_regexp;
		  int    I_flags;
		  
		  I_flags = PI_O_pattern.getFlags();
		  S_regexp = PI_O_pattern.toString();
		  O_retval_named_pattern = new NamedPattern(S_regexp, I_flags);
		  return O_retval_named_pattern;  
	  }
	  
	  public static NamedPattern FO_compile(
			  final String  PI_S_regexp,
			  final String   PI_S_flags) {
		  
		Pattern       O_pattern;  
		NamedPattern  O_retval_named_Pattern;
		
		O_pattern = Pattern.compile(PI_S_regexp, PI_S_flags);
		O_retval_named_Pattern = FO_create_named_pattern(O_pattern);
		
		return O_retval_named_Pattern;
	  }
	  
	  public static NamedPattern FO_compile(
			  final String  PI_S_regexp,
			  final int    PI_I_flags) {
		  
		Pattern       O_pattern;  
		NamedPattern  O_retval_named_Pattern;
		
		O_pattern = Pattern.compile(PI_S_regexp, PI_I_flags);
		O_retval_named_Pattern = FO_create_named_pattern(O_pattern);
		
		return O_retval_named_Pattern;
	  }
	  
	  public static NamedPattern FO_compile(
			  final String  PI_S_regexp) {
		  
		Pattern       O_pattern;  
		NamedPattern  O_retval_named_Pattern;
		
		O_pattern = Pattern.compile(PI_S_regexp);
		O_retval_named_Pattern = FO_create_named_pattern(O_pattern);
		
		return O_retval_named_Pattern;
	  }
	  
	 public static NamedPattern deserializeFromString(String PI_S_serialized) {
		  Pattern O_pattern;
		  NamedPattern O_retval_named_pattern;
		  
		  O_pattern = Pattern.deserializeFromString(PI_S_serialized);
		 
		  O_retval_named_pattern = NamedPattern.FO_create_named_pattern(O_pattern);
		  return O_retval_named_pattern;
	 }
	  
	  public static  HashMap<String, Integer> FHS_get_named_group_map (
			  final Pattern PI_O_pattern) {
		  
		  RuntimeException E_rt;
		  String S_msg_1;
		  
		  HashMap<String, Integer> HS_retval = null;
		  
		 try {
			 HS_retval = (HashMap<String, Integer>) FieldUtils.readField(PI_O_pattern, S_FN_named_group_map, true);
		} catch (IllegalAccessException | ClassCastException PI_E_ill_acc) {
			S_msg_1 = "Error reading attribute: \'" +  S_FN_named_group_map + "\' of type: \'" + 
		    HS_retval.getClass().getName() + "\'";
			E_rt = new RuntimeException(S_msg_1, PI_E_ill_acc);
			throw E_rt;
		    }
	
		 return HS_retval;
	     }
	  }
	  
	  public static class NamedMatch {
		public  String S_val;
		public  int I_idx_d0;
		  
		  public NamedMatch(
				  final String PI_S_val,
				  final int    PI_I_idx_f0) {
			
			  this.S_val    = PI_S_val;
			  this.I_idx_d0 = PI_I_idx_f0;
			  return;
			  
		  }
	  }
	
	  public static class GroupMatchResult {
			public int    I_array_size_f1;
			public int    I_map_size_f1;
			public String AS_numbered_groups[];
			public HashMap<String, NamedMatch> HS_named_groups;
			
		GroupMatchResult(int PI_I_count_f1) {
			I_array_size_f1 = PI_I_count_f1;
			if (I_array_size_f1 >= 1) {
			   AS_numbered_groups = new String[I_array_size_f1];
			   HS_named_groups    = new HashMap<String, NamedMatch>();
			   }
	    	else {
	    	   AS_numbered_groups       = null;
	    	   HS_named_groups          = null;
	    	   }}
	   }

		public static final GroupMatchResult NO_MATCH = new GroupMatchResult(0);

		public static GroupMatchResult FO_match(
				final String PI_S_input_sequence,
				final Pattern PI_P_pattern) {
			
			GroupMatchResult O_retval;
			
			O_retval = FO_match(PI_S_input_sequence, PI_P_pattern, 0);
			return O_retval;
		}
		
		public static GroupMatchResult FO_match(
				final String PI_S_input_sequence,
				final Pattern PI_P_pattern,
				final int PI_I_modes) {
				 
			    AssertionError E_assert;
			    RuntimeException E_rt;
			    
				Matcher M_matcher;
				GroupMatchResult O_retval;
				NamedPattern O_named_pattern;
				String S_match;
				
				int i1, I_group_count_f1, I_array_size_f1;
				BiConsumer<String, Integer> F_action;
				
				M_matcher = PI_P_pattern.matcher(PI_S_input_sequence);
				
				if (M_matcher.matches()) {
					I_group_count_f1 = M_matcher.groupCount();
					I_array_size_f1 = I_group_count_f1 + 1;
					O_retval = new GroupMatchResult(I_array_size_f1);
					O_retval.I_array_size_f1 = I_array_size_f1;
					for (i1 = 0; i1 < I_array_size_f1; i1++) {
						S_match = M_matcher.group(i1);
						O_retval.AS_numbered_groups[i1] = S_match; }
				    if (PI_P_pattern instanceof NamedPattern) {
				    	O_named_pattern = (NamedPattern)PI_P_pattern;
				        }
				    else {
				    	O_named_pattern = new NamedPattern(PI_P_pattern);
				        }
				    O_retval.I_map_size_f1 = O_named_pattern.HS_named_group_map.size();
				    
				    F_action = (final String PI_S_named_gr, final Integer PI_I_idx_f0) -> {
				    	NamedMatch O_named_match;
				    	StringBuilder SB_receiving_match;
				    	String  S_receiving_match, S_match_from_array;
				    	boolean B_found_named_match;
				    	
				   	    SB_receiving_match = new StringBuilder();
				      	B_found_named_match = M_matcher.getGroup(PI_S_named_gr, SB_receiving_match, PI_I_modes);
				    	
				   	    S_receiving_match = SB_receiving_match.toString();
				    	S_match_from_array = O_retval.AS_numbered_groups[PI_I_idx_f0]; 
				    	
				    	O_named_match = new NamedMatch(S_match_from_array, PI_I_idx_f0);
				    	O_retval.HS_named_groups.put(PI_S_named_gr, O_named_match);
				        };
				    
				    O_named_pattern.HS_named_group_map.forEach(F_action);   
				}
				else {
				   O_retval = RegexpUtils.NO_MATCH; }
				
				return O_retval; }

		/**
		 *
		 * @param  PI_S_input_sequence string to be examined for matches
		 * @param  PI_S_pattern regular expression similar to pearl
		 * @param  PI_I_flags flags for compiling the pattern
		 * @param  PI_B_compile_once like the /o flag in perl-regexp, only for constant patterns
		 * 
		 * @return instance of the Object group-match-results
		 * 
		 */

		public static GroupMatchResult FO_match(
		         final String PI_S_input_sequence,
				 final String  PI_S_pattern,
				 final int     PI_I_flags,
				 final boolean PI_B_compile_once) {

		String S_pattern;
		Pattern O_pattern;
		int /* I_hash_code */ I_flags_build, I_flags_returned;

		GroupMatchResult  O_retval_grp_match_result;
		RegexKey O_regex_key;

	//	I_flags_build = 0x0;
	

		if (PI_B_compile_once == true) {
			S_pattern = PI_S_pattern.intern();
			O_regex_key = new RegexKey(S_pattern, PI_I_flags);
		
			O_pattern = HS_known_patterns.get(O_regex_key);
			if (O_pattern == null) {	
				O_pattern = Pattern.compile(S_pattern, PI_I_flags);
				HS_known_patterns.put(O_regex_key, O_pattern); 
				}
			}
		else {
		   O_pattern = Pattern.compile(PI_S_pattern, PI_I_flags); }

		O_retval_grp_match_result = FO_match(PI_S_input_sequence, O_pattern);
		return O_retval_grp_match_result; }
	
		public static GroupMatchResult FO_match(
		          final String PI_S_input_sequence,
				  final String PI_S_pattern,
				  final String PI_S_flags,
				  final boolean PI_B_compile_once) {
			
		GroupMatchResult  O_retval_group_match_result;
		int               I_flags;
		
		I_flags = FI_parse_flags(PI_S_flags);
		
		O_retval_group_match_result = FO_match(
		          PI_S_input_sequence,
				  PI_S_pattern,
				  I_flags,
				  PI_B_compile_once);
		
		return O_retval_group_match_result;
		
}
}