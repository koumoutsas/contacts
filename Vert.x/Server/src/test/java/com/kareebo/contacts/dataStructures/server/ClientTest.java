package com.kareebo.contacts.dataStructures.server;

import com.kareebo.contacts.dataStructures.common.Id;
import com.kareebo.contacts.dataStructures.common.PublicKeys;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashSet;

/**
 * Unit test for {@link com.kareebo.contacts.dataStructures.server.Client}
 */
public class ClientTest
{
	private PublicKeys publicKeys;
	private HashSet<Contact> contacts=new HashSet<Contact>();

	@Before
	public void setUp() throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECCurve curve=new ECCurve.Fp(
			                                  new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839"), // q
			                                  new BigInteger("7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc",16), // a
			                                  new BigInteger("6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a",16)); // b
		final ECParameterSpec ecSpec=new ECParameterSpec(
			                                                curve,
			                                                curve.decodePoint(Hex.decode("020ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf")), // G
			                                                new BigInteger("883423532389192164791648750360308884807550341691627752275345424702807307")); // n
		final String algorithm="ECDSA";
		final KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance(algorithm,"BC");
		keyPairGenerator.initialize(ecSpec,new SecureRandom());
		publicKeys=new PublicKeys(keyPairGenerator.generateKeyPair().getPublic().getEncoded(),algorithm,keyPairGenerator.generateKeyPair().getPublic().getEncoded(),algorithm);
		final byte[] hash=new byte[2];
		contacts.add(new Contact(new Id(1),hash,algorithm));
		contacts.add(new Contact(new Id(2),hash,algorithm));
	}

	@Test
	public void testGetPublicKeys() throws Exception
	{
		org.junit.Assert.assertTrue(new Client("","",new Id(1),true,publicKeys,contacts).isRegistered());
		org.junit.Assert.assertFalse(new Client("","",new Id(1),false,publicKeys,contacts).isRegistered());
	}

	@Test
	public void testIsRegistered() throws Exception
	{
		org.junit.Assert.assertEquals(publicKeys,new Client("","",new Id(1),true,publicKeys,contacts).getPublicKeys());
	}

	@Test
	public void testGetContacts() throws Exception
	{
		org.junit.Assert.assertEquals(contacts,new Client("","",new Id(1),true,publicKeys,contacts).getContacts());
	}
}