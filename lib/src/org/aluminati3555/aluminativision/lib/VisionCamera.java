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

package org.aluminati3555.aluminativision.lib;

/**
 * This class converts between the output of the vision system and degrees
 * 
 * @author Caleb Heydon
 */
public class VisionCamera {
	private double horizontalFOV;
	private double verticalFOV;

	private double x;
	private double y;

	private double targetWidth;
	private double targetHeight;

	private double targetHeading;

	/**
	 * Returns the target x location in degrees
	 * 
	 * @return
	 */
	public double getXDegrees() {
		return x;
	}

	/**
	 * Returns the target y location in degrees
	 * 
	 * @return
	 */
	public double getYDegrees() {
		return y;
	}

	/**
	 * Returns the target width in degrees
	 * 
	 * @return
	 */
	public double getTargetWidthDegrees() {
		return targetWidth;
	}

	/**
	 * Returns the target height in degrees
	 * 
	 * @return
	 */
	public double getTargetHeightDegrees() {
		return targetHeight;
	}

	/**
	 * Returns the calculated gyro angle of the target
	 * WARNING: The output of this function is not normalized
	 * 
	 * @return
	 */
	public double getTargetHeading() {
		return targetHeading;
	}

	/**
	 * Updates the output
	 * 
	 * @param data
	 */
	public void update(VisionData data, double robotHeading) {
		x = data.x * horizontalFOV / 2;
		y = data.y * verticalFOV / 2;

		targetWidth = data.targetWidth * horizontalFOV / 2;
		targetHeight = data.targetHeight * verticalFOV / 2;

		targetHeading = robotHeading + x;
	}

	public VisionCamera(double horizontalFOV, double verticalFOV) {
		this.horizontalFOV = horizontalFOV;
		this.verticalFOV = verticalFOV;
	}
}
