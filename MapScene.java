package Dijkstra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import javafx.animation.PathTransition;
import javafx.animation.PathTransition.OrientationType;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MapScene extends Scene {

	static int numberOfVertices;
	static int numberOfEdges;

	Graph graph;

	File file; // File object for the file to be compressed

	BorderPane bp = new BorderPane(); // Main layout pane for the scene

	double maxWidth = 1150;
	double maxHeight = 770;
	Stage stage; // Stage on which the scene is set
	Scene scene; // Previous scene to return to
	EncancedComboBox sourceBoxCustom = new EncancedComboBox("Enter Source");
	EncancedComboBox desBoxCustom = new EncancedComboBox("Enter Destination");
    EncancedComboBox filterBoxCustom = new EncancedComboBox("Select Filter");


	public MapScene(Stage stage, Scene scene, File file) {
		super(new BorderPane(), 1200, 650);
		this.stage = stage;
		this.scene = scene;

		this.bp = ((BorderPane) this.getRoot());

		this.file = file;
		


		readFile();
		addFX();
		
	}

//	public void readFile() {
//		try (FileInputStream fileInputStream = new FileInputStream(file);
//				InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
//				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
//
//			String line;
//			String[] firstLine = bufferedReader.readLine().split(",");
//
//			numberOfVertices = Integer.parseInt(firstLine[0].trim());
//			numberOfEdges = Integer.parseInt(firstLine[1].trim());
//
//			graph = new Graph(numberOfVertices);
//
//			for (int i = 0; i < numberOfVertices; i++) {
//				line = bufferedReader.readLine();
//
//				String[] tkz = line.split(",");
//				String CapitalName = tkz[0].trim();
//				double latitude = Double.parseDouble(tkz[1].trim());
//				double longitude = Double.parseDouble(tkz[2].trim());
//
//				Capital Capital = new Capital(CapitalName, longitude, latitude);
//
//				Capital.setX((((longitude + 180.0) / 360.0) * maxWidth));
//				Capital.setY((((90.0 - latitude) / 180.0) * maxHeight));
//
//				Vertix vertix = new Vertix(Capital);
//				graph.addVertix(vertix);
//			}
//
//			while ((line = bufferedReader.readLine()) != null) {
//				String[] tkz = line.split(",");
//				Vertix source = graph.getVertix(tkz[0].trim());
//				Vertix destination = graph.getVertix(tkz[1].trim());
//
//				Edge edge = new Edge(source, destination);
//				source.getVertices().addLast(edge);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	public void readFile() {
	    try (FileInputStream fileInputStream = new FileInputStream(file);
	         InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
	         BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

	        String line;
	        String[] header = bufferedReader.readLine().split(",");
	        numberOfVertices = Integer.parseInt(header[0].trim());
	        numberOfEdges = Integer.parseInt(header[1].trim());
	        System.out.println("Header Line: [" + header[0] + ", " + header[1] + "]");
	        System.out.println("Expected Vertices: " + numberOfVertices + ", Expected Edges: " + numberOfEdges);

	        graph = new Graph(numberOfVertices);

	        int vertexCount = 0;
	        while ((line = bufferedReader.readLine()) != null && line.contains(",")) {
	            String[] parts = line.split(",");
	            if (parts.length == 3) { // Vertex definition
	                try {
	                    String name = parts[0].trim();
	                    double latitude = Double.parseDouble(parts[1].trim());
	                    double longitude = Double.parseDouble(parts[2].trim());
	                    Capital Capital = new Capital(name, longitude, latitude);

	                    Capital.setX((((longitude + 180.0) / 360.0) * maxWidth));
	                    Capital.setY((((90.0 - latitude) / 180.0) * maxHeight));

	                    Vertix vertix = new Vertix(Capital);
	                    graph.addVertix(vertix);
	                    vertexCount++;
	                    System.out.println("Added Vertex: " + name + " [" + latitude + ", " + longitude + "]");
	                } catch (NumberFormatException e) {
	                    System.err.println("Skipping invalid vertex: " + line);
	                }
	            } else {
	                break; // Stop processing vertices and move to edges
	            }
	        }

	        int edgeCount = 0;
	        do {
	            if (line == null || !line.contains(",")) continue;

	            String[] parts = line.split(",");
	            if (parts.length == 4) { // Edge definition with cost and time
	                String sourceName = parts[0].trim();
	                String destinationName = parts[1].trim();
	                double cost;
	                double time;

	                try {
	                    cost = Double.parseDouble(parts[2].trim());
	                    time = Double.parseDouble(parts[3].trim());
	                } catch (NumberFormatException e) {
	                    System.err.println("Skipping invalid edge due to invalid cost or time: " + line);
	                    continue;
	                }

	                // Prevent self-loops
	                if (sourceName.equals(destinationName)) {
	                    System.out.println("Skipping self-loop: " + sourceName + " -> " + destinationName);
	                    continue;
	                }

	                Vertix source = graph.getVertix(sourceName);
	                Vertix destination = graph.getVertix(destinationName);

	                if (source != null && destination != null) {
	                    Edge edge = new Edge(source, destination, cost, time); // Updated Edge constructor
	                    source.addEdge(edge); // Add edge to the source's adjacency list

	                    edgeCount++;
	                    System.out.println("Added Edge: " + sourceName + " -> " + destinationName + " | Cost: " + cost + " | Time: " + time);
	                } else {
	                    System.err.println("Skipping invalid edge: " + line);
	                }
	            } else {
	                System.err.println("Skipping invalid line: " + line);
	            }
	        } while ((line = bufferedReader.readLine()) != null);

	        System.out.println("Vertices Added: " + vertexCount + "/" + numberOfVertices);
	        System.out.println("Edges Added: " + edgeCount + "/" + numberOfEdges);

	        if (vertexCount != numberOfVertices) {
	            System.err.println("Vertex count mismatch: Expected " + numberOfVertices + ", but found " + vertexCount);
	        }
	        if (edgeCount != numberOfEdges) {
	            System.err.println("Edge count mismatch: Expected " + numberOfEdges + ", but found " + edgeCount);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}




	
	private ScrollPane getMap() {
	    // Load the map image
	    Image mapImage = new Image(getClass().getResource("Images/World-Physical-Map-Simplified.jpg").toExternalForm());
	    ImageView map = new ImageView(mapImage);

	    map.setFitWidth(maxWidth); // Adjust the size as needed
	    map.setFitHeight(maxHeight);

	    map.getStyleClass().add("map-image");

	    Pane pane = new Pane(map);

	    // Get all vertices (capitals) from the graph
	    Vertix[] nodes = graph.getHashTable().getTable();

	    for (int i = 0; i < nodes.length; i++) {
	        Vertix vertix = nodes[i];
	        if (vertix == null) continue; // Skip empty nodes

	        String capitalName = vertix.getCapital().getCapitalName();
	        ImageView flagImage = getIcon(capitalName);

	        // Skip capitals with no valid icon
	        if (flagImage == null) {
	            continue;
	        }

	        // Add to combo boxes for source and destination
	        sourceBoxCustom.addCapital(capitalName);
	        desBoxCustom.addCapital(capitalName);

	        // Position the marker on the map
	        flagImage.setLayoutX(vertix.getCapital().getX() - 512 / 2 / 20);
	        flagImage.setLayoutY(vertix.getCapital().getY() - 512 / 20 + 10);

	        // Add a label for the capital name
	     // Add a label for the capital name
	        Label capitalLabel = new Label(capitalName);
	        capitalLabel.setStyle("-fx-font-size: 14px;-fx-text-fill: black;-fx-background-color: white;-fx-padding: 3;-fx-border-color: black;-fx-border-radius: 5; -fx-background-radius: 5;");  
	        capitalLabel.setVisible(false); // Initially hidden
	        capitalLabel.layoutXProperty().bind(flagImage.layoutXProperty()); // Bind label position dynamically
	        capitalLabel.layoutYProperty().bind(flagImage.layoutYProperty().add(25)); // Position below the flag dynamically


	        // Show label on hover
	        flagImage.setOnMouseEntered(e -> capitalLabel.setVisible(true));
	        flagImage.setOnMouseExited(e -> capitalLabel.setVisible(false));

	        // Add flag and label to the pane
	        pane.getChildren().addAll(flagImage, capitalLabel);
	    }

	    // Make the map scrollable
	    ScrollPane scrollPane = new ScrollPane(pane);
	    scrollPane.setPannable(true); // Allow panning
	    scrollPane.setPrefViewportWidth(maxWidth - 15);
	    scrollPane.setPrefViewportHeight(maxHeight - 9);

	    return scrollPane;
	}


	private void addFX() {
	    VBox tableBox = new VBox(10, getMap());
	    tableBox.setAlignment(Pos.CENTER);
	    bp.setPadding(new Insets(15));

	    BorderPane.setMargin(tableBox, new Insets(0, 0, 0, 20));
	    bp.setLeft(tableBox);

	    Label sourceLabel = new Label("Source: ");
	    sourceLabel.setStyle("-fx-text-fill: #141E46; " + "-fx-background-color: white; " + "-fx-padding: 3; "
	            + "-fx-border-color: #41B06E; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; "
	            + "-fx-font-size: 16px;");
	    sourceLabel.setPrefWidth(80);

	    Label targetLabel = new Label("Target: ");
	    targetLabel.setStyle("-fx-text-fill: #141E46; " + "-fx-background-color: white; " + "-fx-padding: 3; "
	            + "-fx-border-color: #41B06E; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; "
	            + "-fx-font-size: 16px;");
	    targetLabel.setPrefWidth(80);

	    Label filterLabel = new Label("Filter: ");
	    filterLabel.setStyle("-fx-text-fill: #141E46; " + "-fx-background-color: white; " + "-fx-padding: 3; "
	            + "-fx-border-color: #41B06E; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; "
	            + "-fx-font-size: 16px;");

	    filterBoxCustom.addCapital("Cost");
	    filterBoxCustom.addCapital("Time");
	    filterBoxCustom.addCapital("Distance");

	    // Adjust ComboBox sizes
	    sourceBoxCustom.getComboBox().setPrefWidth(150); // Compact size
	    desBoxCustom.getComboBox().setPrefWidth(150);
	    filterBoxCustom.getComboBox().setPrefWidth(150);

	    Button goButton = new Button("GO");
	    goButton.setPrefWidth(250); // Consistent width for button

	    Label pathLabel = new Label("Path: ");
	    pathLabel.setStyle("-fx-text-fill: #141E46; " + "-fx-background-color: white; " + "-fx-padding: 3; "
	            + "-fx-border-color: #41B06E; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; "
	            + "-fx-font-size: 16px;");

	    // Adjust TextArea size
	    TextArea pathArea = new TextArea();
	    pathArea.setMinWidth(200);
	    pathArea.setMaxWidth(300);
	    pathArea.setPrefHeight(250);
	    pathArea.setEditable(false); // Make the TextField non-editable



	    Label disLabel = new Label("Total: ");
	    disLabel.setStyle("-fx-text-fill: #141E46; " + "-fx-background-color: white; " + "-fx-padding: 3; "
	            + "-fx-border-color: #41B06E; " + "-fx-border-radius: 5; " + "-fx-background-radius: 5; "
	            + "-fx-font-size: 16px;");

	    TextField disField = new TextField();
	    disField.setPrefWidth(250);
	    disField.setStyle("-fx-background-color: #f1f1f1; -fx-border-color: black; -fx-padding: 5;");
	    disField.setEditable(false); // Make the TextField non-editable


	    Button back = new Button("Back");
	    back.setOnAction(e -> stage.setScene(scene));
	    back.setPrefWidth(250); // Consistent width for button

	    GridPane gp = new GridPane();

	    gp.add(new HBox(10, sourceLabel, sourceBoxCustom.getComboBox()), 0, 0);
	    gp.add(new HBox(10, targetLabel, desBoxCustom.getComboBox()), 0, 1);
	    gp.add(new HBox(10, filterLabel, filterBoxCustom.getComboBox()), 0, 2);
	    gp.add(goButton, 0, 3);
	    GridPane.setMargin(goButton, new Insets(0, 0, 25, 0));
	    gp.add(pathLabel, 0, 4);
	    gp.add(pathArea, 0, 5);
	    gp.add(disLabel, 0, 6);
	    gp.add(disField, 0, 7);
	    gp.add(back, 0, 8);

	    gp.setVgap(15);
	    gp.setAlignment(Pos.CENTER);

	    bp.setRight(gp);

	    goButton.setOnAction(e -> {
	        pathArea.clear();
	        disField.clear();

	        String source = sourceBoxCustom.getComboBox().getSelectionModel().getSelectedItem();
	        String destination = desBoxCustom.getComboBox().getSelectionModel().getSelectedItem();
	        String filter = filterBoxCustom.getComboBox().getSelectionModel().getSelectedItem();

	        if (source == null || destination == null) {
	            Alert alert = new Alert(Alert.AlertType.WARNING);
	            alert.setTitle("Warning");
	            alert.setHeaderText("Source or Destination Not Selected");
	            alert.setContentText("Please select both source and destination before proceeding.");
	            alert.showAndWait();
	            return;
	        }

	        if (filter == null || (!filter.equalsIgnoreCase("distance") && !filter.equalsIgnoreCase("cost") && !filter.equalsIgnoreCase("time"))) {
	            Alert alert = new Alert(Alert.AlertType.WARNING);
	            alert.setTitle("Warning");
	            alert.setHeaderText("Filter Not Selected");
	            alert.setContentText("Please select a valid filter (Distance, Cost, or Time) before proceeding.");
	            alert.showAndWait();
	            return;
	        }

	        if (source.equals(destination)) {
	            pathArea.setText("Source and destination are the same.");
	            disField.setText("0.0");
	            return;
	        }

	        Pane mapPane = (Pane) ((ScrollPane) tableBox.getChildren().get(0)).getContent();
	        clearMapElements(mapPane);

	        HeapNode resultNode = graph.getOptimalPath(source, destination, filter.toLowerCase());

	        if (resultNode != null) {
	            StringBuilder pathDetails = new StringBuilder();
	            double totalMetric = 0;
	            String unit = "";

	            switch (filter.toLowerCase()) {
	                case "cost" -> unit = "$";
	                case "time" -> unit = "Minute";
	                case "distance" -> unit = "KM";
	            }

	            LinkedListNode currentNode = resultNode.getPath().getFirstNode();
	            while (currentNode != null) {
	                Edge edge = currentNode.getEdge();

	             // Skip self-loops in path details
	                if (edge.getSource().equals(edge.getDestination())) {
	                    currentNode = currentNode.getNext();
	                    continue;
	                }
	                
	                double metric = switch (filter.toLowerCase()) {
	                    case "cost" -> edge.getCost();
	                    case "time" -> edge.getTime();
	                    case "distance" -> edge.getDistance();
	                    default -> 0;
	                };

	                totalMetric += metric;

	                pathDetails.append("From ").append(edge.getSource().getCapital().getCapitalName())
	                        .append(" to ").append(edge.getDestination().getCapital().getCapitalName())
	                        .append(" | ").append(filter.substring(0, 1).toUpperCase()).append(filter.substring(1))
	                        .append(": ").append(String.format("%.2f", metric)).append(" ").append(unit).append("\n");

	                currentNode = currentNode.getNext();
	            }

	            pathArea.setText(pathDetails.toString());
	            disField.setText(String.format("Total %s: %.2f %s", filter, totalMetric, unit));

	            drawPath(mapPane, resultNode.getPath());
	        } else {
	            pathArea.setText("No valid path found.");
	            disField.setText("N/A");
	        }
	    });
	}



	private void drawPath(Pane mapPane, LinkedList path) {
		Path combinedPath = new Path();
		Edge previousEdge = null;
		LinkedListNode currentNode = path.getFirstNode();

		while (currentNode != null) {
			Edge edge = currentNode.getEdge();
			Capital sourceCapital = edge.getSource().getCapital();
			Capital destinationCapital = edge.getDestination().getCapital();

			if (previousEdge != null) {
				combinedPath.getElements().add(new LineTo(destinationCapital.getX(), destinationCapital.getY()));
			} else {
				combinedPath.getElements().add(new MoveTo(sourceCapital.getX(), sourceCapital.getY()));
				combinedPath.getElements().add(new LineTo(destinationCapital.getX(), destinationCapital.getY()));
			}
			previousEdge = edge;
			currentNode = currentNode.getNext();
		}

		previousEdge = null;
		currentNode = path.getFirstNode();
		while (currentNode != null) {
			Edge edge = currentNode.getEdge();
			Capital sourceCapital = edge.getSource().getCapital();
			Capital destinationCapital = edge.getDestination().getCapital();

			if (previousEdge != null) {
				Line line = new Line(sourceCapital.getX(), sourceCapital.getY(), destinationCapital.getX(),
						destinationCapital.getY());
				line.setStroke(Color.RED);
				line.setStrokeWidth(2);
				line.getStrokeDashArray().addAll(10d, 10d);
				line.setStrokeLineCap(StrokeLineCap.ROUND);
				mapPane.getChildren().add(line);
			}
			previousEdge = edge;
			currentNode = currentNode.getNext();
		}

		Image planeImage = new Image(getClass().getResource("Images/plane.png").toExternalForm());
		ImageView planeImageView = new ImageView(planeImage);

		planeImageView.setFitWidth(30);
		planeImageView.setFitHeight(30);

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.seconds(10));
		pathTransition.setPath(combinedPath);
		pathTransition.setNode(planeImageView);
		pathTransition.setOrientation(OrientationType.ORTHOGONAL_TO_TANGENT);
		pathTransition.setCycleCount(PathTransition.INDEFINITE);
		pathTransition.setAutoReverse(false);

		pathTransition.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
			double angle = calculateAngle(combinedPath,
					pathTransition.getCurrentTime().toMillis() / pathTransition.getCycleDuration().toMillis());
			planeImageView.setRotate(angle);
		});

		pathTransition.setOnFinished(event -> {
			pathTransition.jumpTo(Duration.ZERO);
			pathTransition.play();
		});

		pathTransition.play();

		mapPane.getChildren().add(planeImageView);
	}

	private double calculateAngle(Path path, double t) {
		t = Math.max(0, Math.min(1, t));

		ObservableList<PathElement> elements = path.getElements();
		int segmentIndex = (int) (t * (elements.size() - 1));

		if (segmentIndex < 0 || segmentIndex >= elements.size() - 1) {
			return 0; 
		}

		PathElement startElement = elements.get(segmentIndex);
		PathElement endElement = elements.get(segmentIndex + 1);

		double startX, startY, endX, endY;

		if (startElement instanceof MoveTo) {
			MoveTo moveTo = (MoveTo) startElement;
			startX = moveTo.getX();
			startY = moveTo.getY();
		} else if (startElement instanceof LineTo) {
			LineTo lineTo = (LineTo) startElement;
			startX = lineTo.getX();
			startY = lineTo.getY();
		} else {
			return 0;
		}

		if (endElement instanceof LineTo) {
			LineTo lineTo = (LineTo) endElement;
			endX = lineTo.getX();
			endY = lineTo.getY();
		} else {
			return 0;
		}

		double angle = Math.toDegrees(Math.atan2(endY - startY, endX - startX));
		return angle;
	}

	private ImageView firstSelectedFlag = null;
	private ImageView secondSelectedFlag = null;
	private String firstCapital = null;
	private String secondCapital = null;

	private ImageView getIcon(String image) {
		Glow glow = new Glow();
		glow.setLevel(0.5);

		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setBrightness(0.2);
		colorAdjust.setContrast(0.0);
		colorAdjust.setSaturation(-0.1);
		colorAdjust.setHue(0.166);

		glow.setInput(colorAdjust);
		if (image.equalsIgnoreCase("xx") || image.equalsIgnoreCase("jaber")) {
	        return null; // Return null to exclude these countries
	    }
		String imagePath = "Images/" + image.toLowerCase() + ".png";

		Image flagImage = null;

		try {
		    flagImage = new Image(getClass().getResource(imagePath).toExternalForm());
		} catch (Exception e) {
			System.err.println("Image not found for: " + imagePath + ". Using default unknown image.");
		    flagImage = new Image(getClass().getResource("Images/unknown.png").toExternalForm());
		}
		
		ImageView flagImageView = new ImageView(flagImage);

		final boolean[] isGreen = { false };

		flagImageView.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
			if (!isGreen[0])
				flagImageView.setEffect(glow);
		});
		flagImageView.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
			if (!isGreen[0])
				flagImageView.setEffect(null);
		});

		flagImageView.setFitWidth(flagImage.getWidth() / 20);
		flagImageView.setFitHeight(flagImage.getHeight() / 20);

		ColorAdjust greenAdjust = new ColorAdjust();
		greenAdjust.setHue(0.5); // Adjust the hue to make the image green

		flagImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			if (isGreen[0]) {
				flagImageView.setEffect(null); // Set back to normal

				// Remove from source or destination if already set
				if (sourceBoxCustom.getComboBox().getSelectionModel().getSelectedItem() != null
						&& sourceBoxCustom.getComboBox().getSelectionModel().getSelectedItem().equals(image)) {
					sourceBoxCustom.getComboBox().getSelectionModel().clearSelection();
					sourceBoxCustom.getComboBox().setPromptText("Enter Source");
					firstSelectedFlag = null;
					firstCapital = null;
				} else if (desBoxCustom.getComboBox().getSelectionModel().getSelectedItem() != null
						&& desBoxCustom.getComboBox().getSelectionModel().getSelectedItem().equals(image)) {
					desBoxCustom.getComboBox().getSelectionModel().clearSelection();
					desBoxCustom.getComboBox().setPromptText("Enter Destination");
					secondSelectedFlag = null;
					secondCapital = null;
				}

			} else {
				if (firstSelectedFlag == null) {
					flagImageView.setEffect(greenAdjust); // Set to green
					firstSelectedFlag = flagImageView;
					firstCapital = image;
					sourceBoxCustom.getComboBox().setValue(image);
				} else if (secondSelectedFlag == null) {
					flagImageView.setEffect(greenAdjust); // Set to green
					secondSelectedFlag = flagImageView;
					secondCapital = image;
					desBoxCustom.getComboBox().setValue(image);
				} else {
					// Replace the first selected flag
					firstSelectedFlag.setEffect(null); // Set back to normal
					sourceBoxCustom.getComboBox().getSelectionModel().clearSelection();
					sourceBoxCustom.getComboBox().setPromptText("Enter Source");

					firstSelectedFlag = secondSelectedFlag;
					firstCapital = secondCapital;
					sourceBoxCustom.getComboBox().setValue(firstCapital);

					flagImageView.setEffect(greenAdjust); // Set new one to green
					secondSelectedFlag = flagImageView;
					secondCapital = image;
					desBoxCustom.getComboBox().setValue(image);
				}
			}
			isGreen[0] = !isGreen[0]; // Toggle the state
		});

		return flagImageView;
	}
	
	private void clearMapElements(Pane mapPane) {
	    mapPane.getChildren().removeIf(node -> (node instanceof Line || node instanceof Path)
	            && !(node instanceof ImageView && ((ImageView) node).getImage().getUrl().contains("locations")));

	    mapPane.getChildren().removeIf(
	            node -> node instanceof ImageView && ((ImageView) node).getImage().getUrl().contains("plane"));
	}

}
