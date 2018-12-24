package com.github.michaelederaut.basics.cssselectortoxpath.utilities;
/**
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public class CssSelectorToXPathConverterException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public CssSelectorToXPathConverterException() {
		super();
	}
	
	public CssSelectorToXPathConverterException(String errorMessage) {
		super(errorMessage);
	}
	
	public CssSelectorToXPathConverterException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
