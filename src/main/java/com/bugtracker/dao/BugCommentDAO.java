package com.bugtracker.dao;

import java.util.List;

import com.bugtracker.model.BugComment;

public interface BugCommentDAO {
    boolean addComment(BugComment comment);
    List<BugComment> getCommentsForBug(int bugId);
    boolean deleteComment(int commentId, int requestingUserId);
}