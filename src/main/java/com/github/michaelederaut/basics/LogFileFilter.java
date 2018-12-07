package com.github.michaelederaut.basics;

import static org.apache.commons.lang3.StringUtils.LF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Stack;
// import java.util.regex.Pattern;
import regexodus.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.ReflectionUtils;

import com.github.michaelederaut.basics.ToolsBasics;
import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;

//import tools.exerc.basics.ToolsBasics.LogFileFilter.FileType;


	public  class LogFileFilter implements FilenameFilter {
		
	//	public static final File[] AF_no_files = new File[0];
		public static final File[] AF_no_files = new File[] {};
		
		public  static Field O_field = ReflectionUtils.getFieldByNameIncludingSuperclasses("PAD_LIMIT", StringUtils.class);
		public static int I_io_buf_size_f1 = 0;
		static {
			try {
				  O_field.setAccessible(true);
				  I_io_buf_size_f1 = O_field.getInt(StringUtils.class);
			} catch (IllegalArgumentException|IllegalAccessException PI_E_ill_arg) {
				PI_E_ill_arg.printStackTrace(System.err);
				System.exit(1);
			   }
		}

		public static final byte AC_io_buffer[] = new byte[I_io_buf_size_f1];
		
		public enum FileType {regular, regular_nonempty, dir, any};
		
		public static final String S_regexp_zipped = "\\A(.*?)\\.gz\\Z";
		public static final Pattern P_regexp_zipped = Pattern.compile(S_regexp_zipped);
		
		public Pattern O_pattern; 
	    public FileType E_file_type;
	    public Stack<GroupMatchResult> AO_match_results = new Stack<GroupMatchResult>();
		   
	    public LogFileFilter (Pattern PI_O_pattern, FileType PI_E_file_type) {
	    	   E_file_type = PI_E_file_type;
			   O_pattern = PI_O_pattern; 
		   }
		
		public boolean accept(File PI_O_dir, String PI_S_bn) {
			boolean B_retval;
			long I_file_size;
			String S_pn_full, S_dn_parent;
			File F_pn_full;
			GroupMatchResult O_group_match_result;
			
			B_retval = false;
			O_group_match_result = RegexpUtils.FO_match(
					PI_S_bn,
					O_pattern);
				
			if (O_group_match_result.I_array_size_f1 > 0) {
				if (E_file_type == FileType.any) {
				   B_retval = true; }
				else {
				   S_dn_parent = PI_O_dir.getPath();
	       		   S_pn_full = S_dn_parent + ToolsBasics.FS + PI_S_bn;
	       		   F_pn_full = new File(S_pn_full);
	               if ((E_file_type == FileType.regular) || (E_file_type == FileType.regular_nonempty)) {
	            	  if (F_pn_full.isFile()) {
	            		  if (E_file_type == FileType.regular) {
	            		     B_retval = true; }
	            	      else {
	            	    	  I_file_size = F_pn_full.length();
	            	    	  if (I_file_size > 0) {
	            	    		  B_retval = true; }}}}     
	               else if (E_file_type == FileType.dir) {
	            	   if (F_pn_full.isDirectory()) {
	             		  B_retval = true; }}}
		    	if (B_retval == true) {
			       AO_match_results.push(O_group_match_result); }}
			return B_retval; 
			}
//		-------
		
		public static File[] FAF_get_files_of_dir(
				File PI_F_dir,
				Pattern PI_O_pattern,
				LogFileFilter.FileType PI_E_file_type) {
			
			File AF_retval[];
			LogFileFilter O_filter;
			
			AF_retval = LogFileFilter.AF_no_files;
		
			O_filter = new LogFileFilter(PI_O_pattern, PI_E_file_type);
			AF_retval = PI_F_dir.listFiles(O_filter);
				
			return AF_retval;
		}
//		 ----------------------	
		
		public static long FL_gunzip (File PI_F_zipped) {
			
			String S_dirname, S_basename_zipped, S_basename_unzipped,
			       S_path_zipped, S_path_unzipped,
			       S_msg_1, S_msg_2;
			AssertionError E_assert;
			Throwable E_cause;
			GroupMatchResult O_match;
			
			FileInputStream F_inp_zipped;
			GZIPInputStream O_inp_zipped;
			File F_file_unzipped;
			FileOutputStream F_out_unzipped; 
		
			boolean B_outfile_created, B_outfile_success;
			long L_retval, L_file_size;
			int I_nbr_bytes_read_f1;
		
			L_retval = 0;
			B_outfile_success = false;
			B_outfile_created = false;
			
			S_basename_zipped = PI_F_zipped.getName();
			S_dirname = PI_F_zipped.getParent();
			S_path_zipped = PI_F_zipped.getPath();
			
		//	O_match = FO_match("\\A(.*?)\\.gz\\Z", true, S_basename_zipped , false);
			O_match = RegexpUtils.FO_match( S_basename_zipped, P_regexp_zipped);
			if (O_match.I_array_size_f1 < 2) {
			    S_msg_1 = 	"Invalid pathname for zipped file: \"" + S_path_zipped + "\"";
			    E_assert = new AssertionError(S_msg_1);
			    throw E_assert;	}
			
			S_basename_unzipped = O_match.AS_numbered_groups[1];
			S_path_unzipped = S_dirname + ToolsBasics.FS + S_basename_unzipped;
			F_file_unzipped = new File(S_path_unzipped);
			if (F_file_unzipped.isFile()) {
			   L_file_size = F_file_unzipped.length();
			   if (L_file_size > 0) {
			       S_msg_1 =
			      "WARNING: File \"" + S_basename_zipped + 
			      "\" not unzipped, because a corresponding decompressed target aleardy exists in the same directory with size: " +
			      L_file_size;
			      System.out.println (S_msg_1);
				  return L_retval; }}
			else {
				try {
					B_outfile_created = F_file_unzipped.createNewFile(); } 
				catch (IOException E_io) {
					S_msg_1 = "Trying to create a new empty uncompressed file: \"" + S_path_unzipped + "\"";
					E_cause = new Throwable(S_msg_1);
					E_io.initCause(E_cause);
					E_io.printStackTrace(System.err);
					System.exit(1); }}
			try {
			  F_inp_zipped = new FileInputStream(PI_F_zipped); }
			catch (FileNotFoundException E_fnf) {
			   S_msg_1 = "Trying to create a FileInputStream object from: \"" + S_path_zipped + "\"";
			   E_cause = new Throwable(S_msg_1);
			   E_fnf.initCause(E_cause);
			   E_fnf.printStackTrace(System.err);
			   F_inp_zipped = null; // for performance reasons 
			   System.exit(1); }
		   	
			 try {
				 O_inp_zipped = new GZIPInputStream(F_inp_zipped, I_io_buf_size_f1) ;}
			 catch (IOException E_io) {
				 S_msg_1 = "Trying to create a GZIPInputStream object from: \"" + S_path_zipped + "\" with buffer-size: " + I_io_buf_size_f1;
				 E_cause = new Throwable(S_msg_1);  
				 E_io.initCause(E_cause);
				 E_io.printStackTrace(System.err);
				 O_inp_zipped = null; // for performance reasons 
				 System.exit(1); }
			 try { 
				F_out_unzipped = new FileOutputStream(F_file_unzipped) ;}
			 catch (FileNotFoundException E_fnf) {
				S_msg_1 = "Trying to create FileOutputStream Object from: \"" + S_path_unzipped + "\"";
				E_cause = new Throwable(S_msg_1);
				E_fnf.initCause(E_cause);
				E_fnf.printStackTrace(System.err);
				F_out_unzipped = null; // for performance reasons
				System.exit(1); }
			 
			 try {
				 while ((I_nbr_bytes_read_f1 = O_inp_zipped.read(AC_io_buffer)) > 0) {
					 F_out_unzipped.write(AC_io_buffer, 0, I_nbr_bytes_read_f1);
					 L_retval += I_nbr_bytes_read_f1;  }}
			 catch (IOException E_io) {
				S_msg_1 = "An I/O error occurred after transferring " + L_retval + " bytes from" + LF +
				         "zipped   file: " + S_basename_zipped + "  to" + LF +
				         "unzipped file: " + S_basename_unzipped + LF +
				         "within directory: " + S_dirname;
				E_cause = new Throwable(S_msg_1);
				E_io.initCause(E_cause);
				E_io.printStackTrace(System.err);
				System.exit(1); }	
			try {
				O_inp_zipped.close(); } 
			catch (IOException E_io) {
				S_msg_1 = "Unable to close file: \"" + S_path_zipped + "\" after reading";
				E_cause = new Throwable(S_msg_1);
				E_io.initCause(E_cause);
				E_io.printStackTrace(System.err);
				System.exit(1); }
			try {
			  F_out_unzipped.close(); } 
			catch (IOException E_io) {
				S_msg_1 = "Unable to close file: \"" + S_path_zipped + "\" after writing";
				E_cause = new Throwable(S_msg_1);
				E_io.initCause(E_cause);
				E_io.printStackTrace(System.err);
				System.exit(1); }
			
			S_msg_1 = "Now deleting: \"" + S_path_zipped + "\"";
			System.out.println(S_msg_1);
			PI_F_zipped.delete(); // D E L E T E   O R I G I N A L   Z I P P E D   F I L E
			
			return L_retval;
		}	

	//----------------

	public static File[] FF_unzip_files_of_dir(
			File PB_F_dir,
			String PI_S_pattern_unzipped)

	{
		String S_pattern_zipped_qm, S_pn_zipped, S_pn_unzipped, AS_matching_groups[],
		       S_pattern_head, S_pattern_zipped, S_pattern_unzipped;
		Pattern P_pattern_zipped;
		boolean B_ends_with_eos;
		int I_nbr_files_zipped_f1, i1, I_nbr_files_unzipped_f1, I_len_pattern_f1, I_end_idx;
		long L_len_unzipped;
		
		Stack<File> AF_unzipped;
		File AF_retval[], AF_zipped[], F_zipped, F_unzipped;
		GroupMatchResult O_match_res;
		
		AF_retval = new File[0];

		if (PI_S_pattern_unzipped.endsWith("\\Z")) {
			I_len_pattern_f1 = PI_S_pattern_unzipped.length();
			B_ends_with_eos = true;
			I_end_idx = I_len_pattern_f1 - 2;
			S_pattern_head = PI_S_pattern_unzipped.substring(0, I_end_idx); }
		else {
			B_ends_with_eos = false;
			S_pattern_head = PI_S_pattern_unzipped; }
		
		S_pattern_zipped_qm = S_pattern_head + "\\.gz";
		if (B_ends_with_eos) {
			S_pattern_zipped_qm = S_pattern_zipped_qm + "\\Z"; }
		P_pattern_zipped = Pattern.compile(S_pattern_zipped_qm);
		
		AF_zipped = FAF_get_files_of_dir(
				PB_F_dir,
				P_pattern_zipped,
				LogFileFilter.FileType.regular);
		
		I_nbr_files_zipped_f1 = AF_zipped.length;
		if (I_nbr_files_zipped_f1 == 0) {
			return AF_retval; }
		
		AF_unzipped = new Stack<File>();
		for (i1 = 0; i1 < I_nbr_files_zipped_f1; i1++) {
			F_zipped = AF_zipped[i1];
			L_len_unzipped = FL_gunzip(F_zipped);
			if (L_len_unzipped > 0) {
				S_pn_zipped = F_zipped.getPath();
				O_match_res = RegexpUtils.FO_match(S_pn_zipped, P_regexp_zipped);
				if (O_match_res.I_array_size_f1 >= 2) {
					AS_matching_groups = O_match_res.AS_numbered_groups;
					S_pn_unzipped = AS_matching_groups[1];
					F_unzipped = new File(S_pn_unzipped);
					if (F_unzipped.isFile()) {
						AF_unzipped.push(F_unzipped); }}}}
		
		I_nbr_files_unzipped_f1 = AF_unzipped.size();
		AF_retval = new File[I_nbr_files_unzipped_f1];
		AF_retval = AF_unzipped.toArray(AF_retval);
		return AF_retval;
	}	
	

}
