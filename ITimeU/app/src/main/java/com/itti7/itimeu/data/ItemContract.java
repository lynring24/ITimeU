package com.itti7.itimeu.data;

import android.provider.BaseColumns;

/**
 * 계약 클래스는 URI, 테이블 및 컬럼의 이름을 정의하는 상수를 유지하는 컨테이너입니다.
 * 계약 클래스를 사용하면 동일한 패키지 내 모든 클래스에 동일한 상수를 사용할 수 있습니다.
 * 즉, 어느 한 장소에서 컬럼 이름을 변경하면 코드 전체에 변경 사항이 반영됩니다.
 */

public final class ItemContract {

    private ItemContract() {}

    public static final class ItemEntry implements BaseColumns {

        public final static String TABLE_NAME = "list";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_ITEM_DATE = "date";
        public final static String COLUMN_ITEM_TOTAL_UNIT = "totalUnit";
        public final static String COLUMN_ITEM_UNIT = "unit";
        public final static String COLUMN_ITEM_STATUS = "status";

        public final static int TODO = 0;
        public final static int DO = 1;
        public final static int DONE = 2;
    }
}
