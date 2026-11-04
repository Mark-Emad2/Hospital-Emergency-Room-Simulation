package src;

import java.util.Random;

public class Doctor implements Runnable {
    int doctorID;
    Semaphore waitingRoom, treatmentRooms;
    Hospital hospital;
    private Random random = new Random();

    public Doctor(int doctorID, Hospital hospital) {
        this.doctorID = doctorID;
        this.hospital = hospital;
    }

    public void run() {

        while (hospital.getPatientsFinishedCount() < hospital.getTotalPatients()) {
            try {

                hospital.patientsAvailable.acquire();

                Patient patient = hospital.assignNextPatient(doctorID);
                if (patient == null) {
                    continue;
                }

                hospital.seats.release();
                System.out.println("Doctor " + doctorID + " is trying to acquire a treatment room");

                boolean acquired = hospital.treatmentRooms.tryAcquire(3000);

                if (!acquired) {
                    System.out.println("Doctor " + doctorID + " could not acquire a treatment room");
                    System.out.println("Doctor " + doctorID + " is sending P" + patient.getPatientId()
                            + " back to the waiting room");

                    hospital.reenterWaitingRoom(patient);
                    continue;
                }

                // Treatment room acquired -> begin treatment
                System.out.println("Doctor " + doctorID + " acquired a treatment room");
                System.out.println("Doctor " + doctorID + " begins treating P" + patient.getPatientId());

                Thread.sleep(1000 + random.nextInt(2000));

                System.out.println("Doctor " + doctorID + " finished treating P" + patient.getPatientId());
                System.out.println("Doctor " + doctorID + " released the treatment room");
                System.out.println("Doctor " + doctorID + " is now available.");
                hospital.treatmentRooms.release();// FIX: always print, and BEFORE "Pn left"

                System.out.println("P" + patient.getPatientId() + " left.");
                hospital.incrementPatientsFinished();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}