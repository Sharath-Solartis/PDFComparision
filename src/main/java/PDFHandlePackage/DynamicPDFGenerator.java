package PDFHandlePackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import PDF.PDFComparision;
import PDFHandleException.DatabaseException;
import PDFHandleException.HTTPHandleException;

public class DynamicPDFGenerator 
{
	protected static String Pdf;
	protected static String formTemplete_tmpString;
	protected static boolean forms_flag = true;
	protected static boolean Scheduleforms_flag = true;
	protected static String ScheduleOfFormsList_tmpString;
	protected static DynamicXMLGenrator Source = null;
	protected static DatabaseOperation db_obj = null;
	protected static ConditionsChecking cond_check = null;
	protected static HttpHandle http = null;
	protected static String inputfilepath = "C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\pdfservicesolartisnet_1510893129634_83_request.xml";
	private static String expectedPdfPath;
	private static String actualPdfPath;
	private static String resultPdfPath;
	private static String actualPdfURL;
	private static PDFComparision pdfcompare;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, DatabaseException, HTTPHandleException 
	{
		Source = new DynamicXMLGenrator();
		db_obj = new DatabaseOperation();
		cond_check = new ConditionsChecking();
		db_obj.ConnectionSetup("com.mysql.jdbc.Driver", "jdbc:mysql://192.168.35.2:3391/Aa", "root", "password");
		pdfcompare = new PDFComparision();
		
		LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable = db_obj.GetDataObjects("SELECT * FROM List_Forms_Condition");
		LinkedHashMap<Integer, LinkedHashMap<String, String>> InputTable = db_obj.GetDataObjects("SELECT * FROM STARR_BOP_Quote_Policy_Endrosement_Cancel_INPUT t1 JOIN OUTPUT_ISO_Quote t2 JOIN OUTPUT_ISO_PolicyIssuance t3 ON t1.`S.No` = t2.`S.No` AND t2.`S.No` = t3.`S.No`");
		LinkedHashMap<Integer, LinkedHashMap<String, String>> XMlSource_tag_table = db_obj.GetDataObjects("SELECT * FROM XML_Source_Table");
		
		/*for (Map.Entry<Integer, LinkedHashMap<String, String>> inputRow : InputTable.entrySet() ) 
		{
			LinkedHashMap<String, String> inputRow_hashMap = inputRow.getValue(); //TO GET A ROW OF TESTDATA
			//System.out.println(inputRow_hashMap);
			LinkedHashMap<String, LinkedHashMap<String, String>> FormsList = ListOfForms(inputRow_hashMap,ConditionTable);//IT RETURN LIST FORM TO ATTACH
			String XMl_Request_with_listof_forms = AddingForm_Request(FormsList); //IT WILL RETURN A STRING WITH LIST OF FORMS AND SCHEDULE FORMS 
			//System.out.println(XMl_Request_with_listof_forms);
			LinkedHashMap<String, String> Dynamic_XMLSource = prepareHashMap_DynamicXMLSource(inputRow_hashMap,XMlSource_tag_table); // IT WILL RETURN HASHMAP WITH XMLSOURCE TAG AND RESPECTIVELY VALUES
			String XMl_Request_with_XMlSource= XMlSourceGenerator(XMl_Request_with_listof_forms, Dynamic_XMLSource);// IT RETURN DYNAMIC PDF REQUEST
			//System.out.println(XMl_Request_with_XMlSource);
			StringBuffer buf_to_change_XMLSource = new StringBuffer(XMl_Request_with_listof_forms);
			int TOChange_XmlSource_start = buf_to_change_XMLSource.indexOf("<XmlSource>"); 
			int TOChange_PDF_end = buf_to_change_XMLSource.indexOf("</XmlSource>");
			buf_to_change_XMLSource.replace(TOChange_XmlSource_start+11, TOChange_PDF_end, XMl_Request_with_XMlSource);
			//System.out.println(buf_to_change_XMLSource.toString());
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\Output.xml")));
		    bwr.write(buf_to_change_XMLSource.toString());
		    bwr.flush();
		    bwr.close();
			HTTPRequester(buf_to_change_XMLSource.toString());
		}*/
		DynamicPDFGenerator1(ConditionTable,InputTable,XMlSource_tag_table,inputfilepath);
		
		
	}
	public static void DynamicPDFGenerator1(LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable, LinkedHashMap<Integer, LinkedHashMap<String, String>> InputTable, LinkedHashMap<Integer, LinkedHashMap<String, String>> XMlSource_tag_table, String inputFilepath ) throws DatabaseException, IOException, HTTPHandleException
	{
		for (Map.Entry<Integer, LinkedHashMap<String, String>> inputRow : InputTable.entrySet() ) 
		{
			LinkedHashMap<String, String> inputRow_hashMap = inputRow.getValue(); //TO GET A ROW OF TESTDATA
			LinkedHashMap<String, LinkedHashMap<String, String>> FormsList = ListOfForms(inputRow_hashMap,ConditionTable);//IT RETURN LIST FORM TO ATTACH
			String XMl_Request_with_listof_forms = AddingForm_Request(FormsList, inputFilepath); //IT WILL RETURN A STRING WITH LIST OF FORMS AND SCHEDULE FORMS 
			LinkedHashMap<String, String> Dynamic_XMLSource = prepareHashMap_DynamicXMLSource(inputRow_hashMap,XMlSource_tag_table); // IT WILL RETURN HASHMAP WITH XMLSOURCE TAG AND RESPECTIVELY VALUES
			String XMl_Request_with_XMlSource= XMlSourceGenerator(XMl_Request_with_listof_forms, Dynamic_XMLSource);// IT RETURN DYNAMIC PDF REQUEST
			StringBuffer buf_to_change_XMLSource = new StringBuffer(XMl_Request_with_listof_forms);
			int TOChange_XmlSource_start = buf_to_change_XMLSource.indexOf("<XmlSource>"); 
			int TOChange_PDF_end = buf_to_change_XMLSource.indexOf("</XmlSource>");
			buf_to_change_XMLSource.replace(TOChange_XmlSource_start+11, TOChange_PDF_end, XMl_Request_with_XMlSource);
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File("C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\Output.xml")));
		    bwr.write(buf_to_change_XMLSource.toString());
		    bwr.flush();
		    bwr.close();
			String result = HTTPRequester(buf_to_change_XMLSource.toString());
			int start = result.indexOf("<PdfFileName>");
			int end = result.indexOf("</PdfFileName>");
			expectedPdfPath="A:\\" + result.substring(start+13, end);
			System.out.println("A:\\" + result.substring(start+13, end));
			actualPdfURL=inputRow_hashMap.get("Issurance_PDF");
			actualPdfPath="B:/ActualPDF"+inputRow_hashMap.get("TestData");
			resultPdfPath="B:/ResultPDF"+inputRow_hashMap.get("TestData");
			pdfcompare.comparePDFVisually(expectedPdfPath, actualPdfURL, actualPdfPath,resultPdfPath);
		}
	}
	
	private static LinkedHashMap<String, LinkedHashMap<String, String>> ListOfForms(LinkedHashMap<String, String> inputRow_hashMap,LinkedHashMap<Integer, LinkedHashMap<String, String>> ConditionTable) throws DatabaseException
    {
		LinkedHashMap<String, LinkedHashMap<String, String>> ListOfForms = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		for (Map.Entry<Integer, LinkedHashMap<String, String>> Conditionchecking : ConditionTable.entrySet() ) 
		{
			LinkedHashMap<String, String> value = Conditionchecking.getValue();
			boolean flag = cond_check.ConditionReading(value.get("Condition").trim(), inputRow_hashMap);
			ValidateForms_Insert_List(ListOfForms,value.get("Form_Name"),value.get("Schedule_Form_Name"),value.get("Schedule_Form_Number"),flag);
		}
	    return ListOfForms;
    }
	
	private static void ValidateForms_Insert_List(LinkedHashMap<String, LinkedHashMap<String, String>> FormsList,String form_name,String Schedule_Form_Name, String Schedule_Form_Number,Boolean form_applicable)
    {
         if(form_applicable.equals(true))
          {
        	  LinkedHashMap<String, String> Schedule_Form = new LinkedHashMap<String, String>();
        	  Schedule_Form.put(Schedule_Form_Name, Schedule_Form_Number);
        	  FormsList.put(form_name, Schedule_Form);
          } 
    }
	
	@SuppressWarnings("static-access")
	private static String AddingForm_Request(LinkedHashMap<String, LinkedHashMap<String, String>> FormsList, String inputpath) throws IOException
	{
		for (Map.Entry<String, LinkedHashMap<String, String>> Forms : FormsList.entrySet() ) 
		{
		    String FormsNameTo_Attach_key =  EscapeChar_XSLT(Forms.getKey());	   
		    String FormsNameTo_Attach = "/opt/SharedData/Shared/Configuration/"+FormsNameTo_Attach_key;
			LinkedHashMap<String, String> ScheduleOfForms = Forms.getValue();
			for (Entry<String, String> ScheduleOfForms_name_num : ScheduleOfForms.entrySet() ) 
			{
				String ScheduleOfForms_Name = EscapeChar_XSLT_XMLSource(ScheduleOfForms_name_num.getKey());
				String ScheduleOfForms_Number = EscapeChar_XSLT_XMLSource(ScheduleOfForms_name_num.getValue());	 
				
				if(forms_flag == true && !FormsNameTo_Attach_key.isEmpty())
				{
					formTemplete_tmpString ="\n\t    <FormTemplate>\n\t" + "\t    <FormLocation>"+FormsNameTo_Attach+"</FormLocation>\n" + "\t    </FormTemplate>";
					forms_flag = false;
				}
				else if(!FormsNameTo_Attach_key.isEmpty())
				{
					formTemplete_tmpString = formTemplete_tmpString + "\n\t    <FormTemplate>\n\t" + "\t    <FormLocation>"+FormsNameTo_Attach+"</FormLocation>\n" + "\t    </FormTemplate>";
				}
				if(Scheduleforms_flag == true && !ScheduleOfForms_Number.isEmpty() && !ScheduleOfForms_Name.isEmpty())
				{
					ScheduleOfFormsList_tmpString ="&lt;ISO_BOP_ScheduleOfFormsList&gt;&lt;ISO_BOP_Form_Number&gt;"+ ScheduleOfForms_Number +"&lt;/ISO_BOP_Form_Number&gt;&lt;ISO_BOP_Form_Name&gt;"+ScheduleOfForms_Name+"&lt;/ISO_BOP_Form_Name&gt;&lt;/ISO_BOP_ScheduleOfFormsList&gt;";
					Scheduleforms_flag = false;
				}
				else if(!ScheduleOfForms_Number.isEmpty() && !ScheduleOfForms_Name.isEmpty())
				{
					ScheduleOfFormsList_tmpString = ScheduleOfFormsList_tmpString + "&lt;ISO_BOP_ScheduleOfFormsList&gt;&lt;ISO_BOP_Form_Number&gt;"+ ScheduleOfForms_Number +"&lt;/ISO_BOP_Form_Number&gt;&lt;ISO_BOP_Form_Name&gt;"+ScheduleOfForms_Name+"&lt;/ISO_BOP_Form_Name&gt;&lt;/ISO_BOP_ScheduleOfFormsList&gt;";
				}
			}
		}
		
		Pdf = Source.readFile(inputpath);
		StringBuffer buf = new StringBuffer(Pdf);
		int PDF_start = buf.indexOf("<FormTemplates>"); 
		int PDF_end = buf.indexOf("</FormTemplates>");
		buf.replace(PDF_start+15, PDF_end, formTemplete_tmpString);
		String value = buf.toString();
		
		buf.delete(0, buf.length());
		
		buf = new StringBuffer(value);
		int ScheduleOfFormsList_start = buf.indexOf("&lt;PDF&gt;");
		buf.replace(ScheduleOfFormsList_start+11,ScheduleOfFormsList_start+11, ScheduleOfFormsList_tmpString);
		
		String TemplateString = buf.toString();
		//System.out.println("------------------");
		//System.out.println(TemplateString);
		return TemplateString;
	}
	
	private static LinkedHashMap<String, String> prepareHashMap_DynamicXMLSource(LinkedHashMap<String, String> Input_Rs,LinkedHashMap<Integer, LinkedHashMap<String, String>> XMLSource_tag)
	{
		    LinkedHashMap<String,String> DynamicXMLSource =new LinkedHashMap<String,String>();  
			for (Map.Entry<Integer, LinkedHashMap<String, String>> XMlSource_Tag_hashMap : XMLSource_tag.entrySet() ) 
			{			
				LinkedHashMap<String,String> XMLSource = XMlSource_Tag_hashMap.getValue(); 

			    String value = EscapeChar_XSLT_XMLSource(Input_Rs.get(XMLSource.get("Respective_InputCol_Name")));
			    DynamicXMLSource.put((String) XMLSource.get("XML_Source_Tag"), value);
			 }
			return DynamicXMLSource;
	}
	
	@SuppressWarnings("unused")
	private static String XMlSourceGenerator(String xml,LinkedHashMap<String, String> dynamic_XMLSource)
    {	
		StringBuffer buf = new StringBuffer(xml);
		int PDF_start = buf.indexOf("<XmlSource>"); 
		int PDF_end = buf.indexOf("</XmlSource>");
		//System.out.println(PDF_start);
	    //System.out.println(PDF_end);
		String XmlSource = buf.substring(PDF_start+11, PDF_end);
		XmlSource = XmlSource.replace("&lt;","<");
		XmlSource = XmlSource.replace("&gt;",">");
		//System.out.println(XmlSource);
		
		Document doc = Jsoup.parse(XmlSource, "", Parser.xmlParser());
		
			for (Entry<String, String> XMlSource_hashMap2 : dynamic_XMLSource.entrySet() ) 
			{
				String XmlSoruce_tag = XMlSource_hashMap2.getKey();
				String XmlSoruce_value = XMlSource_hashMap2.getValue();
				for (Element e : doc.select(XmlSoruce_tag)) 
				{
					String oldChar = e.ownText();
					if(!oldChar.isEmpty() && !XmlSoruce_value.isEmpty())
					{
					//System.out.println(oldChar);
					e.text(e.text().replace(e.ownText(), XmlSoruce_value));
					//System.out.println(e.ownText());
					}
					XmlSource = XmlSource.replace(oldChar, e.ownText());
					//System.out.println(XmlSource);
				}
			}
		StringBuffer rev_buf = new StringBuffer(XmlSource);
		int ReV_PDF_start = rev_buf.indexOf("<XmlSource>"); 
		int ReV_PDF_end = rev_buf.indexOf("</PDF>");
		String ReV_XmlSource = rev_buf.substring(0,rev_buf.length());
		ReV_XmlSource = ReV_XmlSource.replace("<", "&lt;");
		ReV_XmlSource = ReV_XmlSource.replace(">","&gt;");
	    //System.out.println(ReV_XmlSource.replace("15", "1"));
		return ReV_XmlSource.replace("15", "1");
    }
	
	private static String HTTPRequester(String PDF) throws HTTPHandleException
	{
		http = new HttpHandle("https://qapdfservice.solartis.net/PDFGenerationService-3.0/PDFGenerationService/3_0/generatePDF","POST");
		http.AddHeader("Content-Type", "application/xml");
		http.SendData(PDF);
		String response_string = http.ReceiveData();
		//System.out.println(response_string);
		return response_string;
	}
	
	@SuppressWarnings("unused")
	private static String readFile(String file) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try 
		{
			while ((line = reader.readLine()) != null) 
			{
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			return stringBuilder.toString();
		} 
		finally 
		{
			reader.close();
		
		}
	}
	
	private static String EscapeChar_XSLT(String value)
	{
		if(value.contains("&'"))
		{
			value = value.replaceAll("&'", "&amp;&apos;");
		}
		else if(value.contains("&"))
	    {
			value = value.replaceAll("&", "&amp;");
	    }
		else if(value.contains("'"))
		{
			value = value.replaceAll("'", "&apos;");
		}
		return value;
	}
	
	private static String EscapeChar_XSLT_XMLSource(String value)
	{
		if(value.contains("&"))
		{
			value = value.replaceAll("&", "&amp;amp;");
		}
		return value;
	}
}
