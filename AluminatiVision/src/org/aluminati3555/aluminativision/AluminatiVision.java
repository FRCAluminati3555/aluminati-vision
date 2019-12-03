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
import java.net.SocketException;
import java.net.UnknownHostException;

import org.aluminati3555.aluminativision.net.ConfigListener;
import org.aluminati3555.aluminativision.net.IVisionOutputHandler;
import org.aluminati3555.aluminativision.net.MJPEGServer;
import org.aluminati3555.aluminativision.net.UDPVisionOutputHandler;
import org.aluminati3555.aluminativision.pipeline.ConfigurablePipeline;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.PipelineMode;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.TargetMode;
import org.aluminati3555.aluminativision.util.VisionUtil;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class AluminatiVision {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private static final CameraResolution CAMERA0_RESOLUTION = new CameraResolution(320, 240);
	private static final CameraResolution STREAM0_RESOLUTION = new CameraResolution(160, 120);

	private static final int CAMERA0_FPS = 187;
	private static final int STREAM0_FPS = 15;

	private static MJPEGServer server0;
	private static VideoCapture camera0;
	private static VisionLoop loop0;
	private static ConfigListener listener0;

	public static void configMicrosoftCamera(int camera, int frameWidth, int frameHeight, int fps, int exposure,
			int brightness, int whiteBalanceTemperature) {
		try {
			Runtime.getRuntime().exec("v4l2-ctl -d " + camera + " --set-fmt-video=width=" + frameWidth + ",height="
					+ frameHeight + ",pixelformat=0 --set-parm=" + fps);
			Runtime.getRuntime()
					.exec("v4l2-ctl -d " + camera + " --set-ctrl=exposure_auto=1,exposure_absolute=" + exposure
							+ ",brightness=" + brightness
							+ ",white_balance_temperature_auto=0,white_balance_temperature=" + whiteBalanceTemperature);
		} catch (IOException e) {
			System.err.println("Error: Unable to run shell command to update settings for camera");
		}
	}

	public static void configPS3Camera(int camera, int frameWidth, int frameHeight, int fps, int exposure,
			int brightness) {
		try {
			Runtime.getRuntime().exec("v4l2-ctl -d " + camera + " --set-fmt-video=width=" + frameWidth + ",height="
					+ frameHeight + ",pixelformat=0 --set-parm=" + fps);
			Runtime.getRuntime().exec("v4l2-ctl -d " + camera + " --set-ctrl=auto_exposure=1,exposure=" + exposure
					+ ",brightness=" + brightness + ",white_balance_automatic=0");
		} catch (IOException e) {
			System.err.println("Error: Unable to run shell command to update settings for camera");
		}
	}

	private static void updateCamera0V4L2() {
		// configMicrosoftCamera(0, CAMERA0_RESOLUTION.width, CAMERA0_RESOLUTION.height,
		// CAMERA0_FPS, 5, -64, 6500);
		configPS3Camera(0, CAMERA0_RESOLUTION.width, CAMERA0_RESOLUTION.height, CAMERA0_FPS, 2, 0);
	}

	private static void printBanner() {
		System.out.println("AluminatiVision\nCopyright (c) 2019 Team 3555\n");
	}

	private static void startMJPEGServers() {
		try {
			server0 = new MJPEGServer(STREAM0_RESOLUTION, 5800, CAMERA0_FPS, STREAM0_FPS);
		} catch (IOException e) {
			System.err.println("Error: Unable to start MJPEG server");
			System.exit(-1);
		}

		server0.setName("MJPEG-Server-0");
		server0.setPriority(Thread.MIN_PRIORITY);
		server0.start();

		System.out.println("MJPEG servers started");
	}

	private static void startCameras() {
		camera0 = new VideoCapture(0);
		if (!camera0.isOpened()) {
			System.err.println("Error: Unable to open camera0");
			System.exit(-1);
		}

		updateCamera0V4L2();

		camera0.set(Videoio.CAP_PROP_FRAME_WIDTH, CAMERA0_RESOLUTION.width);
		camera0.set(Videoio.CAP_PROP_FRAME_HEIGHT, CAMERA0_RESOLUTION.height);
		camera0.set(Videoio.CAP_PROP_FPS, CAMERA0_FPS);

		PipelineConfig config0 = new PipelineConfig();
		config0.pipelineMode = PipelineMode.PROCESSING;
		config0.targetMode = TargetMode.SINGLE;
		config0.thresholdHueMin = 0;
		config0.thresholdHueMax = 180;
		config0.thresholdSaturationMin = 0;
		config0.thresholdSaturationMax = 255;
		config0.thresholdValueMin = 0;
		config0.thresholdValueMax = 255;
		config0.contourAreaMin = 0;
		config0.contourAreaMax = 1;

		ConfigurablePipeline pipeline0 = new ConfigurablePipeline(config0);
		IVisionOutputHandler outputHandler0 = null;
		try {
			outputHandler0 = new UDPVisionOutputHandler("10.35.55.2", 5800, 0);
		} catch (UnknownHostException | SocketException e) {
			System.err.println("Error: Unable to output vision data to robot");
			System.exit(-1);
		}
		loop0 = new VisionLoop(server0, camera0, "camera0", pipeline0, outputHandler0);
		loop0.setName("Vision-Loop-0");
		loop0.setPriority(Thread.MAX_PRIORITY);
		loop0.start();

		try {
			listener0 = new ConfigListener(5800, pipeline0);
		} catch (SocketException e) {
			System.err.println("Error: Unable to start pipeline config listener");
			System.exit(-1);
		}
		listener0.setName("Config-Listener-0");
		listener0.setPriority(Thread.MIN_PRIORITY);
		listener0.start();

		System.out.println("camera0 started");
	}

	public static void main(String[] args) {
		printBanner();
		startMJPEGServers();
		startCameras();

		System.out.println("Initialization complete");

		// Update the camera settings in case V4L2 was not fully initialized when the
		// program started
		while (true) {
			// It is ok to use a simple delay here since the actual timing is not all that
			// important

			updateCamera0V4L2();
			VisionUtil.sleep(1);
		}
	}
}
