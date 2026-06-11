/********************************************************************************
 * Copyright (c) 2021-2025 Robert Bosch Manufacturing Solutions GmbH
 * Copyright (c) 2021-2025 Contributors to the Eclipse Foundation
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.tractusx.semantics.hub.model.SemanticModelType;
import org.eclipse.tractusx.semantics.hub.persistence.triplestore.SAMMSdk.TripleStoreResolutionStrategy;
import org.junit.jupiter.api.Test;

class SAMMSdkTest {

   private static final String SAMM_URN = "urn:samm:org.eclipse.tractusx.test:1.0.0#Test";

   @Test
   void testApplyThrowsWhenModelIsNull() {
      final Function<String, Model> requester = urn -> null;
      final TripleStoreResolutionStrategy strategy =
            new TripleStoreResolutionStrategy( requester, SemanticModelType.SAMM );

      assertThatThrownBy( () -> strategy.apply( AspectModelUrn.fromUrn( SAMM_URN ), null ) )
            .isInstanceOf( ResourceDefinitionNotFoundException.class );
   }

   @Test
   void testApplyThrowsWhenResourceMissingFromModel() {
      final Function<String, Model> requester = urn -> ModelFactory.createDefaultModel();
      final TripleStoreResolutionStrategy strategy =
            new TripleStoreResolutionStrategy( requester, SemanticModelType.SAMM );

      assertThatThrownBy( () -> strategy.apply( AspectModelUrn.fromUrn( SAMM_URN ), null ) )
            .isInstanceOf( ResourceDefinitionNotFoundException.class );
   }

   @Test
   void testApplyReplacesSammWithBammPrefixForBammType() {
      final AtomicReference<String> requestedUrn = new AtomicReference<>();
      final Function<String, Model> requester = urn -> {
         requestedUrn.set( urn );
         return null;
      };
      final TripleStoreResolutionStrategy strategy =
            new TripleStoreResolutionStrategy( requester, SemanticModelType.BAMM );

      assertThatThrownBy( () -> strategy.apply( AspectModelUrn.fromUrn( SAMM_URN ), null ) )
            .isInstanceOf( ResourceDefinitionNotFoundException.class );
      assertThat( requestedUrn.get() ).startsWith( "urn:bamm:" );
   }

   @Test
   void testApplyKeepsSammPrefixForSammType() {
      final AtomicReference<String> requestedUrn = new AtomicReference<>();
      final Function<String, Model> requester = urn -> {
         requestedUrn.set( urn );
         return null;
      };
      final TripleStoreResolutionStrategy strategy =
            new TripleStoreResolutionStrategy( requester, SemanticModelType.SAMM );

      assertThatThrownBy( () -> strategy.apply( AspectModelUrn.fromUrn( SAMM_URN ), null ) )
            .isInstanceOf( ResourceDefinitionNotFoundException.class );
      assertThat( requestedUrn.get() ).startsWith( "urn:samm:" );
   }
}
