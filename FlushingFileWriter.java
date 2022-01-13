import java.io.*;

public class FlushingFileWriter extends FileWriter {

	public FlushingFileWriter (String sss) throws IOException {
		super (sss);
	}

	public void write (String sss) {
		try {
			super.write (sss);
			flush ();
		}

		catch (IOException ioe) {
			System.err.println
				("IOException in FlushingFileWriter");
		}
	}
}
