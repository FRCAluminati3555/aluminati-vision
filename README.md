# AluminatiVision

AluminatiVision is a template for vision processing: The AluminatiVision.java file can be customized by the user for a wide range of vision processing tasks.  It uses OpenCV 4 for fast image processing and has a system for making custom pipelines using a variety of parameters (HSV and contour filters).  The application is capable of hosting MJPEG servers for the cameras.  Multiple cameras can be used.  The pipelines are known to run very quickly on the Raspberry Pi 4 B+ (~2-3 ms).  AluminatiVision should work well with very little modification (such as setting IP addresses and adjusting HSV and contour filters).  More complicated modifications are required to add more cameras.

When all of the components are compiled/added to the source tree, the directory structure should look like this:
```
AluminatiVision
              |-jni
              |-jre
              |-AluminatiVision.sh
              |-AluminatiVision.jar
```

Remember to modify the file permissions of the scripts to make them executable.

![example-2016](https://github.com/FRCAluminati3555/aluminati-vision/raw/master/images/example-2016.png)

Original image from GRIP

![example-2014](https://github.com/FRCAluminati3555/aluminati-vision/raw/master/images/example-2014.png)

Original image from WPILib documentation

# Features
 - Highly customizable
 - Fast custom pipelines
 - HSV thresholding and contour filtering
 - Supports multiple cameras
 - Pipelines can be swapped on the fly
 - Inexpensive hardware (Raspberry Pi 4 B and whatever cameras you want)
 - Remote tuning over UDP
 - Target info over UDP
 - Single and dual target modes
 - Library for robot code (Java)
 - Fast garbage collection (usually under 10 ms)
 - All bytecode is translated to machine language immediately at runtime
 - Runs as a real-time process on Linux
 - Camera stream FPS limiting
 
# TODO
 - Target intersection for the dual target modes.  We have attempted to implement this.  We were not able to get it to work reliably, so we removed it.  Suggestions on how to do this are welcome.
 
# Default program
The default AluminatiVision has an untuned HSV threshold and contour filters and attempts to track one target and send the target info to 10.35.55.2.

# Cameras
Any USB camera that supports V4L2 and a 320x240 resolution should work with AluminatiVision.  Currently, the highest tested FPS is 187.

Supported cameras:
```
PS3 Eye Camera (187 FPS, The maximum FPS is 187 not 120 as it says in the documentation)
Microsoft Lifecam HD-3000 (30 FPS)
```
The default program is setup for the PS3 camera, but it can easily be changed to work with the Microsoft Lifecam.
 
# Remote tuning
AluminatiVision supports tuning via the util application over UDP.  This allows the HSV and contour filter values to be found quickly and accurately while calibrating on the field.  Note that you will need to the MJPEG stream in a web browser for tuning and that the values will need to be copied into the vision program itself (in AluminatiVision.java).  A binary of this tool will be included with releases.  You will need to use a JRE with JavaFX modules, such as Bellsoft's Java 13 JRE.
 
# Dependencies
AluminatiVision only has one dependency OpenCV 4.  Scripts are included for installing the dependencies for OpenCV and for building OpenCV.  If you don't want to setup extra swap space and spend 2 hours compiling, you can use the prebuild OpenCV Java wrapper in the repository.  Please note that the compile options for OpenCV are optimized for the Raspberry Pi and should not be changed.

# Filesystem corruption
Since the vision system will be turned off just by cutting the power, it is important the the filesystem does not become corrupt.  The best way to do this is to make the filesystem read-only, but there needs to be a way to switch back and forth between read-only mode and read-write mode so that new programs can be deployed.  The scripts folder contains a script for the initial setup of a read-only filesystem.

To set the filesystem to rw again, run this:
```
 sudo mount -o remount,rw /
```
To set the filesystem to ro run this command:
```
 sudo mount -o remount,ro /
```

# Target info
The template contains a very basic UDP protocol to transfer the data to the robot, but the user is free to implement any protocol.  AluminatiVision currently does not include any WPILib functions mainly for simplicity.  So, network tables may be more difficult to get working.

# File locations
The root of this project should be copied to /home/pi on a Raspberry Pi.  The executable should be at /home/pi/AluminatiVision/AluminatiVision.jar.  To run AluminatiVision at startup, create a systemd service to run the startup script (/home/pi/AluminatiVision/AluminatiVision.sh).  The Booster program should have a similar setup in the /home/pi/Booster folder.

# Note about the JVM
This project currently does not contain a JVM.  It is necessary to add one before running the vision system.  We recommend using the Bellsoft JRE 13.  It should be placed so that the java executable is at /home/pi/AluminatiVision/jre/bin/java.

Download the JRE for the Raspberry Pi from this link: https://download.bell-sw.com/java/13.0.1/bellsoft-jre13.0.1-linux-arm32-vfp-hflt.tar.gz

# Note about Windows
AluminatiVision uses the V4L2 linux driver to control the cameras, so a different driver would be needed for the software to run smoothly on windows (in addition to some path adjustments).

# Streaming cameras to the default dashboard
Since AluminatiVision does not contain the WPILib libraries, the mjpeg stream will need to be published to network tables from the robot code.  This not very difficult.  You need to add a string array entry (with only one string) to /CameraPublisher/<stream_name>.  The value of the string in the array should be like this:
```
mjpg:http://<ip_or_hostname_of_vision_system>:<port>

Example:
mjpg:http://aluminativision:5800
```

# Running at lower FPS
Running at an FPS lower than 187 may cause the pipeline latency to increase because the Rasperry Pi will lower its CPU frequency.

# Setup steps (using premade image)
1. Flash premade image to an SD card.
2. Connect the Raspberry Pi to your computer with ethernet, and boot from the SD card.
3. Using the hostname aluminativision.local, log in with SSH and SCP (username: pi, password: AluminatiVision).
4. Set the filesystem to read-write.
5. Upload your custom vision program based on the the code in /home/pi/AluminatiVision/src to /home/pi/AluminatiVision/AluminatiVision.jar.
6. Set the filesystem to read-only, and reboot.

# Setup steps (from scratch)
1. Flash Raspbian Buster to an SD card.
2. Add empty SSH file to "boot" partition.
3. Disable wifi and bluetooth (see other section).
4. Connect the Raspberry Pi to your computer with ethernet (bridged to Internet), and boot from the SD card.
5. Using the hostname raspberrypi.local, log in with SSH and SCP (username: pi, password: raspberry).
6. Upload the repository to the home directory so that the root of this project is located at /home/pi.
7. Upload a Java 13 JRE (Bellsoft JRE recommended) to /home/pi/AluminatiVision/jre.
8. Upload your custom vision program to /home/pi/AluminatiVision/AluminatiVision.jar
9. Navigate to /home/pi/scripts, and run the following commands:
```
chmod +x *.sh
./install-deps.sh
./install-vision-service.sh
sudo chmod -x /etc/rc.local
sudo ./read-only-fs.sh
```
10. Reboot

# Library
The lib/src directory contains Java code for communicating with AluminatiVision from the robot code.  The code is simple and should be able to be replicated in other languages (C++) with little difficulty.

# Building OpenCV
A prebuild OpenCV library comes with AluminatiVision (in the repository even).  However, if a new version becomes available and we have not gotten around to building it and publishing it here, the project also contains all of the scripts necessary to build your own OpenCV library for Java.

1. Complete the steps in Setup steps (from scratch) up through ./install-dep.sh.
2. Setup swapspace if you have less than 4 GB of memory (Instructions here https://gist.github.com/willprice/abe456f5f74aa95d7e0bb81d5a710b60.  You will also need to set the max swap size in /etc/dphys-swapfile).
3. Run the following commands
```
./download-opencv.sh
./build-opencv.sh
cd ../opencv/opencv-<version>/build
sudo make install
```
4. Find the OpenCV Java library file somwhere in /usr/local/share/java/opencv4.
5. Copy the library to /home/pi/AluminatiVision/jni.
6. Remove the /home/pi/opencv folder.
7. Navigate back to the scripts directory, and continue with the steps in the setup instructions.

# Disabling wifi and bluetooth
FRC requires that all wireless communcation be disabled on the Raspberry Pi to use it in competition.  Add these lines to /boot/config.txt before the line [pi4] to disable both wifi and bluetooth.
```
dtoverlay=disable-wifi
dtoverlay=disable-bt
```
https://raspberrypi.stackexchange.com/questions/43720/disable-wifi-wlan0-on-pi-3

# Notice
The scripts have been collected from various projects and are released under different licenses.  Here is a list of links to these projects:

 - https://github.com/Drewsif/PiShrink
 - https://gist.github.com/willprice/abe456f5f74aa95d7e0bb81d5a710b60
 - https://github.com/adafruit/Raspberry-Pi-Installer-Scripts/blob/master/read-only-fs.sh
