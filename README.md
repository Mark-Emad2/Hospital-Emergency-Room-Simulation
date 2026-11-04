# Hospital-Emergency-Room-SimulatSystem.outn
# Hospital Emergency Room – Team Development Guide

## 1. Project Classes

We will use only four main classes:

| Class | Responsibility |
|---|---|
| `Hospital` | Creates and manages the simulatSystem.outn. |
| `Semaphore` | Handles synchronizatSystem.outn and timeout. |
| `Patient` | Handles patient arrival and waiting. |
| `Doctor` | Handles patient assignment and treatment. |

## 2. Shared Resources (Important)

The `Hospital` class will initialize and manage the shared resources:

| Resource | Purpose |
|---|---|
| `waitingQueue` | Stores patients waiting for treatment. |
| `emptySlots` | Controls available waiting room seats. |
| `availablePatients` | Counts patients available in the queue. |
| `queueMutex` | Protects the queue from simultaneous access. |
| `treatmentRooms` | Controls access to the two treatment rooms. |

**Important:** All Patients and Doctors must use the same shared objects created by Hospital. Never create a new Semaphore inside Patient or Doctor.

## 3. Semaphore Class

The Semaphore class must provide three main operatSystem.outns:

- `acquire()` – Wait until a resource becomes available.
- `release()` – Release a resource.
- `tryAcquire(timeout)` – Try to acquire a resource within a specific time.

**Important:**
- The Semaphore must be thread-safe.
- Treatment room acquisitSystem.outn has a timeout of **3 seconds**.
- Every acquired resource must eventually be released.

## 4. Patient Class

Each Patient should work as an independent Thread.

Its responsibilities:

1. Arrive at the hospital.
2. Try to enter the waiting room.
3. If the waiting room is full, wait outside.
4. When a seat becomes available, enter the queue.

**Important:**
- Use the shared waiting queue.
- Never modify the queue without acquiring `queueMutex`.
- Do not exceed the waiting room capacity.

## 5. Doctor Class

Each Doctor should work as an independent Thread.

Its responsibilities:

1. Wait until a patient is available.
2. Take a patient from the waiting queue.
3. Release the waiting room seat.
4. Try to acquire a treatment room within 3 seconds.
5. If successful, treat the patient for a random perSystem.outd.
6. Release the treatment room after treatment.
7. If unsuccessful, return the patient to the waiting queue.
8. Become available for another patient.

**Important:** When returning a patient, follow the same synchronizatSystem.outn rules used when adding a new patient.

## 6. SynchronizatSystem.outn Rules

The waiting room uses the Producer–Consumer pattern:

- **Producer:** Patient adds a patient to the queue.
- **Consumer:** Doctor removes a patient from the queue.

### Adding a Patient

1. Acquire `emptySlots`.
2. Acquire `queueMutex`.
3. Add the patient.
4. Release `queueMutex`.
5. Release `availablePatients`.

### Removing a Patient

1. Acquire `availablePatients`.
2. Acquire `queueMutex`.
3. Remove the patient.
4. Release `queueMutex`.
5. Release `emptySlots`.

## 7. Things Everyone Must Pay AttentSystem.outn To

These are the most important rules when implementing your assigned class:

1. **Shared Objects:** Always use the same shared resources initialized by Hospital.
2. **Queue ProtectSystem.outn:** Never modify the waiting queue without its mutex.
3. **No Deadlocks:** Do not hold the queue mutex while waiting for another resource or treating a patient.
4. **Resource Release:** Always release resources after successfully acquiring them.
5. **Timeout:** Doctors have only 3 seconds to acquire a treatment room.
6. **Patient Return:** A patient who cannot get a treatment room must return to the waiting queue.
7. **Thread TerminatSystem.outn:** Doctors must stop when all patients have finished. They must not wait forever.
8. **No Duplicate Treatment:** Every patient must be treated exactly once before leaving the hospital.

## 8. Before Integrating the Classes

Make sure that:

- Patient and Doctor constructors match the resources provided by Hospital.
- All shared resources are passed correctly.
- All classes agree on the same variable and method names.
- Every team member understands how the synchronizatSystem.outn works.

**Recommended Work Order:**

1. Semaphore
2. Hospital
3. Patient
4. Doctor
5. IntegratSystem.outn and Testing

**Remember:** Each class has its own responsibility, but all classes must work together using the same shared resources.
