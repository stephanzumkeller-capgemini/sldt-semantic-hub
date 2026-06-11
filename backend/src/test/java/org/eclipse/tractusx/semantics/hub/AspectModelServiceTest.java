/********************************************************************************
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

package org.eclipse.tractusx.semantics.hub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.tractusx.semantics.hub.domain.ModelPackageStatus;
import org.eclipse.tractusx.semantics.hub.model.AasFormat;
import org.eclipse.tractusx.semantics.hub.model.SemanticModel;
import org.eclipse.tractusx.semantics.hub.model.SemanticModelList;
import org.eclipse.tractusx.semantics.hub.model.SemanticModelStatus;
import org.eclipse.tractusx.semantics.hub.persistence.PersistenceLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.vavr.control.Try;

class AspectModelServiceTest {

   private static final String URN = "urn:samm:org.eclipse.tractusx.test:1.0.0#Test";

   private PersistenceLayer persistenceLayer;
   private SDKAccessHelper sdkHelper;
   private AspectModelService service;

   @BeforeEach
   void setUp() {
      persistenceLayer = mock( PersistenceLayer.class );
      sdkHelper = mock( SDKAccessHelper.class );
      service = new AspectModelService( persistenceLayer, sdkHelper );
   }

   @Test
   void testGetModelByUrnReturnsNotFoundWhenNull() {
      when( persistenceLayer.getModel( any() ) ).thenReturn( null );

      final ResponseEntity<SemanticModel> response = service.getModelByUrn( URN );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
   }

   @Test
   void testGetModelByUrnReturnsOkWhenPresent() {
      final SemanticModel model = mock( SemanticModel.class );
      when( persistenceLayer.getModel( any() ) ).thenReturn( model );

      final ResponseEntity<SemanticModel> response = service.getModelByUrn( URN );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getBody() ).isSameAs( model );
   }

   @Test
   void testGetModelListByUrnsReturnsNotFoundWhenNull() {
      when( persistenceLayer.findModelListByUrns( anyList(), anyInt(), anyInt() ) ).thenReturn( null );

      final ResponseEntity<SemanticModelList> response = service.getModelListByUrns( 10, 0, List.of( URN ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
   }

   @Test
   void testGetModelListByUrnsReturnsOkWhenPresent() {
      final SemanticModelList list = mock( SemanticModelList.class );
      when( persistenceLayer.findModelListByUrns( anyList(), anyInt(), anyInt() ) ).thenReturn( list );

      final ResponseEntity<SemanticModelList> response = service.getModelListByUrns( 10, 0, List.of( URN ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getBody() ).isSameAs( list );
   }

   @Test
   void testGetModelListMapsStatusFilter() {
      final SemanticModelList list = mock( SemanticModelList.class );
      when( persistenceLayer.getModels( eq( "urn:samm" ), eq( ModelPackageStatus.RELEASED ), eq( 0 ), eq( 10 ) ) )
            .thenReturn( list );

      final ResponseEntity<SemanticModelList> response =
            service.getModelList( 10, 0, "urn:samm", SemanticModelStatus.RELEASED );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getBody() ).isSameAs( list );
   }

   @Test
   void testGetModelListWithoutStatusFilter() {
      final SemanticModelList list = mock( SemanticModelList.class );
      when( persistenceLayer.getModels( eq( "urn:samm" ), eq( null ), eq( 0 ), eq( 10 ) ) ).thenReturn( list );

      final ResponseEntity<SemanticModelList> response =
            service.getModelList( 10, 0, "urn:samm", null );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getBody() ).isSameAs( list );
   }

   @Test
   void testGetModelDiagramFailureThrows() {
      when( sdkHelper.generateSvg( URN ) ).thenReturn( Try.failure( new RuntimeException( "boom" ) ) );

      assertThatThrownBy( () -> service.getModelDiagram( URN ) )
            .isInstanceOf( RuntimeException.class )
            .hasMessageContaining( URN );
   }

   @Test
   void testGetModelDocuFailureThrows() {
      when( sdkHelper.getHtmlDocu( URN ) ).thenReturn( Try.failure( new RuntimeException( "boom" ) ) );

      assertThatThrownBy( () -> service.getModelDocu( URN ) )
            .isInstanceOf( RuntimeException.class )
            .hasMessageContaining( URN );
   }

   @Test
   void testGetModelExamplePayloadJsonFailureThrows() {
      when( sdkHelper.getExamplePayloadJson( URN ) ).thenReturn( Try.failure( new RuntimeException( "boom" ) ) );

      assertThatThrownBy( () -> service.getModelExamplePayloadJson( URN ) )
            .isInstanceOf( RuntimeException.class )
            .hasMessageContaining( URN );
   }

   @Test
   void testGetModelDocuSuccessSetsHtmlContentType() {
      when( sdkHelper.getHtmlDocu( URN ) ).thenReturn( Try.success( "<html/>".getBytes() ) );

      final ResponseEntity<?> response = service.getModelDocu( URN );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getHeaders().getContentType() ).isEqualTo( MediaType.TEXT_HTML );
   }

   @Test
   void testGetAasSubmodelTemplateFailureThrows() {
      when( sdkHelper.getAasSubmodelTemplate( eq( URN ), any() ) ).thenReturn( Try.failure( new RuntimeException( "boom" ) ) );

      assertThatThrownBy( () -> service.getAasSubmodelTemplate( URN, AasFormat.JSON ) )
            .isInstanceOf( RuntimeException.class )
            .hasMessageContaining( URN );
   }

   @Test
   void testGetAasSubmodelTemplateFileFormat() {
      when( sdkHelper.getAasSubmodelTemplate( URN, AasFileFormat.AASX ) ).thenReturn( Try.success( new byte[] { 1 } ) );

      final ResponseEntity<?> response = service.getAasSubmodelTemplate( URN, AasFormat.FILE );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getHeaders().getContentType() ).isEqualTo( MediaType.APPLICATION_OCTET_STREAM );
   }

   @Test
   void testGetAasSubmodelTemplateXmlFormat() {
      when( sdkHelper.getAasSubmodelTemplate( URN, AasFileFormat.XML ) ).thenReturn( Try.success( new byte[] { 1 } ) );

      final ResponseEntity<?> response = service.getAasSubmodelTemplate( URN, AasFormat.XML );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getHeaders().getContentType() ).isEqualTo( MediaType.APPLICATION_XML );
   }

   @Test
   void testGetAasSubmodelTemplateJsonFormat() {
      when( sdkHelper.getAasSubmodelTemplate( URN, AasFileFormat.JSON ) ).thenReturn( Try.success( new byte[] { 1 } ) );

      final ResponseEntity<?> response = service.getAasSubmodelTemplate( URN, AasFormat.JSON );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.OK );
      assertThat( response.getHeaders().getContentType() ).isEqualTo( MediaType.APPLICATION_JSON );
   }
}
