import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailGUI extends Application {
    private String emailContent;

    public EmailGUI() {
        this.emailContent=emailContent;
    }
    public EmailGUI(String emailContent) {
        this.emailContent = emailContent;
    }

    @Override
    public void start(Stage primaryStage) {
        Button sendButton = new Button("Send Email");
        sendButton.setOnAction(e -> sendEmail());

        VBox vbox = new VBox(new Text("Email Transcript"), sendButton);
        vbox.setSpacing(10);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setTitle("Send Email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public Scene createEmailScene() {
        Button sendButton = new Button("Send Email");
        sendButton.setOnAction(e -> sendEmail());

        VBox vbox = new VBox(new Text("Email Transcript"), sendButton);
        vbox.setSpacing(10);
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        return new Scene(vbox, 300, 200);
    }

    private void sendEmail() {
        // Email configuration
        String recipientEmail = "....@gmail.com";  // Use the saved user email here
        String subject = "Co-curriculum Transcript";
        String body = emailContent;  // Use the generated transcript content as the email body


        String smtpHost = "smtp.gmail.com";
        String smtpPort = "587";
        String username = "..";
        String password = "...";

        try {
            // Set up mail properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");

            // Create a session with the mail server
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Compose the email
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the message
            Transport.send(message);
            showAlert("Success", "Email sent successfully!");
        } catch (MessagingException e) {
            showAlert("Error", "Failed to send email: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        // Display an alert (similar to JavaFX Alert)
        System.out.println(title + ": " + content);  // Simulating an alert for simplicity
    }

    public static void main(String[] args) {
        launch(args);
    }
}
