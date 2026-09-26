package com.bugtracker.dao;

import java.util.List;

import com.bugtracker.model.Bug;
import com.bugtracker.model.Priority;
import com.bugtracker.model.Status;

public interface BugDAO {
    boolean addBug(Bug bug);
    List<Bug> getAllBugs();
    List<Bug> getBugsByStatus(Status status);
    List<Bug> getBugsByPriority(Priority priority);
    Bug getBugById(int id);
    boolean updateBug(Bug bug, int changedBy);
    boolean deleteBug(int id);
}