package application.tools;

import java.nio.file.Paths;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
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

import model.ConfigurationSave;

public class MailSender {

    public static boolean sendingTest(String _mail, String _password, String _host, int _port,
            boolean _authentification, boolean _tlsenable) {
        final String username = _mail;
        final String password = _password;
        // System.out.println(_mail);
        // System.out.println(_password);
        // System.out.println(_host);
        // System.out.println(_port);
        // System.out.println(_authentification);
        // System.out.println(_tlsenable);

        Properties props = new Properties();
        props.put("mail.smtp.host", _host);
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.auth", _authentification == true ? true : false);
        props.put("mail.smtp.starttls.enable", _tlsenable == true ? true : false);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(javax.mail.Message.RecipientType.TO, "odr-eu@amazon.com");
            message.setSubject("Exemple d'objet.");
            message.setText("Exemple de test.");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean sendMail(ConfigurationSave _conf, String _dest) {
        try {
            final String username = _conf.serverConf.mail;
            final String password = _conf.serverConf.password;

            Properties props = new Properties();
            props.put("mail.smtp.host", _conf.serverConf.host);
            props.put("mail.smtp.port", _conf.serverConf.port);
            props.put("mail.smtp.auth", _conf.serverConf.authentification);
            props.put("mail.smtp.starttls.enable", _conf.serverConf.tlsenable);

            Session session = Session.getInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(_conf.mailContent);

            MimeBodyPart attachmentPart = new MimeBodyPart();

            String file = _conf.pathFilepdf.pop();
            String fileName = Paths.get(file).getFileName().toString();
            DataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(fileName);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("vqlcaniqtlse@hotmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(_dest));
            message.setSubject(_conf.mailSubject);
            message.setContent(multipart);

            Transport.send(message);

            return true;
        } catch (MessagingException | NullPointerException | IllegalArgumentException e) {
            return false;
        }
    }
}
