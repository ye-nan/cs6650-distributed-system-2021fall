package dal;

public class LiftRide {

    private int skierId;
    private int liftId;
    private int resortId;
    private int day;
    private int time;

    public LiftRide(int skierId, int liftId, int resortId, int day, int time) {
        this.skierId = skierId;
        this.liftId = liftId;
        this.resortId = resortId;
        this.day = day;
        this.time = time;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getLiftId() {
        return liftId;
    }

    public int getResortId() {
        return resortId;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "skierId=" + skierId +
                ",liftId=" + liftId +
                ",resortId=" + resortId +
                ",day=" + day +
                ",time=" + time;
    }
}
