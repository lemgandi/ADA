package org.tomshiro.ada;
/*
 *  Extend database context to put db on sd card
 *  Courtesy k3b StackOverflow
 */

import java.io.*;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;
import android.database.sqlite.*;

class DatabaseContext extends ContextWrapper {

private static final String TAG = "DatabaseContext";

public DatabaseContext(Context base) {
    super(base);
}

@Override
public File getDatabasePath(String name) 
{
    File sdcard = Environment.getExternalStorageDirectory();    
    String dbfile = sdcard.getAbsolutePath() + File.separator+ "org.tomshiro.ada/database" + File.separator + name;
    if (!dbfile.endsWith(".db"))
    {
        dbfile += ".db" ;
    }

    File result = new File(dbfile);

    if (!result.getParentFile().exists())
    {
        result.getParentFile().mkdirs();
    }

    if (Log.isLoggable(TAG, Log.WARN))
    {
        Log.w(TAG,
                "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
    }

    return result;
}

@Override
public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) 
{
    SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    // SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
    if (Log.isLoggable(TAG, Log.WARN))
    {
        Log.w(TAG,
                "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
    }
    return result;
}
}
