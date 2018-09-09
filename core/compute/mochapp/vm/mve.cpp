//
// Created by Abdullah Fadhil on 08.09.18.
//

#include "mve.h"

ReferenceCounter::ReferenceCounter(unsigned short references, unsigned char *object) : mReferences(references), mObject(object)
{
}

Reference::Reference(unsigned char* p) : mReference(new ReferenceCounter(1, p))
{
}

Reference::Reference(const Reference& o) : mReference(o.mReference)
{
    mReference->mReferences ++;
}

Reference::~Reference()
{
    mReference->mReferences --;

    if (mReference->mReferences < 1)
        delete (mReference->mObject);

    delete mReference;
}
