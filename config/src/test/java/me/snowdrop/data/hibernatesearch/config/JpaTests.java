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

import me.snowdrop.data.hibernatesearch.TestUtils;
import me.snowdrop.data.hibernatesearch.config.hibernatesearch.FruitHibernateSearchRepository;
import me.snowdrop.data.hibernatesearch.config.jpa.FruitRepository;
import me.snowdrop.data.hibernatesearch.repository.config.EnableHibernateSearchRepositories;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SpringBootTest(classes = JpaConfiguration.class, properties = "debug=false")
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@EnableHibernateSearchRepositories(basePackageClasses = FruitHibernateSearchRepository.class)
@EnableJpaRepositories(basePackageClasses = FruitRepository.class)
public class JpaTests {
  @Autowired
  FruitHibernateSearchRepository hsRepository;

  @Autowired
  FruitRepository jpaRepository;

  @Test
  public void testDefault() {
    Assert.assertNotNull(hsRepository);
    Assert.assertNotNull(jpaRepository);

    Assert.assertEquals(3, TestUtils.size(hsRepository.findAll()));
    Assert.assertEquals(3, TestUtils.size(jpaRepository.findAll()));

    Fruit apple = hsRepository.findByName("Apple");
    Assert.assertNotNull(apple);
    Assert.assertEquals("Apple", apple.getName());
  }
}
