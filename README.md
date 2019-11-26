# AluminatiVision

AluminatiVision is a template for vision processing: The AluminatiVision.java file can be customized by the user for a wide range of vision processing tasks.  It uses OpenCV 4 for fast image processing and has a system for making custom pipelines using a variety of parameters (HSV and contour filters).  The application is capable of hosting MJPEG servers for the cameras.  Multiple cameras can be used.  The pipelines are known to run very quickly on the Raspberry Pi 4 B+ (~2 ms).  AluminatiVision should work well with very little modification (such as setting IP addresses and adjusting HSV and contour filters).  More complicated modifications are required to add more cameras.

When all of the components are compiled/added to the source tree, the directory structure should look like this:
```
AluminatiVision
              |-jni
              |-jre
              |-AluminatiVision.sh
              |-AluminatiVision.jar
```

Remember to modify the file permissions of the scripts to make them executable.

# Features
 - Highly customizable
 - Fast custom pipelines
 - Supports multiple cameras
 - Pipelines can be swapped on the fly
 - Inexpensive hardware (Raspberry Pi 3 B+, The RPi 4 should work even better)
 - Remote tuning over UDP
 - Target info over UDP
 - Single and dual target modes
 - Library for robot code (Java)
 
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
The root of this project should be copied to /home/pi on a Raspberry Pi.  The executable should be at /home/pi/AluminatiVision/AluminatiVision.jar.  To run AluminatiVision at startup, create a systemd service to run the startup script (/home/pi/AluminatiVision/AluminatiVision.sh).

# Note about the JVM
This project currently does not contain a JVM.  It is necessary to add one before running the vision system.  We recommend using the bellsoft JRE 13.  It should be placed so that the java executable is at /home/pi/AluminatiVision/jre/bin/java.

Download the JRE for the Raspberry Pi from this link: https://download.bell-sw.com/java/13.0.1/bellsoft-jre13.0.1-linux-arm32-vfp-hflt.tar.gz

# Note about Windows
AluminatiVision uses the V4L2 linux driver to control the cameras, so a different driver would be needed for the software to run smoothly on windows (in addition to some path adjustments).

# Streaming cameras to the default dashboard
Since AluminatiVision does not contain the WPILib libraries, the mjpeg stream will need to be published to network tables from the robot code.  This not very difficult.  You need to add a string array entry (with only one string) to /CameraPublisher/<stream_name>.  The value of the string in the array should be like this:
```
mjpg:http://<ip_or_hostname_of_vision_system>:<port>

Example:
mjpg:http://AluminatiVision-3555:5801
```

# TODO
 - Target intersection for the dual target modes.  We have attempted to implement this.  We were not able to get it to work reliably, so we removed it.  Suggestions on how to do this are welcome.

# Notice
The scripts have been collected from various projects and are released under different licenses.  Here is a list of links to these projects:

 - https://github.com/diabetlum/PiShrink
 - https://gist.github.com/willprice/abe456f5f74aa95d7e0bb81d5a710b60
 - https://github.com/adafruit/Raspberry-Pi-Installer-Scripts/blob/master/read-only-fs.sh
