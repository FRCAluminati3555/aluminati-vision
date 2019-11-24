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

package org.aluminati3555.aluminativision;

import java.io.IOException;

import org.aluminati3555.aluminativision.net.IVisionOutputHandler;
import org.aluminati3555.aluminativision.net.MJPEGServer;
import org.aluminati3555.aluminativision.net.VisionData;
import org.aluminati3555.aluminativision.pipeline.IVisionPipeline;
import org.aluminati3555.aluminativision.util.VisionUtil;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * This is a simple vision loop
 * 
 * @author Caleb Heydon
 */
public class VisionLoop extends Thread {
	private MJPEGServer server;

	private VideoCapture camera;
	private String cameraName;
	private IVisionPipeline visionPipeline;
	private IVisionOutputHandler visionOutputHandler;

	private double lastTime;
	private double currentFPS;

	private boolean wantsExit;

	/**
	 * Returns the vision pipeline
	 * 
	 * @return
	 */
	public IVisionPipeline getVisionPipeline() {
		return visionPipeline;
	}

	/**
	 * Sets the vision pipeline
	 * 
	 * @param visionPipeline
	 */
	public synchronized void setVisionPipeline(IVisionPipeline visionPipeline) {
		this.visionPipeline = visionPipeline;
	}

	/**
	 * Signals the vision loop to stop
	 * 
	 * @param wantsExit
	 */
	public synchronized void setWantsExit(boolean wantsExit) {
		this.wantsExit = wantsExit;
	}

	/**
	 * The start of the vision thread
	 */
	@Override
	public void run() {
		lastTime = VisionUtil.getTime();
		Mat frame = new Mat();

		while (!wantsExit) {
			camera.read(frame);

			double startTime = VisionUtil.getTime();
			try {
				frame = visionPipeline.process(frame, currentFPS);
			} catch (CvException e) {
				System.err.println("Error: Unable to process frame from " + cameraName);
				e.printStackTrace();
				continue;
			}

			double endTime = VisionUtil.getTime();

			// Get vision output
			VisionData output = visionPipeline.getOutput();
			output.timestamp = startTime;
			output.processingLatency = endTime - startTime;
			try {
				visionOutputHandler.update(output);
			} catch (IOException e) {
				System.err.println("Error: Unable to send vision data to robot");
			}

			server.sendFrame(frame, currentFPS);

			endTime = VisionUtil.getTime();
			currentFPS = 1 / (endTime - lastTime);
			lastTime = endTime;
		}

		frame.release();
	}

	public VisionLoop(MJPEGServer server, VideoCapture camera, String cameraName, IVisionPipeline pipeline,
			IVisionOutputHandler visionOutputHandler) {
		this.server = server;
		this.camera = camera;
		this.cameraName = cameraName;
		this.visionPipeline = pipeline;
		this.visionOutputHandler = visionOutputHandler;
	}
}
