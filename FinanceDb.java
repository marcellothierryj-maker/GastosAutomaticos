package com.thierry.gastosautomaticos;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.*;

public class FinanceDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "finance.db";
    private static final int DB_VERSION = 1;
    public FinanceDb(Context c) { super(c, DB_NAME, null, DB_VERSION); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, amount_cents INTEGER NOT NULL, merchant TEXT NOT NULL, category TEXT NOT NULL, timestamp INTEGER NOT NULL, source TEXT NOT NULL, automatic INTEGER NOT NULL DEFAULT 1, fingerprint TEXT UNIQUE)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public long insert(Transaction t, String fingerprint) {
        ContentValues v = new ContentValues(); v.put("amount_cents", t.amountCents); v.put("merchant", t.merchant); v.put("category", t.category);
        v.put("timestamp", t.timestamp); v.put("source", t.source); v.put("automatic", t.automatic ? 1 : 0); v.put("fingerprint", fingerprint);
        return getWritableDatabase().insertWithOnConflict("transactions", null, v, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public List<Transaction> recent(int limit) {
        List<Transaction> out = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT id,amount_cents,merchant,category,timestamp,source,automatic FROM transactions ORDER BY timestamp DESC LIMIT ?", new String[]{String.valueOf(limit)});
        try { while(c.moveToNext()) out.add(new Transaction(c.getLong(0),c.getLong(1),c.getString(2),c.getString(3),c.getLong(4),c.getString(5),c.getInt(6)==1)); }
        finally { c.close(); }
        return out;
    }

    public long monthTotalCents(Calendar from, Calendar to) {
        Cursor c = getReadableDatabase().rawQuery("SELECT COALESCE(SUM(amount_cents),0) FROM transactions WHERE timestamp>=? AND timestamp<?", new String[]{String.valueOf(from.getTimeInMillis()),String.valueOf(to.getTimeInMillis())});
        try { return c.moveToFirst()?c.getLong(0):0; } finally { c.close(); }
    }
}
