
import java.util.ArrayList;
import java.util.Scanner;

public class Hospital {

    private int waitingRoomCapacity;
    private int numberOfDoctors;
    private int numberOfPatients;

    private ArrayList<Patient> patients;
    private ArrayList<Doctor> doctors;

    private Semaphore waitingRoom;
    private Semaphore treatmentRooms;

    public Hospital() {

        patients = new ArrayList<>();
        doctors = new ArrayList<>();

    }

    // Read user input
    public void readInput() {

        Scanner input = new Scanner(System.in);

        System.out.print("Waiting room capacity: ");
        waitingRoomCapacity = input.nextInt();

        System.out.print("Number of doctors: ");
        numberOfDoctors = input.nextInt();

        System.out.print("Number of patients: ");
        numberOfPatients = input.nextInt();

    }

    // Initialize shared resources
    public void initializeHospital() {

        waitingRoom = new Semaphore(waitingRoomCapacity);

        treatmentRooms = new Semaphore(2);

    }

    // Create patients
    public void createPatients() {

        for (int i = 1; i <= numberOfPatients; i++) {

            patients.add(new Patient(i, waitingRoom));

        }

    }

    // Create doctors
    public void createDoctors() {

        for (int i = 1; i <= numberOfDoctors; i++) {

            doctors.add(new Doctor(i, waitingRoom, treatmentRooms));

        }

    }

    // Start simulation
    public void startSimulation() {

        for (Patient patient : patients) {
            patient.start();
        }

        for (Doctor doctor : doctors) {
            doctor.start();
        }

    }

    public void run() {

        readInput();

        initializeHospital();

        createPatients();

        createDoctors();

        startSimulation();

    }

}
