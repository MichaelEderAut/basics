package com.github.michaelederaut.basics.xpath2cssselector;

import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator;
// import com.github.michaelederaut.basics.xpath2cssselector.DomNavigator ;


public class TestDomNavi01 {
	
public static void main(final String args[]) {
	
	DomNavigator O_dom_navi_01, O_dom_navi_02, O_dom_navi_03;
	String S_xpath_01, S_xpath_02, S_xpath_03;
	
	S_xpath_01 = "//id('xyz1')";     // should be ok
	O_dom_navi_01 = DomNavigator.FO_create(S_xpath_01);
	S_xpath_02 = "//*[@id='xyz2']";  // should be ok
	O_dom_navi_02 = DomNavigator.FO_create(S_xpath_02);
	S_xpath_03 = "//div[@id='xyz3']";  // should produce no result bcs of tag name div
	O_dom_navi_03 = DomNavigator.FO_create(S_xpath_03);
	return;
}

}
