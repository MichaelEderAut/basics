package com.github.michaelederaut.basics;

import regexodus.Pattern;

import com.github.michaelederaut.basics.RegexpUtils.GroupMatchResult;
import org.jsoup.safety.Whitelist;
import org.w3c.dom.DOMException;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.stanbol.enhancer.engines.htmlextractor.impl.DOMBuilder;
import org.dom4j.IllegalAddException;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlUtils {

	public static final String S_re_remove_comments =  "<!--[\\s\\S]*?-->";
	public static final String S_re_doc_type = "^\\s*(<\\!DOCTYPE\\s+([^>]+)>)\\s*(.*?)$";
	public static final Pattern P_doc_type = Pattern.compile(S_re_doc_type, Pattern.DOTALL | Pattern.IGNORE_CASE);
//	public static final String S_re_html_trunk  = "^\\s*(<html[^>]*>)\\s*(<head[^>]*>" +
//	                                              "(.*?)<\\/head>)\\s*(.*?)$";
	public static final String S_re_html_trunk  = 
	   "^\\s*(<html[^>]*>)\\s*" + 
	    "(<head[^>]*>(.*?)<\\/head>)\\s*" +
	    "(<body[^>]*>(.*?)<\\/body>)\\s*" +
	    "<\\/html>\\s*$";
	public static final Pattern P_html_trunk = Pattern.compile(S_re_html_trunk, Pattern.DOTALL);
	
	public static class Components {
		public String S_doctype_outer;
		public String S_doctype_inner;
		public String S_html;
		public String S_head_outer;
		public String S_head_inner;
		public String S_body_outer;
		public String S_body_inner;
	}	
	
	
	public static Components FO_split(final String PI_S_html_raw) {
		
		GroupMatchResult O_grp_match_result;
		
		String S_html_no_comments, S_html_trunk;
		Components O_retval_components = new Components();
		
		S_html_no_comments = PI_S_html_raw.replaceAll(S_re_remove_comments, "");
		O_grp_match_result = RegexpUtils.FO_match(S_html_no_comments, P_doc_type);
		if (O_grp_match_result.I_array_size_f1 >= 4) {
			O_retval_components.S_doctype_outer = O_grp_match_result.AS_numbered_groups[1];
			O_retval_components.S_doctype_inner = O_grp_match_result.AS_numbered_groups[2];
			S_html_trunk                        = O_grp_match_result.AS_numbered_groups[3];
		    }
		else {
			S_html_trunk = S_html_no_comments;
		  }
		O_grp_match_result = RegexpUtils.FO_match(S_html_trunk, P_html_trunk);
		if (O_grp_match_result.I_array_size_f1 >= 6) {
			O_retval_components.S_html       = O_grp_match_result.AS_numbered_groups[1];
			O_retval_components.S_head_outer = O_grp_match_result.AS_numbered_groups[2];
			O_retval_components.S_head_inner = O_grp_match_result.AS_numbered_groups[3];
			O_retval_components.S_body_outer = O_grp_match_result.AS_numbered_groups[4];
			O_retval_components.S_body_inner = O_grp_match_result.AS_numbered_groups[5];
		}
		return O_retval_components;
	}
	
	
/**
 * 	
 * @param PI_S_html {@link java.lang.String String} input <a href="https://www.w3schools.com/html">html</a> source
 * @param PO_O_jsoup_doc {@link org.apache.commons.lang3.mutable.MutableObject MutableObject}
 *        <br>
 *        optional parameter for returning the intermediary {@link org.jsoup.nodes.Document Jsoup-Document}<br>
 *        initializisation: {@code new MutableObject<org.jsoup.nodes.Document>();}<br>  
 * @return {@link org.dom4j.Document dom4j.Document} generated <i>Document</i> from the input source-
 * 
 * @see 
 * <a href="https://stackoverflow.com/questions/17802445/how-to-convert-a-jsoup-document-to-a-w3c-document">How to convert a jsoupdocument to a w3c-document</a><br>
 * <a href="https://mvnrepository.com/artifact/org.apache.stanbol/org.apache.stanbol.enhancer.engines.htmlextractor">Maven Repository Apache Stanbol HTML-Extractor</a>
 * 
 */
	
public static org.dom4j.Document FO_create_dom4j_doc(
		final String PI_S_html,
		final MutableObject<org.jsoup.nodes.Document> PO_O_jsoup_doc) {
	
	RuntimeException         E_rt;
	org.jsoup.nodes.Document O_document_jsoup;
	HashMap<String,String>   HS_name_space;
	DocumentBuilderFactory   O_doc_bldr_factory;
	DocumentBuilder          O_doc_bldr;
	
	org.dom4j.Document       O_retval_dom4j_doc = null;
	
	String S_msg_1;
	
	if (PO_O_jsoup_doc!= null) {
		PO_O_jsoup_doc.setValue(null);
	    }
	
	if (StringUtils.isBlank(PI_S_html)) {
	   return O_retval_dom4j_doc;
 	   }
	
	O_document_jsoup = Jsoup.parse(PI_S_html);
	
	if (PO_O_jsoup_doc!= null) {
		PO_O_jsoup_doc.setValue(O_document_jsoup);
	    }

	HS_name_space = new HashMap<String,String>();
	 
    O_retval_dom4j_doc = new DOMDocument();
    HtmlUtils.FV_create_dom4j(
    		O_document_jsoup, 
    		O_retval_dom4j_doc, 
    		O_retval_dom4j_doc, 
    		HS_name_space);
    
    		
	return O_retval_dom4j_doc;
	
     }

/**
 * The internal helper that copies content from the specified  {@link org.jsoup.nodes.Node Jsoup.Node} into a {@link org.dom4j.Node dom4j.Node}.
 * @param PI_O_node_jsoup The  {@link org.jsoup.nodes.Node Jsoup.Node} containing the content to copy to the specified {@link org.dom4j.Node dom4j.Node}.
 * @param PO_O_node_dom4j The  {@link org.dom4j.Node dom4j.Node} that receives the <a href="https://dom4j.github.io">dom4j</a> content.
 * @param PI_O_doc_dom4j legacy param of type {@link org.dom4j.Document dom4j.Document} for creating new  {@link org.dom4j.Node Nodes} by tag-name.
 * @param PB_O_hs_name_space {@link java.util.Map Map}&lt;{@link java.lang.String String}, {@link java.lang.String String}&gt; for XMX-Namespaces. 
 * 
 */

protected static void FV_create_dom4j(
		  org.jsoup.nodes.Node PI_O_node_jsoup,
		  org.dom4j.Node       PO_O_node_dom4j,
		  org.dom4j.Document   PI_O_doc_dom4j, 
		  Map<String,String>    PB_O_hs_name_space) throws 
ClassCastException, DOMException, IllegalAddException {         
if (PI_O_node_jsoup instanceof org.jsoup.nodes.Document) {
  	org.jsoup.nodes.Document O_doc_jsoup;
    
    O_doc_jsoup = ((org.jsoup.nodes.Document)PI_O_node_jsoup);
    for (org.jsoup.nodes.Node O_node_jsoup : O_doc_jsoup.childNodes()) {
        FV_create_dom4j(O_node_jsoup, PO_O_node_dom4j, PI_O_doc_dom4j, PB_O_hs_name_space);
    }
    
  } else if (PI_O_node_jsoup instanceof org.jsoup.nodes.Element) {
  	   org.dom4j.Element          O_elem_dom4j;
  	   org.jsoup.nodes.Element    O_elem_jsoup;
  	   String                     S_tag_name;
  	   org.jsoup.nodes.Attributes AO_attrs;
  	   String S_attr_name;
  	
	      O_elem_jsoup = ((org.jsoup.nodes.Element)PI_O_node_jsoup);
	      S_tag_name = O_elem_jsoup.tagName();
	      O_elem_dom4j = ((org.dom4j.tree.AbstractBranch)PO_O_node_dom4j).addElement(S_tag_name);
	      AO_attrs = O_elem_jsoup.attributes();
	      
	      for(org.jsoup.nodes.Attribute a : AO_attrs){
	        S_attr_name = a.getKey();
	        //omit xhtml namespace
	        if (S_attr_name.equals("xmlns")) {
	           continue;
	        }
	        String attPrefix = FS_get_ns_prefix(S_attr_name);
	        if (attPrefix != null) {
	          if (attPrefix.equals("xmlns")) {
	             PB_O_hs_name_space.put(FS_get_local_name(S_attr_name), a.getValue());
	          }
	          else if (!attPrefix.equals("xml")) {
	            String namespace = PB_O_hs_name_space.get(attPrefix);
	            if (namespace == null) {
	              //fix attribute names looking like qnames
	              S_attr_name = S_attr_name.replace(':','_');
	            }
	          }
	        }
	       // O_elem_dom4j.setAttribute(attName, a.getValue());
	        O_elem_dom4j.addAttribute(S_attr_name, a.getValue());
	      }
	      
	      for (org.jsoup.nodes.Node n : O_elem_jsoup.childNodes()) {
	        FV_create_dom4j(n, O_elem_dom4j, PI_O_doc_dom4j, PB_O_hs_name_space);
	      }
    
  } else if (PI_O_node_jsoup instanceof org.jsoup.nodes.TextNode) {
  	 org.jsoup.nodes.TextNode O_jsoup_text_node;
  	
  	 
      O_jsoup_text_node = ((org.jsoup.nodes.TextNode)PI_O_node_jsoup);
      if (!(PO_O_node_dom4j instanceof Document)) {
      	String                   S_text_content;
 
      	S_text_content = O_jsoup_text_node.text();
          PO_O_node_dom4j.setText(S_text_content);
    }
  }
}

// some hacks for handling namespace in jsoup2DOM conversion
private static String FS_get_ns_prefix(String name) {
	if (StringUtils.isNotBlank(name)) {
       int pos = name.indexOf(':');
       if (pos > 0) {
          return name.substring(0,pos);
       }
    }
    return null;
}

private static String FS_get_local_name(String name) {
	if (StringUtils.isNotBlank(name)) {
       int pos = name.lastIndexOf(':');
       if (pos > 0) {
          return name.substring(pos+1);
          }
       }
    return name;
    }
}
	

