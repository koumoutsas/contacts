package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.dataStructures.SigningKey;
import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Extension of {@link com.kareebo.contacts.client.jobs.Service} with a {@link TAsyncClient} and a {@link Signer}
 */
abstract class Service<T extends TAsyncClient> extends com.kareebo.contacts.client.jobs.Service
{
	/// The Vertx client
	final protected T asyncClient;
	/// A {@link Signer} for signing payloads
	final private Signer signer;

	protected Service(final Context context,final TAsyncClientManager asyncClientManager,final SigningKey signingKey,final ClientId clientId)
	{
		super(context);
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

	protected <S extends TBase,R extends TBase> Set<R> sign(final Set<S> input,final BiFunction<S,SignatureBuffer,R> constructor) throws
		InvalidKeyException,
			TException,
			NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		final Set<R> ret=new HashSet<>(input.size());
		for(S t : input)
		{
			ret.add(constructor.apply(t,sign(t)));
		}
		return ret;
	}

	protected SignatureBuffer sign(final TBase object) throws InvalidKeyException, TException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException
	{
		return signer.sign(object);
	}

	protected abstract class Functor<S extends TBase> implements com.kareebo.contacts.client.jobs.Service.Functor
	{
		@Override
		public void run(final TBase payload,final Enqueuers enqueuers) throws Exception
		{
			final IntermediateResultEnqueuer intermediateResultEnqueuer=enqueuers.intermediateResultEnqueuer(JobType.Processor);
			if(intermediateResultEnqueuer==null)
			{
				throw new IllegalArgumentException("No enqueuer for the processor job type");
			}
			// The cast error exception is caught in com.kareebo.contacts.client.jobs.Service#run
			//noinspection unchecked
			runInternal(asyncClient,(S)payload,intermediateResultEnqueuer,enqueuers.finalResultEnqueuer());
		}

		abstract protected void runInternal(T asyncClient,S payload,IntermediateResultEnqueuer intermediateResultEnqueuer,
		                                    FinalResultEnqueuer
			                                    finalResultEnqueuer) throws Exception;
	}
}
