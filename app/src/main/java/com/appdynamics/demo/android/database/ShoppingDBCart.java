package com.appdynamics.demo.android.database;

import android.provider.BaseColumns;

public final class ShoppingDBCart {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ShoppingDBCart() {}

    /* Inner class that defines the table contents */
    public static abstract class ItemList implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_NAME = "name";
        public static final String ITEM_IMAGE_PATH = "path";
    }
}