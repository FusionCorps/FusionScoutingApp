//TODO maybe EFFICIENCY: how to encapsulate/declare data fields in more efficient way (e.g. maybe hashmap for each field, like [Object:fx_id]?)
//TODO WANT: TBA integration, needs to be offline +  hardcoded
//TODO confirmation popup for starting over
//TODO flip starting location image

package com.scout;

import com.scout.ui.*;
import com.scout.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FXMLController {
    //scene0:title
    //scene1:pregame
    //scene2:auton
    //scene3:teleop
    //scene4:endgame
    //scene5:qualitative notes
    //scene6:QR CODE

    private static HashMap<String, String> info = new HashMap<>(); //stores user input data
    private static HashMap<String, Integer> toggleMap = new HashMap<>(); //stores toggle group values

    private static int sceneIndex = 0;  //used for changing pages
    private static BufferedImage bufferedImage; //QR code image
    private static StringBuilder data = new StringBuilder(); //used to build data output string in sendInfo()
    private static boolean isNextPageClicked = false;
    private static String autonColor = "R"; //for changing color in auton pickup grid

    //data for each page, variables should be named the same as corresponding fx:ids for consistency
    //page 1 - pregame
    @FXML private LimitedTextField teamNum; //team number
    @FXML private LimitedTextField matchNum; //match number
    @FXML private ToggleGroup alliance; //robot alliance
    @FXML private ToggleGroup startLocation; //starting location
    //page 2 - auton
    @FXML private ToggleGroup preload; // GP type preload
    @FXML private CheckBox mobility; //mobility
    private static final ArrayList<Integer> autoPickups = new ArrayList<>(); //GP intaked at community
    private static final ArrayList<Integer> autoCones = new ArrayList<>(); //cones placed
    private static final ArrayList<Integer> autoCubes = new ArrayList<>(); //cubes placed
    @FXML private ToggleGroup autoBalance; //auton balance status

    @FXML private GridPane a_grid; //GP grid
    @FXML private GridPane a_preGrid; //preload GP grid
    @FXML private ImageView gpAutonPNG;
    //page 3 - teleop
    @FXML private LimitedTextField communityPickups; //community GP intaked
    @FXML private LimitedTextField neutralPickups; //neutral zone GP intaked
    @FXML private LimitedTextField singlePickups; //singlesub GP intaked
    @FXML private LimitedTextField doublePickups; //doublesub GP intaked
    private static final ArrayList<Integer> teleopCones = new ArrayList<>(); //cones intaked
    private static final ArrayList<Integer> teleopCubes = new ArrayList<>(); //cubes intaked

    @FXML private GridPane t_grid; //GP grid
    //page 4 - endgame
    @FXML private CheckBox shuttle; //shuttlebot
    @FXML private ToggleGroup teleopBalance; //endgame balance status
    @FXML private CheckBox buddyClimb; //buddy climb
    @FXML private TimerText balanceTime; //balance time
    //page5 - qualitative notes
    @FXML private CheckBox everybot; //everybot
    @FXML private ToggleGroup drivetrainType; //drivetrain type
    @FXML private Rating drivetrain; //drivetrain rating
    @FXML private Rating intake; //intake rating
    @FXML private Rating speed; //robot speed (1 slow, 5 fast)
    @FXML private Rating driver; //driver rating
    @FXML private LimitedTextField scoutName; //scouter name`
    @FXML private TextArea comments; //general comments
    //page6 - data output
    @FXML private Text f_reminderBox; //You scouted, "[insert team #]"
    @FXML private Text f_dataStr; //data string for QR code
    @FXML private ImageView f_imageBox; //QR code image display box

    public FXMLController() {
        toggleMap.putIfAbsent("alliance", null);
        toggleMap.putIfAbsent("startLocation", null);
        toggleMap.putIfAbsent("preload", null);
        toggleMap.putIfAbsent("autoBalance", null);
        toggleMap.putIfAbsent("teleopBalance", null);
        toggleMap.putIfAbsent("drivetrainType", null);
    }

    //runs at loading of a scene, defaults null values and reloads previously entered data
    public void initialize() {
        //setting defaults for certain nullable fields
        if (isNextPageClicked) {
            if (sceneIndex == 2) {
                if (autonColor.equals("R")) gpAutonPNG.setImage(new Image(getClass().getResource("images/GPstart_red.png").toString()));
                else gpAutonPNG.setImage(new Image(getClass().getResource("images/GPstart_blue.png").toString()));
            }
            if (sceneIndex == 3) {
                communityPickups.setText("0");
                neutralPickups.setText("0");
                singlePickups.setText("0");
                doublePickups.setText("0");
            }
        }
        reloadData();
    }

    //implementations of setPage() for going to next and previous pages
    @FXML private void resetAll(ActionEvent event) throws IOException {
        data = new StringBuilder();
        info = new HashMap<>();
        toggleMap = new HashMap<>();
        sceneIndex = 0;
        nextPage(event);
    }
    @FXML private void nextPage(ActionEvent event) throws IOException {
        if (checkRequiredFields()) {
            collectData();
            sceneIndex++;
            isNextPageClicked = true;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            setPage(stage, sceneIndex);
        }
    }
    @FXML private void prevPage(ActionEvent event) throws IOException {
        collectData();
        if (sceneIndex > 0) sceneIndex--;
        isNextPageClicked = false;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setPage(stage, sceneIndex);
    }

    //changes page to the scene specified by sceneIndex
    static void setPage(Stage stage, int page) throws IOException {
        sceneIndex = page;
        //if this causes errors, check syntax in all fxml files
        Parent root = FXMLLoader.load(FXMLController.class.getResource("scenes/scene" + (sceneIndex) + ".fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Scouting App Page" + (page));
        stage.setScene(scene);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        stage.setWidth(size.getWidth()); //2736 px
        stage.setHeight(size.getHeight()); //1824 px
        stage.setMaximized(true);
        stage.show();
    }

    //sends data to QR code creator and displays it on screen
    @FXML private void sendInfo() throws Exception {
        data = new StringBuilder();

        for (String keyName : info.keySet()) {
            if (info.get(keyName).equals("true")) info.put(keyName, "TRUE");
            else if (info.get(keyName).equals("false")) info.put(keyName, "FALSE");
            else if (info.get(keyName).equals("N/A") || info.get(keyName).equals("N/A or Failed")) info.put(keyName, "NA");
        }

        data.append("teamNum=" + info.get("teamNum") + ";");
        data.append("matchNum=" + info.get("matchNum") + ";");
        data.append("alliance=" + info.get("alliance") + ";");
        data.append("startLocation=" + info.get("startLocation") + ";");
        data.append("preload=" + info.get("preload") + ";");
        data.append("mobility=" + info.get("mobility") + ";");
        data.append("autoPickups=" + info.get("autoPickups") + ";");
        data.append("autoCones=" + info.get("autoCones") + ";");
        data.append("autoCubes=" + info.get("autoCubes") + ";");
        data.append("autoBalance=" + info.get("autoBalance") + ";");
        data.append("communityPickups=" + info.get("communityPickups") + ";");
        data.append("neutralPickups=" + info.get("neutralPickups") + ";");
        data.append("singlePickups=" + info.get("singlePickups") + ";");
        data.append("doublePickups=" + info.get("doublePickups") + ";");
        data.append("teleopCones=" + info.get("teleopCones") + ";");
        data.append("teleopCubes=" + info.get("teleopCubes") + ";");
        data.append("shuttle=" + info.get("shuttle") + ";");
        data.append("teleopBalance=" + info.get("teleopBalance") + ";");
        data.append("buddyClimb=" + info.get("buddyClimb") + ";");
        data.append("balanceTime=" + info.get("balanceTime") + ";");
        data.append("everybot=" + info.get("everybot") + ";");
        data.append("drivetrainType=" + info.get("drivetrainType") + ";");
        data.append("drivetrain=" + info.get("drivetrain") + ";");
        data.append("intake=" + info.get("intake") + ";");
        data.append("speed=" + info.get("speed") + ";");
        data.append("driver=" + info.get("driver") + ";");
        data.append("scoutName=" + info.get("scoutName") + ";");
        data.append("comments=" + info.get("comments") + ";");


        data = data.delete(data.lastIndexOf(";"), data.length());


        bufferedImage = QRFuncs.generateQRCode(data.toString(), "qrcode.png");
        File file = new File("qrcode.png");
        Image img = new Image(file.getAbsolutePath());
        f_imageBox.setImage(img);
        f_dataStr.setText(data.toString());
        outputAll();
    }


    //sends data to info storage HashMap, needs to be edited with introduction of new data elements
    private void collectData() {
        switch (sceneIndex) {
            case 1:
                collectDataTextField(teamNum, "teamNum");
                collectDataTextField(matchNum, "matchNum");
                collectDataToggleGroup(alliance, "alliance");
                collectDataToggleGroup(startLocation, "startLocation");
                break;
            case 2:
                collectDataCheckBox(mobility, "mobility");
                collectDataToggleGroup(preload, "preload");
                collectDataArray(autoPickups, "autoPickups");
                collectDataArray(autoCones, "autoCones");
                for (Integer i : autoCones) {
                    if (!teleopCones.contains(i)) teleopCones.add(i);
                }
                collectDataArray(autoCubes, "autoCubes");
                for (Integer i : autoCubes) {
                    if (!teleopCubes.contains(i)) teleopCubes.add(i);
                }
                collectDataToggleGroup(autoBalance, "autoBalance");
                break;
            case 3:
                collectDataTextField(communityPickups, "communityPickups");
                collectDataTextField(neutralPickups, "neutralPickups");
                collectDataTextField(singlePickups, "singlePickups");
                collectDataTextField(doublePickups, "doublePickups");
                collectDataArray(teleopCones, "teleopCones");
                collectDataArray(teleopCubes, "teleopCubes");
                break;
            case 4:
                collectDataCheckBox(shuttle, "shuttle");
                collectDataToggleGroup(teleopBalance, "teleopBalance");
                collectDataCheckBox(buddyClimb, "buddyClimb");
                collectDataTextField(balanceTime, "balanceTime");
                break;
            case 5:
                collectDataRating(drivetrain, "drivetrain");
                collectDataRating(intake, "intake");
                collectDataRating(speed, "speed");
                collectDataRating(driver, "driver");
                collectDataTextField(scoutName, "scoutName");
                collectDataToggleGroup(drivetrainType, "drivetrainType");
                collectDataTextArea(comments);
                collectDataCheckBox(everybot, "everybot");
                break;
        }
    }

    //reloads data for a scene, should be called when loading scene
    private void reloadData() {
        switch (sceneIndex) {
            case 1:
                reloadDataTextField(teamNum, "teamNum");
                reloadDataTextField(matchNum, "matchNum");
                reloadDataToggleGroup(alliance, "alliance");
                reloadDataToggleGroup(startLocation, "startLocation");
                break;
            case 2:
                reloadDataCheckBox(mobility, "mobility");
                reloadDataToggleGroup(preload, "preload");
                reloadDataToggleGroup(autoBalance, "autoBalance");
                reloadDataGridFieldGP(a_grid, autoCones, autoCubes);
                reloadDataGridFieldPickup(a_preGrid);
                break;
            case 3:
                reloadDataTextField(communityPickups, "communityPickups");
                reloadDataTextField(neutralPickups, "neutralPickups");
                reloadDataTextField(singlePickups, "singlePickups");
                reloadDataTextField(doublePickups, "doublePickups");
                reloadDataGridFieldGP(t_grid, teleopCones, teleopCubes);
                break;
            case 4:
                reloadDataCheckBox(shuttle, "shuttle");
                reloadDataToggleGroup(teleopBalance, "teleopBalance");
                reloadDataCheckBox(buddyClimb, "buddyClimb");
                reloadDataTextField(balanceTime, "balanceTime");
                break;
            case 5:
                reloadDataRating(drivetrain, "drivetrain");
                reloadDataRating(intake, "intake");
                reloadDataRating(speed, "speed");
                reloadDataRating(driver, "driver");
                reloadDataTextField(scoutName, "scoutName");
                reloadDataToggleGroup(drivetrainType, "drivetrainType");
                reloadDataTextArea(comments);
                reloadDataCheckBox(everybot, "everybot");
                break;
            case 6:
                if (info.get("teamNum") != null)
                    f_reminderBox.setText(info.get("scoutName") + " Scouted Team #" + info.get("teamNum") + ".");
                break;
        }

    }

    //copies either data text or QR code based on button source that was clicked
    @FXML private void copyToClipboard(ActionEvent event) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (event.getSource().getClass().equals(javafx.scene.control.Button.class))
            if (((javafx.scene.control.Button) event.getSource()).getText().contains("Text")) {
                String str = data.toString();
                clipboard.setContents(new StringSelection(str), null);
            } else if (((javafx.scene.control.Button) event.getSource()).getText().contains("QR Code")) {
                CopyImageToClipBoard ci = new CopyImageToClipBoard();
                ci.copyImage(bufferedImage);
            }
    }

    //saves output to QR Code and text file on computer
    @FXML private void outputAll() {
        //text file
        try {
            FileWriter writer = new FileWriter("C:\\Users\\robotics\\Desktop\\" +
                    info.get("matchNum") + "-" +
                    info.get("teamNum") + "-" +
                    info.get("p_ran") + ".txt");
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("outputData() text file failed");
        }
        //qr code
       String filePath = "C:\\Users\\robotics\\Desktop\\" +
            info.get("matchNum") + "-" +
            info.get("teamNum") + "-" +
            info.get("alliance") + ".png";
        String fileType = "png";
        try{
        File qrFile = new File(filePath);
        ImageIO.write(bufferedImage, fileType, qrFile);
        } catch (IOException e) {
            System.out.println("outputData() qr code failed");
        }
    }

    //puts restrictions on certain data fields
    @FXML private void validateInput(KeyEvent keyEvent) {
        LimitedTextField src = (LimitedTextField) keyEvent.getSource();
        if (src.equals(teamNum)) { //team number
            src.setIntegerField();
            src.setMaxLength(4);
        } else if (src.equals(matchNum)) { //match number
            src.setIntegerField();
            src.setMaxLength(3);
        } else if (src.equals(scoutName)) { //scouter name
            src.setRestrict("[A-Za-z ]"); //letters + spaces only
            src.setMaxLength(30);
        }
    }

    //validation for required fields
    private boolean checkRequiredFields() {
        switch (sceneIndex) {
            case 1:
                if (teamNum.getText().isEmpty() || matchNum.getText().isEmpty() || alliance.getSelectedToggle() == null || startLocation.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please fill out ALL FIELDS.");
                    return false;
                }
                break;
            case 2:
                if (preload.getSelectedToggle() == null || autoBalance.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please select one of the GP preloads and balance status buttons.");
                    return false;
                }
                break;
            case 4:
                if (teleopBalance.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please select a balance status button.");
                    return false;
                }
                break;
            case 5:
                if (scoutName.getText().isEmpty() || drivetrainType.getSelectedToggle() == null || comments.getText()==null || comments.getText().equals("")) {
                    AlertBox.display("", "Before proceeding, please fill out your name and the drivetrain type button. INCLUDE COMMENTS!!!");
                    return false;
                }
                break;
        }
        return true;
    }

    //grid field/GP pickup field functions
    @FXML private void manipGPStart(ActionEvent event) {
        Button btn = (Button) event.getSource();
        System.out.println(btn.getUserData().toString());
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: green; -fx-border-color: black;");
            autoPickups.add(Integer.valueOf(btn.getUserData().toString()));
        } else if (btn.getStyle().contains("-fx-background-color: green;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            autoPickups.remove(Integer.valueOf(btn.getUserData().toString()));
        }
    }
    @FXML private void manipCones(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it yellow; add to autoCones/teleopCones
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            if (sceneIndex == 2) autoCones.add(btnVal);
            else if (sceneIndex == 3) teleopCones.add(btnVal);
        }
        //if button is yellow, make it white; remove from autoCones/teleopCones
        else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) autoCones.remove((Integer) btnVal);
            else if (sceneIndex == 3) teleopCones.remove((Integer) btnVal);
        }
    }
    @FXML private void manipCubes(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it purple; add to autoCubes/teleopCubes
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
            if (sceneIndex == 2) autoCubes.add(btnVal);
            else if (sceneIndex == 3) teleopCubes.add(btnVal);
        }
        //if button is purple, make it white; remove from autoCubes/teleopCubes
        else if (btn.getStyle().contains("-fx-background-color: purple;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) autoCubes.remove((Integer) btnVal);
            else if (sceneIndex == 3) teleopCubes.remove((Integer) btnVal);
        }
    }
    @FXML private void manipVar(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it yellow; add to autoCones/teleopCones
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            if (sceneIndex == 2) autoCones.add(btnVal);
            else if (sceneIndex == 3) teleopCones.add(btnVal);
        }
        //if button is yellow, make it purple; remove from autoCones/teleopCones, add to autoCubes/teleopCubes
        else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
            if (sceneIndex == 2) {
                autoCones.remove((Integer) btnVal);
                autoCubes.add(btnVal);
            } else if (sceneIndex == 3) {
                teleopCones.remove((Integer) btnVal);
                teleopCubes.add(btnVal);
            }
        }
        //if button is purple, make it white; remove from autoCubes/teleopCubes
        else if (btn.getStyle().contains("-fx-background-color: purple;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) autoCubes.remove((Integer) btnVal);
            else if (sceneIndex == 3) teleopCubes.remove((Integer) btnVal);
        }
    }

    //timer functions
    @FXML private void startTimer(ActionEvent ignoredEvent) {
        balanceTime.start();
    }
    @FXML private void stopTimer(ActionEvent ignoredEvent) {
        balanceTime.pause();
    }
    @FXML private void resetTimer(ActionEvent ignoredEvent) {
        balanceTime.reset();
    }

    //template incrementer functions
    private void increment(LimitedTextField txtfield) {
        txtfield.setText(String.valueOf(Integer.parseInt(txtfield.getText()) + 1));
    }
    private void decrement(LimitedTextField txtfield) {
        if (!txtfield.getText().equals("0")) txtfield.setText(String.valueOf(Integer.parseInt(txtfield.getText()) - 1));
    }

    //general methods for +/- buttons affecting corr. txtfields
    @FXML private void incrementT_cmty(ActionEvent ignoredEvent) {
        increment(communityPickups);
    }
    @FXML private void decrementT_cmty(ActionEvent ignoredEvent) {
        decrement(communityPickups);
    }
    @FXML private void incrementT_neutzone(ActionEvent ignoredEvent) {
        increment(neutralPickups);
    }
    @FXML private void decrementT_neutzone(ActionEvent ignoredEvent) {
        decrement(neutralPickups);
    }
    @FXML private void incrementT_singlesub(ActionEvent ignoredEvent) {
        increment(singlePickups);
    }
    @FXML private void decrementT_singlesub(ActionEvent ignoredEvent) {
        decrement(singlePickups);
    }
    @FXML private void incrementT_doublesub(ActionEvent ignoredEvent) {
        increment(doublePickups);
    }
    @FXML private void decrementT_doublesub(ActionEvent ignoredEvent) {
        decrement(doublePickups);
    }

    //used in collectData()
    private void collectDataCheckBox(CheckBox checkBox, String key) {
        info.put(key, String.valueOf(checkBox.isSelected()));
    }
    private void collectDataTextField(LimitedTextField textField, String key) {info.put(key, textField.getText());}
    private void collectDataArray(ArrayList<Integer> array, String key) {
        info.put(key, array.toString());
    }
    private void collectDataRating(Rating rating, String key) {
        info.put(key, String.valueOf((int) rating.getRating()));
    }
    private void collectDataTextArea(TextArea textArea) {
        info.put("comments", textArea.getText());
    }
    private void collectDataToggleGroup(ToggleGroup toggleGroup, String key) {
        if (toggleGroup.getSelectedToggle() == null) return;
        Toggle selectedToggle = toggleGroup.getSelectedToggle();
        int index = toggleGroup.getToggles().indexOf(selectedToggle);
        String value = selectedToggle.getUserData().toString();
        info.put(key, value);
        toggleMap.put(key, index);
    }

    //used in reloadData()
    private void reloadDataCheckBox(CheckBox checkBox, String key) {
        checkBox.setSelected(Boolean.parseBoolean(info.get(key)));
    }
    private void reloadDataTextField(LimitedTextField textField, String key) {
        if (info.get(key) != null) textField.setText(info.get(key));
    }
    private void reloadDataGridFieldGP(GridPane grid, ArrayList<Integer> coneArray, ArrayList<Integer> cubeArray) {
        int gridLength = grid.getChildren().size();
        for (int i = 0; i < gridLength; i++) {
            Button btn = (Button) grid.getChildren().get(i);
            if (coneArray.contains(Integer.valueOf(btn.getUserData().toString())))
                btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            else if (cubeArray.contains(Integer.valueOf(btn.getUserData().toString())))
                btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
        }
    }
    private void reloadDataGridFieldPickup(GridPane grid) {
        int gridLength = grid.getChildren().size();
        for (int i = 0; i < gridLength; i++) {
            Button btn = (Button) grid.getChildren().get(i);
            if (FXMLController.autoPickups.contains(Integer.valueOf(btn.getUserData().toString())))
                btn.setStyle("-fx-background-color: green; -fx-border-color: black;");
        }
    }
    private void reloadDataRating(Rating rating, String key) {
        if (info.get(key) != null) rating.setRating(Double.parseDouble(info.get(key)));
    }
    private void reloadDataTextArea(TextArea textArea) {
        textArea.setText(info.get("comments"));
    }
    private void reloadDataToggleGroup(ToggleGroup toggleGroup, String key) {
        if (toggleMap.get(key) != null) toggleGroup.selectToggle(toggleGroup.getToggles().get(toggleMap.get(key)));
    }

    @FXML private void changeGPAutonPNG(ActionEvent ignoredEvent) {
        if (autonColor.equals("R")) {
            autonColor = "B";
            gpAutonPNG.setImage(new Image(getClass().getResource("images/GPstart_blue.png").toString()));
        } else if (autonColor.equals("B")) {
            autonColor = "R";
            gpAutonPNG.setImage(new Image(getClass().getResource("images/GPstart_red.png").toString()));
        }
    }
}