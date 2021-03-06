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
package com.github.daytron.revworks.presenter;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

/**
 * Custom column data for student's id. This is use to extract
 * nested StudentUser object from Coursework object.
 * 
 * @author Ryan Gilera
 */
import com.github.daytron.revworks.model.StudentUser;
public class StudentIdColumnGenerator implements Table.ColumnGenerator {

    /**
     * Generates a custom cell to display the student id of the associated 
     * itemId.
     * 
     * @param source the associated table 
     * @param itemId item id which pertains to a specific row in the table
     * @param columnId the corresponding column
     * @return the student id
     */
    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Item item = source.getItem(itemId);
        
        Property<StudentUser> studentProperty = item.getItemProperty("studentUser");
        
        return studentProperty.getValue().getStudentID();
    }
    
}
