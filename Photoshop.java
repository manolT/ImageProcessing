
//Manol Tonchev 966022
//All code submitted is my own, besides the one provided to us for this assignment;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import com.sun.glass.events.MouseEvent;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Photoshop extends Application {

	@Override
	public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("Photoshop");

		// Read the image
		Image image = new Image(new FileInputStream("raytrace.jpg"));

		// Create the graphical view of the image
		ImageView imageView = new ImageView(image);

		// Create the simple GUI
		Button invert_button = new Button("Invert");
		Button gamma_button = new Button("Gamma Correct");
		Button contrast_button = new Button("Contrast Stretching");
		Button histogram_button = new Button("Histograms");
		Button cc_button = new Button("Cross Correlation");

		// gamma interface
		Label gammaLabel = new Label("GammaValue: ");
		TextField gammaValue = new TextField();

		// label for contrast
		Label oneCont = new Label("x1: 20 / y1: 10 ");
		Label twoCont = new Label("x2: 200 / y2: 220");

		// chart for contrast
		NumberAxis inputAxis = new NumberAxis(0, 255, 50);
		NumberAxis outputAxis = new NumberAxis(0, 255, 50);
		LineChart<Number, Number> contrastChart = new LineChart<Number, Number>(
				inputAxis, outputAxis);
		contrastChart.setAnimated(false);
		contrastChart.setMaxHeight(300);
		contrastChart.setMaxWidth(300);
		contrastChart.setTitle("Contrast Stretching");

		// starting series for the chart
		XYChart.Series series = new XYChart.Series();
		series.getData().add(new XYChart.Data(0, 0));
		series.getData().add(new XYChart.Data(20, 10));
		series.getData().add(new XYChart.Data(200, 220));
		series.getData().add(new XYChart.Data(255, 255));

		contrastChart.getData().add(series);

		// making interactive chart
		Data data1 = (Data) series.getData().get(1);
		Data data2 = (Data) series.getData().get(2);

		Node node1 = data1.getNode();
		node1.setCursor(Cursor.CLOSED_HAND);

		node1.setOnMouseDragged(e -> {
			// creates a new point based on coordinates of the data

			Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());

			double xLoc = inputAxis.sceneToLocal(pointInScene).getX();
			double yLoc = outputAxis.sceneToLocal(pointInScene).getY();

			Number x = inputAxis.getValueForDisplay(xLoc);
			Number y = outputAxis.getValueForDisplay(yLoc);

			data1.setXValue(x);
			data1.setYValue(y);

			oneCont.setText("x1: " + Math.round((double) x) + " / y1: "
					+ Math.round((double) y));

		});

		Node node2 = data2.getNode();
		node2.setCursor(Cursor.CLOSED_HAND);

		node2.setOnMouseDragged(e -> {
			// creates a new point based on coordinates of the data

			Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());

			double xLoc = inputAxis.sceneToLocal(pointInScene).getX();
			double yLoc = outputAxis.sceneToLocal(pointInScene).getY();

			Number x = inputAxis.getValueForDisplay(xLoc);
			Number y = outputAxis.getValueForDisplay(yLoc);

			data2.setXValue(x);
			data2.setYValue(y);

			twoCont.setText("x2: " + Math.round((double) x) + " / y2: "
					+ Math.round((double) y));

		});
		// buttons for equalisation and reseting the image
		Button greyButton = new Button("make grey");

		greyButton.setOnAction(e -> {
			imageView.setImage(greyScale(imageView.getImage()));
		});

		Button equalizeButton = new Button("equalize");

		equalizeButton.setOnAction(e -> {
			imageView.setImage(equalize(imageView.getImage()));
		});

		Button resetButton = new Button("reset Image");

		resetButton.setOnAction(e -> {
			imageView.setImage(image);
		});

		// Add all the event handlers (this is a minimal GUI - you may try to do
		// better)
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Invert");
				// At this point, "image" will be the original image
				// imageView is the graphical representation of an image
				// imageView.getImage() is the currently displayed image

				// Let's invert the currently displayed image by calling the
				// invert function later in the code
				Image inverted_image = ImageInverter(imageView.getImage());
				// Update the GUI so the new image is displayed
				imageView.setImage(inverted_image);
			}
		});

		gamma_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Gamma Correction");
				double gamma = 0;
				if (gammaValue.getText().isEmpty()) {
					gamma = 1.0;
				} else {
					gamma = Double.parseDouble(gammaValue.getText());
				}
				Image correctedImage = ImageGammaCorrector(imageView.getImage(),
						gamma);
				imageView.setImage(correctedImage);
			}
		});

		contrast_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Contrast Stretching");

				double x1 = Double.valueOf(data1.getXValue().toString());
				double y1 = Double.valueOf(data1.getYValue().toString());
				double x2 = Double.valueOf(data2.getXValue().toString());
				double y2 = Double.valueOf(data2.getYValue().toString());

				Image correctedImage = constrastStretch(imageView.getImage(),
						x1, y1, x2, y2);
				imageView.setImage(correctedImage);
			}
		});
		
		
		
		histogram_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Histogram");
				Stage popup = new Stage();
				Button update = new Button("update");

				// creates histogram charts, contained in a flow pane
				FlowPane histograms = new FlowPane();

				NumberAxis pixelsR = new NumberAxis();
				NumberAxis brightR = new NumberAxis(0, 255, 50);
				pixelsR.setTickUnit(5000);
				AreaChart<Number, Number> redHist = new AreaChart<>(brightR,
						pixelsR);
				redHist.setTitle("Red Histogram");
				pixelsR.setLabel("pixels");
				brightR.setLabel("brightness");

				NumberAxis pixelsG = new NumberAxis();
				NumberAxis brightG = new NumberAxis(0, 255, 50);
				pixelsR.setTickUnit(5000);
				AreaChart<Number, Number> greenHist = new AreaChart<>(brightG,
						pixelsG);
				greenHist.setTitle("Green Histogram");
				pixelsG.setLabel("pixels");
				brightG.setLabel("brightness");

				NumberAxis pixelsB = new NumberAxis();
				NumberAxis brightB = new NumberAxis(0, 255, 50);
				pixelsR.setTickUnit(5000);
				AreaChart<Number, Number> blueHist = new AreaChart<>(brightB,
						pixelsB);
				blueHist.setTitle("Blue Histogram");
				pixelsB.setLabel("pixels");
				brightB.setLabel("brightness");

				NumberAxis pixelsA = new NumberAxis();
				NumberAxis brightA = new NumberAxis(0, 255, 50);
				pixelsA.setTickUnit(5000);
				AreaChart<Number, Number> averageHist = new AreaChart<>(brightA,
						pixelsA);
				averageHist.setTitle("Average Histogram");
				pixelsA.setLabel("pixels");
				brightA.setLabel("brightness");

				histograms.getChildren().addAll(redHist, greenHist, blueHist,
						averageHist, update);

				update.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						setHistograms(imageView.getImage(), redHist, greenHist,
								blueHist, averageHist);
						System.out.println("new hists");
					}
				});

				setHistograms(imageView.getImage(), redHist, greenHist,
						blueHist, averageHist);

				popup.setScene(new Scene(histograms, 1200, 800));
				popup.show();

			}
		});

		cc_button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				int[][] f = { { -4, -1, 0, -1, -4 }, { -1, 2, 3, 2, -1 },
						{ 0, 3, 4, 3, 0 }, { -1, 2, 3, 2, -1 },
						{ -4, -1, 0, -1, -4 } };
				imageView.setImage(crossCorrelation(imageView.getImage(), f));
				System.out.println("Cross Correlation");
			}
		});

		Button otherFilter = new Button("gaussian");

		otherFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				int[][] f = { { 1, 4, 6, 4, 1 }, { 4, 16, 24, 16, 4 },
						{ 6, 24, 36, 24, 6 }, { 4, 16, 24, 16, 4 },
						{ 1, 4, 6, 4, 1 } };
				imageView.setImage(crossCorrelation(imageView.getImage(), f));
				System.out.println("Gaussian blur");
			}
		});

		

		// Using a flow pane
		FlowPane root = new FlowPane();
		// Gaps between buttons

		root.setVgap(10);
		root.setHgap(5);

		// Add all the buttons and the image for the GUI
		root.getChildren().addAll(invert_button, gamma_button, contrast_button,
				histogram_button, cc_button, imageView, gammaLabel, gammaValue,
				contrastChart, oneCont, twoCont, greyButton, equalizeButton,
				resetButton, otherFilter);

		// Display to user
		Scene scene = new Scene(root, 1024, 800);
		stage.setScene(scene);
		stage.show();
	}

	public void setHistograms(Image image, AreaChart redChart,
			AreaChart greenChart, AreaChart blueChart, AreaChart avgChart) {

		int[] redData = new int[256];
		int[] greenData = new int[256];
		int[] blueData = new int[256];
		int[] avgData = new int[256];

		// Find the width and height of the image to be process
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		PixelReader image_reader = image.getPixelReader();

		// red; we go trough every pixel and we increment the correponding brightness
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				redData[(int) (color.getRed() * 255.0)]++;
			}
		}

		XYChart.Series seriesRed = new XYChart.Series<>();

		for (int i = 0; i < 256; i++) {
			seriesRed.getData().add(new XYChart.Data(i, redData[i]));
		}

		redChart.getData().add(seriesRed);
		redChart.setCreateSymbols(false);

		// green
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				greenData[(int) (color.getGreen() * 255.0)]++;
			}
		}

		XYChart.Series seriesGreen = new XYChart.Series<>();

		for (int i = 0; i < 256; i++) {
			seriesGreen.getData().add(new XYChart.Data(i, greenData[i]));
		}

		greenChart.getData().add(seriesGreen);
		greenChart.setCreateSymbols(false);

		// blue
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				blueData[(int) (color.getBlue() * 255.0)]++;
			}
		}

		XYChart.Series seriesBlue = new XYChart.Series<>();

		for (int i = 0; i < 256; i++) {
			seriesBlue.getData().add(new XYChart.Data(i, blueData[i]));
		}

		blueChart.getData().add(seriesBlue);
		blueChart.setCreateSymbols(false);

		// average

		XYChart.Series seriesAvg = new XYChart.Series<>();

		for (int i = 0; i < 256; i++) {
			avgData[i] = (int) Math.round((((double) redData[i]) / 3.0)
					+ (((double) greenData[i]) / 3.0)
					+ (((double) blueData[i]) / 3.0));
			seriesAvg.getData().add(new XYChart.Data(i, avgData[i]));
		}

		avgChart.getData().add(seriesAvg);
		avgChart.setCreateSymbols(false);
	}

	//invert
	public Image ImageInverter(Image image) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage inverted_image = new WritableImage(width, height);
		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();

		// Iterate over all pixels
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				color = Color.color(1.0 - color.getRed(),
				1.0 - color.getGreen(), 1.0 - color.getBlue());
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}

	public Image equalize(Image oldImage) {
		// make image grey
		Image image = greyScale(oldImage);
		//histogram holds the normal histogram
		int[] histogram = new int[256];
		//t holds the comulative histogram
		int[] t = new int[256];
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage eqImage = new WritableImage(width, height);
		PixelWriter new_image_writer = eqImage.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();

		// get histogram for greyscale
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);

				histogram[(int) (color.getRed() * 255.0)]++;
			}
		}
		// set compound histogram
		int tVal = 0;
		for (int i = 0; i < 256; i++) {
			t[i] = tVal + histogram[i];
			tVal = t[i];
		}
		// create map by equation
		int size = width * height;
		int[] map = new int[256];
		for (int i = 0; i < 256; i++) {
			map[i] = (int) Math.round(255.0 * ((double) (t[i]) / size));

		}
		// finalising; calculating every pixel by map value
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);

				int newCol = map[(int) (color.getRed() * 255)];
				double col = newCol / 255.0;
				color = Color.color(col, col, col);

				new_image_writer.setColor(x, y, color);
			}
		}

		return eqImage;

	}

	public Image ImageGammaCorrector(Image image, double gamma) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage new_image = new WritableImage(width, height);
		PixelWriter new_image_writer = new_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();

		double[] lookUp = new double[256];

		// populate lookUp table
		for (int i = 0; i <= 255; i++) {
			lookUp[i] = Math.pow((double) i / 255, (double) 1 / gamma);
		}
		//match pixels with lookup table values
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				Color color = image_reader.getColor(x, y);

				// calculate index in array
				int red = (int) (color.getRed() * 255.0);
				int green = (int) (color.getGreen() * 255.0);
				int blue = (int) (color.getBlue() * 255.0);
				// set values by look up table
				color = Color.color(lookUp[red], lookUp[green], lookUp[blue]);
				new_image_writer.setColor(x, y, color);
			}
		}
		return new_image;
	}

	//contrast tretching by two points
	public Image constrastStretch(Image image, double x1, double y1, double x2,
			double y2) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage new_image = new WritableImage(width, height);
		PixelWriter correctedImageWriter = new_image.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		//oes over every pixel and caculated the new value
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);

				int red = (int) (color.getRed() * 255.0);
				int green = (int) (color.getGreen() * 255.0);
				int blue = (int) (color.getBlue() * 255.0);

				double new_red = calculateStretch(x1, y1, x2, y2, red) / 255;
				double new_green = calculateStretch(x1, y1, x2, y2, green)
						/ 255;
				double new_blue = calculateStretch(x1, y1, x2, y2, blue) / 255;

				color = Color.color(new_red, new_green, new_blue);
				correctedImageWriter.setColor(x, y, color);
			}
		}
		return new_image;
	}

	public Image greyScale(Image image) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		WritableImage greyImage = new WritableImage(width, height);
		PixelWriter correctedImageWriter = greyImage.getPixelWriter();
		PixelReader image_reader = image.getPixelReader();
		
		//goes over every pixel and avereges out the rgb values
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);

				double new_red = (color.getRed() + color.getBlue()
						+ color.getGreen()) / 3.0;
				double new_green = new_red;
				double new_blue = new_red;

				color = Color.color(new_red, new_green, new_blue);
				correctedImageWriter.setColor(x, y, color);
			}
		}
		return greyImage;
	}

	// applies the contrast stretching formula
	private double calculateStretch(double x1, double y1, double x2, double y2,
			double color) {
		if (color < x1) {
			return (int) ((y1 / x1) * color);

		} else if (x1 <= color & color <= x2) {
			return (int) ((((y2 - y1) / (x2 - x1)) * (color - x1)) + y1);
		} else {
			return (int) ((((255 - y2) / (255 - x2)) * (color - x2)) + y2);
		}

	}

	public Image crossCorrelation(Image image, int[][] filter) {
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();

		WritableImage filter_image = new WritableImage(width, height);

		PixelWriter image_writer = filter_image.getPixelWriter();

		PixelReader image_reader = image.getPixelReader();

		int[][] rawRed = new int[height][width];
		int[][] rawGreen = new int[height][width];
		int[][] rawBlue = new int[height][width];

		// we loop trough every inner pixel
		for (int y = 2; y < height - 2; y++) {
			for (int x = 2; x < width - 2; x++) {

				int xNew = x - 2;
				int yNew = y - 2;
				// get red value for every surrounding pixel

				int[][] r = new int[5][5];
				int[][] g = new int[5][5];
				int[][] b = new int[5][5];
				// assign surrounding pixels values in each 2d array
				// every column/ x = i
				for (int i = 0; i < 5; i++) {
					// every row/ y = j
					for (int j = 0; j < 5; j++) {
						Color color = image_reader.getColor(xNew + j, yNew + i);

						r[i][j] = (int) Math.round(color.getRed() * 255);
						g[i][j] = (int) Math.round(color.getGreen() * 255);
						b[i][j] = (int) Math.round(color.getBlue() * 255);

					}
				}
				// calculate filter values
				rawRed[y][x] = matrix(r, filter);
				rawGreen[y][x] = matrix(g, filter);
				rawBlue[y][x] = matrix(b, filter);

			}
		}

		// find out min and max
		int min = rawRed[0][0];
		int max = rawRed[0][0];

		for (int[] row : rawRed) {
			for (int i : row) {
				if (i > max) {
					max = i;
				}
				if (i < min) {
					min = i;
				}
			}
		}

		for (int[] row : rawGreen) {
			for (int i : row) {
				if (i > max) {
					max = i;
				}
				if (i < min) {
					min = i;
				}
			}
		}
		for (int[] row : rawBlue) {
			for (int i : row) {
				if (i > max) {
					max = i;
				}
				if (i < min) {
					min = i;
				}
			}
		}

		double minD = (double) (min);
		double maxD = (double) (max);

		// normalise
		for (int y = 2; y < height - 2; y++) {
			for (int x = 2; x < width - 2; x++) {
				double red = (((double) rawRed[y][x]) - minD) / (maxD - minD);
				double green = ((double) rawGreen[y][x] - minD) / (maxD - minD);
				double blue = ((double) rawBlue[y][x] - minD) / (maxD - minD);

				Color color = Color.color(red, green, blue);
				image_writer.setColor(x, y, color);

			}
		}

		// MAKING BORDERS BLACK
		// make top 2 rows black
		for (int y = 0; y < 2; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				
				color = Color.color(0, 0, 0);

				image_writer.setColor(x, y, color);
			}
		}
		// make left 2 pixels black
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < 2; x++) {
				// For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				
				color = Color.color(0, 0, 0);

				image_writer.setColor(x, y, color);
			}
		}
		// make right 2 pixels black
		for (int y = 0; y < height; y++) {
			for (int x = width - 2; x < width; x++) {
				// For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				
				color = Color.color(0, 0, 0);

				image_writer.setColor(x, y, color);
			}
		}
		// make bottom 2 pixels black
		for (int y = height - 2; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				
				color = Color.color(0, 0, 0);
				

				// Apply the new colour
				image_writer.setColor(x, y, color);
			}
		}
		return filter_image;
	}

	private int matrix(int[][] m, int[][] f) {

		int r = 0;

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				r += f[j][i] * m[j][i];

			}
		}

		return r;
	}

	public static void main(String[] args) {
		launch();
	}

}