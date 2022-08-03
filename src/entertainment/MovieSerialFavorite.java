package entertainment;

import lombok.Getter;

@Getter
public final class MovieSerialFavorite {
    private final String name;
    private final Integer apCount;

    public MovieSerialFavorite(final String name, final Integer apCount) {
        this.name = name;
        this.apCount = apCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
