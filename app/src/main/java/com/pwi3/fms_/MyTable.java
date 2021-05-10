package com.pwi3.fms_;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class MyTable extends MyDBHelper {
    private SQLiteDatabase db;
    private static final String TAG_COUNT = "COUNT_NO";
    private static final String TAG_RACK = "RACK_NO";
    private static final String TAG_CARTON ="CARTON_BARCODE";
    private static final String TAG_LAY = "LAYER_NO";
    private static final String TAG_DATE ="MODIFY_DATE";
    private static final String TAG_CODE ="SUCCESS_CODE";
    public MyTable(Context context) {
        super(context);
        // 데이터를 쓰고 읽기 위해서 db 열기
        db = getWritableDatabase();
    }

    public void insert(String column2, String column3, String column4, String column5, String column6) {
        // 데이터 쓰기
        db.execSQL("INSERT INTO mytable (column2,column3,column4,column5,column6)VALUES("+"'" + column2 + "','" + column3 + "','" + column4 +"','" + column5 +"','" + column6 + "')");
    }

    public void updateTime(String column2, String column3, String column4, String column5) {
        // 조건에 일치하는 행의 데이터 변경
        db.execSQL("UPDATE mytable SET column5='" + column5 + "'  WHERE column2=" +"'"+ column2+"' AND column3=" +"'"+ column3+"' AND column4="+"'"+ column4+"'");
    }
    public void updateCode(String column2, String column3, String column4, String column6) {
        // 조건에 일치하는 행의 데이터 변경
        db.execSQL("UPDATE mytable SET column6='" + column6 + "'  WHERE column2=" +"'"+ column2+"' AND column3=" +"'"+ column3+"' AND column4="+"'"+ column4+"'");
    }

    public void deleteSUCCEED() {
        // 조건에 일치하는 행을 삭제
        db.execSQL("DELETE FROM mytable WHERE column6='SUCCEED'");
    }
    public void deleteAll(){
        db.execSQL("delete from mytable");

    }
    public ArrayList<MyTableList> select() {
        // 테이블의 모든 데이터 선택
        Cursor mCursor = db.rawQuery("SELECT * FROM mytable", null);
        ArrayList<MyTableList> list = new ArrayList<>();
        if(mCursor.moveToFirst()) {
            do {
                list.add(new MyTableList(mCursor.getInt(0), mCursor.getString(1), mCursor.getString(2), mCursor.getString(3), mCursor.getString(4), mCursor.getString(5)));
            } while(mCursor.moveToNext());
        }
        mCursor.close();
        return list;
    }
    public int checkDuplicate(String column2, String column3, String column4){
        String query = "select count(*) from mytable WHERE column6 <> 'SUCCEED' AND column2=" +"'"+ column2+"' AND column3=" +"'"+ column3+"' AND column4="+"'"+ column4+"'" ;
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        int i =cursor.getInt(0);
        cursor.close();
        return i;
    }
    public ArrayList<HashMap<String, String>> getData(){

        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        String query = "SELECT distinct * FROM mytable ";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put(TAG_COUNT,String.valueOf(cursor.getInt(0)));
            user.put(TAG_RACK,cursor.getString(1));
            user.put(TAG_CARTON,cursor.getString(2));
            user.put(TAG_LAY,cursor.getString(3));
            user.put(TAG_DATE,cursor.getString(4));
            user.put(TAG_CODE,cursor.getString(5));
            dataList.add(user);
        }
        return  dataList;
    }

    public String getRackQuery(){
        ArrayList<String> racklist = new ArrayList<>();
        String rackQuery = "";
        String query = "SELECT distinct column2 FROM mytable group by column2";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            racklist.add(cursor.getString(0));
        }
        for(int i=0;i<racklist.size();i++){
            if(i==0){
                rackQuery += ("'"+racklist.get(i)+"'");
            }
            else rackQuery+=(" or RC.RACK_CD='"+racklist.get(i)+"'");
        }
        return rackQuery;
    }

    public  ArrayList<HashMap<String, String>> selectUnsentData(){
        String query = "SELECT column2,column3,column4 FROM mytable where column6 ="+"'"+"-"+"'";
        Cursor cursor = db.rawQuery(query,null);
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();

            user.put(TAG_RACK,cursor.getString(0));
            user.put(TAG_CARTON,cursor.getString(1));
            user.put(TAG_LAY,cursor.getString(2));

            dataList.add(user);
        }
        return  dataList;
    }
}
