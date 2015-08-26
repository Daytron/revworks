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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Custom column data for date submitted. This is use to format LocalDateTime
 * object to local datetime from Coursework object.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class LocalDateTimeColumnGenerator implements Table.ColumnGenerator {

    /**
     * Generates a custom cell a text formatted String value of LocalDateTime.
     * 
     * @param source the associated table 
     * @param itemId item id which pertains to a specific row in the table
     * @param columnId the corresponding column
     * @return the formatted text date time
     */
    @Override
    @SuppressWarnings("Unchecked")
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Item cellItem = source.getItem(itemId);

        Property<LocalDateTime> dateSubmittedProperty = cellItem
                .getItemProperty(columnId);
        LocalDateTime localDateTime = dateSubmittedProperty.getValue();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);

        return localDateTime.format(dateTimeFormatter);
    }

}
