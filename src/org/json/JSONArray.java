/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JSONArray
        implements Iterable<Object>
{

    private final ArrayList<Object> myArrayList = new ArrayList();

    public JSONArray()
    {
    }

    public JSONArray(JSONTokener x) throws JSONException
    {
        this();
        if (x.nextClean() != '[')
        {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') return;
        x.back();
        block4:
        do
        {
            if (x.nextClean() == ',')
            {
                x.back();
                this.myArrayList.add(JSONObject.NULL);
            } else
            {
                x.back();
                this.myArrayList.add(x.nextValue());
            }
            switch (x.nextClean())
            {
                case ',':
                {
                    if (x.nextClean() == ']')
                    {
                        return;
                    }
                    x.back();
                    continue block4;
                }
                case ']':
                {
                    return;
                }
            }
            break;
        } while (true);
        throw x.syntaxError("Expected a ',' or ']'");
    }

    public JSONArray(String source) throws JSONException
    {
        this(new JSONTokener(source));
    }

    public JSONArray(Collection<?> collection)
    {
        if (collection == null) return;
        Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext())
        {
            Object o = iterator.next();
            this.myArrayList.add(JSONObject.wrap(o));
        }
    }

    public JSONArray(Object array) throws JSONException
    {
        this();
        if (!array.getClass().isArray())
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        int length = Array.getLength(array);
        int i = 0;
        while (i < length)
        {
            this.put(JSONObject.wrap(Array.get(array, i)));
            ++i;
        }
    }

    @Override
    public Iterator<Object> iterator()
    {
        return this.myArrayList.iterator();
    }

    public Object get(int index) throws JSONException
    {
        Object object = this.opt(index);
        if (object != null) return object;
        throw new JSONException("JSONArray[" + index + "] not found.");
    }

    public boolean getBoolean(int index) throws JSONException
    {
        Object object = this.get(index);
        if (object.equals(Boolean.FALSE)) return false;
        if (object instanceof String && ((String) object).equalsIgnoreCase("false"))
        {
            return false;
        }
        if (object.equals(Boolean.TRUE)) return true;
        if (!(object instanceof String)) throw new JSONException("JSONArray[" + index + "] is not a boolean.");
        if (!((String) object).equalsIgnoreCase("true"))
            throw new JSONException("JSONArray[" + index + "] is not a boolean.");
        return true;
    }

    public double getDouble(int index) throws JSONException
    {
        Object object = this.get(index);
        try
        {
            double d;
            if (object instanceof Number)
            {
                d = ((Number) object).doubleValue();
                return d;
            }
            d = Double.parseDouble((String) object);
            return d;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, int index) throws JSONException
    {
        E val = this.optEnum(clazz, index);
        if (val != null) return val;
        throw new JSONException("JSONObject[" + JSONObject.quote(Integer.toString(index)) + "] is not an enum of type " + JSONObject.quote(clazz.getSimpleName()) + ".");
    }

    public BigDecimal getBigDecimal(int index) throws JSONException
    {
        Object object = this.get(index);
        try
        {
            return new BigDecimal(object.toString());
        }
        catch (Exception e)
        {
            throw new JSONException("JSONArray[" + index + "] could not convert to BigDecimal.");
        }
    }

    public BigInteger getBigInteger(int index) throws JSONException
    {
        Object object = this.get(index);
        try
        {
            return new BigInteger(object.toString());
        }
        catch (Exception e)
        {
            throw new JSONException("JSONArray[" + index + "] could not convert to BigInteger.");
        }
    }

    public int getInt(int index) throws JSONException
    {
        Object object = this.get(index);
        try
        {
            int n;
            if (object instanceof Number)
            {
                n = ((Number) object).intValue();
                return n;
            }
            n = Integer.parseInt((String) object);
            return n;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    public JSONArray getJSONArray(int index) throws JSONException
    {
        Object object = this.get(index);
        if (!(object instanceof JSONArray)) throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
        return (JSONArray) object;
    }

    public JSONObject getJSONObject(int index) throws JSONException
    {
        Object object = this.get(index);
        if (!(object instanceof JSONObject)) throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
        return (JSONObject) object;
    }

    public long getLong(int index) throws JSONException
    {
        Object object = this.get(index);
        try
        {
            long l;
            if (object instanceof Number)
            {
                l = ((Number) object).longValue();
                return l;
            }
            l = Long.parseLong((String) object);
            return l;
        }
        catch (Exception e)
        {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    public String getString(int index) throws JSONException
    {
        Object object = this.get(index);
        if (!(object instanceof String)) throw new JSONException("JSONArray[" + index + "] not a string.");
        return (String) object;
    }

    public boolean isNull(int index)
    {
        return JSONObject.NULL.equals(this.opt(index));
    }

    public String join(String separator) throws JSONException
    {
        int len = this.length();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < len)
        {
            if (i > 0)
            {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
            ++i;
        }
        return sb.toString();
    }

    public int length()
    {
        return this.myArrayList.size();
    }

    public Object opt(int index)
    {
        if (index < 0) return null;
        if (index >= this.length()) return null;
        Object object = this.myArrayList.get(index);
        return object;
    }

    public boolean optBoolean(int index)
    {
        return this.optBoolean(index, false);
    }

    public boolean optBoolean(int index, boolean defaultValue)
    {
        try
        {
            return this.getBoolean(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public double optDouble(int index)
    {
        return this.optDouble(index, Double.NaN);
    }

    public double optDouble(int index, double defaultValue)
    {
        try
        {
            return this.getDouble(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public int optInt(int index)
    {
        return this.optInt(index, 0);
    }

    public int optInt(int index, int defaultValue)
    {
        try
        {
            return this.getInt(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index)
    {
        return this.optEnum(clazz, index, null);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index, E defaultValue)
    {
        try
        {
            Object val = this.opt(index);
            if (JSONObject.NULL.equals(val))
            {
                return defaultValue;
            }
            if (!clazz.isAssignableFrom(val.getClass())) return Enum.valueOf(clazz, val.toString());
            Enum myE = (Enum) val;
            return (E) myE;
        }
        catch (IllegalArgumentException e)
        {
            return defaultValue;
        }
        catch (NullPointerException e)
        {
            return defaultValue;
        }
    }

    public BigInteger optBigInteger(int index, BigInteger defaultValue)
    {
        try
        {
            return this.getBigInteger(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public BigDecimal optBigDecimal(int index, BigDecimal defaultValue)
    {
        try
        {
            return this.getBigDecimal(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public JSONArray optJSONArray(int index)
    {
        Object o = this.opt(index);
        if (!(o instanceof JSONArray)) return null;
        JSONArray jSONArray = (JSONArray) o;
        return jSONArray;
    }

    public JSONObject optJSONObject(int index)
    {
        Object o = this.opt(index);
        if (!(o instanceof JSONObject)) return null;
        JSONObject jSONObject = (JSONObject) o;
        return jSONObject;
    }

    public long optLong(int index)
    {
        return this.optLong(index, 0L);
    }

    public long optLong(int index, long defaultValue)
    {
        try
        {
            return this.getLong(index);
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public String optString(int index)
    {
        return this.optString(index, "");
    }

    public String optString(int index, String defaultValue)
    {
        String string;
        Object object = this.opt(index);
        if (JSONObject.NULL.equals(object))
        {
            string = defaultValue;
            return string;
        }
        string = object.toString();
        return string;
    }

    public JSONArray put(boolean value)
    {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(Collection<?> value)
    {
        this.put(new JSONArray(value));
        return this;
    }

    public JSONArray put(double value) throws JSONException
    {
        Double d = new Double(value);
        JSONObject.testValidity(d);
        this.put(d);
        return this;
    }

    public JSONArray put(int value)
    {
        this.put(new Integer(value));
        return this;
    }

    public JSONArray put(long value)
    {
        this.put(new Long(value));
        return this;
    }

    public JSONArray put(Map<?, ?> value)
    {
        this.put(new JSONObject(value));
        return this;
    }

    public JSONArray put(Object value)
    {
        this.myArrayList.add(value);
        return this;
    }

    public JSONArray put(int index, boolean value) throws JSONException
    {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(int index, Collection<?> value) throws JSONException
    {
        this.put(index, new JSONArray(value));
        return this;
    }

    public JSONArray put(int index, double value) throws JSONException
    {
        this.put(index, new Double(value));
        return this;
    }

    public JSONArray put(int index, int value) throws JSONException
    {
        this.put(index, new Integer(value));
        return this;
    }

    public JSONArray put(int index, long value) throws JSONException
    {
        this.put(index, new Long(value));
        return this;
    }

    public JSONArray put(int index, Map<?, ?> value) throws JSONException
    {
        this.put(index, new JSONObject(value));
        return this;
    }

    public JSONArray put(int index, Object value) throws JSONException
    {
        JSONObject.testValidity(value);
        if (index < 0)
        {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length())
        {
            this.myArrayList.set(index, value);
            return this;
        }
        do
        {
            if (index == this.length())
            {
                this.put(value);
                return this;
            }
            this.put(JSONObject.NULL);
        } while (true);
    }

    public Object remove(int index)
    {
        if (index < 0) return null;
        if (index >= this.length()) return null;
        Object object = this.myArrayList.remove(index);
        return object;
    }

    public boolean similar(Object other)
    {
        if (!(other instanceof JSONArray))
        {
            return false;
        }
        int len = this.length();
        if (len != ((JSONArray) other).length())
        {
            return false;
        }
        int i = 0;
        while (i < len)
        {
            Object valueThis = this.get(i);
            Object valueOther = ((JSONArray) other).get(i);
            if (valueThis instanceof JSONObject ? !((JSONObject) valueThis).similar(valueOther) : (valueThis instanceof JSONArray ? !((JSONArray) valueThis).similar(valueOther) : !valueThis.equals(valueOther)))
            {
                return false;
            }
            ++i;
        }
        return true;
    }

    public JSONObject toJSONObject(JSONArray names) throws JSONException
    {
        if (names == null) return null;
        if (names.length() == 0) return null;
        if (this.length() == 0)
        {
            return null;
        }
        JSONObject jo = new JSONObject();
        int i = 0;
        while (i < names.length())
        {
            jo.put(names.getString(i), this.opt(i));
            ++i;
        }
        return jo;
    }

    public String toString()
    {
        try
        {
            return this.toString(0);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String toString(int indentFactor) throws JSONException
    {
        StringWriter sw = new StringWriter();
        StringBuffer stringBuffer = sw.getBuffer();
        synchronized (stringBuffer)
        {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    public Writer write(Writer writer) throws JSONException
    {
        return this.write(writer, 0, 0);
    }

    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException
    {
        try
        {
            boolean commanate = false;
            int length = this.length();
            writer.write(91);
            if (length == 1)
            {
                JSONObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
            } else if (length != 0)
            {
                int newindent = indent + indentFactor;
                for (int i = 0; i < length; ++i)
                {
                    if (commanate)
                    {
                        writer.write(44);
                    }
                    if (indentFactor > 0)
                    {
                        writer.write(10);
                    }
                    JSONObject.indent(writer, newindent);
                    JSONObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0)
                {
                    writer.write(10);
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(93);
            return writer;
        }
        catch (IOException e)
        {
            throw new JSONException(e);
        }
    }
}

