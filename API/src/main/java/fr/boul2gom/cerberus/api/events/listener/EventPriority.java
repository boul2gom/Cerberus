package fr.boul2gom.cerberus.api.events.listener;

public enum EventPriority {

    HIGH(1),
    NORMAL(2),
    LOW(3);

    private final int weight;

    EventPriority(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
