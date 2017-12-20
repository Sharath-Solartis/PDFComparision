package PDF;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import PDFHandleException.DatabaseException;
import PDFHandleException.HTTPHandleException;
import PDFHandlePackage.DatabaseOperation;
import PDFHandlePackage.DynamicPDFGenerator;
import PDFHandlePackage.PDFComparision;

public class DynamicPDFComparision 
{	
	public static DatabaseOperation db_obj = null;
	public static DynamicPDFGenerator DynamicPDF;
	public static PDFComparision pdfcompare;
	private static String expectedPdfPath;
	private static String actualPdfPath;
	private static String resultPdfPath;
	private static String actualPdfURL;
	public static String inputfilepath = "C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\pdfservicesolartisnet_1510893129634_83_request.xml";

	@SuppressWarnings("static-access")
	public static void main(String args[]) throws DatabaseException, IOException, HTTPHandleException
	{
		db_obj = new DatabaseOperation();
		db_obj.ConnectionSetup(System.getProperty("JDBC_DRIVER"), System.getProperty("DB_URL"), System.getProperty("USER"), System.getProperty("password"));
		pdfcompare = new PDFComparision();
		DynamicPDF = new DynamicPDFGenerator();
		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable = db_obj.GetDataObjects("SELECT * FROM " + System.getProperty("FormsConditionalTable"));
		LinkedHashMap<Integer, LinkedHashMap<String, String>> XMlSource_tag_table = db_obj.GetDataObjects("SELECT * FROM "+ System.getProperty("XmlSourceTable"));
		LinkedHashMap<Integer, LinkedHashMap<String, String>> InputTable = db_obj.GetDataObjects("SELECT * FROM "+ System.getProperty("InputTable") +" t1 JOIN "+ System.getProperty("OutputQuoteTable") +" t2 JOIN "+ System.getProperty("OutputPolicyTable") +" t3 ON t1.`S.No` = t2.`S.No` AND t2.`S.No` = t3.`S.No`");
		
		for (Map.Entry<Integer, LinkedHashMap<String, String>> Input : InputTable.entrySet() ) 
		{
			System.out.println(Input.getValue().get("Testdata") + " is Running");
			LinkedHashMap<String, String> inputrow = Input.getValue();
			expectedPdfPath = DynamicPDF.PDFGenerator(ConditionTable, Input, XMlSource_tag_table, System.getProperty("SamplePDFRequest"), System.getProperty("TransactionType"));
			System.out.println(expectedPdfPath);
			
			/*actualPdfURL=Input.getValue().get("Issurance_PDF");
			actualPdfPath= System.getProperty("ActualPDFPath")+ Input.getValue().get("Testdata");
			resultPdfPath= System.getProperty("ResultPDFPath") + Input.getValue().get("Testdata");
            System.out.println(Input.getValue().get("Testdata") + " is Starts Comparing"); 
		    String status=	pdfcompare.comparePDFVisually(expectedPdfPath, actualPdfURL, actualPdfPath,resultPdfPath);
		    inputrow.put("Status", status);
		    db_obj.UpdateRow(Integer.parseInt(Input.getValue().get("S.No")), inputrow);
		    System.out.println("Comparison Completed for "+Input.getValue().get("Testdata"));*/
		}
	}
}
