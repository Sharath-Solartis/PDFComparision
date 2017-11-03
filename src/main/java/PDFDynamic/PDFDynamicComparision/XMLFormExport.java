package PDFDynamic.PDFDynamicComparision;

import com.snowtide.PDF;
import com.snowtide.pdf.forms.*;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("rawtypes")
public class XMLFormExport 
{   
    private XMLFormExport () {}
    
	public static Document exportFormAsXML (com.snowtide.pdf.Document source) throws IOException 
    {
        Document doc;
        try 
        {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } 
        catch (Exception e) 
        {
            throw new IOException("Configuration error encountered while initializing XML facilities: " + e.getMessage());
        }
        
        Element root = doc.createElement("form");
        root.setAttribute("sourcefile", source.getPDFFile().getAbsolutePath());
        doc.appendChild(root);
        AcroForm form = (AcroForm)source.getFormData();
       
        if (form == null) 
        	return doc;
                
        for (Iterator iter = form.iterator(); iter.hasNext(); ) 
        {
            exportField((AcroFormField)iter.next(), doc, root);            
        }
        
        return doc;
    }
    
    private static void exportField (AcroFormField field, Document doc, Element docroot) 
    {
        Element felt = doc.createElement("field");
        docroot.appendChild(felt);
        felt.setAttribute("localname", field.getLocalName());
        felt.setAttribute("fullname", field.getFullName());
        felt.setAttribute("type", String.valueOf(field.getType()));
        if (field.getMappingName() != null) felt.setAttribute("mappingname", field.getMappingName());        
        if (field instanceof AcroTextField) 
        {
            AcroTextField tf = (AcroTextField)field;
            if (tf.hasRichTextValue()) 
            {
                Element richvalue = doc.createElement("value-richtext");                
                felt.appendChild(richvalue);
                felt.appendChild(doc.createTextNode(tf.getRichTextValue()));
            }
        } 
        else if (field instanceof AcroChoiceField) 
        {
            AcroChoiceField ch = (AcroChoiceField)field;
            addOptionsElt(ch.getOptions(), doc, felt);
        } 
        else if (field instanceof AcroButtonField) 
        {
            AcroButtonField bt = (AcroButtonField)field;
            
            Element etype = doc.createElement("button-type");
            felt.appendChild(etype);
            
            int type = bt.getButtonType();
            if (type == AcroButtonField.BUTTON_TYPE_CHECKBOX) 
            {
                etype.appendChild(doc.createTextNode("check"));
            } 
            else if (type == AcroButtonField.BUTTON_TYPE_PUSHBUTTON)
            {
                etype.appendChild(doc.createTextNode("push"));
            }
            else if (type == AcroButtonField.BUTTON_TYPE_RADIO_GROUP)
            {
                etype.appendChild(doc.createTextNode("radio"));
            }
            
            List expvals = bt.getExportValues();
            if (expvals != null && expvals.size() > 0) 
            {
                Element eexpvals = doc.createElement("exp-values");
                felt.appendChild(eexpvals);
                
                for (Iterator iter = expvals.iterator(); iter.hasNext(); ) 
                {
                    Element eexp = doc.createElement("exp-value");
                    eexpvals.appendChild(eexp);
                    eexp.appendChild(doc.createTextNode(String.valueOf(iter.next())));
                }
            }
            
            String expval = bt.getExportValue();
            if (expval != null) 
            {
                Element eval = doc.createElement("exp-value");
                felt.appendChild(eval);
                eval.appendChild(doc.createTextNode(expval));
            }
        }
        else if (field instanceof AcroSignatureField) 
        {
            felt.appendChild(doc.createComment("Signature field data is not exported by pdfts.examples.XMLFormExport"));
            return;
        } 
        else 
        {
            // leave the door open for future form field types
        }
        
        Object value = field.getValue();
        if (value != null) 
        {
            // properly handle possibly multiple values, i.e. AcroChoiceFields when multiple selections are possible
            if (value instanceof String) 
            {
                addValueElt((String)value, doc, felt);
            } 
            else if (value instanceof Object[]) 
            {
                Object[] varr = (Object[])value;
                for (int i = 0, len = varr.length; i < len; i++) 
                {
                    addValueElt(String.valueOf(varr[i]), doc, felt);
                }
            }
        }
    }
    
    private static void addValueElt (String value, Document doc, Element fieldElt) 
    {
        Element eval = doc.createElement("value");
        fieldElt.appendChild(eval);
        eval.appendChild(doc.createTextNode(value));
    }
    
    /**
     * Outputs an options element to represent the available options provided by an AcroChoiceField.
     */
    private static void addOptionsElt (Map options, Document doc, Element fieldElt) 
    {
        Element opts = doc.createElement("options");
        fieldElt.appendChild(opts);
        
        for (Iterator iter = options.entrySet().iterator(); iter.hasNext(); ) 
        {
            Entry opt = (Entry)iter.next();
            Element eopt = doc.createElement("option");
            opts.appendChild(eopt);
            
            Element eexp = doc.createElement("exp-value");
            eopt.appendChild(eexp);
            eexp.appendChild(doc.createTextNode(String.valueOf(opt.getKey())));
            
            Element edis = doc.createElement("disp-value");
            eopt.appendChild(edis);
            edis.appendChild(doc.createTextNode(String.valueOf(opt.getValue())));
        }
    }
   
    public static void main (String[] args)
    {
        File pdf;
        Writer xml;
        
        if (args.length >= 2) 
        {
            pdf = new File(args[0]);
            checkPDFFileExists(pdf);
            
            try 
            {
                xml = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[1]))));
            } 
            catch (FileNotFoundException e)
            {
                System.err.println("Could not create output xml file at [" + args[1] + ']');
                System.exit(1);
                return;
            }
        } 
        else if (args.length == 1)
        {
            pdf = new File(args[0]);
            checkPDFFileExists(pdf);
            
            xml = new BufferedWriter(new OutputStreamWriter(System.out));
        }
        else
        {
            System.err.println("Usage: java pdfts.examples.XMLFormExport pdf_file_path [output_xml_path]");
            System.exit(1);
            return;
        }
        
        com.snowtide.pdf.Document stream = null;
        try 
        {
            stream = PDF.open(pdf);
            Document doc = exportFormAsXML(stream);
            serializeXMLDocument(doc, xml);
        } 
        catch (IOException e)
        {
            System.err.println("Error occurred while exporting PDF form data from [" + pdf.getAbsolutePath() + ']');
            e.printStackTrace(System.err);
        } 
        catch (TransformerException e) 
        {
            System.err.println("Error occurred serializing extracted PDF form data to XML");
            e.printStackTrace(System.err);
        } 
        finally 
        {
            try 
            {
                if (stream != null) stream.close();
                xml.close();
            } 
            catch (IOException e) 
            {
                System.err.println("Error closing PDFxStream Document instance or XML writer");
                e.printStackTrace(System.err);
            }
        }
        
    }
    
    public static void serializeXMLDocument (Document doc, Writer output) throws TransformerException
    {
        // serialize XML document using identity transformation
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        trans.transform(source, result);
    }
    
    private static void checkPDFFileExists (File pdf) 
    {
        if (!pdf.exists()) 
        {
            System.out.println("PDF file path given does not exist [" + pdf.getAbsolutePath() + ']');
            System.exit(1);
        } 
        else if (!pdf.canRead()) 
        {
            System.out.println("PDF file path given cannot be read -- check permissions [" + pdf.getAbsolutePath() + ']');
            System.exit(1);            
        }
    }

}

