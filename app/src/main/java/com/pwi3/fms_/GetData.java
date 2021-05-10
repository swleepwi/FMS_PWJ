package com.pwi3.fms_;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alaeddin on 5/21/2017.
 */
/*
public class GetData {

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    public String getQuery() {
        return query;
    }
    public GetData(String rackno){
        setQuery(rackno);
    }
    public void setQuery(String rackno) {
        this.query =    "select RC.RACK_CD as RACK_NO,RC.CARTON_BARCODE,RC.LAYER_SEQ as LAYER_CD from PWIFMS.DBO.FMS_RACK_CARTON RC with (NOLOCK)"
                + "left outer join SAL_CARTON_MST CM on (CM.CARTON_BARCODE = RC.CARTON_BARCODE)"
                + "left outer join SAL_ORDER_MST OM on (OM.PWI_NO = CM.PWI_NO)"
                + "left outer join SAL_PROD_DATE PD on (PD.PWI_NO =CM.PWI_NO)"
                + "left outer join [PWIFMS].[dbo].[FMS_SPOT_MST] C on (RC.RACK_CD=C.RACK_CD)"
                + "where 1 = 1 and RC.RACK_CD = "+
                rackno

                + "order by LAYER_SEQ, CARTON_STAT, CARTON_BARCODE";
    }

    String query;
    public List<Map<String,String>> doInBackground() {

        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String, String>>();
        try
        {
            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();        // Connect to database
            if (connect == null)
            {
                ConnectionResult = "Check Your Internet Access!";
            }
            else
            {
                // Change below query according to your own database.
                //setQuery("");
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(getQuery());
                while (rs.next()){
                    Map<String,String> datanum=new HashMap<String,String>();
                    datanum.put("RACK_NO",rs.getString("RACK_NO"));
                    datanum.put("CARTON_BARCODE",rs.getString("CARTON_BARCODE"));
                    datanum.put("LAYER_NO",rs.getString("LAYER_NO"));
                    // datanum.put("NEW_LAYER_SEQ",rs.getString("NEW_LAYER_SEQ"));
                    data.add(datanum);
                }


                ConnectionResult = " successful";
                isSuccess=true;
                connect.close();
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            ConnectionResult = ex.getMessage();
        }

        return data;
    }


}
*/