const express = require('express');
const oracledb = require('oracledb');
const route = express.Router();
const logger = require('../Helper/LogHelper');
const bodyParser = require('body-parser'); 

route.use(bodyParser.json());
route.use(bodyParser.urlencoded({extended: true}));


route.get('/', (req, res, next) => {
    res.send('response with a resources');
});

route.post('/login', async (req, res, next) => {

    let id = req.body.id;
    let pw = req.body.pw;

    console.log("id : " + id + ", pw : " + pw);

    let connection;

    try {

        oracledb.autoCommit = true;
    
        connection = await oracledb.getConnection( {
          user          : "ALI_NOTE",
          password      : "1234",
          connectString : "localhost:1521/XE"
        });
    
        const result = await connection.execute(
    
            `SELECT *
            FROM ALI_USER_TBL
            WHERE ID = :id AND PW = :PW`,
           [id, pw],  // bind value for :id);
        );
    
        console.log(result.rows);
        logger.debug(result.rows);
    
      } catch (err) {
        console.error(err);
        logger.debug(err);

        return res.status(500).end();

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

})

module.exports = route;