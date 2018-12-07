package com.github.michaelederaut.basics.props;

import java.util.List;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerListArrObj extends PropertyContainer {
		
		public PropertyContainerListArrObj(final List<Object[]> PI_LAO_attrs_array) {
		this.E_type_source = TypeSource.listOfArrays;
		this.AAO_attrs = PropertyContainerUtils.FLAO_to_array(PI_LAO_attrs_array);
	    }
}


