package dal;

public class LiftRide {

    private int skierId;
    private int liftId;
    private String season;
    private int day;
    private int vertical;

    public LiftRide(int skierId, int liftId, String season, int day, int vertical) {
        this.skierId = skierId;
        this.liftId = liftId;
        this.season = season;
        this.day = day;
        this.vertical = vertical;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getLiftId() {
        return liftId;
    }

    public String getSeason() {
        return season;
    }

    public int getDay() {
        return day;
    }

    public int getVertical() {
        return vertical;
    }

    @Override
    public String toString() {
        return "skierId=" + skierId +
                ",liftId=" + liftId +
                ",season=" + season +
                ",day=" + day +
                ",vertical=" + vertical;
    }
}
