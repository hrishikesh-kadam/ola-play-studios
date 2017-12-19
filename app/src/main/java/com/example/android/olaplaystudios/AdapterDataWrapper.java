package com.example.android.olaplaystudios;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Hrishikesh Kadam on 19/12/2017
 */

public class AdapterDataWrapper {

    public int dataViewType;
    public Object data;

    public AdapterDataWrapper(int dataViewType, Object data) {
        this.dataViewType = dataViewType;
        this.data = data;
    }

    public String getViewTypeString() {

        Field[] fields = ViewType.class.getFields();

        for (Field field : fields) {

            if (Modifier.isFinal(field.getModifiers())) {

                int tempViewType = -1;

                try {

                    tempViewType = field.getInt(null);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {

                }

                if (tempViewType == dataViewType)
                    return field.getName();
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "AdapterDataWrapper { " +
                "dataViewType = " + getViewTypeString() +
                ", data = " + data +
                " }";
    }
}
