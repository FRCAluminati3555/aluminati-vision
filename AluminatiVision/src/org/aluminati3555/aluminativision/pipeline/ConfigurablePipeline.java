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

package org.aluminati3555.aluminativision.pipeline;

import java.util.ArrayList;
import java.util.Comparator;

import org.aluminati3555.aluminativision.net.VisionData;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.PipelineMode;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.TargetMode;
import org.aluminati3555.aluminativision.util.VisionUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * A pipeline that can be configured
 * 
 * @author Caleb Heydon
 */
public class ConfigurablePipeline implements IVisionPipeline {
	private static final Scalar GREEN = new Scalar(0, 255, 0);

	private static final double DILATE_SIZE = 1.6;
	private static final Mat DILATE_ELEMENT = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
			new Size(2 * DILATE_SIZE + 1, 2 * DILATE_SIZE + 1), new Point(DILATE_SIZE, DILATE_SIZE));

	private VisionData visionData;
	private PipelineConfig pipelineConfig;

	private Scalar minScalar;
	private Scalar maxScalar;

	private Mat hierarchy;
	private Mat thresholdFrame;

	private ArrayList<MatOfPoint> contours;
	private ArrayList<ContourWithArea> contoursWithAreas;

	private Mat outputFrame;

	private Point upperLeft;
	private Point upperRight;
	private Point lowerLeft;
	private Point lowerRight;

	/**
	 * Returns the pipeline configuration
	 * 
	 * @return
	 */
	public PipelineConfig getPipelineConfig() {
		return pipelineConfig;
	}

	/**
	 * Gets the vision output
	 */
	public VisionData getOutput() {
		return visionData;
	}

	/**
	 * Sets the configuration
	 * 
	 * @param pipelineConfig
	 */
	public synchronized void setPipelineConfig(PipelineConfig pipelineConfig) {
		minScalar = new Scalar(pipelineConfig.thresholdHueMin, pipelineConfig.thresholdSaturationMin,
				pipelineConfig.thresholdValueMin);
		maxScalar = new Scalar(pipelineConfig.thresholdHueMax, pipelineConfig.thresholdSaturationMax,
				pipelineConfig.thresholdValueMax);
		this.pipelineConfig = pipelineConfig;
	}

	/**
	 * Picks a group of contours for dual target modes
	 */
	public void sortContours(ArrayList<ContourWithArea> contours) {
		if (contours.size() < 2) {
			return;
		}

		contours.sort(new Comparator<ContourWithArea>() {
			public int compare(ContourWithArea contour1, ContourWithArea contour2) {
				if (contour1.area < contour2.area) {
					return 1;
				} else if (contour1.area == contour2.area) {
					return 0;
				} else {
					return -1;
				}
			}
		});
	}

	/**
	 * Processes a frame
	 */
	public Mat process(Mat frame, double fps) {
		visionData.fps = fps;
		visionData.hasTarget = false;
		visionData.targetWidth = 0;
		visionData.targetHeight = 0;
		visionData.targetArea = 0;
		visionData.x = 0;
		visionData.y = 0;

		if (pipelineConfig.pipelineMode == PipelineMode.DRIVER) {
			return frame;
		}

		// Convert to hsv
		Imgproc.cvtColor(frame, thresholdFrame, Imgproc.COLOR_BGR2HSV);

		// Thresholding
		Core.inRange(thresholdFrame, minScalar, maxScalar, thresholdFrame);
		
		// Dilate
		Imgproc.dilate(thresholdFrame, thresholdFrame, DILATE_ELEMENT);

		// Contours
		contours.clear();
		contoursWithAreas.clear();

		Imgproc.findContours(thresholdFrame, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		double frameArea = (double) (frame.width() * frame.height());

		// Filter contours
		for (int i = 0; i < contours.size(); i++) {
			double actualArea = Imgproc.contourArea(contours.get(i));
			double area = actualArea / frameArea;

			Rect rect = Imgproc.boundingRect(contours.get(i));
			double ratio = (double) (rect.width) / rect.height;

			double boxArea = VisionUtil.computeBoxArea(rect);
			double density = VisionUtil.computeDensity(actualArea, boxArea);

			if (area < pipelineConfig.contourAreaMin || area > pipelineConfig.contourAreaMax
					|| ratio < pipelineConfig.contourRatioMin || ratio > pipelineConfig.contourRatioMax
					|| density < pipelineConfig.contourDensityMin || density > pipelineConfig.contourDensityMax) {
				contours.remove(i);
				i--;
				continue;
			}

			contoursWithAreas.add(new ContourWithArea(contours.get(i), rect, boxArea));
		}

		Imgproc.cvtColor(thresholdFrame, outputFrame, Imgproc.COLOR_GRAY2RGB);

		if (contours.size() > 0) {
			sortContours(contoursWithAreas);

			Rect rect1 = contoursWithAreas.get(0).rect;
			if (pipelineConfig.targetMode == TargetMode.SINGLE) {
				Imgproc.rectangle(outputFrame, rect1, GREEN, 3);
			}

			if ((pipelineConfig.targetMode == TargetMode.DUAL_HORIZONTAL
					|| pipelineConfig.targetMode == TargetMode.DUAL_VERTICAL) && contours.size() > 1) {
				visionData.hasTarget = true;

				Rect rect2 = contoursWithAreas.get(1).rect;
				
				if (pipelineConfig.targetMode == TargetMode.DUAL_HORIZONTAL) {
					// Properly order the targets
					if (rect2.x < rect1.x) {
						Rect temp = rect1;
						rect1 = rect2;
						rect2 = temp;
					}

					upperLeft.x = rect1.x;
					upperLeft.y = rect1.y;

					upperRight.x = rect2.x + rect2.width;
					upperRight.y = rect2.y;

					lowerLeft.x = rect1.x;
					lowerLeft.y = rect1.y + rect1.height;

					lowerRight.x = rect2.x + rect2.width;
					lowerRight.y = rect2.y + rect2.height;
				} else {
					// Properly order the targets
					if (rect2.y < rect1.y) {
						Rect temp = rect1;
						rect1 = rect2;
						rect2 = temp;
					}

					upperLeft.x = rect1.x;
					upperLeft.y = rect1.y;

					upperRight.x = rect1.x + rect1.width;
					upperRight.y = rect1.y;

					lowerLeft.x = rect2.x;
					lowerLeft.y = rect2.y + rect2.height;

					lowerRight.x = rect2.x + rect2.width;
					lowerRight.y = rect2.y + rect2.height;
				}

				VisionUtil.drawQuadrilateral(outputFrame, GREEN, 3, upperLeft, upperRight, lowerLeft, lowerRight);

				visionData.x = 2 * (((((upperLeft.x + lowerLeft.x) / 2) + ((upperRight.x + lowerRight.x) / 2)) / 2)
						- frame.width() / 2.0) / frame.width();
				visionData.y = 2 * -(((((upperLeft.y + lowerLeft.y) / 2) + ((upperRight.y + lowerRight.y) / 2)) / 2)
						- frame.height() / 2.0) / frame.height();

				visionData.targetWidth = VisionUtil.computeQuadrilateralWidth(upperLeft, upperRight, lowerLeft,
						lowerRight) / frameArea;
				visionData.targetHeight = VisionUtil.computeQuadrilateralHeight(upperLeft, upperRight, lowerLeft,
						lowerRight) / frameArea;
				visionData.targetArea = VisionUtil.computeQuadrilateralArea(upperLeft, upperRight, lowerLeft,
						lowerRight) / frameArea;
			} else if (pipelineConfig.targetMode == TargetMode.SINGLE) {
				visionData.hasTarget = true;

				visionData.x = 2 * (rect1.x + rect1.width / 2.0 - frame.width() / 2.0) / frame.width();
				visionData.y = 2 * -(rect1.y + rect1.height / 2.0 - frame.height() / 2.0) / frame.height();

				visionData.targetWidth = (double) (rect1.width) / frame.width();
				visionData.targetHeight = (double) (rect1.height) / frame.height();
				visionData.targetArea = ((double) rect1.width * rect1.height) / frameArea;
			}
		}

		return outputFrame;
	}

	public void release() {
		thresholdFrame.release();
		hierarchy.release();
		outputFrame.release();
	}

	public ConfigurablePipeline(PipelineConfig pipelineConfig) {
		setPipelineConfig(pipelineConfig);

		visionData = new VisionData();

		hierarchy = new Mat();
		thresholdFrame = new Mat();

		contours = new ArrayList<MatOfPoint>();
		contoursWithAreas = new ArrayList<ContourWithArea>();

		outputFrame = new Mat();

		upperLeft = new Point();
		upperRight = new Point();
		lowerLeft = new Point();
		lowerRight = new Point();
	}
}
