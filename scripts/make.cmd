@echo off
cd ..
javac -d classes src/com/michaelbommer/darktower/*.java
jar cvfm scripts/darktower.jar scripts/manifest -C classes .
jar cvfM scripts/darktower-bin.zip -C scripts darktower.cmd -C scripts darktower.jar -C scripts license.txt -C scripts readme.txt
jar cvfM scripts/darktower-src.zip scripts/license.txt scripts/readme.txt scripts/make.cmd scripts/darktower.cmd scripts/manifest src/com/michaelbommer/darktower/*.java classes/com/michaelbommer/darktower/*.class classes/com/michaelbommer/darktower/audio/*.* classes/com/michaelbommer/darktower/images/*.*
jar cvfM scripts/darktower-doc.zip docs/*.* docs/images/*.*
