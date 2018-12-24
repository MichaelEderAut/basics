package com.github.michaelederaut.basics.cssselectortoxpath.model;

import com.github.michaelederaut.basics.cssselectortoxpath.utilities.CssElementAttributeParser;
import com.github.michaelederaut.basics.cssselectortoxpath.utilities.CssSelectorToXPathConverterException;

/**
 * 
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public class CssElementCombinatorPair {
	private CssCombinatorType combinatorType;
	private CssElementAttributes cssElementAttributes;
	
	public CssElementCombinatorPair(CssCombinatorType combinatorTypeIn, String cssElementAttributesStringIn) throws CssSelectorToXPathConverterException
	{
		this.combinatorType=combinatorTypeIn;
		this.cssElementAttributes=new CssElementAttributeParser().createElementAttribute(cssElementAttributesStringIn);
	}
	public CssCombinatorType getCombinatorType() {
		return combinatorType;
	}
	public CssElementAttributes getCssElementAttributes() {
		return cssElementAttributes;
	}
	
	@Override
	public String toString()
	{
		return "(Combinator="+this.getCombinatorType()+", "+this.cssElementAttributes+")";
	}
	@Override
	public boolean equals(Object cssElementCombinatorPair)
	{
		if(cssElementCombinatorPair==null)
		{
			return false;
		}
		return this.toString().equals(cssElementCombinatorPair.toString());
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
