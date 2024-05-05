package emailUtils;

import Enumerations.MessageLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;
import static propertyUtils.PropertyUtils.getGlobalProperty;
import static ReportUtils.Report.reportAndLog;

public class ZimSMTP {

    private Logger logger = LoggerFactory.getLogger(ZimSMTP.class);

    private static Integer amountOfSentEmails = 0;

    public ZimSMTP() {
    }

    /**
     * Sending email(HTML content) by SMTP without authentication
     *
     * @param fromEmail from which email send
     * @param toEmail to which email send - TO
     * @param ccEmail to which email send copy - CC
     * @param subject subject of email
     * @param htmlContent content of email
     * @author reed.dakota
     * @since 07.05.2023
     */
    public void sendBySMTPNoAuthentication(String fromEmail, List<String> toEmail, List<String> ccEmail, String subject, String htmlContent) throws Exception {
        if (amountOfSentEmails < 500) {
        Properties properties = System.getProperties();
        String jobName = System.getProperty("jobName");
        String buildId = System.getProperty("BuildID");

            //in case of Kubernetes MSO cluster we use main smtp
        if (jobName!=null && buildId!=null && jobName.equalsIgnoreCase("mvn-test"))
            properties.setProperty("mail.smtp.host", getGlobalProperty("smtpHost"));
        else
            properties.setProperty("mail.smtp.host", getGlobalProperty("smtpdHost"));

        try {
            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            for (String email : toEmail)
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            for (String email : ccEmail) {
                if (email.contains("@"))
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
            }
            message.setSubject(subject);
            // create the HTML part of the email
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html");
            // create the multipart message and add the HTML part to it
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(htmlPart);
            // add the multipart message to the email message
            message.setContent(multipart);
            // send the message
            Transport.send(message);
            reportAndLog("Email sent to: " + toEmail + " and to: " + ccEmail, MessageLevel.INFO);
                amountOfSentEmails += 1;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            reportAndLog("Failed to send email: " + mex.getMessage(),MessageLevel.ERROR);
            throw new Exception("Failed to send email: " + mex.getMessage());
        }
        }
        else {
            reportAndLog("Send limit(500) for a single run reached",MessageLevel.ERROR);
            throw new Exception("Send limit of 500 reached");
        }
    }
}
