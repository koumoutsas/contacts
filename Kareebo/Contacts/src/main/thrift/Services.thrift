include "Client.thrift"
include "Common.thrift"

namespace java com.kareebo.contacts.thrift

typedef string RegistrationCode

struct IdPair
{
    1: Common.Id userId,
    2: Common.Id clientId,
}

struct Signature
{
    1: Common.ByteArray signature,
    2: IdPair ids,
}

typedef Common.ByteArray EncryptedData

exception InvalidArgument
{
    1: string what,
}

service UserRegistration
{
    IdPair userRegistration1(1: Common.Handle handle, 2: Common.UserAgent userAgent, 3: Common.PublicKeys publicKeys) throws (1: InvalidArgument invalidArgument),
    void userRegistration2(1: RegistrationCode code, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

service DeviceRegistration
{
    IdPair deviceRegistration1(1: Common.Handle handle,2: Common.UserAgent userAgent, 3: Common.PublicKeys publicKeys) throws (1: InvalidArgument invalidArgument),
    void deviceRegistration2(1: RegistrationCode code, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

service ModifyKeys
{
    void modifyKeys1(1: Common.PublicKeys newPublicKeys, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

service ModifyUserAgent
{
    void modifyUserAgent1(1: Common.UserAgent userAgent, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

service ModifyHandle
{
    void modifyHandle1(1: Common.Handle newHandle, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
    void modifyHandle2(1: RegistrationCode code, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

enum ContactOperationType
{
    Add = 1,
    Delete = 2,
}

struct ContactOperation
{
    1: Common.HashedContact contact,
    2: ContactOperationType type,
}

typedef set<ContactOperation> ContactOperationSet

service UpdateServerContactBook
{
    void updateServerContactBook1(1: ContactOperationSet contactOperationSet, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

struct EncryptedContact
{
    1: Common.HashedContact hashedContact,
    2: Common.CryptoBuffer encrypted,
}

typedef set<EncryptedContact> EncryptedContactSet

service UpdateLocalContactBook
{
    EncryptedContactSet updateLocalContactBook1(1: string fixed, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}

struct EncryptionKey
{
    1: Common.Id clientId,
    2: Common.CryptoBuffer encryption,
}

typedef set<EncryptionKey> EncryptionKeySet

struct UploadedContact
{
    1: IdPair ids,
    2: Common.CryptoBuffer encrypted,
}

typedef set<UploadedContact> UploadedContactSet

service UploadEncryptedContactCard
{
    EncryptionKeySet uploadEncryptedContactCard1(1: Common.Id userId, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
    void uploadEncryptedContactCard2(1: UploadedContactSet uploadedContactSet, 2: Signature signature) throws (1: InvalidArgument invalidArgument),
}