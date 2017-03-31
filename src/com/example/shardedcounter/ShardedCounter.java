package com.example.shardedcounter;

import com.google.appengine.api.datastore.*;

import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShardedCounter {
	private static final DatastoreService DS = DatastoreServiceFactory.getDatastoreService();
	private static final int NUM_SHARDS = 20;
	private final Random generator = new Random();
	private static final Logger LOG = Logger.getLogger(ShardedCounter.class.getName());
	
	public final long getCount(){
		long sum = 0;
		Query query = new Query("SimpleCounterShard");
		for (Entity e:DS.prepare(query).asIterable()) {
			sum += (Long) e.getProperty("count");
		}
		return sum;
	}
	
	public final void increment(){
		int shardNum = generator.nextInt(NUM_SHARDS);
		Key shardKey = KeyFactory.createKey("SimpleCounterShard", Integer.toString(shardNum));
		Transaction tx = DS.beginTransaction();
		Entity shard;
		try {
			try {
				shard = DS.get(tx, shardKey);
				long count = (Long) shard.getProperty("count");
				shard.setUnindexedProperty("count", count + 1L);
			} catch (EntityNotFoundException e) {
				shard = new Entity(shardKey);
				shard.setUnindexedProperty("count", 1L);
			}
			DS.put(tx, shard);
			tx.commit();
		} catch(ConcurrentModificationException e) {
			LOG.log(Level.WARNING, "You need more shards");
			LOG.log(Level.WARNING, e.toString(), e);
		} catch (Exception e) {
			LOG.log(Level.WARNING, e.toString(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
	
}
