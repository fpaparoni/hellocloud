var S3FS = require('s3fs');
const uuidv4 = require('uuid/v4');

exports.helloCloud = (req, res) => {
  console.log(req.body.compare);
  console.log(req.body.pushername);
  console.log(req.body.pusheremail);

  var gitEvent={
    "compareUrl":req.body.compare,
    "pusherName":req.body.pushername,
    "pusherEmail":req.body.pusheremail
  };

  var s3Options = {
    region: 'eu-west-3',
    accessKeyId: 'ACCESS_KEY',
    secretAccessKey: 'SECRET_KEY'
  };
  var fsImpl = new S3FS('gitpush', s3Options);
  fsImpl.writeFile('gitEvent-'+uuidv4(), JSON.stringify(gitEvent), function (err) {
    if (err) throw err;
    console.log('It\'s saved!');
  });
  res.status(200).end();
};
