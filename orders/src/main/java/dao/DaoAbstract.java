package dao;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import data.ConnectionFactory;


public class DaoAbstract<T> {
    protected static final Logger LOGGER = Logger.getLogger(DaoAbstract.class.getName());

    private final Class<T> type;

    public DaoAbstract() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    public String getPrimaryKeyColumnName() {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.equalsIgnoreCase("id")) {
                return "id";
            }
        }
        return "ID";
    }



    public String getTableName() {
        StringBuilder sb= new StringBuilder();
        sb.append( type.getSimpleName());
        return sb.toString();
    }

    protected Connection getConnection() throws SQLException {
        return ConnectionFactory.getConnection();
    }

    protected void closeConnection(Connection connection) {
        ConnectionFactory.close(connection);
    }

    protected void closeStatement(PreparedStatement statement) {
        ConnectionFactory.close(statement);
    }

    protected void closeResultSet(ResultSet resultSet) {
        ConnectionFactory.close(resultSet);
    }

    protected ResultSet executeSelectQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing SELECT query: " + e.getMessage());
        }

        return resultSet;
    }

    private int executeInsertQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int generatedId = -1;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                generatedId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing INSERT query: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }

        return generatedId;
    }

    private void executeUpdateQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing UPDATE query: " + e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    private void executeDeleteQuery(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error executing DELETE query: " + e.getMessage());
        } finally {
            closeStatement(statement);
        }
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        try{
            while(resultSet.next()){
                T instance = type.getDeclaredConstructor().newInstance();
                for(Field field : type.getDeclaredFields()) {
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.WARNING, "Could not create object");
        }

        return list;
    }
    public String selectAllFrom(){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        return sb.toString();
    }

    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + "=?");
        return sb.toString();
    }


    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet=null;
        String query = selectAllFrom();
        List<T> result=null;
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            System.out.println(query);
            System.out.println(resultSet);
            result= createObjects(resultSet);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }


        return result;
    }

    public T findById(String id){
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try{
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setString(1, id);
            System.out.println(statement);
            resultSet = statement.executeQuery();
            if(!resultSet.isBeforeFirst())
                return null;
            else
                return createObjects(resultSet).get(0);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + " DAO:findById "+ e.getMessage());
        }
        finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }




    public int insert(T object) {
        String query = "INSERT INTO " + this.getTableName() + " (" + getColumnNames() + ") VALUES (" + getQuestionMarks() + ")";
        List<Object> values = getColumnValues(object);
        return executeInsertQuery(query, values.toArray());
    }

    private String getColumnNames() {
        StringBuilder sb = new StringBuilder();
        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            sb.append(field.getName());
            if (i != fields.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private String getQuestionMarks() {
        StringBuilder sb = new StringBuilder();
        Field[] fields = type.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            sb.append("?");
            if (i != fields.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private List<Object> getColumnValues(T object) {
        List<Object> values = new ArrayList<Object>();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                Method method = propertyDescriptor.getReadMethod();
                Object value = method.invoke(object);
                values.add(value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                     | IntrospectionException e) {
                LOGGER.log(Level.WARNING, "Error getting column values: " + e.getMessage());
            }
        }
        return values;
    }


    public void update(T object) {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append(getTableName());
        query.append(" SET ");

        // Get the primary key column name
        String primaryKeyColumnName = getPrimaryKeyColumnName();

        try {
            // Get the value of the primary key field of the object
            Field primaryKeyField = type.getDeclaredField(primaryKeyColumnName);
            primaryKeyField.setAccessible(true);
            Object primaryKeyValue = primaryKeyField.get(object);

            // Add the SET clauses to the query
            boolean first = true;
            for (Field field : type.getDeclaredFields()) {
                // Ignore the primary key field
                if (!field.getName().equals(primaryKeyColumnName)) {
                    // Get the value of the field of the object
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);

                    // Add the field name and value to the SET clause
                    if (!first) {
                        query.append(", ");
                    }
                    query.append(field.getName());
                    query.append(" = ?");
                    first = false;
                }
            }

            // Add the WHERE clause to the query
            query.append(" WHERE ");
            query.append(primaryKeyColumnName);
            query.append(" = ?");

            // Execute the update query
            Object[] parameters = new Object[type.getDeclaredFields().length];
            int i = 0;
            for (Field field : type.getDeclaredFields()) {
                // Ignore the primary key field
                if (!field.getName().equals(primaryKeyColumnName)) {
                    // Get the value of the field of the object
                    field.setAccessible(true);
                    Object fieldValue = field.get(object);

                    // Add the field value to the parameters array
                    parameters[i] = fieldValue;
                    i++;
                }
            }
            // Add the primary key value to the parameters array
            parameters[i] = primaryKeyValue;

            executeUpdateQuery(query.toString(), parameters);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            LOGGER.log(Level.WARNING, "Error updating object: " + e.getMessage());
        }
    }


    public void delete(int id) {
        String query = "DELETE FROM " + getTableName() + " WHERE " + getPrimaryKeyColumnName() + " = ?";
        executeDeleteQuery(query, id);
    }


}