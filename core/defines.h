//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_DEFINES_H
#define RIVERCOIN_CPP_DEFINES_H

#define VERSION 1000000001
#define UNIX_PLATFORM
#define LEDGER_USE_BALANCE
//#define LEDGER_USE_UTXO
//#define LEDGER_USE_BULLETPROOFS
//#define WINDOWS_PLATFORM

#ifdef UNIX_PLATFORM
#define LIB_PATH "~/Library/Riverssen/RiverCoin/"
#endif

#endif //RIVERCOIN_CPP_DEFINES_H
