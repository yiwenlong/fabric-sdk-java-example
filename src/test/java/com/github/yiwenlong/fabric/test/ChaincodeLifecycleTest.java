//
// Copyright 2020 Yiwenlong(wlong.yi#gmail.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.github.yiwenlong.fabric.test;

import junit.framework.TestResult;
import org.junit.Assert;
import org.junit.Test;

public class ChaincodeLifecycleTest {

    @Test
    public void testInstallChaincode() {
        TestResult r = new ChaincodeLifecycleTestCase("installChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testQueryInstalledChaincode() {
        TestResult r = new ChaincodeLifecycleTestCase("queryInstalledChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testApproveChaincode() {
        TestResult r = new ChaincodeLifecycleTestCase("approveChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testQueyApproveChaincode() {
        TestResult r = new ChaincodeLifecycleTestCase("queryApproveChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testCommitChaincode() {
        TestResult r = new ChaincodeLifecycleTestCase("commitChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testChaincodeDefinition() {
        TestResult r = new ChaincodeLifecycleTestCase("queryCommittedChaincode").run();
        Assert.assertTrue(r.wasSuccessful());
    }
}
