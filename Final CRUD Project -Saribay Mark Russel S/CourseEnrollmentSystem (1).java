import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CourseEnrollmentSystem extends JFrame {
    // Colors & fonts for a modern look
    private final Color sidebarBg = new Color(30, 41, 59);
    private final Color sidebarHover = new Color(59, 130, 246);
    private final Color mainBg = new Color(248, 250, 252);
    private final Color primaryColor = new Color(59, 130, 246);
    private final Font font = new Font("Segoe UI", Font.PLAIN, 14);

    // Sidebar buttons
    private JButton btnStudents, btnCourses, btnEnrollments;

    // Main content panel with CardLayout
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Module panels
    private StudentPanel studentPanel;
    private CoursePanel coursePanel;
    private EnrollmentPanel enrollmentPanel;

    public CourseEnrollmentSystem() {
        setTitle("Course Enrollment System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main panel with card layout for different modules
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(mainBg);

        // Create panels for each module
        studentPanel = new StudentPanel();
        coursePanel = new CoursePanel();
        enrollmentPanel = new EnrollmentPanel();

        mainPanel.add(studentPanel, "STUDENTS");
        mainPanel.add(coursePanel, "COURSES");
        mainPanel.add(enrollmentPanel, "ENROLLMENTS");

        add(mainPanel, BorderLayout.CENTER);

        // Show student panel by default
        cardLayout.show(mainPanel, "STUDENTS");

        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Branding / Title
        JLabel title = new JLabel("Course System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(30, 15, 40, 15));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        // Buttons
        btnStudents = createSidebarButton("Students");
        btnCourses = createSidebarButton("Courses");
        btnEnrollments = createSidebarButton("Enrollments");

        // Add action listeners
        btnStudents.addActionListener(e -> switchPanel("STUDENTS"));
        btnCourses.addActionListener(e -> switchPanel("COURSES"));
        btnEnrollments.addActionListener(e -> switchPanel("ENROLLMENTS"));

        sidebar.add(btnStudents);
        sidebar.add(btnCourses);
        sidebar.add(btnEnrollments);

        // Space filler
        sidebar.add(Box.createVerticalGlue());

        // Footer
        JLabel footer = new JLabel("© 2025 My University");
        footer.setForeground(Color.GRAY);
        footer.setBorder(new EmptyBorder(20, 15, 20, 15));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);

        // Highlight Students button by default
        highlightButton(btnStudents);

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(font.deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(sidebarBg);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getBackground().equals(sidebarHover))
                    btn.setBackground(new Color(75, 85, 99));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getBackground().equals(sidebarHover))
                    btn.setBackground(sidebarBg);
            }
        });

        return btn;
    }

    private void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        // Reset button highlights
        resetButtonHighlight();

        // Highlight the active button
        switch (panelName) {
            case "STUDENTS" -> highlightButton(btnStudents);
            case "COURSES" -> highlightButton(btnCourses);
            case "ENROLLMENTS" -> highlightButton(btnEnrollments);
        }
    }

    private void resetButtonHighlight() {
        btnStudents.setBackground(sidebarBg);
        btnCourses.setBackground(sidebarBg);
        btnEnrollments.setBackground(sidebarBg);
    }

    private void highlightButton(JButton btn) {
        btn.setBackground(sidebarHover);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CourseEnrollmentSystem::new);
    }

    // --------- STUDENT PANEL ---------
    static class StudentPanel extends JPanel {
        private JTextField txtStudentId, txtFirstName, txtLastName, txtMiddleName, txtSearch;
        private JTable table;
        private DefaultTableModel model;
        private final Color primaryColor = new Color(59, 130, 246);
        private final Font font = new Font("Segoe UI", Font.PLAIN, 14);

        public StudentPanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.WHITE);

            JLabel header = new JLabel("Student Management");
            header.setFont(new Font("Segoe UI", Font.BOLD, 24));
            header.setForeground(primaryColor);
            add(header, BorderLayout.NORTH);

            // Top search bar
            JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
            searchPanel.setBackground(Color.WHITE);
            txtSearch = new JTextField();
            txtSearch.setFont(font);
            txtSearch.setToolTipText("Search students by ID, First Name, Last Name, or Middle Name");
            JLabel lblSearch = new JLabel("Search:");
            lblSearch.setFont(font.deriveFont(Font.BOLD));
            searchPanel.add(lblSearch, BorderLayout.WEST);
            searchPanel.add(txtSearch, BorderLayout.CENTER);
            add(searchPanel, BorderLayout.SOUTH);

            // Split center panel: left form, right table
            JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
            centerPanel.setBackground(Color.WHITE);

            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtStudentId = new JTextField();
            txtFirstName = new JTextField();
            txtLastName = new JTextField();
            txtMiddleName = new JTextField();

            int row = 0;

            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Student ID:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtStudentId, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("First Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtFirstName, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Last Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtLastName, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Middle Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtMiddleName, gbc);

            // Buttons panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            btnPanel.setBackground(Color.WHITE);
            JButton btnAdd = createButton("Add");
            JButton btnUpdate = createButton("Update");
            JButton btnDelete = createButton("Delete");
            btnPanel.add(btnAdd);
            btnPanel.add(btnUpdate);
            btnPanel.add(btnDelete);

            JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(formPanel, BorderLayout.CENTER);
            leftPanel.add(btnPanel, BorderLayout.SOUTH);
            leftPanel.setPreferredSize(new Dimension(360, 0));

            centerPanel.add(leftPanel, BorderLayout.WEST);

            // Table setup
            model = new DefaultTableModel(new String[]{"DB ID", "Student ID", "First Name", "Last Name", "Middle Name"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // make table non-editable directly
                }
            };
            table = new JTable(model);
            styleTable(table);

            JScrollPane tableScroll = new JScrollPane(table);
            centerPanel.add(tableScroll, BorderLayout.CENTER);

            add(centerPanel, BorderLayout.CENTER);

            // Load students
            loadStudents();

            // Event handlers
            btnAdd.addActionListener(e -> addStudent());
            btnUpdate.addActionListener(e -> updateStudent());
            btnDelete.addActionListener(e -> deleteStudent());

            table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

            txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            });
        }

        private void filterTable() {
            String query = txtSearch.getText().toLowerCase().trim();
            model.setRowCount(0);
            String sql = "SELECT * FROM students WHERE " +
                    "LOWER(student_id) LIKE ? OR LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? OR LOWER(middle_name) LIKE ?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                String likeQuery = "%" + query + "%";
                for (int i = 1; i <= 4; i++) {
                    ps.setString(i, likeQuery);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("student_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("middle_name")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error searching students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(font.deriveFont(Font.BOLD));
            return lbl;
        }

        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(font.deriveFont(Font.BOLD));
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(100, 35));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(primaryColor.darker());
                }

                public void mouseExited(MouseEvent e) {
                    btn.setBackground(primaryColor);
                }
            });
            return btn;
        }

        private void styleTable(JTable table) {
            table.setFont(font);
            table.setRowHeight(28);
            table.getTableHeader().setFont(font.deriveFont(Font.BOLD));
            table.getTableHeader().setBackground(primaryColor);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionBackground(primaryColor.brighter());
            table.setSelectionForeground(Color.WHITE);
        }

        private void fillFormFromTable() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            txtStudentId.setText(model.getValueAt(row, 1).toString());
            txtFirstName.setText(model.getValueAt(row, 2).toString());
            txtLastName.setText(model.getValueAt(row, 3).toString());
            txtMiddleName.setText(model.getValueAt(row, 4).toString());
        }

        private void loadStudents() {
            model.setRowCount(0);
            String sql = "SELECT * FROM students ORDER BY student_id";
            try (Connection conn = connectDB();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("middle_name")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addStudent() {
            String sid = txtStudentId.getText().trim();
            String fname = txtFirstName.getText().trim();
            String lname = txtLastName.getText().trim();
            String mname = txtMiddleName.getText().trim();

            if (sid.isEmpty() || fname.isEmpty() || lname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID, First Name, and Last Name are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO students (student_id, first_name, last_name, middle_name) VALUES (?, ?, ?, ?)";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sid);
                ps.setString(2, fname);
                ps.setString(3, lname);
                ps.setString(4, mname.isEmpty() ? null : mname);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateStudent() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a student to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int dbId = (int) model.getValueAt(row, 0);

            String sid = txtStudentId.getText().trim();
            String fname = txtFirstName.getText().trim();
            String lname = txtLastName.getText().trim();
            String mname = txtMiddleName.getText().trim();

            if (sid.isEmpty() || fname.isEmpty() || lname.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID, First Name, and Last Name are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "UPDATE students SET student_id=?, first_name=?, last_name=?, middle_name=? WHERE id=?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sid);
                ps.setString(2, fname);
                ps.setString(3, lname);
                ps.setString(4, mname.isEmpty() ? null : mname);
                ps.setInt(5, dbId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteStudent() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a student to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int dbId = (int) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete the selected student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String sql = "DELETE FROM students WHERE id=?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dbId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStudents();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearForm() {
            txtStudentId.setText("");
            txtFirstName.setText("");
            txtLastName.setText("");
            txtMiddleName.setText("");
            table.clearSelection();
        }

        // Database connection method
        private Connection connectDB() throws SQLException {
        	  String url = "jdbc:mysql://localhost:3306/course_enrollment";
              String user = "root";
              String pass = "";
            return DriverManager.getConnection(url, user, pass);
        }
    }

    // --------- COURSE PANEL ---------
    static class CoursePanel extends JPanel {
        private JTextField txtCourseCode, txtCourseName, txtInstructor, txtSearch;
        private JTable table;
        private DefaultTableModel model;
        private final Color primaryColor = new Color(59, 130, 246);
        private final Font font = new Font("Segoe UI", Font.PLAIN, 14);

        public CoursePanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.WHITE);

            JLabel header = new JLabel("Course Management");
            header.setFont(new Font("Segoe UI", Font.BOLD, 24));
            header.setForeground(primaryColor);
            add(header, BorderLayout.NORTH);

            // Search bar
            JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
            searchPanel.setBackground(Color.WHITE);
            txtSearch = new JTextField();
            txtSearch.setFont(font);
            txtSearch.setToolTipText("Search courses by code or name");
            JLabel lblSearch = new JLabel("Search:");
            lblSearch.setFont(font.deriveFont(Font.BOLD));
            searchPanel.add(lblSearch, BorderLayout.WEST);
            searchPanel.add(txtSearch, BorderLayout.CENTER);
            add(searchPanel, BorderLayout.SOUTH);

            JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
            centerPanel.setBackground(Color.WHITE);

            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            txtCourseCode = new JTextField();
            txtCourseName = new JTextField();
            txtInstructor = new JTextField();

            int row = 0;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Course Code:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtCourseCode, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Course Name:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtCourseName, gbc);

            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(createLabel("Instructor:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtInstructor, gbc);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            btnPanel.setBackground(Color.WHITE);
            JButton btnAdd = createButton("Add");
            JButton btnUpdate = createButton("Update");
            JButton btnDelete = createButton("Delete");
            btnPanel.add(btnAdd);
            btnPanel.add(btnUpdate);
            btnPanel.add(btnDelete);

            JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
            leftPanel.setBackground(Color.WHITE);
            leftPanel.add(formPanel, BorderLayout.CENTER);
            leftPanel.add(btnPanel, BorderLayout.SOUTH);
            leftPanel.setPreferredSize(new Dimension(360, 0));

            centerPanel.add(leftPanel, BorderLayout.WEST);

            model = new DefaultTableModel(new String[]{"ID", "Course Code", "Course Name", "Instructor"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            styleTable(table);

            JScrollPane scrollPane = new JScrollPane(table);
            centerPanel.add(scrollPane, BorderLayout.CENTER);

            add(centerPanel, BorderLayout.CENTER);

            loadCourses();

            btnAdd.addActionListener(e -> addCourse());
            btnUpdate.addActionListener(e -> updateCourse());
            btnDelete.addActionListener(e -> deleteCourse());

            table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

            txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            });
        }

        private void filterTable() {
            String query = txtSearch.getText().toLowerCase().trim();
            model.setRowCount(0);
            String sql = "SELECT * FROM courses WHERE LOWER(course_code) LIKE ? OR LOWER(course_name) LIKE ?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                String likeQuery = "%" + query + "%";
                ps.setString(1, likeQuery);
                ps.setString(2, likeQuery);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("course_code"),
                                rs.getString("course_name"),
                                rs.getString("instructor")
                        });
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error searching courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private JLabel createLabel(String text) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(font.deriveFont(Font.BOLD));
            return lbl;
        }

        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(font.deriveFont(Font.BOLD));
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(100, 35));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(primaryColor.darker());
                }

                public void mouseExited(MouseEvent e) {
                    btn.setBackground(primaryColor);
                }
            });
            return btn;
        }

        private void styleTable(JTable table) {
            table.setFont(font);
            table.setRowHeight(28);
            table.getTableHeader().setFont(font.deriveFont(Font.BOLD));
            table.getTableHeader().setBackground(primaryColor);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionBackground(primaryColor.brighter());
            table.setSelectionForeground(Color.WHITE);
        }

        private void fillFormFromTable() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            txtCourseCode.setText(model.getValueAt(row, 1).toString());
            txtCourseName.setText(model.getValueAt(row, 2).toString());
            txtInstructor.setText(model.getValueAt(row, 3).toString());
        }

        private void loadCourses() {
            model.setRowCount(0);
            String sql = "SELECT * FROM courses ORDER BY course_code";
            try (Connection conn = connectDB();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getString("instructor")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void addCourse() {
            String code = txtCourseCode.getText().trim();
            String name = txtCourseName.getText().trim();
            String instructor = txtInstructor.getText().trim();

            if (code.isEmpty() || name.isEmpty() || instructor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code, Name, and Instructor are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "INSERT INTO courses (course_code, course_name, instructor) VALUES (?, ?, ?)";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setString(3, instructor);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCourses();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void updateCourse() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a course to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int dbId = (int) model.getValueAt(row, 0);

            String code = txtCourseCode.getText().trim();
            String name = txtCourseName.getText().trim();
            String instructor = txtInstructor.getText().trim();

            if (code.isEmpty() || name.isEmpty() || instructor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code, Name, and Instructor are required.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "UPDATE courses SET course_code=?, course_name=?, instructor=? WHERE id=?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                ps.setString(2, name);
                ps.setString(3, instructor);
                ps.setInt(4, dbId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCourses();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteCourse() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a course to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int dbId = (int) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete the selected course?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String sql = "DELETE FROM courses WHERE id=?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dbId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCourses();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearForm() {
            txtCourseCode.setText("");
            txtCourseName.setText("");
            txtInstructor.setText("");
            table.clearSelection();
        }

        // Database connection method
        private Connection connectDB() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/course_enrollment";
            String user = "root";
            String pass = "";
            return DriverManager.getConnection(url, user, pass);
        }
    }

    // --------- ENROLLMENT PANEL ---------
    static class EnrollmentPanel extends JPanel {
        private JComboBox<String> cbStudents, cbCourses;
        private JButton btnEnroll, btnDeleteEnrollment;
        private JTable table;
        private DefaultTableModel model;
        private final Color primaryColor = new Color(59, 130, 246);
        private final Font font = new Font("Segoe UI", Font.PLAIN, 14);

        public EnrollmentPanel() {
            setLayout(new BorderLayout(15, 15));
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setBackground(Color.WHITE);

            JLabel header = new JLabel("Course Enrollment");
            header.setFont(new Font("Segoe UI", Font.BOLD, 24));
            header.setForeground(primaryColor);
            add(header, BorderLayout.NORTH);

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topPanel.setBackground(Color.WHITE);

            cbStudents = new JComboBox<>();
            cbCourses = new JComboBox<>();
            btnEnroll = new JButton("Enroll");
            btnDeleteEnrollment = new JButton("Delete Enrollment");

            styleButton(btnEnroll);
            styleButton(btnDeleteEnrollment);

            topPanel.add(new JLabel("Student:"));
            topPanel.add(cbStudents);
            topPanel.add(new JLabel("Course:"));
            topPanel.add(cbCourses);
            topPanel.add(btnEnroll);
            topPanel.add(btnDeleteEnrollment);

            add(topPanel, BorderLayout.NORTH);

            model = new DefaultTableModel(new String[]{"Enrollment ID", "Student ID", "Student Name", "Course Code", "Course Name"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(model);
            styleTable(table);

            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);

            loadStudents();
            loadCourses();
            loadEnrollments();

            btnEnroll.addActionListener(e -> enrollStudent());
            btnDeleteEnrollment.addActionListener(e -> deleteEnrollment());
        }

        private void styleButton(JButton btn) {
            btn.setFont(font.deriveFont(Font.BOLD));
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(140, 35));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(primaryColor.darker());
                }

                public void mouseExited(MouseEvent e) {
                    btn.setBackground(primaryColor);
                }
            });
        }

        private void styleTable(JTable table) {
            table.setFont(font);
            table.setRowHeight(28);
            table.getTableHeader().setFont(font.deriveFont(Font.BOLD));
            table.getTableHeader().setBackground(primaryColor);
            table.getTableHeader().setForeground(Color.WHITE);
            table.setSelectionBackground(primaryColor.brighter());
            table.setSelectionForeground(Color.WHITE);
        }

        private void loadStudents() {
            cbStudents.removeAllItems();
            String sql = "SELECT id, student_id, first_name, last_name FROM students ORDER BY student_id";
            try (Connection conn = connectDB();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String display = rs.getString("student_id") + " - " + rs.getString("first_name") + " " + rs.getString("last_name");
                    cbStudents.addItem(display + ":" + rs.getInt("id"));  // Store ID after colon
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void loadCourses() {
            cbCourses.removeAllItems();
            String sql = "SELECT id, course_code, course_name FROM courses ORDER BY course_code";
            try (Connection conn = connectDB();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String display = rs.getString("course_code") + " - " + rs.getString("course_name");
                    cbCourses.addItem(display + ":" + rs.getInt("id"));  // Store ID after colon
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void loadEnrollments() {
            model.setRowCount(0);
            String sql = "SELECT e.id AS enrollment_id, s.student_id, CONCAT(s.first_name, ' ', s.last_name) AS student_name, c.course_code, c.course_name " +
                         "FROM enrollments e " +
                         "JOIN students s ON e.student_id = s.id " +
                         "JOIN courses c ON e.course_id = c.id " +
                         "ORDER BY s.student_id, c.course_code";
            try (Connection conn = connectDB();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("enrollment_id"),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("course_code"),
                            rs.getString("course_name")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void enrollStudent() {
            if (cbStudents.getSelectedItem() == null || cbCourses.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select both a student and a course.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int studentId = getIdFromComboItem(cbStudents.getSelectedItem().toString());
            int courseId = getIdFromComboItem(cbCourses.getSelectedItem().toString());

            String checkSql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
            String insertSql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
            try (Connection conn = connectDB();
                 PreparedStatement checkPs = conn.prepareStatement(checkSql);
                 PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                checkPs.setInt(1, studentId);
                checkPs.setInt(2, courseId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "Student is already enrolled in this course.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                insertPs.setInt(1, studentId);
                insertPs.setInt(2, courseId);
                insertPs.executeUpdate();
                JOptionPane.showMessageDialog(this, "Enrollment successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error enrolling student: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteEnrollment() {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an enrollment to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int enrollmentId = (int) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete the selected enrollment?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String sql = "DELETE FROM enrollments WHERE id=?";
            try (Connection conn = connectDB();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, enrollmentId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Enrollment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEnrollments();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting enrollment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private int getIdFromComboItem(String item) {
            int colonIndex = item.lastIndexOf(':');
            if (colonIndex >= 0) {
                try {
                    return Integer.parseInt(item.substring(colonIndex + 1));
                } catch (NumberFormatException ignored) {}
            }
            return -1;
        }

        // Database connection method
        private Connection connectDB() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/course_enrollment";
            String user = "root";
            String pass = "";
            return DriverManager.getConnection(url, user, pass);
        }
    }
}