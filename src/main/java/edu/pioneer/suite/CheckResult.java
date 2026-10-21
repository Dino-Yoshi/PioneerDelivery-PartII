package edu.pioneer.suite;

public record CheckResult(String id, String description, String expected, String actual, boolean passed) {
}
