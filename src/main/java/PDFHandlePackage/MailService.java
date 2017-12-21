package PDFHandlePackage;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailService 
{
   public static void sendMail(String From,String To,String username,String password,String subject,String body,String filetoAttach) 
   {
      
      String to = To;
      String from = From;
      final String Username = username;
      final String Password = password;
      String host = "smtp-mail.outlook.com";
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");
      Session session = Session.getInstance(props,new javax.mail.Authenticator() 
      {
            protected PasswordAuthentication getPasswordAuthentication()
            {
               return new PasswordAuthentication(Username, Password);
            }
      });
      try 
      {
         
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(from));
         message.setRecipients(Message.RecipientType.TO,
         InternetAddress.parse(to));
         message.setSubject(subject);
         BodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setText(body);
         Multipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageBodyPart);
         messageBodyPart = new MimeBodyPart();
         String filename = filetoAttach;
         DataSource source = new FileDataSource(filename);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(filename);
         multipart.addBodyPart(messageBodyPart);
         message.setContent(multipart);
         Transport.send(message);
        
         System.out.println("Sent message successfully....");
  
      }
      catch (MessagingException e)
      {
         throw new RuntimeException(e);
      }
   }
  }
