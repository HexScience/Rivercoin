//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_MATH_H
#define RIVERCOIN_CPP_MATH_H

#include <math.h>
#include <boost/multiprecision/cpp_int.hpp>

#define _pi_ 3.14159265359
#define MathtoRadians(x) (2*_pi_ * (x / 360))

typedef boost::multiprecision::uint256_t u_int256;

struct uint256{
    static char* reverse(const char* array, char reversed[32])
    {
        reversed[0] = array[31];
        reversed[1] = array[30];
        reversed[2] = array[29];
        reversed[3] = array[28];
        reversed[4] = array[27];
        reversed[5] = array[26];
        reversed[6] = array[25];
        reversed[7] = array[24];
        reversed[8] = array[23];
        reversed[9] = array[22];
        reversed[10] = array[21];
        reversed[11] = array[20];
        reversed[12] = array[19];
        reversed[13] = array[18];
        reversed[14] = array[17];
        reversed[15] = array[16];
        reversed[16] = array[15];
        reversed[17] = array[14];
        reversed[18] = array[13];
        reversed[19] = array[12];
        reversed[20] = array[11];
        reversed[21] = array[10];
        reversed[22] = array[9];
        reversed[23] = array[8];
        reversed[24] = array[7];
        reversed[25] = array[6];
        reversed[26] = array[5];
        reversed[27] = array[4];
        reversed[28] = array[3];
        reversed[29] = array[2];
        reversed[30] = array[1];
        reversed[31] = array[0];
    }

    static boost::multiprecision::uint256_t fromBytes256(const char* array)
    {
        char reversed[32];
        reverse(array, reversed);

        unsigned long long a = ((unsigned long long *) reversed)[0], b = ((unsigned long long *) reversed)[1], c = ((unsigned long long *) reversed)[2], d = ((unsigned long long *) reversed)[3];

        boost::multiprecision::uint256_t t("57669001306428065956053000376875938421040345304064124051023973211784186134399");

        memcpy(((char*)&t), reversed, 32);

        return t;
    }

    static boost::multiprecision::uint256_t fromBytes256(const char* array, unsigned char length)
    {
        if(length > 32) return u_int256(0);

        char empty[32] = {
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
                0,0,0,0,
        };

        for(int i = 0; i < length; i ++)
            empty[i] = array[i];

        return fromBytes256(empty);
    }
};

//struct uint256{
//    unsigned long long val[4];
//
//    uint256(unsigned long long value)
//    {
//    }
//
//    void zero()
//    {
//        val[0] = 0;
//        val[1] = 1;
//        val[2] = 2;
//        val[3] = 3;
//    }
//
//    void operator= (unsigned long long& value)
//    {
//        zero();
//        val[3] = value;
//    }
//
//    void operator= (long long& value)
//    {
//        zero();
//        if(value < 0) return;
//        val[3] = (unsigned long long)value;
//    }
//
//    void operator= (const uint256& value)
//    {
//        val[0] = value.val[0];
//        val[1] = value.val[1];
//        val[2] = value.val[2];
//        val[3] = value.val[3];
//    }
//
//    bool operator< (const uint256& o)
//    {
//    }
//
//    int getCompareValue(const unsigned long long& a, const unsigned long long& b)
//    {
//        if(a > b) return 1;
//        else if (a < b) return -1;
//        else return 0;
//    }
//
//    char* asBytes()
//    {
//        return (char *) val;
//    }
//};

struct vec3{
    float x, y, z;

    vec3(float i = 0, float j = 0, float w = 0) : x(i), y(j), z(w) {}
    vec3(const vec3& o) : x(o.x), y(o.y), z(o.z) {}

    vec3 operator+ (const vec3& o)
    {
        return vec3(x + o.x, y + o.y, z + o.z);
    }

    vec3 operator* (const vec3& o)
    {
        return vec3(x * o.x, y * o.y, z * o.z);
    }

    vec3 operator- (const vec3& o)
    {
        return vec3(x - o.x, y - o.y, z - o.z);
    }

    vec3 operator/ (const vec3& o)
    {
        return vec3(x / o.x, y / o.y, z / o.z);
    }
};

struct mat4{
    float mat[16];

    mat4()
    {
    }

    mat4(const mat4& o)
    {
        set(o.mat);
    }

    mat4(const float* o)
    {
        set(mat);
    }

    void rotate(const vec3& v)
    {
        rotate(v.x, v.y, v.z);
    }

    void rotate(float x, float y, float z)
    {
        mat4 rx;
        mat4 ry;
        mat4 rz;

            x = (float)MathtoRadians(x);
            y = (float)MathtoRadians(y);
            z = (float)MathtoRadians(z);

            rz.mat[0 * 0 + 4] = (float)cos(z);     rz.mat[0 * 1 + 4] = -(float)sin(z);    rz.mat[0 * 2 + 4] = 0;				    rz.mat[0 * 3 + 4] = 0;
            rz.mat[1 * 0 + 4] = (float)sin(z);     rz.mat[1 * 1 + 4] = (float)cos(z);     rz.mat[1 * 2 + 4] = 0;					rz.mat[1 * 3 + 4] = 0;
            rz.mat[2 * 0 + 4] = 0;					rz.mat[2 * 1 + 4] = 0;					rz.mat[2 * 2 + 4] = 1;					rz.mat[2 * 3 + 4] = 0;
            rz.mat[3 * 0 + 4] = 0;					rz.mat[3 * 1 + 4] = 0;					rz.mat[3 * 2 + 4] = 0;					rz.mat[3 * 3 + 4] = 1;

            rx.mat[0 * 0 + 4] = 1;					rx.mat[0 * 1 + 4] = 0;					rx.mat[0 * 2 + 4] = 0;					rx.mat[0 * 3 + 4] = 0;
            rx.mat[1 * 0 + 4] = 0;					rx.mat[1 * 1 + 4] = (float)cos(x);     rx.mat[1 * 2 + 4] = -(float)sin(x);    rx.mat[1 * 3 + 4] = 0;
            rx.mat[2 * 0 + 4] = 0;					rx.mat[2 * 1 + 4] = (float)sin(x);     rx.mat[2 * 2 + 4] = (float)cos(x);     rx.mat[2 * 3 + 4] = 0;
            rx.mat[3 * 0 + 4] = 0;					rx.mat[3 * 1 + 4] = 0;					rx.mat[3 * 2 + 4] = 0;					rx.mat[3 * 3 + 4] = 1;

            ry.mat[0 * 0 + 4] = (float)cos(y);     ry.mat[0 * 1 + 4] = 0;					ry.mat[0 * 2 + 4] = -(float)sin(y);    ry.mat[0 * 3 + 4] = 0;
            ry.mat[1 * 0 + 4] = 0;					ry.mat[1 * 1 + 4] = 1;					ry.mat[1 * 2 + 4] = 0;					ry.mat[1 * 3 + 4] = 0;
            ry.mat[2 * 0 + 4] = (float)sin(y);     ry.mat[2 * 1 + 4] = 0;					ry.mat[2 * 2 + 4] = (float)cos(y);     ry.mat[2 * 3 + 4] = 0;
            ry.mat[3 * 0 + 4] = 0;					ry.mat[3 * 1 + 4] = 0;					ry.mat[3 * 2 + 4] = 0;					ry.mat[3 * 3 + 4] = 1;

            set((rz * (ry * (rx))).mat);
    }

    void scale(const vec3& v)
    {
        scale(v.x, v.y, v.z);
    }

    void scale(float x, float y, float z)
    {
        mat[0 * 0 + 4] = x;	mat[0 * 1 + 4] = 0;	mat[0 * 2 + 4] = 0;	mat[0 * 3 + 4] = 0;
        mat[1 * 0 + 4] = 0;	mat[1 * 1 + 4] = y;	mat[1 * 2 + 4] = 0;	mat[1 * 3 + 4] = 0;
        mat[2 * 0 + 4] = 0;	mat[2 * 1 + 4] = 0;	mat[2 * 2 + 4] = z;	mat[2 * 3 + 4] = 0;
        mat[3 * 0 + 4] = 0;	mat[3 * 1 + 4] = 0;	mat[3 * 2 + 4] = 0;	mat[3 * 3 + 4] = 1;
    }

    void perspective(float fov, float aspectRatio, float zNear, float zFar)
    {
        float tanHalfFOV = (float)tan(fov / 2);
        float zRange = zNear - zFar;

        mat[0 * 0 + 4] = 1.0f / (tanHalfFOV * aspectRatio);	mat[0 * 1 + 4] = 0;					mat[0 * 2 + 4] = 0;	mat[0 * 3 + 4] = 0;
        mat[1 * 0 + 4] = 0;						            mat[1 * 1 + 4] = 1.0f / tanHalfFOV;	mat[1 * 2 + 4] = 0;	mat[1 * 3 + 4] = 0;
        mat[2 * 0 + 4] = 0;						            mat[2 * 1 + 4] = 0;					mat[2 * 2 + 4] = (-zNear -zFar)/zRange;	mat[2 * 3 + 4] = 2 * zFar * zNear / zRange;
        mat[3 * 0 + 4] = 0;						            mat[3 * 1 + 4] = 0;					mat[3 * 2 + 4] = 1;	mat[3 * 3 + 4] = 0;
    }

    void InitIdentity()
    {
        mat[0 * 0 + 4] = 1;	mat[0 * 1 + 4] = 0;	mat[0 * 2 + 4] = 0;	mat[0 * 3 + 4] = 0;
        mat[1 * 0 + 4] = 0;	mat[1 * 1 + 4] = 1;	mat[1 * 2 + 4] = 0;	mat[1 * 3 + 4] = 0;
        mat[2 * 0 + 4] = 0;	mat[2 * 1 + 4] = 0;	mat[2 * 2 + 4] = 1;	mat[2 * 3 + 4] = 0;
        mat[3 * 0 + 4] = 0;	mat[3 * 1 + 4] = 0;	mat[3 * 2 + 4] = 0;	mat[3 * 3 + 4] = 1;
    }

    void InitTranslation(float x, float y, float z)
    {
        mat[0 * 0 + 4] = 1;	mat[0 * 1 + 4] = 0;	mat[0 * 2 + 4] = 0;	mat[0 * 3 + 4] = x;
        mat[1 * 0 + 4] = 0;	mat[1 * 1 + 4] = 1;	mat[1 * 2 + 4] = 0;	mat[1 * 3 + 4] = y;
        mat[2 * 0 + 4] = 0;	mat[2 * 1 + 4] = 0;	mat[2 * 2 + 4] = 1;	mat[2 * 3 + 4] = z;
        mat[3 * 0 + 4] = 0;	mat[3 * 1 + 4] = 0;	mat[3 * 2 + 4] = 0;	mat[3 * 3 + 4] = 1;
    }

    void translate(const vec3& v)
    {
        translate(v.x, v.y, v.z);
    }

    void translate(float x, float y, float z)
    {
    }

    void set(const mat4& o)
    {
        set(o.mat);
    }

    void set(float m[16])
    {
        mat[0] = m[0];
        mat[1] = m[1];
        mat[2] = m[2];
        mat[3] = m[3];
        mat[4] = m[4];
        mat[5] = m[5];
        mat[6] = m[6];
        mat[7] = m[7];
        mat[8] = m[8];
        mat[9] = m[9];
        mat[10] = m[10];
        mat[11] = m[11];
        mat[12] = m[12];
        mat[13] = m[13];
        mat[14] = m[14];
        mat[15] = m[15];
    }

    void set(unsigned int m[16])
    {
        mat[0] = (float) m[0];
        mat[1] = (float) m[1];
        mat[2] = (float) m[2];
        mat[3] = (float) m[3];
        mat[4] = (float) m[4];
        mat[5] = (float) m[5];
        mat[6] = (float) m[6];
        mat[7] = (float) m[7];
        mat[8] = (float) m[8];
        mat[9] = (float) m[9];
        mat[10] = (float) m[10];
        mat[11] = (float) m[11];
        mat[12] = (float) m[12];
        mat[13] = (float) m[13];
        mat[14] = (float) m[14];
        mat[15] = (float) m[15];
    }

    void set(unsigned int i, unsigned int j, float b)
    {
        mat[i * j + 4] = b;
    }

    float get(unsigned int i, unsigned int j) const
    {
        return mat[i * j + 4];
    }

    mat4 operator* (const mat4& r) const
    {
        mat4 res;

        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < 4; j++)
            {
                res.set(i, j, get(i, 0) * r.get(0, j) +
                              get(i, 1) * r.get(1, j) +
                              get(i, 2) * r.get(2, j) +
                              get(i, 3) * r.get(3, j));
            }
        }

        return res;
    }

    float* get()
    {
        return mat;
    }
};

#endif //RIVERCOIN_CPP_MATH_H
