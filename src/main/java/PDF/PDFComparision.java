package PDF;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.xml.sax.SAXException;

import de.redsix.pdfcompare.PdfComparator;

public class PDFComparision
{	
	public PDFComparision() 
	{
		
	}
	
	@SuppressWarnings("rawtypes")
	public void comparePDFVisually(String PDF1, String PDF2, String Resultpath) throws IOException
	{
		PdfComparator pdfcompare = new PdfComparator(PDF1, PDF2);
		pdfcompare.compare().writeTo(Resultpath);
		System.out.println(pdfcompare.getResult());
        boolean isEqual=new PdfComparator(PDF1, PDF2).compare().writeTo(Resultpath);
        if(!isEqual)
        {
            System.out.println("Difference found in PDFs");
        }
        else
        {
            System.out.println("Difference not found in PDFs");
        }
	}
	
	public void urltopdf(String URL,String path,String filename) throws IOException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path + File.separator + filename+".pdf").toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	public static void main(String args[]) throws SAXException, IOException
	{
		PDFComparision comp = new PDFComparision();
		String pdfpath1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0017NH.pdf";
		String pdfpath2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0114OW.pdf";
		comp.comparePDFVisually(pdfpath1,pdfpath2,"D:/ftl/");
		//comp.
		//comp.urltopdf("D:/ftl/");	
	
	}
} 
