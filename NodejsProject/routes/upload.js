const express = require('express');
const multer = require('multer');
const route = express.Router();
const logger = require('../Helper/LogHelper');
const bodyParser = require('body-parser');
const oracledb = require('oracledb');


route.use(bodyParser.urlencoded({extended: true}));

// try{
//   oracledb.initOracleClient({libDir: 'D:\\0.sorizava\\0.Project\\0.UploadVoiceFile\\NodejsProject\\instantclient_19_12'});
// }catch(err){
//   console.log(err);
// }

oracledb.outFormat = oracledb.OUT_FORMAT_OBJECT;

storage = multer.diskStorage({
    destination: './uploads/',
    filename: 
    // function(req, file, cb) {
    //   return crypto.pseudoRandomBytes(16, function(err, raw) {
    //     if (err) {
    //       return cb(err);
    //     }
    //     return cb(null, "" + (raw.toString('hex')) + (path.extname(file.originalname)));
    //   });
    // }
    (req, file, callback) => {
        callback(null, file.originalname);
    }
  });

route.get('/', (req, res, next) => {
    res.send('response with a resources');
});

// Post files             
//화이팅 해봅시다. 이게 더 좋은거 같다. 
route.post(
    "/",
    multer({
      storage: storage
    }).single('upload'), function(req, res) {
      //console.log(req.file);
      //filesize == 0 라고 나온다... 사이즈가 0인걸 보냈으니 당연 그럴 수 밖에 
      //console.log(req.body);
      //res.redirect("/uploads/" + req.file.filename);
      //console.log(req.file.filename);
      logger.debug('/upload method 진입');
      //logger.debug('req.file : ', req.file, ', req.body : ', req.body, ', req.file.filename :', req.file.filename);
      console.log(req.file.originalname);

      return res.status(200).end();
    });

route.post("/calllist", async (req, res) => {

  //const {dateString, number} = req.body;

  //console.log("FILELIST REQ" + req);
  //console.log(dateString + "," + number);
  console.log("dateString : " + req.body.dateString + ", " + "number : " + req.body.numberString);
  logger.debug("callist method : dateString : " + req.body.dateString + ", " + "number : " + req.body.numberString);
  ///insertData(req.body.number, req.body.dateString, req.body.duration);

  //oracledb.autoCommit = true;

  // oracledb.getConnection({
  //   user: 'jcs',
  //   password: 'jcs',
  //   connectString: '192.168.0.44:1521/XE'
  // }, function(err, connection){
  //   if(err){
  //     console.log('err : ' + 'getConnection' + err );
  //     return;
  //   }

  //   console.log("before insert state");

  //   const query = "INSERT INTO CALL_LIST(CALL_TIME, PHONE_NUMBER)" 
  //   + " SELECT :datestring, :number "
  //   + " FROM DUAL "
  //   + " WHERE NOT EXISTS "
  //   + "(SELECT * FROM CALL_LIST WHERE (CALL_TIME = :datestring AND "
  //   + " PHONE_NUMBER = :number))";

  // const bindData = [
  //   req.body.datestring,
  //   req.body.number
  // ];

  // connection.execute(query, bindData, (err, result) => {
  //   if(err){
  //     console.log(err.message);
  //   }else{
  //     console.log('Inserted Row :' + result.rowsAffected);
  //   }

  //   connection.close();

  // });

  // });

  let connection;
  
  try {

    oracledb.autoCommit = true;

    connection = await oracledb.getConnection( {
      user          : "ALI_NOTE",
      password      : "1234",
      connectString : "localhost:1521/XE"
    });

    const result = await connection.execute(

      `INSERT INTO CALL_LIST_TBL(CALL_TIME, PHONE_NUMBER)
      SELECT :dateString, :numberString
      FROM DUAL
      WHERE NOT EXISTS
      (SELECT * FROM CALL_LIST_TBL WHERE (CALL_TIME = :dateString AND 
              PHONE_NUMBER = :numberString))`, [req.body.dateString, req.body.numberString]
    );

    console.log(result.rows);
    logger.debug("INSERT INTO : "  + result.rows);
""
    

  } catch (err) {
    console.error(err);
    logger.debug(err);
  } finally {
    if (connection) {
      try {
        await connection.close();
      } catch (err) {
        console.error(err);
        logger.debug(err);
      }
    }
  }

  return res.status(200).end();
});

//const insertData =  (number, dateString, duration) => {



module.exports = route;