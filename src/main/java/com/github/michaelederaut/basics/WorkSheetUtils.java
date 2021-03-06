package com.github.michaelederaut.basics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyleXfs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTStylesheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyles;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTStylesheetImpl;

import com.google.common.collect.Streams;

import static com.github.michaelederaut.basics.ToolsBasics.LS;
import static com.github.michaelederaut.basics.WorkSheetUtils.I_ord_of_A;

public class WorkSheetUtils {

public static final int I_ord_of_A = (int)'A';	
protected static final boolean B_trace = true;

public static final CellCopyPolicy O_cell_copy_policy_dflt = new CellCopyPolicy();

class CellStyleCT {
	
	String S_name;
	long   I_xf_id;
	long   I_built_in_id;
}

// StylesTable 438
public static int[] FAI_add_to_styles_table (
	final StylesTable PB_O_style_table_target,
	final StylesTable PI_O_style_table_source) {
	
	int i1, I_nbr_cell_styles_source, I_idx_f0;
	XSSFCellStyle O_cell_style_source;
	
	int AI_target_indices[] = null;
	
	if (PB_O_style_table_target == null) {
		return AI_target_indices;
	}
	
	if (PI_O_style_table_source == null) {
		return AI_target_indices;
	    }
	
	I_nbr_cell_styles_source = PI_O_style_table_source.getNumCellStyles();
	AI_target_indices = new int[I_nbr_cell_styles_source];
	
	for (i1 = 0; i1 < I_nbr_cell_styles_source; i1++) {
		O_cell_style_source = PI_O_style_table_source.getStyleAt(i1);
		I_idx_f0 = PB_O_style_table_target.putStyle(O_cell_style_source);
		AI_target_indices[i1] = I_idx_f0;
	    }
	return AI_target_indices;
	
}
/**
 * @see <a href="https://stackoverflow.com/questions/10773961/apache-poi-apply-one-style-to-different-workbooks">
 * Apache POI: apply one style to different workbooks</a>
 */
public static final CellCopyPolicy O_cell_copy_policy_failsafe = new CellCopyPolicy()
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

CellStyleCT[] FAO_dump_style_source (final XSSFWorkbook PI_O_workbook) {

   StylesTable O_style_table;	
	
   CellStyleCT[]	AO_retval_cell_styles_ct = null;

if (PI_O_workbook == null) {
   return AO_retval_cell_styles_ct;
   }

O_style_table = PI_O_workbook.getStylesSource();

return AO_retval_cell_styles_ct;
	
}

/**
 * @param PB_O_ws_target target worksheet
 * @param PI_AI_rows rows to copy to the target workshee
 * @param PI_O_cell_copy_policy cell copy policy
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
		
		XSSFWorkbook O_wrk_book_source, O_wrk_book_target;
		XSSFSheet    O_wrk_sheet_source;
		StylesTable O_styles_table_source, O_styles_table_target;
		CTCellStyles O_cell_styles_source_ct;
		List<CTCellStyle> AO_cell_styles_source_ct;
    	CTStylesheet O_style_sheet_soruce_ct;  // CT = complex type
	//	CTStylesheetImpl O_ct_style_sheet_impl;
	//	CTCellStyleXfs O_cell_style_xfs; 
		CellCopyPolicy O_cell_copy_policy;
		XSSFCell O_cell_dest;
		XSSFCellStyle   O_cell_style_source, O_cell_style_target;
		XSSFRow O_row_src_0, O_row_dest_needed_0, O_row_dest_temp_dummy_1;
		
		Field AF_style_source[];
		
		Set<String> HS_table_style_names_source, HS_table_style_names_target;
		Set<Short>  HI_table_style_source_idxs;
		String  S_msg_1, S_msg_2, S_row_dump;
		char    C_col;
		int     i1, i2, i3, i4, i5, I_nbr_rows_f1, I_nbr_cells_f0, 
		        I_nbr_cell_styles_src_ct_f1,  I_nbr_cell_styles_target_f1,
		        AI_styles_idx[];
		long    I_nbr_cell_styles_source_f1;
		short   I_idx_cell_style_source;
		boolean B_indivitual_cell_style;
		
		
		XSSFCellStyle AO_cell_styles_source[] = null;
		
		int I_nbr_cells_f1 = 0;
		
	    if (PI_O_cell_copy_policy == null) {
	    	O_cell_copy_policy = O_cell_copy_policy_failsafe;
			B_indivitual_cell_style = true;
	    	}
		 else {
			O_cell_copy_policy = PI_O_cell_copy_policy;
			B_indivitual_cell_style = false;
		    }
		
		if (PI_AI_rows == null) {
			return;
		    }
		
		if (PB_O_ws_target == null) {
			S_msg_1 = "Parameter of type " + XSSFSheet.class.getName() + " must not be null";
			E_np = new NullPointerException(S_msg_1);
			throw E_np;
		    }
		else {
		// TODO
		}
		
		O_styles_table_target = null;
		if (B_indivitual_cell_style) {
			HI_table_style_source_idxs = new HashSet<Short>();
			O_wrk_book_target = PB_O_ws_target.getWorkbook();
			I_nbr_cell_styles_target_f1 = O_wrk_book_target.getNumCellStyles();
			if (B_trace) {
			   S_msg_1 = "Target work-book nbr of cell styles: " + I_nbr_cell_styles_target_f1;
			   System.out.println(S_msg_1);
			   }
			O_styles_table_target = O_wrk_book_target.getStylesSource();
		
			HS_table_style_names_target = O_styles_table_target.getExplicitTableStyleNames();
			if (B_trace) {
			   if (I_nbr_cell_styles_target_f1 > 0) {
			       S_msg_1 = String.join(LS, HS_table_style_names_target);
			       System.out.println(LS + S_msg_1);
			       }
		        }
		    }  
		else {
			O_wrk_book_target = null;
			HI_table_style_source_idxs = null;
		    }
		
		I_nbr_rows_f1 = PI_AI_rows.size();
		for (i1 = 0; i1 < I_nbr_rows_f1; i1++) {
			i2 = i1 + 1;
			O_row_dest_temp_dummy_1 = PB_O_ws_target.createRow(i2);
			O_row_src_0 = PI_AI_rows.get(i1);	
			I_nbr_cells_f0 = O_row_src_0.getLastCellNum();
			I_nbr_cells_f1 = I_nbr_cells_f0 + 1;
			AO_cell_styles_source = new XSSFCellStyle[I_nbr_cells_f1];
			for (i3 = 0; i3 < I_nbr_cells_f1; i3++) {
			     O_cell_dest = O_row_src_0.getCell(i3);
				   if (O_cell_dest != null) {
					  if (B_indivitual_cell_style) {
					      if (i1 == 0) {
					         O_wrk_sheet_source = O_cell_dest.getSheet(); 
					         O_wrk_book_source = O_wrk_sheet_source.getWorkbook();
					         O_styles_table_source = O_wrk_book_source.getStylesSource();
					         AI_styles_idx = FAI_add_to_styles_table(O_styles_table_source, O_styles_table_target);
					         try {
				                 FieldUtils.writeField(O_wrk_book_target, "stylesSource", O_styles_table_source, true);
			                 } catch (IllegalAccessException PI_E_ill_arg) {
				                  S_msg_1 = "Unable to update field '" + "stylesSource" + "' on target workbook";
				                  E_rt = new RuntimeException(S_msg_1, PI_E_ill_arg); 
				                  throw E_rt;
			                      }
					         if (B_trace) {
					        	
					             //	 Arrays.stream(AI_styles_idx)...
					            S_msg_1 = "Style indices: " +
					                    IntStream.of(AI_styles_idx).mapToObj(String::valueOf).collect(Collectors.joining(","));
					            System.out.println(S_msg_1);
					        	// https://stackoverflow.com/questions/38425623/java-join-array-of-primitives-with-separator 
					        	// google: apache poi determine/get name of cellStyle
					        	// stackoverflow.com/questions/26675062/poi-excel-get-style-name
					             
					        	
    				          //  I_nbr_cell_styles_src_source_f1 = O_styles_table_source.getNumCellStyles();
    				            O_style_sheet_soruce_ct = O_styles_table_source.getCTStylesheet();
    				            O_cell_styles_source_ct = O_style_sheet_soruce_ct.getCellStyles();
					          //  O_ct_style_sheet_impl = (CTStylesheetImpl)O_ct_style_sheet;
					         
					           //  O_ct_style_sheet.getCellStyles();
//					            Method[] AO_methods;
					            String       S_style_name_ct;
					            long          I_id_ct;
					            StringBuffer SB_style_names_ct = new StringBuffer();
					            XmlUnsignedInt I_built_in_id;
//					            Class T_style_sheet;
//					            
//					            // AO_methods = O_ct_style_sheet.getClass().getMethods();
//					             T_style_sheet = O_ct_style_sheet.getClass();
//					             AO_methods =  T_style_sheet.getMethods();
//					             
//					            for (Method O_method : AO_methods) {
//					            	S_method_name = O_method.getName();
//					            	SB_method_names.append(LS + S_method_name);
//					            }
//					            
//					            S_msg_1 = "Method-names: " + SB_method_names;
//					            System.out.println();
					            
					           // O_ct_style_sheet.getTableStyles().getTableStyleArray()[0].getName();
					           // System.out.println(S_msg_1);
					            
					           // I_nbr_cell_styles_source_f1 =  AO_cell_styles.getCount(); 
					            
					        	// I_nbr_cell_styles_source_f1 = O_wrk_book_source.getNumCellStyles();
					        	
					            AO_cell_styles_source_ct = O_cell_styles_source_ct.getCellStyleList();
				               // AO_cell_styles_source_ct.contains(???);
				               // AO_cell_styles_source_ct.add(???) 
					            I_nbr_cell_styles_src_ct_f1 =  O_cell_styles_source_ct.sizeOfCellStyleArray();
					            for (CTCellStyle O_cell_style_ct : AO_cell_styles_source_ct) {
					            	S_style_name_ct = O_cell_style_ct.getName();
					            	I_id_ct = O_cell_style_ct.getXfId();
					            	I_built_in_id = O_cell_style_ct.xgetBuiltinId();
					            	SB_style_names_ct.append(LS + S_style_name_ct + ", " + I_id_ct);
					            	if (I_built_in_id != null) {
					            		SB_style_names_ct.append(", " + I_built_in_id);
					            	    }
					                }
					        	
					            S_msg_1 = "Source work-book nbr of cell styles: " +  I_nbr_cell_styles_src_ct_f1;
					            System.out.println(S_msg_1);
					            System.out.println("Joined by convential iteration:" + LS + SB_style_names_ct + LS);
					            
					             System.out.println(S_msg_1);
					             
//					             S_msg_2 = 
//					             AO_cell_styles_ct.stream().
//					             map(x -> new String[] {x.getName(), String.valueOf(x.getXfId()), String.valueOf(x.xgetBuiltinId())}).
//					             map(a -> (a[0] + ", " +  a[1] + ((StringUtils.isNotBlank(a[2]) ? (", " + a[2]) : "" )))).
//					             collect(Collectors.joining(LS));
//					         
//					             System.out.println("Joined by Streams:" + LS + S_msg_2 + LS);
					             
					             
//					             HS_table_style_names_source = O_styles_table_source.getExplicitTableStyleNames();
//					             
//								 if (I_nbr_cell_styles_src_source_f1 > 0) {
//									S_msg_1 = String.join(LS, HS_table_style_names_source);
//								    System.out.println(LS + S_msg_1);
//									}
//								
//								for (i5 = 0; i5 < I_nbr_cell_styles_source_f1; i5++) {
//									O_wrk_book_source.getCellStyleAt(i5);
//								
//								     }
					            }
					        }
					      }
				      O_cell_style_source = O_cell_dest.getCellStyle();
				      AO_cell_styles_source[i3] = O_cell_style_source;
			          }
			     }
		     try {
			     O_row_dest_temp_dummy_1.copyRowFrom(O_row_src_0, O_cell_copy_policy);
			   } catch (IllegalArgumentException PI_E_ill_arg) {
				S_row_dump = FS_dump_row(O_row_src_0);
				S_msg_1 = "Error filling dummy row at 0-based calculated index: " + i1 + " from" + LS + LS +
						S_row_dump;
				E_rt = new RuntimeException(S_msg_1, PI_E_ill_arg);
				throw E_rt;
			    }  
			O_row_dest_needed_0 = PB_O_ws_target.createRow(i1);
			O_row_dest_needed_0.copyRowFrom(O_row_dest_temp_dummy_1, O_cell_copy_policy);
			if (B_indivitual_cell_style) {
				// see also: stackoverflow.com/questions/10773961/apache-poi-apply-one-style-to-different-workbooks
				for (i3 = 0; i3 < I_nbr_cells_f1; i3++) {
					O_cell_dest = O_row_dest_needed_0.getCell(i3);
					if (O_cell_dest != null) {
						O_cell_style_source = AO_cell_styles_source[i3];
						if (O_cell_style_source != null) {
							I_idx_cell_style_source = O_cell_style_source.getIndex();
							if (B_trace) {
							   i4 = i3 + 1;	
							   C_col = (char)(I_ord_of_A + i3);
							   S_msg_1 = "Row_idx_f1: " + i2 + ", " + C_col + "/" + i4 + ", src_idx: " + I_idx_cell_style_source;
							   System.out.println(S_msg_1);
							   }
							O_cell_style_target = O_cell_style_source;
							try {
								O_cell_dest.setCellStyle(O_cell_style_target);
							} catch (IllegalArgumentException PI_E_ill_arg) {
								if (B_trace) {
									S_msg_1 = "Cought " +  PI_E_ill_arg.getClass().getSimpleName();
									System.out.println(S_msg_1);
								    }
								
								O_cell_style_target = O_wrk_book_target.createCellStyle();
							   	O_cell_style_target.cloneStyleFrom(O_cell_style_source);
							//   	HI_table_style_source_idxs.add(I_idx_cell_style_source);
							   	O_cell_dest.setCellStyle(O_cell_style_target);
							}
//							if (HI_table_style_source_idxs.contains(I_idx_cell_style_source)) {
//								O_cell_style_target = O_cell_style_source;
//							   }
//							else {
//							    O_cell_style_target = O_wrk_book_target.createCellStyle();
//							   	O_cell_style_target.cloneStyleFrom(O_cell_style_source);
//							   	HI_table_style_source_idxs.add(I_idx_cell_style_source);
//							   }
//							O_cell_dest.setCellStyle(O_cell_style_target);
						    }
					    }
				    }
			     }
			PB_O_ws_target.removeRow(O_row_dest_temp_dummy_1);
		}   // end for
		if (B_trace) {
			I_nbr_cell_styles_target_f1 = O_wrk_book_target.getNumCellStyles();
		    S_msg_1 = "Target work-book nbr of cell styles: " + I_nbr_cell_styles_target_f1;
		    System.out.println(S_msg_1);
		}
		return;
	}

	public static void FV_copy_wrk_sheet(
			XSSFSheet PB_O_ws, 
			Vector<XSSFRow> PI_AI_rows) {
		
		FV_copy_wrk_sheet(
			PB_O_ws, 
			PI_AI_rows,
			O_cell_copy_policy_failsafe);
		
		return;	
	}
	
	public static void FV_copy_wrk_sheet(
			XSSFSheet PB_O_ws_target, 
			XSSFSheet PB_O_ws_src) {
		
		FV_copy_wrk_sheet(
			PB_O_ws_target, 
			PB_O_ws_src,
			O_cell_copy_policy_failsafe);
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
			PI_O_cell_copy_policy = O_cell_copy_policy_failsafe;
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
