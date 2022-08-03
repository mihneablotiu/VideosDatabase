package actor;

import lombok.Getter;

@Getter
public final class AverageActor {
    private final String name;
    private final Double average;

    public AverageActor(final String name, final Double average) {
        this.name = name;
        this.average = average;
    }

    @Override
    public String toString() {
        return name;
    }
}
