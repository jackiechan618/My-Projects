import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;



public class LanguageModel {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		int threashold = 0;  // output threashold

		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			threashold = conf.getInt("threashold", 5);  
		}

		// input comes from the output of NGramLibraryBuilder, which has been stored at HDFS 
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			if ((value == null) || (value.toString().trim().length() == 0)) {
				return;
			}
			
			String line = value.toString().trim();
			String[] wordsPlusCount = line.split("\t");

			if (wordsPlusCount.length < 2) {
				return;
			}
			
			String[] words = wordsPlusCount[0].split("\\s+");
			int count = Integer.valueOf(wordsPlusCount[wordsPlusCount.length - 1]);

			// if count less than threashold, return
			if (count <= threashold) {
				return;
			}

			// split into output key and value
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < words.length - 1; i++) {
				sb.append(words[i]).append(" ");
			}
			
			String outputKey = sb.toString().trim();
			String outputValue = words[words.length - 1];
			
			if (!((outputKey == null) || (outputKey.length() < 1))) {
				context.write(new Text(outputKey), new Text(outputValue + "=" + count));
			}
		}
	}

	// write output to database
	public static class Reduce extends Reducer<Text, Text, DBOutputWritable, NullWritable> {
		int n = 0;

		public void setup(Context context) {
			Configuration conf = context.getConfiguration();
			n = conf.getInt("n", 5);
		}

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
	
			// key: count, value: words list
			TreeMap<Integer, List<String>> tm = new TreeMap<Integer, List<String>>(Collections.reverseOrder());
			for (Text val : values) {
				String cur_val = val.toString().trim();
				String word = cur_val.split("=")[0].trim();
				int count = Integer.parseInt(cur_val.split("=")[1].trim());
				
				if(tm.containsKey(count)) {
					tm.get(count).add(word);
				} else {
					List<String> list = new ArrayList<>();
					list.add(word);
					tm.put(count, list);
				}
			}


			// get top n words
			Iterator<Integer> iter = tm.keySet().iterator();
			
			for(int j=0 ; iter.hasNext() && j < n; j++) {
				int keyCount = iter.next();
				List<String> words = tm.get(keyCount);
				
				for(String curWord: words) {
					context.write(new DBOutputWritable(key.toString(), curWord, keyCount), NullWritable.get());
					j++;
				}
			}
		}
	}
}
