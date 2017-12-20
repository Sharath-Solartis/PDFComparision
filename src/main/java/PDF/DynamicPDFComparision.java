package PDF;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import PDFHandleException.DatabaseException;
import PDFHandleException.HTTPHandleException;
import PDFHandlePackage.DatabaseOperation;
import PDFHandlePackage.DynamicPDFGenerator;
import PDFHandlePackage.PDFComparision;

public class DynamicPDFComparision 
{	
	public static DatabaseOperation Forms_db_obj = null;
	public static DatabaseOperation XMLSource_db_obj = null;
	public static DatabaseOperation Input_db_obj = null;
	public static DatabaseOperation Output_db_obj = null;
	public static DynamicPDFGenerator DynamicPDF;
	public static PDFComparision pdfcompare;
	private static String expectedPdfPath;
	private static String actualPdfPath;
	private static String resultPdfPath;
	private static String actualPdfURL;
	public static String inputfilepath = "C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\pdfservicesolartisnet_1510893129634_83_request.xml";

	@SuppressWarnings({ "static-access", "unused" })
	public static void main(String args[]) throws DatabaseException, IOException, HTTPHandleException
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
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> OutputTable = Output_db_obj.GetDataObjects("SELECT * FROM "+System.getProperty("InputTable"));
		Iterator<Entry<Integer, LinkedHashMap<String, String>>> outputtableiterator = OutputTable.entrySet().iterator();
		
		while (inputtableiterator.hasNext() && outputtableiterator.hasNext())
		{
			Entry<Integer, LinkedHashMap<String, String>> Input = inputtableiterator.next();
			Entry<Integer, LinkedHashMap<String, String>> Output = outputtableiterator.next();
			
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
		    Output_db_obj.UpdateRow(Output.getKey(), inputrow);
		    System.out.println("Comparison Completed for "+Input.getValue().get("Testdata"));*/
		}
	}
}
