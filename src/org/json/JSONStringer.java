/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json;

import java.io.StringWriter;

public class JSONStringer
        extends JSONWriter
{

    public JSONStringer()
    {
        super(new StringWriter());
    }

    public String toString()
    {
        if (this.mode != 'd') return null;
        String string = this.writer.toString();
        return string;
    }
}

