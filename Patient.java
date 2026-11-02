public class Patient implements Runnable {
    private int patientId;
    private Hospital hospital;

    // Constructor to initialize patient ID and hospital reference
    public Patient(int patientId, Hospital hospital) {
        this.patientId = patientId;
        this.hospital = hospital;
    }

    @Override
    public void run() {
        try {
            // 1. Patient arrives at the hospital
            arrive();

            // 2. Patient attempts to enter the waiting room
            boolean entered = hospital.enterWaitingRoom(this);

            // 3. If the waiting room is full, wait outside until a seat becomes available
            if (!entered) {
                waitOutside();
                hospital.waitForSeatAndEnter(this);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Patient " + patientId + " was interrupted.");
        }
    }

    // Print arrival message
    public void arrive() {
        System.out.println("P" + patientId + " arrived.");
    }

    
    // Print waiting outside message when the room is full
    public void waitOutside() {
        System.out.println("The waiting room is full.");
        System.out.println("P" + patientId + " is waiting outside for an available seat.");
    }

    // Get patient ID
    public int getPatientId() {
        return patientId;
    }
}
