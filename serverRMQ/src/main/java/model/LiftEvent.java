package model;

public class LiftEvent {

    private int liftId;
    private int time;

    public LiftEvent(int liftId, int time) {
        this.liftId = liftId;
        this.time = time;
    }

    public int getLiftId() {
        return liftId;
    }

    public int getTime() {
        return time;
    }
}
