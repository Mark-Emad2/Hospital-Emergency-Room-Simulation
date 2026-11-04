package src;

public class Semaphore {

    private int value;
    private int wakeups = 0;

    public Semaphore(int initial) {
        this.value = initial;
    }

    public synchronized void acquire() {
        value--;
        if (value < 0) {
            boolean interrupted = false;
            while (wakeups == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            wakeups--;
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized void release() {
        value++;
        if (value <= 0) {
            wakeups++;
            notify();
        }
    }

    public synchronized boolean tryAcquire(long timeoutMillis) {
        if (timeoutMillis < 0) timeoutMillis = 0;

        value--;
        if (value >= 0) {
            return true;   // permit was free
        }

        long deadline = System.currentTimeMillis() + timeoutMillis;
        boolean interrupted = false;

        while (wakeups == 0) {
            long timeLeft = deadline - System.currentTimeMillis();
            if (timeLeft <= 0) {
                value++;   // undo our decrement -- we never got the permit
                if (interrupted) Thread.currentThread().interrupt();
                return false;
            }
            try {
                wait(timeLeft);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        wakeups--;
        if (interrupted) Thread.currentThread().interrupt();
        return true;
    }
}