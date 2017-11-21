package PDFHandlePackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PDFGenerator 
{
	int j = 0;
	int ifCount = 0;
	protected static String request = "";
    protected static FileWriter fw = null;
    protected static ArrayList<String> slctkeysArr = new ArrayList<String>();
    protected static ArrayList<String> otherkeysArr = new ArrayList<String>();
    protected static boolean flag = true;
    protected static boolean image_flag = true;	protected static BufferedReader reader;
	protected static String Pdf;
	protected static String SourceString;
	protected static HashMap<String, String> Hashmap;
	
	public static void GeneratePDF(String ModifiedXML, String OriginalXML, String DataFeededXML, HashMap<String, String> Hashmap) throws IOException 
	{
		String Value = null;
		List<String> ValueList = new ArrayList<String> ();
		reader = new BufferedReader(new FileReader(ModifiedXML));
		String line = reader.readLine();
		while (line != null)
		{	
			if(line.contains("<FormLocation>") || line.toString().contains("</FormLocation>")) 
			{
				int openbrace = line.indexOf(">"); 
				int closebrace = line.indexOf("</FormLocation>");
				Value = line.substring(openbrace+1, closebrace);
				ValueList.add(Value);
			}
			line = reader.readLine();
		}
		for(int i=0;i<ValueList.size();i++)
		{
		    System.out.println(ValueList.get(i));
		} 
		SourceString = GenerateSourceXML(ValueList, Hashmap);
		
		Pdf = readFile(OriginalXML);
		StringBuffer buf = new StringBuffer(Pdf);
		int PDF_start = buf.lastIndexOf("&lt;?xml"); 
		int PDF_end = buf.indexOf("&lt;/PDF&gt;");
		buf.replace(PDF_start, PDF_end+12, SourceString); 

		BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(DataFeededXML)));
	    bwr.write(buf.toString());
	    bwr.flush();
	    bwr.close();
		System.out.println("Success");
	}
	
	@SuppressWarnings("rawtypes")
	private static String  GenerateSourceXML(List Pathlist, HashMap<String, String> Hashmap) throws IOException
	{	
		for(int i=0;i<Pathlist.size();i++)
		{
		    String Path = (String) Pathlist.get(i);
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			Document doc = null;
			String xsl = readFile(Path);
			ByteArrayInputStream xslStream = new ByteArrayInputStream(xsl.getBytes(StandardCharsets.UTF_8.name()));
			
			try 
			{
				builder = factory.newDocumentBuilder();
			} 
			catch (ParserConfigurationException e1) 
			{
				e1.printStackTrace();
			}
			try 
			{
				doc = builder.parse(xslStream);
			} 
			catch (SAXException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			if(doc == null)
				System.out.println("yes null");
			
			generateXMLRequest(doc.getDocumentElement());
			
			request = request + formRequest(slctkeysArr, Hashmap);
	
	        flag = false;
	            
		}
		request=request + "\n&lt;/PDF&gt;";
		return request;
	}

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
	
	
	private static void generateXMLRequest(Node node)
	{
		NodeList nodeList = node.getChildNodes();
		String tempStr=null;
		String tempStr2=null;
		String tempStr3=null;
		for (int i = 0; i < nodeList.getLength(); i++) 
		{
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) 
			{
				if (currentNode.getNodeName().equals("xsl:value-of"))
				{
					if(!slctkeysArr.contains(currentNode.getAttributes().getNamedItem("select").getNodeValue().replace("/", "").replace(".", "")) && !otherkeysArr.contains(currentNode.getAttributes().getNamedItem("select").getNodeValue().replace("/", "").replace(".", "")))
					{
					slctkeysArr.add(currentNode.getAttributes().getNamedItem("select").getNodeValue().replace("/", "").replace(".", ""));
					}
				}
				if (currentNode.getNodeName().equals("xsl:if")) 
				{
					tempStr =currentNode.getAttributes().getNamedItem("test").getNodeValue().replace("/", "").replace(".", "");
					if(!otherkeysArr.contains(tempStr) && !slctkeysArr.contains(tempStr))
					{
						otherkeysArr.add(tempStr);
					}
				}
				if (currentNode.getNodeName().equals("xsl:when")) 
				{
					tempStr2 = currentNode.getAttributes().getNamedItem("test").getNodeValue().replace("/", "").replace(".", "");
					if(!otherkeysArr.contains(tempStr2) && !slctkeysArr.contains(tempStr2))
					{
						otherkeysArr.add(currentNode.getAttributes().getNamedItem("test").getNodeValue());
					}
				}
				if (currentNode.getNodeName().equals("xsl:apply-templates")) 
				{
					tempStr3 = currentNode.getAttributes().getNamedItem("select").getNodeValue().replace("/", "").replace(".", "");
					if(!otherkeysArr.contains(tempStr3)  && !slctkeysArr.contains(tempStr3))
					{
						otherkeysArr.add(currentNode.getAttributes().getNamedItem("select").getNodeValue());
					}
				}
				generateXMLRequest(currentNode);
			}
		}
	}
	
	
	private static String formRequest(ArrayList<String> slctkeysArr, HashMap<String, String> Hashmap)
	{
		String reqStr = new String();
		if(flag)
   		{
			reqStr="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;PDF&gt;";
   		}
		for (int i = 0; i < slctkeysArr.size(); i++) 
		{

		   slctkeysArr.set(i, filter(slctkeysArr.get(i).toString()));
		
		    String PDFAttributeValue = Hashmap.get(slctkeysArr.get(i).toString());          
			reqStr = reqStr + "\n\t" + "&lt;" + slctkeysArr.get(i).toString() + "&gt;" + PDFAttributeValue + "&lt;/" + slctkeysArr.get(i).toString() +  "&gt;";
		  
		}	
		//reqStr = reqStr + "\n</PDF>";
		return reqStr;
	}
	
	private static String filter(String str)
	{
		if(str.contains("(") || str.toString().contains(")")) 
		{
			int openbrace = str.lastIndexOf("(");
			int closebrace = str.lastIndexOf(")");
			str = str.substring(openbrace+1, closebrace);
			if(str.contains(","))
			{
				int comma = str.indexOf(",");
				str = str.substring(0, comma);
			}
		}
		return str;
	}
}
