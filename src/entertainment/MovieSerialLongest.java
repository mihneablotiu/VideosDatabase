package entertainment;

import lombok.Getter;

@Getter
public final class MovieSerialLongest {
    private final String name;
    private final int duration;

    public MovieSerialLongest(final String name, final int duration) {
        this.name = name;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return name;
    }
}
