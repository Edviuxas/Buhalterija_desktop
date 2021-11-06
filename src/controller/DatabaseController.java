package controller;

import GSONSerializable.IncomeGSONSerializer;
import com.mysql.cj.jdbc.CallableStatement;
import ds.*;
import org.springframework.cglib.core.Local;
import org.springframework.test.context.jdbc.Sql;
import sun.java2d.windows.GDIWindowSurfaceData;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseController {
    public static Connection connectToDb() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String DB_URL = "jdbc:mysql://localhost/financemanagementsystem";
        String USER = "root";
        String PASS = "";
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        return conn;
    }

    public static void disconnectFromDb(Connection connection, Statement statement) {
        try {
            if (connection != null && statement != null) {
                connection.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(int id, User user) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "";
            PreparedStatement preparedStatement = null;
            if (user instanceof IndividualPersonUser) {
                sql = "UPDATE users set `username` = ?, `password` = ?, `name` = ?, `surname` = ?, `emailAddress` = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, ((IndividualPersonUser) user).getName());
                preparedStatement.setString(4, ((IndividualPersonUser) user).getSurname());
                preparedStatement.setString(5, ((IndividualPersonUser) user).getEmailAddress());
                preparedStatement.setInt(6, id);
            } else {
                sql = "UPDATE users set `username` = ?, `password` = ?, `name` = ?, `contactPerson` = ? WHERE id = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, ((CompanyUser) user).getName());
                preparedStatement.setString(4, ((CompanyUser) user).getContactPerson());
                preparedStatement.setInt(5, id);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateUser(int id, String username, String password, boolean isCompanyUser, String name, String contactPerson, String surname, String emailAddress) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "UPDATE users SET `username` = ?, `password` = ?, `name` = ?, `contactPerson` = ?, `surname` = ?, emailAddress = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, contactPerson);
            preparedStatement.setString(5, surname);
            preparedStatement.setString(6, emailAddress);
            preparedStatement.setInt(7, id);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateUser(int id, String username, String password) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "UPDATE users SET `username` = ?, `password` = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, id);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static String getUsernamePassword(int userID) {
        String result = "";
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * FROM users WHERE id = " + userID;
            ResultSet resultSet = statement.executeQuery(SQL);
            while(resultSet.next()) {
                result += resultSet.getString("username") + " " + resultSet.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return result;
    }

    public static User validateUser(String username, String password) {
        Connection connection = null;
        Statement statement = null;
        User user = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getBoolean("isCompanyUser") == true) {
                    user = new CompanyUser(resultSet.getString("username"), resultSet.getString("password"),
                            resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("contactPerson"));
                } else {
                    user = new IndividualPersonUser(resultSet.getString("username"), resultSet.getString("password"),
                            resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("surname"),
                            resultSet.getString("emailAddress"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return user;
    }

    public static void deleteUser(int id) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "DELETE from users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addUser(User user) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            if (user instanceof CompanyUser) {
                String sql = "INSERT into `users` (`id`, `username`, `password`, `isCompanyUser`, `name`, `contactPerson`) VALUES (NULL, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setBoolean(3, true);
                preparedStatement.setString(4, ((CompanyUser) user).getName());
                preparedStatement.setString(5, ((CompanyUser) user).getContactPerson());

                preparedStatement.executeUpdate();
            } else {
                String sql = "INSERT into `users` (`id`, `username`, `password`, `isCompanyUser`, `name`, `surname`, `emailAddress`) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUserName());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setBoolean(3, false);
                preparedStatement.setString(4, ((IndividualPersonUser) user).getName());
                preparedStatement.setString(5, ((IndividualPersonUser) user).getSurname());
                preparedStatement.setString(6, ((IndividualPersonUser) user).getEmailAddress());

                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addUser(String username, String password, boolean isCompanyUser, String name, String contactPerson, String surname, String emailAddress) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "INSERT INTO `users` (`id`, `username`, `password`, `isCompanyUser`, `name`, `contactPerson`, `surname`, `emailAddress`) VALUES" +
                    "(NULL, ?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBoolean(3, isCompanyUser);
            preparedStatement.setString(4, name);
            preparedStatement.setString(5, contactPerson);
            preparedStatement.setString(6, surname);
            preparedStatement.setString(7, emailAddress);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addResponsiblePerson(User user, Category category) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "INSERT into `responsiblepeople` (`userID`, `categoryName`) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, category.getName());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static ArrayList<Category> getAllCategories() {
        ArrayList<Category> allCategoriesTmp = new ArrayList<>();
        ArrayList<Category> allCategoriesFinal = new ArrayList<>();
        ArrayList<String> parentCategories = new ArrayList<>();
        ArrayList<Integer> creators = new ArrayList<>();
        Category category = new Category();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String sql = "SELECT * from categories";
            ResultSet rs1 = statement.executeQuery(sql);
            while (rs1.next()) {
                String name = rs1.getString("name");
                String description = rs1.getString("description");
                int creatorID = rs1.getInt("creator");
                String parentCategory = rs1.getString("parentCategory");
                double sumOfExpenses = rs1.getDouble("sumOfExpenses");
                double sumOfIncomes = rs1.getDouble("sumOfIncomes");
                creators.add(creatorID);
                parentCategories.add(parentCategory);
                category = new Category(name, description, sumOfExpenses, sumOfIncomes);
                allCategoriesTmp.add(category);
            }

            ArrayList<User> allUsers = DatabaseController.getAllUsers();

            for (int i = 0; i < parentCategories.stream().count(); i++) {
                for (User user : allUsers) {
                    if (user.getId() == creators.get(i)) {
                        allCategoriesTmp.get(i).setCreator(user);
                    }
                }
                if (parentCategories.get(i) != null) {
                    Category c = ManageCategoriesPage.getCategory(allCategoriesFinal, parentCategories.get(i));
                    if (c != null) {
                        c.getSubCategories().add(allCategoriesTmp.get(i));
                        allCategoriesTmp.get(i).setParentCategory(c);
                    }
                } else {
                    allCategoriesFinal.add(allCategoriesTmp.get(i));
                }
            }

            sql = "SELECT * from responsiblepeople";
            ResultSet rs2 = statement.executeQuery(sql);
            while (rs2.next()) {
                for (Category category1 : allCategoriesFinal) {
                    if (category1.getName().equals(rs2.getString("categoryName"))) {
                        for (User user : allUsers) {
                            if (user.getId() == rs2.getInt("userID")) {
                                category1.getResponsiblePeople().add(user);
                            }
                        }
                    }
                }
            }

            sql = "SELECT * from incomes";
            ResultSet rs3 = statement.executeQuery(sql);
            while (rs3.next()) {
                Category cat = ManageCategoriesPage.getCategory(allCategoriesFinal, rs3.getString("belongsTo"));
                cat.getIncomes().add(new Income(rs3.getString("name"), rs3.getString("description"), rs3.getDouble("amount")));
            }

            sql = "SELECT * from expenses";
            ResultSet rs4 = statement.executeQuery(sql);
            while (rs4.next()) {
                Category cat = ManageCategoriesPage.getCategory(allCategoriesFinal, rs4.getString("belongsTo"));
                cat.getExpenses().add(new Expense(rs4.getString("name"), rs4.getString("description"), rs4.getDouble("amount")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        DatabaseController.disconnectFromDb(connection, statement);
        return allCategoriesFinal;
    }

    public static Category getCategory(String name) {
        Category cat = new Category();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * FROM categories WHERE name = " + "'" + name + "'";
            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                cat = new Category(resultSet.getString("name"), resultSet.getString("description"), resultSet.getString("parentCategory"), resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return cat;
    }

    public static void updateIncomeBelongsTo(String name, String belongsTo) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String SQL = "UPDATE incomes set `belongsTo` = ? WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, belongsTo);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateExpenseBelongTo(String name, String belongsTo) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String SQL = "UPDATE expenses set `belongsTo` = ? WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, belongsTo);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static ArrayList<Category> getAllCategories(int userId) {
        ArrayList<Category> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String SQL;
            PreparedStatement preparedStatement;
            SQL = "SELECT * from categories WHERE creator = ?";
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("parentCategory") != null) {
                    categories.add(new Category(resultSet.getString("name"), resultSet.getString("description"),
                            resultSet.getString("parentCategory"), resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes")));
                } else {
                    categories.add(new Category(resultSet.getString("name"), resultSet.getString("description"),
                            "", resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes")));
                }
            }
            ArrayList<String> categoriesNames = new ArrayList<>();
            SQL = "SELECT categoryName FROM responsiblepeople WHERE userID = " + userId;
            ResultSet rs = statement.executeQuery(SQL);
            while (rs.next()) {
                categoriesNames.add(rs.getString("categoryName"));
            }
            for (String name : categoriesNames) {
                SQL = "SELECT * FROM categories WHERE name = " + "'" + name + "'";
                ResultSet rs1 = statement.executeQuery(SQL);
                while (rs1.next()) {
                    categories.add(new Category(rs1.getString("name"), rs1.getString("description"),
                            rs1.getString("parentCategory"), rs1.getDouble("sumOfExpenses"), rs1.getDouble("sumOfIncomes")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        disconnectFromDb(connection, statement);

        for (Category category : categories) {
            String parentName = category.getParentName();
            while (parentName != "" && parentName != null) {
                Category parent = getCategory(parentName);
                String path = category.getPath();
                category.setPath(path + ">" + parent.getName());
                parentName = parent.getParentName();
            }
        }
        return categories;
    }

    public static ArrayList<Category> getCategories(int userId, String parentName) {
        ArrayList<Category> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String SQL;
            PreparedStatement preparedStatement;
            if (parentName == null) {
                SQL = "SELECT * from categories WHERE creator = ? AND parentCategory IS NULL";
                preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setInt(1, userId);
            } else {
                SQL = "SELECT * from categories WHERE parentCategory = ?";
                preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, parentName);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("parentCategory") != null) {
                    categories.add(new Category(resultSet.getString("name"), resultSet.getString("description"),
                            resultSet.getString("parentCategory"), resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes")));
                } else {
                    categories.add(new Category(resultSet.getString("name"), resultSet.getString("description"),
                            "", resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes")));
                }
            }
            if (parentName == null) {
                ArrayList<String> categoriesNames = new ArrayList<>();
                SQL = "SELECT categoryName FROM responsiblepeople WHERE userID = " + userId;
                ResultSet rs = statement.executeQuery(SQL);
                while (rs.next()) {
                    categoriesNames.add(rs.getString("categoryName"));
                }
                for (String name : categoriesNames) {
                    SQL = "SELECT * FROM categories WHERE name = " + name;
                    ResultSet rs1 = statement.executeQuery(SQL);
                    while (rs1.next()) {
                        categories.add(new Category(resultSet.getString("name"), resultSet.getString("description"),
                                resultSet.getString("parentCategory"), resultSet.getDouble("sumOfExpenses"), resultSet.getDouble("sumOfIncomes")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return categories;
    }

    public static boolean categoryExists(String name) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        Statement statement = null;
        boolean result = false;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "SELECT * from categories WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            result = preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return result;
    }

    public static void addCategory(Category category) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "INSERT into `categories` (`id`, `name`, `description`, `creator`, `parentCategory`, `sumOfExpenses`, `sumOfIncomes`) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getCreator().getId());
            if (category.getParentCategory() != null) {
                preparedStatement.setString(4, category.getParentCategory().getName());
            } else {
                preparedStatement.setString(4, null);
            }
            preparedStatement.setDouble(5, category.getSumOfExpenses());
            preparedStatement.setDouble(6, category.getSumOfIncomes());

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addCategory(String name, String description, int creatorId, String parentName) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "INSERT into `categories` (`id`, `name`, `description`, `creator`, `parentCategory`, `sumOfExpenses`, `sumOfIncomes`) VALUES (NULL, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, creatorId);
            preparedStatement.setString(4, parentName);
            preparedStatement.setDouble(5, 0);
            preparedStatement.setDouble(6, 0);

            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DatabaseController.connectToDb();
            statement = connection.createStatement();
            String sql = "SELECT * from users";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                if (rs.getInt("isCompanyUser") == 1) {
                    users.add(new CompanyUser(rs.getString("username"), rs.getString("password"), rs.getInt("id"), rs.getString("name"), rs.getString("contactPerson")));
                } else {
                    users.add(new IndividualPersonUser(rs.getString("username"), rs.getString("password"), rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("emailAddress")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return users;
    }

    public static void updateCategory(String oldCategoryName, Category newCategory) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "UPDATE categories SET `name` = ?, `description` = ? WHERE `name` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newCategory.getName());
            preparedStatement.setString(2, newCategory.getDescription());
            preparedStatement.setString(3, oldCategoryName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateCategory(String oldCategoryName, String newName, String newDesciption, double sumOfIncomes, double sumOfExpenses) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "UPDATE categories SET `name` = ?, `description` = ?, `sumOfExpenses` = ?, `sumOfIncomes` = ? WHERE `name` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDesciption);
            preparedStatement.setDouble(3, sumOfExpenses);
            preparedStatement.setDouble(4, sumOfIncomes);
            preparedStatement.setString(5, oldCategoryName);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateCategory(Category category) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "UPDATE categories SET `sumOfIncomes` = ?, `sumOfExpenses` = ? WHERE `name` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, category.getSumOfIncomes());
            preparedStatement.setDouble(2, category.getSumOfExpenses());
            preparedStatement.setString(3, category.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void removeCategory(ArrayList<Category> allCategories) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "DELETE FROM categories";
            statement.executeUpdate(sql);
            sql = "DELETE FROM expenses";
            statement.executeUpdate(sql);
            sql = "DELETE FROM incomes";
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        for (Category category : allCategories) {
            addCategory(category);
            for (Income income : category.getIncomes()) {
                addIncome(income, category.getName());
            }
            for (Expense expense : category.getExpenses()) {
                addExpense(expense, category.getName());
            }
        }
    }

    public static void removeCategory(String name) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "DELETE from categories WHERE `name` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, name);
            preparedStatement.execute();

            SQL = "DELETE from incomes WHERE `belongsTo` = " + "'" + name + "'";
            statement.execute(SQL);

            SQL = "DELETE from expenses WHERE `belongsTo` = " + "'" + name + "'";
            statement.execute(SQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addIncome(Income income, String categoryName) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "INSERT into `incomes` (`id`, `belongsTo`, `name`, `description`, `amount`, `dateCreated`) VALUES (NULL, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, categoryName);
            preparedStatement.setString(2, income.getName());
            preparedStatement.setString(3, income.getDescription());
            preparedStatement.setDouble(4, income.getAmount());
            preparedStatement.setDate(5, Date.valueOf(income.getDateCreated()));
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void addExpense(Expense expense, String categoryName) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "INSERT into `expenses` (`id`, `belongsTo`, `name`, `description`, `amount`, `dateCreated`) VALUES (NULL, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, categoryName);
            preparedStatement.setString(2, expense.getName());
            preparedStatement.setString(3, expense.getDescription());
            preparedStatement.setDouble(4, expense.getAmount());
            preparedStatement.setDate(5, Date.valueOf(expense.getDateCreated()));
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateIncome(String oldName, String oldDescription, Income income) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "UPDATE incomes SET `name` = ?, `description` = ?, `amount` = ? WHERE `name` = ? AND `description` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, income.getName());
            preparedStatement.setString(2, income.getDescription());
            preparedStatement.setDouble(3, income.getAmount());
            preparedStatement.setString(4, oldName);
            preparedStatement.setString(5, oldDescription);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void updateExpense(String oldName, String oldDescription, Expense expense) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "UPDATE expenses SET `name` = ?, `description` = ?, `amount` = ? WHERE `name` = ? AND `description` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, expense.getName());
            preparedStatement.setString(2, expense.getDescription());
            preparedStatement.setDouble(3, expense.getAmount());
            preparedStatement.setString(4, oldName);
            preparedStatement.setString(5, oldDescription);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
    }

    public static void deleteExpense(Expense expense) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "DELETE from expenses WHERE `name` = ? AND `description` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, expense.getName());
            preparedStatement.setString(2, expense.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        Category category = getCategory(expense.getBelongsTo());
        double exp = category.getSumOfExpenses();
        category.setSumOfExpenses(exp - expense.getAmount());
        updateCategory(category);
        while (category.getParentName() != null) {
            category = getCategory(category.getParentName());
            exp = category.getSumOfExpenses();
            category.setSumOfExpenses(exp - expense.getAmount());
            updateCategory(category);
        }
    }

    public static void deleteIncome(Income income) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "DELETE from incomes WHERE `name` = ? AND `description` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, income.getName());
            preparedStatement.setString(2, income.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        Category category = getCategory(income.getBelongsTo());
        double inc = category.getSumOfIncomes();
        category.setSumOfIncomes(inc - income.getAmount());
        updateCategory(category);
        while (category.getParentName() != null) {
            category = getCategory(category.getParentName());
            inc = category.getSumOfIncomes();
            category.setSumOfIncomes(inc - income.getAmount());
            updateCategory(category);
        }
    }

    public static ArrayList<Income> getAllIncomes() throws SQLException, ClassNotFoundException {
        ArrayList<Income> allIncomes = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "SELECT * from incomes";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                allIncomes.add(new Income(rs.getString("name"), rs.getString("description"), rs.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allIncomes;
    }

    public static ArrayList<Expense> getAllExpenses() throws SQLException, ClassNotFoundException {
        ArrayList<Expense> allExpenses = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String sql = "SELECT * from expenses";
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                allExpenses.add(new Expense(rs.getString("name"), rs.getString("description"), rs.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allExpenses;
    }

    public static ArrayList<Income> getIncomes(String categoryName) {
        ArrayList<Income> allIncomes = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * FROM incomes WHERE belongsTo = " + "'" + categoryName + "'";

            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                allIncomes.add(new Income(resultSet.getString("belongsTo"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allIncomes;
    }

    public static ArrayList<Expense> getExpenses(String categoryName) {
        ArrayList<Expense> allExpenses = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * FROM expenses WHERE belongsTo = " + "'" + categoryName + "'";

            ResultSet resultSet = statement.executeQuery(SQL);
            while (resultSet.next()) {
                allExpenses.add(new Expense(resultSet.getString("belongsTo"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allExpenses;
    }

    public static double getTotalIncomesByDate(LocalDate start, LocalDate end) {
        double sum = 0.0;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL;
            PreparedStatement preparedStatement;
            if (start != null && end != null) {
                SQL = "SELECT amount FROM incomes WHERE dateCreated BETWEEN ? AND ?";
                preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setDate(1, Date.valueOf(start));
                preparedStatement.setDate(2, Date.valueOf(end));
            } else {
                SQL = "SELECT amount FROM incomes";
                preparedStatement = connection.prepareStatement(SQL);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sum += resultSet.getDouble("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return sum;
    }

    public static ArrayList<Income> getIncomesByDate(LocalDate start, LocalDate end) {
        ArrayList<Income> allIncomes = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * from incomes WHERE dateCreated BETWEEN ? AND ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setDate(1, Date.valueOf(start));
            preparedStatement.setDate(2, Date.valueOf(end));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allIncomes.add(new Income(resultSet.getString("belongsTo"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allIncomes;
    }

    public static ArrayList<Expense> getExpensesByDate(LocalDate start, LocalDate end) {
        ArrayList<Expense> allExpenses = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * from expenses WHERE dateCreated BETWEEN ? AND ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setDate(1, Date.valueOf(start));
            preparedStatement.setDate(2, Date.valueOf(end));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                allExpenses.add(new Expense(resultSet.getString("belongsTo"), resultSet.getString("name"),
                        resultSet.getString("description"), resultSet.getDouble("amount")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return allExpenses;
    }

    public static double getTotalExpensesByDate(LocalDate start, LocalDate end) {
        double sum = 0.0;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL;
            PreparedStatement preparedStatement;
            if (start != null && end != null) {
                SQL = "SELECT amount FROM expenses WHERE dateCreated BETWEEN ? AND ?";
                preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setDate(1, Date.valueOf(start));
                preparedStatement.setDate(2, Date.valueOf(end));
            } else {
                SQL = "SELECT amount FROM expenses";
                preparedStatement = connection.prepareStatement(SQL);
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                sum += resultSet.getDouble("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return sum;
    }

    public static double getTotalIncomes(int userID) {
        double sum = 0.0;
        ArrayList<Category> allCategories = getAllCategories(userID);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * from incomes WHERE `belongsTo` = ?";
            for (Category category : allCategories) {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, category.getName());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    sum += resultSet.getDouble("amount");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return sum;
    }

    public static double getTotalExpenses(int userID) {
        double sum = 0.0;
        ArrayList<Category> allCategories = getAllCategories(userID);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connectToDb();
            statement = connection.createStatement();
            String SQL = "SELECT * from expenses WHERE `belongsTo` = ?";
            for (Category category : allCategories) {
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
                preparedStatement.setString(1, category.getName());
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    sum += resultSet.getDouble("amount");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        disconnectFromDb(connection, statement);
        return sum;
    }

//    public static double getTotalExpensesAllUsers() {
//        double sum = 0.0;
//        Connection connection = null;
//        Statement statement = null;
//        try {
//            connection = connectToDb();
//            statement = connection.createStatement();
//            String SQL = "SELECT amount from expenses";
//
//            ResultSet resultSet = statement.executeQuery(SQL);
//            while (resultSet.next()) {
//                sum += resultSet.getDouble("amount");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        disconnectFromDb(connection, statement);
//        return sum;
//    }
//
//    public static double getTotalIncomesAllUsers() {
//        double sum = 0.0;
//        Connection connection = null;
//        Statement statement = null;
//        try {
//            connection = connectToDb();
//            statement = connection.createStatement();
//            String SQL = "SELECT amount from incomes";
//
//            ResultSet resultSet = statement.executeQuery(SQL);
//            while (resultSet.next()) {
//                sum += resultSet.getDouble("amount");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        disconnectFromDb(connection, statement);
//        return sum;
//    }
}