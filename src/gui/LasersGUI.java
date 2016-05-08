package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import model.LasersModel;

import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author YOUR NAME HERE
 */
public class LasersGUI extends Application implements Observer {
    /**
     * this can be removed - it is used to demonstrates the button toggle
     */
    private static boolean status = true;
    /**
     * The UI's connection to the model
     */
    private LasersModel model;

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            String filename = params.getRaw().get(0);
            this.model = new LasersModel(filename);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button    the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResource("resources/" + bgImgName).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void buttonDemo(Stage stage) {
        // this demonstrates how to create a button and attach a foreground and
        // background image to it.
        Button button = new Button();
        Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
        ImageView laserIcon = new ImageView(laserImg);
        button.setGraphic(laserIcon);
        setButtonBackground(button, "yellow.png");
        button.setOnAction(e -> {
            // toggles background between yellow and red
            if (!status) {
                setButtonBackground(button, "yellow.png");
            } else {
                setButtonBackground(button, "red.png");
            }
            status = !status;
        });

        Scene scene = new Scene(button);
        stage.setScene(scene);
    }

    /**
     * Initialization function to add components to the stage
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        // TODO
        BorderPane main = new BorderPane();

        /** Set up title */
        Label title = new Label("Welcome to the LasersGUI");
        title.setFont(new Font("Helvectiva", 15));
        main.setTop(title);
        title.managedProperty().bind(title.visibleProperty());

        /** Set up grid */
        GridPane board = createGrid();
        main.setCenter(board);
        board.managedProperty().bind(board.visibleProperty());

        /** Add buttons */
        HBox buttons = createButtons();
        main.setBottom(buttons);

        stage.setScene(new Scene(main));
        //buttonDemo(stage);  // this can be removed/altered
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO
        init(primaryStage);  // do all your UI initialization here

        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO
    }

    /**
     * Creates and returns a grid pane containing the laser object
     *
     * @return a gridPane that contains the laser board
     */
    private GridPane createGrid() {
        GridPane board = new GridPane();
        board.setHgap(5);
        board.setVgap(5);
        for (int row = 0; row < this.model.getHeight(); row++) {
            for (int col = 0; col < this.model.getWidth(); col++) {
                int tileSize = 50;
                int arc = tileSize / 4;
                StackPane stack = new StackPane();
                Rectangle rect = new Rectangle(tileSize, tileSize, Color.LIGHTGRAY);
                rect.setArcHeight(arc);
                rect.setArcWidth(arc);
                if (this.model.getGrid(row, col) != '.') {
                    rect.setFill(Color.BLACK);
                    Text text = new Text("" + this.model.getGrid(row, col));
                    text.setFill(Color.WHITE);
                    text.setFont(new Font("Arial", tileSize - 5));
                    text.setBoundsType(TextBoundsType.VISUAL);
                    stack.getChildren().add(rect);
                    stack.getChildren().add(text);
                } else {
                    stack.getChildren().add(rect);
                }

                board.add(stack, col, row);
            }
        }
        return board;
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(2);
        Button check = new Button("Check");
        Button hint = new Button("Hint");
        Button solve = new Button("Solve");
        Button restart = new Button("Restart");
        Button load = new Button("Load");
        buttonBox.getChildren().addAll(check, hint, solve, restart, load);
        return buttonBox;
    }
}
