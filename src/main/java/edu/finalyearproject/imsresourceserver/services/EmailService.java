package edu.finalyearproject.imsresourceserver.services;

import edu.finalyearproject.imsresourceserver.controllers.PurchaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Service for sending emails with HTML reports to the requested user.
 */
@Service("emailService")
public class EmailService
{
    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender javaMailSender;

    private Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Method for configuring email and attaching an HTML report, before sending to specified recipient.
     * @param recipient - the email address to send the email to
     * @param subject - the subject of the email
     * @param body - the text body of the email
     * @param report - the HTML report to attach as a file to the email
     * @param filename - the name of the created HTML file containing the report
     */
    public void sendEmailWithAttachment(String recipient, String subject, String body, String report, String filename)
    {
        log.info("Sending email with HTML report to "+ recipient);
        File file = null;
        try
        {
            file = createFileToAttach(report, filename);
        } catch (IOException e)
        {
            log.error("Unable to create HTML file locally: "+e);
        }

        MimeMessagePreparator preparator = generateEmail(recipient, subject, body, filename, file);

        try
        {
            javaMailSender.send(preparator);
        }
        catch (MailException ex)
        {
            log.error("Exception when sending email with " + filename + " report ", ex);
        }

        file.delete();
    }

    private MimeMessagePreparator generateEmail(String recipient, String subject, String body, String filename, File file)
    {
        return mimeMessage ->
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
                mimeMessage.setFrom(new InternetAddress(username));
                mimeMessage.setSubject(subject);
                mimeMessage.setText(body);


                FileSystemResource fileSystemResource = new FileSystemResource(file);
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setText("Example");
                helper.addAttachment(filename+".html", fileSystemResource, "text/html");
            };
    }

    private File createFileToAttach(String report, String filename) throws IOException
    {
        File file = new File(filename+".html");

        try (FileWriter fileWriter = new FileWriter(file))
        {
            fileWriter.write(report);
        } catch (IOException e)
        {
            log.error("Unable to write HTML report to file. " + e);
        }

        return file;
    }
}
