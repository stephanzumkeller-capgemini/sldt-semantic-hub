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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthorizationEvaluatorTest {

   private static final String CLIENT_ID = "catenax-portal";

   private final AuthorizationEvaluator evaluator = new AuthorizationEvaluator( CLIENT_ID );

   @AfterEach
   void clearContext() {
      SecurityContextHolder.clearContext();
   }

   private void setAuthentication( final Authentication authentication ) {
      SecurityContextHolder.getContext().setAuthentication( authentication );
   }

   private JwtAuthenticationToken jwtWithClaims( final Map<String, Object> claims ) {
      final Jwt.Builder builder = Jwt.withTokenValue( "token" ).header( "alg", "none" ).claim( "sub", "user" );
      claims.forEach( builder::claim );
      return new JwtAuthenticationToken( builder.build(), Collections.emptyList() );
   }

   @Test
   void testReturnsFalseWhenAuthenticationIsNotJwt() {
      setAuthentication( new UsernamePasswordAuthenticationToken( "user", "password" ) );

      assertThat( evaluator.hasRoleViewSemanticModel() ).isFalse();
   }

   @Test
   void testReturnsFalseWhenResourceAccessNotAMap() {
      setAuthentication( jwtWithClaims( Map.of( "resource_access", "not-a-map" ) ) );

      assertThat( evaluator.hasRoleAddSemanticModel() ).isFalse();
   }

   @Test
   void testReturnsFalseWhenClientResourceNotAMap() {
      setAuthentication( jwtWithClaims( Map.of( "resource_access", Map.of( CLIENT_ID, "not-a-map" ) ) ) );

      assertThat( evaluator.hasRoleUpdateSemanticModel() ).isFalse();
   }

   @Test
   void testReturnsFalseWhenRolesNotACollection() {
      setAuthentication( jwtWithClaims(
            Map.of( "resource_access", Map.of( CLIENT_ID, Map.of( "roles", "not-a-collection" ) ) ) ) );

      assertThat( evaluator.hasRoleDeleteSemanticModel() ).isFalse();
   }

   @Test
   void testReturnsFalseWhenRoleNotPresent() {
      setAuthentication( jwtWithClaims(
            Map.of( "resource_access", Map.of( CLIENT_ID, Map.of( "roles", List.of( "some_other_role" ) ) ) ) ) );

      assertThat( evaluator.hasRoleViewSemanticModel() ).isFalse();
   }

   @Test
   void testReturnsTrueForEachMatchingRole() {
      setAuthentication( jwtWithClaims( Map.of( "resource_access", Map.of( CLIENT_ID, Map.of( "roles", List.of(
            AuthorizationEvaluator.Roles.ROLE_VIEW_SEMANTIC_MODEL,
            AuthorizationEvaluator.Roles.ROLE_ADD_SEMANTIC_MODEL,
            AuthorizationEvaluator.Roles.ROLE_UPDATE_SEMANTIC_MODEL,
            AuthorizationEvaluator.Roles.ROLE_DELETE_SEMANTIC_MODEL ) ) ) ) ) );

      assertThat( evaluator.hasRoleViewSemanticModel() ).isTrue();
      assertThat( evaluator.hasRoleAddSemanticModel() ).isTrue();
      assertThat( evaluator.hasRoleUpdateSemanticModel() ).isTrue();
      assertThat( evaluator.hasRoleDeleteSemanticModel() ).isTrue();
   }
}
