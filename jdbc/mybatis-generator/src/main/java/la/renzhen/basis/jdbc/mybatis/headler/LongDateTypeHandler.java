package la.renzhen.basis.jdbc.mybatis.headler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Date;

/**
 * @author Clinton Begin
 */
public class LongDateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setObject(i, null);
        } else {
            ps.setLong(i, parameter.getTime());
        }
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long sqlTimestamp = rs.getLong(columnName);
        if (sqlTimestamp != 0) {
            return new Date(sqlTimestamp);
        }
        return null;
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long sqlTimestamp = rs.getLong(columnIndex);
        if (sqlTimestamp != 0) {
            return new Date(sqlTimestamp);
        }
        return null;
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long sqlTimestamp = cs.getLong(columnIndex);
        if (sqlTimestamp != 0) {
            return new Date(sqlTimestamp);
        }
        return null;
    }
}
