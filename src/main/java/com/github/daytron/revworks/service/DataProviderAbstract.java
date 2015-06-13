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

import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.AnnouncementType;
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.data.UserType;
import com.github.daytron.revworks.model.Announcement;
import com.github.daytron.revworks.model.ClassTable;
import com.github.daytron.revworks.model.Comment;
import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.model.Review;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implementing {@link DataProvider} interface for common
 * data retrieval query process.
 *
 * @author Ryan Gilera
 */
public abstract class DataProviderAbstract extends QueryManagerAbstract
        implements DataProvider {

    private static final String GSM_LONDON = "GSM London";

    private List<Announcement> listOfAnnouncements;
    private Coursework receivedCoursework;

    public DataProviderAbstract() {
        // init empty
        receivedCoursework = null;
    }

    @Override
    public List<Announcement> populateHomeViewWithData() throws
            SQLErrorQueryException, 
            SQLErrorRetrievingConnectionAndPoolException {

        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatement;
                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement.STUDENT_SELECT_ANNOUNCEMENT
                            .getQuery());

                } else {
                    // Otherwise it's a lecturer
                    preparedStatement = getConnection().prepareStatement(
                            PreparedQueryStatement.LECTURER_SELECT_ANNOUNCEMENT
                            .getQuery());
                }

                preparedStatement.setInt(1,
                        MainUI.get().getAccessControl().getUserId());

                ResultSet resultSet = preparedStatement.executeQuery();

                // Return empty list if no result
                if (!resultSet.next()) {
                    preparedStatement.close();
                    resultSet.close();
                    releaseConnection();
                    
                    return new ArrayList<>();
                }

                resultSet.beforeFirst();

                this.listOfAnnouncements = new ArrayList<>();

                while (resultSet.next()) {
                    Timestamp timestamp = resultSet.getTimestamp(4);
                    LocalDateTime dateSubmitted = timestamp.toLocalDateTime();

                    AnnouncementType announcementType;
                    String announcementSource;
                    Announcement announcement;

                    CopyOnWriteArrayList<ClassTable> listOfClassTables 
                            = CurrentUserSession.getCurrentClassTables();
                    
                    if (resultSet.getInt(5) == 2) {
                        int classId = resultSet.getInt(6);
                        
                        ClassTable selectedClassTable = null;
                        for (ClassTable classTable : listOfClassTables) {
                            if (classTable.getId() == classId) {
                                selectedClassTable = classTable;
                                break;
                            }
                        }
                        
                        String moduleId = selectedClassTable.getModuleId();
                        String moduleName = selectedClassTable.getModuleName();

                        announcementType = AnnouncementType.CLASS_WIDE;

                        if (MainUI.get().getAccessControl().getUserType()
                                == UserType.STUDENT) {
                            announcementSource = selectedClassTable.getLecturerUser()
                                    .getFirstName()
                                    + " " + selectedClassTable.getLecturerUser()
                                    .getLastName();
                        } else {
                            announcementSource
                                    = MainUI.get().getAccessControl().getFullName();
                        }

                        announcement = new Announcement(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                dateSubmitted,
                                announcementType,
                                announcementSource,
                                moduleId,
                                moduleName);
                    } else {
                        announcementType = AnnouncementType.SYSTEM_WIDE;
                        announcementSource = GSM_LONDON;

                        announcement = new Announcement(
                                resultSet.getInt(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                dateSubmitted,
                                announcementType,
                                announcementSource);
                    }

                    this.listOfAnnouncements.add(announcement);
                }

                preparedStatement.close();
                resultSet.close();
                releaseConnection();

                return listOfAnnouncements;

            } catch (SQLException ex) {
                Logger.getLogger(DataProviderAbstract.class.getName())
                        .log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }
    
    @Override
    public Coursework extractReviewsAndComments() throws SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException {

        if (reserveConnectionPool()) {
            try {
                PreparedStatement preparedStatementReview
                        = getConnection().prepareStatement(
                                PreparedQueryStatement.SELECT_REVIEW.getQuery());

                preparedStatementReview.setInt(1, this.receivedCoursework.getId());
                ResultSet resultSetReview = preparedStatementReview.executeQuery();

                // if no reviews found, return the coursework
                // By default, the list of reviews is empty
                if (!resultSetReview.next()) {
                    preparedStatementReview.close();
                    resultSetReview.close();
                    releaseConnection();

                    return this.receivedCoursework;
                }

                final List<Review> listOfReviews = new ArrayList<>();
                resultSetReview.beforeFirst();

                // Iterate through reviews
                while (resultSetReview.next()) {
                    int reviewId = resultSetReview.getInt(1);
                    int scrollLocation = resultSetReview.getInt(2);

                    Timestamp timestamp = resultSetReview.getTimestamp(3);
                    LocalDateTime dateSubmittedReview = timestamp.toLocalDateTime();

                    // Then extract all comments from this review
                    PreparedStatement preparedStatementComment
                            = getConnection().prepareStatement(
                                    PreparedQueryStatement.SELECT_COMMENT.getQuery());

                    preparedStatementComment.setInt(1, reviewId);
                    ResultSet resultSetComment
                            = preparedStatementComment.executeQuery();

                    // If no comments found, create empty list of comments
                    if (!resultSetComment.next()) {
                        listOfReviews.add(new Review(
                                reviewId, scrollLocation, dateSubmittedReview, 
                                new ArrayList<>()));

                        preparedStatementComment.close();
                        resultSetComment.close();
                        continue;
                    }

                    final List<Comment> listOfComments = new ArrayList<>();
                    resultSetComment.beforeFirst();

                    // Iterate through comments
                    while (resultSetComment.next()) {
                        int commentId = resultSetComment.getInt(1);
                        String commentMesage = resultSetComment.getString(2);

                        Timestamp timestampComment = resultSetComment.getTimestamp(3);
                        LocalDateTime dateSubmittedComment
                                = timestampComment.toLocalDateTime();

                        boolean isStudentToLecturer = resultSetComment.getBoolean(4);

                        Comment comment;
                        if (isStudentToLecturer) {
                            comment = new Comment(
                                    commentId,
                                    this.receivedCoursework.getStudentUser(),
                                    this.receivedCoursework.getClassTable()
                                    .getLecturerUser(),
                                    commentMesage, dateSubmittedComment);
                        } else {
                            comment = new Comment(
                                    commentId,
                                    this.receivedCoursework.getClassTable()
                                    .getLecturerUser(), 
                                    this.receivedCoursework.getStudentUser(),
                                    commentMesage, dateSubmittedComment);
                        }
                        
                        listOfComments.add(comment);
                    }
                    
                    preparedStatementComment.close();
                    resultSetComment.close();
                    
                    Review review = new Review(reviewId, scrollLocation, 
                            dateSubmittedReview, listOfComments);
                    listOfReviews.add(review);

                }
                
                preparedStatementReview.close();
                resultSetReview.close();
                releaseConnection();
                
                this.receivedCoursework.setListOfReviews(listOfReviews);
                return this.receivedCoursework;

            } catch (SQLException ex) {
                Logger.getLogger(LecturerDataProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
                releaseConnection();
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }

    }

    public void setReceivedCoursework(Coursework receivedCoursework) {
        this.receivedCoursework = receivedCoursework;
    }
    
    public Coursework getReceivedCoursework() {
        return receivedCoursework;
    }

}
