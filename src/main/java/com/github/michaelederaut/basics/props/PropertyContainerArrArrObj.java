package com.github.michaelederaut.basics.props;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerArrArrObj extends PropertyContainer {
	
public PropertyContainerArrArrObj(final Object PI_AAO_attrs_array[][]) {
		this.E_type_source = TypeSource.array;
		this.AAO_attrs = PI_AAO_attrs_array;
		return;
	    }
}
