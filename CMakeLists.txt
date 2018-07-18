cmake_minimum_required(VERSION 3.11.4)
project(RiverCoin_cpp)

set(CMAKE_CXX_STANDARD 17)

FIND_PACKAGE(Boost REQUIRED)
INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIR})

set(OPENSSL_ROOT_DIR "/usr/local/Cellar/openssl/1.0.2o_2/")

FIND_PACKAGE(OPENSSL REQUIRED)
INCLUDE_DIRECTORIES(${OPENSSL_INCLUDE_DIR})

message("OPENSSL_FOUND is "  ${OPENSSL_FOUND})
message("OPENSSL_INCLUDE_DIR is "  ${OPENSSL_INCLUDE_DIR})
message("OPENSSL_CRYPTO_LIBRARY is "  ${OPENSSL_CRYPTO_LIBRARY})
message("OPENSSL_SSL_LIBRARY is "  ${OPENSSL_SSL_LIBRARY})
message("OPENSSL_LIBRARIES is "  ${OPENSSL_LIBRARIES})
message("OPENSSL_VERSION is "  ${OPENSSL_VERSION})

add_executable(RiverCoin_cpp core/main.cpp core/network.cpp core/network.h core/block.h core/security/security.h core/security/security.cpp core/transaction.h core/protocol.h core/hash.h core/logger.h core/tcp.h core/tree.h core/base58.h core/base58.cpp core/config.h core/context.cpp core/context.h core/compute/MochaPP/VM/instructions.h core/compute/MochaPP/VM/tinyvm.h core/compute/MochaPP/VM/tinyvm.cpp core/logger.cpp core/fileutils.h core/defines.h core/math/math.h core/math/riverhash.h core/math/hashutil.h core/serializeable.h core/blockchain.h core/blockchain.cpp core/async.h core/miner.cpp core/miner.h)
target_link_libraries(RiverCoin_cpp LINK_PUBLIC ${Boost_LIBRARIES})
target_link_libraries(RiverCoin_cpp LINK_PUBLIC ${OPENSSL_LIBRARIES})