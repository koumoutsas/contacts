namespace java com.kareebo.contacts.thrift

struct ServiceMethod
{
	1:string serviceName,
        2:string methodName,
}

struct Notification
{
	1:ServiceMethod method,
	3:i64 id,
}
