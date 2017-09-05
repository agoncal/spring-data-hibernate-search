package org.jboss.data.hibernatesearch.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;
import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.core.mapping.HibernateSearchPersistentProperty;
import org.jboss.data.hibernatesearch.core.mapping.SimpleHibernateSearchMappingContext;
import org.jboss.data.hibernatesearch.core.query.CriteriaQuery;
import org.jboss.data.hibernatesearch.core.query.QueryConverter;
import org.jboss.data.hibernatesearch.core.query.StringQuery;
import org.jboss.data.hibernatesearch.spi.DatasourceMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mapping.context.MappingContext;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HibernateSearchTemplate implements HibernateSearchOperations {
  private final SearchIntegrator searchIntegrator;
  private final DatasourceMapper datasourceMapper;
  private MappingContext<?, HibernateSearchPersistentProperty> mappingContext;

  public HibernateSearchTemplate(SearchIntegrator searchIntegrator, DatasourceMapper datasourceMapper) {
    this.searchIntegrator = searchIntegrator;
    this.datasourceMapper = datasourceMapper;
  }

  private <T> List<T> findAllInternal(org.jboss.data.hibernatesearch.core.query.Query query) {
    Class<?> entityClass = query.getEntityClass();
    QueryConverter queryConverter = new QueryConverter(searchIntegrator, entityClass);
    HSQuery hsQuery;
    if (query instanceof CriteriaQuery) {
      CriteriaQuery criteriaQuery = (CriteriaQuery) query;
      hsQuery = queryConverter.convert(criteriaQuery);
    } else if (query instanceof StringQuery) {
      StringQuery stringQuery = (StringQuery) query;
      hsQuery = queryConverter.string(stringQuery);
    } else {
      hsQuery = queryConverter.query(query);
    }
    List<EntityInfo> entityInfos = hsQuery.queryEntityInfos();
    List<T> entities = new ArrayList<>();
    for (EntityInfo ei : entityInfos) {
      //noinspection unchecked
      entities.add(datasourceMapper.get((Class<T>) entityClass, ei.getId()));
    }
    return entities;
  }

  @Override
  public synchronized MappingContext<?, HibernateSearchPersistentProperty> getMappingContext() {
    if (mappingContext == null) {
      mappingContext = new SimpleHibernateSearchMappingContext<>();
    }
    return mappingContext;
  }

  @Override
  public <T> long count(org.jboss.data.hibernatesearch.core.query.Query countQuery) {
    HSQuery hsQuery = new QueryConverter(searchIntegrator, countQuery.getEntityClass()).query(countQuery);
    return hsQuery.queryResultSize();
  }

  @Override
  public <T> T findSingle(org.jboss.data.hibernatesearch.core.query.Query query) {
    List<T> list = findAllInternal(query);
    return (list.isEmpty() ? null : list.get(0));
  }

  @Override
  public <T> Iterable<T> findAll(org.jboss.data.hibernatesearch.core.query.Query allQuery) {
    return findAllInternal(allQuery);
  }

  @Override
  public <T> Page<T> findPageable(org.jboss.data.hibernatesearch.core.query.Query query) {
    return new PageImpl<T>(findAllInternal(query));
  }

  @Override
  public <T> Iterator<T> stream(org.jboss.data.hibernatesearch.core.query.Query query) {
    //noinspection unchecked
    return (Iterator<T>) findAllInternal(query).iterator();
  }
}
