package com.mozen.springboothibernatesearch.index;

import javax.persistence.EntityManager;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class Indexer {

    private EntityManager entityManager;

    private static final int THREAD_NUMBER = 4;

    public Indexer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void indexPersistedData(String indexClassName) throws IndexException {

        try {
//            SearchSession searchSession = Search.session(entityManager);
//            Class<?> classToIndex = Class.forName(indexClassName);
//            MassIndexer indexer =
//                    searchSession
//                            .massIndexer(classToIndex)
//                            .threadsToLoadObjects(THREAD_NUMBER);
//            indexer.startAndWait();
       	FullTextEntityManager ftem = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);
       	Class<?> classToIndex = Class.forName(indexClassName);
       	org.hibernate.search.MassIndexer mi = ftem.createIndexer(classToIndex).threadsToLoadObjects(THREAD_NUMBER).progressMonitor(new MassIndexerProgressMonitor() {
			@Override
			public void documentsAdded(long increment) {}
			@Override
			public void documentsBuilt(int increment) {}
			@Override
			public void entitiesLoaded(int increment) {}
			@Override
			public void addToTotalCount(long increment) {}
			@Override
			public void indexingCompleted() {}
       	});
       	mi.startAndWait();
        } catch (ClassNotFoundException e) {
            throw new IndexException("Invalid class " + indexClassName, e);
        } catch (InterruptedException e) {
            throw new IndexException("Index Interrupted", e);
        }
    }
}
