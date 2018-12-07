package com.github.michaelederaut.basics.props;

import java.util.List;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerListArrStr extends PropertyContainer {
		
		public PropertyContainerListArrStr(final List<Object[]> PI_LAO_attrs_array) {
		this.E_type_source = TypeSource.listOfArrays;
		this.AAO_attrs = PropertyContainerUtils.FLAS_to_array(PI_LAO_attrs_array);
		this.B_all_strings = true;
	    }
}


