/********************************************************************************
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.semantics.hub.samm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.tractusx.semantics.hub.persistence.PersistenceLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vavr.control.Try;

class SDKAccessHelperSAMMTest {

   private static final String URN = "urn:samm:org.eclipse.tractusx.test:1.0.0#Test";
   private static final String INVALID_MODEL = "this is not a valid turtle aspect model";

   private PersistenceLayer persistenceLayer;
   private SDKAccessHelperSAMM helper;

   @BeforeEach
   void setUp() {
      persistenceLayer = mock( PersistenceLayer.class );
      helper = new SDKAccessHelperSAMM();
      helper.setPersistenceLayer( persistenceLayer );
   }

   @Test
   void testLoadAspectModelWithInvalidModelReturnsFailure() {
      final Try<AspectModel> result = helper.loadAspectModel( INVALID_MODEL );

      assertThat( result.isFailure() ).isTrue();
   }

   @Test
   void testGetHtmlDocumentWithInvalidModelReturnsFailure() {
      when( persistenceLayer.getModelDefinition( any() ) ).thenReturn( INVALID_MODEL );

      final Try<byte[]> result = helper.getHtmlDocument( URN );

      assertThat( result.isFailure() ).isTrue();
   }

   @Test
   void testGetExamplePayloadJsonWithInvalidModelReturnsFailure() {
      when( persistenceLayer.getModelDefinition( any() ) ).thenReturn( INVALID_MODEL );

      final Try<String> result = helper.getExamplePayloadJson( URN );

      assertThat( result.isFailure() ).isTrue();
   }

   @Test
   void testGenerateDiagramWithInvalidModelReturnsFailure() {
      when( persistenceLayer.getModelDefinition( any() ) ).thenReturn( INVALID_MODEL );

      final Try<byte[]> result =
            helper.generateDiagram( URN, org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig.Format.SVG );

      assertThat( result.isFailure() ).isTrue();
   }
}
