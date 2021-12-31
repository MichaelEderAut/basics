package com.github.michaelederaut.basics.proxies.poc;

import com.github.michaelederaut.basics.proxies.poc.MyInvocHndlr;
import java.lang.reflect.Proxy;

public class TestProxy {
	
	public static void main(final String args[]) {
		Class O_clazz;
		ClassLoader O_cls_ldr;
		Object O_proxy_class;
		MyInvocHndlr O_invocation_handler;
		
		String S_line_out;
		
		O_clazz = TestProxy.class;
		O_cls_ldr = O_clazz.getClassLoader();
		
		O_invocation_handler = new MyInvocHndlr();
		O_proxy_class = Proxy.newProxyInstance(
				O_cls_ldr, new Class[] {CharSequence.class}, O_invocation_handler);
		
	//	S_line_out = O_proxy_class.toString();
		S_line_out = ClassInstances.FS_dump(O_proxy_class, "Proxy-Instance");
		System.out.println(S_line_out);
		System.exit(0);
	}
}
