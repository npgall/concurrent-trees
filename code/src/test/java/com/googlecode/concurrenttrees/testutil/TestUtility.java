/**
 * Copyright 2012-2013 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.testutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper methods used by unit tests only.
 *
 * @author niall.gallagher
 */
public class TestUtility {

    public static byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(object);
            out.flush();
            return baos.toByteArray();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(Class<? super T> expectedType, byte[] data) {
        try {
            ObjectInputStream out = new ObjectInputStream(new ByteArrayInputStream(data));
            Object o = out.readObject();
            out.close();
            if (!(expectedType.isAssignableFrom(o.getClass()))) {
                throw new IllegalStateException("Unexpected type: " + o.getClass());
            }
            return (T) o;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Constructor, not used.
     */
    TestUtility() {
    }
}
