//
// Created by Ragnarr Ivarssen on 10.07.18.
//

#ifndef RIVERCOIN_CPP_LOGGER_H
#define RIVERCOIN_CPP_LOGGER_H

#include <iostream>
#include <string>

#define COLOR_RED 0
#define COLOR_GRN 1
#define COLOR_BLU 2
#define COLOR_YLW 3
#define COLOR_LME 4
#define COLOR_WTE 5

//Black        0;30     Dark Gray     1;30
//Red          0;31     Light Red     1;31
//Green        0;32     Light Green   1;32
//Brown/Orange 0;33     Yellow        1;33
//Blue         0;34     Light Blue    1;34
//Purple       0;35     Light Purple  1;35
//Cyan         0;36     Light Cyan    1;36
//Light Gray   0;37     White         1;37

#ifdef WINDOWS_PLATFORM
#endif

namespace logger
{
    void msg(unsigned int color, const char* msg);
    void alert(const char* msg_);
    void err(const char* msg_);
}

#endif //RIVERCOIN_CPP_LOGGER_H
