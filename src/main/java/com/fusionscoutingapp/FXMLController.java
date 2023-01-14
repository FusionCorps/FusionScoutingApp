//TODO  checkBox pickup cones/cubes
//TODO high/mid/hybrid
//TODO type of intake - ground/handoff
//TODO climb time


package com.fusionscoutingapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

public class FXMLController {

    //scene0:begin
    //scene1:pregame
    //scene2:auton
    //scene3:teleop
    //scene4:endgame
    //scene5:othernotes
    //scene6:QR CODE


    //used for changing pages
    public static int sceneIndex = 0;
    //stores user input data
    private static HashMap<String, String> info = new HashMap<>();
    private static StringBuilder data;
    public BufferedImage bufferedImage;
    //compiles data in info HashMap into a String of text and sends to console/QR
    private static boolean isNextPageClicked = false;

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
        if (sceneIndex == 6) sceneIndex = 0;
        else sceneIndex++;
        isNextPageClicked = true;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setPage(stage);
    }
    public void prevPage(ActionEvent event) throws IOException {
        collectData();
        if (sceneIndex > 0) sceneIndex--;
        isNextPageClicked = false;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        setPage(stage);
    }

    //changes page to the scene specified by sceneIndex
    public static void setPage(Stage stage) throws IOException {
        System.out.println(sceneIndex);
        var thing = FXMLController.class.getResource("scenes/scene" + (sceneIndex) + ".fxml");
        Parent root = FXMLLoader.load(FXMLController.class.getResource("scenes/scene" + (sceneIndex) + ".fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Scouting App Page" + (sceneIndex));
        stage.setScene(scene);

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        stage.setWidth(size.getWidth()); //2736 px
        stage.setHeight(size.getHeight()); //1824 px
        stage.setMaximized(true);
        stage.show();
    }
    //sends data to QR code creator and displays it on screen
    public void sendInfo() throws Exception {
//       data = new StringBuilder();
////       Integer aca = Integer.parseInt(lcsa.getText()) + Integer.parseInt(ucsa.getText() + Integer.parseInt(cmda.getText()));
//       Integer tca = Integer.parseInt(info.get("lcst")) + Integer.parseInt(info.get("ucst"))+ Integer.parseInt(info.get("cmdt"));
////       info.put("aca", String.valueOf(aca));
//       info.put("tca", String.valueOf(tca));
//       for (Object keyName : info.keySet()) {
//           data.append(keyName).append("=");
//           if (info.get(keyName) == null) {}
//           else if (info.get(keyName).equals("true"))  data.append("1");
//           else if (info.get(keyName).equals("false")) data.append("0");
//           else if (info.get(keyName).equals("N/A") || info.get(keyName).equals("N/A or Failed")) data.append("0");
//           else if (info.get(keyName).equals("Below Average")) data.append("1");
//           else if (info.get(keyName).equals("Average")) data.append("2");
//           else if (info.get(keyName).equals("Above Average")) data.append("3");
//           else if (info.get(keyName).equals("Low Rung")) data.append("1");
//           else if (info.get(keyName).equals("Middle Rung")) data.append("2");
//           else if (info.get(keyName).equals("High Rung")) data.append("3");
//           else if (info.get(keyName).equals("Traversal Rung")) data.append("4");
//           else data.append(info.get(keyName));
//           data.append(";");
//        }
//
//        data = data.delete(data.lastIndexOf(";"), data.length());
//
////        two plausible ways to send QR Code
////        QRFuncs.generateQRCode(data, "src\\main\\codes\\qrcode" + info.get("mn") + "-" + info.get("tn") +".png");
//        bufferedImage = QRFuncs.generateQRCode(data.toString(), "qrcode.png");
//        File file = new File("qrcode.png");
//        Image img = new Image(file.getAbsolutePath());
//        imageBox.setImage(img);
//        System.out.println(Arrays.toString(info.entrySet().toArray()) + "info sent");
        }

    //sends data to info storage HashMap, needs to be edited with introduction of new data elements
    public void collectData() {
//        if (sceneIndex == 1) {
//            info.put("sln", sln.getText());
//            info.put("tn", tn.getText());
//            info.put("mn", mn.getText());
//            info.put("ml", ml.getValue());
//            info.put("ran", ran.getValue());
//            info.put("rp", rp.getValue());
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
//        } else {
//            System.out.println("default case");
//        }
//        System.out.println(Arrays.toString(info.entrySet().toArray()));
    }
    //reloads data for a scene, should be called when loading scene
    public void reloadData() {
//            if (sceneIndex == 1) {
//                if(info.get("sln")!=null)sln.setText(info.get("sln"));
//                if(info.get("mn")!=null)mn.setText(info.get("mn"));
//                if(info.get("tn")!=null)tn.setText(info.get("tn"));
//                if(info.get("ran")!=null)ran.setValue(info.get("ran"));
//                if(info.get("rp")!=null)rp.setValue(info.get("rp"));
//                if(info.get("ml")!=null)ml.setValue(info.get("ml"));
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
    }

    //puts restrictions on certain data fields
    public void limit(KeyEvent keyEvent) {
//        LimitedTextField src = (LimitedTextField) keyEvent.getSource();
//        if (src.equals(tn)) {
//            src.setIntegerField();
//            src.setMaxLength(4);
//        }
//        else if (src.equals(mn)) {
//            src.setIntegerField();
//            src.setMaxLength(3);
//        }
//        else if (src.equals(sln)) {
//            src.setRestrict("[A-Za-z ]"); //letters + spaces only
//            src.setMaxLength(30);
//        }
    }

}