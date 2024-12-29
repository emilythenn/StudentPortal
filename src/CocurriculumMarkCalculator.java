import java.io.*;
import java.util.*;

public class CocurriculumMarkCalculator {
    // Maps to store data from files
    private static Map<String, List<String>> studentClubs = new HashMap<>();
    private static Map<String, String[]> studentPositions = new HashMap<>();
    private static Map<String, String> activityLogs = new HashMap<>();
    private static Map<String, String> clubSocieties = new HashMap<>();

    // Marks data for positions, activity levels, and achievements
    private static final Map<String, Integer> positionMarks = Map.of(
            "President", 10,
            "Vice President", 9,
            "Secretary", 9,
            "Treasurer", 9,
            "Vice Secretary", 8,
            "Vice Treasurer", 8,
            "Committee", 7,
            "Active Member", 6
    );

    private static final Map<String, Integer> activityLevelMarks = Map.of(
            "International", 20,
            "National", 15,
            "State", 12,
            "School", 10
    );

    private static final Map<String, Integer> achievementLevelMarks = Map.of(
            "Gold", 20,
            "Silver", 19,
            "Bronze", 18,
            "Participation", 0
    );

    public static void main(String[] args) {
        loadData();
        calculateAndGenerateTranscript("s100201");
        System.out.println("\n");
        calculateAndGenerateTranscript("s100202");
    }

    public static void loadData() {
        try {
            // Load UserData.txt
            BufferedReader userDataReader = new BufferedReader(new FileReader("UserData.txt"));
            String line;
            String currentStudent = null;

            while ((line = userDataReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                if (line.contains("@")) {
                    currentStudent = line.split("@")[0];
                } else if (line.contains(",")) {
                    studentClubs.put(currentStudent, Arrays.asList(line.split(",")));
                }
            }
            userDataReader.close();

            // Load ClubSocieties.txt
            BufferedReader clubSocietiesReader = new BufferedReader(new FileReader("ClubSocieties.txt"));
            while ((line = clubSocietiesReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.split(",");
                if (parts.length == 2) {
                    clubSocieties.put(parts[0], parts[1]);
                }
            }
            clubSocietiesReader.close();

            // Load StudentPositions.txt
            BufferedReader studentPositionsReader = new BufferedReader(new FileReader("StudentPositions.txt"));
            while ((line = studentPositionsReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    studentPositions.put(parts[0], new String[]{parts[1], parts[2], parts[3]});
                }
            }
            studentPositionsReader.close();

            // Load ActivitiesLog.txt
            BufferedReader activitiesLogReader = new BufferedReader(new FileReader("ActivitiesLog.txt"));
            while ((line = activitiesLogReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                String[] parts = line.replace("\"", "").split(",");
                if (parts.length == 5) {
                    String key = parts[0] + "," + parts[1];
                    String value = String.join(",", Arrays.copyOfRange(parts, 2, 5));
                    activityLogs.put(key, value);
                }
            }
            activitiesLogReader.close();

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }

    public static void calculateAndGenerateTranscript(String studentId) {
        List<String> clubs = studentClubs.get(studentId);
        if (clubs == null) {
            System.out.println("No data found for student " + studentId);
            return;
        }

        System.out.println("Co-curriculum Transcript for " + studentId);
        System.out.println("============================================================================");

        List<Integer> clubMarks = new ArrayList<>();

        for (String clubCode : clubs) {
            String clubName = clubSocieties.get(clubCode);
            System.out.printf("[%s - %s]\n", clubCode, clubName);

            // Calculate attendance (assumed full)
            System.out.println("Attendance: assume full ------------> 50/50 marks");

            // Calculate position marks
            String[] positions = studentPositions.get(studentId);
            String position = "";
            if (clubCode.startsWith("P")) position = positions[0];
            else if (clubCode.startsWith("B")) position = positions[1];
            else if (clubCode.startsWith("S")) position = positions[2];

            int positionMark = positionMarks.getOrDefault(position, 0);
            System.out.printf("Position: %s ------------> %d/10 marks\n", position, positionMark);

            // Get activity details
            String activityKey = studentId + "," + clubCode;
            String activityData = activityLogs.get(activityKey);

            String activityName = "None";
            String activityLevel = "None";
            String achievement = "None";
            int levelMarks = 0;
            int achievementMarks = 0;

            if (activityData != null) {
                String[] parts = activityData.split(",");
                activityName = parts[0];
                activityLevel = parts[1];
                achievement = parts[2];
                levelMarks = activityLevelMarks.getOrDefault(activityLevel, 0);
                achievementMarks = achievementLevelMarks.getOrDefault(achievement, 0);
            }

            System.out.println("Selected Activity: " + activityName);
            System.out.printf("Level of Activities: %s ------> %d/20 marks\n", activityLevel, levelMarks);
            System.out.printf("Achievement Level: %s ------------> %d/20 marks\n", achievement, achievementMarks);

            System.out.println("============================================================================");

            int total = 50 + positionMark + levelMarks + achievementMarks;
            clubMarks.add(total);
            System.out.printf("TOTAL: %d/100 marks\n", total);
            System.out.println("============================================================================");
        }

        // Calculate final mark (average of two highest marks)
        Collections.sort(clubMarks, Collections.reverseOrder());
        double finalMark = (clubMarks.get(0) + clubMarks.get(1)) / 2.0;
        System.out.printf("FINAL MARKS: %.1f marks\n", finalMark);
    }
}