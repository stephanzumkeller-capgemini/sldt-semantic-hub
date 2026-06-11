/********************************************************************************
 * Copyright (c) 2021-2022 Robert Bosch Manufacturing Solutions GmbH
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.semantics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.jena.atlas.web.HttpException;
import org.eclipse.esmf.aspectmodel.urn.UrnSyntaxException;
import org.eclipse.tractusx.semantics.hub.EntityNotFoundException;
import org.eclipse.tractusx.semantics.hub.InvalidStateTransitionException;
import org.eclipse.tractusx.semantics.hub.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import jakarta.servlet.http.HttpServletRequest;

class ApiExceptionHandlerTest {

   private static final String REQUEST_URI = "/api/v1/models";

   private final ApiExceptionHandler handler = new ApiExceptionHandler();

   private HttpServletRequest requestWithUri() {
      final HttpServletRequest request = mock( HttpServletRequest.class );
      when( request.getRequestURI() ).thenReturn( REQUEST_URI );
      return request;
   }

   @Test
   void testHandleIllegalArgumentException() {
      final ResponseEntity<ErrorResponse> response =
            handler.handleIllegalArgumentException( requestWithUri(), new IllegalArgumentException( "bad arg" ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      assertThat( response.getBody().getError().getMessage() ).isEqualTo( "bad arg" );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleMethodArgumentNotSupportedException() {
      final HttpServletRequest request = requestWithUri();
      when( request.getQueryString() ).thenReturn( "pageSize=abc" );

      final ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentNotSupportedException( request );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      assertThat( response.getBody().getError().getMessage() ).contains( "pageSize=abc" );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleInvalidStateTransitionException() {
      final HttpServletRequest request = requestWithUri();

      final ResponseEntity<ErrorResponse> response = handler.handleInvalidStateTransitionException(
            request, new InvalidStateTransitionException( "invalid transition" ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      assertThat( response.getBody().getError().getMessage() ).isEqualTo( "invalid transition" );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleUrnSyntaxException() {
      final HttpServletRequest request = requestWithUri();

      final ResponseEntity<ErrorResponse> response = handler.handleInvalidStateTransitionException(
            request, new UrnSyntaxException( "invalid urn" ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      assertThat( response.getBody().getError().getMessage() ).isEqualTo( "invalid urn" );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleHttpException() {
      final HttpServletRequest request = requestWithUri();

      final ResponseEntity<ErrorResponse> response = handler.handleHttpException(
            request, new HttpException( "triplestore error" ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleNotFoundException() {
      final ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(
            requestWithUri(), new EntityNotFoundException( "not found" ) );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.NOT_FOUND );
      assertThat( response.getBody().getError().getMessage() ).isEqualTo( "not found" );
      assertThat( response.getBody().getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleMethodArgumentNotValidWithMessage() {
      final ServletWebRequest webRequest = mock( ServletWebRequest.class );
      final HttpServletRequest request = requestWithUri();
      when( webRequest.getRequest() ).thenReturn( request );

      final FieldError fieldError = new FieldError( "model", "name", "must not be blank" );
      final MethodArgumentNotValidException ex = mock( MethodArgumentNotValidException.class );
      final BindingResult bindingResult = mock( BindingResult.class );
      when( ex.getBindingResult() ).thenReturn( bindingResult );
      when( bindingResult.getFieldErrors() ).thenReturn( List.of( fieldError ) );

      final ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      final ErrorResponse body = (ErrorResponse) response.getBody();
      assertThat( body.getError().getMessage() ).isEqualTo( "Validation failed." );
      assertThat( body.getError().getDetails() ).containsEntry( "name", "must not be blank" );
      assertThat( body.getError().getPath() ).isEqualTo( REQUEST_URI );
   }

   @Test
   void testHandleMethodArgumentNotValidWithNullMessage() {
      final ServletWebRequest webRequest = mock( ServletWebRequest.class );
      final HttpServletRequest request = requestWithUri();
      when( webRequest.getRequest() ).thenReturn( request );

      final FieldError fieldError = new FieldError( "model", "name", null );
      final MethodArgumentNotValidException ex = mock( MethodArgumentNotValidException.class );
      final BindingResult bindingResult = mock( BindingResult.class );
      when( ex.getBindingResult() ).thenReturn( bindingResult );
      when( bindingResult.getFieldErrors() ).thenReturn( List.of( fieldError ) );

      final ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
            ex, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest );

      assertThat( response.getStatusCode() ).isEqualTo( HttpStatus.BAD_REQUEST );
      final ErrorResponse body = (ErrorResponse) response.getBody();
      assertThat( body.getError().getDetails() ).containsEntry( "name", "null" );
   }
}
