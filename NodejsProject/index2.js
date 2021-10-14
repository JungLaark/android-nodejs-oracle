const express = require('express');
const app = express();

const logger = require('./Helper/LogHelper');


const userRouter = require('./routes/users');
const fileUploadRouter = require('./routes/upload');

//const dbHelper = require('./Helper/DBHelper');


app.use('/users', userRouter);
app.use('/upload', fileUploadRouter);

app.use(express.json());
//app.use(express.urlencoded({extended: true}));

// storage = multer.diskStorage({
//     destination: './uploads/',
//     filename: 
//     // function(req, file, cb) {
//     //   return crypto.pseudoRandomBytes(16, function(err, raw) {
//     //     if (err) {
//     //       return cb(err);r
//     //     }
//     //     return cb(null, "" + (raw.toString('hex')) + (path.extname(file.originalname)));
//     //   });
//     // }
//     (req, file, callback) => {
//         callback(null, file.originalname);
//     }
//   });

// // Post files
// app.post(
//   "/upload/",
//   multer({
//     storage: storage
//   }).single('upload'), function(req, res) {
//     console.log(req.file);
//     //filesize == 0 라고 나온다... 사이즈가 0인걸 보냈으니 당연 그럴 수 밖에 
//     console.log(req.body);
//     //res.redirect("/uploads/" + req.file.filename);
//     console.log(req.file.filename);
//     logger.debug('/upload method 진입');
//     logger.debug('req.body : ' + req.file + 'req.file.filename :' + req.file.filename);

//     return res.status(200).end();
//   }); 

// app.post('/login', (req, res) => {
 
//   // const reqId = req.body.ID;
//   // const reqPW = req.body.PW; 
//   logger.debug('/login method 진입');
//   const {ID, PW} = req.body;
//   console.log('reqId : ', ID, 'reqPW : ', PW);
//   //const result = dbHelper(ID, PW);
//   console.log('/login : ', result.row);
//   res.send(req.body);

// });

  //app.post

// const data = [
//   {id: "jcs",
//   password: "jcsjcs"}
// ];

// const id = "bis1";
// const password = "bis1234";

// const data = {id, password};

// console.log(data.id, data.password);
  
//dbHelper({data});
//dbHelper(id, password);

  //이제 오라클로 붙어보자 

const port = 28100;
app.listen(port, () => {
    logger.debug('--------------------------------------------------');
    logger.debug('|              Start Express Server : 8080       |');
    logger.debug('--------------------------------------------------');
});