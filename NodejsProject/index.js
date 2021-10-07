const express = require('express');
const multer = require('multer');
const {urlencoded} = require('express');
const retrofitRouter = require('./routes/retrofit');
const path = require('path');
const app = express();

app.use(express.json());
app.use(urlencoded({extended: true}));
app.use('/retrofit', retrofitRouter);
app.use(express.static(path.join(__dirname, 'uploads')));

console.log(__dirname);

const recodedFileUpload = multer({
    storage: multer.diskStorage({
        destination: (req, file, cb) => {
            cb(null, `${__dirname}/../uploads`);
        },
        filename: (req, file, cb) => {

            const extName = path.extname(file.originalname);
            const saveName = new Date().getTime().toString() + extName.toLowerCase();
            

            file.savename = saveName;
            file.url = path.join('/uploads', saveName).replace(/\\/gi, '/');

            console.log("file Path : ", file.url);

            req.file = file;

            cb(null, saveName);
        }
    })
});


retrofitRouter.post('/uploadFile', recodedFileUpload.single('voice'), (req, res) => {
    console.log("왔어요 왔어요 파일이 왔어요");
    return res.status(200).json(req.file.filename);
});


const port = 3030;
const ip = "192.168.10.142";

app.listen(port, () => {
    console.log('--------------------------------------------------');
    console.log('|              Start Express Server              |');
    console.log('--------------------------------------------------');
    console.log('server address => http://192.168.10.142:3030');
});
















