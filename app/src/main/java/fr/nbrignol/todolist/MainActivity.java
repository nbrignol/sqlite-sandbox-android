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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TodoListDb todoDb;
    long selectedTaskId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("DB", "MainActivity.onCreate");

        todoDb = new TodoListDb( this );

        Cursor results = todoDb.fetchAll();
        Log.d("DB", "Nombre de résultats : " + results.getCount() );

        //ListView & cursor
        refreshListView();

        //Form "add task" submit
        Button submitButton = (Button) findViewById(R.id.form_add_submit);
        submitButton.setOnClickListener(  this  );

        //Selected item
        ListView taskList = (ListView) findViewById(R.id.task_list);
        taskList.setOnItemClickListener( this );

        //delete task
        Button deleteButton = (Button) findViewById(R.id.delete_task);
        deleteButton.setOnClickListener(  this  );
        deleteButton.setEnabled(false);

        //toggle
        Button toggleButton = (Button) findViewById(R.id.form_toggle);
        toggleButton.setOnClickListener(  this  );

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

        if (v.getId() == R.id.form_add_submit) {
            makeNewTask();
            return;
        }

        if (v.getId() == R.id.delete_task) {
            deleteSelectedTask();
            return;
        }

        if (v.getId() == R.id.form_toggle) {
            showFormAdd();
            return;
        }

    }

    public void makeNewTask (){
        //save new task ...
        EditText titleInput =   (EditText) findViewById( R.id.form_add_title_field );
        EditText contentInput = (EditText) findViewById( R.id.form_add_content_field );

        Task taskToAdd = new Task();
        taskToAdd.title = titleInput.getText().toString();
        taskToAdd.content = contentInput.getText().toString();

        todoDb.addTask( taskToAdd );

        //clean UI ...
        refreshListView();
        titleInput.setText("");
        contentInput.setText("");

        hideSoftKeyboard();

        hideFormAdd();
    }

    public void refreshListView(){
        Cursor cursor = todoDb.fetchAll();

        ListView myList = (ListView) findViewById(R.id.task_list);
        myList.setAdapter( makeAdapter(cursor) );
    }

    public void deleteSelectedTask(){

        if (selectedTaskId == 0) {
            return;
        }

        todoDb.deleteTask(selectedTaskId);

        //reset
        selectedTaskId = 0;
        Button deleteButton = (Button) findViewById(R.id.delete_task);
        deleteButton.setEnabled(false);

        TextView label = (TextView) findViewById(R.id.selected_task);
        label.setText(R.string.no_selected_task);

        refreshListView();
    }

    public void showFormAdd(){
        View toggleButton = findViewById(R.id.form_toggle);
        View formLayout = findViewById(R.id.form_add);

        toggleButton.setVisibility(View.GONE);
        formLayout.setVisibility(View.VISIBLE);
    }

    public void hideFormAdd(){
        View toggleButton = findViewById(R.id.form_toggle);
        View formLayout = findViewById(R.id.form_add);

        toggleButton.setVisibility(View.VISIBLE);
        formLayout.setVisibility(View.GONE);
    }

    public void hideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedTaskId = id;
        Button deleteButton = (Button) findViewById(R.id.delete_task);
        deleteButton.setEnabled(true);

        Cursor cursor = todoDb.fetchById( id );
        cursor.moveToFirst();

        String title = cursor.getString( 1 );

        TextView label = (TextView) findViewById(R.id.selected_task);
        label.setText(title);

    }

}
