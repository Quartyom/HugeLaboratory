package com.quartyom.lab6.SceneControllers;

import com.quartyom.lab6.QuApplication;
import javafx.scene.Scene;

public abstract class SceneController {
    protected Scene scene;

    protected QuApplication application;

    public SceneController(QuApplication application){
        this.application = application;
    }

    public Scene getScene(){
        return scene;
    }

    public abstract void update();
    public abstract void stop();
}
