#!/bin/bash

path="/home/chris/Downloads/lwjgl-release-3.1.2-custom/"

javac -cp $path"lwjgl.jar":$path"lwjgl-glfw.jar":$path"lwjgl-opengl.jar":$path"":. $1
