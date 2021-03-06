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

package me.snowdrop.data.hibernatesearch.ops;

import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.snowdrop.data.hibernatesearch.AbstractEntity;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.Store;
import org.springframework.data.annotation.Id;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Indexed
@Entity
@Table(name = "simple")
public class SimpleEntity implements AbstractEntity {
  @Id
  @DocumentId
  @javax.persistence.Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Field(store = Store.NO)
  @SortableField(forField = "identity.name")
  @Field(name = "identity.name")
  @Field(name = "bridge", bridge = @FieldBridge(impl = MyCustomFieldBridge.class))
  private String name;
  @Field(store = Store.NO)
  private String text;
  @Field(store = Store.NO)
  @Field(name = "var")
  private int number;
  @Field(name = "boool", store = Store.NO)
  private boolean buul;
  @Field(store = Store.NO)
  @SortableField
  private String hero;
  @Field(store = Store.NO)
  @SortableField
  private String color;
  @Spatial
  @Embedded
  private Location location;
  @IndexedEmbedded
  @Embedded
  private Address address;
  @IndexedEmbedded(prefix = "containedList.somePrefix_")
  @OneToMany
  private List<ContainedEntity> contained;
}
