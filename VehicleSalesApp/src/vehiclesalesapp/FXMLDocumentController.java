/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vehiclesalesapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

/**
 *
 * @author Neil
 */
public class FXMLDocumentController implements Initializable {

    private String markup;
    private LinkedList<Sales> sales;
    private DashService service;
    private ProgressIndicator ServiceIndicator;

    private List<String> distvehicle;
    private List<String> distyear;
    private List<String> distregion;

    private BarChart leftBarChart;
    private BarChart rightBarChart;
    private LineChart leftLineChart;
    private LineChart rightLineChart;
    private PieChart pieChart;

    private final StackPane leftOptions = new StackPane();
    private final StackPane rightOptions = new StackPane();

    private CheckBox[] leftBarCheckBoxes;
    private CheckBox[] rightBarCheckBoxes;
    private CheckBox[] leftLineCheckBoxes;
    private CheckBox[] rightLineCheckBoxes;
    private RadioButton[] leftYearRadioButtons;
    private RadioButton[] rightYearRadioButtons;
    private RadioButton[] leftCarRadioButtons;
    private RadioButton[] rightCarRadioButtons;

    private final VBox leftBarOptions = new VBox();
    private final VBox rightBarOptions = new VBox();
    private final VBox leftLineOptions = new VBox();
    private final VBox rightLineOptions = new VBox();

    ImageView lotusLogo = new ImageView(new Image(VehicleSalesApp.class.getResourceAsStream("resources/lotus_logo.png")));
    AudioClip ping = new AudioClip(this.getClass().getResource("resources/ping.wav").toExternalForm());

    @FXML
    private MenuItem menuFileExit;
    @FXML
    private MenuItem menuFileLoad;
    @FXML
    private BorderPane MainBorderPane;
    @FXML
    private BorderPane LeftBorderPane;
    @FXML
    private BorderPane RightBorderPane;
    @FXML
    private MenuItem menuHelpAbout;
    @FXML
    private MenuItem menuHelpView;
    @FXML
    private Button leftPieButton;
    @FXML
    private Button rightPieButton;
    @FXML
    private Button leftTableButton;
    @FXML
    private Button rightTableButton;
    @FXML
    private VBox serviceIndicatorVBox;
    @FXML
    private Button leftBarButton;
    @FXML
    private Button rightBarButton;
    @FXML
    private BorderPane footer;
    @FXML
    private GridPane salesGridPane;
    @FXML
    private Label homeText;
    @FXML
    private Button homeButton;
    @FXML
    private Label totalSales;
    @FXML
    private Label europeSales;
    @FXML
    private Label asiaSales;
    @FXML
    private Label americaSales;
    @FXML
    private Button leftLineButton;
    @FXML
    private Button rightLineButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // service sets list of sales based on data loaded from web service, as well as distinct lists
        service = new DashService();
        service.setAddress("http://glynserver.cms.livjm.ac.uk/DashService/SalesGetSales");
        service.setOnSucceeded((WorkerStateEvent e) -> {            
            markup = e.getSource().getValue().toString();
            sales = (new Gson()).fromJson(markup, new TypeToken<LinkedList<Sales>>() {
            }.getType());
            
            // if sales list is not null as a result of an incorrect address
            if (sales != null) {
                System.out.println("Service loaded from: [" + service.getAddress() + "]");
                ping.play();
            
                distyear = sales.stream().map(o -> o.getYear()).distinct().collect(Collectors.toList());
                distregion = sales.stream().map(o -> o.getRegion()).distinct().collect(Collectors.toList());
                distvehicle = sales.stream().map(o -> o.getVehicle()).distinct().collect(Collectors.toList());
                
                mainApp();
            } else {
                System.out.println("Error loading sales serivce, please check the address");
            }
        });

        service.start();

        ServiceIndicator = new ProgressIndicator();

        // disable all relevant elements when service is started
        ServiceIndicator.visibleProperty().bind(service.runningProperty());
        LeftBorderPane.disableProperty().bind(service.runningProperty());
        RightBorderPane.disableProperty().bind(service.runningProperty());
        menuFileLoad.disableProperty().bind(service.runningProperty());
        menuHelpView.disableProperty().bind(service.runningProperty());
        homeButton.disableProperty().bind(service.runningProperty());

        // service indicator always hidden center between 2 data panes
        serviceIndicatorVBox.getChildren().add(ServiceIndicator);
        serviceIndicatorVBox.setAlignment(Pos.CENTER);
        MainBorderPane.setCenter(serviceIndicatorVBox);

        // date & time set in the bottom right of the app
        DateFormat dateFormat = new SimpleDateFormat("EEEE d MMMM");
        Date date = new Date();
        Text displayDate = new Text();
        displayDate.setText(dateFormat.format(date));
        displayDate.setStyle("-fx-fill: #FFFFFF");
        footer.setRight(displayDate);

        // set layout options for all check boxes                
        leftBarOptions.setPadding(new Insets(20, 20, 20, 20));
        leftBarOptions.setSpacing(10);
        leftBarOptions.setAlignment(Pos.CENTER_LEFT);
        leftBarOptions.setVisible(false);

        rightBarOptions.setPadding(new Insets(20, 20, 20, 20));
        rightBarOptions.setSpacing(10);
        rightBarOptions.setAlignment(Pos.CENTER_LEFT);
        rightBarOptions.setVisible(false);

        leftLineOptions.setPadding(new Insets(20, 20, 20, 20));
        leftLineOptions.setSpacing(10);
        leftLineOptions.setAlignment(Pos.CENTER_LEFT);
        leftLineOptions.setVisible(false);

        rightLineOptions.setPadding(new Insets(20, 20, 20, 20));
        rightLineOptions.setSpacing(10);
        rightLineOptions.setAlignment(Pos.CENTER_LEFT);
        rightLineOptions.setVisible(false);

        leftOptions.getChildren().addAll(leftBarOptions, leftLineOptions);
        rightOptions.getChildren().addAll(rightBarOptions, rightLineOptions);

        LeftBorderPane.setLeft(leftOptions);
        RightBorderPane.setRight(rightOptions);
    }

    public void mainApp() {

        // clear check boxes after service loads
        leftBarOptions.getChildren().clear();
        rightBarOptions.getChildren().clear();
        leftLineOptions.getChildren().clear();
        rightLineOptions.getChildren().clear();

        /* home screen, values derived from aggregator method displaying the
           total number of values for each region respectively */
        salesGridPane.setVisible(true);
        homeText.setVisible(true);
        int totalSalesValue = (totalSalesAgg(sales).get("Asia") + totalSalesAgg(sales).get("America") + totalSalesAgg(sales).get("Europe"));
        totalSales.setText(Integer.toString(totalSalesValue));
        europeSales.setText(Integer.toString(totalSalesAgg(sales).get("Europe")));
        americaSales.setText(Integer.toString(totalSalesAgg(sales).get("America")));
        asiaSales.setText(Integer.toString(totalSalesAgg(sales).get("Asia")));

        // exit
        menuFileExit.setOnAction((ActionEvent) -> {
            System.exit(0);
        });

        /* load web service, additionally reset all variable check boxes and
           radio buttons to original state */
        menuFileLoad.setOnAction((ActionEvent) -> {
            service.restart();
            mainApp();
            if (leftBarChart != null) {
                constructSeriesBar("left");
            }
            if (rightBarChart != null) {
                constructSeriesBar("right");
            }
            if (leftLineChart != null) {
                constructSeriesLine("left", "2011", "Elise");
            }
            if (rightLineChart != null) {
                constructSeriesLine("right", "2011", "Elise");
            }
        });

        // view
        menuHelpView.setOnAction((ActionEvent) -> {
            Alert view = new Alert(AlertType.INFORMATION);
            view.initStyle(StageStyle.UTILITY);
            view.setTitle("View");
            view.setGraphic(null);
            view.setHeaderText(null);
            view.setContentText(service.getAddress());
            TextArea expand = new TextArea();
            sales.forEach((sale) -> {
                expand.appendText(sale.toString());
            });
            view.getDialogPane().setExpandableContent(expand);
            view.showAndWait();
        });

        // display typical 'about' window, identifying app version and authors
        menuHelpAbout.setOnAction((ActionEvent) -> {
            lotusLogo.setFitWidth(50);
            lotusLogo.setFitHeight(50);
            Alert about = new Alert(AlertType.INFORMATION);
            about.initStyle(StageStyle.UTILITY);
            about.setTitle("About");
            about.setHeaderText("Lotus Business Intelligence System 1.0");
            about.setGraphic(lotusLogo);
            about.setContentText("Neil Buckley & Steve Pye");
            about.showAndWait();
        });

        /* home button resets both left and right border panes to original
           state, hiding coresponding charts if enabled before-hand */
        homeButton.setOnAction((ActionEvent) -> {
            LeftBorderPane.setCenter(salesGridPane);
            RightBorderPane.setCenter(homeText);
            salesGridPane.setVisible(true);
            homeText.setVisible(true);
            leftBarOptions.setVisible(false);
            leftLineOptions.setVisible(false);
            rightBarOptions.setVisible(false);
            rightLineOptions.setVisible(false);
        });

        // left pie chart
        leftPieButton.setOnAction((ActionEvent) -> {
            pieChart = new PieChart();
            pieChart.setTitle("Sales Percentage");
            constructSeriesPie();
            LeftBorderPane.setCenter(pieChart);
            leftOptions.setVisible(false);
            homeText.setVisible(false);
        });

        // right pie chart
        rightPieButton.setOnAction((ActionEvent) -> {
            pieChart = new PieChart();
            pieChart.setTitle("Sales Percentage");
            constructSeriesPie();
            RightBorderPane.setCenter(pieChart);
            rightOptions.setVisible(false);
            salesGridPane.setVisible(false);
        });

        // left table with raw data
        leftTableButton.setOnAction((ActionEvent) -> {
            LeftBorderPane.setCenter(constructSeriesTable());
            leftOptions.setVisible(false);
            homeText.setVisible(false);
        });

        // right table with raw data
        rightTableButton.setOnAction((ActionEvent) -> {
            RightBorderPane.setCenter(constructSeriesTable());
            rightOptions.setVisible(false);
            salesGridPane.setVisible(false);
        });

        // instantiate check boxes using the distinct year size
        leftBarCheckBoxes = new CheckBox[distyear.size()];
        rightBarCheckBoxes = new CheckBox[distyear.size()];

        // for loop containing functionality for bar chart check boxes
        for (byte index = 0; index < distyear.size(); index++) {
            leftBarCheckBoxes[index] = new CheckBox(distyear.get(index));
            leftBarCheckBoxes[index].setSelected(true);
            leftBarCheckBoxes[index].addEventFilter(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            leftBarCheckBoxes[index].addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            leftBarCheckBoxes[index].setOnAction((ActionEvent e) -> {
                constructSeriesBar("left");
            });

            rightBarCheckBoxes[index] = new CheckBox(distyear.get(index));
            rightBarCheckBoxes[index].setSelected(true);
            rightBarCheckBoxes[index].addEventFilter(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            rightBarCheckBoxes[index].addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            rightBarCheckBoxes[index].setOnAction((ActionEvent e) -> {
                constructSeriesBar("right");
            });

            leftBarOptions.getChildren().add(leftBarCheckBoxes[index]);
            rightBarOptions.getChildren().add(rightBarCheckBoxes[index]);
        }

        // left bar chart
        leftBarButton.setOnAction((ActionEvent) -> {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel("Country");
            yAxis.setLabel("Sales");

            leftBarChart = new BarChart(xAxis, yAxis);
            leftBarChart.setAnimated(false);
            leftBarChart.setTitle("Yearly sales per region");

            LeftBorderPane.setCenter(leftBarChart);
            constructSeriesBar("left");
            leftOptions.setVisible(true);
            leftBarOptions.setVisible(true);
            leftLineOptions.setVisible(false);
            homeText.setVisible(false);
        });

        // right bar chart
        rightBarButton.setOnAction((ActionEvent) -> {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel("Country");
            yAxis.setLabel("Sales");

            rightBarChart = new BarChart(xAxis, yAxis);
            rightBarChart.setAnimated(false);
            rightBarChart.setTitle("Yearly sales per region");

            RightBorderPane.setCenter(rightBarChart);
            constructSeriesBar("right");
            rightOptions.setVisible(true);
            rightBarOptions.setVisible(true);
            rightLineOptions.setVisible(false);
            salesGridPane.setVisible(false);
        });

        /* instantiate raido buttons and check boxes based on the distinct
           lists */
        leftYearRadioButtons = new RadioButton[distyear.size()];
        rightYearRadioButtons = new RadioButton[distyear.size()];
        leftCarRadioButtons = new RadioButton[distvehicle.size()];
        rightCarRadioButtons = new RadioButton[distvehicle.size()];
        leftLineCheckBoxes = new CheckBox[4];
        rightLineCheckBoxes = new CheckBox[4];

        // toggle groups to assign radio buttons
        final ToggleGroup leftYearGroup = new ToggleGroup();
        final ToggleGroup leftCarGroup = new ToggleGroup();
        final ToggleGroup rightYearGroup = new ToggleGroup();
        final ToggleGroup rightCarGroup = new ToggleGroup();

        // for loop containing functionality for line chart year radio buttons
        for (byte index = 0; index < distyear.size(); index++) {
            leftYearRadioButtons[index] = new RadioButton(distyear.get(index));
            leftYearRadioButtons[0].setSelected(true);
            leftYearRadioButtons[index].setText(distyear.get(index));
            leftYearRadioButtons[index].setUserData(distyear.get(index));
            leftYearRadioButtons[index].setToggleGroup(leftYearGroup);
            leftYearRadioButtons[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("left", leftYearGroup.getSelectedToggle().getUserData().toString(), leftCarGroup.getSelectedToggle().getUserData().toString());
            });

            rightYearRadioButtons[index] = new RadioButton(distyear.get(index));
            rightYearRadioButtons[0].setSelected(true);
            rightYearRadioButtons[index].setText(distyear.get(index));
            rightYearRadioButtons[index].setUserData(distyear.get(index));
            rightYearRadioButtons[index].setToggleGroup(rightYearGroup);
            rightYearRadioButtons[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("right", rightYearGroup.getSelectedToggle().getUserData().toString(), rightCarGroup.getSelectedToggle().getUserData().toString());
            });

            leftLineOptions.getChildren().add(leftYearRadioButtons[index]);
            rightLineOptions.getChildren().add(rightYearRadioButtons[index]);
        }

        // spacing between radio buttons & check boxes
        VBox spacing1 = new VBox();
        spacing1.setPrefHeight(20);
        leftLineOptions.getChildren().add(spacing1);

        VBox spacing2 = new VBox();
        spacing2.setPrefHeight(20);
        rightLineOptions.getChildren().add(spacing2);

        // for loop containing functionality for line chart vehicle radio buttons
        for (byte index = 0; index < distvehicle.size(); index++) {
            leftCarRadioButtons[index] = new RadioButton(distvehicle.get(index));
            leftCarRadioButtons[0].setSelected(true);
            leftCarRadioButtons[index].setText(distvehicle.get(index));
            leftCarRadioButtons[index].setUserData(distvehicle.get(index));
            leftCarRadioButtons[index].setToggleGroup(leftCarGroup);
            leftCarRadioButtons[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("left", leftYearGroup.getSelectedToggle().getUserData().toString(), leftCarGroup.getSelectedToggle().getUserData().toString());
            });

            rightCarRadioButtons[index] = new RadioButton(distvehicle.get(index));
            rightCarRadioButtons[0].setSelected(true);
            rightCarRadioButtons[index].setText(distvehicle.get(index));
            rightCarRadioButtons[index].setUserData(distvehicle.get(index));
            rightCarRadioButtons[index].setToggleGroup(rightCarGroup);
            rightCarRadioButtons[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("right", rightYearGroup.getSelectedToggle().getUserData().toString(), rightCarGroup.getSelectedToggle().getUserData().toString());
            });

            leftLineOptions.getChildren().add(leftCarRadioButtons[index]);
            rightLineOptions.getChildren().add(rightCarRadioButtons[index]);
        }

        VBox spacing3 = new VBox();
        spacing2.setPrefHeight(20);
        leftLineOptions.getChildren().add(spacing3);

        VBox spacing4 = new VBox();
        spacing4.setPrefHeight(20);
        rightLineOptions.getChildren().add(spacing4);

        // for loop containing hard-coded quarter check boxes
        for (byte index = 0; index < 4; index++) {
            leftLineCheckBoxes[index] = new CheckBox("Q" + (index + 1));
            leftLineCheckBoxes[index].setSelected(true);
            leftLineCheckBoxes[index].addEventFilter(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            leftLineCheckBoxes[index].addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            leftLineCheckBoxes[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("left", leftYearGroup.getSelectedToggle().getUserData().toString(), leftCarGroup.getSelectedToggle().getUserData().toString());

            });

            rightLineCheckBoxes[index] = new CheckBox("Q" + (index + 1));
            rightLineCheckBoxes[index].setSelected(true);
            rightLineCheckBoxes[index].addEventFilter(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            rightLineCheckBoxes[index].addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> {
            });
            rightLineCheckBoxes[index].setOnAction((ActionEvent e) -> {
                constructSeriesLine("right", rightYearGroup.getSelectedToggle().getUserData().toString(), rightCarGroup.getSelectedToggle().getUserData().toString());

            });

            leftLineOptions.getChildren().add(leftLineCheckBoxes[index]);
            rightLineOptions.getChildren().add(rightLineCheckBoxes[index]);
        }

        // left line chart
        leftLineButton.setOnAction((ActionEvent) -> {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel("Region");
            yAxis.setLabel("Sales");

            leftLineChart = new LineChart(xAxis, yAxis);
            leftLineChart.setAnimated(false);
            leftLineChart.setTitle("Quarterly sales per region");

            LeftBorderPane.setCenter(leftLineChart);
            constructSeriesLine("left", "2011", "Elise");
            leftOptions.setVisible(true);
            leftBarOptions.setVisible(false);
            leftLineOptions.setVisible(true);
            homeText.setVisible(false);
            leftYearRadioButtons[0].setSelected(true);
            leftCarRadioButtons[0].setSelected(true);
        });

        // right line chart
        rightLineButton.setOnAction((ActionEvent) -> {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();

            xAxis.setLabel("Region");
            yAxis.setLabel("Sales");

            rightLineChart = new LineChart(xAxis, yAxis);
            rightLineChart.setAnimated(false);
            rightLineChart.setTitle("Quarterly sales per region");

            RightBorderPane.setCenter(rightLineChart);
            constructSeriesLine("right", "2011", "Elise");
            rightOptions.setVisible(true);
            rightBarOptions.setVisible(false);
            rightLineOptions.setVisible(true);
            salesGridPane.setVisible(false);
            rightYearRadioButtons[0].setSelected(true);
            rightCarRadioButtons[0].setSelected(true);
        });
    }

    private static class DashService extends Service<String> {

        private final StringProperty address = new SimpleStringProperty();

        public final void setAddress(String address) {
            this.address.set(address);
        }

        public final String getAddress() {
            return address.get();
        }

        public final StringProperty addressProperty() {
            return address;
        }

        @Override
        protected Task<String> createTask() {
            return new Task<String>() {
                private URL url;
                private HttpURLConnection connect;
                private String markup = "";

                @Override
                protected String call() {
                    try {
                        url = new URL(getAddress());
                        connect = (HttpURLConnection) url.openConnection();
                        connect.setRequestMethod("GET");
                        connect.setRequestProperty("Accept", "application/json");
                        connect.setRequestProperty("Content-Type", "application/json");

                        markup = (new BufferedReader(new InputStreamReader(connect.getInputStream()))).readLine();
                    } catch (IOException e) {
                    } finally {
                        if (connect != null) {
                            connect.disconnect();
                        }
                    }

                    return markup;
                }
            };

        }
    }

    private void constructSeriesPie() {
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList();
        int saleTotal;

        for (String str : distvehicle) {
            saleTotal = 0;
            saleTotal = sales.stream().filter((sD) -> (sD.getVehicle().equals(str))).map((sD) -> sD.getQuantity()).reduce(saleTotal, Integer::sum);
            pieChartData.add(new PieChart.Data(str, saleTotal));
        }

        pieChart.setStyle("-fx-inner-text-color: white");
        pieChart.setData(pieChartData);

        // percentage
        pieChart.getData().forEach((data) -> {
            double total = 0;
            total = pieChart.getData().stream().map((d) -> d.getPieValue()).reduce(total, (accumulator, _item) -> accumulator + _item);
            String percent = String.format("%.1f%%", 100 * data.getPieValue() / total);
            data.nameProperty().bind(Bindings.concat(data.getName(), " (", percent + ")"));
        });
    }

    private TableView constructSeriesTable() {
        ObservableList<Sales> saleTable = FXCollections.observableArrayList(sales);

        TableColumn qtrCol = new TableColumn();
        TableColumn quantityCol = new TableColumn();
        TableColumn regionCol = new TableColumn();
        TableColumn vehicleCol = new TableColumn();
        TableColumn yearCol = new TableColumn();

        qtrCol.setText("QTR");
        quantityCol.setText("Quantity");
        regionCol.setText("Region");
        vehicleCol.setText("Vehicle");
        yearCol.setText("Year");

        qtrCol.setCellValueFactory(new PropertyValueFactory("QTR"));
        quantityCol.setCellValueFactory(new PropertyValueFactory("Quantity"));
        regionCol.setCellValueFactory(new PropertyValueFactory("Region"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory("Vehicle"));
        yearCol.setCellValueFactory(new PropertyValueFactory("Year"));

        TableView tableView = new TableView();
        tableView.setItems(saleTable);
        tableView.getColumns().addAll(qtrCol, quantityCol, regionCol, vehicleCol, yearCol);

        return tableView;
    }

    public void constructSeriesBar(String side) {
        if (side.equals("left")) {
            leftBarChart.getData().clear();
            for (CheckBox checkBox : leftBarCheckBoxes) {
                if (checkBox.isSelected()) {
                    XYChart.Series series = new XYChart.Series();
                    series.setName(checkBox.getText());

                    sales.stream().filter((sale) -> (sale.getYear().equals(checkBox.getText()))).forEach((sale) -> {
                        series.getData().add(new XYChart.Data<>(sale.getRegion(), sale.getQuantity()));
                    });

                    leftBarChart.getData().add(series);
                }
            }
        }

        if (side.equals("right")) {
            rightBarChart.getData().clear();
            for (CheckBox checkBox : rightBarCheckBoxes) {
                if (checkBox.isSelected()) {
                    XYChart.Series series = new XYChart.Series();
                    series.setName(checkBox.getText());

                    sales.stream().filter((sale) -> (sale.getYear().equals(checkBox.getText()))).forEach((sale) -> {
                        series.getData().add(new XYChart.Data<>(sale.getRegion(), sale.getQuantity()));
                    });

                    rightBarChart.getData().add(series);
                }
            }
        }
    }

    public void constructSeriesLine(String side, String yearPick, String carPick) {
        if (side.equals("left")) {
            Map<String, List<Integer>> carChoice;

            switch (carPick) {
                case "Exige":
                    carChoice = lineChartAggregator("Exige", yearPick, sales);
                    break;
                case "Evora":
                    carChoice = lineChartAggregator("Evora", yearPick, sales);
                    break;
                default:
                    carChoice = lineChartAggregator("Elise", yearPick, sales);
                    break;
            }

            leftLineChart.getData().clear();

            XYChart.Series series1 = new XYChart.Series();
            XYChart.Series series2 = new XYChart.Series();
            XYChart.Series series3 = new XYChart.Series();
            XYChart.Series series4 = new XYChart.Series();

            series1.setName("Q1");
            series2.setName("Q2");
            series3.setName("Q3");
            series4.setName("Q4");

            for (Map.Entry<String, List<Integer>> temp : carChoice.entrySet()) {
                //System.out.println(temp.getValue());
                if (leftLineCheckBoxes[0].isSelected()) {
                    series1.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(0)));
                }
                if (leftLineCheckBoxes[1].isSelected()) {
                    series2.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(1)));
                }
                if (leftLineCheckBoxes[2].isSelected()) {
                    series3.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(2)));
                }
                if (leftLineCheckBoxes[3].isSelected()) {
                    series4.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(3)));
                }
            }

            leftLineChart.getData().addAll(series1, series2, series3, series4);
        }

        if (side.equals("right")) {
            Map<String, List<Integer>> carChoice;

            switch (carPick) {
                case "Exige":
                    carChoice = lineChartAggregator("Exige", yearPick, sales);
                    break;
                case "Evora":
                    carChoice = lineChartAggregator("Evora", yearPick, sales);
                    break;
                default:
                    carChoice = lineChartAggregator("Elise", yearPick, sales);
                    break;
            }

            rightLineChart.getData().clear();

            XYChart.Series series1 = new XYChart.Series();
            XYChart.Series series2 = new XYChart.Series();
            XYChart.Series series3 = new XYChart.Series();
            XYChart.Series series4 = new XYChart.Series();

            series1.setName("Q1");
            series2.setName("Q2");
            series3.setName("Q3");
            series4.setName("Q4");

            for (Map.Entry<String, List<Integer>> temp : carChoice.entrySet()) {
                //System.out.println(temp.getValue());
                if (rightLineCheckBoxes[0].isSelected()) {
                    series1.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(0)));
                }
                if (rightLineCheckBoxes[1].isSelected()) {
                    series2.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(1)));
                }
                if (rightLineCheckBoxes[2].isSelected()) {
                    series3.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(2)));
                }
                if (rightLineCheckBoxes[3].isSelected()) {
                    series4.getData().add(new XYChart.Data(temp.getKey(), temp.getValue().get(3)));
                }
            }

            rightLineChart.getData().addAll(series1, series2, series3, series4);
        }
    }

    public Map<String, Integer> aggregator(String salesType, String year, LinkedList<Sales> objects) {

        Map<String, Integer> aggregation = objects.stream()
                .filter(o -> o.getVehicle().equals(salesType))
                .filter(o -> o.getYear().equals(year))
                .collect(Collectors.groupingBy(Sales::getRegion, Collectors.reducing(0, Sales::getQuantity, Integer::sum)));
        return aggregation;
    }

    public Map<String, Integer> totalSalesAgg(LinkedList<Sales> objects) {

        Map<String, Integer> aggregation = objects.stream()
                .collect(Collectors.groupingBy(Sales::getRegion, Collectors.reducing(0, Sales::getQuantity, Integer::sum)));
        return aggregation;
    }

    public Map<String, List<Integer>> lineChartAggregator(String salesType, String year, LinkedList<Sales> objects) {

        Map<String, List<Integer>> aggregation = objects.stream()
                .filter(o -> o.getVehicle().equals(salesType))
                .filter(o -> o.getYear().equals(year))
                .collect(Collectors.groupingBy(Sales::getRegion, Collectors.mapping(Sales::getQuantity, Collectors.toList())));

        return aggregation;
    }
}