package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * This is the entry point to the job processing part of the client. It is an implementation of {@link Runnable} that on each cycle pulls a
 * {@link Job} from a queue implementing {@link Dequeuer} and passes it to the {@link Dispatcher} that is assigned to the {@link JobType}
 */
class Runner implements Runnable
{
	private static final Logger logger=LoggerFactory.getLogger(Runner.class.getName());
	final private Map<JobType,Dispatcher> dispatchers;
	final private Dequeuer dequeuer;

	/**
	 * Create a {@link Runner} from a {@link Map} from {@link JobType} to {@link Dispatcher} and a {@link Dequeuer} implementation
	 *
	 * @param dispatchers The {@link Dispatcher} map
	 * @param dequeuer    The queue providing {@link Job}s
	 */
	Runner(final @Nonnull Map<JobType,Dispatcher> dispatchers,final @Nonnull Dequeuer dequeuer)
	{
		this.dispatchers=dispatchers;
		this.dequeuer=dequeuer;
	}

	/**
	 * Pulls the next {@link Job} and dispatches it to the appropriate {@link Dispatcher} according to its {@link JobType}
	 * @throws RuntimeException When one of the following happens: there is no {@link Dispatcher} matched to the {@link JobType}, there is no
	 * {@link Service} or {@link com.kareebo.contacts.thrift.client.jobs.ServiceMethod} registered to the {@link Dispatcher}, or the execution
	 * of the {@link Job} failed. You can inspect the {@link RuntimeException#getCause()} to get the cause of the exception
	 */
	@Override
	public void run()
	{
		final Job job=dequeuer.get();
		if(job==null)
		{
			return;
		}
		final JobType type=job.getType();
		final Dispatcher dispatcher=dispatchers.get(type);
		if(dispatcher==null)
		{
			final String errorMessage="No service dispatcher for job type "+type;
			logger.error(errorMessage);
			throw new RuntimeException(errorMessage);
		}
		try
		{
			job.dispatch(dispatcher);
		}
		catch(Service.NoSuchMethod|Service.ExecutionFailed|ServiceDispatcher.NoSuchService e)
		{
			logger.error("Unable to execute job",e);
			throw new RuntimeException(e);
		}
	}
}
