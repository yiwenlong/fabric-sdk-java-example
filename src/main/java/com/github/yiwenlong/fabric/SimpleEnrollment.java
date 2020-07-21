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

import com.github.yiwenlong.fabric.utils.FileUtils;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.IOException;
import java.io.Serializable;
import java.security.PrivateKey;

public class SimpleEnrollment implements Enrollment, Serializable {

    private final PrivateKey privKey;
    private final String cert;

    private SimpleEnrollment(String cert, PrivateKey key) {
        this.privKey = key;
        this.cert = cert;
    }

    public PrivateKey getKey() {
        return privKey;
    }

    public String getCert() {
        return cert;
    }

    public static Enrollment createInstance(String certFile, String keyFile) throws IOException {
        return new SimpleEnrollment(FileUtils.readCert(certFile), FileUtils.readPrivateKey(keyFile));
    }
}
