package com.han.zhangben;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;


import com.han.zhangben.adapter.BillAdapter;
import com.han.zhangben.dao.Ledger;
import com.han.zhangben.entity.Bill;
import com.han.zhangben.entity.Checked;
import com.han.zhangben.entity.SearchBill;
import com.han.zhangben.utils.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static boolean flag = false;  //checkbox visibility
    public static boolean isSeached = false;  //checkbox visibility
    private CheckBox checkBox;
    private ImageView del;
    private ImageView search;
    private ImageView add;
    private Ledger ledger;
    public static Map<Integer,Checked> checkedSet = new HashMap<>();
    public static List<Checked> checkedList = new LinkedList<>();
    private ListView listView;
    private int page;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initBillList(null,null,null);
                    break;
                case 2:
                    checkBox.setChecked(false);
                    checkBox.setVisibility(CheckBox.VISIBLE);
                    del.setVisibility(ImageView.VISIBLE);
                    break;
                case 3:
                    SearchBill searchBill = (SearchBill) msg.obj;
                    initBillList(searchBill.getStart(),searchBill.getEnd(),searchBill.getName());
                    break;

            }

        }

    };

    /*显示账单列表*/
    private void initBillList(Date start,Date end,String name) {
        if(start!=null||end!=null||name!=null)
            isSeached = true;
        List<Bill> list = ledger.query(start,end,name,page);
        int total = ledger.totalPageNum();
        BillAdapter adapter = new BillAdapter(this,list,handler,ledger);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem != 0) { // 不为0则表示有下拉动作
                    if ((firstVisibleItem + visibleItemCount) > totalItemCount - 2) { // 当前第一个完全可见的item再下拉一个页面长度，即变为倒数第二个时
                        // 在此加载数据
                        if(page<total) {
                            list.addAll(ledger.query(start,end,name,++page));
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }

    private void initAdd() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }


    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.add_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().equals("新增")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.add_pop_window,null);
                    builder.setView(view);
                    EditText add_name = view.findViewById(R.id.add_name);
                    EditText add_from = view.findViewById(R.id.add_from);
                    EditText add_to = view.findViewById(R.id.add_to);
                    EditText add_date = view.findViewById(R.id.add_date);
                    EditText add_price = view.findViewById(R.id.add_price);
                    EditText add_model = view.findViewById(R.id.add_model);
                    CheckBox add_paied = view.findViewById(R.id.add_paied);
                    Button submit = view.findViewById(R.id.add_submit);
                    add_date.setText(DateFormat.date2Str(new Date(System.currentTimeMillis())));
                    Dialog dialog = builder.show();
                    Bill bill = new Bill();
                    add_date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar calendar=Calendar.getInstance();
                            new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String text = year + "." + (month + 1) + "." + dayOfMonth;
                                    add_date.setText(text);
                                }
                            }
                                    ,calendar.get(Calendar.YEAR)
                                    ,calendar.get(Calendar.MONTH)
                                    ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                            bill.setDate(new Date(calendar.getTimeInMillis()));
                        }
                    });
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(add_paied.isChecked())
                                bill.setStatus(1);
                            else
                                bill.setStatus(0);
                            if(!add_name.getText().toString().isEmpty())
                                bill.setName(add_name.getText().toString());
                            if(!add_from.getText().toString().isEmpty())
                                bill.setFrom(add_from.getText().toString());
                            if(!add_to.getText().toString().isEmpty())
                                bill.setTo(add_to.getText().toString());
                            if(add_price.getText().toString().isEmpty())
                                bill.setPrice(0);
                            else
                                bill.setPrice(Integer.parseInt(add_price.getText().toString()));
                            if(!add_model.getText().toString().isEmpty())
                                bill.setModel(add_model.getText().toString());
                            ledger.insert(bill);
                            handler.sendEmptyMessage(1);
                            dialog.dismiss();

                        }
                    });
                }
                else if (item.getTitle().equals("标记")) {
                    MainActivity.flag = true;
                    handler.sendEmptyMessage(2);
                    MainActivity.checkedList.forEach(i->{
                        i.getCheckBox().setChecked(false);
                        i.getCheckBox().setVisibility(CheckBox.VISIBLE);
                    });
                }
                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onBackPressed() {
        if(flag==true) {
            flag=false;
            checkedSet = new HashMap<>();
            checkBox.setVisibility(CheckBox.GONE);
            del.setVisibility(ImageView.GONE);
            checkedList.forEach(item->{
                item.getCheckBox().setVisibility(CheckBox.GONE);
            });
        } else if (isSeached==true){
            isSeached = false;
            initBillList(null,null,null);
        }
        else {
            super.onBackPressed();
        }

    }
    private void initDel() {
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedSet.forEach((key,value)->{
                    ledger.delete(key);
                    BillAdapter adapter = (BillAdapter) listView.getAdapter();
                    adapter.remove(value.getPosition());
                    adapter.notifyDataSetChanged();
                });
                onBackPressed();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        page = 0;
        ledger = new Ledger(this);
        checkBox = findViewById(R.id.main_ckeck_box);
        del = findViewById(R.id.main_del);
        search = findViewById(R.id.main_search);
        add = findViewById(R.id.main_add);
        listView = findViewById(R.id.main_list);
        initBillList(null,null,null);
        initAdd();
        initCheckBox();
        initDel();
        initSearch();
    }

    private void initSearch() {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.search_pop_window,null);
                builder.setView(view);
                EditText name =  view.findViewById(R.id.search_name);
                EditText start = view.findViewById(R.id.search_start);
                EditText end = view.findViewById(R.id.search_end);
                Button submit = view.findViewById(R.id.search_submit);
                Dialog dialog = builder.show();
                SearchBill searchBill = new SearchBill();
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar=Calendar.getInstance();
                        new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String text = year + "." + (month + 1) + "." + dayOfMonth;
                                start.setText(text);
                            }
                        }
                                ,calendar.get(Calendar.YEAR)
                                ,calendar.get(Calendar.MONTH)
                                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                        searchBill.setStart(new Date(calendar.getTimeInMillis()));
                    }
                });
                end.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar=Calendar.getInstance();
                        new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String text = year + "." + (month + 1) + "." + dayOfMonth;
                                end.setText(text);
                            }
                        }
                                ,calendar.get(Calendar.YEAR)
                                ,calendar.get(Calendar.MONTH)
                                ,calendar.get(Calendar.DAY_OF_MONTH)).show();
                        searchBill.setEnd(new Date(calendar.getTimeInMillis()));
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchBill.setName(name.getText().toString());
                        initBillList(searchBill.getStart(),searchBill.getEnd(),searchBill.getName());
                        dialog.dismiss();

                    }
                });
            }
        });
    }

    private void initCheckBox() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedList.forEach(item->{
                    item.getCheckBox().setChecked(isChecked);
                });
            }
        });
    }
}