package com.nidoos.doocti.sqliteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nidoos.doocti.request.CallActivityRequest;
import com.nidoos.doocti.request.FetchLeadsRequest;
import com.nidoos.doocti.request.OfflineCallActivityRequest;
import com.nidoos.doocti.request.UpdateLeadstatusRequest;
import com.nidoos.doocti.response.FetchLeadsResponse;

import java.util.ArrayList;
import java.util.List;

public class SqliteDB extends SQLiteOpenHelper {


    public SqliteDB(Context context) {
        super(context, "doocti.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LEADS (ID INTEGER PRIMARY KEY AUTOINCREMENT,LEADID TEXT,NAME TEXT,NUMBER TEXT,CALLSTATUS TEXT)");
        db.execSQL("CREATE TABLE MAINFILTER (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT)");
        db.execSQL("CREATE TABLE SUBFILTER (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT)");
        db.execSQL("CREATE TABLE LEADCALLSTATUS (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT)");
        db.execSQL("CREATE TABLE CONTACTCALLSTATUS (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT)");
        db.execSQL("CREATE TABLE CAMPAIGNS (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT)");
        db.execSQL("CREATE TABLE LIVEAGENTS (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,USER TEXT,STATION TEXT,QUEUE TEXT,STATUS TEXT)");
        db.execSQL("CREATE TABLE CALLACTIVITY (ID INTEGER PRIMARY KEY AUTOINCREMENT,SUBJECT TEXT,CALLTYPE TEXT,STARTTIME TEXT,PHONENUMBER TEXT,DESCRIPTION TEXT,DURATION TEXT,CALLRESULT TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS LEADS");
        db.execSQL("DROP TABLE IF EXISTS MAINFILTER");
        db.execSQL("DROP TABLE IF EXISTS SUBFILTER");
        db.execSQL("DROP TABLE IF EXISTS LEADCALLSTATUS");
        db.execSQL("DROP TABLE IF EXISTS CONTACTCALLSTATUS");
        db.execSQL("DROP TABLE IF EXISTS CAMPAIGNS");
        db.execSQL("DROP TABLE IF EXISTS LIVEAGENTS");
        db.execSQL("DROP TABLE IF EXISTS CALLACTIVITY");
    }

    public void clearData(){
        this.getReadableDatabase().execSQL("DELETE FROM LEADS");
        this.getReadableDatabase().execSQL("DELETE FROM MAINFILTER");
        this.getReadableDatabase().execSQL("DELETE FROM SUBFILTER");
        this.getReadableDatabase().execSQL("DELETE FROM LEADCALLSTATUS");
        this.getReadableDatabase().execSQL("DELETE FROM CONTACTCALLSTATUS");
        this.getReadableDatabase().execSQL("DELETE FROM CAMPAIGNS");
        this.getReadableDatabase().execSQL("DELETE FROM LIVEAGENTS");
        this.getReadableDatabase().execSQL("DELETE FROM CALLACTIVITY");
    }

    public void insert_call_activity(String subject, String call_type, String start_time, String phone_number, String description, String duration, String call_result) {
        ContentValues types = new ContentValues();
        types.put("SUBJECT", subject);
        types.put("CALLTYPE", call_type);
        types.put("STARTTIME", start_time);
        types.put("PHONENUMBER", phone_number);
        types.put("DESCRIPTION", description);
        types.put("DURATION", duration);
        types.put("CALLRESULT", call_result);
        this.getWritableDatabase().insertOrThrow("CALLACTIVITY", "", types);
    }


    public List<OfflineCallActivityRequest> get_call_activity() {
        Cursor product_list = this.getReadableDatabase().rawQuery("SELECT * FROM CALLACTIVITY", null);
        List<OfflineCallActivityRequest> group_name_list = new ArrayList<>();
        while (product_list.moveToNext()) {
            group_name_list.add(new OfflineCallActivityRequest(
                    product_list.getInt(0),
                    product_list.getString(1),
                    product_list.getString(2),
                    product_list.getString(3),
                    product_list.getString(4),
                    product_list.getString(5),
                    product_list.getString(6),
                    product_list.getString(7)
            ));

        }
        product_list.close();
        return group_name_list;
    }

    public void deleteActivity(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM CALLACTIVITY WHERE ID = '" + id + "'");
    }

    public void deleteLeads(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String SQL = "DELETE FROM LEADS WHERE ID IN (" + id + ")";
        Log.i("SQL",SQL);
        db.execSQL(SQL);
    }

    public void insert_data(String table, String name, String user, String station, String queue, String status){
        ContentValues types = new ContentValues();
        types.put("NAME", name);
        types.put("USER", user);
        types.put("STATION", station);
        types.put("QUEUE", queue);
        types.put("STATUS", status);
        this.getWritableDatabase().insertOrThrow(table, "", types);
    }

    public void insert_values(String table, String name, String value) {
        ContentValues types = new ContentValues();
        types.put("NAME", name);
        types.put("VALUE", value);
        this.getWritableDatabase().insertOrThrow(table, "", types);
    }

    public void insert_lead_info(String lead_id, String name, String number, String call_status) {
        ContentValues types = new ContentValues();
        types.put("LEADID", lead_id);
        types.put("NAME", name);
        types.put("NUMBER", number);
        types.put("CALLSTATUS", call_status);
        this.getWritableDatabase().insertOrThrow("LEADS", "", types);
    }

    public List<FetchLeadsResponse.Datum> get_leads() {
        Cursor product_list = this.getReadableDatabase().rawQuery("SELECT * FROM LEADS", null);
        List<FetchLeadsResponse.Datum> group_name_list = new ArrayList<>();
        while (product_list.moveToNext()) {
            Log.e("DB data",product_list.getString(0));
            group_name_list.add(new FetchLeadsResponse.Datum(
                    product_list.getString(0),
                    product_list.getString(1),
                    product_list.getString(2),
                    product_list.getString(3),
                    product_list.getString(4)
            ));

        }
        product_list.close();
        return group_name_list;
    }

    public String[] get_top_lead() {
        Cursor getName = this.getReadableDatabase().rawQuery("SELECT NUMBER,LEADID,ID FROM LEADS LIMIT 1", null);
        String[] name_value = new String[3];
        while (getName.moveToNext()) {
            name_value[0]=String.valueOf(getName.getString(0));
            name_value[1]=String.valueOf(getName.getString(1));
//            remove_attented_lead(getName.getString(2));
        }
        getName.close();
        return name_value;
    }

//    public void remove_attented_lead(String id) {
//        SQLiteDatabase db = getWritableDatabase();
//        String SQL = "DELETE FROM LEADS WHERE ID IN (" + id + ")";
//        Log.i("SQL Query",SQL);
//        db.execSQL(SQL);
//    }

    public List<FetchLeadsRequest.SubFilterValue> getSubFilterValue(String table, String values) {
        List<FetchLeadsRequest.SubFilterValue> group_name_list = new ArrayList<>();
        String[] valueArray = values.split(",");
        for (String s : valueArray) {
            String SQL = "SELECT * FROM " + table + " WHERE NAME" + "='" + s + "'";
            Cursor product_list = this.getReadableDatabase().rawQuery(SQL, null);
            while (product_list.moveToNext()) {
                group_name_list.add(new FetchLeadsRequest.SubFilterValue(
                        product_list.getString(1),
                        product_list.getString(2)));
            }
            product_list.close();
        }
        return group_name_list;
    }

    public List<FetchLeadsRequest.Callstatus> getCallStatusValue(String table, String values) {
        List<FetchLeadsRequest.Callstatus> group_name_list = new ArrayList<>();
        String[] valueArray = values.split(",");
        for (String s : valueArray) {
            String SQL = "SELECT * FROM " + table + " WHERE NAME" + "='" + s + "'";
            Cursor product_list = this.getReadableDatabase().rawQuery(SQL, null);
            while (product_list.moveToNext()) {
                group_name_list.add(new FetchLeadsRequest.Callstatus(
                        product_list.getString(1),
                        product_list.getString(2)));
            }
            product_list.close();
        }
        return group_name_list;
    }

    public UpdateLeadstatusRequest.LeadStatus getLeadStatus(String table, String values) {
        List<UpdateLeadstatusRequest.LeadStatus> group_name_list = new ArrayList<>();
        String[] valueArray = values.split(",");
        for (String s : valueArray) {
            String SQL = "SELECT * FROM " + table + " WHERE NAME" + "='" + s + "'";
            Cursor product_list = this.getReadableDatabase().rawQuery(SQL, null);
            while (product_list.moveToNext()) {
                group_name_list.add(new UpdateLeadstatusRequest.LeadStatus(
                        product_list.getString(1),
                        product_list.getString(2)));
            }
            product_list.close();
        }
        return group_name_list.get(0);
    }

    public CallActivityRequest.CallResult getCallStatus(String table, String values) {
        CallActivityRequest.CallResult group_name_list = new CallActivityRequest.CallResult();
        String SQL = "SELECT * FROM " + table + " WHERE NAME" + "='" + values + "'";
        Cursor product_list = this.getReadableDatabase().rawQuery(SQL, null);
        while (product_list.moveToNext()) {
            group_name_list.setName(product_list.getString(1));
            group_name_list.setValue(product_list.getString(2));
        }
        product_list.close();
        return group_name_list;
    }


    public void deleteID() {
        Cursor getName = this.getReadableDatabase().rawQuery("SELECT LEADID FROM LEADS LIMIT 1", null);
        String name_value = "";
        while (getName.moveToNext()) {
            name_value = String.valueOf(getName.getString(0));
        }
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM LEADS WHERE LEADID = '" + name_value + "'");
    }

    public void delete_leads() {
        String SQL = "SELECT ID FROM LEADS LIMIT 1";
        Cursor selected_data = this.getReadableDatabase().rawQuery(SQL, null);
        String data = "";
        while(selected_data.moveToNext()){
            data = selected_data.getString(0);
        }
        selected_data.close();
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM CALLACTIVITY WHERE ID = '" + data + "'");
    }

    public void delete_rows(String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + table);
    }

    public String[] get_station(String table, String title) {

        String SQL = "SELECT STATION,STATUS FROM " + table + " WHERE NAME" + "='" + title + "'";
        Cursor selected_data = this.getReadableDatabase().rawQuery(SQL, null);
        String[] data = new String[2];
        while(selected_data.moveToNext()){
            data[0] = selected_data.getString(0);
            data[1] = selected_data.getString(1);
        }
        selected_data.close();
        return data;
    }


    public List<String> get_values(String table, String title) {
        Cursor groups_list = this.getReadableDatabase().rawQuery("SELECT NAME FROM " + table, null);
        List<String> group_name_list = new ArrayList<>();
        group_name_list.add(title);
        while (groups_list.moveToNext()) {
            group_name_list.add(groups_list.getString(0));
        }
        groups_list.close();
        return group_name_list;
    }

    public List<String> get_liveAgent_values(String table, String title) {
        Cursor groups_list = this.getReadableDatabase().rawQuery("SELECT NAME FROM " + table + " WHERE STATUS" + "='" + "READY" + "'", null);
        List<String> group_name_list = new ArrayList<>();
        group_name_list.add(title);
        while (groups_list.moveToNext()) {
            group_name_list.add(groups_list.getString(0));
        }
        groups_list.close();
        return group_name_list;
    }

    public List<String> get_call_values(String table) {
        Cursor groups_list = this.getReadableDatabase().rawQuery("SELECT NAME FROM " + table, null);
        List<String> group_name_list = new ArrayList<>();
//        group_name_list.add(title);
        while (groups_list.moveToNext()) {
            group_name_list.add(groups_list.getString(0));
        }
        groups_list.close();
        return group_name_list;
    }

    public String get_id_by_name(String table_name, String name) {
        Cursor getName = this.getReadableDatabase().rawQuery("SELECT VALUE FROM " + table_name + " WHERE NAME" + "='" + name + "'", null);
        String name_value = "";
        while (getName.moveToNext()) {
            name_value = String.valueOf(getName.getString(0));
        }
        getName.close();
        return name_value;
    }

}
