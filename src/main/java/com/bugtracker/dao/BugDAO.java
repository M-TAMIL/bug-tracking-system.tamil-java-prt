package com.bugtracker.dao;

import java.util.List;

import com.bugtracker.model.Bug;

public interface BugDAO {
    boolean addBug(Bug bug);
    List<Bug> getAllBugs();
    Bug getBugById(int id);
    boolean updateBug(Bug bug, int changedBy);
    boolean deleteBug(int id);
}