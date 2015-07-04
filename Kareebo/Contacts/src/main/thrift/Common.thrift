namespace java com.kareebo.contacts.common

typedef i64 Id
typedef binary ByteArray

struct UserAgent
{
    1: string platform,
    2: string version,
}

struct Client
{
    1: UserAgent userAgent,
    2: Id id,
}

enum Algorithm
{
    SHA256 = 1,
    SHA256withECDSAprime239v1 = 2,
    Fake = 3,
}


struct CryptoBuffer
{
    1: ByteArray buffer,
    2: Algorithm algorithm,
}

struct PublicKeys
{
    1: CryptoBuffer encryption,
    2: CryptoBuffer verification,
}

struct HashedContact
{
    1: Id id,
    2: CryptoBuffer hash,
}