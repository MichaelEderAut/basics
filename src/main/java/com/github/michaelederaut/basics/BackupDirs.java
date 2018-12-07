package com.github.michaelederaut.basics;

import static com.github.michaelederaut.basics.ToolsBasics.FS;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
// import java.util.regex.Pattern;
import regexodus.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.michaelederaut.basics.ToolsBasics;
import com.github.michaelederaut.basics.RegexpUtils.*;

import org.apache.commons.io.FileUtils;

public class BackupDirs {
	
	public static final int MIN_ARITY = 1;
	public static final int MAX_ARITY = 5;
	public static final String S_suffix = ".bak";
	public static final String S_suffix_quoted = java.util.regex.Pattern.quote(S_suffix);
	
	public int I_arity = MIN_ARITY;
	public String S_max_serial = StringUtils.repeat('9', I_arity);
	
	public  String S_regexp_backup_rel_dir_name, S_regexp_backup_gen_rel_dir_name, S_fmt_string;
	public  Pattern P_backup_rel_dir_name, P_backup_gen_rel_dir_name;

	public String S_infix, S_infix_quoted;
	 
	public static BackupDirs FO_ctor(
			BackupDirs PI_O_Backup_Dirs,
			final int PI_I_arity,
			final String PI_S_infix) {
		
		BackupDirs O_retval_backup_dirs;
		
		O_retval_backup_dirs = PI_O_Backup_Dirs;
		
		IllegalArgumentException E_ill_arg;
		String S_msg_1, S_infix_quoted;
		
		if (PI_S_infix == null) {
			S_infix_quoted = "";
			PI_O_Backup_Dirs.S_infix = "";
		    }
		else {
			PI_O_Backup_Dirs.S_infix = PI_S_infix;
			if (StringUtils.isBlank(PI_S_infix)) {
			   S_infix_quoted = PI_S_infix; 	
		       }
		   else {
			  S_infix_quoted = java.util.regex.Pattern.quote(PI_S_infix);
		        }
		      }
		
		if ((PI_I_arity < MIN_ARITY) || (PI_I_arity > MAX_ARITY)) {
			S_msg_1 = "Arity " + PI_I_arity + " out of boundaries " + MIN_ARITY + " - " + MAX_ARITY + ".";
			E_ill_arg = new IllegalArgumentException(S_msg_1);
			throw E_ill_arg;
		}
		
		PI_O_Backup_Dirs.I_arity =  PI_I_arity;
		PI_O_Backup_Dirs.S_max_serial = StringUtils.repeat('9', PI_O_Backup_Dirs.I_arity);
		// perl example:    p int('aaaa,-.bak.00001' =~ m/^([\w\.\,\-]+?)(\.bak\.\d{5})$/)
		// 1
		
		PI_O_Backup_Dirs.S_infix_quoted = S_infix_quoted;		
 		
//		PI_O_Backup_Dirs.S_regexp_backup_rel_dir_name = "^([\\w\\.\\,\\-]+?)" + S_infix_quoted + "(\\.(\\d{" + PI_O_Backup_Dirs.I_arity + "})\\.bak)$";
		PI_O_Backup_Dirs.S_regexp_backup_rel_dir_name = "^([\\w\\.\\,\\-]+?)" + S_infix_quoted + "(\\.(\\d{" + PI_O_Backup_Dirs.I_arity + "})" + S_suffix_quoted + ")$";
		PI_O_Backup_Dirs.P_backup_rel_dir_name = Pattern.compile(PI_O_Backup_Dirs.S_regexp_backup_rel_dir_name);
		
	//	PI_O_Backup_Dirs.S_regexp_backup_gen_rel_dir_name = "^([\\w\\.\\,\\-]+?)" + S_infix_quoted + "(\\.\\d{" + MIN_ARITY + "," + MAX_ARITY + "}\\.bak)$";
		PI_O_Backup_Dirs.S_regexp_backup_gen_rel_dir_name = "^([\\w\\.\\,\\-]+?)" + S_infix_quoted + "(\\.\\d{" + MIN_ARITY + "," + MAX_ARITY + "}" +  S_suffix_quoted + ")$";
		PI_O_Backup_Dirs.P_backup_gen_rel_dir_name = 
				Pattern.compile(PI_O_Backup_Dirs.S_regexp_backup_gen_rel_dir_name);
		PI_O_Backup_Dirs.S_fmt_string = "%0" + PI_I_arity + "d";
		return O_retval_backup_dirs;
		
		
	}
	public BackupDirs (final int PI_I_arity) {
		
		FO_ctor(this, PI_I_arity, "");
		
	}

    public BackupDirs (final int PI_I_arity, final String PI_S_infix) {
		
		FO_ctor(this, PI_I_arity, PI_S_infix);
		
	}
	
	public  boolean B_is_backupdir(final String PI_S_dn_dir_to_backup) {
		
		NullPointerException E_np;
		IllegalArgumentException E_ill_arg;
		
	    GroupMatchResult O_group_match_result;
		File F_dir_to_backup;
		
		String S_msg_1, S_msg_2, S_dnr_leaf;
		
		boolean B_retval;
		
		B_retval = false;
		
		if (PI_S_dn_dir_to_backup == null) {
			S_msg_1 = "Directory-name must not be null.";
			E_np = new NullPointerException(S_msg_1);
			S_msg_2 = "Invalid backup-directory-name";
			E_ill_arg = new IllegalArgumentException(S_msg_2, E_np);
			throw E_ill_arg;
		}
		
		F_dir_to_backup = new File(PI_S_dn_dir_to_backup);
		S_dnr_leaf = F_dir_to_backup.getName();
		if (S_dnr_leaf == null) {
			S_msg_1 = "Backup-directory-name must not be null.";
			E_np = new NullPointerException(S_msg_1);
			throw E_np;
		}
		O_group_match_result = RegexpUtils.FO_match(S_dnr_leaf, this.P_backup_gen_rel_dir_name);
		if (O_group_match_result != RegexpUtils.NO_MATCH) {
			B_retval = true;
		    }
		
		return B_retval;
	    }
	
	public String FS_get_next_backup_dir (
			final String PI_S_dn_dir_to_backup,
			final StringBuffer PB_S_dn_backup_target_repository) {
		
		IOException E_io;
		RuntimeException E_rt;
		NullPointerException E_np;
		AssertionError E_assert;
		GroupMatchResult O_grp_match_result;
		
		File F_dnr_backup_source, AF_dna_res_backup[], F_dna_res_backup, F_dn_backup_target ;
		String S_msg_1, S_msg_2, S_dnr_retval, S_dnr_backup, S_dnr_res_backup, S_serial_f1, S_dn_backup_target_repository;
		int i1, I_nbr_backup_dirs_f1, I_nbr_backup_dirs_f0;
		int I_serial_f1;
		
		S_dnr_retval = null;
		
		if (PB_S_dn_backup_target_repository == null) {
			S_msg_1 = "2nd Parameter of type \'" + StringBuffer.class.getName() + "\' for target-repository must not be null";
			E_np = new NullPointerException(S_msg_1);
			throw E_np;
		}
		
		S_dn_backup_target_repository = PB_S_dn_backup_target_repository.toString();
		F_dnr_backup_source = new File(PI_S_dn_dir_to_backup);
		if (!F_dnr_backup_source.isDirectory()) {
		   S_msg_1 = "Unable to find directory path name: \"" + PI_S_dn_dir_to_backup + "\" .";
		   E_io = new IOException(S_msg_1);
		   S_msg_2 = "Unable to determine directory to be backed up: \"" + PI_S_dn_dir_to_backup + "\" .";
		   E_rt = new RuntimeException(S_msg_2, E_io);
		   throw E_rt;
		   }
		
		if (B_is_backupdir(PI_S_dn_dir_to_backup)) {
			PB_S_dn_backup_target_repository.setLength(0);
			S_msg_1 = "Source Directory: \"" + PI_S_dn_dir_to_backup + "\" is already a backup-direrctory.";
			E_rt = new RuntimeException(S_msg_1);
			E_rt.printStackTrace(System.err);
			return S_dnr_retval;
		   }
		
		if (StringUtils.isEmpty(S_dn_backup_target_repository)) {
			// backup into the same folder as source dir, as no target repository is specified by 2nd input parameter
			F_dn_backup_target = F_dnr_backup_source.getParentFile();
			if (F_dn_backup_target == null) {
			 	S_msg_1 = "No parent found for directory: \"" + PI_S_dn_dir_to_backup + "\"";
			 	E_io = new IOException(S_msg_1);
			 	S_msg_2 = "Unable to determine parent folder of: \"" + PI_S_dn_dir_to_backup + "\" for backup-dirs.";
			 	E_rt = new RuntimeException(S_msg_2, E_io);
			 	throw E_rt;
			    }
			S_dn_backup_target_repository = F_dn_backup_target.getPath();
			PB_S_dn_backup_target_repository.setLength(0);
			PB_S_dn_backup_target_repository.append(S_dn_backup_target_repository);
       	   }
		else {
			// target repository specified by 2nd input parameter
			if (B_is_backupdir(S_dn_backup_target_repository)) {
				PB_S_dn_backup_target_repository.setLength(0);
				S_msg_1 = "Target Directory: \"" + PB_S_dn_backup_target_repository + "\" is already a backup-direrctory.";
				E_rt = new RuntimeException(S_msg_1);
				E_rt.printStackTrace(System.err);
				return S_dnr_retval;
			    }
			F_dn_backup_target = new File(S_dn_backup_target_repository);
			if (!F_dn_backup_target.isDirectory()) {
				S_msg_1 = "Unable to find directory path name: \"" + PB_S_dn_backup_target_repository + "\" .";
				E_io = new IOException(S_msg_1);
				S_msg_2 = "Unable to determine backup target directory for: \"" + PI_S_dn_dir_to_backup + "\" .";
				E_rt = new RuntimeException(S_msg_2, E_io);
				throw E_rt;
				}
		    }
		S_dnr_backup =  F_dnr_backup_source.getName();
		I_nbr_backup_dirs_f1 = 0;
		AF_dna_res_backup = LogFileFilter.FAF_get_files_of_dir(
				F_dn_backup_target, this.P_backup_rel_dir_name, LogFileFilter.FileType.dir);
		if (AF_dna_res_backup.length == 0) {
			S_dnr_retval = S_dnr_backup + "." + String.format(this.S_fmt_string, 1) + S_suffix; 
		    }
		else {
			I_nbr_backup_dirs_f1 = AF_dna_res_backup.length;
			I_nbr_backup_dirs_f0 = I_nbr_backup_dirs_f1 - 1;
			LOOP_DIRS: for (i1 = 0; i1 < I_nbr_backup_dirs_f1; i1++) {
				F_dna_res_backup = AF_dna_res_backup[i1];
				S_dnr_res_backup = F_dna_res_backup.getName();
				O_grp_match_result = RegexpUtils.FO_match(S_dnr_res_backup, P_backup_rel_dir_name);  
				if (O_grp_match_result.I_array_size_f1 < 4) {
					S_msg_1 = "Index: " + i1 + " - relative directory-name \'" + S_dnr_res_backup + 
							  "\' doesn't match the regular expression: \'" + P_backup_rel_dir_name.toString() + "\'";
					E_assert = new AssertionError(S_msg_1);
					S_msg_2 = "Inconsistent backup-dirs in folder \"" + F_dn_backup_target.getPath() + "\"";
					E_rt = new RuntimeException(S_msg_2, E_assert);
					throw E_rt;
				    }
				S_serial_f1 = O_grp_match_result.AS_numbered_groups[3];
				if (S_serial_f1.equals(this.S_max_serial)) {
					S_msg_1 = "Index: " + i1 + " - Serial number " + S_serial_f1 +  " of existing relative directory-name \'" + S_dnr_res_backup + 
							  "\' has reached already the maximum value: \'" + this.S_max_serial;
					E_assert = new AssertionError(S_msg_1);
					S_msg_2 = "Possible overflow (for arity: " + this.I_arity +  ") of subdirs in folder \"" + F_dn_backup_target.getPath() + "\"";
					E_rt = new RuntimeException(S_msg_2, E_assert);
					throw E_rt;
					
				}
				I_serial_f1 = ToolsBasics.FI_parse_decimal(S_serial_f1);
				I_serial_f1++;
				S_dnr_retval = S_dnr_backup + "." + String.format(this.S_fmt_string, I_serial_f1) + S_suffix; 
			}
		}
		return S_dnr_retval;
	}
	
	//------------------
	
		
		public int FI_copy(
				final String PI_S_dn_dir_to_backup,
				final StringBuffer PB_S_dn_backup_target_repository) {
			 
			GroupMatchResult O_grp_match_result;
			IOException E_io;
			RuntimeException E_rt;
			
			int I_retval;
			String S_msg_1, S_msg_2, S_dnr_res_backup, S_dn_backup_to, S_dn_backup_target_repository, S_serial_f1;
			File F_dna_backup_from, F_dna_backup_to;
			
			I_retval = 0;
			
			S_dnr_res_backup = FS_get_next_backup_dir(PI_S_dn_dir_to_backup, PB_S_dn_backup_target_repository);
			if (S_dnr_res_backup != null) {
				O_grp_match_result = RegexpUtils.FO_match(S_dnr_res_backup, P_backup_rel_dir_name);
				
//				if (O_grp_match_result.I_array_size_f1 < 4) {
//			       }
				S_dn_backup_target_repository = PB_S_dn_backup_target_repository.toString();
				F_dna_backup_from = new File(PI_S_dn_dir_to_backup);
				
				S_dn_backup_to = S_dn_backup_target_repository + FS + S_dnr_res_backup;
	        	F_dna_backup_to = new File(S_dn_backup_to);
	        	try {
					FileUtils.copyDirectory(F_dna_backup_from, F_dna_backup_to);
				} catch (IOException PI_E_io) {
					S_msg_1 = "Unable to copy directory: \"" + F_dna_backup_from.getPath() +
					"\" to new directory \"" + F_dna_backup_to.getPath();
					E_io = new IOException(S_msg_1, PI_E_io);
					S_msg_2 = "Unable to copy directory: \"" + F_dna_backup_from.getPath() +
							"\" into existing folder \"" + PB_S_dn_backup_target_repository.toString();
							E_io = new IOException(S_msg_1, PI_E_io);
					E_rt = new RuntimeException(S_msg_1, E_io);
					throw E_rt;
				}
				
			    S_serial_f1 = O_grp_match_result.AS_numbered_groups[3];
			    I_retval = ToolsBasics.FI_parse_decimal(S_serial_f1);
		
			}
			
			return I_retval;
		}
	
}
