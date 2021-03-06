package com.github.michaelederaut.basics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.util.concurrent.SimpleTimeLimiter;
// import sun.nio.ch.FileChannelImpl;

// https://stackoverflow.com/questions/42538750/unable-to-export-a-package-from-java-base-module/42541096#42541096

import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;
// import regexodus.Pattern;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;

public class StreamUtils {
	
	public static final String S_field_name_chan = "channel";
	public static final String S_field_name_path = "path";

	public static class RangedPattern {
		
		public static final String S_re_default_non_empty = "^.+$";
		public static final Pattern P_default_non_empty = Pattern.compile(S_re_default_non_empty);
		
		public Pattern P_end_criterion;
		public int I_from_line_f1 = 1;
		public int I_to_line_f1   = Integer.MAX_VALUE;
		
		
		public RangedPattern (final Pattern PI_P_end_criterion) {
			this.P_end_criterion = PI_P_end_criterion;
			}
		
		public RangedPattern(
				final Pattern PI_P_end_criterion,
				final int PI_I_line_from_f1) {
			
		this(PI_P_end_criterion, PI_I_line_from_f1, Integer.MAX_VALUE)	;
		}
		
		public RangedPattern(
				final Pattern PI_P_end_criterion,
				final int PI_I_line_from_f1,
				final int PI_I_line_to_f1) {
			
			IllegalArgumentException E_ill_arg;
			AssertionError           E_assert;
			String S_msg_1;
			
			this.P_end_criterion = PI_P_end_criterion;
			if (PI_I_line_from_f1 < 1) {
				S_msg_1 = "From line-nbr argument" + PI_I_line_from_f1 + " must be >= 1";
				E_ill_arg = new IllegalArgumentException(S_msg_1);
			    throw E_ill_arg;
				}
			
			if (PI_I_line_from_f1 > PI_I_line_to_f1) {
				S_msg_1 = "1st from-line-nbr argument: " + PI_I_line_from_f1 + 
						  "must not be greater than 2nd to-line-nbr argument: " + PI_I_line_to_f1 + ".";
				E_assert = new AssertionError(S_msg_1);
			    throw E_assert;
				}
			
			this.I_from_line_f1 = PI_I_line_from_f1;
			this.I_to_line_f1   = PI_I_line_to_f1;
			}
		
	/**
	 * @deprecated use RangePattern(Pattern) instead
	 */
		public RangedPattern () {
		this(P_default_non_empty);
			}
		
		/**
		 * @deprecated use RangePattern(Pattern) instead
		 * @param PI_S_re_end_criterion end criterion
		 */
		public RangedPattern (final String PI_S_re_end_criterion) {
			this(Pattern.compile(PI_S_re_end_criterion));
			}
	    }
	
	public static class EndCriterion {
		 
		   public   static final long	L_timeout_dflt               = 10_000L;   
		
		   public          Long          L_timeout                   = 0L;  
		   public          RangedPattern AO_ranged_patterns_stdoud[] = null;
		   public          Pattern       P_end_crit_stderr           = null;
		   
		   public GroupMatchResult O_grp_match_result_req     = null;
		   public GroupMatchResult O_grp_match_result_sess    = null;
		   public          long L_time_elapsed_first_line     = -1L;
		   public          long L_time_elapsed_total          = -1L;
		   public          int  I_idx_qualifying_pattern_f0   = -1;
	
		   /**
		    * @deprecated
		    */
	    public EndCriterion() {
			  
			  this(
				L_timeout_dflt,
				new RangedPattern[] 
			    {new RangedPattern()}, 
				RangedPattern.P_default_non_empty);
					    return;
		   }
			
	    
	   /**
	    * @deprecated
	    * @param PI_L_timeout timeout
	    */
	   public EndCriterion(final Long PI_L_timeout) {
		   
		     this(PI_L_timeout,
			      new RangedPattern[] 
				 {new RangedPattern()}, 
						RangedPattern.P_default_non_empty);
				    return;
	   }
		
	   
	   public EndCriterion(
			   final Long PI_L_timeout, 
			   final Pattern PI_P_end_criterion) {
		   
		   this(PI_L_timeout,
		            new RangedPattern[] 
					{new RangedPattern(PI_P_end_criterion)}, 
					RangedPattern.P_default_non_empty);
			    return;
		    }
	   
	   public EndCriterion(
			   final Long PI_L_timeout, 
			   final Pattern PI_P_end_criterion,
			   final int PI_I_line_from_f1) {
		   
		   this(PI_L_timeout, 
			    	new RangedPattern[] 
					{new RangedPattern(PI_P_end_criterion, PI_I_line_from_f1)}, 
					RangedPattern.P_default_non_empty);
			    return;
		    }
	   
	   public EndCriterion(
			   final Long PI_L_timeout, 
			   final Pattern PI_P_end_criterion,
			   final int PI_I_line_from_f1,
			   final int PI_I_line_to_f1) {
		   
		    this(PI_L_timeout, 
		    	new RangedPattern[] 
				{new RangedPattern(PI_P_end_criterion, PI_I_line_from_f1, PI_I_line_to_f1)}, 
				RangedPattern.P_default_non_empty);
		    return;
		    }
	   
	   public EndCriterion(
			   final Long PI_L_timeout, 
			   final RangedPattern PI_AO_ranged_pattern_stdout[])  {
		   
		   this(PI_L_timeout, PI_AO_ranged_pattern_stdout, RangedPattern.P_default_non_empty);
	       }
		
		   public EndCriterion(
				   final Long PI_L_timeout, 
				   final RangedPattern PI_AO_ranged_pattern_stdout[],
				   final Pattern PI_P_end_crit_stderr)  {
		   
			this.L_timeout          = PI_L_timeout;
			this.AO_ranged_patterns_stdoud = PI_AO_ranged_pattern_stdout;
			this.P_end_crit_stderr  = PI_P_end_crit_stderr;
			
			return;
	   }
	}
	
	/**
	 * 
	 * timeout at read operations 
	 *
	 */
	 protected static class CallableBufferedReader implements Callable<String>{
		 
		 LineNumberReader O_buff_rdr;
		 
		 @Override
		 public String call() {
			 String S_retval_inp_line, S_msg_1;
			 RuntimeException E_rt;
			 int I_line_nbr_f1; 
			
			 S_retval_inp_line = null;
			 
			 I_line_nbr_f1 = this.O_buff_rdr.getLineNumber();
			 try {
				S_retval_inp_line = O_buff_rdr.readLine();
			} catch (IOException PI_E_io) {
				S_msg_1 = "Error ocurred reading data from line: " + I_line_nbr_f1;
				E_rt = new RuntimeException(S_msg_1, PI_E_io);
				throw E_rt;
			} 
			 return S_retval_inp_line; 
		 }
		 
		 public CallableBufferedReader(final LineNumberReader  PI_O_buff_reader) {
			 this.O_buff_rdr = PI_O_buff_reader;
		 }
	 }
	
	 public static LineNumberReader FO_get_lnr_rdr_from_stream (final InputStream PI_O_inp_stream) {
	    	
	    	LineNumberReader O_retval_lnr_rdr;
	    	InputStreamReader O_inp_str_reader;
	    	
	    	
	    	O_retval_lnr_rdr = null;
	    	
	    	if (PI_O_inp_stream != null) {
//	    		byte AC_debug_dummy[];  
//	    		String S_debug_dummy;
//	    		try {
//					AC_debug_dummy = PI_O_inp_stream.readAllBytes();
//					S_debug_dummy = new String(AC_debug_dummy);
//				} catch (IOException e) {
//					S_debug_dummy = null;
//					e.printStackTrace(System.err);
//				}
//	    		System.out.println("Debug-String: " + S_debug_dummy);  // OK has data H:\10\gnu\python\3\python.exe
	    		O_inp_str_reader = new InputStreamReader(PI_O_inp_stream);
	    		O_retval_lnr_rdr = new LineNumberReader(O_inp_str_reader);
	    	    }
	    	
	    	return O_retval_lnr_rdr;
	    	
	    }
	 
	   public static List<String> FAS_get_contents_from_stream(
	    		final InputStream PI_O_inp_stream) {
		   
		 List<String> AS_retval;
		  
		 AS_retval = FAS_get_contents_from_stream(
				  PI_O_inp_stream,
				  (EndCriterion)null);
				
		 return AS_retval;   
	     }
	    
	    public static List<String> FAS_get_contents_from_stream(
	    		final InputStream PI_O_inp_stream,
	    		final EndCriterion PB_O_end_criterion) {
	    	
	    	IllegalArgumentException E_ill_arg;
	    	RuntimeException         E_rt;
	    	LineNumberReader O_buff_rdr;
	    	Stack<String> AS_retval;
	    	SimpleTimeLimiter O_time_limiter;
	    	ExecutorService O_exec_service;
	    	
	    	Pattern P_end_requ, P_end_session;

	    	RangedPattern O_ranged_pattern, AO_ranged_patterns[];
	    	GroupMatchResult O_grp_match_result;
	    	CallableBufferedReader O_callable_buff_reader;
	    	
	    	Long L_timeout;
	    	long L_timestamp_init, L_timestamp_2;
	    	boolean B_end_criterion_found;
	    	String S_line_inp, S_msg_1, S_msg_2;
	    	int I_line_nbr_f1, i2, I_nbr_ranged_patterns_f1, I_line_nbr_from_f1, I_line_nbr_to_f1, I_idx_pattern_f0;
	    
	    	AS_retval = (Stack<String>)null;
	    	
	    	if (PI_O_inp_stream == null) {
	    		return AS_retval;
	    	    }
	    	
	    	O_buff_rdr = FO_get_lnr_rdr_from_stream(PI_O_inp_stream);
//	    	String S_dummy_line;
//	    	try {
//				S_dummy_line = O_buff_rdr.readLine();
//			} catch (IOException e) {
//				S_dummy_line = null;
//				e.printStackTrace(System.err);
//			}
//	    	System.out.println("Dummy-line: " + S_dummy_line); // OK has data: H:\10\gnu\python\3\python.exe
	    	O_callable_buff_reader = new CallableBufferedReader(O_buff_rdr);
	    	
	    	O_time_limiter = null;
	    	P_end_requ     = null;
	    	P_end_session  = null;
	    	L_timeout      = null; // 0L;
	    	
	    	if (PB_O_end_criterion == null) {
	    		AO_ranged_patterns = null;
	    	   }
	    	else {
	    		L_timeout = PB_O_end_criterion.L_timeout;
	    		if ((L_timeout != null) && (L_timeout < 0)) {
	    			S_msg_1 = "Invalid value for timeout: " + L_timeout + ".";
	    			E_ill_arg = new IllegalArgumentException(S_msg_1);
	    			S_msg_2 = "Unable to read contents from input-stream.";
	    			E_rt = new RuntimeException(S_msg_2, E_ill_arg);
	    			throw E_ill_arg;
	    		    }
	    		PB_O_end_criterion.I_idx_qualifying_pattern_f0 = -1;
	    		PB_O_end_criterion.O_grp_match_result_req      = null;
	    		PB_O_end_criterion.O_grp_match_result_sess     = null;
	    		PB_O_end_criterion.L_time_elapsed_first_line   = -1L;
	    		PB_O_end_criterion.L_time_elapsed_total        = -1L;		
	    		if (L_timeout != null) {
	    			O_exec_service = Executors.newCachedThreadPool();
	    			O_time_limiter = SimpleTimeLimiter.create(O_exec_service);
	    		    }
	    		AO_ranged_patterns = PB_O_end_criterion.AO_ranged_patterns_stdoud;
	    	    }
	    	  
	    	AS_retval = new Stack<String>();
	    	I_line_nbr_f1 = 0;
	    	B_end_criterion_found = false;
	    	
	    	L_timestamp_init = System.currentTimeMillis();
	    	LOOP_LINES: while (!B_end_criterion_found) {
	    	   I_line_nbr_f1 = O_buff_rdr.getLineNumber();
	    	   S_line_inp = null;
	    	   if (I_line_nbr_f1 == 0) {
	    		   L_timestamp_init = System.currentTimeMillis();
	    	       }
	    	   try {
	    	      if (O_time_limiter == null) {
	    	    	  S_line_inp = O_callable_buff_reader.call();
	    	         }  
	    	      else {
	    	    	 try {
						S_line_inp = O_time_limiter.callWithTimeout(
								 O_callable_buff_reader, L_timeout, TimeUnit.MILLISECONDS);
					 } catch (TimeoutException | InterruptedException | ExecutionException PI_E_timeout) {
						S_line_inp = null;
					    }
	    	         }     
			    } catch (Exception PI_E_io) {
			    	break LOOP_LINES;
			        }
	    	    if (S_line_inp == (String)null) {
	    	    	break LOOP_LINES;
	    	        }
	    	    I_line_nbr_f1 = O_buff_rdr.getLineNumber();	
	    	    if (I_line_nbr_f1 == 1) {
	    	       L_timestamp_2 = System.currentTimeMillis();
	    	       PB_O_end_criterion.L_time_elapsed_first_line = L_timestamp_2 - L_timestamp_init; 
	    	       }
	    	       
	    		P_end_requ = null;
	    		I_idx_pattern_f0 = -1;
	    		if (AO_ranged_patterns != null) {
		    	    I_nbr_ranged_patterns_f1 = AO_ranged_patterns.length;
		    	LOOP_PATTERNS:  for (i2 = 0; i2 < I_nbr_ranged_patterns_f1; i2++) {
	    	    	O_ranged_pattern = AO_ranged_patterns[i2];
	    	    	I_line_nbr_from_f1 = O_ranged_pattern.I_from_line_f1;
	    	    	if (I_line_nbr_f1 >= I_line_nbr_from_f1) {
	    	    		I_line_nbr_to_f1 = O_ranged_pattern.I_to_line_f1;
	    	    		if (I_line_nbr_to_f1 <= I_line_nbr_to_f1) {
	    	    		   P_end_requ = O_ranged_pattern.P_end_criterion;
	    	    		   if (P_end_requ != null) {
	    		    	    	O_grp_match_result = RegexpUtils.FO_match(S_line_inp, P_end_requ);
	    		    	    	if (O_grp_match_result.I_array_size_f1 > 0) {
	    		    	    		PB_O_end_criterion.O_grp_match_result_req = O_grp_match_result; 
	    		    	    		PB_O_end_criterion.I_idx_qualifying_pattern_f0 = i2;
	    		    	    		B_end_criterion_found = true;
	    		    	    		break LOOP_PATTERNS;
	    		    	    	    }
	    	    		        }	    	    		
	    	    	          }
		    	    	    }
		    	         }
	    		     }
	    	 
	    	    AS_retval.push(S_line_inp);
	    	    }

// TODO 
//	    	if (!B_end_criterion_found) {
//	    		try {
//					 O_buff_rdr.close();
//				} catch (IOException PI_E_io) {
//					S_msg_1 = "Error ocurred when closing readar after obtaining : " + I_line_nbr + " lines";
//					E_rt = new RuntimeException(S_msg_1, PI_E_io);
//					PI_E_io.printStackTrace(System.err);
//				}
//	    	}
	    	
	        L_timestamp_2 = System.currentTimeMillis();
    	    PB_O_end_criterion.L_time_elapsed_total = L_timestamp_2 - L_timestamp_init;
	    	return AS_retval;	    
	    } 
	    
	    public static String FS_get_path_from_buffered_reader(BufferedReader PI_O_buff_rdr) {
	    	String S_pna_retval = null;
	    	
	    	RuntimeException E_rt_1, E_rt_2;
	    	NullPointerException E_np;
	    	ClassCastException E_class_cast;
	    	Object O_res_channel, O_res_path; 
	    	
	    	String S_msg_1, S_msg_2;
	    	
	    	O_res_path = null;
	    	try {
				try {
					O_res_channel = FieldUtils.readField(PI_O_buff_rdr, S_field_name_chan , true);
				} catch (IllegalAccessException PI_E_ill_acc) {
				    S_msg_1 = "Unable to read field: \'" + S_field_name_chan + "\' from BufferedReader"; 
					E_rt_1 = new RuntimeException(S_msg_1, PI_E_ill_acc);
					throw E_rt_1;
				    }
				if (O_res_channel == null) {
					S_msg_1 = "Field: \'" + S_field_name_chan + "\' in BufferedReader is null";
					E_np = new NullPointerException(S_msg_1);
					throw E_np;
				    }
				
				try {
				   O_res_path = FieldUtils.readField(O_res_channel, S_field_name_path , true);
				} catch (IllegalAccessException PI_E_ill_acc) {
				    S_msg_1 = "Unable to read field: \'" + S_field_name_path + "\' from FileChannel"; 
					E_rt_1 = new RuntimeException(S_msg_1, PI_E_ill_acc);
					throw E_rt_1;
				    }
				 if (O_res_path == null) {
					S_msg_1 = "Field: \'" + S_field_name_path + "\' in FileChannel is null"; 
					E_np = new NullPointerException(S_msg_1);
					throw E_np;
				    } 
				 if (!(O_res_path instanceof String)) {
					 S_msg_1 = "Field: \'" +  S_field_name_path + "of type \'" + O_res_path.getClass().getName() + "\'\n" +
				               "should be String";
					 E_class_cast = new ClassCastException(S_msg_1);
					 throw E_class_cast;
				 }
				 S_pna_retval = (String)S_pna_retval;
			} catch (NullPointerException|ClassCastException PI_E_np) {
				 S_msg_2 = "Unable to get path from BufferedReader";
				 E_rt_2 = new RuntimeException(S_msg_2, PI_E_np);
				 E_rt_2.printStackTrace(System.err);
			     }
	    	
	    	return S_pna_retval;
	    }
	}
