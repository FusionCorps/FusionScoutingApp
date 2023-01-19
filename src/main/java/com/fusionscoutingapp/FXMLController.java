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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
    @FXML private LimitedTextField p_sn; //scouter name`
    @FXML private LimitedTextField p_tnum; //team number
    @FXML private ComboBox<String> p_mlvl; //match level
    @FXML private LimitedTextField p_mnum; //match number
    @FXML private ComboBox<String> p_ran; //robot alliance number
    @FXML private ComboBox<String> p_rfp; //robot field position

    //page 2 - auton
    @FXML private CheckBox a_mob; //mobility
    @FXML private ComboBox<String> a_pre; // game piece preload
    @FXML private ComboBox<String> a_cstat; //charging station robot status

    //page 3 - teleop/endgame
    @FXML private LimitedTextField t_neut; //neutral zone game pieces intaked
    @FXML private LimitedTextField t_singlesub; //single substation game pieces intaked
    @FXML private LimitedTextField t_doublesub; //double substation game pieces intaked
    @FXML private LimitedTextField t_cmty; //community game pieces intaked

    @FXML private ComboBox<String> e_cstat; //charging station robot status
    @FXML private CheckBox e_budclmb; //buddy climb
    @FXML private ComboBox<String> e_statloc; //endgame charging station
    @FXML private Timer e_timer; //docking timer

    //page4 - qualitative notes
    @FXML private Rating n_drat; //driver rating
    @FXML private Rating n_spd; //robot speed (1 slow, 5 fast)
    @FXML private CheckBox n_dt; // died/tipped
    @FXML private ComboBox<String> de; //defensive evasion
    @FXML private ComboBox<String> n_dp; //defensive performance
    @FXML private TextArea n_co; //general comments

    //page5 - QR code
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
        System.out.println(page);
        var thing = FXMLController.class.getResource("scenes/scene" + (page) + ".fxml");
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
           if (info.get(keyName) == null) {}
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

    //collects data from each page and stores it in a hashmap

    //sends data to info storage HashMap, needs to be edited with introduction of new data elements
    public void collectData() {
        if (sceneIndex == 1) {
            info.put("p_sn", p_sn.getText());
            info.put("p_tnum", p_tnum.getText());
            info.put("p_mlvl", p_mlvl.getValue());
            info.put("p_mnum", p_mnum.getText());
            info.put("p_ran", p_ran.getValue());
            info.put("p_rfp", p_rfp.getValue());
//        } else if (sceneIndex == 2) {
//            info.put("cp", String.valueOf(cp.isSelected()));
//            info.put("aca", aca.getText());
//            info.put("ucsa", ucsa.getText());
//            info.put("lcsa", lcsa.getText());
//            info.put("cmda", cmda.getText());
//            info.put("ta", String.valueOf(ta.isSelected()));
//        } else if (sceneIndex == 3) {
//            info.put("ucst", ucst.getText());
//            info.put("lcst", lcst.getText());
//            info.put("cmdt", cmdt.getText());
//        } else if (sceneIndex == 4) {
//            info.put("cla", String.valueOf(cla.isSelected()));
//            info.put("cl", cl.getValue());
//        } else if (sceneIndex == 5) {
//            info.put("dt", String.valueOf(dt.isSelected()));
//            info.put("dp", dp.getValue());
//            info.put("de", de.getValue());
//            info.put("co", co.getText());
//            info.put("f", f.getText());
//            info.put("tf", tf.getText());
        } else {
            System.out.println("default case");
        }
        System.out.println(Arrays.toString(info.entrySet().toArray()));
    }
    //reloads data for a scene, should be called when loading scene
    public void reloadData() {
            if (sceneIndex == 1) {
                if (info.get("p_sn") != null) p_sn.setText(info.get("p_sn"));
                if (info.get("p_tnum") != null) p_tnum.setText(info.get("p_tnum"));
                if (info.get("p_mlvl") != null) p_mlvl.setValue(info.get("p_mlvl"));
                if (info.get("p_mnum") != null) p_mnum.setText(info.get("p_mnum"));
                if (info.get("p_ran") != null) p_ran.setValue(info.get("p_ran"));
                if (info.get("p_rfp") != null) p_rfp.setValue(info.get("p_rfp"));
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
    public void doCopyToClipboard(ActionEvent event) {
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

    //outputs data to text file
    public void outputDataToFile() {
        try {
            FileWriter writer = new FileWriter("C:\\Users\\robotics\\Desktop\\scoutingFiles\\" +
                info.get("p_mnum") + "-" +
                info.get("p_tnum") + "-" +
                info.get("p_ran") + ".txt");
             writer.write(data.toString());
        } catch (IOException e) {
            System.out.println("outputData failed");;
        }
    }

    //outputs data to QR code
    public void outputQRCode() {
        try {
            String filePath = "C:\\Users\\robotics\\Desktop\\scoutingQRCodes\\" +
                    info.get("p_mnum") + "-" +
                    info.get("p_tnum") + "-" +
                    info.get("p_ran") + ".png";
            int size = 250;
            String fileType = "png";
            File qrFile = new File(filePath);
            ImageIO.write(bufferedImage, fileType, qrFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //outputs data to text file and QR code
    public void outputAll() {
        outputDataToFile();
        outputQRCode();
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
        else if (src.equals(p_sn)) { //scouter name
            src.setRestrict("[A-Za-z ]"); //letters + spaces only
            src.setMaxLength(30);
        }
    }

}