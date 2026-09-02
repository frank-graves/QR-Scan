// app/src/main/java/org/foss/lens/observability/ObservabilityDb.kt
package org.foss.lens.observability

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File

class ObservabilityDb(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "lens_observability.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE logs (id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER NOT NULL, level INTEGER NOT NULL, tag TEXT NOT NULL, msg TEXT NOT NULL)")
        db.execSQL("CREATE TABLE metrics (id INTEGER PRIMARY KEY AUTOINCREMENT, ts INTEGER NOT NULL, name TEXT NOT NULL, value REAL NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS logs")
        db.execSQL("DROP TABLE IF EXISTS metrics")
        onCreate(db)
    }

    fun insertLog(level: Int, tag: String, msg: String) {
        val db = writableDatabase
        db.execSQL(
            "INSERT INTO logs(ts,level,tag,msg) VALUES(?,?,?,?)",
            arrayOf(System.currentTimeMillis(), level, tag, msg)
        )
        db.execSQL("DELETE FROM logs WHERE id NOT IN (SELECT id FROM logs ORDER BY id DESC LIMIT 200)")
    }

    fun insertMetric(name: String, value: Double) {
        writableDatabase.execSQL(
            "INSERT INTO metrics(ts,name,value) VALUES(?,?,?)",
            arrayOf(System.currentTimeMillis(), name, value)
        )
    }

    fun getMetrics(): Map<String, Double> {
        val out = mutableMapOf<String, Double>()
        readableDatabase.rawQuery(
            "SELECT name, AVG(value) AS avg FROM metrics WHERE name IN " +
                "('cold_start_ms','frame_render_ms','analyzer_ok_rate','analyzer_error_rate','saturation_mem_kb','saturation_battery_pct') " +
                "GROUP BY name",
            null
        ).use { c ->
            while (c.moveToNext()) out[c.getString(0)] = c.getDouble(1)
        }
        return out
    }

    fun exportLogs(): File {
        val file = File(context.filesDir, "lens_logs_${System.currentTimeMillis()}.txt")
        file.printWriter().use { w ->
            readableDatabase.rawQuery("SELECT ts,level,tag,msg FROM logs ORDER BY id DESC LIMIT 200", null).use { c ->
                while (c.moveToNext()) w.println("${c.getLong(0)}|${c.getInt(1)}|${c.getString(2)}|${c.getString(3)}")
            }
        }
        return file
    }
}