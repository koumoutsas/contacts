package com.kareebo.contacts.client.jobs;

import com.kareebo.contacts.thrift.client.jobs.ErrorCode;
import com.kareebo.contacts.thrift.client.jobs.JobType;
import com.kareebo.contacts.thrift.client.jobs.ServiceMethod;
import com.kareebo.contacts.thrift.client.jobs.SuccessCode;
import org.apache.thrift.TBase;

import javax.annotation.Nonnull;

public class EnqueuerImplementation implements FinalResultEnqueuer, IntermediateResultEnqueuer
{
	private ErrorCode errorCode;
	private SuccessCode successCode;
	private ServiceMethod method;
	private TBase payload;
	private JobType jobType;

	public boolean hasJob(final JobType type,final ServiceMethod method,final TBase payload)
	{
		return type==jobType&&errorCode==null&&method.equals(this.method)&&(this.payload==null?payload==null:this.payload.equals(payload));
	}

	public boolean isError(final JobType type,final ServiceMethod method,final ErrorCode errorCode)
	{
		return type==jobType&&payload==null&&this.method.equals(method)&&(this.errorCode==null?errorCode==null:this.errorCode.equals(errorCode));
	}

	public boolean isSuccess(final JobType type,final ServiceMethod method,final SuccessCode successCode)
	{
		return type==jobType&&payload==null&&this.method.equals(method)&&(this.successCode==null?successCode==null:this.successCode.equals
			                                                                                                                            (successCode));
	}

	public boolean initialState()
	{
		return method==null&&errorCode==null&&payload==null&&jobType==null;
	}

	void reset()
	{
		method=null;
		errorCode=null;
		payload=null;
		jobType=null;
	}

	@Override
	public void success(@Nonnull final SuccessJob job)
	{
		set(null,job.getResult(),new ServiceMethod(job.getService(),null),null,job.getType());
	}

	private void set(final ErrorCode errorCode,final SuccessCode successCode,final ServiceMethod serviceMethod,final TBase payload,final
	JobType jobType)
	{
		this.errorCode=errorCode;
		this.successCode=successCode;
		method=serviceMethod;
		this.payload=payload;
		this.jobType=jobType;
	}

	@Override
	public void error(@Nonnull final ErrorJob job)
	{
		set(job.getError(),null,job.getMethod(),null,job.getType());
	}

	@Override
	public void put(@Nonnull final IntermediateJob job)
	{
		set(null,null,job.getMethod(),job.getPayload(),job.getType());
	}
}
