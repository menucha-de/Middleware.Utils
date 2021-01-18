package havis.middleware.utils.data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides basic byte calculations
 */
public class Calculator {

	/**
	 * Returns the ceiling byte size by bit length;
	 *
	 * @param length
	 *            The bit length
	 * @return The byte size
	 * @throws IllegalArgumentException
	 *             If length is not positive or length + 8 is greater then max
	 *             value of integer
	 */
	public static int size(int length) {
		return size(length, 8);
	}

	/**
	 * Returns the ceiling byte size by bit length;
	 *
	 * @param length
	 *            The bit length
	 * @param size
	 *            The bit size, default 8
	 * @return The byte size
	 * @throws IllegalArgumentException
	 *             If length is not positive or length + 8 is greater then max
	 *             value of integer
	 */
	public static int size(int length, int size) {
		if ((length > -1) && (length < Integer.MAX_VALUE)) {
			return (length + (size - length % size) % size) / size;
		} else {
			throw new IllegalArgumentException(
					"Length should be greater or equal to zero and lower then max value of integer minus size");
		}
	}

	/**
	 * Calculates the sum of left and right byte array and returns the result by
	 * length
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param length
	 *            The bit length
	 * @return The result sum
	 * @throws IllegalArgumentException
	 *             If array is to short or if the result is greater then length
	 *             or if length is not positive
	 */
	public static byte[] sum(byte[] left, byte[] right, int length) {
		int size = size(length);
		byte[] result = new byte[size];
		int sum = 0;
		if (length % 8 > 0) {
			result[length / 8] = (byte) (sum = ((left[length / 8] & 0xFF) & 0xFF << 8 - length % 8)
					+ ((right[length / 8] & 0xFF) & 0xFF << 8 - length % 8));
		}
		for (int i = length / 8; i > 0; i--) {
			result[i - 1] = (byte) (sum = (left[i - 1] & 0xFF)
					+ (right[i - 1] & 0xFF) + (sum > 255 ? 1 : 0));
		}
		if (sum > 255) {
			throw new IllegalArgumentException("Overflow occurs");
		}
		return result;
	}

	/**
	 * Calculates the difference of left and right array and returns the result
	 * by length
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param length
	 *            The bit length
	 * @return The result different
	 * @throws IllegalArgumentException
	 *             If left or right array is to short or if length is not
	 *             positive
	 */
	public static byte[] diff(byte[] left, byte[] right, int length) {
		int size = size(length);
		byte[] result = new byte[size];
		int diff = 0;
		if (length % 8 > 0) {
			result[length / 8] = (byte) (diff = ((left[length / 8] & 0xFF) & 0xFF << 8 - length % 8)
					- ((right[length / 8] & 0xFF) & 0xFF << 8 - length % 8));
		}
		for (int i = length / 8; i > 0; i--) {
			result[i - 1] = (byte) (diff = (left[i - 1] & 0xFF)
					- (right[i - 1] & 0xFF) - (diff < 0 ? 1 : 0));
		}
		return result;
	}

	/**
	 * Calculates the bit-wise AND of left and right array and returns the
	 * result by length
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param length
	 *            The bit length
	 * @return The result different
	 * @throws IllegalArgumentException
	 *             if left or right array is to short or if length is not
	 *             positive
	 */
	public static byte[] and(byte[] left, byte[] right, int length) {
		int size = size(length);
		byte[] result = new byte[size];
		for (int i = 0; i < size; i++) {
			result[i] = (byte) (left[i] & right[i]);
		}
		if (length % 8 > 0) {
			result[length / 8] = (byte) (result[length / 8] & (0xFF << 8 - length % 8));
		}
		return result;
	}

	/**
	 * Sets all bits left to position and right to length to zero. Cuts the
	 * bytes right to length
	 *
	 * @param bytes
	 *            The byte array
	 * @param pos
	 *            The bit position
	 * @param length
	 *            The bit length
	 * @return The result array
	 * @throws IllegalArgumentException
	 *             if left array is to short or if length is not positive or
	 *             position is not lower or equal to length
	 */
	public static byte[] cut(byte[] bytes, int pos, int length) {
		if ((pos > -1) && (pos <= length)) {
			int size = size(length);
			byte[] result = new byte[size];
			for (int i = (length - pos) / 8; i < size; i++) {
				result[i] = bytes[i];
			}
			if (length % 8 > 0) {
				result[length / 8] = (byte) (result[length / 8] & 0xFF << 8 - length % 8);
			}
			if ((length - pos) % 8 > 0) {
				result[(length - pos) / 8] = (byte) (result[(length - pos) / 8] & (0xFF >> ((length + pos) % 8)));
			}
			return result;
		} else {
			throw new IllegalArgumentException(
					"Position must be positive and lower then length");
		}
	}

	/**
	 * Applies the bytes of the right byte array to left byte array from byte
	 * offset to byte length.
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param offset
	 *            The byte offset
	 * @param length
	 *            The byte length
	 * @throws IllegalArgumentException
	 *             if left or right array is to short or if length is negative
	 */
	public static void apply(byte[] left, Iterable<Byte> right, int offset,
			int length) {
		if (length > -1) {
			int i = 0;
			for (byte b : right) {
				if (i < length) {
					left[offset + i] = b;
				} else {
					break;
				}
				i++;
			}
		} else {
			throw new IllegalArgumentException(
					"Length should be greater or equal to zero");
		}
	}

	/**
	 * Applies the bytes of the right byte array to left byte array from bit
	 * offset to bit length. Returns a byte array of left byte array size
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param offset
	 *            The bit offset
	 * @param length
	 *            The bit length
	 * @return The result array
	 * @throws IllegalArgumentException
	 *             if left or right array is to short or offset is negative or
	 *             if length not positive
	 */
	public static byte[] apply(byte[] left, byte[] right, int offset, int length) {
		if (offset > -1) {
			if (length > 0) {
				int size = size(offset + length);
				byte[] result = new byte[left.length];
				for (int i = 0; i < offset / 8; i++) {
					result[i] = left[i];
				}
				if (offset % 8 > 0) {
					result[offset / 8] = (byte) (((left[offset / 8] & 0xFF) & (0xFF << (8 - offset % 8))) + ((right[0] & 0xFF) >> (offset % 8)));
					for (int i = size(offset), j = 0; i < (offset + length) / 8; i++, j++) {
						result[i] = (byte) (((right[j] & 0xFF) << (8 - (offset % 8))) + (j + 1 < right.length ? ((right[j + 1] & 0xFF) >> (offset % 8))
								: 0));
					}
				} else {
					for (int i = offset / 8, j = 0; i < (offset + length) / 8; i++, j++) {
						result[i] = right[j];
					}
				}
				if ((offset + length) % 8 > 0) {
					if (offset % 8 > 0) {
						if (offset % 8 + length > 8) {
							result[size - 1] = (byte) (((right[size(length) - 1] & 0xFF) << (8 - ((offset + length) % 8)) & (0xFF << (8 - (offset + length) % 8))) + ((left[size - 1] & 0xFF) & (0xFF >> (offset % 8))));
						} else {
							result[size - 1] = (byte) ((result[size - 1] & 0xFF) + ((left[size - 1] & 0xFF) & (0xFF >> ((offset + length) % 8))));
						}
					} else {
						result[size - 1] = (byte) ((right[size(length) - 1] & 0xFF) & (0xFF << (8 - (length % 8))) + ((left[size - 1] & 0xFF) & (0xFF >> (length % 8))));
					}
				}
				for (int i = size; i < left.length; i++) {
					result[i] = left[i];
				}
				return result;
			} else {
				throw new IllegalArgumentException(
						"Length should be greater then zero");
			}
		} else {
			throw new IllegalArgumentException(
					"Offset should be greater or equal to zero");
		}
	}

	/**
	 * Shifts byte array n bits left
	 *
	 * @param bytes
	 *            The byte array
	 * @param n
	 *            The bit count
	 * @return The shifted byte array
	 * @throws IllegalArgumentException
	 *             if n is not lower than bytes size
	 */
	public static byte[] shift(byte[] bytes, int n) {
		return shift(bytes, bytes.length * 8, n);
	}

	/**
	 * Shifts byte array n bits left
	 *
	 * @param bytes
	 *            The byte array
	 * @param length
	 *            The bit length of bytes
	 * @param n
	 *            The bit count
	 * @return The shifted byte array
	 * @throws IllegalArgumentException
	 *             if n is not lower than bytes size
	 */
	public static byte[] shift(byte[] bytes, int length, int n) {
		if (n <= bytes.length * 8) {
			byte[] result = new byte[size(bytes.length * 8 - n)];
			if (bytes.length > 0) {
				if (n % 8 > 0) {
					for (int i = 0; i < result.length - 1; i++) {
						result[i] = (byte) (((bytes[i + n / 8] & 0xFF) << (n % 8)) + ((bytes[i
								+ n / 8 + 1] & 0xFF) >> (8 - (n % 8))));
					}
					result[result.length - 1] = (byte) ((bytes[result.length
							- 1 + n / 8] & 0xFF) << (n % 8));
				} else if (n % 8 < 0) {
					result[-n / 8] = (byte) ((bytes[0] & 0xFF) >> (-n % 8));
					for (int i = 0; i < bytes.length - 1; i++) {
						result[i - n / 8 + 1] = (byte) (((bytes[i] & 0xFF) << (8 + (n % 8))) + ((bytes[i + 1] & 0xFF) >> (-n % 8)));
					}
					result[result.length - 1] = (byte) ((bytes[bytes.length - 1] & 0xFF) << (8 + (n % 8)));
				} else {
					if (n < 0) {
						for (int i = 0; i < bytes.length; i++) {
							result[i - n / 8] = bytes[i];
						}
					} else {
						for (int i = 0; i < result.length; i++) {
							result[i] = bytes[i + n / 8];
						}
					}
				}
			}
			return strip(result, 0,
					(((length - n) / 8) + (((length - n) % 8 > 0) ? 1 : 0)) * 8);
		} else {
			throw new IllegalArgumentException(
					"The bit count should be lower or equal to bit length of bytes");
		}
	}

	/**
	 * Strips the byte array. Shifts byte array offset bits left and cuts data
	 * by length
	 *
	 * @param bytes
	 *            The byte array
	 * @param offset
	 *            The bit offset
	 * @param length
	 *            The bit length
	 * @return The striped byte array
	 */
    public static byte[] strip(byte[] bytes, int offset, int length) {
        if ((offset + length) / 8 + ((offset + length) % 8 == 0 ? 0 : 1) <= bytes.length) {
            int l = length == 0 ? bytes.length - offset / 8 : size(length);
            // number of bytes of the result data
            byte[] result = new byte[l];


            // skip bytes depending on field offset
            int byteIndex = offset / 8;
            int byteLength = byteIndex + (length == 0 ? bytes.length - offset / 8 : size(offset % 8 + length));

            // number of bits to move within a byte
            offset = offset % 8;
            if (byteIndex < byteLength) {
                for (int i = 0; i < result.length; i++) {
                    // move current byte offset bits left and add next byte, if
                    // offset greater then zero move next byte (8 - offset) bits right
                    result[i] = (byte) (((bytes[byteIndex] & 0xFF) << offset) + ((++byteIndex < byteLength) && offset > 0 ? (bytes[byteIndex] & 0xFF) >> (8 - offset) : 0));
                }
                if (length % 8 > 0) {
                    // blank last bits
                    int current = result[result.length - 1] & 0xFF;
                    current &= 0xFF << (8 - length % 8);
                    result[result.length - 1] = (byte) current;
                }
            }
            return result;
        } else {
            throw new IndexOutOfBoundsException("Offset plus length must not be greater than bytes.lenght");
        }
	}

	/**
	 * Truncates the first irrelevant zero bits from byte array. Input length
	 * indicates the left relevant bits
	 *
	 * @param bytes
	 *            The source byte array
	 * @param length
	 *            The bit length
	 * @return The truncated byte array
	 */
	public static byte[] trunc(byte[] bytes, AtomicInteger length) {
		int n = 0;
		for (int i = 0; i < bytes.length; i++) {
			if ((bytes[i] & 0xFF) > 0) {
				for (int j = 0; j < 8; j++) {
					if (((bytes[i] & 0xFF) & (1 << (7 - j))) == (1 << (7 - j))) {
						break;
					} else {
						n++;
					}
				}
				break;
			} else {
				n += 8;
			}
		}
		if (n < length.get()) {
			length.set(length.get() - n);
			return shift(bytes, n);
		} else {
			length.set(0);
			return new byte[] {};
		}
	}

	/**
	 * Joins two arrays
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @return The joined array
	 */
	public static byte[] concat(byte[] left, byte[] right) {
		byte[] result = new byte[left.length + right.length];
		int i = 0;
		for (byte b : left) {
			result[i++] = b;
		}
		for (byte b : right) {
			result[i++] = b;
		}
		return result;
	}
}
