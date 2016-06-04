namespace java com.kareebo.contacts.thrift

typedef binary ByteArray

enum HashAlgorithm
{
	SHA256=1,
	Fake=2,
}

struct HashBuffer
{
	1:ByteArray buffer,
	2:HashAlgorithm algorithm,
}

enum EncryptionAlgorithm
{
	RSA2048=1,
	Fake=2,
}

enum SignatureAlgorithm
{
	SHA512withECDSAprime239v1=1,
	Fake=2,
}
