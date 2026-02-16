package com.mohit.durable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StepStore {

    public boolean stepExists(String workflowId, int sequenceNumber) {
        String sql = "SELECT COUNT(*) FROM steps WHERE workflow_id = ? AND sequence_number = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workflowId);
            statement.setInt(2, sequenceNumber);

            ResultSet rs = statement.executeQuery();
            rs.next();

            return rs.getInt(1) > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check step existence", e);
        }
    }

    public String getStepResult(String workflowId, int sequenceNumber) {
        String sql = "SELECT result FROM steps WHERE workflow_id = ? AND sequence_number = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workflowId);
            statement.setInt(2, sequenceNumber);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("result");
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve step result", e);
        }
    }

    public void saveStep(String workflowId, String stepName, int sequenceNumber, String result) {
        String sql = "INSERT INTO steps (workflow_id, step_name, sequence_number, result) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, workflowId);
            statement.setString(2, stepName);
            statement.setInt(3, sequenceNumber);
            statement.setString(4, result);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save step", e);
        }
    }
}
