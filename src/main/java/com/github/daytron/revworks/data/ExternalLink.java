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
package com.github.daytron.revworks.data;

/**
 * Collection of external links.
 *
 * @author Ryan Gilera
 */
public enum ExternalLink {

    GSM_LONDON("GSM London",
            "https://www.gsm.org.uk/"),
    STUDENT_PORTAL("Student Portal", 
            "https://www.gsomportal.com/student/login.aspx"),
    LECTURER_PORTAL("Lecturer Portal", 
            "https://www.gsomportal.com/lecturer/login.aspx"),
    GSM_LEARN("GSM Learn",
            "https://learn.gsm.org.uk/"),
    MY_GITHUB_PAGE("Github",
            "https://github.com/Daytron"),
    MY_LINKEDIN_PAGE("LinkedIn",
            "https://uk.linkedin.com/in/ryangilera");

    private final String name;
    private final String link;

    private ExternalLink(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

}
