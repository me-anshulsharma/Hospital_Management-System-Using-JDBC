# Hospital Management System

## Overview

The `HospitalManagementSystem` Java program is designed to manage hospital operations, including adding patients, viewing patient and doctor information, booking appointments, and tracking billing. The program uses a MySQL database to store and retrieve data and interacts with the user through a command-line interface.

## Features

1. **Add Patient**: Allows the user to add new patient information to the database.
2. **View Patients**: Displays a list of all patients stored in the database.
3. **View Doctors**: Displays a list of all doctors stored in the database.
4. **Book Appointment**: Books an appointment between a patient and a doctor, checking for the doctor’s availability and recording billing information.
5. **View Appointments**: Lists all scheduled appointments.
6. **Total Amount Collected**: Calculates the total amount collected from appointments on a specific date.
7. **Exit**: Exits the application.

## Classes and Methods

### `HospitalManagementSystem`
- **Main Class**: Manages the main workflow of the application.
- **Methods**:
  - `main(String[] args)`: Initializes the JDBC driver and sets up the database connection. Contains the main loop for user interaction.
  - `displayMenu()`: Displays the main menu options to the user.
  - `bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner)`: Books an appointment, checking patient and doctor existence and availability, and records billing.
  - `checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection)`: Checks if a doctor is available on a given date.
  - `getFeeForDoctor(int doctorId, Connection connection)`: Retrieves the consultation fee for a doctor based on their specialization.
  - `recordBilling(int appointmentId, String incomeDate, double amount, Connection connection)`: Records the billing information for an appointment.
  - `viewAppointments(Connection connection)`: Displays all scheduled appointments.
  - `checkTotalAmountCollected(Connection connection, Scanner scanner)`: Calculates the total amount collected on a specific date.

### `Doctor`
- **Attributes**:
  - `connection`: Database connection object.
- **Methods**:
  - `Doctor(Connection connection)`: Constructor that initializes the database connection.
  - `viewDoctors()`: Displays all doctors stored in the database.
  - `getDoctorById(int id)`: Checks if a doctor exists in the database by their ID.
  - `getFeeForSpecialization(String specialization)`: Returns the consultation fee based on the doctor's specialization.

### `Patient`
- **Attributes**:
  - `connection`: Database connection object.
  - `scanner`: Scanner object for user input.
- **Methods**:
  - `Patient(Connection connection, Scanner scanner)`: Constructor that initializes the database connection and scanner.
  - `addPatient()`: Adds a new patient to the database.
  - `viewPatients()`: Displays all patients stored in the database.
  - `getPatientById(int id)`: Checks if a patient exists in the database by their ID.

## Database Schema

### `patients` Table
- `id` (INT, Primary Key): Unique identifier for each patient.
- `name` (VARCHAR): Name of the patient.
- `age` (INT): Age of the patient.
- `gender` (VARCHAR): Gender of the patient.

### `doctors` Table
- `id` (INT, Primary Key): Unique identifier for each doctor.
- `name` (VARCHAR): Name of the doctor.
- `specialization` (VARCHAR): Specialization of the doctor.

### `appointments` Table
- `id` (INT, Primary Key): Unique identifier for each appointment.
- `patient_id` (INT, Foreign Key): ID of the patient.
- `doctor_id` (INT, Foreign Key): ID of the doctor.
- `appointment_date` (DATE): Date of the appointment.
- `fee` (DOUBLE): Consultation fee for the appointment.

### `billing` Table
- `appointment_id` (INT, Foreign Key): ID of the appointment.
- `income_date` (DATE): Date when the fee was recorded.
- `amount` (DOUBLE): Amount collected for the appointment.

## Usage

1. Run the program.
2. Select options from the menu to perform various operations such as adding patients, viewing patient and doctor details, booking appointments, and checking total amounts collected.

## Dependencies

- MySQL JDBC Driver
- MySQL Database

## Error Handling

- The program uses logging to handle and record errors, ensuring that exceptions are caught and logged appropriately for troubleshooting.

This program is a basic implementation of a hospital management system designed to demonstrate the integration of Java with a MySQL database and to provide a simple interface for managing hospital operations.
