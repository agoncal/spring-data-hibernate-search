/*
 * Copyright 2017 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.snowdrop.data.hibernatesearch.config;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import me.snowdrop.data.hibernatesearch.spi.DatasourceMapper;
import me.snowdrop.data.hibernatesearch.spi.QueryAdapter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.spi.SearchIntegrator;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.Assert;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JpaDatasourceMapper implements DatasourceMapper {

  private EntityManagerFactory emf;

  public JpaDatasourceMapper(EntityManagerFactory emf) {
    Assert.notNull(emf, "Null EntityManagerFactory!");
    this.emf = emf;
  }

  @Override
  public <T> QueryAdapter createQueryAdapter(Class<T> entityClass) {
    return new OrmQueryAdapter<>(entityClass);
  }

  private class OrmQueryAdapter<T> implements QueryAdapter<T> {
    private final Class<T> entityClass;
    private FullTextQuery fullTextQuery;
    private EntityManager entityManager;

    public OrmQueryAdapter(Class<T> entityClass) {
      this.entityClass = entityClass;
    }

    private void close() {
      if (entityManager != null) {
        entityManager.close();
      }
    }

    @Override
    public void applyLuceneQuery(SearchIntegrator searchIntegrator, Query query) {
      EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
      if (em == null) {
        entityManager = emf.createEntityManager();
        em = entityManager;
      }
      FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
      fullTextQuery = fullTextEntityManager.createFullTextQuery(query, entityClass);
    }

    @Override
    public long size() {
      try {
        return fullTextQuery.getResultSize();
      } finally {
        close();
      }
    }

    @Override
    public List<T> list() {
      try {
        //noinspection unchecked
        return fullTextQuery.getResultList();
      } finally {
        close();
      }
    }

    @Override
    public void setSort(Sort sort) {
      fullTextQuery.setSort(sort);
    }

    @Override
    public void setFirstResult(int firstResult) {
      fullTextQuery.setFirstResult(firstResult);
    }

    @Override
    public void setMaxResults(int maxResults) {
      fullTextQuery.setMaxResults(maxResults);
    }
  }
}
