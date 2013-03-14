package com.wtj.stormdemo;

import java.util.Map;
import java.util.Random;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

/**
 * Hello world!
 *
 */
public class App 
{
	public static class ExclaimBolts extends BaseRichBolt{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		OutputCollector _collector;
		
		@SuppressWarnings("rawtypes")
		@Override
		public void prepare(Map stormConf, TopologyContext context,
				OutputCollector collector) {
			_collector = collector;			
		}

		@Override
		public void execute(Tuple input) {
			_collector.emit(input, new Values(input.getString(0)+"!!!"));
			_collector.ack(input);
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
		
	}
	
	public static class MyWordSpout extends BaseRichSpout{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		SpoutOutputCollector _collector;
		private String[] words ={"wangtianju","huhailiang","jiaoshou","chengdawei","cuixin","mahuachun","guangquan"}; 
		
		@Override
		public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
				SpoutOutputCollector collector) {
			_collector = collector;
		}

		@Override
		public void nextTuple() {
				Utils.sleep(100);
				Random ran = new Random();
				_collector.emit(new Values(words[ran.nextInt(words.length)]));
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
		
	}
	
    public static void main( String[] args ) throws Exception
    {
        TopologyBuilder builder = new TopologyBuilder();
        
        builder.setSpout("word", new MyWordSpout(),1);
        builder.setBolt("exclaim", new ExclaimBolts(),1).shuffleGrouping("word");
        
        Config conf = new Config();
        conf.setDebug(true);
        
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
        Thread.sleep(1000*5);
        cluster.killTopology("test");
        cluster.shutdown();
    }
}
