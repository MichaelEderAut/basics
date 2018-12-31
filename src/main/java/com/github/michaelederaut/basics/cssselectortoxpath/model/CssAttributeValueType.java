package com.github.michaelederaut.basics.cssselectortoxpath.model;

/**
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public enum CssAttributeValueType 
{
	EQUAL("="), 
	TILDE_EQUAL("~="),
	PIPE_EQUAL("|="),
	CARET_EQUAL("^="),
	DOLLAR_SIGN_EQUAL("$="),
	STAR_EQUAL("*=");

	private String equalString;

	private CssAttributeValueType(String nameIn)
	{
		this.equalString = nameIn;
	}

	public String getEqualStringName() {
		return equalString;
	}
	public static CssAttributeValueType valueTypeString(String unknownString) {
		if(unknownString==null)
		{
			return null;
		}

		switch (unknownString) 
		{
			case "=": 
                 return EQUAL;
        	case "~=":  
        		return TILDE_EQUAL;
        	case "|=": 
        		return PIPE_EQUAL;
        	case "$=":  
        		return DOLLAR_SIGN_EQUAL;
        	case "^=": 
        		return CARET_EQUAL;
        	case "*=":  
        		return STAR_EQUAL;
        	default:
        		throw new IllegalArgumentException(unknownString);
		}
	}
}
