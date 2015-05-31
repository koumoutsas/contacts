include "Common.thrift"

namespace java org.kareebo.contacts.client

struct PrivateKeys
{
    1: Common.CryptoBuffer decryption,
    2: Common.CryptoBuffer signing,
}

struct User
{
    1: Common.Id id,
    2: Common.Client client,
    3: PrivateKeys keys,
}

enum ContactType
{
    MSISDN = 1,
    Email = 2,
    Facebook = 3,
    Twitter = 4,
    Skype = 5,
    Address = 6,
    Other = 7,
}

struct Contact
{
    1: Common.Id id,
    2: string target,
    3: ContactType type,
    4: map<string,string> attributes,
}

struct PersonalName
{
    1: string first,
    2: string middle,
    3: string last,
    4: string title,
}

struct ContactCard
{
    1: Common.Id id,
    2: PersonalName name,
    3: set<Contact> contacts,
}

typedef set<ContactCard> ContactBook
