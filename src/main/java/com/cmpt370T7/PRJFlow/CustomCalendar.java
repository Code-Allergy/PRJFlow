package com.cmpt370T7.PRJFlow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CustomCalendar extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(CustomCalendar.class);
    private YearMonth currentYearMonth;
    private GridPane calendarGrid;
    private Label monthYearLabel;
    private final Map<LocalDate, List<String>> remindersMap;
    private ListView<String> remindersList;
    private LocalDate selectedDate;

    public CustomCalendar(Map<LocalDate, List<String>> remindersMap) {
        this.remindersMap = remindersMap;
        this.currentYearMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        createCalendarView();
    }

    private void createCalendarView() {
        // Month navigation
        HBox monthNavigation = new HBox(10);
        monthNavigation.setAlignment(Pos.CENTER); // Center-align the controls
        Button prevMonthButton = new Button("<");
        Button nextMonthButton = new Button(">");
        monthYearLabel = new Label();
        monthYearLabel.setFont(new Font("Arial", 16));
        updateMonthYearLabel();

        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        monthNavigation.getChildren().addAll(prevMonthButton, monthYearLabel, nextMonthButton);

        // Calendar grid
        calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setPadding(new Insets(10));

        // Reminders section
        HBox remindersHeader = new HBox(5);
        remindersHeader.setPadding(new Insets(5, 0, 5, 0));
        Label remindersLabel = new Label("Reminders");
        remindersLabel.setFont(new Font("Arial", 14)); // Increase font size
        Button addReminderButton = new Button("+");

        // Spacer to push the add button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        remindersHeader.getChildren().addAll(remindersLabel, spacer, addReminderButton);

        remindersList = new ListView<>();

        addReminderButton.setOnAction(e -> showAddReminderDialog());

        // Allow deletion of reminders
        remindersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to delete
                String selectedItem = remindersList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Reminder");
                    alert.setHeaderText("Delete this reminder?");
                    alert.setContentText(selectedItem);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        remindersMap.get(selectedDate).remove(selectedItem);
                        if (remindersMap.get(selectedDate).isEmpty()) {
                            remindersMap.remove(selectedDate);
                        }
                        updateReminders();
                        updateCalendar();
                    }
                }
            }
        });

        this.getChildren().addAll(monthNavigation, calendarGrid, remindersHeader, remindersList);
        this.setPadding(new Insets(10));

        updateCalendar();
        updateReminders();
    }

    void updateCalendar() {
        calendarGrid.getChildren().clear();

        // Weekday labels
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(new Font("Arial", 12));
            dayLabel.setStyle("-fx-font-weight: bold");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekOfFirst = firstDayOfMonth.getDayOfWeek().getValue(); // 1 (Monday) to 7 (Sunday)
        int col = (dayOfWeekOfFirst - 1) % 7; // Adjust for 0-based index

        int daysInMonth = currentYearMonth.lengthOfMonth();
        int row = 1;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));

            // Highlight today's date
            if (date.equals(LocalDate.now())) {
                dayButton.setStyle("-fx-background-color: #ADD8E6;"); // Light blue
            }

            // Highlight selected date
            if (date.equals(selectedDate)) {
                dayButton.setStyle("-fx-background-color: #90EE90;"); // Light green
            }

            dayButton.setOnAction(e -> {
                selectedDate = date;
                updateCalendar();
                updateReminders();
            });

            calendarGrid.add(dayButton, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        updateMonthYearLabel();
    }

    private void updateMonthYearLabel() {
        String month = currentYearMonth.getMonth().toString().substring(0, 1).toUpperCase()
                + currentYearMonth.getMonth().toString().substring(1).toLowerCase();
        monthYearLabel.setText(month + " " + currentYearMonth.getYear());
    }

    void updateReminders() {
        logger.info("Loading reminders for {}", selectedDate);
        List<String> reminders = remindersMap.getOrDefault(selectedDate, new ArrayList<>());
        remindersList.getItems().setAll(reminders);
    }

    private void showAddReminderDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Reminder");
        dialog.setHeaderText("Add a reminder for " + selectedDate.toString());
        dialog.setContentText("Reminder:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reminder -> {
            // Add the reminder to the map
            remindersMap.computeIfAbsent(selectedDate, k -> new ArrayList<>()).add(reminder);
            AppDataManager.getInstance().getConfigManager().setReminderMap(remindersMap);
            // Update the reminders list
            updateReminders();
            // Update the calendar to reflect the new reminder
            updateCalendar();
            logger.info("Added reminder for {}: {}", selectedDate, reminder);
        });
    }
}
