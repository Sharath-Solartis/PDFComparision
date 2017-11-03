package PDFHandleException;

public class PDFHandleException extends Exception 
{
    private static final long serialVersionUID = 1L;
    
    public PDFHandleException(String message)
	{
    	super (message);
	}
    
    public PDFHandleException(Exception e) 
    {
        super(e);
    }

    public PDFHandleException(String message, Exception e) 
    {
        super(message, e);
    }
}
