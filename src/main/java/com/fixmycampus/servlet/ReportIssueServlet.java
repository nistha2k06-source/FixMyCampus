
package com.fixmycampus.servlet;

import com.fixmycampus.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/report-issue")
public class ReportIssueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String location = request.getParameter("location");
        String severity = request.getParameter("severity");
        String description = request.getParameter("description");

        if (title == null || title.isBlank()
                || category == null || category.isBlank()
                || location == null || location.isBlank()
                || severity == null || severity.isBlank()
                || description == null || description.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please fill in all required fields."
            );
            return;
        }

        String sql = "INSERT INTO complaints "
                + "(title, category, location, severity, description, "
                + "priority_score) VALUES (?, ?, ?, ?, ?, ?)";

        int priority = calculatePriority(severity);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             PrintWriter out = response.getWriter()) {

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, severity);
            ps.setString(5, description);
            ps.setInt(6, priority);

            int rows = ps.executeUpdate();

            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Issue Reported</title></head><body>");

            if (rows > 0) {
                out.println("<h1>Issue reported successfully!</h1>");
                out.println("<p>Your complaint has been saved.</p>");
                out.println("<p>Priority score: " + priority + "</p>");
            } else {
                out.println("<h1>Issue could not be saved.</h1>");
            }

            out.println("<a href='report-issue.html'>Report another issue</a>");
            out.println("</body></html>");

        } catch (SQLException e) {
            throw new ServletException("Unable to save the complaint.", e);
        }
    }

    private int calculatePriority(String severity) {
        switch (severity) {
            case "Critical":
                return 100;
            case "High":
                return 75;
            case "Medium":
                return 50;
            case "Low":
                return 25;
            default:
                return 0;
        }
    }
}

