package com.github.michaelederaut.basics.props;

import java.util.List;

import com.github.michaelederaut.basics.props.PropertyContainerUtils.TypeSource;

public class PropertyContainerListListString extends PropertyContainer {
		public PropertyContainerListListString(final List<List<String>> PI_LLS_attrs_array) {
		this.E_type_source = TypeSource.list;
		this.AAO_attrs = PropertyContainerUtils.FLLS_to_array(PI_LLS_attrs_array);
		this.B_all_strings = true;
	    }
	
}


