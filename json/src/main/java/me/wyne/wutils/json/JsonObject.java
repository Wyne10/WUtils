package me.wyne.wutils.json;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public record JsonObject(Object holder, Field field, Type type) {}