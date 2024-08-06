package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HospitalManagementSystem {
    private static final Logger logger = Logger.getLogger(HospitalManagementSystem.class.getName());
    private static final String url = "jdbc:mysql://localhost:3306/Hospital";
    private static final String username = "root";
    private static final String password = "root";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 5:
                        viewAppointments(connection);
                        break;
                    case 6:
                        checkTotalAmountCollected(connection, scanner);
                        break;
                    case 7:
                        System.out.println("THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter a valid choice!");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQL Exception occurred", e);
        }
    }

    private static void displayMenu() {
        System.out.println("+------------------------------+");
        System.out.println("| HOSPITAL MANAGEMENT SYSTEM   |");
        System.out.println("+------------------------------+");
        System.out.println("| 1. Add Patient               |");
        System.out.println("| 2. View Patients             |");
        System.out.println("| 3. View Doctors              |");
        System.out.println("| 4. Book Appointment          |");
        System.out.println("| 5. View Appointments         |");
        System.out.println("| 6. Total Amount Collected    |");
        System.out.println("| 7. Exit                      |");
        System.out.println("+------------------------------+");
        System.out.print("Enter your choice: ");
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                double fee = getFeeForDoctor(doctorId, connection);
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date, fee) VALUES(?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    preparedStatement.setDouble(4, fee);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                int appointmentId = generatedKeys.getInt(1);
                                recordBilling(appointmentId, appointmentDate, fee, connection);
                            }
                        }
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed to Book Appointment!");
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error while booking appointment", e);
                }
            } else {
                System.out.println("Doctor not available on this date!");
            }
        } else {
            System.out.println("Either doctor or patient doesn't exist!");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while checking doctor availability", e);
        }
        return false;
    }

    private static double getFeeForDoctor(int doctorId, Connection connection) {
        String query = "SELECT specialization FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String specialization = resultSet.getString("specialization");
                    Doctor doctor = new Doctor(connection);
                    return doctor.getFeeForSpecialization(specialization);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while retrieving fee for doctor", e);
        }
        return 0;
    }

    private static void recordBilling(int appointmentId, String incomeDate, double amount, Connection connection) {
        String billingQuery = "INSERT INTO billing(appointment_id, income_date, amount) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(billingQuery)) {
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.setString(2, incomeDate);
            preparedStatement.setDouble(3, amount);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Billing Record Added!");
            } else {
                System.out.println("Failed to Add Billing Record!");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while recording billing", e);
        }
    }

    private static void viewAppointments(Connection connection) {
        String query = "SELECT a.id AS appointment_id, p.name AS patient_name, d.name AS doctor_name, a.appointment_date, a.fee " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Appointments: ");
            System.out.println("+---------------------------------------------------------------------------------------+");
            System.out.println("| Appointment Id | Patient Name       | Doctor Name        | Appointment Date | Fee      |");
            System.out.println("+---------------------------------------------------------------------------------------+");
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String patientName = resultSet.getString("patient_name");
                String doctorName = resultSet.getString("doctor_name");
                String appointmentDate = resultSet.getString("appointment_date");
                double fee = resultSet.getDouble("fee");
                System.out.printf("| %-14d | %-18s | %-18s | %-17s | %-6.2f |\n", appointmentId, patientName, doctorName, appointmentDate, fee);
                System.out.println("+---------------------------------------------------------------------------------------+");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while viewing appointments", e);
        }
    }

    private static void checkTotalAmountCollected(Connection connection, Scanner scanner) {
        System.out.print("Enter the date (YYYY-MM-DD) to check total amount collected: ");
        String date = scanner.next();

        String query = "SELECT SUM(amount) AS total_amount FROM billing WHERE income_date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, date);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double totalAmount = resultSet.getDouble("total_amount");
                    if (totalAmount > 0) {
                        System.out.printf("Total amount collected on %s: %.2f\n", date, totalAmount);
                    } else {
                        System.out.println("No billing records found for the given date.");
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while checking total amount collected", e);
        }
    }
}
