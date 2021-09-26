package model;

public class LiftRide {

    private int skierId;
    private int liftId;
    private int resortId;
    private String seasonId;
    private String dayId;
    private int time;

    public LiftRide(int skierId, int liftId, int resortId, String seasonId, String dayId, int time) {
        this.skierId = skierId;
        this.liftId = liftId;
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.dayId = dayId;
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

    public String getSeasonId() {
        return seasonId;
    }

    public String getDayId() {
        return dayId;
    }

    public int getTime() {
        return time;
    }
}
