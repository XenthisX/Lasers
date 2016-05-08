import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Elijah Bosley on 5/5/2016.
 */
public class LasersGraphicalVC extends Application implements Observer {


    public LasersModel model;
    private Label title;
    private GridPane board;
    private HBox buttons;

    public LasersGraphicalVC() {

    }


    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initially we need to read in the safeFile and setup the model using it.
        final List<String> params = getParameters().getRaw();
        String safeFile = params.get(1);
        model = new LasersModel(safeFile);

        /** Sets up a BorderPane to contain the Lasers Grid */
        BorderPane main = new BorderPane();
        primaryStage.setResizable(true);
        primaryStage.setTitle("Lasers");

        /** Set up title */
        title = new Label("Welcome to the LasersGUI");
        title.setFont(new Font("Helvectiva", 15));
        main.setTop(title);

        /** Set up grid */
        board = createGrid();
        main.setCenter(board);

        /** Add buttons */
        buttons = createButtons();
        main.setBottom(buttons);

        primaryStage.setScene(new Scene(main));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * Potential helper to update the title
     */
    private void updateMessageArea() {

    }

    /**
     * Creates and returns a grid pane containing the laser object
     * @return a gridPane that contains the laser board
     */
    private GridPane createGrid() {
        GridPane board = new GridPane();

        return board;
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(5);
        Button check = new Button("Check");
        Button hint = new Button("Hint");
        Button solve = new Button("Solve");
        Button restart = new Button("Restart");
        Button load = new Button("Load");
        buttonBox.getChildren().addAll(check, hint, solve, restart, load);
        return buttonBox;
    }


    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>Observable</tt> object's
     * <code>notifyObservers</code> method to have all the object's
     * observers notified of the change.
     *
     * @param o   the observable object.
     * @param arg an argument passed to the <code>notifyObservers</code>
     */
    @Override
    public void update(Observable o, Object arg) {

    }

}
