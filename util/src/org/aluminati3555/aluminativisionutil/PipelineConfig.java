package org.aluminati3555.aluminativisionutil;

public class PipelineConfig {
	// Pipeline mode
	public PipelineMode pipelineMode = PipelineMode.DRIVER;

	// Target mode
	public TargetMode targetMode = TargetMode.SINGLE;

	// Thresholding
	public double thresholdHueMin = 0;
	public double thresholdHueMax = 180;

	public double thresholdSaturationMin = 0;
	public double thresholdSaturationMax = 255;

	public double thresholdValueMin = 0;
	public double thresholdValueMax = 255;

	// Contour filtering
	public double contourAreaMin = 0;
	public double contourAreaMax = 1;

	public double contourRatioMin = 0.5;
	public double contourRatioMax = 1.5;

	public double contourDensityMin = 0;
	public double contourDensityMax = 1;

	public enum PipelineMode {
		DRIVER, PROCESSING
	}

	public enum TargetMode {
		SINGLE, DUAL_HORIZONTAL, DUAL_VERTICAL
	}
}
