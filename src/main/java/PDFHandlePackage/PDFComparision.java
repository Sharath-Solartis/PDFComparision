package PDFHandlePackage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;
import com.testautomationguru.utility.CompareMode;
import com.testautomationguru.utility.PDFUtil;

public class PDFComparision
{	
	public static boolean successful;
	public PDFComparision() 
	{
		
	}
	
	public String comparePDFVisually(String PDF1path, String PDF2URL,String PDF2pathwithpdfname, String Resultpath) throws IOException
	{
		String result = null;
		
		urltopdf(PDF2URL,PDF2pathwithpdfname);
        
		PDFUtil pdfUtil = new PDFUtil();
	    pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
	    pdfUtil.highlightPdfDifference(true);
	    
	    int Expected_PageCount=pdfUtil.getPageCount(PDF1path);    
	    int Autual_PageCount=pdfUtil.getPageCount(PDF2pathwithpdfname+".pdf"); 
	    
	    if(Expected_PageCount==Autual_PageCount)
	    {
	    	File dir = new File(Resultpath);
	            if (! dir.exists())
	            {
	                 successful = dir.mkdir();
	            }
	            else
	            {
	                System.out.println("Clearing all Past Datas from "+dir);
	                FileUtils.cleanDirectory(dir);
	                successful = true;
	            }
	           
	            if (successful)
	            {
	            	pdfUtil.setImageDestinationPath(Resultpath);
	    		    boolean isEqual=pdfUtil.compare(PDF1path, PDF2pathwithpdfname+".pdf", 1, Expected_PageCount, true, true);
	    		    if(!isEqual)
	    	        {
	    	            System.out.println("Difference found in PDFs");
	    	            result = "Fail";
	    	        }
	    	        else
	    	        {
	    	            System.out.println("Difference not found in PDFs");
	    	            result = "Pass";
	    	        }
	      }
	      else
	      {
	         System.out.println("Directory is not present");
	      } 
	        
	    }
	    else
	    {
	    	System.out.println("Count of PDF Pages are different");
            result = "PageCountError";
	    }
        return result;
	}
	
	public void urltopdf(String URL,String path) throws IOException
	{
		System.setProperty("jsse.enableSNIExtension", "false");	
		URL website = new URL(URL);
		Path targetPath = new File(path+".pdf").toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	public static void main(String args[]) throws SAXException, IOException
	{
		//PDFComparision comp = new PDFComparision();
		//String pdfpath1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0017NH.pdf";
		//String pdfpath2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/IL0114OW.pdf";
		//comp.comparePDFVisually(pdfpath1,pdfpath2,"D:/ftl/");
		//comp.
		//comp.urltopdf("D:/ftl/");	
	
	}
} 
