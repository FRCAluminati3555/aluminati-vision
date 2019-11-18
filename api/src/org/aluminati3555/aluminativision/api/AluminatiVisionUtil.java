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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.aluminati3555.aluminativision.api.PipelineConfig.PipelineMode;
import org.aluminati3555.aluminativision.api.PipelineConfig.TargetMode;

/**
 * Utility class for vision system
 * 
 * @author Caleb Heydon
 */
public class AluminatiVisionUtil {
	/**
	 * Sends a pipeline config to the vision system
	 * 
	 * @param socket
	 * @param config
	 * @param address
	 * @param port
	 * @throws IOException
	 */
	public static void sendConfig(DatagramSocket socket, PipelineConfig config, String address, int port)
			throws IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(byteOutput);

		output.writeBoolean(config.pipelineMode == PipelineMode.PROCESSING);

		int mode;
		if (config.targetMode == TargetMode.SINGLE) {
			mode = 0;
		} else if (config.targetMode == TargetMode.DUAL_HORIZONTAL) {
			mode = 1;
		} else {
			mode = 2;
		}
		output.writeInt(mode);

		output.writeDouble(config.thresholdHueMin);
		output.writeDouble(config.thresholdHueMax);
		output.writeDouble(config.thresholdSaturationMin);
		output.writeDouble(config.thresholdSaturationMax);
		output.writeDouble(config.thresholdValueMin);
		output.writeDouble(config.thresholdValueMax);

		output.writeDouble(config.contourAreaMin);
		output.writeDouble(config.contourAreaMax);
		output.writeDouble(config.contourRatioMin);
		output.writeDouble(config.contourRatioMax);
		output.writeDouble(config.contourDensityMin);
		output.writeDouble(config.contourDensityMax);

		output.close();

		DatagramPacket packet = new DatagramPacket(byteOutput.toByteArray(), byteOutput.toByteArray().length);
		packet.setAddress(InetAddress.getByName(address));
		packet.setPort(port);
		socket.send(packet);
	}
}
