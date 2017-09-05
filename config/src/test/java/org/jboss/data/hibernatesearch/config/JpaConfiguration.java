package org.jboss.data.hibernatesearch.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Configuration
@EnableHibernateSearchRepositories
public class JpaConfiguration {
  @PersistenceContext
  EntityManager entityManager;

  @Bean
  public BuildSearchIndex buildSearchIndex() {
    return new BuildSearchIndex();
  }

  public class BuildSearchIndex implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
      try {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
      } catch (InterruptedException e) {
        System.out.println("An error occurred trying to build the search index: " + e.toString());
      }
    }
  }

}
