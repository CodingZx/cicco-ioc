package lol.cicco.ioc.handle;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDHandler extends BaseTypeHandler<UUID> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
		PGobject uuidObj = new PGobject();
		uuidObj.setType("uuid");
		uuidObj.setValue(parameter.toString());
		ps.setObject(i, uuidObj);
	}

	@Override
	public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		if(value == null) {
			return null;
		}
		return UUID.fromString(value);
	}

	@Override
	public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		if(value == null) {
			return null;
		}
		return UUID.fromString(value);
	}

	@Override
	public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		if(value == null) {
			return null;
		}
		return UUID.fromString(value);
	}

}
