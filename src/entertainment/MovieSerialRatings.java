package entertainment;

import lombok.Getter;

@Getter
public final class MovieSerialRatings {
    private final String name;
    private final Double rating;

    public MovieSerialRatings(final String name, final Double rating) {
        this.name = name;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return name;
    }
}
