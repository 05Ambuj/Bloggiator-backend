package com.project.bloggiator.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(String to, String username) {
        String subject = "Welcome to Bloggiator!";
        String loginUrl = "http://localhost:5500/login.html";

        // Create HTML content for the email
        String htmlContent = "<div style=\"font-family: 'Helvetica Neue', Arial, sans-serif; background-color: #f9f9f9; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);\">"
                + "<div style=\"background-color: #0066cc; padding: 20px; color: #ffffff; text-align: center;\">"
                + "<h1 style=\"margin: 0; font-size: 24px;\">Welcome to Bloggiator, " + username + "!</h1>"
                + "</div>"
                + "<div style=\"padding: 30px; color: #333333;\">"
                + "<p style=\"font-size: 16px; line-height: 1.5;\">Thank you for joining our community! We're excited to have you onboard. "
                + "To get started with your blogging journey, simply click the button below to log in:</p>"
                + "<div style=\"text-align: center; margin: 40px 0;\">"
                + "<a href=\"" + loginUrl + "\" style=\"display: inline-block; padding: 15px 25px; font-size: 18px; color: white; background-color: #0066cc; border-radius: 5px; text-decoration: none; font-weight: bold;\">Log In to Bloggiator</a>"
                + "</div>"
                + "<p style=\"font-size: 14px; line-height: 1.5; color: #555555;\">If you did not register for a Bloggiator account, please ignore this email.</p>"
                + "<p style=\"font-size: 14px; color: #555555;\">Best regards,<br>The Bloggiator Team</p>"
                + "</div>"
                + "<div style=\"background-color: #f1f1f1; padding: 20px; text-align: center; font-size: 12px; color: #999999;\">"
                + "<p style=\"margin: 0;\">&copy; 2024 Bloggiator. All rights reserved.</p>"
                + "<p style=\"margin: 0;\">You received this email because you recently registered on Bloggiator. If you did not, please contact us.</p>"
                + "</div>"
                + "</div>"
                + "</div>";
        try {
            // Create a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();

            // Use MimeMessageHelper to set up the email
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Set to true to send HTML content

            // Send the email
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle the exception appropriately in your application
            // This could involve logging the error or notifying an admin
        }
    }
}


//package com.project.bloggiator.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public void sendRegistrationEmail(String to, String username) {
//        String subject = "Welcome to Bloggiator!";
//        String loginUrl = "http://localhost:5500/login.html";
//        // the URL as necessary
//        String text = "Dear " + username + ",\n\n" +
//                "Thank you for registering on Bloggiator. " +
//                "Click the button below to log in and start blogging:\n\n" +
//                "<a href=\"" + loginUrl + "\">Login</a>";
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(text);
//
//        mailSender.send(message);
//    }
//}
