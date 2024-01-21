import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import static java.lang.System.out;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {


    final Random rand = new Random();



    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;    // false by default



        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;

        SatisfactionForEveryActor(world, threshold);
        nullsAndActorSwitch(world);










        // TODO update world
    }

    int amtNulls(Actor[][] world){
        int count = 0;
        for (int row = 0; row < world.length; row++){
            for (int col = 0; col < world.length; col++){
                if (world[row][col] == null){
                    count++;
                }
            }
        } return count;
    }
    void nullsAndActorSwitch(Actor[][] world){
        int [] nulls = shuffle(Nulls(world));
        int i = 0;
        for (int row = 0; row < world.length; row++){
            for (int col = 0; col < world.length; col++){
                if (world[row][col] != null && !world[row][col].isSatisfied) {
                    if (i < nulls.length) {
                        Actor tmp = world[row][col];
                        world[row][col] = null;
                        int rowValue = nulls[i] / world.length;
                        int colValue = nulls[i] % world.length;
                        world[rowValue][colValue] = tmp;
                        i++;
                    }

                }
            }
        }

    }


    //Go through the world and check which are nulls, save the coordinates of the nulls in an array
    int[] Nulls(Actor[][] world){
        int [] NullCoord = new int[amtNulls(world)];
        int j = 0;
        for (int row = 0; row < world.length; row++){
            for (int col = 0; col < world.length;col++){
                if (world[row][col] == null){
                    NullCoord[j] = (row *  world.length) + col;
                    j++;
                }
            }
        }
        return NullCoord;
    }

    //Shuffles the index of nulls
    int[] shuffle(int[] nulls){
        for (int i = nulls.length; i > 0; i--){
            int j = rand.nextInt(i);
            int tmp = nulls[i -1];
            nulls[i -1] = nulls[j];
            nulls[j] = tmp;
        }
        return nulls;
    }

    //Check for every Actor and se if they are satisfied
    void SatisfactionForEveryActor(Actor[][] world, double threshold){
        for (int row = 0; row < world.length; row++){
            for (int col = 0; col < world.length; col++){
                if (world[row][col] != null){
                    if (SatisfactionforOneActor(world, threshold, row, col)){
                        world[row][col].isSatisfied = true;
                    }
                }
            }
        }
    }

    //Check if one Actor is satisfied or not
    boolean SatisfactionforOneActor(Actor[][] world, double threshold, int row, int col) {
        int sameColor = 0;
        int differentColor = 0;

        for (int r = row - 1; r <= row + 1; r++){
            for (int c = col - 1; c <= col + 1; c++){
                if (isValidLocation(world.length, r, c)){
                    if (world[r][c] != null && world[row][col] != world[r][c]){
                        if (world[row][col].color == world[r][c].color){
                                sameColor++;
                        }
                        else {
                            differentColor++;
                        }
                    }
                }
            }
        }
        if(sameColor + differentColor == 0){
            return true;
        }
        else if (sameColor/(sameColor + differentColor) >= threshold){
            return true;
        } else {
            return false;
        }
    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 900;   // Should also try 90 000
        Actor[] actors = getActorArray(nLocations, dist);
        Shuffle(actors);
        world = toMatrix(actors);







        // TODO initialize the world

        // Should be last
        fixScreenSize(nLocations);
    }

    // ---------------  Methods ------------------------------

    // TODO Many ...

    //Switch places with the nulls and the unsatisfied actor




    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    // TODO (general method possible reusable elsewhere)
    //1. Crate Array of all actors
    Actor[] getActorArray(int nLocations, double[] dist){
        Actor [] actor = new Actor[nLocations];
        int nRed = (int) round( nLocations * dist[0]);
        int nBlue = (int) round( nLocations * dist[1]);
        int nWhite = (int) round( nLocations * dist[2]);

        int i = 0;
        while (nRed > 0){
            actor[i] = new Actor(Color.RED);
            i++;
            nRed--;
        } while(i < nLocations * dist[2]){
            actor[i] = new Actor(Color.BLUE);
            i++;
        } return actor;
    }

    void Shuffle(Actor[] arr){
        for (int i = arr.length; i > 0; i--){
            int j = rand.nextInt(i);
            Actor tmp = arr[j];
            arr[j] = arr[i-1];
            arr[i-1] = tmp;
        }
    }

    Actor[][] toMatrix(Actor[] arr){
        int size = (int)round(sqrt(arr.length));
        Actor[][] matrix = new Actor[size][size];
        int k = 0;
        for (int row = 0; row < size; row++){
            for (int col = 0; col < size; col++){
                matrix[row][col] = arr[k++];
            }
        } return matrix;
    }


    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, new Actor(Color.BLUE), null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing
        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));
        // TODO

        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 450_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
