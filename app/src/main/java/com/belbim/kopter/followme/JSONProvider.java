package com.belbim.kopter.followme;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;



/**
 * Created by asay on 23.12.2014.
 */
public class JSONProvider<T>  {

    Gson gson = new Gson();

    public String entityToJson(T entity) {

        String s = gson.toJson(entity);
        return s;
    }

    public T jsonToEntity(String json) {

        Type type = new TypeToken<T>() {}.getType();
        T entity = gson.fromJson(json, type);
        return entity;
    }

    public Collection<T> fromJSonToEntityList(String json) {

        Collection<T> list = gson.fromJson(json, new TypeToken<List<T>>() {}.getType());
        return list;
    }


}
