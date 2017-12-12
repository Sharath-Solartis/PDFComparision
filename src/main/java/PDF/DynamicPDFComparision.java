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
		db_obj.ConnectionSetup("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.35.2:3391/Aa", "root", "password");
		pdfcompare = new PDFComparision();
		DynamicPDF = new DynamicPDFGenerator();
		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable = db_obj.GetDataObjects("SELECT * FROM List_Forms_Condition");
		LinkedHashMap<Integer, LinkedHashMap<String, String>> InputTable = db_obj.GetDataObjects("SELECT * FROM STARR_BOP_Quote_Policy_Endrosement_Cancel_INPUT t1 JOIN OUTPUT_ISO_Quote t2 JOIN OUTPUT_ISO_PolicyIssuance t3 ON t1.`S.No` = t2.`S.No` AND t2.`S.No` = t3.`S.No`");
		LinkedHashMap<Integer, LinkedHashMap<String, String>> XMlSource_tag_table = db_obj.GetDataObjects("SELECT * FROM XML_Source_Table");
		
		for (Map.Entry<Integer, LinkedHashMap<String, String>> Input : InputTable.entrySet() ) 
		{
			System.out.println("TestData--1");
			
			expectedPdfPath = DynamicPDF.PDFGenerator(ConditionTable, Input, XMlSource_tag_table, inputfilepath);
					
			actualPdfURL=Input.getValue().get("Issurance_PDF");
			actualPdfPath="E:\\Jmeter-server\\STARR_BOP-PaaS\\Request_Response\\ActualPDF\\"+Input.getValue().get("Testdata");
			resultPdfPath="E:\\Jmeter-server\\STARR_BOP-PaaS\\Request_Response\\ResultPDF\\"+Input.getValue().get("Testdata");
			
			pdfcompare.comparePDFVisually(expectedPdfPath, actualPdfURL, actualPdfPath,resultPdfPath);
		}
	}
}
