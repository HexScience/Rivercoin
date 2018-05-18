package com.riverssen.core.db;

import com.riverssen.core.Config;

import java.io.File;

public class Database
{
    private String name;
    private File   db;

    public Database(String name)
    {
        this.name = name;
        this.db   = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + "//db//" + name);
    }

    public synchronized void insert()
    {
    }

    public synchronized Table newTable(String name, int numRows)
    {
        return new Table(this, name, numRows);
    }

    public String getLocation()
    {
        return db.toString();
    }
}
