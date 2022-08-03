package recommendation;
import common.Constants;
import entertainment.Movie;
import entertainment.MovieSerialRatings;
import entertainment.Serial;
import lombok.Getter;
import org.json.simple.JSONObject;
import user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter

public final class Recommendation {
    private final String username;
    private final String genre;

    public Recommendation(final String username) {
        this.username = username;
        this.genre = null;
    }

    public Recommendation(final String username, final String genre) {
        this.username = username;
        this.genre = genre;
    }

    /**
     * Used for recommending the first video unseen by a user
     * taking into consideration the order in the database
     * @param rec the recommendation restrictions such as
     *            the user we are recommending to or the gender
     *            of the video he is searching for
     * @param userList the list with all the users
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void standardRec(final Recommendation rec, final ArrayList<User> userList,
                            final ArrayList<Movie> movieList,
                            final ArrayList<Serial> serialList, final JSONObject object) {
        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())) {
                for (Movie currentMovie : movieList) {
                    if (!currentUser.getHistory()
                            .containsKey(currentMovie.getTitle())) {
                        object.put(Constants.MESSAGE,
                                "StandardRecommendation result: " + currentMovie.getTitle());
                        return;
                    }
                }

                for (Serial currentSerial : serialList) {
                    if (!currentUser.getHistory()
                            .containsKey(currentSerial.getTitle())) {
                        object.put(Constants.MESSAGE,
                                "StandardRecommendation result: " + currentSerial.getTitle());
                        return;
                    }
                }

                object.put(Constants.MESSAGE, "StandardRecommendation cannot be applied!");
            }
        }
    }

    /**
     * Used to determine the best unseen video for a user based
     * on the rating
     * @param rec the recommendation restrictions such as
     *            the user we are recommending to or the gender
     *            of the video he is searching for
     * @param userList the list with all the users
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void bestUnseen(final Recommendation rec, final ArrayList<User> userList,
                           final ArrayList<Movie> movieList,
                           final ArrayList<Serial> serialList, final JSONObject object) {
        ArrayList<MovieSerialRatings> ratingsList = new ArrayList<>();
        for (Movie currentMovie : movieList) {
            Double average = currentMovie.avgRatings();
            MovieSerialRatings newObject =
                    new MovieSerialRatings(currentMovie.getTitle(), average);
            ratingsList.add(newObject);
        }

        for (Serial currentSerial : serialList) {
            Double average = currentSerial.avgRating();
            MovieSerialRatings newObject =
                    new MovieSerialRatings(currentSerial.getTitle(), average);
            ratingsList.add(newObject);
        }

        ratingsList.sort((o1, o2) -> o2.getRating().compareTo(o1.getRating()));


        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())) {
                for (MovieSerialRatings currentMovie : ratingsList) {
                    if (!currentUser.getHistory().containsKey(currentMovie.getName())) {
                        object.put(Constants.MESSAGE,
                                "BestRatedUnseenRecommendation result: " + currentMovie.getName());
                        return;
                    }
                }
            }
        }

        object.put(Constants.MESSAGE,
                "BestRatedUnseenRecommendation cannot be applied!");
    }

    /**
     * Used for determining the first movie of the most popular
     * genre unseen by a specific user
     * @param rec the recommendation restrictions such as
     *            the user we are recommending to or the gender
     *            of the video he is searching for
     * @param userList the list with all the users
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void popular(final Recommendation rec, final ArrayList<User> userList,
                        final ArrayList<Movie> movieList, final ArrayList<Serial> serialList,
                        final JSONObject object) {
        HashMap<String, Integer> genres = new HashMap<>();
        for (User currentUser : userList) {
            currentUser.popularGenre(movieList, serialList, genres);
        }
        List<Map.Entry<String, Integer>> list = new LinkedList<>(genres.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        HashMap<String, Integer> sortedGenres = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedGenres.put(entry.getKey(), entry.getValue());
        }

        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())) {
                if (!currentUser.getSubscriptionType().equals("PREMIUM")) {
                    object.put(Constants.MESSAGE, "PopularRecommendation cannot be applied!");
                } else {
                    for (Map.Entry<String, Integer> entry : sortedGenres.entrySet()) {
                        for (Movie currentMovie : movieList) {
                            if (currentMovie.getGenres().contains(entry.getKey())
                                    && !currentUser.getHistory()
                                         .containsKey(currentMovie.getTitle())) {
                                object.put(Constants.MESSAGE,
                                        "PopularRecommendation result: " + currentMovie.getTitle());
                                return;
                            }
                        }

                        for (Serial currentSerial : serialList) {
                            if (currentSerial.getGenres().contains(entry.getKey())
                                    && !currentUser.getHistory()
                                         .containsKey(currentSerial.getTitle())) {
                                object.put(Constants.MESSAGE,
                                        "PopularRecommendation result: "
                                                + currentSerial.getTitle());
                                return;
                            }
                        }
                    }
                }
            }
        }

        object.put(Constants.MESSAGE, "PopularRecommendation cannot be applied!");
    }

    /**
     * Used for determining the most appreciated movie unseen by a specific
     * user by the number of times it appears in the users favorite lists
     * @param rec the recommendation restrictions such as
     *            the user we are recommending to or the gender
     *            of the video he is searching for
     * @param userList the list with all the users
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void favorite(final Recommendation rec, final ArrayList<User> userList,
                         final JSONObject object) {
        HashMap<String, Integer> favoriteHash = new HashMap<>();
        for (User currentUser : userList) {
            currentUser.favoriteMovie(favoriteHash);
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(favoriteHash.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        HashMap<String, Integer> sortedFavoriteHash = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedFavoriteHash.put(entry.getKey(), entry.getValue());
        }

        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())
                    && !currentUser.getSubscriptionType().equals("PREMIUM")) {
                object.put(Constants.MESSAGE,
                        "FavoriteRecommendation cannot be applied!");
            } else if (currentUser.getUsername().equals(rec.getUsername())) {
                for (Map.Entry<String, Integer> entry : sortedFavoriteHash.entrySet()) {
                    if (!currentUser.getHistory().containsKey(entry.getKey())) {
                        object.put(Constants.MESSAGE,
                                "FavoriteRecommendation result: " + entry.getKey());
                        return;
                    }
                }
            }
        }
        object.put(Constants.MESSAGE, "FavoriteRecommendation cannot be applied!");
    }

    /**
     * Used to determine all the videos unseen by a specific user from
     * a genre
     * @param rec the recommendation restrictions such as
     *            the user we are recommending to or the gender
     *            of the video he is searching for
     * @param userList the list with all the users
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void search(final Recommendation rec, final ArrayList<User> userList,
                       final ArrayList<Movie> movieList, final ArrayList<Serial> serialList,
                       final JSONObject object) {
        HashMap<String, Double> searchMap = new HashMap<>();
        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())) {
                currentUser.videoSeen(searchMap, movieList, serialList, rec.getGenre());
                break;
            }
        }

        if (searchMap.size() == 0) {
            object.put(Constants.MESSAGE, "SearchRecommendation cannot be applied!");
            return;
        }

        List<Map.Entry<String, Double>> list = new LinkedList<>(searchMap.entrySet());
        list.sort((o1, o2) -> {
            if (o1.getValue().compareTo(o2.getValue()) != 0) {
                return o1.getValue().compareTo(o2.getValue());
            } else {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        ArrayList<String> sortedSearchList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedSearchList.add(entry.getKey());
        }



        for (User currentUser : userList) {
            if (currentUser.getUsername().equals(rec.getUsername())) {
                if (!currentUser.getSubscriptionType().equals("PREMIUM")) {
                    object.put(Constants.MESSAGE, "SearchRecommendation cannot be applied!");
                } else {
                    object.put(Constants.MESSAGE,
                            "SearchRecommendation result: " + sortedSearchList);
                }
                return;
            }
        }

    }

}
