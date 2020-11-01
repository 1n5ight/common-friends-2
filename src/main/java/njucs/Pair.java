package njucs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Pair implements WritableComparable<Pair> {
    private String name1;
    private String name2;

    public Pair(String s1, String s2) {
        this.name1 = s1;
        this.name2 = s2;
    }

    public String outputPair() {
        return "([" + name1 + ", " + name2 + "]";
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    public void readFields(DataInput in) throws IOException {
        name1 = in.readLine();
        name2 = in.readLine();
    }

    public void write(DataOutput out) throws IOException {
        out.writeChars(name1);
        out.writeChars(name2);
    }

    public int compareTo(Pair p) {
        if (name1.compareTo(p.getName1()) < 0) {
            return -1;
        } else if (name1.compareTo(p.getName1()) > 0) {
            return 1;
        } else if (name1.equals(p.getName1()) && name2.compareTo(p.getName2()) < 0) {
            return -1;
        } else if (name1.equals(p.getName1()) && name2.compareTo(p.getName2()) > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}