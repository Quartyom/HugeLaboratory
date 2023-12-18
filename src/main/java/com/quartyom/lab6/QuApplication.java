package com.quartyom.lab6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.quartyom.lab6.Database.LocalDB;
import com.quartyom.lab6.Database.RemoteDB;
import com.quartyom.lab6.SceneControllers.*;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuApplication extends Application {
    public Stage stage;
    public LocalDB localDB;
    public RemoteDB remoteDB;
    static HashMap<Class, SceneController> sceneControllers;
    private static final Logger log = LoggerFactory.getLogger(QuApplication.class);

    public final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    public final Properties properties = new Properties();

    @Override
    public void start(Stage stage) {
        log.info("Program started");
        this.stage = stage;
        readProperties();
        localDB = new LocalDB();
        remoteDB = new RemoteDB(this);

        sceneControllers = new HashMap<>();
        addSceneController(new MainSceneController(this));
        addSceneController(new ManageSceneController(this));
        addSceneController(new ChooseDeviceTypeSceneController(this));
        addSceneController(new SelectDevicesSceneController(this));
        addSceneController(new ResultSceneController(this));

        loadAppState();
        setScene(MainSceneController.class);

        stage.setTitle("Lab 6. Network devices");
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.getIcons().add(new Image("logo.png"));    // hardcoded, don't change
        stage.setOnCloseRequest( actionEvent -> sceneControllers.forEach((k, v) -> v.stop()) ); // закрыть все сцены

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public void saveAppState(){
        try (FileOutputStream fos = new FileOutputStream(properties.getProperty("appDataFile"));
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(localDB);
            log.info("Saved app state");
        } catch (IOException e) {
            log.error("Couldn't save app state, " + e.getMessage());
        }
    }

    public void loadAppState(){
        try (FileInputStream fis = new FileInputStream(properties.getProperty("appDataFile"));
            ObjectInputStream ois = new ObjectInputStream(fis))
        {
            localDB.setDevices(  ((LocalDB)ois.readObject()).getDevices()  );
            log.info("Loaded app state");
        }
        catch (IOException | ClassNotFoundException e) {
            log.error("Couldn't load app state, " + e.getMessage());
        }
    }

    public void readProperties(){
        //try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties"))
        try (InputStream input = new FileInputStream("app.properties")) {
            properties.load(input);
            log.info("Loaded properties");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void addSceneController(SceneController sceneController){
        sceneControllers.put(sceneController.getClass(), sceneController);
    }

    private SceneController currentSceneController;

    public void setScene(Class name){
        if (currentSceneController != null) {
            currentSceneController.stop();
        }
        currentSceneController = sceneControllers.get(name);
        currentSceneController.update();
        stage.setScene(currentSceneController.getScene());
        log.info("Set scene " + name);
    }

    public void setScene(SceneController sceneController){
        addSceneController(sceneController);
        setScene(sceneController.getClass());
    }

    public void showResult(String labelText, String textAreaText, Class whereToReturn){
        ResultSceneController.labelText = labelText;
        ResultSceneController.textAreaText = textAreaText;
        ResultSceneController.whereToReturn = whereToReturn;
        setScene(ResultSceneController.class);
    }

    public void stopScenes(){
        sceneControllers.forEach((k,v) -> v.stop());
    }


}