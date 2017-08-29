package org.jboss.data.hibernatesearch.smoke;

import java.util.List;

import org.hibernate.search.spi.SearchIntegrator;
import org.jboss.data.hibernatesearch.DatasourceMapperForTest;
import org.jboss.data.hibernatesearch.TestUtils;
import org.jboss.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class SmokeTest {

  @Configuration
  @EnableHibernateSearchRepositories
  public static class Config {
    @Bean(destroyMethod = "close")
    public SearchIntegrator searchIntegrator() {
      return TestUtils.createSearchIntegrator(SmokeEntity.class);
    }

    @Bean
    public DatasourceMapperForTest datasourceMapper() {
      return TestUtils.createDatasourceMapper();
    }
  }

  @Autowired
  SmokeRepository repository;

  @Autowired
  SearchIntegrator searchIntegrator;

  @Autowired
  DatasourceMapperForTest datasourceMapper;

  @Before
  public void setUp() {
    SmokeEntity[] entities = new SmokeEntity[4];

    SmokeEntity entity = new SmokeEntity();
    entity.setId("1");
    entity.setName("a");
    entity.setType("foo");
    entities[0] = entity;

    entity = new SmokeEntity();
    entity.setId("2");
    entity.setName("b");
    entity.setType("bar");
    entities[1] = entity;

    entity = new SmokeEntity();
    entity.setId("3");
    entity.setName("c");
    entity.setType("foo");
    entities[2] = entity;

    entity = new SmokeEntity();
    entity.setId("4");
    entity.setName("d");
    entity.setType("baz");
    entities[3] = entity;

    TestUtils.preindexEntities(searchIntegrator, datasourceMapper, entities);
  }

  @Test
  public void testSmokeRepositry() {
    Assert.assertNotNull(repository);

//    Assert.assertEquals(4L, repository.count());
//
//    Assert.assertEquals(2, repository.findByType("foo").size());
//
//    SmokeEntity byName = repository.findByName("b");
//    Assert.assertNotNull(byName);
//    Assert.assertEquals("2", byName.getId());
//
//    SmokeEntity byNameAndType = repository.findByNameAndType("c", "foo");
//    Assert.assertNotNull(byNameAndType);
//    Assert.assertEquals("3", byNameAndType.getId());

    List<SmokeEntity> byTypeQuery = repository.findByTypeQuery("foo");
    Assert.assertEquals(2, byTypeQuery.size());

    Pageable pageable = new PageRequest(1, 2, new Sort(new Sort.Order("type")));
    Page<SmokeEntity> pageables = repository.findAll(pageable);
    Assert.assertEquals(2, pageables.getTotalElements());

    List<SmokeEntity> byNameOrType = repository.findByNameOrType("a", "bar");
    //Assert.assertEquals(2, byNameOrType.size());
  }
}
