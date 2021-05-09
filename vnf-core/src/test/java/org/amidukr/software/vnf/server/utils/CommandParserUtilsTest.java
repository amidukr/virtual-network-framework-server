package org.amidukr.software.vnf.server.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Dmytro Brazhnyk on 6/10/2017.
 */
public class CommandParserUtilsTest {

    @Test
    public void testWithTail() {
        Assert.assertArrayEquals(new String[]{"aaa", "bbb", "ccc\nddd"}, CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 3, true));
    }

    @Test
    public void testWithoutTail() {
        Assert.assertArrayEquals(new String[]{"aaa", "bbb", "ccc", "ddd"}, CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 4, false));
    }

    @Test
    public void testEmptyString() {
        Assert.assertArrayEquals(new String[]{}, CommandParserUtils.parseCommand("", 0, true));
        Assert.assertArrayEquals(new String[]{}, CommandParserUtils.parseCommand("", 0, false));
    }

    @Test
    public void testTailOnly() {
        Assert.assertArrayEquals(new String[]{"aaa\nbbb\nccc\nddd"}, CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 1, true));
    }

    @Test
    public void testFailWithNullMessage() {
        Assert.assertNull(CommandParserUtils.parseCommand(null, 3, false));
    }

    @Test
    public void testFailWithoutTail() {
        Assert.assertNull(CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 3, false));
    }

    @Test
    public void testFailTooFewWithTail() {
        Assert.assertNull(CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 5, true));
    }

    @Test
    public void testFailTooFewWithoutTail() {
        Assert.assertNull(CommandParserUtils.parseCommand("aaa\nbbb\nccc\nddd", 5, false));
    }
}
