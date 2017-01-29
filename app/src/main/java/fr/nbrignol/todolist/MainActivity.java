package fr.nbrignol.todolist;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TodoListDb todoDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DB", "MainActivity.onCreate");

        todoDb = new TodoListDb( this );

        Cursor results = todoDb.fetchAll();
        Log.d("DB", "Nombre de résultats : " + results.getCount() );

        //adapt Cursor (from Sqlite) to ListView
        SimpleCursorAdapter adapter = makeAdapter( results );

        ListView taskList = (ListView) findViewById(R.id.task_list);
        taskList.setAdapter(  adapter  );

        //Form "add task" submit
        Button submitButton = (Button) findViewById(R.id.form_add_submit);
        submitButton.setOnClickListener(  this  );


    }

    public SimpleCursorAdapter makeAdapter(Cursor cursor) {
        String[] columns = new String[] {
                "title",
                "content"
        };

        int[] views = new int[] {
                R.id.task_title,
                R.id.task_content
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this, R.layout.list_item, cursor,  columns, views, 0);

        return adapter;
    }

    @Override
    public void onClick(View v) {

        //save new task ...
        EditText titleInput =   (EditText) findViewById( R.id.form_add_title_field );
        EditText contentInput = (EditText) findViewById( R.id.form_add_content_field );

        Task taskToAdd = new Task();
        taskToAdd.title = titleInput.getText().toString();
        taskToAdd.content = contentInput.getText().toString();

        todoDb.addTask( taskToAdd );

        //refresh list ...
        Cursor cursor = todoDb.fetchAll();

        ListView myList = (ListView) findViewById(R.id.task_list);
        myList.setAdapter( makeAdapter(cursor) );

        //clean UI ...
        titleInput.setText("");
        contentInput.setText("");

        hideSoftKeyboard();
    }

    public void hideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
