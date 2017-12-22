package PDF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import PDFHandleException.DatabaseException;
import PDFHandleException.HTTPHandleException;
import PDFHandlePackage.DatabaseOperation;
import PDFHandlePackage.DynamicPDFGenerator;
import PDFHandlePackage.PDFComparision;
import PDFHandlePackage.MailService;
import PDFHandlePackage.PackageHandle;;

public class DynamicPDFComparision 
{	
	public static DatabaseOperation Forms_db_obj = null;
	public static DatabaseOperation XMLSource_db_obj = null;
	public static DatabaseOperation Input_db_obj = null;
	public static DatabaseOperation Output_db_obj = null;
	public static DynamicPDFGenerator DynamicPDF;
	public static PDFComparision pdfcompare;
	public static MailService Mailing;
	public static PackageHandle Archiving;
	private static String expectedPdfPath;
	private static String actualPdfPath;
	private static String resultPdfPath;
	private static String actualPdfURL;
	public static String inputfilepath = "C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\pdfservicesolartisnet_1510893129634_83_request.xml";

	@SuppressWarnings({ "all" })
	public static void main(String args[]) throws DatabaseException, IOException, HTTPHandleException, InterruptedException
	{
		DatabaseOperation.ConnectionSetup(System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"));
		
		Forms_db_obj = new DatabaseOperation();
		XMLSource_db_obj = new DatabaseOperation();
		Input_db_obj = new DatabaseOperation();
		Output_db_obj = new DatabaseOperation();
		
		pdfcompare = new PDFComparision();
		DynamicPDF = new DynamicPDFGenerator();
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable = Forms_db_obj.GetDataObjects("SELECT * FROM " + System.getProperty("FormsConditionalTable"));
		LinkedHashMap<Integer, LinkedHashMap<String, String>> XMlSource_tag_table = XMLSource_db_obj.GetDataObjects("SELECT * FROM "+ System.getProperty("XmlSourceTable"));
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> InputTable = Input_db_obj.GetDataObjects("SELECT * FROM "+ System.getProperty("InputTable") +" t1 JOIN "+ System.getProperty("OutputQuoteTable") +" t2 JOIN "+ System.getProperty("OutputPolicyTable") +" t3 ON t1.`S.No` = t2.`S.No` AND t2.`S.No` = t3.`S.No`");
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> inputtableiterator = InputTable.entrySet().iterator();
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> OutputTable = Output_db_obj.GetDataObjects("SELECT * FROM " + System.getProperty("OutputTable"));
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> outputtableiterator = OutputTable.entrySet().iterator();
		
		 Path Result = Paths.get(System.getProperty("ResultPDFPath"));
         Path ResultZIP = Paths.get(System.getProperty("ResultPDFPath")+".zip");
 
		while (inputtableiterator.hasNext() && outputtableiterator.hasNext())
		{
			Entry<Integer, LinkedHashMap<String, String>> Input = inputtableiterator.next();
			Entry<Integer, LinkedHashMap<String, String>> Output = outputtableiterator.next();
			
			System.out.println(Input.getValue().get("Testdata") + " is Running");
			LinkedHashMap<String, String> inputrow = Input.getValue();
			expectedPdfPath = DynamicPDF.PDFGenerator(ConditionTable, Input, XMlSource_tag_table, System.getProperty("SamplePDFRequest"), System.getProperty("TransactionType"));
			expectedPdfPath = expectedPdfPath.replace("\\", "/");
			System.out.println(expectedPdfPath);
			
			actualPdfURL=Input.getValue().get("Issurance_PDF");
			actualPdfPath= System.getProperty("ActualPDFPath")+"/"+ Input.getValue().get("Testdata");
			resultPdfPath= System.getProperty("ResultPDFPath")+"/" + Input.getValue().get("Testdata");
            System.out.println(Input.getValue().get("Testdata") + " is Starts Comparing"); 
            
            
            File source = new File(expectedPdfPath);
            File dest = new File("E:/Jmeter-server/STARR_BOP-PaaS/Request_Response/ExpectedPDF/");
            try 
            {
            	System.out.println(source);
                FileUtils.copyFileToDirectory(source, dest);
            } 
            catch (IOException e) 
            {
            	System.out.println("Error in copying Expected");
                e.printStackTrace();
            }
            
		    String status=	pdfcompare.comparePDFVisually(expectedPdfPath, actualPdfURL, actualPdfPath,resultPdfPath);
		    inputrow.put("Status", status);
		    Output_db_obj.UpdateRow(Output.getKey(), inputrow);
		    System.out.println("Comparison Completed for "+Input.getValue().get("Testdata"));
		}
		 Archiving.pack(Result, ResultZIP);
		 System.out.print("Archiving the Folder : "+ Result);
         Mailing.sendMail(System.getProperty("From"), System.getProperty("To"), System.getProperty("Username"), System.getProperty("Password"), System.getProperty("Subject"), System.getProperty("Body"), System.getProperty("ResultPDFPath")+".zip");
         System.out.print("Sending Result as Mail to : "+ System.getProperty("To"));
	}
	
}
