package ua.edu.sumdu.j2se.kikhtenkoDmytro;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.joda.time.format.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.ApiConfig;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.DatetimeRange;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.pojo.SearchParams;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.controller.util.ApiClient;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.Context;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.FxmlManager;
import ua.edu.sumdu.j2se.kikhtenkoDmytro.service.ThreadManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

public class Main extends Application {
    private static final Logger
            logger = LoggerFactory.getLogger(Main.class);
    private static final String CONFIG_FILE = "config.json";

    @Override
    public void start(Stage stage) throws IOException {
        Context.set("stage", stage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sign-in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Task manager");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void init() {
        logger.debug("Program started");

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            logger.error(
                    "Fatal error. " + Objects.requireNonNullElse(
                            e.getMessage(), e.toString())
            );
            Platform.exit();
        });

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule()).
                build();

        ApiConfig config = readConfig(mapper);
        if(config == null) {
            logger.debug("Using default configuration parameters");
            config = new ApiConfig();
            writeConfig(mapper, config);
        }

        ApiClient uriBuilder = new ApiClient(config);
        ThreadManager threadManager = new ThreadManager();
        FxmlManager fxmlManager = new FxmlManager();
        fxmlManager.setThreadManager(threadManager);

        DatetimeRange params = new DatetimeRange();
        params.setStart(LocalDateTime.now());
        params.setEnd(params.getStart().plusMinutes(5));

        Context.set("threadManager", threadManager);
        Context.set("fxmlManager", fxmlManager);
        Context.set("mapper", mapper);
        Context.set("apiClient", uriBuilder);
        Context.set("startTaskSearchParams", new SearchParams());
        Context.set("startUserSearchParams", new SearchParams());
        Context.set("startCalendarParams", params);
        Context.set("timeFormatter", DateTimeFormat.
                forPattern("HH:mm"));
        Context.set("dateFormatter", DateTimeFormat.
                forPattern("yyyy-MM-dd"));
        Context.set("dateTimeFormatter", DateTimeFormat.
                forPattern("yyyy-MM-dd HH:mm"));
        Context.set("periodFormatter",
                new PeriodFormatterBuilder()
                        .appendYears().appendSuffix("y")
                        .appendWeeks().appendSuffix("w")
                        .appendDays().appendSuffix("d")
                        .appendHours().appendSuffix("h")
                        .appendMinutes().appendSuffix("m")
                        .appendSeconds().appendSuffix("s")
                        .toFormatter());
    }

    public ApiConfig readConfig(ObjectMapper mapper) {
        logger.debug("Reading config file '" + CONFIG_FILE + "'");
        try {
            return mapper.readValue(
                    new File(CONFIG_FILE), ApiConfig.class);
        } catch (IOException e) {
            logger.debug(
                    "Config file '" + CONFIG_FILE + "' reading error"
            );
        }
        return null;
    }

    public void writeConfig(ObjectMapper mapper, ApiConfig config) {
        logger.debug("Writing config file '" + CONFIG_FILE + "'");
        try {
            mapper.writeValue(new File(CONFIG_FILE), config);
        } catch (IOException e) {
            logger.debug(
                    "Config file '" + CONFIG_FILE + "' writing error"
            );
        }
    }

    @Override
    public void stop() throws Exception {
        logger.debug("Program stopped");

        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}