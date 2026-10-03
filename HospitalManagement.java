package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HospitalManagement extends Application {

    // =========================================================
    // DATA
    // =========================================================

    public static Doctor[] doctors = new Doctor[25];
    public static Patient[] patients = new Patient[100];
    public static Medical[] medicals = new Medical[100];
    public static Lab[] laboratories = new Lab[20];
    public static Facility[] facilities = new Facility[20];
    public static Staff[] staffs = new Staff[100];

    private static int doctorCount = 0;
    private static int patientCount = 0;
    private static int medicalCount = 0;
    private static int labCount = 0;
    private static int facilityCount = 0;
    private static int staffCount = 0;

    private static final String DOCTOR_FILE = "doctors.txt";
    private static final String PATIENT_FILE = "patients.txt";
    private static final String MEDICAL_FILE = "medicals.txt";
    private static final String LAB_FILE = "labs.txt";
    private static final String FACILITY_FILE = "facilities.txt";
    private static final String STAFF_FILE = "staffs.txt";

    private Stage primaryStage;


    // =========================================================
    // COLOURS
    // =========================================================

    private static final String BG = "#F4F8FA";
    private static final String CARD = "#FFFFFF";
    private static final String PRIMARY = "#1F6E8C";
    private static final String PRIMARY_DARK = "#155A73";
    private static final String TEXT = "#243746";
    private static final String MUTED = "#6B7C89";
    private static final String BORDER = "#D9E4EA";

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        if (dataFilesExist()) {
            loadAllData();
        } else {
            initializeData();
            saveAllData();
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Hospital Management System");
        primaryStage.setOnCloseRequest(e -> saveAllData());
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(680);

        showMainMenu();

        primaryStage.show();
    }

    // =========================================================
    // MAIN MENU
    // =========================================================

    public void showMainMenu() {

        BorderPane root = createRoot();

        root.setTop(createHeader(
                "Hospital Management System",
                "Manage hospital records in one place."
        ));

        GridPane menu = new GridPane();
        menu.setAlignment(Pos.CENTER);
        menu.setHgap(18);
        menu.setVgap(18);

        Button doctorButton =
                createMenuButton("Doctors", "Doctor records and schedules");

        Button patientButton =
                createMenuButton("Patients", "Patient admission records");

        Button medicalButton =
                createMenuButton("Medical", "Medicine and stock records");

        Button labButton =
                createMenuButton("Laboratories", "Laboratory services and costs");

        Button facilityButton =
                createMenuButton("Facilities", "Hospital facility records");

        Button staffButton =
                createMenuButton("Staff", "Hospital staff records");

        doctorButton.setOnAction(e -> showDoctorPage());
        patientButton.setOnAction(e -> showPatientPage());
        medicalButton.setOnAction(e -> showMedicalPage());
        labButton.setOnAction(e -> showLabPage());
        facilityButton.setOnAction(e -> showFacilityPage());
        staffButton.setOnAction(e -> showStaffPage());

        menu.add(doctorButton, 0, 0);
        menu.add(patientButton, 1, 0);
        menu.add(medicalButton, 0, 1);
        menu.add(labButton, 1, 1);
        menu.add(facilityButton, 0, 2);
        menu.add(staffButton, 1, 2);

        root.setCenter(menu);

        Button exitButton = createSecondaryButton("Exit System");
        exitButton.setOnAction(e -> primaryStage.close());

        HBox footer = new HBox(exitButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        root.setBottom(footer);

        primaryStage.setScene(new Scene(root, 1080, 720));
    }

    // =========================================================
    // DOCTOR
    // =========================================================

    public void showDoctorPage() {

        TableView<Doctor> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getDoctorList()));

        addStringColumn(table, "ID", Doctor::getId, 90);
        addStringColumn(table, "Name", Doctor::getName, 180);
        addStringColumn(table, "Specialist", Doctor::getSpecialist, 160);
        addStringColumn(table, "Work Time", Doctor::getWorkTime, 130);
        addStringColumn(table, "Qualification", Doctor::getQualification, 170);
        addIntegerColumn(table, "Room", Doctor::getRoom, 90);

        showTablePage(
                "Doctor Management",
                doctorCount + " doctor record(s)",
                table,
                "Add Doctor",
                e -> showAddDoctorForm()
        );
    }

    public void showAddDoctorForm() {

        TextField id = createField("e.g. 004");
        TextField name = createField("e.g. Dr James Chao");
        TextField specialist = createField("e.g. Dermatologist");
        TextField workTime = createField("e.g. 8am-4pm");
        TextField qualification = createField("e.g. MBBS, MD");
        TextField room = createField("e.g. 10");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Doctor ID", id);
        addFormRow(form, 1, "Name", name);
        addFormRow(form, 2, "Specialist", specialist);
        addFormRow(form, 3, "Work Time", workTime);
        addFormRow(form, 4, "Qualification", qualification);
        addFormRow(form, 5, "Room No.", room);

        Button save = createPrimaryButton("Save Doctor");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (hasEmpty(id, name, specialist, workTime, qualification, room)) {
                showError("Missing Information", "Please fill in all doctor fields.");
                return;
            }
if (doctorIdExists(id.getText().trim())) {
                showError("Duplicate ID", "This doctor ID already exists.");
                return;
            }

            try {
                int roomNo = Integer.parseInt(room.getText().trim());

                if (roomNo <= 0) {
                    showError("Invalid Room", "Room number must be greater than 0.");
                    return;
                }

                if (doctorCount >= doctors.length) {
                    showError("Doctor Limit Reached",
                            "The hospital can only store " + doctors.length + " doctors.");
                    return;
                }

                doctors[doctorCount++] = new Doctor(
                        id.getText().trim(),
                        name.getText().trim(),
                        specialist.getText().trim(),
                        workTime.getText().trim(),
                        qualification.getText().trim(),
                        roomNo
                );

                saveAllData();

                showSuccess("Doctor Added", "Doctor record added successfully.");

                // Rebuilds the page using the updated array.
                showDoctorPage();

            } catch (NumberFormatException ex) {
                showError("Invalid Room", "Room number must be a whole number.");
            }
        });

        cancel.setOnAction(e -> showDoctorPage());

        showFormPage(
                "Add New Doctor",
                "Enter the doctor's information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // PATIENT
    // =========================================================

    public void showPatientPage() {

        TableView<Patient> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getPatientList()));
        
        addStringColumn(
        	    table,
        	    "ID",
        	    patient -> patient.getId(),
        	    100
        	);

        	addStringColumn(
        	    table,
        	    "Name",
        	    patient -> patient.getName(),
        	    180
        	);

        	addStringColumn(
        	    table,
        	    "Disease",
        	    patient -> patient.getDisease(),
        	    170
        	);

        	addStringColumn(
        	    table,
        	    "Sex",
        	    patient -> patient.getSex(),
        	    100
        	);

        	addStringColumn(
        	    table,
        	    "Admit Status",
        	    patient -> patient.getAdmitStatus(),
        	    150
        	);

        	addIntegerColumn(
        	    table,
        	    "Age",
        	    patient -> patient.getAge(),
        	    80
        	);


        showTablePage(
                "Patient Management",
                patientCount + " patient record(s)",
                table,
                "Add Patient",
                e -> showAddPatientForm()
        );
    }

    public void showAddPatientForm() {

        TextField id = createField("e.g. P006");
        TextField name = createField("e.g. Ahmad");
        TextField disease = createField("e.g. Fever");
        TextField age = createField("e.g. 25");

        ComboBox<String> sex = createComboBox("Male", "Female");
        ComboBox<String> admitStatus =
                createComboBox("Admitted", "Not Admitted");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Patient ID", id);
        addFormRow(form, 1, "Name", name);
        addFormRow(form, 2, "Disease", disease);
        addFormRow(form, 3, "Sex", sex);
        addFormRow(form, 4, "Admit Status", admitStatus);
        addFormRow(form, 5, "Age", age);

        Button save = createPrimaryButton("Save Patient");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (id.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || disease.getText().trim().isEmpty()
                    || age.getText().trim().isEmpty()
                    || sex.getValue() == null
                    || admitStatus.getValue() == null) {

                showError("Missing Information", "Please fill in all patient fields.");
                return;
            }
if (patientIdExists(id.getText().trim())) {
                showError("Duplicate ID", "This patient ID already exists.");
                return;
            }

            try {
                int patientAge = Integer.parseInt(age.getText().trim());

                if (patientAge <= 0 || patientAge > 120) {
                    showError("Invalid Age", "Please enter an age between 1 and 120.");
                    return;
                }

                if (patientCount >= patients.length) {
                    showError("Patient Limit Reached",
                            "The hospital can only store " + patients.length + " patients.");
                    return;
                }

                patients[patientCount++] = new Patient(
                        id.getText().trim(),
                        name.getText().trim(),
                        disease.getText().trim(),
                        sex.getValue(),
                        admitStatus.getValue(),
                        patientAge
                );

                saveAllData();

                showSuccess("Patient Added", "Patient record added successfully.");
                showPatientPage();

            } catch (NumberFormatException ex) {
                showError("Invalid Age", "Age must be a whole number.");
            }
        });

        cancel.setOnAction(e -> showPatientPage());

        showFormPage(
                "Add New Patient",
                "Enter the patient's information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // MEDICAL
    // =========================================================

    public void showMedicalPage() {

        TableView<Medical> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getMedicalList()));

        addStringColumn(table, "Medicine", Medical::getName, 190);
        addStringColumn(table, "Manufacturer", Medical::getManufacturer, 190);
        addStringColumn(table, "Expiry Date", Medical::getExpiryDate, 150);
        addIntegerColumn(table, "Cost (RM)", Medical::getCost, 110);
        addIntegerColumn(table, "Quantity", Medical::getCount, 100);

        showTablePage(
                "Medical Management",
                medicalCount + " medical record(s)",
                table,
                "Add Medical",
                e -> showAddMedicalForm()
        );
    }

    public void showAddMedicalForm() {

        TextField name = createField("e.g. Panadol");
        TextField manufacturer = createField("e.g. GSK");

        DatePicker expiryDate = new DatePicker();
        styleInput(expiryDate);

        TextField cost = createField("e.g. 10");
        TextField count = createField("e.g. 50");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Medicine Name", name);
        addFormRow(form, 1, "Manufacturer", manufacturer);
        addFormRow(form, 2, "Expiry Date", expiryDate);
        addFormRow(form, 3, "Cost (RM)", cost);
        addFormRow(form, 4, "Quantity", count);

        Button save = createPrimaryButton("Save Medical");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (name.getText().trim().isEmpty()
                    || manufacturer.getText().trim().isEmpty()
                    || expiryDate.getValue() == null
                    || cost.getText().trim().isEmpty()
                    || count.getText().trim().isEmpty()) {

                showError("Missing Information", "Please fill in all medical fields.");
                return;
            }
try {
                int medicineCost = Integer.parseInt(cost.getText().trim());
                int quantity = Integer.parseInt(count.getText().trim());

                if (medicineCost < 0 || quantity < 0) {
                    showError("Invalid Value", "Cost and quantity cannot be negative.");
                    return;
                }

                if (medicalCount >= medicals.length) {
                    showError("Medical Limit Reached",
                            "The hospital can only store " + medicals.length + " medical records.");
                    return;
                }

                medicals[medicalCount++] = new Medical(
                        name.getText().trim(),
                        manufacturer.getText().trim(),
                        expiryDate.getValue().toString(),
                        medicineCost,
                        quantity
                );

                saveAllData();

                showSuccess("Medical Added", "Medical record added successfully.");
                showMedicalPage();

            } catch (NumberFormatException ex) {
                showError("Invalid Value", "Cost and quantity must be whole numbers.");
            }
        });

        cancel.setOnAction(e -> showMedicalPage());

        showFormPage(
                "Add New Medical",
                "Enter the medicine information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // LABORATORY
    // =========================================================

    public void showLabPage() {

        TableView<Lab> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getLabList()));

        addStringColumn(table, "Laboratory", Lab::getLab, 400);
        addIntegerColumn(table, "Cost (RM)", Lab::getCost, 150);

        showTablePage(
                "Laboratory Management",
                labCount + " laboratory record(s)",
                table,
                "Add Laboratory",
                e -> showAddLabForm()
        );
    }

    public void showAddLabForm() {

        TextField lab = createField("e.g. Blood Test Lab");
        TextField cost = createField("e.g. 50");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Laboratory", lab);
        addFormRow(form, 1, "Cost (RM)", cost);

        Button save = createPrimaryButton("Save Laboratory");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (hasEmpty(lab, cost)) {
                showError("Missing Information", "Please fill in all laboratory fields.");
                return;
            }
try {
                int labCost = Integer.parseInt(cost.getText().trim());

                if (labCost < 0) {
                    showError("Invalid Cost", "Cost cannot be negative.");
                    return;
                }

                if (labCount >= laboratories.length) {
                    showError("Laboratory Limit Reached",
                            "The hospital can only store " + laboratories.length + " laboratories.");
                    return;
                }

                laboratories[labCount++] = new Lab(
                        lab.getText().trim(),
                        labCost
                );

                saveAllData();

                showSuccess("Laboratory Added", "Laboratory record added successfully.");
                showLabPage();

            } catch (NumberFormatException ex) {
                showError("Invalid Cost", "Cost must be a whole number.");
            }
        });

        cancel.setOnAction(e -> showLabPage());

        showFormPage(
                "Add New Laboratory",
                "Enter the laboratory information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // FACILITY
    // =========================================================

    public void showFacilityPage() {

        TableView<Facility> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getFacilityList()));

        addStringColumn(
                table,
                "Facility",
                (Facility facility) -> facility.getFacility(),
                600.0
            );

        showTablePage(
                "Facility Management",
                facilityCount + " facility record(s)",
                table,
                "Add Facility",
                e -> showAddFacilityForm()
        );
    }

    public void showAddFacilityForm() {

        TextField facility = createField("e.g. Emergency Room");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Facility", facility);

        Button save = createPrimaryButton("Save Facility");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (facility.getText().trim().isEmpty()) {
                showError("Missing Information", "Please enter the facility name.");
                return;
            }
if (facilityCount >= facilities.length) {
                showError("Facility Limit Reached",
                        "The hospital can only store " + facilities.length + " facilities.");
                return;
            }

            facilities[facilityCount++] = new Facility(facility.getText().trim());

            saveAllData();

            showSuccess("Facility Added", "Facility record added successfully.");
            showFacilityPage();
        });

        cancel.setOnAction(e -> showFacilityPage());

        showFormPage(
                "Add New Facility",
                "Enter the facility information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // STAFF
    // =========================================================

    public void showStaffPage() {

        TableView<Staff> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(getStaffList()));

        addStringColumn(table, "ID", Staff::getId, 100);
        addStringColumn(table, "Name", Staff::getName, 180);
        addStringColumn(table, "Designation", Staff::getDesignation, 180);
        addStringColumn(table, "Sex", Staff::getSex, 100);
        addIntegerColumn(table, "Salary (RM)", Staff::getSalary, 140);

        showTablePage(
                "Staff Management",
                staffCount + " staff record(s)",
                table,
                "Add Staff",
                e -> showAddStaffForm()
        );
    }

    public void showAddStaffForm() {

        TextField id = createField("e.g. S006");
        TextField name = createField("e.g. Nur Aisyah");
        TextField designation = createField("e.g. Nurse");
        ComboBox<String> sex = createComboBox("Male", "Female");
        TextField salary = createField("e.g. 3500");

        GridPane form = createFormGrid();
        addFormRow(form, 0, "Staff ID", id);
        addFormRow(form, 1, "Name", name);
        addFormRow(form, 2, "Designation", designation);
        addFormRow(form, 3, "Sex", sex);
        addFormRow(form, 4, "Salary (RM)", salary);

        Button save = createPrimaryButton("Save Staff");
        Button cancel = createSecondaryButton("Cancel");

        save.setOnAction(e -> {

            if (id.getText().trim().isEmpty()
                    || name.getText().trim().isEmpty()
                    || designation.getText().trim().isEmpty()
                    || sex.getValue() == null
                    || salary.getText().trim().isEmpty()) {

                showError("Missing Information", "Please fill in all staff fields.");
                return;
            }
if (staffIdExists(id.getText().trim())) {
                showError("Duplicate ID", "This staff ID already exists.");
                return;
            }

            try {
                int staffSalary = Integer.parseInt(salary.getText().trim());

                if (staffSalary < 0) {
                    showError("Invalid Salary", "Salary cannot be negative.");
                    return;
                }

                if (staffCount >= staffs.length) {
                    showError("Staff Limit Reached",
                            "The hospital can only store " + staffs.length + " staff records.");
                    return;
                }

                staffs[staffCount++] = new Staff(
                        id.getText().trim(),
                        name.getText().trim(),
                        designation.getText().trim(),
                        sex.getValue(),
                        staffSalary
                );

                saveAllData();

                showSuccess("Staff Added", "Staff record added successfully.");
                showStaffPage();

            } catch (NumberFormatException ex) {
                showError("Invalid Salary", "Salary must be a whole number.");
            }
        });

        cancel.setOnAction(e -> showStaffPage());

        showFormPage(
                "Add New Staff",
                "Enter the staff information below.",
                form,
                cancel,
                save
        );
    }

    // =========================================================
    // TABLE PAGE
    // =========================================================

    private <T> void showTablePage(
            String title,
            String subtitle,
            TableView<T> table,
            String addButtonText,
            javafx.event.EventHandler<javafx.event.ActionEvent> addAction) {

        BorderPane root = createRoot();
        root.setTop(createHeader(title, subtitle));

        table.setPlaceholder(new Label("No records found."));
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 14px;"
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        Button back = createSecondaryButton("Back");
        Button add = createPrimaryButton(addButtonText);

        back.setOnAction(e -> showMainMenu());
        add.setOnAction(addAction);

        HBox actions = new HBox(12, back, add);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(18, table, actions);
        card.setPadding(new Insets(20));
        card.setStyle(cardStyle());

        root.setCenter(card);
        BorderPane.setMargin(card, new Insets(20, 0, 0, 0));

        primaryStage.setScene(new Scene(root, 1080, 720));
    }

    // =========================================================
    // FORM PAGE
    // =========================================================

    private void showFormPage(
            String title,
            String subtitle,
            GridPane form,
            Button cancel,
            Button save) {

        BorderPane root = createRoot();
        root.setTop(createHeader(title, subtitle));

        HBox actions = new HBox(12, cancel, save);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox card = new VBox(25, form, actions);
        card.setPadding(new Insets(28));
        card.setMaxWidth(650);
        card.setStyle(cardStyle());

        VBox center = new VBox(card);
        center.setAlignment(Pos.CENTER);

        root.setCenter(center);
        BorderPane.setMargin(center, new Insets(20, 0, 0, 0));

        primaryStage.setScene(new Scene(root, 1080, 720));
    }

    // =========================================================
    // TABLE COLUMN HELPERS
    // =========================================================

    private <T> void addStringColumn(
            TableView<T> table,
            String title,
            Function<T, String> getter,
            double width) {

        TableColumn<T, String> column = new TableColumn<>(title);

        column.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(
                        getter.apply(data.getValue())
                )
        );

        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private <T> void addIntegerColumn(
            TableView<T> table,
            String title,
            Function<T, Integer> getter,
            double width) {

        TableColumn<T, Integer> column = new TableColumn<>(title);

        column.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(
                        getter.apply(data.getValue())
                )
        );

        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private Doctor[] getDoctorList() {
        Doctor[] result = new Doctor[doctorCount];
        System.arraycopy(doctors, 0, result, 0, doctorCount);
        return result;
    }

    private Patient[] getPatientList() {
        Patient[] result = new Patient[patientCount];
        System.arraycopy(patients, 0, result, 0, patientCount);
        return result;
    }

    private Medical[] getMedicalList() {
        Medical[] result = new Medical[medicalCount];
        System.arraycopy(medicals, 0, result, 0, medicalCount);
        return result;
    }

    private Lab[] getLabList() {
        Lab[] result = new Lab[labCount];
        System.arraycopy(laboratories, 0, result, 0, labCount);
        return result;
    }

    private Facility[] getFacilityList() {
        Facility[] result = new Facility[facilityCount];
        System.arraycopy(facilities, 0, result, 0, facilityCount);
        return result;
    }

    private Staff[] getStaffList() {
        Staff[] result = new Staff[staffCount];
        System.arraycopy(staffs, 0, result, 0, staffCount);
        return result;
    }

    // =========================================================
    // VALIDATION HELPERS
    // =========================================================

    private boolean doctorIdExists(String id) {
        for (int i = 0; i < doctorCount; i++) {
            if (doctors[i].getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean patientIdExists(String id) {
        for (int i = 0; i < patientCount; i++) {
            if (patients[i].getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean staffIdExists(String id) {
        for (int i = 0; i < staffCount; i++) {
            if (staffs[i].getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private BorderPane createRoot() {

        BorderPane root = new BorderPane();

        root.setPadding(new Insets(30));

        root.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        return root;
    }

    private VBox createHeader(String titleText, String subtitleText) {

        Label title = new Label(titleText);

        title.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT + ";"
        );

        Label subtitle = new Label(subtitleText);

        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-text-fill: " + MUTED + ";"
        );

        String time = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        );

        Label dateTime = new Label("Updated: " + time);

        dateTime.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: " + MUTED + ";"
        );

        return new VBox(5, title, subtitle, dateTime);
    }

    private Button createMenuButton(String title, String subtitle) {

        Button button = new Button(
                title + "\n" + subtitle
        );

        button.setPrefSize(320, 95);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setWrapText(true);

        String normalStyle =
                "-fx-background-color: " + CARD + ";" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 18;" +
                "-fx-cursor: hand;";

        String hoverStyle =
                "-fx-background-color: #EAF4F7;" +
                "-fx-text-fill: " + PRIMARY_DARK + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: " + PRIMARY + ";" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 18;" +
                "-fx-cursor: hand;";

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));

        return button;
    }

    private Button createPrimaryButton(String text) {

        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    private Button createSecondaryButton(String text) {

        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: " + PRIMARY + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 18;" +
                "-fx-cursor: hand;"
        );

        return button;
    }

    private TextField createField(String prompt) {

        TextField field = new TextField();

        field.setPromptText(prompt);
        styleInput(field);

        return field;
    }

    private ComboBox<String> createComboBox(String... items) {

        ComboBox<String> box = new ComboBox<>();

        box.getItems().addAll(items);
        box.setPromptText("Select");
        styleInput(box);

        return box;
    }

    private void styleInput(javafx.scene.control.Control control) {

        control.setPrefWidth(360);

        control.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 9;" +
                "-fx-font-size: 14px;"
        );
    }

    private GridPane createFormGrid() {

        GridPane grid = new GridPane();

        grid.setHgap(20);
        grid.setVgap(16);
        grid.setAlignment(Pos.CENTER_LEFT);

        return grid;
    }

    private void addFormRow(
            GridPane grid,
            int row,
            String labelText,
            Node input) {

        Label label = new Label(labelText);

        label.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + TEXT + ";"
        );

        grid.add(label, 0, row);
        grid.add(input, 1, row);
    }

    private String cardStyle() {

        return
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;";
    }

    // =========================================================
    // ALERTS
    // =========================================================

    private void showSuccess(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void showError(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // =========================================================
    // TEXT FILE STORAGE
    // =========================================================

    private static boolean dataFilesExist() {
        return new File(DOCTOR_FILE).exists()
                || new File(PATIENT_FILE).exists()
                || new File(MEDICAL_FILE).exists()
                || new File(LAB_FILE).exists()
                || new File(FACILITY_FILE).exists()
                || new File(STAFF_FILE).exists();
    }

    public static void saveAllData() {
        saveDoctors();
        savePatients();
        saveMedicals();
        saveLabs();
        saveFacilities();
        saveStaffs();
    }

    private static void saveDoctors() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DOCTOR_FILE))) {
            for (int i = 0; i < doctorCount; i++) {
                Doctor d = doctors[i];
                writer.write(escape(d.getId()) + "\t"
                        + escape(d.getName()) + "\t"
                        + escape(d.getSpecialist()) + "\t"
                        + escape(d.getWorkTime()) + "\t"
                        + escape(d.getQualification()) + "\t"
                        + d.getRoom());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save doctors: " + e.getMessage());
        }
    }

    private static void savePatients() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATIENT_FILE))) {
            for (int i = 0; i < patientCount; i++) {
                Patient p = patients[i];
                writer.write(escape(p.getId()) + "\t"
                        + escape(p.getName()) + "\t"
                        + escape(p.getDisease()) + "\t"
                        + escape(p.getSex()) + "\t"
                        + escape(p.getAdmitStatus()) + "\t"
                        + p.getAge());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save patients: " + e.getMessage());
        }
    }

    private static void saveMedicals() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MEDICAL_FILE))) {
            for (int i = 0; i < medicalCount; i++) {
                Medical m = medicals[i];
                writer.write(escape(m.getName()) + "\t"
                        + escape(m.getManufacturer()) + "\t"
                        + escape(m.getExpiryDate()) + "\t"
                        + m.getCost() + "\t"
                        + m.getCount());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save medicals: " + e.getMessage());
        }
    }

    private static void saveLabs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LAB_FILE))) {
            for (int i = 0; i < labCount; i++) {
                Lab lab = laboratories[i];
                writer.write(escape(lab.getLab()) + "\t" + lab.getCost());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save laboratories: " + e.getMessage());
        }
    }

    private static void saveFacilities() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FACILITY_FILE))) {
            for (int i = 0; i < facilityCount; i++) {
                writer.write(escape(facilities[i].getFacility()));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save facilities: " + e.getMessage());
        }
    }

    private static void saveStaffs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STAFF_FILE))) {
            for (int i = 0; i < staffCount; i++) {
                Staff s = staffs[i];
                writer.write(escape(s.getId()) + "\t"
                        + escape(s.getName()) + "\t"
                        + escape(s.getDesignation()) + "\t"
                        + escape(s.getSex()) + "\t"
                        + s.getSalary());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Unable to save staffs: " + e.getMessage());
        }
    }

    public static void loadAllData() {
        doctorCount = 0;
        patientCount = 0;
        medicalCount = 0;
        labCount = 0;
        facilityCount = 0;
        staffCount = 0;

        loadDoctors();
        loadPatients();
        loadMedicals();
        loadLabs();
        loadFacilities();
        loadStaffs();
    }

    private static void loadDoctors() {
        File file = new File(DOCTOR_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && doctorCount < doctors.length) {
                String[] data = line.split("\t", -1);
                if (data.length == 6) {
                    doctors[doctorCount++] = new Doctor(
                            unescape(data[0]), unescape(data[1]),
                            unescape(data[2]), unescape(data[3]),
                            unescape(data[4]), Integer.parseInt(data[5]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Unable to load doctors: " + e.getMessage());
        }
    }

    private static void loadPatients() {
        File file = new File(PATIENT_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && patientCount < patients.length) {
                String[] data = line.split("\t", -1);
                if (data.length == 6) {
                    patients[patientCount++] = new Patient(
                            unescape(data[0]), unescape(data[1]),
                            unescape(data[2]), unescape(data[3]),
                            unescape(data[4]), Integer.parseInt(data[5]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Unable to load patients: " + e.getMessage());
        }
    }

    private static void loadMedicals() {
        File file = new File(MEDICAL_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && medicalCount < medicals.length) {
                String[] data = line.split("\t", -1);
                if (data.length == 5) {
                    medicals[medicalCount++] = new Medical(
                            unescape(data[0]), unescape(data[1]),
                            unescape(data[2]),
                            Integer.parseInt(data[3]),
                            Integer.parseInt(data[4]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Unable to load medicals: " + e.getMessage());
        }
    }

    private static void loadLabs() {
        File file = new File(LAB_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && labCount < laboratories.length) {
                String[] data = line.split("\t", -1);
                if (data.length == 2) {
                    laboratories[labCount++] =
                            new Lab(unescape(data[0]), Integer.parseInt(data[1]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Unable to load laboratories: " + e.getMessage());
        }
    }

    private static void loadFacilities() {
        File file = new File(FACILITY_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && facilityCount < facilities.length) {
                facilities[facilityCount++] = new Facility(unescape(line));
            }
        } catch (IOException e) {
            System.err.println("Unable to load facilities: " + e.getMessage());
        }
    }

    private static void loadStaffs() {
        File file = new File(STAFF_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && staffCount < staffs.length) {
                String[] data = line.split("\t", -1);
                if (data.length == 5) {
                    staffs[staffCount++] = new Staff(
                            unescape(data[0]), unescape(data[1]),
                            unescape(data[2]), unescape(data[3]),
                            Integer.parseInt(data[4]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Unable to load staffs: " + e.getMessage());
        }
    }

    private static String escape(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String unescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (escaped) {
                if (ch == 't') result.append('\t');
                else if (ch == 'n') result.append('\n');
                else if (ch == 'r') result.append('\r');
                else result.append(ch);

                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                result.append(ch);
            }
        }

        if (escaped) result.append('\\');

        return result.toString();
    }

    // =========================================================
    // INITIAL DATA
    // =========================================================

    public static void initializeData() {

        doctorCount = 0;
        patientCount = 0;
        medicalCount = 0;
        labCount = 0;
        facilityCount = 0;
        staffCount = 0;

        // Doctors
        doctors[doctorCount++] = new Doctor(
                "001", "Dr Tom Wong", "Surgeon",
                "8-11am", "MBBS,MD", 11
        );

        doctors[doctorCount++] = new Doctor(
                "002", "Dr John Lim", "Physician",
                "10-3pm", "MBBS,MS", 45
        );

        doctors[doctorCount++] = new Doctor(
                "003", "Dr Amy Chia", "Surgeon",
                "7-11am", "MBBS,MD", 8
        );

        doctors[doctorCount++] = new Doctor(
                "004", "Dr Sarah Lee", "Cardiologist",
                "9am-4pm", "MBBS,MD", 12
        );

        doctors[doctorCount++] = new Doctor(
                "005", "Dr Amir Tan", "Paediatrician",
                "8am-2pm", "MBBS,MRCP", 15
        );

        // Patients
        patients[patientCount++] = new Patient(
                "P001", "Ali", "Diarrhoea",
                "Male", "Admitted", 32
        );

        patients[patientCount++] = new Patient(
                "P002", "Sarah", "Rash",
                "Female", "Admitted", 24
        );

        patients[patientCount++] = new Patient(
                "P003", "Daniel", "Fever",
                "Male", "Not Admitted", 19
        );

        patients[patientCount++] = new Patient(
                "P004", "Mei Ling", "Asthma",
                "Female", "Admitted", 28
        );

        patients[patientCount++] = new Patient(
                "P005", "Kumar", "Migraine",
                "Male", "Not Admitted", 35
        );

        // Medical
        medicals[medicalCount++] = new Medical(
                "Panadol", "GSK",
                "2027-12-01", 10, 50
        );

        medicals[medicalCount++] = new Medical(
                "Paracetamol", "Pharmaniaga",
                "2028-03-15", 8, 70
        );

        medicals[medicalCount++] = new Medical(
                "Amoxicillin", "Duopharma",
                "2027-09-20", 20, 30
        );

        medicals[medicalCount++] = new Medical(
                "Cough Syrup", "Kotra Pharma",
                "2028-01-10", 15, 40
        );

        medicals[medicalCount++] = new Medical(
                "Antacid", "Xepa",
                "2027-11-05", 12, 35
        );

        // Laboratories
        laboratories[labCount++] = new Lab("Blood Test Lab", 50);
        laboratories[labCount++] = new Lab("X-Ray Lab", 100);
        laboratories[labCount++] = new Lab("MRI Lab", 300);
        laboratories[labCount++] = new Lab("Pathology Lab", 80);
        laboratories[labCount++] = new Lab("Ultrasound Lab", 150);

        // Facilities
        facilities[facilityCount++] = new Facility("Emergency Room");
        facilities[facilityCount++] = new Facility("ICU");
        facilities[facilityCount++] = new Facility("Pharmacy");
        facilities[facilityCount++] = new Facility("Operation Theatre");
        facilities[facilityCount++] = new Facility("Radiology Department");

        // Staff
        staffs[staffCount++] = new Staff(
                "S001", "Adam",
                "Nurse", "Male", 3500
        );

        staffs[staffCount++] = new Staff(
                "S002", "Mary",
                "Receptionist", "Female", 3000
        );

        staffs[staffCount++] = new Staff(
                "S003", "David",
                "Administrator", "Male", 4500
        );

        staffs[staffCount++] = new Staff(
                "S004", "Aina",
                "Pharmacist", "Female", 4200
        );

        staffs[staffCount++] = new Staff(
                "S005", "Jason",
                "Technician", "Male", 3800
        );
    }


}
