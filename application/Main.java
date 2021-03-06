package application;
/**
 * Main.java created by Abhilash Dharmavarapu on Lenovo Thinkpad X1 Extreme in MilkWeightProject
 *
 * Author: Abhilash Dharmavarapu (dharmavarapu@wisc.edu)
 * Date: 04/26/2020
 *
 * Course: CS400
 * Semester: Spring 2020
 * Lecture: 001
 *
 * IDE: Eclipse IDE for Java Developers
 * Version: 2019-12 (4.14.0)
 * Build id: 20191212-1212
 *
 * Device: DHARMAVARAPU_X1EXTREME
 * OS: Windows 10 Pro
 * Version: 1903
 * OS Build: 18362.535
 *
 * List of Collaborators: Name, email.wisc.edu, lecture number
 * Atharva Kudkilwar, kudkilwar@wisc.edu, lecture 002
 *
 * Other Credits: describe other source (web sites or people)
 *
 * Known Bugs: describe known unresolved bugs here
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The graphical user interface of the Milk Weight program
 * 
 * @author Abhilash Dharmavarapu, Atharva Kudkilwar
 *
 */
public class Main extends Application {
	private List<String> args; // System arguments
	private static final String APP_TITLE = "Milk Weights"; // Title of the main window
	private FarmTable farmTable; // Farm table to store data
	private final Comparator c = new Comparator<Label>() { // Comparator used to sort the lists
		/**
		 * Compares the beginning of each label(id)
		 * 
		 * @param o1 first label to compare
		 * @param o2 second label to compare
		 * @return int comparison in the form of o1.compareTo(o2)
		 */
		@Override
		public int compare(Label o1, Label o2) {
			String s1 = o1.getText().substring(0, o1.getText().indexOf(':')).trim();
			String s2 = o2.getText().substring(0, o2.getText().indexOf(':')).trim();
			String numS1 = numericalString(s1);
			String numS2 = numericalString(s2);
			if (numS1.length() == 0) {
				numS1 = "0";
			}
			if (numS2.length() == 0) {
				numS2 = "0";
			}
			return Integer.parseInt(numS1) - Integer.parseInt(numS2);
		}

		public String numericalString(String string) {
			String numS = "";
			for (int i = 0; i < string.length() - 1; i++) {
				String c = string.substring(i, i + 1);
				try {
					int number = Integer.parseInt(c);
					numS += c;
				} catch (Exception e) {
					// pass
				}
			}
			return numS;
		}
	};

	/**
	 * Method that runs show primary Stage
	 * 
	 * @param primaryStage Stage passed down in launch of application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		farmTable = new FarmTable();
		args = this.getParameters().getRaw();
		BorderPane root = new BorderPane();

		// Set left side of the main screen
		VBox leftButtons = new VBox();
		Button newData = new Button("", new Label("Add/Remove Data"));
		newData.setOnAction(e -> addRemoveDataWindow(primaryStage));
		Button updateData = new Button("", new Label("Update Data"));
		updateData.setOnAction(e -> updateDataWindow(primaryStage));
		leftButtons.getChildren().addAll(newData, updateData);
		leftButtons.setSpacing(15);
		root.setLeft(leftButtons);

		// Set Right side of the main screen
		VBox rightButtons = new VBox();
		Button weightDifference = new Button("", new Label("Recent Growth"));
		weightDifference.setOnAction(e -> weightDifferenceWindow(primaryStage));
		Button uploadData = new Button("", new Label("Upload Data"));
		uploadData.setOnAction(e -> uploadFileWindow(primaryStage));
		rightButtons.getChildren().addAll(weightDifference, uploadData);
		rightButtons.setSpacing(15);
		root.setRight(rightButtons);

		// Set bottom of the main screen
		Button viewData = new Button("", new Label("View Results"));
		viewData.setOnAction(e -> viewDataWindow(primaryStage));
		root.setBottom(viewData);
		BorderPane.setAlignment(viewData, Pos.BASELINE_CENTER);

		// Output Results button
		Button outputResults = new Button("", new Label("Output Results "));
		outputResults.setOnAction(e -> outputResults(primaryStage));
		BorderPane.setAlignment(outputResults, Pos.CENTER);

		root.setCenter(outputResults);
		// Finalize and set stage to main scene
		Scene mainScene = new Scene(root);
		primaryStage.setWidth(400);
		primaryStage.setHeight(150);
		primaryStage.setTitle(APP_TITLE);
		primaryStage.setScene(mainScene);
		primaryStage.show();

		// Prompt user for file before going to dashboard
		uploadFileWindow(primaryStage);
	}

	/**
	 * Window to output analysis to a file
	 * 
	 * @param primaryStage primaryStage parent stage to set modality
	 */
	private void outputResults(Stage primaryStage) {
		Stage oR = new Stage();
		oR.setTitle("Output Results");
		oR.initModality(Modality.APPLICATION_MODAL);
		oR.initOwner(primaryStage);
		BorderPane root = new BorderPane();

		// Setting up options horizontal box to select which type of output you prefer
		ToggleGroup group = new ToggleGroup();
		RadioButton rb1 = new RadioButton("Farm Report");
		rb1.setToggleGroup(group);
		RadioButton rb2 = new RadioButton("Annual Report");
		rb2.setToggleGroup(group);
		RadioButton rb3 = new RadioButton("Monthly Report");
		rb3.setToggleGroup(group);
		RadioButton rb4 = new RadioButton("Date Range Report");
		rb4.setToggleGroup(group);
		HBox options = new HBox();
		options.setSpacing(10);
		options.getChildren().addAll(rb1, rb2, rb3, rb4);

		HBox hb0 = new HBox(); // HBox for farm ID line
		TextField farmID = new TextField();
		farmID.setPromptText("Farm ID");
		hb0.getChildren().addAll(new Label("Enter Farm ID: "), farmID);

		HBox hb1 = new HBox(); // HBox for year line
		TextField year = new TextField();
		year.setPromptText("yyyy");
		hb1.getChildren().addAll(new Label("Year: "), year);

		HBox hb2 = new HBox(); // HBox for month line
		TextField month = new TextField();
		month.setPromptText("mm");
		hb2.getChildren().addAll(new Label("Month:  "), month);

		HBox hb3 = new HBox(); // HBox for start date
		TextField startDate = new TextField();
		startDate.setPromptText("yyyy-mm-dd");
		hb3.getChildren().addAll(new Label("Start Date: "), startDate);

		HBox hb4 = new HBox(); // HBox for start date
		TextField endDate = new TextField();
		endDate.setPromptText("yyyy-mm-dd");
		hb4.getChildren().addAll(new Label("End Date: "), endDate);

		HBox hb5 = new HBox();
		TextField filePath = new TextField(); // HBox for file path
		filePath.setPromptText("file path (.csv)");
		hb5.getChildren().addAll(new Label("File Path: "), filePath);

		// Box of input types for specific output
		VBox inputBox = new VBox();
		inputBox.setSpacing(25);

		HBox buttonCheckBox = new HBox();
		Button bt = new Button("Output"); // Output Button

		// Check if you output to file
		CheckBox output = new CheckBox("Output to file");
		buttonCheckBox.getChildren().addAll(bt, output);
		buttonCheckBox.setSpacing(10);
		buttonCheckBox.setVisible(false);

		// Change the inputs available based on output option
		rb1.setOnAction(e -> {
			buttonCheckBox.setVisible(true);
			inputBox.getChildren().clear();
			inputBox.getChildren().addAll(hb0, hb1, hb5);
		});
		rb2.setOnAction(e -> {
			buttonCheckBox.setVisible(true);
			inputBox.getChildren().clear();
			inputBox.getChildren().addAll(hb1, hb5);
		});
		rb3.setOnAction(e -> {
			buttonCheckBox.setVisible(true);
			inputBox.getChildren().clear();
			inputBox.getChildren().addAll(hb1, hb2, hb5);
		});
		rb4.setOnAction(e -> {
			buttonCheckBox.setVisible(true);
			inputBox.getChildren().clear();
			inputBox.getChildren().addAll(hb3, hb4, hb5);
		});

		// Data list for output
		ListView results = new ListView();

		// Vertical box containing options, input and upload button
		VBox vb = new VBox();
		vb.getChildren().addAll(options, inputBox, buttonCheckBox, results);
		vb.setSpacing(25);
		root.setCenter(vb);

		// Upload and compute outputs based off output selection
		bt.setOnAction(e -> {
			Alert alert = new Alert(AlertType.ERROR, "", ButtonType.CANCEL);
			String path = filePath.getText();
			// If farm report
			if (rb1.isSelected()) {
				try {
					farmTable.farmReport(farmID.getText(), year.getText(), results);
					if (output.isSelected()) {
						listViewtoFile(results, path);
						Alert success = new Alert(AlertType.CONFIRMATION, "Results successfully uploaded to " + path,
								ButtonType.OK);
						success.show();
					}
				} catch (IOException e1) {
					alert.setContentText(path + " not found");
					alert.show();
				} catch (NumberFormatException n) {
					alert.setContentText("year format is incorrect");
					alert.show();
				}
				// If annual report
			} else if (rb2.isSelected()) {
				try {
					farmTable.annualReport(year.getText(), results);
					results.getItems().sort(c);
					vb.getChildren().remove(results);
					vb.getChildren().add(results);
					if (output.isSelected()) {
						listViewtoFile(results, path);
						Alert success = new Alert(AlertType.CONFIRMATION, "Results successfully uploaded to " + path,
								ButtonType.OK);
						success.show();
					}
				} catch (IOException e1) {
					alert.setContentText(path + " not found");
					alert.show();
				} catch (NumberFormatException n) {
					alert.setContentText("year format is incorrect");
					alert.show();
				}
				// If monthly report
			} else if (rb3.isSelected()) {
				try {
					farmTable.monthlyReport(year.getText(), month.getText(), results);
					results.getItems().sort(c);
					vb.getChildren().remove(results);
					vb.getChildren().add(results);
					if (output.isSelected()) {
						listViewtoFile(results, path);
						Alert success = new Alert(AlertType.CONFIRMATION, "Results successfully uploaded to " + path,
								ButtonType.OK);
						success.show();
					}
				} catch (IOException e1) {
					alert.setContentText(path + " not found");
					alert.show();
				} catch (NumberFormatException n) {
					alert.setContentText("year format is incorrect");
					alert.show();
				}
				// If date range report
			} else if (rb4.isSelected()) {
				try {
					farmTable.dateRangeReport(startDate.getText(), endDate.getText(), results);
					results.getItems().sort(c);
					vb.getChildren().remove(results);
					vb.getChildren().add(results);
					if (output.isSelected()) {
						listViewtoFile(results, path);
						Alert success = new Alert(AlertType.CONFIRMATION, "Results successfully uploaded to " + path,
								ButtonType.OK);
						success.show();
					}
				} catch (IOException e1) {
					alert.setContentText(path + " not found");
					alert.show();
				} catch (NumberFormatException n) {
					alert.setContentText("year format is incorrect");
					alert.show();
				}
			}
		});

		// Set scene and size of window
		Scene sc = new Scene(root);
		oR.setScene(sc);
		oR.setHeight(500);
		oR.setWidth(500);
		oR.show();

	}

	/**
	 * Converts list view to file
	 * 
	 * @param results listView source
	 * @param f       file destination
	 * @throws IOException if file is not found
	 */
	private void listViewtoFile(ListView results, String f) throws IOException {
		File check = new File(f);
		if (!check.exists() || check.isDirectory()) {
			throw new IOException();
		}
		FileWriter writer = new FileWriter(check);
		for (Object l : results.getItems()) {
			writer.write(((Label) l).getText() + "\n");
		}
		writer.flush();
		writer.close();
	}

	/**
	 * Method to create a new window for viewing data
	 * 
	 * @param primaryStage parent stage to set modality
	 */
	private void viewDataWindow(Stage primaryStage) {
		Stage vD = new Stage();
		vD.setTitle("Data Viewer");
		vD.initModality(Modality.APPLICATION_MODAL);
		vD.initOwner(primaryStage);
		BorderPane root = new BorderPane();

		// Set the titles of headings of the window
		Label title1 = new Label("By Farm:");
		Label title2 = new Label("By Month-Year:");
		Label title3 = new Label("All Farm Data: ");
		title1.setStyle("-fx-font-weight: bold");
		title2.setStyle("-fx-font-weight: bold");
		title3.setStyle("-fx-font-weight: bold");
		HBox titleBox = new HBox();
		titleBox.getChildren().addAll(title1, title3, title2);
		titleBox.setAlignment(Pos.TOP_CENTER);
		titleBox.setSpacing(350);

		// Set the search by farm results section
		VBox byFarm = new VBox();
		HBox userInput1 = new HBox();
		TextField farmId = new TextField();
		farmId.setPromptText("Farm ID");
		farmId.setFocusTraversable(false);
		TextField year1 = new TextField();
		year1.setPromptText("Year: yyyy");
		year1.setFocusTraversable(false);
		Button go1 = new Button("Go");
		userInput1.getChildren().addAll(farmId, year1, go1);
		ListView results1 = new ListView();
		go1.setOnAction(e -> farmTable.onFarmFilter(farmId.getText(), year1.getText(), results1));
		byFarm.getChildren().addAll(userInput1, results1);

		// Set the search by date results section
		VBox byMonth = new VBox();
		HBox userInput2 = new HBox();
		TextField month = new TextField();
		month.setPromptText("Month: mm");
		month.setFocusTraversable(false);
		TextField year2 = new TextField();
		year2.setPromptText("Year: yyyy");
		year2.setFocusTraversable(false);
		Button go2 = new Button("Go");
		userInput2.getChildren().addAll(month, year2, go2);
		ListView results2 = new ListView();
		go2.setOnAction(e -> {
			farmTable.onMonthFilter(month.getText(), year2.getText(), results2);
			results2.getItems().sort(c);
		});
		byMonth.getChildren().addAll(userInput2, results2);

		// Set the filter of all farms section
		VBox allFarms = new VBox();
		HBox userInput3 = new HBox();
		TextField month2 = new TextField();
		month2.setPromptText("Month: mm");
		month2.setFocusTraversable(false);
		TextField year3 = new TextField();
		year3.setPromptText("Year: yyyy");
		year3.setFocusTraversable(false);
		Button filter = new Button("Filter");
		ListView allFarmsList = new ListView();
		filter.setOnAction(e -> {
			farmTable.monthlyReport(year3.getText(), month2.getText(), allFarmsList);
			allFarmsList.getItems().sort(c);
		});
		userInput3.getChildren().addAll(month2, year3, filter);
		allFarms.getChildren().addAll(userInput3, allFarmsList);

		// Fix alignment and set each search to their section
		BorderPane.setAlignment(titleBox, Pos.CENTER);
		root.setTop(titleBox);
		root.setLeft(byFarm);
		root.setCenter(allFarms);
		root.setRight(byMonth);

		// Finalize properties of window and scene
		Scene sc = new Scene(root);
		vD.setScene(sc);
		vD.setHeight(500);
		vD.setWidth(1200);
		vD.show();
	}

	/**
	 * Method to create a new window for seeing weight difference(most recent
	 * increase or decrease)
	 * 
	 * @param primaryStage parent stage to set modality
	 */
	private void weightDifferenceWindow(Stage primaryStage) {
		// CREATE WINDOW FOR WEIGHT DIFFERENCE
		Stage wD = new Stage();
		wD.setTitle("Recent Growth");
		wD.initModality(Modality.APPLICATION_MODAL);
		wD.initOwner(primaryStage);

		BorderPane root = new BorderPane();
		Scene main = new Scene(root);
		// For prompt and text input
		HBox hb = new HBox();
		TextField idPrompt = new TextField();
		idPrompt.setPromptText("[Unique String ID]");
		idPrompt.setFocusTraversable(false);
		hb.getChildren().addAll(new Label("Enter Farm ID: "), idPrompt);

		Button bt = new Button("DONE"); // Done Button
		bt.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * Handle the done button event
			 * 
			 * @param arg0 current action event
			 */
			@Override
			public void handle(ActionEvent arg0) {
				String ID = idPrompt.getText();
				// GET MOST RECENT WEIGHT DIFFERNECE AND GO TO
				Farm f = farmTable.get(ID);
				if (f == null) {
					Alert error = new Alert(AlertType.ERROR, "Farm ID does not exist in data.", ButtonType.OK);
					error.show();
					wD.close();
					return;
				}
				// NEW WINDOW DISPLAYING DIFFERENCE
				Alert success = new Alert(AlertType.CONFIRMATION, "Most recent growth/decay is: " + f.getDifference(),
						ButtonType.OK);
				success.show();
				wD.close();
			}
		});
		root.setTop(hb);
		root.setBottom(bt);
		hb.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(bt, Pos.BASELINE_CENTER);
		wD.setHeight(125);
		wD.setWidth(300);
		wD.setScene(main);
		wD.show();
	}

	/**
	 * Method to create a new window for updating existing data
	 * 
	 * @param primaryStage parent stage to set modality
	 */
	private void updateDataWindow(Stage primaryStage) {
		// CREATE WINDOW FOR UPDATE DATA
		Stage uD = new Stage();
		uD.setTitle("Update Existing Data");
		uD.initModality(Modality.APPLICATION_MODAL);
		uD.initOwner(primaryStage);

		BorderPane root = new BorderPane();

		HBox hb0 = new HBox(); // HBox for farm ID line
		TextField farmID = new TextField();
		farmID.setPromptText("Farm ID");
		hb0.getChildren().addAll(new Label("Enter Farm ID: "), farmID);

		HBox hb1 = new HBox(); // HBox for Previous Date line
		TextField oldDate = new TextField();
		oldDate.setPromptText("yyyy-mm-dd");
		hb1.getChildren().addAll(new Label("Enter Previous Date: "), oldDate);

		HBox hb2 = new HBox(); // HBox for New Date line
		TextField newDate = new TextField();
		newDate.setPromptText("yyyy-mm-dd");
		hb2.getChildren().addAll(new Label("Enter New Date: "), newDate);

		HBox hb3 = new HBox(); // HBox for Old Weight
		TextField oldWeight = new TextField();
		oldWeight.setPromptText("Enter Weight");
		hb3.getChildren().addAll(new Label("Enter Previous Weight: "), oldWeight);

		HBox hb4 = new HBox();
		TextField newWeight = new TextField(); // HBox for new Weight
		newWeight.setPromptText("Enter Weight: ");
		hb4.getChildren().addAll(new Label("Enter New Weight: "), newWeight);

		Button bt = new Button("DONE"); // Done Button
		bt.setOnAction(new EventHandler<ActionEvent>() {
			Alert alert = new Alert(AlertType.ERROR, "", ButtonType.CANCEL); // Alert window

			/**
			 * Handle the done button event
			 * 
			 * @param arg0 current action event
			 */
			@Override
			public void handle(ActionEvent arg0) {
				try {
					String ID = farmID.getText();
					int oldWeightVal = Integer.parseInt(oldWeight.getText());
					int newWeightVal = Integer.parseInt(newWeight.getText());
					String[] oldDateArray = oldDate.getText().split("-");
					String[] newDateArray = newDate.getText().split("-");
					if (oldDateArray.length != 3 || newDateArray.length != 3) {
						throw new IllegalArgumentException();
					}
					// UPDATE FARM IN DATA STRUCTURE HERE
					Farm f = farmTable.get(ID);
					if (f == null) {
						alert.setContentText("The given ID does not exist in the data.");
						alert.show();
						return;
					}
					f.update(oldDate.getText(), newDate.getText(), oldWeightVal, newWeightVal);
					Alert success = new Alert(AlertType.CONFIRMATION, "Data has been successfully updated",
							ButtonType.OK);
					success.show();

				} catch (IllegalArgumentException i) {
					alert.setContentText("Please follow format shown in text field for the date.");
					alert.show();
				} finally {
					uD.close();
				}
			}
		});
		VBox vb = new VBox();
		vb.getChildren().addAll(hb0, hb1, hb2, hb3, hb4, bt);
		vb.setSpacing(25);

		root.setCenter(vb);

		Scene sc = new Scene(root);
		uD.setScene(sc);
		uD.setHeight(500);
		uD.setWidth(350);
		uD.show();
	}

	/**
	 * Method to create a new window for adding or removing data increase or
	 * decrease)
	 * 
	 * @param primaryStage parent stage to set modality
	 */
	public void addRemoveDataWindow(Stage primaryStage) {
		// CREATE WINDOW FOR NEW DATA
		Stage nD = new Stage();
		nD.setTitle("Add/Remove Data");
		nD.initModality(Modality.APPLICATION_MODAL);
		nD.initOwner(primaryStage);
		BorderPane root = new BorderPane();

		HBox hb1 = new HBox(); // HBox for farm ID line
		TextField farmID = new TextField();
		farmID.setPromptText("Farm ID");
		hb1.getChildren().addAll(new Label("Enter Farm ID: "), farmID);

		HBox hb2 = new HBox(); // HBox for Date line
		TextField date = new TextField();
		date.setPromptText("yyyy-mm-dd");
		hb2.getChildren().addAll(new Label("Enter Date: "), date);

		HBox hb3 = new HBox(); // HBox for Weight line
		TextField weight = new TextField();
		weight.setPromptText("Enter Weight");
		hb3.getChildren().addAll(new Label("Enter Milk Weight: "), weight);

		HBox hb4 = new HBox();
		Button add = new Button("Add");
		Button remove = new Button("Remove");
		hb4.getChildren().addAll(add, remove);
		hb4.setSpacing(5);
		Alert alert = new Alert(AlertType.ERROR, "", ButtonType.CANCEL);
		remove.setOnAction(new EventHandler<ActionEvent>() {

			/**
			 * Handle the done button event
			 * 
			 * @param arg0 current action event
			 */
			@Override
			public void handle(ActionEvent arg0) {
				try {
					String ID = farmID.getText();
					String[] dateArray = date.getText().split("-");
					checkDate(dateArray);
					// CHECK IF FARM ID EXISTS
					if (farmTable.get(ID) == null) {
						throw new NullPointerException();
					}
					// REMOVE DATA
					farmTable.get(ID).remove(date.getText());
					Alert success = new Alert(AlertType.CONFIRMATION, "Data has been successfully removed",
							ButtonType.OK);
					success.show();
				} catch (IllegalArgumentException i) {
					alert.setContentText("Please follow format shown in text field.");
					alert.show();
				} catch (NullPointerException n) {
					alert.setContentText("The given ID does not exist in the data.");
					alert.show();
				} finally {
					nD.close();
				}
			}
		});
		add.setOnAction(new EventHandler<ActionEvent>() {

			/**
			 * Handle the done button event
			 * 
			 * @param arg0 current action event
			 */
			@Override
			public void handle(ActionEvent arg0) {
				try {
					String ID = farmID.getText();
					int weightVal = Integer.parseInt(weight.getText());
					String[] dateArray = date.getText().split("-");
					// CHECK IF DATE IS IN VALID FORMAT
					checkDate(dateArray);
					// CHECK IF FARM EXISTS
					if (farmTable.get(ID) == null) {
						throw new NullPointerException();
					}
					farmTable.get(ID).updateFarm(date.getText(), weightVal);
					Alert success = new Alert(AlertType.CONFIRMATION, "Data has been successfully added",
							ButtonType.OK);
					success.show();
				} catch (IllegalArgumentException i) {
					alert.setContentText("Please follow format shown in text field.");
					alert.show();
				} catch (NullPointerException n) {
					alert.setContentText("The given ID does not exist in the data.");
					alert.show();
				} finally {

					nD.close();
				}
			}
		});

		VBox vb = new VBox(); // VBox to put all 4 elements together
		vb.getChildren().addAll(hb1, hb2, hb3, hb4);
		vb.setSpacing(25);

		root.setCenter(vb);

		Scene scene = new Scene(root);
		nD.setScene(scene);
		nD.setWidth(500);
		nD.setHeight(350);
		nD.show();
	}

	/**
	 * Checks if the date is valid or not
	 * 
	 * @param dateArray array in date format to check
	 * @throws IllegalArgumentException thrown if the date format is not valid
	 */
	private void checkDate(String[] dateArray) throws IllegalArgumentException {
		if (dateArray.length != 3) {
			throw new IllegalArgumentException(Arrays.toString(dateArray));
		} else {
			// Check string formatting
			if (dateArray[0].length() != 4 && dateArray[1].length() != 2 && dateArray[2].length() != 2) {
				throw new IllegalArgumentException(Arrays.toString(dateArray));
			}
			// Check value qualifications
			if (Integer.parseInt(dateArray[1]) > 12 || Integer.parseInt(dateArray[2]) > 31) {
				throw new IllegalArgumentException(Arrays.toString(dateArray));
			}
		}
	}

	/**
	 * Method to create a new window for initializing data with file(CSV) increase
	 * or decrease)
	 * 
	 * @param primaryStage parent stage to set modality
	 */
	public void uploadFileWindow(Stage primaryStage) {
		Stage uF = new Stage();
		uF.setTitle("Upload Data");
		uF.initModality(Modality.APPLICATION_MODAL);
		uF.initOwner(primaryStage);

		BorderPane root = new BorderPane();

		HBox upload = new HBox(); // HBox for file input
		TextField fileInput = new TextField();
		fileInput.setPromptText("File path (.csv)");
		fileInput.setFocusTraversable(false);
		upload.getChildren().addAll(new Label("Enter Path of File: "), fileInput);

		HBox buttons = new HBox();
		Button bt = new Button("Upload"); // Upload Button
		Button afterUpload = new Button("Data Interaction ->"); // Data interaction after file upload button
		afterUpload.setOnAction(e -> uF.close());
		buttons.getChildren().addAll(bt, afterUpload);
		afterUpload.setVisible(false);

		bt.setOnAction(new EventHandler<ActionEvent>() {
			Alert alert = new Alert(AlertType.ERROR, "", ButtonType.OK, ButtonType.CANCEL); // Alert window
			Optional<ButtonType> response = null; // Response of alert

			/**
			 * Handle the done button event
			 * 
			 * @param arg0 current action event
			 */
			@Override
			public void handle(ActionEvent arg0) {
				try {
					List<String[]> data = fileRead(fileInput.getText());
					data.remove(0);
					for (String[] d : data) {
						// CHECK IF DATE IS VALID
						checkDate(d[0].split("-"));
						try {
							// ADD FARM DATA
							farmTable.insert(d[1], new Farm(d[1], Integer.parseInt(d[2]), d[0]));
						} catch (NumberFormatException n) {
							throw new NumberFormatException(d[2]);
						}
					}
					Alert success = new Alert(AlertType.CONFIRMATION, "Data has been successfully uploaded",
							ButtonType.OK);
					success.show();
					afterUpload.setVisible(true);
				} catch (IOException e) {
					alert.setContentText("Did not input valid file path. Press OK to try again.");
					response = alert.showAndWait();
				} catch (IndexOutOfBoundsException i) {
					alert.setContentText("Invalid Format. Lines: " + i.getMessage() + ". Press OK to try again.");
					response = alert.showAndWait();
				} catch (NumberFormatException n) {
					alert.setContentText("Invalid Weight: " + n.getMessage() + ". Press OK to try again.");
					response = alert.showAndWait();
				} catch (IllegalArgumentException il) {
					alert.setContentText("Invalid Date: " + il.getMessage() + ". Press OK to try again.");
					response = alert.showAndWait();
				} finally {
					if (response != null) {
						farmTable.clear();
						if (!response.isPresent()) {

						} else if (response.get() == ButtonType.OK) {
							alert.close();
							uF.close();
							uploadFileWindow(primaryStage);
						} else {
							alert.close();
							uF.close();
						}
					}
				}
			}
		});

		VBox vb = new VBox(); // VBox to put all 4 elements together
		vb.getChildren().addAll(upload, buttons);
		vb.setSpacing(25);

		root.setCenter(vb);

		Scene scene = new Scene(root);
		uF.setScene(scene);
		uF.setWidth(500);
		uF.setHeight(175);
		uF.show();
	}

	/**
	 * Read a file filter to valid lines
	 * 
	 * @param f file to read
	 * @return list of data
	 * @throws IOException              if file is not found
	 * @throws IllegalArgumentException thrown when one or more lines have incorrect
	 *                                  format
	 */
	private static List<String[]> fileRead(String f) throws IOException, IllegalArgumentException {
		try (Stream<String> stream = Files.lines(Paths.get(f))) {
			List<String[]> data = stream.filter(u -> u.split(",").length == 3).map(m -> m.split(","))
					.collect(Collectors.toList());
			List<String> error = getFileErrors(f);
			if (error.size() > 0) {
				throw new IndexOutOfBoundsException(error.toString());
			}
			return data;
		} catch (IOException e) {
			throw new IOException();
		}
	}

	/**
	 * Gets all the obvious format error lines in file
	 * 
	 * @param f file to check for errors
	 * @return a list of lines with errors
	 * @throws IOException thrown if the file is not found
	 */
	private static List<String> getFileErrors(String f) throws IOException {
		Stream<String> errorStream = Files.lines(Paths.get(f));
		List<String> data = errorStream.filter(u -> u.split(",").length != 3).collect(Collectors.toList());
		errorStream.close();
		return data;
	}

	/**
	 * Method that will run upon program execution
	 * 
	 * @param args System arguments to pass down before running
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
