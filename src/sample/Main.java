package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.BinaryOperator;

public class Main extends Application {

    Label exprDisplay, resultDisplay;
    Button calc;
    Button input0, input1;
    Button opAdd, opSub, opMul, opDiv, opMod, clear;
    boolean argChanged = false;
    BigInteger accumulator = new BigInteger("0"), arg = new BigInteger("0");
    BinaryOperator<BigInteger> op = (BigInteger acc, BigInteger arg) -> arg; // current operation

    private void applyOp(BinaryOperator<BigInteger> op) {
        try { // for proper operators and arguments, accumulator is updated, arg is left intact
            if (argChanged) {
                accumulator = this.op.apply(accumulator, arg);
                resultDisplay.setText(accumulator.toString(2));
                argChanged = false;
                arg = new BigInteger("0");
                exprDisplay.setText(arg.toString(2));
            }
            this.op = op;
        } catch (ArithmeticException e) { // doesn't update anything if operation failed.
            System.out.println("operation failed");
            return;
        }
    }

    private void updateArg(BigInteger value) {
        argChanged = true;
        arg = value;
        exprDisplay.setText(arg.toString(2));
    }

    private void build(GridPane root) {
        root.setStyle("-fx-background-color: #bbb;");

        exprDisplay = new Label("0");
        exprDisplay.setStyle("-fx-background-color: #70b5a1;");
        resultDisplay = new Label("0");
        Arrays.asList(exprDisplay, resultDisplay).forEach((x) -> {
            x.setMaxWidth(Double.MAX_VALUE);
            x.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(x, Priority.ALWAYS);
            x.setAlignment(Pos.BASELINE_RIGHT);
            x.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 20));
        });

        calc = new Button("=");
        calc.setOnAction((e) -> applyOp(this.op));
        input0 = new Button("0");
        input0.setOnAction((e) -> updateArg(arg.shiftLeft(1)));
        input1 = new Button("1");
        input1.setOnAction((e) -> updateArg(arg.shiftLeft(1).setBit(0)));

        opAdd = new Button("+");
        opAdd.setOnAction((e) -> applyOp(BigInteger::add));
        opSub = new Button("-");
        opSub.setOnAction((e) -> applyOp(BigInteger::subtract));
        opMul = new Button("*");
        opMul.setOnAction((e) -> applyOp(BigInteger::multiply));
        opDiv = new Button("/");
        opDiv.setOnAction((e) -> applyOp(BigInteger::divide));
        opMod = new Button("%");
        opMod.setOnAction((e) -> applyOp(BigInteger::mod));
        clear = new Button("C");
        clear.setOnAction((e) -> {
                accumulator = new BigInteger("0");
                resultDisplay.setText("0");
                arg = new BigInteger("0");
                exprDisplay.setText("0");
                argChanged = false;
                op = (BigInteger acc, BigInteger arg) -> arg;

        });

        Arrays.asList(exprDisplay, calc, resultDisplay, input0, input1, opAdd, opDiv, opMod, opMul, opSub, clear).forEach((x) -> {
            GridPane.setHgrow(x, Priority.ALWAYS);
            GridPane.setVgrow(x, Priority.ALWAYS);
            x.setPrefWidth(120);
            x.setMaxWidth(Double.MAX_VALUE);
            x.setPrefHeight(60);
            x.setMaxHeight(Double.MAX_VALUE);
            x.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 20));

        });

        root.add(exprDisplay, 0, 0, 4, 1);
        root.add(calc, 0, 1);
        root.add(resultDisplay, 1, 1, 3, 1);
        root.add(input0, 0, 2);
        root.add(input1, 0, 3);
        root.add(opAdd, 1, 2);
        root.add(opSub, 1, 3);
        root.add(opMul, 2, 2);
        root.add(opDiv, 2, 3);
        root.add(opMod, 3, 2);
        root.add(clear, 3, 3);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = new GridPane();
        primaryStage.setTitle("Binary calculator");
        primaryStage.setScene(new Scene(root, 300, 300));
        //primaryStage.setResizable(false);
        root.setMinSize(300, 300);
        build(root);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
