

/***
 * 
 * �版��璁块���ュ�ｅ���扮被
 * 
 * 
 * 
 * 
 * 
 */

package com.database;




import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;





public class DatabaseAccessImpl implements DatabaseAccessInterface {



	public void DatabaseAccessImpl()
	{
		
	}
	
	
	
	/**
	 * 
	 * 
	 * 
	 *
	 */
	public void executeSQL(String sqlStatement) throws SQLException { 
        Connection connection = null; 
        PreparedStatement statement = null; 
        try { 
            connection = new DBConnectionManager().getConnection(); 
            connection.setAutoCommit(true); 
            statement = connection.prepareStatement(sqlStatement); 
            statement.execute(); 
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
            //log.error(ex.getMessage()); 
            throw ex; 
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            //og.error(ex.getMessage()); 
        } finally { 
            try { 
                try { 
                    statement.close(); 
                } catch (SQLException ex) { 
                    ex.printStackTrace(); 
                    //log.error("close statement exception in execute sql method"); 
                } 
                connection.close(); 
            } catch (SQLException ex) { 
                ex.printStackTrace(); 
                //log.error(ex.getMessage()); 
                throw ex; 
            } 
        } 
        //log.debug("exit execute sql statement method"); 
        return; 
    } 



	public void executeSQL(String[] sqlStatement) throws SQLException {
		// TODO Auto-generated method stub

	}

	//娴�璇�杩��ユ���绋冲����
	public static void main(String [] are)
	{
		
		System.out.println("===========1==================");
		DatabaseAccessImpl altsql=new DatabaseAccessImpl();
		try {
			
			for(int i=0;i<100000000;i++)
			{
			 altsql.executeSQL("insert into testname (name) values('test')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=========2====================");
		
	}
	
	

}
