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

package org.aluminati3555.aluminativision.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

import org.aluminati3555.aluminativision.CameraResolution;
import org.aluminati3555.aluminativision.util.VisionUtil;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * This is a simple mjpeg server for streaming video to the driver station
 * 
 * @author Caleb Heydon
 */
public class MJPEGServer extends Thread {
	private static final String SERVER_NAME = "MJPEGServer";
	private static final int STREAM_COMPRESSION = 50;

	/**
	 * Compresses an image
	 * 
	 * @param mat
	 */
	private static void compress(Mat mat) {
		MatOfByte matOfByte = new MatOfByte();
		MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, STREAM_COMPRESSION);
		Imgcodecs.imencode(".jpg", mat, matOfByte, params);
		params.release();

		Mat output = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
		output.copyTo(mat);
		output.release();
	}

	/**
	 * Converts a mat to a compressed jpeg
	 * 
	 * @param mat
	 * @return
	 */
	private static byte[] getJPEGBytes(Mat mat) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", mat, matOfByte);

		byte[] buffer = matOfByte.toArray();
		matOfByte.release();

		return buffer;
	}

	private CameraResolution resolution;
	private ServerSocket serverSocket;
	private Vector<ClientHandler> clients;
	private DecimalFormat decimalFormat;

	private boolean skip;
	private int interval;
	private long counter;

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				ClientHandler client = new ClientHandler(socket);
				client.start();

				clients.add(client);
			} catch (IOException e) {
				System.err.println("Warning: Socket error");
				continue;
			}
		}
	}

	/**
	 * Sends a frame to the clients
	 * 
	 * @param frame
	 */
	public synchronized void sendFrame(Mat frame, double fps) {
		if (skip && counter++ % interval != 0) {
			return;
		}
		
		Mat workingFrame = new Mat();
		frame.copyTo(workingFrame);

		CompletableFuture.runAsync(() -> {
			VisionUtil.resize(workingFrame, resolution.width, resolution.height);

			double outputFPS = Double.parseDouble(decimalFormat.format(fps));
			Imgproc.putText(workingFrame, outputFPS + " FPS", new Point(5, 10), 0, 0.25, new Scalar(0, 255, 0));

			// See this link for crosshair
			// https://answers.opencv.org/question/22960/how-to-draw-crosshairsmarked-axes/

			Imgproc.line(workingFrame, new Point(workingFrame.width() / 2.0, workingFrame.height() / 2.0 - 8),
					new Point(workingFrame.width() / 2.0, workingFrame.height() / 2.0 + 8), new Scalar(0, 255, 0), 2);

			Imgproc.line(workingFrame, new Point(workingFrame.width() / 2.0 - 8, workingFrame.height() / 2.0),
					new Point(workingFrame.width() / 2.0 + 8, workingFrame.height() / 2.0), new Scalar(0, 255, 0), 2);

			compress(workingFrame);

			byte[] buffer = getJPEGBytes(workingFrame);
			workingFrame.release();

			for (int i = 0; i < clients.size(); i++) {
				try {
					clients.get(i).sendFrame(buffer);
				} catch (IOException e) {
					clients.remove(i);
					i--;
				}
			}
		});
	}

	public MJPEGServer(CameraResolution resolution, int port) throws IOException {
		this.resolution = resolution;
		serverSocket = new ServerSocket(port);
		clients = new Vector<ClientHandler>();
		decimalFormat = new DecimalFormat("###.#");

		skip = false;
	}

	public MJPEGServer(CameraResolution resolution, int port, int cameraFPS, int streamFPS) throws IOException {
		this.resolution = resolution;
		serverSocket = new ServerSocket(port);
		clients = new Vector<ClientHandler>();
		decimalFormat = new DecimalFormat("###.#");

		skip = true;
		interval = cameraFPS / streamFPS;
		counter = 0l;
	}

	private class ClientHandler {
		private OutputStream outputStream;

		/**
		 * Starts the initial response to the client
		 * 
		 * @throws IOException
		 */
		public void start() throws IOException {
			outputStream.write(("HTTP/1.0 200 OK\r\nServer: " + SERVER_NAME
					+ "\r\nContent-Type: multipart/x-mixed-replace; boundary=--BoundaryString\r\n\r\n").getBytes());
			outputStream.flush();
		}

		/**
		 * Sends a frame after the response has been started
		 * 
		 * @param frame
		 * @throws IOException
		 */
		public void sendFrame(byte[] frame) throws IOException {
			outputStream.write(
					("--BoundaryString\r\nContent-Type: image/jpeg\r\nContent-Length: " + frame.length + "\r\n\r\n")
							.getBytes());
			outputStream.write(frame);

			outputStream.flush();
		}

		public ClientHandler(Socket socket) throws IOException {
			this.outputStream = socket.getOutputStream();
		}
	}
}
