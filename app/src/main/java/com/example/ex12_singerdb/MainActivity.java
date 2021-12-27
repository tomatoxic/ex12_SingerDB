package com.example.ex12_singerdb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // CMD -> CD C:\Users\Nexus\AppData\Local\Android\Sdk\platform-tools
    // ADB.EXE
    // adb root, adb shell
    // cd /data/data/com.example.ex12_singerdb
    // ls -l
    // (mkdir databases)
    // cd databases
    // pwd -> sqlite3 groupDB
    // 이후 SQL 문으로 조회

    // myDBHelper 클래스 변수
    myDBHelper myHelper;
    EditText editName, editNumber, editNameResult, editNumberResult;
    Button btnInit, btnInsert, btnSelect, btnUpdate, btnDelete;
    // SQLiteDatabase 클래스 변수
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("가수 그룹 관리 DB");

        editName = findViewById(R.id.edit_Name);
        editNumber = findViewById(R.id.edit_Number);
        editNameResult = findViewById(R.id.editNameResult);
        editNumberResult = findViewById(R.id.editNumberResult);

        btnInit = findViewById(R.id.btnInit);
        btnInsert = findViewById(R.id.btnInsert);
        btnSelect = findViewById(R.id.btnSelect);

        // 수정 삭제 추가
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        myHelper = new myDBHelper(this);
        // 초기화를 클릭했을 때
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                myHelper.onUpgrade(sqlDB, 1, 2);
                sqlDB.close();
            }
        });

        // 입력을 클릭했을 때 EditText의 값이 입력
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO groupTBL VALUES ('"+ editName.getText().toString() + "' , "+ editNumber.getText().toString() + ");");
                sqlDB.close();
                // 입력 눌렀을 때 바로 내용 조회
                btnSelect.callOnClick();
                Toast.makeText(getApplicationContext(), "입력됨", Toast.LENGTH_SHORT).show();
            }
        });

        // 수정 클릭 시 UPDATE
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("UPDATE groupTBL SET gNumber = " + editNumber.getText().toString().trim() + " + WHERE gName = '" + editName.getText().toString().trim() + "';");
                sqlDB.close();
                btnSelect.callOnClick();
                Toast.makeText(getApplicationContext(), "수정됨", Toast.LENGTH_SHORT).show();
            }
        });

        // 삭제 클릭 시 DELETE
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("DELETE from groupTBL WHERE gName = '" + editName.getText().toString() + "';");
                sqlDB.close();
                btnSelect.callOnClick();
                Toast.makeText(getApplicationContext(), "삭제됨", Toast.LENGTH_SHORT).show();
            }
        });

        // 조회 클릭시 DB 내용 출력
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

                String strNames = "그룹 이름" + "\r\n" + "--------" + "\r\n";
                String strNumber = "인원" + "\r\n" + "--------" + "\r\n";

                while (cursor.moveToNext()) {
                    strNames += cursor.getString(0) + "\r\n";
                    strNumber += cursor.getString(1) + "\r\n";
                }

                editNameResult.setText(strNames);
                editNumberResult.setText(strNumber);

                cursor.close();
                sqlDB.close();
            }
        });


    }

    // SQLiteOpenHelper 클래스에서 상속받은 myDBHelper 클래스 정의 후 생성자 추가
    // ALT + INSERT -> Import implements
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            // 생성자 정의, "groupDB"는 새로 생성될 db의 이름, 1 : db version 처음에는 1로 지정
            super(context, "groupDB", null, 1);
        }

        // 테이블 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gName CHAR(20) PRIMARY KEY, gNumber INTEGER);");
        }

        // 테이블을 삭제 한 후 다시 생성 (초기화할 때 호출)
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }


    }
}