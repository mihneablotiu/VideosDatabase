package actor;

import lombok.Getter;

@Getter
public final class AwardActor {
    private final String name;
    private final Integer totalAwards;

    public AwardActor(final String name, final Integer totalAwards) {
        this.name = name;
        this.totalAwards = totalAwards;
    }

    @Override
    public String toString() {
        return name;
    }
}
