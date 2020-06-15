package view;

import controller.CalculationThread;
import controller.SearchValueFunctionsThread;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ParametersForm {
    private final Group group = new Group();

    private Double x = 0.0;
    private Double rightThreshold;
    private Double n = 1.0;

    private final ConcurrentLinkedQueue<Double> numberQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Double> valueFunctionQueue = new ConcurrentLinkedQueue<>();

    private Thread numberCalculationThread = new Thread();
    private Thread valueFunctionCalculationThread = new Thread();

    public ParametersForm(ChartGroup chartGroup, Table table) {
        setNumberTempLine(chartGroup);
        Label emptyLabel = new Label("                                    Диапазон х");
        Label inputRangeFromLabel = new Label("                  от: ");
        TextField inputLeftThreshold = new TextField();
        HBox leftThresholdBox = new HBox(inputRangeFromLabel, inputLeftThreshold);

        Label inputRangeToLabel = new Label("                 до: ");
        TextField inputRightThreshold = new TextField();
        HBox rightThresholdBox = new HBox(inputRangeToLabel, inputRightThreshold);

        Button startBuildButton = new Button("           Начать построение         ");
        Button stopBuildButton = new Button("        Остановить построние      ");
        Label startSpaceLabel = new Label("          ");
        Label stopSpaceLabel = new Label("          ");
        HBox startButtonBox = new HBox(startSpaceLabel, startBuildButton, stopBuildButton);
        startButtonBox.setSpacing(15);
        HBox stopButtonBox = new HBox(stopSpaceLabel, stopBuildButton);
        stopButtonBox.setSpacing(15);

        VBox buttonsGroup = new VBox(emptyLabel, leftThresholdBox, rightThresholdBox, startButtonBox, stopButtonBox);
        buttonsGroup.setSpacing(5);

        startBuildButton.setOnAction(actionEvent -> {
            if (!numberCalculationThread.isAlive()) {
                x = Double.parseDouble(inputLeftThreshold.getText());
                if (Double.parseDouble(inputRightThreshold.getText()) >= x) {
                    rightThreshold = Double.parseDouble(inputRightThreshold.getText());
                    numberQueue.clear();
                    table.clearTable();
                    chartGroup.createNewNumberSeries("2 * x - 5", x, rightThreshold);
                    numberCalculationThread = new Thread(new CalculationThread(x, rightThreshold, numberQueue));
                    numberCalculationThread.start();
                }
                else {
                    errorAlert();
                }
            }
            if (!valueFunctionCalculationThread.isAlive()) {
                valueFunctionQueue.clear();
                valueFunctionCalculationThread = new Thread(new SearchValueFunctionsThread(rightThreshold, x, valueFunctionQueue, table));
                valueFunctionCalculationThread.start();
            }
        });

        stopBuildButton.setOnAction(actionEvent -> {
            if (!numberCalculationThread.isInterrupted()) {
                numberCalculationThread.interrupt();
                valueFunctionCalculationThread.interrupt();
                numberQueue.clear();
                valueFunctionQueue.clear();
                table.clearTable();
                inputLeftThreshold.clear();
                inputRightThreshold.clear();
            }
        });
        group.getChildren().addAll(buttonsGroup);
    }

    public Group getGroup() {
        return group;
    }

    private void errorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setContentText("Введите корректные данные!");
        alert.showAndWait();
    }

    private void setNumberTempLine(ChartGroup chartGroup) {
        Timeline numberTempLine = new Timeline();
        numberTempLine.setCycleCount(Timeline.INDEFINITE);
        numberTempLine.getKeyFrames().add(new KeyFrame(Duration.millis(0.1),
                actionEvent -> {
                    if (!numberQueue.isEmpty()) {
                        chartGroup.updateNumberSeriesList(x, numberQueue.poll());
                        x += 0.01;
                    }
                }));
        numberTempLine.play();
    }
}
