import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

class Student {
    String email;
    String id;
    String password;
    List<String> subjects;
    List<String> clubs;

    public Student(String email, String id, String password, List<String> subjects, List<String> clubs) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.subjects = subjects;
        this.clubs = clubs;
    }
}

public class StudentPortalLogin extends Application {
    private Stage primaryStage;
    private String currentStudentId;
    private Map<String, Student> students = new HashMap<>();
    private Map<String, Integer> failedLoginAttempts = new HashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final String FILE_PATH = "UserData.txt";

    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^s\\d{6}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@student\\.fop$");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadStudentData();
        showLoginScreen();
    }

    private void loadStudentData() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
            for (int i = 0; i < lines.size(); i += 5) {
                if (i + 4 >= lines.size()) break;

                String email = lines.get(i).trim();
                String id = lines.get(i + 1).trim();
                String password = lines.get(i + 2).trim();
                List<String> subjects = Arrays.asList(lines.get(i + 3).trim().split(","));
                List<String> clubs = Arrays.asList(lines.get(i + 4).trim().split(","));

                if (!STUDENT_ID_PATTERN.matcher(id).matches() ||
                        !EMAIL_PATTERN.matcher(email).matches()) {
                    System.err.println("Invalid format for ID: " + id + " or email: " + email);
                    continue;
                }

                students.put(id.toLowerCase(), new Student(email, id, password, subjects, clubs));
                System.out.println("Loaded student: " + id);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load student data: " + e.getMessage());
        }
    }

    private void showLoginScreen() {
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: #f5f5f5;");

        // Add exit button at top-right corner
        Button btnExit = new Button("Exit");
        btnExit.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        AnchorPane exitPane = new AnchorPane();
        exitPane.getChildren().add(btnExit);
        AnchorPane.setRightAnchor(btnExit, 10.0);
        AnchorPane.setTopAnchor(btnExit, 10.0);

        VBox contentContainer = new VBox(15);
        contentContainer.setMaxWidth(350);
        contentContainer.setAlignment(Pos.CENTER);
        contentContainer.setPadding(new Insets(30));
        contentContainer.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label titleLabel = new Label("Student Portal Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox inputContainer = new VBox(15);
        inputContainer.setMaxWidth(300);

        // Email/ID field with toggle
        VBox loginBox = new VBox(5);
        Label loginLabel = new Label("Student ID or Email");
        loginLabel.setStyle("-fx-font-weight: bold;");
        TextField loginField = new TextField();
        loginField.setPromptText("Enter ID or Email");
        loginField.setPrefHeight(35);
        loginField.setStyle("-fx-font-size: 14px;");
        loginBox.getChildren().addAll(loginLabel, loginField);

        // Password field
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(35);
        passwordField.setStyle("-fx-font-size: 14px;");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        Button loginButton = new Button("Login");
        loginButton.setStyle(
                "-fx-background-color: #2196F3; " +
                        "-fx-text-fill: white; " +
                        "-fx-min-width: 300px; " +
                        "-fx-min-height: 35px; " +
                        "-fx-font-size: 14px; " +
                        "-fx-cursor: hand;"
        );

        Separator separator = new Separator();
        separator.setMaxWidth(300);

        HBox linksBox = new HBox(20);
        linksBox.setAlignment(Pos.CENTER);

        Hyperlink forgotPasswordLink = new Hyperlink("Forgot Password?");
        Hyperlink signUpLink = new Hyperlink("Sign Up");

        forgotPasswordLink.setStyle("-fx-font-size: 14px;");
        signUpLink.setStyle("-fx-font-size: 14px;");

        linksBox.getChildren().addAll(forgotPasswordLink, signUpLink);

        // Add components to containers
        inputContainer.getChildren().addAll(loginBox, passwordBox);
        contentContainer.getChildren().addAll(
                titleLabel,
                inputContainer,
                loginButton,
                separator,
                linksBox
        );

        mainContainer.getChildren().addAll(exitPane, contentContainer);

        // Event handlers
        loginButton.setOnAction(e -> validateLogin(loginField.getText(), passwordField.getText()));
        btnExit.setOnAction(e -> primaryStage.close());
        forgotPasswordLink.setOnAction(e -> showForgotPasswordDialog());
        signUpLink.setOnAction(e -> showSignUpDialog());
        passwordField.setOnAction(e -> validateLogin(loginField.getText(), passwordField.getText()));

        Scene scene = new Scene(mainContainer, 500, 600);
        primaryStage.setTitle("Student Portal Login");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void showForgotPasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Password Recovery");
        dialog.setHeaderText("Enter your student email to recover password");

        // Create the dialog content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField emailField = new TextField();
        emailField.setPromptText("student@student.fop");
        content.getChildren().addAll(new Label("Email:"), emailField);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the recovery process
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String email = emailField.getText().trim();
                if (email.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please enter your email address");
                    return null;
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format");
                    return null;
                }

                // Find student by email
                Optional<Student> student = students.values().stream()
                        .filter(s -> s.email.equalsIgnoreCase(email))
                        .findFirst();

                if (student.isPresent()) {
                    showAlert(Alert.AlertType.INFORMATION, "Password Recovery",
                            "Password reset instructions have been sent to your email address.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Email address not found");
                }
            }
            return null;
        });

        dialog.show();
    }

    private void showSignUpDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Student Registration");
        dialog.setHeaderText("Create new student account");

        // Create the form content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField emailField = new TextField();
        emailField.setPromptText("student@student.fop");

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("s123456");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        content.getChildren().addAll(
                new Label("Email:"), emailField,
                new Label("Student ID:"), studentIdField,
                new Label("Password:"), passwordField,
                new Label("Confirm Password:"), confirmPasswordField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the registration process
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String email = emailField.getText().trim();
                String id = studentIdField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                // Validate input
                if (email.isEmpty() || id.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "All fields are required");
                    return null;
                }

                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format");
                    return null;
                }

                if (!STUDENT_ID_PATTERN.matcher(id).matches()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid student ID format (must be s followed by 6 digits)");
                    return null;
                }

                if (!password.equals(confirmPassword)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match");
                    return null;
                }

                if (students.containsKey(id.toLowerCase())) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Student ID already exists");
                    return null;
                }

                // Registration successful
                showAlert(Alert.AlertType.INFORMATION, "Success",
                        "Registration successful! Please wait for admin approval.");
            }
            return null;
        });

        dialog.show();
    }

    private void validateLogin(String login, String password) {
        if (login.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both login credentials and password");
            return;
        }

        // Check for login attempts
        if (failedLoginAttempts.getOrDefault(login, 0) >= MAX_LOGIN_ATTEMPTS) {
            showAlert(Alert.AlertType.ERROR, "Account Locked",
                    "Too many failed attempts. Please reset your password or contact support.");
            return;
        }

        Student student = null;
        // Try to find student by ID first
        student = students.get(login.toLowerCase());

        // If not found by ID, try email
        if (student == null) {
            student = students.values().stream()
                    .filter(s -> s.email.equalsIgnoreCase(login))
                    .findFirst()
                    .orElse(null);
        }

        if (student != null && student.password.equals(password)) {
            currentStudentId = student.id;
            failedLoginAttempts.remove(login); // Reset failed attempts on successful login
            System.out.println("Login successful for ID: " + currentStudentId);
            showDashboard();
        } else {
            int attempts = failedLoginAttempts.getOrDefault(login, 0) + 1;
            failedLoginAttempts.put(login, attempts);
            showAlert(Alert.AlertType.ERROR, "Login Error",
                    String.format("Invalid credentials. %d attempts remaining.",
                            MAX_LOGIN_ATTEMPTS - attempts));
        }
    }

    private void showDashboard() {
        VBox dashboardLayout = new VBox(15);
        dashboardLayout.setPadding(new Insets(20));
        dashboardLayout.setAlignment(Pos.CENTER);
        dashboardLayout.setStyle("-fx-background-color: #f5f5f5;");

        Student student = students.get(currentStudentId.toLowerCase());
        Label welcomeLabel = new Label("Welcome, " + student.email);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button academicButton = createStyledButton("View Academic Information", "#2196F3");
        Button cocurricularButton = createStyledButton("View Club & Activities", "#4CAF50");
        Button transcriptButton = createStyledButton("Generate Full Transcript", "#FF9800");
        Button logoutButton = createStyledButton("Logout", "#f44336");

        academicButton.setOnAction(e -> openAcademicInfo());
        cocurricularButton.setOnAction(e -> openClubMembership());
        transcriptButton.setOnAction(e -> openCocurriculumTranscript());
        logoutButton.setOnAction(e -> logout());

        dashboardLayout.getChildren().addAll(
                welcomeLabel,
                new Separator(),
                academicButton,
                cocurricularButton,
                transcriptButton,
                new Separator(),
                logoutButton
        );

        Scene scene = new Scene(dashboardLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Student Portal Dashboard");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-pref-width: 250px; -fx-font-size: 14px;",
                color));
        return button;
    }

    private void openAcademicInfo() {
        try {
            System.out.println("Opening academic info with ID: " + currentStudentId);
            Stage academicStage = new Stage();
            AcademicInfoGUI academicInfo = new AcademicInfoGUI();
            academicInfo.setStudentId(currentStudentId);
            academicInfo.start(academicStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Academic Information");
        }
    }

    private void openClubMembership() {
        Stage clubStage = new Stage();
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Student student = students.get(currentStudentId.toLowerCase());
        Label titleLabel = new Label("Club & Activities");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Create separate VBoxes for each category
        VBox societyBox = new VBox(5);
        VBox uniformBox = new VBox(5);
        VBox sportBox = new VBox(5);

        // Add category labels
        Label societyLabel = new Label("Society:");
        Label uniformLabel = new Label("Body Uniform:");
        Label sportLabel = new Label("Sport:");

        societyBox.getChildren().add(societyLabel);
        uniformBox.getChildren().add(uniformLabel);
        sportBox.getChildren().add(sportLabel);

        // Load club names from file
        Map<String, String> clubNames = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("ClubSocieties.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    clubNames.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Categorize clubs
        for (String clubCode : student.clubs) {
            String clubName = clubNames.getOrDefault(clubCode, clubCode);
            Label clubLabel = new Label("- " + clubName);
            clubLabel.setStyle("-fx-padding: 0 0 0 20;"); // Add indent

            if (clubCode.startsWith("P")) {
                societyBox.getChildren().add(clubLabel);
            } else if (clubCode.startsWith("B")) {
                uniformBox.getChildren().add(clubLabel);
            } else if (clubCode.startsWith("S")) {
                sportBox.getChildren().add(clubLabel);
            }
        }

        Button closeButton = createStyledButton("Close", "#f44336");
        closeButton.setOnAction(e -> clubStage.close());

        layout.getChildren().addAll(titleLabel, societyBox, uniformBox, sportBox, closeButton);

        Scene scene = new Scene(layout, 400, 500);
        clubStage.setTitle("Club & Activities");
        clubStage.setScene(scene);
        clubStage.show();
    }

    private void openCocurriculumTranscript() {
        try {
            Stage transcriptStage = new Stage();
            CocurriculumMarkCalculatorGUI transcriptGUI = new CocurriculumMarkCalculatorGUI();
            transcriptGUI.setStudentId(currentStudentId);
            transcriptGUI.start(transcriptStage);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Co-curriculum Transcript: " + e.getMessage());
        }
    }

    private void logout() {
        currentStudentId = null;
        showLoginScreen();
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