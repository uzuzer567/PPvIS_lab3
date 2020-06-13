package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ChartGroup {

    private ObservableList<XYChart.Data<Double, Double>> numberSeriesData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<Double, Double>> valueFunctionSeriesData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series<Double, Double>> seriesList = FXCollections.observableArrayList();

    private final NumberAxis xAxis = new NumberAxis("x", 0, 50, 2);
    private final NumberAxis yAxis = new NumberAxis("y", 0, 100, 5);

    private final LineChart chart = new LineChart(xAxis, yAxis, seriesList);
    private final Group chartGroup = new Group();

    private double mouseOnChartX;
    private double mouseOnChartY;

    private int zoom = 0;

    public ChartGroup() {
        chart.setLayoutY(20);
        Button zoomIn = new Button(" Масштаб + ");
        zoomIn.setOnAction(actionEvent -> doZoomIn());
        Button zoomOut = new Button(" Масштаб - ");
        zoomOut.setOnAction(actionEvent -> doZoomOut());
        HBox zoomBox = new HBox(zoomIn, zoomOut);
        zoomBox.setLayoutX(190);
        zoomBox.setLayoutY(10);
        zoomBox.setSpacing(5);

        setChartPressAndDragEvents();

        chartGroup.getChildren().addAll(chart, zoomBox);
    }

    public Group getChartGroup() {
        return chartGroup;
    }

    public void updateNumberSeriesList(Double x, Double y) {
        numberSeriesData.add(new XYChart.Data<>(x, y));
    }

    public void createNewNumberSeries(String fx, Double leftThreshold, Double rightThreshold) {
        String seriesName = "f(x) = " + fx + ", x ∈ [" + leftThreshold + "; " + rightThreshold + "]";
        numberSeriesData = FXCollections.observableArrayList();
        XYChart.Series<Double, Double> series = new XYChart.Series<>(seriesName, numberSeriesData);
        seriesList.add(series);
    }

    private void doZoomIn() {
        xAxis.setLowerBound(xAxis.getLowerBound() + xAxis.getTickUnit());
        xAxis.setUpperBound(xAxis.getUpperBound() - xAxis.getTickUnit());
        yAxis.setLowerBound(yAxis.getLowerBound() + yAxis.getTickUnit());
        yAxis.setUpperBound(yAxis.getUpperBound() - yAxis.getTickUnit());
        zoom += 1;
    }

    private void doZoomOut() {
        xAxis.setLowerBound(xAxis.getLowerBound() - xAxis.getTickUnit());
        xAxis.setUpperBound(xAxis.getUpperBound() + xAxis.getTickUnit());
        yAxis.setLowerBound(yAxis.getLowerBound() - yAxis.getTickUnit());
        yAxis.setUpperBound(yAxis.getUpperBound() + yAxis.getTickUnit());
        zoom -= 1;
    }

    public void setChartScroll() {
        chart.setOnScroll(scrollEvent -> {
            if(scrollEvent.getDeltaY() > 0) {
                doZoomIn();
            }
            if (scrollEvent.getDeltaY() < 0) {
                doZoomOut();
            }
        });
    }

    public void clearChartScroll() {
        chart.setOnScroll(scrollEvent -> {});
    }

    private void setChartPressAndDragEvents() {
        chart.setOnMousePressed(mouseEvent -> {
            mouseOnChartX = mouseEvent.getX();
            mouseOnChartY = mouseEvent.getY();
        });

        chart.setOnMouseDragged(mouseEvent -> {
            xAxis.setLowerBound(xAxis.getLowerBound() + (mouseOnChartX - mouseEvent.getX()) / 1);
            xAxis.setUpperBound(xAxis.getUpperBound() + (mouseOnChartX - mouseEvent.getX()) / 1);
            mouseOnChartX = mouseEvent.getX();

            yAxis.setLowerBound(yAxis.getLowerBound() + (mouseEvent.getY() - mouseOnChartY) * 1);
            yAxis.setUpperBound(yAxis.getUpperBound() + (mouseEvent.getY() - mouseOnChartY) * 1);
            mouseOnChartY = mouseEvent.getY();
        });

    }
}
