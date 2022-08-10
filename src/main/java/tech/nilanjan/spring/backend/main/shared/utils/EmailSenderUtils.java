package tech.nilanjan.spring.backend.main.shared.utils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.apache.http.entity.ContentType;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;
import tech.nilanjan.spring.backend.main.ui.controller.EmailVerificationController;

import java.io.IOException;

@Component
public class EmailSenderUtils {
    private final String VERIFICATION_EMAIL_SUBJECT = "Verify your Email address";
    private final String RESET_PASSWORD_SUBJECT = "Reset your password";
    private Mail createMail(
            String destinationEmailAddress,
            String mailSubject,
            String mailBody
    ) {
        Email senderEmail = new Email(EmailSendGridKeys.SENDER_EMAIL_ADDRESS);
        Email destinationEmail = new Email(destinationEmailAddress);
        Content content = new Content(ContentType.TEXT_PLAIN.getMimeType(), mailBody);
        return new Mail(senderEmail, mailSubject, destinationEmail, content);
    }

    private void sendEmail(Mail mailRequest) {
        try {
            SendGrid sg = new SendGrid(EmailSendGridKeys.SEND_GRID_API_KEY);
            Request request = new Request();

            request.setEndpoint("mail/send");
            request.setBody(mailRequest.build());
            request.setMethod(Method.POST);

            sg.api(request);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendVerificationEmail(
            String destinationEmailAddress,
            String verificationToken
    ) {
        Link verificationLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder
                        .methodOn(EmailVerificationController.class)
                        .verifyEmailToken(verificationToken)
        ).withSelfRel();

        Mail mailObject = this.createMail(
                destinationEmailAddress,
                this.VERIFICATION_EMAIL_SUBJECT,
                verificationLink.toString()
        );

        this.sendEmail(mailObject);
    }

    public void sendResetPasswordEmail(
            String destinationEmailAddress,
            String resetPasswordToken
    ) {
        Mail mailObject = this.createMail(
                destinationEmailAddress,
                this.RESET_PASSWORD_SUBJECT,
                resetPasswordToken
        );

        this.sendEmail(mailObject);
    }

}
