package com.github.michaelederaut.basics.cssselectortoxpath.utilities;
/**
 * @author Mr. Samuel Rosenthal<br>
 * Adapted by Mr. Michael Eder
 * @see
 * <a href="https://css-selector-to-xpath.appspot.com">Samuel Rosenthal - Css Selector to Xpath</a>
 *
 */
public class CssSelectorToXPathConverterUnsupportedPseudoClassException extends CssSelectorToXPathConverterException {

	private static final long serialVersionUID = 1L;
	private static String PSEUDO_CLASS_UNSUPPORTED_ERROR_FORMAT="Unable to convert(%s). A converter for CSS Seletor Pseudo-Classes has not been implement at this time. TODO: A future capability.";
	public CssSelectorToXPathConverterUnsupportedPseudoClassException(String pseudoClass) {
		super(getPseudoClassUnsupportedError(pseudoClass));
	}
	public static String getPseudoClassUnsupportedError(String pseudoClass)
	{
		return String.format(PSEUDO_CLASS_UNSUPPORTED_ERROR_FORMAT, pseudoClass);
		
	}
}
