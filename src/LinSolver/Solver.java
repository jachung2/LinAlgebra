/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LinSolver;

/**
 * LinSolver Application
 * @author Jon
 */
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.text.FontWeight;
import javafx.scene.input.KeyCode;

import java.util.Stack;

public class Solver extends Application{
    private BorderPane borderPane = new BorderPane();
    private TextArea A = new TextArea();
    private GridPane B = new GridPane();
    private HBox C = new HBox();
    private Matrix m;
    private Label label = new Label();
    private Label errorLabel = new Label("Invalid input");
    private TextArea outputMatrix = new TextArea();
    private TextField fontSize = new TextField();
    private Font fontOutput;
    private int defFontSize = 12;
    private static String font = "Lucida Sans Unicode";

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String lastRequest;

    @Override
    public void start(Stage primaryStage) {
        //Setup A
        A.setMaxSize(480, 500);
        A.setPadding(new Insets(4));
        A.getStyleClass().add("text-area");
        A.setPromptText("Enter Matrix here:");

        //These lines set the inputText
        Font fontInput = Font.font("Helvetica", FontWeight.NORMAL, 24);
        A.setStyle("-fx-text-fill: slategray; -fx-text-box-border: slategray ; -fx-focus-color: black;");
        A.setFont(fontInput);

        //Setup B
        B.setPadding(new Insets(20));
        B.setHgap(20);
        B.setVgap(30);
        B.setStyle("-fx-background-color: #00295b");

        //Buttons
        Button RREF = new Button("RREF");
        Button REF = new Button("REF");
        Button DET = new Button("DET");
        Button ADJ = new Button("ADJ");
        Button INV = new Button("INV");
        Button NULLM = new Button("NullM");
        Button CLEAR = new Button("CLR");

        Image undoIcon = new Image(getClass().getResourceAsStream("action-undo.png"),
                20,
                20,
                true,
                true);
        Image redoIcon = new Image(getClass().getResourceAsStream("action-redo.png"),
                20,
                20,
                true,
                true);

        Button UNDO = new Button();
        Button REDO = new Button();
        UNDO.setGraphic(new ImageView(undoIcon));
        REDO.setGraphic(new ImageView(redoIcon));

        RREF.getStyleClass().setAll("button");


        //Register Handlers
        RREF.setOnAction(e -> {
            process(Action.RREF);
        });
        REF.setOnAction(e -> {
            process(Action.REF);
        });
        DET.setOnAction(e -> {
            process(Action.DET);
        });
        ADJ.setOnAction(e -> {
            process(Action.ADJ);
        });
        INV.setOnAction(e -> {
            process(Action.INV);
        });
        NULLM.setOnAction(e -> {
            process(Action.NULLM);
        });
        UNDO.setOnAction(e -> {
            UNDO();
        });
        REDO.setOnAction(e -> {
            REDO();
        });
        CLEAR.setOnAction(e -> {
            CLEAR();
        });

        //Add Buttons to B
        B.add(RREF, 0, 0);
        B.add(REF, 1, 0);
        B.add(DET, 2, 0);
        B.add(ADJ, 0, 1);
        B.add(INV, 1, 1);
        B.add(NULLM, 2, 1);
        B.add(UNDO, 0, 2);
        B.add(REDO, 1, 2);
        B.add(CLEAR, 2, 2);

        //Style the fontSize TextField
        fontSize.setPromptText("Size");
        fontSize.setStyle("-fx-text-fill: black; -fx-text-box-border: slategray ; -fx-focus-color: black;");
        Font fontSizeInput = Font.font(font, FontWeight.BOLD, 12);
        fontSize.setFont(fontSizeInput);
        Label fontSizeLabel = new Label("Ft Size:");
        fontSizeLabel.setStyle("-fx-text-fill: slategray; -fx-text-box-border: slategray ; -fx-focus-color: black;");
        fontSizeLabel.setFont(Font.font(font, FontWeight.BOLD, 12));
        B.add(fontSize, 2, 7);
        B.add(fontSizeLabel, 1, 7);

        //Register fontSize
        fontSize.setOnKeyPressed((e) -> {
            if(e.getCode() == KeyCode.ENTER) {
                try {
                    defFontSize = Integer.parseInt(fontSize.getText());
                }
                catch(NumberFormatException ex) {
                    defFontSize = 12;
                }
                fontOutput = Font.font(font, FontWeight.LIGHT, defFontSize);
            }
            setupOutputMatrix();
        });

        //Setup C
        C.setMinSize(780, 240);
        C.setPadding(new Insets(20));
        C.setStyle("-fx-background-color: lightgray; -fx-text-box-border: slategray ; -fx-focus-color: black;");
        C.setSpacing(50);

        //Setup outputMatrix
        setupOutputMatrix();

        //Add Panes to borderPane
        borderPane.setCenter(B);
        borderPane.setLeft(A);
        borderPane.setBottom(C);
        BorderPane.setAlignment(C, Pos.CENTER);

        //Set my scene
        Scene scene = new Scene(borderPane, 780, 595);

        String packageName = "LinSolver";
        scene.getStylesheets().add(packageName + "/Buttons.css");
        scene.getStylesheets().add(packageName + "/TextArea.css");

        //Set my Stage
        primaryStage.setResizable(true);
        primaryStage.setTitle("Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupOutputMatrix() {
        outputMatrix.setMinSize(300, 160);
        outputMatrix.setMaxSize(610, 160);
        outputMatrix.setEditable(false);
        outputMatrix.setStyle("-fx-text-fill: black");
        fontOutput = Font.font(font, FontWeight.LIGHT, defFontSize);
        outputMatrix.setFont(fontOutput);
    }

    private void process(Action e) {
        try {
            processData();
            display(e);

        } catch (NoInputException ignored){

        } catch (Exception ex){
            displayError();
        }
    }

    private void processData() throws Exception{
        String input = A.getText();
        if (input.equals("")) throw new NoInputException();
        m = getMatrix(input);

    }

    private void displayError() {
        System.out.println("error");

        C.getChildren().removeAll(outputMatrix, errorLabel);
        System.out.println(C.getChildren().toString());
        if(!C.getChildren().contains(errorLabel)) {
            errorLabel.setFont(new Font(font, 24));
            errorLabel.setTextFill(Color.FIREBRICK);
            C.getChildren().add(errorLabel);
        }
    }

    private void display(Action e) {
        saveHistory();
        C.getChildren().removeAll(label, outputMatrix, errorLabel);

        if (e == Action.DET) {
            RationalBigInteger DET = Matrix.det(m);
            outputMatrix.setText(DET.toString());
        } else if (e == Action.INV) {
            if (!Matrix.det(m).toString().equals("0")) {
                outputMatrix.setText(m.getInverse().toString());
            } else {
                outputMatrix.setText("Inverse doesn't exist.");
            }
        } else {

            Matrix res;
            switch (e) {
                case RREF:
                    res = Matrix.RREFMatrix(m);
                    break;
                case REF:
                    res = Matrix.REFMatrix(m);
                    break;
                case ADJ:
                    res = Matrix.adj(m);
                    break;
                case NULLM:
                    res = Matrix.NullM(m);
                    break;
                default : res = null;

            }
            outputMatrix.setText(res.toString());
        }

        // Set label.
        label = new Label(String.format("%7s", e.toString() + ": "));
        setStyle(label);

        // Add all to bottom pane.
        C.getChildren().addAll(label, outputMatrix);
        C.setAlignment(Pos.TOP_LEFT);
    }

    private static Matrix getMatrix(String s) throws Exception{
        String[] rows = s.split("\n");
        int numRows = rows.length;
        int numColumns = rows[0].split(" +").length;
        RationalBigInteger[][] data = new RationalBigInteger[numRows][numColumns];
        for(int i = 0; i < numRows; i++) {
            String[] columns = rows[i].split(" +");
            if (columns.length != numColumns) throw new Exception();
            for(int j = 0; j < numColumns; j++) {
                if (columns[j].contains(".")) {
                    data[i][j] = new RationalBigInteger(Double.valueOf(columns[j]));
                }
                else {
                    data[i][j] = new RationalBigInteger(columns[j]);
                }
            }
        }

        return new Matrix(data);
    }

    private static void setStyle(Label l) {
        l.setFont(new Font(font, 24));
        l.setStyle("-fx-font-weight: bold");
        l.setTextFill(Color.DARKSLATEGREY);

    }

    private void saveHistory() {
        if (!A.getText().equals(lastRequest)) {
            undoStack.push(A.getText());
            lastRequest = A.getText();
        }
    }

    private void CLEAR() {
        saveHistory();
        A.clear();
        saveHistory();
    }

    private void UNDO() {
        if (!undoStack.empty()) {
            String prev = undoStack.pop();
            A.setText(prev);
            redoStack.push(prev);
        }
    }

    private void REDO() {
        if (!redoStack.empty()) {
            String prev = redoStack.pop();
            A.setText(prev);
            undoStack.push(prev);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}

enum Action {
    RREF,
    REF,
    DET,
    ADJ,
    INV,
    NULLM
}

class NoInputException extends Exception {
    NoInputException() {
        super("No Input");
    }
}
