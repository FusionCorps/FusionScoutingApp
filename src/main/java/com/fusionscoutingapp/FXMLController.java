//TODO type of intake - ground/handoff
//TODO cycle timer/docking timer
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.Rating;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.TextArea;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FXMLController {

    //scene0:
    //scene1:pregame
    //scene2:auton
    //scene3:teleop + endgame
    //scene4:qualitative notes
    //scene5:QR CODE

//data for each page, variables should be named the same as corresponding fx:ids for consistency

    //page 1 - pregame

    @FXML private LimitedTextField p_tnum; //team number
//    @FXML private ComboBox<String> p_mlvl; //match level
    @FXML private LimitedTextField p_mnum; //match number
    @FXML private ToggleGroup p_ra; //robot alliance
    @FXML private ToggleGroup p_sloc; //starting location
    private int p_raIndex, p_slocIndex; //index of selected radio button in each group

    //page 2 - auton
    @FXML private CheckBox a_mob; //mobility
    @FXML private ToggleGroup a_pre; // GP type preload
    @FXML ArrayList<Integer> a_pickup; //GP intaked at community
    private ArrayList<Integer> a_cones = new ArrayList<>(); //cones placed
    private ArrayList<Integer> a_cubes = new ArrayList<>(); //cubes placed
    @FXML private ToggleGroup a_balstat; //auton balance status
    private int a_preIndex, a_balstatIndex; //index of selected radio button in each group

    //page 3 - teleop
    @FXML private LimitedTextField t_neut; //neutral zone GP intaked
    @FXML private LimitedTextField t_singlesub; //singlesub GP intaked
    @FXML private LimitedTextField t_doublesub; //doublesub GP intaked
    @FXML private LimitedTextField t_cmty; //community GP intaked
    @FXML private LimitedTextField t_cones; //cones intaked
    @FXML private LimitedTextField t_cubes; //cubes intaked


    //page 4 - endgame
    @FXML private CheckBox e_shut; //shuttlebot
    @FXML private ToggleGroup e_balstat; //endgame balance status
    @FXML private CheckBox e_budclmb; //buddy climb
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
    private  int n_dtraintypeIndex; //index of selected radio button in each group

    //page6 - QR code
    @FXML private ImageView imageBox; //QR code image display box
    @FXML private Text reminderBox; //You scouted, "[insert team #, alliance #]"


    //used for changing pages
    private static int sceneIndex = 0;
    private BufferedImage bufferedImage;
    //stores user input data
    private HashMap<String, String> info = new HashMap<>();
    private StringBuilder data = new StringBuilder();
    private boolean isNextPageClicked = false;

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
//        //reload data for each page
//       reloadData();
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

    //sends data to info storage HashMap, needs to be edited with introduction of new data elements
    public void collectData() {
        switch (sceneIndex) {
            case 1:
                info.put("p_tnum", p_tnum.getText());
//                info.put("p_mlvl", p_mlvl.getValue());
                info.put("p_mnum", p_mnum.getText());
                //get index of selected radio button in p_ra.getToggles()
                p_raIndex = p_ra.getToggles().indexOf(p_ra.getSelectedToggle());
                info.put("p_ra", p_ra.getToggles().get(p_raIndex).getUserData().toString());
                //get index of selected radio button in p_sloc.getToggles()
                p_slocIndex = p_sloc.getToggles().indexOf(p_sloc.getSelectedToggle());
                info.put("p_sloc", p_sloc.getToggles().get(p_slocIndex).getUserData().toString());
                break;
            case 2:
                info.put("a_mob", String.valueOf(a_mob.isSelected()));
                a_preIndex = a_pre.getToggles().indexOf(a_pre.getSelectedToggle());
                info.put("a_pre", a_pre.getToggles().get(a_preIndex).getUserData().toString());
//                info.put("a_pickup", a_pickup.toString());
                info.put("a_cones", a_cones.toString());
                info.put("a_cubes", a_cubes.toString());
                a_balstatIndex = a_balstat.getToggles().indexOf(a_balstat.getSelectedToggle());
                info.put("a_balstat", a_balstat.getToggles().get(a_balstatIndex).getUserData().toString());
                break;
            case 3:
                //TODO
                break;
            case 4:
                //TODO
                break;
            case 5:
                info.put("n_dtrainrat", String.valueOf(n_dtrainrat.getRating()));
                info.put("n_dtraintype", n_dtraintype.getSelectedToggle().toString());
                info.put("n_intake", String.valueOf(n_intake.getRating()));
                info.put("n_spd", String.valueOf(n_spd.getRating()));
                info.put("n_drat", String.valueOf(n_drat.getRating()));
                info.put("n_sn", n_sn.getText());
                info.put("n_co", n_co.getText());
                break;
            default:
                System.out.println("collectData() default");
                break;
        }
        System.out.println("stuff:" + Arrays.toString(info.entrySet().toArray()));
    }
    //reloads data for a scene, should be called when loading scene
    public void reloadData() {
            if (sceneIndex == 1) {
                if (info.get("p_tnum") != null) p_tnum.setText(info.get("p_tnum"));
//                if (info.get("p_mlvl") != null) p_mlvl.setValue(info.get("p_mlvl"));
                if (info.get("p_mnum") != null) p_mnum.setText(info.get("p_mnum"));
                if(info.get("p_ra")!=null) p_ra.selectToggle(p_ra.getToggles().get(p_raIndex));
                if (info.get("p_sloc") != null) p_sloc.selectToggle(p_sloc.getToggles().get(p_slocIndex));
//            } else if (sceneIndex == 2) {
//                if(info.get("cp")!=null)cp.setSelected(Boolean.parseBoolean(info.get("cp")));
//                if(info.get("aca")!=null)aca.setText(info.get("aca"));
//                if(info.get("ucsa")!=null)ucsa.setText(info.get("ucsa"));
//                if(info.get("lcsa")!=null)lcsa.setText(info.get("lcsa"));
//                if(info.get("cmda")!=null)cmda.setText(info.get("cmda"));
//                if(info.get("ta")!=null)ta.setSelected(Boolean.parseBoolean(info.get("ta")));
//            } else if (sceneIndex == 3) {
//                if(info.get("ucst")!=null)ucst.setText(info.get("ucst"));
//                if(info.get("lcst")!=null) lcst.setText(info.get("lcst"));
//                if(info.get("cmdt")!=null)cmdt.setText(info.get("cmdt"));
//            } else if (sceneIndex == 4) {
//                if(info.get("cla")!=null)cla.setSelected(Boolean.parseBoolean(info.get("cla")));
//                if(info.get("cl")!=null)cl.setValue(info.get("cl"));
//            } else if (sceneIndex == 5) {
//                if(info.get("dt")!=null)dt.setSelected(Boolean.parseBoolean(info.get("dt")));
//                if(info.get("de")!=null)de.setValue(info.get("de"));
//                if(info.get("dp")!=null)dp.setValue(info.get("dp"));
//                if(info.get("co")!=null)co.setText(info.get("co"));
//                if(info.get("f")!=null)f.setText(info.get("f"));
//                if(info.get("tf")!=null)tf.setText(info.get("tf"));
//            }
//            else if (sceneIndex == 6) {
//                if(info.get("tn")!=null)reminderBox.setText(info.get("sln") + " Scouted Team " + info.get("tn") + ".");
//            }
            } else {
                System.out.println("default case");
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
    public void startTimer(ActionEvent event) {((Timer)event.getSource()).start();}
    public void stopTimer(ActionEvent event) {((Timer)event.getSource()).stop();}
    public void resetTimer(ActionEvent event) {((Timer)event.getSource()).reset();}

}