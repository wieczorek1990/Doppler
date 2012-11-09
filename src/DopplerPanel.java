import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.text.AttributedString;

import javax.swing.JPanel;

public class DopplerPanel extends JPanel {
	private static final BasicStroke dashed = new BasicStroke(3.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
			new float[] { 10.0f }, 0.0f);
	private static final String frequencyObserverDescription = "Częstotliwość słyszalna";
	private static final String frequencySourceDescription = "Częstotliwość emisji";
	private static final Color green = new Color(60, 140, 40);
	private static final String observedFrequencyDescription = "Częstotliwość obserwowana f(t)";
	private static final String observerAngleDescription = "\u03B8";
	private static final Color purple = new Color(75, 30, 140);
	private static final long serialVersionUID = 1891140777059740472L;
	private static final int timePointsCount = 100;
	private static final Polygon vehicule = new Polygon(new int[] { 0, 2, 3, 2,
			0 }, new int[] { -1, -1, 0, 1, 1 }, 5);
	private static final String velocityDescription = "\u03BD";
	private static final AttributedString velocityRelativeDescription = new AttributedString(
			"\u03BDr");
	private static final String xPositionDescription = "\u03C7";
	private static final Color yellow = new Color(225, 180, 45);
	private double[] frequencyObservedPoints;
	private double frequencySource;
	private int legendHeight;
	private int legendPaddingX = 15;
	private double observerLocation;
	private int plotHeight;
	private boolean plotUpToDate = false;
	private int plotWithLegendHeight;
	private int plotX[];
	private int plotY[];
	private double roadDistance = 200.0;
	private int roadWidth = 30;
	private double scaleObjects = 0.8;
	private double scalePlot = 0.5;
	private int sceneHeight;
	private int sceneLegendTextPaddingX = 25;
	/**
	 * At 15 deg. Celsius
	 */
	private double soundWaveSpeed = 340.2;
	private double time;
	private double timeMax;
	private double[] timePoints;
	private double velocityInitial;

	public DopplerPanel(double observerLocation, double sourceFrequency,
			double initialVelocity, double time, double timeMax) {
		super();
		this.velocityInitial = initialVelocity;
		this.observerLocation = observerLocation;
		this.frequencySource = sourceFrequency;
		this.time = time;
		this.timeMax = timeMax;
		velocityRelativeDescription.addAttribute(TextAttribute.SUPERSCRIPT,
				TextAttribute.SUPERSCRIPT_SUB, 1, 2);
	}

	private int convertFrequencyForPlot(double f) {
		return (int) (scalePlot * (map(f, 0.0, frequencySource * 2.0, 0.0,
				plotWithLegendHeight)));
	}

	private int convertTimeForPlot(double c) {
		return (int) (scalePlot * map(c, 0.0, timeMax, 0.0, getWidth()));
	}

	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
	
	// TODO generacja okręgów częstotliwości
	private void drawCircles(Graphics2D g2d) {
		g2d.setStroke(dashed);
		g2d.setColor(purple);

		// double period = 1.0 / frequencySource;
		// for (double timeCurrent = 0.0; timeCurrent < time; timeCurrent +=
		// period) {
		//
		// }

		// int x = getVehiculeCenterX();
		// int y = getRoadY();
		// int radius;
		// radius = 10;
		// Ellipse2D circle = getCricle(x, y, radius);
		// g2d.setColor(purple);
		// g2d.draw(circle);
	}

	private void drawLegend(Graphics2D g2d) {
		int legendPaddingY = g2d.getFontMetrics().getHeight();
		g2d.setColor(Color.black);
		g2d.drawString(frequencySourceDescription + " = " + frequencySource,
				legendPaddingX, legendPaddingY);
		g2d.drawString(frequencyObserverDescription + " = "
				+ formatDouble(getFrequencyObserver()), legendPaddingX
				+ getWidth() / 2, legendPaddingY);
		g2d.drawString(xPositionDescription + " = "
				+ formatDouble(getXPosition()), legendPaddingX,
				legendPaddingY * 2);
		g2d.drawString(velocityDescription + " = "
				+ formatDouble(velocityInitial), legendPaddingX + getWidth()
				/ 2, legendPaddingY * 2);
		g2d.drawString(observerAngleDescription + " = "
				+ formatDouble(getObserverAngle()), legendPaddingX,
				legendPaddingY * 3);
		g2d.drawString(velocityRelativeDescription.getIterator(),
				legendPaddingX + getWidth() / 2, legendPaddingY * 3);
		g2d.drawString(" = " + formatDouble(getVelocityRelative()),
				legendPaddingX + getWidth() / 2 + 6, legendPaddingY * 3);
	}

	private void drawObserver(Graphics2D g2d) {
		double observerSize = scaleObjects * roadWidth;
		Shape observer = getCricle(getWidth() / 2, getObserverY(),
				observerSize / 2);
		g2d.setPaint(green);
		g2d.setStroke(new BasicStroke());
		g2d.fill(observer);
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(observer);
	}

	private void drawObserverLegend(Graphics2D g2d) {
		g2d.setPaint(yellow);
		g2d.setStroke(new BasicStroke(2));
		g2d.drawLine(getWidth() / 2, getRoadY(), getWidth() / 2, getObserverY());
		g2d.drawLine(getVehiculeCenterX(), getRoadY(), getWidth() / 2,
				getObserverY());
	}

	private void drawObserverLegendText(Graphics2D g2d) {
		g2d.setPaint(yellow);
		g2d.drawString(formatDouble(observerLocation) + " m", getWidth() / 2
				+ sceneLegendTextPaddingX / 4 * 3, getObserverY());
	}

	private void drawPlot(Graphics2D g2d) {
		preparePlotData();
		g2d.scale(1, -1);
		g2d.translate(0, -plotHeight / 2.0);
		g2d.setPaint(purple);
		g2d.setStroke(new BasicStroke());
		for (int x = 0; x < timePointsCount; x++) {
			g2d.drawLine(plotX[x], plotY[x], plotX[x + 1], plotY[x + 1]);
		}
		g2d.setStroke(new BasicStroke(2));
		int timeCurrent = convertTimeForPlot(time);
		g2d.drawLine(timeCurrent, 0, timeCurrent, plotWithLegendHeight);
	}

	private void drawPlotLegend(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke());
		g2d.drawString(
				observedFrequencyDescription,
				(getWidth() - g2d.getFontMetrics().stringWidth(
						observedFrequencyDescription)) / 2, g2d
						.getFontMetrics().getHeight());
	}

	private void drawRoad(Graphics2D g2d) {
		int roadY = getRoadY();
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke());
		g2d.drawLine(0, roadY - roadWidth / 2, getWidth(), roadY - roadWidth
				/ 2);
		g2d.drawLine(0, roadY + roadWidth / 2, getWidth(), roadY + roadWidth
				/ 2);
		g2d.setStroke(new BasicStroke(6));
		g2d.drawLine(0, roadY, getWidth(), roadY);
	}

	private void drawScene(Graphics2D g2d) {
		drawRoad(g2d);
		drawObserverLegend(g2d);
		drawVehicule(g2d);
		drawObserver(g2d);
		drawObserverLegendText(g2d);
		drawCircles(g2d);
	}

	private void drawVehicule(Graphics2D g2d) {
		Shape vehiculeToDraw = getVehiculeToDraw();
		g2d.setPaint(purple);
		g2d.setStroke(new BasicStroke());
		g2d.fill(vehiculeToDraw);
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(vehiculeToDraw);
	}

	private String formatDouble(double val) {
		return String.format("%1$,.1f", val);
	}

	private Ellipse2D getCricle(int x, int y, double radius) {
		return new Ellipse2D.Double(x - radius, y - radius, radius * 2,
				radius * 2);
	}

	public double getDistance() {
		return roadDistance;
	}

	private double getFrequencyObserver() {
		return getFrequencyObserver(time);
	}

	// TODO poprawić wzór
	private double getFrequencyObserver(double time) {
		return (1.0 - (velocityInitial - getVelocityRelative(time))
				/ soundWaveSpeed)
				* frequencySource;
	}

	private double getObserverAngle() {
		return getObserverAngle(observerLocation, time);
	}

	private double getObserverAngle(double observerLocation, double time) {
		return Math.toDegrees(Math.PI / 2
				- Math.atan(Math.abs(getXPosition()) / observerLocation));
	}

	private int getObserverY() {
		return (int) (getRoadY() + observerLocation);
	}

	private int getRoadY() {
		return sceneHeight / 2;
	}

	private int getVehiculeCenterX() {
		return (int) (scaleObjects * roadWidth / 2) + getVehiculePositionX();
	}

	private int getVehiculePositionX() {
		return (int) (velocityInitial * time / roadDistance * getWidth());
	}

	private Shape getVehiculeToDraw() {
		double vehiculeSize = scaleObjects * roadWidth / 2;
		AffineTransform at = new AffineTransform();
		at.translate(getVehiculePositionX(), getRoadY());
		at.scale(vehiculeSize, vehiculeSize);
		return at.createTransformedShape(vehicule);
	}

	private double getVelocityRelative() {
		return getVelocityRelative(time);
	}

	private double getVelocityRelative(double time) {
		return velocityInitial
				* Math.cos(Math.toRadians(getObserverAngle(observerLocation,
						time)));
	}

	private double getXPosition() {
		return getXPosition(velocityInitial, time);
	}

	private double getXPosition(double velocityInitial, double time) {
		return velocityInitial * time - 100.0;
	}

	public double map(double x, double inMin, double inMax, double outMin,
			double outMax) {
		return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D main = (Graphics2D) g;
		int fontHeight = main.getFontMetrics().getHeight();
		legendHeight = fontHeight * 3;
		sceneHeight = (int) ((getHeight() - legendHeight) * 0.4);
		plotWithLegendHeight = getHeight() - sceneHeight - legendHeight;
		plotHeight = plotWithLegendHeight - fontHeight;
		drawBackground(main);
		Graphics2D legend = (Graphics2D) main.create(0, 0, getWidth(),
				legendHeight);
		drawLegend(legend);
		legend.dispose();
		Graphics2D scene = (Graphics2D) main.create(0, legendHeight,
				getWidth(), sceneHeight);
		drawScene(scene);
		scene.dispose();
		Graphics2D plotWithLegend = (Graphics2D) main.create(0, legendHeight
				+ sceneHeight, getWidth(), plotWithLegendHeight);
		drawPlotLegend(plotWithLegend);
		Graphics2D plot = (Graphics2D) plotWithLegend.create((int) (getWidth()
				* scalePlot / 2.0), (int) (plotHeight * scalePlot / 2.0),
				(int) (getWidth() * scalePlot),
				(int) (plotWithLegendHeight * scalePlot));
		drawPlot(plot);
		plot.dispose();
		plotWithLegend.dispose();
		main.dispose();
	}

	private void preparePlotData() {
		if (!plotUpToDate) {
			timePoints = new double[timePointsCount + 1];
			frequencyObservedPoints = new double[timePointsCount + 1];
			plotX = new int[timePointsCount + 1];
			plotY = new int[timePointsCount + 1];
			for (int x = 0; x < timePointsCount + 1; x++) {
				timePoints[x] = (double) x * timeMax / (double) timePointsCount;
				frequencyObservedPoints[x] = getFrequencyObserver(time);
				plotX[x] = convertTimeForPlot(timePoints[x]);
				plotY[x] = convertFrequencyForPlot(frequencyObservedPoints[x]);
			}
			plotUpToDate = true;
		}
	}

	public void setInitialVelocity(double initialVelocity) {
		this.velocityInitial = initialVelocity;
		plotUpToDate = false;
	}

	public void setObserverLocation(double observerLocation) {
		this.observerLocation = observerLocation;
		plotUpToDate = false;
	}

	public void setSourceFrequency(double sourceFrequency) {
		this.frequencySource = sourceFrequency;
		plotUpToDate = false;
	}

	public void setTime(double time) {
		this.time = time;
		plotUpToDate = false;
	}

	public void setTimeMax(int timeMax) {
		this.timeMax = timeMax;
		plotUpToDate = false;
	}
}
