package util;

import config.Config;

import java.security.MessageDigest;
import java.util.Random;
import java.util.Properties;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.*;
/**
 * Created by slgu1 on 11/5/15.
 */
public class Util {
    public static String encrypt(String input) {
        try {
            byte[] bytesOfMessage = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            StringBuilder md5StrBuff = new StringBuilder();
            for (int i = 0; i < thedigest.length; i++) {
                if (Integer.toHexString(0xFF & thedigest[i]).length() == 1) {
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & thedigest[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & thedigest[i]));
                }
            }
            return md5StrBuff.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String random(int number) {
        StringBuilder res = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < number; ++i) {
            res.append(random.nextInt(9));
        }
        return res.toString();
    }

    public static void sendEmail(String email, String code) throws AddressException, MessagingException {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", 25);

        // Set properties indicating that we want to use STARTTLS to encrypt the connection.
        // The SMTP session will begin on an unencrypted connection, and then the client
        // will issue a STARTTLS command to upgrade to an encrypted connection.
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("sg3301@columbia.edu"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        msg.setSubject("cloud lzgg verify email");
        msg.setContent(String.format("your lzgg cloud code is %s", code), "text/plain");

        // Create a transport.
        Transport transport = session.getTransport();

        // Send the message.
        try {
            System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(Config.SMTPServer, Config.SMTPUsername, Config.SMTPPasswd);
            // Send the email.
            System.out.println("Email sent!");
            transport.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        } finally {
            // Close and terminate the connection.
            transport.close();
        }
    }

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        //System.out.println(random(6));
        /*
        try {
            sendEmail("blackhero98@gmail.com", "142857");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
        //System.out.println(encrypt("slgu"));
        //checkFloat("37.5a");
    }

    public static boolean checkFloat(String uid) {
        System.out.println(uid);
        try {
            Float a = Float.parseFloat(uid);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
