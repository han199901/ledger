package com.han.zhangben.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.han.zhangben.entity.Bill;
import com.han.zhangben.utils.DbHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ledger {
    private final DbHelper dbHelper;

    public Ledger(Context context) {
        this.dbHelper = new DbHelper(context,"ledger.db",null,1);
    }

    public void insert(Bill bill) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "insert into bill(name,car_model,from_addr,to_addr,price,date,status,del) values(?,?,?,?,?,?,?,0)";
        db.execSQL(sql,new String[]{bill.getName(),bill.getModel(),bill.getFrom(),bill.getTo(),bill.getPrice().toString(),Long.toString(bill.getDate().getTime()),bill.getStatus().toString()});
        db.close();
    }
    public void delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "update bill set del=1 where id=?";
        db.execSQL(sql,new String[]{Integer.toString(id)});

        db.close();
    }
    public void update(Bill bill) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "update bill set name=?,car_model=?,from_addr=?,to_addr=?,price=?,date=?,status=? where id=?";
        db.execSQL(sql,new String[]{bill.getName(),bill.getModel(),bill.getFrom(),bill.getTo(),bill.getPrice().toString(),Long.toString(bill.getDate().getTime()),bill.getStatus().toString(),bill.getId().toString()});
        db.close();
    }

    public int totalPageNum() {
        int total=-1;
        String sql = "select count(*) as total from bill";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex("total");
            total = cursor.getInt(index);
        }
        db.close();
        int num = total%15==0?0:1;
        int res = total/15+num;
        Log.d("totalnum", "totalPageNum: "+total+" "+res);
        return res;
    }

    public List<Bill> query(Date start, Date end, String name, int page) {
        List<Bill> list = new ArrayList<>();

        String where = " ";
        if(start!=null) {
            if(where.equals(" "))
                where = " where date > '" + start.toString() + "'";
            else {
                where += " AND date > '" + start.toString() + "'";
            }
        }
        if(end!=null) {
            if(where.equals(" "))
                where = " where date < '" + end.toString() + "'";
            else {
                where += " AND date < '" + end.toString() + "'";
            }
        }
        if(name!=null&&!name.isEmpty()) {
            if(where.equals(" "))
                where = " where \"name\"=\""+name+"\"";
            else {
                where += " AND \"name\"=\""+name+"\"";
            }
        }
        if(where.isEmpty()||where.equals(" ")) {
            where = " where del=0";
        }
        else {
            where += " AND del=0";
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        page = page*15;
        String sql = "select * from bill" + where +" ) order by `date` desc ";
        String sql2 = "select * from ( " + sql  +" LIMIT "+page+",15";
        Cursor cursor = db.rawQuery(sql2,null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex("id");
            int id = cursor.getInt(index);
            index = cursor.getColumnIndex("name");
            String bName = cursor.getString(index);
            index = cursor.getColumnIndex("car_model");
            String model = cursor.getString(index);
            index = cursor.getColumnIndex("from_addr");
            String from = cursor.getString(index);
            index = cursor.getColumnIndex("to_addr");
            String to = cursor.getString(index);
            index = cursor.getColumnIndex("price");
            int price = cursor.getInt(index);
            index = cursor.getColumnIndex("date");
            Date date = new Date(Long.parseLong(cursor.getString(index)));
            index = cursor.getColumnIndex("status");
            int status = cursor.getInt(index);
            list.add(new Bill(id,bName,date,from,to,model,price,status));
        }
        db.close();
        return list;
    }
}
