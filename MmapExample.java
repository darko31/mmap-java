/* Example how to use mmap from Java program with the help of Java Native Access library */
/* https://github.com/java-native-access/jna */
/* Tested on 32 bit ARM Cortex A9 arch */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.LastErrorException;

/* Note that SharedSecrets is deprecated and may only  be used in Java 8 or older versions */

import sun.misc.SharedSecrets;

public class MmapExample {
	
	/* Declare interface to the C functions, loading "c" works on POSIX systems only */

	public interface LibC extends Library {
	    LibC libc = (LibC) Native.loadLibrary("c", LibC.class);
		
		/* 
			Special care should be taken on how length and offset are declared
			If long values are used it will fail on 32 bit arch
		*/
	    Pointer mmap(Pointer address, int length, 
	                 int protect, int flags, int fd, 
	                 int offset) throws LastErrorException;
					 
		/* Add printf for fun */
	    void printf(String format, Object... args);
	}

	public static final int MMAP_SIZE = 16777216;	//16MB

	/* These values are from mman.h */
	public static final int PROT_READ = 0x1;
	public static final int PROT_WRITE = 0x2;
	public static final int MAP_SHARED = 0x1;

	public static void main(String[] args) throws IOException {
		
		LibC.libc.printf("test printf \n");
		
		/* Tested with udmabuf driver, it can be some other block device */

		File umdabuf_file = new File("/dev/udmabuf0");
		int udmabuf_fd = -1;
		RandomAccessFile randomAccessFile;
		try {
			randomAccessFile = new RandomAccessFile(umdabuf_file, "rw");
	        udmabuf_fd = sun.misc.SharedSecrets.getJavaIOFileDescriptorAccess()
	        			.get(randomAccessFile.getFD());
	        System.out.println( "Udmabuf fd " + udmabuf_fd);
	        

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
       Pointer memory_segment_pointer = null;
       
		if (udmabuf_fd != -1) {
			System.out.println("MMAP_SIZE " + MMAP_SIZE);
			System.out.println("PROT_READ | PROT_WRITE " + (PROT_READ | PROT_WRITE));
			System.out.println("MAP_SHARED " + MAP_SHARED);
			System.out.println("udmabuf_fd " + udmabuf_fd);

			memory_segment_pointer = LibC.libc.mmap(
					null,
					MMAP_SIZE,
					PROT_READ | PROT_WRITE,
					MAP_SHARED,
					udmabuf_fd,
					0
			);
			
			System.out.println("mmaped pointer " + memory_segment_pointer);	
		
			/* Now write or read values thorugh JNA Pointers */
			/* Offsets are in bytes */
			
			memory_segment_pointer.setInt(0, 1);
			System.out.println("set value " + 1);
			
			int value = memory_segment_pointer.getInt(4);
			System.out.println("get value " + value);
			
			memory_segment_pointer.setInt(8, 3);
			System.out.println("set value " + 3);

			LibC.libc.printf("end of program \n");
		}
		else{
			System.out.println("mmap failed!");
		}
	}
}
