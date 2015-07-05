package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.base.PlaintextSerializer;
import com.kareebo.contacts.base.StringPlaintextSerializer;
import com.kareebo.contacts.common.Algorithm;
import com.kareebo.contacts.common.PublicKeys;
import com.kareebo.contacts.server.gora.RegistrationCode;
import com.kareebo.contacts.server.gora.RegistrationCodeId;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.InvalidArgument;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.gora.util.GoraException;
import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for {@link UserRegistration}
 */
public class UserRegistrationTest extends SignatureVerifierTestBase
{
	final private long expirationTime=100;
	final private String code="abc";
	private DataStore<RegistrationCodeId,RegistrationCode> registrationCodeDataStore;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	PlaintextSerializer constructPlaintext()
	{
		return new StringPlaintextSerializer(code);
	}

	@Override
	SignatureVerifier construct(final DataStore<Long,User> dataStore)
	{
		try
		{
			registrationCodeDataStore=DataStoreFactory.getDataStore(RegistrationCodeId.class,
				                                                       RegistrationCode.class,new Configuration());
			return new UserRegistration(dataStore,registrationCodeDataStore,expirationTime);
		}
		catch(GoraException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Test
	public void testUserRegistration1() throws Exception
	{
		final RegistrationCode registrationCode=new RegistrationCode();
		final RegistrationCodeId registrationCodeId=new RegistrationCodeId();
		registrationCodeId.setUserId(idPairValid.getUserId());
		registrationCodeId.setClientId(idPairValid.getClientId());
		registrationCodeId.setCode(code);
		registrationCode.setId(registrationCodeId);
		registrationCode.setCreated(new Date().getTime());
		registrationCodeDataStore.put(registrationCodeId,registrationCode);
		final Future<Void> result=new DefaultFutureResult<>();
		((UserRegistration)signatureVerifier).userRegistration1(code,signature,TypeConverter
			                                                                                 .convert(clientValid
				                                                                                          .getUserAgent()),TypeConverter
					                                                                                                           .convert(clientValid.getKeys()),result);
		assertTrue(result.succeeded());
	}

	@Test
	public void testUserRegistration1ExpiredCode() throws Exception
	{
		final RegistrationCode registrationCode=new RegistrationCode();
		final RegistrationCodeId registrationCodeId=new RegistrationCodeId();
		registrationCodeId.setUserId(idPairValid.getUserId());
		registrationCodeId.setClientId(idPairValid.getClientId());
		registrationCodeId.setCode(code);
		registrationCode.setId(registrationCodeId);
		registrationCode.setCreated(new Date().getTime()-TimeUnit.SECONDS.toMillis(expirationTime+1));
		registrationCodeDataStore.put(registrationCodeId,registrationCode);
		final Future<Void> result=new DefaultFutureResult<>();
		((UserRegistration)signatureVerifier).userRegistration1(code,signature,TypeConverter
			                                                                                 .convert(clientValid
				                                                                                          .getUserAgent()),TypeConverter
					                                                                                                           .convert(clientValid.getKeys()),result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}

	@Test
	public void testUserRegistration1InvalidAlgorithm() throws Exception
	{
		final RegistrationCode registrationCode=new RegistrationCode();
		final RegistrationCodeId registrationCodeId=new RegistrationCodeId();
		registrationCodeId.setUserId(idPairValid.getUserId());
		registrationCodeId.setClientId(idPairValid.getClientId());
		registrationCodeId.setCode(code);
		registrationCode.setId(registrationCodeId);
		registrationCode.setCreated(new Date().getTime());
		registrationCodeDataStore.put(registrationCodeId,registrationCode);
		final Future<Void> result=new DefaultFutureResult<>();
		final PublicKeys publicKeys=TypeConverter.convert(clientValid.getKeys());
		publicKeys.getEncryption().setAlgorithm(Algorithm.Fake);
		((UserRegistration)signatureVerifier).userRegistration1(code,signature,TypeConverter
			                                                                                 .convert(clientValid
				                                                                                          .getUserAgent()),publicKeys,result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}

	@Test
	public void testUserRegistration1InvalidCode() throws Exception
	{
		final Future<Void> result=new DefaultFutureResult<>();
		((UserRegistration)signatureVerifier).userRegistration1(code,signature,TypeConverter
			                                                                                 .convert(clientValid
				                                                                                          .getUserAgent()),TypeConverter
					                                                                                                           .convert(clientValid.getKeys()),result);
		assertTrue(result.failed());
		//noinspection ThrowableResultOfMethodCallIgnored
		assertEquals(InvalidArgument.class,result.cause().getClass());
	}
}
