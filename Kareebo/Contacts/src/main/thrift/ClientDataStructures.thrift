namespace java com.kareebo.contacts.thrift.client

include "DataStructures.thrift"

struct SigningKey
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.SignatureAlgorithm algorithm,
}