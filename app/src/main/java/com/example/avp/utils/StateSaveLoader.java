package com.example.avp.utils;

import java.io.Serializable;

public interface StateSaveLoader {
    boolean isSavedStateExist();
    void writeSerializable(String key, Serializable value);
    Object readSerializable(String key, Class<?> classForRead);
}
