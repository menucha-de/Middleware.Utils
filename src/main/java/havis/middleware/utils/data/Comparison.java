package havis.middleware.utils.data;

/**
 * Implements basic compare methods
 */
public class Comparison {

	/**
	 * Compares the first n-th bits of left and right array
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param n
	 *            The bit length
	 * @param offset
	 *            The bit offset
	 * @return Positiv number if the first n-th bits of left array are greater
	 *         than bits of right array. Negative number if the first n-th bits
	 *         of left are lower than bits of right array. Zero if the first
	 *         n-th bits of both arrays are equal.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if the size of left or right array is to low for comparison
	 */
	static int compare(byte[] left, byte[] right, int n, int offset) {
		if (offset > 0) {
			int c = compare(left[offset / 8], right[offset / 8],
					n > 8 - offset % 8 ? 8 - offset % 8 : n, offset % 8);
			if (c != 0)
				return c;
		}
		for (int i = Calculator.size(offset); i < n / 8; i++) {
			if ((left[i] & 0xFF) > (right[i] & 0xFF)) {
				return 1;
			}
			if ((left[i] & 0xFF) < (right[i] & 0xFF)) {
				return -1;
			}
		}
		if (n % 8 > 0) {
			return compare(left[n / 8], right[n / 8], n % 8, n > 8 ? 0 : offset);
		}
		return 0;
	}

	/**
	 * Compares the first n-th bits of two bytes
	 *
	 * @param left
	 *            The left byte
	 * @param right
	 *            The right byte
	 * @param n
	 *            Th bit length
	 * @param offset
	 *            The bit offset
	 * @return Positiv number if the first n-th bits of left byte is greater
	 *         than bits of right byte. Negative number if the first n-th bits
	 *         of left byte is lower than bits of right byte. Zero if the first
	 *         n-th bits of both bytes are equal.
	 */
    static int compare(byte left, byte right, int n, int offset) {
        byte mask = (byte) ((0xFF << (8 - n % 8) - offset % 8) & (0xFF >> (offset % 8)));
        int l = 0, r = 0;
        if (mask != 0) {
            l = left & 0xFF & mask;
            r = right & 0xFF & mask;
        } else {
            // don't use the mask if the whole byte is compared
            l = left & 0xFF;
            r = right & 0xFF;
        }

        if (l > r) {
            return 1;
        } else if (l < r) {
            return -1;
        }
        return 0;
	}

	public static boolean equal(byte[] left, byte[] right, int n) {
		return equal(left, right, n, 0);
	}

	/**
	 * Returns if the first n-th bits of array left and right are equal
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param n
	 *            The bit length
	 * @param offset
	 *            The bit offset
	 * @return True if the first n-th bits of array left and right are equal,
	 *         false otherwise
	 * @throws ArrayIndexOutOfBoundsException
	 *             - if bit size of left or right array is lower then n
	 */
	public static boolean equal(byte[] left, byte[] right, int n, int offset) {
		return compare(left, right, n, offset) == 0;
	}

	/**
	 * Returns if left array is equal to right array with mask. If relate is
	 * negative, mask will apply to left array. If relate is positive, mask will
	 * apply to right array. If relate is zero, mask will apply to left and right array.
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param length
	 *            The bit length
	 * @param mask
	 *            The mask
	 * @param relate
	 *            The relation
	 * @return True if left array is equal to right array
	 * @throws ArrayIndexOutOfBoundsException
	 *             if length of left array is equal to length of right array and
	 *             greater then length of mask array
	 */
	public static boolean equal(byte[] left, byte[] right, int length,
			byte[] mask, int relate) {
		if (relate < 0) {
			for (int i = 0; i < length / 8; i++) {
				if ((left[i] & mask[i]) != right[i]) {
					return false;
				}
			}
			if (length % 8 > 0) {
				return compare((byte) (left[length / 8] & mask[length / 8]),
						right[length / 8], length % 8, 0) == 0;
			}
		} else {
			if (relate > 0) {
				for (int i = 0; i < length / 8; i++) {
					if (left[i] != (right[i] & mask[i])) {
						return false;
					}
				}
				if (length % 8 > 0) {
					return compare(left[length / 8],
							(byte) (right[length / 8] & mask[length / 8]),
							length % 8, 0) == 0;
				}
			} else {
				for (int i = 0; i < length / 8; i++) {
					if ((left[i] & mask[i]) != (right[i] & mask[i])) {
						return false;
					}
				}
				if (length % 8 > 0) {
					return compare(
							(byte) (left[length / 8] & mask[length / 8]),
							(byte) (right[length / 8] & mask[length / 8]),
							length % 8, 0) == 0;
				}
			}
		}
		return true;
	}

	/**
	 * Returns if first n-th bits of left array are lower then the bits of the
	 * right array
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param n
	 *            The bit length
	 * @return True if first n-th bits of left array are lower then the bits of
	 *         the right array
	 */
	public static boolean lower(byte[] left, byte[] right, int n) {
		return compare(left, right, n, 0) < 0;
	}

	/**
	 * Returns if first n-th bits of left array are greater then the bits of the
	 * right array
	 *
	 * @param left
	 *            The left array
	 * @param right
	 *            The right array
	 * @param n
	 *            The bit length
	 * @return True if first n-th bits of left array are greater then the bits
	 *         of the right array
	 */
	public static boolean greater(byte[] left, byte[] right, int n) {
		return compare(left, right, n, 0) > 0;
	}

	/**
	 * Returns a 32 bit hash code for a given byte array striped by offset and
	 *
	 * @param dataToHash
	 *            The byte array to create a hash code for.
	 * @param offset
	 *            The bitwise offset to start with.
	 * @param length
	 *            The bitwise length to use.
	 * @return The hash code
	 */
	public static int hashCode(byte[] dataToHash, int offset, int length) {
		if (offset + 1 > dataToHash.length * 8)
			return 0;
		else if (length + offset > dataToHash.length * 8)
	        length = (dataToHash.length * 8) - offset;

		return hashCode(Calculator.strip(dataToHash, offset, length));
	}

	/**
	 * Returns a 32 bit hash code for a given byte array
	 *
	 * @param dataToHash
	 *            The byte array to create a hash code for.
	 * @return The hash code
	 */
	public static int hashCode(byte[] dataToHash) {
		int dataLength = dataToHash.length;
		if (dataLength == 0)
			return 0;
		int hash = dataLength;
		int remainingBytes = dataLength & 3; // mod 4
		int numberOfLoops = dataLength >> 2; // div 4
		int currentIndex = 0;
		while (numberOfLoops > 0) {
			hash += (dataToHash[currentIndex++] & 0xFF) | (dataToHash[currentIndex++] & 0xFF) << 8;
			int tmp = ((dataToHash[currentIndex++] & 0xFF) | (dataToHash[currentIndex++] & 0xFF) << 8) << 11
					^ hash;
			hash = (hash << 16) ^ tmp;
			hash += hash >> 11;
			numberOfLoops--;
		}

		switch (remainingBytes) {
		case 3:
			hash += (dataToHash[currentIndex++] & 0xFF) | (dataToHash[currentIndex++] & 0xFF) << 8;
			hash ^= hash << 16;
			hash ^= (dataToHash[currentIndex] & 0xFF) << 18;
			hash += hash >> 11;
			break;
		case 2:
			hash += (dataToHash[currentIndex++] & 0xFF) | (dataToHash[currentIndex] & 0xFF) << 8;
			hash ^= hash << 11;
			hash += hash >> 17;
			break;
		case 1:
			hash += (dataToHash[currentIndex] & 0xFF);
			hash ^= hash << 10;
			hash += hash >> 1;
			break;
		default:
			break;
		}

		/* Force "avalanching" of final 127 bits */
		hash ^= hash << 3;
		hash += hash >> 5;
		hash ^= hash << 4;
		hash += hash >> 17;
		hash ^= hash << 25;
		hash += hash >> 6;

		return hash;
	}
}
