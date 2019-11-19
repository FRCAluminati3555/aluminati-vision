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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Sends the vision data as UDP packets
 * 
 * @author Caleb Heydon
 */
public class UDPVisionOutputHandler implements IVisionOutputHandler {
	private InetAddress address;
	private int port;
	private int camera;

	private DatagramSocket socket;

	public void update(VisionData data) throws IOException {
		// Add camera number
		data.camera = camera;

		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(byteOutput);

		output.writeInt(data.camera);
		output.writeDouble(data.fps);
		output.writeDouble(data.timestamp);
		output.writeDouble(data.processingLatency);
		output.writeBoolean(data.hasTarget);
		output.writeDouble(data.x);
		output.writeDouble(data.y);
		output.writeDouble(data.targetWidth);
		output.writeDouble(data.targetHeight);
		output.writeDouble(data.targetArea);

		output.close();

		DatagramPacket packet = new DatagramPacket(byteOutput.toByteArray(), byteOutput.toByteArray().length);
		packet.setAddress(address);
		packet.setPort(port);
		
		socket.send(packet);
	}

	public UDPVisionOutputHandler(String address, int port, int camera) throws UnknownHostException, SocketException {
		this.address = InetAddress.getByName(address);
		this.port = port;
		this.camera = camera;

		this.socket = new DatagramSocket();
	}
}
