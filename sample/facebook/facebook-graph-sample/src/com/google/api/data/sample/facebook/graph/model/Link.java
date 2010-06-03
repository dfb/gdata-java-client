/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.data.sample.facebook.graph.model;

import com.google.api.client.util.Key;

/**
 * @author Yaniv Inbar
 */
public final class Link {

  @Key
  public String id;
  
  @Key
  public String from;
  
  @Key
  public String link;

  @Key
  public String name;

  @Key
  public String caption;

  @Key
  public String description;

  @Key
  public String message;

  @Key("updated_time")
  public String updatedTime;
}
