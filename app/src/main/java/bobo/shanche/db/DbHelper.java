package bobo.shanche.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import bobo.shanche.R;
import bobo.shanche.bean.BeanCollection;

/**
 * Created by bobo1 on 2017/2/13.
 */

public class DbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String mTableLine;
    private String mTableSite;
    /*
    现版本 2
    文件名 "stBus.db"
    限制 50
     */
    public DbHelper(Context context) {
        super(context, context.getString(R.string.db_file_name), null, 2);
        mContext=context;
        mTableLine = mContext.getString(R.string.db_table_line);
        mTableSite = mContext.getString(R.string.db_table_site);
    }

    public List<BeanCollection> getCollection(String tableName){
        Cursor cursor = getReadableDatabase().query(tableName,null,null,null,null,null,"_id DESC","50");
        List<BeanCollection> list = new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                BeanCollection tmp = new BeanCollection();
                tmp.setId(cursor.getString(1));
                tmp.setName(cursor.getString(2));
                tmp.setUpDown(cursor.getInt(3));
                list.add(tmp);
            }while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public boolean isCollected(String table ,String id, int upDown){
        Cursor cursor = getReadableDatabase().query(table,null,"id=? AND up_down=?",new String[]{id,Integer.toString(upDown)},null,null,null);
        boolean re = cursor.moveToFirst();
        cursor.close();
        return re;
    }

    public void deleteCollection(String table ,String id, int upDown ){
        getWritableDatabase().delete(table,"id=? and up_down=?",new String[]{id,Integer.toString(upDown)});
    }


    public void addCollection(String table ,String id ,String name , int upDown ){
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        if(table.equals(mContext.getString(R.string.db_table_line))){
            cv.put("line_name",name);
        }else {
            cv.put("site_name",name);
        }
        cv.put("up_down",Integer.toString(upDown));
        db.insert(table,null,cv);

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + mTableLine + " (_id integer PRIMARY KEY ,id text,line_name text,up_down text)");
        db.execSQL("CREATE TABLE " + mTableSite + " (_id integer PRIMARY KEY ,id text,site_name text,up_down text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch (oldVersion){
            case 1:
                //直接删除
                db.execSQL("DROP TABLE IF EXISTS collection");
                db.execSQL("DROP TABLE IF EXISTS record");
                db.execSQL("CREATE TABLE IF EXISTS " + mTableLine + " (_id integer PRIMARY KEY ,id text,line_name text,up_down text)");
                db.execSQL("CREATE TABLE IF EXISTS " + mTableSite + " (_id integer PRIMARY KEY ,id text,site_name text,up_down text)");
            default:

        }

    }
}
