/*
 *****************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *
 ******************************************************************************
 */ 

package org.eclipse.birt.data.engine.odaconsumer;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import org.eclipse.birt.data.engine.executor.ResultClass;
import org.eclipse.birt.data.engine.executor.ResultObject;
import org.eclipse.birt.data.engine.i18n.ResourceConstants;
import org.eclipse.birt.data.engine.odi.IResultClass;
import org.eclipse.birt.data.engine.odi.IResultObject;
import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.oda.IResultSet;
import org.eclipse.birt.data.oda.OdaException;

/**
 * <code>ResultSet</code> maintains an incremental pointer to rows in the  
 * result set.
 */
public class ResultSet
{
	private IResultSet m_resultSet;
	private IResultClass m_resultClass;		// cached result class
	
	ResultSet( IResultSet resultSet, IResultClass resultClass )
	{
		assert( resultSet != null && resultClass != null );
		m_resultSet = resultSet;
		m_resultClass = resultClass;
	}
	
	/**
	 * Returns an <code>IResultClass</code> representing the metadata of the 
	 * result set for this <code>ResultSet</code>.
	 * @return	this <code>ResultSet</code>'s metadata
	 * @throws DataException	if data source error occurs.
	 */
	public IResultClass getMetaData() throws DataException
	{
		return m_resultClass;
	}
	
	/**
	 * Specifies the maximum number of <code>IResultObjects</code> that can be 
	 * fetched from this <code>ResultSet</code>.
	 * @param max	the maximum number of <code>IResultObjects</code> that can be 
	 *				fetched; 0 means no limit.
	 * @throws DataException	if data source error occurs.
	 */
	public void setMaxRows( int max ) throws DataException
	{
		try
		{
			m_resultSet.setMaxRows( max );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_SET_MAX_ROWS, ex );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_SET_MAX_ROWS, ex );
		}
	}
	
	/**
	 * Returns the IResultObject representing the next row in the result set.
	 * @return 	the IResultObject representing the next row; null if there are 
	 * 			no more rows available or if max rows limit has been reached.
	 * @throws DataException	if data source error occurs.
	 */
	public IResultObject fetch( ) throws DataException
	{
		try
		{
			if( ! m_resultSet.next( ) )
				return null;
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_FETCH_NEXT_ROW, ex );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_FETCH_NEXT_ROW, ex );
		}

		int columnCount = m_resultClass.getFieldCount();
		int[] driverPositions = 
			( (ResultClass) m_resultClass ).getFieldDriverPositions();
		assert( columnCount == driverPositions.length );
		
		Object[] fields = new Object[ columnCount ];
		
		for( int i = 1; i <= columnCount; i++ )
		{
			if ( m_resultClass.isCustomField( i ) == true )
				continue;
			
			Class dataType = m_resultClass.getFieldValueClass( i );
			int driverPosition = driverPositions[i - 1];
			Object colValue = null;
			
			if( dataType == Integer.class )
			{
				int j = getInt( driverPosition );
				if( ! wasNull() )
					colValue = new Integer( j ); 
			}
			else if( dataType == Double.class )
			{
				double d = getDouble( driverPosition );
				if( ! wasNull() )
					colValue = new Double( d );
			}
			else if( dataType == String.class )
				colValue = getString( driverPosition );
			else if( dataType == BigDecimal.class )
				colValue = getBigDecimal( driverPosition );
			else if( dataType == Date.class )
				colValue = getDate( driverPosition );
			else if( dataType == Time.class )
				colValue = getTime( driverPosition );
			else if( dataType == Timestamp.class )
				colValue = getTimestamp( driverPosition );
			else 
				assert false;
			
			if( wasNull( ) )
				colValue = null;
			
			fields[i - 1] = colValue;
		}
		
		return new ResultObject( m_resultClass, fields );
	}
	
	private int getInt( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getInt( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_INT_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_INT_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private double getDouble( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getDouble( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_DOUBLE_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_DOUBLE_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private String getString( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getString( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_STRING_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_STRING_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private BigDecimal getBigDecimal( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getBigDecimal( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_BIGDECIMAL_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_BIGDECIMAL_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private Date getDate( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getDate( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_DATE_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_DATE_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private Time getTime( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getTime( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_TIME_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_TIME_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private Timestamp getTimestamp( int driverPosition ) throws DataException
	{
		try
		{
			return m_resultSet.getTimestamp( driverPosition );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_TIMESTAMP_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_TIMESTAMP_FROM_COLUMN, ex, 
			                         new Object[] { new Integer( driverPosition ) } );
		}
	}
	
	private boolean wasNull() throws DataException
	{
		try
		{
			return m_resultSet.wasNull();
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_DETERMINE_WAS_NULL, ex );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_DETERMINE_WAS_NULL, ex );
		}
	}

	/**
	 * Returns the current row's 1-based index position.
	 * @return	current row's 1-based index position.
	 * @throws DataException	if data source error occurs.
	 */
	public int getRowPosition( ) throws DataException
	{
		try
		{
			return m_resultSet.getRow( );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_ROW_POSITION, ex );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_GET_ROW_POSITION, ex );
		}
	}
	
	/**
	 * Closes this <code>ResultSet</code>.
	 * @throws DataException	if data source error occurs.
	 */
	public void close( ) throws DataException
	{
		try
		{
			m_resultSet.close( );
		}
		catch( OdaException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_CLOSE_RESULT_SET, ex );
		}
		catch( UnsupportedOperationException ex )
		{
			throw new DataException( ResourceConstants.CANNOT_CLOSE_RESULT_SET, ex );
		}
	}
}
