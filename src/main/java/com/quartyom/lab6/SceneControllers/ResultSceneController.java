package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.QuApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResultSceneController extends SceneController{

    private static final Logger log = LoggerFactory.getLogger("scenes");
    Parent root;
    Label label;
    TextArea textArea;
    Button okButton;

    public static String labelText;
    public static String textAreaText;
    public static Class whereToReturn;

    public ResultSceneController(QuApplication application) {
        super(application);
        try {
            root = FXMLLoader.load(application.getClass().getResource("result-view.fxml"));

            label = (Label) root.lookup("#label");
            textArea = (TextArea) root.lookup("#textArea");
            okButton = (Button) root.lookup("#okButton");

        }
        catch (IOException e) {
            log.error(e.getMessage());
        }

        scene = new Scene(root);
    }


    @Override
    public void update() {
        label.setText(labelText);
        textArea.setText(textAreaText);
        okButton.setOnAction(actionEvent -> application.setScene(whereToReturn));
    }

    @Override
    public void stop() {}
}
