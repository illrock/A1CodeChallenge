package my.illrock.a1codechallenge.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import my.illrock.a1codechallenge.data.db.A1Database.Companion.VERSION
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateDao
import my.illrock.a1codechallenge.data.db.builtdates.BuiltDateEntity
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeDao
import my.illrock.a1codechallenge.data.db.maintypes.MainTypeEntity

@Database(
    entities = [
        MainTypeEntity::class,
        BuiltDateEntity::class
    ],
    version = VERSION
)
abstract class A1Database : RoomDatabase() {
    abstract fun builtDateDao(): BuiltDateDao
    abstract fun mainTypeDao(): MainTypeDao

    companion object {
        const val NAME = "a1.db"
        const val VERSION = 1
    }
}