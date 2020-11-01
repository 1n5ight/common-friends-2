package njucs;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class CommonFriends2 {
    static class Mapper1 extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] splits = line.split(", ");
            String name = splits[0];
            String[] friends = splits[1].split(" ");
            for (String friend : friends) {
                context.write(new Text(friend), new Text(name));
            }
        }
    }

    static class Reducer1 extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            NameList nl = new NameList();
            for (Text name : values) {
                nl.addName(name.toString());
            }
            context.write(key, new Text(nl.getList()));
        }
    }

    static class Mapper2 extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] splits = line.split("\t");
            String friend = splits[0];
            String[] names = splits[1].split(", ");
            Arrays.sort(names);// 排序以防重复

            for (int i = 0; i < names.length - 1; i++) {
                for (int j = i + 1; j < names.length; j++) {
                    Pair pair = new Pair(names[i], names[j]);
                    context.write(new Text(pair.outputPair()), new Text(friend));
                }
            }
        }
    }

    static class Reducer2 extends Reducer<Text, Text, Text, Text> {
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            Set<String> set = new HashSet<String>();
            for (Text friend : values) {
                if (!set.contains(friend.toString()))
                    set.add(friend.toString());
            }
            NameList nl = new NameList();
            for (String friend : set) {
                nl.addName(friend);
            }

            StringBuffer buffer = new StringBuffer();
            buffer.append("[" + nl.getList() + "])");

            context.write(key, new Text(buffer.toString()));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job1 = Job.getInstance(conf);
        job1.setJarByClass(CommonFriends2.class);
        job1.setMapperClass(Mapper1.class);
        job1.setReducerClass(Reducer1.class);

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job1, new Path("input"));
        FileOutputFormat.setOutputPath(job1, new Path("output"));

        boolean res1 = job1.waitForCompletion(true);

        conf.set("mapred.textoutputformat.ignoreseparator", "true");
        conf.set("mapred.textoutputformat.separator", ", ");
        Job job2 = Job.getInstance(conf);
        job2.setJarByClass(CommonFriends2.class);
        job2.setMapperClass(Mapper2.class);
        job2.setReducerClass(Reducer2.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job2, new Path("output"));
        FileOutputFormat.setOutputPath(job2, new Path("output1"));

        boolean res2 = job2.waitForCompletion(true);

        if (res1 == true && res2 == true) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}