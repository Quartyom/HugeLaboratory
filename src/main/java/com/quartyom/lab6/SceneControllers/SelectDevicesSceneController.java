package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.NetworkDevices.Computer;
import com.quartyom.lab6.QuApplication;
import com.quartyom.lab6.NetworkDevices.NetworkDevice;
import com.quartyom.lab6.NetworkDevices.Smartphone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;

public class SelectDevicesSceneController extends SceneController {

    private static final Logger log = LoggerFactory.getLogger("scenes");
    Parent root;
    URL tileResource;
    Label actionLabel;
    Button cancelButton, selectAllButton, nextButton;
    ScrollPane scrollPane;
    ObservableList<Parent> items;
    HashSet<Long> chosenDevices;

    public static NetworkDevice targetDevice;
    public static String action;

    public SelectDevicesSceneController(QuApplication application) {
        super(application);
        try {
            root = FXMLLoader.load(application.getClass().getResource("select-devices-view.fxml"));
            tileResource = application.getClass().getResource("id-name-pick-tile.fxml");

            actionLabel = (Label) root.lookup("#actionLabel");
            cancelButton = (Button) root.lookup("#cancelButton");
            selectAllButton = (Button) root.lookup("#selectAllButton");
            nextButton = (Button) root.lookup("#nextButton");
            scrollPane = (ScrollPane) root.lookup("#scrollPane");

            items = FXCollections.observableArrayList();
            chosenDevices = new HashSet<>();

            cancelButton.setOnAction(actionEvent -> application.setScene(ManageSceneController.class));
            selectAllButton.setOnAction(actionEvent -> {
                for (NetworkDevice device : application.localDB.getDevices()){
                    if (device != targetDevice) {
                        chosenDevices.add(device.id);
                    }
                }
                goNext();

            });
            nextButton.setOnAction(actionEvent -> goNext());

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        scene = new Scene(root);
    }

    private void goNext(){
        StringBuilder message = new StringBuilder();
        switch (action) {
            case "wifi":
                for (Long item : chosenDevices) {
                    message.append("id ").append(item).append(": ");
                    message.append(targetDevice.connectToWiFi(item));
                    message.append("\n");
                }
                break;
            case "bluetooth":
                for (Long item : chosenDevices) {
                    message.append("id ").append(item).append(": ");
                    message.append(targetDevice.requestBluetoothConnection(item));
                    message.append("\n");
                }
                break;
            case "noblue":
                for (Long item : chosenDevices) {
                    message.append("id ").append(item).append(": ");
                    message.append(targetDevice.disconnectBluetooth(item));
                    message.append("\n");
                }
                break;
            case "wire":
                if (targetDevice instanceof Computer computer) {
                    for (Long item : chosenDevices) {
                        message.append("id ").append(item).append(": ");
                        message.append(computer.wire(item));
                        message.append("\n");
                    }
                }
                else {
                    System.err.println("Wrong device " + targetDevice);
                }
                break;
            case "call":
                if (targetDevice instanceof Smartphone smartphone){
                    for (Long item : chosenDevices) {
                        message.append("id ").append(item).append(": ");
                        message.append(smartphone.call(item));
                        message.append("\n");
                    }
                }
                else {
                    log.error("Wrong device " + targetDevice);
                }
                break;
            default:
                System.err.println("Wrong action");
                log.error("Wrong action " + action);
        }

        ResultSceneController.labelText = "Результат";
        ResultSceneController.textAreaText = message.toString();
        ResultSceneController.whereToReturn = ManageSceneController.class;
        application.setScene(ResultSceneController.class);
    }

    @Override
    public void update() {
        if (action.equals("wifi")){
            actionLabel.setText("Подключение по wifi");
        }
        else if (action.equals("bluetooth")){
            actionLabel.setText("Подключение по bluetooth");
        }
        else if (action.equals("noblue")){
            actionLabel.setText("Отключение от bluetooth");
        }
        else if (action.equals("wire")){
            actionLabel.setText("Подключение по проводу");
        }
        else if (action.equals("call")){
            actionLabel.setText("Звонок");
        }

        items.clear();
        chosenDevices.clear();

        for (NetworkDevice item : application.localDB.getDevices()){
            if (item == targetDevice){continue;}
            try {

                Parent tile = FXMLLoader.load(tileResource);

                Label idLabel = (Label) tile.lookup("#idLabel");
                Label nameLabel = (Label) tile.lookup("#nameLabel");
                CheckBox checkBox = (CheckBox) tile.lookup("#checkBox");

                idLabel.setText("id: " + item.id);
                nameLabel.setText("name: " + item.name);

                checkBox.setOnAction(event -> {
                    if (checkBox.isSelected()) {
                        chosenDevices.add(item.id);
                    }
                    else {
                       chosenDevices.remove(item.id);
                    }
                });

                items.add(tile);
            }
            catch (Exception ex){
                log.error(ex.getMessage());
            }
        }

        scrollPane.setContent(new ListView<>(items));
    }

    @Override
    public void stop() {}
}
