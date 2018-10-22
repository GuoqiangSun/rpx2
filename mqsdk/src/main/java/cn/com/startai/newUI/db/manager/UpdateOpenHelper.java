package cn.com.startai.newUI.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import org.greenrobot.greendao.database.Database;

import cn.com.startai.newUI.db.gen.DaoMaster;
import cn.com.startai.newUI.db.gen.WanBindingDeviceDao;

/**
 * author: Guoqiang_Sun
 * date : 2018/4/20 0020
 * desc :
 */
public class UpdateOpenHelper extends DaoMaster.OpenHelper {

    private static final String NAME_DB = "rpx.db";

    public UpdateOpenHelper(Context mContext) {
        this(mContext, NAME_DB);
    }

    public UpdateOpenHelper(Context context, String name) {
        super(context, name);
    }

    public UpdateOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);

    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                },
                WanBindingDeviceDao.class);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
