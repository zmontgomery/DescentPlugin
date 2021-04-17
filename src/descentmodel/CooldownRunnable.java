package descentmodel;
/**
 * UNUSED CLASS
 * COOLDOWN MODEL THREAD
 */
public class CooldownRunnable implements Runnable{

    private double startTime;
    
    public CooldownRunnable(){
        this.startTime = System.currentTimeMillis();
    }

    public double getStartTime() {
        return this.startTime;
    }

    public double getSecondsSinceStart(){
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    @Override
    public void run() {
        while(getSecondsSinceStart() < 5){
                System.out.println(getSecondsSinceStart());
        }
    }
}
