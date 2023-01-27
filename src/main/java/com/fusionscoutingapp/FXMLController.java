//TODO fix docking timer
//TODO CSS styling - do in SceneBuilder or external CSS sheet
//TODO think about how to encapsulate data fields in more efficient way (e.g. maybe hashmap for each field, like [Object:fx_id]?)
//TODO MIGHT: add required fields to each page, and only allow user to proceed if all required fields are filled
//TODO WANT: TBA integration

package com.fusionscoutingapp;

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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FXMLController {

    //scene0:title
    //scene1:pregame
    //scene2:auton
    //scene3:teleop
    //scene4:endgame
    //scene5:qualitative notes
    //scene6:QR CODE

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
    private static ArrayList<Integer> a_pickup = new ArrayList<>(); //GP intaked at community
    private static ArrayList<Integer> a_cones = new ArrayList<>(); //cones placed
    private static ArrayList<Integer> a_cubes = new ArrayList<>(); //cubes placed
    @FXML private ToggleGroup a_balstat; //auton balance status

    //page 3 - teleop
    @FXML private LimitedTextField t_cmty; //community GP intaked
    @FXML private LimitedTextField t_neutzone; //neutral zone GP intaked
    @FXML private LimitedTextField t_singlesub; //singlesub GP intaked
    @FXML private LimitedTextField t_doublesub; //doublesub GP intaked
    @FXML private GridPane t_grid; //GP grid
    private static ArrayList<Integer> t_cones = new ArrayList<>(); //cones intaked
    private static ArrayList<Integer> t_cubes = new ArrayList<>(); //cubes intaked


    //page 4 - endgame
    @FXML private CheckBox e_shuttle; //shuttlebot
    @FXML private ToggleGroup e_balstat; //endgame balance status
    @FXML private CheckBox e_budclimb; //buddy climb
    @FXML private Timer e_timer; //balance time

    //page5 - qualitative notes
    @FXML private Rating n_dtrainrat; //drivetrain rating
    @FXML private ToggleGroup n_dtraintype; //drivetrain type
    @FXML private Rating n_intake; //intake rating
    @FXML private Rating n_spd; //robot speed (1 slow, 5 fast)
    @FXML private Rating n_drat; //driver rating
    @FXML private LimitedTextField n_sn; //scouter name`
    @FXML private TextArea n_co; //general comments
    @FXML private CheckBox n_everybot; //everybot

    //page6 - QR code
    @FXML private ImageView imageBox; //QR code image display box
    @FXML private Text reminderBox; //You scouted, "[insert team #]"

    private static Map<ToggleGroup, Integer> toggleMap = new HashMap(); //map of toggle groups to their indexes
    //map of toggle groups to their indexes
     {
        toggleMap.put(p_ra, 0);
        toggleMap.put(p_sloc, 0);
        toggleMap.put(a_pre, 0);
        toggleMap.put(a_balstat, 0);
        toggleMap.put(e_balstat, 0);
        toggleMap.put(n_dtraintype, 0);
    }

    //used for changing pages
    private static int sceneIndex = 0;
    private static BufferedImage bufferedImage;
    //stores user input data
    private static HashMap<String, String> info = new HashMap<>();
    private static StringBuilder data = new StringBuilder();
    private static boolean isNextPageClicked = false;

    public FXMLController() {
    }

    //runs at start of every load of a scene, defaults null values and reloads previously entered data
    public void initialize() {
//        //setting presets for nullable checkboxes, so they are not null by default
//        if (isNextPageClicked) {
//            if (sceneIndex == 1) {
//                ml.setValue("Quals");
//                ran.setValue("Red-1");
//                rp.setValue("1");
//            }
//            else if (sceneIndex == 4) cl.setValue("N/A or Failed");
//            else if (sceneIndex ==5) {
//                de.setValue("N/A");
//                dp.setValue("N/A");
//            }
//        }
       //reload data for each page
       reloadData();
    }

    //implementations of setPage() for going to next and previous pages
    public void resetAll(ActionEvent event) throws IOException {
        data = new StringBuilder();
        info = new HashMap<>();
        nextPage(event);
    }

    public void nextPage(ActionEvent event) throws IOException {
//        System.out.println("prev page is " + sceneIndex);
        collectData();
        if (sceneIndex == 6) sceneIndex = 1;
        else sceneIndex++;
        isNextPageClicked = true;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setPage(stage, sceneIndex);
    }
    public void prevPage(ActionEvent event) throws IOException {
        collectData();
        if (sceneIndex > 0) sceneIndex--;
        isNextPageClicked = false;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setPage(stage, sceneIndex);
    }

    //changes page to the scene specified by sceneIndex
    public static void setPage(Stage stage, int page) throws IOException {
        System.out.println("page" + page);
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
    public void sendInfo() throws Exception {
       data = new StringBuilder();
       Integer tca = Integer.parseInt(info.get("lcst")) + Integer.parseInt(info.get("ucst"))+ Integer.parseInt(info.get("cmdt"));
       info.put("tca", String.valueOf(tca));
       for (Object keyName : info.keySet()) {
           data.append(keyName).append("=");
           if (info.get(keyName) == null) continue;
           else if (info.get(keyName).equals("true"))  data.append("1");
           else if (info.get(keyName).equals("false")) data.append("0");
           else if (info.get(keyName).equals("N/A") || info.get(keyName).equals("N/A or Failed")) data.append("0");
           else if (info.get(keyName).equals("Below Average")) data.append("1");
           else if (info.get(keyName).equals("Average")) data.append("2");
           else if (info.get(keyName).equals("Above Average")) data.append("3");
           else if (info.get(keyName).equals("Low Rung")) data.append("1");
           else if (info.get(keyName).equals("Middle Rung")) data.append("2");
           else if (info.get(keyName).equals("High Rung")) data.append("3");
           else if (info.get(keyName).equals("Traversal Rung")) data.append("4");
           else data.append(info.get(keyName));
           data.append(";");
        }

        data = data.delete(data.lastIndexOf(";"), data.length());

//        two plausible ways to send QR Code
//        bufferedImage = QRFuncs.generateQRCode(data, "src\\main\\codes\\qrcode" + info.get("mn") + "-" + info.get("tn") +".png");
         bufferedImage = QRFuncs.generateQRCode(data.toString(), "qrcode.png");
        File file = new File("qrcode.png");
        Image img = new Image(file.getAbsolutePath());
        imageBox.setImage(img);

        //saves output to QR Code and file on computer
        outputAll();
//        System.out.println(Arrays.toString(info.entrySet().toArray()) + "info sent");
        }


    private void collectDataToggleGroup(ToggleGroup toggleGroup, String key) {
        int index = toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle());
        if (index >= 0) info.put(key, toggleGroup.getToggles().get(index).getUserData().toString());
        toggleMap.put(toggleGroup, index);
    }

    //sends data to info storage HashMap, needs to be edited with introduction of new data elements
    public void collectData() {
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
                collectDataArray(a_cubes, "a_cubes");
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
                collectDataTextArea(n_co, "n_co");
                collectDataCheckBox(n_everybot, "n_everybot");
                break;
            default:
                System.out.println("collectData() default");
                break;
        }
        System.out.println("stuff:" + Arrays.toString(info.entrySet().toArray()));
    }

    //reloads data for a scene, should be called when loading scene
    public void reloadData() {
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
                reloadDataGridFieldGP(a_grid, a_cones, a_cubes);
                reloadDataGridFieldPickup(a_preGrid, a_pickup);
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
                reloadDataTextArea(n_co, "n_co");
                reloadDataCheckBox(n_everybot, "n_everybot");
                break;
            case 6:
                if(info.get("p_tnum")!=null)reminderBox.setText(info.get("n_sn") + " Scouted Team #" + info.get("p_tnum") + ".");
                break;
            default:
                System.out.println("reloadData() default");
                break;
            }

    }

    //copies either data text or QR code based on button source that was clicked
    public void copyToClipboard(ActionEvent event) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (event.getSource().getClass().equals(javafx.scene.control.Button.class))
            if (((javafx.scene.control.Button) event.getSource()).getText().contains("Text")) {
                String str = data.toString();
                clipboard.setContents(new StringSelection(str), null);
            }
            else if (((javafx.scene.control.Button) event.getSource()).getText().contains("QR Code")) {
                CopyImageToClipBoard ci = new CopyImageToClipBoard();
                ci.copyImage(bufferedImage);
            }
    }

    //outputs data to text file and QR code
    public void outputAll() {
        //text file
        try {
            FileWriter writer = new FileWriter("C:\\Users\\robotics\\Desktop\\scoutingFiles\\" +
                    info.get("p_mnum") + "-" +
                    info.get("p_tnum") + "-" +
                    info.get("p_ran") + ".txt");
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("outputData text file failed");
        }
        //qr code
        try {
            String filePath = "C:\\Users\\robotics\\Desktop\\scoutingQRCodes\\" +
                    info.get("p_mnum") + "-" +
                    info.get("p_tnum") + "-" +
                    info.get("p_ran") + ".png";
            String fileType = "png";
            File qrFile = new File(filePath);
            ImageIO.write(bufferedImage, fileType, qrFile);
        } catch (IOException e) {
            System.out.println("outputData qr code failed");
        }
    }

    //puts restrictions on certain data fields
    public void limit(KeyEvent keyEvent) {
        LimitedTextField src = (LimitedTextField) keyEvent.getSource();
        if (src.equals(p_tnum)) { //team number
            src.setIntegerField();
            src.setMaxLength(4);
        }
        else if (src.equals(p_mnum)) { //match number
            src.setIntegerField();
            src.setMaxLength(3);
        }
        else if (src.equals(n_sn)) { //scouter name
            src.setRestrict("[A-Za-z ]"); //letters + spaces only
            src.setMaxLength(30);
        }
    }

    public void manipGPStart(ActionEvent event) {
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

    public void manipCones(ActionEvent event) {
        Button btn = (Button) event.getSource();
        System.out.println(btn.getUserData().toString());
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            a_cones.add(Integer.valueOf(btn.getUserData().toString()));
        } else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            a_cones.remove(Integer.valueOf(btn.getUserData().toString()));
        }
    }
    public void manipCubes(ActionEvent event) {
            Button btn = (Button) event.getSource();
            System.out.println();
            if (btn.getStyle().contains("-fx-background-color: white;")) {
                btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
                a_cubes.add(Integer.valueOf(btn.getUserData().toString()));
            } else if (btn.getStyle().contains("-fx-background-color: purple;")) {
                btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
                a_cubes.remove(Integer.valueOf(btn.getUserData().toString()));

            }
        }
    public void manipVar(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.getStyle().contains("-fx-background-color: white;")) {
            btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            a_cones.add(Integer.valueOf(btn.getUserData().toString()));
        } else if (btn.getStyle().contains("-fx-background-color: yellow;")) {
            btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
            a_cones.remove(Integer.valueOf(btn.getUserData().toString()));
            a_cubes.add(Integer.valueOf(btn.getUserData().toString()));

        }
        else if (btn.getStyle().contains("-fx-background-color: purple;")) {
            btn.setStyle("-fx-background-color: white; -fx-border-color: black;");
            a_cubes.remove(Integer.valueOf(btn.getUserData().toString()));
        }
    }

    //timer functions
    public void startTimer(ActionEvent event) {e_timer.start();}
    public void stopTimer(ActionEvent event) {e_timer.stop();}
    public void resetTimer(ActionEvent event) {e_timer.reset();}

    //template incrementer functions
    public void increment(LimitedTextField txtfield) {
        txtfield.setText(String.valueOf(Integer.parseInt(txtfield.getText())+1));}
    public void decrement(LimitedTextField txtfield) {
        if(!txtfield.getText().equals("0")) txtfield.setText(String.valueOf(Integer.parseInt(txtfield.getText())-1));}

    //general methods for +/- buttons affecting corr. txtfields
    public void incrementT_cmty(ActionEvent event) {increment(t_cmty);}
    public void decrementT_cmty(ActionEvent event) {decrement(t_cmty);}
    public void incrementT_neutzone(ActionEvent event) {increment(t_neutzone);}
    public void decrementT_neutzone(ActionEvent event) {decrement(t_neutzone);}
    public void incrementT_singlesub(ActionEvent event) {increment(t_singlesub);}
    public void decrementT_singlesub(ActionEvent event) {decrement(t_singlesub);}
    public void incrementT_doublesub(ActionEvent event) {increment(t_doublesub);}
    public void decrementT_doublesub(ActionEvent event) {decrement(t_doublesub);}

    //used in collectData()
    private void collectDataCheckBox(CheckBox checkBox, String key) {info.put(key, String.valueOf(checkBox.isSelected()));}
    private void collectDataTextField(LimitedTextField textField, String key) {info.put(key, textField.getText());}
    private void collectDataArray(ArrayList<Integer> array, String key) {info.put(key, array.toString());}
    private void collectDataRating(Rating rating, String key) {info.put(key, String.valueOf(rating.getRating()));}
    private void collectDataTextArea(TextArea textArea, String key) {info.put(key, textArea.getText());}

    //used in reloadData()
    private void reloadDataCheckBox(CheckBox checkBox, String key) {checkBox.setSelected(Boolean.parseBoolean(info.get(key)));}
    private void reloadDataTextField(LimitedTextField textField, String key) {textField.setText(info.get(key));}
    private void reloadDataGridFieldGP(GridPane grid, ArrayList<Integer> coneArray, ArrayList<Integer> cubeArray) {
        int gridLength = grid.getChildren().size();
        for (int i=0; i < gridLength; i++) {
            Button btn = (Button) grid.getChildren().get(i);
            if (coneArray.contains(Integer.valueOf(btn.getUserData().toString()))) btn.setStyle("-fx-background-color: yellow; -fx-border-color: black;");
            else if (cubeArray.contains(Integer.valueOf(btn.getUserData().toString()))) btn.setStyle("-fx-background-color: purple; -fx-border-color: black;");
        }
    }
    private void reloadDataGridFieldPickup(GridPane grid, ArrayList<Integer> pickupArray) {
        int gridLength = grid.getChildren().size();
        for (int i=0; i < gridLength; i++) {
            Button btn = (Button) grid.getChildren().get(i);
            if (pickupArray.contains(Integer.valueOf(btn.getUserData().toString()))) btn.setStyle("-fx-background-color: green; -fx-border-color: black;");
        }
    }
    private void reloadDataRating(Rating rating, String key) {if (info.get(key) != null) rating.setRating(Integer.parseInt(info.get(key)));}
    private void reloadDataTextArea(TextArea textArea, String key) {textArea.setText(info.get(key));}
    private void reloadDataToggleGroup(ToggleGroup toggleGroup, String key) {
        int index = toggleGroup.getToggles().indexOf(toggleGroup.getSelectedToggle());
        if (index >= 0) toggleGroup.getToggles().get(index).setUserData(info.get(key));
        toggleMap.put(toggleGroup, index);
    }
}