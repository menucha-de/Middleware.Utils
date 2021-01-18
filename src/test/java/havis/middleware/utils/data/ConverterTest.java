package havis.middleware.utils.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class ConverterTest {

	/**
	 * Test for "decToBytes"
	 */
	@Test
	public void decToBytesTest() {
		AtomicInteger length = new AtomicInteger(0);
		byte[] array;
		// Zero should return a valid result.
		// 0b 00000000
		array = Converter.decToBytes("0", length);
		// 0b 00000000
		Assert.assertTrue(Arrays.equals(array, new byte[] {}));
		Assert.assertEquals(0, length.get());

		// 0b 00000001
		array = Converter.decToBytes("1", length);
		// 0b 10000000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0x80 }));
		Assert.assertEquals(1, length.get());

		// 0b 00001111
		array = Converter.decToBytes("15", length);
		// 0b 11110000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xF0 }));
		Assert.assertEquals(4, length.get());

		// 0b 01100000
		array = Converter.decToBytes("96", length);
		// 0b 11000000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xC0 }));
		Assert.assertEquals(7, length.get());

		// 0b 01100100
		array = Converter.decToBytes("100", length);
		// 0b 11001000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xC8 }));
		Assert.assertEquals(7, length.get());

		// 0b 10000000
		array = Converter.decToBytes("128", length);
		// 0b 10000000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0x80 }));
		Assert.assertEquals(8, length.get());

		// 0b 11111111
		array = Converter.decToBytes("255", length);
		// 0b 11111111
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xFF }));
		Assert.assertEquals(8, length.get());

		// 0b 00000001 00000000
		array = Converter.decToBytes("256", length);
		// 0b 10000000 00000000
		Assert.assertTrue(Arrays
				.equals(array, new byte[] { (byte) 0x80, 0x00 }));
		Assert.assertEquals(9, length.get());

		// 0b 11111111 11111111
		array = Converter.decToBytes("65535", length);
		// 0b 11111111 11111111
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xFF,
				(byte) 0xFF }));
		Assert.assertEquals(16, length.get());

		// 0b 00111010 11011110 01101000 10110001
		array = Converter.decToBytes("987654321", length);
		// 0b 11101011 01111001 10100010 11000100
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xEB, 0x79,
				(byte) 0xA2, (byte) 0xC4 }));
		Assert.assertEquals(30, length.get());

		// 0b 00001001 00011000 01010100 01101000 10101000 00011010
		array = Converter.decToBytes("10000100010010", length);
		// 0b 10010001 10000101 01000110 10001010 10000001 10100000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0x91,
				(byte) 0x85, 0x46, (byte) 0x8A, (byte) 0x81, (byte) 0xA0 }));
		Assert.assertEquals(44, length.get());

		// Int32.MaxValue = 2147483647
		// 0b 01111111 11111111 11111111 11111111
		array = Converter.decToBytes(Integer.toString(Integer.MAX_VALUE),
				length);
		// 0b 11111111 11111111 11111111 11111110
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFE }));
		Assert.assertEquals(31, length.get());

		// Int64.MaxValue = 9223372036854775807
		// 0b 01111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		array = Converter.decToBytes(Long.toString(Long.MAX_VALUE), length);
		// 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111110
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFE }));
		Assert.assertEquals(63, length.get());

		// 0b 10000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000
		array = Converter.decToBytes("9223372036854775808", length);
		// 0b 10000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000
		Assert.assertTrue(Arrays.equals(array, new byte[] { (byte) 0x80, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }));
		Assert.assertEquals(64, length.get());

		// Checking the result by using a invalid value for "string dec"
		try {
			array = Converter.decToBytes("", length);
			Assert.fail();
		} catch (NumberFormatException e) {
		}

		// Checking the result by using a negative number
		try {
			array = Converter.decToBytes("-1", length);
			Assert.fail();
		} catch (NumberFormatException e) {
		}
	}

	/**
	 * Test for hexToBytes
	 */
	@Test
	public void hexToBytesTest() {
		// The summary of the HexToBytes function is not complete.
		// There is no information which error message appears e.g.
		// when the length parameter is negative.

		byte[] actual = Converter.hexToBytes("0", 1);
		byte[] expected = new byte[] { 0x00 };
		Assert.assertTrue(Arrays.equals(actual, expected));

		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("", 8),
				new byte[] { 0x00 }));
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("", 4),
				new byte[] { 0x00 }));

		// Try to run the HextoBytes function with a negative lenght
		try {
			Converter.hexToBytes("0", -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length should be greater or equal to zero and lower then max value of integer minus size", e.getMessage());
		}

		// Try to run the HextoBytes function with a negative hex value
		try {
			Converter.hexToBytes("-1", 0);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Negative values are not allowed for 'hex'", e.getMessage());
		}

		// Try to run the HextoBytes function with a negative hex value and
		// negative length
		try {
			Converter.hexToBytes("-1", -1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Negative values are not allowed for 'hex'", e.getMessage());
		}

		// Try to run the HextoBytes function with a invalid hex value
		try {
			Converter.hexToBytes("k", 1);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		// 0b 00000000
		// = 0b 00000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("0", 8),
				new byte[] { 0x00 }));
		// 0b 00000001
		// = 0b 00000001
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1", 8),
				new byte[] { 0x01 }));
		// 0b 00000010
		// = 0b 00000010
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("2", 8),
				new byte[] { 0x02 }));
		// 0b 00000011
		// = 0b 00000011
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("3", 8),
				new byte[] { 0x03 }));
		// 0b 00000100
		// = 0b 00000100
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("4", 8),
				new byte[] { 0x04 }));
		// 0b 00000101
		// = 0b 00000101
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("5", 8),
				new byte[] { 0x05 }));
		// 0b 00000110
		// = 0b 00000110
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("6", 8),
				new byte[] { 0x06 }));
		// 0b 00000111
		// = 0b 00000111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("7", 8),
				new byte[] { 0x07 }));
		// 0b 00001000
		// = 0b 00001000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("8", 8),
				new byte[] { 0x08 }));
		// 0b 00001001
		// = 0b 00001001
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("9", 8),
				new byte[] { 0x09 }));
		// 0b 00001010
		// = 0b 00001010
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("A", 8),
				new byte[] { 0x0A }));
		// 0b 00001011
		// = 0b 00001011
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("B", 8),
				new byte[] { 0x0B }));
		// 0b 00001100
		// = 0b 00001100
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("C", 8),
				new byte[] { 0x0C }));
		// 0b 00001101
		// = 0b 00001101
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("D", 8),
				new byte[] { 0x0D }));
		// 0b 00001110
		// = 0b 00001110
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("E", 8),
				new byte[] { 0x0E }));
		// 0b 00001111
		// = 0b 00001111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("F", 8),
				new byte[] { 0x0F }));
		// 0b 00000000
		// = 0b 00000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("0", 4),
				new byte[] { 0x00 }));
		// 0b 00000001
		// = 0b 00010000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1", 4),
				new byte[] { 0x10 }));
		// 0b 00000010
		// = 0b 00100000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("2", 4),
				new byte[] { 0x20 }));
		// 0b 00000011
		// = 0b 00110000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("3", 4),
				new byte[] { 0x30 }));
		// 0b 00000100
		// = 0b 01000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("4", 4),
				new byte[] { 0x40 }));
		// 0b 00000101
		// = 0b 01010000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("5", 4),
				new byte[] { 0x50 }));
		// 0b 00000110
		// = 0b 01100000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("6", 4),
				new byte[] { 0x60 }));
		// 0b 00000111
		// = 0b 01110000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("7", 4),
				new byte[] { 0x70 }));
		// 0b 00001000
		// = 0b 10000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("8", 4),
				new byte[] { (byte) 0x80 }));
		// 0b 00001001
		// = 0b 10010000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("9", 4),
				new byte[] { (byte) 0x90 }));
		// 0b 00001010
		// = 0b 10100000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("A", 4),
				new byte[] { (byte) 0xA0 }));
		// 0b 00001011
		// = 0b 10110000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("B", 4),
				new byte[] { (byte) 0xB0 }));
		// 0b 00001100
		// = 0b 11000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("C", 4),
				new byte[] { (byte) 0xC0 }));
		// 0b 00001101
		// = 0b 11010000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("D", 4),
				new byte[] { (byte) 0xD0 }));
		// 0b 00001110
		// = 0b 11100000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("E", 4),
				new byte[] { (byte) 0xE0 }));
		// 0b 00001111
		// = 0b 11110000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("F", 4),
				new byte[] { (byte) 0xF0 }));

		// 0b 00000001
		// = 0b 10000000 00000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1", 9),
				new byte[] { (byte) 0x80, 0x00 }));
		// 0b 00000001
		// = 0b 00000010 00000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1", 15),
				new byte[] { 0x00, 0x02 }));
		// 0b 00000001
		// = 0b 00000000 00000000 00000010
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1", 23),
				new byte[] { 0x00, 0x00, 0x02 }));
		// 0b 00000001 11111010
		// = 0b 00000000 00000000 00001111 11010000
		expected = Converter.hexToBytes("1FA", 29);
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("1FA", 29),
				new byte[] { 0x00, 0x00, 0x0F, (byte) 0xD0 }));

		// 0b 11111111
		// = 0b 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FF", 8),
				new byte[] { (byte) 0xFF }));
		// 0b 11111111 11111111
		// = 0b 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFF", 16),
				new byte[] { (byte) 0xFF, (byte) 0xFF }));
		// 0b 11111111 11111111 11111111
		// = 0b 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFFFF", 24),
				new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }));
		// 0b 11111111 11111111 11111111 11111111
		// = 0b 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("FFFFFFFF", 32),
				new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }));
		// 0b 11111111 11111111 11111111 11111111 11111111
		// = 0b 11111111 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFFFFFFFF", 40),
				new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF }));
		// 0b 11111111 11111111 11111111 11111111 11111111 11111111
		// = 0b 11111111 11111111 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("FFFFFFFFFFFF", 48), new byte[] {
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF }));
		// 0b 11110000 00000000 00000110 00111001 10110000 11011110 00100000
		// = 0b 11110000 00000000 00000110 00111001 10110000 11011110 00100000
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("F0000639B0DE20", 56), new byte[] {
						(byte) 0xF0, 0x00, 0x06, 0x39, (byte) 0xB0,
						(byte) 0xDE, 0x20 }));
		// 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		// = 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("FFFFFFFFFFFFFFFF", 64), new byte[] {
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }));

		// 0b 00001111 11111111
		// = 0b 00001111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFF", 16),
				new byte[] { 0x0F, (byte) 0xFF }));
		// 0b 00001111 11111111 11111111
		// = 0b 00001111 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFFF", 24),
				new byte[] { 0x0F, (byte) 0xFF, (byte) 0xFF }));
		// 0b 00001111 11111111 11111111 11111111
		// = 0b 00001111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFFFFF", 32),
				new byte[] { 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }));
		// 0b 00001111 11111111 11111111 11111111 11111111
		// = 0b 00001111 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFFFFFFFF", 40),
				new byte[] { 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF }));
		// 0b 00001111 11111111 11111111 11111111 11111111 11111111
		// = 0b 00001111 11111111 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("FFFFFFFFFFF", 48), new byte[] { 0x0F,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF }));
		// 0b 00001111 11111111 11111111 11111111 11111111 11111111 11111111
		// = 0b 00001111 11111111 11111111 11111111 11111111 11111111 11111111
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("FFFFFFFFFFFFF", 56), new byte[] { 0x0F,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF }));
		// 0b 00001010 00111011 00011100 00110010 10000000 00000001 00100110
		// 01110001
		// = 0b 00001010 00111011 00011100 00110010 10000000 00000001 00100110
		// 01110001
		Assert.assertTrue(Arrays.equals(
				Converter.hexToBytes("A3B1C3280012671", 64), new byte[] { 0x0A,
						0x3B, 0x1C, 0x32, (byte) 0x80, 0x01, 0x26, 0x71 }));

		// 0b 11111111
		// = 0b 00111111 11000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FF", 10),
				new byte[] { 0x3F, (byte) 0xC0 }));
		// 0b 11111111
		// = 0b 00111111 11000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("FFF", 12),
				new byte[] { (byte) 0xFF, (byte) 0xF0 }));
		// 0b 00001111 00000000
		// = 0b 11110000 00000000
		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("F00", 12),
				new byte[] { (byte) 0xF0, 0x00 }));


		Assert.assertTrue(Arrays.equals(Converter.hexToBytes("F0"),
	            new byte[] { (byte) 0xF0 }));

	    Assert.assertTrue(Arrays.equals(Converter.hexToBytes("F000FF"),
                new byte[] { (byte) 0xF0, (byte) 0x00, (byte) 0xFF }));
	}

	/**
	 * Test for "set"
	 */
	@Test
	public void setTest() {
		// The summary of the Set function has spelling errors e.g. 'nummbers'

		// 0b 00000001
		byte[] bytes = new byte[] { 0x01 };
		// 0b 11111000
		byte[] expected = new byte[] { (byte) 0xF1 };
		// 0b 11110000
		byte[] data = new byte[] { (byte) 0xF0 };
		int offset = 0;
		int length = 4;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 00000000
		bytes = new byte[] { 0x00 };
		// 0b 11111111
		expected = new byte[] { (byte) 0xFF };
		// 0b 11111111
		data = new byte[] { (byte) 0xFF };
		offset = 0;
		length = 8;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 11111111
		bytes = new byte[] { (byte) 0xFF };
		// 0b 11111111
		expected = new byte[] { (byte) 0xFF };
		// 0b 00000000
		data = new byte[] { 0x00 };
		offset = 0;
		length = 0;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 00000000
		bytes = new byte[] { 0x00 };
		// 0b 11111111 11000000
		expected = new byte[] { (byte) 0xFF, (byte) 0xC0 };
		// 0b 11111111 11111111
		data = new byte[] { (byte) 0xFF, (byte) 0xFF };
		offset = 0;
		length = 10;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 10101010 10101010 10101010 10101010
		bytes = new byte[] { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA };
		// 0b 10101101 01010101 01010101 01101010
		expected = new byte[] { (byte) 0xAD, 0x55, 0x55, 0x6A };
		// 0b 10101010 10101010 10101010
		data = new byte[] { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA };
		offset = 5;
		length = 21;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 01100011 11010111 10101010
		bytes = new byte[] { 0x63, (byte) 0xD7, (byte) 0xAA };
		// 0b 10011010 10111101 10101010
		expected = new byte[] { (byte) 0x9A, (byte) 0xBD, (byte) 0xAA };
		// 0b 10011010 10111101
		data = new byte[] { (byte) 0x9A, (byte) 0xBD };
		offset = 0;
		length = 16;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 01100011 11010111 10101010
		bytes = new byte[] { 0x63, (byte) 0xD7, (byte) 0xAA };
		// 0b 01100011 10111111 10101010
		expected = new byte[] { (byte) 0x9A, (byte) 0xBF, (byte) 0xAA };
		// 0b 01100011 10111101
		data = new byte[] { (byte) 0x9A, (byte) 0xBD };
		offset = 0;
		length = 13;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		// 0b 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000 00000000
		expected = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		// 0b 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000
		// 00000000 00000000
		data = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		offset = 0;
		length = 128;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111
		bytes = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		// 0b 11111111 11111111 00000000 11111111 00000000 00000000 00000000
		// 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111 11111111
		expected = new byte[] { (byte) 0xFF, (byte) 0xFF, 0x00, (byte) 0xFF,
				0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF };
		// 0b 00000000 11111111 00000000 00000000 00000000 11111111
		data = new byte[] { 0x00, (byte) 0xFF, 0x00, 0x00, 0x00, (byte) 0xFF };
		offset = 16;
		length = 48;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// 0b 11111111 00001111
		bytes = new byte[] { (byte) 0xFF, 0x0F };
		// 0b 11111111 11111111
		expected = new byte[] { (byte) 0xFF, (byte) 0xFF };
		// 0b 11111111
		data = new byte[] { (byte) 0xFF };
		offset = 0;
		length = 12;
		bytes = Converter.set(bytes, data, offset, length);
		Assert.assertTrue(Arrays.equals(bytes, expected));

		// Is a negative offset value allowed?
		try {
			offset = -1;
			length = 13;
			bytes = new byte[] { 0x63, (byte) 0xD7, (byte) 0xAA };
			data = new byte[] { (byte) 0x9A, (byte) 0xBD };
			bytes = Converter.set(bytes, data, offset, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Offset is less than zero", e.getMessage());
		}

		// Is a negative length value allowed?
		try {
			offset = 1;
			length = -13;
			bytes = new byte[] { 0x63, (byte) 0xD7, (byte) 0xAA };
			bytes = Converter.set(bytes, data, offset, length);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("Length is less than zero", e.getMessage());
		}
	}

	/**
	 * Test for "toString"
	 */
	@Test
	public void toStringTest() {
		byte[] bytes = new byte[] { 0x00 };
		int length = 8;
		byte size = 4;
		String expected = "00";
		String actual = Converter.toString(bytes, length, size);
		Assert.assertEquals(expected, actual);

		// I guess there should be thrown an exception if the size is not 4 or 8
		try {
			Converter.toString(new byte[] { (byte) 0xFF, (byte) 0xFF }, 8, 7);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("size must be 4 or 8", e.getMessage());
		}

		// 0b 10000000
		// = 0b 00010000
		Assert.assertEquals("10",
				Converter.toString(new byte[] { (byte) 0x80 }, 1, 4));
		// 0b 10000000
		// = 0b 00000001
		Assert.assertEquals("01",
				Converter.toString(new byte[] { (byte) 0x80 }, 1, 8));
		// 0b 00100000
		// = 0b 00001000
		Assert.assertEquals("08", Converter.toString(new byte[] { 0x20 }, 2, 4));
		// 0b 10000000
		// = 0b 01000000
		Assert.assertEquals("40",
				Converter.toString(new byte[] { (byte) 0x80 }, 7, 4));
		// 0b 00001111
		// = 0b 00001111
		Assert.assertEquals("0F",
				Converter.toString(new byte[] { 0x0F }, 12, 4));
		// 0b 11111111
		// = 0b 11111111
		Assert.assertEquals("FF",
				Converter.toString(new byte[] { (byte) 0xFF }, 8, 8));
		// 0b 01011010
		// = 0b 00001011
		Assert.assertEquals("0B", Converter.toString(new byte[] { 0x5A }, 5, 8));
		// 0b 00001111 11111111
		// = 0b 00001111 11111111
		Assert.assertEquals("0FFF",
				Converter.toString(new byte[] { 0x0F, (byte) 0xFF }, 8, 8));
		// 0b 11111111 11111111
		// = 0b 11111111 11111111
		Assert.assertEquals("FFFF", Converter.toString(new byte[] {
				(byte) 0xFF, (byte) 0xFF }, 8, 8));
		// 0b 00001111 11111111 11111111
		// = 0b 00001111 11111111 11111111
		Assert.assertEquals("0FFFFF", Converter.toString(new byte[] { 0x0F,
				(byte) 0xFF, (byte) 0xFF }, 8, 8));
		// 0b 11111111 11111111 11111111
		// = 0b 11111111 11111111 11111111
		Assert.assertEquals(
				"FFFFFF",
				Converter.toString(new byte[] { (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF }, 8, 8));
		// 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		// = 0b 11111111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		Assert.assertEquals(
				"FFFFFFFFFFFFFFFF",
				Converter.toString(new byte[] { (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF }, 8, 4));
		// 0b 00001111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		// = 0b 00001111 11111111 11111111 11111111 11111111 11111111 11111111
		// 11111111
		Assert.assertEquals(
				"0FFFFFFFFFFFFFFF",
				Converter.toString(new byte[] { 0x0F, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF }, 8, 8));
	}

	@Test
	public void binaryToBytesTest() {
		// Test Null Value
		try {
			Converter.binaryToBytes(null);
			Assert.fail();
		} catch (NumberFormatException e) {
			Assert.assertEquals("Binary string value is null", e.getMessage());
		}

		// Test Negative Value
		try {
			Converter.binaryToBytes("-1");
			Assert.fail();
		} catch (NumberFormatException e) {
			Assert.assertEquals("Negative values are not allowed", e.getMessage());
		}

		// Test Invalid Digits
		try {
			Converter.binaryToBytes("2");
			Assert.fail();
		} catch (NumberFormatException e) {
			Assert.assertEquals("Binary contains invalid digits", e.getMessage());
		}
		try {
			Converter.binaryToBytes("101010101010101A");
			Assert.fail();
		} catch (NumberFormatException e) {
			Assert.assertEquals("Binary contains invalid digits", e.getMessage());
		}

		// Valid Values
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes(""),
				new byte[0]));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("0"),
				new byte[0]));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("000000000"),
				new byte[0]));

		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("1"),
				new byte[] { 0x01 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("10"),
				new byte[] { 0x02 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("11"),
				new byte[] { 0x03 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("100"),
				new byte[] { 0x04 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("110"),
				new byte[] { 0x06 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("111"),
				new byte[] { 0x07 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("1000"),
				new byte[] { 0x08 }));
		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("11111111"),
				new byte[] { (byte) 0xFF }));

		Assert.assertTrue(Arrays.equals(Converter.binaryToBytes("111111111"),
				new byte[] { 0x01, (byte) 0xFF }));
		Assert.assertTrue(Arrays.equals(
				Converter.binaryToBytes("1111111100000000"), new byte[] {
						(byte) 0xFF, 0x00 }));

	}

	@Test
	public void toDecimalStringTest() {
		Assert.assertEquals("0",
				Converter.toDecimalString(Converter.binaryToBytes("")));
		Assert.assertEquals("0",
				Converter.toDecimalString(Converter.binaryToBytes("0")));
		Assert.assertEquals("0",
				Converter.toDecimalString(new byte[] { 0, 0, 0, 0 }));

		Assert.assertEquals("1",
				Converter.toDecimalString(Converter.binaryToBytes("1")));
		Assert.assertEquals("1",
				Converter.toDecimalString(Converter.binaryToBytes("01")));
		Assert.assertEquals("1",
				Converter.toDecimalString(Converter.binaryToBytes("000000001")));

		Assert.assertEquals("2",
				Converter.toDecimalString(Converter.binaryToBytes("10")));
		Assert.assertEquals("3",
				Converter.toDecimalString(Converter.binaryToBytes("11")));
		Assert.assertEquals("5",
				Converter.toDecimalString(Converter.binaryToBytes("101")));
		Assert.assertEquals("15",
				Converter.toDecimalString(Converter.binaryToBytes("1111")));

		Assert.assertEquals("256",
				Converter.toDecimalString(Converter.binaryToBytes("100000000")));

		Assert.assertEquals("65536", Converter.toDecimalString(Converter
				.binaryToBytes("10000000000000000")));

		Assert.assertEquals("4294967295", Converter
				.toDecimalString(Converter
						.binaryToBytes("11111111111111111111111111111111")));
		Assert.assertEquals("8589934591", Converter.toDecimalString(Converter
				.binaryToBytes("111111111111111111111111111111111")));
		Assert.assertEquals(
				"18446744073709551616",
				Converter.toDecimalString(Converter
						.binaryToBytes("10000000000000000000000000000000000000000000000000000000000000000")));

		// Big Values
		Assert.assertEquals(
				"14879814827561585699475947521",
				Converter.toDecimalString(Converter
						.binaryToBytes("1100000001010001001011010110100001110001111000100100000000000000000000000000000000000000000001")));
		Assert.assertEquals(
				"14879814827561585699475947530",
				Converter.toDecimalString(Converter
						.binaryToBytes("1100000001010001001011010110100001110001111000100100000000000000000000000000000000000000001010")));
		Assert.assertEquals(
				"14880103057965225201914347521",
				Converter.toDecimalString(Converter
						.binaryToBytes("1100000001010010001000011000110001110011011010001110000000000000000000000000000000000000000001")));
		Assert.assertEquals(
				"14880103057965225201914347522",
				Converter.toDecimalString(Converter
						.binaryToBytes("1100000001010010001000011000110001110011011010001110000000000000000000000000000000000000000010")));
		Assert.assertEquals(
				"339457482556892400289558683384831160158009773522819714383872",
				Converter.toDecimalString(Converter
						.binaryToBytes("110110000101000010010101111011111101000110110110110110010110001011000001101000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000")));

		// Big Values
		Assert.assertEquals("14879814827561585699475947521", Converter
				.toDecimalString(Converter.hexToBytes(
						"30144B5A1C78900000000001", 96)));
		Assert.assertEquals("14879814827561585699475947530", Converter
				.toDecimalString(Converter.hexToBytes(
						"30144B5A1C7890000000000A", 96)));
		Assert.assertEquals("14880103057965225201914347521", Converter
				.toDecimalString(Converter.hexToBytes(
						"301488631CDA380000000001", 96)));
		Assert.assertEquals("14880103057965225201914347522", Converter
				.toDecimalString(Converter.hexToBytes(
						"301488631CDA380000000002", 96)));
		Assert.assertEquals(
				"339457482556892400289558683384831160158009773522819714383872",
				Converter.toDecimalString(Converter.hexToBytes(
						"3614257BF46DB658B068000000000000000000000000000000",
						200)));
	}

	@Test
	public void toArrayTest() {
	    List<Byte> data = new ArrayList<>();
	    data.add(Byte.valueOf((byte) 0xF0));
	    data.add(Byte.valueOf((byte) 0x00));
	    data.add(Byte.valueOf((byte) 0xFF));
	    byte[] actual = Converter.toArray(data);
	    byte[] expected = new byte[] { (byte) 0xF0, (byte) 0x00, (byte) 0xFF };
	    Assert.assertTrue(Arrays.equals(actual, expected));
	}

    @Test
    public void toListTest() {
        byte[] data = new byte[] { (byte) 0xF0, (byte) 0x00, (byte) 0xFF };
        List<Byte> actual = Converter.toList(data);
        List<Byte> expected = new ArrayList<>();
        expected.add(Byte.valueOf((byte) 0xF0));
        expected.add(Byte.valueOf((byte) 0x00));
        expected.add(Byte.valueOf((byte) 0xFF));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void joinTest() {
        String actual = Converter.join(new String[] { "first", "second", "third" }, '0' );
        String expected = "first0second0third";
        Assert.assertEquals(expected, actual);

        actual = Converter.join(new String[0], '0' );
        expected = "";
        Assert.assertEquals(expected, actual);
    }
}
