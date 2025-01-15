import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import java.io.*;
import javafx.application.Platform;

public class CocurriculumMarkCalculatorGUI extends Application {
    private TextArea transcriptArea;
    private TextField matricField;
    private CocurriculumMarkCalculator calculator;
    private PDFGenerator pdfGenerator;
    private String generatedTranscriptContent;
    private String studentId; // Add this field

    // Add constructor
    public CocurriculumMarkCalculatorGUI(String studentId) {
        this.studentId = studentId;
    }

    // Add no-arg constructor for backward compatibility
    public CocurriculumMarkCalculatorGUI() {
        this.studentId = null;
    }

    @Override
    public void start(Stage primaryStage) {
        calculator = new CocurriculumMarkCalculator();
        calculator.loadData();
        pdfGenerator = new PDFGenerator();

        // Create main container
        VBox mainContainer = new VBox(15);
        mainContainer.setPadding(new Insets(15));
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");

        // Create title
        Label titleLabel = new Label("Co-curriculum Mark Calculator");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create input section
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);

        Label matricLabel = new Label("Matric Number:");
        matricLabel.setStyle("-fx-font-size: 14px;");

        matricField = new TextField();
        matricField.setPromptText("Enter matric number");
        matricField.setPrefWidth(150);

        // If studentId is provided, set it and disable the field
        if (studentId != null) {
            matricField.setText(studentId);
            matricField.setEditable(false);
            // Automatically generate transcript when opened
            Platform.runLater(this::generateTranscript);
        }

        Button generateButton = new Button("Generate Transcript");
        generateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        Button sendEmailButton = new Button("Send Email");
        sendEmailButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        inputBox.getChildren().addAll(matricLabel, matricField, generateButton, sendEmailButton);

        // Create transcript area
        transcriptArea = new TextArea();
        transcriptArea.setEditable(false);
        transcriptArea.setFont(Font.font("Monospaced", 14));
        transcriptArea.setStyle("-fx-control-inner-background: #ffffff;");
        transcriptArea.setPrefRowCount(20);
        transcriptArea.setWrapText(true);

        mainContainer.getChildren().addAll(titleLabel, inputBox, transcriptArea);

        Scene scene = new Scene(mainContainer, 800, 600);
        primaryStage.setTitle("Co-curriculum Mark Calculator");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        generateButton.setOnAction(e -> generateTranscript());
        sendEmailButton.setOnAction(e -> openEmailGUI());

        primaryStage.show();
    }

    private void generateTranscript() {
        String matricNumber = matricField.getText().trim().toLowerCase();

        if (matricNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a matric number");
            return;
        }

        // Capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream oldOut = System.out;
        System.setOut(printStream);

        // Generate transcript
        calculator.calculateAndGenerateTranscript(matricNumber);

        // Restore original System.out
        System.setOut(oldOut);

        // Display the output
        generatedTranscriptContent = outputStream.toString();  // Store the generated content here
        if (generatedTranscriptContent.trim().isEmpty() || generatedTranscriptContent.contains("No data found")) {
            showAlert(Alert.AlertType.ERROR, "Error", "No data found for matric number: " + matricNumber);
            transcriptArea.clear();
        } else {
            transcriptArea.setText(generatedTranscriptContent);  // Display the transcript in the TextArea
            try {
                String pdfFileName = "Transcript_" + matricNumber + ".pdf";
                pdfGenerator.generatePDF(pdfFileName, generatedTranscriptContent);  // Generate the PDF with the content
                showAlert(Alert.AlertType.INFORMATION, "PDF Generated", "Transcript saved as " + pdfFileName);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF: " + e.getMessage());
            }
        }
    }

    private void openEmailGUI() {
        if (this.generatedTranscriptContent == null || this.generatedTranscriptContent.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please generate the transcript first.");
            return;
        }

        // Open Email GUI and pass the transcript content
        EmailGUI emailGUI = new EmailGUI(this.generatedTranscriptContent);
        Stage emailStage = new Stage();

        Scene emailScene = emailGUI.createEmailScene();  // Assuming createEmailScene() in EmailGUI returns the scene
        emailStage.setTitle("Send Transcript Email");
        emailStage.setScene(emailScene);
        emailStage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
