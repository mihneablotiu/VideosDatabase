package user;

import lombok.Getter;

@Getter
public final class ActiveUser {
    private final String username;
    private final int numberRatings;

    public ActiveUser(final String username, final int numberRatings) {
        this.username = username;
        this.numberRatings = numberRatings;
    }

    @Override
    public String toString() {
        return username;
    }
}
