import java.io.*;

public abstract class Base {

        public Base copy () {
                return null;
        }
        public String getName () {
                return null;
        }
        public String getTrimName () {
                return null;
        }
        public boolean isLessThan (Base base) {
		System.err.println("HERE");
                return true;
        }
	public void jettison () {}
        public void read (RandomAccessFile fio) {}
        public void write (RandomAccessFile fio) {}
}
