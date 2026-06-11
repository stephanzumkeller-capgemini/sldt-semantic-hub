/********************************************************************************
 * Copyright (c) 2021-2023 Robert Bosch Manufacturing Solutions GmbH
 * Copyright (c) 2021-2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.semantics.hub.persistence.triplestore;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.jena.query.Query;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.tractusx.semantics.hub.domain.ModelPackageStatus;
import org.junit.jupiter.api.Test;

class SparqlQueriesTest {

   private static final String URN = "urn:samm:org.eclipse.tractusx.test:1.0.0#Test";

   @Test
   void testFindAllQueryWithNamespaceAndStatusFilter() {
      final Query query = SparqlQueries.buildFindAllQuery( "urn:samm:org.eclipse", ModelPackageStatus.RELEASED, 0, 10 );

      final String queryString = query.toString();
      assertThat( queryString ).contains( "urn:samm:org.eclipse" );
      assertThat( queryString ).contains( "RELEASED" );
      assertThat( queryString ).doesNotContain( "?namespaceFilter " );
   }

   @Test
   void testFindAllQueryWithoutNamespaceAndStatusFilter() {
      final Query query = SparqlQueries.buildFindAllQuery( null, null, 0, 10 );

      final String queryString = query.toString();
      // Unset parameters remain as unbound variables in the query.
      assertThat( queryString ).contains( "?namespaceFilter" );
      assertThat( queryString ).contains( "?statusFilter" );
   }

   @Test
   void testFindAllQueryWithBlankNamespaceFilterIsTreatedAsAbsent() {
      final Query query = SparqlQueries.buildFindAllQuery( "  ", ModelPackageStatus.DRAFT, 0, 10 );

      final String queryString = query.toString();
      assertThat( queryString ).contains( "?namespaceFilter" );
      assertThat( queryString ).contains( "DRAFT" );
   }

   @Test
   void testFindAllQueryPaginationFirstPage() {
      final Query query = SparqlQueries.buildFindAllQuery( null, null, 0, 10 );

      assertThat( query.getOffset() ).isEqualTo( 0 );
      assertThat( query.getLimit() ).isEqualTo( 10 );
   }

   @Test
   void testFindAllQueryPaginationLaterPage() {
      final Query query = SparqlQueries.buildFindAllQuery( null, null, 2, 10 );

      // getOffset: (page - 1) * pageSize for page >= 2
      assertThat( query.getOffset() ).isEqualTo( 10 );
      assertThat( query.getLimit() ).isEqualTo( 10 );
   }

   @Test
   void testFindByUrnQueryContainsUrn() {
      final Query query = SparqlQueries.buildFindByUrnQuery( AspectModelUrn.fromUrn( URN ) );

      assertThat( query.toString() ).contains( URN );
   }

   @Test
   void testCountAspectModelsQueryWithStatusFilter() {
      final Query query = SparqlQueries.buildCountAspectModelsQuery( "urn:samm", ModelPackageStatus.STANDARDIZED );

      final String queryString = query.toString();
      assertThat( queryString ).contains( "urn:samm" );
      assertThat( queryString ).contains( "STANDARDIZED" );
      assertThat( queryString ).containsIgnoringCase( "count" );
   }

   @Test
   void testEchoQuery() {
      assertThat( SparqlQueries.echoQuery().isAskType() ).isTrue();
   }
}
