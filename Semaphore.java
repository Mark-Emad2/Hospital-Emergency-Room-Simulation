public class Semaphore {

    // like the lab: negative value = number of waiting threads
    private int value;

    // wake-up tickets handed out by V(), so a waiter never wakes by mistake
    private int wakeups = 0;

    public Semaphore(int initial) {
        this.value = initial;
    }

    // wait(): take a permit, block if none are available
    public synchronized void P() {
        value--;
        if (value < 0) {
            boolean interrupted = false;
            while (wakeups == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    interrupted = true; // keep waiting, restore flag later
                }
            }
            wakeups--;
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // signal(): give a permit back, wake one waiter if there is any
    public synchronized void V() {
        value++;
        if (value <= 0) {
            wakeups++;
            notify();
        }
    }

    // custom method: try to take a permit, give up after timeoutMillis
    // returns true if we got it, false if time ran out
    public synchronized boolean tryP(long timeoutMillis) {
        value--;
        if (value >= 0) {
            return true; // permit was free, no waiting needed
        }

        long deadline = System.currentTimeMillis() + timeoutMillis;
        boolean interrupted = false;

        while (wakeups == 0) {
            long timeLeft = deadline - System.currentTimeMillis();
            if (timeLeft <= 0) {
                value++; // timed out: undo our decrement, we're no longer waiting
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                return false;
            }
            try {
                wait(timeLeft);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        wakeups--;
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return true;
    }
}
