package  com.github.michaelederaut.basics;

import org.codehaus.plexus.util.ReflectionUtils;

import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;

import org.apache.commons.lang3.StringUtils;
import java.lang.reflect.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import regexodus.Matcher;
import regexodus.Pattern;
import java.util.zip.GZIPInputStream;

import  java.lang.ref.*;


// import         apps.multimedia.subtitles.SubtitlesBasis;
import static  org.apache.commons.lang3.StringUtils.LF;
import static  org.apache.commons.lang3.SystemUtils.PATH_SEPARATOR;
import static  org.apache.commons.lang3.SystemUtils.FILE_SEPARATOR;


public class ToolsBasics {

	public static final String FS    = FILE_SEPARATOR;
	public static final String S_windows_suffix_exe = "exe";
	public static final String S_windows_suffix_dll = "dll";
	
	public static final int I_max_line_len_stored_f1  =  4096;  // 512
	public static final String S_date_format = ("dd.MM.yyyy");
	public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(S_date_format);
	
//	public static final File[] AF_no_files = new File[0];
	public static final String[] AS_no_strings = new String[0];
	public static final String S_dnr_logfiles         = "resources";
//	public static final String S_dnr_jboss_head       = "jboss";

	public static final String S_re_leading_zeroes = "^0*([1-9]\\d*)$";
	public static final Pattern P_leading_zeroes = Pattern.compile(S_re_leading_zeroes);
	
	public static final int I_flags_case_insensitive = Pattern.IGNORE_CASE;
	
	public static final String S_re_bnt_timestamp_short = "(\\d{4})\\-(\\d{2})\\-(\\d{2})_(\\d{2})\\-(\\d{2})\\-(\\d{2})";
	public static final String S_re_bnt_timestamp_short_serial = S_re_bnt_timestamp_short + "\\." + "(\\d{1,4})";
	public static final String S_re_bnt_timestamp_long = S_re_bnt_timestamp_short + "\\," + "(\\d{1,3})"; // <timestamp_short>,<millisecs>
	public static final String S_re_bnt_timestamp_long_serial = S_re_bnt_timestamp_long + "\\." + "(\\d{1,4})";
	
	public static final String S_re_bn_timestamp_short =  S_re_bnt_timestamp_short + "\\.csv";
	public static final String S_re_bn_timestamp_short_serial =  S_re_bnt_timestamp_short_serial + "\\.csv";
	public static final String S_re_bn_timestamp_long = S_re_bnt_timestamp_long +  "\\.csv";
	public static final String S_re_bn_timestamp_long_serial = S_re_bnt_timestamp_long_serial + "\\.csv";
	
	// public static final Pattern P_re_bnt_csv = Pattern.compile("\\A(" + S_re_bnt_timestamp_long + ")\\.csv\\Z");
	
	public static final int    I_year_min = 1970;
	public static final String S_year_min = String.format("%04d", I_year_min);
	public static final int    I_year_max = 2099;
	public static final String S_year_max = Integer.toString(I_year_max);
	
	public static final int    I_month_min = 1;
	public static final String S_month_min = String.format("%02d", I_month_min);
	public static final int    I_month_max = 12;
	public static final String S_month_max = Integer.toString(I_month_max);
	
	public static final int    I_day_min   = 1;
	public static final String S_day_min   = String.format("%02d",I_day_min);
	public static final int    I_day_max   = 31;
	public static final String S_day_max   = Integer.toString(I_day_max);
	
	public static final int    I_hour_min  = 0;
	public static final String S_hour_min  = String.format("%02d",I_hour_min);
	public static final int    I_hour_max  = 23;
	public static final String S_hour_max  = Integer.toString(I_hour_max);
	
	public static final int    I_minute_min = 0;
	public static final String S_minute_min = String.format("%02d", I_minute_min);
	public static final int    I_minute_max = 59;
	public static final String S_minute_max = Integer.toString(I_minute_max);
	
	public static final int    I_second_min = 0;
	public static final String S_second_min = String.format("%02d", I_second_min);
	public static final int    I_second_max = 59;
	public static final String S_second_max = Integer.toString(I_second_max);
	
	public static final int    I_ms_min     = 0;
	public static final String S_ms_min     = String.format("%02d", I_ms_min);
	public static final int    I_ms_max     = 999;
	public static final String S_ms_max     = Integer.toString(I_ms_max);
	
	public static final String S_date_min_csv = S_year_min + "-" + S_month_min + "-" + S_day_min + "_" +
	                                            S_hour_min + ":" + S_minute_min + ":" + S_second_min;
	public static final int    I_len_date_min_csv_f1 = S_date_min_csv.length();
	
 	public static final String S_bnt_timestamp_max_short = 
 		    S_year_max + "-" +
 		    S_month_max + "-" + 
 		    S_day_max + "_" +
 		    S_hour_max + "-" +
 		    S_minute_max + "-" + 
 		    S_second_max;
 	
 	public static final String S_bnt_timestamp_max_long =  S_bnt_timestamp_max_short + "," + S_ms_max;
 		    
	public static final String S_bn_timestamp_max_short = S_bnt_timestamp_max_short + "\\.csv";
	public static final String S_bn_timestamp_max_long = S_bnt_timestamp_max_long + "\\.csv";
	
    //----------------------
    
   public static long FL_get_time_in_ms (String PI_S_time_fmt) {
		
	   long L_retval = 0;
	   IllegalArgumentException O_EX_date_fmt_error;
	   	
	   int I_new_parse_pos_f0;
	   Date O_date;

	   ParsePosition O_parse_pos;
	   O_parse_pos = new ParsePosition(0);

	   O_date = ToolsBasics.DATE_FMT.parse(PI_S_time_fmt, O_parse_pos);
	   I_new_parse_pos_f0 = O_parse_pos.getIndex();
	   if (I_new_parse_pos_f0 > 0) {
	   	   L_retval = O_date.getTime(); }
	   else {
	   	   O_EX_date_fmt_error = new IllegalArgumentException(
	   			"Invalid format for date : \"" + ToolsBasics.S_date_format + "\" \"" + PI_S_time_fmt + "\"");
	   	throw O_EX_date_fmt_error; }
	   		
	   return L_retval;
	   }
   
   public static Integer FI_parse_strict_decimal(final String PI_S_number) {
	   
	   GroupMatchResult O_group_match_result;
	   String S_number;
	   Integer I_retval;
	   
	   I_retval = null;
	   O_group_match_result = RegexpUtils.FO_match(PI_S_number, P_leading_zeroes);
	   if (O_group_match_result.I_array_size_f1 < 2) {
		   return I_retval; 
	       }
	   S_number = O_group_match_result.AS_numbered_groups[1];
	   I_retval = Integer.parseUnsignedInt(S_number);
	   
	   return I_retval;
 }
 
 public static int FI_parse_decimal(final String PI_S_number, int PI_I_default) {
	   
	   int I_retval;
	   Integer I_res_parse;
	
	   I_retval = PI_I_default;
	   I_res_parse = FI_parse_strict_decimal(PI_S_number);
	   if (I_res_parse != null) {
		   I_retval = I_res_parse;
	       }
	   
	   return I_retval;
 }
 
 
 public static int FI_parse_decimal(final String PI_S_number) {
	   
	   int I_retval;
	  
	   I_retval = FI_parse_decimal(PI_S_number, 0);
	   
	   return I_retval;
}
 
 
 public static Long FL_parse_strict_decimal(final String PI_S_number) {
	   
	   GroupMatchResult O_group_match_result;
	   String S_number;
	   Long L_retval;
	   
	   L_retval = null;
	   O_group_match_result = RegexpUtils.FO_match(PI_S_number, P_leading_zeroes);
	   if (O_group_match_result.I_array_size_f1 < 2) {
		   return L_retval; 
	       }
	   S_number = O_group_match_result.AS_numbered_groups[1];
	   L_retval = Long.parseUnsignedLong(S_number);
	   
	   return L_retval;	   
 }
 
 public static Long FL_parse_decimal(final String PI_S_number, long PI_L_default) {
	   
	   long L_retval;
	   Long L_res_parse;
	   
	   L_retval = PI_L_default;
	   L_res_parse = FL_parse_strict_decimal(PI_S_number);
	   if (L_res_parse != null) {
		   L_retval = L_res_parse; 
	       }
	   
	   return L_retval;
 }
 
public static Long FL_parse_decimal(final String PI_S_number) {
	   
	   long L_retval;
	   
	   L_retval = FL_parse_decimal(PI_S_number, 0L);
	 
	   return L_retval;
 }
  
 
   //-----------------------------------------------------------------------
   
 
   /** 
    * 
    * @param PI_S_pn                   pathname to be truncated
    * @param PI_S_suffix String        S_suffix to be truncated without the dot(.)
    * @param PI_B_case_sensisitve      upper/lower case of suffix does matter (true for Unix, false possible for Windows but not recommended)
    * @param PI_B_die_on_wrong_suffix  if suffix doesn't match 
    *                                      if true throws IllegalArgumentException
    *                                      if false returns null  
    * @return String
    * 
    */
   
   public static String FS_pn_truncate_suffix (
		 String PI_S_pn, 
		 String PI_S_suffix,
		 boolean PI_B_case_sensisitve,
		 boolean PI_B_die_on_wrong_suffix) {
   
   String S_retval, S_suffix_full, S_tail, S_msg;
   int I_len_pn_f1, I_len_suffix_f1, I_idx_suffix_start_f0;
   boolean B_matches;
   IllegalArgumentException E_ill_arg;
   
   S_retval = "";
   I_len_pn_f1           = PI_S_pn.length();
   S_suffix_full         = "." + PI_S_suffix;
   I_len_suffix_f1       = S_suffix_full.length();
   I_idx_suffix_start_f0 = I_len_pn_f1 - I_len_suffix_f1;
   S_tail = PI_S_pn.substring(I_idx_suffix_start_f0, I_len_pn_f1);
   B_matches = false;
   if (PI_B_case_sensisitve) {
      if (S_tail.equals(S_suffix_full)) {
    	  B_matches = true; }
      else {
    	  if (S_tail.equalsIgnoreCase(S_suffix_full)) {
    		  B_matches = true; }}}
   
   if (B_matches) {   
	   S_retval = PI_S_pn.substring(0, I_idx_suffix_start_f0); }
   else {
	   if (PI_B_die_on_wrong_suffix) {
		   S_msg = "Path: " + PI_S_pn + "has no suffix: " +  S_suffix_full;
		   E_ill_arg = new IllegalArgumentException(S_msg); }
	   else {
		  S_retval = null; }}
 
   return S_retval; }

   // ------------------------
   
 
//   
//   public static class PrintStreamCsv /* extends PrintStream */ {
//   
//	   public static final PrintStream[] NO_PRINT_STREAMS = new PrintStream[0]; // used as dummy param
//	   public static final int I_max_lines_per_logfile_f1 = 65460; // 2^16 == 65536;
//     
//       public /* static final */ Pattern P_re_bn_csv ; /* = Pattern.compile("\\A(\\p{Graph}+?)\\.(\\d{3})\\.csv\\Z") */
//         
//       public short I_nbr_logiles_mx_f1 = 1;
//       boolean B_overflow = false;  
//       public HvbToolsBasis.StringByteArrayOutputStream O_print_stream = new StringByteArrayOutputStream();
//       
//	   int I_line_nbr_per_file_f1 = 0;
//	   int I_line_nbr_total = 0;  // not counting header
//	   int I_file_serial_f0 = 0; 
//	   
//	   String S_dot_file_serial_f0 = null;
//	   
//	   public FileOutputStream O_wtr_csv;
//	   public BufferedOutputStream O_wtr_buf_csv;
//	   
//	   File F_csv;
//	   public int I_nbr_digits_for_serial_f1;
//	   String S_file_serial_f0;
//	   String S_fmt_file_serial;
//	   
//	   File F_parent;
//	   String S_header;
//	   String S_bn_trunk;
//	   String S_bn_full;
//	  
//	   //----
//	   
//	   static protected void FV_init_print_stream(
//			   PrintStreamCsv PB_print_stream_csv) {
//		
//		  try {
//		     PB_print_stream_csv.O_wtr_csv = new FileOutputStream(PB_print_stream_csv.F_csv); }	   
//		  catch (FileNotFoundException E_fnf) {
//		        E_fnf.printStackTrace(System.err);
//		        System.exit(1); }
//		  PB_print_stream_csv.O_wtr_buf_csv = new BufferedOutputStream(PB_print_stream_csv.O_wtr_csv);
//		  return; }
//	
//	  //----
//	   public String FS_get_bn_full (int PI_csv_serial_f0) {
//		   		   
//	   final String S_re_bn_csv_prefix = "\\A(\\p{Graph}+?)\\." ;
//	   String S_retval, S_re_bn_csv;
//	
//	   if (this.S_dot_file_serial_f0 == null) {
//		  if (this.I_nbr_logiles_mx_f1 > 1) {
//		     this.I_nbr_digits_for_serial_f1 = Short.toString(this.I_nbr_logiles_mx_f1).length();
//	         this.S_fmt_file_serial = "%0" + I_nbr_digits_for_serial_f1 + "d"; 
//	         S_re_bn_csv = S_re_bn_csv_prefix + "(\\d{"  +  I_nbr_digits_for_serial_f1  +  "})\\.csv\\Z"; }
//	      else {
//	    	 this.I_nbr_digits_for_serial_f1 = 0;
//	    	 this.S_dot_file_serial_f0 = "";
//	    	 S_re_bn_csv = S_re_bn_csv_prefix  + "csv\\Z"; }
//		  
//	      this.P_re_bn_csv = Pattern.compile(S_re_bn_csv); }
//   
//       if (this.I_nbr_logiles_mx_f1 > 1) {
//    	   this.S_file_serial_f0 = String.format(this.S_fmt_file_serial, PI_csv_serial_f0); 
//	       this.S_dot_file_serial_f0 = "." + this.S_file_serial_f0; }	   
//    	   
//	   S_retval =  S_bn_trunk + this.S_dot_file_serial_f0  + ".csv";
//	   return S_retval;
//	   }
//	 //----
//	   
//	   public PrintStreamCsv(
//			   File PI_F_dnr_parent,
//			   String PI_S_header,
//			   String PI_S_bn_trunk,
//			   short  PI_I_nbr_logiles_mx_f1)  {
//			   
//		   this.F_parent = PI_F_dnr_parent;
//		   this.S_bn_trunk = PI_S_bn_trunk;
//		   this.S_header = PI_S_header;
//		   this.I_nbr_logiles_mx_f1 = PI_I_nbr_logiles_mx_f1;
//		   this.I_file_serial_f0 = 0;
//		   this.S_bn_full = FS_get_bn_full(this.I_file_serial_f0) ; 
//		 
//		   this.F_csv = new File(PI_F_dnr_parent, this.S_bn_full);
//		   FV_init_print_stream(this); }
//	  
//	   //------------------------------------
//	   
//	   public PrintStreamCsv(
//			   File PI_f,
//			   String PI_S_header,
//			   short  PI_I_nbr_logiles_mx_f1) throws FileNotFoundException { 
//		    
//		   this.F_csv     = PI_f;
//		   this.F_parent  = this.F_csv.getParentFile();
//		   this.S_bn_full = this.F_csv.getName();
//		   this.I_nbr_logiles_mx_f1 = PI_I_nbr_logiles_mx_f1;
//			   
//		   int I_array_size_f1;
//
//		   AssertionError E_assert;
//		   String AS_matching_groups[];
//		   GroupMatchResult O_match_result;
//		
//		   O_match_result = FO_match(this.S_bn_full, P_re_bn_csv);
//		   I_array_size_f1 = O_match_result.I_array_size_f1;
//		   if (I_array_size_f1 < 3) {
//	//		  this.O_print_stream.setError();
//			  E_assert = new AssertionError(this.S_bn_full);
//			  throw E_assert; }
//		   AS_matching_groups = O_match_result.AS_matching_groups;
//		   this.S_bn_trunk =  AS_matching_groups[2];
//		   this.S_header = PI_S_header; 
//		   FV_init_print_stream(this);   
//	   }
//	   
////---------------------
//	   
//	   public int FI_println(String PI_S_line) {
//		   AssertionError E_assert;
//		 //  FileNotFoundException E_fnf;
//		   String S_msg;
//		   int I_retval;
//		   
//		   I_retval = this.I_file_serial_f0;
//		   this.I_line_nbr_total++;
//		   
//	  if (this.I_line_nbr_per_file_f1 >= I_max_lines_per_logfile_f1) { // bcs header is included in line-count.
//		   try {
//			  this.O_wtr_buf_csv.close(); }
//		   catch (IOException E_io) {
//			  E_io.printStackTrace(System.err); }
//		  this.I_file_serial_f0++;
//	      this.I_line_nbr_per_file_f1 = 0;
//	      if (this.I_file_serial_f0 >= I_nbr_logiles_mx_f1) {
//		      S_msg = "Maximum number of " + I_nbr_logiles_mx_f1 + " exceeded for file " +  this.S_bn_full + LS +
//		      "\"" + PI_S_line + "\"";
//		      E_assert = new AssertionError(S_msg);
//		      throw E_assert; }
//	      I_retval = this.I_file_serial_f0;
//	      this.S_bn_full = FS_get_bn_full(this.I_file_serial_f0);
//	      this.F_csv = new File(this.F_parent, this.S_bn_full);
//	   	  this.O_print_stream.reset(); }
//	  
//      try {
//		if (this.I_line_nbr_per_file_f1 == 0) {
//			 FV_init_print_stream(this);
//			 if (this.S_header != null) {
//			    this.O_print_stream.FV_append(this.S_header);
//			    this.O_print_stream.FV_append_cr_lf();
//			    this.O_print_stream.writeTo(this.O_wtr_buf_csv);
//			    this.O_print_stream.reset();
//			    this.I_line_nbr_per_file_f1++; }}
//		
//		this.O_print_stream.FV_append(PI_S_line);
//		this.O_print_stream.FV_append_cr_lf(); 
//		this.O_print_stream.writeTo(this.O_wtr_buf_csv);
//		this.O_print_stream.reset();
//		this.I_line_nbr_per_file_f1++; }
//      catch (IOException E_io) {
//		E_io.printStackTrace(System.err);
//		System.exit(1);	}
//	   return I_retval; 
//	   }}
  
   //------------------------------------------------------
   public static String FS_unix_2_dos (String PI_S_line) { 
	   
	   int I_len_line_old_f1, I_pos_trail_1_f0, I_pos_trail_2_f0,  I_len_trailing_newlines_f1,
	       I_char_type_1,  I_char_type_2, I_pos_trim_end_f1;
	  
	   char c_trail_1, c_trail_2;
	   String S_retval, S_line_dos;
	 
	   
	   S_retval = PI_S_line;
	   I_len_line_old_f1 = PI_S_line.length();
	   if (I_len_line_old_f1 < 1) {
		  return S_retval;
	   }
	   I_pos_trail_1_f0 = I_len_line_old_f1 - 1;  
	   
	   I_len_trailing_newlines_f1 = 0;
	   c_trail_1 = PI_S_line.charAt(I_pos_trail_1_f0);
	   I_char_type_1 = Character.getType(c_trail_1);
	
	   if ((I_char_type_1 == Character.LINE_SEPARATOR) || (I_char_type_1 == Character.CONTROL)) {
		   I_len_trailing_newlines_f1++;
		
		   I_pos_trail_2_f0 = I_pos_trail_1_f0 - 1;
		   c_trail_2 = PI_S_line.charAt(I_pos_trail_2_f0);
		 
		   I_char_type_2 = Character.getType(c_trail_2);
		   if ((I_char_type_2 == Character.LINE_SEPARATOR) || (I_char_type_2 == Character.CONTROL)) {
			  I_len_trailing_newlines_f1++;
			 }} 
	   I_pos_trim_end_f1 = I_len_line_old_f1 - I_len_trailing_newlines_f1;
	   
	   S_line_dos = PI_S_line.substring(0, I_pos_trim_end_f1);
	   S_retval = S_line_dos + LF;
	   
	   return S_retval;
   }
   
   //------------------------------------------------------
   public static String FS_truncate_input_line (String PI_S_line) {
   // cut off tail of input string by preserving up to 2 trailing newline characters;	   
	   String S_retval;
	   StringBuffer SB_shrinked;
	   int I_len_line_old_f1, I_pos_trail_1_f0, I_pos_trail_2_f0, 
	   I_char_type_1,  I_char_type_2, 
	   I_len_trailing_newlines_f1, I_pos_delete_end_f1,
	       I_len_head_f1;
	    
	   char c_trail_1, c_trail_2;
	   
	   S_retval = PI_S_line;
	   I_len_line_old_f1 = PI_S_line.length();
	   if (I_len_line_old_f1 <= I_max_line_len_stored_f1) {
		   return S_retval; }
	   
	
	   I_len_trailing_newlines_f1 = 0;
	   I_pos_trail_1_f0 = I_len_line_old_f1 - 1;  
	   c_trail_1 = PI_S_line.charAt(I_pos_trail_1_f0);
	   I_len_trailing_newlines_f1 = 0;
	   I_char_type_1 = Character.getType(c_trail_1);
	   if ((I_char_type_1 == Character.LINE_SEPARATOR) || (I_char_type_1 == Character.CONTROL)) {
		   I_len_trailing_newlines_f1++;
		
		   I_pos_trail_2_f0 = I_pos_trail_1_f0 - 1;
		   c_trail_2 = PI_S_line.charAt(I_pos_trail_2_f0);
		 
		   I_char_type_2 = Character.getType(c_trail_2);
		   if ((I_char_type_2 == Character.LINE_SEPARATOR) || (I_char_type_2 == Character.CONTROL)) {
			  I_len_trailing_newlines_f1++;
			 }} 

	   I_pos_delete_end_f1 = I_len_line_old_f1 - I_len_trailing_newlines_f1; 
	   
	   SB_shrinked = new StringBuffer(PI_S_line);
	   I_len_head_f1 = I_max_line_len_stored_f1 - I_len_trailing_newlines_f1;
	   SB_shrinked.delete(I_len_head_f1, I_pos_delete_end_f1);  
	   S_retval = SB_shrinked.toString(); 
	   return S_retval;
   }
   
   //---------------------------------------------------
   
   public static void FV_print_log (
			
			String         PI_S_line,
			String         PI_S_prefix,
			PrintStream    PI_AO_files[]) {
	
	PrintStream  O_prt_stream;
	int I_nbr_fh_f1, i1;
	String S_prefix, S_line_out, S_line_dos;
						
	I_nbr_fh_f1 = PI_AO_files.length;
	
	if (I_nbr_fh_f1 >= 1) {
	   if (PI_S_prefix == null) {
		  S_prefix = ""; }
	   else {
		  S_prefix = PI_S_prefix; }
	   
	//   S_line_dos = FS_unix_2_dos(PI_S_line);
	//   S_line_out = S_prefix + S_line_dos; 
	   
	   S_line_out = S_prefix + PI_S_line;
	   for (i1 = 0; i1 < I_nbr_fh_f1; i1++) {
		   O_prt_stream = PI_AO_files[i1];
		   O_prt_stream.println(S_line_out); }}
	
	return; }
   

//----------------------------------
	public static String[] FAS_concat_arrays(
			String[] PI_AS_1, String[] PI_AS_2, boolean B_deep) {
				
		String AS_retval[], s1, s2;
		int I_size_1_f1, I_size_2_f1, I_size_3_f1, i1, i3;
			
		AS_retval = ToolsBasics.AS_no_strings;
		
		I_size_1_f1 = PI_AS_1.length;
		I_size_2_f1 = PI_AS_2.length;
		I_size_3_f1 = I_size_1_f1 + I_size_2_f1;
		if (I_size_3_f1 == 0) {
			return AS_retval; }
		
		AS_retval = new String[I_size_3_f1];
		i3 = 0;
		for (i1 = 0; i1 < I_size_1_f1; i1++) {
			s1 = PI_AS_1[i1];
			if (B_deep == true) {
				s2 = new String(s1); }
			else {
				s2 = s1; }
			AS_retval[i3] = s2; 
			i3++; }
		for (i1 = 0; i1 < I_size_2_f1; i1++) {
			s1 = PI_AS_2[i1];
			if (B_deep == true) {
				s2 = new String(s1); }
			else {
				s2 = s1; }
			AS_retval[i3] = s2; 
			i3++; }
		
		return AS_retval; }
//	----------------------------------
/**
 * 	
 * @param PI_I_msecs millisecs to wait
 * 
 */
public static void FV_sleep(final int PI_I_msecs) {
	
	String S_msg_1;
	
	try {
		Thread.sleep(PI_I_msecs);
	}
	catch (IllegalArgumentException|InterruptedException PI_E_int) {
		S_msg_1 = "Error: " + PI_E_int.getClass().getName() + " occured when waiting " + PI_E_int + "mesecs";
		System.err.println(S_msg_1);
	} 
}
	
}