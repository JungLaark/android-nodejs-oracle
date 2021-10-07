var express = require('express');
var router = express.Router();

//http://192.168.10.142:3030/retrofit/get
router.get('/get', (req, res, next) => {
    console.log('GET 호출 / data : ', req.query.data);
    console.log('path: ' + req.path);

    res.send('get success');
});

router.post('/post', (req, res, next) => {
    console.log('POST 호출 / data : ', req.body.data);
    console.log('path: ' + req.path);

    res.send('get success');
});

router.post('/upload', (req, res, next) => {

});



module.exports = router;

//get query
//post body
//put params
//delete params