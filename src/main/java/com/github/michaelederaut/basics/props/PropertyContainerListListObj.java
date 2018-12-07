package com.github.michaelederaut.basics.props;

import java.util.List;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerListListObj extends PropertyContainer {
		public PropertyContainerListListObj(final List<List<Object>> PI_LLO_attrs_array) {
		this.E_type_source = TypeSource.list;
		this.AAO_attrs = PropertyContainerUtils.FLLS_to_array(PI_LLO_attrs_array);
	    }
	
}


