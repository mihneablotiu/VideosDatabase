package entertainment;

import lombok.Getter;
import user.User;

import java.util.ArrayList;
@Getter

public final class Movie {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private final int duration;
    private final ArrayList<Double> ratings = new ArrayList<>();

    public Movie(final String title, final int year,
                 final ArrayList<String> cast,
                 final ArrayList<String> genres, final int duration) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.duration = duration;
    }

    /**
     * Used for determining the average rating
     * of a movie
     * @return the average rating
     */
    public Double avgRatings() {
        if (this.ratings.size() == 0) {
            return 0d;
        }

        double sum = 0d;
        for (Double rating : this.ratings) {
            sum += rating;
        }
        return sum / this.ratings.size();
    }

    /**
     * Used for determining how many times a movie
     * appears as favorite in all the user lists
     * @param userList the user list
     * @return number of appears as favorite
     */
    public int numberFavorite(final ArrayList<User> userList) {
        int number = 0;
        for (User currentUser : userList) {
            ArrayList<String> favorites = currentUser.getFavoriteMovies();
            for (String currentMovie : favorites) {
                if (currentMovie.equals(this.getTitle())) {
                    number++;
                }
            }
        }
        return number;
    }

    /**
     * Used to determine how many times a movie was
     * seen by all the users
     * @param userList the users list
     * @return the total number of views
     */
    public int numberViews(final ArrayList<User> userList) {
        int number = 0;
        for (User currentUser : userList) {
            if (currentUser.getHistory().containsKey(this.getTitle())) {
                number += currentUser.getHistory().get(this.getTitle());
            }
        }

        return number;
    }

    @Override
    public String toString() {
        return title;
    }
}
