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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.aluminati3555.aluminativision.pipeline.ConfigurablePipeline;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.PipelineMode;
import org.aluminati3555.aluminativision.pipeline.PipelineConfig.TargetMode;

/**
 * This class listens on a udp port for a pipeline configuration
 * 
 * @author Caleb Heydon
 */
public class ConfigListener extends Thread {
	private ConfigurablePipeline pipeline;
	
	private DatagramSocket socket;
	
	@Override
	public void run() {
		byte[] buffer = new byte[101];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		while (true) {
			try {
				socket.receive(packet);
				
				ByteArrayInputStream byteInput = new ByteArrayInputStream(buffer);
				DataInputStream input = new DataInputStream(byteInput);
				
				PipelineConfig config = new PipelineConfig();
				config.pipelineMode = input.readBoolean() ? PipelineMode.PROCESSING : PipelineMode.DRIVER;
				
				switch (input.readInt()) {
				default:
				case 0:
					config.targetMode = TargetMode.SINGLE;
					break;
				case 1:
					config.targetMode = TargetMode.DUAL_HORIZONTAL;
					break;
				case 2:
					config.targetMode = TargetMode.DUAL_VERTICAL;
					break;
				}
				
				config.thresholdHueMin = input.readDouble();
				config.thresholdHueMax = input.readDouble();
				config.thresholdSaturationMin = input.readDouble();
				config.thresholdSaturationMax = input.readDouble();
				config.thresholdValueMin = input.readDouble();
				config.thresholdValueMax = input.readDouble();
				
				config.contourAreaMin = input.readDouble();
				config.contourAreaMax = input.readDouble();
				config.contourRatioMin = input.readDouble();
				config.contourRatioMax = input.readDouble();
				config.contourDensityMin = input.readDouble();
				config.contourDensityMax = input.readDouble();
				
				input.close();
				pipeline.setPipelineConfig(config);
			} catch (IOException e) {
				System.err.println("Error: Unable to receive pipeline config");
				continue;
			}
		}
	}
	
	public ConfigListener(int port, ConfigurablePipeline pipeline) throws SocketException {
		this.pipeline = pipeline;
		
		this.socket = new DatagramSocket(port);
	}
}
