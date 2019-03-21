package tooster.L2;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.*;

class Renderable {

    private static final String numberCssClass = "number-label";
    private static final String zeroCssClass = "zero-label";
    private static final String primeCssClass = "prime-label";
    private static final String compositeCssClass = "composite-label";

    private final static List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97);
    private Actor actor;
    Label label;

    Renderable(Actor actor) {
        this.actor = actor;
        label = new Label(Integer.toString(actor.getActorId()));
        if (actor.getActorId() == 0)
            label.getStyleClass().addAll(numberCssClass, zeroCssClass);
        else if (primes.contains(actor.getActorId()))
            label.getStyleClass().addAll(numberCssClass, primeCssClass);
        else
            label.getStyleClass().addAll(numberCssClass, compositeCssClass);

        int[] cords = actor.getField().getCords();
        Controller.getInstance().getGrid().add(label, cords[1], cords[0]);
    }

    void moveAnimation(Model.Field nextField) {
        // TODO animation instead of instant swapping
        GridPane grid = Controller.getInstance().getGrid();
        int[] cords = nextField.getCords();
        GridPane.setRowIndex(label, cords[0]);
        GridPane.setColumnIndex(label, cords[1]);
    }
}
