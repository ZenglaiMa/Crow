package com.happier.crow.entities;

public class Result {

    private Location location;
    private int precise;
    private int confidence;
    private int comprehension;
    private String level;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getPrecise() {
        return precise;
    }

    public void setPrecise(int precise) {
        this.precise = precise;
    }

    public int getConfidence() {
        return confidence;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    public int getComprehension() {
        return comprehension;
    }

    public void setComprehension(int comprehension) {
        this.comprehension = comprehension;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Result{" +
                "location=" + location +
                ", precise=" + precise +
                ", confidence=" + confidence +
                ", comprehension=" + comprehension +
                ", level='" + level + '\'' +
                '}';
    }
}
