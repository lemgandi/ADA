 public boolean updateDatabase(DataInputStream inStream, SQLiteDatabase db, boolean doClear) throws Error {
    String sqlStatement = null;
    boolean result = true;
    boolean inOnCreate = true;
    boolean wasInTransaction;

    if(doClear) dropDatabase();

    // if called from onCreate() db is open and inTransaction, else getWritableDatabase()
    if(db == null) {
        inOnCreate = false;
        db = this.getWritableDatabase();
    }

    wasInTransaction = db.inTransaction();  // see NB below

    boolean doExecute;
    try {
        while ((sqlStatement = inStream.readLine()) != null) {
            // trim, so we can look for ';'
            sqlStatement.trim();
            if(!sqlStatement.endsWith(";")) {
                continue;   // line breaks in file, get whole statement
            }

            // NB - my file (exported from SQLite Database Browser starts with "BEGIN TRANSACTION;". 
            // executing this throws SQLiteException: cannot start a transaction within a transaction
            // According to SQLiteDatabase doc for beginTransaction(), "Transactions can be nested"
            // so this is a problem
            // but... possibly it is an "exclusive transaction" ?
            doExecute = true;
            if(wasInTransaction) {
                // don't execute BEGIN TRANSACTION; or COMMIT;
                if((sqlStatement.contains("BEGIN" ) || sqlStatement.contains("begin" )) &&
                        (sqlStatement. contains("TRANSACTION") || sqlStatement.contains("transaction" ))) {
                    doExecute = false;
                }
                if(sqlStatement.contains("COMMIT") || sqlStatement.contains("commit")) {
                    doExecute = false;
                }
            }   // inTransaction
            // this statement could be in older databases, but this scheme doesn't need, can't have it
            if(sqlStatement.contains("android_metadata")) {
                doExecute = false;
            }
            if(doExecute) {
                try {
                    db.execSQL(sqlStatement);
                } catch (SQLException e) {
                    throw(new Error("Error executing SQL " + sqlStatement));
                }   // try/catch
            }   // doExecute
        }   // while()
    } catch (IOException e) {
        result = false; // which won't matter if we throw 
        throw(new Error("Error reading  " + DB_SQL));
    } 

    if(!inOnCreate) {
        db.close();
    }

    return result;
}

