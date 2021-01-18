package havis.middleware.utils.data;

import org.junit.Assert;
import org.junit.Test;

public class ComparisonTest {

	/**
	 * Test for "compare"
	 */
	@Test
	public void compareTest() {
		// 0b 00000000
		byte[] left = new byte[] { 0x00 };
		// 0b 00000000
		byte[] right = new byte[] { 0x00 };

		int lenght = 8;
		int offset = 0;

		Assert.assertEquals(0, Comparison.compare(left, right, lenght, offset));

		// 0b 00000010
		left = new byte[] { 0x02 };
		// 0b 00000000
		right = new byte[] { 0x00 };
		lenght = 8;
		offset = 0;
		// Left > Right
		Assert.assertEquals(1, Comparison.compare(left, right, lenght, offset));

		// 0b 00000000
		left = new byte[] { 0x00 };
		// 0b 00000001
		right = new byte[] { 0x01 };
		lenght = 8;
		offset = 0;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 00000001 00000000
		left = new byte[] { 0x01, 0x00 };
		// 0b 00000000 11111111
		right = new byte[] { 0x00, (byte) 0xFF };
		lenght = 16;
		offset = 0;
		// Left > Right
		Assert.assertEquals(1, Comparison.compare(left, right, lenght, offset));

		// checking 11111111, 01111111 ... 00000001
		for (int i = 0; i < 8; i++) {
	        left = new byte[] { 0x00, (byte) (0xFF >> i) };
	        right = new byte[] { 0x00, (byte) 0xFF };
	        lenght = 8 - i;
	        offset = 8 + i;
	        // Left = Right
	        Assert.assertEquals(0, Comparison.compare(left, right, lenght, offset));

	        left = new byte[] { 0x00, (byte) (0xFF >> (i + 1)) };
	        Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));
		}

        // checking 10000000, 01000000 ... 00000001
        for (int i = 0; i < 8; i++) {
            left = new byte[] { 0x00, (byte) (0x80 >> i) };
            right = new byte[] { 0x00, (byte) 0xFF };
            lenght = 1;
            offset = 8 + i;
            // Left = Right
            Assert.assertEquals(0, Comparison.compare(left, right, lenght, offset));
            left = new byte[] { 0x00, 0x00 };
            // Left < Right
            Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));
        }

		// 0b 00000001 00000000
		left = new byte[] { 0x01, 0x00 };
		// 0b 11111111
		right = new byte[] { (byte) 0xFF };
		lenght = 8;
		offset = 0;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 00000001 00000000
		left = new byte[] { 0x01, 0x00 };
		// 0b 11111111
		right = new byte[] { (byte) 0xFF };
		lenght = 16;
		offset = 0;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// Try to catch an exception
		try {
			// 0b 00000000
			left = new byte[] { 0x00 };
			// 0b 11111111
			right = new byte[] { 0x00 };
			lenght = 16;
			offset = 0;
			Comparison.compare(left, right, lenght, offset);
			Assert.fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// empty arrays
		left = new byte[] {};
		right = new byte[] {};
		lenght = 0;
		offset = 0;
		Assert.assertEquals(0, Comparison.compare(left, right, lenght, offset));

		// Try to catch an exception
		try {
			left = new byte[] {};
			right = new byte[] {};
			lenght = 8;
			offset = 0;
			Comparison.compare(left, right, lenght, offset);
			Assert.fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// 0b 10000000
		left = new byte[] { (byte) 0x80 };
		// 0b 01110000
		right = new byte[] { 0x70 };
		lenght = 8;
		offset = 1;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 11111010
		left = new byte[] { (byte) 0xFA };
		// 0b 10101111
		right = new byte[] { (byte) 0xAF };
		lenght = 3;
		offset = 5;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 01111101 10101011
		left = new byte[] { 0x7F, (byte) 0xAB };
		// 0b 10111111 00000000
		right = new byte[] { (byte) 0xBF, 0x00 };
		lenght = 2;
		offset = 1;
		// Left > Right
		Assert.assertEquals(1, Comparison.compare(left, right, lenght, offset));

		// 0b 01111101 10101011
		left = new byte[] { 0x7D, (byte) 0xAB };
		// 0b 10111111 00000000
		right = new byte[] { (byte) 0xBF, 0x00 };
		lenght = 6;
		offset = 2;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 01111101 10101011 11111111 00000000 11000001 00110001
		left = new byte[] { 0x7F, (byte) 0xAB, (byte) 0xFF, 0x00, (byte) 0xC1,
				0x31 };
		// 0b 10111111 00000000 11110000 10100110 00010001 00000000
		right = new byte[] { (byte) 0xBF, 0x00, (byte) 0xF0, (byte) 0xA6, 0x11,
				0x00 };
		lenght = 48;
		offset = 0;
		// Left < Right
		Assert.assertEquals(-1, Comparison.compare(left, right, lenght, offset));

		// 0b 01111101 10101011 11111111 00000000 11000001 00110001
		left = new byte[] { 0x7F, (byte) 0xAB, (byte) 0xFF, 0x00, (byte) 0xC1,
				0x31 };
		// 0b 10111111 00000000 11110000 10100110 00010001 00000000
		right = new byte[] { (byte) 0xBF, 0x00, (byte) 0xF0, (byte) 0xA6, 0x11,
				0x00 };
		lenght = 44;
		offset = 2;
		// Left > Right
		Assert.assertEquals(1, Comparison.compare(left, right, lenght, offset));

	}

	/**
	 * Test for "equal"
	 */
	@Test
	public void equalTest() {
		// A TESTCASE FOR A SINGLE EQUAL TEST
		// Case1
		// 0b 11111111
		// 0b 11111111
		byte[] left = new byte[] { (byte) 0xFF };
		byte[] right = new byte[] { (byte) 0xFF };
		int length = 8;

		boolean actual = Comparison.equal(left, right, length, 0);
		Assert.assertTrue(actual);

		// 0b 00000000
		// 0b 11111111
		left = new byte[] { 0x00 };
		right = new byte[] { (byte) 0xFF };
		length = 8;

		actual = Comparison.equal(left, right, length, 0);
		Assert.assertFalse(actual);

		// 0b 00010010
		// 0b 10101011
		left = new byte[] { 0x12 };
		right = new byte[] { (byte) 0xAB };
		length = 8;

		actual = Comparison.equal(left, right, length, 0);
		Assert.assertFalse(actual);

		// 0b 11111110
		// 0b 11111110
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = 4;

		actual = Comparison.equal(left, right, length, 3);
		Assert.assertTrue(actual);

		// 0b 11111110
		// 0b 00000011
		left = new byte[] { (byte) 0xFE };
		right = new byte[] { 0x03 };
		length = 3;

		actual = Comparison.equal(left, right, length, 0);
		Assert.assertFalse(actual);

		// 0b 00000011
		// 0b 00000100
		left = new byte[] { 0x03 };
		right = new byte[] { 0x04 };
		length = 5;

		actual = Comparison.equal(left, right, length, 0);
		Assert.assertTrue(actual);

        // 0b 00000011
        // 0b 00000100
        left = new byte[] { 0x03 };
        right = new byte[] { 0x04 };
        length = 5;

        actual = Comparison.equal(left, right, length);
        Assert.assertTrue(actual);

		// A TESTCASE FOR A RANDOM EQUAL TEST
		// Case2
		// 0b 00000000 00000000 00000000 00000000
		// 0b 00000000 00000000 00000000 00000000
		left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		right = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		length = 16;

		actual = Comparison.equal(left, right, length, 2);
		Assert.assertTrue(actual);

		// 0b 00000000 11111111 00000000 00000000
		// 0b 00000000 00000000 00000000 00000000
		left = new byte[] { 0x00, (byte) 0xFF, 0x00, 0x00 };
		right = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		length = 7;

		actual = Comparison.equal(left, right, length, 17);
		Assert.assertTrue(actual);

		// 0b 10101011 11001111 01100111 00010011
		// 0b 10101011 11001111 01100111 00010011
		left = new byte[] { (byte) 0xAB, (byte) 0xCF, 0x67, 0x13 };
		right = new byte[] { (byte) 0xAB, (byte) 0xCF, 0x67, 0x13 };
		length = 7;

		actual = Comparison.equal(left, right, length, 23);
		Assert.assertTrue(actual);

		// 0b 10101011 11001110 01100111 00010011
		// 0b 10101011 11001111 01100111 00010011
		left = new byte[] { (byte) 0xAB, (byte) 0xCE, 0x67, 0x13 };
		right = new byte[] { (byte) 0xAB, (byte) 0xCF, 0x67, 0x13 };
		length = 24;

		actual = Comparison.equal(left, right, length, 0);
		Assert.assertFalse(actual);

		// A TESTCASE FOR A THROWN EXCEPTION IF ANY ARRAY IS OUT OF THE INDEX
		// Case3
		// 0b 10101011 11001111 01100111 00010011
		// 0b 10101011 11001111 01100111 00010011
		left = new byte[] { (byte) 0xAB, (byte) 0xCE, 0x67, 0x13 };
		right = new byte[] { (byte) 0xAB, (byte) 0xCF, 0x67, 0x13 };
		length = 24;
		try {
			actual = Comparison.equal(left, right, length, 62);
			Assert.fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// A TESTCASE FOR A RANDOM EQUAL TEST WITH MASK
		// Case4
        //  0b 00000000 00000000 00000000 00000000
        //  0b 00000000 00000000 00000000 00000000
        left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        right = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        byte[] mask = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
        length = 32;

        actual = Comparison.equal(left, right, length, mask, -1);
        Assert.assertTrue(actual);
        actual = Comparison.equal(left, right, length, mask, +1);
        Assert.assertTrue(actual);
        actual = Comparison.equal(left, right, length, mask, 0);
        Assert.assertTrue(actual);

        //  0b 00000000 11111111 00000000 00000000
        //  0b 00000000 00000000 00000000 00000000
        left = new byte[] { 0x00, (byte) 0xFF, 0x00, 0x00 };
        right = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        mask = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

        actual = Comparison.equal(left, right, 32, mask, -1);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 16, mask, -1);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 18, mask, -1);
        Assert.assertFalse(actual);

        mask = new byte[] { (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xFF };
        actual = Comparison.equal(left, right, 32, mask, -1);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 16, mask, -1);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 18, mask, -1);
        Assert.assertTrue(actual);

        //  0b 00000000 00000000 00000000 00000000
        //  0b 00000000 11111111 00000000 00000000
        left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        right = new byte[] { 0x00, (byte) 0xFF, 0x00, 0x00 };
        mask = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

        actual = Comparison.equal(left, right, 32, mask, +1);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 16, mask, +1);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 18, mask, +1);
        Assert.assertFalse(actual);

        mask = new byte[] { (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xFF };
        actual = Comparison.equal(left, right, 32, mask, +1);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 16, mask, +1);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 18, mask, +1);
        Assert.assertTrue(actual);

        //  0b 00000000 00000000 00000000 00000000
        //  0b 00000000 11111111 00000000 00000000
        left = new byte[] { 0x00, 0x00, 0x00, 0x00 };
        right = new byte[] { 0x00, (byte) 0xFF, 0x00, 0x00 };
        mask = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };

        actual = Comparison.equal(left, right, 32, mask, 0);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 16, mask, 0);
        Assert.assertFalse(actual);

        actual = Comparison.equal(left, right, 18, mask, 0);
        Assert.assertFalse(actual);

        mask = new byte[] { (byte) 0xFF, 0x00, (byte) 0xFF, (byte) 0xFF };
        actual = Comparison.equal(left, right, 32, mask, 0);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 16, mask, 0);
        Assert.assertTrue(actual);

        actual = Comparison.equal(left, right, 18, mask, 0);
        Assert.assertTrue(actual);

        byte[] tid = new byte[] { (byte)0xe2, (byte)0x80, (byte)0xb0, 0x40 };
        Assert.assertTrue(Comparison.equal(new byte[] { 0x00, 0x0B }, Calculator.shift(new byte[] { tid[1], tid[2] }, -4), 9, 7));
        Assert.assertTrue(Comparison.equal(new byte[] { 0x00, 0x40 }, new byte[] { tid[2], tid[3] }, 12, 4));
	}

	/**
	 * Test for "greater"
	 */
	@Test
	public void greaterTest() {
		// 0b 00000000
		byte[] left = new byte[] { 0x00 };
		// 0b 00000000
		byte[] right = new byte[] { 0x00 };
		// warum n und nicht length. Warum int und nicht uint?
		int length = 0;
		Assert.assertFalse(Comparison.greater(left, right, length));

		// 0b 00000000
		left = new byte[] { 0x00 };
		// 0b 00000000
		right = new byte[] { 0x00 };
		length = 8;
		Assert.assertFalse(Comparison.greater(left, right, length));

		// empty arrays
		left = new byte[] {};
		right = new byte[] {};
		length = 0;
		Assert.assertFalse(Comparison.greater(left, right, length));

		try {
			left = new byte[] {};
			right = new byte[] {};
			length = 8;
			Comparison.greater(left, right, length);
			Assert.fail();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		// 0b 00010000
		left = new byte[] { 0x10 };
		// 0b 00000000
		right = new byte[] { 0x00 };
		length = 8;
		Assert.assertTrue(Comparison.greater(left, right, length));

		// 0b 00100000
		left = new byte[] { 0x20 };
		// 0b 00010000
		right = new byte[] { 0x10 };
		length = 8;
		Assert.assertTrue(Comparison.greater(left, right, length));

		// 0b 00100000
		left = new byte[] { 0x20 };
		// 0b 00010000
		right = new byte[] { 0x10 };
		length = 1;
		Assert.assertFalse(Comparison.greater(left, right, length));

		// 0b 11111111 11111111 11111111
		left = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		// 0b 11111111 11111111 11111110
		right = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFE };
		length = 23;
		Assert.assertFalse(Comparison.greater(left, right, length));

		// 0b 11111010 11111111 10001000 10001000 10110001
		left = new byte[] { (byte) 0xFA, (byte) 0xFF, (byte) 0x88, (byte) 0x88,
				(byte) 0xB1 };
		// 0b 11111001 10101010 10011001 00000000 11111111
		right = new byte[] { (byte) 0xF9, (byte) 0xAA, (byte) 0x99, 0x00,
				(byte) 0xFF };
		length = 40;
		Assert.assertTrue(Comparison.greater(left, right, length));
	}

	/**
	 * Test for "lower"
	 */
	@Test
	public void lowerTest() {
		// A TESTCASE FOR A SINGLE LOWER TEST#
		// Case1
		// 0b 11111111
		// 0b 11111111
		byte[] left = new byte[] { (byte) 0xFF };
		byte[] right = new byte[] { (byte) 0xFE };
		int length = 8;
		boolean actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// 0b 00000000
		// 0b 11111111
		left = new byte[] { 0x00 };
		right = new byte[] { (byte) 0xFF };
		length = 8;
		actual = Comparison.lower(left, right, length);
		Assert.assertTrue(actual);

		// 0b 00010010
		// 0b 10101011
		left = new byte[] { 0x12 };
		right = new byte[] { (byte) 0xAB };
		length = 8;
		actual = Comparison.lower(left, right, length);
		Assert.assertTrue(actual);

		// 0b 11111110
		// 0b 11111110
		left = new byte[] { 0x00 };
		right = new byte[] { 0x00 };
		length = 4;

		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// 0b 11111110
		// 0b 00000011
		left = new byte[] { (byte) 0xFE };
		right = new byte[] { 0x03 };
		length = 3;

		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// 0b 00000011
		// 0b 00000100
		left = new byte[] { 0x03 };
		right = new byte[] { 0x04 };
		length = 5;

		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// A TESTCASE FOR A RANDOM LOWER TEST
		// Case2
		// 0b 11111111 11111111 11111111 11111111
		// 0b 00000000 00000000 00000000 00000000
		left = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		right = new byte[] { 0x00, 0x00, 0x00, 0x00 };
		length = 16;
		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// 0b 11111111 11111111 11111111 11111111
		// 0b 11111111 11111111 11111111 11111111
		left = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		right = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		length = 16;
		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);

		// 0b 11111111 11111111 11111111 11111110
		// 0b 11111111 11111111 11111111 11111111
		left = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE };
		right = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		length = 40;
		actual = Comparison.lower(left, right, length);
		Assert.assertTrue(actual);

		// 0b 11111111 11111111 11111111 11111111
		// 0b 00000000 00000000 00000000 00000000
		left = new byte[] { (byte) 0xAB, (byte) 0xC9, 0x15, (byte) 0xFF };
		right = new byte[] { (byte) 0xAB, (byte) 0xCA, 0x15, (byte) 0xFF };
		length = 19;
		actual = Comparison.lower(left, right, length);
		Assert.assertTrue(actual);

		// 0b 11111111 11111111 11111111 11111111
		// 0b 00000000 00000000 00000000 00000000
		left = new byte[] { (byte) 0xAB, (byte) 0xC9, 0x15, (byte) 0xFF };
		right = new byte[] { (byte) 0xAB, (byte) 0xCA, 0x15, (byte) 0xFF };
		length = 9;
		actual = Comparison.lower(left, right, length);
		Assert.assertFalse(actual);
	}

	@Test
    public void hashCodeTest()
    {
        // 11111010 11111111 10001000 10001000 10110001
        byte[] data = new byte[] { (byte) 0xFA, (byte) 0xFF, (byte) 0x88, (byte) 0x88, (byte) 0xB1 };
        int actual = Comparison.hashCode(data);
        int expected = -1383303187;
        Assert.assertEquals(expected, actual);

        // 11111111 00000000
        data = new byte[] { (byte) 0xFF, (byte) 0x00 };
        actual = Comparison.hashCode(data);
        expected = -1995555438;
        Assert.assertEquals(expected, actual);

        // 10101010 10111011 11001100
        data = new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC };
        actual = Comparison.hashCode(data);
        expected = -591013234;
        Assert.assertEquals(expected, actual);

        // empty
        data = new byte[0];
        actual = Comparison.hashCode(data);
        expected = 0;
        Assert.assertEquals(expected, actual);

        // 11010000
        data = new byte[] { (byte) 0xD0 };
        actual = Comparison.hashCode(data);
        expected = 961335599;
        Assert.assertEquals(expected, actual);

        // 11011011 01001100 stipped to 11010000
        data = new byte[] { (byte) 0xDB, (byte) 0x4C };
        actual = Comparison.hashCode(data, 6, 6);
        expected = 961335599;
        Assert.assertEquals(expected, actual);

        // 11011011 01001100 stipped to 10011000
        data = new byte[] { (byte) 0xDB, (byte) 0x4C };
        actual = Comparison.hashCode(data, 9, 7);
        expected = -202227876;
        Assert.assertEquals(expected, actual);

        // too long, will be cut: 11011011 01001100 stipped to 10011000
        data = new byte[] { (byte) 0xDB, (byte) 0x4C };
        actual = Comparison.hashCode(data, 9, 8);
        expected = -202227876;
        Assert.assertEquals(expected, actual);

        // offset overflow
        data = new byte[] { (byte) 0xDB, (byte) 0x4C };
        actual = Comparison.hashCode(data, 17, 1);
        expected = 0;
        Assert.assertEquals(expected, actual);
    }
}
