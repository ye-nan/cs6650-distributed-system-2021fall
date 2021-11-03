package model;

import java.util.List;

public class Resort {

    private int id;
    private String name;
    private List<String> seasons;

    public Resort(int id, String name, List<String> seasons) {
        this.id = id;
        this.name = name;
        this.seasons = seasons;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return name;
    }

    public List<String> getSeasons() {
        return seasons;
    }
}
