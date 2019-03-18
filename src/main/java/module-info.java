module basics {
	exports com.github.michaelederaut.basics.joox;
	exports com.github.michaelederaut.basics.cssselectortoxpath.model;
	exports com.github.michaelederaut.basics.joox.selector;
	exports com.github.michaelederaut.basics;
	exports com.github.michaelederaut.basics.props;
	exports com.github.michaelederaut.basics.cssselectortoxpath.utilities;
	exports com.github.michaelederaut.basics.xml;
	exports com.github.michaelederaut.basics.xpath2cssselector;

	requires com.google.common;
	requires commons.collections4;
	requires commons.io;
	requires dom4j;
	requires java.desktop;
	requires java.prefs;
	requires java.sql;
	requires java.xml;
	requires java.xml.bind;
	requires jsoup;
	requires org.apache.commons.lang3;
	requires plexus.utils;
	requires regexodus;
	requires vfsjfilechooser2;
}