////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

import static com.puppycrawl.tools.checkstyle.checks.BanishedMethodsCheck.MSG_KEY;

public class BanishedMethodsCheckTest extends AbstractModuleTestSupport {
    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/misc/banishedmethods";
    }

    @Test
    public void testMethodCallToken() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(BanishedMethodsCheck.class);
        checkConfig.addAttribute("file", "file://" + getPath("banished_methods.xml"));
        final String[] expected = {
            "19:20: " + getCheckMessage(MSG_KEY, "exit", 1),
            "20:47: " + getCheckMessage(MSG_KEY, "BannedConstructor", 1),
            "27:26: " + getCheckMessage(MSG_KEY, "assertTrue", 1),
        };
        verify(checkConfig, getPath("InputBanishedMethods.java"), expected);
    }
}
