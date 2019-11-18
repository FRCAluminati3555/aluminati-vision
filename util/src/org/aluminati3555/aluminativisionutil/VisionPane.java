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

package org.aluminati3555.aluminativisionutil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.aluminati3555.aluminativisionutil.PipelineConfig.PipelineMode;
import org.aluminati3555.aluminativisionutil.PipelineConfig.TargetMode;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Configuration interface
 * 
 * @author Caleb Heydon
 */
public class VisionPane extends VBox {
	private void sendConfig(PipelineConfig config, String address, int port) throws IOException {
		DatagramSocket socket = new DatagramSocket();

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

		socket.close();
	}

	private boolean validateIP(String ip) {
		if (ip.contains(".")) {
			String[] segments = ip.split("\\.");

			if (segments.length == 4) {
				for (int i = 0; i < segments.length; i++) {
					try {
						Integer.parseInt(segments[i]);
					} catch (NumberFormatException e) {
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	public VisionPane() {
		super(1);
		this.setPadding(new Insets(10, 10, 10, 10));

		this.getChildren().add(new Label("IP Address"));
		TextField ipField = new TextField();
		this.getChildren().add(ipField);

		this.getChildren().add(new Label("Port"));
		TextField portField = new TextField();
		this.getChildren().add(portField);

		this.getChildren().add(new Label("Pipeline mode"));
		ComboBox<String> pipelineModeField = new ComboBox<String>();
		pipelineModeField.getItems().add("DRIVER");
		pipelineModeField.getItems().add("PROCESSING");
		this.getChildren().add(pipelineModeField);

		this.getChildren().add(new Label("Target mode"));
		ComboBox<String> targetModeField = new ComboBox<String>();
		targetModeField.getItems().add("SINGLE");
		targetModeField.getItems().add("DUAL_HORIZONTAL");
		targetModeField.getItems().add("DUAL_VERTICAL");
		this.getChildren().add(targetModeField);

		this.getChildren().add(new Label("Hue"));
		Slider hueMinField = new Slider();
		hueMinField.setMin(0);
		hueMinField.setMax(180);
		hueMinField.setShowTickLabels(true);
		hueMinField.setValue(0);
		this.getChildren().add(hueMinField);
		Slider hueMaxField = new Slider();
		hueMaxField.setMin(0);
		hueMaxField.setMax(180);
		hueMaxField.setShowTickLabels(true);
		hueMaxField.setValue(180);
		this.getChildren().add(hueMaxField);

		this.getChildren().add(new Label("Saturation"));
		Slider saturationMinField = new Slider();
		saturationMinField.setMin(0);
		saturationMinField.setMax(255);
		saturationMinField.setShowTickLabels(true);
		saturationMinField.setValue(0);
		this.getChildren().add(saturationMinField);
		Slider saturationMaxField = new Slider();
		saturationMaxField.setMin(0);
		saturationMaxField.setMax(255);
		saturationMaxField.setShowTickLabels(true);
		saturationMaxField.setValue(255);
		this.getChildren().add(saturationMaxField);

		this.getChildren().add(new Label("Value"));
		Slider valueMinField = new Slider();
		valueMinField.setMin(0);
		valueMinField.setMax(255);
		valueMinField.setShowTickLabels(true);
		valueMinField.setValue(0);
		this.getChildren().add(valueMinField);
		Slider valueMaxField = new Slider();
		valueMaxField.setMin(0);
		valueMaxField.setMax(255);
		valueMaxField.setShowTickLabels(true);
		valueMaxField.setValue(255);
		this.getChildren().add(valueMaxField);

		this.getChildren().add(new Label("Area"));
		Slider areaMinField = new Slider();
		areaMinField.setMin(0);
		areaMinField.setMax(1);
		areaMinField.setShowTickLabels(true);
		areaMinField.setValue(0);
		this.getChildren().add(areaMinField);
		Slider areaMaxField = new Slider();
		areaMaxField.setMin(0);
		areaMaxField.setMax(1);
		areaMaxField.setShowTickLabels(true);
		areaMaxField.setValue(1);
		this.getChildren().add(areaMaxField);

		this.getChildren().add(new Label("Ratio"));
		Slider ratioMinField = new Slider();
		ratioMinField.setMin(0);
		ratioMinField.setMax(10);
		ratioMinField.setShowTickLabels(true);
		ratioMinField.setValue(0);
		this.getChildren().add(ratioMinField);
		Slider ratioMaxField = new Slider();
		ratioMaxField.setMin(0);
		ratioMaxField.setMax(10);
		ratioMaxField.setShowTickLabels(true);
		ratioMaxField.setValue(10);
		this.getChildren().add(ratioMaxField);

		this.getChildren().add(new Label("Density"));
		Slider densityMinField = new Slider();
		densityMinField.setMin(0);
		densityMinField.setMax(1);
		densityMinField.setShowTickLabels(true);
		densityMinField.setValue(0);
		this.getChildren().add(densityMinField);
		Slider densityMaxField = new Slider();
		densityMaxField.setMin(0);
		densityMaxField.setMax(1);
		densityMaxField.setShowTickLabels(true);
		densityMaxField.setValue(1);
		this.getChildren().add(densityMaxField);

		Button displayButton = new Button("Display Values");
		displayButton.setOnMouseClicked((e) -> {
			new OutputWindow("VALUES:\n\tHue min:\t\t\t" + Math.round(hueMinField.getValue()) + "\n\tHue max:\t\t\t"
					+ Math.round(hueMaxField.getValue()) + "\n\tSaturation min:\t"
					+ Math.round(saturationMinField.getValue()) + "\n\tSaturation max:\t"
					+ Math.round(saturationMaxField.getValue()) + "\n\tValue min:\t\t"
					+ Math.round(valueMinField.getValue()) + "\n\tValue max:\t\t" + Math.round(valueMaxField.getValue())
					+ "\n\tArea min:\t\t\t" + areaMinField.getValue() + "\n\tArea max:\t\t\t" + areaMaxField.getValue()
					+ "\n\tRatio min:\t\t" + ratioMinField.getValue() + "\n\tRatio max:\t\t" + ratioMaxField.getValue()
					+ "\n\tDensity min:\t\t" + densityMinField.getValue() + "\n\tDensity max:\t\t"
					+ densityMaxField.getValue()).show();
		});
		this.getChildren().add(displayButton);

		InvalidationListener listener = new InvalidationListener() {
			public void invalidated(Observable arg) {
				String ip = ipField.getText();
				int port;
				try {
					port = Integer.parseInt(portField.getText());
				} catch (NumberFormatException e) {
					return;
				}

				if (!validateIP(ip)) {
					return;
				}

				PipelineConfig config = new PipelineConfig();

				String pipelineMode = pipelineModeField.getValue();
				if (pipelineMode == null) {
					pipelineMode = "DRIVER";
				}

				switch (pipelineMode) {
				default:
				case "DRIVER":
					config.pipelineMode = PipelineMode.DRIVER;
					break;
				case "PROCESSING":
					config.pipelineMode = PipelineMode.PROCESSING;
					break;
				}

				String targetMode = targetModeField.getValue();
				if (targetMode == null) {
					targetMode = "SINGLE";
				}

				switch (targetMode) {
				default:
				case "SINGLE":
					config.targetMode = TargetMode.SINGLE;
					break;
				case "DUAL_HORIZONTAL":
					config.targetMode = TargetMode.DUAL_HORIZONTAL;
					break;
				case "DUAL_VERTICAL":
					config.targetMode = TargetMode.DUAL_VERTICAL;
					break;
				}

				config.thresholdHueMin = Math.round(hueMinField.getValue());
				config.thresholdHueMax = Math.round(hueMaxField.getValue());
				config.thresholdSaturationMin = Math.round(saturationMinField.getValue());
				config.thresholdSaturationMax = Math.round(saturationMaxField.getValue());
				config.thresholdValueMin = Math.round(valueMinField.getValue());
				config.thresholdValueMax = Math.round(valueMaxField.getValue());
				config.contourAreaMin = areaMinField.getValue();
				config.contourAreaMax = areaMaxField.getValue();
				config.contourRatioMin = ratioMinField.getValue();
				config.contourRatioMax = ratioMaxField.getValue();
				config.contourDensityMin = densityMinField.getValue();
				config.contourDensityMax = densityMaxField.getValue();

				try {
					sendConfig(config, ip, port);
				} catch (IOException e) {

				}
			}
		};
		ipField.textProperty().addListener(listener);
		portField.textProperty().addListener(listener);
		pipelineModeField.valueProperty().addListener(listener);
		targetModeField.valueProperty().addListener(listener);
		hueMinField.valueProperty().addListener(listener);
		hueMaxField.valueProperty().addListener(listener);
		saturationMinField.valueProperty().addListener(listener);
		saturationMaxField.valueProperty().addListener(listener);
		valueMinField.valueProperty().addListener(listener);
		valueMaxField.valueProperty().addListener(listener);
		areaMinField.valueProperty().addListener(listener);
		areaMaxField.valueProperty().addListener(listener);
		ratioMinField.valueProperty().addListener(listener);
		ratioMaxField.valueProperty().addListener(listener);
		densityMinField.valueProperty().addListener(listener);
		densityMaxField.valueProperty().addListener(listener);
	}
}
