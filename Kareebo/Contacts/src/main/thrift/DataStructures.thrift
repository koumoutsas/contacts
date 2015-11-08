namespace java com.kareebo.contacts.thrift

struct NotificationMethod
{
	1:string serviceName,
        2:string methodName,
}

struct Notification
{
	1:NotificationMethod method,
	3:i64 id,
}