
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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/view-complaints")
public class ViewComplaintsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String sql = "SELECT complaint_id, title, category, location, "
                + "severity, status, priority_score, created_at "
                + "FROM complaints ORDER BY complaint_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>All Complaints | FixMyCampus</title>");

            out.println("<style>");
            out.println("body{font-family:Arial;background:#f1f5f9;color:#172033;padding:25px}");
            out.println("h1{background:#14213d;color:white;padding:20px;border-radius:10px}");
            out.println("table{width:100%;border-collapse:collapse;background:white}");
            out.println("th,td{padding:12px;border:1px solid #ddd;text-align:left}");
            out.println("th{background:#087f8c;color:white}");
            out.println("tr:nth-child(even){background:#f8fafc}");
            out.println("a{color:#087f8c;font-weight:bold}");
            out.println("</style></head><body>");

            out.println("<h1>FixMyCampus - All Complaints</h1>");
            out.println("<p><a href='report-issue.html'>+ Report New Issue</a></p>");

            out.println("<table><tr>");
            out.println("<th>ID</th><th>Title</th><th>Category</th>");
            out.println("<th>Location</th><th>Severity</th>");
            out.println("<th>Status</th><th>Priority</th><th>Reported At</th>");
            out.println("</tr>");

            boolean found = false;

            while (rs.next()) {
                found = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("complaint_id") + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("title")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("category")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("location")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("severity")) + "</td>");
                out.println("<td>" + escapeHtml(rs.getString("status")) + "</td>");
                out.println("<td>" + rs.getInt("priority_score") + "</td>");
                out.println("<td>" + rs.getTimestamp("created_at") + "</td>");
                out.println("</tr>");
            }

            if (!found) {
                out.println("<tr><td colspan='8'>No complaints found.</td></tr>");
            }

            out.println("</table></body></html>");

        } catch (SQLException e) {
            throw new ServletException("Unable to load complaints.", e);
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
}

