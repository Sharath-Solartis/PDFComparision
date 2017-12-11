package PDF;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import de.redsix.pdfcompare.PdfComparator;

public class PDFComparison 
{
	public static ExcelOperationsJXL objectLoginScript=null;
	public void pdfcompare(String PDFpath1, String PDFpath2, String ResultPDFPath) throws IOException
	{
		
        boolean isEqual=new PdfComparator(PDFpath1, PDFpath2).compare().writeTo(ResultPDFPath);
        if(!isEqual)
        {
            System.out.println("Difference found in PDFs");
        }
        else
        {
            System.out.println("Difference not found in PDFs");
        }
        System.out.println("PDF ProcessCompleted");
	}
	
	public static void urltopdf(String URL,String path,String filename) throws IOException
	{
		URL website = new URL(URL);
		Path targetPath = new File(path + File.separator + filename+".pdf").toPath();
		InputStream in = website.openStream();		
		Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);		
	}
	
	/*public void pdfcompare1() throws IOException
	{
		String pdfpath1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 (0117).pdf";
		String pdfpath2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 FL (0117) Common Policy Dec Page.pdf";    //IL0114OW
		PDFUtil pdfUtil = new PDFUtil();
		// compares the pdf documents and returns a boolean
		// true if both files have same content. false otherwise.
		// Default is CompareMode.TEXT_MODE
		pdfUtil.setCompareMode(CompareMode.VISUAL_MODE);
		pdfUtil.compare(pdfpath1, pdfpath2);

		// compare the 3rd page alone
		//pdfUtil.compare(pdfpath1, pdfpath2, 3, 3);

		// compare the pages from 1 to 5
		//pdfUtil.compare(pdfpath1, pdfpath2, 1, 5);

		//if you need to store the result
		pdfUtil.highlightPdfDifference(true);
		pdfUtil.setImageDestinationPath("D:/ftl");
		pdfUtil.compare(pdfpath1, pdfpath2);
	}*/
	
	public static void main(String args[]) throws IOException, ClassNotFoundException, SQLException
	{
		//propertiesHandle configFile = new propertiesHandle("A:/1 Projects/20 CHIC_UI/Release1/Configuration/Config_C1128.properties");
		//databaseOperartions objectInput = new databaseOperartions();
		//databaseOperartions.conn_setup(configFile);
		//System.setProperty("jsse.enableSNIExtension", "false");	
		//objectInput.get_dataobjects(configFile.getProperty("pdfexcelfile"));
		//int i=1;
		
			for(int i=3;i<=58;i++)
			{
			//String QAPDFNAME=objectInput.read_data("pdf1");
			//String POSTPRODPDFNAME =objectInput.read_data("pdf2");
			//System.out.println(QAPDFNAME+POSTPRODPDFNAME);
			String QApath="A:\\1 Projects\\09 ISO\\PDFCompareResults\\qa\\TestData"+i+".pdf";
			String Mockpath="A:\\1 Projects\\09 ISO\\PDFCompareResults\\mock\\TestData"+i+".pdf";
			//urltopdf(QAPDFNAME,"A:/1 Projects/09 ISO/PDFCompareResults/qa/","TestData"+objectInput.read_data("S.No"));
			//urltopdf(QAPDFNAME,"A:/1 Projects/09 ISO/PDFCompareResults/mock/","TestData"+objectInput.read_data("S.No"));
			PDFComparison newcompare = new PDFComparison();
			System.out.println(QApath);
			System.out.println(Mockpath);
			newcompare.pdfcompare(QApath,Mockpath,"A:/1 Projects/09 ISO/PDFCompareResults/"+"TestDataCompareResult"+i);
			}
		//String file1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 (0117).pdf";
       // String file2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 FL (0117) Common Policy Dec Page.pdf";
        //String result="D:/ftl/resultPDF";
		//PDFComparison newcompare = new PDFComparison();
		//newcompare.pdfcompare(file1,file2,result);
		//newcompare.pdfcompare1();
	}
}
