package com.github.michaelederaut.basics.proxies.poc;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
// import org.apache.commons.lang3.ArrayUtils;

import static com.github.michaelederaut.basics.ToolsBasics.LS;

public class ClassInstances {
	
	public static StringBuffer FSB_dump (
			Object PI_O_instance,
			String PI_S_title) {

   String S_title;
   
   if (StringUtils.isEmpty(PI_S_title)) { 
	   S_title = "";
       }
   else {
	   S_title = PI_S_title + "" + LS; 
       }
   StringBuffer SB_retval = new StringBuffer(S_title);
   StringBuffer SB_methods = new StringBuffer("Methods:"); 
   StringBuffer SB_fields = new StringBuffer("Fields:");  
    
    Field AO_fields[], O_field;
    Method AO_methods[], O_method;
    String S_method, S_methods, S_fields, S_field;
    int i1, I_nbr_methods_f1, I_nbr_methods_f0, I_nbr_fields_f1, I_nbr_fields_f0;
    
    SB_retval.append("Name:");
    if (PI_O_instance == null) {
       SB_retval.append(" <null>" + LS);
       return SB_retval;
       }
    
    Class O_clazz = PI_O_instance.getClass();
    String S_clazz_name = O_clazz.getName();
    SB_retval.append(" " + S_clazz_name + LS);
   
    AO_fields = O_clazz.getFields();
    if (AO_fields == null) {
       I_nbr_fields_f1 = 0;
       }
    else {
       I_nbr_fields_f1 = AO_fields.length;
       }
    if (I_nbr_fields_f1 == 0) {
    	SB_fields.append(" <null>");	
        }
    else {
       I_nbr_fields_f0 = I_nbr_fields_f1 - 1;
       for (i1 = 0; i1 < I_nbr_fields_f1; i1++) {
    	  O_field = AO_fields[i1];
    	  if (O_field == null) {
    		 S_field = "<null>"; }
    	else {
    		 S_field = O_field.toString();
    		 }
    	SB_fields.append(LS + "  " + S_field);
       }
    }
    SB_fields.append(LS);
    SB_retval.append(SB_fields);
    
    AO_methods = O_clazz.getMethods();
    if (AO_methods == null) {
       I_nbr_methods_f1 = 0;
       }
    else {
       I_nbr_methods_f1 = AO_methods.length;
       }
    if (I_nbr_methods_f1 == 0) {
    	SB_methods.append(" <null>");	
        }
    else {
       I_nbr_methods_f0 = I_nbr_methods_f1 - 1;
       for (i1 = 0; i1 < I_nbr_methods_f1; i1++) {
    	  O_method = AO_methods[i1];
    	  if (O_method == null) {
    		 S_method = "<null>"; }
    	else {
    		 S_method = O_method.toString();
    		 }
    	SB_methods.append(LS + "  " + S_method);
       }
    }
    SB_methods.append(LS);
    SB_retval.append(SB_methods);
   // "fields:  " + ArrayUtils.toString(S_proxy_clazz.getFields()) + "\n" +
    
	return SB_retval; 		
	}
	
	
	public static StringBuffer FSB_dump (Object PI_O_instance) {
		
		StringBuffer SB_retval;
		
		SB_retval = FSB_dump(PI_O_instance, null);
		
		return SB_retval;
	}
	
	
	public static String FS_dump (Object PI_O_instance) {
		
		String S_retval;
		String S_res;
		
		S_res = FS_dump(PI_O_instance, (String)null);
		S_retval = S_res.toString();
		
		return S_retval;
	}
	
	public static String FS_dump (Object PI_O_instance, String PI_S_title) {
		
		String S_retval;
		StringBuffer SB_res;
		
		SB_res = FSB_dump(PI_O_instance, PI_S_title);
		S_retval = SB_res.toString();
		
		return S_retval;
	}
	
}
