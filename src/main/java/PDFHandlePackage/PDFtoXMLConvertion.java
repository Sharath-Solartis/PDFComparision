package PDFHandlePackage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.snowtide.PDF;
import com.snowtide.pdf.Configuration;
import com.snowtide.pdf.OutputHandler;
import com.snowtide.pdf.Page;
import com.snowtide.pdf.layout.Block;
import com.snowtide.pdf.layout.Region;
import com.snowtide.pdf.layout.Table;
import com.snowtide.pdf.layout.TextUnit;

@SuppressWarnings("unused")
public class PDFtoXMLConvertion extends OutputHandler 
{   
    private static final String ELT_BOLD = "bold";
    private static final String ELT_UNDERLINED = "underlined";
    private static final String ELT_ITALIC = "italic";
    private static final String ELT_TEXT = "text";
    private static final String ELT_STRUCKTHROUGH = "strikethrough";   
    private final Document doc;
    private final Element root;
	private static String xmlfile;
    private Element textEltParent;
    private Element currentElt;
    
    private String linebreak = Configuration.getDefault().getLinebreakString();
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderlined = false;
    private boolean isStruckThrough = false;
	private StringBuilder whitespace = new StringBuilder(512);
	public boolean flag=false;
  
    public PDFtoXMLConvertion () throws IOException 
    {
        try 
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            doc.appendChild(currentElt = root = doc.createElement("pdftsExtract"));
        } catch (Exception e) {
            throw new IOException("Configuration error encountered while initializing XML facilities: " +e.getMessage());
        }
    }
    
    public void startPDF (String pdfName, File pdfFile) 
    {
    	
    		newContext("pdf").setAttribute("name", pdfName);
    	
    }

    public void endPDF (String pdfName, File pdfFile) 
    {
       closeText();
       closeContext("pdf");
    }
    
    public void startPage (Page page) 
    {
    	linebreak = page.getConfig().getLinebreakString();
        closeText();
        newContext("page").setAttribute("number", Integer.toString(page.getPageNumber()));
    }
    
    public void endPage (Page page) 
    {
        closeText();
        closeContext("page");
    }
    

    public void startBlock (Block block) 
    {
        closeText();
        Element e = newContext("block");
        if (block instanceof Table) e.setAttribute("type", "table");
        writeCoordsAsAttrs(block.bounds(), e);
    }
    
    public void endBlock (Block block) 
    {
        closeText();
        closeContext("block");
    }

    public void textUnit (TextUnit tu) 
    {
        openText();
        
        normalizeStyleElts(tu.getFont().isBold(), tu.getFont().isItalic(), tu.isUnderlined(), tu.isStruckThrough());
        
        String s;
        if (tu.getCharacterSequence() == null) 
        {
            int cc = tu.getCharCode();
            if (cc < 32) 
            {
            	return;
            } 
            else 
            {
            	s = Character.toString((char)cc);
            }
        } 
        else 
        {
        	s = new String(tu.getCharacterSequence());
        }
        currentElt.appendChild(doc.createTextNode(s));
    }
    public void spaces (int spaceCnt) 
    {
        openText().appendChild(doc.createTextNode(getStringOf(" ", spaceCnt)));
    }
    
    public void linebreaks (int linebreakCnt)
    {
        openText().appendChild(doc.createTextNode(getStringOf(linebreak, linebreakCnt)));
    }

    public Document getXMLDocument () 
    {
    	return doc;
    }
    
    private Element newContext (String tag) 
    {
    	return currentElt = (Element)currentElt.appendChild(doc.createElement(tag));
    }
    
    private void closeContext (String tag) 
    {
    	if (currentElt == root) throw new IllegalStateException(
    			"Cannot close context; current element is the document root");
    	if (tag != null && !tag.equals(currentElt.getNodeName())) throw new IllegalStateException
    		("Cannot close context; current element `" + currentElt.getNodeName() +	"' does not match expected tag name `" + tag + "'");
    	currentElt = (Element)currentElt.getParentNode();
    }
    
    private Element openText () 
    {
    	if (textEltParent == null) 
    	{
    		textEltParent = currentElt;
    		return newContext(ELT_TEXT);
    	}
    	else 
    	{
    		return currentElt;
    	}
    }   
    
    private void closeText () 
    {
    	if (textEltParent == null) return;
    	while (currentElt != textEltParent)
    	{
    		closeContext(null);
    	}
    	
    	textEltParent = null;
    	isBold = isUnderlined = isStruckThrough = isItalic = false;
    }    
  
    private void normalizeStyleElts (boolean bold, boolean italic, boolean underline, boolean struckThrough) 
    {
        if (isBold && !bold) closeThroughTextContext(ELT_BOLD);
        if (isItalic && !italic) closeThroughTextContext(ELT_ITALIC);
        if (isUnderlined && !underline) closeThroughTextContext(ELT_UNDERLINED);
        if (isStruckThrough && !struckThrough) closeThroughTextContext(ELT_STRUCKTHROUGH);
        
        if (bold && (!isBold || !isStyleContextCurrent(ELT_BOLD))) newContext(ELT_BOLD);
        if (underline && (!isUnderlined || !isStyleContextCurrent(ELT_UNDERLINED))) newContext(ELT_UNDERLINED);
        if (struckThrough && (!isStruckThrough || !isStyleContextCurrent(ELT_STRUCKTHROUGH))) newContext(ELT_STRUCKTHROUGH);
        if (italic && (!isItalic || !isStyleContextCurrent(ELT_ITALIC))) newContext(ELT_ITALIC);
        
        isItalic = italic;
        isBold = bold;
        isUnderlined = underline;
        isStruckThrough = struckThrough;
    }
    
    private void closeThroughTextContext (String tag) 
    {
        while (true) 
        {
            if (currentElt == root) throw new IllegalStateException("Cannot close context; current element is the document root");
            if (currentElt.getNodeName().equals(ELT_TEXT)) return;
            String nodeName = currentElt.getNodeName();
            currentElt = (Element)currentElt.getParentNode();
            if (nodeName.equals(tag)) return;
        }
    }
    

    private boolean isStyleContextCurrent (String tag) 
    {
        Element elt = currentElt;
        while (true) 
        {
            if (elt == root || elt.getNodeName().equals(ELT_TEXT)) return false;
            if (elt.getNodeName().equals(tag)) return true;
            elt = (Element)elt.getParentNode();
        }
    }   
    
    @SuppressWarnings("deprecation")
	private static void writeCoordsAsAttrs (Region r, Element context) 
    {
    	context.setAttribute("xpos", Float.toString(r.xpos()));
    	context.setAttribute("ypos", Float.toString(r.ypos()));
    	context.setAttribute("width", Float.toString(r.width()));
    	context.setAttribute("height", Float.toString(r.height()));
    }
    
    private String getStringOf (String s, int cnt) 
    {
    	whitespace.delete(0, whitespace.length());
    	for (int i = 0; i < cnt; i++) whitespace.append(s);
    	return whitespace.toString();
    }
    
    protected String getXMLAsString () throws IOException 
    {
    	StringWriter s = new StringWriter();
    	try 
    	{
    		serializeXMLDocument(doc, s);
		} 
    	catch (TransformerException e) 
    	{
            throw new IOException("Error occurred serializing extracted PDF form data to XML: " + e.getMessage());
		}
    	return s.toString();
    	
    }  
    
    private void serializeXMLDocument (Document doc, Writer output) throws TransformerException 
    {
        // serialize XML document using identity transformation
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        trans.transform(source, result);
    }  
    
     
    
   
    public static void main (String[] args) throws Exception 
    {
    		String filename="C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\StarrInsure_ISSUANCE_NOV-3-2017-17-4-3.pdf";
            File src = new File(filename);
            if (!src.exists()) System.out.println("No such file: " + filename);
            if (!src.canRead()) System.out.println("Cannot read file (check permissions): " + filename);
            xmlfile=filename + ".xml";
            File destFile = new File(filename + ".xml");
            com.snowtide.pdf.Document stream = PDF.open(src);
            
            PDFtoXMLConvertion tgt = new PDFtoXMLConvertion();
            stream.pipe(tgt);
            
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
            writer.write(tgt.getXMLAsString());
            writer.flush();
            writer.close();
            stream.close();
            System.out.println("Wrote text extracted from " + filename + " to " + destFile.getAbsolutePath() +" as UTF-8 encoded XML.");
        
    }
  
    
}
