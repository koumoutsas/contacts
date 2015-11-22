package com.kareebo.contacts.client.dataStructures;

import com.kareebo.contacts.thrift.SignatureAlgorithm;
import org.junit.Test;

import java.security.PrivateKey;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link SigningKey}
 */
public class SigningKeyTest
{
	@Test
	public void test()
	{
		final PrivateKey expectedKey=new PrivateKey()
		{
			@Override
			public String getAlgorithm()
			{
				return null;
			}

			@Override
			public String getFormat()
			{
				return null;
			}

			@Override
			public byte[] getEncoded()
			{
				return new byte[0];
			}
		};
		final SignatureAlgorithm expectedAlgorithm=SignatureAlgorithm.Fake;
		final SigningKey signingKey=new SigningKey(expectedKey,expectedAlgorithm);
		assertEquals(expectedAlgorithm,signingKey.algorithm);
		assertEquals(expectedKey,signingKey.key);
	}
}
