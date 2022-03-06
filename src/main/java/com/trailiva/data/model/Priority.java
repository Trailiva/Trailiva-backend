package com.trailiva.data.model;


public enum Priority {
    HIGHEST("highest"), HIGH("high"), MEDIUM("medium"), LOW("low");

    private String code;

    Priority(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Priority fetchPriority(String code) {
       for (Priority priority : Priority.values()) {
           if (priority.getCode().equalsIgnoreCase(code))
               return priority;
       }
       return null;
    }
}
