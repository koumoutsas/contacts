namespace java com.kareebo.contacts.thrift

typedef i64 Id
typedef binary ByteArray

struct UserAgent
{
	1:string platform,
	2:string version,
}

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

enum SignatureAlgorithm
{
	SHA256withECDSAprime239v1=1,
	Fake=2,
}

struct ClientId
{
	1:Id user,
	2:Id client,
}

struct SignatureBuffer
{
	1:ByteArray buffer,
	2:SignatureAlgorithm algorithm,
	3:ClientId client,
}

enum EncryptionAlgorithm
{
	RSA2048=1,
	Fake=2,
}

struct EncryptedBuffer
{
	1:ByteArray buffer,
	2:EncryptionAlgorithm algorithm,
	3:ClientId client,
}

struct EncryptionKey
{
	1:ByteArray buffer,
	2:EncryptionAlgorithm algorithm,
}

struct VerificationKey
{
	1:ByteArray buffer,
	2:SignatureAlgorithm algorithm,
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
	1:HashBuffer contact,
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
	2:HashBuffer u,
}

exception FailedOperation
{
}

struct RegisterIdentityInput
{
	1:PublicKeys publicKeys,
	2:HashBuffer uA,
	3:Id userIdA,
	4:set<HashBuffer> uSet,
	5:HashBuffer uJ,
	6:UserAgent userAgent,
	7:i64 deviceToken,
}

service RegisterIdentity
{
	// Steps 10-14
	RegisterIdentityReply registerIdentity1(1:HashBuffer uA,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Steps 19-22
	RegisterIdentityReply registerIdentity2(1:Id userIdA) throws (1:FailedOperation failedOperation),

	// Steps 30-35
	void registerIdentity3(1:RegisterIdentityInput registerIdentityInput,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

struct HashBufferSet
{
	1:set<HashBuffer> hashBuffers,
}

service RegisterUnconfirmedIdentity
{
	// Steps 8-12
	void registerUnconfirmedIdentity1(1:HashBufferSet uSet,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),
}

struct HashBufferPair
{
	1:HashBuffer UC,
	2:HashBuffer UPrimeC,
}

struct EncryptedBufferPairSet
{
	1:set<EncryptedBufferPair> encryptedBufferPairs,
}

service BroadcastNewContactIdentity
{
	// Steps 3-5
	map<ClientId,EncryptionKey> broadcastNewContactIdentity1(1:LongId userIdB,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),

	// Steps 11-15
	map<ClientId,EncryptionKey> broadcastNewContactIdentity2(1:EncryptedBufferPairSet encryptedBufferPairs,2:SignatureBuffer signature) throws
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
	void sendContactCard1(1:HashBuffer u,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Step 9
	EncryptionKeys sendContactCard2(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Step 16
	void sendContactCard3(1:set<EncryptedBufferSigned> encryptedBuffers) throws (1:FailedOperation failedOperation),

	// Step 19.a
	EncryptedBufferSignedWithVerificationKey sendContactCard4(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation
	failedOperation),
}

service SuggestNewContact
{
	// Step 2.a
	EncryptionKeysWithHashBuffer suggestNewContact1(1:LongId id,2:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

	// Steps 2.e-2.f
	void suggestNewContact2(1:set<EncryptedBufferSigned> encryptedBuffers,2:HashBuffer uB,3:SignatureBuffer signature) throws (1:FailedOperation failedOperation),

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