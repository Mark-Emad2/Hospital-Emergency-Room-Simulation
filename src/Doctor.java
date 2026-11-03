package src;

import java.util.Random;

public class Doctor implements Runnable {
    int doctorID;
    Semaphore waitingRoom , treatmentRooms;
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
                Patient patient=hospital.getNextPatientFromQueue();
                if(patient!=null){
                    continue;
                }
                IO.println("Doctor " + doctorID + " is assigned to P" + patient.getPatientId() );
                IO.println("Doctor " + doctorID + " is trying to acquire a treatment room");

                boolean acquired = hospital.treatmentRooms.tryAcquire(3000);
                if(!acquired){
                    IO.println("Doctor " + doctorID + " could not acquire a treatment room");
                    IO.println("Doctor " + doctorID + " is sending P" + patient.getPatientId() + " back to the waiting room");
                    hospital.addPatientToQueue(patient);
                    continue;

                }else{
                    IO.println("Doctor " + doctorID + " acquired a treatment room");
                    IO.println("Doctor " + doctorID + " begins treating P" + patient.getPatientId());

                }
                Thread.sleep(1000 + random.nextInt(2000));
                IO.println("Doctor " + doctorID + " finished treating P" + patient.getPatientId());
                hospital.treatmentRooms.release();
                IO.println("Doctor " + doctorID + " released the treatment room");
                hospital.seats.release();
                IO.println("P" + patient.getPatientId() + " left.");
                hospital.incrementPatientsFinished();
                IO.println("Doctor " + doctorID + " is now available.");

            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
