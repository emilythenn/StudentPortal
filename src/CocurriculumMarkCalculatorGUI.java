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

public class CocurriculumMarkCalculatorGUI extends Application {
    private TextArea transcriptArea;
    private TextField matricField;
    private CocurriculumMarkCalculator calculator;

    @Override
    public void start(Stage primaryStage) {
        calculator = new CocurriculumMarkCalculator();
        calculator.loadData();

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

        Button generateButton = new Button("Generate Transcript");
        generateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        inputBox.getChildren().addAll(matricLabel, matricField, generateButton);

        // Create transcript area
        transcriptArea = new TextArea();
        transcriptArea.setEditable(false);
        transcriptArea.setFont(Font.font("Monospaced", 14));
        transcriptArea.setStyle("-fx-control-inner-background: #ffffff;");
        transcriptArea.setPrefRowCount(20);
        transcriptArea.setWrapText(true);

        // Add components to main container
        mainContainer.getChildren().addAll(titleLabel, inputBox, transcriptArea);

        // Create scene
        Scene scene = new Scene(mainContainer, 800, 600);

        // Set up stage
        primaryStage.setTitle("Co-curriculum Mark Calculator");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        // Add button action
        generateButton.setOnAction(e -> generateTranscript());

        // Show the stage
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
        String transcript = outputStream.toString();
        if (transcript.trim().isEmpty() || transcript.contains("No data found")) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "No data found for matric number: " + matricNumber);
            transcriptArea.clear();
        } else {
            transcriptArea.setText(transcript);
        }
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

    public void setStudentId(String currentStudentId) {

    }
}