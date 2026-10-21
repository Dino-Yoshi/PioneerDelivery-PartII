package edu.pioneer.suite;

public enum Task {
    A("Closest Delivery Locations"),
    B("Counting Dispatch-Order Inversions"),
    C("Selecting a Delivery-Time Threshold");

    private final String title;

    Task(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
