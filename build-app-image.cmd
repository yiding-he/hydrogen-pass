rem First use Spring to package it into a jar and then use jpackage to package it into an exe.
set JDK20_HOME=E:\JDK\jdk-20.0.1
%JDK20_HOME%\bin\jpackage.exe --name hydrogen-pass --input target\dist --main-jar hydrogen-pass-0.3.0-SNAPSHOT.jar --type app-image --dest target
