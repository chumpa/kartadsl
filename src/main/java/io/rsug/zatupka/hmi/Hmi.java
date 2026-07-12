package io.rsug.zatupka.hmi;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Hmi {
    public final String typeID;
    public final Map<String, Attribute> attributeMap;

    public Hmi(Instance instance) {
        this.typeID = Objects.requireNonNull(instance.getTypeid());
        this.attributeMap = map(instance);
    }

    public String getString(String name) {
        return getString(attributeMap, name);
    }

    public List<Value> getValues(String name) {
        return getValues(attributeMap, name);
    }

    public Instance getInstanceOrNull(String name) {
        return getInstanceOrNull(attributeMap, name);
    }

    public static Map<String, Attribute> map(Instance instance) {
        Map<String, Attribute> map = new LinkedHashMap<>();
        for (Attribute attr : instance.getAttribute()) {
            Objects.requireNonNull(attr);
            Objects.requireNonNull(attr.getName());
            map.put(attr.getName(), attr);
        }
        return map;
    }

    public static String getString(Map<String, Attribute> map, String name) {
        Attribute attr = Objects.requireNonNull(map.get(name));
        if (attr.isIsleave() && "string".equals(attr.getLeaveTypeid()) && attr.getValue() != null && attr.getValue().size() == 1) {
            Value v = attr.getValue().getFirst();
            Objects.requireNonNull(v);
            if (v.isIsnull()) return null;
            Objects.requireNonNull(v.getContent());
            if (v.getContent().size() == 1) return (String) v.getContent().getFirst();
        }
        throw new RuntimeException();
    }

    public static List<Value> getValues(Map<String, Attribute> map, String name) {
        Attribute attr = Objects.requireNonNull(map.get(name));
        if (!attr.isIsleave() && attr.getLeaveTypeid() == null) {
            return Objects.requireNonNull(attr.getValue());
        }
        throw new RuntimeException();
    }

    /**
     * Возвращает первый //attribute[name]/value[index==0]/instance
     *
     * @param map
     * @param name
     * @return
     */
    public static Instance getInstanceOrNull(Map<String, Attribute> map, String name) {
        List<Value> values = getValues(map, name);
        if (values.isEmpty()) throw new RuntimeException();
        Value value = Objects.requireNonNull(values.getFirst());
        if (value.isIsnull()) {
            return null;    // самый правильный вариант
        } else if (value.getContent() != null && !value.getContent().isEmpty() && value.getContent().getFirst() instanceof Instance) {
            return (Instance) value.getContent().getFirst();
        } else {
            throw new RuntimeException();
        }
    }
}
