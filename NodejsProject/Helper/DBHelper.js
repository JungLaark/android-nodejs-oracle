const oracledb = require('oracledb');

console.log('DBHelper');

try{
    oracledb.initOracleClient({libDir: 'D:\\0.sorizava\\0.Project\\0.UploadVoiceFile\\NodejsProject\\instantclient_19_12'});
}catch(err){
    console.log(err);
}

oracledb.outFormat = oracledb.OUT_FORMAT_OBJECT;

const dbRun = async (paramId, paramPassword) => {

    // console.log('daRun start');
    // const {idParam, passwordParam} = data;
    console.log('dbRun ', paramId, paramPassword);

    let connection;

    try{
        connection = await oracledb.getConnection({
            user: "ALI_NOTE",//이거 id가 아니라 user로 해야 함 ㅋㅋㅋㅋ 
            password: "1234",
            connectString: '61.32.218.74:1521/XE'
            //connectString: '192.168.0.44:1521/XE'
            //connectString: "(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.0.44)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=XE)))"
            //connectString: "(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=61.32.218.74)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=XE)))"
        });

        const result = await connection.execute(
            `SELECT * FROM ALI_USER_TBL 
            WHERE ID = :ID 
            AND PW = :PW`,
            [paramId, paramPassword],
        );

        console.log(result.rows);
        return result;

    }catch(err){
        console.log('error', err);
    }finally{
        if(connection){
            try{
                await connection.close();
            }catch(err){
                console.error(err);
            }
            
        }
    }
}

module.exports = dbRun;