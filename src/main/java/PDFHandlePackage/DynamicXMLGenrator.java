package PDFHandlePackage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DynamicXMLGenrator 
{
	int j = 0;
	int ifCount = 0;
	static String request = "";
	static FileWriter fw = null;
	static ArrayList<String> slctkeysArr = new ArrayList<String>();
	static ArrayList<String> otherkeysArr = new ArrayList<String>();
	static boolean flag = true;
	static boolean image_flag = true;
	
	@SuppressWarnings("rawtypes")
	public static String  GenerateSourceXML(List Pathlist) throws IOException
	{	
		File newTextFile = new File("C:\\Users\\vigneshkumar_p.SOLARTISTECH\\Desktop\\vicky.xsl");
        fw = new FileWriter(newTextFile);
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
			
			request = request + formRequest(slctkeysArr);
	
	        flag = false;
	            
		}
		request=request + "\n&lt;/PDF&gt;";
		fw.write(request+"\n&lt;/PDF&gt;");
		fw.close();
		return request;
	}

	public static String readFile(String file) throws IOException 
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
//						lastIndex = tempStr.lastIndexOf("\"")+1;
//						firstIndex =tempStr.indexOf("\"");
//						slctkeysArr.add(tempStr+"<-->"+tempStr.substring(lastIndex, firstIndex));
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
	
	
	public static void filterKeys(ArrayList<String> keylist)
	{
		String repStr =null;
		int lastIndex =0;
		int firstIndex =0;
		for (int i = 0; i < slctkeysArr.size(); i++) 
		{
			repStr = new String(slctkeysArr.get(i)).replace("/", "").replace(".", "");
			if(slctkeysArr.get(i).contains("(") || slctkeysArr.get(i).contains(")"))
			{
				lastIndex = slctkeysArr.get(i).lastIndexOf("(")+1;
				firstIndex =slctkeysArr.get(i).indexOf(",");
				repStr = new String(slctkeysArr.get(i).substring(lastIndex, firstIndex).replace("/", "").replace(".", ""));
			}
			slctkeysArr.set(i, repStr); 
		}
	}
	
	
	public static String formRequest(ArrayList<String> slctkeysArr)
	{
		String reqStr = new String();
		String testData = "TestData";
		/*if(flag)
   		{
			reqStr="&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;PDF&gt;";
   		}*/
		for (int i = 0; i < slctkeysArr.size(); i++) 
		{

		   slctkeysArr.set(i, filter(slctkeysArr.get(i).toString()));
                      
			//if(slctkeysArr.get(i).toString().contentEquals("ISO_BOP_Policy_Number"))//dynamic values
			//{  
		   reqStr = reqStr + "\n\t" + slctkeysArr.get(i).toString();
		   			/*reqStr = reqStr + "\n\t" + "&lt;" + slctkeysArr.get(i).toString()
					+ "&gt;" + "vicky" + "&lt;/" + slctkeysArr.get(i).toString()//dynamic values
					+ "&gt;";*/
			//}
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
