package com.github.michaelederaut.basics.cssselectortoxpath.model;

import java.util.ArrayList;
import java.util.List;
import  com.github.michaelederaut.basics.cssselectortoxpath.utilities.CssSelectorToXPathConverterException;

/**
* Take a string and the format of the string is...<br>
* xxx[yyy]...[zzz]<br>
* break it up into a list = xxx,[yyy],..,[zzz]<br>
* if xxx DNE==&gt;break into list=*,[yyy],...,[zzz]<br>
*<br>
* create new class<br>
* make into css element attribute<br>
* fills this stuff,<br>
* only has constructor and getters<br> 

 * 
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public class CssElementAttributes
{
	private String element;
	private List<CssAttribute> cssAttributeList;

	public CssElementAttributes(String elementIn, List<CssAttribute> cssAttributeListIn) throws CssSelectorToXPathConverterException
	{
		this.element=elementIn;
		this.cssAttributeList=new ArrayList<>(cssAttributeListIn);
	}
	public String getElement() 
	{
		return element;
	}

	public List<CssAttribute> getCssAttributeList() 
	{
		return cssAttributeList;
	}
	
	@Override
	public String toString()
	{
		return "Element="+this.element+", CssAttributeList="+this.cssAttributeList;
	}
	
	@Override
	public boolean equals(Object cssElementAttributes)
	{
		if(cssElementAttributes==null)
		{
			return false;
		}
		return this.toString().equals(cssElementAttributes.toString());
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}