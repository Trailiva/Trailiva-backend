package com.trailiva.data.model;


public enum Priority {
    HIGH("high"), MEDIUM("medium"), LOW("low");

    private final String priority;

    Priority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public static Priority fetchPriority(String priority){
        for (Priority item : Priority.values()){
            if(item.getPriority().toString().equalsIgnoreCase(priority))
                return item;
        }
        return Priority.LOW;
    }
}
