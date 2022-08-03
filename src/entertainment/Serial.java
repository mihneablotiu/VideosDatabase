package entertainment;

import lombok.Getter;
import user.User;

import java.util.ArrayList;
import java.util.List;

@Getter

public final class Serial {
    private final String title;
    private final int year;
    private final ArrayList<String> cast;
    private final ArrayList<String> genres;
    private final int numberOfSeasons;
    private final ArrayList<Season> seasons;

    public Serial(final String title, final int year, final ArrayList<String> cast,
                  final ArrayList<String> genres, final int numberOfSeasons,
                  final ArrayList<Season> seasons) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    /**
     * Used to determine the average rating of
     * a serial by taking into consideration
     * all the seasons
     * @return the average rating
     */
    public Double avgRating() {
        double sum;
        double avgSeason;
        double averageSum = 0d;
        for (Season currentSeason : this.seasons) {
            sum = 0d;
            if (currentSeason.getRatings().size() != 0) {
                List<Double> ratingList = currentSeason.getRatings();
                for (Double currentGrade : ratingList) {
                    sum += currentGrade;
                }
                avgSeason = sum / currentSeason.getRatings().size();
                averageSum += avgSeason;
            }
        }

        return averageSum / this.numberOfSeasons;
    }

    /**
     * Used to determine how many times a serial appears
     * as a favorite for all the users
     * @param userList the users list
     * @return the total number of appearances
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
     * Used for getting the duration
     * of a serial by summing the duration of
     * all the seasons
     * @return the total duration
     */
    public int getDuration() {
        int duration = 0;
        for (Season currentSeason : this.getSeasons()) {
            duration += currentSeason.getDuration();
        }

        return duration;
    }

    /**
     * Used for determining the total number of views
     * a serial has by summing all the views of each
     * user
     * @param userList the user list
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

}
