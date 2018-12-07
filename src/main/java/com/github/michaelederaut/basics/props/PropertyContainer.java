package com.github.michaelederaut.basics.props;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;
import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeTarget;

public class PropertyContainer {
	
	public PatriciaTrie<Integer> HS_attrs;
	public ArrayList<String[]>   LAS_attrs;  // for later use
	public Object                AAO_attrs;  // Object[][] | ArrayList<Obj[]> | ArrayList<ArrayList> | AttributeList
	public TypeSource E_type_source;
	public TypeTarget E_type_target;
	public boolean B_all_strings;

	public PropertyContainer() {
		E_type_target = TypeTarget.patriciaTrie;
		return;
	}

	public String FS_get (final String PI_A_attr_name) {
	   	String S_retval_attr_val;
	   	S_retval_attr_val = this.FS_get(PI_A_attr_name, TypeTarget.patriciaTrie);
	   	return S_retval_attr_val;
	}
	
	public String FS_get (final String PI_A_attr_name, final TypeTarget PI_E_target) {
		
		Exception E_cause;
		AssertionError E_assert;
		IllegalArgumentException E_ill_arg;
		IndexOutOfBoundsException E_idx;
		NullPointerException E_np;
		RuntimeException E_rt;
		
		List<? extends Object> AS_attr_list;
		String S_msg_1, S_msg_2, S_attr_name, AS_attr_arr[];
		
		Object O_attr_name;
		Integer I_attr_val_pos_f0;
	  
		int i1, I_nbr_attrs_f1, I_pos_f0, I_size_name_value_pair_f1;
		String S_retval_attr_val = null;
	
		E_cause = null;
		if (this.HS_attrs == null) {
			this.HS_attrs = new PatriciaTrie<Integer>();
		    if (this.E_type_source == TypeSource.array) {
		  
		    	if (this.AAO_attrs == null) {
					return S_retval_attr_val;
				    }
			    I_nbr_attrs_f1 = ((String[][])this.AAO_attrs).length;
			    LOOP_ATTRS_ARR: for (i1 = 0; i1 < I_nbr_attrs_f1; i1++) {
					AS_attr_arr =  ((String[][])this.AAO_attrs)[i1];
					if (AS_attr_arr == null) {
					   S_msg_1 = "name-value pair of type \'" + int[].class.getName() + "\' at index: " + i1 + " must not be null.";
					   E_cause = new NullPointerException(S_msg_1);
				       break LOOP_ATTRS_ARR;	 
					   }
					I_size_name_value_pair_f1 = AS_attr_arr.length;
					if (I_size_name_value_pair_f1 < 2) {
					    S_msg_1 = "Size of name-value pair of type \'" + int[].class.getName() + "\' at index: " + i1 + " must be at least 2.";
					    E_cause = new IndexOutOfBoundsException(S_msg_1);
					    break LOOP_ATTRS_ARR;
					    }
					S_attr_name = AS_attr_arr[0];
					if (StringUtils.isBlank(S_attr_name)) {
					   S_msg_1 = "Attribute-name \'" + S_attr_name + "\' at index: " + i1 +  "must not be null, blank, or epmpty.";
					   E_cause = new NullPointerException(S_msg_1);
					   break LOOP_ATTRS_ARR;
					}
					I_attr_val_pos_f0 = new Integer(i1);
					this.HS_attrs.put(S_attr_name, I_attr_val_pos_f0);
			       }
		       }
		    else { // (E_type == Type.list) {
		    	List<? extends Object> AS_attrs_list;
		    	
		    	if (this.AAO_attrs == null) {
					return S_retval_attr_val;
		    	   }
				I_nbr_attrs_f1 = ((List<? extends List<? extends Object>>)(this.AAO_attrs)).size(); 
				LOOP_ATTRS_LIST: for (i1 = 0; i1 < I_nbr_attrs_f1; i1++) {
					AS_attrs_list = ((List<? extends List<? extends Object>>)(this.AAO_attrs)).get(i1);
					if (AS_attrs_list == null) {
					   S_msg_1 = "name-value pair of type \'" + List.class.getName() + "\' at row: " + i1 + " must not be null.";
					   E_cause = new NullPointerException(S_msg_1);
					   break LOOP_ATTRS_LIST;
					   }
					I_size_name_value_pair_f1 = AS_attrs_list.size(); 
					if (I_size_name_value_pair_f1 < 2) {
					    S_msg_1 = "Size of name-value pair of type \'" + List.class.getName() + "\' at rpw: " + i1 + " must be at least 2.";
					    E_cause = new IndexOutOfBoundsException(S_msg_1);
					    break LOOP_ATTRS_LIST;
					    }
					O_attr_name = AS_attrs_list.get(0);
					if (O_attr_name == null) {
					   S_msg_1 = "Attribute-name at row: " + i1 +  "must not be null.";
					   E_cause = new NullPointerException(S_msg_1);
					   break LOOP_ATTRS_LIST;
					   }  
					 if (!(O_attr_name instanceof String)) {
						S_msg_1 = "Class \'" + O_attr_name.getClass().getName() + "\' of attribute name at row " + i1 + " not assignement compatible with " + String.class.getName() + ".";
						E_cause = new ClassCastException(S_msg_1);
						break LOOP_ATTRS_LIST;
					    }
					 S_retval_attr_val = (String)O_attr_name;
				     }
		          }  // Type = List
		     }  // TRIE == null
		
		if (E_cause != null) {
		  S_msg_2 = "Unable to store value for key: \'" + PI_A_attr_name + "\' in container of type: \'" + this.HS_attrs.getClass().getSimpleName() + "\'.";
		  E_rt = new RuntimeException(S_msg_2, E_cause);
		  throw E_rt; }
		  
		I_attr_val_pos_f0 = this.HS_attrs.get(PI_A_attr_name);
		if (I_attr_val_pos_f0 == null) {
			return S_retval_attr_val;
			}
		
		I_pos_f0 = I_attr_val_pos_f0.intValue();
		if (this.E_type_source == TypeSource.array) {
			AS_attr_arr = ((String[][])this.AAO_attrs)[I_pos_f0];
			S_retval_attr_val = AS_attr_arr[1]; 
		    }
		else {
			AS_attr_list = ((List<? extends List<? extends Object>>)this.AAO_attrs).get(I_pos_f0);
			S_retval_attr_val = (String)AS_attr_list.get(1);
		    }
		
		return S_retval_attr_val;
	}
}
