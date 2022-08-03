package action;

import lombok.Getter;
import java.util.List;
@Getter

public class Action {
    private final int actionId;
    private final String actionType;
    private final String type;
    private final String username;
    private final String objectType;
    private final String sortType;
    private final String criteria;
    private final String title;
    private final String genre;
    private final int number;
    private final double grade;
    private final int seasonNumber;
    private final List<List<String>> filters;

    public Action(final int actionId, final String actionType, final String type,
                  final String username, final String objectType, final String sortType,
                  final String criteria, final String title, final String genre,
                  final int number, final double grade, final int seasonNumber,
                  final List<List<String>> filters) {
        this.actionId = actionId;
        this.actionType = actionType;
        this.type = type;
        this.username = username;
        this.objectType = objectType;
        this.sortType = sortType;
        this.criteria = criteria;
        this.title = title;
        this.genre = genre;
        this.number = number;
        this.grade = grade;
        this.seasonNumber = seasonNumber;
        this.filters = filters;
    }
}
