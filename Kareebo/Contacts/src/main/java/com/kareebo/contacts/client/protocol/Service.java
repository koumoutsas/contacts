package com.kareebo.contacts.client.protocol;

import com.kareebo.contacts.client.jobs.Enqueuers;
import com.kareebo.contacts.client.jobs.FinalResultEnqueuer;
import com.kareebo.contacts.client.jobs.IntermediateResultEnqueuer;
import com.kareebo.contacts.crypto.SigningKey;
import com.kareebo.contacts.thrift.ClientId;
import com.kareebo.contacts.thrift.SignatureBuffer;
import com.kareebo.contacts.thrift.client.jobs.Context;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.async.TAsyncClient;
import org.apache.thrift.async.TAsyncClientManager;

import javax.annotation.Nonnull;
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
public abstract class Service<T extends TAsyncClient> extends com.kareebo.contacts.client.jobs.Service
{
	/// The Vertx client
	final protected T asyncClient;
	/// A {@link Signer} for signing payloads
	final private Signer signer;

	protected Service(final @Nonnull Context context,final @Nonnull TAsyncClientManager asyncClientManager,final @Nonnull SigningKey signingKey,final
	@Nonnull ClientId
		                                                                                                                                            clientId)
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
	abstract protected T construct(@Nonnull TAsyncClientManager asyncClientManager);

	protected <S extends TBase,R extends TBase> Set<R> sign(final @Nonnull Set<S> input,final @Nonnull BiFunction<S,SignatureBuffer,R> constructor)
		throws
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

	protected SignatureBuffer sign(final @Nonnull TBase object) throws InvalidKeyException, TException, NoSuchAlgorithmException,
		                                                                   NoSuchProviderException, SignatureException
	{
		return signer.sign(object);
	}

	protected abstract class Functor<S extends TBase> implements com.kareebo.contacts.client.jobs.Service.Functor
	{
		@Override
		public void run(@Nonnull final TBase payload,@Nonnull final Enqueuers enqueuers) throws Exception
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

		abstract protected void runInternal(@Nonnull T asyncClient,@Nonnull S payload,@Nonnull IntermediateResultEnqueuer
			                                                                              intermediateResultEnqueuer,
		                                    @Nonnull FinalResultEnqueuer
			                                    finalResultEnqueuer) throws Exception;
	}
}
