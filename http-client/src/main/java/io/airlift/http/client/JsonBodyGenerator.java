/*
 * Copyright 2010 Proofpoint, Inc.
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
package io.airlift.http.client;

import io.airlift.json.JsonCodec;

import javax.annotation.concurrent.GuardedBy;

import java.io.OutputStream;

import static java.util.Objects.requireNonNull;

public class JsonBodyGenerator<T>
        implements BodyGenerator
{
    private final JsonCodec<T> jsonCodec;
    private final T instance;
    @GuardedBy("this")
    private byte[] body;

    public static <T> JsonBodyGenerator<T> jsonBodyGenerator(JsonCodec<T> jsonCodec, T instance)
    {
        return new JsonBodyGenerator<>(jsonCodec, instance);
    }

    private JsonBodyGenerator(JsonCodec<T> jsonCodec, T instance)
    {
        requireNonNull(jsonCodec, "jsonCodec is null");
        requireNonNull(instance, "instance is null");

        this.jsonCodec = jsonCodec;
        this.instance = instance;
    }

    public T getInstance()
    {
        return instance;
    }

    public JsonCodec<T> getJsonCodec()
    {
        return jsonCodec;
    }

    public synchronized byte[] getBody()
    {
        if (body == null) {
            body = jsonCodec.toJsonBytes(instance);
        }
        return body;
    }

    @Override
    public void write(OutputStream out)
            throws Exception
    {
        out.write(getBody());
    }
}
