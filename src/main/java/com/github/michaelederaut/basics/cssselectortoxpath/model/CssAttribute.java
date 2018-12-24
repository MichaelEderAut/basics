package com.github.michaelederaut.basics.cssselectortoxpath.model;

/**
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public class CssAttribute 
{
	private String name;
	private String value;
	private CssAttributeValueType type;
	public CssAttribute(String nameIn,String valueIn, String typeStringIn)
	{
		this(nameIn,valueIn,CssAttributeValueType.valueTypeString(typeStringIn));
		
	}
	public CssAttribute(String nameIn,String valueIn, CssAttributeValueType typeIn)
	{
		this.name=nameIn;
		this.value=valueIn;
		this.type=typeIn;
	}
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	public CssAttributeValueType getType() {
		return type;
	}
	
	@Override
	public String toString()
	{
		return "Name="+this.name+"; Value="+this.value+"; Type="+this.type;
	}
	
	@Override
	public boolean equals(Object cssAttribute)
	{
		if(cssAttribute==null)
		{
			return false;
		}
		return this.toString().equals(cssAttribute.toString());
	}
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

}
