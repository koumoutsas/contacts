package com.kareebo.contacts.base;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link Utils}
 */
public class UtilsTest
{
	static
	{
		// For 100% coverage
		new Utils();
	}

	@Test(expected=ClassNotFoundException.class)
	public void resolveClass() throws Exception
	{
		final Class<?> testClass=TestClass.class;
		final Class<?> thisClass=this.getClass();
		final String simpleName=thisClass.getSimpleName()+'$'+testClass.getSimpleName();
		final String packageName=thisClass.getPackage().getName();
		final String fullName=testClass.getName();
		assertEquals(testClass,Utils.resolveClass(simpleName,packageName));
		assertEquals(testClass,Utils.resolveClass(fullName,null));
		assertEquals(testClass,Utils.resolveClass(fullName,packageName));
		Utils.resolveClass(simpleName,null);
	}

	@Test
	public void getBytes() throws Exception
	{
		final byte[] expected="abc".getBytes();
		assertArrayEquals(expected,Utils.getBytes(ByteBuffer.wrap(expected)));
	}

	private static class TestClass
	{
	}
}