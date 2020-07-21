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
package com.github.yiwenlong.fabric;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class SimpleFabricUser implements User, Serializable {

    private final String name;
    private final String mspId;
    private final Enrollment enrollment;
    private SimpleFabricUser(String name, String mspId, Enrollment enrollment) {
        this.enrollment = enrollment;
        this.name = name;
        this.mspId = mspId;
    }

    public String getName() {
        return name;
    }

    public Set<String> getRoles() {
        return null;
    }

    public String getAccount() {
        return null;
    }

    public String getAffiliation() {
        return null;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public String getMspId() {
        return mspId;
    }

    public static User createInstance(String userName, String orgMspId, String certFile, String keyFile) {
        try {
            Enrollment enrollment = SimpleEnrollment.createInstance(certFile, keyFile);
            return new SimpleFabricUser(userName, orgMspId, enrollment);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
