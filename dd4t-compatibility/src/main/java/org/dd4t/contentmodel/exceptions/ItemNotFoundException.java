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

public class ItemNotFoundException extends org.dd4t.core.exceptions.ItemNotFoundException {

    private static final long serialVersionUID = 8243724759254216595L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Throwable t) {
        super(t);
    }

    public ItemNotFoundException(String message, Throwable t) {
        super(message, t);
    }
}
