package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.crypto.Utils;
import com.kareebo.contacts.server.gora.*;
import com.kareebo.contacts.thrift.IdPair;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ModifyKeysAsyncIface}
 */
public class ModifyKeysAsyncIfaceTest
{
	final static private String ecdsa="ECDSA";
	private final IdPair idPairValid=new IdPair();
	private final IdPair idPairInvalid=new IdPair();
	private final com.kareebo.contacts.thrift.Signature signature=new com.kareebo.contacts.thrift.Signature();
	private final com.kareebo.contacts.thrift.Signature wrongSignature=new com.kareebo.contacts.thrift.Signature();
	private final com.kareebo.contacts.common.PublicKeys newPublicKeys=new com.kareebo.contacts.common.PublicKeys();
	private CryptoBuffer verificationKey;
	private ModifyKeysAsyncIface iface;

	@Before
	public void setUp() throws Exception
	{
		setUpCrypto();
		final long userId=0;
		long clientId=0;
		idPairValid.setClientId(clientId++);
		idPairValid.setUserId(userId);
		idPairInvalid.setClientId(clientId);
		idPairInvalid.setUserId(userId);
		final Client client=new Client();
		final UserAgent userAgent=new UserAgent();
		userAgent.setPlatform("A");
		userAgent.setVersion("B");
		final PublicKeys publicKeys=new PublicKeys();
		final byte[] buffer={'a','b'};
		publicKeys.setEncryption(TypeConverter.convert(setUpCryptoBuffer(buffer)));
		publicKeys.setVerification(verificationKey);
		client.setUserAgent(userAgent);
		client.setKeys(publicKeys);
		client.setRegistered(true);
		client.setContacts(new ArrayList<HashedContact>());
		final User user=new User();
		final Handle handle=new Handle();
		handle.setType(HandleType.EmailAddress);
		handle.setContents("a");
		user.setHandle(handle);
		user.setClients(new HashMap<CharSequence,Client>());
		final DataStore<Long,User> dataStore=DataStoreFactory.getDataStore(Long.class,User.class,new Configuration());
		dataStore.put(userId,user);
		iface=new ModifyKeysAsyncIface(dataStore);
		iface.put(idPairValid,client);
	}

	private void setUpCrypto() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException, InvalidKeySpecException
	{
		Security.addProvider(new BouncyCastleProvider());
		final ECParameterSpec ecSpec=ECNamedCurveTable.getParameterSpec("prime192v1");
		final KeyPairGenerator g=KeyPairGenerator.getInstance(ecdsa,Utils.getProvider());
		g.initialize(ecSpec,new SecureRandom());
		final KeyPair keyPair=g.generateKeyPair();
		final Vector<byte[]> plaintext=new Vector<>(2);
		plaintext.add("abc".getBytes());
		plaintext.add("cde".getBytes());
		newPublicKeys.setEncryption(setUpCryptoBuffer(plaintext.elementAt(0)));
		newPublicKeys.setVerification(setUpCryptoBuffer(plaintext.elementAt(1)));
		Signature ecdsaSign=Signature.getInstance("SHA256withECDSA",Utils.getProvider());
		ecdsaSign.initSign(keyPair.getPrivate());
		for(final Object a : plaintext)
		{
			ecdsaSign.update((byte[])a);
		}
		signature.setIds(idPairValid);
		signature.setSignature(ecdsaSign.sign());
		ecdsaSign.update("fgh".getBytes());
		wrongSignature.setIds(idPairValid);
		wrongSignature.setSignature(ecdsaSign.sign());
		verificationKey=TypeConverter.convert(setUpCryptoBuffer(new X509EncodedKeySpec(
			                                                                              keyPair.getPublic().getEncoded
				                                                                                                  ())
			                                                        .getEncoded()));
	}

	private com.kareebo.contacts.common.CryptoBuffer setUpCryptoBuffer(final byte[] buffer)
	{
		final com.kareebo.contacts.common.CryptoBuffer cryptoBuffer=new com.kareebo.contacts.common.CryptoBuffer();
		cryptoBuffer.setAlgorithm(com.kareebo.contacts.common.Algorithm.SHA256withECDSAprime239v1);
		final ByteBuffer byteBuffer=ByteBuffer.wrap(buffer);
		byteBuffer.mark();
		cryptoBuffer.setBuffer(byteBuffer);
		return cryptoBuffer;
	}

	@Test
	public void testModifyKeys1() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		iface.modifyKeys1(newPublicKeys,signature,result);
		assertTrue(result.succeeded());
	}

	@Test
	public void testModifyKeys1InvalidUser() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		signature.setIds(idPairInvalid);
		try
		{
			iface.modifyKeys1(newPublicKeys,signature,result);
		}
		catch(Exception e)
		{
			signature.setIds(idPairValid);
			throw e;
		}
		signature.setIds(idPairValid);
		assertTrue(result.failed());
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}

	@Test
	public void testModifyKeys1CorruptedSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final ByteBuffer signatureBuffer=signature.bufferForSignature();
		signatureBuffer.rewind();
		final byte[] falseSignatureBytes=new byte[signatureBuffer.remaining()];
		signatureBuffer.get(falseSignatureBytes);
		falseSignatureBytes[0]=(byte)(falseSignatureBytes[0]+1);
		final ByteBuffer falseSignatureBuffer=ByteBuffer.wrap(falseSignatureBytes);
		falseSignatureBuffer.mark();
		final com.kareebo.contacts.thrift.Signature falseSignature=new com.kareebo.contacts.thrift.Signature();
		falseSignature.setIds(idPairValid);
		falseSignature.setSignature(falseSignatureBuffer);
		iface.modifyKeys1(newPublicKeys,falseSignature,result);
		assertTrue(result.failed());
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}

	@Test
	public void testModifyKeys1InvalidAlgorithm() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		final Client client=iface.get(idPairValid);
		final Algorithm algorithm=client.getKeys().getVerification().getAlgorithm();
		client.getKeys().getVerification().setAlgorithm(Algorithm.SHA256);
		try
		{
			iface.put(client);
			iface.modifyKeys1(newPublicKeys,signature,result);
		}
		catch(Exception e)
		{
			client.getKeys().getVerification().setAlgorithm(algorithm);
			throw e;
		}
		client.getKeys().getVerification().setAlgorithm(algorithm);
		assertTrue(result.failed());
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}

	@Test
	public void testModifyKeys1InvalidSignature() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		iface.modifyKeys1(newPublicKeys,wrongSignature,result);
		assertTrue(result.failed());
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}
}