namespace java com.kareebo.contacts.thrift.client.jobs

typedef string Service

struct ServiceMethod
{
	1:Service serviceName,
        2:string methodName,
}

struct Notification
{
	1:ServiceMethod method,
	3:i64 id,
}

enum SuccessCode
{
	Ok=1,
}

enum ErrorCode
{
	Failure=1,
}

enum JobType
{
	Protocol=1,
	Processor=2,
	ExternalService=3,
}
