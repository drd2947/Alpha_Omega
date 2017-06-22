#!/bin/bash

path="/home/chris/Downloads/lwjgl-release-3.1.2-custom/"

java -cp $path"lwjgl.jar":$path"lwjgl-glfw.jar":$path"lwjgl-opengl.jar":$path"lwjgl-natives-linux.jar":$path"lwjgl-glfw-natives-linux.jar":$path"lwjgl-opengl-natives-linux.jar":.  $1
