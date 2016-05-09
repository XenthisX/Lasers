package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
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
     * The UI's connection to the model
     */
    private LasersModel model;
    private String modelOut;
    private GridPane board;
    private Label title;

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
     * Initialization function to add components to the stage
     *
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) {
        // TODO
        BorderPane main = new BorderPane();

        /** Set up title */
        title = new Label("Welcome to the LasersGUI");
        title.setFont(new Font("Helvectiva", 15));
        main.setTop(title);
        title.managedProperty().bind(title.visibleProperty());

        /** Set up grid */
        board = new GridPane();
        board.setVgap(5);
        board.setHgap(5);
        loadBoard(-1, -1);
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
        init(primaryStage);  // do all your UI initialization here

        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg) {
        modelOut = (String) arg;
        title.setText((String) arg);
        updateBoard();
    }

    private void updateBoard() {
        model.updateBeams();
        loadBoard(-1, -1);
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(2);
        Button check = new Button("Check");
        check.setOnAction(MouseEvent -> verify());
        Button hint = new Button("Hint");
        hint.setOnAction(MouseEvent -> hint());
        Button solve = new Button("Solve");
        Button restart = new Button("Restart");
        restart.setOnAction(MouseEvent -> reset());
        Button load = new Button("Load");
        load.setOnAction(MouseEvent -> loadNew());
        buttonBox.getChildren().addAll(check, hint, solve, restart, load);
        return buttonBox;
    }

    private void hint() {

    }

    private void verify() {
        model.verify();
        if (modelOut.startsWith("Error verifying at: (")) {
            String temp = modelOut.toLowerCase();
            temp = temp.replaceAll("[^\\d,]+", "");
            int[] coords = Arrays.stream(temp.split(",")).mapToInt(Integer::parseInt).toArray();
            loadBoard(coords[0], coords[1]);
        }
    }

    private void loadNew() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Safe File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());

        try {
            String filename = file.getPath();
            board.getChildren().clear();
            this.model.updateModel(filename);
            model.updateBeams();
            loadBoard(-1, -1);


        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Function to reset the board, called when the restart button is pressed.
     */
    private void reset() {
        model.reset();
        loadBoard(-1, -1);
    }

    /**
     * Function that loads the board into the GridPane board object. This is used on the redraw of the board as well
     * as on the initial initialization of the program.
     */
    private void loadBoard(int rowS, int colS) {
        for (int row = 0; row < this.model.getHeight(); row++) {
            for (int col = 0; col < this.model.getWidth(); col++) {
                int tileSize = 40;
                int arc = 5;
                StackPane stack = new StackPane();
                /** Setup background fill */
                Rectangle background = new Rectangle(tileSize, tileSize, Color.LIGHTGRAY);
                background.setArcWidth(arc);
                background.setArcHeight(arc);

                /** Setup rectangle */
                RectangleGrid rect = new RectangleGrid(tileSize, tileSize, Color.TRANSPARENT, row, col);
                rect.setArcHeight(arc);
                rect.setArcWidth(arc);

                /** Setup text */
                Text text = new Text("");
                text.setFill(Color.WHITE);
                text.setFont(new Font("Arial", tileSize - 5));
                text.setBoundsType(TextBoundsType.VISUAL);

                if ("01234X".indexOf(this.model.getGrid(row, col)) != -1) { // if it's a black tile
                    if (row == rowS && col == colS) {
                        rect.setFill(Color.RED);
                    } else {
                        rect.setFill(Color.BLACK);
                    }
                    //rect.setFill(Color.BLACK);
                    if (this.model.getGrid(row, col) != 'X') {
                        text.setText(this.model.getGrid(row, col) + "");
                    }

                    /** This else loop is responsible for dealing with coloring lasers and beams */
                } else { // it's either a laser or a beam
                    if (this.model.getGrid(row, col) == '*') {
                        if (row == rowS && col == colS) {
                            background.setFill(Color.RED);
                        } else {
                            background.setFill(Color.YELLOW);
                        }
                        Image beam = new Image("gui/resources/beam2.png");
                        ImagePattern fill = new ImagePattern(beam);
                        rect.setFill(fill);

                    } else if (this.model.getGrid(row, col) == 'L') {
                        if (row == rowS && col == colS) {
                            background.setFill(Color.RED);
                        } else {
                            background.setFill(Color.ORANGE);
                        }
                        Image laser = new Image("gui/resources/laser2.png");
                        ImagePattern fill = new ImagePattern(laser);
                        rect.setFill(fill);
                    } else {
                        if (row == rowS && col == colS) {
                            background.setFill(Color.RED);
                        } else {
                            background.setFill(Color.LIGHTGRAY);
                        }
                    }

                }
                stack.getChildren().add(background);
                stack.getChildren().add(rect);
                stack.getChildren().add(text);
                stack.setOnMouseClicked(MouseClickEvent -> updateLaser(stack));
                board.add(stack, col, row);
            }
        }
    }

    /**
     * Given a RectangleGrid object will check the position and add a laser to the model, as well as changing the
     * icon to a laser or back to the blank icon
     *
     * @param stack the stack object to change
     */
    private void updateLaser(StackPane stack) {
        RectangleGrid rect = (RectangleGrid) stack.getChildren().get(1);
        Character curr = this.model.getGrid(rect.getRow(), rect.getCol());
        if (curr == 'L') {
            //ImageView background = (ImageView) stack.getChildren().get(0);
            this.model.remove(rect.getRow(), rect.getCol());
            rect.setFill(Color.LIGHTGRAY);
        } else if (curr == '.' || curr == '*') {
            //ImageView background = (ImageView) stack.getChildren().get(0);
            this.model.add(rect.getRow(), rect.getCol());
            Image laser = new Image("gui/resources/laser.png");
            ImagePattern fill = new ImagePattern(laser);
            rect.setFill(fill);

        } else {
            this.model.add(rect.getRow(), rect.getCol());
        }
    }
}
