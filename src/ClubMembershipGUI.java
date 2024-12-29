import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class ClubMembershipGUI extends Application {
    private String studentId;
    private List<String> studentClubs = new ArrayList<>();
    private Map<String, String> clubDetails = new HashMap<>();

    public void setStudentId(String studentId) {
        this.studentId = studentId;
        System.out.println("Set student ID: " + studentId);
    }

    @Override
    public void start(Stage primaryStage) {
        loadClubDetails();
        loadStudentClubs();

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Student Cocurricular Club Membership");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TextArea clubsArea = new TextArea();
        clubsArea.setEditable(false);
        clubsArea.setWrapText(true);
        clubsArea.setPrefRowCount(15);

        displayClubs(clubsArea);

        Button transcriptButton = new Button("Generate Transcript");
        transcriptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        layout.getChildren().addAll(titleLabel, new Separator(), clubsArea, transcriptButton);

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void displayClubs(TextArea clubsArea) {
        StringBuilder display = new StringBuilder();

        // Uniform Bodies (B)
        display.append("Uniform Bodies:\n");
        for (String club : studentClubs) {
            if (club.startsWith("B")) {
                display.append(String.format("%s - %s\n", club, clubDetails.getOrDefault(club, "Unknown")));
            }
        }

        // Societies (P)
        display.append("\nSocieties:\n");
        for (String club : studentClubs) {
            if (club.startsWith("P")) {
                display.append(String.format("%s - %s\n", club, clubDetails.getOrDefault(club, "Unknown")));
            }
        }

        // Sports Clubs (S)
        display.append("\nSports Clubs:\n");
        for (String club : studentClubs) {
            if (club.startsWith("S")) {
                display.append(String.format("%s - %s\n", club, clubDetails.getOrDefault(club, "Unknown")));
            }
        }

        clubsArea.setText(display.toString());
    }

    private void loadClubDetails() {
        clubDetails.put("B01", "Scout");
        clubDetails.put("B03", "Police Cadet");
        clubDetails.put("B07", "The Boys' Brigade");
        clubDetails.put("P27", "Computer Society");
        clubDetails.put("P81", "Young Entrepreneur Society");
        clubDetails.put("P82", "Robotic Club");
        clubDetails.put("S01", "Badminton Club");
        clubDetails.put("S10", "Swimming Club");
        clubDetails.put("S15", "Fencing Club");
    }

    private void loadStudentClubs() {
        try (BufferedReader reader = new BufferedReader(new FileReader("UserData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = reader.readLine();
                reader.readLine(); // password
                reader.readLine(); // subjects
                String clubs = reader.readLine();

                if (id != null && id.equals(studentId)) {
                    studentClubs = Arrays.asList(clubs.split(","));
                    System.out.println("Loaded clubs for " + studentId + ": " + studentClubs);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load student clubs");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}