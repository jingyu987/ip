package gui;

import commands.Command;
import exceptions.FishronException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import parser.Parser;
import storage.Storage;
import tasks.TaskList;
import ui.Ui;

/**
 * The main class for the Fishron task management application.
 */
public class Fishron extends Application {

    // Fields
    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;
    private Scene scene;
    private Image user = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image duke = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Initializes a new instance of the Fishron class.
     *
     * @param filePath The file path for storing task data.
     */
    public Fishron(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.taskList = storage.loadTasksFromFile();
    }

    /**
     * Initializes a new instance of the Fishron class with a default file path.
     */
    public Fishron() {
        this.ui = new Ui();
        this.storage = new Storage("./data/fishron.txt");
        this.taskList = storage.loadTasksFromFile();
    }

    @Override
    public void start(Stage stage) {
        // Step 1. Setting up required components

        // The container for the content of the chat to scroll.
        scrollPane = new ScrollPane();
        dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        userInput = new TextField();
        sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        // Step 2. Formatting the window to look as expected
        stage.setTitle("Fishron");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        // Step 3. Add functionality to handle user input.
        sendButton.setOnMouseClicked((event) -> {
            handleUserInput();
        });

        userInput.setOnAction((event) -> {
            handleUserInput();
        });

        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        Label welcomeMessage = new Label(ui.showWelcomeMessage());
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(welcomeMessage, new ImageView(duke))
        );
    }

    /**
     * Handles user input by processing it and displaying the appropriate response in the chat.
     * This method is called when the user clicks the "Send" button or presses Enter after typing a message.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        String output = getResponse(input);
        Label userText = new Label(input);
        Label dukeText = new Label(output);
        if (input.toLowerCase().equals("bye")) {
            closeProgram();
        }
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getDukeDialog(dukeText, new ImageView(duke))
        );
        userInput.clear();
    }

    /**
     * Generates a response to user input by parsing and executing a command.
     *
     * @param input The user's input message.
     * @return The response message generated by the application.
     */
    private String getResponse(String input) {
        try {
            Command command = Parser.parse(input, taskList);
            String output = command.execute(taskList, ui, storage);
            return output;
        } catch (FishronException e) {
            return ui.showErrorMessage(e.getMessage());
        }
    }

    /**
     * Closes the Fishron application by saving tasks to a file and exiting the program.
     */
    private void closeProgram() {
        storage.saveTasksToFile(taskList.getList());
        System.exit(0);
    }

    /**
     * Runs the Fishron application in console mode.
     */
    public void run() {
        ui.showWelcomeMessage();
        boolean isExit = false;

        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand, this.taskList);
                command.execute(taskList, ui, storage);
                isExit = command.isExit();
            } catch (FishronException e) {
                ui.showErrorMessage(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * The main entry point of the Fishron application.
     *
     * @param args The command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        new Fishron("./data/fishron.txt").run();
    }
}