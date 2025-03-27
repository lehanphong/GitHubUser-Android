package ntd.molea.githubuser.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [GitHubUserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GitHubUserDatabase : RoomDatabase() {
    abstract fun githubUserDao(): GitHubUserDao

    companion object {
        fun create(context: Context): GitHubUserDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GitHubUserDatabase::class.java,
                "github_user_database"
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
} 