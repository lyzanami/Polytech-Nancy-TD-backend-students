package com.example.todoapp.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object for {@link Task} model.
 */
public class TaskDao {
	
	private static final String url = "jdbc:sqlite:database.db";
	
	public TaskDao() {
		String sqlCreateTable = """
				
			CREATE TABLE IF NOT EXISTS tasks (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				title TEXT NOT NULL,
				description TEXT,
				done INTEGER NOT NULL
			);
			""";
		try(Connection connection = DriverManager.getConnection(url);
				Statement statement = connection.createStatement()) {
			statement.execute(sqlCreateTable);
			System.out.println("bitches !!");
			save(new Task(1, "Réviser DS de maths", "Séries numériques et probabilités.", false));
	        save(new Task(2, "Valider mon PIVE", "PIVE Club Poker.", true));
	        save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?", false));
	    } catch (SQLException e) {
	    	System.out.println("no bitches :(");
	        throw new RuntimeException("Impossible d'initialiser la base de données SQLite", e);
	    }
	}
/*
    private final Map<Integer, Task> storage = new HashMap<>();

    {
        save(new Task(1, "Réviser DS de maths", "Séries numériques et probabilités.", false));
        save(new Task(2, "Valider mon PIVE", "PIVE Club Poker.", true));
        save(new Task(3, "Choisir mon parcours de 4A", "SIR ou SIA ?", false));
    }
*/
	
    /**
     * Persist {@link Task} model.
     * @param task task to save.
     * @return task model.
     */
    public Task save(Task task) {
    	if (task.id() <= 0) {
    		String sqlInsertIdRequest = "INSERT INTO tasks (title, description, done) VALUES (?, ?, ?)";
    		try (Connection connection = DriverManager.getConnection(url);
    				PreparedStatement preparedsatement = connection.prepareStatement(sqlInsertIdRequest, Statement.RETURN_GENERATED_KEYS)) {

    			preparedsatement.setString(1, task.title());
    			preparedsatement.setString(2, task.description());
    			preparedsatement.setInt(3, task.done() ? 1 : 0);
    			preparedsatement.executeUpdate();
    			
    			try (ResultSet resultset = preparedsatement.getGeneratedKeys()) {
    				if (resultset.next()) {
    					int resultId = resultset.getInt(1);
    					return new Task(resultId, task.title(), task.description(), task.done());
    				}
    			}
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	} else {
    		String sql = "UPDATE tasks SET title = ?, description = ?, done = ? WHERE id = ?";
    		try (Connection connection = DriverManager.getConnection(url);
    				PreparedStatement preparedsatement = connection.prepareStatement(sql)) {

    			preparedsatement.setString(1, task.title());
    			preparedsatement.setString(2, task.description());
    			preparedsatement.setInt(3, task.done() ? 1 : 0);
    			preparedsatement.setInt(4, task.id());
    			preparedsatement.executeUpdate();
    			return task;
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	return task;
    }

    /**
     * Retrieve {@link Task} model by id.
     * @param id identifier of the {@link Task}.
     * @return {@link Task} model wrapped by Optional.
     */
    public Optional<Task> findById(int id) {
    	String sqlFindIdRequest = "SELECT id, title, description, done FROM tasks WHERE id = ?";
    	try(Connection connection = DriverManager.getConnection(url);
    			PreparedStatement preparedstatement = connection.prepareStatement(sqlFindIdRequest)) {
    		
    		preparedstatement.setInt(1, id);
    		try(ResultSet resultset = preparedstatement.executeQuery()) {
    			if(resultset.next()) {
    				Task task = new Task(
    					resultset.getInt("id"),
        				resultset.getString("title"),
        				resultset.getString("description"),
        				resultset.getInt("done") == 1
        			);
    				return Optional.of(task);
    			}
    		}
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return Optional.empty();
    }
    
    /**
     * Retrieve all {@link Task} model.
     * @return {@link Task} collection.
     */
    public Collection<Task> findAll() {
    	Collection<Task> tasks = new ArrayList<>();
    	String sqlFindAllRequest = "SELECT id, title, description, done FROM tasks";
    	
    	try(Connection connection = DriverManager.getConnection(url);
    			Statement statement = connection.createStatement();
    			ResultSet resultset = statement.executeQuery(sqlFindAllRequest)) {
    		
    		while (resultset.next()) {
    			tasks.add(new Task(
    					resultset.getInt("id"),
    					resultset.getString("title"),
    					resultset.getString("description"),
    					resultset.getInt("done") == 1
    					));
    		}
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return tasks;
    }

    /**
     * Delete {@link Task} model by id.
     * @param id identifier of the {@link Task}.
     * @return true if chosen {@link Task} exist and is deleted.
     */
    public boolean deleteById(int id) {
    	String sqlDeleteIdRequest = "DELETE FROM tasks WHERE id = ?";
    	try (Connection connection = DriverManager.getConnection(url);
    			PreparedStatement preparedstatement = connection.prepareStatement(sqlDeleteIdRequest)) {

    		preparedstatement.setInt(1, id);
    		int affectedRows = preparedstatement.executeUpdate();
    		return affectedRows > 0;
    		
           } catch (SQLException e) {
               e.printStackTrace();
               return false;
           }
    }
}
