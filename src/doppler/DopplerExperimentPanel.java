package doppler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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

public class DopplerExperimentPanel extends JPanel {
	private static final int circleSkipCount = 100;
	private static final BasicStroke dashed = new BasicStroke(2.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
			new float[] { 10.0f }, 0.0f);
	private static final int decimalDigits = 3;
	private static final String frequencyObserverDescriptionFormat = "Częstotliwość słyszalna = %s Hz";
	private static final String frequencySourceDescriptionFormat = "Częstotliwość emisji = %s Hz";
	private static final double frequencySourceMax = 1250.0;
	private static final double frequencySourceMin = 200.0;
	private static final Color green = new Color(60, 140, 40);
	private static final int legendPaddingX = 15;
	private static final String observedFrequencyDescriptionFormat = "Częstotliwość obserwowana";
	private static final String observerAngleDescriptionFormat = "\u03B8 = %s\u00B0";
	private static final double observerLocationMax = 40.0;
	private static final double observerLocationMin = 1.0;
	private static final double percentSurrounding = 0.35;
	private static final Color purple = new Color(75, 30, 140);
	private static final double roadDistance = 200.0;
	private static final int roadWidth = 30;
	private static final double scaleObjects = 0.8;
	private static final double scalePlot = 0.75;
	private static final double scaleScenePlot = 0.4;
	private static final int sceneLegendTextPaddingX = 25;
	private static final long serialVersionUID = 1891140777059740472L;
	private static final double temperature = 25.0;
	private static final double timeMin = 0;
	private static final Polygon vehicule = new Polygon(new int[] { 0, 2, 3, 2,
			0 }, new int[] { -1, -1, 0, 1, 1 }, 5);
	private static final String velocityDescriptionFormat = "\u03BD = %s m/s";
	private static final double velocityInitialMax = 50.0;
	private static final double velocityInitialMin = 10.0;
	private static final String velocityRelativeDescriptionFormat = "\u03BDr = %s m/s";
	private static final String xPositionDescriptionFormat = "\u03C7 = %s m";
	private static final String plotXAxisDescription = "t [s]";
	private static final String plotYAxisDescription = "f [Hz]";
	private static final Color yellow = new Color(225, 180, 45);

	public static String formatDouble(double val) {
		return String.format("%1$,.1f", val);
	}

	private double[] frequencyObservedPoints;
	private double frequencySource;
	private int legendHeight;
	private double observerLocation;
	private boolean plotDataUpToDate = false;
	private int plotFromLegendHeight;
	private int plotHeight;
	private int plotLegendHeight;
	private int plotX[];
	private int plotY[];
	private int sceneHeight;
	private double time;
	private double timeMax;
	private double[] timePoints;
	private int timePointsCount;
	private double velocityInitial;
	private double velocitySoundWave;
	private int plotXAxisDescriptionXPadding = 5;
	private int plotYAxisDescriptionXPadding = 5;

	public DopplerExperimentPanel() {
		super();
		this.velocityInitial = velocityInitialMin;
		this.observerLocation = observerLocationMin;
		this.frequencySource = frequencySourceMin;
		this.time = timeMin;
		this.timeMax = roadDistance / this.velocityInitial;
		this.velocitySoundWave = 332.0 * (1.0 + Math
				.sqrt((temperature / 273.0)));
	}

	public int convertDoubleToSlider(double value, double inMin, double inMax,
			int outMax) {
		return (int) map(value, inMin, inMax, 0, outMax);
	}

	private int convertFrequencyForPlot(double frequency) {
		return (int) (map(frequency - frequencySource
				* (1.0 - percentSurrounding), 0.0, frequencySource
				* (2.0 * percentSurrounding), 0.0, plotHeight));
	}

	public double convertSliderToDouble(int value, int inMax, double outMin,
			double outMax) {
		return roundDouble(map(value, 0, inMax, outMin, outMax));
	}

	private int convertTimeForPlot(double time) {
		return (int) (map(time, 0.0, timeMax, 0.0, getWidth()));
	}

	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	private void drawCircles(Graphics2D g2d) {
		g2d.setStroke(dashed);
		g2d.setColor(purple);

		double period = 1.0 / frequencySource;
		for (double timeCurrent = 0.0; timeCurrent < time; timeCurrent += period
				* circleSkipCount) {
			int x = getVehiculeCenterX();
			int y = getRoadY();
			double radius = velocitySoundWave * (time - timeCurrent);
			Ellipse2D circle = getCricle(x, y, radius);
			g2d.draw(circle);
		}
	}

	private void drawLegend(Graphics2D g2d) {
		int legendPaddingY = g2d.getFontMetrics().getHeight();
		g2d.setColor(Color.black);
		g2d.drawString(String.format(frequencySourceDescriptionFormat,
				formatDouble(frequencySource)), legendPaddingX, legendPaddingY);
		g2d.drawString(String.format(frequencyObserverDescriptionFormat,
				formatDouble(getFrequencyObserver())), legendPaddingX
				+ getWidth() / 2, legendPaddingY);
		g2d.drawString(String.format(xPositionDescriptionFormat,
				formatDouble(getXPosition())), legendPaddingX,
				legendPaddingY * 2);
		g2d.drawString(String.format(velocityDescriptionFormat,
				formatDouble(velocityInitial)),
				legendPaddingX + getWidth() / 2, legendPaddingY * 2);
		g2d.drawString(String.format(observerAngleDescriptionFormat,
				formatDouble(getObserverAngle())), legendPaddingX,
				legendPaddingY * 3);
		AttributedString as = new AttributedString(String.format(
				velocityRelativeDescriptionFormat,
				formatDouble(getVelocityRelative())));
		as.addAttribute(TextAttribute.SUPERSCRIPT,
				TextAttribute.SUPERSCRIPT_SUB, 1, 2);
		g2d.drawString(as.getIterator(), legendPaddingX + getWidth() / 2,
				legendPaddingY * 3);
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
		g2d.translate(0, plotHeight);
		g2d.scale(1, -1);
		AffineTransform at = new AffineTransform(g2d.getTransform());
		at.translate(getWidth() * (1.0 - scalePlot) / 2.0, plotHeight
				* (1.0 - scalePlot) / 2.0);
		at.scale(scalePlot, scalePlot);
		g2d.setTransform(at);
		g2d.setPaint(purple);
		g2d.setStroke(new BasicStroke());
		for (int x = 0; x < timePointsCount; x++) {
			g2d.drawLine(plotX[x], plotY[x], plotX[x + 1], plotY[x + 1]);
		}
		g2d.setStroke(new BasicStroke(2));
		int timeCurrent = convertTimeForPlot(time);
		g2d.drawLine(timeCurrent, 0, timeCurrent, plotHeight);

		g2d.setColor(Color.red);
		g2d.setStroke(new BasicStroke());
		g2d.drawLine(0, 0, 0, plotHeight);
		g2d.drawLine(0, plotHeight / 2, getWidth(), plotHeight / 2);
		
		g2d.scale(1, -1);
		Font font = g2d.getFont();
		float size = font.getSize2D() * (float)(1.0 / scalePlot);
		Font newFont = font.deriveFont(size);
		g2d.setFont(newFont);
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke());
		g2d.drawString(plotXAxisDescription, getWidth() + plotXAxisDescriptionXPadding, -plotHeight / 2);
		g2d.drawString(plotYAxisDescription, plotYAxisDescriptionXPadding, -plotHeight);

	}

	private void drawPlotLegend(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke());
		g2d.drawString(
				observedFrequencyDescriptionFormat,
				(getWidth() - g2d.getFontMetrics().stringWidth(
						observedFrequencyDescriptionFormat)) / 2, g2d
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

	public int getFrequencySourceFromDouble(double frequencySource) {
		return convertDoubleToSlider(frequencySource, frequencySourceMin,
				frequencySourceMax, getFrequencySourceMaxForSlider());
	}

	public double getFrequencySourceFromSlider(int frequencySource) {
		return convertSliderToDouble(frequencySource,
				getSourceFrequencyMaxForSlider(), frequencySourceMin,
				frequencySourceMax);
	}

	public int getFrequencySourceMaxForSlider() {
		return getMaxForSlider(frequencySourceMin, frequencySourceMax);
	}

	public int getMaxForSlider(double min, double max) {
		return (int) (Math.pow(10, decimalDigits) * (max - min));
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

	public int getObserverLocationFromDouble(double observerLocation) {
		return convertDoubleToSlider(observerLocation, observerLocationMin,
				observerLocationMax, getObserverLocationMaxForSlider());
	}

	public double getObserverLocationFromSlider(int observerLocation) {
		return convertSliderToDouble(observerLocation,
				getObserverLocationMaxForSlider(), observerLocationMin,
				observerLocationMax);
	}

	public int getObserverLocationMaxForSlider() {
		return getMaxForSlider(observerLocationMin, observerLocationMax);
	}

	private int getObserverY() {
		return (int) (getRoadY() + observerLocation);
	}

	private int getRoadY() {
		return sceneHeight / 2;
	}

	private int getSourceFrequencyMaxForSlider() {
		return getMaxForSlider(frequencySourceMin, frequencySourceMax);
	}

	public int getTimeFromDouble(double time) {
		return convertDoubleToSlider(time, timeMin, timeMax,
				getTimeMaxForSlider());
	}

	public double getTimeFromSlider(int time) {
		return convertSliderToDouble(time, getTimeMaxForSlider(), timeMin,
				timeMax);
	}

	public int getTimeMaxForSlider() {
		return getMaxForSlider(timeMin, timeMax);
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

	public int getVelocityInitialFromDouble(double velocityInitial) {
		return convertDoubleToSlider(velocityInitial, velocityInitialMin,
				velocityInitialMax, getVelocityInitialMaxForSlider());
	}

	public double getVelocityInitialFromSlider(int velocityInitial) {
		return convertSliderToDouble(velocityInitial,
				getVelocityInitialMaxForSlider(), velocityInitialMin,
				velocityInitialMax);
	}

	public int getVelocityInitialMaxForSlider() {
		return getMaxForSlider(velocityInitialMin, velocityInitialMax);
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

	public void invalidatePlot() {
		this.plotDataUpToDate = false;
		repaint();
	}

	private double map(double x, double inMin, double inMax, double outMin,
			double outMax) {
		return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D main = (Graphics2D) g;
		int fontHeight = main.getFontMetrics().getHeight();
		legendHeight = fontHeight * 4;
		sceneHeight = (int) ((getHeight() - legendHeight) * scaleScenePlot);
		plotFromLegendHeight = getHeight() - sceneHeight - legendHeight;
		plotLegendHeight = fontHeight * 2;
		plotHeight = plotFromLegendHeight - plotLegendHeight;
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
		Graphics2D plotFromLegend = (Graphics2D) main.create(0, legendHeight
				+ sceneHeight, getWidth(), plotFromLegendHeight);
		drawPlotLegend(plotFromLegend);
		Graphics2D plot = (Graphics2D) plotFromLegend.create(0,
				plotLegendHeight, getWidth(), plotHeight);
		drawPlot(plot);
		plot.dispose();
		plotFromLegend.dispose();
		main.dispose();
	}

	private void preparePlotData() {
		if (!plotDataUpToDate) {
			timePoints = new double[timePointsCount + 1];
			frequencyObservedPoints = new double[timePointsCount + 1];
			plotX = new int[timePointsCount + 1];
			plotY = new int[timePointsCount + 1];
			for (int x = 0; x < timePointsCount + 1; x++) {
				timePoints[x] = x * timeMax / timePointsCount;
				frequencyObservedPoints[x] = getFrequencyObserver(timePoints[x]);
				plotX[x] = convertTimeForPlot(timePoints[x]);
				plotY[x] = convertFrequencyForPlot(frequencyObservedPoints[x]);
			}
			plotDataUpToDate = true;
		}
	}

	private double roundDouble(double z) {
		return new BigDecimal(z).setScale(3, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	public void setFrequencySource(double sourceFrequency) {
		this.frequencySource = sourceFrequency;
		repaint();
	}

	public void setFrequencySourceFromSlider(int frequencySource) {
		setFrequencySource(convertSliderToDouble(frequencySource,
				getFrequencySourceMaxForSlider(), frequencySourceMin,
				frequencySourceMax));
	}

	public void setObserverLocation(double observerLocation) {
		this.observerLocation = observerLocation;
		invalidatePlot();
	}

	public void setObserverLocationFromSlider(int observerLocation) {
		setObserverLocation(convertSliderToDouble(observerLocation,
				getObserverLocationMaxForSlider(), observerLocationMin,
				observerLocationMax));
	}

	public void setTime(double time) {
		this.time = time;
		repaint();
	}

	public void setTimeFromSlider(int time) {
		setTime(convertSliderToDouble(time, getTimeMaxForSlider(), timeMin,
				timeMax));
	}

	public void setTimeMax(int timeMax) {
		this.timeMax = timeMax;
		invalidatePlot();
	}

	public void setVelocityInitial(double velocityInitial) {
		this.velocityInitial = velocityInitial;
		this.timeMax = roadDistance / velocityInitial;
		invalidatePlot();
	}

	public void setVelocityInitialFromSlider(int velocityInitial) {
		setVelocityInitial(convertSliderToDouble(velocityInitial,
				getVelocityInitialMaxForSlider(), velocityInitialMin,
				velocityInitialMax));
	}
}