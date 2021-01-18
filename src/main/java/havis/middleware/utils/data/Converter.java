package havis.middleware.utils.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements basic convert methods
 */
public class Converter {

	/**
	 * Sets length number of bits from data in bytes starts by offset. Resizes
	 * the bytes byte array, if the sum of offset and length is greater then
	 * count of bits in bytes
	 *
	 * @param bytes
	 *            The source byte array
	 * @param data
	 *            The data
	 * @param offset
	 *            The bit offset
	 * @param length
	 *            The bit length
	 * @return A copy of the resized byte array
	 */
	public static byte[] set(byte[] bytes, byte[] data, int offset, int length) {
		if (offset > -1) {
			if (length > -1) {
				if (offset + length > bytes.length * 8) {
					byte[] tmp = new byte[(offset + length + (8 - (offset + length) % 8) % 8) / 8];
					System.arraycopy(bytes, 0, tmp, 0, bytes.length);
					bytes = tmp;
				}
				if (offset % 8 > 0) {
					byte pow = (byte) (Math.pow(2, 8 - offset % 8) - 1);
					int i = offset / 8;
					bytes[i] = (byte) (((0xFF ^ pow) & (bytes[i] & 0xFF)) + (pow & (data[0] & 0xFF) >> (8 - offset % 8)));
					i++;
					int j = 0;
					while (i < bytes.length) {
						byte b = (byte) ((data[j] & 0xFF) << (offset % 8));
						if (++j < data.length) {
							bytes[i] = (byte) ((b & 0xFF) + (byte) ((data[j] & 0xFF) >> (8 - offset % 8)));
						} else {
							pow = (byte) (Math
									.pow(2, 8 - (offset + length) % 8) - 1);
							bytes[i] = (byte) (((0xFF ^ pow) & (b & 0xFF)) + (byte) (pow & (bytes[i] & 0xFF)));
						}
						i++;
					}
				} else {
					System.arraycopy(data, 0, bytes, offset / 8, length / 8);
					if (length % 8 > 0) {
						short pow = (short) (Math.pow(2, 8 - length % 8) - 1);
						int i = length / 8;
						bytes[i] = (byte) ((pow & (bytes[i] & 0xFF)) + ((0xFF ^ pow) & (data[data.length - 1] & 0xFF)));
					}
				}
				return bytes;
			} else {
				throw new IllegalArgumentException("Length is less than zero");
			}
		} else {
			throw new IllegalArgumentException("Offset is less than zero");
		}
	}

	/**
	 * Converts the decimal string into big endian byte array. Truncates the
	 * leading zero bits.
	 *
	 * @param dec
	 *            The numeric
	 * @param length
	 *            The bit length
	 * @return The big endian byte array
	 */
	public static byte[] decToBytes(String dec, AtomicInteger length) {
		if (dec == null || dec.isEmpty()) {
			throw new NumberFormatException();
		} else {
			int j = 0;
			List<Byte> bytes = new ArrayList<Byte>();
			boolean last = false;
			while (j < dec.length()) {
				int digit = Integer.parseInt(dec.substring(j, j + 1), 10);
				if (++j < dec.length()) {
					digit = digit * 10
							+ Integer.parseInt(dec.substring(j, j + 1), 10);
				} else
					last = true;
				if (bytes.size() > 0) {
					int b = digit;
					for (int i = bytes.size() - 1; i > -1; i--) {
						bytes.set(
								i,
								Byte.valueOf((byte) ((b += ((bytes.get(i).byteValue() & 0xFF) * (last ? 10
										: 100))) & 0xFF)));
						b >>= 8;
					}
					digit = b;
				}
				if (digit > 0) {
					bytes.add(0, Byte.valueOf((byte) digit));
				}
				j++;
			}
			length.set(bytes.size() * 8);
			byte[] result = Calculator.trunc(toArray(bytes), length);
			return result;
		}
	}

	/**
	 * Converts the hex string into big endian byte array
	 *
	 * @param hex
	 *            The hex string
	 * @param length
	 *            The bit length
	 * @return The big endian byte array
	 */
	public static byte[] hexToBytes(String hex, int length) {
		if (hex != null) {
			if (hex.startsWith("-")) {
				throw new IllegalArgumentException(
						"Negative values are not allowed for 'hex'");
			} else {
				byte[] bytes = new byte[Calculator.size(length)];
				int j = 0;
				int offset;
				int p = length - hex.length() * 4;
				if (p < 0) {
					if ((p <= -4)
							|| (((0xF << 4 + p) & Short.parseShort(
									hex.substring(0, 1), 16)) != 0)) {
						throw new IllegalArgumentException(
								"Hex string is to long");
					}
					offset = 4 - p;
				} else {
					offset = (4 - length % 8 + 4 * (hex.length() % 2)) % 8;
				}
				p /= 8;
				while (j < hex.length()) {
					short b = Short.parseShort(hex.substring(j, j + 1), 16);
					while (p < bytes.length) {
						bytes[p] += (byte) (offset > 0 ? b << offset
								: b >> -offset);
						if (offset < 0) {
							p++;
							offset += 8;
						} else {
							offset -= 4;
							break;
						}
					}
					j++;
				}
				return bytes;
			}
		} else {
			throw new IllegalArgumentException("Hex string is null");
		}
	}

	/**
	 * Converts the hex string into big endian byte array
	 *
	 * @param hex
	 *            The hex string
	 * @return The big endian byte array
	 */
	public static byte[] hexToBytes(String hex) {
		List<Byte> bytes = new ArrayList<Byte>(
				(hex.length() + (2 - hex.length() % 2) % 2) / 2);
		int j = 0;
		while (j < hex.length()) {
			byte b = (byte) (Short.parseShort(hex.substring(j, j + 1), 16) << 4);
			if (++j < hex.length())
				b += Short.parseShort(hex.substring(j, j + 1), 16);
			bytes.add(Byte.valueOf(b));
			j++;
		}
		return toArray(bytes);
	}

	/**
	 * Converts byte list to byte array
	 *
	 * @param bytes
	 *            The byte list
	 * @return The byte array
	 */
	public static byte[] toArray(List<Byte> bytes) {
		byte[] result = new byte[bytes.size()];
		int i = 0;
		for (Byte b : bytes) {
			result[i++] = b.byteValue();
		}
		return result;
	}

	/**
	 * Converts byte array to byte list
	 *
	 * @param bytes
	 *            The byte array
	 * @return The byte list
	 */
	public static List<Byte> toList(byte[] bytes) {
		List<Byte> result = new ArrayList<Byte>(bytes.length);
		for (byte b : bytes) {
			result.add(Byte.valueOf(b));
		}
		return result;
	}

	/**
	 * Converts the binary string into little endian byte array
	 *
	 * @param binary
	 *            The binary string
	 * @return The little endian byte array
	 */
	public static byte[] binaryToBytes(String binary) {
		// Handle null
		if (binary == null)
			throw new NumberFormatException("Binary string value is null");

		// Handle negative value
		if (binary.startsWith("-")) {
			throw new NumberFormatException("Negative values are not allowed");
		}

		// Trim leading zeros
		binary = binary.replaceFirst("^0*", "");

		// Pad Left to have 8 bit pairs
		if (binary.length() % 8 != 0)
			binary = "00000000".substring(0, 8 - (binary.length() % 8)).concat(
					binary);

		// Create Little-Endian Byte Array
		byte[] bytes = new byte[(binary.length() / 8)
				+ (binary.length() % 8 > 0 ? 1 : 0)];
		for (int i = 0; i < bytes.length; ++i) {
			try {
				bytes[i] = Short.valueOf(binary.substring(8 * i, 8 * i + 8), 2)
						.byteValue();
			} catch (NumberFormatException e) {
				throw new NumberFormatException(
						"Binary contains invalid digits");
			}
		}
		return bytes;
	}

	/**
	 * Converts the byte array into hex string with size modulo 4 or 8
	 *
	 * @param bytes
	 *            The byte array
	 * @param length
	 *            The bit length
	 * @param size
	 *            The size modulo 4 or 8
	 * @return The hex string
	 */
	public static String toString(byte[] bytes, int length, int size) {
		if (size == 4 || size == 8) {
			if (length > -1) {
				int offset = (size - length % size) % size;
				int count = bytes.length * 2;
				int j = 0;// bytes
				StringBuilder builder = new StringBuilder(count);
				if (j < bytes.length) {
					builder.append(String.format("%02X",
					        Integer.valueOf(((bytes[j] & 0xFF) >> offset))));
					while (builder.length() < count) {
						builder.append(String
								.format("%02X", Integer.valueOf(
								// move current byte (8 - offset) bits
								// left
										(((bytes[j] & 0xFF) << (8 - offset)) & 0xFF) +
										// move to next byte, if offset greater
										// then
										// zero move next byte offset bits right
										// and add
										// up
										(++j < bytes.length ? (bytes[j] & 0xFF) >> offset
												: 0))));
					}
				}
				return builder.toString();
			} else {
				throw new IllegalArgumentException("length must be positive");
			}
		} else {
			throw new IllegalArgumentException("size must be 4 or 8");
		}
	}

	/**
	 * Converts a Little-Endian byte array into a decimal string
	 *
	 * @param bytes
	 *            The byte array
	 * @return The decimal string
	 */
	public static String toDecimalString(byte[] bytes) {
		// Handle input is null
		if (bytes == null)
			throw new IllegalArgumentException("Bytes value is null");

		// Handle zero
		if (bytes.length == 0)
			return "0";

		// Character Set to Convert byte values to String, Hey character could
		// be append
		String charSet = "0123456789";
		// Dynamic Array to manage the result
		StringBuilder builder = new StringBuilder();

		// Divisor for the long division could be 16 for HexString
		short divisor = 10;

		// Execute division
		byte[] divisionResult = bytes.clone();
		int startIndex = 0;
		// While division result != 0
		while (startIndex >= 0) {
			short remainder = 0;
			int nextStartIndex = -1;
			// For each byte of the division result starting with the first byte
			// which is > 0
			for (int i = startIndex; i < divisionResult.length; i++) {
				// Get remainder from previous division and append the current
				// byte
				short dividend = (short) ((remainder << 8) + (divisionResult[i] & 0xFF));
				// Apply divisor
				divisionResult[i] = (byte) (dividend / divisor);
				remainder = (short) (dividend % divisor);
				// If it is the first byte of division result which is > 0
				// then save current index as start index for next division
				if (nextStartIndex < 0 && (divisionResult[i] & 0xFF) > 0)
					nextStartIndex = i;
			}
			startIndex = nextStartIndex;

			// Add last remainder byte as char to array
			builder.insert(0, charSet.charAt(remainder));
		}
		// return result Array as string
		return builder.toString();
	}

	/**
	 * Joins string array
	 *
	 * @param strings
	 *            The string array
	 * @param separator
	 *            The separator
	 * @return The joined string
	 */
	public static String join(String[] strings, char separator) {
		StringBuilder builder = new StringBuilder();
		for (String string : strings) {
			if (builder.length() > 0)
				builder.append(separator);
			builder.append(string);
		}
		return builder.toString();
	}

	/**
	 * Convert a byte array to a long
	 * 
	 * @param data
	 *            the data to convert
	 * @param length
	 *            the bit length to use from the byte array
	 * @return the long value
	 */
	public static long toLong(byte[] data, int length) {
		long l = 0;
		for (byte b : data) {
			l = (b & 0xFF) + (l << 8);
		}
		return l >> (8 - length % 8) % 8;
	}

	/**
	 * Convert byte array to double
	 * 
	 * @param data
	 *            the data to convert
	 * @return the double value
	 */
	public static double toDouble(byte[] data) {
		return ByteBuffer.wrap(data).getDouble();
	}

	/**
	 * Convert byte array to float
	 * 
	 * @param data
	 *            the data to convert
	 * @return the float value
	 */
	public static float toFloat(byte[] data) {
		return ByteBuffer.wrap(data).getFloat();
	}

	/**
	 * Convert double to byte array
	 * 
	 * @param value
	 *            the value to convert
	 * @return the byte array
	 */
	public static byte[] toByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}
}
