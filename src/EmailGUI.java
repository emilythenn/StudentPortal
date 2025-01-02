package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.geometry.Pos;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class EmailGUI extends Application {
    private String emailContent;
    private TextField emailInput;

    public EmailGUI() {
        this.emailContent = "Sample Transcript Content"; // Example default content
    }

    public EmailGUI(String emailContent) {
        this.emailContent = emailContent;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the email scene using the createEmailScene method
        Scene emailScene = createEmailScene();

        // Set the email scene to the stage
        primaryStage.setTitle("Send Email with PDF");
        primaryStage.setScene(emailScene);
        primaryStage.show();
    }

    public Scene createEmailScene() {
        // Text field for email input
        emailInput = new TextField();
        emailInput.setPromptText("Enter recipient email");

        // Button to generate the PDF and send the email
        Button sendButton = new Button("Generate PDF and Send Email");
        sendButton.setOnAction(e -> {
            System.out.println("Button clicked");
            generatePDFAndSendEmail();
        });

        // Layout for the email scene
        VBox vbox = new VBox(
                new Text("Email Transcript"),
                emailInput,
                sendButton
        );
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        // Return the scene with the layout
        return new Scene(vbox, 300, 200);
    }

    private void generatePDFAndSendEmail() {
        String recipientEmail = emailInput.getText().trim();
        if (recipientEmail.isEmpty()) {
            showAlert("Error", "Please enter a valid email address.");
            return;
        }
        System.out.println("Recipient email: " + recipientEmail);

        try {
            // Step 1: Generate the PDF file
            String pdfFileName = "transcript.pdf";
            System.out.println("Generating PDF...");
            generatePDF(pdfFileName, emailContent);
            System.out.println("PDF generated: " + pdfFileName);

            // Step 2: Send the email with the PDF attachment
            System.out.println("Sending email...");
            sendEmailWithAttachment(recipientEmail, pdfFileName);
            System.out.println("Email sent successfully.");
        } catch (FileNotFoundException e) {
            showAlert("Error", "Failed to generate PDF: " + e.getMessage());
        }
    }

    private void generatePDF(String fileName, String content) throws FileNotFoundException {
        // Create a PdfWriter instance that writes to the file
        PdfWriter writer = new PdfWriter(fileName);

        // Create a PdfDocument using the writer
        PdfDocument pdfDocument = new PdfDocument(writer);

        // Create a Document (the main object where content will be added)
        Document document = new Document(pdfDocument);

        // Add content to the document
        document.add(new Paragraph(content));

        // Close the document
        document.close();
        System.out.println("PDF creation complete.");
    }

    private void sendEmailWithAttachment(String recipientEmail, String pdfFileName) {
        // Email configuration
        String subject = "Co-curriculum Transcript";
        String body = "Please find your co-curriculum transcript attached.";

        String smtpHost = "smtp.gmail.com";
        String smtpPort = "587";
        String username = "...";  // Enter your Gmail username
        String password = ".....";  // Enter your Gmail password

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

            // Attach the PDF file
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.attachFile(new File(pdfFileName));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            // Send the message
            Transport.send(message);
            showAlert("Success", "Email sent successfully with the PDF attachment!");
        } catch (MessagingException | java.io.IOException e) {
            showAlert("Error", "Failed to send email: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        // Display an alert (simulating a simple alert for simplicity)
        System.out.println(title + ": " + content);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
