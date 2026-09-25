package com.bugtracker.dao;

import com.bugtracker.model.Project;
import java.util.List;

/**
 * Data Access Object (DAO) interface for Project entity operations.
 * Defines standard CRUD operations for Project Management.
 */
public interface ProjectDAO {

    /**
     * Inserts a new project into the database.
     *
     * @param project Project object containing name, description, and createdBy
     * @return true if insertion was successful, false otherwise
     */
    boolean addProject(Project project);

    /**
     * Retrieves all projects registered in the database.
     *
     * @return List of all Project objects
     */
    List<Project> getAllProjects();

    /**
     * Finds and retrieves a project by its unique primary key ID.
     *
     * @param id the project ID to search for
     * @return Project object if found, or null if no project matches the ID
     */
    Project getProjectById(int id);

    /**
     * Updates an existing project's name, description, or creator.
     *
     * @param project Project object with updated information and valid ID
     * @return true if update was successful, false otherwise
     */
    boolean updateProject(Project project);

    /**
     * Deletes a project by its unique ID.
     *
     * @param id the ID of the project to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteProject(int id);
}
