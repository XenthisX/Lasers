package gui;

import backtracking.Backtracker;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Coordinate;
import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author Elijah Bosley, Stefan Marchart
 */
public class LasersGUI extends Application implements Observer {
    /**
     * The UI's connection to the model
     */
    private LasersModel model;
    private String modelOut;
    private GridPane board;
    private Text title;
    private int width;
    private DoubleProperty fontSize = new SimpleDoubleProperty(10);
    private IntegerProperty tilesSize = new SimpleIntegerProperty(10);

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
        VBox main = new VBox();
        Scene scene = new Scene(main);

        width = model.getWidth();
        fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(50));
        tilesSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(width * 3));
        stage.setHeight(Screen.getPrimary().getBounds().getHeight() / 3);
        stage.setWidth(stage.getHeight());

        /** Set up title */
        title = new Text("Welcome to the LasersGUI");
        title.setFont(new Font("Helvectiva", fontSize.doubleValue()));
        title.wrappingWidthProperty().bind(scene.widthProperty());
        main.getChildren().add(title);
        title.managedProperty().bind(title.visibleProperty());
        title.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));
        title.setTextAlignment(TextAlignment.CENTER);
        /** Set up grid */
        board = new GridPane();
        board.setVgap(5);
        board.setHgap(5);
        loadBoard(-1, -1);
        board.setAlignment(Pos.CENTER);
        main.getChildren().add(board);

        /** Add buttons */
        HBox buttons = createButtons();
        buttons.setAlignment(Pos.CENTER);
        main.getChildren().add(buttons);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        /** Initialize stage */
        main.setPadding(new Insets(10, 10, 10, 10));
        main.setAlignment(Pos.CENTER);
        stage.sizeToScene();
        stage.setScene(scene);
        scene.widthProperty().addListener(ChangeListener -> updateBoard());
        scene.heightProperty().addListener(ChangeListener -> updateBoard());
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


    /**
     * Simple helper function that simply updates the beams and then reloads the game board
     */
    private void updateBoard() {
        model.updateBeams();
        width = model.getWidth();
        loadBoard(-1, -1);
    }

    /**
     * Function that creates the buttons, and returns an HBox containing them
     *
     * @return HBox node that contains all of the buttons
     */
    private HBox createButtons() {
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(2);
        Button check = new Button("Check");
        check.setOnAction(MouseEvent -> verify());
        Button hint = new Button("Hint");
        hint.setOnAction(MouseEvent -> hint());
        Button solve = new Button("Solve");
        solve.setOnAction(MouseEvent -> solve());
        Button restart = new Button("Restart");
        restart.setOnAction(MouseEvent -> reset());
        Button load = new Button("Load");
        load.setOnAction(MouseEvent -> loadNew());
        buttonBox.getChildren().addAll(check, hint, solve, restart, load);
        buttonBox.autosize();
        return buttonBox;
    }

    private void solve() {
        Backtracker backtracker = new Backtracker(false);
        this.reset();
        Optional temp = backtracker.solve(this.model);
        if (temp.isPresent()) {
            LasersModel replacement = (LasersModel) temp.get();
            System.out.println(replacement);
            this.model.replaceModel(replacement);
            title.setText("Solved!");
            loadBoard(-1, -1);
        } else {

            title.setText("This safe has no solution!");
        }


    }

    /**
     * Uses a backtracker to solve the puzzle, then chooses an item from the solutionList to display as a the next step
     */
    private void hint() {
        Backtracker backtracker = new Backtracker(false);
        Optional temp = backtracker.solve(this.model);
        if (temp.isPresent()) {
            LasersModel solution = (LasersModel) temp.get();

            ArrayList<Coordinate> solList = solution.getLasers();
            for (Coordinate cord : solList) {
                if (!model.getLasers().contains(cord)) {
                    model.add(cord.getRow(), cord.getCol());
                    title.setText("Hint: " + title.getText());
                    model.updateBeams();
                    return;
                }
            }
            title.setText("Hint: no next step!");

        }
    }

    /**
     * Verification function that sends the coordinates received from the model to the loadBoard function which will
     * then color the tile that was not verified
     */
    private void verify() {
        model.verify();
        if (modelOut.startsWith("Error verifying at: (")) {
            String temp = modelOut.toLowerCase();
            temp = temp.replaceAll("[^\\d,]+", "");
            int[] coords = Arrays.stream(temp.split(",")).mapToInt(Integer::parseInt).toArray();
            loadBoard(coords[0], coords[1]);
        }
    }

    /**
     * Helper function responsible for loading a new board. Uses the model's updateModel function and the loadBoard
     * function
     */
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
            System.out.println("Error on Load");
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
        for (int i = 0; i < board.getChildren().size() - 1; i++) {
            board.getChildren().remove(0);
        }

        for (int row = 0; row < this.model.getHeight(); row++) {
            for (int col = 0; col < this.model.getWidth(); col++) {
                int tileSize = tilesSize.getValue();
                int arc = tileSize / 3;
                StackPane stack = new StackPane();
                /** Setup background fill */
                Rectangle background = new Rectangle(tileSize, tileSize, Color.LIGHTGRAY);
                background.setArcWidth(arc);
                background.setArcHeight(arc);
                background.setSmooth(true);
                /** Setup rectangle */
                RectangleGrid rect = new RectangleGrid(tileSize, tileSize, Color.TRANSPARENT, row, col);
                rect.setArcHeight(arc);
                rect.setArcWidth(arc);
                rect.setSmooth(true);
                /** Setup text */
                Text text = new Text("");
                text.setFill(Color.WHITE);
                text.setFont(new Font("Arial", tileSize - 5));
                text.setBoundsType(TextBoundsType.VISUAL);

                if ("01234X".indexOf(this.model.getGrid(row, col)) != -1) { // if it's a black tile
                    if (row == rowS && col == colS) {
                        background.setFill(Color.RED);
                    } else {
                        background.setFill(Color.BLACK);
                    }
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

                board.setMaxHeight(tileSize * 40);
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
