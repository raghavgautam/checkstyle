////////////////////////////////////////////////////////////////////////////////
// Test case file for checkstyle.
// Created: 2017
////////////////////////////////////////////////////////////////////////////////
package com.puppycrawl.tools.checkstyle.checks.misc.banishedmethods;

import org.junit.Assert;

/**
 * Test case for detecting usage of banished methods & constructors.
 * @author Raghav Kumar Gautam
 **/
class InputBanishedMethods
{
    /**
     * no param constructor
     */
    InputBanishedMethods() {
        System.exit(1);
        BannedConstructor bannedConstructor = new BannedConstructor("oneArgument");
    }

    /**
     * non final param method
     */
    void method(String s) {
        Assert.assertTrue(1 != 2);
        Assert.assertTrue("Good assert with some reason.", true);
    }

}