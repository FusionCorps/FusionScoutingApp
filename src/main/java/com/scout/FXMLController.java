//TODO CSS styling: do in SceneBuilder or external CSS sheet
//TODO EFFICIENCY: how to encapsulate/declare data fields in more efficient way (e.g. maybe hashmap for each field, like [Object:fx_id]?)
//TODO WANT: TBA integration, needs to be offline +  hardcoded

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
    @FXML private LimitedTextField p_tnum; //team number
    @FXML private LimitedTextField p_mnum; //match number
    @FXML private ToggleGroup p_ra; //robot alliance
    @FXML private ToggleGroup p_sloc; //starting location
    //page 2 - auton
    @FXML private CheckBox a_mob; //mobility
    @FXML private ToggleGroup a_pre; // GP type preload
    @FXML private GridPane a_grid; //GP grid
    @FXML private GridPane a_preGrid; //preload GP grid
    @FXML private ToggleGroup a_balstat; //auton balance status
    @FXML private ImageView gpAutonPNG;
    private static final ArrayList<Integer> a_pickup = new ArrayList<>(); //GP intaked at community
    private static final ArrayList<Integer> a_cones = new ArrayList<>(); //cones placed
    private static final ArrayList<Integer> a_cubes = new ArrayList<>(); //cubes placed
    //page 3 - teleop
    @FXML private LimitedTextField t_cmty; //community GP intaked
    @FXML private LimitedTextField t_neutzone; //neutral zone GP intaked
    @FXML private LimitedTextField t_singlesub; //singlesub GP intaked
    @FXML private LimitedTextField t_doublesub; //doublesub GP intaked
    @FXML private GridPane t_grid; //GP grid
    private static final ArrayList<Integer> t_cones = new ArrayList<>(); //cones intaked
    private static final ArrayList<Integer> t_cubes = new ArrayList<>(); //cubes intaked
    //page 4 - endgame
    @FXML private CheckBox e_shuttle; //shuttlebot
    @FXML private ToggleGroup e_balstat; //endgame balance status
    @FXML private CheckBox e_budclimb; //buddy climb
    @FXML private TimerText e_timer; //balance time
    //page5 - qualitative notes
    @FXML private Rating n_dtrainrat; //drivetrain rating
    @FXML private ToggleGroup n_dtraintype; //drivetrain type
    @FXML private Rating n_intake; //intake rating
    @FXML private Rating n_spd; //robot speed (1 slow, 5 fast)
    @FXML private Rating n_drat; //driver rating
    @FXML private LimitedTextField n_sn; //scouter name`
    @FXML private TextArea n_co; //general comments
    @FXML private CheckBox n_everybot; //everybot
    //page6 - data output
    @FXML private Text f_reminderBox; //You scouted, "[insert team #]"
    @FXML private Text f_dataStr; //data string for QR code
    @FXML private ImageView f_imageBox; //QR code image display box

    public FXMLController() {
        toggleMap.putIfAbsent("p_ra", null);
        toggleMap.putIfAbsent("p_sloc", null);
        toggleMap.putIfAbsent("a_pre", null);
        toggleMap.putIfAbsent("a_balstat", null);
        toggleMap.putIfAbsent("e_balstat", null);
        toggleMap.putIfAbsent("n_dtraintype", null);
    }

    //runs at loading of a scene, defaults null values and reloads previously entered data
    public void initialize() {
        //setting defaults for certain nullable fields
        if (isNextPageClicked) {
            if (sceneIndex == 2) {
                if (autonColor.equals("R")) gpAutonPNG.setImage(new Image("file:src\\main\\resources\\com\\scout\\images\\GPstart_red.png"));
                else gpAutonPNG.setImage(new Image("file:src\\main\\resources\\com\\scout\\images\\GPstart_blue.png"));
            }
            if (sceneIndex == 3) {
                t_cmty.setText("0");
                t_neutzone.setText("0");
                t_singlesub.setText("0");
                t_doublesub.setText("0");
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
        for (Object keyName : info.keySet()) {
            data.append(keyName).append("=");
            if (info.get(keyName) == null) continue;
            else if (info.get(keyName).equals("true")) data.append("T");
            else if (info.get(keyName).equals("false")) data.append("F");
            else if (info.get(keyName).equals("N/A") || info.get(keyName).equals("N/A or Failed")) data.append("NA");
            else data.append(info.get(keyName));
            data.append(";");
        }
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
                collectDataTextField(p_tnum, "p_tnum");
                collectDataTextField(p_mnum, "p_mnum");
                collectDataToggleGroup(p_ra, "p_ra");
                collectDataToggleGroup(p_sloc, "p_sloc");
                break;
            case 2:
                collectDataCheckBox(a_mob, "a_mob");
                collectDataToggleGroup(a_pre, "a_pre");
                collectDataArray(a_pickup, "a_pickup");
                collectDataArray(a_cones, "a_cones");
                for (Integer i : a_cones) {
                    if (!t_cones.contains(i)) t_cones.add(i);
                }
                collectDataArray(a_cubes, "a_cubes");
                for (Integer i : a_cubes) {
                    if (!t_cubes.contains(i)) t_cubes.add(i);
                }
                collectDataToggleGroup(a_balstat, "a_balstat");
                break;
            case 3:
                collectDataTextField(t_cmty, "t_cmty");
                collectDataTextField(t_neutzone, "t_neutzone");
                collectDataTextField(t_singlesub, "t_singlesub");
                collectDataTextField(t_doublesub, "t_doublesub");
                collectDataArray(t_cones, "t_cones");
                collectDataArray(t_cubes, "t_cubes");
                break;
            case 4:
                collectDataCheckBox(e_shuttle, "e_shuttle");
                collectDataToggleGroup(e_balstat, "e_balstat");
                collectDataCheckBox(e_budclimb, "e_budclimb");
                collectDataTextField(e_timer, "e_timer");
                break;
            case 5:
                collectDataRating(n_dtrainrat, "n_dtrainrat");
                collectDataRating(n_intake, "n_intake");
                collectDataRating(n_spd, "n_spd");
                collectDataRating(n_drat, "n_drat");
                collectDataTextField(n_sn, "n_sn");
                collectDataToggleGroup(n_dtraintype, "n_dtraintype");
                collectDataTextArea(n_co);
                collectDataCheckBox(n_everybot, "n_everybot");
                break;
        }
    }

    //reloads data for a scene, should be called when loading scene
    private void reloadData() {
        switch (sceneIndex) {
            case 1:
                reloadDataTextField(p_tnum, "p_tnum");
                reloadDataTextField(p_mnum, "p_mnum");
                reloadDataToggleGroup(p_ra, "p_ra");
                reloadDataToggleGroup(p_sloc, "p_sloc");
                break;
            case 2:
                reloadDataCheckBox(a_mob, "a_mob");
                reloadDataToggleGroup(a_pre, "a_pre");
                reloadDataToggleGroup(a_balstat, "a_balstat");
                reloadDataGridFieldGP(a_grid, a_cones, a_cubes);
                reloadDataGridFieldPickup(a_preGrid);
                break;
            case 3:
                reloadDataTextField(t_cmty, "t_cmty");
                reloadDataTextField(t_neutzone, "t_neutzone");
                reloadDataTextField(t_singlesub, "t_singlesub");
                reloadDataTextField(t_doublesub, "t_doublesub");
                reloadDataGridFieldGP(t_grid, t_cones, t_cubes);
                break;
            case 4:
                reloadDataCheckBox(e_shuttle, "e_shuttle");
                reloadDataToggleGroup(e_balstat, "e_balstat");
                reloadDataCheckBox(e_budclimb, "e_budclimb");
                reloadDataTextField(e_timer, "e_timer");
                break;
            case 5:
                reloadDataRating(n_dtrainrat, "n_dtrainrat");
                reloadDataRating(n_intake, "n_intake");
                reloadDataRating(n_spd, "n_spd");
                reloadDataRating(n_drat, "n_drat");
                reloadDataTextField(n_sn, "n_sn");
                reloadDataToggleGroup(n_dtraintype, "n_dtraintype");
                reloadDataTextArea(n_co);
                reloadDataCheckBox(n_everybot, "n_everybot");
                break;
            case 6:
                if (info.get("p_tnum") != null)
                    f_reminderBox.setText(info.get("n_sn") + " Scouted Team #" + info.get("p_tnum") + ".");
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
                    info.get("p_mnum") + "-" +
                    info.get("p_tnum") + "-" +
                    info.get("p_ran") + ".txt");
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("outputData() text file failed");
        }
        //qr code
       String filePath = "C:\\Users\\robotics\\Desktop\\" +
            info.get("p_mnum") + "-" +
            info.get("p_tnum") + "-" +
            info.get("p_ran") + ".png";
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
        if (src.equals(p_tnum)) { //team number
            src.setIntegerField();
            src.setMaxLength(4);
        } else if (src.equals(p_mnum)) { //match number
            src.setIntegerField();
            src.setMaxLength(3);
        } else if (src.equals(n_sn)) { //scouter name
            src.setRestrict("[A-Za-z ]"); //letters + spaces only
            src.setMaxLength(30);
        }
    }

    //validation for required fields
    private boolean checkRequiredFields() {
        switch (sceneIndex) {
            case 1:
                if (p_tnum.getText().isEmpty() || p_mnum.getText().isEmpty() || p_ra.getSelectedToggle() == null || p_sloc.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please fill out ALL FIELDS.");
                    return false;
                }
                break;
            case 2:
                if (a_pre.getSelectedToggle() == null || a_balstat.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please select one of the GP preloads and balance status buttons.");
                    return false;
                }
                break;
            case 4:
                if (e_balstat.getSelectedToggle() == null) {
                    AlertBox.display("", "Before proceeding, please select a balance status button.");
                    return false;
                }
                break;
            case 5:
                if (n_sn.getText().isEmpty() || n_dtraintype.getSelectedToggle() == null || n_co.getText()==null || n_co.getText().equals("")) {
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
            a_pickup.add(Integer.valueOf(btn.getUserData().toString()));
        } else if (btn.getStyle().contains("-fx-background-color: green;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            a_pickup.remove(Integer.valueOf(btn.getUserData().toString()));
        }
    }
    @FXML private void manipCones(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it yellow; add to a_cones/t_cones
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            if (sceneIndex == 2) a_cones.add(btnVal);
            else if (sceneIndex == 3) t_cones.add(btnVal);
        }
        //if button is yellow, make it white; remove from a_cones/t_cones
        else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) a_cones.remove((Integer) btnVal);
            else if (sceneIndex == 3) t_cones.remove((Integer) btnVal);
        }
    }
    @FXML private void manipCubes(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it purple; add to a_cubes/t_cubes
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
            if (sceneIndex == 2) a_cubes.add(btnVal);
            else if (sceneIndex == 3) t_cubes.add(btnVal);
        }
        //if button is purple, make it white; remove from a_cubes/t_cubes
        else if (btn.getStyle().contains("-fx-background-color: purple;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) a_cubes.remove((Integer) btnVal);
            else if (sceneIndex == 3) t_cubes.remove((Integer) btnVal);
        }
    }
    @FXML private void manipVar(ActionEvent event) {
        Button btn = (Button) event.getSource();
        int btnVal = Integer.parseInt(btn.getUserData().toString());
        //if button is white, make it yellow; add to a_cones/t_cones
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            if (sceneIndex == 2) a_cones.add(btnVal);
            else if (sceneIndex == 3) t_cones.add(btnVal);
        }
        //if button is yellow, make it purple; remove from a_cones/t_cones, add to a_cubes/t_cubes
        else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
            if (sceneIndex == 2) {
                a_cones.remove((Integer) btnVal);
                a_cubes.add(btnVal);
            } else if (sceneIndex == 3) {
                t_cones.remove((Integer) btnVal);
                t_cubes.add(btnVal);
            }
        }
        //if button is purple, make it white; remove from a_cubes/t_cubes
        else if (btn.getStyle().contains("-fx-background-color: purple;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            if (sceneIndex == 2) a_cubes.remove((Integer) btnVal);
            else if (sceneIndex == 3) t_cubes.remove((Integer) btnVal);
        }
    }

    //timer functions
    @FXML private void startTimer(ActionEvent ignoredEvent) {
        e_timer.start();
    }
    @FXML private void stopTimer(ActionEvent ignoredEvent) {
        e_timer.pause();
    }
    @FXML private void resetTimer(ActionEvent ignoredEvent) {
        e_timer.reset();
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
        increment(t_cmty);
    }
    @FXML private void decrementT_cmty(ActionEvent ignoredEvent) {
        decrement(t_cmty);
    }
    @FXML private void incrementT_neutzone(ActionEvent ignoredEvent) {
        increment(t_neutzone);
    }
    @FXML private void decrementT_neutzone(ActionEvent ignoredEvent) {
        decrement(t_neutzone);
    }
    @FXML private void incrementT_singlesub(ActionEvent ignoredEvent) {
        increment(t_singlesub);
    }
    @FXML private void decrementT_singlesub(ActionEvent ignoredEvent) {
        decrement(t_singlesub);
    }
    @FXML private void incrementT_doublesub(ActionEvent ignoredEvent) {
        increment(t_doublesub);
    }
    @FXML private void decrementT_doublesub(ActionEvent ignoredEvent) {
        decrement(t_doublesub);
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
        info.put("n_co", textArea.getText());
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
            if (FXMLController.a_pickup.contains(Integer.valueOf(btn.getUserData().toString())))
                btn.setStyle("-fx-background-color: green; -fx-border-color: black;");
        }
    }
    private void reloadDataRating(Rating rating, String key) {
        if (info.get(key) != null) rating.setRating(Double.parseDouble(info.get(key)));
    }
    private void reloadDataTextArea(TextArea textArea) {
        textArea.setText(info.get("n_co"));
    }
    private void reloadDataToggleGroup(ToggleGroup toggleGroup, String key) {
        if (toggleMap.get(key) != null) toggleGroup.selectToggle(toggleGroup.getToggles().get(toggleMap.get(key)));
    }

    @FXML private void changeGPAutonPNG(ActionEvent ignoredEvent) {
        if (autonColor.equals("R")) {
            autonColor = "B";
            gpAutonPNG.setImage(new Image("file:src\\main\\resources\\com\\scout\\images\\GPstart_blue.png"));
        } else if (autonColor.equals("B")) {
            autonColor = "R";
            gpAutonPNG.setImage(new Image("file:src\\main\\resources\\com\\scout\\images\\GPstart_red.png"));
        }
    }
}