/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
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

package org.dd4t.contentmodel;

import java.util.Map;

/**
 * Interface for items which have metadata
 *
 * @author bjornl
 */
public interface HasMetadata {

    /**
     * Get the metadata as a map of fields where the field name is the key.
     *
     * @return the content as a map of fields.
     */
    Map<String, Field> getMetadata();

    /**
     * Set the metadata
     *
     * @param metadata
     */
    void setMetadata(Map<String, Field> metadata);
}
