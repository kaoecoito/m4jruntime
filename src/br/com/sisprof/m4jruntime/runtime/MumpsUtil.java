package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 12/09/16.
 */
public abstract class MumpsUtil {


    public static boolean isNull(MValue test) {
        if (test==null) return true;
        Object value = test.getValue();
        return (MValue.NULL.equals(test) || value==null || "".equals(value.toString()));
    }

    public static boolean isFalse(MValue test) {
        if (isNull(test)) return true;
        Object value = test.getValue();
        if ((value instanceof Number && ((Number)value).doubleValue()==0d) || "0".equals(value.toString())) {
            return true;
        }
        return false;
    }

}
