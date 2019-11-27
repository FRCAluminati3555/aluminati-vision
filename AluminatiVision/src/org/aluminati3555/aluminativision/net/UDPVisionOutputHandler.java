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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Sends the vision data as UDP packets
 * 
 * @author Caleb Heydon
 */
public class UDPVisionOutputHandler implements IVisionOutputHandler {
	private int camera;

	private DatagramSocket socket;

	private ByteBuffer buffer;
	private DatagramPacket packet;

	/**
	 * Sends the vision data to the robot
	 */
	public synchronized void update(VisionData data) throws IOException {
		// Add camera number
		data.camera = camera;

		buffer.putInt(0, data.camera);
		buffer.putDouble(4, data.fps);
		buffer.putDouble(12, data.timestamp);
		buffer.putDouble(20, data.processingLatency);
		buffer.putInt(28, data.hasTarget ? 1 : 0);
		buffer.array()[28] = (byte) (data.hasTarget ? 1 : 0);
		buffer.putDouble(29, data.x);
		buffer.putDouble(37, data.y);
		buffer.putDouble(45, data.targetWidth);
		buffer.putDouble(53, data.targetHeight);
		buffer.putDouble(61, data.targetArea);

		socket.send(packet);
	}

	public UDPVisionOutputHandler(String address, int port, int camera) throws UnknownHostException, SocketException {
		this.camera = camera;

		this.socket = new DatagramSocket();

		byte[] bufferBytes = new byte[69];
		this.buffer = ByteBuffer.wrap(bufferBytes);
		this.packet = new DatagramPacket(bufferBytes, bufferBytes.length);

		this.packet.setAddress(InetAddress.getByName(address));
		this.packet.setPort(port);
	}
}
