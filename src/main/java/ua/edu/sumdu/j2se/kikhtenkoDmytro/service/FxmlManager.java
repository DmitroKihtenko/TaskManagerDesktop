package ua.edu.sumdu.j2se.kikhtenkoDmytro.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.Main;

import java.io.IOException;

public class FxmlManager {
    private static final Logger logger =
            LoggerFactory.getLogger(FxmlManager.class);
    private ThreadManager threadManager;

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public void setThreadManager(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    public void loadFxml(String location) {
        if(threadManager != null) {
            threadManager.removeAll();
        }

        Platform.runLater(() -> {
            Stage stage = Context.get("stage");
            Scene scene = stage.getScene();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.
                    getResource(location));
            try {
                scene.setRoot(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        });
    }
}
