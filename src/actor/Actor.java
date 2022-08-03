package actor;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Map;
@Getter

public final class Actor {
    private final String name;
    private final String careerDescription;
    private final ArrayList<String> filmography;
    private final Map<ActorsAwards, Integer> awards;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
    }

    /**
     * Used for determining the total number of
     * awards for an Actor
     * @return the total number of Awards
     */
    public int totalAwards() {
        int totalAwards = 0;
        for (ActorsAwards award : ActorsAwards.values()) {
            if (this.getAwards().containsKey(award)) {
                totalAwards += this.getAwards().get(award);
            }
        }

        return totalAwards;
    }

    @Override
    public String toString() {
        return name;
    }
}
