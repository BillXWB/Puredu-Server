package edu.pure.server.model;

import org.jetbrains.annotations.NotNull;

public enum CourseName {
    CHINESE,
    ENGLISH,
    MATH,
    PHYSICS,
    CHEMISTRY,
    BIOLOGY,
    HISTORY,
    GEOGRAPHY,
    POLITICS;

    private static final String GEOGRAPHY_ = "geo";

    public static CourseName fromOpedukg(final @NotNull String courseName) {
        if (courseName.equals(CourseName.GEOGRAPHY_)) {
            return CourseName.GEOGRAPHY;
        }
        return CourseName.valueOf(courseName.toUpperCase());
    }

    public String toOpedukg() {
        return CourseName.toOpedukg(this);
    }

    private static String toOpedukg(final CourseName courseName) {
        if (courseName == CourseName.GEOGRAPHY) {
            return CourseName.GEOGRAPHY_;
        }
        return courseName.toString().toLowerCase();
    }
}
