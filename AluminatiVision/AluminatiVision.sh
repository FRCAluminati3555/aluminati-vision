#!/bin/sh

LD_LIBRARY_PATH=/home/pi/AluminatiVision/jni chrt --rr 99 /home/pi/AluminatiVision/jre/bin/java -Xcomp -Xmx128M -XX:+UseG1GC -XX:MaxGCPauseMillis=10 -XX:InitiatingHeapOccupancyPercent=50 -jar /home/pi/AluminatiVision/AluminatiVision.jar
