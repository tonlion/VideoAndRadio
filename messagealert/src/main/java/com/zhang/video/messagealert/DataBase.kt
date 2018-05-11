package com.zhang.video.messagealert

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase.CursorFactory

class DataBase : SQLiteOpenHelper {

    constructor(context: Context, name: String, factory: CursorFactory,
                version: Int) : super(context, name, factory, version) {
    }

    constructor(context: Context) : super(context, DB_NAME, null, DB_VERSION) {}

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table if not exists notes(id integer not null primary key autoincrement,title text not null,desc text not null,time varchar(20) not null,alerttime varchar(20) not null,alerttype int not null)"!!)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        private val DB_NAME = "note.db"
        private val DB_VERSION = 1
    }

}