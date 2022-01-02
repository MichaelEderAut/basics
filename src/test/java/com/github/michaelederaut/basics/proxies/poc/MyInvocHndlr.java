package com.github.michaelederaut.basics.proxies.poc;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.Objects;
import org.apache.commons.lang3.ArrayUtils;
// import org.apache.commons.lang3.ObjectUtils;


public class MyInvocHndlr implements InvocationHandler {
	
	@Override
	public Object invoke(
			Object proxy,
            Method method,
            Object[] args)
              throws Throwable {
		
		String S_res, S_methods;
		StringBuffer SB_res;
		Class S_proxy_clazz;
		S_proxy_clazz = proxy.getClass();
		
	//	S_methods = ArrayUtils.toString(S_proxy_clazz.getMethods());
	//	S_methods = S_methods.replaceAll("\\,", "\n   ");
		
//		S_res = "proxy :  " + S_proxy_clazz.getName() + "\n" +
//		        "fields:  " + ArrayUtils.toString(S_proxy_clazz.getFields()) + "\n" +
//		        "methods: " + S_methods +  "\n" + 
//		        "dclcl: "   + ArrayUtils.toString(S_proxy_clazz.getDeclaringClass()) + "\n" +
//		        "method: " + method.getName() + "\n" + 
//				"args  : " + org.apache.commons.lang3.ArrayUtils.toString(args);
	
//		S_res =  "proxy: " + proxy.getClass().getName() + "\n" +
//		         method.getName() + "\n" ;
//		SB_res = new StringBuffer(S_res);
//		if (args != null) {}
		
		
		SB_res = ClassInstances.FSB_dump(proxy, "From within invocation-handler");
		
		System.out.println(SB_res);
		return null;
	}
}