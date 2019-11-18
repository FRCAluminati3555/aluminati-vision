# AluminatiVision

AluminatiVision is a template for vision processing: The AluminatiVision.java file can be customized by the user for a wide range of vision processing tasks.  It uses OpenCV 4 for fast image processing and has a system for making custom pipelines using a variety of parameters (HSV and contour filters).  The application is capable of hosting MJPEG servers for the cameras.  Multiple cameras can be used.  The pipelines are known to run very quickly on the Raspberry Pi 3 B+ (~4 ms).

# Features
 - Highly customizable
 - Fast custom pipelines
 - Supports multiple cameras
 - Pipelines can be swapped on the fly
 - Inexpensive hardware (Raspberry Pi 3 B+, The RPi 4 should work even better)
 
# Dependencies
AluminatiVision only has one dependency OpenCV 4.  Scripts are included for installing the dependencies for OpenCV and for building OpenCV.  If you don't want to setup extra swap space and spend 2 hours compiling, you can use the prebuild OpenCV Java wrapper in the repository.  Please note that the compile options for OpenCV are optimized for the Raspberry Pi and should not be changed.
