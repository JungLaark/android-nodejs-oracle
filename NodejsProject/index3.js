const oracledb = require('oracledb');

try{
    oracledb.initOracleClient({libDir: 'D:\\0.sorizava\\0.Project\\0.UploadVoiceFile\\NodejsProject\\instantclient_19_12'});
}catch(err){
    console.log(err);
}

oracledb.outFormat = oracledb.OUT_FORMAT_OBJECT;

oracledb.getConnection({
    user: 'system',
    password: 'system',
    host: '192.168.0.44',
    database: 'xe'
}, (err, conn) => {
    if(err){
        console.log('실패 : ', err);
    }

    console.log('접속 성공');
});