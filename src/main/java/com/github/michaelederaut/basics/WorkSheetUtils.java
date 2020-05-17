package com.github.michaelederaut.basics;

import static com.github.michaelederaut.basics.ToolsBasics.LS;

import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;


public class WorkSheetUtils {

public static final int I_ord_of_A = (int)'A';	
	
/**
 * @see <a href="https://stackoverflow.com/questions/10773961/apache-poi-apply-one-style-to-different-workbooks">
 * Apache POI: apply one style to different workbooks</a>
 */
public static final CellCopyPolicy O_cell_copy_policy_dflt = new CellCopyPolicy()
{{ setCopyCellStyle(false); }};


public static String FS_dump_row (XSSFRow PI_O_row) {
	
	String S_retval;
	StringBuffer SB_res_row;
	
	SB_res_row = FSB_dump_row(PI_O_row);
	S_retval = SB_res_row.toString();
	return S_retval;
}

public static StringBuffer FSB_dump_row (XSSFRow PI_O_row) {
	
	Iterator<Cell> O_iterat_cell;
	Cell O_cell;
	CellType E_cell_type;
		
	String  S_msg_1, S_cell, S_cell_header, S_cell_tail, S_cell_type, S_cell_value;
	char C_col;
	double L_numeric_cell_value;
	int I_nbr_cells_f1, I_row_num_f0;
	
	StringBuffer SB_row = new StringBuffer();
	
	I_row_num_f0 = PI_O_row.getRowNum();
	S_msg_1 = "Native row-index 0-based: " + I_row_num_f0;
	SB_row.append(S_msg_1 + LS);
	
	I_nbr_cells_f1 = 0;
	O_iterat_cell = PI_O_row.cellIterator();
	while (O_iterat_cell.hasNext()) {
		if (I_nbr_cells_f1 > 0) {
			SB_row.append(LS);
		    }
		I_nbr_cells_f1++;
		C_col = (char)(I_ord_of_A + I_nbr_cells_f1 - 1);
		S_cell_header =  "col: " +  C_col + "/" + I_nbr_cells_f1;
		O_cell = O_iterat_cell.next();
		if (O_cell == null) {
			S_cell_tail = "<NULL>";	
		    }
		else {
			E_cell_type = O_cell.getCellType();
			S_cell_type = E_cell_type.toString();
		    if (E_cell_type == CellType.STRING) {
        	   S_cell_value = O_cell.getStringCellValue();
        		    }
        	else if (E_cell_type == CellType.NUMERIC) {
        		L_numeric_cell_value = O_cell.getNumericCellValue();
         		S_cell_value =  String.valueOf(L_numeric_cell_value);
        		   }
        	else if (E_cell_type == CellType.BLANK) {
        		 S_cell_value = "";
        	     }
        	else {
        		S_cell_value = "<NOT IMPLEMENTED>";
        	    }
			S_cell_tail = " type: " + S_cell_type + ", value: " + S_cell_value;
		 }
		 S_cell = S_cell_header + " " + S_cell_tail;
		 SB_row.append(S_cell);
	   }
	return SB_row;
}

/**
 * @param PB_O_ws_target
 * @param PI_AI_rows
 * @param PI_O_cell_copy_policy
 * 
 * @see <a href="https://stackoverflow.com/questions/54430518/why-am-i-getting-illegalargumentexception-while-using-copyrowfrom-in-xssfro">
 * Workaround for Apache-POI bug:<br>
 * Why am I getting IllegalArgumentException while using copyRowFrom(…) in XSSFRow?</a>
 */
 public static void FV_copy_wrk_sheet(
			final XSSFSheet PB_O_ws_target, 
			final Vector<XSSFRow> PI_AI_rows,
			final CellCopyPolicy PI_O_cell_copy_policy) {
		
	    RuntimeException E_rt;
		NullPointerException E_np;
		
		CellCopyPolicy O_cell_copy_policy;
		XSSFCellStyle AO_cell_styles[], O_cell_stype;
		XSSFRow O_row_src_0, O_row_dest_needed_0, O_row_dest_temp_dummy_1;
		String  S_msg_1, S_row_dump;
		int     i1, i2, i3, I_nbr_rows_f1, I_nbr_cells_f1, I_nbr_cells_f0;
		boolean B_indivitual_cell_style;
		
	    if (PI_O_cell_copy_policy != null) {
			O_cell_copy_policy = PI_O_cell_copy_policy;
			 B_indivitual_cell_style = false;
		     }
		 else {
			O_cell_copy_policy = null;
			B_indivitual_cell_style = true;
		     }
		
		if (PI_AI_rows == null) {
			return;
		    }
		
		if (PB_O_ws_target == null) {
			S_msg_1 = "Parameter of type " + XSSFSheet.class.getName() + " must not be null";
			E_np = new NullPointerException(S_msg_1);
			throw E_np;
		    }
		
		I_nbr_rows_f1 = PI_AI_rows.size();
		for (i1 = 0; i1 < I_nbr_rows_f1; i1++) {
			i2 = i1 + 1;
			O_row_dest_temp_dummy_1 = PB_O_ws_target.createRow(i2);
			O_row_src_0 = PI_AI_rows.get(i1);
			I_nbr_cells_f0 = O_row_src_0.getLastCellNum();
			
			try {
				O_row_dest_temp_dummy_1.copyRowFrom(O_row_src_0, PI_O_cell_copy_policy);
			} catch (IllegalArgumentException PI_E_ill_arg) {
				S_row_dump = FS_dump_row(O_row_src_0);
				S_msg_1 = "Error filling dummy row at 0-based calculated index: " + i1 + " from" + LS + LS +
						S_row_dump;
				E_rt = new RuntimeException(S_msg_1, PI_E_ill_arg);
				throw E_rt;
			    }  
			O_row_dest_needed_0 = PB_O_ws_target.createRow(i1);
			O_row_dest_needed_0.copyRowFrom(O_row_dest_temp_dummy_1, PI_O_cell_copy_policy);
			
			PB_O_ws_target.removeRow(O_row_dest_temp_dummy_1);
		}
		return;
	}

	public static void FV_copy_wrk_sheet(
			XSSFSheet PB_O_ws, 
			Vector<XSSFRow> PI_AI_rows) {
		
		FV_copy_wrk_sheet(
			PB_O_ws, 
			PI_AI_rows,
			O_cell_copy_policy_dflt);
		
		return;	
	}
	
	public static void FV_copy_wrk_sheet(
			XSSFSheet PB_O_ws_target, 
			XSSFSheet PB_O_ws_src) {
		
		FV_copy_wrk_sheet(
			PB_O_ws_target, 
			PB_O_ws_src,
			O_cell_copy_policy_dflt);
		return;
		}
	
	
	public static void FV_copy_wrk_sheet(
			final XSSFSheet PB_O_ws_target, 
			final XSSFSheet PB_O_ws_src,
			CellCopyPolicy PI_O_cell_copy_policy) {
		
		if (PB_O_ws_src == null) {
			return;
		    }
		
		NullPointerException E_np;
	    String S_msg_1;
	    
		if (PB_O_ws_target == null) {
			S_msg_1 = "Parameter of type " + XSSFSheet.class.getName() + " must not be null";
			E_np = new NullPointerException(S_msg_1);
			throw E_np;
		    }
		
		if (PI_O_cell_copy_policy == null) {
			PI_O_cell_copy_policy = O_cell_copy_policy_dflt;
		    }
		Stack<XSSFRow> AO_rows;
		XSSFRow O_row;
		Iterator<Row> O_iterat_row;
		int i1, I_nbr_rows_f1;
		
		AO_rows = new Stack<XSSFRow>();
		O_iterat_row = PB_O_ws_src.iterator();
	    LOOP_ROWS_1: while (O_iterat_row.hasNext()) {
		    O_row = (XSSFRow)O_iterat_row.next();
		    if (O_row != null) {
		    	AO_rows.push(O_row);
		    }
		}
		FV_copy_wrk_sheet(PB_O_ws_target, AO_rows, PI_O_cell_copy_policy);
	}
}
