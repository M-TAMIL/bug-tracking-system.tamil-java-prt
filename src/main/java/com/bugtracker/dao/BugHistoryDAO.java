package com.bugtracker.dao;

import java.util.List;

import com.bugtracker.model.BugHistory;

public interface BugHistoryDAO {
    List<BugHistory> getHistoryForBug(int bugId);
}