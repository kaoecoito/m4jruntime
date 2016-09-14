package br.com.sisprof.m4jruntime.runtime;

/**
 * Created by kaoe on 12/09/16.
 */
public abstract class NumberOperations {


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
            for (int i=0;i<digits.length;i++) {
                char digit = digits[i];
                if ((digit=='-' || digit=='+') && i==0) {
                    point = true;
                } else if (digit=='.' && !point) {
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
            boolean hasPoint = false;
            for (int i=0;i<digits.length;i++) {
                char digit = digits[i];
                if ((digit=='-' || digit=='+') && i==0) {
                    builder.append(digit);
                } else if (digit=='.' && !hasPoint) {
                    builder.append(digit);
                    hasPoint = true;
                } else if (!Character.isDigit(digit)) {
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
            return number.longValue();
        } else {
            return number.longValue();
        }
    }

    public static  Double toDouble(String value) {
        Number number = toNumber(value);
        if (number instanceof Double) {
            return number.doubleValue();
        } else {
            return number.doubleValue();
        }
    }

    public static int compareAsNumber(String number1, String number2) {
        Number value = toNumber(number1);
        if (value instanceof Long) {
            return ((Long)value).compareTo(NumberOperations.toLong(number2.toString()));
        } else {
            return ((Double)value).compareTo(NumberOperations.toDouble(number2.toString()));
        }
    }

    public static Number add(Number v1, Number v2) {
        Number ret;
        if (v1 instanceof Double || v2 instanceof Double) {
            ret = v1.doubleValue() + v2.doubleValue();
        } else {
            ret = v1.longValue() + v2.longValue();
        }
        return ret;
    }

    public static Number sub(Number v1, Number v2) {
        Number ret;
        if (v1 instanceof Double || v2 instanceof Double) {
            ret = v1.doubleValue() - v2.doubleValue();
        } else {
            ret = v1.longValue() - v2.longValue();
        }
        return ret;
    }

    public static Number multi(Number v1, Number v2) {
        Number ret;
        if (v1 instanceof Double || v2 instanceof Double) {
            ret = v1.doubleValue() * v2.doubleValue();
        } else {
            ret = v1.longValue() * v2.longValue();
        }
        return ret;
    }

    public static Number expr(Number v1, Number v2) {
        Number ret;
        if (v1 instanceof Double || v2 instanceof Double) {
            ret = Math.pow(v1.doubleValue(), v2.doubleValue());
        } else {
            ret = Math.pow(v1.longValue(), v2.longValue());
        }
        return ret;
    }

    public static Number div(Number v1, Number v2) {
        return (v1.doubleValue() / v2.doubleValue());
    }

    public static Number idiv(Number v1, Number v2) {
        return ((Double)(v1.doubleValue() / v2.doubleValue())).longValue();
    }

    public static Number mod(Number v1, Number v2) {
        return ((Double)(v1.doubleValue() % v2.doubleValue())).longValue();
    }

}
