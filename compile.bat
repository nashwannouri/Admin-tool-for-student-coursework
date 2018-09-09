@ECHO OFF

if not exist "bin/" mkdir "bin/"

set CLASSPATH=.
set CLASSPATH=%CLASSPATH%;bin/
set CLASSPATH=%CLASSPATH%;lib/javax.mail.jar
set CLASSPATH=%CLASSPATH%;lib/jcommon-1.0.23.jar
set CLASSPATH=%CLASSPATH%;lib/jfreechart-1.0.19.jar
set CLASSPATH=%CLASSPATH%;lib/jfreechart-1.0.19-experimental.jar
set CLASSPATH=%CLASSPATH%;lib/jfreechart-1.0.19-swt.jar
set CLASSPATH=%CLASSPATH%;lib/jfxrt.jar
set CLASSPATH=%CLASSPATH%;lib/StudentData.jar
set CLASSPATH=%CLASSPATH%;lib/itextpdf-5.5.5.jar

javac -sourcepath src/ -d bin/ src/*.java

echo Source files compiled

@PAUSE