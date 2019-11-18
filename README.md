# AluminatiVision

AluminatiVision is a template for vision processing: The AluminatiVision.java file can be customized by the user for a wide range of vision processing tasks.  It uses OpenCV 4 for fast image processing and has a system for making custom pipelines using a variety of parameters (HSV and contour filters).  The application is capable of hosting MJPEG servers for the cameras.  Multiple cameras can be used.  The pipelines are known to run very quickly on the Raspberry Pi 3 B+ (~4 ms).

# Features
 - Highly customizable
 - Fast custom pipelines
 - Supports multiple cameras
 - Pipelines can be swapped on the fly
 - Inexpensive hardware (Raspberry Pi 3 B+, The RPi 4 should work even better)
 - Remote tuning
 
# Remote tuning
AluminatiVision supports tuning via the util application (needs Java 13).  This allows the HSV and contour filter values to be found quickly and accurately.  Note that you will need to the MJPEG stream in a web browser for tuning and that the values will need to be copied into the vision program itself (in AluminatiVision.java).  A binary of this tool will be included with releases.
 
# Dependencies
AluminatiVision only has one dependency OpenCV 4.  Scripts are included for installing the dependencies for OpenCV and for building OpenCV.  If you don't want to setup extra swap space and spend 2 hours compiling, you can use the prebuild OpenCV Java wrapper in the repository.  Please note that the compile options for OpenCV are optimized for the Raspberry Pi and should not be changed.

# Filesystem corruption
Since the vision system will be turned off just by cutting the power, it is important the the filesystem does not become corrupt.  The best way to do this is to make the filesystem read-only, but there needs to be a way to switch back and forth between read-only mode and read-write mode so that new programs can be deployed.  The scripts folder contains a script for the initial setup of a read-only filesystem.

To set the filesystem to rw again, run this:
```
 sudo mount -o remount,rw /
```
This sets the root partition to rw.

To set the filesystem to ro run this command:
```
 sudo mount -o remount,ro /
```

# Target info
The template contains a very basic UDP protocol to transfer the data to the robot, but the user is free to implement any protocol.  AluminatiVision currently does not include any WPILib functions mainly for simplicity.  So, network tables may be more difficult to get going.

# File locations
The root of this project should be copied to /home/pi on a Raspberry Pi.  The executable should be at /home/pi/AluminatiVision/AluminatiVision.jar.  To run AluminatiVision at startup, create a systemd service to run the startup script (/home/pi/AluminatiVision/AluminatiVision.sh).

# Note about the JVM
This project currently does not contain a JVM.  It is necessary to add one before running the vision system.  We recommend using the bellsoft JRE 13.  It should be placed so that the java executable is at /home/pi/AluminatiVision/jre/bin/java.

# TODO
 - Target intersection for the dual target modes.  We have attempted to implement this, but we were not able to get it to work reliably, so we removed it.  Suggestions on how to do this are welcome.

# Notice
The scripts have been collected from various projects and are released under different licenses.  Here is a list of links to these projects:

 - https://github.com/diabetlum/PiShrink
 - https://gist.github.com/willprice/abe456f5f74aa95d7e0bb81d5a710b60
 - https://github.com/adafruit/Raspberry-Pi-Installer-Scripts/blob/master/read-only-fs.sh
