package njucs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class NameList implements Writable {
    private String namelist = "";

    public String getList() {
        return namelist;
    }

    public String getName(int i) {
        String[] names = namelist.split(", ");
        return names[i];
    }

    public void addName(String name) {
        StringBuffer sb = new StringBuffer();
        if (namelist.equals("")) {
            sb.append(name);
        } else {
            sb.append(namelist + ", " + name);
        }
        namelist = sb.toString();
    }

    public void readFields(DataInput in) throws IOException {
        namelist = in.readLine();
    }

    public void write(DataOutput out) throws IOException {
        out.writeChars(namelist);
    }
}