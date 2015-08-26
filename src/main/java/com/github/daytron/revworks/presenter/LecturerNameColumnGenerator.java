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

import com.github.daytron.revworks.model.ClassTable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;

/**
 * Custom column data for lecturer's complete name. This is use to extract
 * nested LecturerUser object from ClassTable object from Coursework object.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class LecturerNameColumnGenerator implements Table.ColumnGenerator {

    /**
     * Generates a custom cell to display the associated lecturer's full name.
     * 
     * @param source the associated table 
     * @param itemId item id which pertains to a specific row in the table
     * @param columnId the corresponding column
     * @return the lecturer's full name
     */
    @Override
    @SuppressWarnings("Unchecked")
    public Object generateCell(Table source, Object itemId, Object columnId) {
        // First retrieve the item which is the row item 
        Item cellItem = source.getItem(itemId);
        
        // Extract the ClassTable object from it
        Property<ClassTable> classTableProperty = 
                cellItem.getItemProperty("classTable");
        ClassTable classTable = classTableProperty.getValue();
        
        return classTable.getLecturerUser().getFullName();
    }

}
