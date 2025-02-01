package de.junaeisenhauer.puzzlesolver.controller;

import de.junaeisenhauer.puzzlesolver.algorithm.Node;
import de.junaeisenhauer.puzzlesolver.algorithm.PuzzleNode;
import de.junaeisenhauer.puzzlesolver.algorithm.strategy.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The controller for puzzle solver which handles user inputs, accesses the solve strategies and updates the view.
 */
public class PuzzleSolverController {

    @FXML
    private Pane tiles;
    @FXML
    private Button shuffle;
    @FXML
    private Button reset;
    @FXML
    private Button solve;
    @FXML
    private Button cancel;
    @FXML
    private ProgressIndicator progress;

    @FXML
    private TextField gridSize;
    @FXML
    private ComboBox<String> algorithm;
    @FXML
    private TextField shuffleCount;
    @FXML
    private Slider tileSpeed;

    @FXML
    private Text solveDuration;
    @FXML
    private Text solveDepth;

    private Thread solveThread;
    private Thread walkAlongPathThread;

    private PuzzleNode.PuzzleSize size;
    private Node<int[]> currentNode;

    private TileController[] tileController;

    private Map<String, SolveStrategy> strategies;

    @FXML
    public void initialize() {
        size = new PuzzleNode.PuzzleSize(3, 3);
        Node<int[]> initNode = getInitNode(size);
        initializeTiles();
        displayNode(initNode);
        initializeSettings();
        Platform.runLater(() -> tiles.getScene().getWindow().sizeToScene());
    }

    @FXML
    public void onShuffle(ActionEvent event) {
        int shuffleCount;
        try {
            shuffleCount = Integer.parseInt(this.shuffleCount.getText());
        } catch (NumberFormatException ignored) {
            return;
        }

        SolveStrategy strategy = new ShuffleStrategy(shuffleCount);
        disableActionButtons();
        cancel.setDisable(false);
        solve(strategy, result -> {
            if (result != null) {
                Stack<Node<int[]>> resultPath = getResultPath(result);
                walkAlongPath(resultPath, aVoid -> {
                    enableActionButtons();
                    cancel.setDisable(true);
                });
            }
        });
    }

    @FXML
    public void onReset(ActionEvent event) {
        disableActionButtons();
        Node<int[]> initNode = getInitNode(size);
        displayNode(initNode);
        enableActionButtons();
    }

    @FXML
    public void onSolve(ActionEvent event) {
        SolveStrategy strategy = strategies.get(algorithm.getSelectionModel().getSelectedItem());
        if (strategy != null) {
            disableActionButtons();
            algorithm.setDisable(true);
            progress.setVisible(true);
            cancel.setDisable(false);
            long solveStartTime = System.currentTimeMillis();
            solve(strategy, result -> {
                long solveFinishTime = System.currentTimeMillis();
                displaySolveDuration(solveStartTime, solveFinishTime);
                progress.setVisible(false);
                if (result != null) {
                    Stack<Node<int[]>> resultPath = getResultPath(result);
                    solveDepth.setText((resultPath.size() - 1) + "");
                    walkAlongPath(resultPath, aVoid -> {
                        enableActionButtons();
                        algorithm.setDisable(false);
                        cancel.setDisable(true);
                    });
                }
            });
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        if (solveThread != null) {
            solveThread.stop();
            solveThread = null;
        }
        if (walkAlongPathThread != null) {
            walkAlongPathThread.interrupt();
            walkAlongPathThread = null;
        }

        progress.setVisible(false);
        enableActionButtons();
        algorithm.setDisable(false);
        cancel.setDisable(true);
    }

    double getTileMoveSpeed() {
        double moveSpeed = tileSpeed.getValue();
        if (moveSpeed == tileSpeed.getMax()) {
            // instant move
            moveSpeed = 0;
        }
        return moveSpeed;
    }

    private Node<int[]> getInitNode(PuzzleNode.PuzzleSize size) {
        int length = size.getWidth() * size.getHeight();
        int[] initState = new int[length];
        for (int i = 0; i < length - 1; i++) {
            initState[i] = i + 1;
        }
        initState[length - 1] = 0;

        return new PuzzleNode(null, size, initState, null);
    }

    private void initializeTiles() {
        int length = size.getWidth() * size.getHeight();
        tileController = new TileController[length];
        for (int i = 0; i < length - 1; i++) {
            initializeTile(i + 1, i);
        }
    }

    private void initializeTile(int id, int position) {
        // load localization
        ResourceBundle resourceBundle = ResourceBundle.getBundle("localization/PuzzleSolver");

        // load fxml file
        URL tileResource = getClass().getClassLoader().getResource("view/Tile.fxml");
        FXMLLoader loader = new FXMLLoader(tileResource, resourceBundle);
        try {
            Parent tile = loader.load();
            TileController controller = loader.getController();
            controller.initializeTile(this, id, size, position);
            tiles.getChildren().add(tile);
            tileController[id] = controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeSettings() {
        // grid size
        TextFormatter<String> gridSizeFormatter =
                new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null);
        gridSize.setTextFormatter(gridSizeFormatter);
        gridSize.setText(size.getWidth() + "");
        gridSize.textProperty().addListener((observableValue, oldText, newText) -> {
            if (!newText.isEmpty() && !newText.equals(size.getWidth() + "")) {
                int widthAndHeight = Integer.parseInt(newText);
                size = new PuzzleNode.PuzzleSize(widthAndHeight, widthAndHeight);
                tiles.getChildren().clear();
                initializeTiles();

                Node<int[]> initNode = getInitNode(size);
                Platform.runLater(() -> {
                    displayNode(initNode);
                });
            }
        });

        // algorithms
        ResourceBundle resourceBundle = ResourceBundle.getBundle("localization/PuzzleSolver");

        strategies = new HashMap<>();
        String breadthSearchName = resourceBundle.getString("settings.strategy.breadth");
        strategies.put(breadthSearchName, new BreadthSearch());
        strategies.put(resourceBundle.getString("settings.strategy.depth"), new DepthSearch());
        strategies.put(resourceBundle.getString("settings.strategy.iterative"), new IterativeDeepening());
        strategies.put(resourceBundle.getString("settings.strategy.astar"), new AStarSearch());

        ObservableList<String> strategyItems =
                FXCollections.observableArrayList(strategies.keySet().stream().sorted().collect(Collectors.toList()));
        algorithm.setItems(strategyItems);
        algorithm.getSelectionModel().select(breadthSearchName);

        // shuffle count
        TextFormatter<String> shuffleCountFormatter =
                new TextFormatter<>(change -> change.getText().matches("[0-9]*") ? change : null);
        shuffleCount.setTextFormatter(shuffleCountFormatter);
        shuffleCount.setText("5");

        // tile speed
        tileSpeed.setMin(1);
        tileSpeed.setMax(10);
    }

    private void enableActionButtons() {
        shuffle.setDisable(false);
        reset.setDisable(false);
        solve.setDisable(false);
    }

    private void disableActionButtons() {
        shuffle.setDisable(true);
        reset.setDisable(true);
        solve.setDisable(true);
    }

    private void solve(SolveStrategy strategy, Consumer<Node<int[]>> callback) {
        Node<int[]> startNode = new PuzzleNode(null, size, currentNode.getState(), null);
        solveThread = new Thread(() -> {
            Node<int[]> result = null;
            try {
                result = strategy.solve(startNode);
            } catch (OutOfMemoryError | StackOverflowError e) {
                e.printStackTrace();
            }

            callback.accept(result);
            solveThread = null;
        });
        solveThread.start();
    }

    private Stack<Node<int[]>> getResultPath(Node<int[]> result) {
        Stack<Node<int[]>> resultPath = new Stack<>();
        while (result != null) {
            resultPath.push(result);
            result = result.getParent();
        }

        return resultPath;
    }

    private void walkAlongPath(Stack<Node<int[]>> resultPath, Consumer<Void> callback) {
        walkAlongPathThread = new Thread(() -> {
            for (int depth = 0; !resultPath.empty(); depth++) {
                Node<int[]> node = resultPath.pop();
                boolean interrupted = displayNodeChange(node);
                if (interrupted) {
                    break;
                }
            }

            callback.accept(null);
        });
        walkAlongPathThread.start();
    }

    private void displayNode(Node<int[]> node) {
        int[] state = node.getState();
        currentNode = node;

        for (int i = 0; i < state.length; i++) {
            int id = state[i];
            if (id != 0) {
                tileController[id].setPosition(i);
            }
        }
    }

    private boolean displayNodeChange(Node<int[]> node) {
        int[] state = node.getState();
        int[] currentState = currentNode.getState();
        int tileId = -1, newTilePosition = -1;

        for (int i = 0; i < state.length; i++) {
            if (state[i] == currentState[i]) {
                continue;
            }
            if (state[i] == 0) {
                tileId = currentState[i];
            } else {
                newTilePosition = i;
            }
        }

        currentNode = node;

        if (tileId == -1 || newTilePosition == -1) {
            // nothing has changed
            return false;
        }

        return tileController[tileId].move(newTilePosition);
    }

    private void displaySolveDuration(long startTime, long finishTime) {
        long duration = finishTime - startTime;
        String timePattern;
        if (duration < 1000 * 60 * 60) {
            // less then 1 hour
            timePattern = "mm:ss,SSS";
        } else {
            timePattern = "hh:mm:ss";
        }

        SimpleDateFormat format = new SimpleDateFormat(timePattern);

        String displayDuration = format.format(new Date(duration));
        solveDuration.setText(displayDuration);
    }

}
