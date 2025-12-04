package incuat.kg.svetoofor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;



//это че за код ваще я ни панимаю



import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

public class TrafficLightApp extends Application {
    private TrafficLightSpec wsClient = new TrafficLightSpec();



    private Circle redCircle = new Circle(27, Color.DARKGRAY);
    private Circle yellowCircle = new Circle(27, Color.DARKGRAY);
    private Circle greenCircle = new Circle(27, Color.DARKGRAY);



    private Timeline redBlinkTimeline;
    private Timeline yellowBlinkTimeline;
    private Timeline greenBlinkTimeline;

    private boolean isIncidentActive = false; // Отслеживаем активный инцидент (красный горит)
    private boolean isAdmin = false;

    private final String ADMIN_LOGIN = "s_mirlanova";
    private final String ADMIN_PASSWORD = "qwerty";

    public static void main(String[] args) {
        launch(args);
    }

    public static void launchApp(String admin) {
    }

    @Override
    public void start(Stage primaryStage) {
        showRoleSelection(primaryStage);
    }

    private void showRoleSelection(Stage stage) {
        // Устанавливаем иконку приложения (временно отключено)
        /*try {
            Image icon = new Image(getClass().getResourceAsStream("/svetofor.ico"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }*/

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
        stage.setTitle("Светофор - Выбор роли");
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
        stage.initStyle(StageStyle.UTILITY);

        // Устанавливаем иконку приложения (временно отключено)
        /*try {
            Image icon = new Image(getClass().getResourceAsStream("/svetofor.ico"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }*/

        VBox trafficLight = new VBox(5);
        trafficLight.setAlignment(Pos.CENTER);
        trafficLight.setStyle(
            "-fx-padding: 8;" +
            "-fx-background-color: #1a1a1a;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #333333;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );



        Label redLabel = new Label("Инцидент");
        Label yellowLabel = new Label("Алерт");
        Label greenLabel = new Label("Инцидент решен");

        String labelStyle = "-fx-font-size: 11px; -fx-font-weight: bold;";

        redLabel.setStyle(labelStyle);
        yellowLabel.setStyle(labelStyle);
        greenLabel.setStyle(labelStyle);

        redLabel.setTextFill(Color.WHITE);
        yellowLabel.setTextFill(Color.WHITE);
        greenLabel.setTextFill(Color.WHITE);

        // --- Простые круги без эффектов ---
        redCircle.setStroke(Color.BLACK);
        redCircle.setStrokeWidth(2);

        yellowCircle.setStroke(Color.BLACK);
        yellowCircle.setStrokeWidth(2);

        greenCircle.setStroke(Color.BLACK);
        greenCircle.setStrokeWidth(2);


        VBox redBox = new VBox(3, redLabel, redCircle);
        redBox.setAlignment(Pos.CENTER);
        VBox yellowBox = new VBox(3, yellowLabel, yellowCircle);
        yellowBox.setAlignment(Pos.CENTER);
        VBox greenBox = new VBox(3, greenLabel, greenCircle);
        greenBox.setAlignment(Pos.CENTER);

        trafficLight.getChildren().addAll(redBox, yellowBox, greenBox);

        VBox root = new VBox(10, trafficLight);
        root.setAlignment(Pos.CENTER);

        // Временный тестовый режим - кликабельно для всех (не только админ)
        redCircle.setOnMouseClicked(e -> {
            blinkColor(redCircle, Color.RED, 120, "red");
            wsClient.sendMessage("RED_BLINK");
        });
        yellowCircle.setOnMouseClicked(e -> {
            blinkColor(yellowCircle, Color.YELLOW, 120, "yellow");
            wsClient.sendMessage("YELLOW_BLINK");
        });
        greenCircle.setOnMouseClicked(e -> {
            blinkColor(greenCircle, Color.LIMEGREEN, 120, "green");
            wsClient.sendMessage("GREEN_BLINK");
        });

        Scene scene = new Scene(root, 130, 250);
        root.setStyle("-fx-background-color: #000000;");  // Измените #808080 на нужный цвет
        stage.setScene(scene);

        // Автопозиция в правом нижнем углу
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        stage.setX(screenWidth - 170);
        stage.setY(screenHeight - 320);

        stage.show();

        // Гарантируем, что окно всегда остается поверх
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                stage.toFront();
            }
        });
    }

    private void blinkColor(Circle circle, Color color, int seconds, String colorName) {
        // Если включается зеленый и есть активный инцидент (красный горит) - это разрешение инцидента
        if (colorName.equals("green") && isIncidentActive) {
            // Останавливаем красный таймер
            if (redBlinkTimeline != null) {
                redBlinkTimeline.stop();
            }
            // Гасим красный
            redCircle.setFill(Color.DARKGRAY);
            isIncidentActive = false;
        }

        // Если зажигается красный и зеленый горит (новый инцидент пока предыдущий разрешается)
        if (colorName.equals("red") && greenCircle.getFill() != Color.DARKGRAY) {
            // Останавливаем зеленый таймер
            if (greenBlinkTimeline != null) {
                greenBlinkTimeline.stop();
            }
            // Гасим зеленый
            greenCircle.setFill(Color.DARKGRAY);
        }

        // Зажигаем нужный цвет
        circle.setFill(color);

        // Если зажигается красный - активируем флаг инцидента
        if (colorName.equals("red")) {
            isIncidentActive = true;
        }

        // Останавливаем предыдущий таймер для этого цвета
        Timeline existingTimeline = switch (colorName) {
            case "red" -> redBlinkTimeline;
            case "yellow" -> yellowBlinkTimeline;
            case "green" -> greenBlinkTimeline;
            default -> null;
        };
        if (existingTimeline != null) {
            existingTimeline.stop();
        }

        // Через указанное время гасим этот цвет (возвращаем серый)
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds), e -> {
                    circle.setFill(Color.DARKGRAY);
                    // Если гаснет красный - снимаем флаг инцидента
                    if (colorName.equals("red")) {
                        isIncidentActive = false;
                    }
                })
        );
        timeline.setCycleCount(1);
        timeline.play();

        // Сохраняем таймер
        switch (colorName) {
            case "red" -> redBlinkTimeline = timeline;
            case "yellow" -> yellowBlinkTimeline = timeline;
            case "green" -> greenBlinkTimeline = timeline;
        }
    }

    private void turnOffAllLights() {
        redCircle.setFill(Color.DARKGRAY);
        yellowCircle.setFill(Color.DARKGRAY);
        greenCircle.setFill(Color.DARKGRAY);
    }


    public void handleServerMessage(String message) {
            switch (message) {
                case "RED_BLINK" -> blinkColor(redCircle, Color.RED, 5, "red");
                case "YELLOW_BLINK" -> blinkColor(yellowCircle, Color.YELLOW, 5, "yellow");
                case "GREEN_BLINK" -> blinkColor(greenCircle, Color.LIMEGREEN, 5, "green");
            }
        }
}

