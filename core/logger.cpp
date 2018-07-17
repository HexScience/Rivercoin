//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#include "logger.h"
#include "defines.h"

void logger::msg(unsigned int color, const char *msg)
{
    #ifdef WINDOWS_PLATFORM
        std::cout << msg << std::endl;
    #elif defined(UNIX_PLATFORM)
        std::string color_;
            color_.push_back((char)27);

            switch (color)
            {
                case COLOR_RED: color_ = color_ + "[31m";
                    break;
                case COLOR_GRN: color_ = color_ + "[32m";
                    break;
                case COLOR_LME: color_ = color_ + "[32m";
                    break;
                case COLOR_YLW: color_ = color_ + "[33m";
                    break;
                case COLOR_BLU: color_ = color_ + "[34m";
                    break;
                case COLOR_WTE: color_ = color_ + "[27m";
                    break;
            }


            std::cout << color_ << msg << std::endl;
    #endif
}

void logger::alert(const char *msg_)
{
    msg(COLOR_GRN, msg_);
}

void logger::err(const char *msg_)
{
    msg(COLOR_RED, msg_);
}