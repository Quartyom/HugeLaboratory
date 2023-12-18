package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.QuApplication;
import com.quartyom.lab6.NetworkDevices.NetworkDevice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainSceneController extends SceneController{
    private static final Logger log = LoggerFactory.getLogger("scenes");
    Parent root;
    ObservableList<NetworkDevice> items;
    TableView<NetworkDevice> tableView;
    Button addButton, manageButton, deleteButton, saveButton, loadButton;
    CheckBox dbCheckBox;

    public MainSceneController(QuApplication application){
        super(application);
        try {
            root = FXMLLoader.load(application.getClass().getResource("main-view.fxml"));
            items = FXCollections.observableArrayList();

            tableView = (TableView<NetworkDevice>) root.lookup("#tableView");

            addButton = (Button) root.lookup("#addButton");
            manageButton = (Button) root.lookup("#manageButton");
            deleteButton = (Button) root.lookup("#deleteButton");
            saveButton = (Button) root.lookup("#saveButton");
            loadButton = (Button) root.lookup("#loadButton");
            dbCheckBox = (CheckBox) root.lookup("#dbCheckBox");

            TableColumn<NetworkDevice, Long> idColumn = new TableColumn<>("id");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<NetworkDevice, String> typeColumn = new TableColumn<>("type");
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

            TableColumn<NetworkDevice, String> nameColumn = new TableColumn<>("name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<NetworkDevice, Float> positionColumn = new TableColumn<>("position");
            positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

            // Add columns
            tableView.getColumns().add(idColumn);
            tableView.getColumns().add(typeColumn);
            tableView.getColumns().add(nameColumn);
            tableView.getColumns().add(positionColumn);

            addButton.setOnAction(actionEvent -> {
                application.setScene(ChooseDeviceTypeSceneController.class);
            });
            manageButton.setOnAction(actionEvent -> {
                NetworkDevice selectedDevice = getSelectedDevice();
                if (selectedDevice != null){
                    // сообщить сцене о выбранном id
                    ManageSceneController.idOfDeviceToLoad = selectedDevice.id;
                    application.setScene(ManageSceneController.class);
                }
            });
            deleteButton.setOnAction(actionEvent -> {
                NetworkDevice selectedDevice = getSelectedDevice();
                if (selectedDevice != null) {
                    application.localDB.removeDevice(selectedDevice);
                    update();
                }
            });
            saveButton.setOnAction(actionEvent -> {
                application.saveAppState();
                if (dbCheckBox.isSelected()) {
                    if (!application.remoteDB.saveToDB()) {
                        application.showResult("Ошибка",
                                "Не удалось сохранить данные в базу данных. Данные сохранены локально",
                                MainSceneController.class);
                    }
                }
            });
            loadButton.setOnAction(actionEvent -> {
                if (dbCheckBox.isSelected()){
                    if (!application.remoteDB.loadFromDB()){
                        application.showResult("Ошибка",
                                "Не удалось загрузить данные из базы данных. Вы работаете в локальной версии",
                                MainSceneController.class);
                    }
                }
                else {
                    application.loadAppState();
                }
                update();
            });

        } catch (IOException e) {
            log.error(e.getMessage());
        }

        scene = new Scene(root);
        log.info("Created Main Scene");

    }

    public NetworkDevice getSelectedDevice(){
        NetworkDevice selectedDevice = tableView.getSelectionModel().getSelectedItem();
        if (selectedDevice == null){
            log.error("{}: Device not chosen by user", getClass().getSimpleName());
            ResultSceneController.labelText = "Ошибка";
            ResultSceneController.textAreaText = "Не выбрано устройство";
            ResultSceneController.whereToReturn = MainSceneController.class;
            application.setScene(ResultSceneController.class);
        }
        return selectedDevice;
    }

    @Override
    public void update() {
        items.clear();
        items.addAll(application.localDB.getDevices());
        tableView.setItems(items);
    }

    @Override
    public void stop() {}
}

