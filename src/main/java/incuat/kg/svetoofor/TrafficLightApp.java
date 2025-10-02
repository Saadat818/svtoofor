package incuat.kg.svetoofor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

public class TrafficLightApp extends Application {
    private TrafficLightSpec wsClient = new TrafficLightSpec();



    private Circle redCircle = new Circle(27, Color.RED);
    private Circle yellowCircle = new Circle(27, Color.YELLOW);
    private Circle greenCircle = new Circle(27, Color.GREEN);



    private Timeline redBlinkTimeline;
    private Timeline yellowBlinkTimeline;
    private Timeline greenBlinkTimeline;

    private boolean isAdmin = false;

    private final String ADMIN_LOGIN = "s_mirlanova";
    private final String ADMIN_PASSWORD = "qwerty";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        showRoleSelection(primaryStage);
    }

    private void showRoleSelection(Stage stage) {
        Label label = new Label("Выберите роль:");
        Button adminButton = new Button("Админ");
        Button specialistButton = new Button("Специалист");

        HBox buttons = new HBox(10, adminButton, specialistButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, label, buttons);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(200, 100);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Выбор роли");
        stage.show();

        adminButton.setOnAction(e -> {
            if (showLoginDialog()) {
                isAdmin = true;
                showTrafficLightStage();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Неверный логин или пароль", ButtonType.OK);
                alert.showAndWait();
            }
        });

        specialistButton.setOnAction(e -> {
            isAdmin = false;
            showTrafficLightStage();
            stage.close();
        });
    }

    private boolean showLoginDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Авторизация Админа");

        Label userLabel = new Label("Логин:");
        Label passLabel = new Label("Пароль:");
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Pair<>(userField.getText(), passField.getText());
            }
            return null;
        });

        var result = dialog.showAndWait();
        return result.isPresent() &&
                ADMIN_LOGIN.equals(result.get().getKey()) &&
                ADMIN_PASSWORD.equals(result.get().getValue());
    }

    private void showTrafficLightStage() {

        wsClient.connect("ws://localhost:8887", this);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.DECORATED);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);

        VBox trafficLight = new VBox(4);
        trafficLight.setAlignment(Pos.CENTER);



        Label redLabel = new Label("Инцидент");
        Label yellowLabel = new Label("Алерт");
        Label greenLabel = new Label("Нет инцидента");
        redLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        yellowLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        greenLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        redLabel.setTextFill(Color.WHITE);
        yellowLabel.setTextFill(Color.WHITE);
        greenLabel.setTextFill(Color.WHITE);

        // --- Улучшение внешнего вида кругов ---
        redCircle.setStroke(Color.DARKRED.darker());
        redCircle.setStrokeWidth(2);
        DropShadow redGlow = new DropShadow();
        redGlow.setColor(Color.RED);
        redGlow.setRadius(15);
        redGlow.setSpread(0.4);
        redCircle.setEffect(redGlow);

        yellowCircle.setStroke(Color.GOLDENROD.darker());
        yellowCircle.setStrokeWidth(2);
        DropShadow yellowGlow = new DropShadow();
        yellowGlow.setColor(Color.YELLOW);
        yellowGlow.setRadius(15);
        yellowGlow.setSpread(0.4);
        yellowCircle.setEffect(yellowGlow);

        greenCircle.setStroke(Color.DARKGREEN.darker());
        greenCircle.setStrokeWidth(2);
        DropShadow greenGlow = new DropShadow();
        greenGlow.setColor(Color.LIMEGREEN);
        greenGlow.setRadius(15);
        greenGlow.setSpread(0.4);
        greenCircle.setEffect(greenGlow);


        VBox redBox = new VBox(1, redLabel, redCircle);
        redBox.setAlignment(Pos.CENTER);
        VBox yellowBox = new VBox(1, yellowLabel, yellowCircle);
        yellowBox.setAlignment(Pos.CENTER);
        VBox greenBox = new VBox(1, greenLabel, greenCircle);
        greenBox.setAlignment(Pos.CENTER);

        trafficLight.getChildren().addAll(redBox, yellowBox, greenBox);

        VBox root = new VBox(20, trafficLight);
        root.setAlignment(Pos.CENTER);
        if (isAdmin) {
            redCircle.setOnMouseClicked(e -> {
                blinkColor(redCircle, Color.RED, 20, "red");
                wsClient.sendMessage("RED_BLINK");
            });
            yellowCircle.setOnMouseClicked(e -> {
                blinkColor(yellowCircle, Color.YELLOW, 20, "yellow");
                wsClient.sendMessage("YELLOW_BLINK");
            });
            greenCircle.setOnMouseClicked(e -> {
                blinkColor(greenCircle, Color.LIMEGREEN, 20, "green");
                wsClient.sendMessage("GREEN_BLINK");
            });
        }

        Scene scene = new Scene(root, 128, 245);
        root.setStyle("-fx-background-color: #2F2F2F;");
        stage.setScene(scene);

        // Автопозиция в правом нижнем углу
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        stage.setX(screenWidth - 170);
        stage.setY(screenHeight - 350);

        stage.show();
    }

    private void blinkColor(Circle circle, Color color, int seconds, String colorName) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> circle.setFill(color)),
                new KeyFrame(Duration.seconds(0.5), e -> resetCircleColor(circle, colorName))
        );
        timeline.setCycleCount(seconds * 2);
        timeline.play();

        // Перезаписываем соответствующий Timeline, но не останавливаем другие
        switch (colorName) {
            case "red" -> {
                if (redBlinkTimeline != null) redBlinkTimeline.stop();
                redBlinkTimeline = timeline;
            }
            case "yellow" -> {
                if (yellowBlinkTimeline != null) yellowBlinkTimeline.stop();
                yellowBlinkTimeline = timeline;
            }
            case "green" -> {
                if (greenBlinkTimeline != null) greenBlinkTimeline.stop();
                greenBlinkTimeline = timeline;
            }
        }
    }

    private void resetCircleColor(Circle circle, String colorName) {
        switch (colorName) {
            case "red" -> circle.setFill(Color.DARKRED);
            case "yellow" -> circle.setFill(Color.DARKGOLDENROD);
            case "green" -> circle.setFill(Color.DARKGREEN);
        }
    }

    public void handleServerMessage(String message) {
            switch (message) {
                case "RED_BLINK" -> blinkColor(redCircle, Color.RED, 20, "red");
                case "YELLOW_BLINK" -> blinkColor(yellowCircle, Color.YELLOW, 20, "yellow");
                case "GREEN_BLINK" -> blinkColor(greenCircle, Color.LIMEGREEN, 20, "green");
            }
        }
    }

