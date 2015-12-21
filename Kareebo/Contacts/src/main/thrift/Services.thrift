namespace java com.kareebo.contacts.thrift

include "DataStructures.thrift"

typedef i64 Id

struct UserAgent
{
	1:string platform,
	2:string version,
}

struct ClientId
{
	1:Id user,
	2:Id client,
}

struct SignatureBuffer
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.SignatureAlgorithm algorithm,
	3:ClientId client,
}

struct EncryptedBuffer
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.EncryptionAlgorithm algorithm,
	3:ClientId client,
}

struct EncryptionKey
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.EncryptionAlgorithm algorithm,
}

struct VerificationKey
{
	1:DataStructures.ByteArray buffer,
	2:DataStructures.SignatureAlgorithm algorithm,
}

struct PublicKeys
{
	1:EncryptionKey encryption,
	2:VerificationKey verification,
}

enum ContactOperationType
{
	Add=1,
	Delete=2,
	Update=3,
	Fake=4,
}

struct ContactOperation
{
	1:DataStructures.HashBuffer contact,
	2:ContactOperationType type,
	3:EncryptedBuffer comparisonIdentity,
}

struct ContactOperationSet
{
	1:set<ContactOperation> contactOperations,
}

typedef binary RandomHashPad

struct RegisterIdentityReply
{
	1:Id id,
	2:RandomHashPad blind,
}

struct EncryptedBufferPair
{
	1:EncryptedBuffer i,
	2:EncryptedBuffer iR,
}

struct EncryptedBufferSigned
{
	1:EncryptedBuffer encryptedBuffer,
	2:SignatureBuffer signature,
}

struct EncryptedBufferSignedWithVerificationKey
{
	1:EncryptedBufferSigned encryptedBufferSigned,
	2:VerificationKey verificationKey,
}

// Wrapper around i64 to allow the usage of Thrift serializers with long
struct LongId
{
	1:Id id,
}

struct EncryptionKeys
{
	1:Id userId,
	2:map<Id,EncryptionKey> keys,
}

struct EncryptionKeysWithHashBuffer
{
	1:EncryptionKeys encryptionKeys,
	2:DataStructures.HashBuffer u,
}

exception FailedOperation
{
}

struct RegisterIdentityInput
{
	1:PublicKeys publicKeys,
	2:DataStructures.HashBuffer uA,
	3:Id userIdA,
	4:set<DataStructures.HashBuffer> uSet,
	5:DataStructures.HashBuffer uJ,
	6:UserAgent userAgent,
	7:i64 deviceToken,
}

service RegisterIdentity
{
	// Steps 10-14
	RegisterIdentityReply registerIdentity1(1:DataStructures.HashBuffer uA,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Steps 19-22
	RegisterIdentityReply registerIdentity2(1:Id userIdA) throws (1:FailedOperation failedOperation),

	// Steps 30-35
	void registerIdentity3(1:RegisterIdentityInput registerIdentityInput,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

struct HashBufferSet
{
	1:set<DataStructures.HashBuffer> hashBuffers,
}

service RegisterUnconfirmedIdentity
{
	// Steps 8-12
	void registerUnconfirmedIdentity1(1:HashBufferSet uSet,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

struct HashBufferPair
{
	1:DataStructures.HashBuffer UC,
	2:DataStructures.HashBuffer UPrimeC,
}

struct EncryptedBufferPairSet
{
	1:set<EncryptedBufferPair> encryptedBufferPairs,
}

struct MapClientIdEncryptionKey
{
	1:map<ClientId,EncryptionKey> keyMap,
}

struct SetEncryptedBuffer
{
	1:set<EncryptedBuffer> bufferSet,
}

service BroadcastNewContactIdentity
{
	// Steps 3-5
	MapClientIdEncryptionKey broadcastNewContactIdentity1(1:LongId userIdB,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),

	// Steps 11-15
	MapClientIdEncryptionKey broadcastNewContactIdentity2(1:EncryptedBufferPairSet encryptedBufferPairs,2:SignatureBuffer signature) throws
	(1:FailedOperation failedOperation),

	// Steps 19-21
	void broadcastNewContactIdentity3(1:set<EncryptedBufferSigned> encryptedBuffers) throws (1:FailedOperation failedOperation),

	// Step 22
	EncryptedBufferSignedWithVerificationKey broadcastNewContactIdentity4(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),

	// Steps 26.c-26.g
	void broadcastNewContactIdentity5(1:HashBufferPair uCs,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

service ModifyKeys
{
	// Steps 3-8
	void modifyKeys1(1:PublicKeys newPublicKeys,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

service ModifyUserAgent
{
	// Steps 3-8
	void modifyUserAgent1(1:UserAgent userAgent,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

service UpdateServerContactBook
{
	// Steps 3-7
	void updateServerContactBook1(1:ContactOperationSet contactOperationSet,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

service SendContactCard
{
	// Steps 5-7
	void sendContactCard1(1:DataStructures.HashBuffer u,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Step 9
	EncryptionKeys sendContactCard2(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Step 16
	void sendContactCard3(1:set<EncryptedBufferSigned> encryptedBuffers) throws (1:FailedOperation failedOperation),

	// Step 19.a
	EncryptedBufferSignedWithVerificationKey sendContactCard4(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),
}

struct EncryptedBuffersWithHashBuffer
{
	1:set<EncryptedBuffer> buffers,
	2:DataStructures.HashBuffer uB,
}

service SuggestNewContact
{
	// Step 2.a
	EncryptionKeysWithHashBuffer suggestNewContact1(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Steps 2.e-2.f
	void suggestNewContact2(1:set<EncryptedBufferSigned> encryptedBuffers,2:DataStructures.HashBuffer uB,3:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Step 3
	EncryptedBufferSignedWithVerificationKey suggestNewContact3(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),
}

service ConfirmIdentity
{
}

service RegisterNewClient
{
}