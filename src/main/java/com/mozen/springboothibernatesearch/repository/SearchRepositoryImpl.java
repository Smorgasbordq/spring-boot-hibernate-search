package com.mozen.springboothibernatesearch.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SearchRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements SearchRepository<T, ID> {

    private final EntityManager entityManager;

    public SearchRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public SearchRepositoryImpl(
            JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

	@Override
    public List<T> searchBy(String text, int limit, String... fields) {
//        SearchResult<T> result = getSearchResult(text, limit, fields);
//        return result.hits();
    	return getSearchResult(text, limit, fields);
    }

    @SuppressWarnings("unchecked")
	private /*SearchResult<T>*/ List<T> getSearchResult(String text, int limit, String[] fields) {
    	Class<T> clazz = getDomainClass();
    	FullTextEntityManager ftem = Search.getFullTextEntityManager(entityManager);
		QueryBuilder queryBuilder = ftem.getSearchFactory().buildQueryBuilder().forEntity(clazz).get();
		Query query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(2).onFields(fields).matching(text).createQuery();
		FullTextQuery jpaQuery = ftem.createFullTextQuery(query, clazz).setMaxResults(limit > 0 ? 0 : 999);
		return jpaQuery.getResultList();
//        SearchSession searchSession = Search.session(entityManager);
//        SearchResult<T> result =
//                searchSession
//                        .search(getDomainClass())
//                        .where(f -> f.match().fields(fields).matching(text).fuzzy(2))
//                        .fetch(limit);
//        return result;
    }
}
