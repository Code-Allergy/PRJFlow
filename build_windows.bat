@echo off

rem Define paths
set "sourceJar=target\PRJFlow-0.0.1.jar"
set "buildDir=build"

rem Create the build directory if it doesn't exist
if not exist "%buildDir%" mkdir "%buildDir%"

rem Copy the fat JAR file to the build directory
copy "%sourceJar%" "%buildDir%"

echo Running jpackage...
jpackage --input "%buildDir%" --name PRJFlow --main-jar PRJFlow-0.0.1.jar --type exe --win-shortcut

rem Clean up: Delete the build directory after packaging
rmdir /s /q "%buildDir%"

echo Packaging complete. Temporary files cleaned up.

