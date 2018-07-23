//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#include "security.h"


bool Address::operator< (const Address& a) const
{
    return asuint256() < a.asuint256();
}

bool Address::operator>(const Address &a) const
{
    return asuint256() > a.asuint256();
}

bool Address::operator== (const Address& a) const
{
    return asuint256() == a.asuint256();
}

bool Address::operator<= (const Address& a) const
{
    return asuint256() <= a.asuint256();
}

bool Address::operator>= (const Address& a) const
{
    return asuint256() >= a.asuint256();
}

uint256 Address::asuint256() const
{
    return ByteUtil::fromBytes256(address_, ADDRESS_SIZE);
}

bool Address::compare(const Address &o) const
{
    for (int i = 0; i < ADDRESS_SIZE; i ++)
        if(address_[i] != o.address_[i]) return false;

    return true;
}

bool Address::__check_address_valid(const char *addr_)
{
    return false;
}

const char* Address::addr()
{
    return address_;
}

void Address::setAddress(const char *address)
{
    for (int i = 0; i < ADDRESS_SIZE; i ++)
        address_[i] = address[i];
}