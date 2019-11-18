/**
 * Copyright (c) 2019 Team 3555
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.aluminati3555.aluminativision.api;

/**
 * Pipeline config class
 * 
 * @author Caleb Heydon
 */
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
