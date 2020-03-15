package com.github.michaelederaut.basics.xpath2cssselector;

import java.util.Stack;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator.EleType;
import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator.Elem;
// import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator.ParsingState;

import static org.apache.commons.lang3.StringUtils.LF;

public class DomRootElements {
	/**
	 * This class can be used to generate a node element in an <a href='https://www.guru99.com/xpath-selenium.html#3'><i>Absolute Xpath</i></a>.<br>
	 * It  contains 2 attributes for the context node<ul>
	 * <li><b>I_idx_f0</b>: <code>int</code> number of previous sibling elements<br>
	 *  For second parameter <i>contextNode</i> of method 
	 *  <a href="https://developer.mozilla.org/en-US/docs/Web/API/Document/evaluate"><code>document.evaluate(...)</code></a><br>
	 *  or the beginning of the only argument of method <a href='https://developer.mozilla.org/de/docs/Web/API/Document/querySelector'><code>document.QuerySelector(...)</code></a>.
	 *  </li>
	 * <li><b>S_node_name</b>: {@link String} HTML-tag of this element, defaults to <code>null</code>.</li>
	 * </ul>
	 * 
	 * @author <a href="mailto:michael.eder.vie@gmx.at?subject=github&nbsp;Selenium&nbsp;DomOffset">Mr. Michael Eder</a>
	 * 
	 **/
   public static class DomOffset {
		
		public int    I_idx_any_tag_f0;		  // for DOM root-node
		public String S_node_name;			  // for absolute xpath
		public int    I_idx_same_tag_f0;	  // == " ==
		
		public DomOffset(final int PI_I_idx_f0) {
			this(PI_I_idx_f0, (String)null, -1);
			return;
		}
		
		public DomOffset(
				final int PI_I_idx_any_tag_f0, 
				final String PI_I_S_node_name) {
			this(PI_I_idx_any_tag_f0, PI_I_S_node_name, -1);
			return;
		}
		
	public DomOffset(
				final int PI_I_idx_any_tag_f0, 
				final String PI_I_S_node_name,
				final int PI_I_idx_same_tag_f0) {
			this.I_idx_any_tag_f0   = PI_I_idx_any_tag_f0;
			this.S_node_name        = PI_I_S_node_name;
			this.I_idx_same_tag_f0  = PI_I_idx_same_tag_f0;
			return;
		}	
		
		@Override
		public String toString() {
			String S_retval;
			S_retval = "[" + this.I_idx_any_tag_f0 + "," + this.S_node_name + "," + I_idx_same_tag_f0 + "]";
		    return S_retval;
		}
	}
public static int[] FAI_reduce_DOM_offset_vector (final DomOffset PI_AO_dom_offsets[]) {
	int AI_retval_DOM_offsets[] = null;
	
	if (PI_AO_dom_offsets == null) {
		return AI_retval_DOM_offsets;
	    }
	
	IllegalArgumentException E_ill_arg;
	/* WebDriverException */ RuntimeException E_rt;
	DomOffset O_DOM_offset;
	String S_msg_1, S_msg_2, S_node_name;
	int i1, I_len_offset_vector_f1, I_dom_idx_f0;
	
	I_len_offset_vector_f1 = PI_AO_dom_offsets.length;
	AI_retval_DOM_offsets = new int[I_len_offset_vector_f1];
	
	for (i1 = 0; i1 < I_len_offset_vector_f1; i1++) {
		O_DOM_offset = PI_AO_dom_offsets[i1];
		I_dom_idx_f0 = O_DOM_offset.I_idx_any_tag_f0;
		if (I_dom_idx_f0 < 0) {
		    S_msg_1 = "Invalid negative offset: " + I_dom_idx_f0 + " at index: " + i1;
		        E_ill_arg = new IllegalArgumentException(S_msg_1);
		        S_msg_2 = "Unable to process parameter parameter of type array of \'" + DomOffset.class.getName() + "\'";
		        E_rt = new RuntimeException(S_msg_2, E_ill_arg);
		        throw E_rt;
	       }
		AI_retval_DOM_offsets[i1] = I_dom_idx_f0;
	    }
	
	return AI_retval_DOM_offsets;
}

public static DomOffset[] FAO_create_DOM_offsets(final int PI_AI_DOM_offset_vector[]) {
	DomOffset[] AO_retval_dom_offsets = null;
	
	if (PI_AI_DOM_offset_vector == null) {
		return AO_retval_dom_offsets;
	    }
	
	IllegalArgumentException E_ill_arg;
	/* WebDriverException */ RuntimeException E_rt;
	String S_msg_1, S_msg_2;
	DomOffset O_dom_offset;
	int i1, I_nbr_offsets_f1, I_offset_f0;
	
	I_nbr_offsets_f1 = PI_AI_DOM_offset_vector.length;
	AO_retval_dom_offsets = new DomOffset[I_nbr_offsets_f1];
	for (i1 = 0; i1 < I_nbr_offsets_f1; i1++) {
		I_offset_f0 = PI_AI_DOM_offset_vector[i1];
		if (I_offset_f0 < 0) {
		   S_msg_1 = "Invalid negative offset: " + I_offset_f0 + " at index: " + i1;
		   E_ill_arg = new IllegalArgumentException(S_msg_1);
		   S_msg_2 = "Unable to init actual parameter of type array of \'" + DomOffset.class.getName() + "\'";
		   E_rt = new RuntimeException(S_msg_2, E_ill_arg);
		   throw E_rt;
		   }
		O_dom_offset = new DomOffset(I_offset_f0, null);
		AO_retval_dom_offsets[i1] = O_dom_offset;
	    }
	return AO_retval_dom_offsets;
    }

public static StringBuilder FS_get_context_node (
		   final int PI_AI_DOM_offset_vector[]) {
	StringBuilder SB_retval_document_root;
	
	SB_retval_document_root = FS_get_context_node(
			PI_AI_DOM_offset_vector,
			(DomNavigator)null,
			(MutableBoolean)null);
	return SB_retval_document_root;
}

// e.g.
// var x = document.getElementById("myLI").parentNode.nodeName; 
// https://www.w3schools.com/jsref/prop_node_parentnode.asp

public static StringBuilder FS_get_context_node (
		   final int PI_AI_DOM_offset_vector[],
		   final DomNavigator PI_O_dom_navigator,
		   final MutableBoolean PO_B_multi) {
		
	  
	    IllegalArgumentException E_ill_arg;
    	RuntimeException         E_rt;
    	AssertionError           E_assert;
	    Object O_xpath_originated_value;
	    Stack<Elem> AO_dom_nav_elems;
	    Elem O_dom_navigator_pending;
	    EleType E_ele_type;
		
    	String S_msg_1, S_msg_2, S_xpath_originated_value;
    	boolean B_select_multiple_elements;
		int i1, i2, I_nbr_offsets_orig_f1, I_nbr_offsets_effective_f1, I_dom_idx_f0, I_idx_dom_nav_f0, I_idx_max_dom_nav_f1,
		         I_nbr_ups_f1, AI_DOM_offset_vector[];
	    
	    
		final String S_document_root = "document";
		StringBuilder SB_retval_document_root = new StringBuilder(S_document_root + ".body");
	
		
		if (PI_AI_DOM_offset_vector == null) {
			AI_DOM_offset_vector = new int[0];
		    }
		else {
			AI_DOM_offset_vector = PI_AI_DOM_offset_vector;
		    }
		
		I_idx_max_dom_nav_f1  = 0;
		I_idx_dom_nav_f0 = -1;
    	if (PI_O_dom_navigator != null) {
    	   AO_dom_nav_elems = PI_O_dom_navigator.AO_ele_types;
    	   if (AO_dom_nav_elems != null) {
    		  I_idx_max_dom_nav_f1 = AO_dom_nav_elems.size();
    	      }
    	   }
    	else {
    		AO_dom_nav_elems = null;
    	    }
    	I_nbr_ups_f1 = 0;
		I_nbr_offsets_orig_f1 = AI_DOM_offset_vector.length;
		if (I_idx_max_dom_nav_f1 > 0) {
			I_idx_dom_nav_f0 = 0;
			O_dom_navigator_pending = AO_dom_nav_elems.get(I_idx_dom_nav_f0);
			if (O_dom_navigator_pending.E_type == EleType.ups) {
				I_nbr_ups_f1 = (int)O_dom_navigator_pending.O_content;
				I_idx_dom_nav_f0++;
			}
		}
		
		if (I_nbr_ups_f1 > I_nbr_offsets_orig_f1) {
		   S_msg_1 = I_nbr_ups_f1 + " ups the DOM hierarchy exceeds maximum possible number of " + I_nbr_offsets_orig_f1 + ".";
	       E_rt = new RuntimeException(S_msg_1);
	       throw E_rt;
		   }
		B_select_multiple_elements = false;
		I_nbr_offsets_effective_f1 = I_nbr_offsets_orig_f1 - I_nbr_ups_f1;
		for (i1 = 0; i1 < I_nbr_offsets_effective_f1; i1++) {
			I_dom_idx_f0 = AI_DOM_offset_vector[i1];
			if (I_dom_idx_f0 < 0) {
			   S_msg_1 = "Invalid negative offset: " + I_dom_idx_f0 + " at index: " + i1;
	    	   E_ill_arg = new IllegalArgumentException(S_msg_1);
	    	   S_msg_2 = "Unable to evaluate parameter of type array of \'" + int.class.getName() + "\'";
	    	   E_rt = new RuntimeException(S_msg_2, E_ill_arg);
	    	   throw E_rt;
			   }
			SB_retval_document_root.append(".children[" + I_dom_idx_f0 + "]");
			}
		
		if (I_idx_dom_nav_f0 >= 0) {
			for (i1 = I_idx_dom_nav_f0; i1 < I_idx_max_dom_nav_f1; i1++) {
				if (B_select_multiple_elements) {
					S_msg_1 = "No element must follow a multiselector at index " + i1 + "." + LF +
							  "after selector string" + LF +
							  SB_retval_document_root; 
					E_assert = new AssertionError(S_msg_1);
					throw E_assert;
				   }
				O_dom_navigator_pending = AO_dom_nav_elems.get(i1);
				E_ele_type = O_dom_navigator_pending.E_type;
				O_xpath_originated_value = O_dom_navigator_pending.O_content;
				if (E_ele_type == EleType.ups) {
					I_nbr_ups_f1 = (int)O_xpath_originated_value;
					for (i2 = 0; i2 < I_nbr_ups_f1; i2++) {
						SB_retval_document_root.append(".parentNode");
					    }
				    }
				else {
					S_xpath_originated_value = (String)O_xpath_originated_value;
					switch (E_ele_type) {
//					case id: SB_retval_document_root.append(".getElementById('" + S_xpath_originated_value + "')"); 
//					   break;
					case id: 
						if (SB_retval_document_root.toString().endsWith(".body")) {
						   SB_retval_document_root = new StringBuilder(S_document_root + ".getElementById('" + S_xpath_originated_value + "')");
						   }
						else {
							S_msg_1 = "Element-type id is only valid at top level, but found" + LF +
									  SB_retval_document_root.toString();
							E_assert = new AssertionError(S_msg_1);
					        throw E_assert;
						    }
						break;
					case className: SB_retval_document_root.append(".getElementsByClassName('" + S_xpath_originated_value + "')");
					   B_select_multiple_elements = true;
					   break;
					case tagName: SB_retval_document_root.append(".getElementsByTagName('" + S_xpath_originated_value + "')");
					   B_select_multiple_elements = true;
					   break;
					}
				}
			}
		}
		
		if (PO_B_multi != null) {
			PO_B_multi.setValue(B_select_multiple_elements);
		    }
		return SB_retval_document_root;
	}
	
	public static StringBuilder FS_get_context_node (final DomOffset PI_AO_dom_offsets[]) {
		StringBuilder SB_retval_document_root;
		int AI_DOM_offsets[];
		
		AI_DOM_offsets = FAI_reduce_DOM_offset_vector(PI_AO_dom_offsets);
		SB_retval_document_root = FS_get_context_node(
				AI_DOM_offsets,
				(DomNavigator)null,
			    (MutableBoolean)null);
			
		return SB_retval_document_root;
	}	

	public static StringBuilder FS_get_context_node (
			final DomOffset PI_AO_dom_offsets[],
			final DomNavigator PI_O_dom_navigator,
			final MutableBoolean PO_B_multi) {
		StringBuilder SB_retval_document_root;
		int AI_DOM_offsets[];
		
		AI_DOM_offsets = FAI_reduce_DOM_offset_vector(PI_AO_dom_offsets);
		SB_retval_document_root = FS_get_context_node(
				AI_DOM_offsets,
				PI_O_dom_navigator,
				PO_B_multi);
		
		return SB_retval_document_root;
	}	
	
}
