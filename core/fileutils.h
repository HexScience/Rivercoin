//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_FILEUTILS_H
#define RIVERCOIN_CPP_FILEUTILS_H

#include <fstream>
#include <string>
#include <sstream>
#include <vector>

class file{
public:
    static std::string readUTF(std::string file)
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

    static std::vector<char> read(const std::string file)
    {
        using std::ifstream;
        using std::ios;
        using std::vector;

        ifstream ifs(file, ios::binary|ios::ate);
        ifstream::pos_type pos = ifs.tellg();

        std::vector<char>  result(pos);

        ifs.seekg(0, ios::beg);
        ifs.read(&result[0], pos);

        return result;
    }

    template <typename T> static void read(const std::string file, T* o)
    {
        using std::vector;

        vector<char> data = read(file);

        memcpy(o, data.data(), sizeof(T));
    }

    template <typename T> static void write(const std::string file, const T& o)
    {
        using std::ofstream;
        using std::ios;
        using std::vector;

        ofstream fout;
        fout.open(file, ios::binary | ios::out);

        fout.write((char*) &o, sizeof(o));

        fout.close();
    }
};

#endif //RIVERCOIN_CPP_FILEUTILS_H
