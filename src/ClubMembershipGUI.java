package com.example.studentportal;

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
    private Map<String, String[]> clubDetails = new HashMap<>();

    public void setStudentId(String studentId) {
        this.studentId = studentId;
        System.out.println("Set student ID: " + studentId);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label titleLabel = new Label("Student Cocurricular Club Membership");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Enter Student ID");

        Button loadButton = new Button("Load Clubs");
        loadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        TextArea clubsArea = new TextArea();
        clubsArea.setEditable(false);
        clubsArea.setWrapText(true);
        clubsArea.setPrefRowCount(15);

        loadButton.setOnAction(e -> {
            setStudentId(studentIdField.getText());  // Get the student ID from the input field
            loadClubDetails();  // Load the club details from the file
            loadStudentClubs();  // Load the clubs for the entered student ID
            displayClubs(clubsArea);  // Display the clubs in the TextArea
        });

        layout.getChildren().addAll(titleLabel, studentIdField, loadButton, new Separator(), clubsArea);

        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadClubDetails() {
        try {
            clubDetails = retrieveClubData("ClubSocieties.txt");
        } catch (IOException e) {
            showAlert("Error", "Failed to load club details");
        }
    }

    private void loadStudentClubs() {
        try {
            studentClubs = getStudentClubs("UserData.txt", studentId);
        } catch (IOException e) {
            showAlert("Error", "Failed to load student clubs");
        }
    }

    private void displayClubs(TextArea clubsArea) {
        // If no clubs are found, show a message
        if (studentClubs.isEmpty()) {
            clubsArea.setText("No clubs found for student ID: " + studentId);
            return;
        }

        // Categorize the clubs
        Map<String, List<String>> categorisedClubs = new LinkedHashMap<>();
        categorisedClubs.put("Societies", new ArrayList<>());
        categorisedClubs.put("Uniform Body", new ArrayList<>());
        categorisedClubs.put("Sports Club", new ArrayList<>());

        for (String club : studentClubs) {
            if (clubDetails.containsKey(club)) {
                String[] clubDetailsArray = clubDetails.get(club);
                String name = clubDetailsArray[0];
                String category = clubDetailsArray[1];
                categorisedClubs.get(category).add(club + " - " + name);
            }
        }

        // Build the display string for the clubs
        StringBuilder display = new StringBuilder();
        for (String category : categorisedClubs.keySet()) {
            if (!categorisedClubs.get(category).isEmpty()) {
                display.append(category).append(": ")
                        .append(String.join(", ", categorisedClubs.get(category)))
                        .append("\n");
            }
        }

        // Set the text of the TextArea with the categorized clubs
        if (display.length() == 0) {
            clubsArea.setText("No clubs found for the selected student.");
        } else {
            clubsArea.setText(display.toString());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static Map<String, String[]> retrieveClubData(String filePath) throws IOException {
        Map<String, String[]> clubs = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] club = line.split(",");
            String code = club[0];
            String name = club[1];

            String category;
            if (code.startsWith("P")) {
                category = "Societies";
            } else if (code.startsWith("B")) {
                category = "Uniform Body";
            } else {
                category = "Sports Club";
            }

            clubs.put(code, new String[]{name, category});
        }
        reader.close();
        return clubs;
    }

    public static List<String> getStudentClubs(String filePath, String matricNumber) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals(matricNumber)) {
                for (int i = 0; i < 3; i++) line = reader.readLine();
                reader.close();
                return Arrays.asList(line.split(","));
            }
        }
        reader.close();
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
