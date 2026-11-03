package src;

    public class Patient implements Runnable {
    private int patientId;
    private Hospital hospital;

    public Patient(int patientId, Hospital hospital) {
        this.patientId = patientId;
        this.hospital = hospital;
    }

    @Override
    public void run() {
            arrive();

            while (true) {
                if (hospital.seats.tryAcquire(0)) { // Non-blocking check for a seat
                    enterWaitingRoom();
                    break;
                } else {
                    waitOutside();
                    // Wait until a seat becomes available (blocks on seats semaphore)
                    hospital.seats.acquire(); 
                    enterWaitingRoom();
                    break;
                }
            }
    }

    public void arrive() {
        System.out.println("P" + patientId + " arrived.");
    }

    public void enterWaitingRoom() {
        System.out.println("P" + patientId + " entered the waiting room.");
        hospital.addPatientToQueue(this);
    }

    public void waitOutside() {
        System.out.println("The waiting room is full.");
        System.out.println("P" + patientId + " is waiting outside for an available seat.");
    }

    public int getPatientId() {
        return patientId;
    }
}
