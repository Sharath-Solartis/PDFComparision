package PDFDynamic.PDFDynamicComparision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.custommonkey.xmlunit.DetailedDiff; 
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.xml.sax.SAXException;
public class XMLComparision
{
	//final static Logger logger = Logger.getLogger(XMLComparision.class);

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) 
	{
		File f1 = new File("C:/Users/vigneshkumar_p.SOLARTISTECH/Desktop/eg1.xml");
		File f2= new File("C:/Users/vigneshkumar_p.SOLARTISTECH/Desktop/eg2.xml");
		File f3= new File("C:/Users/vigneshkumar_p.SOLARTISTECH/Desktop/Activity log.log");

		try 
		{
			f3.createNewFile();
	    } 
		catch (IOException e) 
		{
			e.printStackTrace();
	    }
		
		FileReader fr1 = null;
		FileReader fr2 = null;
		
		try 
		{
			fr1 = new FileReader(f1);
			fr2 = new FileReader(f2);
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
        }

		try 
		{
			Diff diff = new Diff(fr1, fr2);
			System.out.println("Similar? " + diff.similar());
			System.out.println("Identical? " + diff.identical());
			DetailedDiff detDiff = new DetailedDiff(diff);
			List differences = detDiff.getAllDifferences();
				for (Object object : differences) 
				{
				    Difference difference = (Difference)object;
				    System.out.println("***********************");
				    System.out.println(difference);
				    System.out.println("***********************");
				}
		}
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}

		XMLComparision obj = new XMLComparision();
		obj.runMe("ila");
	}

	private void runMe(String parameter)
	{
		/*if(logger.isDebugEnabled())
		{
		    logger.debug("This is debug : " + parameter);
		}
	
		if(logger.isInfoEnabled())
		{
		    logger.info("This is info : " + parameter);
		}
	
		logger.warn("This is warn : " + parameter);
		logger.error("This is error : " + parameter);
		logger.fatal("This is fatal : " + parameter);*/
	}
} 
