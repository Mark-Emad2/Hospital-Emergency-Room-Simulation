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

        if (!hospital.seats.tryAcquire(0)) {
            waitOutside();
            hospital.seats.acquire();   // blocks until a seat is free
        }
        enterWaitingRoom();

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