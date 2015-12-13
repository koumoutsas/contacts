package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.*;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Client-side implementation of the register identity service
 */
public class RegisterIdentity extends Service<com.kareebo.contacts.thrift.RegisterIdentity.VertxClient>
{
	public static final String serviceName=RegisterIdentity.class.getSimpleName();
	public final static ServiceMethod method1=new ServiceMethod(serviceName,"1");
	public final static ServiceMethod method2=new ServiceMethod(serviceName,"2");
	public final static ServiceMethod method3=new ServiceMethod(serviceName,"3");

	RegisterIdentity(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(asyncClientManager,signingKey,clientId);
	}

	@Override
	protected com.kareebo.contacts.thrift.RegisterIdentity.VertxClient construct(final TAsyncClientManager asyncClientManager)
	{
		return new com.kareebo.contacts.thrift.RegisterIdentity.VertxClient(asyncClientManager);
	}

	@Override
	protected void runInternal(final com.kareebo.contacts.thrift.client.jobs.ServiceMethod method,final TBase payload,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws Exception
	{
		if(method.equals(method1))
		{
			registerIdentity1((HashBuffer)payload,intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method2))
		{
			registerIdentity2(((LongId)payload).getId(),intermediateResultEnqueuer,finalResultEnqueuer);
		}
		else if(method.equals(method3))
		{
			registerIdentity3((RegisterIdentityInput)payload,finalResultEnqueuer);
		}
		else
		{
			throw new NoSuchMethod();
		}
	}

	private void registerIdentity1(final HashBuffer uA,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                                                                                                                     NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity1(uA,sign(uA),new IntermediateResultHandler<RegisterIdentityReply>(intermediateResultEnqueuer,
			                                                                                              com.kareebo.contacts.client.processor.RegisterIdentity.method1,
			                                                                                              finalResultEnqueuer,method1
		));
	}

	private void registerIdentity2(final long userIdA,final IntermediateResultEnqueuer intermediateResultEnqueuer,final FinalResultEnqueuer finalResultEnqueuer)
	{
		asyncClient.registerIdentity2(userIdA,new IntermediateResultHandler<RegisterIdentityReply>(intermediateResultEnqueuer,com.kareebo.contacts
			                                                                                                                      .client.processor.RegisterIdentity.method2,finalResultEnqueuer,method2));
	}

	private void registerIdentity3(final RegisterIdentityInput registerIdentityInput,final FinalResultEnqueuer enqueuer) throws InvalidKeyException,
		                                                                                                                            TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		asyncClient.registerIdentity3(registerIdentityInput,sign(registerIdentityInput),new FinalResultHandler(enqueuer,method3));
	}
}
