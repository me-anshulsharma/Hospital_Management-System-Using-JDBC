package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Doctor {
    private static final Logger logger = Logger.getLogger(Doctor.class.getName());
    private final Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Doctors: ");
            System.out.println("+----------------------------------------------------+");
            System.out.println("| Doctor Id  | Name               | Specialization   |");
            System.out.println("+----------------------------------------------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("| %-10s | %-18s | %-16s |\n", id, name, specialization);
                System.out.println("+----------------------------------------------------+");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while viewing doctors", e);
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while fetching doctor by ID", e);
        }
        return false;
    }

    public double getFeeForSpecialization(String specialization) {
        return switch (specialization.toLowerCase()) {
            case "cardiologist" -> 500;
            case "neurologist" -> 450;
            case "orthopedic", "urologist" -> 400;
            case "gynecologist", "dermatologist" -> 350;
            case "pediatrician" -> 300;
            case "ent specialist" -> 375;
            case "general surgeon" -> 325;
            case "ophthalmologist" -> 425;
            default -> 300; // Default fee for unspecified specializations
        };
    }

}
