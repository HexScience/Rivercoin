//
// Created by Abdullah Fadhil on 12.08.18.
//

#ifndef RIVERCOIN_CPP_SITE_H
#define RIVERCOIN_CPP_SITE_H

#include <map>
#include <set>
#include <vector>
#include <string>

class Context;

struct w_input_t{
};

struct w_output_t{
};

struct page_t{
    virtual void display();
    virtual w_output_t* POST(w_input_t* input);
};

struct site_t{
    std::set<std::string> _pages_;
    page_t* accesspage(Context* context, const std::string& page);
};

#endif //RIVERCOIN_CPP_SITE_H
