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

    // Read user inputs for capacity, doctors, and patients[cite: 2]
    public void readInput() {
        Scanner input = new Scanner(System.in);

        System.out.print("Waiting room capacity: ");
        waitingRoomCapacity = input.nextInt();

        System.out.print("Number of doctors: ");
        numberOfDoctors = input.nextInt();

        System.out.print("Number of patients: ");
        numberOfPatients = input.nextInt();
    }

    // Initialize shared resources like waiting room and treatment rooms[cite: 3]
    public void initializeHospital() {
        waitingRoom = new Semaphore(waitingRoomCapacity);
        treatmentRooms = new Semaphore(2); // Exactly 2 treatment rooms exist[cite: 1]
    }

    // Create patient threads and pass the hospital reference
    public void createPatients() {
        for (int i = 1; i <= numberOfPatients; i++) {
            patients.add(new Patient(i, this));
        }
    }

    // Create doctor threads[cite: 2]
    public void createDoctors() {
        for (int i = 1; i <= numberOfDoctors; i++) {
            doctors.add(new Doctor(i, waitingRoom, treatmentRooms));
        }
    }

    // Try to acquire a seat in the waiting room
    public boolean enterWaitingRoom(Patient patient) {
        return waitingRoom.tryAcquire(); 
    }

    // Block and wait until a seat is available, then enter
    public void waitForSeatAndEnter(Patient patient) throws InterruptedException {
        waitingRoom.acquire();
        System.out.println("P" + patient.getPatientId() + " entered the waiting room.");
    }

    // Start running all patient and doctor threads
    public void startSimulation() {
        for (Patient patient : patients) {
            new Thread(patient).start();
        }

        for (Doctor doctor : doctors) {
            new Thread(doctor).start();
        }
    }

    // Main execution flow for the simulation
    public void runSimulation() {
        readInput();
        initializeHospital();
        createPatients();
        createDoctors();
        startSimulation();
    }
}
