package com.github.michaelederaut.basics.props;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerArrArrStr extends PropertyContainer {
	
public PropertyContainerArrArrStr(final String PI_AAS_attrs_array[][]) {
		this.E_type_source = TypeSource.array;
		this.AAO_attrs = PI_AAS_attrs_array;
		this.B_all_strings = true;
		return;
	    }
}
