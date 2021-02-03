package com.han.zhangben.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.han.zhangben.MainActivity;
import com.han.zhangben.R;
import com.han.zhangben.dao.Ledger;
import com.han.zhangben.entity.Bill;
import com.han.zhangben.entity.Checked;
import com.han.zhangben.utils.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class BillAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private Handler handler;
    private Ledger ledger;
    List<Bill> list;
    public BillAdapter(Context context,List<Bill> billList,Handler handler,Ledger ledger) {
        inflater = LayoutInflater.from(context);
        list = billList;
        this.context = context;
        this.handler = handler;
        if(ledger!=null)
            this.ledger= ledger;
    }

    public List<Bill> getList() {
        return list;
    }

    public void setList(List<Bill> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_entry,null);

        ImageView status = view.findViewById(R.id.list_item_status);
        TextView name = view.findViewById(R.id.list_item_name);
        TextView from = view.findViewById(R.id.list_item_from);
        TextView to = view.findViewById(R.id.list_item_to);
        TextView date = view.findViewById(R.id.list_item_date);
        TextView price = view.findViewById(R.id.list_item_price);
        TextView model = view.findViewById(R.id.list_item_model);
        CheckBox checkBox = view.findViewById(R.id.list_item_ckeck_box);


        Bill current = list.get(position);
        MainActivity.checkedList.add(new Checked(checkBox,position,current.getId()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    MainActivity.checkedSet.put(current.getId(),new Checked(checkBox,position,current.getId()));
                }
                else {
                    MainActivity.checkedSet.remove(current.getId());
                }
            }
        });
        if (current.getStatus() == 0) {
            status.setImageResource(R.mipmap.time);
        } else {
            status.setImageResource(R.mipmap.success);
        }
        name.setText("车主："+current.getName());
        from.setText(current.getFrom());
        to.setText(current.getTo());
        date.setText(DateFormat.date2Str(current.getDate()));
        price.setText(current.getPrice().toString());
        model.setText("车型："+current.getModel());

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createDialog(current);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MainActivity.flag = true;
                handler.sendEmptyMessage(2);
                MainActivity.checkedList.forEach(item->{
                    item.getCheckBox().setChecked(false);
                    item.getCheckBox().setVisibility(CheckBox.VISIBLE);
                });
                return false;
            }
        });
        return view;
    }
    public void remove(int position) {
        list.remove(position);
    }


    private void createDialog(Bill current) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogview = LayoutInflater.from(context).inflate(R.layout.add_pop_window,null);
        builder.setView(dialogview);
        EditText add_name = dialogview.findViewById(R.id.add_name);
        EditText add_from = dialogview.findViewById(R.id.add_from);
        EditText add_to = dialogview.findViewById(R.id.add_to);
        EditText add_date = dialogview.findViewById(R.id.add_date);
        EditText add_price = dialogview.findViewById(R.id.add_price);
        EditText add_model = dialogview.findViewById(R.id.add_model);
        CheckBox add_paied = dialogview.findViewById(R.id.add_paied);
        Button submit = dialogview.findViewById(R.id.add_submit);
        add_name.setText(current.getName());
        add_from.setText(current.getFrom());
        add_to.setText(current.getTo());
        Date date = current.getDate();
        if(date == null) {
            add_date.setText(DateFormat.date2Str(new Date(System.currentTimeMillis())));
        } else {
            add_date.setText(DateFormat.date2Str(date));
        }
        add_model.setText(current.getModel());
        boolean isChecked = current.getStatus()==0?false:true;
        add_paied.setChecked(isChecked);
        add_price.setText(current.getPrice().toString());

        Dialog dialog = builder.show();



        Bill bill = current;
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                new DatePickerDialog( context, new DatePickerDialog.OnDateSetListener() {
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
                ledger.update(bill);
                handler.sendEmptyMessage(1);
                dialog.dismiss();

            }
        });
    }
}
