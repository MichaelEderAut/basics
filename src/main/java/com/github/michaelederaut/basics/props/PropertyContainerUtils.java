package com.github.michaelederaut.basics.props;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang3.StringUtils;

public class PropertyContainerUtils /* <TwoDimensionalContainer> */ {
	
	/**
	 * <ul>
	 * <li>{@link String} property name</li>
	 * <li>{@link String} property value</li>
	 * </ul>
	 */
	public static final Class<?>[] AT_s2 = new Class<?>[] {String.class, String.class};
	public static final Class<?>[] AT_o2 = new Class<?>[] {Object.class, Object.class};
	/**
	 * 
	 * return-type of method, simple arrray([]) or interface {@link List}
	 *
	 */
	public static enum TypeSource {array, listOfArrays, list, propertyList};
	public static enum TypeTarget {patriciaTrie, arrayList, propertyList};
	
	public PatriciaTrie<Integer> HS_attrs;

    //	public String AAS_attrs_array[][];
    //	public List<? extends List<? extends Object>> AAS_attrs_list; 
	//  TwoDimensionalContainer AAS_attrs;
	Object  AAS_attrs;
	public TypeSource E_type;
	

public static Object[][] FLAO_to_array(
		final List<Object[]> PI_LLO_attrs_list) {
	
		Object AAO_retval[][];
		
		AAO_retval = PropertyContainerUtils.<Object>FLAO_to_array(PI_LLO_attrs_list, AT_o2);
	
		return AAO_retval;
	    }	

public static String[][] FLAS_to_array(
		final List<Object[]> PI_LLO_attrs_list) {
	
		String AAS_retval[][];
		
		AAS_retval = PropertyContainerUtils.<String>FLAO_to_array(PI_LLO_attrs_list, AT_s2);
	
		return AAS_retval;
	    }
	
	/**
	 * @see <a href=https://stackoverflow.com/questions/8191536/dynamic-return-type-in-java-method>Dynamic Return Type in Java Method</a>
	 * @param <AnyType> generic method type inference 
	 *        <a href=https://docs.oracle.com/javase/tutorial/java/generics/genTypeInference.html>Oracle Docs: Type Inference</a>
	 * @param PI_LAO_attrs_list  {@link List} of array of {@link Object Objects} to be converted to a 2 dimensional array of {@link Object Objects}.
	 * @param PI_AT_clazz {@link Class} for each input row. Defaults to <tt>Class&lt;String&gt;[2]</tt>.
	 * @return <tt>&lt;AnyType&gt;[][]</tt>
	 */
	public static <AnyType> AnyType[][] FLAO_to_array(
			final List<Object[]> PI_LAO_attrs_list,
			final Class<?> PI_AT_clazz[]) {
		
		 if (PI_LAO_attrs_list == null) {
			 return null;
		     }
		 NullPointerException E_np;
		 RuntimeException E_rt;
		 IllegalArgumentException E_ill_arg;
		 Throwable E_cause;
		 
		 Class<?> T_clazz_expected;
		 String S_msg_1, S_msg_2;
		 int i1, I_nbr_cols_expected_f1, I_nbr_cols_received_f1;
		 boolean B_all_strings_expected;
		 
		 B_all_strings_expected = true;
		 E_cause = null;
		 I_nbr_cols_expected_f1 = Integer.MAX_VALUE;
		 if (PI_AT_clazz == null) {
			 S_msg_1 = "Class Input-Parameter of type " + Class[].class.getSimpleName() + " must not be null.";
			 E_cause = new NullPointerException(S_msg_1);
			 }
		 else {
			 I_nbr_cols_expected_f1 = PI_AT_clazz.length;
			 if (I_nbr_cols_expected_f1 == 0) {
				S_msg_1 = "Class Input-Parameter of type " + Class[].class.getSimpleName() + " must contain elemts."; 
				E_cause = new IllegalArgumentException(S_msg_1);
			    }
			 else {
				LOOP_CLASSES: for (i1 = 0; i1 < I_nbr_cols_expected_f1; i1++) {
					T_clazz_expected = PI_AT_clazz[i1];
					if (T_clazz_expected == null) {
						S_msg_1 = "Class parameter with index: " + i1 + " must not be null";
						E_cause = new NullPointerException(S_msg_1);
						break LOOP_CLASSES;
					    }
					 if (!String.class.isAssignableFrom(T_clazz_expected)) {
						 B_all_strings_expected = false; 
					    }
				     }
			      }
			   }
		 if (E_cause != null) {
		    S_msg_2 = "Unable to determine target classes for function result.";
		    E_rt = new RuntimeException(S_msg_2, E_cause);
		    throw E_rt;
		    }
		 
		 int i2, I_nbr_attrs_f1;
		 Class<?> T_clazz_received;
		 Object O_attr_item, AO_row[];
		 Object AO_attr[];
		 
		 E_cause = null;
		 I_nbr_attrs_f1 = PI_LAO_attrs_list.size();
	
		 AnyType[][] AAO_retval;
		 
		 if (B_all_strings_expected) {
			 AAO_retval = (AnyType[][])(new String[I_nbr_attrs_f1][I_nbr_cols_expected_f1]); 
		     }
		 else {
		     AAO_retval = (AnyType[][])(new Object[I_nbr_attrs_f1][I_nbr_cols_expected_f1]);
	         }
		 LOOP_ROWS: for (i1 = 0; i1 < I_nbr_attrs_f1; i1++) {
			 AO_attr = PI_LAO_attrs_list.get(i1);
			 if (AO_attr == null) {
				 S_msg_1 = "Source for row number: " + i1 + " must not be null.";
				 E_cause = new NullPointerException(S_msg_1);
				 break LOOP_ROWS;
			     }
			 I_nbr_cols_received_f1 = AO_attr.length;
			 if (I_nbr_cols_received_f1 < I_nbr_cols_expected_f1) {
				S_msg_1 = "Actual number of items at row :" + i1 + ": " + I_nbr_cols_received_f1 + "-  expected: " + I_nbr_cols_expected_f1 + " .";
				E_cause = new IllegalArgumentException(S_msg_1);
				break LOOP_ROWS;
			     }
			 AO_row = AAO_retval[i1];
			 LOOP_COLS: for (i2 = 0; i2 < I_nbr_cols_expected_f1; i2++) {
		          O_attr_item = AO_attr[i2];
		          if (O_attr_item == null) {
		        	  AO_row[i2] = O_attr_item;
		              }
		          else {
		        	  T_clazz_expected = PI_AT_clazz[i2];
		        	  T_clazz_received = O_attr_item.getClass();
		        	  if (!(T_clazz_expected.isAssignableFrom(T_clazz_received))) {
		        		 S_msg_1 = "class-compatibility-error at row/col:" + i1 + "/" + i2 + 
		        				  " Expected class: \'" + T_clazz_expected.getName() + 
		        				   "\' not assignable from received class: \'" +  T_clazz_received.getName() + "\'";
		        		 E_cause = new ClassCastException(S_msg_1);
		        		 break LOOP_ROWS;
		        	     }
		        	  AO_row[i2] = T_clazz_expected.cast(O_attr_item);
		              }
			       }
		        }
		 if (E_cause != null) {
			S_msg_2 = "Unable to determine function result of type: \'" + AAO_retval.getClass().getName() + "\'.";
			E_rt = new RuntimeException(S_msg_2, E_cause);
			AAO_retval = null; // bcs of gc
			throw E_rt;
			}
		 return AAO_retval;
	}
	
	
	
	public PropertyContainerUtils(final List<? extends List<? extends Object>> PI_AAS_attrs_list) {
		E_type = TypeSource.list;
		this.AAS_attrs = PI_AAS_attrs_list;
	}
	
	
	public static String[][] FLLS_to_array(
			final List<? extends List<? extends Object>> PI_LLS_attrs_list) {
		String AAS_retval[][];
		
		AAS_retval = PropertyContainerUtils.<String>FLLO_to_array(PI_LLS_attrs_list, AT_s2);
	
		return AAS_retval;
	    }
	
	/**
	 * @see <a href=https://stackoverflow.com/questions/8191536/dynamic-return-type-in-java-method>Dynamic Return Type in Java Method</a>
	 * @param <AnyType> generic method type inference 
	 *        <a href=https://docs.oracle.com/javase/tutorial/java/generics/genTypeInference.html>Oracle Docs: Type Inference</a>
	 * @param PI_LLO_attrs_list 2 dimensional {@link List} of {@link Object Objects} to be converted to a 2 dimensional array of {@link Object Objects}.
	 * @param PI_LT_clazz {@link Class} for each input row. Defaults to <tt>Class&lt;String&gt;[2]</tt>.
	 * @return <tt>&lt;AnyType&gt;[][]</tt>
	 */
	public static <AnyType> AnyType[][] FLLO_to_array(
			final List<? extends List<? extends Object>> PI_LLO_attrs_list,
			final Class<?> PI_LT_clazz[]) {
		
		 if (PI_LLO_attrs_list == null) {
			 return null;
		     }
		 NullPointerException E_np;
		 RuntimeException E_rt;
		 IllegalArgumentException E_ill_arg;
		 Throwable E_cause;
		 
		 Class<?> T_clazz_expected;
		 String S_msg_1, S_msg_2;
		 int i1, I_nbr_cols_expected_f1, I_nbr_cols_received_f1;
		 boolean B_all_strings_expected;
		 
		 B_all_strings_expected = true;
		 E_cause = null;
		 I_nbr_cols_expected_f1 = Integer.MAX_VALUE;
		 if (PI_LT_clazz == null) {
			 S_msg_1 = "Class Input-Parameter of type " + Class[].class.getSimpleName() + " must not be null.";
			 E_cause = new NullPointerException(S_msg_1);
			 }
		 else {
			 I_nbr_cols_expected_f1 = PI_LT_clazz.length;
			 if (I_nbr_cols_expected_f1 == 0) {
				S_msg_1 = "Class Input-Parameter of type " + Class[].class.getSimpleName() + " must contain elemts."; 
				E_cause = new IllegalArgumentException(S_msg_1);
			    }
			 else {
				LOOP_CLASSES: for (i1 = 0; i1 < I_nbr_cols_expected_f1; i1++) {
					T_clazz_expected = PI_LT_clazz[i1];
					if (T_clazz_expected == null) {
						S_msg_1 = "Class parameter with index: " + i1 + " must not be null";
						E_cause = new NullPointerException(S_msg_1);
						break LOOP_CLASSES;
					    }
					 if (!String.class.isAssignableFrom(T_clazz_expected)) {
						 B_all_strings_expected = false; 
					    }
				     }
			      }
			   }
		 if (E_cause != null) {
		    S_msg_2 = "Unable to determine target classes for function result.";
		    E_rt = new RuntimeException(S_msg_2, E_cause);
		    throw E_rt;
		    }
		 
		 int i2, I_nbr_attrs_f1;
		 Class<?> T_clazz_received;
		 Object O_attr_item, AO_row[];
		 List<? extends Object> LO_attr;
		 
		 E_cause = null;
		 I_nbr_attrs_f1 = PI_LLO_attrs_list.size();
	
		 AnyType[][] AAO_retval;
		 
		 if (B_all_strings_expected) {
			 AAO_retval = (AnyType[][])(new String[I_nbr_attrs_f1][I_nbr_cols_expected_f1]); 
		     }
		 else {
		     AAO_retval = (AnyType[][])(new Object[I_nbr_attrs_f1][I_nbr_cols_expected_f1]);
	      }
		 LOOP_ROWS: for (i1 = 0; i1 < I_nbr_attrs_f1; i1++) {
			 LO_attr = PI_LLO_attrs_list.get(i1);
			 if (LO_attr == null) {
				 S_msg_1 = "Source for row number: " + i1 + " must not be null.";
				 E_cause = new NullPointerException(S_msg_1);
				 break LOOP_ROWS;
			     }
			 I_nbr_cols_received_f1 = LO_attr.size();
			 if (I_nbr_cols_received_f1 < I_nbr_cols_expected_f1) {
				S_msg_1 = "Actual number of items at row :" + i1 + ": " + I_nbr_cols_received_f1 + "-  expected: " + I_nbr_cols_expected_f1 + " .";
				E_cause = new IllegalArgumentException(S_msg_1);
				break LOOP_ROWS;
			     }
			 AO_row = AAO_retval[i1];
			 LOOP_COLS: for (i2 = 0; i2 < I_nbr_cols_expected_f1; i2++) {
		          O_attr_item = LO_attr.get(i2);
		          if (O_attr_item == null) {
		        	  AO_row[i2] = O_attr_item;
		              }
		          else {
		        	  T_clazz_expected = PI_LT_clazz[i2];
		        	  T_clazz_received = O_attr_item.getClass();
		        	  if (!(T_clazz_expected.isAssignableFrom(T_clazz_received))) {
		        		 S_msg_1 = "class-compatibility-error at row/col:" + i1 + "/" + i2 + 
		        				  " Expected class: \'" + T_clazz_expected.getName() + 
		        				   "\' not assignable from received class: \'" +  T_clazz_received.getName() + "\'";
		        		 E_cause = new ClassCastException(S_msg_1);
		        		 break LOOP_ROWS;
		        	     }
		        	  AO_row[i2] = T_clazz_expected.cast(O_attr_item);
		              }
			       }
		        }
		 if (E_cause != null) {
			S_msg_2 = "Unable to determine function result of type: \'" + AAO_retval.getClass().getName() + "\'.";
			E_rt = new RuntimeException(S_msg_2, E_cause);
			AAO_retval = null; // bcs of gc
			throw E_rt;
			}
		 return AAO_retval;
	}
}
