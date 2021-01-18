package havis.middleware.utils.data;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class CalculatorTest {

	byte[] reverse(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		int i = bytes.length - 1;
		for (byte b : bytes) {
			result[i] = b;
			i--;
		}
		return result;
	}

	/**
	 * Test for "size"
	 */
	@Test
	public void sizeTest() {
		// TESTCASE TO CREATE EVERY BIT OF A BYTE
		// Case1
		Assert.assertEquals(0, Calculator.size(0));
		Assert.assertEquals(1, Calculator.size(1));
		Assert.assertEquals(1, Calculator.size(5));
		Assert.assertEquals(1, Calculator.size(8));
		Assert.assertEquals(2, Calculator.size(9));

		// TESTCASE TO CREATE A BIT OF A RANDOM BYTE
		// Case2
		Assert.assertEquals(99999, Calculator.size(799992));

		// TESTCASE TO CREATE NEGATIVE BITS AND BYTES
		// Case3
		try {
			Calculator.size(-1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater or equal to zero and lower then max value of integer minus size", e.getMessage());
		}

		try {
			Calculator.size(Integer.MAX_VALUE);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater or equal to zero and lower then max value of integer minus size", e.getMessage());
		}

		// TESTCASE FOR INT32 MAXVALUES
		// Case4
		Assert.assertEquals(268435455, Calculator.size(Integer.MAX_VALUE - 7)); // MAXVALUE
																				// IS
																				// TO
																				// BIG
	}

	/**
	 * Test for "sum"
	 */
	@Test
	public void sumTest() {
		// TESCASE FOR SOME SIMPLE SUMMATION ---> SUMMATION DOES NOT WORK ACTUAL
		// RESULT DIFFERENT FROM EXPECTED RIGHT RESULT
		// Case1
		// b0 11111111
		// b0 + 00000001
		// b0 = 100000000
		byte[] left = new byte[] { (byte) 0xFF };
		byte[] right = new byte[] { (byte) 0x01 };
		int length = 8;
		try {
			Calculator.sum(left, right, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		// b0 00000001
		// b0 + 00000010
		// b0 = 00000011
		left = new byte[] { 0x01 };
		right = new byte[] { 0x02 };
		length = 8;
		byte[] expected = new byte[] { 0x03 };
		byte[] actual = Calculator.sum(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 00000100
		// b0 + 00000100
		// b0 = 0000
		left = new byte[] { 0x04 };
		right = new byte[] { 0x04 };
		length = 4;
		expected = new byte[] { 0x0 };
		actual = Calculator.sum(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 00000001
		// b0 + 00000010
		// b0 = 000
		left = new byte[] { 0x01 };
		right = new byte[] { 0x02 };
		length = 3;
		expected = new byte[] { 0x0 };
		actual = Calculator.sum(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// TESCASE FOR A RANDOM SUMMATION ---> SUMMATION DOES NOT WORK ACTUAL
		// RESULT DIFFERENT FROM EXPECTED RIGHT RESULT
		// Case2
		// b0 11111111 10101100 00010010 00010110 00011010 10101010
		// 281114502896298
		// b0 + 00000001 10101011 00110100 01100110 11001101 00011010
		// 1834830187802
		// b0 = 100000001 01010111 01000110 01111100 11100111 11000100
		// 282949333084100
		left = new byte[] { (byte) 0xFF, (byte) 0xAC, (byte) 0x12, (byte) 0x16,
				(byte) 0x1A, (byte) 0xAA };
		right = new byte[] { (byte) 0x01, (byte) 0xAB, (byte) 0x34,
				(byte) 0x66, (byte) 0xCD, (byte) 0x1A };
		length = 48;
		try {
			Calculator.sum(left, right, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		// TESTCASE FOR A RANDOM SUMMATION TEST USING THE BITCONVERTER
		// Case3
		// 0b00111100 01100101 10111111 10011000 00000001 00100110 01010101
		// 01111000
		left = new BigInteger("4352095274498676088").toByteArray();
		// 0b10000000 00001011 11101011 10010101 10101000 10001101 11111100
		// 11001100
		right = Arrays.copyOfRange(new BigInteger("9226727289609125068").toByteArray(), 1, 9);
		length = 64 - 11;
		// 0b10111100 01110001 10101011 00101101 10101001 10110100 01001000
		expected = Arrays.copyOfRange(new BigInteger("53042275641046088").toByteArray(), 1, 8);
		actual = Calculator.sum(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// TESTCASE FOR A RESULT ZERO SUMMATION
		// Case4
		// b0 00000000
		// b0 + 00000000
		// b0 = 00000000
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = 8;
		expected = new byte[] { 0x00 };
		actual = Calculator.sum(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE TO CATCH A NEGATIVE LENGTH EXCEPTION MESSAGE
		// Case5
		// b0 00000000
		// b0 + 00000000
		// b0 = 00000000
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = -1;
		try {
			Calculator.sum(left, right, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater or equal to zero and lower then max value of integer minus size", e.getMessage());
		}

		// A TESTCASE TO CATCH A ARRAY TO SHORT EXCEPTION MESSAGE
		// Case6
		// b0 00000000
		// b0 + 00000000
		// b0 = 00000000
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = 16;
		try {
			Calculator.sum(left, right, length);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
		}

	}

	/**
	 * Test for "diff"
	 */
	@Test
	public void diffTest() {
		// A TESTCASE FOR A SIMPLE DIFFERENCE TEST
		// Case1
		// b0 00000010
		// b0 00000001
		// b0 = 00000001
		byte[] left = new byte[] { 0x02 };
		byte[] right = new byte[] { 0x01 };
		int length = 8;
		byte[] expected = new byte[] { 0x01 };
		byte[] actual = Calculator.diff(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 10101011
		// b0 - 10101001
		// b0 = 00000010
		left = new byte[] { (byte) 0xAB };
		right = new byte[] { (byte) 0xA9 };
		length = 8;
		expected = new byte[] { 0x02 };
		actual = Calculator.diff(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A SHIFTING DIFFERENCE TEST
		// Case2
		// b0 11111111 00000001
		// b0 - 00000010
		// b0 = 11111110
		left = new byte[] { (byte) 0xFF, (byte) 0x01 };
		right = new byte[] { 0x02 };
		length = 8;
		expected = new byte[] { (byte) 0xFD };
		actual = Calculator.diff(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A ZERO DIFFERENCE TEST
		// Case1
		// b0 11111111
		// b0 - 00000000
		// b0 = 11111111
		left = new byte[] { (byte) 0xFF };
		right = new byte[] { 0x00 };
		length = 4;
		expected = new byte[] { (byte) 0xF0 };
		actual = Calculator.diff(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A RANDOM DIFFERENCE TEST USING THE BITCONVERTER
		// Case5
		// 0b10001000 11101000 10110001 10101011 00010110 01110100 100 11111
		// 00000000
		left = Arrays.copyOfRange(new BigInteger("9865330332129337088").toByteArray(), 1, 9);
		// 0b00100000 10101011 00111101 00101111 10111110 00101010 111 11111
		// 00000000
		// 1001001 101 00000
		right = new BigInteger("2354042505494462208").toByteArray();
		length = 64 - 13;
		// 73 160
		// 0b01101000 00111101 01110100 01111011 01011000 01001001 101 00000
		// 00000000
		expected = new BigInteger("7511287826634874880").toByteArray();
		actual = Calculator.diff(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, Arrays.copyOf(expected, 7)));

		// A TESTCASE TO CATCH A NEGATIVE LENGTH EXCEPTION MESSAGE
		// Case6
		// b0 00000000
		// b0 - 00000000
		// b0 = 00000000
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = -1;
		expected = new byte[] { 0x00 };
		try {
			actual = Calculator.diff(left, right, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater or equal to zero and lower then max value of integer minus size", e.getMessage());
		}

		// A TESTCASE TO CATCH A ARRAY TO SHORT EXCEPTION MESSAGE
		// Case7
		// b0 00000010
		// b0 + 00000001
		// b0 = 00000001
		left = new byte[] { 0x02 };
		right = new byte[] { 0x01 };
		length = 16;
		expected = new byte[] { 0x01 };
		try {
			actual = Calculator.diff(left, right, length);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Test for "and"
	 */
	@Test
	public void andTest() {
		// 0b 00000000
		byte[] left = new byte[] { 0x00 };
		// 0b 00000000
		byte[] right = new byte[] { 0x00 };
		// 0b 00000000
		byte[] expected = new byte[] { 0x00 };
		int length = 8;
		byte[] actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 00000000
		left = new byte[] { 0x00 };
		// 0b 00000001
		right = new byte[] { 0x01 };
		// 0b 00000000
		expected = new byte[] { 0x00 };
		length = 8;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 11111111
		left = new byte[] { (byte) 0xFF };
		// 0b 11111111
		right = new byte[] { (byte) 0xFF };
		// 0b 11111111
		expected = new byte[] { (byte) 0xFF };
		length = 8;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 11110000
		left = new byte[] { (byte) 0xF0 };
		// 0b 11111111
		right = new byte[] { (byte) 0xFF };
		// 0b 11110000
		expected = new byte[] { (byte) 0xF0 };
		length = 8;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 11110000
		left = new byte[] { (byte) 0xF0 };
		// 0b 10001111
		right = new byte[] { (byte) 0x8F };
		// 0b 10000000
		expected = new byte[] { (byte) 0x80 };
		length = 8;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 00011001
		left = new byte[] { 0x19 };
		// 0b 01111001
		right = new byte[] { 0x79 };
		// 0b 00011001
		expected = new byte[] { 0x19 };
		length = 8;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 00010000 00011111
		left = new byte[] { 0x10, 0x1F };
		// 0b 00000001 00010001
		right = new byte[] { 0x01, 0x11 };
		// 0b 00000000 00010001
		expected = new byte[] { 0x00, 0x11 };
		length = 16;
		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// 0b 11111000 10111111 00111011 00101110 00101110 10001010 10110000
		// 00000000
		left = new BigInteger("17924110111493238784").toByteArray();
		// 0b 11001000 10000100 00100011 00101110 00001010 00000000 11110000
		// 00000000
		right = new BigInteger("14448712185154695168").toByteArray();
		// 0b 11001000 10000100 00100011 00101110 00001010 00000000 10000000
		// 00000000
		expected = new BigInteger("14448712185154666496").toByteArray();
		length = 64 - 15;

		actual = Calculator.and(left, right, length);
		Assert.assertTrue(Arrays.equals(actual, Arrays.copyOf(expected, 7)));

		// Try to catch an exception if left and right array is to short
		try {
			left = new byte[] { (byte) 0x0F };
			right = new byte[] { (byte) 0xFF };
			length = 16;
			Calculator.and(left, right, length);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
		}

		// Try to catch an exeption if right array is to short
		try {
			left = new byte[] { (byte) 0x0F, (byte) 0xFF };
			right = new byte[] { (byte) 0xFF };
			length = 16;
			Calculator.and(left, right, length);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
		}
	}

	/**
	 * Test for "cut"
	 */
	@Test
	public void cutTest() {
		// A TESTCASE FOR A RANDOM CUT TEST USING THE BITCONVERTER
		// Case1
		// 0b 01110111 11110000 11101011 11100000 10101011 10011101 01001000
		// 11011001
		byte[] bytes = new BigInteger("8570609441070467289").toByteArray();
		int pos = 40;
		int length = 47;
		// 0b 00000001 11110000 11101011 11100000 10101011 10011100
		byte[] expected = new BigInteger("1034749520796").toByteArray();
		byte[] actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A SIMPLE CUT TEST
		// Case2
		// b0 11111111
		// b0 cut at position 4 to 11110000
		// b0 = 00001111
		bytes = new byte[] { (byte) 0xFF };
		pos = 4;
		length = 8;
		expected = new byte[] { 0x0F };
		actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 00000001 RIGHT OR WRONG?
		// b0 cut at position 0 to
		// b0 = 00000000
		bytes = new byte[] { 0x01 };
		pos = 0;
		length = 8;
		expected = new byte[] { 0x00 };
		actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 11111111
		// b0 cut at position 0 to 01111111
		// b0 = 11111110
		bytes = new byte[] { (byte) 0xFF };
		pos = 1;
		length = 7;
		expected = new byte[] { (byte) 0xFE };
		actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 00001111 RIGHT OR WRONG?
		// b0 cut at position 4 to 00000001
		// b0 = 10000000
		bytes = new byte[] { 0x0F };
		pos = 4;
		length = 8;
		expected = new byte[] { 0xF };
		actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A SHIFTING CUT TEST
		// Case3
		// b0 11111111 00000001
		// b0 cut at position 8 to 11111111
		// b0 = 11111111
		bytes = new byte[] { (byte) 0xFF, 0x01 };
		pos = 8;
		length = 8;
		expected = new byte[] { (byte) 0xFF };
		actual = Calculator.cut(bytes, pos, length);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A RANDOM CUT TEST ---> RIGHT EXPECTED RESULT DIFFERENT
		// TO ACTUAL RESULT WHICH IS AN EXCEPTION
		/*
		 * #region Case4 // b0 11111111 11111111 11111111 11111111 11111111
		 * 11111111 11111111 // b0 cut at position 23 to 11111111 11 // b0 =
		 * 11111111 11000000 bytes = new byte[] { 0xFF, (byte)0xFF, (byte)0xFF,
		 * (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF }; pos = 27; length =
		 * 10; expected = new byte[] { 0xFF, 0xC0 }; actual =
		 * Calculator.Cut(bytes, pos, length);
		 * Assert.assertTrue(Arrays.equals(actual, expected)); #endregion Case4
		 */

		// A TESTCASE FOR A RANDOM SHIFTING CUT TEST
		/*
		 * #region Case5 // b0 11111111 11111111 11111111 11111111 11111111
		 * 11111111 11111111 // b0 cutted at position 13 to 1111111 // b0 =
		 * 11111110 bytes = new byte[] { 0xFF, (byte)0xFF, (byte)0xFF,
		 * (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF }; pos = 13; length =
		 * 7; expected = new byte[] { 0xFE }; actual = Calculator.Cut(bytes,
		 * pos, length); Assert.assertTrue(Arrays.equals(actual, expected));
		 * #endregion Case5
		 */

		// A TESTCASE TO CATCH A NEGATIVE LENGTH EXCEPTION MESSAGE
		// Case6
		// b0 00000000
		// b0 cut at position 4 00000000
		// b0 = 00000000
		bytes = new byte[] { 0x00 };
		length = 12;
		try {
			actual = Calculator.cut(bytes, 4, length);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
		}

		// b0 00000000
		// b0 cut at position 9 00000000
		// b0 = 00000000
		bytes = new byte[] { 0x00 };
		length = 4;
		try {
			actual = Calculator.cut(bytes, 9, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(
					"Position must be positive and lower then length",
					e.getMessage());
		}
	}

    @Test
    public void applyTest_Iterable() {
        // 1000 0001 1000 0001 1000 0001 1000 0001
        byte[] left = new byte[] { (byte) 0x81, (byte) 0x81, (byte) 0x81, (byte) 0x81 };
        // 0001 1000 0001 1000 0001 1000 0001 1000
        byte[] right = new byte[] { (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18 };
        // 1000 0001 1000 0001 0001 1000 0001 1000
        byte[] expected = new byte[] { (byte) 0x81, (byte) 0x81, (byte) 0x18, (byte) 0x18 };
        Calculator.apply(left, Converter.toList(right), 2, 2);
        byte[] actual = left;
        Assert.assertTrue(Arrays.equals(actual, expected));
    }

	/**
	 * Test for "apply"
	 */
	@Test
	public void applyTest() {
		// A TESTCASE FOR A SINGLE APPLY TEST
		// b0 11111111
		// b0 applied with offset 1 and length 4 00000000
		// b0 = 10000111
		byte[]left = new byte[] { (byte) 0xFF };
		byte[]right = new byte[] { 0x00 };
		byte[]expected = new byte[] { (byte) 0x87 };
		byte[]actual = Calculator.apply(left, right, 1, 4);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A SHIFTING APPLY TEST
		// b0 00000000 00000000 00000000 0000000
		// b0 applied with offset 3 and length 7 11111111
		// b0 = 00011111 11000000 00000000 0000000
		left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		right = new byte[] { (byte) 0xFF };
		expected = new byte[] { 0x1F, (byte) 0xC0, 0x00, 0x00 };
		actual = Calculator.apply(left, right, 3, 7);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A BIGGER APPLY TEST SIZE
		// b0 00000000 00000000 00000000 00000000
		// b0 applied with offset 11 and length 16 11111111 11111111
		// b0 = 00000000 00011111 11111111 11100000
		left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		right = new byte[] { (byte) 0xFF, (byte) 0xFF };
		expected = new byte[] { 0x00, 0x1F, (byte) 0xFF, (byte) 0xE0 };
		actual = Calculator.apply(left, right, 11, 16);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 00000000 00000000 00000000 00000000
		// b0 applied with offset 11 and length 15 11111111 1111111
		// b0 = 00000000 00011111 11111111 11000000
		left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		right = new byte[] { (byte) 0xFF, (byte) 0xFF };
		expected = new byte[] { 0x00, 0x1F, (byte) 0xFF, (byte) 0xC0 };
		actual = Calculator.apply(left, right, 11, 15);
		Assert.assertTrue(Arrays.equals(actual, expected));

	    // b0 00000000 00000000 00000000 00000000
        // b0 applied with offset 8 and length 15 11111111 1111111
        // b0 = 00000000 11111111 11111110 00000000
        left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        right = new byte[] { (byte) 0xFF, (byte) 0xFF };
        expected = new byte[] { 0x00, (byte) 0xFF, (byte) 0xFE, (byte) 0x00 };
        actual = Calculator.apply(left, right, 8, 15);
        Assert.assertTrue(Arrays.equals(actual, expected));

        // b0 00000000 00000000 00000000 00000000 00000000 00000000
        // b0 applied with offset 9 and length 17 11111111 11111111 11111111
        // b0 = 00000000 01111111 11111111 11000000 00000000 00000000
        left = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        right = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        expected = new byte[] { (byte) 0x00, (byte) 0x7F, (byte) 0xFF, (byte) 0xC0, (byte) 0x00, (byte) 0x00 };
        actual = Calculator.apply(left, right, 9, 17);
        Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE TO CATCH A NEGATIVE LENGTH EXCEPTION MESSAGE
		// b0 00000000
		// b0 offset 1 length -1 11111111
		// b0 = 00000000
		left = new byte[] { 0x00 };
		right = new byte[] { (byte) 0xFF };

		expected = new byte[] { 0x00 };
		try {
			actual = Calculator.apply(left, right, 1, -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater then zero", e.getMessage());
		}
	}

	/**
	 * Test for "shift"
	 */
	@Test
	public void shiftTest() {
		// A TESTCASE FOR A RANDOM SIFT TEST USING THE BITCONVERTER
		// Case1
		// 0b10111101 11110010 01100100 11000001 00000100 10000001 10101011
		// 11011111
		byte[] bytes = Arrays.copyOfRange(new BigInteger("13687112997699431391").toByteArray(), 1, 9);
		// 0b01001100 10011000 00100000 10010000 00110101 01111011 11100000
		// 00000000
		byte[] expected = new BigInteger("5519197147087233024").toByteArray();
		byte[] actual = Calculator.shift(bytes, 13);
		Assert.assertTrue(Arrays.equals(actual, Arrays.copyOf(expected, 7)));

		// A TESTCASE FOR A SINGLE SHIFT TEST
		// Case2
		// b0 11111111 11111111
		// b0 Shifted by 4 Bits 0000
		// b0 = 11111111 11110000
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF };
		expected = new byte[] { (byte) 0xFF, (byte) 0xF0 };
		actual = Calculator.shift(bytes, 4);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A RANDOM SHIFT TEST
		// Case3
		// b0 00010001 11111111
		// b0 Shifted by -3 Bits 0000
		// b0 = 00000010 00111111 11100000
		bytes = new byte[] { 0x11, (byte) 0xFF };
		expected = new byte[] { 0x02, 0x3F, (byte) 0xE0 };
		actual = Calculator.shift(bytes, -3);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A ZERO SHIFT TEST
		// Case4
		// b0 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		// b0 Shifted by 120 Bits
		// b0 = 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		expected = new byte[] {};
		actual = Calculator.shift(bytes, 120);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A BIGGER RANDOM SHIFT TEST
		// Case5
		// b0 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111
		// b0 Shifted by -23 Bits
		// b0 = 00000000 00000000 00000000 11111111 11111111 11111111 11111111
		// 11111111 11111111 11111111 11111111 11111111
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };
		expected = new byte[] { 0x00, 0x00, 0x01, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFE, };
		actual = Calculator.shift(bytes, -23);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// A TESTCASE FOR A THROWN EXCEPTION IF N IS LONGER THEN ARRAY TEST
		// Case6
		// b0 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		// b0 Shifted by 121 Bits
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		try {
			actual = Calculator.shift(bytes, 121);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals(
					"The bit count should be lower or equal to bit length of bytes",
					e.getMessage());
		}

		// A TESTCASE FOR A NEGATIVE SHIFT TEST
		// Case7
		// b0 00010010
		// b0 Shifted by -3 Bits 000
		// b0 10010000
		bytes = new byte[] { 0x12 };
		expected = new byte[] { (byte) 0x90 };
		actual = Calculator.shift(bytes, 5, 3);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0 10010000
		// b0 Shifted by -3 Bits 000
		// b0 00010010
		bytes = new byte[] { (byte) 0x90 };
		expected = new byte[] { 0x12 };
		actual = Calculator.shift(bytes, 5, -3);
		Assert.assertTrue(Arrays.equals(actual, expected));

		// b0
		// b0 Shifted by -8 Bits 00000000
		// b0 00000000
		bytes = new byte[0];
		expected = new byte[] { 0x00 };
		actual = Calculator.shift(bytes, 0, -8);
		Assert.assertTrue(Arrays.equals(actual, expected));

        // b0 10000000
        // b0 Shifted by -8 Bits 00000000
        // b0 00000000
        bytes = new byte[] { (byte) 0x80 };
        expected = new byte[] { 0x00 };
        actual = Calculator.shift(bytes, 0, -8);
        Assert.assertTrue(Arrays.equals(actual, expected));

		// b0
		// b0 Shifted by -9 Bits 00000000 0
		// b0 00000000 00000000
		bytes = new byte[0];
		expected = new byte[] { 0x00, 0x00 };
		actual = Calculator.shift(bytes, 0, -9);
		Assert.assertTrue(Arrays.equals(actual, expected));
	}

	/**
	 * Test for "trunc"
	 */
	@Test
	public void truncTest() {
		// A TESTCASE FOR A SINGLE TRUNC TEST
		// Case1
		// 0b10000000 length 8
		byte[] bytes = new byte[] { (byte) 0x80 };
		AtomicInteger lenght = new AtomicInteger(8);
		byte[] expectedBytes = new byte[] { (byte) 0x80 };
		Integer expectedLenght = Integer.valueOf(8);
		byte[] actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());

		// A TESTCASE FOR TRUNK USING A DIFFERENT LENGHT
		// Case2
		// 0b10000000 length 16
		bytes = new byte[] { 0x00, (byte) 0x80 };
		lenght = new AtomicInteger(16);
		expectedBytes = new byte[] { (byte) 0x80 };
		expectedLenght = Integer.valueOf(8);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());

		// A TESTCASE USING A BIGGER BYTE FOR A TRUNC TEST
		// Case3
		// 0b01000000 length 16
		bytes = new byte[] { 0x40 };
		lenght = new AtomicInteger(16);
		expectedBytes = new byte[] { (byte) 0x80 };
		expectedLenght = Integer.valueOf(15);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());

		// A TESTCASE FOR A LOWER LENGHT TRUNC TEST
		// Case4
		// 0b00000000 length 4
		bytes = new byte[] { 0x00 };
		lenght = new AtomicInteger(4);
		expectedBytes = new byte[0];
		expectedLenght = Integer.valueOf(0);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());

		// A TESTCASE TO TEST THE BYTE LENGHT AFTER TRUNC
		// Case5
		// 0b00000001 length 8
		bytes = new byte[] { 0x00, 0x01 };
		lenght = new AtomicInteger(8);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(actual.length == 0);
		Assert.assertEquals(0, lenght.intValue());

		// A TESTCASE FOR A DIFFERENT LENGTH
		// Case6
		// 0b00100000 length 7
		bytes = new byte[] { 0x20 };
		lenght = new AtomicInteger(8);
		expectedBytes = new byte[] { (byte) 0x80 };
		expectedLenght = Integer.valueOf(6);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());

		// A TESTCASE FOR A RANDOM TRUNC TEST
		// Case7
		// 0b000011111111 length 12
		bytes = new byte[] { 0x0F, (byte) 0xF0 };
		lenght = new AtomicInteger(12);
		expectedBytes = new byte[] { (byte) 0xFF, 0x00 };
		expectedLenght = Integer.valueOf(8);
		actual = Calculator.trunc(bytes, lenght);

		Assert.assertTrue(Arrays.equals(actual, expectedBytes));
		Assert.assertEquals(expectedLenght.intValue(), lenght.intValue());
    }

	@Test
	public void stripTest()
    {
        //  A TESTCASE FOR A COMMON STRIP TEST
        byte[] bytes = new byte[] { (byte) 0xDB, (byte) 0x4C };
        byte[] expected = new byte[] { (byte) 0xD0 };
        byte [] actual = Calculator.strip(bytes, 6, 6);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A COMMON LONGER STRIP TEST
        bytes = new byte[] { (byte) 0xAF, (byte) 0xFE, (byte) 0x00, (byte) 0x00 };
        expected = new byte[] { (byte) 0xAF, (byte) 0xFE, (byte) 0x00, (byte) 0x00 };
        actual = Calculator.strip(bytes, 0, 0);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A LONG NULLED STRIP TEST
        bytes = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
        expected = new byte[] { (byte) 0x00, (byte) 0x00 };
        actual = Calculator.strip(bytes, 8, 16);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A LONG EXPECTED RESULT STRIP TEST
        bytes = new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };
        expected = new byte[] { (byte) 0xBC, (byte) 0xCD, (byte) 0xDE, (byte) 0xEF, (byte) 0xF0 };
        actual = Calculator.strip(bytes, 12, 36);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A SMALL STRIP TEST
        bytes = new byte[] { (byte) 0xDB };
        expected = new byte[] { (byte) 0xB0 };
        actual = Calculator.strip(bytes, 4, 4);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A SMALL STRIP TEST (WITHOUT OFFSET)
        bytes = new byte[] { (byte) 0xDB };
        expected = new byte[] { (byte) 0xDB };
        actual = Calculator.strip(bytes, 0, 8);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A SMALL STRIP TEST (WITHOUT OFFSET AND LENGTH)
        bytes = new byte[] { (byte) 0xDB };
        expected = new byte[] { (byte) 0xDB };
        actual = Calculator.strip(bytes, 0, 0);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A LONG STRIP TEST (WITHOUT OFFSET)
        bytes = new byte[] { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };
        expected = new byte[] { (byte) 0x11 };
        actual = Calculator.strip(bytes, 0, 8);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44 };
        actual = Calculator.strip(bytes, 0, 32);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0x11, (byte) 0x22};
        actual = Calculator.strip(bytes, 0, 15);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };
        actual = Calculator.strip(bytes, 0, 88);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A LONG STRIP TEST
        bytes = new byte[] { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };
        expected = new byte[] { (byte) 0x33, (byte) 0x44 };
        actual = Calculator.strip(bytes, 16, 16);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0x55, (byte) 0xAA };
        actual = Calculator.strip(bytes, 32, 16);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0xFF };
        actual = Calculator.strip(bytes, 80, 0);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR A LONG STRIP TEST (UNEVEN OFFSET)
        bytes = new byte[] { (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };
        expected = new byte[] { (byte) 0xBB };
        actual = Calculator.strip(bytes, 65, 8);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0xBD, (byte) 0xDF, (byte) 0xE0 };
        actual = Calculator.strip(bytes, 69, 0);
        Assert.assertTrue(Arrays.equals(actual, expected));

        //  A TESTCASE FOR RANDOMIZED STRIP TESTS
        bytes = new byte[] { (byte) 0x65, (byte) 0xA3, (byte) 0x2F, (byte) 0x9C, (byte) 0x32, (byte) 0x11, (byte) 0xFC, (byte) 0xBA, (byte) 0x1F, (byte) 0xC3, (byte) 0xE9, (byte) 0x43, (byte) 0x51, (byte) 0x66, (byte) 0xE4, (byte) 0x4E, (byte) 0xCE, (byte) 0x99};
        expected = new byte[] { (byte) 0x20 };
        actual = Calculator.strip(bytes, 32, 3);
        Assert.assertTrue(Arrays.equals(actual, expected));
        expected = new byte[] { (byte) 0xD0, (byte) 0xFE, (byte) 0x1E };
        actual = Calculator.strip(bytes, 59, 23);
        Assert.assertTrue(Arrays.equals(actual, expected));

        // A TESTCASE FOR THE THROWN EXCEPTION
        try
        {
            bytes = new byte[] { (byte) 0xAF, (byte) 0xFE };
            actual = Calculator.strip(bytes, 4, 14);
            Assert.fail();
        }
        catch (IndexOutOfBoundsException e) { }

        try
        {
            bytes = new byte[] { (byte) 0x65, (byte) 0xA3, (byte) 0x2F, (byte) 0x9C, (byte) 0x32, (byte) 0x11, (byte) 0xFC, (byte) 0xBA, (byte) 0x1F, (byte) 0xC3, (byte) 0xE9, (byte) 0x43, (byte) 0x51, (byte) 0x66, (byte) 0xE4, (byte) 0x4E, (byte) 0xCE, (byte) 0x99 };
            actual = Calculator.strip(bytes, 90, 80);
            Assert.fail();
        }
        catch (IndexOutOfBoundsException e) { }
    }

	@Test
	public void concatTest() {
	    byte[] expected = new byte[] { (byte) 0xFF, (byte) 0xAA, (byte) 0xBB, (byte) 0x01, (byte) 0x02, (byte) 0x03 };
	    byte[] actual = Calculator.concat(new byte[] { (byte) 0xFF, (byte) 0xAA, (byte) 0xBB },  new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03 });
        Assert.assertTrue(Arrays.equals(actual, expected));
    }
}
