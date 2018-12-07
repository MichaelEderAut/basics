package com.github.michaelederaut.basics;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.lang3.reflect.FieldUtils;

public class LineNbrRandomAccessFile extends RandomAccessFile {
	
	public static enum ReadLinePolicy {
		
		ReadNext, ReRead, SkipRead
	}
	
	public static final String READ_ONLY  = "r";
	public static final String READ_WRITE = "rw";
	public static final String CLOSED =     "closed";
	private static final String PATH      = "path";
	private static final String ERR_MSG   = "line number related methods not applicable for mode rw: ";
	
	public boolean B_rw_mode = true;
	public String  S_pn;
	public FileChannel    O_file_channel;
	public FileDescriptor O_fd;
	
	public static class Line {
		
		public long    L_pos_f0;
		public int     I_len_f1;
		
		public Line (
				final long PI_L_pos_f0,
				final int  PI_I_len_f1) {
			this.L_pos_f0 =  PI_L_pos_f0;
			this.I_len_f1 = PI_I_len_f1;
			return;
		}
	}
	
	public HashMap<Integer, Line>     HI_lines;
	public TreeMap<Long, Integer>     HL_address_to_line_nbrs;
	public int                        I_curr_line_nbr;
	
	public LineNbrRandomAccessFile(
			final String PI_pn_from,
            final String PI_S_mode) throws FileNotFoundException {
		super(PI_pn_from, PI_S_mode);
		this.FV_init();
	     }
			
	public LineNbrRandomAccessFile(
			final File   PI_F_from,
            final String PI_S_mode) throws FileNotFoundException {
		super(PI_F_from, PI_S_mode);
		this.FV_init();
	}
	
	public boolean FB_get_rw_status() {
		
	    Throwable        E_cause;
	    RuntimeException E_rt;
		
		String S_msg_1;
		boolean B_retval_rw;
		Object O_rw;
		
		B_retval_rw = true;
		E_cause = null;
		O_rw  = null;
		try {
			O_rw = FieldUtils.readField(this, READ_WRITE,  true);
		} catch (IllegalAccessException PI_E_ill_access) {
			E_cause = PI_E_ill_access;
		    }
		
		try {
			B_retval_rw = (Boolean)O_rw;
		} catch (NullPointerException | ClassCastException PI_E_np) {
			E_cause = PI_E_np;
		    }
		
		if (E_cause != null) {
		   S_msg_1 = "Unable to read field \'" + READ_WRITE + "\' from object of type " + this.getClass().getName() + ".";
		   E_rt = new RuntimeException(S_msg_1, E_cause);
		   }
		
		return B_retval_rw;
	}
	
	public boolean FB_is_closed() {
		
	    Throwable        E_cause;
	    RuntimeException E_rt;
		
		String S_msg_1;
		boolean B_retval_is_closed;
		Object O_is_closed;
		
		B_retval_is_closed = false;
		E_cause = null;
		O_is_closed  = null;
		try {
			O_is_closed = FieldUtils.readField(this, CLOSED,  true);
		} catch (IllegalAccessException PI_E_ill_access) {
			E_cause = PI_E_ill_access;
		    }
		
		try {
			B_retval_is_closed = (Boolean)O_is_closed;
		} catch (NullPointerException | ClassCastException PI_E_np) {
			E_cause = PI_E_np;
		    }
		
		if (E_cause != null) {
		   S_msg_1 = "Unable to read field \'" + CLOSED + "\' from object of type " + this.getClass().getName() + ".";
		   E_rt = new RuntimeException(S_msg_1, E_cause);
		   }
		
		return B_retval_is_closed;
	}
	
	public long FL_get_position() {
		
		RuntimeException E_rt;
		String S_msg_1;
		long L_retval_position;
		
		try {
			L_retval_position = this.getFilePointer();
		} catch (IOException PI_E_io) {
			// TODO Auto-generated catch block
			S_msg_1 = "Unable to determine current position in file: \"" + this.S_pn + "\"";
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		}
		return L_retval_position;
	}
	
	public void FV_reset(final boolean PI_B_nullify_tables) {
		RuntimeException E_rt;
		IllegalStateException E_ill_state;
		
		String   S_msg_1, S_msg_2;
		boolean  B_is_closed;
		
		this.I_curr_line_nbr = 0;
		if (this.B_rw_mode) {
			S_msg_1 = ERR_MSG + this.B_rw_mode;
			E_ill_state = new IllegalStateException(S_msg_1);
			S_msg_2 = "Unabl to execute reset function in this context";
			E_rt = new RuntimeException(S_msg_2, E_ill_state);
			throw E_rt;
		    }
		
		if (PI_B_nullify_tables) {
			this.HL_address_to_line_nbrs = null;
			this.HI_lines     = null;
		    }
		else {
		   this.HL_address_to_line_nbrs = new TreeMap<Long, Integer>();
		   this.HI_lines     = new HashMap<Integer, Line>();
		    }
		B_is_closed = this.FB_is_closed();
		if (!B_is_closed) {
			try {
				this.seek(0L);
			} catch (IOException PI_E_io) {
				S_msg_1 = "Failed to reset seek-position of file of type:\'" + this.getClass().getName() + "\' to 0";
				E_rt    = new RuntimeException(S_msg_1, PI_E_io);
				throw E_rt;
		    }
		}
	}
	
	public void FV_reset_for_read_only() {
		this.FV_reset(false); // don't nullify tables	
	}
	
	
	public void FV_close(final boolean PI_B_nullify_tables) {
		
		 RuntimeException E_rt;
		 StringBuffer SB_msg_1;
		 String S_msg_1;
		
		 try {
			  this.close();
			} catch (IOException PI_E_io) {
				SB_msg_1 = new StringBuffer ("Error closing file: \'" + this.S_pn + "\'");
				if (!this.B_rw_mode) {
					SB_msg_1.append(" after reading " + this.I_curr_line_nbr + " lines"); 
				    }
				SB_msg_1.append(".");
				S_msg_1 = SB_msg_1.toString();
				E_rt = new RuntimeException(S_msg_1, PI_E_io);
			}
		if (!this.B_rw_mode) {
			this.FV_reset(PI_B_nullify_tables);
		    }
	}
	
	public void FV_close() {
		this.FV_close(true);  // nullify tables = true;
	}
	
//	public void FV_init_readonly_mode() { 
//
//		RuntimeException E_rt;
//		Throwable        E_cause;
//		Object O_path;
//        String S_pn, S_msg_1;
//		
//        S_pn   = null;
//        O_path = null;
//        E_cause = null;
//        try {
//			O_path = FieldUtils.readField(this, PATH, true);
//		} catch (IllegalAccessException PI_E_ill_access) {
//			E_cause = PI_E_ill_access;
//		    }
//		
//		try {
//			S_pn = (String)O_path;
//		} catch (NullPointerException | ClassCastException PI_E_np) {
//			E_cause = PI_E_np;
//		    }
//		
//		if (E_cause != null) {
//		   S_msg_1 = "Unable to read field \'" + PATH + "\' from object of type " + this.getClass().getName() + ".";
//		   E_rt = new RuntimeException(S_msg_1, E_cause);
//		   throw E_rt;
//		   }
//        this.S_pn = S_pn;
//        
//		// this.B_rw_mode = this.FB_get_rw_status();
//		if (this.B_rw_mode) {
//			return;
//		    }
//		this.FV_reset();
//	}	
	
	public void FV_init() {
	
		RuntimeException E_rt;
		Throwable        E_cause;
		
		Object O_path;
        String S_pn, S_msg_1;
		
    	this.I_curr_line_nbr = 0;
    	this.B_rw_mode = this.FB_get_rw_status();
        S_pn   = null;
        O_path = null;
        E_cause = null;
        
        try {
			O_path = FieldUtils.readField(this, PATH, true);
		} catch (IllegalAccessException PI_E_ill_access) {
			E_cause = PI_E_ill_access;
		    }
		
		try {
			S_pn = (String)O_path;
		} catch (NullPointerException | ClassCastException PI_E_np) {
			E_cause = PI_E_np;
		    }
		
		if (E_cause != null) {
		   S_msg_1 = "Unable to read field \'" + PATH + "\' from object of type " + this.getClass().getName() + ".";
		   E_rt = new RuntimeException(S_msg_1, E_cause);
		   throw E_rt;
		   }
        this.S_pn = S_pn;
        
        this.O_file_channel = this.getChannel();
        try {
			this.O_fd           = this.getFD();
		} catch (IOException PI_E_io) {
			S_msg_1 = "Unable to obtain channel of File: \"" + this.S_pn + "\"";
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		}
       
        
        if (this.B_rw_mode) {
			return;
		    }
		this.FV_reset_for_read_only();
	
		
	}
		public void FV_refresh_lines() {
			
			IllegalStateException E_ill_state;
			RuntimeException E_rt;
			String S_res_line, S_msg_1, S_msg_2;
			
			boolean B_continue_loop;
			if (this.B_rw_mode) {
				S_msg_1 = ERR_MSG + this.B_rw_mode;
				E_ill_state = new IllegalStateException(S_msg_1);
				S_msg_2 = "Unabl to refresh lines in this context";
				E_rt = new RuntimeException(S_msg_2, E_ill_state);
				throw E_rt;
			    }
			
		    this.FV_reset_for_read_only();
		    B_continue_loop = true;
			while (B_continue_loop) { 
				S_res_line = FS_readLine();
				if (S_res_line == null) {
					B_continue_loop = false;
				    }
				}	
		    }		
	
	public String FS_readLine() {
		IllegalStateException E_ill_state;
		RuntimeException E_rt;
		
		String S_msg_1, S_msg_2;
		long L_pos_f0;
		int I_len_line_f1;
		Line O_line;
		
		if (this.B_rw_mode) {
			S_msg_1 = ERR_MSG + this.B_rw_mode;
			E_ill_state = new IllegalStateException(S_msg_1);
			S_msg_2 = "Unable to read lines in this context";
			E_rt = new RuntimeException(S_msg_2, E_ill_state);
			throw E_rt;
		    }
		
		String S_retval_line = null;
		I_len_line_f1 = -1;
		L_pos_f0 = this.FL_get_position();   
		try {
			S_retval_line = this.readLine();
			if (S_retval_line != null) {
			   I_len_line_f1 = S_retval_line.length();
			   }
		} catch (IOException PI_E_io) {
			S_msg_1 = "Unable to read a single line from file of type:\'" + this.getClass().getName() + "\'.\n" +
		              "Last successful received line: " + this.I_curr_line_nbr + "." ;
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		    }
		
		if (I_len_line_f1 < 0) {
			return S_retval_line;
		    }
		
		this.I_curr_line_nbr++;
		if (this.HI_lines.containsKey(this.I_curr_line_nbr)) {
			return S_retval_line;
		}
		
		O_line = new Line(L_pos_f0, I_len_line_f1);
		this.HI_lines.put(this.I_curr_line_nbr, O_line);
		this.HL_address_to_line_nbrs.put(L_pos_f0, this.I_curr_line_nbr);
	
		return S_retval_line;
	}
	
	public String FS_re_read_line(final int PI_I_line_nbr_f1) {
		IllegalStateException E_ill_state;
		AssertionError E_assert;
		RuntimeException E_rt;
		
		String S_msg_1, S_msg_2;
		long L_pos_f0;
		int I_len_line_f1;
		Line O_line;
		
		if (this.B_rw_mode) {
			S_msg_1 = ERR_MSG + this.B_rw_mode;
			E_ill_state = new IllegalStateException(S_msg_1);
			S_msg_2 = "Unable to read line " + PI_I_line_nbr_f1 + " in this context";
			E_rt = new RuntimeException(S_msg_2, E_ill_state);
			throw E_rt;
		    }
		
		String S_retval_line = null;
		
		O_line = this.HI_lines.get(PI_I_line_nbr_f1);
		if (O_line == null) {
			S_msg_1 = "Unable to retrieve object of type: " + Line.class.getName() + " for line number: " + PI_I_line_nbr_f1 + ".";
			E_assert = new AssertionError (S_msg_1);
			S_msg_2 = "Unable to obtain seek position of line number: " + PI_I_line_nbr_f1 + ".";
			E_rt = new RuntimeException(S_msg_1, E_assert);
			throw E_rt;
		    }
		L_pos_f0 = O_line.L_pos_f0;
		
		try {
			this.seek(L_pos_f0);
		} catch (IOException PI_E_io) {
			S_msg_1 = "Unable seek for position " + String.format("%08X", L_pos_f0);
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		    }
		
		try {
		    S_retval_line = this.readLine(); 
		} catch (IOException PI_E_io) {
			S_msg_1 = "Unable to read line from position " + String.format("%08X", L_pos_f0);
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		    }
	    return S_retval_line;
	}
		
	public String FS_re_read_line() {
		
		String S_retval_line;
		int I_line_nbr;
		
		S_retval_line = null;
		I_line_nbr = this.I_curr_line_nbr;
		if (I_line_nbr < 1) {
		   return S_retval_line;
		   }
		
		S_retval_line = FS_re_read_line(I_line_nbr);
		
		return S_retval_line;
	}
	
	public void FV_println(final String PI_S_line) {
		
		RuntimeException E_rt;
		String           S_msg_1;
		
		try {
			this.writeBytes(PI_S_line + System.lineSeparator());
		} catch (IOException PI_E_io) {
			S_msg_1 = "Error when adding line: \"" + PI_S_line + "\" to file\n" +
		              "\"" + this.S_pn + "\"";
			E_rt = new RuntimeException(S_msg_1, PI_E_io);
			throw E_rt;
		}
		this.I_curr_line_nbr++;	}
}
