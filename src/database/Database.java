package database;

import action.Action;
import actor.Actor;
import common.Constants;
import entertainment.Movie;
import entertainment.Serial;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import query.Query;
import recommendation.Recommendation;
import user.User;

import java.util.ArrayList;
@Getter

public final class Database {
    private final ArrayList<User> userList;
    private final ArrayList<Action> actionList;
    private final ArrayList<Movie> movieList;
    private final ArrayList<Serial> serialList;
    private final ArrayList<Actor> actorList;
    private final JSONArray jsonArray;

    public Database(final ArrayList<User> userList, final ArrayList<Action> actionList,
                    final ArrayList<Movie> movieList, final ArrayList<Serial> serialList,
                    final ArrayList<Actor> actorList, final JSONArray object) {
        this.userList = userList;
        this.actionList = actionList;
        this.movieList = movieList;
        this.serialList = serialList;
        this.actorList = actorList;
        this.jsonArray = object;
    }

    /**
     * Used for determining which command/query/recommendation
     * will be applied to the database
     */
    @SuppressWarnings("unchecked")
    public void interrogate() {
        for (Action currentAction : this.actionList) {
            JSONObject currentObject = new JSONObject();
            currentObject.put(Constants.ID_STRING, currentAction.getActionId());
            switch (currentAction.getActionType()) {
                case "command":
                    switch (currentAction.getType()) {
                        case "favorite":
                            for (User currentUser : this.userList) {
                                if (currentUser.getUsername().equals(currentAction.getUsername())) {
                                    currentUser.favorite(currentAction.getTitle(), currentObject);
                                    break;
                                }
                            }
                            break;
                        case "view":
                            for (User currentUser : this.userList) {
                                if (currentUser.getUsername().equals(currentAction.getUsername())) {
                                    currentUser.view(currentAction.getTitle(), currentObject);
                                    break;
                                }
                            }
                            break;
                        case "rating":
                            for (User currentUser : this.userList) {
                                if (currentUser.getUsername().equals(currentAction.getUsername())) {
                                    if (currentAction.getSeasonNumber() == 0) {
                                        currentUser.rateMovie(currentAction.getTitle(),
                                                currentAction.getGrade(), this.movieList,
                                                currentObject);
                                    } else {
                                        currentUser.rateSerial(currentAction.getTitle(),
                                                currentAction.getSeasonNumber(),
                                                currentAction.getGrade(),
                                                this.serialList, currentObject);
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case "query":
                    switch (currentAction.getCriteria()) {
                        case "average" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType());
                            query.average(query, this.actorList, this.movieList,
                                    this.serialList, currentObject);
                        }
                        case "awards" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(), currentAction.getFilters());
                            query.awards(query, this.actorList, currentObject);
                        }
                        case "filter_description" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(), currentAction.getFilters());
                            query.filterDescription(query, this.actorList, currentObject);
                        }
                        case "ratings" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(),
                                    currentAction.getFilters(), currentAction.getObjectType());
                            query.rating(query, this.movieList, this.serialList, currentObject);
                        }
                        case "favorite" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(),
                                    currentAction.getFilters(), currentAction.getObjectType());
                            query.favorite(query, this.movieList, this.serialList,
                                    this.userList, currentObject);
                        }
                        case "longest" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(),
                                    currentAction.getFilters(), currentAction.getObjectType());
                            query.longest(query, this.movieList, this.serialList, currentObject);
                        }
                        case "most_viewed" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(),
                                    currentAction.getFilters(), currentAction.getObjectType());
                            query.mostViewed(query, this.userList,
                                    this.movieList, this.serialList, currentObject);
                        }
                        case "num_ratings" -> {
                            Query query = new Query(currentAction.getNumber(),
                                    currentAction.getSortType(), currentAction.getFilters());
                            query.mostActive(query, this.userList, currentObject);
                        }
                        default -> {
                            break;
                        }
                    }
                    break;
                case "recommendation":
                    switch (currentAction.getType()) {
                        case "standard" -> {
                            Recommendation rec = new Recommendation(currentAction.getUsername());
                            rec.standardRec(rec, this.userList, this.movieList,
                                    this.serialList, currentObject);
                        }
                        case "best_unseen" -> {
                            Recommendation rec = new Recommendation(currentAction.getUsername());
                            rec.bestUnseen(rec, this.userList, this.movieList,
                                    this.serialList, currentObject);
                        }
                        case "popular" -> {
                            Recommendation rec = new Recommendation(currentAction.getUsername());
                            rec.popular(rec, this.userList, this.movieList,
                                    this.serialList, currentObject);
                        }
                        case "favorite" -> {
                            Recommendation rec = new Recommendation(currentAction.getUsername());
                            rec.favorite(rec, this.userList, currentObject);
                        }
                        case "search" -> {
                            Recommendation rec = new Recommendation(currentAction.getUsername(),
                                    currentAction.getGenre());
                            rec.search(rec, this.userList, this.movieList,
                                    this.serialList, currentObject);
                        }
                        default -> {
                            break;
                        }
                    }
                    break;

                default:
                    break;
            }

            this.getJsonArray().add(currentObject);
        }
    }
}
