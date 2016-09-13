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

    public static boolean isNumber(String value) {
        boolean ret = true;
        if (value!=null && !value.isEmpty()) {
            char[] digits = value.toCharArray();
            boolean point = false;
            for (char digit:digits) {
                if (digit=='.' && !point) {
                    point = true;
                } else if (!Character.isDigit(digit)) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    public static Number toNumber(String value) {
        Number ret = 0l;
        if (value!=null) {
            StringBuilder builder = new StringBuilder();
            char[] digits = value.toCharArray();
            for (char digit:digits) {
                if (!Character.isDigit(digit) && digit!='.') {
                    break;
                } else {
                    builder.append(digit);
                }
            }
            if (builder.length()>0) {
                String str = builder.toString();
                try {
                    if (str.contains(".")) {
                        ret = Double.parseDouble(str);
                    } else {
                        ret = Long.parseLong(str);
                    }
                } catch (Exception ignore){}
            }
        }
        return ret;
    }

    public static  Long toLong(String value) {
        Number number = toNumber(value);
        if (number instanceof Long) {
            return (Long)number;
        } else {
            return ((Double)number).longValue();
        }
    }

    public static  Double toDouble(String value) {
        Number number = toNumber(value);
        if (number instanceof Double) {
            return (Double) number;
        } else {
            return number.doubleValue();
        }
    }

    public static int compareAsNumber(String number1, String number2) {
        Number value = toNumber(number1);
        if (value instanceof Long) {
            return ((Long)value).compareTo(MumpsUtil.toLong(number2.toString()));
        } else {
            return ((Double)value).compareTo(MumpsUtil.toDouble(number2.toString()));
        }
    }

}
