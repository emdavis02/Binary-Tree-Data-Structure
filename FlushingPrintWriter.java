import java.io.*;

public class FlushingPrintWriter extends PrintWriter {

	public FlushingPrintWriter (OutputStream stream, boolean flag) {
		super (stream, flag);
	}

	public void write (String sss) {
		super.write (sss);
		flush ();
	}
}
