/**
 * Copyright 2011 Capgemini & SDL
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dd4t.contentmodel.exceptions;

public class NotAuthenticatedException extends org.dd4t.core.exceptions.NotAuthenticatedException {

    /**
     *
     */
    private static final long serialVersionUID = -1393489613071343863L;

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(Throwable t) {
        super(t);
    }

    public NotAuthenticatedException(String message, Throwable t) {
        super(message, t);
    }
}
