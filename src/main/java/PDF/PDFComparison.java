package PDF;

import java.io.IOException;

import de.redsix.pdfcompare.PdfComparator;

public class PDFComparison 
{
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
	
	public static void main(String args[]) throws IOException
	{
		String file1="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 (0117).pdf";
        String file2="Q:/Manual Testing/Starr/Starr-GL/FormsTemplate/All Forms/SIIL DS 01 FL (0117) Common Policy Dec Page.pdf";
        String result="D:/ftl/resultPDF";
		PDFComparison newcompare = new PDFComparison();
		newcompare.pdfcompare(file1,file2,result);
		//newcompare.pdfcompare1();
	}
}
