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

import org.eclipse.tractusx.semantics.hub.persistence.PersistenceLayer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

class TriplestoreLivenessProbeTest {

   @Test
   void testHealthUpWhenEchoReturnsTrue() {
      final PersistenceLayer pl = mock( PersistenceLayer.class );
      when( pl.echo() ).thenReturn( true );

      final Health health = new TriplestoreLivenessProbe( pl ).health();

      assertThat( health.getStatus() ).isEqualTo( Status.UP );
   }

   @Test
   void testHealthDownWhenEchoReturnsFalse() {
      final PersistenceLayer pl = mock( PersistenceLayer.class );
      when( pl.echo() ).thenReturn( false );

      final Health health = new TriplestoreLivenessProbe( pl ).health();

      assertThat( health.getStatus() ).isEqualTo( Status.DOWN );
   }

   @Test
   void testHealthDownWhenEchoThrows() {
      final PersistenceLayer pl = mock( PersistenceLayer.class );
      when( pl.echo() ).thenThrow( new RuntimeException( "triplestore unreachable" ) );

      final Health health = new TriplestoreLivenessProbe( pl ).health();

      assertThat( health.getStatus() ).isEqualTo( Status.DOWN );
   }
}
