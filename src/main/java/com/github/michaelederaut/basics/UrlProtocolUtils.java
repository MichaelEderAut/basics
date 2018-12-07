package com.github.michaelederaut.basics;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.vfsjfilechooser2.accessories.connection.Protocol;

public class UrlProtocolUtils {

public static URL FO_get_url(final String PI_S_url) {
	
	 RuntimeException E_rt;
	 String S_msg_1;
	 
	URL O_retval_url = null;
	
	try {
		O_retval_url = new URL(PI_S_url);
	} catch (MalformedURLException | NullPointerException PI_E_malformed_url) {
		S_msg_1 = "Unable to create Object of type: \'" + URL.class.getName() + "\' from \"" + PI_S_url + "\"" ;
		E_rt = new RuntimeException(S_msg_1, PI_E_malformed_url);
		throw E_rt;
	    }
	
	return O_retval_url;
}
	
public static Protocol FE_get_protocol(final URL PI_O_url) {
		
		AssertionError E_assert;
	    RuntimeException E_rt;
		URL O_res_url;
		String S_msg_1, S_msg_2, S_protocol_type_to_choose, S_protcol_type_expected;
		
		Protocol E_retval_protocol = null;
		
		S_protcol_type_expected = PI_O_url.getProtocol();
		
		LOOP_PROTOCOLS: for (Protocol E_protocol: Protocol.values()) {
			S_protocol_type_to_choose = E_protocol.getName();
			if (StringUtils.equalsIgnoreCase(S_protocol_type_to_choose, S_protcol_type_expected)) {
			   E_retval_protocol = E_protocol;
			   break LOOP_PROTOCOLS;
			   }
		    }
		if (E_retval_protocol == null) {
			S_msg_1 = "Unable to find protocol named: \'" + S_protcol_type_expected + "\' in " + Protocol.class.getName();
			E_assert = new AssertionError(S_msg_1);
			S_msg_2 = "Unable to determine protocol-type of URL: \"" + PI_O_url.toExternalForm() + "\""; 
			E_rt = new RuntimeException(S_msg_2, E_assert);
			throw E_rt;
		}
		return E_retval_protocol;
	}
	
public static Protocol FE_get_protocol(final String PI_S_url) {
	URL E_res_url;
	Protocol E_retval_protocol = null;
	
	E_res_url         = FO_get_url(PI_S_url);
	E_retval_protocol = FE_get_protocol(E_res_url);
	
	return E_retval_protocol;
    }
}