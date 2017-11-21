package PDF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.ComparisonControllers;


import org.xmlunit.diff.Difference;

import PDFHandlePackage.PDFtoXMLConvertion;

import com.snowtide.PDF;
public class XMLComparision
{
	@SuppressWarnings("rawtypes")
	public List alldifferences;

		
	public void urltopdf(String URL,String path,String filename) throws IOException
	{
		URL website = new URL(URL);
		Path targetPath = new File(path + File.separator + filename+".pdf").toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	protected String pdftoxml(String filepath) throws IOException
	{
		String xmlfile="";
		int pagenumber=0;
		File src = new File(filepath);
		PDFtoXMLConvertion tgt = new PDFtoXMLConvertion(pagenumber);
		com.snowtide.pdf.Document stream = PDF.open(src);
		stream.pipe(tgt);
		xmlfile=xmlfile+tgt.getXMLAsString();
		stream.close();	
		pagenumber=tgt.pagenumber()+1;
		File destFile = new File(filepath+".xml");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
        writer.write(xmlfile);
        writer.flush();
        writer.close();
        return xmlfile;

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList compareXML(String xml1, String xml2) throws SAXException, IOException
	{		
		alldifferences = new ArrayList();
		org.xmlunit.diff.Diff myDiff = DiffBuilder.compare(xml1).withTest(xml2).withComparisonController(ComparisonControllers.Default).build();
	    Iterator<Difference> iter = myDiff.getDifferences().iterator();
	    int size = 0;
	    while (iter.hasNext()) 
	    {
	    	alldifferences.add(iter.next().toString());
	        System.out.println(iter.next().toString()+"\n");
	        size++;
	    }
	    System.out.println(size);
		return (ArrayList) alldifferences;
	}
	
	public void compareDoc(Document doc1, Document doc2) throws SAXException, IOException
	{
		String xml1=doc1.asXML().toString();
		String xml2=doc1.asXML().toString();
		compareXML(xml1,xml2);
	}
	
	public void comparePDF(String pdfpath1,String pdfpath2) throws SAXException, IOException
	{
		compareXML(pdftoxml(pdfpath1),pdftoxml(pdfpath2));
	}
	
	public void comparePDF(String expectedURL, String actualURL, String path, String filename)
	{
		
	}
	
	
	
	
	
	public static void main(String args[]) throws SAXException, IOException
	{
		XMLComparision comp = new XMLComparision();
		String pdfpath1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0017NH.pdf";
		String pdfpath2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0114OW.pdf";
		//comp.comparePDF(pdfpath1,pdfpath2);
		//comp.
		//comp.urltopdf("D:/ftl/");
		
	}

	
} 
