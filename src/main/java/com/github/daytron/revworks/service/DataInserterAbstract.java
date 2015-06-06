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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.util.NotificationUtil;

/**
 * Abstract class for implementing {@link DataInserter} interface for common
 * data insertion and update query process.
 *
 * @author Ryan Gilera
 */
public class DataInserterAbstract extends QueryManagerAbstract {

    void notifyDataSendError() {
        NotificationUtil.showError(
                ErrorMsg.DATA_SEND_ERROR.getText(),
                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
    }
}
