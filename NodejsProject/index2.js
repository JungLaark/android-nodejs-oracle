const express = require('express');
const app = express();


const multer = require('multer');
const path = require('path');
const crypto = require('crypto');

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


// Post files
app.post(
  "/upload/",
  multer({
    storage: storage
  }).single('upload'), function(req, res) {
    console.log(req.file);
    //filesize == 0 라고 나온다... 사이즈가 0인걸 보냈으니 당연 그럴 수 밖에 
    console.log(req.body);
    //res.redirect("/uploads/" + req.file.filename);
    console.log(req.file.filename);

    return res.status(200).end();
  });

  //이제 오라클로 붙어보자 

const port = 3030;
app.listen(port, () => {
    console.log('--------------------------------------------------');
    console.log('|              Start Express Server              |');
    console.log('--------------------------------------------------');
});


