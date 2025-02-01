package de.junaeisenhauer.puzzlesolver.controller;

import de.junaeisenhauer.puzzlesolver.algorithm.PuzzleNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * The tile controller which handles a single tile of the grid.
 */
public class TileController {

    @FXML
    private StackPane tile;
    @FXML
    private Text tileId;

    private PuzzleSolverController puzzleSolverController;
    private PuzzleNode.PuzzleSize size;

    void initializeTile(PuzzleSolverController puzzleSolverController, int id, PuzzleNode.PuzzleSize size,
                        int position) {
        this.puzzleSolverController = puzzleSolverController;
        this.size = size;

        tileId.setText(id + "");
        Platform.runLater(() -> setPosition(position));
    }

    boolean move(int newPosition) {
        setPosition(newPosition);

        double tileMoveSpeed = puzzleSolverController.getTileMoveSpeed();
        if (tileMoveSpeed > 0) {
            try {
                Thread.sleep((long) ((1 / tileMoveSpeed) * 1000));
            } catch (InterruptedException ignored) {
                return true;
            }
        }

        return false;
    }

    void setPosition(int position) {
        int x = position % size.getWidth();
        int y = (position - x) / size.getWidth();

        AnchorPane.setLeftAnchor(tile, x * tile.getPrefWidth());
        AnchorPane.setTopAnchor(tile, y * tile.getPrefHeight());
    }

}
