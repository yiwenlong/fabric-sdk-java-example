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

public class ChannelTest {

    @Test
    public void testCreateChannel() {
        TestResult r = new ChannelTestCase("createChannel").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testJoinChannelPeer0() {
        TestResult r = new ChannelTestCase("joinChannelPeer0").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testJoinChannelPeer1() {
        TestResult r = new ChannelTestCase("joinChannelPeer1").run();
        Assert.assertTrue(r.wasSuccessful());
    }

    @Test
    public void testViewChannel() {
        TestResult r = new ChannelTestCase("viewChannelInformation").run();
        Assert.assertTrue(r.wasSuccessful());
    }
}
