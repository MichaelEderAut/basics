package com.github.michaelederaut.basics;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.SwingWorker;  // not relevant for windows OS

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import regexodus.Pattern;

import com.github.michaelederaut.basics.StreamUtils.RangedPattern;
import static org.apache.commons.lang3.SystemUtils.FILE_SEPARATOR;

public class ExecUtils {
	public static final Long L_timeout_stderr = 500L; //;  100_000_000
	protected static final String S_re_end_request = "^(.+)$";
	protected static final String S_re_end_session = "^(.+)$";
	protected static final Pattern P_end_request = Pattern.compile(S_re_end_request);
	protected static final Pattern P_end_session = Pattern.compile(S_re_end_session);
	
	// TODO
	public static final StreamUtils.EndCriterion O_end_criterion_stderr = 
			new StreamUtils.EndCriterion(
					L_timeout_stderr,
			        new RangedPattern[] {new RangedPattern(P_end_request)},
			        P_end_session);
	
	public static final long MAX_TIMEOUT_DFLT = 120_000;
	
	public static String S_dn_parent;
    public static SwingWorker<Void, Void> worker = null;
    public static ProcessBuilder pb = new ProcessBuilder();
    public static Process O_proc = null;
    
    public static boolean B_kill     = false;
    public static boolean B_by_force = false;
    public static String  S_pna_exec = null;
    public static Stack<Long> AL_pids = new Stack<Long>();

    public static class ExecResult {
    	public List<String>[] AAS_retvals = null;
    	public Process O_proc;
    	public int I_exit_code = -1;
    }
    
    public  static ExecResult FAAS_exec_sync (
			  final String PI_S_cmd,
			  final String PI_AS_envp[],
			  final File PI_F_wd) {
		  
		 ExecResult O_retval_exec_result;
		 String AS_cmd[];
		 
		 
		 AS_cmd = new String[] {PI_S_cmd};
		 
		 O_retval_exec_result = FAAS_exec_sync (
			 AS_cmd,
			 PI_AS_envp,
			 PI_F_wd,
		    (StreamUtils.EndCriterion)null) ;
				 
			 return O_retval_exec_result;
		 }	

//    * @param PI_L_timeout_proc long timeout in millisecs for the process (0L : wait forever)
//    * @param PI_L_timeout_stdout long timeout in millisecs for the stdout, stderr of the process (0L : wait forever)   
    
    /**
     * 
     * @param PI_AS_cmd {@link String String[]} command to execute.
     * @param PI_AS_envp  {@link String String[]} environment variables
     * @param PI_F_wd {@link File} set working dir (null : current) 
     * @return ExecResult
     */
	public  static ExecResult FAAS_exec_sync (
			  final String PI_AS_cmd[],
			  final String PI_AS_envp[],
			  final File PI_F_wd) {
		  
		 ExecResult O_retval_exec_result;
		  
		 O_retval_exec_result = FAAS_exec_sync (
			 PI_AS_cmd,
			 PI_AS_envp,
			 PI_F_wd,
		    (StreamUtils.EndCriterion)null) ;
				 
			 return O_retval_exec_result;
		 }	

    /**
     * 
     * @param PI_AS_cmd {@link String String[]} command to execute.
     * @param PI_AS_envp  {@link String String[]} environment variables
     * @param PI_F_wd {@link File} set working dir (null : current) 
     * @param PI_L_timeout long timeout in millisecs for the stdout, stderr of the process (0L : wait forever)   
     * 
     * @return ExecResult
     */
	public  static ExecResult FAAS_exec_sync (
			  final String PI_AS_cmd[],
			  final String PI_AS_envp[],
			  final File PI_F_wd,
			  Long  PI_L_timeout) {
		  
		 ExecResult O_retval_exec_result;
		 StreamUtils.EndCriterion O_end_crit; 
		 
		 O_end_crit = new StreamUtils.EndCriterion(PI_L_timeout);
		 
		 O_retval_exec_result = FAAS_exec_sync (
			 PI_AS_cmd,
			 PI_AS_envp,
			 PI_F_wd,
			 O_end_crit) ;
				 
			 return O_retval_exec_result;
		 }	
	
	 /**
     * 
     * @param PI_AS_cmd {@link String String[]} command to execute.
     * @param PI_AS_envp  {@link String String[]} environment variables
     * @param PI_F_wd {@link File} set working dir (null : current) 
     * @param PB_O_end_criterion long StreamUtils.EndCriterion Criterion to terminate request session
     * @return ExecResult
     */	 
public  static ExecResult FAAS_exec_sync (
		  final String PI_AS_cmd[],
		  final String PI_AS_envp[],
		  final File PI_F_wd,
		  final StreamUtils.EndCriterion PB_O_end_criterion) {
	  
	  ExecResult O_retval_exec_result;  
	  	  
	  O_retval_exec_result = FAAS_exec_sync (
			  PI_AS_cmd,
			  PI_AS_envp,
			  PI_F_wd,
			  PB_O_end_criterion,  
			  0L);
			  
	  return O_retval_exec_result;
  }
	
    /**
     * 
     * @param PI_AS_cmd {@link String String[]} command to execute.
     * @param PI_AS_envp  {@link String String[]} environment variables
     * @param PI_F_wd {@link File} set working dir (null : current) 
     * @param PB_O_end_criterion end/timeout criteria for the proecess output streams
     * @param PI_L_timeout_proc long timeout in millisecs for the process (0L : wait forever)<br>
     *        null: don't wait at all
     * @return ExecResult
     */
	public static ExecResult FAAS_exec_sync (
			  final String PI_AS_cmd[],
			  final String PI_AS_envp[],
			  final File PI_F_wd,
			  final StreamUtils.EndCriterion PB_O_end_criterion,
			  final Long PI_L_timeout_proc) {
		  
		  RuntimeException E_rt_1, E_rt_2;
		  NullPointerException E_np;
		  IllegalArgumentException E_ill_arg;
		  Exception E_ex;
		  InputStream O_stream_out, O_stream_err;
//		  BufferedReader O_buff_rdr_out, O_buff_rdr_err;
		  
		  Runtime        O_runtime;
		  ProcessBuilder O_proc_builder;
		  Process        O_proc;
		  
		 String S_msg_1, S_msg_2, S_cmd_diagnostics;
	     boolean B_finished_ok, B_is_alive;
		 ExecResult O_retval_exec_result;
		  
		 O_retval_exec_result =  new ExecResult();
		
		E_rt_2 = null;  
		S_cmd_diagnostics = StringUtils.join(PI_AS_cmd, ' ');
		if ((PI_AS_cmd == null) || (PI_AS_cmd.length == 0) || (PI_AS_cmd[0] == null)) {
			S_msg_2 = "command-string null";
			E_rt_2 = new NullPointerException(S_msg_2);
		    }
		else { 
			if (StringUtils.isBlank(PI_AS_cmd[0])) {
			   S_msg_2 = "command-string blank";
			   E_rt_2 = new IllegalArgumentException(S_msg_2);
		       }
		    }
		if (E_rt_2 != null) {
			S_msg_1 = "Invalid Command-String \"" + S_cmd_diagnostics + "\" to execute";
			E_rt_1 = new RuntimeException (S_msg_1, E_rt_2);
			throw E_rt_1;
		     }
		
		if (PI_F_wd != null) {
			if (! PI_F_wd.isDirectory()) {
			    S_msg_1 = "Invalid Directory: \"" + PI_F_wd.toPath() + "\"";
			    E_rt_1 = new RuntimeException(S_msg_1);
			    throw E_rt_1;
			    }
		      }
		//	  O_runtime = Runtime.getRuntime();
		try {
	//		O_proc = O_runtime.exec(PI_AS_cmd, PI_AS_envp, PI_F_wd);
			
			O_proc_builder = new ProcessBuilder(PI_AS_cmd);
			O_proc_builder = O_proc_builder.directory(PI_F_wd);
			// O_proc_builder.environment(PI_AS_envp);
			 O_proc_builder = O_proc_builder.redirectError(Redirect.PIPE);  // PIPE = default value
			 O_proc_builder = O_proc_builder.redirectOutput(Redirect.PIPE); // PIPE = default value
			 
			O_proc = O_proc_builder.start();
			
			B_is_alive = O_proc.isAlive();
			if (B_is_alive) {
			   O_retval_exec_result.O_proc = O_proc;
			   ExecUtils.O_proc = O_proc;
			   }
		} catch (NullPointerException|IllegalArgumentException|IndexOutOfBoundsException|IOException|SecurityException PI_E_io) {
			 S_msg_1 = PI_E_io.getClass().getName() + 
   	    		  " occured during execution of \'" + S_cmd_diagnostics + "'" ; 
   	         E_rt_1 = new RuntimeException(S_msg_1, PI_E_io);
   	         E_rt_1.printStackTrace(System.err);
   	         return O_retval_exec_result;	
		} 
		  
		O_stream_out = O_proc.getInputStream();
		if (O_stream_out != null) {
			O_retval_exec_result.AAS_retvals = new List[2];
			O_retval_exec_result.AAS_retvals[0] =  StreamUtils.FAS_get_contents_from_stream(
					O_stream_out,
					PB_O_end_criterion);
			O_retval_exec_result.AAS_retvals[1] = (List<String>)null;	
		    } 
		O_stream_err = O_proc.getErrorStream();	
		if (O_stream_err != null) {
			if (O_retval_exec_result.AAS_retvals == null) {
				O_retval_exec_result.AAS_retvals = new List[2];
				O_retval_exec_result.AAS_retvals[0] = (List<String>)null;	
			    }
			O_retval_exec_result.AAS_retvals[1] = StreamUtils.FAS_get_contents_from_stream(
					O_stream_err,
					O_end_criterion_stderr);
		    }
		
		if (PI_L_timeout_proc == null) {
		   return O_retval_exec_result;
		   }
		
		if (PI_L_timeout_proc == 0L) {
			try {
			    O_retval_exec_result.I_exit_code = O_proc.waitFor();
		        }
		    catch (InterruptedException PI_E_intr) {
		      O_retval_exec_result.I_exit_code = -1;
			  S_msg_1 = PI_E_intr.getClass().getSimpleName() + " occurred during excution of " + "'PI_S_cmd";
			  E_rt_1 = new RuntimeException(S_msg_1, PI_E_intr);
			  PI_E_intr.printStackTrace(System.err);
		      }
	       }
		 else {  // Timeout defined and > 0
			B_finished_ok = false;
			try {
				B_finished_ok =  O_proc.waitFor(PI_L_timeout_proc, TimeUnit.MILLISECONDS);
			} catch (InterruptedException PI_E_intr) {
				 O_retval_exec_result.I_exit_code = -1;
				 S_msg_1 = PI_E_intr.getClass().getSimpleName() + " occurred during excution of " + "'PI_S_cmd";
				 E_rt_1 = new RuntimeException(S_msg_1, PI_E_intr);
				 PI_E_intr.printStackTrace(System.err);
			    }
			 if (!B_finished_ok) {
				 O_retval_exec_result.I_exit_code = -1; 
			     } 
			  }
		   return O_retval_exec_result;
		 }
	         
	  public static String FS_get_parent_of_executable(final String PI_S_bn_executable) {
		  S_dn_parent = (String)null;
		  
		  List<String> AS_pna_executables, AAS_retvals[];
		  ExecResult O_exec_result;
		  String S_bn_executable;
		  String S_cmd, S_pna_executable, S_pn_suffix;
		  int I_nbr_exec_paths, i1, I_len_suffix, I_len_pna, I_len_dn_parent_f1;
		 
		  S_pn_suffix = FILE_SEPARATOR + PI_S_bn_executable;
		  I_len_suffix = S_pn_suffix.length();
		  
		  if (SystemUtils.IS_OS_WINDOWS) {
			  S_cmd = "WHERE " + PI_S_bn_executable;
			//  O_exec_result = FAAS_exec_sync(S_cmd, null, null, null);
			  O_exec_result = FAAS_exec_sync(
					  new String[]{"WHERE", PI_S_bn_executable},  
					  null, // env vars
					  null, // working dir
					  0L);  // timeout
			  AAS_retvals = O_exec_result.AAS_retvals;
			  if ((AAS_retvals != null) && (AAS_retvals.length >= 1)) {
				  AS_pna_executables = AAS_retvals[0];
				  if (AS_pna_executables != null) {
					 I_nbr_exec_paths = AS_pna_executables.size();
					 LOOP_EXEC_PATHS: for (i1 = 0; i1 < I_nbr_exec_paths; i1++) {
						 S_pna_executable = AS_pna_executables.get(i1);
						 I_len_pna = StringUtils.length(S_pna_executable);
						 if (I_len_pna > I_len_suffix) {
							if (StringUtils.endsWithIgnoreCase(S_pna_executable, S_pn_suffix)) {
								I_len_dn_parent_f1 = I_len_pna - I_len_suffix;
								S_dn_parent = S_pna_executable.substring(0, I_len_dn_parent_f1);
								break LOOP_EXEC_PATHS;
							}
						 }
					 }  
				  }
				  
			  }
			  AS_pna_executables = O_exec_result.AAS_retvals[0];
		  }
		  else {   // Non Windows OS
	        worker = new SwingWorker<Void, Void>() {
	        	
	        	String S_msg_1;
	        	RuntimeException E_rt;
	        	
	            @Override
	            public Void doInBackground() {
	            	
	                try {
	                    pb.command(PI_S_bn_executable);
	                    pb.redirectErrorStream(true);
	                    O_proc = pb.start();

	                    BufferedReader in = new BufferedReader(new InputStreamReader(O_proc.getInputStream()));
	                    O_proc.waitFor();
	                    in.close();

	                    S_dn_parent = StringUtils.EMPTY;
	                }
	                   catch (IOException | InterruptedException PI_E_io) {
	                	    S_msg_1 = PI_E_io.getClass().getName() + 
	                	    		  " occured during execution of \'" + PI_S_bn_executable + "'" ; 
	                	    E_rt = new RuntimeException(S_msg_1, PI_E_io);
	                	    E_rt.printStackTrace(System.err);
	                	    S_dn_parent = null;
	                   }
	                return null;
	            }
	        };

	        worker.execute();
	        while (!worker.isDone()) { }
   
	    } // End Non-Windows environment
		  return S_dn_parent;	  
	  }
	
//	 public class ProcessHandleExtended implements ProcessHandle {
//		 public boolean destroy() {
//			 
//			 boolean B_retval_destroy;
//			 B_retval_destroy = this.destroy();
//			 return B_retval_destroy;
//		 }
//		 
//		  public boolean destroyForcibly() {
//			 
//			 boolean B_retval_destroy;
//			 B_retval_destroy = this.destroyForcibly();
//			 return B_retval_destroy;
//		 }
		 
//	 }
	    
 public static	Consumer <? super ProcessHandle> F_proc_handle = (ProcessHandle ph) -> {
		ProcessHandle.Info O_ph_info;
		Optional<String> O_cmd;
		String S_cmd;
		
		boolean B_desctruction_success;
		long P_pid;
		
		if (ph.isAlive()) {
			O_ph_info = ph.info();
			O_cmd = O_ph_info.command();
			S_cmd = null;
			synchronized (ph) {
			   if (O_cmd.isPresent()) {
				  try {
					S_cmd = O_cmd.get();
				} catch (NoSuchElementException PI_E_no_such_eleme) {
					S_cmd = null;
				    }
				if (StringUtils.isNotBlank(S_cmd)) {
				   if (S_cmd.equals(ExecUtils.S_pna_exec)) {
					   P_pid = ph.pid();
					   if (ExecUtils.B_kill) {
						   if (ExecUtils.B_by_force) {
							   B_desctruction_success = ph.destroyForcibly();
						      }
						   else {
							   B_desctruction_success = ph.destroy();
						      }
						   if (B_desctruction_success) {
							  ExecUtils.AL_pids.push(P_pid); 
						      }
					       }
					   else {
						    ExecUtils.AL_pids.push(P_pid);
					      }
				       }
				    }
			      }
			   }
		    }
		};
	  
	public static List<Long> FAL_get_processes_by_path(
			final String  PI_S_pna_exec,
			final boolean PI_B_kill) {
		
		List<Long> AL_retval_pid;
		
		AL_retval_pid = FAL_get_processes_by_path(
				PI_S_pna_exec,
				PI_B_kill,
				false);  // by force 
		
		return AL_retval_pid;
	}
		
    public static List<Long> FAL_get_processes_by_path(
			final String PI_S_pna_exec,
			final boolean PI_B_kill,
			final boolean PI_B_by_force)  {
		
    	Stream<ProcessHandle> AO_proc_handle;
	
		ExecUtils.S_pna_exec = PI_S_pna_exec;
		ExecUtils.B_kill     = PI_B_kill;
		ExecUtils.B_by_force = PI_B_by_force;
		ExecUtils.AL_pids.clear();
		
		AO_proc_handle = ProcessHandle.allProcesses();
	    AO_proc_handle.forEach(F_proc_handle);
		
		return ExecUtils.AL_pids;
	}

}
