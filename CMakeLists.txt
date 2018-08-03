cmake_minimum_required(VERSION 3.11.4)
project(RiverCoin_cpp)

set(CMAKE_CXX_STANDARD 17)

#due to high-sierra sucking the rot out of a half bitten apple, we have to set up the libraries manually.
IF(APPLE)
    set(OPENSSL_ROOT_DIR "/usr/local/Cellar/openssl/1.0.2o_2/")
    set(BOOST_ROOT "/usr/local/Cellar/boost/1.67.0_1")
#    set(Boost_INCLUDE_DIR "/usr/local/Cellar/boost/1.67.0_1/include/")
#    set(Boost_LIBRARIES "/usr/local/Cellar/boost/1.67.0_1/lib/")
#    INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIR})

    FIND_PACKAGE(Boost REQUIRED COMPONENTS filesystem system)
    INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIR})
ELSEIF(WIN32)
    FIND_PACKAGE(Boost REQUIRED)
    INCLUDE_DIRECTORIES(${Boost_INCLUDE_DIR})
endif()

FIND_PACKAGE(OPENSSL REQUIRED)
INCLUDE_DIRECTORIES(${OPENSSL_INCLUDE_DIR})

# copy the config file to the correct file-path in the build-directory.
file(COPY ${CMAKE_CURRENT_SOURCE_DIR}/resources/config.xml
        DESTINATION ${CMAKE_CURRENT_BINARY_DIR}/structure)

#message("OPENSSL_FOUND is "  ${OPENSSL_FOUND})
#message("OPENSSL_INCLUDE_DIR is "  ${OPENSSL_INCLUDE_DIR})
#message("OPENSSL_CRYPTO_LIBRARY is "  ${OPENSSL_CRYPTO_LIBRARY})
#message("OPENSSL_SSL_LIBRARY is "  ${OPENSSL_SSL_LIBRARY})
#message("OPENSSL_LIBRARIES is "  ${OPENSSL_LIBRARIES})
#message("OPENSSL_VERSION is "  ${OPENSSL_VERSION})

add_executable(RiverCoin_cpp core/main.cpp
        core/network.h core/network.cpp
        core/block.h core/block.cpp
        core/security/security.h core/security/security.cpp
        core/transaction.h core/transaction.cpp
        core/protocol.h core/hash.h core/logger.h core/logger.cpp core/tcp.h core/tree.h core/base58.h core/base58.cpp core/config.h core/context.cpp core/context.h core/compute/MochaPP/VM/instructions.h
        core/compute/MochaPP/VM/tinyvm.h core/compute/MochaPP/VM/tinyvm.cpp
        core/fileutils.h core/defines.h core/math/math.h core/math/riverhash.h core/math/hashutil.h core/serializeable.h
        core/blockchain.h core/blockchain.cpp
        core/async.h
        core/miner.h core/miner.cpp
        core/math/progpow.h core/math/progpow.cpp core/pools.cpp core/pools.h core/utils.cpp core/utils.h core/security/ecad.h core/security/ecad.cpp core/security/ecdsa.cpp core/security/ecdsa.h core/math/HashAlgorithm.cpp core/math/HashAlgorithm.h core/math/hashalgorithm.cpp core/math/hashalgorithm.h)
target_link_libraries(RiverCoin_cpp LINK_PUBLIC ${Boost_LIBRARIES})
target_link_libraries(RiverCoin_cpp LINK_PUBLIC ${OPENSSL_LIBRARIES})