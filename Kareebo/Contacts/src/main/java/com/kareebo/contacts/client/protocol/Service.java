package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Extension of {@link com.kareebo.contacts.client.jobs.Service} with a {@link TAsyncClient} and a {@link Signer}
 */
abstract class Service<T extends TAsyncClient> extends com.kareebo.contacts.client.jobs.Service
{
	/// The Vertx client
	final protected T asyncClient;
	/// A {@link Signer} for signing payloads
	final private Signer signer;

	Service(final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		signer=new Signer(signingKey,clientId);
		asyncClient=construct(asyncClientManager);
	}

	/**
	 * Factory method for creating the Vertx async client
	 *
	 * @param asyncClientManager The async client manager
	 * @return The Vertx async client
	 */
	abstract protected T construct(TAsyncClientManager asyncClientManager);

	@Override
	protected void runInternal(final ServiceMethod method,TBase payload,final Enqueuers enqueuers) throws Exception
	{
		final IntermediateResultEnqueuer intermediateResultEnqueuer=enqueuers.intermediateResultEnqueuer(JobType.Protocol);
		if(intermediateResultEnqueuer==null)
		{
			throw new IllegalArgumentException("No enqueuer for the job type");
		}
		runInternal(method,payload,intermediateResultEnqueuer,enqueuers.finalResultEnqueuer());
	}

	abstract protected void runInternal(ServiceMethod method,TBase payload,IntermediateResultEnqueuer intermediateResultEnqueuer,FinalResultEnqueuer finalResultEnqueuer) throws Exception;

	protected SignatureBuffer sign(final TBase object) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		return signer.sign(object);
	}
}
