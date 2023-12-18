package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.*;
import com.quartyom.lab6.NetworkDevices.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class ManageSceneController extends SceneController{
    private static final Logger log = LoggerFactory.getLogger("scenes");
    public static long idOfDeviceToLoad;
    Parent root;
    Label caption;
    TextField nameText, positionText, drawingText, wireText, callText;
    TextArea connectionsText;
    Button saveButton, backButton, wifiButton, nowifiButton, bluetoothButton, noblueButton, wireButton, callButton, updateButton;
    NetworkDevice device;

    ConnectionsThread connectionsThread;

    public ManageSceneController(QuApplication application) {
        super(application);
        try {
            root = FXMLLoader.load(application.getClass().getResource("manage-view.fxml"));

            caption = (Label) root.lookup("#caption");
            nameText = (TextField) root.lookup("#nameText");
            positionText = (TextField) root.lookup("#positionText");
            drawingText = (TextField) root.lookup("#drawingText");
            wireText = (TextField) root.lookup("#wireText");
            callText = (TextField) root.lookup("#callText");
            connectionsText = (TextArea) root.lookup("#connectionsText");

            saveButton = (Button) root.lookup("#saveButton");
            backButton = (Button) root.lookup("#backButton");
            wifiButton = (Button) root.lookup("#wifiButton");
            nowifiButton = (Button) root.lookup("#nowifiButton");
            bluetoothButton = (Button) root.lookup("#bluetoothButton");
            noblueButton = (Button) root.lookup("#noblueButton");
            wireButton = (Button) root.lookup("#wireButton");
            callButton = (Button) root.lookup("#callButton");
            updateButton = (Button) root.lookup("#updateButton");

            updateButton.setOnAction(actionEvent -> updateConnections());

            saveButton.setOnAction(actionEvent -> {
                save();
                update();
            });

            backButton.setOnAction(actionEvent -> {
                application.setScene(MainSceneController.class);
            });

            wifiButton.setOnAction(actionEvent -> {
                SelectDevicesSceneController.targetDevice = device;
                SelectDevicesSceneController.action = "wifi";
                application.setScene(new SelectDevicesSceneController(application));
            });
            nowifiButton.setOnAction(actionEvent -> {
                device.disconnectFromWiFi();
                update();
            });
            bluetoothButton.setOnAction(actionEvent -> {
                SelectDevicesSceneController.targetDevice = device;
                SelectDevicesSceneController.action = "bluetooth";
                application.setScene(new SelectDevicesSceneController(application));
            });
            noblueButton.setOnAction(actionEvent -> {
                SelectDevicesSceneController.targetDevice = device;
                SelectDevicesSceneController.action = "noblue";
                application.setScene(new SelectDevicesSceneController(application));
            });
            wireButton.setOnAction(actionEvent -> {
                SelectDevicesSceneController.targetDevice = device;
                SelectDevicesSceneController.action = "wire";
                application.setScene(new SelectDevicesSceneController(application));
            });
            callButton.setOnAction(actionEvent -> {
                SelectDevicesSceneController.targetDevice = device;
                SelectDevicesSceneController.action = "call";
                application.setScene(new SelectDevicesSceneController(application));
            });


        } catch (IOException e) {
            log.error(e.getMessage());
        }

        scene = new Scene(root);
    }

    public void save(){
        device.name = nameText.getText();
        try {
            device.position = Float.parseFloat(positionText.getText());
        }
        catch (Exception ex){
            log.error(ex.getMessage());
        }

        if (device instanceof Smartphone smartphone){
            smartphone.callResponse = callText.getText();
        }
        else if (device instanceof Tablet tablet){
            tablet.drawing = drawingText.getText();
        }
        else if (device instanceof Computer computer){
            computer.wireResponse = wireText.getText();
        }
    }

    @Override
    public void update() {
        device = Medium.idToDevice(idOfDeviceToLoad);
        if (device != null) {
            connectionsThread = new ConnectionsThread(this);
            connectionsThread.start();

            caption.setText(device.nameWithId());
            nameText.setText(device.name);
            positionText.setText(String.valueOf(device.position));

            if (device instanceof Smartphone smartphone){
                caption.setText("Smartphone, id: " + smartphone.id);
                callText.setText(smartphone.callResponse);
                clearDisable(drawingText);
                callText.setDisable(false);
                clearDisable(wireText);
                wireButton.setDisable(true);
                callButton.setDisable(false);
            }
            else if (device instanceof Tablet tablet){
                caption.setText("Tablet, id: " + tablet.id);
                drawingText.setDisable(false);
                clearDisable(callText);
                clearDisable(wireText);
                wireButton.setDisable(true);
                callButton.setDisable(true);
            }
            else if (device instanceof Computer computer){
                caption.setText("Computer, id: " + computer.id);
                wireText.setText(computer.wireResponse);
                clearDisable(drawingText);
                clearDisable(callText);
                wireText.setDisable(false);
                wireButton.setDisable(false);
                callButton.setDisable(true);
            }
        }
        else {
            log.error("{}: NO DEVICE PROVIDED", getClass().getSimpleName());
        }

    }
    @Override
    public void stop() {
        if (connectionsThread != null) {
            connectionsThread.interrupt();
        }
    }

    private void clearDisable(TextField node){
        node.setDisable(true);
        node.setText(null);
    }

    public volatile long last_time_updated = 0;
    synchronized public void updateConnections(){
        if (device != null) {
            last_time_updated = System.currentTimeMillis();
            String text = "Обновлено: %s\n%s".
                    formatted(new Date(last_time_updated).toString(), device.connectionsToString());
            connectionsText.setText(text);
        }
    }


}
