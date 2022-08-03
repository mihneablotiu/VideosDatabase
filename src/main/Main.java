package main;

import action.Action;
import actor.Actor;
import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import database.Database;
import entertainment.Movie;
import entertainment.Serial;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import user.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        ArrayList<UserInputData> newUserInputList = new ArrayList<>(input.getUsers());
        ArrayList<User> newUserList = new ArrayList<>();
        for (UserInputData user : newUserInputList) {
            User newUser = new User(user.getUsername(), user.getSubscriptionType(),
                    user.getHistory(), user.getFavoriteMovies());
            newUserList.add(newUser);
        }

        ArrayList<ActorInputData> newActorInputList = new ArrayList<>(input.getActors());
        ArrayList<Actor> newActorList = new ArrayList<>();
        for (ActorInputData actor : newActorInputList) {
            Actor newActor = new Actor(actor.getName(), actor.getCareerDescription(),
                    actor.getFilmography(), actor.getAwards());
            newActorList.add(newActor);
        }

        ArrayList<ActionInputData> newActionInputList = new ArrayList<>(input.getCommands());
        ArrayList<Action> newActionList = new ArrayList<>();
        for (ActionInputData action : newActionInputList) {
            Action newAction = new Action(action.getActionId(), action.getActionType(),
                    action.getType(), action.getUsername(), action.getObjectType(),
                    action.getSortType(), action.getCriteria(), action.getTitle(),
                    action.getGenre(), action.getNumber(), action.getGrade(),
                    action.getSeasonNumber(), action.getFilters());
            newActionList.add(newAction);
        }

        ArrayList<MovieInputData> newMovieInputList = new ArrayList<>(input.getMovies());
        ArrayList<Movie> newMovieList = new ArrayList<>();
        for (MovieInputData movie : newMovieInputList) {
            Movie newMovie = new Movie(movie.getTitle(), movie.getYear(),
                    movie.getCast(), movie.getGenres(), movie.getDuration());
            newMovieList.add(newMovie);
        }

        ArrayList<SerialInputData> newSerialInputList = new ArrayList<>(input.getSerials());
        ArrayList<Serial> newSerialList = new ArrayList<>();
        for (SerialInputData serial : newSerialInputList) {
            Serial newSerial = new Serial(serial.getTitle(), serial.getYear(),
                    serial.getCast(), serial.getGenres(), serial.getNumberSeason(),
                    serial.getSeasons());
            newSerialList.add(newSerial);
        }

        Database database = new Database(newUserList, newActionList,
                newMovieList, newSerialList, newActorList, arrayResult);
        database.interrogate();
        fileWriter.closeJSON(arrayResult);
    }
}
