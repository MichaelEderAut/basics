package com.github.michaelederaut.basics;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;


public class EnumReflectionUtils {
	
public static final String ORDINAL = "ordinal";
public static final String NAME    = "name";

public static final void FV_set_ordinal (Enum<?> PB_enum, int PI_I_ordinal) {
	
	Field  AO_fields[], O_field;
	RuntimeException E_rt;
	String S_msg_1, S_name;
	
	try {
		FieldUtils.writeField(PB_enum, ORDINAL, PI_I_ordinal, true);
	} catch (IllegalAccessException| IllegalArgumentException PI_E_ill_arg) {
		S_msg_1 = "Unable to update field+ \'" +  ORDINAL + " in object of type " +  PB_enum.getClass() + " to " +  PI_I_ordinal + "."; 
		E_rt = new RuntimeException(S_msg_1, PI_E_ill_arg);
		throw E_rt;
	}	
	
	AO_fields = PB_enum.getClass().getFields();
	O_field = AO_fields[PI_I_ordinal];
	S_name = O_field.getName();
	
	try {
		FieldUtils.writeField(PB_enum, NAME, S_name, true);
	} catch (IllegalAccessException| IllegalArgumentException PI_E_ill_arg) {
		S_msg_1 = "Unable to update field+ \'" +  NAME + " in object of type " +  PB_enum.getClass() + " to \'" +  S_name + "\'."; 
		E_rt = new RuntimeException(S_msg_1, PI_E_ill_arg);
		throw E_rt;
	}	
	
}
	public static final void FV_modify_ordinal_by (Enum<?> PB_enum, int PI_I_diff) {
	
	
	int I_ordinal_old, I_ordinal_new;
	
	I_ordinal_old = PB_enum.ordinal();
	I_ordinal_new = I_ordinal_old + PI_I_diff;
	FV_set_ordinal(PB_enum, I_ordinal_new);
	return;
    }
}
