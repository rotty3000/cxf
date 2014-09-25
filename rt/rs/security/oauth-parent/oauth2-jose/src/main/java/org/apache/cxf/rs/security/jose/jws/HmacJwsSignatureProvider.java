/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.rs.security.jose.jws;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.Mac;

import org.apache.cxf.common.util.Base64Exception;
import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rs.security.jose.jwa.Algorithm;
import org.apache.cxf.rs.security.oauth2.utils.crypto.HmacUtils;

public class HmacJwsSignatureProvider extends AbstractJwsSignatureProvider {
    private static final Set<String> SUPPORTED_ALGORITHMS = new HashSet<String>(
        Arrays.asList(Algorithm.HmacSHA256.getJwtName(),
                      Algorithm.HmacSHA384.getJwtName(),
                      Algorithm.HmacSHA512.getJwtName())); 
    private byte[] key;
    private AlgorithmParameterSpec hmacSpec;
    
    public HmacJwsSignatureProvider(byte[] key, String algo) {
        this(key, null, algo);
    }
    public HmacJwsSignatureProvider(byte[] key, AlgorithmParameterSpec spec, String algo) {
        super(SUPPORTED_ALGORITHMS, algo);
        this.key = key;
        this.hmacSpec = spec;
    }
    public HmacJwsSignatureProvider(String encodedKey, String algo) {
        super(SUPPORTED_ALGORITHMS, algo);
        try {
            this.key = Base64UrlUtility.decode(encodedKey);
        } catch (Base64Exception ex) {
            throw new SecurityException();
        }
    }
    
    protected JwsSignature doCreateJwsSignature(JwsHeaders headers) {
        final Mac mac = HmacUtils.getInitializedMac(key, Algorithm.toJavaName(headers.getAlgorithm()),
                                                    hmacSpec);
        return new JwsSignature() {

            @Override
            public void update(byte[] src, int off, int len) {
                mac.update(src, off, len);
            }

            @Override
            public byte[] sign() {
                return mac.doFinal();
            }
            
        };
    }

}
