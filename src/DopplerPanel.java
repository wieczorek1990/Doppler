import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.text.AttributedString;

import javax.swing.JPanel;

public class DopplerPanel extends JPanel {
	private static final double scaleScenePlot = 0.4;
	private static final BasicStroke dashed = new BasicStroke(2.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
			new float[] { 10.0f }, 0.0f);
	private static final String frequencyObserverDescription = "Częstotliwość słyszalna";
	private static final String frequencySourceDescription = "Częstotliwość emisji";
	private static final Color green = new Color(60, 140, 40);
	private static final String observedFrequencyDescription = "Częstotliwość obserwowana";
	private static final String observerAngleDescription = "\u03B8";
	private static final Color purple = new Color(75, 30, 140);
	private static final long serialVersionUID = 1891140777059740472L;
	private static final double temperature = 25.0;
	private int timePointsCount;
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
	private double scalePlot = 0.75;
	private int sceneHeight;
	private int sceneLegendTextPaddingX = 25;
	private double time;
	private double[] timePoints;
	private double velocityInitial;
	private double velocitySoundWave;

	private double observerLocationMin = 1.0;
	private double observerLocationMax = 40.0;
	private double frequencySourceMin = 200.0;
	private double frequencySourceMax = 1250.0;
	private double velocityInitialMin = 10.0;
	private double velocityInitialMax = 50.0;
	private double timeMin = 0;
	private double timeMax;
	private double decimalDigits = 3.0;

	public DopplerPanel() {
		super();
		this.velocityInitial = velocityInitialMin;
		this.observerLocation = observerLocationMin;
		this.frequencySource = frequencySourceMin;
		this.time = timeMin;
		this.timeMax = roadDistance / this.velocityInitial;
		this.velocitySoundWave = 332.0 * (1.0 + Math
				.sqrt((temperature / 273.0)));
		velocityRelativeDescription.addAttribute(TextAttribute.SUPERSCRIPT,
				TextAttribute.SUPERSCRIPT_SUB, 1, 2);
	}

	// TODO poprawić
	private int convertFrequencyForPlot(double frequency) {
		double x = 0.35;
		return (int) (map(frequency - frequencySource * (1.0 - x), 0.0,
				frequencySource * (2.0 * x), 0.0, scalePlot * plotHeight));
	}

	private int convertTimeForPlot(double time) {
		return (int) (map(time, 0.0, timeMax, 0.0, scalePlot * getWidth()));
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
		g2d.translate(0, -plotHeight);
		g2d.setPaint(purple);
		g2d.setStroke(new BasicStroke());
		for (int x = 0; x < timePointsCount; x++) {
			g2d.drawLine(plotX[x], plotY[x], plotX[x + 1], plotY[x + 1]);
		}
		g2d.setStroke(new BasicStroke(2));
		int timeCurrent = convertTimeForPlot(time);
		g2d.drawLine(timeCurrent, 0, timeCurrent, plotHeight);
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
		g2d.setStroke(new BasicStroke(4));
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

	private double getFrequencyObserver() {
		return getFrequencyObserver(time);
	}

	private double getFrequencyObserver(double time) {
		double sign;
		if (getXPosition(time) < 0.0) {
			sign = -1.0;
		} else {
			sign = 1.0;
		}
		return (velocitySoundWave / (velocitySoundWave + sign
				* getVelocityRelative(time)))
				* frequencySource;
	}

	private double getObserverAngle() {
		return getObserverAngle(time);
	}

	private double getObserverAngle(double time) {
		double x = getXPosition(time);
		if (Math.abs(x) < 0.5) {
			x = 0.5;
		}
		return Math.toDegrees(Math.atan(observerLocation / Math.abs(x)));
	}

	private int getObserverY() {
		return (int) (getRoadY() + observerLocation);
	}

	public double getRoadDistance() {
		return roadDistance;
	}

	private int getRoadY() {
		return sceneHeight / 2;
	}

	private int getVehiculeCenterX() {
		return (int) (scaleObjects * roadWidth / 2.0) + getVehiculePositionX();
	}

	private int getVehiculePositionX() {
		return (int) (velocityInitial * time / roadDistance * getWidth());
	}

	private Shape getVehiculeToDraw() {
		double vehiculeSize = scaleObjects * roadWidth / 2.0;
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
				* Math.cos(Math.toRadians(getObserverAngle(time)));
	}

	private double getXPosition() {
		return getXPosition(time);
	}

	private double getXPosition(double time) {
		return velocityInitial * time - roadDistance / 2.0;
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
		sceneHeight = (int) ((getHeight() - legendHeight) * scaleScenePlot);
		plotWithLegendHeight = getHeight() - sceneHeight - legendHeight;
		plotHeight = plotWithLegendHeight - fontHeight;
		timePointsCount = (int) (getWidth() * scalePlot);
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
		// plotWithLegend.setColor(Color.black);
		// plotWithLegend.drawRect(0, 0, getWidth(), plotWithLegendHeight);
		drawPlotLegend(plotWithLegend);
		Graphics2D plot = (Graphics2D) plotWithLegend.create(
				(int) (getWidth() * ((1.0 - scalePlot) / 2.0)),
				(int) (fontHeight + plotHeight * ((1.0 - scalePlot) / 2.0)),
				(int) (getWidth() * scalePlot), (int) (plotHeight * scalePlot));
		// plot.drawRect(0, 0, (int) (getWidth() * scalePlot),
		// (int) (plotWithLegendHeight * scalePlot));
		// plot.setColor(Color.black);
		// int y = (int) (plotHeight * 0.5);
		// plot.drawLine(0, y, (int) (getWidth() * scalePlot), y);
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
				frequencyObservedPoints[x] = getFrequencyObserver(timePoints[x]);
				plotX[x] = convertTimeForPlot(timePoints[x]);
				plotY[x] = convertFrequencyForPlot(frequencyObservedPoints[x]);
			}
			plotUpToDate = true;
		}
	}

	public void setVelocityInitial(double velocityInitial) {
		this.velocityInitial = velocityInitial;
		this.timeMax = roadDistance / velocityInitial;
		plotUpToDate = false;
	}

	public void setObserverLocation(double observerLocation) {
		this.observerLocation = observerLocation;
		plotUpToDate = false;
	}

	private double roundDouble(double z) {
		return new BigDecimal(z).setScale(3, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	public void setObserverLocationSlider(double observerLocation) {
		setObserverLocation(roundDouble(map(observerLocation, 0,
				getObserverLocationMaxSlider(), observerLocationMin,
				observerLocationMax)));
	}

	public void setFrequencySourceSlider(double frequencySource) {
		setFrequencySource(roundDouble(map(frequencySource, 0,
				getFrequencySourceMaxSlider(), frequencySourceMin,
				frequencySourceMax)));
	}

	public void setVelocityInitialSlider(double velocityInitial) {
		setVelocityInitial(roundDouble(map(velocityInitial, 0,
				getVelocityInitialMaxSlider(), velocityInitialMin,
				velocityInitialMax)));
	}

	public void setTimeSlider(double time) {
		setTime(roundDouble(map(time, 0, getTimeMaxSlider(), timeMin, timeMax)));
	}

	public void setFrequencySource(double sourceFrequency) {
		this.frequencySource = sourceFrequency;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public void setTimeMax(int timeMax) {
		this.timeMax = timeMax;
		plotUpToDate = false;
	}

	public void invalidatePlot() {
		this.plotUpToDate = false;
	}

	public int getFrequencySourceMaxSlider() {
		return (int) (Math.pow(10, decimalDigits) * (frequencySourceMax - frequencySourceMin));
	}

	public int getObserverLocationMaxSlider() {
		return (int) (Math.pow(10, decimalDigits) * (observerLocationMax - observerLocationMin));
	}

	public int getVelocityInitialMaxSlider() {
		return (int) (Math.pow(10, decimalDigits) * (velocityInitialMax - velocityInitialMin));
	}

	public int getTimeMaxSlider() {
		return (int) (Math.pow(10, decimalDigits) * (timeMax - timeMin));
	}
}