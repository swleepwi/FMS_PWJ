package com.pwi3.fms_;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class uploadDB {
    /*
    @iFlag				VARCHAR(30),
    @P_FAC_CD			VARCHAR(4),
    @P_LAYOUT_CD		VARCHAR(10),
    @P_UCC_BARCODE		VARCHAR(40) = NULL,
    @P_OLD_RACK_CD		VARCHAR(20) = NULL,
    @P_OLD_LAYER_SEQ	INT			= NULL,
    @P_NEW_RACK_CD		VARCHAR(20) = NULL,
    @P_NEW_LAYER_SEQ	INT			= NULL,
    @P_SPOT_NM			VARCHAR(20) = NULL,
    @P_FG_OUT_TYPE		VARCHAR(2)	= NULL,
    @P_USER_IP  		VARCHAR(20) = NULL,
    @P_USER_CD			VARCHAR(50)
        */
    /*	     @iFlag				= @iFlag
	   , @FAC_CD			= @P_FAC_CD
	   , @LAYOUT_CD			= @P_LAYOUT_CD
	   , @CARTON_BARCODE	= @V_CARTON_BARCODE
	   , @NEW_RACK_CD		= @P_NEW_RACK_CD
	   , @NEW_LAYER_SEQ		= @P_NEW_LAYER_SEQ
	   , @SPOT_NM			= @P_SPOT_NM
	   , @FG_OUT_TYPE		= @P_FG_OUT_TYPE
	   , @USER_CD			= @P_USER_CD;       */
    Connection connect;
    String ConnectionResult = "";

    public Integer  query(String layout_cd,String rackno, String carno, String layno,String userid){



        int reply=5;
        //CallableStatement cstmt = null;
        PreparedStatement cstmt =null;
        ResultSet rs = null;
        Boolean ok=false;
        String answer;

        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();// Connect to database
            answer ="";
            if (connect == null) {
                ConnectionResult = "Check Your Internet Access!";
            } else {
                cstmt = connect.prepareStatement("SP_FMS_SCAN_SAVE ?,?,?,? ,?,?,?,? ,?,?,?,?");
                cstmt.setString(1,"FixScanByCarton");
                cstmt.setString(2, "04");
                cstmt.setString(3, layout_cd);
                cstmt.setString(4, carno);
                cstmt.setNull(5, Types.VARCHAR);
                cstmt.setNull(6,java.sql.Types.INTEGER);
                cstmt.setString(7, rackno);
                cstmt.setInt(8, Integer.parseInt(layno));
                cstmt.setNull(9, Types.VARCHAR);
                cstmt.setNull(10, Types.VARCHAR);
                cstmt.setNull(11, Types.VARCHAR);
                cstmt.setString(12, userid);

                rs = cstmt.executeQuery();
                while (rs.next()) {
                   answer = rs.getString("RTN_DESC");
                }
                if(answer.equals("OK!")){
                    reply=1;
                }else reply=0;
            }
        }catch (Exception ex) {
            Logger.getLogger(MainActivity.class.getName()).log(
                    Level.SEVERE, null, ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainActivity.class.getName()).log(
                            Level.WARNING, null, ex);
                }
            }
        }
            return reply;
    }

        //EXEC PWIERP.dbo.SP_FMS_SCAN_SAVE FixScanByCarton,'04','','2021020505490070','','','000319','1','','','',''
        // "EXEC PWIERP.dbo.SP_FMS_SCAN_SAVE FixScanByCarton,'04','"+layout_cd+"',' "+carno+"','','','"+rackno+"','"+layno+"','','','','"+userid+"'";



}
