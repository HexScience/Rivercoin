package com.riverssen.core.db;

import java.io.*;

public class Table
{
    public static final int NO_ERRORS = 0;
    public static final int ERR_ELMNT_NULL = 1;
    public static final int ERR_ELMNT_INCM = 2;
    public static final int ERR_ELMNT_FXPT = 3;
    public static final int ERR_ELMNT_EXPT = 4;

    private String        name;
    private File          file;
//    private Element       table[][];
    private int           index;
    private int           numRows;

    public Table(Database database, String name, int numRows)
    {
        this.name = name;
        this.file = new File(database.getLocation());
        this.file.mkdirs();
//        this.table = new Element[100][numRows];
    }

    public int addColoumn(String hash, Element ...elements)
    {
        if(elements == null) return ERR_ELMNT_NULL;

        if(elements.length != numRows) return ERR_ELMNT_INCM;

        File file = new File(this.file + File.separator + hash);

        try
        {
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            for(Element element : elements)
                stream.writeUTF(element.toString());

            stream.flush();
            stream.close();

        } catch (FileNotFoundException e)
        {
            return ERR_ELMNT_FXPT;
        } catch (IOException e)
        {
            return ERR_ELMNT_EXPT;
        }

        return NO_ERRORS;
    }
}