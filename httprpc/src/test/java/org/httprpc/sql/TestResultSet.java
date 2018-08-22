/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.httprpc.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class TestResultSet implements ResultSet {
    private ResultSetMetaData metaData = new ResultSetMetaData() {
        @Override
        public <T> T unwrap(Class<T> iface) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColumnCount() {
            return labels.length;
        }

        @Override
        public boolean isAutoIncrement(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCaseSensitive(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSearchable(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCurrency(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int isNullable(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSigned(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColumnDisplaySize(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnLabel(int column) {
            return labels[column - 1];
        }

        @Override
        public String getColumnName(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSchemaName(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPrecision(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getScale(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getTableName(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCatalogName(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColumnType(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnTypeName(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReadOnly(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWritable(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDefinitelyWritable(int column) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnClassName(int column) {
            throw new UnsupportedOperationException();
        }
    };

    private String[] labels = {"long", "double", "boolean", "nestedValue", "nestedValue.lower", "nestedValue.upper", "date"};

    private Object[][] values = {
        {2L, 3.0, true, new Object(), "abc", "ABC", new Date(0)},
        {4L, 6.0, false, new Object(), "def", "DEF", new Date(0)},
        {8L, 9.0, false, new Object(), null, null, null}
    };

    private int i = -1;
    private boolean closed = false;

    @Override
    public <T> T unwrap(Class<T> iface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean next() {
        return (++i < values.length);
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean wasNull() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte getByte(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getShort(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getFloat(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte getByte(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public short getShort(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInt(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getFloat(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public String getCursorName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSetMetaData getMetaData() {
        return metaData;
    }

    @Override
    public Object getObject(int columnIndex) {
        return values[i][columnIndex - 1];
    }

    @Override
    public Object getObject(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int findColumn(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getCharacterStream(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getCharacterStream(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBeforeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAfterLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void afterLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean first() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean last() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean absolute(int row) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean relative(int rows) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean previous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchDirection(int direction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchSize(int rows) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getConcurrency() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowUpdated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowInserted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean rowDeleted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNull(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateByte(int columnIndex, byte x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateShort(int columnIndex, short x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateInt(int columnIndex, int x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateLong(int columnIndex, long x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateFloat(int columnIndex, float x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDouble(int columnIndex, double x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateString(int columnIndex, String x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDate(int columnIndex, Date x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTime(int columnIndex, Time x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(int columnIndex, Object x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNull(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateByte(String columnLabel, byte x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateShort(String columnLabel, short x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateInt(String columnLabel, int x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateLong(String columnLabel, long x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateFloat(String columnLabel, float x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDouble(String columnLabel, double x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateString(String columnLabel, String x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateDate(String columnLabel, Date x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTime(String columnLabel, Time x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObject(String columnLabel, Object x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelRowUpdates() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToInsertRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveToCurrentRow() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Statement getStatement() {
        return null;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref getRef(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Blob getBlob(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Clob getClob(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Array getArray(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ref getRef(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Blob getBlob(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Clob getClob(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Array getArray(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getURL(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRef(int columnIndex, Ref x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArray(int columnIndex, Array x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateArray(String columnLabel, Array x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RowId getRowId(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RowId getRowId(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getHoldability() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNString(String columnLabel, String nString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NClob getNClob(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NClob getNClob(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNString(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNString(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) {
        throw new UnsupportedOperationException();
    }
}
