package model;

public class LiftRide {

    private int skierId;
    private int liftId;
    private int resortId;
    private String season;
    private int day;
    private int time;
    private int vertical;

    public LiftRide(int skierId, int liftId, int resortId, String season, int day, int time, int vertical) {
        this.skierId = skierId;
        this.liftId = liftId;
        this.resortId = resortId;
        this.season = season;
        this.day = day;
        this.time = time;
        this.vertical = vertical;
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

    public String getSeason() {
        return season;
    }

    public int getDay() {
        return day;
    }

    public int getTime() {
        return time;
    }

    public int getVertical() {
        return vertical;
    }

    @Override
    public String toString() {
        return "skierId=" + skierId +
                ",liftId=" + liftId +
                ",resortId=" + resortId +
                ",season=" + season +
                ",day=" + day +
                ",time=" + time +
                ",vertical=" + vertical;
    }
}
