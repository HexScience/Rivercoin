//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_FILEUTILS_H
#define RIVERCOIN_CPP_FILEUTILS_H

#include <fstream>
#include <string>
#include <sstream>
#include <vector>

#define ERR_NO_SUCH_FILE_EXISTS 1
#define ERR_IN_MAGIC_HEADER 2
#define ERR_NO_EXPORT 3
#define ERR_NO_ISTREAM 4

class file{
public:
    static std::string readUTF(std::string file, unsigned char& ERROR)
    {
        using std::string;
        using std::ifstream;
        using std::stringstream;

        ifstream stream(file.c_str());

        if (stream)
        {
            stringstream stringStream;

            stringStream << stream.rdbuf();

            stream.close();

            return stringStream.str();
        }

        ERROR = ERR_NO_ISTREAM;

        return "null";
    }

    static bool exists(const std::string file)
    {
        using std::ifstream;
        using std::ios;

        ifstream ifs(file, ios::binary|ios::ate);
        bool exist = ifs.good();

        ifs.close();

        return exist;
    }

    static std::vector<char> read(const std::string file, unsigned char& ERROR)
    {
        using std::ifstream;
        using std::ios;
        using std::vector;

        ifstream ifs(file, ios::binary|ios::ate);
        ifstream::pos_type pos = ifs.tellg();

        std::vector<char>  result(pos);

        if (ifs.good())
        {
            ifs.seekg(0, ios::beg);
            ifs.read(&result[0], pos);

            return result;
        }

        ERROR = ERR_NO_SUCH_FILE_EXISTS;

        return result;
    }

    template <typename T> static void read(const std::string file, T* o, unsigned char& ERROR)
    {
        using std::vector;

        vector<char> data = read(file, ERROR);

        memcpy(o, data.data(), sizeof(T));
    }

    template <typename T> static void read(const std::string file, const short magic_header, T* o, unsigned char& ERROR)
    {
        using std::vector;

        vector<char> data = read(file, ERROR);

        char header[2] = {data[0], data[1]};

        if (magic_header == ((const short *)(header))[0])
            memcpy(o, data.data(), sizeof(T));

        else ERROR = ERR_IN_MAGIC_HEADER;
    }

    template <typename T> static void write(const std::string file, const T& o, unsigned char& ERROR)
    {
        using std::ofstream;
        using std::ios;
        using std::vector;

        ofstream fout;
        fout.open(file, ios::binary | ios::out);

        if (fout.good())
        {
            fout.write((char*) &o, sizeof(o));

            fout.close();
        }
        else ERROR = ERR_NO_SUCH_FILE_EXISTS;
    }
};

#endif //RIVERCOIN_CPP_FILEUTILS_H
