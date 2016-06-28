namespace java com.kareebo.contacts.thrift.client

include "DataStructures.thrift"

const string Protocol="kareebo"

enum ContactType
{
	MSISDN=1,
	Email=2,
	Facebook=3,
	Skype=4,
	Twitter=5,
	Address=6,
	Other=7,
}

struct InvitationRequestLink
{
	1:string target,
	2:ContactType type,
	3:Identity userB,
}

struct Identity
{
	1:string target,
	2:ContactType type,
	3:DataStructures.HashBuffer blinded,
}
