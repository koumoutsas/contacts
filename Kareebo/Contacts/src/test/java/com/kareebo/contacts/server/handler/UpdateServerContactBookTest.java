package com.kareebo.contacts.server.handler;

import com.kareebo.contacts.server.gora.HashIdentity;
import com.kareebo.contacts.server.gora.HashIdentityValue;
import com.kareebo.contacts.server.gora.User;
import com.kareebo.contacts.thrift.*;
import org.apache.gora.store.DataStore;
import org.apache.gora.store.DataStoreFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.TBase;
import org.junit.Test;
import org.vertx.java.core.Future;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Unit test for {@link UpdateServerContactBook}
 */
public class UpdateServerContactBookTest
{
	@Test
	public void testUpdateServerContactBook() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(true);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Add);
				operations.add(ContactOperationType.Add);
				operations.add(ContactOperationType.Delete);
				operations.add(ContactOperationType.Delete);
				operations.add(ContactOperationType.Update);
				operations.add(ContactOperationType.Update);
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				for(long i=userId-3;i<userId-1;++i)
				{
					final com.kareebo.contacts.server.gora.EncryptedBuffer encryptedBuffer=new com.kareebo.contacts.server.gora
						                                                                           .EncryptedBuffer();
					encryptedBuffer.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
					final ByteBuffer key=getKey(i);
					encryptedBuffer.setBuffer(key);
					clientValid.getComparisonIdentities().add(encryptedBuffer);
				}
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookEmpty() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(true);
			}

			@Override
			void setupOperations()
			{
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
				graph.close();
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookWrongOperation() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(false);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Fake);
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookRepeatedComparisonIdentity() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(false);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Add);
				operations.add(ContactOperationType.Add);
			}

			@Override
			void adjustContactOperations()
			{
				final ArrayList<ContactOperation> list=new ArrayList<>(contactOperations);
				list.get(0).setComparisonIdentity(list.get(1).getComparisonIdentity());
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookUnresolvedAdd() throws Exception
	{
		new UnresolvedTest()
		{
			@Override
			ContactOperationType type()
			{
				return ContactOperationType.Add;
			}
		}.run();
	}

	@Test
	public void testUpdateServerContactBookUnresolvedDelete() throws Exception
	{
		new UnresolvedTest()
		{
			@Override
			ContactOperationType type()
			{
				return ContactOperationType.Delete;
			}
		}.run();
	}

	@Test
	public void testUpdateServerContactBookUnresolvedUpdate() throws Exception
	{
		new UnresolvedTest()
		{
			@Override
			ContactOperationType type()
			{
				return ContactOperationType.Update;
			}
		}.run();
	}

	@Test
	public void testUpdateServerContactBookMissingComparisonIdentity() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(false);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Delete);
			}

			@Override
			void adjustContactOperations()
			{
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				expectedEdges.add(new Edge(clientIdValid.getUser(),userId));
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookIllegalState() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(false);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Add);
			}

			@Override
			void adjustContactOperations()
			{
				final HashIdentity identity=identityDataStore.get(getKey(userId));
				identity.setHashIdentity("");
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	@Test
	public void testUpdateServerContactBookWrongAlgorithm() throws Exception
	{
		class Test extends Base
		{
			Test() throws Exception
			{
				super(false);
			}

			@Override
			void setupOperations()
			{
				operations.add(ContactOperationType.Add);
			}

			@Override
			void adjustContactOperations()
			{
				contactOperations.iterator().next().getComparisonIdentity().setAlgorithm(EncryptionAlgorithm.Fake);
			}

			@Override
			void runImpl(final Future<Void> result)
			{
				((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),
					                                                                     signature,result);
			}
		}
		new Test().run();
	}

	private abstract class UnresolvedTest extends Base
	{
		UnresolvedTest() throws Exception
		{
			super(false);
		}

		@Override
		void adjustContactOperations()
		{
			identityDataStore.delete(getKey(userId));
		}

		@Override
		void setupOperations()
		{
			operations.add(type());
		}

		abstract ContactOperationType type();

		@Override
		void runImpl(final Future<Void> result)
		{
			if(type()==ContactOperationType.Delete)
			{
				final com.kareebo.contacts.server.gora.EncryptedBuffer encryptedBuffer=new com.kareebo.contacts.server.gora
					                                                                           .EncryptedBuffer();
				encryptedBuffer.setAlgorithm(com.kareebo.contacts.server.gora.EncryptionAlgorithm.RSA2048);
				final ByteBuffer key=getKey(userId);
				encryptedBuffer.setBuffer(key);
				expectedComparisonIdentities.add(encryptedBuffer);
				clientValid.getComparisonIdentities().add(encryptedBuffer);
				expectedEdges.add(new Edge(clientIdValid.getUser(),userId));
			}
			((UpdateServerContactBook)signatureVerifier).updateServerContactBook1(new ContactOperationSet(contactOperations),signature,
				                                                                     result);
		}
	}

	private abstract class Base extends SignatureVerifierTestBase
	{
		final ArrayList<ContactOperationType> operations=new ArrayList<>();
		final HashSet<ContactOperation> contactOperations=new HashSet<>();
		final boolean expectedResult;
		final DataStore<ByteBuffer,HashIdentity> identityDataStore;
		final HashSet<Edge> expectedEdges=new HashSet<>();
		final HashSet<com.kareebo.contacts.server.gora.EncryptedBuffer> expectedComparisonIdentities=new HashSet<>();
		Graph graph=new Graph();
		private long startUserId;

		Base(final boolean expectedResult) throws Exception
		{
			identityDataStore=DataStoreFactory.getDataStore(ByteBuffer.class,HashIdentity.class,new Configuration());
			this.expectedResult=expectedResult;
			setUp();
		}

		@Override
		SignatureVerifier construct(final DataStore<Long,User> dataStore)
		{
			return new UpdateServerContactBook(dataStore,identityDataStore,graph);
		}

		@Override
		TBase constructPlaintext()
		{
			setupOperations();
			setupIdentityDatastore();
			for(ContactOperationType t : operations)
			{
				if(!contactOperations.add(createContactOperation(t)))
				{
					fail("Contact operation already added");
				}
			}
			adjustContactOperations();
			return new ContactOperationSet(contactOperations);
		}

		abstract void setupOperations();

		private void setupIdentityDatastore()
		{
			final int n=operations.size();
			startUserId=addUsers(n);
			for(int i=0;i<n;++i)
			{
				addIdentity(startUserId+i);
			}
			identityDataStore.close();
		}

		private ContactOperation createContactOperation(final ContactOperationType type)
		{
			final long userId=startUserId++;
			final ByteBuffer key=getKey(userId);
			final EncryptedBuffer encryptedBuffer=new EncryptedBuffer(key,EncryptionAlgorithm.RSA2048,clientIdValid);
			switch(type)
			{
				case Add:
					try
					{
						expectedComparisonIdentities.add(TypeConverter.convert(encryptedBuffer));
						addEdge(userId);
					}
					catch(NoSuchAlgorithmException e)
					{
						assertTrue(false);
						return null;
					}
					break;
				case Delete:
					graph.edges.add(new Edge(clientIdValid.getUser(),userId));
					break;
				case Update:
					addEdge(userId);
					break;
			}
			return new ContactOperation(new HashBuffer(key,HashAlgorithm.SHA256),type,encryptedBuffer);
		}

		void adjustContactOperations()
		{
		}

		private void addIdentity(final Long user)
		{
			final ByteBuffer key=getKey(user);
			final HashIdentity hashIdentity=new HashIdentity();
			hashIdentity.setHash(key);
			final HashIdentityValue value=new HashIdentityValue();
			value.setId(user);
			value.setConfirmers(new ArrayList<Long>());
			hashIdentity.setHashIdentity(value);
			identityDataStore.put(key,hashIdentity);
		}

		ByteBuffer getKey(final Long user)
		{
			final ByteBuffer key=ByteBuffer.allocate(Long.SIZE/8);
			key.putLong(user);
			key.rewind();
			key.mark();
			return key;
		}

		void addEdge(final long n)
		{
			expectedEdges.add(new Edge(clientIdValid.getUser(),n));
		}

		void run() throws NoSuchAlgorithmException
		{
			if(!expectedResult)
			{
				expectedEdges.clear();
				expectedComparisonIdentities.clear();
			}
			final Future<Void> result=new DefaultFutureResult<>();
			runImpl(result);
			assertEquals(expectedResult,result.succeeded());
			if(!expectedResult)
			{
				//noinspection ThrowableResultOfMethodCallIgnored
				assertEquals(FailedOperation.class,result.cause().getClass());
			}
			assertEquals(expectedResult,graph.isClosed);
			assertTrue(testGraph(expectedEdges));
			assertEquals(expectedComparisonIdentities.size(),clientValid.getComparisonIdentities().size());
			if(!expectedComparisonIdentities.isEmpty())
			{
				final com.kareebo.contacts.server.gora.EncryptionAlgorithm algorithm=expectedComparisonIdentities.iterator().next()
					                                                                     .getAlgorithm();
				final HashSet<ByteBuffer> s1=new HashSet<>(expectedComparisonIdentities.size());
				final HashSet<ByteBuffer> s2=new HashSet<>(expectedComparisonIdentities.size());
				for(final com.kareebo.contacts.server.gora.EncryptedBuffer e : clientValid.getComparisonIdentities())
				{
					assertEquals(algorithm,e.getAlgorithm());
					final ByteBuffer b=e.getBuffer();
					b.rewind();
					b.mark();
					s1.add(b);
				}
				for(final com.kareebo.contacts.server.gora.EncryptedBuffer e : expectedComparisonIdentities)
				{
					final ByteBuffer b=e.getBuffer();
					b.rewind();
					b.mark();
					s2.add(b);
				}
				assertTrue(s2.containsAll(s1));
			}
		}

		abstract void runImpl(final Future<Void> result);

		private boolean testGraph(final HashSet<Edge> edges)
		{
			if(graph.edges.size()!=edges.size())
			{
				return false;
			}
			final ArrayList<Edge> list1=new ArrayList<>(graph.edges);
			final ArrayList<Edge> list2=new ArrayList<>(edges);
			final HashSet<Long> nodes1=new HashSet<>(list1.size());
			final HashSet<Long> nodes2=new HashSet<>(list2.size());
			for(int i=0;i<list1.size();++i)
			{
				if(!list1.get(i).n1.equals(list2.get(i).n1))
				{
					return false;
				}
				nodes1.add(list1.get(i).n2);
				nodes2.add(list2.get(i).n2);
			}
			return nodes1.equals(nodes2);
		}

		class Edge
		{
			final Long n1;
			final Long n2;

			Edge(final Long n1,final Long n2)
			{
				this.n1=n1;
				this.n2=n2;
			}
		}

		class Graph implements GraphAccessor
		{
			final HashSet<Edge> edges=new HashSet<>();
			boolean isClosed=false;

			@Override
			public void addEdges(final Long from,final HashSet<Long> to)
			{
				isClosed=false;
				for(Long e : to)
				{
					edges.add(new Edge(from,e));
				}
			}

			@Override
			public void removeEdges(final Long from,final HashSet<Long> to) throws IllegalStateException
			{
				isClosed=false;
				for(final Long e : to)
				{
					boolean removed=false;
					for(final Edge edge : edges)
					{
						if(edge.n1.equals(from)&&edge.n2.equals(e))
						{
							edges.remove(edge);
							removed=true;
							break;
						}
					}
					if(!removed)
					{
						throw new IllegalStateException();
					}
				}
			}

			@Override
			public void close()
			{
				isClosed=true;
			}
		}
	}
}