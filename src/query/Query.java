package query;
import actor.Actor;
import actor.ActorsAwards;
import actor.AverageActor;
import actor.AwardActor;
import common.Constants;
import entertainment.MoiveSerialMostViewed;
import entertainment.Movie;
import entertainment.MovieSerialFavorite;
import entertainment.MovieSerialLongest;
import entertainment.MovieSerialRatings;
import entertainment.Serial;
import lombok.Getter;
import org.json.simple.JSONObject;
import user.ActiveUser;
import user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Getter

public final class Query {
    private final Integer number;
    private final String sortType;
    private final List<List<String>> filters;
    private final String objectType;
    private static final int YEAR = 0;
    private static final int GENRE = 1;
    private static final int WORDS = 2;
    private static final int AWARDS = 3;

    public Query(final Integer number, final String sortType) {
        this.number = number;
        this.sortType = sortType;
        this.filters = new ArrayList<>();
        this.objectType = null;
    }

    public Query(final Integer number, final String sortType,
                 final List<List<String>> filters) {
        this.number = number;
        this.sortType = sortType;
        this.filters = filters;
        this.objectType = null;
    }

    public Query(final Integer number, final String sortType,
                 final List<List<String>> filters,
                 final String objectType) {
        this.number = number;
        this.sortType = sortType;
        this.filters = filters;
        this.objectType = objectType;
    }

    /**
     * Used for getting the average rating of a movie
     * @param movie the movie we are determining the average
     * @param movieList the list with all the movies
     * @return the average rating
     */
    public Double searchAverageRatingMovie(final String movie,
                                           final ArrayList<Movie> movieList) {
        for (Movie currentMovie : movieList) {
            if (currentMovie.getTitle().equals(movie)) {
                return currentMovie.avgRatings();
            }
        }

        return 0d;
    }

    /**
     * Used for getting the average rating of a serial
     * @param serial the serial we are determining the average for
     * @param serialList the list will all the serial
     * @return the average rating
     */
    public Double searchAverageRatingSerial(final String serial,
                                            final ArrayList<Serial> serialList) {
        for (Serial currentSerial: serialList) {
            if (currentSerial.getTitle().equals(serial)) {
                return currentSerial.avgRating();
            }
        }

        return 0d;
    }

    /**
     * Used for sorting the actors by the average of all the
     * movies and serials they appeared into
     * @param query The query information such as the
     *              number of actors we are searching for,
     *              the sort type and the filters
     * @param actorList the list with all the actors
     * @param movieList the list with all the movies
     * @param serialList the list will all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void average(final Query query, final ArrayList<Actor> actorList,
                        final ArrayList<Movie> movieList,
                        final ArrayList<Serial> serialList, final JSONObject object) {
        ArrayList<AverageActor> list = new ArrayList<>();
        for (Actor currActor : actorList) {
            double totalSum = 0d;
            Double counter = 0d;
            for (String video : currActor.getFilmography()) {
                if (searchAverageRatingSerial(video, serialList).compareTo(0d) != 0) {
                    totalSum += searchAverageRatingSerial(video, serialList);
                    counter++;
                } else if (searchAverageRatingMovie(video, movieList).compareTo(0d) != 0) {
                    totalSum += searchAverageRatingMovie(video, movieList);
                    counter++;
                }
            }

            if (counter.compareTo(0d) != 0) {
                Double average = totalSum / counter;
                AverageActor newActor = new AverageActor(currActor.getName(), average);
                list.add(newActor);
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                int counter = o1.getAverage().compareTo(o2.getAverage());
                if (counter != 0) {
                    return counter;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            } else {
                int counter2 = o2.getAverage().compareTo(o1.getAverage());
                if (counter2 != 0) {
                    return counter2;
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });

        ArrayList<AverageActor> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (AverageActor actor : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(actor);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used to determine all the actors with all the awards
     * in the query
     * @param query the query information such as the number
     *              of actors we are looking for, the sort type
     *              or the filters
     * @param actorList the actor list
     * @param object the JSON object used to write the answer
     */
    @SuppressWarnings("unchecked")
    public void awards(final Query query, final ArrayList<Actor> actorList,
                       final JSONObject object) {
        ArrayList<AwardActor> list = new ArrayList<>();
        List<String> awardsList = new ArrayList<>(query.getFilters().get(AWARDS));
        int ok;
        for (Actor current : actorList) {
            ok = 1;
            for (String award : awardsList) {
                if (!current.getAwards().containsKey(ActorsAwards.valueOf(award))) {
                    ok = 0;
                    break;
                }
            }

            if (ok == 1) {
                int totalAwards = 0;
                totalAwards += current.totalAwards();
                AwardActor newActor = new AwardActor(current.getName(), totalAwards);
                list.add(newActor);
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                if (o1.getTotalAwards().compareTo(o2.getTotalAwards()) != 0) {
                    return Integer.compare(o1.getTotalAwards(), o2.getTotalAwards());
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            } else {
                if (o1.getTotalAwards().compareTo(o2.getTotalAwards()) != 0) {
                    return Integer.compare(o2.getTotalAwards(), o1.getTotalAwards());
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });

        ArrayList<AwardActor> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (AwardActor actor : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(actor);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used to determine all the actors with their description
     * containing the words mentioned in the query
     * @param query the query information such as the number
     *              of actors we are looking for, the sort type
     *              or the filters
     * @param actorList the list with all the actors we are
     *                  searching into
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void filterDescription(final Query query,
                                  final ArrayList<Actor> actorList,
                                  final JSONObject object) {
        ArrayList<Actor> list = new ArrayList<>();
        List<String> words = new ArrayList<>(query.getFilters().get(2));
        int ok;
        for (Actor currentActor : actorList) {
            ok = 1;
            String pattern1 = "[\\s-]";
            String pattern2 = "[\\s,.]";
            for (String word : words) {
                Pattern pattern = Pattern.compile(pattern1
                        + word.toLowerCase() + pattern2);
                Matcher matcher =
                        pattern.matcher(currentActor.getCareerDescription().toLowerCase());
                if (!matcher.find()) {
                    ok = 0;
                    break;
                }
            }

            if (ok == 1) {
                list.add(currentActor);
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return o2.getName().compareTo(o1.getName());
            }
        });

        object.put(Constants.MESSAGE, "Query result: " + list);
    }

    /**
     * Used for determining the first N videos sorted
     * by their rating
     * @param query the query information such as the
     *              number of videos we are searching for
     *              or the sort type
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void rating(final Query query, final ArrayList<Movie> movieList,
                         final ArrayList<Serial> serialList, final JSONObject object) {
        ArrayList<MovieSerialRatings> ratingsList = new ArrayList<>();

        if (query.getObjectType().equals("movies")) {
            for (Movie currentMovie : movieList) {
                if (query.getFilters().get(0).get(0) != null) {
                    int year = currentMovie.getYear();
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (query.getFilters().get(0).contains(Integer.toString(year))
                                && currentMovie.getGenres().containsAll(words)) {
                            Double average = currentMovie.avgRatings();
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentMovie.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    } else {
                        if (query.getFilters().get(0).contains(Integer.toString(year))) {
                            Double average = currentMovie.avgRatings();
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentMovie.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (currentMovie.getGenres().containsAll(words)) {
                            Double average = currentMovie.avgRatings();
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentMovie.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    } else {
                        Double average = currentMovie.avgRatings();
                        if (average.compareTo(0d) != 0) {
                            MovieSerialRatings newObject =
                                    new MovieSerialRatings(currentMovie.getTitle(), average);
                            ratingsList.add(newObject);
                        }
                    }
                }
            }
        }

        if (query.getObjectType().equals("shows")) {
            for (Serial currentSerial : serialList) {
                Double average = currentSerial.avgRating();
                if (query.getFilters().get(0).get(0) != null) {
                    int year = currentSerial.getYear();
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (query.getFilters().get(0).contains(Integer.toString(year))
                                && currentSerial.getGenres().containsAll(words)) {
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentSerial.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    } else {
                        if (query.getFilters().get(0).contains(Integer.toString(year))) {
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentSerial.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (currentSerial.getGenres().containsAll(words)) {
                            if (average.compareTo(0d) != 0) {
                                MovieSerialRatings newObject =
                                        new MovieSerialRatings(currentSerial.getTitle(), average);
                                ratingsList.add(newObject);
                            }
                        }
                    } else {
                        if (average.compareTo(0d) != 0) {
                            MovieSerialRatings newObject =
                                    new MovieSerialRatings(currentSerial.getTitle(), average);
                            ratingsList.add(newObject);
                        }
                    }
                }
            }
        }

        ratingsList.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                int counter = o1.getRating().compareTo(o2.getRating());
                if (counter != 0) {
                    return counter;
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            } else {
                int counter = o2.getRating().compareTo(o1.getRating());
                if (counter != 0) {
                    return counter;
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });

        ArrayList<MovieSerialRatings> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (MovieSerialRatings movieSerial : ratingsList) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(movieSerial);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used to determine the first N videos sorted by the number
     * of appearances in the users favorite list
     * @param query the query information such as the number
     *              of videos we are searching for or the sort
     *              type
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param userList the list with all the users
     * @param object the JSON object used for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void favorite(final Query query, final ArrayList<Movie> movieList,
                         final ArrayList<Serial> serialList,
                         final ArrayList<User> userList, final JSONObject object) {
        ArrayList<MovieSerialFavorite> list = new ArrayList<>();

        if (query.getObjectType().equals("movies")) {
            for (Movie currentMovie : movieList) {
                if (query.getFilters().get(1).get(0) != null) {
                    if (query.getFilters().get(0).get(0) != null) {
                        int year = currentMovie.getYear();
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (query.getFilters().get(0).contains(Integer.toString(year))
                                && currentMovie.getGenres().containsAll(words)) {
                            int numberAp = currentMovie.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentMovie.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    } else {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (currentMovie.getGenres().containsAll(words)) {
                            int numberAp = currentMovie.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentMovie.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(0).get(0) != null) {
                        int year = currentMovie.getYear();
                        if (query.getFilters().get(0).contains(Integer.toString(year))) {
                            int numberAp = currentMovie.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentMovie.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    } else {
                        int numberAp = currentMovie.numberFavorite(userList);
                        MovieSerialFavorite current =
                                new MovieSerialFavorite(currentMovie.getTitle(), numberAp);
                        list.add(current);
                    }
                }
            }
        }

        if (query.getObjectType().equals("shows")) {
            for (Serial currentSerial : serialList) {
                int year = currentSerial.getYear();
                if (query.getFilters().get(1).get(0) != null) {
                    List<String> words = new ArrayList<>(query.getFilters().get(1));
                    if (query.getFilters().get(0).get(0) != null) {
                        if (query.getFilters().get(0).contains(Integer.toString(year))
                                && currentSerial.getGenres().containsAll(words)) {
                            int numberAp = currentSerial.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentSerial.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    } else {
                        if (currentSerial.getGenres().containsAll(words)) {
                            int numberAp = currentSerial.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentSerial.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(0).get(0) != null) {
                        if (query.getFilters().get(0).contains(Integer.toString(year))) {
                            int numberAp = currentSerial.numberFavorite(userList);
                            if (numberAp != 0) {
                                MovieSerialFavorite current =
                                        new MovieSerialFavorite(currentSerial.getTitle(), numberAp);
                                list.add(current);
                            }
                        }
                    } else {
                        int numberAp = currentSerial.numberFavorite(userList);
                        if (numberAp != 0) {
                            MovieSerialFavorite current =
                                    new MovieSerialFavorite(currentSerial.getTitle(), numberAp);
                            list.add(current);
                        }
                    }
                }
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                if (o1.getApCount().compareTo(o2.getApCount()) != 0) {
                    return Integer.compare(o1.getApCount(), o2.getApCount());
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            } else {
                if (o1.getApCount().compareTo(o2.getApCount()) != 0) {
                    return Integer.compare(o2.getApCount(), o1.getApCount());
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });


        ArrayList<MovieSerialFavorite> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (MovieSerialFavorite movieSerial : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(movieSerial);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used for determining the first N videos sorted by their length
     * @param query the query information such as the number N of videos
     *              we are searching for
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void longest(final Query query, final ArrayList<Movie> movieList,
                         final ArrayList<Serial> serialList, final JSONObject object) {
        ArrayList<MovieSerialLongest> list = new ArrayList<>();
        if (query.getObjectType().equals("movies")) {
            for (Movie currentMovie : movieList) {
                if (query.getFilters().get(0).get(0) != null) {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        String year = Integer.toString(currentMovie.getYear());
                        if (query.getFilters().get(0).contains(year)
                                && currentMovie.getGenres().containsAll(words)) {
                            int duration = currentMovie.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newMovie =
                                        new MovieSerialLongest(currentMovie.getTitle(), duration);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        String year = Integer.toString(currentMovie.getYear());
                        if (query.getFilters().get(0).contains(year)) {
                            int duration = currentMovie.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newMovie =
                                        new MovieSerialLongest(currentMovie.getTitle(), duration);
                                list.add(newMovie);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (currentMovie.getGenres().containsAll(words)) {
                            int duration = currentMovie.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newMovie =
                                        new MovieSerialLongest(currentMovie.getTitle(), duration);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        int duration = currentMovie.getDuration();
                        if (duration != 0) {
                            MovieSerialLongest newMovie =
                                    new MovieSerialLongest(currentMovie.getTitle(), duration);
                            list.add(newMovie);
                        }
                    }
                }
            }
        }

        if (query.getObjectType().equals("shows")) {
            for (Serial currentSerial : serialList) {
                if (query.getFilters().get(0).get(0) != null) {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        String year = Integer.toString(currentSerial.getYear());
                        if (query.getFilters().get(0).contains(year)
                                && currentSerial.getGenres().containsAll(words)) {
                            int duration = currentSerial.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newSerial =
                                        new MovieSerialLongest(currentSerial.getTitle(), duration);
                                list.add(newSerial);
                            }
                        }
                    } else {
                        String year = Integer.toString(currentSerial.getYear());
                        if (query.getFilters().get(0).contains(year)) {
                            int duration = currentSerial.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newSerial =
                                        new MovieSerialLongest(currentSerial.getTitle(), duration);
                                list.add(newSerial);
                            }
                        }
                    }
                } else {
                    if (query.getFilters().get(1).get(0) != null) {
                        List<String> words = new ArrayList<>(query.getFilters().get(1));
                        if (currentSerial.getGenres().containsAll(words)) {
                            int duration = currentSerial.getDuration();
                            if (duration != 0) {
                                MovieSerialLongest newSerial =
                                        new MovieSerialLongest(currentSerial.getTitle(), duration);
                                list.add(newSerial);
                            }
                        }
                    } else {
                        int duration = currentSerial.getDuration();
                        if (duration != 0) {
                            MovieSerialLongest newSerial =
                                    new MovieSerialLongest(currentSerial.getTitle(), duration);
                            list.add(newSerial);
                        }
                    }
                }
            }
        }

        list.sort((o1, o2) -> {
            int counter;
            if (query.sortType.equals("asc")) {
                counter = Integer.compare(o1.getDuration(), o2.getDuration());
            } else {
                counter = Integer.compare(o2.getDuration(), o1.getDuration());
            }
            if (counter != 0) {
                return counter;
            } else {
                return o1.getName().compareTo(o2.getName());
            }
        });

        ArrayList<MovieSerialLongest> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (MovieSerialLongest movieSerial : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(movieSerial);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used for determining the first N videos sorted by their
     * number of views
     * @param query the query information such as the sort type
     *              or the number N of videos we are searching for
     * @param userList the list with all the users
     * @param movieList the list with all the movies
     * @param serialList the list with all the serials
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void mostViewed(final Query query, final ArrayList<User> userList,
                            final ArrayList<Movie> movieList, final ArrayList<Serial> serialList,
                            final JSONObject object) {
        ArrayList<MoiveSerialMostViewed> list = new ArrayList<>();
        if (query.getObjectType().equals("movies")) {
            for (Movie currentMovie : movieList) {
                List<String> genres = query.getFilters().get(1);
                if (query.getFilters().get(0).get(0) != null) {
                    String year = Integer.toString(currentMovie.getYear());
                    if (genres.get(0) != null) {
                        if (query.getFilters().get(0).contains(year)
                                && currentMovie.getGenres().containsAll(genres)) {
                            int numberViews = currentMovie.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentMovie.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        if (query.getFilters().get(0).contains(year)) {
                            int numberViews = currentMovie.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentMovie.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    }
                } else {
                    if (genres.get(0) != null) {
                        if (currentMovie.getGenres().containsAll(genres)) {
                            int numberViews = currentMovie.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentMovie.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        int numberViews = currentMovie.numberViews(userList);
                        if (numberViews != 0) {
                            MoiveSerialMostViewed newMovie =
                                    new MoiveSerialMostViewed(currentMovie.getTitle(),
                                            numberViews);
                            list.add(newMovie);
                        }
                    }
                }
            }
        }

        if (query.getObjectType().equals("shows")) {
            for (Serial currentSerial : serialList) {
                List<String> genres = query.getFilters().get(1);
                if (query.getFilters().get(0).get(0) != null) {
                    String year = Integer.toString(currentSerial.getYear());
                    if (genres.get(0) != null) {
                        if (query.getFilters().get(0).contains(year)
                                && currentSerial.getGenres().containsAll(genres)) {
                            int numberViews = currentSerial.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentSerial.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        if (query.getFilters().get(0).contains(year)) {
                            int numberViews = currentSerial.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentSerial.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    }
                } else {
                    if (genres.get(0) != null) {
                        if (currentSerial.getGenres().containsAll(genres)) {
                            int numberViews = currentSerial.numberViews(userList);
                            if (numberViews != 0) {
                                MoiveSerialMostViewed newMovie =
                                        new MoiveSerialMostViewed(currentSerial.getTitle(),
                                                numberViews);
                                list.add(newMovie);
                            }
                        }
                    } else {
                        int numberViews = currentSerial.numberViews(userList);
                        if (numberViews != 0) {
                            MoiveSerialMostViewed newMovie =
                                    new MoiveSerialMostViewed(currentSerial.getTitle(),
                                            numberViews);
                            list.add(newMovie);
                        }
                    }
                }
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                if (o1.getViewNumber() != o2.getViewNumber()) {
                    return Integer.compare(o1.getViewNumber(), o2.getViewNumber());
                } else {
                    return o1.getName().compareTo(o2.getName());
                }
            } else {
                if (o1.getViewNumber() != o2.getViewNumber()) {
                    return Integer.compare(o2.getViewNumber(), o1.getViewNumber());
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });


        ArrayList<MoiveSerialMostViewed> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (MoiveSerialMostViewed movieSerial : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(movieSerial);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);
    }

    /**
     * Used for determining the most active N users by the
     * total number of ratings they gave
     * @param query the query information such as the number
     *              N we are looking for or the sort type
     * @param userList the list with all the users
     * @param object the JSON object for writing the answer
     */
    @SuppressWarnings("unchecked")
    public void mostActive(final Query query, final ArrayList<User> userList,
                           final JSONObject object) {
        ArrayList<ActiveUser> list = new ArrayList<>();
        for (User currentUser : userList) {
            int numberRatings = currentUser.getNumberRatings();
            if (numberRatings != 0) {
                ActiveUser newUser = new ActiveUser(currentUser.getUsername(), numberRatings);
                list.add(newUser);
            }
        }

        list.sort((o1, o2) -> {
            if (query.getSortType().equals("asc")) {
                if (o1.getNumberRatings() != o2.getNumberRatings()) {
                    return Integer.compare(o1.getNumberRatings(), o2.getNumberRatings());
                } else {
                    return o1.getUsername().compareTo(o2.getUsername());
                }
            } else {
                if (o1.getNumberRatings() != o2.getNumberRatings()) {
                    return Integer.compare(o2.getNumberRatings(), o1.getNumberRatings());
                } else {
                    return o2.getUsername().compareTo(o1.getUsername());
                }
            }
        });


        ArrayList<ActiveUser> finalList = new ArrayList<>();
        int counter = query.getNumber();
        for (ActiveUser user : list) {
            if (counter == 0) {
                break;
            }

            counter--;
            finalList.add(user);
        }

        object.put(Constants.MESSAGE, "Query result: " + finalList);

    }
}
