#ifndef RIVERCOIN_H

#define RIVERCOIN_H
#define MAX_NANO_COINS 100000000000
#define MAX_NANO_COIN_LENGTH 11
#define cast_nano_rvc(x) rvc((unsigned long long) x)

#include <string>

/**
 * Max uint64 = 18.446.744.073.709.551.615
 * max / MAX_NANO_COINS = 184.467.440,73.709.551.615
 */

class rvc {
private:
    unsigned long long nanocoin;
public:
    rvc(unsigned long long nanocoin_value) : nanocoin(nanocoin_value) {}
    rvc(double rvc_value) : nanocoin((unsigned long long) (rvc_value * MAX_NANO_COINS)) {}
    rvc(const rvc& o) : nanocoin(o.nanocoin) {}

    unsigned long long asLong()
    {
        return nanocoin;
    }

    double asDouble()
    {
        return (double)nanocoin / MAX_NANO_COINS;
    }

    std::string asPlainString()
    {
        std::string start_ = std::to_string((unsigned long long)asDouble());
        std::string string = std::to_string(nanocoin).substr(start_.length());

        return start_ + "." + string;
    }

    rvc operator= (unsigned long long n)
    {
        return rvc(n);
    }

    rvc operator= (unsigned int n)
    {
        return rvc((unsigned long long)n);
    }

    rvc operator= (int n)
    {
        return rvc((unsigned long long)n);
    }

    bool operator== (const rvc& o) const
    {
        return nanocoin == o.nanocoin;
    }
};

#endif