/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.taglibs.authz;

import junit.framework.TestCase;


import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;


/**
 *
 * @author Francois Beausoleil
 */
public class AuthorizeTagCustomGrantedAuthorityTests extends TestCase {
    //~ Instance fields ================================================================================================

    private final LegacyAuthorizeTag authorizeTag = new LegacyAuthorizeTag();
    private TestingAuthenticationToken currentUser;

    //~ Methods ========================================================================================================

    protected void setUp() throws Exception {
        super.setUp();

        currentUser = new TestingAuthenticationToken("abc", "123",
                new GrantedAuthority[] {new CustomGrantedAuthority("ROLE_TELLER")});

        SecurityContextHolder.getContext().setAuthentication(currentUser);
    }

    protected void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    public void testAllowsRequestWhenCustomAuthorityPresentsCorrectRole()
        throws JspException {
        authorizeTag.setIfAnyGranted("ROLE_TELLER");
        assertEquals("authorized - ROLE_TELLER in both sets", Tag.EVAL_BODY_INCLUDE, authorizeTag.doStartTag());
    }

    public void testRejectsRequestWhenCustomAuthorityReturnsNull()
        throws JspException {
        authorizeTag.setIfAnyGranted("ROLE_TELLER");
        SecurityContextHolder.getContext()
                             .setAuthentication(new TestingAuthenticationToken("abc", "123",
                new GrantedAuthority[] {new CustomGrantedAuthority(null)}));

        try {
            authorizeTag.doStartTag();
            fail("Failed to reject GrantedAuthority with NULL getAuthority()");
        } catch (IllegalArgumentException expected) {
            assertTrue("expected", true);
        }
    }

    //~ Inner Classes ==================================================================================================

    private static class CustomGrantedAuthority implements GrantedAuthority {
        private final String authority;

        public CustomGrantedAuthority(String authority) {
            this.authority = authority;
        }

        public String getAuthority() {
            return authority;
        }
    }
}
