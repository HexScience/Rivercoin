//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#ifndef RIVERCOIN_CPP_UTILS_H
#define RIVERCOIN_CPP_UTILS_H

#include <memory>

#define Tptr std::shared_ptr<T>
#define Eptr std::shared_ptr<Element>

namespace stl{
    /** a linked hashset **/
    template <typename T> class lset{
    private:
        struct Element{
            Tptr value;
            Eptr next;

            Element() : next(nullptr) {}
            Element(T v) : value(v), next(nullptr) {}

            bool insert(T& v)
            {
                if (next.get() == nullptr) next = new Element(v);
                else if (*(next.get()->value.get()) == v) return false;
                else return next.get()->insert(v);

                return true;
            }
        };

        Eptr root;
    public:
        lset() : root(nullptr) {}

        bool insert(T& v)
        {
            if (root.get() == nullptr)
                root = Eptr(new Element(v));
            else return root.get()->insert(v);

            return true;
        }

        T peek()
        {
        }
    };
}


#endif //RIVERCOIN_CPP_UTILS_H
