import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class AcademicInfoGUI extends Application {
    private String studentId;
    private Map<String, SubjectEntry> subjectDatabase;
    private static String currentStudentId;

    public static void setCurrentStudentId(String studentId) {
        currentStudentId = studentId;
    }

    public static String getCurrentStudentId() {
        return currentStudentId;
    }

    // Add a no-argument constructor
    public AcademicInfoGUI() {
        this.studentId = "";
        this.subjectDatabase = new HashMap<>();
    }

    public AcademicInfoGUI(String studentId) {
        this.studentId = studentId;
        this.subjectDatabase = new HashMap<>();
        loadSubjectDatabase();
    }

    public void setStudentId(String studentId) {
        System.out.println("Setting student ID in AcademicInfo: " + studentId);
        this.studentId = studentId;
        loadSubjectDatabase();
    }

    private void loadSubjectDatabase() {
        System.out.println("Loading subject database...");
        subjectDatabase.clear(); // Clear existing data

        try (BufferedReader reader = new BufferedReader(new FileReader("AcademicSubjects.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String code = parts[0].trim();
                        String name = parts[1].trim();
                        subjectDatabase.put(code, new SubjectEntry(code, name));
                        System.out.println("Loaded subject: " + code + " - " + name);
                    }
                }
            }
            System.out.println("Successfully loaded " + subjectDatabase.size() + " subjects into database");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading subject database: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage stage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Academic Information");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TableView<SubjectEntry> subjectsTable = createSubjectsTable();
        loadStudentSubjects(subjectsTable);

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        closeButton.setOnAction(e -> stage.close());

        layout.getChildren().addAll(titleLabel, subjectsTable, closeButton);

        Scene scene = new Scene(layout, 600, 400);
        stage.setTitle("Academic Information - Student " + studentId);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<SubjectEntry> createSubjectsTable() {
        TableView<SubjectEntry> table = new TableView<>();

        TableColumn<SubjectEntry, String> codeCol = new TableColumn<>("Subject Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setPrefWidth(150);

        TableColumn<SubjectEntry, String> nameCol = new TableColumn<>("Subject Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(400);

        table.getColumns().addAll(codeCol, nameCol);
        return table;
    }

    private void loadStudentSubjects(TableView<SubjectEntry> table) {
        List<SubjectEntry> subjectList = new ArrayList<>();
        System.out.println("Looking for subjects for student ID: " + studentId);

        try {
            // First read the entire file content and filter out empty lines
            List<String> allLines = Files.readAllLines(Paths.get("UserData.txt"))
                    .stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());

            // Process lines in groups of 5 (email, id, password, subjects, clubs)
            for (int i = 0; i < allLines.size(); i += 5) {
                if (i + 3 >= allLines.size()) break;

                String id = allLines.get(i + 1).trim();

                if (id.equalsIgnoreCase(studentId)) {
                    String subjects = allLines.get(i + 3).trim();
                    System.out.println("Found matching student! Subjects line: " + subjects);

                    // Split and process subjects
                    String[] subjectCodes = subjects.split(",");
                    for (String code : subjectCodes) {
                        code = code.trim();
                        SubjectEntry entry = subjectDatabase.get(code);
                        if (entry != null) {
                            subjectList.add(entry);
                            System.out.println("Added subject: " + code + " - " + entry.getName());
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error reading UserData.txt: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load student data: " + e.getMessage());
        }

        if (subjectList.isEmpty()) {
            System.out.println("No subjects found for student ID: " + studentId);
            showAlert(Alert.AlertType.WARNING, "Warning",
                    "No subjects found for student ID: " + studentId);
        }

        table.setItems(FXCollections.observableArrayList(subjectList));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class SubjectEntry {
        private String code;
        private String name;

        public SubjectEntry(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public void setCode(String code) { this.code = code; }
        public void setName(String name) { this.name = name; }
    }
}
