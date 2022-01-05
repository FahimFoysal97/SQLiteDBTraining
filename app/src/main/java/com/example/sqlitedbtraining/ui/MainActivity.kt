package com.example.sqlitedbtraining.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.sqlitedbtraining.data.local.FeedReaderContract
import com.example.sqlitedbtraining.data.local.FeedReaderDbHelper
import com.example.sqlitedbtraining.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: FeedReaderDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    @SuppressLint("Range")
    private fun initUI() {
        dbHelper = FeedReaderDbHelper(this)
        //writeToDB()
        readFromDB()
    }

    @SuppressLint("Range")
    private fun readFromDB (){
        //----------Reading part----------
        val dbReaderDbHelper = dbHelper.readableDatabase
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
        // Filter results WHERE "title" = 'My Title'
        val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
        val selectionArgs = arrayOf("My Title")
        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"
        val cursor = dbReaderDbHelper.query(
            FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )
        val itemIds = mutableListOf<Long>()
        val itemTitles = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val title = getString(getColumnIndexOrThrow("title"))
                itemIds.add(itemId)
                itemTitles.add(title)
                //Log.i("SQLiteDBApp", "Title:$title")
            }
        }
        cursor.close()
        Log.i("SQLiteDBApp", itemTitles.size.toString())
        itemTitles.forEach{
            Log.i("SQLiteDBApp", it)
        }
    }

    fun writeToDB() {
        //----------Writing part----------
        // Gets the data repository in write mode
        val dbWriterDbHelper = dbHelper.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "title")
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "subtitle")
        }
        // Insert the new row, returning the primary key value of the new row
        val newRowId = dbWriterDbHelper?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
    }

}