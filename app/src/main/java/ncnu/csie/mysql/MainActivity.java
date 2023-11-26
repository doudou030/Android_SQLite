package ncnu.csie.mysql;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView input_edt, number, data, price;
    private Button btn_exec, btn_ins, btn_del, btn_update, btn_search, btn_search_all,btn_fin;
    private ListView mylist;
    private SQLiteDatabase mydb = null;
    String itemdata, str;
    int n,id = 0;

    private static String CREATE_TABLE = "CREATE TABLE table01(_id INTEGER PRIMARY KEY, data TEXT, num INTEGER)";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mylist = findViewById(R.id.database_listview);
        input_edt = findViewById(R.id.editTextText);
        btn_exec = findViewById(R.id.exec_button);
        btn_exec.setOnClickListener(btnDoClick);

        btn_ins = findViewById(R.id.button2);
        btn_del = findViewById(R.id.button3);
        btn_update = findViewById(R.id.button6);
        btn_search = findViewById(R.id.button4);
        btn_search_all = findViewById(R.id.button5);
        btn_fin = findViewById(R.id.button7);
        btn_ins.setOnClickListener(btn_listner);
        btn_del.setOnClickListener(btn_listner);
        btn_update.setOnClickListener(btn_listner);
        btn_search.setOnClickListener(btn_listner);
        btn_search_all.setOnClickListener(btn_listner);
        btn_fin.setOnClickListener(btn_listner);

        number = findViewById(R.id.number_edit);
        data = findViewById(R.id.editTextText3);
        price = findViewById(R.id.editTextPhone);

        itemdata="資料項目" + n;
        str="INSERT INTO table01 (num,data) values (" + n + ",'" + itemdata + "')";
        input_edt.setText(str);

        mydb  = openOrCreateDatabase("mydb.db", MODE_PRIVATE, null);
        try {
            mydb.execSQL(CREATE_TABLE);
        }
        catch (Exception e){
            UpdateAdapter();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //故意刪除原有的資料表，讓每次執行時資料表是空的
        mydb.execSQL("DROP TABLE table01");
        mydb.close(); // 關閉資料庫
    }

    private View.OnClickListener btnDoClick=new Button.OnClickListener(){
        public void onClick(View v){
            try{
                mydb.execSQL(input_edt.getText().toString()); // 執行SQL
                UpdateAdapter(); // 更新 ListView
                n++;
                itemdata="資料項目" + n;
                str="INSERT INTO table01 (num,data) values (" + n + ",'" + itemdata + "')";
                input_edt.setText(str);
                Toast.makeText(getApplicationContext(),"資料更新完畢!", Toast.LENGTH_SHORT).show();
            }catch (Exception err){
//                setTitle("SQL 語法錯誤!");
                Toast.makeText(getApplicationContext(),"SQL 語法錯誤!!", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View.OnClickListener btn_listner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ContentValues cv = new ContentValues();

            switch (view.getId()){
                case R.id.button2: // insert
                    cv.put("data", data.getText().toString());
                    cv.put("num", price.getText().toString());
                    mydb.insert("table01",null,cv);
                    UpdateAdapter(); // 更新 ListView
                    Toast.makeText(getApplicationContext(),"Insert", Toast.LENGTH_SHORT).show();
                    clean_all_text();
                    break;
                case R.id.button3: // delete
                    if(number.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"你沒有ID", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    id = Integer.parseInt(number.getText().toString());
                    mydb.delete("table01","_id=" + id,null);
                    UpdateAdapter(); // 更新 ListView
                    Toast.makeText(getApplicationContext(),"Delete", Toast.LENGTH_SHORT).show();
                    clean_all_text();
                    break;
                case R.id.button6: // update
                    if(number.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"你沒有ID", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    cv.put("data", data.getText().toString());
                    cv.put("num", price.getText().toString());
                    id = Integer.parseInt(number.getText().toString());
                    mydb.update("table01", cv, "_id=" + id, null);
                    UpdateAdapter(); // 更新 ListView
                    Toast.makeText(getApplicationContext(),"Update", Toast.LENGTH_SHORT).show();
                    clean_all_text();
                    break;
                case R.id.button4: // search
                    if(number.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"你沒有ID", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    id = Integer.parseInt(number.getText().toString());
                    Cursor search_cursor = mydb.rawQuery("SELECT _id, _id||'.'||data pname, num FROM table01 WHERE _id=" + id,null);
                    Search_Adapter(search_cursor);
                    Toast.makeText(getApplicationContext(),"Search", Toast.LENGTH_SHORT).show();
                    clean_all_text();
                    break;
                case R.id.button5: // search all
                    Cursor search_all_cursor= mydb.rawQuery("SELECT _id, _id||'.'||data pname, num FROM table01",null);
                    Search_Adapter(search_all_cursor);
                    Toast.makeText(getApplicationContext(),"Search ALL", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.button7: // finish
                    finish();
                    break;
            }

        }
    };

    private void clean_all_text() {
        number.setText("");
        data.setText("");
        price.setText("");
    }

    private void UpdateAdapter() {
        Cursor cursor= mydb.rawQuery("SELECT _id, _id||'.'||data pname, num FROM table01",null);
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2, // 包含兩個資料項
                    cursor, // 資料庫的 Cursors 物件
                    new String[] { "pname", "num" }, // num、data 欄位
                    new int[] { android.R.id.text1, android.R.id.text2 }, // 與 num、data對應的元件
                    0);  // adapter 行為最佳化
            mylist.setAdapter(adapter); // 將adapter增加到listview中
        }
    }
    public void Search_Adapter(Cursor cursor){
        if (cursor != null && cursor.getCount() >= 0){
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2, // 包含兩個資料項
                    cursor, // 資料庫的 Cursors 物件
                    new String[] {"pname", "num" }, // pname、price 欄位
                    new int[] { android.R.id.text1, android.R.id.text2 }, // 與 pname、price對應的元件
                    0); // adapter 行為最佳化
            mylist.setAdapter(adapter); // 將adapter增加到listview01中
        }
    }
}