package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.NetworkDevices.Computer;
import com.quartyom.lab6.QuApplication;
import com.quartyom.lab6.NetworkDevices.Smartphone;
import com.quartyom.lab6.NetworkDevices.Tablet;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChooseDeviceTypeSceneController extends SceneController{

    private static final Logger log = LoggerFactory.getLogger("scenes");
    Parent root;
    Button smartphoneButton, tabletButton, computerButton, cancelButton;

    public ChooseDeviceTypeSceneController(QuApplication application) {
        super(application);
        try {
            root = FXMLLoader.load(application.getClass().getResource("choose-device-type-view.fxml"));

            smartphoneButton = (Button) root.lookup("#smartphoneButton");
            tabletButton = (Button) root.lookup("#tabletButton");
            computerButton = (Button) root.lookup("#computerButton");
            cancelButton = (Button) root.lookup("#cancelButton");

            smartphoneButton.setOnAction(actionEvent -> {
                Smartphone smartphone = new Smartphone();
                application.localDB.addDevice(smartphone);
                ManageSceneController.idOfDeviceToLoad = smartphone.id;
                application.setScene(ManageSceneController.class);
            });

            tabletButton.setOnAction(actionEvent -> {
                Tablet tablet = new Tablet();
                application.localDB.addDevice(tablet);
                ManageSceneController.idOfDeviceToLoad = tablet.id;
                application.setScene(ManageSceneController.class);
            });

            computerButton.setOnAction(actionEvent -> {
                Computer computer = new Computer();
                application.localDB.addDevice(computer);
                ManageSceneController.idOfDeviceToLoad = computer.id;
                application.setScene(ManageSceneController.class);
            });

            cancelButton.setOnAction(actionEvent -> application.setScene(MainSceneController.class));

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        scene = new Scene(root);
    }

    @Override
    public void update() {}

    @Override
    public void stop() {}

}
