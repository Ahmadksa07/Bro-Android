package com.randomappsinc.bro.Persistence;

import com.randomappsinc.bro.Models.Record;

import java.util.List;

/**
 * Created by alexanderchiou on 8/25/15.
 */
public class RecordDataSource {
    public static void insertRecord(Record record) {
        record.save();
    }

    public static void deleteRecord(long recordId) {
        Record record = Record.findById(Record.class, recordId);
        record.delete();
    }

    public static List<Record> getAllRecords() {
        return Record.findWithQuery(Record.class, "SELECT * FROM RECORD ORDER BY id DESC;");
    }
}
