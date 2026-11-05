package src;

import java.util.Random;

public class Doctor implements Runnable {
    int doctorID;
    Hospital hospital;
    private Random random = new Random();

    public Doctor(int doctorID, Hospital hospital) {
        this.doctorID = doctorID;
        this.hospital = hospital;
    }

    public void run() {
        WaitingRoom waitingRoom = hospital.getWaitingRoom();
        TreatmentRoom treatmentRoom = hospital.getTreatmentRoom();

        while (hospital.getPatientsFinishedCount() < hospital.getTotalPatients()) {
            try {
                waitingRoom.patientsAvailable.acquire();

                Patient patient = waitingRoom.assignNextPatient(doctorID);
                if (patient == null) {
                    continue;
                }

                System.out.println("Doctor " + doctorID + " is trying to acquire a treatment room");

                boolean acquired = treatmentRoom.tryAcquire(3000);

                if (!acquired) {
                    System.out.println("Doctor " + doctorID + " could not acquire a treatment room");
                    System.out.println("Doctor " + doctorID + " is sending P" + patient.getPatientId()
                            + " back to the waiting room");

                    waitingRoom.reenterWaitingRoom(patient);
                    continue;
                }

                System.out.println("Doctor " + doctorID + " acquired a treatment room");
                System.out.println("Doctor " + doctorID + " begins treating P" + patient.getPatientId());
                waitingRoom.releaseSeat();

                Thread.sleep(2000 + random.nextInt(3000));

                System.out.println("Doctor " + doctorID + " finished treating P" + patient.getPatientId());
                System.out.println("Doctor " + doctorID + " released the treatment room");
                System.out.println("Doctor " + doctorID + " is now available.");
                treatmentRoom.release();

                System.out.println("P" + patient.getPatientId() + " left.");
                hospital.incrementPatientsFinished();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}