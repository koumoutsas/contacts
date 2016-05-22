package com.kareebo.contacts.server.handler;

import org.apache.avro.Schema.Field;
import org.apache.gora.persistency.impl.PersistentBase;
import org.apache.gora.query.PartitionQuery;
import org.apache.gora.query.Query;
import org.apache.gora.query.Result;
import org.apache.gora.query.impl.PartitionQueryImpl;
import org.apache.gora.query.impl.QueryBase;
import org.apache.gora.query.impl.ResultBase;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.impl.DataStoreBase;
import org.apache.gora.util.AvroUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Extension of {@link org.apache.gora.memory.store.MemStore} that doesn't clear its contents at close
 */
public class MemStore<K,T extends PersistentBase> extends DataStoreBase<K,T>
{
	// This map behaves like DB, has to be static and concurrent collection
	public ConcurrentSkipListMap<K,T> map=new ConcurrentSkipListMap<>();
	K useId;
	private boolean isClosed=false;

	boolean hasBeenClosed()
	{
		return isClosed;
	}

	@Override
	public String getSchemaName()
	{
		return "default";
	}

	@Override
	public void createSchema()
	{
	}

	@Override
	public void deleteSchema()
	{
		map.clear();
	}

	@Override
	public boolean schemaExists()
	{
		return true;
	}

	@Override
	public T get(K key,String[] fields)
	{
		T obj=map.get(useId==null?key:useId);
		if(obj==null)
		{
			return null;
		}
		return getPersistent(obj,getFieldsToQuery(fields));
	}

	/**
	 * Returns a clone with exactly the requested fields shallowly copied
	 */
	private static <T extends PersistentBase> T getPersistent(T obj,String[] fields)
	{
		List<Field> otherFields=obj.getSchema().getFields();
		String[] otherFieldStrings=new String[otherFields.size()];
		for(int i=0;i<otherFields.size();i++)
		{
			otherFieldStrings[i]=otherFields.get(i).name();
		}
		if(Arrays.equals(fields,otherFieldStrings))
		{
			return obj;
		}
		T newObj=AvroUtils.deepClonePersistent(obj);
		for(Field otherField : otherFields)
		{
			int index=otherField.pos();
			newObj.put(index,obj.get(index));
		}
		return newObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void put(K key,T obj)
	{
		map.put(key,obj);
	}

	@Override
	public boolean delete(K key)
	{
		return map.remove(key)!=null;
	}

	@Override
	public long deleteByQuery(Query<K,T> query)
	{
		try
		{
			long deletedRows=0;
			Result<K,T> result=query.execute();
			while(result.next())
			{
				if(delete(result.getKey()))
				{
					deletedRows++;
				}
			}
			return deletedRows;
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	@Override
	public Result<K,T> execute(Query<K,T> query)
	{
		K startKey=query.getStartKey();
		K endKey=query.getEndKey();
		if(startKey==null)
		{
			startKey=map.firstKey();
		}
		if(endKey==null)
		{
			endKey=map.lastKey();
		}
		//check if query.fields is null
		query.setFields(getFieldsToQuery(query.getFields()));
		return new MemResult<>(this,query,map.subMap(startKey,true,endKey,true));
	}

	@Override
	public Query<K,T> newQuery()
	{
		return new MemQuery<>(this);
	}

	@Override
	/**
	 * Returns a single partition containing the original query
	 */
	public List<PartitionQuery<K,T>> getPartitions(Query<K,T> query)
	{
		List<PartitionQuery<K,T>> list=new ArrayList<>();
		PartitionQueryImpl<K,T> pqi=new PartitionQueryImpl<>(query);
		pqi.setConf(getConf());
		list.add(pqi);
		return list;
	}

	@Override
	public void flush()
	{
	}

	@Override
	public void close()
	{
		isClosed=true;
	}

	private static class MemQuery<K,T extends PersistentBase> extends QueryBase<K,T>
	{
		MemQuery(DataStore<K,T> dataStore)
		{
			super(dataStore);
		}
	}

	private static class MemResult<K,T extends PersistentBase> extends ResultBase<K,T>
	{
		private NavigableMap<K,T> map;
		private Iterator<K> iterator;

		MemResult(DataStore<K,T> dataStore,Query<K,T> query
			         ,NavigableMap<K,T> map)
		{
			super(dataStore,query);
			this.map=map;
			iterator=map.navigableKeySet().iterator();
		}

		@Override
		public float getProgress() throws IOException
		{
			return 0;
		}

		@Override
		protected void clear()
		{
		} //do not clear the object in the store

		@Override
		public boolean nextInner() throws IOException
		{
			if(!iterator.hasNext())
			{
				return false;
			}
			key=iterator.next();
			persistent=map.get(key);
			return true;
		}

		//@Override
		public void close()
		{
		}
	}
}
