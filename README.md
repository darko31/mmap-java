# mmap-java
Example on how to use mmap from a Java program with the help of Java Native Access library

SharedSecrets are deprecated in newer Java versions, use Java 8 or older.

This was tested on 32 bit Arm Cortex A9 arch.
In order to run it, make sure that JNA lib is properly added and defined in Class-Path before running.

# Compiling
Here's an example how to pack the files on the dev PC in a .jar and run it on the target.
In this case, Class-Path is defined in manifest.mf file, and jna.jar is located in the same
directory as the MmapExample.jar.

1) Compile the files
Windows
```
javac -classpath .;jna.jar -g MmapExample.java
```
Linux
```
javac -classpath .:jna.jar -g MmapExample.java
```

2) Pack the files in a .jar
```
jar cvmf manifest.mf MmapExample.jar MmapExample.class MmapExample$LibC.class
```

3) Transfer and run the MmapExample.jar to the target. Make sure that jna.jar is also transfered.
 ```
 java -jar MmapExample.jar
 ```