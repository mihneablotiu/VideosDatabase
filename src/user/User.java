package user;

import common.Constants;
import entertainment.Movie;
import entertainment.Season;
import entertainment.Serial;
import lombok.Getter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Getter

public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    private final HashMap<String, Double> movieRatings = new HashMap<>();
    private final HashMap<String, HashMap<Integer, Double>> serialRatings = new HashMap<>();

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    /**
     * Used for mapping all the movies and serials with
     * a specific genre with their ratings in order to
     * sort them in the search recommendation
     * @param videoList the HashMap with all the videos
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param genre the genre we are looking for
     */
    public void videoSeen(final HashMap<String, Double> videoList,
                          final ArrayList<Movie> movieList,
                          final ArrayList<Serial> serialList, final String genre) {
        for (Movie currentMovie : movieList) {
            if (currentMovie.getGenres().contains(genre)
                    && !this.history.containsKey(currentMovie.getTitle())) {
                videoList.put(currentMovie.getTitle(), currentMovie.avgRatings());
            }
        }

        for (Serial currentSerial : serialList) {
            if (currentSerial.getGenres().contains(genre)
                    && !this.history.containsKey(currentSerial.getTitle())) {
                videoList.put(currentSerial.getTitle(), currentSerial.avgRating());
            }
        }
    }

    /**
     * Used for mapping each video appearing in the favorite
     * list of a user with the total number of appearances
     * for the favorite recommendation
     * @param hash the HashMap used for the association
     */
    public void favoriteMovie(final HashMap<String, Integer> hash) {
        ArrayList<String> favorite = this.getFavoriteMovies();
        for (String currentMovie : favorite) {
            if (hash.containsKey(currentMovie)) {
                Integer currentValue = hash.get(currentMovie);
                currentValue++;
                hash.replace(currentMovie, currentValue);
            } else {
                hash.put(currentMovie, 1);
            }
        }
    }

    /**
     * Used for determining the total number of views / the
     * popularity each movie / serial has from a specific
     * genre
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param hash the HashMap used for this association
     */
    public void popularGenre(final ArrayList<Movie> movieList,
                             final ArrayList<Serial> serialList,
                             final HashMap<String, Integer> hash) {
        for (Movie currentMovie : movieList) {
            ArrayList<String> genres = currentMovie.getGenres();
            for (String currentGenre : genres) {
                if (!hash.containsKey(currentGenre)) {
                    if (this.history.containsKey(currentMovie.getTitle())) {
                        hash.put(currentGenre, this.history.get(currentMovie.getTitle()));
                    }
                } else {
                    if (this.history.containsKey(currentMovie.getTitle())) {
                        Integer current = hash.get(currentGenre);
                        hash.replace(currentGenre,
                                current + this.history.get(currentMovie.getTitle()));
                    }
                }
            }
        }

        for (Serial currentSerial : serialList) {
            ArrayList<String> genres = currentSerial.getGenres();
            for (String currentGenre : genres) {
                if (!hash.containsKey(currentGenre)) {
                    if (this.history.containsKey(currentSerial.getTitle())) {
                        hash.put(currentGenre, this.history.get(currentSerial.getTitle()));
                    }
                } else {
                    if (this.history.containsKey(currentSerial.getTitle())) {
                        Integer current = hash.get(currentGenre);
                        hash.replace(currentGenre,
                                current + this.history.get(currentSerial.getTitle()));
                    }
                }
            }
        }
    }

    /**
     * Used for determining how many ratings a user gave /
     * the most active user
     * @return the total number of ratings given by a user
     */
    public int getNumberRatings() {
        int number = 0;
        number += this.movieRatings.size();
        for (Map.Entry<String, HashMap<Integer, Double>> entry : this.serialRatings.entrySet()) {
            number += entry.getValue().size();
        }

        return number;
    }

    /**
     * Used for adding a video in a specific user favorite list
     * @param movieName the movie we are adding to the list
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void favorite(final String movieName, final JSONObject object) {
        if (this.history.containsKey(movieName)) {
            for (String currentName : this.favoriteMovies) {
                if (movieName.equals(currentName)) {
                    object.put(Constants.MESSAGE,
                            "error -> " + movieName + " is already in favourite list");
                    return;
                }
            }
            this.favoriteMovies.add(movieName);
            object.put(Constants.MESSAGE, "success -> " + movieName + " was added as favourite");
            return;
        }

        object.put(Constants.MESSAGE, "error -> " + movieName + " is not seen");
    }

    /**
     * Used for setting a movie as seen or for updating
     * the number of times it has been seen
     * @param movieName the name of the movie we are seeing
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void view(final String movieName, final JSONObject object) {
        if (this.history.containsKey(movieName)) {
            Integer actualViews = this.history.get(movieName);
            actualViews++;
            this.history.replace(movieName, actualViews);
            object.put(Constants.MESSAGE,
                    "success -> " + movieName + " was viewed with total views of " + actualViews);
        } else {
            this.history.put(movieName, 1);
            object.put(Constants.MESSAGE,
                    "success -> " + movieName + " was viewed with total views of 1");
        }
    }

    /**
     * Used for rating a movie by a user if it has not been
     * rated yet
     * @param movieName the movie name to be rated
     * @param grade the rate grade
     * @param movieList the list with all the movies
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void rateMovie(final String movieName, final Double grade,
                           final ArrayList<Movie> movieList, final JSONObject object) {
        if (this.history.containsKey(movieName)) {
            if (!this.movieRatings.containsKey(movieName)) {
                for (Movie currentMovie : movieList) {
                    if (movieName.equals(currentMovie.getTitle())) {
                        currentMovie.getRatings().add(grade);
                        break;
                    }
                }
                this.movieRatings.put(movieName, grade);
                object.put(Constants.MESSAGE, "success -> "
                        + movieName + " was rated with " + grade + " by " + this.getUsername());
            } else if (this.movieRatings.containsKey(movieName)) {
                object.put(Constants.MESSAGE, "error -> " + movieName + " has been already rated");
            }
        } else {
            object.put(Constants.MESSAGE, "error -> " + movieName + " is not seen");
        }
    }

    /**
     * Used for rating a season of a serial by a user
     * if it has not been rated yet
     * @param serialName the serial name to be rated
     * @param seasonNumber the season from the serial
     *                     to be rated
     * @param grade the grade for the rating
     * @param serialList the list with all the serial
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void rateSerial(final String serialName, final Integer seasonNumber,
                           final Double grade, final ArrayList<Serial> serialList,
                           final JSONObject object) {
        if (this.history.containsKey(serialName)) {
            if (!this.serialRatings.containsKey(serialName)) {
                HashMap<Integer, Double> newRating = new HashMap<>();
                newRating.put(seasonNumber, grade);
                this.serialRatings.put(serialName, newRating);
                for (Serial currentSerial : serialList) {
                    if (serialName.equals(currentSerial.getTitle())) {
                        ArrayList<Season> seasons = currentSerial.getSeasons();
                        for (Season currentSeason : seasons) {
                            if (currentSeason.getCurrentSeason() == seasonNumber) {
                                currentSeason.getRatings().add(grade);
                                break;
                            }
                        }
                    }
                }
                object.put(Constants.MESSAGE, "success -> "
                        + serialName + " was rated with " + grade + " by " + this.getUsername());
            } else {
                if (!this.serialRatings.get(serialName).containsKey(seasonNumber)) {
                    for (Serial currentSerial : serialList) {
                        if (serialName.equals(currentSerial.getTitle())) {
                            ArrayList<Season> seasons = currentSerial.getSeasons();
                            for (Season currentSeason : seasons) {
                                if (currentSeason.getCurrentSeason() == seasonNumber) {
                                    currentSeason.getRatings().add(grade);
                                    break;
                                }
                            }
                        }
                    }
                    this.serialRatings.get(serialName).put(seasonNumber, grade);
                    object.put(Constants.MESSAGE, "success -> "
                            + serialName + " was rated with "
                            + grade + " by " + this.getUsername());
                } else {
                    object.put(Constants.MESSAGE, "error -> "
                            + serialName + " has been already rated");
                }
            }
        } else {
            object.put(Constants.MESSAGE, "error -> " + serialName + " is not seen");
        }
    }
}
