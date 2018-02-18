package GUI.ReportsUI;

import Domain.Assignment;
import Domain.Grade;
import Domain.Student;
import Domain.User;
import GUI.AbstractController;
import GUI.InitView;
import Main.StartApplication;
import Repository.RepositoryException;
import Service.AssignmentService;
import Service.GeneralService;
import Service.GradeService;
import Service.StudentService;
import Utils.ListEvent;
import Utils.Observer;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Rpt3Ctrl extends AbstractController implements Observer<Grade> {
    private StudentService studentService;
    private GradeService gradeService;
    private AssignmentService assignmentService;
    private GeneralService generalService;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    ObservableList<Grade> model = FXCollections.observableArrayList();
    ObservableList<XYChart.Series<Number,Number>> modelSeries = FXCollections.observableArrayList();


    @FXML
    AnchorPane contentStd;

    @FXML
    ImageView imageViewStd;
    @FXML
    JFXButton buttonBackRpt2;

    @FXML
    JFXButton buttonDownload;

    @FXML
    BubbleChart<Number,Number> bubbleChart;

    @FXML
    NumberAxis xAxis;

    @FXML
    NumberAxis yAxis;

    private Node reportsView;

    public void setStudentService(StudentService studentService) throws Exception {
        this.studentService = studentService;

    }

    public void setGradeService(GradeService gradeService) throws RepositoryException {
        this.gradeService = gradeService;
        model.setAll(gradeService.getAll());

    }

    public void setAssignmentService(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    public void setGeneralService(GeneralService generalService) throws RepositoryException {
        this.generalService = generalService;

        bubbleChart.setData(modelSeries);

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();
        bubbleChart = new BubbleChart<Number, Number>(xAxis,yAxis);
        bubbleChart.setTitle("assignments");
        xAxis.setLabel("assignment");
        yAxis.setLabel("grade");



        for (Assignment asg: assignmentService.getAllAssignments()) {
            XYChart.Series series = new XYChart.Series();
            series.setName("assignment " + asg.getIdAsg());
            series.setName(asg.getDescription());
            for (Grade grade : model) {
                if (grade.getAsg().equals(asg)) {
                    series.getData().add(new XYChart.Data(asg.getIdAsg(),grade.getValue(),0.5));
                }
            }
            modelSeries.add(series);


        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("5.jpg");
        Image image = new Image(file.toURI().toString());
        imageViewStd.setImage(image);


    }

    public Rpt3Ctrl(StudentService studentService, GradeService gradeService, AssignmentService assignmentService, GeneralService generalService) {
        this.studentService = studentService;
        this.gradeService = gradeService;
        this.assignmentService = assignmentService;
        this.generalService = generalService;
    }

    public void handleBackRpt2() throws IOException {
        reportsView  = InitView.initRptView(studentService,assignmentService,gradeService,generalService,user);
        contentStd.getChildren().setAll(reportsView);

        AnchorPane.setTopAnchor(reportsView, 0d);
        AnchorPane.setBottomAnchor(reportsView, 0d);
        AnchorPane.setLeftAnchor(reportsView, 0d);
        AnchorPane.setRightAnchor(reportsView, 0d);
    }

    public void handleDownload() throws Exception {
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job != null){
            job.showPrintDialog(StartApplication.getStage()); // Window must be your main Stage
            bubbleChart.setData(modelSeries);
            job.printPage(bubbleChart);
            job.endJob();
        }

        contentStd.getChildren().setAll(InitView.initRpt3View(studentService,assignmentService,gradeService,generalService,user));
    }

    public Rpt3Ctrl() {
    }

    @Override
    public void notifyEvent(ListEvent<Grade> listEvent) {
        try {
            model.setAll(gradeService.getAll());
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}
