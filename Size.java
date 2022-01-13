public class Size {
	public static long of (byte b) { return Byte.BYTES; }
	public static long of (short s) { return Short.BYTES; }
	public static long of (int i) { return Integer.BYTES; }
	public static long of (long l) { return Long.BYTES; }
	public static long of (long[] la) { return Long.BYTES * la.length; }
	public static long of (Object ref) { return Long.BYTES; }
}
