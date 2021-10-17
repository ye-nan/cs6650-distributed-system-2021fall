package model;

public class SkierVertical {

    private int skierId;
    private int resortId;
    private String seasonId;
    private int vertical;

    public SkierVertical(int skierId, int resortId, String seasonId, int vertical) {
        this.skierId = skierId;
        this.resortId = resortId;
        this.seasonId = seasonId;
        this.vertical = vertical;
    }

    public int getSkierId() {
        return skierId;
    }

    public int getResortId() {
        return resortId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public int getVertical() {
        return vertical;
    }
}
