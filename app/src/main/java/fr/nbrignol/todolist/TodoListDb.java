package fr.nbrignol.todolist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TodoListDb extends SQLiteOpenHelper {

    public TodoListDb(Context context) {
        super(context, "TODO_LIST", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE Task (" +
                "_id integer PRIMARY KEY autoincrement," +
                "title TEXT, " +
                "content TEXT " +
                ");";

        db.execSQL(query);

        Log.d("DB", "TodoListDb.onCreate : " + query );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addTask(Task task){

        Log.d("DB", "TodoListDb : Add Task ...");

        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("content", task.content);

        this.getWritableDatabase().insert("Task", null, values);

        Log.d("DB", "TodoListDb : Add Task done.");

    }

    public Cursor fetchAll(){

        String table = "Task";

        String[] columns = new String[] {
                "_id",
                "title",
                "content"
        };

        String orderBy = "_id ASC";

        Cursor cursor = this.getReadableDatabase().query(
                table, columns,
                null, null, null, null, orderBy);

        return cursor;
    }

    public Cursor fetchById(long id) {

        String table = "Task";

        String[] cols = new String[] {
                "_id",
                "title",
                "content"
        };

        String whereClause =  "_id = ?";

        String[] whereArgs = new String[] {
                String.valueOf(id)
        };

        Cursor cursor = this.getReadableDatabase().query(table, cols,
                whereClause, whereArgs, null, null, null);

        return cursor;
    }

}
