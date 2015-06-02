/*
 * Copyright 2015 Ryan Gilera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.daytron.revworks.model;

/**
 * Class model class for Class table.
 *
 * @author Ryan Gilera
 */
public class Class {

    private final int id;
    private final String moduleId;
    private final String moduleName;

    public Class(int id, String moduleId, String moduleName) {
        this.id = id;
        this.moduleId = moduleId;
        this.moduleName = moduleName;
    }

    public int getId() {
        return id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

}