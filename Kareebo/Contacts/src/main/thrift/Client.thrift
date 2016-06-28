namespace java com.kareebo.contacts.thrift.client

include "DataStructures.thrift"

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

struct DecryptionKey
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.EncryptionAlgorithm algorithm,
}

struct SigningKey
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.SignatureAlgorithm algorithm,
}

struct PrivateKeys
{
	1:DecryptionKey decryption,
	2:SigningKey signing,
}

struct BroadcastIdentities
{
	1:set<DataStructures.HashBuffer> identities,
}

const string Protocol="kareebo"
