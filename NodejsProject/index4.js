const oracledb = require('oracledb');

try{
    oracledb.initOracleClient({libDir: 'D:\\0.sorizava\\0.Project\\0.UploadVoiceFile\\NodejsProject\\instantclient_19_12'});
}catch(err){
    console.log(err);
}

oracledb.outFormat = oracledb.OUT_FORMAT_OBJECT;

async function run() {

    let connection;
  
    try {
      connection = await oracledb.getConnection( {
        user          : "hr",
        password      : "hr",
        connectString : "192.168.0.44:1521/XE"
      });
  
      const result = await connection.execute(
        `SELECT manager_id, department_id, department_name
         FROM departments
         WHERE manager_id = :id`,
        [103],  // bind value for :id
      );
      console.log(result.rows);
  
    } catch (err) {
      console.error(err);
    } finally {
      if (connection) {
        try {
          await connection.close();
        } catch (err) {
          console.error(err);
        }
      }
    }
  }
  
  run();