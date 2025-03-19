package me.rahul.thoughts.config;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailDispatcherService {

    final private SendGrid sendGrid;
    final private Email fromEmail;

    private static final String EMAIL_ENDPOINT = "mail/send";

    @Async
    public void dispatchEmail(String subject, Email toEmail, Content content) {
        try {
            log.info("Email sending is started.");
//            Email toEmail = new Email(emailId);
//            Content content = new Content("text/plain", body);
            Mail mail = new Mail(fromEmail, subject, toEmail, content);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint(EMAIL_ENDPOINT);
            request.setBody(mail.build());

            sendGrid.api(request);
            log.info("Email sending is complete.");
        } catch (IOException ex) {
            log.info("Failed to send email: {}", ex.getMessage());
        }
    }

    public void sendAccountCreationEmail(String emailId, String username) {
        String subject = "Welcome to Thought Blog! Start Sharing Your Thoughts";
        String bodyTemplet = """
               <!DOCTYPE html>
               <html lang="en">
               <head>
                   <meta charset="UTF-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                   <title>Welcome to Thought Blog</title>
                   <style>
                       body {
                           font-family: Arial, sans-serif;
                           background-color: #f4f4f4;
                           margin: 0;
                           padding: 0;
                       }
                       .container {
                           width: 100%%;
                           max-width: 600px;
                           margin: 0 auto;
                           background-color: #ffffff;
                           padding: 20px;
                           border-radius: 8px;
                           box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                       }
                       .header {
                           text-align: center;
                           padding-bottom: 20px;
                       }
                       .header img {
                           width: 150px;
                           height: auto;
                       }
                       .content {
                           font-size: 16px;
                           line-height: 1.6;
                           color: #333333;
                       }
                       .button {
                           display: inline-block;
                           padding: 10px 20px;
                           background-color: #007BFF;
                           color: #ffffff;
                           text-decoration: none;
                           border-radius: 5px;
                           margin-top: 20px;
                       }
                       .footer {
                           margin-top: 20px;
                           font-size: 14px;
                           color: #777777;
                           text-align: center;
                       }
                   </style>
               </head>
               <body>
                   <div class="container">
                       <div class="header">
                           <h1>Welcome to Thought Blog!</h1>
                       </div>
                       <div class="content">
                           <p>Hello %s,</p>
                           <p>We're thrilled to have you join the Thought Blog community! This is your space to share your thoughts, ideas, and stories with the world.</p>
                           <p>Here are a few things to get you started:</p>
                           <ul>
                               <li>Explore the latest blogs and engage with other writers.</li>
                               <li>Start writing your own blog posts and share your unique perspective.</li>
                               <li>Connect with fellow bloggers and build your network.</li>
                           </ul>
                           <p>If you have any questions or need assistance, feel free to reach out to our support team.</p>
                           <p>Happy blogging!</p>
                           <p>Best regards,<br>The Thought Blog Team</p>
                       </div>
                       <div class="footer">
                           <p>&copy; 2023 Thought Blog. All rights reserved.</p>
                       </div>
                   </div>
               </body>
               </html>
               """;
        String body = String.format(bodyTemplet, username);
        Email toEmail = new Email(emailId);
        Content content = new Content("text/html", body);

        dispatchEmail(subject, toEmail, content);
    }

}
